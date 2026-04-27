package com.XCLONE.KhataBackend.ServiceImpl.Notification;

import com.XCLONE.KhataBackend.Entity.Bill;
import com.XCLONE.KhataBackend.Entity.Customer;
import com.XCLONE.KhataBackend.Entity.User;
import com.XCLONE.KhataBackend.Repository.BillRepository;
import com.XCLONE.KhataBackend.Repository.CustomerRepository;
import com.XCLONE.KhataBackend.Repository.UserRepository;
import com.XCLONE.KhataBackend.Service.Notification.BillNotificationOrchestrator;
import com.XCLONE.KhataBackend.Service.Notification.EmailNotificationService;
import com.XCLONE.KhataBackend.Service.PDFGeneration.PdfGenerationService;
import com.XCLONE.KhataBackend.Service.QRCodeGeneration.QRGeneratorService;
import com.XCLONE.KhataBackend.Service.Storage.S3StorageService;
import com.XCLONE.KhataBackend.enums.DeliveryChannel;
import com.XCLONE.KhataBackend.enums.DeliveryStatus;
import com.XCLONE.KhataBackend.enums.PaymentType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillNotificationOrchestratorImpl implements BillNotificationOrchestrator {

    private final PdfGenerationService pdfGenerationService;
    private final S3StorageService s3StorageService;
    private final EmailNotificationService emailNotificationService;
    private final BillRepository billRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final QRGeneratorService qrGeneratorService;

    private static final int MAX_DELIVERY_ATTEMPTS = 3;

    @Override
    @Async("notificationExecutor")
    public void processAndDeliver(Bill bill) {
        log.info("Starting receipt delivery pipeline for bill [{}]", bill.getBillNumber());

        try {

            String qrCodeBase64 = null;
            BigDecimal totalPendingAmount = null;

            if(bill.getPaymentType() == PaymentType.CREDIT){
                Customer customer = customerRepository.findById(bill.getCustomerId()).orElse(null);
                if(customer != null && customer.getPendingAmount() != null){
                    totalPendingAmount = customer.getPendingAmount();
                }

                User user = userRepository.findById(bill.getUserId()).orElse(null);
                if(user != null && user.getUpiId() != null && totalPendingAmount != null){
                    qrCodeBase64 = qrGeneratorService.generateUpiQRCode(
                        user.getUpiId(),
                        user.getShopName(),
                        totalPendingAmount
                    );
                    log.info("QR Code generated for bill [{}]", bill.getBillNumber());
                }
            }
            // ─── Step 1: Generate PDF ───
            byte[] pdfBytes = pdfGenerationService.generateReceiptPdf(bill, qrCodeBase64, totalPendingAmount);
            log.info("PDF generated for bill [{}] — {} bytes", bill.getBillNumber(), pdfBytes.length);

            // ─── Step 2: Upload to S3 ───
            String receiptUrl = s3StorageService.uploadReceipt(
                    bill.getUserId(), bill.getBillNumber(), pdfBytes
            );
            bill.setReceiptUrl(receiptUrl);
            log.info("PDF uploaded to S3 for bill [{}]: {}", bill.getBillNumber(), receiptUrl);

            // ─── Step 3: Dispatch via best channel ───
            dispatchNotification(bill, pdfBytes, totalPendingAmount);

        } catch (Exception e) {
            log.error("Receipt delivery pipeline failed for bill [{}]: {}",
                    bill.getBillNumber(), e.getMessage());
            markDeliveryFailed(bill, e.getMessage());
        }
    }

    /**
     * Picks the best available channel and dispatches.
     * Priority: Email > (future: WhatsApp > SMS)
     */
    private void dispatchNotification(Bill bill, byte[] pdfBytes, java.math.BigDecimal totalPendingAmount) {
        String customerEmail = getCustomerEmail(bill);

        if (customerEmail != null && !customerEmail.isBlank()) {
            deliverViaEmail(bill, pdfBytes, customerEmail, totalPendingAmount);
            return;
        }

        // No contact channel available — skip delivery
        log.warn("No contact info available for bill [{}], skipping notification", bill.getBillNumber());
        bill.setDeliveryStatus(DeliveryStatus.PENDING);
        bill.setLastDeliveryAttempt(Instant.now());
        billRepository.save(bill);
    }

    private void deliverViaEmail(Bill bill, byte[] pdfBytes, String customerEmail, java.math.BigDecimal totalPendingAmount) {
        try {
            incrementAttempt(bill);

            emailNotificationService.sendReceiptEmail(
                    customerEmail,
                    bill.getShopName() != null ? bill.getShopName() : "Khata Shop",
                    bill.getBillNumber(),
                    pdfBytes,
                    totalPendingAmount
            );

            // ─── Success ───
            bill.setDeliveryStatus(DeliveryStatus.DELIVERED);
            bill.setDeliveryChannel(DeliveryChannel.EMAIL);
            bill.setLastDeliveryAttempt(Instant.now());
            bill.setDeliveryError(null);
            billRepository.save(bill);

            log.info("✅ Receipt delivered via EMAIL for bill [{}] to [{}]",
                    bill.getBillNumber(), customerEmail);

        } catch (Exception e) {
            log.error("Email delivery failed for bill [{}]: {}", bill.getBillNumber(), e.getMessage());
            markDeliveryFailed(bill, "EMAIL: " + e.getMessage());
        }
    }

    // =================== HELPERS ===================

    /**
     * Resolves the customer's email from the Bill snapshot.
     */
    private String getCustomerEmail(Bill bill) {
        return bill.getCustomerEmail();
    }

    private void incrementAttempt(Bill bill) {
        int current = bill.getDeliveryAttemptCount() != null ? bill.getDeliveryAttemptCount() : 0;
        bill.setDeliveryAttemptCount(current + 1);
    }

    private void markDeliveryFailed(Bill bill, String errorMessage) {
        incrementAttempt(bill);
        bill.setDeliveryStatus(DeliveryStatus.FAILED);
        bill.setLastDeliveryAttempt(Instant.now());
        bill.setDeliveryError(errorMessage);
        billRepository.save(bill);
    }
}
