package model;

/**
 * CartItem: รายการในตะกร้า
 * รองรับได้ทั้งสินค้าแบบมี Product และรายการ custom (เช่น ข้อความพิเศษ)
 */
public class CartItem {

    private Product product;   // อาจเป็น null ถ้าเป็น custom
    private int qty;
    private double unitPrice;  // ใช้เมื่อมี product
    private String customDesc; // ใช้เมื่อเป็น custom
    private double customPrice;

    public CartItem() {}

    // ใช้กับสินค้าปกติ
    public CartItem(Product product, int qty, double unitPrice) {
        this.product = product;
        this.qty = qty;
        this.unitPrice = unitPrice;
    }

    // ใช้กับรายการ custom
    public CartItem(String customDesc, double customPrice) {
        this.customDesc = customDesc;
        this.customPrice = customPrice;
        this.qty = 1;
    }

    // Getters/Setters
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public String getCustomDesc() { return customDesc; }
    public void setCustomDesc(String customDesc) { this.customDesc = customDesc; }

    public double getCustomPrice() { return customPrice; }
    public void setCustomPrice(double customPrice) { this.customPrice = customPrice; }

    /** ชื่อที่ใช้แสดง */
    public String displayName() {
        return (product != null ? product.getName() : (customDesc != null ? customDesc : "UNKNOWN")) + " x" + qty;
    }

    /** รวมยอดต่อบรรทัด */
    public double lineTotal() {
        if (product != null) return qty * unitPrice;
        return qty * customPrice;
    }
}
