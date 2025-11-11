package model;

/**
 * Manager: ผู้ดูแลระบบฝั่งร้าน
 * - โฟกัสที่ความรับผิดชอบหลัก เช่น verifyPayment(order)
 * - เมธอดจัดการสินค้า/คูปองสามารถค่อยเติมภายหลังตาม Flow ที่คุณมีอยู่
 */
public class Manager extends Person {
    private String username;
    private String password;
    private String displayName;
    private String employeeNo;

    public Manager(String username, String password, String displayName, String employeeNo) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.employeeNo = employeeNo;
    }

    /**
     * ยืนยันการชำระเงินของออเดอร์:
     * - ต้องมี Payment
     * - Payment อยู่สถานะ PENDING_VERIFY
     * - จากนั้น mark paid ทั้ง Payment และ Order
     */
    public boolean verifyPayment(Order order) {
        if (order == null || order.getPayment() == null) return false;
        Payment p = order.getPayment();
        if (!p.isPendingVerification()) return false;

        p.markPaid();       // Payment: PAID + set paidAt
        order.markPaid();   // Order: PAID
        return true;
    }

    // Getters / Setters ขั้นต่ำ
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getDisplayName() { return displayName; }
    public String getEmployeeNo() { return employeeNo; }

    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
}
