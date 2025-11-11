package model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Coupon – รุ่นสะอาด ไม่มีเมธอดซ้ำ
 * รองรับทั้งการเรียกแบบเดิม (setter-style) และแบบใหม่ (getter/ตรวจเงื่อนไข)
 * DiscountType ควรมี PERCENT และ AMOUNT อยู่แล้วในโปรเจกต์
 */
public class Coupon {

    // ===== Fields =====
    private String code;                 // รหัสคูปอง (unique)
    private String name;                 // ชื่อ/คำอธิบาย
    private DiscountType discountType = DiscountType.PERCENT;
    private int value;                   // ถ้า PERCENT = % , ถ้า AMOUNT = จำนวนเงิน
    private int minSpend;                // ยอดขั้นต่ำ
    private LocalDate expiry;            // วันหมดอายุ (null = ไม่มีกำหนด)
    private boolean active = true;       // เปิด/ปิดใช้งาน
    private List<String> allowedSets;    // ขอบเขตการใช้ (เฉพาะเซ็ต/สินค้า)

    // ===== Constructors =====
    public Coupon() {}

    // ตรงกับซิกเนเจอร์เก่าที่บางโค้ดเรียก (minSpend เป็น double -> ปัดลงเป็น int)
    public Coupon(String code, String title, int percent, double minSpend, LocalDate expiredAt) {
        this.code = Objects.requireNonNull(code, "code");
        this.name = (title == null ? "" : title);
        this.discountType = DiscountType.PERCENT;
        this.value = Math.max(0, Math.min(100, percent));
        this.minSpend = (int)Math.max(0, Math.floor(minSpend));
        this.expiry = expiredAt; // null = ไม่มีวันหมดอายุ
        this.active = true;
    }

    // ===== Legacy setters (ให้ chain ได้) =====
    public Coupon setCode(String code)                { this.code = code; return this; }
    public Coupon setName(String name)                { this.name = name; return this; }
    public Coupon setDiscountType(DiscountType dt)    { if (dt != null) this.discountType = dt; return this; }
    /** ถ้า PERCENT = % , ถ้า AMOUNT = จำนวนเงิน */
    public Coupon setValue(int v)                     { this.value = Math.max(0, v); return this; }
    public Coupon setMinSpend(int v)                  { this.minSpend = Math.max(0, v); return this; }
    public Coupon setExpiry(LocalDate d)              { this.expiry = d; return this; }
    public Coupon setActive(boolean a)                { this.active = a; return this; }
    public Coupon setAllowedSets(List<String> sets)   { this.allowedSets = sets; return this; }

    // ===== Getters =====
    public String getCode()           { return code; }
    public String getName()           { return name; }
    public DiscountType getDiscountType() { return discountType; }
    public int getValue()             { return value; }
    public int getMinSpend()          { return minSpend; }
    public LocalDate getExpiry()      { return expiry; }
    public boolean isActive()         { return active; }
    public List<String> getAllowedSets() { return allowedSets; }

    // ===== Compatibility helpers (ตรงกับโค้ดเดิมที่เรียกใช้) =====
    /** แสดงขอบเขตการใช้เป็นข้อความ */
    public String scopeText() {
        return (allowedSets == null || allowedSets.isEmpty())
                ? "ทั้งร้าน"
                : "เฉพาะเซ็ต/สินค้า: " + String.join(",", allowedSets);
    }

    /** กำหนดให้ใช้ได้เฉพาะบางเซ็ต/สินค้า (แบบเดิม) */
    public Coupon allowOnlySets(List<String> ids) {
        this.allowedSets = ids;
        return this;
    }

    /** ตรวจว่า “ใช้ได้” ณ วันที่กำหนดและยอดรวมที่กำหนด (เวอร์ชันเดิมเรียก usable) */
    public boolean usable(double subtotal, LocalDate today) {
        if (!active) return false;
        if (expiry != null && today != null && today.isAfter(expiry)) return false;
        return subtotal >= minSpend;
    }

    /**
     * รวมยอดที่เข้าเกณฑ์จากรายการตะกร้า (เวอร์ชันเดิมใน Flow เรียกใช้)
     * หมายเหตุ: ถ้าคูปองจำกัดเฉพาะบางเซ็ต จะรวมเฉพาะรายการที่เกี่ยวข้อง
     */
    public double eligibleSubtotalForItems(java.util.List<CartItem> items) {
        if (items == null || items.isEmpty()) return 0.0;
        boolean limit = (allowedSets != null && !allowedSets.isEmpty());
        double sum = 0.0;
        for (CartItem ci : items) {
            // ถ้าจำกัดเซ็ต/สินค้า ให้พิจารณาเฉพาะที่อยู่ใน allowedSets
            String pid = null;
            if (ci.getProduct() != null) pid = ci.getProduct().getId();
            if (!limit || (pid != null && allowedSets.contains(pid))) {
                sum += ci.lineTotal();
            }
        }
        return sum;
    }

    /** คำนวณส่วนลดจากยอดที่เข้าเกณฑ์ */
    public double discountFor(double eligibleSubtotal) {
        if (!usable(eligibleSubtotal, LocalDate.now())) return 0.0;
        if (discountType == DiscountType.PERCENT) {
            return eligibleSubtotal * (Math.max(0, Math.min(100, value)) / 100.0);
        }
        // AMOUNT
        return Math.min(eligibleSubtotal, Math.max(0, value));
    }

    @Override public String toString() {
        return "Coupon{code=" + code + ", type=" + discountType + ", value=" + value +
               ", minSpend=" + minSpend + ", expiry=" + expiry + ", active=" + active + "}";
    }
}
