package model;

public class Product {
    public String id;
    public String name;

    // เดิมมี ProductCategory อยู่แล้ว: SMALL/MEDIUM/... (คงไว้)
    public ProductCategory category;

    // เพิ่มชนิดสินค้าเชิงระบบ: FINISHED/SET/CUSTOM
    private ProductType productType = ProductType.FINISHED;

    public double price;   // base price (FINISHED/SET ใช้ตรงนี้เป็นหลัก)
    public int stock;
    public String material; // รายละเอียดวัสดุ/คำอธิบาย

    public Product(String id, String name, ProductCategory category, double price, int stock, String material) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.material = material;
    }

    // ใช้เป็น base price ตามชนิดสินค้า (CUSTOM จะบวก/ลบเพิ่มในบรรทัดสั่งซื้อ)
    public double effectivePrice() {
        // ถ้าจะให้ SET ไปดึงราคา ProductSet จริง ให้เติมตรรกะภายนอกแล้ว set ค่า price เข้ามา
        return price;
    }

    // Stock helpers
    public boolean isInStock(int qty) {
        return stock >= qty;
    }

    public void adjustStock(int delta) {
        this.stock += delta;
        if (this.stock < 0) this.stock = 0;
    }

    // Getters / Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public ProductCategory getCategory() { return category; }
    public ProductType getProductType() { return productType; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public String getMaterial() { return material; }

    public void setName(String name) { this.name = name; }
    public void setCategory(ProductCategory category) { this.category = category; }
    public void setProductType(ProductType productType) { this.productType = productType; }
    public void setPrice(double price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
    public void setMaterial(String material) { this.material = material; }
}
