package model;

public enum OrderStatus {
    PENDING,        // สร้างออเดอร์แล้ว รอชำระ/รอตรวจ
    PAID,           // ชำระและยืนยันแล้ว
    PREPARING,      // กำลังเตรียมสินค้า
    SHIPPING,       // อยู่ระหว่างจัดส่ง
    COMPLETED,      // เสร็จสิ้นกระบวนการทั้งหมด
    CANCELLED,       // ยกเลิกออเดอร์
    RECEIVED        // รับสินค้า
}
