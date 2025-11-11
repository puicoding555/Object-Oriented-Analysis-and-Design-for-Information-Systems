package model;

public class OrderItem {
    private Product product;
    private int qty;
    private double unitPrice; // ราคา น เวลาสร้างออเดอร์
    private String customDesc; // ใช้เมื่อเป็นสินค้า CUSTOM
    private double customPrice; // ส่วนเพิ่ม/ลดราคาต่อบรรทัด (CUSTOM)

    public OrderItem(Product product, int qty, double unitPrice) {
        this.product = product;
        this.qty = qty;
        this.unitPrice = unitPrice;
    }

    // ใส่ในคลาส OrderItem (เพิ่ม overload ไม่ทับของเดิม)
    public OrderItem(Product product, int qty, double unitPrice, String customDesc, double customPrice) {
        this.product = product;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.customDesc = customDesc;
        this.customPrice = customPrice;
    }

    public Product getProduct() {
        return product;
    }

    public int getQty() {
        return qty;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public String getCustomDesc() {
        return customDesc;
    }

    public double getCustomPrice() {
        return customPrice;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setCustomDesc(String customDesc) {
        this.customDesc = customDesc;
    }

    public void setCustomPrice(double customPrice) {
        this.customPrice = customPrice;
    }

    public double lineTotal() {
        return (unitPrice * qty) + customPrice;
    }
}
