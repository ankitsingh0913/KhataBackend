package com.XCLONE.KhataBackend.Service.Storage;

import java.util.UUID;

public interface S3StorageService {

    /**
     * Uploads a receipt PDF to S3 and returns the permanent object URL.
     * File is stored at: receipts/{userId}/{billNumber}.pdf
     */
    String uploadReceipt(UUID userId, String billNumber, byte[] pdfData);

    /**
     * Generates a time-limited pre-signed URL for secure download.
     * Use this when sharing receipt links via WhatsApp/SMS/Email.
     * @param expirationMinutes how long the link stays valid
     */
    String generatePresignedUrl(String s3Key, int expirationMinutes);

    /**
     * Downloads a receipt PDF from S3 as a byte array.
     * Useful for attaching directly to emails.
     */
    byte[] downloadReceipt(String s3Key);

    /**
     * Deletes a receipt PDF from S3.
     * Call this when a bill is permanently deleted.
     */
    void deleteReceipt(String s3Key);

    /**
     * Checks if a receipt already exists in S3.
     * Prevents duplicate uploads during retry flows.
     */
    boolean receiptExists(String s3Key);

    /**
     * Extracts the S3 object key from a full S3 URL.
     * e.g., "https://bucket.s3.amazonaws.com/receipts/uid/BILL-123.pdf"
     *       -> "receipts/uid/BILL-123.pdf"
     */
    String extractKeyFromUrl(String receiptUrl);
}
