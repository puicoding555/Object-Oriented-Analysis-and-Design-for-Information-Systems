package model;

/**
 * ProductType แสดงชนิดของสินค้าในเชิงระบบ
 * FINISHED: สินค้าสำเร็จรูป, SET: สินค้าแบบชุด, CUSTOM: สินค้าปรับแต่งเอง
 */
public enum ProductType {
    FINISHED,   // สินค้าสำเร็จรูป (เช่นช่อพร้อมขาย)
    SET,        // สินค้าแบบชุด (เช่นเซ็ตของขวัญ)
    CUSTOM      // สินค้าสั่งทำ/ปรับแต่งเอง
}
