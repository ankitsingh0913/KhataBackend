package com.XCLONE.KhataBackend.ServiceImpl.Notification;

import com.XCLONE.KhataBackend.Service.Notification.EmailNotificationService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationServiceImpl implements EmailNotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendReceiptEmail(String toEmail, String shopName, String billNumber, byte[] pdfData) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Receipt from " + shopName + " — " + billNumber);

            String body = buildEmailBody(shopName, billNumber);
            helper.setText(body, true); // true = HTML content

            // Attach the PDF
            String attachmentName = billNumber + ".pdf";
            helper.addAttachment(attachmentName, new ByteArrayResource(pdfData));

            mailSender.send(message);
            log.info("Receipt email sent to [{}] for bill [{}]", toEmail, billNumber);

        } catch (Exception e) {
            log.error("Failed to send receipt email to [{}] for bill [{}]: {}", toEmail, billNumber, e.getMessage());
            throw new RuntimeException("Email delivery failed for bill: " + billNumber, e);
        }
    }

    private String buildEmailBody(String shopName, String billNumber) {
        return """
                <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 480px; margin: auto; padding: 24px;">
                    <h2 style="color: #1a1a2e; margin-bottom: 4px;">🧾 Your Receipt</h2>
                    <p style="color: #555; font-size: 14px; margin-top: 0;">
                        Bill <strong>%s</strong> from <strong>%s</strong>
                    </p>
                    <hr style="border: none; border-top: 1px solid #eee; margin: 16px 0;">
                    <p style="color: #333; font-size: 14px; line-height: 1.6;">
                        Thank you for your purchase! Your receipt is attached as a PDF.
                        <br><br>
                        If you have any questions about this bill, please contact the shop directly.
                    </p>
                    <hr style="border: none; border-top: 1px solid #eee; margin: 16px 0;">
                    <p style="color: #999; font-size: 11px; text-align: center;">
                        Powered by Khata — Smart billing for smart businesses
                    </p>
                </div>
                """.formatted(billNumber, shopName);
    }
}
