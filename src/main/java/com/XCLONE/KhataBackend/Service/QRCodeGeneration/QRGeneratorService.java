package com.XCLONE.KhataBackend.Service.QRCodeGeneration;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.nio.file.Path;
import java.util.Base64;
import java.nio.file.FileSystems;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class QRGeneratorService {

    public void generateQRCodeImage(String text, int width, int height, String filePath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    } 

    public String generateBase64QRCode(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            
            byte[] pngData = outputStream.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(pngData);
            
            // Return exactly what the HTML <img> tag needs
            return "data:image/png;base64," + base64Image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper method specifically for UPI links
    public String generateUpiQRCode(String upiId, String payeeName, BigDecimal amount) {
        if (upiId == null || upiId.isEmpty()) {
            return null;
        }
        
        // Build UPI Intent URL
        // Example: upi://pay?pa=merchant@upi&pn=Shop%20Name&am=1200.50&cu=INR
        String encodedName = payeeName != null ? payeeName.replace(" ", "%20") : "";
        String upiUrl = String.format("upi://pay?pa=%s&pn=%s&am=%s&cu=INR", 
                                      upiId, 
                                      encodedName, 
                                      amount.toString());
                                      
        // Generate a 250x250 QR Code
        return generateBase64QRCode(upiUrl, 250, 250);
    }
}
