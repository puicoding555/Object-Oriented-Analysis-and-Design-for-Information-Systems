package model;

import java.time.LocalDateTime;

/**
 * Payment: รายการชำระเงินของคำสั่งซื้อ
 * แยกหน้าที่ออกจาก Order เพื่อควบคุมวิธีจ่าย/สถานะ/สลิปได้ชัดเจน
 */
public class Payment {
    private String ref;
    public String getRef(){ return ref; }
    public void setRef(String r){ this.ref = r; }
    public void markApproved(){ this.status = PaymentStatus.PAID; }
    public void markPendingVerify(){ this.status = PaymentStatus.PENDING_VERIFY; }
    private int id;
    private PaymentMethod method;
    private double amount;
    private LocalDateTime paidAt;
    private PaymentStatus status;
    private Slip slip; // หลักฐาน (optional)

    public Payment(int id, PaymentMethod method, double amount) {
        this.id = id;
        this.method = method;
        this.amount = amount;
        this.status = PaymentStatus.PENDING_VERIFY;
    }

    // --- domain operations ---
    public void attachSlip(Slip slip) {
        this.slip = slip;
    }

    public void markPaid() {
        this.status = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    public void markRejected() {
        this.status = PaymentStatus.REJECTED;
        this.paidAt = null;
    }

    public boolean isPendingVerification() {
        return this.status == PaymentStatus.PENDING_VERIFY;
    }

    // --- getters/setters ---
    public int getId() { return id; }
    public PaymentMethod getMethod() { return method; }
    public double getAmount() { return amount; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public PaymentStatus getStatus() { return status; }
    public Slip getSlip() { return slip; }

    public void setAmount(double amount) { this.amount = amount; }
    public void setMethod(PaymentMethod method) { this.method = method; }
}
