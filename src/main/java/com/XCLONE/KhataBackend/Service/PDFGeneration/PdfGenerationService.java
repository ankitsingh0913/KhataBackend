package com.XCLONE.KhataBackend.Service.PDFGeneration;

import com.XCLONE.KhataBackend.Entity.Bill;
import com.XCLONE.KhataBackend.Entity.BillItem;
import com.XCLONE.KhataBackend.Repository.BillItemRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.io.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
// Service for generating PDF documents from HTML using OpenHTMLtoPDF
public class PdfGenerationService {

    // Generates a PDF from HTML content and saves it to the specified path
    private final TemplateEngine templateEngine;
    private final BillItemRepository billItemRepository;

    // Overloaded method to support backward compatibility
    public byte[] generateReceiptPdf(Bill bill) {
        return generateReceiptPdf(bill, null, null);
    }

    public byte[] generateReceiptPdf(Bill bill, String qrCodeBase64, BigDecimal totalPendingAmount) {
        try {
            Context context = new Context();
            context.setVariable("shopName", bill.getShopName());
            context.setVariable("shopPhone", bill.getShopPhone());
            context.setVariable("shopAddress", bill.getShopAddress());

            context.setVariable("billNumber", bill.getBillNumber());
            context.setVariable("billDate", bill.getCreatedAt().atZone(java.time.ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a")));
            context.setVariable("customerName", bill.getCustomerName());
            context.setVariable("customerPhone", bill.getCustomerPhone());
            context.setVariable("paymentMode", bill.getPaymentType().name());
            context.setVariable("paymentStatus", bill.getStatus().name());

            List<BillItem> items = billItemRepository.findByBillId(bill.getId());
            context.setVariable("items", items);

            BigDecimal subtotal = bill.getSubtotal();
            BigDecimal discount = bill.getDiscount() != null ? bill.getDiscount() : BigDecimal.ZERO;
            BigDecimal tax = bill.getTax() != null ? bill.getTax() : BigDecimal.ZERO;
            BigDecimal total = bill.getTotal();
            BigDecimal paid = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
            BigDecimal due = total.subtract(paid);

            context.setVariable("subtotal", subtotal);
            context.setVariable("discount", discount);
            context.setVariable("tax", tax);
            context.setVariable("total", total);
            context.setVariable("paid", paid);
            context.setVariable("due", due);
            
            // --- NEW VARIABLES FOR SMART INVOICE ---
            context.setVariable("qrCodeBase64", qrCodeBase64);
            context.setVariable("totalPendingAmount", totalPendingAmount);
            // ---------------------------------------

            String html = templateEngine.process("invoice", context);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate receipt PDF", e);
        }
    }
}