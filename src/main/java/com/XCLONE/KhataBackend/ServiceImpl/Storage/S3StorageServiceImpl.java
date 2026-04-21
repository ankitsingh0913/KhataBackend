package com.XCLONE.KhataBackend.ServiceImpl.Storage;

import com.XCLONE.KhataBackend.Service.Storage.S3StorageService;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3StorageServiceImpl implements S3StorageService {

    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    // ======================== UPLOAD ========================

    @Override
    public String uploadReceipt(UUID userId, String billNumber, byte[] pdfData) {
        String key = buildKey(userId, billNumber);

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("application/pdf");
            metadata.setContentLength(pdfData.length);
            metadata.addUserMetadata("bill-number", billNumber);
            metadata.addUserMetadata("user-id", userId.toString());

            PutObjectRequest request = new PutObjectRequest(
                    bucketName,
                    key,
                    new ByteArrayInputStream(pdfData),
                    metadata
            );

            s3Client.putObject(request);

            String url = s3Client.getUrl(bucketName, key).toString();
            log.info("Receipt uploaded successfully: {} -> {}", key, url);
            return url;

        } catch (Exception e) {
            log.error("Failed to upload receipt [{}]: {}", key, e.getMessage());
            throw new RuntimeException("Cloud storage upload failed for bill: " + billNumber, e);
        }
    }

    // =================== PRESIGNED URL =====================

    @Override
    public String generatePresignedUrl(String s3Key, int expirationMinutes) {
        try {
            Date expiration = new Date(System.currentTimeMillis() + (long) expirationMinutes * 60 * 1000);

            GeneratePresignedUrlRequest presignedRequest = new GeneratePresignedUrlRequest(bucketName, s3Key)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration);

            URL presignedUrl = s3Client.generatePresignedUrl(presignedRequest);
            log.info("Presigned URL generated for [{}], expires in {} min", s3Key, expirationMinutes);
            return presignedUrl.toString();

        } catch (Exception e) {
            log.error("Failed to generate presigned URL for [{}]: {}", s3Key, e.getMessage());
            throw new RuntimeException("Failed to generate download link", e);
        }
    }

    // ===================== DOWNLOAD ========================

    @Override
    public byte[] downloadReceipt(String s3Key) {
        try (S3Object s3Object = s3Client.getObject(bucketName,s3Key)) {
            byte[] content = s3Object.getObjectContent().readAllBytes();
            log.info("Receipt downloaded from S3: {}", s3Key);
            return content;

        } catch (AmazonS3Exception e) {
            log.error("S3 error downloading [{}]: {}", s3Key, e.getMessage());
            throw new RuntimeException("Receipt not found in cloud storage", e);
        } catch (IOException e) {
            log.error("IO error reading [{}]: {}", s3Key, e.getMessage());
            throw new RuntimeException("Failed to read receipt from cloud storage", e);
        }
    }

    // ===================== DELETE ===========================

    @Override
    public void deleteReceipt(String s3Key) {
        try {
            if (!receiptExists(s3Key)) {
                log.warn("Receipt does not exist, skipping delete: {}", s3Key);
                return;
            }

            s3Client.deleteObject(bucketName, s3Key);
            log.info("Receipt deleted from S3: {}", s3Key);

        } catch (Exception e) {
            log.error("Failed to delete receipt [{}]: {}", s3Key, e.getMessage());
            throw new RuntimeException("Failed to delete receipt from cloud storage", e);
        }
    }

    // =================== EXISTS CHECK ======================

    @Override
    public boolean receiptExists(String s3Key) {
        try {
            return s3Client.doesObjectExist(bucketName, s3Key);
        } catch (Exception e) {
            log.error("Failed to check existence of [{}]: {}", s3Key, e.getMessage());
            return false;
        }
    }

    // ================== URL -> KEY =========================

    @Override
    public String extractKeyFromUrl(String receiptUrl) {
        if (receiptUrl == null || receiptUrl.isBlank()) {
            throw new IllegalArgumentException("Receipt URL cannot be null or blank");
        }

        // Handles both path-style and virtual-hosted-style S3 URLs:
        //   https://bucket.s3.region.amazonaws.com/receipts/uid/BILL-123.pdf
        //   https://s3.region.amazonaws.com/bucket/receipts/uid/BILL-123.pdf
        try {
            URL url = new URL(receiptUrl);
            String path = url.getPath();

            // Remove leading slash
            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            // If path-style URL, the first segment is the bucket name — strip it
            if (path.startsWith(bucketName + "/")) {
                path = path.substring(bucketName.length() + 1);
            }

            return path;

        } catch (Exception e) {
            log.error("Failed to extract S3 key from URL [{}]: {}", receiptUrl, e.getMessage());
            throw new IllegalArgumentException("Invalid S3 URL: " + receiptUrl, e);
        }
    }

    // ================== HELPER =============================

    /**
     * Builds a consistent S3 key path: receipts/{userId}/{billNumber}.pdf
     * Organizing by userId ensures each merchant's receipts are isolated.
     */
    private String buildKey(UUID userId, String billNumber) {
        return String.format("receipts/%s/%s.pdf", userId, billNumber);
    }
}
