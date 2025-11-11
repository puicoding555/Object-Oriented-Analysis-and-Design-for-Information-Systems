package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Member: ผู้ใช้ฝั่งลูกค้า
 * - เพิ่ม Cart จริง (ถือหลาย CartItem ภายใน)
 * - เพิ่ม placeOrder(): แปลง Cart → Order + OrderItem
 * - คงแนวคิดเดิม: เก็บ username/password ผ่าน Person (ในโปรเจ็กต์เดิมคุณมี
 * Person)
 * ถ้ายังไม่มี Person ให้เพิ่มฟิลด์ที่ต้องการเองในคลาสนี้แทน
 */
public class Member extends Person {
    private String username;
    private String password; // ตามโค้ดเดิมเก็บเป็นข้อความธรรมดา
    private String fullName;
    private String phone;
    private String address;

    // ตะกร้าจริง (ใหม่)
    public final Cart cart = new Cart();

    // ประวัติออเดอร์
    public final List<Order> orders = new ArrayList<>();

    public Member(String username, String password, String fullName, String phone, String address) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
    }

    // ==== Cart operations (เรียกใช้ใน Flow ได้ทันที) ====
    public void addToCart(Product p, int qty) {
        if (p == null || qty <= 0)
            return;
        cart.addItem(p, qty);
    }

    public void removeFromCart(Product p) {
        if (p == null)
            return;
        cart.removeItem(p);
    }

    public void clearCart() {
        cart.clear();
    }

    // ==== Order creation (แปลง Cart → Order + OrderItem) ====
    public Order placeOrder(String orderId) {
        // guard
        if (cart.isEmpty())
            return null;

        // snapshot ที่อยู่จัดส่งจาก member ตอนนี้ (คงเดิมตาม text-mode)
        Order order = new Order(orderId, this.username, this.address);
        
        // แปลงรายการใน cart → order items
        for (CartItem ci : cart.getItems()) {
            double unitPrice = ci.getProduct().effectivePrice(); // base price ตามชนิดสินค้า
            OrderItem oi = new OrderItem(ci.getProduct(), ci.getQty(), unitPrice);
            // กรณี CUSTOM: เติม customDesc/customPrice จาก cart item
            if (ci.getCustomDesc() != null)
                oi.setCustomDesc(ci.getCustomDesc());
            oi.setCustomPrice(ci.getCustomPrice());
            order.addItem(oi);
        }

        // เคลียร์ cart หลังสั่งซื้อ (ตามพฤติกรรม e-commerce ปกติ)
        cart.clear();

        // เก็บประวัติ
        orders.add(order);
        return order;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public Cart getCart() {
        return cart;
    }

    // Getters / Setters (ขั้นต่ำที่ UI ใช้)
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    } // **ตามโค้ดเดิม**

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean hasUsedCoupon(String code) {
        if (code == null)
            return false;
        for (Order o : orders) {
            if (o.getCoupon() != null && code.equalsIgnoreCase(o.getCoupon().getCode()))
                return true;
        }
        return false;
    }

    public void updateQty(Product p, int qty) {
        cart.updateQty(p, qty);
    }

}