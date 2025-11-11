package model;

/**
 * Shipping: การขนส่งของคำสั่งซื้อ
 * แยกออกจาก Order เพื่อควบคุมที่อยู่/ผู้ติดต่อ/สถานะขนส่งได้อิสระ
 */
public class Shipping {
    private int id;
    private String address;
    private String contactName;
    private String contactPhone;
    private String carrier;             // บริษัทขนส่ง (optional)
    private String trackingNo;          // เลขติดตามพัสดุ (optional)
    private ShippingStatus status;

    public Shipping(int id, String address, String contactName, String contactPhone) {
        this.id = id;
        this.address = address;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.status = ShippingStatus.PENDING;
    }

    // --- domain operations ---
    public void markDispatched(String carrier, String trackingNo) {
        this.carrier = carrier;
        this.trackingNo = trackingNo;
        this.status = ShippingStatus.IN_TRANSIT;
    }

    public void markDelivered() {
        this.status = ShippingStatus.DELIVERED;
    }

    // เพิ่มไว้ท้ายคลาส Shipping.java
    public void setStatus(ShippingStatus newStatus) {
        this.status = newStatus;
    }

    public void markPending() {
        this.status = ShippingStatus.PENDING;
    }

    public void markInTransit() {
        this.status = ShippingStatus.IN_TRANSIT;
    }

    // ถ้ายังไม่มี getter/setter ต่อไปนี้ แนะนำเพิ่มด้วย
    public ShippingStatus getStatus() {
        return status;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setContactName(String name) {
        this.contactName = name;
    }

    public void setContactPhone(String phone) {
        this.contactPhone = phone;
    }

    // --- getters/setters ---
    public int getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getCarrier() {
        return carrier;
    }

    public String getTrackingNo() {
        return trackingNo;
    }








}
