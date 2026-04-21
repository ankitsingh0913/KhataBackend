package com.XCLONE.KhataBackend.ServiceImpl;

import com.XCLONE.KhataBackend.DTO.bill.BillCreateRequestDTO;
import com.XCLONE.KhataBackend.DTO.bill.BillResponseDTO;
import com.XCLONE.KhataBackend.DTO.billItem.BillItemRequestDTO;
import com.XCLONE.KhataBackend.DTO.billItem.BillItemResponseDTO;
import com.XCLONE.KhataBackend.Entity.Bill;
import com.XCLONE.KhataBackend.Entity.BillItem;
import com.XCLONE.KhataBackend.Entity.Customer;
import com.XCLONE.KhataBackend.Entity.Product;
import com.XCLONE.KhataBackend.Entity.User;
import com.XCLONE.KhataBackend.Repository.*;
import com.XCLONE.KhataBackend.Service.BillService;
import com.XCLONE.KhataBackend.Service.Notification.BillNotificationOrchestrator;
import com.XCLONE.KhataBackend.enums.BillStatus;
import com.XCLONE.KhataBackend.enums.PaymentType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final BillNotificationOrchestrator notificationOrchestrator;

    @Override
    @Transactional
    public BillResponseDTO createBill(BillCreateRequestDTO request, UUID userId) {

        // 1️⃣ Validate & fetch products
        List<Product> products = fetchAndValidateProducts(request.getItems(), userId);

        // 2️⃣ Handle CREDIT payment type
        if (request.getPaymentType() == PaymentType.CREDIT) {
            if (request.getCustomerId() == null) {
                throw new RuntimeException("Customer is required for CREDIT payment type");
            }
            request.setPaidAmount(BigDecimal.ZERO);
        }

        // 3️⃣ Calculate financials
        BigDecimal subtotal = calculateSubtotal(products, request.getItems());
        BigDecimal total = calculateTotal(subtotal, request.getDiscount(), request.getTax());

        // Handle paidAmount for non-credit payments (CASH, UPI, CARD)
        if (request.getPaymentType() != PaymentType.CREDIT) {
             // If paidAmount is exactly 0, default it to the full total amount.
             if (request.getPaidAmount().compareTo(BigDecimal.ZERO) == 0) {
                 request.setPaidAmount(total);
             }
        }

        validatePayment(total, request.getPaidAmount());

        BillStatus status = request.getPaymentType() == PaymentType.CREDIT
                ? BillStatus.UNPAID
                : determineStatus(total, request.getPaidAmount());

        // 3️⃣ Handle customer logic
        Customer customer = handleCustomerIfPresent(request.getCustomerId(), userId);

        // 4️⃣ Fetch User details for Shop metadata
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 5️⃣ Create Bill entity
        Bill bill = buildBillEntity(request, user, subtotal, total, status, customer);

        Bill savedBill = billRepository.save(bill);

        // 5️⃣ Create Bill Items
        List<BillItem> billItems = createBillItems(savedBill.getId(), products, request.getItems());

        billItemRepository.saveAll(billItems);

        // 6️⃣ Update Inventory
        updateInventory(products, request.getItems());

        // 7️⃣ Update Customer Ledger
        updateCustomerLedger(customer, total, request.getPaidAmount());

        // 8️⃣ Fire async receipt delivery (PDF → S3 → Email)
        notificationOrchestrator.processAndDeliver(savedBill);

        return mapToResponse(savedBill, billItems);
    }

    // ================= HELPER METHODS =================

    private List<Product> fetchAndValidateProducts(List<BillItemRequestDTO> items, UUID userId) {

        List<Product> products = new ArrayList<>();

        for (BillItemRequestDTO item : items) {

            Product product = productRepository
                    .findByIdAndUserId(item.getProductId(), userId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            products.add(product);
        }

        return products;
    }

    private BigDecimal calculateSubtotal(List<Product> products,
                                         List<BillItemRequestDTO> items) {

        BigDecimal subtotal = BigDecimal.ZERO;

        for (int i = 0; i < products.size(); i++) {

            BigDecimal price = products.get(i).getSellingPrice();
            BigDecimal quantity = BigDecimal.valueOf(items.get(i).getQuantity());

            subtotal = subtotal.add(price.multiply(quantity));
        }

        return subtotal;
    }

    private BigDecimal calculateTotal(BigDecimal subtotal,
                                      BigDecimal discount,
                                      BigDecimal tax) {

        if (discount.compareTo(subtotal) > 0) {
            throw new RuntimeException("Discount cannot exceed subtotal");
        }

        return subtotal.subtract(discount).add(tax);
    }

    private void validatePayment(BigDecimal total, BigDecimal paidAmount) {

        if (paidAmount.compareTo(total) > 0) {
            throw new RuntimeException("Paid amount cannot exceed total");
        }
    }

    private BillStatus determineStatus(BigDecimal total, BigDecimal paidAmount) {

        if (paidAmount.compareTo(total) == 0) {
            return BillStatus.PAID;
        } else if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            return BillStatus.PARTIALLY_PAID;
        } else {
            return BillStatus.UNPAID;
        }
    }

    private Customer handleCustomerIfPresent(UUID customerId, UUID userId) {

        if (customerId == null) return null;

        return customerRepository
                .findByIdAndUserId(customerId, userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    private Bill buildBillEntity(BillCreateRequestDTO request,
                                 User user,
                                 BigDecimal subtotal,
                                 BigDecimal total,
                                 BillStatus status,
                                 Customer customer) {

        return Bill.builder()
                .billNumber(generateBillNumber())
                .userId(user.getId())
                .shopName(user.getShopName())
                .shopPhone(user.getPhone())
                .shopAddress(null) // User entity doesn't have an address field yet, but we'll set it as null for now
                .customerId(customer != null ? customer.getId() : null)
                .customerName(customer != null ? customer.getName() : null)
                .customerPhone(customer != null ? customer.getPhone() : null)
                .customerEmail(customer != null ? customer.getEmail() : null)
                .subtotal(subtotal)
                .discount(request.getDiscount())
                .tax(request.getTax())
                .total(total)
                .paidAmount(request.getPaidAmount())
                .paymentType(request.getPaymentType())
                .status(status)
                .notes(request.getNotes())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private List<BillItem> createBillItems(UUID billId,
                                           List<Product> products,
                                           List<BillItemRequestDTO> items) {

        List<BillItem> billItems = new ArrayList<>();

        for (int i = 0; i < products.size(); i++) {

            Product product = products.get(i);
            Integer quantity = items.get(i).getQuantity();

            BigDecimal total = product.getSellingPrice()
                    .multiply(BigDecimal.valueOf(quantity));

            billItems.add(
                    BillItem.builder()
                            .billId(billId)
                            .productId(product.getId())
                            .productName(product.getName())
                            .price(product.getSellingPrice())
                            .quantity(quantity)
                            .total(total)
                            .build()
            );
        }

        return billItems;
    }

    private void updateInventory(List<Product> products,
                                 List<BillItemRequestDTO> items) {

        for (int i = 0; i < products.size(); i++) {

            Product product = products.get(i);
            product.setStock(product.getStock() - items.get(i).getQuantity());
        }
    }

    private void updateCustomerLedger(Customer customer,
                                      BigDecimal total,
                                      BigDecimal paidAmount) {

        if (customer == null) return;

        customer.setTotalPurchase(
                customer.getTotalPurchase().add(total));

        BigDecimal pending = total.subtract(paidAmount);

        if (pending.compareTo(BigDecimal.ZERO) > 0) {
            customer.setPendingAmount(
                    customer.getPendingAmount().add(pending));
        }
    }

    private String generateBillNumber() {
        return "BILL-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private BillResponseDTO mapToResponse(Bill bill,
                                          List<BillItem> items) {

        List<BillItemResponseDTO> itemDTOs = items != null ? items.stream()
                .map(item -> BillItemResponseDTO.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .total(item.getTotal())
                        .build())
                .toList() : null;

        return BillResponseDTO.builder()
                .id(bill.getId())
                .billNumber(bill.getBillNumber())
                .customerId(bill.getCustomerId())
                .customerName(bill.getCustomerName())
                .customerPhone(bill.getCustomerPhone())
                .items(itemDTOs)
                .subtotal(bill.getSubtotal())
                .discount(bill.getDiscount())
                .tax(bill.getTax())
                .total(bill.getTotal())
                .paidAmount(bill.getPaidAmount())
                .paymentType(bill.getPaymentType())
                .status(bill.getStatus())
                .notes(bill.getNotes())
                .createdAt(bill.getCreatedAt())
                .updatedAt(bill.getUpdatedAt())
                .build();
    }

    @Override
    public List<BillResponseDTO> getAllBills(UUID userId) {

        List<Bill> bills = billRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return bills.stream()
                .map(bill -> {
                    List<BillItem> items =
                            billItemRepository.findByBillId(bill.getId());
                    return mapToResponse(bill, items);
                })
                .toList();
    }


    @Override
    public BillResponseDTO getBillById(UUID billId, UUID userId) {

        Bill bill = billRepository
                .findByIdAndUserId(billId, userId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        List<BillItem> items =
                billItemRepository.findByBillId(bill.getId());

        return mapToResponse(bill, items);
    }
}
