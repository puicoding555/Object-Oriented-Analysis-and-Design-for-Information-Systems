package model;

import java.time.LocalDateTime;

/**
 * Receipt: เอกสารยืนยันรายการชำระสำเร็จ
 * เก็บยอดสุทธิ ณ เวลาที่ออกใบเสร็จ (แยกจาก Order เพื่อไม่แกว่งตามการแก้ไขภายหลัง)
 */
public class Receipt {
    private int id;
    private LocalDateTime issuedAt;

    private double subtotal;
    private double discount;
    private double tax;
    private double total;

    private String memberName;   // สรุปชื่อผู้ซื้อ ณ เวลาที่ออกใบเสร็จ
    private String address;      // ที่อยู่ที่แสดงในใบเสร็จ

    public Receipt(int id,
                   double subtotal, double discount, double tax, double total,
                   String memberName, String address) {
        this.id = id;
        this.issuedAt = LocalDateTime.now();
        this.subtotal = subtotal;
        this.discount = discount;
        this.tax = tax;
        this.total = total;
        this.memberName = memberName;
        this.address = address;
    }

    // --- getters ---
    public int getId() { return id; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public double getSubtotal() { return subtotal; }
    public double getDiscount() { return discount; }
    public double getTax() { return tax; }
    public double getTotal() { return total; }
    public String getMemberName() { return memberName; }
    public String getAddress() { return address; }
}
