package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Cart: เก็บรายการสินค้าในตะกร้าและจัดการจำนวน
 */
public class Cart {

    private int cartId;
    private final List<CartItem> items = new ArrayList<>();

    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }

    public List<CartItem> getItems() { return items; }
    public boolean isEmpty() { return items.isEmpty(); }
    public void clear() { items.clear(); }

    /** เพิ่มจำนวนของสินค้าถ้ามีอยู่แล้ว มิฉะนั้นเพิ่มรายการใหม่ */
    public void addItem(Product product, int qty) {
        if (product == null || qty <= 0) return;
        for (CartItem item : items) {
            if (item.getProduct() != null && Objects.equals(item.getProduct().getId(), product.getId())) {
                item.setQty(item.getQty() + qty);
                return;
            }
        }
        items.add(new CartItem(product, qty, product.getPrice()));
    }

    /** ตั้งค่าจำนวนใหม่ (0 หรือน้อยกว่า = ลบออก) */
    public void updateQty(Product product, int qty) {
        if (product == null) return;
        for (int i = 0; i < items.size(); i++) {
            CartItem ci = items.get(i);
            if (ci.getProduct() != null && Objects.equals(ci.getProduct().getId(), product.getId())) {
                if (qty <= 0) items.remove(i);
                else ci.setQty(qty);
                return;
            }
        }
        if (qty > 0) {
            items.add(new CartItem(product, qty, product.getPrice()));
        }
    }

    /** ลบรายการออกจากตะกร้า */
    public void removeItem(Product product) {
        if (product == null) return;
        items.removeIf(ci -> ci.getProduct() != null && Objects.equals(ci.getProduct().getId(), product.getId()));
    }

    /** รวมยอดตามบรรทัดทั้งหมด */
    public double subtotal() {
        double s = 0.0;
        for (CartItem ci : items) s += ci.lineTotal();
        return s;
    }
}
