package model;

import service.Store;

public class SetItem {
    public String productId;   // อาจเป็นรหัสสินค้า (เช่น "S1","V2") หรือชื่อวัตถุดิบ (เช่น "กระดาษคราฟต์สีน้ำตาล")
    public int qty;

    public SetItem() {}

    public SetItem(String productId, int qty) {
        this.productId = productId;
        this.qty = qty;
    }

    public String getProductId() {
        return productId;
    }

    public int getQty() {
        return qty;
    }

    /** ชื่อที่ใช้แสดงผล: ถ้าเป็นสินค้า ให้ใช้ชื่อสินค้า; ถ้าเป็นวัตถุดิบ ให้ใช้ key เดิม; หาไม่เจอให้ขึ้น "(รายการไม่ทราบชื่อ)" */
    public String getDisplayName(Store store) {
        // 1) ลองหาจากสินค้าก่อน
        var p = store.getProduct(productId);
        if (p != null && p.getName() != null && !p.getName().isBlank()) {
            return p.getName();
        }
        // 2) ถัดมา ลองดูว่าเป็นวัตถุดิบในคลังหรือไม่ (ใช้ key เป็นชื่ออยู่แล้ว)
        if (store.materials != null && store.materials.containsKey(productId)) {
            return productId;
        }
        // 3) ไม่พบทั้งสองอย่าง
        return "(รายการไม่ทราบชื่อ)";
    }
}
