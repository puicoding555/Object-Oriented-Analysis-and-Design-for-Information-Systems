package service;

import model.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Store: in-memory facade used by flows (MemberFlow/ManagerFlow).
 * เก็บ products/members/orders/coupons/sets/materials พร้อม helper ต่าง ๆ
 */
public class Store {

    // ===== Singleton =====
    private static final Store INSTANCE = new Store();

    public static Store get() {
        return INSTANCE;
    }

    private Store() {
    }

    // ===== Data =====
    public final Map<String, Product> products = new LinkedHashMap<>();
    public final Map<String, Member> members = new LinkedHashMap<>();
    public final Map<String, Order> orders = new LinkedHashMap<>();
    public final Map<String, Manager> managers = new LinkedHashMap<>();
    public final Map<String, Coupon> coupons = new LinkedHashMap<>();
    public final Map<String, Integer> materials = new LinkedHashMap<>(); // material-code -> qty
    // optional human-readable display names for materials (code -> display name)
    public final Map<String, String> materialNames = new LinkedHashMap<>();
    public final Map<String, ProductSet> sets = new LinkedHashMap<>(); // setId -> ProductSet

    // sequences
    private final AtomicInteger seqOrder = new AtomicInteger(1000);
    private final AtomicInteger seqPayment = new AtomicInteger(5000);
    private final AtomicInteger seqReceipt = new AtomicInteger(7000);
    private final AtomicInteger seqShipping = new AtomicInteger(9000);
    private final AtomicInteger seqSlip = new AtomicInteger(3000);

    // ===== Bootstrap helpers =====
    public void upsertProduct(Product p) {
        if (p != null)
            products.put(p.getId(), p);
    }

    public void upsertMember(Member m) {
        if (m != null)
            members.put(m.getUsername(), m);
    }

    public void upsertManager(Manager g) {
        if (g != null)
            managers.put(g.getUsername(), g);
    }

    public void upsertCoupon(Coupon c) {
        if (c != null && c.getCode() != null)
            coupons.put(c.getCode(), c);
    }

    // ===== Convenience API for flows =====
    public List<Product> productsByCategory(ProductCategory cat) {
        return products.values().stream().filter(p -> p.getCategory() == cat).collect(Collectors.toList());
    }

    public Product getProduct(String id) {
        return products.get(id);
    }

    public void createProduct(String id, String name, ProductCategory category, double price, int stock,
            String material) {
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("รหัสว่าง");
        if (products.containsKey(id))
            throw new IllegalArgumentException("มีรหัสนี้แล้ว");
        Product p = new Product(id, name, category, price, stock, material);
        p.setProductType(ProductType.FINISHED);
        upsertProduct(p);
    }

    public boolean updateProduct(String id, String name, ProductCategory category, Double price, Integer stock,
            String material) {
        Product p = products.get(id);
        if (p == null)
            return false;
        if (name != null && !name.isBlank())
            p.setName(name);
        if (category != null)
            p.setCategory(category);
        if (price != null)
            p.setPrice(price);
        if (stock != null)
            p.setStock(Math.max(0, stock));
        if (material != null && !material.isBlank())
            p.setMaterial(material);
        return true;
    }

    public boolean deleteProduct(String id) {
        return products.remove(id) != null;
    }

    // ===== Sets / Promotions =====
    public ProductSet getProductSet(String setId) {
        return sets.get(setId);
    }

    public void createProductSet(String id, String name, double price) {
        if (sets.containsKey(id))
            throw new IllegalArgumentException("มีรหัสเซ็ตนี้แล้ว");
        sets.put(id, new ProductSet(id, name, price));
    }

    public boolean updateProductSet(String id, String name, Double price) {
        ProductSet s = sets.get(id);
        if (s == null)
            return false;
        if (name != null && !name.isBlank())
            s.setName(name);
        if (price != null)
            s.setPrice(price);
        return true;
    }

    public boolean deleteProductSet(String id) {
        return sets.remove(id) != null;
    }

    public int availableStockForSet(String setId) {
        ProductSet ps = sets.get(setId);
        if (ps == null)
            return 0;
        int can = Integer.MAX_VALUE;
        for (SetItem si : ps.getItems()) {
            int need = Math.max(1, si.getQty());
            int have;
            Product p = products.get(si.getProductId());
            if (p != null)
                have = p.getStock();
            else
                have = materials.getOrDefault(si.getProductId(), 0);
            can = Math.min(can, have / need);
        }
        return can == Integer.MAX_VALUE ? 0 : can;
    }

    public boolean consumeSetStock(String setId, int count) {
        ProductSet ps = sets.get(setId);
        if (ps == null || count <= 0)
            return false;
        if (availableStockForSet(setId) < count)
            return false;
        for (SetItem si : ps.getItems()) {
            String pid = si.getProductId();
            int need = si.getQty() * count;
            Product p = products.get(pid);
            if (p != null)
                p.setStock(p.getStock() - need);
            else
                materials.put(pid, Math.max(0, materials.getOrDefault(pid, 0) - need));
        }
        return true;
    }

    public void setProductSetPromo(String id, String desc, LocalDate start, LocalDate end) {
        ProductSet ps = sets.get(id);
        if (ps == null)
            return;
        ps.setPromoDescription(desc);
        ps.setPromoStart(start);
        ps.setPromoEnd(end);
    }

    public void clearProductSetPromo(String id) {
        ProductSet ps = sets.get(id);
        if (ps == null)
            return;
        ps.setPromoDescription(null);
        ps.setPromoStart(null);
        ps.setPromoEnd(null);
    }

    // ===== Orders & Payments =====
    public List<Order> listOrdersSortedByCreated() {
        return orders.values().stream()
                .sorted(Comparator.comparing(Order::getCreatedAt))
                .collect(Collectors.toList());
    }

    public Optional<Order> findOrder(String orderCode) {
        return Optional.ofNullable(orders.get(orderCode));
    }

    /** สร้างออเดอร์จาก snapshot ตะกร้า (ส่วนลดเป็น "จำนวนเงิน") */
    public Order createOrder(Member m, List<CartItem> items, String address,
            LocalDate delivery, double shippingFee, double discountAmount) {

        String orderId = nextOrderCode(); // <-- ใช้โค้ดเต็ม
        Order o = new Order(orderId, m.getUsername(), items); // <-- ใช้คอนสตรัคเตอร์ (String id, ...)

        o.setAddress(address);
        o.setDeliveryDate(delivery);
        o.setShippingFee(shippingFee);

        double subtotal = 0.0;
        for (CartItem ci : items)
            subtotal += ci.lineTotal();

        double discount = Math.max(0, Math.min(subtotal, discountAmount));
        o.setDiscount(discount);
        o.setSubtotal(subtotal);

        double vat = (subtotal - discount + shippingFee) * 0.07;
        o.setVat(vat);
        o.setTotal(subtotal - discount + shippingFee + vat);

        upsertOrder(o);
        return o;
    }

    /** ใส่/อัปเดตออเดอร์ลง map */
    public void upsertOrder(Order o) {
        if (o != null && o.getId() != null) {
            orders.put(o.getId(), o);
        }
    }

    public boolean updateShipping(String orderCode, String address, LocalDate delivery) {
        Order o = orders.get(orderCode);
        if (o == null)
            return false;
        Shipping s = o.getShipping();
        if (s == null) {
            s = new Shipping(nextShippingId(), address, o.getMemberUsername(), "");
            o.attachShipping(s);
        }
        s.setAddress(address);
        o.setDeliveryDate(delivery);
        return true;
    }

    public boolean managerVerifyPayment(String orderCode, boolean approve) {
        Order o = orders.get(orderCode);
        if (o == null || o.getPayment() == null)
            return false;
        if (approve)
            o.getPayment().markApproved();
        else
            o.getPayment().markRejected();
        o.setUpdatedAt(LocalDateTime.now());
        return true;
    }

    // เผื่อโค้ดเก่าเรียกด้วย int tail id
    public boolean managerVerifyPayment(int legacyId, boolean approve) {
        String needle = String.format("%05d", legacyId);
        for (Order o : orders.values()) {
            String id = o.getId();
            if (id != null && id.endsWith(needle)) {
                return managerVerifyPayment(id, approve);
            }
        }
        return false;
    }

    public boolean submitPaymentRef(Order o, String ref) {
        if (o == null || ref == null || ref.isBlank())
            return false;
        for (Order x : orders.values()) {
            if (x.getPayment() != null && ref.equals(x.getPayment().getRef()))
                return false; // duplicate
        }
        Payment p = new Payment(nextPaymentId(), PaymentMethod.TRANSFER, o.getTotal());
        p.setRef(ref);
        p.markPendingVerify();
        o.attachPayment(p);
        return true;
    }

    // ===== Coupon helpers =====
    public Coupon getCoupon(String code) {
        return coupons.get(code);
    }

    public void addCoupon(Coupon c) {
        if (c != null && c.getCode() != null)
            coupons.put(c.getCode(), c);
    }

    public boolean deleteCoupon(String code) {
        return coupons.remove(code) != null;
    }

    // ===== Materials helpers =====
    public void upsertMaterial(String code, String displayName, int qty) {
        if (code == null || code.isBlank())
            throw new IllegalArgumentException("รหัสวัตถุดิบว่าง");
        materials.put(code, Math.max(0, qty));
        // store provided display name for UI (fall back to code if null/blank)
        if (displayName == null || displayName.isBlank())
            materialNames.put(code, code);
        else
            materialNames.put(code, displayName);
    }

    public boolean adjustMaterial(String code, int delta) {
        if (code == null || !materials.containsKey(code))
            return false;
        int now = materials.getOrDefault(code, 0);
        int after = now + delta;
        if (after < 0)
            return false;
        materials.put(code, after);
        return true;
    }

    public boolean deleteMaterial(String code) {
        boolean ok = materials.remove(code) != null;
        materialNames.remove(code);
        return ok;
    }

    public Map<String, Integer> listMaterials() {
        return Collections.unmodifiableMap(materials);
    }

    // ตัวนับ tail 5 หลัก (เดิมของคุณอาจมีอยู่แล้ว)
    private static int orderCounter = 1;

    // ให้ seq วิ่ง 5 หลัก
    private int nextOrderTail() {
        return orderCounter++; // ได้ 1,2,3,... ไว้เติมเป็น %05d
    }

    // คืน "โค้ดออเดอร์" รูปแบบ #ORDyyyyMMddNNNNN
    public String nextOrderCode() {
        return model.Order.generateOrderId(nextOrderTail());
    }

    // ===== Authentication =====
    public Member authMember(String username, String password) {
        for (Member m : members.values())
            if (m.getUsername().equals(username) && m.getPassword().equals(password))
                return m;
        return null;
    }

    public Manager authManager(String username, String password) {
        for (Manager mg : managers.values())
            if (mg.getUsername().equals(username) && mg.getPassword().equals(password))
                return mg;
        return null;
    }

    // ===== Sequences =====
    public int nextOrderId() {
        return seqOrder.incrementAndGet();
    }

    public int nextPaymentId() {
        return seqPayment.incrementAndGet();
    }

    public int nextReceiptId() {
        return seqReceipt.incrementAndGet();
    }

    public int nextShippingId() {
        return seqShipping.incrementAndGet();
    }

    public int nextSlipId() {
        return seqSlip.incrementAndGet();
    }

    // ====== ส่วน field ใหม่ ======
    private final Map<String, java.util.Set<String>> couponUsers = new LinkedHashMap<>(); // code -> ชุด username
                                                                                          // ที่เคยใช้ (นับคนไม่ซ้ำ)
    private final Map<String, java.util.Set<String>> couponOrders = new LinkedHashMap<>(); // code -> ชุด orderId
                                                                                           // ที่เคยใช้
                                                                                           // (กันซ้ำต่อคำสั่งซื้อ)
    private final Map<String, Integer> couponUserLimit = new LinkedHashMap<>(); // (เลือกใส่) เพดานจำนวนสมาชิกที่ใช้ได้

    // ====== helper ตั้งเพดานคนใช้ (ถ้าต้องการแสดง x / limit) ======
    public void setCouponUserLimit(String code, int limit) {
        if (code != null)
            couponUserLimit.put(code, Math.max(0, limit));
    }

    public int getCouponUserLimit(String code) {
        return couponUserLimit.getOrDefault(code, 0);
    }


    // ====== บันทึกการใช้คูปองแบบ "ครั้งเดียวต่อคำสั่งซื้อ" ======
    public void recordCouponUse(String code, String orderId, String username) {
        if (code == null || orderId == null || username == null)
            return;

        // กันซ้ำต่อคำสั่งซื้อ (ถ้า order นี้บันทึกไปแล้วจะไม่บันทึกซ้ำ)
        java.util.Set<String> orders = couponOrders.computeIfAbsent(code, k -> new LinkedHashSet<>());
        if (orders.contains(orderId))
            return; // <== ใช้แล้วใน order นี้ ห้ามซ้ำ
        orders.add(orderId);

        // นับจำนวน "คน" ที่ใช้ (นับ username ไม่ซ้ำ)
        java.util.Set<String> users = couponUsers.computeIfAbsent(code, k -> new LinkedHashSet<>());
        users.add(username);
    }

    /** ผู้ใช้นี้เคยใช้คูปอง code นี้ไปแล้วหรือไม่ (ครั้งเดียวตลอดอายุบัญชี) */
    public boolean hasUserUsedCoupon(String code, String username) {
        if (code == null || username == null)
            return false;
        java.util.Set<String> users = couponUsers.get(code);
        return users != null && users.contains(username);
    }

    /** บันทึกว่า user ใช้คูปอง code นี้แล้ว (ล็อกห้ามใช้ซ้ำในอนาคต) */
    public void recordCouponUseByUser(String code, String username) {
        if (code == null || username == null)
            return;
        couponUsers.computeIfAbsent(code, k -> new java.util.LinkedHashSet<>()).add(username);
    }

    /** สำหรับ ManagerFlow: จำนวนผู้ใช้ที่เคยใช้คูปอง code นี้แล้ว */
    public int countCouponUsers(String code) {
        return couponUsers.getOrDefault(code, java.util.Collections.emptySet()).size();
    }

    /** สำหรับ ManagerFlow: รายชื่อผู้ใช้ที่เคยใช้คูปอง code นี้แล้ว */
    public java.util.Set<String> listCouponUsers(String code) {
        return java.util.Collections.unmodifiableSet(
                couponUsers.getOrDefault(code, java.util.Collections.emptySet()));
    }

    // ====== อ่านสถิติให้ ManagerFlow ======
    public int couponUsedUsersCount(String code) {
        return couponUsers.getOrDefault(code, java.util.Collections.emptySet()).size();
    }

    public java.util.Set<String> couponUsers(String code) {
        return java.util.Collections.unmodifiableSet(
                couponUsers.getOrDefault(code, java.util.Collections.emptySet()));
    }

    public int couponUsedOrdersCount(String code) {
        return couponOrders.getOrDefault(code, java.util.Collections.emptySet()).size();
    }

    
}
