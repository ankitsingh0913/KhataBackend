package com.XCLONE.KhataBackend.Service.Notification;

import com.XCLONE.KhataBackend.Entity.Bill;

public interface BillNotificationOrchestrator {

    /**
     * Asynchronously generates a receipt PDF, uploads it to S3,
     * and dispatches it via the best available channel (email/whatsapp/sms).
     * Updates the Bill entity with delivery tracking info.
     */
    void processAndDeliver(Bill bill);
}
