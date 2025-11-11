package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ชุดสินค้า (ใช้กับ ProductType.SET)
 */
public class ProductSet {
    public String id;
    public String name;
    public double price; // ราคาชุด
    public final List<SetItem> items = new ArrayList<>();

    // โปรโมชัน (ทางเลือก)
    public LocalDate promoStart;
    public LocalDate promoEnd;
    public String promoDescription;

    public ProductSet() {
    }

    public ProductSet(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // ===== Getter/Setter พื้นฐาน =====
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public LocalDate getPromoStart() {
        return promoStart;
    }

    public LocalDate getPromoEnd() {
        return promoEnd;
    }

    public String getPromoDescription() {
        return promoDescription;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPromoStart(LocalDate promoStart) {
        this.promoStart = promoStart;
    }

    public void setPromoEnd(LocalDate promoEnd) {
        this.promoEnd = promoEnd;
    }

    public void setPromoDescription(String promoDescription) {
        this.promoDescription = promoDescription;
    }

    // ===== ส่วนจัดการ items =====
    public List<SetItem> getItems() {
        return items;
    }

    public void addItem(SetItem item) {
        this.items.add(item);
    }

    public void clearItems() {
        this.items.clear();
    }
}
