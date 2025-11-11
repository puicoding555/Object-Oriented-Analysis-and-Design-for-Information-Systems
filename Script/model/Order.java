package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Order: ตัวแทนคำสั่งซื้อ
 */
public class Order {
    // สร้างรหัสออเดอร์รูปแบบ #ORDyyyyMMddNNNNN
    public static String generateOrderId(int seq) {
        java.time.LocalDate today = java.time.LocalDate.now();
        String datePart = today.format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE); // yyyyMMdd
        return String.format("#ORD%s%05d", datePart, seq);
    }

    // --- ข้อมูลอ้างอิง ---
    private String id;
    public String memberUsername;
    public LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public LocalDateTime receivedAt;

    // --- รายการสินค้าในออเดอร์ ---
    private final List<OrderItem> items = new ArrayList<>();

    // --- ยอดเงิน ---
    private double subtotal;
    private double discount;
    private double shippingFee;
    private double vat;
    private double total;
    private double vatRate = 0.07;
    private int discountPercent;

    // --- ข้อมูลการจัดส่ง ---
    private String address;
    private LocalDate deliveryDate;

    // --- สถานะรวมของออเดอร์ ---
    private OrderStatus orderStatus = OrderStatus.PENDING;

    // --- องค์ประกอบที่แยกออกเป็นคลาสเฉพาะ ---
    private Coupon coupon; // 0..1
    private Payment payment;
    private Shipping shipping;
    private Receipt receipt;

    // ===============================
    // Constructor หลัก
    // ===============================
    public Order(String id, String memberUsername, List<CartItem> cartItems) {
        this.id = id;
        this.memberUsername = memberUsername;
        if (cartItems != null) {
            for (CartItem ci : cartItems) {
                OrderItem oi = new OrderItem(ci.getProduct(), ci.getQty(), ci.getUnitPrice());
                this.items.add(oi);
            }
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    // ✅ รองรับโค้ดเดิมที่ใช้ int
    public Order(int idSeq, String memberUsername, List<CartItem> items) {
        this(String.valueOf(idSeq), memberUsername, items);
    }

    // ✅ ตัวเต็ม (พร้อม address/delivery/shippingFee/discountPercent)
    public Order(String id, String memberUsername, List<CartItem> items,
            String address, LocalDate delivery, double shippingFee, int discountPercent) {
        this(id, memberUsername, items);
        this.address = address;
        this.deliveryDate = delivery;
        this.shippingFee = shippingFee;
        this.discountPercent = discountPercent;
    }

    // ===============================
    // Domain operations (Items)
    // ===============================
    public void addItem(OrderItem item) {
        if (item == null)
            return;
        items.add(item);
        touch();
    }

    public void addItem(CartItem ci) {
        if (ci == null)
            return;
        items.add(new OrderItem(ci.getProduct(), ci.getQty(), ci.getUnitPrice()));
        touch();
    }

    public void removeItemByProduct(Product product) {
        if (product == null)
            return;
        for (Iterator<OrderItem> it = items.iterator(); it.hasNext();) {
            OrderItem oi = it.next();
            if (product.equals(oi.getProduct()))
                it.remove();
        }
        touch();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    // ===============================
    // Pricing
    // ===============================
    public double subtotal() {
        double sum = 0.0;
        for (OrderItem oi : items)
            sum += oi.lineTotal();
        this.subtotal = sum;
        return this.subtotal;
    }

    public double discountTotal() {
        if (coupon == null) {
            this.discount = 0.0;
            return 0.0;
        }
        double base = subtotal();
        this.discount = Math.max(0.0, couponDiscountFor(base));
        return this.discount;
    }

    public double computeVat() {
        double base = Math.max(0.0, subtotal() - discountTotal());
        this.vat = round2(base * vatRate);
        return this.vat;
    }

    public double totalBeforeVatAndShipping() {
        return Math.max(0.0, subtotal() - discountTotal());
    }

    public double totalAfterDiscount() {
        double base = totalBeforeVatAndShipping();
        this.total = round2(base + computeVat() + shippingFee);
        return this.total;
    }

    // ===============================
    // Coupon / Payment / Shipping / Receipt
    // ===============================
    public void applyCoupon(Coupon coupon) {
        this.coupon = coupon;
        touch();
    }

    public void attachPayment(Payment payment) {
        this.payment = payment;
        touch();
    }

    public void attachShipping(Shipping shipping) {
        this.shipping = shipping;
        touch();
    }

    public Receipt issueReceipt(int receiptId, String memberNameSnapshot) {
        subtotal();
        discountTotal();
        computeVat();
        totalAfterDiscount();
        this.receipt = new Receipt(receiptId, this.subtotal, this.discount, this.vat, this.total,
                memberNameSnapshot, this.address);
        touch();
        return this.receipt;
    }

    // ===============================
    // Status Handling
    // ===============================
    public void markPaid() {
        this.orderStatus = OrderStatus.PAID;
        touch();
    }

    public void markPreparing() {
        this.orderStatus = OrderStatus.PREPARING;
        touch();
    }

    public void markShipping() {
        this.orderStatus = OrderStatus.SHIPPING;
        touch();
    }

    public void markCompleted() {
        this.orderStatus = OrderStatus.COMPLETED;
        touch();
    }

    public void cancel() {
        this.orderStatus = OrderStatus.CANCELLED;
        touch();
    }

    // ===============================
    // Helpers
    // ===============================
    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    private double couponDiscountFor(double base) {
        return (coupon != null) ? Math.max(0.0, coupon.discountFor(base)) : 0.0;
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    // ===============================
    // Getters / Setters
    // ===============================
    public String getId() {
        return id;
    }

    public String getMemberUsername() {
        return memberUsername;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDiscount() {
        return discount;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public double getVat() {
        return vat;
    }

    public double getTotal() {
        return total;
    }

    public String getAddress() {
        return address;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public double getVatRate() {
        return vatRate;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public Payment getPayment() {
        return payment;
    }

    public Shipping getShipping() {
        return shipping;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public void setUpdatedAt(LocalDateTime t) {
        this.updatedAt = t;
    }

    public void setAddress(String address) {
        this.address = address;
        touch();
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
        touch();
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
        touch();
    }

    public void setVatRate(double vatRate) {
        this.vatRate = vatRate;
        touch();
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // เพิ่มคอนสตรัคเตอร์แบบย่อ (ใช้ใน Member.java)
    // เพิ่มคอนสตรัคเตอร์ใหม่ ใช้โครงสร้าง #ORDyyyyMMddNNNNN
    public Order(int idSeq, String memberUsername, String address) {
        this.id = generateOrderId(idSeq);
        this.memberUsername = memberUsername;
        this.address = address;
        this.createdAt = java.time.LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    // Constructor สำหรับสร้าง Order จาก Member (ใช้ id เป็น String เช่น
    // #ORD2025110900001)
    public Order(String id, String memberUsername, String address) {
        this.id = id;
        this.memberUsername = memberUsername;
        this.address = address;
        this.createdAt = java.time.LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    // เมธอดเช็คว่าออเดอร์พร้อมให้สมาชิกยืนยันหรือยัง
    public boolean canMemberConfirmReceived() {
    return this.getShipping() != null
        && this.getShipping().getStatus() == ShippingStatus.DELIVERED
        && this.orderStatus != OrderStatus.COMPLETED;
}


    // เมธอดยืนยันรับสินค้า
    public void confirmReceived() {
        this.orderStatus = OrderStatus.COMPLETED;
        this.receivedAt = LocalDateTime.now();
    }

}
