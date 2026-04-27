package com.XCLONE.KhataBackend.Service.Notification;

public interface EmailNotificationService {

    /**
     * Sends an email with the receipt PDF attached.
     * @param toEmail     recipient's email address
     * @param shopName    merchant's shop name (for the subject line)
     * @param billNumber  bill number (for the subject line)
     * @param pdfData     the generated PDF as bytes
     */
    void sendReceiptEmail(String toEmail, String shopName, String billNumber, byte[] pdfData);

    void sendReceiptEmail(String toEmail, String shopName, String billNumber, byte[] pdfData, java.math.BigDecimal totalPendingAmount);
}
