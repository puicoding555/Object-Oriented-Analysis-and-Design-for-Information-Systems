package flow;

import model.*;
import service.Store;
import util.Formatter;
import util.InputHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MemberFlow {

    private final Scanner scanner;
    private final Store store;

    private Member currentMember;   // ใช้งานหลัก
    private Member member;          // alias สำหรับโค้ดเก่า

    public MemberFlow(Scanner scanner, Store store, Member member) {
        this.scanner = scanner;
        this.store = store;
        this.currentMember = member;
        this.member = member; // sync alias
    }

    public MemberFlow(Store store, Member member) {
        this(new Scanner(System.in), store, member);
    }

    public void menu() {
        while (true) {
            System.out.println("==========  เมนูหลัก (สมาชิก)  ==========");
            System.out.println("[1] สั่งซื้อสินค้า");
            System.out.println("[2] ดูสินค้าในตะกร้า");
            System.out.println("[3] ดูคูปองและโปรโมชัน");
            System.out.println("[4] ดูประวัติการสั่งซื้อ");
            System.out.println("[5] ชำระเงินค่าสินค้า");
            System.out.println("[6] ติดตามสถานะคำสั่งซื้อ");
            System.out.println("[7] ข้อมูลบัญชีผู้ใช้");
            System.out.println("[0] ออกจากระบบ");
            int sel = InputHelper.askInt("กรุณาเลือกเมนูที่ต้องการ : ");
            switch (sel) {
                case 1 -> orderProductsMenu();
                case 2 -> showCartMenu();
                case 3 -> showCouponsAndPromos();
                case 4 -> showOrderHistory();
                case 5 -> paymentsMenu();
                case 6 -> trackOrdersAndConfirm();
                case 7 -> accountInfo();
                case 0 -> {
                    return;
                }
                default -> System.out.println("ตัวเลือกไม่ถูกต้อง");
            }
        }
    }

    private void orderProductsMenu() {
        while (true) {
            System.out.println("----- สั่งซื้อสินค้า -----");
            System.out.println("[1] สินค้าสำเร็จรูป");
            System.out.println("[2] ออกแบบช่อดอกไม้ (สั่งจัดแต่งเอง)");
            System.out.println("[0] กลับไปเมนูก่อนหน้า");
            int sel = InputHelper.askInt("กรุณาเลือกเมนูที่ต้องการ : ");
            if (sel == 0)
                return;
            if (sel == 1)
                finishedProductsMenu();
            else if (sel == 2)
                customBouquetMenu();
            else
                System.out.println("ตัวเลือกไม่ถูกต้อง");
        }
    }

    private void finishedProductsMenu() {
        while (true) {
            System.out.println("เลือกประเภทสินค้า");
            System.out.println("[1] ช่อดอกไม้ขนาดเล็ก");
            System.out.println("[2] ช่อดอกไม้ขนาดกลาง");
            System.out.println("[3] ช่อดอกไม้ขนาดใหญ่");
            System.out.println("[4] พวงหรีด");
            System.out.println("[5] แจกันดอกไม้");
            System.out.println("[6] เซ็ตสินค้า");
            System.out.println("[0] กลับ");
            int sel = InputHelper.askInt("กรุณาเลือกเมนูที่ต้องการ : ");
            if (sel == 0)
                return;
            switch (sel) {
                case 1 -> selectProductFromList(filterProductsByKeywords("(เล็ก)", "เล็ก"));
                case 2 -> selectProductFromList(filterProductsByKeywords("(กลาง)", "กลาง"));
                case 3 -> selectProductFromList(filterProductsByKeywords("(ใหญ่)", "ใหญ่"));
                case 4 -> selectProductFromList(filterProductsByKeywords("พวงหรีด"));
                case 5 -> selectProductFromList(filterProductsByKeywords("แจกัน"));
                case 6 -> listSetsAndPick(); // <-- ใช้ฟังก์ชันเดียวจบ
                default -> System.out.println("ตัวเลือกไม่ถูกต้อง");
            }
        }
    }

    private List<Product> filterProductsByKeywords(String... keys) {
        List<Product> res = new ArrayList<>();
        for (Map.Entry<String, Product> e : store.products.entrySet()) {
            Product p = e.getValue();
            String name = p.getName() == null ? "" : p.getName();
            boolean ok = false;
            for (String k : keys) {
                if (name.contains(k)) {
                    ok = true;
                    break;
                }
            }
            if (ok)
                res.add(p);
        }
        return res;
    }

    private void selectProductFromList(List<Product> list) {
        if (list.isEmpty()) {
            System.out.println("(ยังไม่มีสินค้าในหมวดนี้)");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            Product p = list.get(i);
            System.out.printf("[%d] %s (%.2f บาท)%n", i + 1, p.getName(), p.getPrice());
        }
        System.out.println("[0] กลับ");
        int sel = InputHelper.askInt("กรุณาเลือกเมนูที่ต้องการ : ");
        if (sel == 0 || sel > list.size())
            return;

        Product p = list.get(sel - 1);
        int qty = InputHelper.askInt("กรุณาระบุจำนวนสินค้า: ");
        System.out.println("วิธีดำเนินการ");
        System.out.println("[1] สั่งซื้อทันที");
        System.out.println("[2] เพิ่มลงตะกร้า");
        System.out.println("[0] ยกเลิก");
        int act = InputHelper.askInt("กรุณาเลือกเมนูที่ต้องการ : ");
        if (act == 1) {
            List<CartItem> temp = new ArrayList<>();
            temp.add(new CartItem(p, qty, p.getPrice()));
            handleCheckout(scanner, currentMember, store, temp);
        } else if (act == 2) {
            member.cart.addItem(p, qty);
            System.out.println("เพิ่มสินค้าไปยังตะกร้าเรียบร้อย");
        }
    }

    // ===== เซ็ตสินค้า: แสดงทั้งหมด -> ดูรายละเอียด -> เลือกจำนวน ->
    // ซื้อทันที/ตะกร้า =====
    private void listSetsAndPick() {
        System.out.println("----- เซ็ตสินค้า -----");
        LocalDate today = LocalDate.now();

        List<String> ids = new ArrayList<>(store.sets.keySet());
        java.util.Collections.sort(ids);
        for (String id : ids) {
            ProductSet s = store.sets.get(id);
            boolean active = (s.getPromoDescription() != null)
                    && (s.getPromoStart() == null || !today.isBefore(s.getPromoStart()))
                    && (s.getPromoEnd() == null || !today.isAfter(s.getPromoEnd()));
            String status = (s.getPromoEnd() != null && today.isAfter(s.getPromoEnd())) ? "ชั่วคราวหมด"
                    : "พร้อมจำหน่าย";
            String flag = active ? " | *** โปรโมชัน ***" : "";
            System.out.printf("- [%s] %s | ราคา: %.2f | %s%s%n", s.getId(), s.getName(), s.getPrice(), status, flag);
        }

        String key = InputHelper.ask("พิมพ์รหัสเซ็ตเพื่อดูรายละเอียด (เว้นว่าง=กลับ): ").trim();
        if (key.isEmpty())
            return;
        ProductSet sel = store.getProductSet(key);
        if (sel == null) {
            System.out.println("ไม่พบรหัสเซ็ตนี้");
            return;
        }

        System.out.println();
        System.out.println("รายละเอียดเซ็ต: " + sel.getName());
        if (sel.getItems() != null && !sel.getItems().isEmpty()) {
            for (SetItem it : sel.getItems()) {
                String label = it.getDisplayName(store); // ชื่อจากสินค้า/วัตถุดิบจริง
                System.out.printf("  • %s x%d%n", label, it.getQty());
            }
        } else {
            System.out.println("  (ไม่มีรายละเอียดรายการในเซ็ต)");
        }

        if (sel.getPromoDescription() != null) {
            System.out.println("** โปรโมชัน : " + sel.getPromoDescription());
            String range = String.format("ช่วงโปร : %s – %s",
                    (sel.getPromoStart() == null ? "-" : sel.getPromoStart().toString()),
                    (sel.getPromoEnd() == null ? "-" : sel.getPromoEnd().toString()));
            System.out.println(range);
        }

        int qty = Math.max(1, Math.min(10, InputHelper.askInt("จำนวนที่ต้องการ : ")));

        // สร้าง Product ชั่วคราว (SET) และกำหนด unitPrice = ราคาเซ็ต (สำคัญ!)
        Product pSet = new Product(sel.getId(), "เซ็ต: " + sel.getName(),
                ProductCategory.MEDIUM, sel.getPrice(), 9999, "SET");
        pSet.setProductType(ProductType.SET);
        CartItem ci = new CartItem(pSet, qty, sel.getPrice());

        System.out.println("[1] สั่งซื้อทันที");
        System.out.println("[2] เพิ่มลงตะกร้า");
        System.out.println("[0] ยกเลิก");
        int act = InputHelper.askInt("กรุณา เลือก เมนูที่ ต้องการ : ");

        if (act == 1) {
            List<CartItem> snap = new ArrayList<>();
            snap.add(ci);
            // เรียกเมธอดกลาง (แก้จาก checkoutFlow -> handleCheckout)
            handleCheckout(this.scanner, this.currentMember, this.store, snap);
        } else if (act == 2) {
            this.currentMember.getCart().getItems().add(ci);
            System.out.println("เพิ่มลงตะกร้าเรียบร้อย");
        }
    }

    // ===== ออกแบบช่อดอกไม้ (สั่งจัดแต่งเอง) =====
    // ===== ออกแบบช่อดอกไม้ (สั่งจัดแต่งเอง) — เลือก 'ประเภทดอกไม้' ก่อน =====
private void customBouquetMenu() {
    // 0) เลือกประเภทดอกไม้ก่อน
    System.out.println("เลือกประเภทดอกไม้");
    System.out.println("[1] ดอกไม้สด");
    System.out.println("[2] ดอกไม้ประดิษฐ์");
    System.out.println("[3] ผสม (Mixed)");
    System.out.println("[0] กลับ");
    int typeSel = InputHelper.askInt("กรุณาเลือกเมนูที่ต้องการ : ");
    if (typeSel == 0) return;

    String[] typeNames = { "ดอกไม้สด", "ดอกไม้ประดิษฐ์", "ผสม (Mixed)" };
    if (typeSel < 1 || typeSel > 3) {
        System.out.println("ตัวเลือกไม่ถูกต้อง");
        return;
    }
    String flowerCategory = typeNames[typeSel - 1];

    // 1) เลือกชนิดดอกไม้
    System.out.println("เลือกชนิดดอกไม้");
    System.out.println("[1] ดอกกุหลาบ");
    System.out.println("[2] ดอกทิวลิป");
    System.out.println("[3] ดอกลิลลี่");
    System.out.println("[4] ดอกคาร์เนชัน");
    System.out.println("[5] ดอกไฮเดรนเยีย");
    System.out.println("[6] ดอกเดซี่");
    System.out.println("[7] ชนิดผสม (Mixed)");
    System.out.println("[0] กลับ");
    int sel = InputHelper.askInt("กรุณาเลือกเมนูที่ต้องการ : ");
    if (sel == 0) return;

    String[] types = {
        "ดอกกุหลาบ","ดอกทิวลิป","ดอกลิลลี่","ดอกคาร์เนชัน","ดอกไฮเดรนเยีย","ดอกเดซี่","ชนิดผสม (Mixed)"
    };
    if (sel < 1 || sel > 7) {
        System.out.println("ตัวเลือกไม่ถูกต้อง");
        return;
    }
    String flowerType = types[sel - 1];

    // 2) เลือกโทนสี
    System.out.println("เลือกโทนสี");
    System.out.println("[1] ขาว");
    System.out.println("[2] สีแดง");
    System.out.println("[3] สีชมพู");
    System.out.println("[0] กลับ");
    sel = InputHelper.askInt("กรุณาเลือกเมนูที่ต้องการ : ");
    if (sel == 0) return;
    String[] colors = { "ขาว", "สีแดง", "สีชมพู" };
    if (sel < 1 || sel > 3) {
        System.out.println("ตัวเลือกไม่ถูกต้อง");
        return;
    }
    String color = colors[sel - 1];

    // 3) เลือกจำนวนดอก + คำนวณราคาเริ่มต้น
    System.out.println("เลือกจำนวนดอก");
    System.out.println("[1] 9 ดอก  (299 บาท)");
    System.out.println("[2] 12 ดอก (399 บาท)");
    System.out.println("[3] 25 ดอก (599 บาท)");
    System.out.println("[4] 50 ดอก (1199 บาท)");
    System.out.println("[0] กลับ");
    sel = InputHelper.askInt("กรุณาเลือกเมนูที่ต้องการ : ");
    if (sel == 0) return;
    int[] stems = { 9, 12, 25, 50 };
    double[] base = { 299, 399, 599, 1199 };
    if (sel < 1 || sel > 4) {
        System.out.println("ตัวเลือกไม่ถูกต้อง");
        return;
    }
    int stem = stems[sel - 1];
    double price = base[sel - 1];

    // **ถ้าต้องการตั้งราคาแตกต่างตามประเภทดอกไม้ ใส่ปรับแต่งได้ที่นี่**
    // ตัวอย่าง (คอมเมนต์ไว้ก่อน):
    // if ("ดอกไม้ประดิษฐ์".equals(flowerCategory)) price -= 20; // ลดนิดหน่อย
    // if ("ผสม (Mixed)".equals(flowerCategory)) price += 30;

    // 4) เลือกการห่อ
System.out.println("เลือกกระดาษห่อ");
System.out.println("[1] กระดาษคราฟต์สีน้ำตาล");
System.out.println("[2] กระดาษโฮโลแกรม");
System.out.println("[3] กระดาษเนื้อด้าน (มีสีให้เลือก)");
System.out.println("[0] กลับ");
sel = InputHelper.askInt("กรุณาเลือกเมนูที่ต้องการ : ");
if (sel == 0) return;

String wrap = null;
String wrapColor = null;

if (sel == 1) {
    wrap = "กระดาษคราฟต์สีน้ำตาล";
} else if (sel == 2) {
    wrap = "กระดาษโฮโลแกรม";
} else if (sel == 3) {
    // เมนูเลือกสีของกระดาษเนื้อด้าน
    System.out.println("เลือกสีของกระดาษเนื้อด้าน");
    System.out.println("[1] ชมพูอ่อน");
    System.out.println("[2] ฟ้าอ่อน");
    System.out.println("[3] ม่วง");
    System.out.println("[4] ขาวครีม");
    System.out.println("[5] ดำ");
    int pc = InputHelper.askInt("กรุณาเลือกเมนูที่คุณต้องการ : ");

    wrapColor = switch (pc) {
        case 1 -> "ชมพูอ่อน";
        case 2 -> "ฟ้าอ่อน";
        case 3 -> "ม่วง";
        case 4 -> "ขาวครีม";
        case 5 -> "ดำ";
        default -> "ชมพูอ่อน";
    };

    wrap = "กระดาษเนื้อด้าน (" + wrapColor + ")";
} else {
    System.out.println("ตัวเลือกไม่ถูกต้อง");
    return;
}

    //String wrap = wraps[sel - 1];

    // 5) ของตกแต่งเพิ่มเติม
List<String> addons = new ArrayList<>();
boolean addDone = false;

while (!addDone) {
    System.out.println("เลือกของตกแต่งเพิ่มเติม");
    System.out.println("[1] ไม่ต้องการเพิ่ม");
    System.out.println("[2] การ์ดอวยพร   (+20)");
    System.out.println("[3] ตุ๊กตาหมีขนาดเล็ก  (+120)");
    System.out.println("[4] ช็อกโกแลตแบบกล่อง (+100)");
    System.out.println("[5] เพิ่มทั้งหมด (+240)");
    //System.out.println("[0] เสร็จสิ้นการเลือก");
    int ch = InputHelper.askInt("กรุณาเลือกเมนูที่ต้องการ : ");

    switch (ch) {
        //case 0 -> addDone = true; // จบการเลือก
        case 1 -> addDone = true; // ไม่เพิ่มอะไร -> ออกเลย
        case 2 -> {
            price += 20;
            addons.add("การ์ดอวยพร");
            addDone = true;   // เลือกเสร็จก็ออก
        }
        case 3 -> {
            price += 120;
            addons.add("ตุ๊กตาหมีขนาดเล็ก");
            addDone = true;
        }
        case 4 -> {
            price += 100;
            addons.add("ช็อกโกแลตแบบกล่อง");
            addDone = true;
        }
        case 5 -> {
            price += 240;
            addons.add("การ์ดอวยพร");
            addons.add("ตุ๊กตาหมีขนาดเล็ก");
            addons.add("ช็อกโกแลตแบบกล่อง");
            addDone = true;
        }
        default -> System.out.println("ตัวเลือกไม่ถูกต้อง");
    }
}

    // 6) สรุปชื่อ + ทำรายการ
    String name = "ช่อดอกไม้สั่งจัดเอง: [" + flowerCategory + "] "
            + flowerType + " โทน" + color + " " + stem + " ดอก | ห่อ: " + wrap 
            + (addons.isEmpty() ? "" : " | เพิ่ม: " + String.join(", ", addons));

    System.out.println("----- สรุปรายการ -----");
    System.out.println("- " + name);
    System.out.printf("ราคารวม: %.2f บาท%n", price);
    System.out.println("[1] สั่งซื้อทันที");
    System.out.println("[2] เพิ่มลงตะกร้า");
    System.out.println("[0] ยกเลิก");
    int act = InputHelper.askInt("กรุณาเลือกเมนูที่ต้องการ : ");
    if (act == 0) return;

    String pid = "CUST-" + java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    Product p = new Product(pid, name, ProductCategory.MEDIUM, price, 9999, "CUSTOM");
    p.setProductType(ProductType.CUSTOM);
    CartItem ci = new CartItem(p, 1, price);

    if (act == 2) {
        this.currentMember.getCart().getItems().add(ci);
        System.out.println("เพิ่มลงตะกร้าเรียบร้อย");
        return;
    }
    if (act == 1) {
        List<CartItem> temp = new ArrayList<>();
        temp.add(ci);
        handleCheckout(this.scanner, this.currentMember, this.store, temp);
    }
}


    // ===== ตะกร้าสินค้า =====
    public void showCartMenu() {
        Scanner sc = this.scanner;
        Member m = this.currentMember;
        Store store = this.store;
        while (true) {
            List<CartItem> items = m.getCart().getItems();
            System.out.println("----- ตะกร้าสินค้า -----");
            if (items.isEmpty()) {
                System.out.println("(ตะกร้าว่าง)");
            } else {
                printCart(items);
            }
            System.out.println("[1] ดำเนินการสั่งซื้อ");
            System.out.println("[2] แก้ไขรายการ");
            System.out.println("[3] ล้างตะกร้าสินค้า");
            System.out.println("[0] กลับ");
            System.out.print("กรุณาเลือกเมนูที่คุณต้องการ : ");

            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1":
                    if (items.isEmpty()) {
                        System.out.println("ตะกร้าว่าง ไม่สามารถสั่งซื้อได้");
                        break;
                    }
                    handleCheckout(sc, m, store, new ArrayList<>(items));
                    return;
                case "2":
                    editCartFlow(sc, m);
                    break;
                case "3":
                    m.getCart().clear();
                    System.out.println("ล้างตะกร้าเรียบร้อย");
                    break;
                case "0":
                    return;
                default:
                    System.out.println("กรุณาเลือกเมนูให้ถูกต้อง");
            }
        }
    }

    private void editCartFlow(Scanner sc, Member m) {
        List<CartItem> items = m.getCart().getItems();
        if (items.isEmpty()) {
            System.out.println("ตะกร้าว่าง");
            return;
        }
        printCart(items);

        System.out.print("กรุณาระบุหมายเลขรายการที่ต้องการแก้ไข : ");
        Integer idx = parseIndex(sc.nextLine(), items.size());
        if (idx == null) {
            System.out.println("หมายเลขไม่ถูกต้อง");
            return;
        }
        int i = idx - 1;
        CartItem target = items.get(i);

        System.out.println("เลือกการแก้ไข");
        System.out.println("[1] เปลี่ยนจำนวน");
        System.out.println("[2] ลบรายการนี้ออก");
        System.out.println("[0] ยกเลิก");
        System.out.print("กรุณาเลือกเมนูที่คุณต้องการ : ");
        String ch = sc.nextLine().trim();

        switch (ch) {
            case "1" -> {
                System.out.print("ระบุจำนวนใหม่ (จำนวนเต็มบวก, 0 = ลบออก) : ");
                Integer q = parsePositiveOrZero(sc.nextLine());
                if (q == null) {
                    System.out.println("จำนวนไม่ถูกต้อง");
                    return;
                }
                if (q == 0) {
                    items.remove(i);
                    System.out.println("ลบรายการเรียบร้อย");
                } else {
                    target.setQty(q);
                    System.out.println("อัปเดตจำนวนเรียบร้อย");
                }
            }
            case "2" -> {
                items.remove(i);
                System.out.println("ลบรายการเรียบร้อย");
            }
            case "0" -> {
            }
            default -> System.out.println("กรุณาเลือกเมนูให้ถูกต้อง");
        }
    }

    private void printCart(List<CartItem> items) {
        double sum = 0.0;
        for (int i = 0; i < items.size(); i++) {
            CartItem ci = items.get(i);
            double line = ci.lineTotal();
            sum += line;
            System.out.printf("%d) %s x%d  (%.2f) -> %.2f%n",
                    i + 1,
                    ci.getProduct().getName(),
                    ci.getQty(),
                    ci.getUnitPrice(),
                    line);
        }
        System.out.printf("ยอดรวมย่อย : %.2f บาท%n", sum);
    }

    private Integer parseIndex(String s, int max) {
        try {
            int v = Integer.parseInt(s.trim());
            if (v >= 1 && v <= max)
                return v;
        } catch (Exception ignored) {
        }
        return null;
    }

    private Integer parsePositiveOrZero(String s) {
        try {
            int v = Integer.parseInt(s.trim());
            if (v >= 0)
                return v;
        } catch (Exception ignored) {
        }
        return null;
    }

    // ใช้คูปองได้ "ครั้งเดียว" ต่อคำสั่งซื้อ: ลูปรับโค้ดจนกว่าจะผ่าน
    // หรือผู้ใช้กดเว้นว่าง


    // แสดงคูปองทั้งหมด + ลูปกรอกโค้ด (เว้นว่าง = ไม่ใช้)
    // ถ้าโค้ดผิด/หมดอายุ/ไม่เข้าเงื่อนไข -> แจ้งและวนกรอกใหม่


    // ====== ฟังก์ชันกลาง: Checkout สำหรับทุกทางเข้า ======
    private void handleCheckout(Scanner sc, Member m, Store store, List<CartItem> snap) {
        if (snap == null || snap.isEmpty()) {
            System.out.println("ไม่มีสินค้า");
            return;
        }

        // 1) สรุปยอดก่อนส่วนลด/ค่าส่ง/ภาษี
        double subtotal = 0.0;
        for (CartItem ci : snap)
            subtotal += ci.lineTotal();
        System.out.printf("ยอดสั่งซื้อ (ก่อนส่วนลด/ค่าส่ง/ภาษี): %.2f บาท%n", subtotal);

        // 2) คูปอง (1 ผู้ใช้ ใช้ 1 ครั้งตลอดอายุบัญชี) -> ให้ได้ "discountAmount"
        double discountAmount = 0.0; // <<-- ประกาศตัวนี้
        Coupon chosen = null;

        System.out.println("ต้องการใช้คูปองหรือไม่");
        System.out.println("[1] ใช้คูปอง");
        System.out.println("[2] ไม่ใช้คูปอง");
        int csel = InputHelper.askInt("กรุณาเลือกเมนูที่คุณต้องการ : ");

        if (csel == 1) {
            showCouponsForSnapshot(snap);
            System.out.println("พิมพ์รหัสคูปอง (เว้นว่าง = ไม่ใช้คูปอง)");
            while (true) {
                String code = InputHelper.ask("รหัสคูปอง : ").trim();
                if (code.isEmpty())
                    break;

                Coupon coupon = store.getCoupon(code);
                if (coupon == null) {
                    System.out.println("ไม่พบคูปอง กรุณาลองใหม่");
                    continue;
                }

                // ห้ามใช้ซ้ำทั้งชีวิตบัญชี
                if (store.hasUserUsedCoupon(code, m.getUsername())) {
                    System.out.println("คูปองนี้ถูกใช้โดยบัญชีของคุณไปแล้ว ใช้ได้ 1 ครั้งต่อผู้ใช้/คำสั่งซื้อ โปรดใช้คูปองอื่น" );
                    continue;
                }

                double eligible = coupon.eligibleSubtotalForItems(snap);
                if (!coupon.usable(eligible, LocalDate.now())) {
                    System.out.println("คูปองไม่พร้อมใช้งาน กรุณาลองใหม่");
                    continue;
                }

                // คำนวณ “จำนวนเงินส่วนลด”
                if (coupon.getDiscountType() == DiscountType.PERCENT) {
                    discountAmount = eligible * (coupon.getValue() / 100.0);
                } else {
                    discountAmount = Math.min(eligible, coupon.getValue());
                }
                discountAmount = Math.max(0.0, Math.min(subtotal, discountAmount)); // กันล้น

                chosen = coupon;
                // ล็อกว่าผู้ใช้นี้ใช้โค้ดนี้แล้ว (กันใช้ซ้ำในอนาคต)
                store.recordCouponUseByUser(code, m.getUsername());

                System.out.printf(
                        "ใช้คูปองสำเร็จ: %s (- %.2f จากยอดที่เข้าเงื่อนไข %.2f) — ระบบได้ล็อกคูปองให้คำสั่งซื้อของคุณแล้ว%n",
                        code, discountAmount, eligible);
                break;
            }
        }

        // 3) วิธีรับสินค้า/ที่อยู่
        System.out.println("เลือกรูปแบบการรับสินค้า");
        System.out.println("[1] รับที่ร้าน");
        System.out.println("[2] จัดส่งถึงที่ (ค่าส่ง 20 บาท)");
        int how = InputHelper.askInt("กรุณาเลือกเมนูที่คุณต้องการ : ");
        double shippingFee = (how == 2 ? 20.0 : 0.0);

        String address = m.getAddress();
        if (how == 2) {
            System.out.printf("ที่อยู่จัดส่งปัจจุบัน : %s%n", address);
            System.out.println("[1] ใช้ที่อยู่นี้");
            System.out.println("[2] แก้ไขที่อยู่ใหม่");
            int a = InputHelper.askInt("กรุณาเลือกเมนูที่คุณต้องการ : ");
            if (a == 2) {
                String newAddr = InputHelper.ask("กรอกที่อยู่ใหม่: ").trim();
                if (!newAddr.isEmpty()) {
                    address = newAddr;
                    m.setAddress(address);
                }
            }
        }

        // 4) วันรับสินค้า
        System.out.println("กำหนดวันรับสินค้า");
        System.out.println("[1] รับใน 2 วันจากวันนี้");
        System.out.println("[2] ระบุวันเอง (รูปแบบ dd/MM/yyyy)");
        int dsel = InputHelper.askInt("กรุณาเลือกเมนูที่คุณต้องการ : ");
        LocalDate delivery = (dsel == 1) ? LocalDate.now().plusDays(2) : null;
        if (dsel == 2) {
            String dx = InputHelper.ask("ระบุวัน (dd/MM/yyyy): ").trim();
            try {
                delivery = LocalDate.parse(dx, Formatter.DATE);
            } catch (Exception ignore) {
            }
        }

        // 5) คำนวณยอดสุทธิ
        double vat = (subtotal - discountAmount + shippingFee) * 0.07;
        double total = subtotal - discountAmount + shippingFee + vat;

        // 6) สร้างออเดอร์ (เวอร์ชันรับ “discountAmount”)
        Order o = store.createOrder(m, snap, address, delivery, shippingFee, discountAmount);
        if (o == null) {
            System.out.println("สร้างคำสั่งซื้อไม่สำเร็จ");
            return;
        }
        if (chosen != null) {
            o.applyCoupon(chosen); // แนบคูปอง (เพื่อประวัติ)
        }

        System.out.printf("สร้างคำสั่งซื้อเสร็จสิ้น%nOrder ID : %s%nยอดที่ต้องชำระ : %.2f บาท%n",
                o.getId(), total);
        System.out.println("[1] ชำระเงินทันที");
        System.out.println("[2] ชำระภายหลัง");
        int pay = InputHelper.askInt("กรุณาเลือกเมนูที่คุณต้องการ : ");
        if (pay == 1) {
            payOrderFlow(sc, m, store, o);
        } else {
            System.out.println("บันทึกคำสั่งซื้อแล้ว สามารถชำระได้ภายหลังในเมนู 'ชำระเงินค่าสินค้า'");
        }

        // 7) เคลียร์ตะกร้า
        m.getCart().clear();
    }

    private void showCouponsAndPromos() {
        System.out.println("===== คูปองและโปรโมชัน =====");

        System.out.println("[คูปอง]");
        LocalDate today = LocalDate.now();
        if (store.coupons.isEmpty()) {
            System.out.println("(ยังไม่มีคูปอง)");
        } else {
            for (Coupon c : store.coupons.values()) {
                String code = c.getCode();
                String name = c.getName();
                String benefit = (c.getDiscountType() == DiscountType.PERCENT)
                        ? (c.getValue() + "% ขั้นต่ำ " + c.getMinSpend())
                        : ("ลด " + c.getValue() + " บาท ขั้นต่ำ " + c.getMinSpend());
                LocalDate exp = c.getExpiry();
                String expTxt = (exp == null ? "-" : exp.toString());
                String remain = "-";
                if (exp != null) {
                    long days = java.time.temporal.ChronoUnit.DAYS.between(today, exp);
                    remain = (days >= 0 ? (days + " วัน") : "หมดอายุแล้ว");
                }
                String scope = (c.getAllowedSets() == null || c.getAllowedSets().isEmpty())
                        ? "สินค้าทั้งหมด"
                        : "เฉพาะเซ็ต: " + String.join(", ", c.getAllowedSets());
                System.out.printf("• CODE: %s | %s | %s | หมดอายุ: %s | เหลือ: %s | ขอบเขต: %s%n",
                        code, name, benefit, expTxt, remain, scope);
            }
        }

        System.out.println();
        System.out.println("[โปรโมชัน]");
        List<ProductSet> withPromo = new ArrayList<>();
        for (ProductSet s : store.sets.values()) {
            boolean active = (s.promoDescription != null)
                    && (s.promoStart == null || !today.isBefore(s.promoStart))
                    && (s.promoEnd == null || !today.isAfter(s.promoEnd));
            if (active)
                withPromo.add(s);
        }
        if (withPromo.isEmpty()) {
            System.out.println("(ยังไม่มีโปรโมชันในขณะนี้)");
            return;
        }
        for (ProductSet s : withPromo) {
            String start = (s.promoStart == null ? "-" : s.promoStart.toString());
            String end = (s.promoEnd == null ? "-" : s.promoEnd.toString());
            String desc = (s.promoDescription == null ? "-" : s.promoDescription);
            System.out.printf("• %s : %s%n", s.id, s.name);
            System.out.printf("  ราคา: %.2f บาท | ช่วงโปร: %s – %s%n", s.price, start, end);
            System.out.printf("  รายละเอียด: %s%n", desc);
        }
    }

    private void showOrderHistory() {
        System.out.println("===== ประวัติการสั่งซื้อ =====");
        List<Order> all = store.listOrdersSortedByCreated();
        boolean found = false;

        for (Order o : all) {
            if (!o.getMemberUsername().equals(member.getUsername()))
                continue;
            found = true;

            double total = (o.getTotal() > 0) ? o.getTotal() : o.totalAfterDiscount();
            System.out.printf("%s | ยอด %.2f | ชำระ:%s | จัดส่ง:%s | เวลา: %s%n",
                    o.getId(), total,
                    (o.getPayment() == null ? "UNPAID" : o.getPayment().getStatus().name()),
                    (o.getShipping() == null ? "-" : o.getShipping().getStatus().name()),
                    Formatter.fmt(o.getCreatedAt()));
            printOrderItems(o);
            System.out.println();
        }
        if (!found)
            System.out.println("ยังไม่มีประวัติคำสั่งซื้อ");
    }

    private void paymentsMenu() {
        System.out.println("รายการคำสั่งซื้อที่ยังไม่ชำระ");
        List<Order> mine = new ArrayList<>();

        for (Order o : store.listOrdersSortedByCreated()) {
            if (!o.getMemberUsername().equals(member.getUsername()))
                continue;

            // อ่านสถานะการชำระผ่าน Payment object ถ้ามี
            String payText = (o.getPayment() == null ? "UNPAID" : o.getPayment().getStatus().name());
            // เงื่อนไข: ยังไม่ได้ชำระหรือรอตรวจสอบ
            boolean unpaidOrPending = (o.getPayment() == null)
                    || "UNPAID".equals(payText)
                    || "PENDING_VERIFICATION".equals(payText);

            if (unpaidOrPending)
                mine.add(o);
        }

        if (mine.isEmpty()) {
            System.out.println("(ไม่มีรายการค้างชำระ)");
            return;
        }

        for (int i = 0; i < mine.size(); i++) {
            Order o = mine.get(i);
            double total = (o.getTotal() > 0) ? o.getTotal() : o.totalAfterDiscount();
            String payText = (o.getPayment() == null ? "UNPAID" : o.getPayment().getStatus().name());
            System.out.printf("[%d] %s | ยอด %.2f | สถานะชำระ:%s%n",
                    i + 1, o.getId(), total, payText);
            printOrderItems(o);
        }

        int sel = InputHelper.askInt("กรุณา เลือก เมนูที่ ต้องการ : ");
        if (sel <= 0 || sel > mine.size())
            return;
        Order target = mine.get(sel - 1);

        System.out.println("ข้อมูลการโอนเงิน");
        System.out.println("ธนาคาร : กสิกรไทย เลขที่ 123-4-56789-0");
        System.out.println("ชื่อบัญชี GM Flower Shop");
        System.out.println("โปรดชำระเงินภายใน 10 นาที จากนั้นแนบสลิปยืนยันการโอน");
        String ref = InputHelper.ask("กรุณากรอกหมายเลขอ้างอิงการโอน (หรือพิมพ์คำว่า 'แนบสลิป') : ").trim();

        boolean ok = store.submitPaymentRef(target, ref);
        if (ok) {
            System.out.println("ระบบได้รับข้อมูลการชำระเงินแล้ว (รอตรวจสอบโดยผู้จัดการ)");
        } else {
            System.out.println("ไม่สามารถบันทึกข้อมูลชำระเงินได้");
        }
    }



    private void accountInfo() {
        while (true) {
            System.out.println("===== ข้อมูลบัญชีผู้ใช้ =====");
            System.out.printf("ชื่อผู้ใช้ : %s%n", member.getUsername());
            System.out.printf("ชื่อ-นามสกุล : %s%n", member.getFullName());
            System.out.printf("เบอร์โทร : %s%n", member.getPhone());
            System.out.printf("ที่อยู่ : %s%n", member.getAddress());
            System.out.println();
            System.out.println("[1] แก้ไขที่อยู่");
            System.out.println("[0] กลับ");

            int sel = InputHelper.askInt("กรุณาเลือกเมนูที่คุณต้องการ : ");
            if (sel == 0)
                return;

            if (sel == 1) {
                String newAddr = InputHelper.ask("กรุณากรอกที่อยู่ใหม่ : ").trim();
                if (newAddr.isEmpty()) {
                    System.out.println("ที่อยู่ต้องไม่ว่าง");
                    continue;
                }
                member.setAddress(newAddr);
                if (currentMember != null && currentMember != member)
                    currentMember.setAddress(newAddr);
                store.upsertMember(member);
                System.out.println("อัปเดตที่อยู่เรียบร้อย");
                System.out.printf("ที่อยู่ใหม่ : %s%n%n", member.getAddress());
            } else {
                System.out.println("ตัวเลือกไม่ถูกต้อง");
            }
        }
    }

    private void showCouponsForSnapshot(List<CartItem> snap) {
        System.out.println("-----คูปอง-----");
        LocalDate today = LocalDate.now();
        for (Map.Entry<String, Coupon> e : store.coupons.entrySet()) {
            Coupon c = e.getValue();
            double eligible = c.eligibleSubtotalForItems(snap);
            boolean ok = c.usable(eligible, today);
            String status = ok ? "พร้อมใช้" : "ไม่พร้อมใช้งาน";
            String scope = (c.getAllowedSets() == null || c.getAllowedSets().isEmpty())
                    ? "สินค้าทั้งหมด"
                    : ("เฉพาะเซ็ต: " + String.join(",", c.getAllowedSets()));
            double expect = 0.0;
            if (ok) {
                if (c.getDiscountType() == DiscountType.PERCENT)
                    expect = eligible * (c.getValue() / 100.0);
                else
                    expect = Math.min(eligible, c.getValue());
            }
            String exp = (c.getExpiry() == null ? "-" : c.getExpiry().toString());
            if (c.getDiscountType() == DiscountType.PERCENT) {
                System.out.printf(
                        "- %s : %s | %d%% ขั้นต่ำ %d | หมดอายุ %s | ขอบเขต: %s | สถานะ: %s  | ส่วนลดคาดการณ์: %.2f%n",
                        c.getCode(), c.getName(), c.getValue(), c.getMinSpend(), exp, scope, status, expect);
            } else {
                System.out.printf(
                        "- %s : %s | ลด %d บาท ขั้นต่ำ %d | หมดอายุ %s | ขอบเขต: %s | สถานะ: %s  | ส่วนลดคาดการณ์: %.2f%n",
                        c.getCode(), c.getName(), c.getValue(), c.getMinSpend(), exp, scope, status, expect);
            }
        }
    }


    private void payOrderFlow(Scanner sc, Member m, Store store, Order o) {
        System.out.println("ข้อมูลการโอนเงิน");
        System.out.println("ธนาคาร : กสิกรไทย เลขที่ 123-4-56789-0");
        System.out.println("ชื่อบัญชี GM Flower Shop");
        System.out.println("โปรดชำระเงินภายใน 10 นาที จากนั้นแนบสลิปยืนยันการโอน");

        System.out.print("กรุณากรอกหมายเลขอ้างอิงการโอน (หรือพิมพ์คำว่า 'แนบสลิป') : ");
        String ref = sc.nextLine().trim();
        if (ref.equalsIgnoreCase("แนบสลิป"))
            ref = "SLIP-" + store.nextSlipId();

        if (!store.submitPaymentRef(o, ref)) {
            System.out.println("หมายเลขอ้างอิงซ้ำ หรือไม่ถูกต้อง");
            return;
        }
        System.out.println("ระบบได้รับข้อมูลการชำระเงินแล้ว (รอตรวจสอบโดยผู้จัดการ)");
        printReceipt(o, m);
        return;
    }

    // แทนที่เมธอดเดิมทั้งหมด
    private void printReceipt(Order o, Member m) {
        // ----- คำนวณยอดจากรายการในออเดอร์โดยตรง -----
        double subtotal = 0.0;
        if (o.getItems() != null) {
            for (OrderItem it : o.getItems()) {
                subtotal += (it.getUnitPrice() * it.getQty());
            }
        }
        // ถ้า Order เก็บส่วนลดเป็นจำนวนเงิน ใช้เลย; ไม่งั้นถือว่า 0
        double discount = (o.getDiscount() > 0) ? o.getDiscount() : 0.0;
        double shipping = (o.getShippingFee() > 0) ? o.getShippingFee() : 0.0;
        double vat = (subtotal - discount + shipping) * 0.07;
        double total = subtotal - discount + shipping + vat;

        // ป้องกัน # ซ้ำ
        String oid = o.getId(); // ใช้ตรง ๆ ได้เลย เพราะ id มี # อยู่แล้ว

        System.out.println("ใบเสร็จการชำระเงิน GM Flower");
        System.out.println("สาขา : ท่าโพธิ์ พิษณุโลก");
        System.out.println("เบอร์โทรร้าน : 096-950-4124");
        System.out.println("เลขที่ใบเสร็จ: INV-" + LocalDate.now().getYear() + "-"
                + String.format("%04d", java.time.LocalTime.now().toSecondOfDay() % 10000));
        System.out.println("วันที่ออก : " + Formatter.fmt(LocalDateTime.now()));
        System.out.println("Order ID : " + oid);
        System.out.println("ชื่อลูกค้า : " + m.getFullName());
        System.out.println("เบอร์โทร : " + m.getPhone());
        System.out.println("ที่อยู่จัดส่ง : " + o.getAddress());
        System.out.println("----------------------------------------");
        System.out.println("รายการสั่งซื้อ");
        if (o.getItems() != null) {
            for (OrderItem it : o.getItems()) {
                Product p = it.getProduct();
                String name = (p != null ? p.getName() : "สินค้าไม่ทราบชื่อ");
                System.out.printf("- %s  x%d  %.2f฿%n", name, it.getQty(), it.getUnitPrice() * it.getQty());
            }
        }
        System.out.printf("ยอดรวมย่อย : %.2f฿%n", subtotal);
        System.out.printf("ส่วนลด : %.2f฿%n", discount);
        System.out.printf("ค่าจัดส่ง : %.2f฿%n", shipping);
        System.out.printf("ภาษี 7%% : %.2f฿%n", vat);
        System.out.printf("รวมทั้งหมด : %.2f฿%n", total);
        System.out.println("----------------------------------------");
        System.out.println("ขอบคุณที่ใช้บริการ GM Flower Shop");
    }

    private void printOrderItems(Order o) {
        if (o.getItems() == null || o.getItems().isEmpty()) {
            System.out.println("  (ไม่มีรายการสินค้า)");
            return;
        }
        System.out.println("  รายการสินค้า:");
        for (OrderItem it : o.getItems()) {
            Product p = it.getProduct();
            String name = (p != null ? p.getName() : "สินค้าไม่ทราบชื่อ");
            double line = it.lineTotal();
            System.out.printf("    - %s  x%d  = %.2f (฿%.2f)%n",
                    name, it.getQty(), it.getUnitPrice(), line);
        }
    }

    // ===================== เมนูติดตามสถานะ + ยืนยันรับสินค้า ====================
    // (ไม่อ้างถึง Order.OrderStatus โดยตรง) 
    private void trackOrdersAndConfirm() {
        String username = currentMember.getUsername();

        // ดึงทุกออเดอร์แล้วกรองเฉพาะของผู้ใช้
        List<Order> orders = new ArrayList<>();
        for (Order o : store.listOrdersSortedByCreated()) {
            if (o.getMemberUsername().equals(username))
                orders.add(o);
        }
        if (orders.isEmpty()) {
            System.out.println("(ยังไม่มีคำสั่งซื้อของคุณ)");
            return;
        }

        // เรียงใหม่สุดก่อน
        orders.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        while (true) {
            System.out.println("=== ติดตามสถานะคำสั่งซื้อ ===");
            for (int i = 0; i < orders.size(); i++) {
                Order o = orders.get(i);
                String pay = (o.getPayment() == null ? "UNPAID" : o.getPayment().getStatus().name());
                String ship = (o.getShipping() == null ? "NONE" : o.getShipping().getStatus().name());
                String ord = (o.getOrderStatus() == null ? "-" : o.getOrderStatus().toString()); // อย่าอ้าง enum type
                System.out.printf("[%d] %s | เวลา %s | ราคา %,.2f | ชำระ:%s | จัดส่ง:%s | สถานะ:%s%n",
                        i + 1,
                        o.getId(),
                        Formatter.fmt(o.getCreatedAt()),
                        (o.getTotal() > 0 ? o.getTotal() : o.totalAfterDiscount()),
                        pay, ship, ord);
            }
            System.out.println("[0] กลับ");
            int sel = InputHelper.askInt("กรุณาเลือกหมายเลข Order ที่ต้องการดูรายละเอียด/ยืนยันรับสินค้า : ");
            if (sel == 0)
                return;
            if (sel < 1 || sel > orders.size()) {
                System.out.println("หมายเลขไม่ถูกต้อง");
                continue;
            }

            Order o = orders.get(sel - 1);
            showOrderDetailForMember(o);

            // ----- ตรวจว่า พร้อมยืนยันรับสินค้าไหม -----
            boolean canConfirm = false;
            try {
                // ถ้ามีเมธอดนี้ใน Order ใช้อันนี้เลย
                canConfirm = (boolean) Order.class.getMethod("canMemberConfirmReceived").invoke(o);
            } catch (Throwable ignore) {
                // fallback: DELIVERED และยังไม่ COMPLETE (เปรียบเทียบด้วย String)
                String ship = (o.getShipping() == null ? "NONE" : o.getShipping().getStatus().name());
                String ord = (o.getOrderStatus() == null ? "-" : o.getOrderStatus().toString());
                canConfirm = "DELIVERED".equals(ship) && !"COMPLETED".equals(ord);
            }

            System.out.println();
            if (canConfirm) {
                System.out.println("[1] ยืนยันการรับสินค้า");
                System.out.println("[0] กลับ");
                int act = InputHelper.askInt("เลือก : ");
                if (act == 1) {
                    boolean done = false;
                    // 1) พยายามเรียก o.confirmReceived() ถ้ามี
                    try {
                        Order.class.getMethod("confirmReceived").invoke(o);
                        done = true;
                    } catch (Throwable ignore) {
                    }

                    // 2) ถ้าไม่มี confirmReceived ให้ตั้งสถานะผ่าน reflection โดยไม่อ้าง enum type
                    if (!done) {
                        try {
                            Object curStatus = o.getOrderStatus(); // instance ของ enum ภายใน
                            if (curStatus != null) {
                                Class<?> enumCls = curStatus.getClass(); // ชนิด enum ภายใน
                                @SuppressWarnings("unchecked")
                                Object completed = java.lang.Enum.valueOf((Class<Enum>) enumCls, "COMPLETED");
                                // หาเมธอด setOrderStatus(enum) ถ้ามี
                                try {
                                    java.lang.reflect.Method setter = Order.class.getMethod("setOrderStatus", enumCls);
                                    setter.invoke(o, completed);
                                    done = true;
                                } catch (NoSuchMethodException nsme) {
                                    // ไม่มี setter -> เข้าถึงฟิลด์โดยตรง
                                    try {
                                        java.lang.reflect.Field fld = Order.class.getDeclaredField("orderStatus");
                                        fld.setAccessible(true);
                                        fld.set(o, completed);
                                        done = true;
                                    } catch (Throwable ignore2) {
                                    }
                                }
                            }
                        } catch (Throwable ignore3) {
                        }
                    }

                    // 3) แสตมป์เวลารับ (ถ้ามีฟิลด์)
                    try {
                        java.lang.reflect.Field f = Order.class.getDeclaredField("receivedAt");
                        f.setAccessible(true);
                        f.set(o, java.time.LocalDateTime.now());
                    } catch (Throwable ignore) {
                        /* ไม่มีฟิลด์ก็ข้าม */ }

                    store.upsertOrder(o);
                    System.out.println("ขอบคุณค่ะ/ครับ — ยืนยันรับสินค้าเรียบร้อย 🎉");
                } else if (act == 0) {
                    // กลับหน้ารายการ
                } else {
                    System.out.println("ตัวเลือกไม่ถูกต้อง");
                }
            } else {
                System.out.println(
                        "(ออเดอร์นี้ยังไม่พร้อมยืนยันรับสินค้า — ต้องเป็นสถานะจัดส่ง: DELIVERED และยังไม่ COMPLETE)");
                InputHelper.ask("กด Enter เพื่อกลับ");
            }
        }
    }

    private void showOrderDetailForMember(Order o) {
        System.out.println();
        System.out.printf("#%s | เวลา %s | ราคา %,.2f | ชำระ:%s | จัดส่ง:%s | สถานะ:%s%n",
                o.getId(),
                Formatter.fmt(o.getCreatedAt()),
                (o.getTotal() > 0 ? o.getTotal() : o.totalAfterDiscount()),
                (o.getPayment() == null ? "UNPAID" : o.getPayment().getStatus().name()),
                (o.getShipping() == null ? "NONE" : o.getShipping().getStatus().name()),
                o.getOrderStatus().name());
        System.out.println("รายการสินค้า:");
        printOrderItems(o);

        // ถ้ามีเวลายืนยันรับสินค้า ให้แสดง
        try {
            java.lang.reflect.Field f = o.getClass().getDeclaredField("receivedAt");
            f.setAccessible(true);
            Object dt = f.get(o);
            if (dt instanceof java.time.LocalDateTime ldt) {
                System.out.println("ยืนยันรับสินค้าเมื่อ : " + Formatter.fmt(ldt));
            }
        } catch (Exception ignored) {
        }
    }

}
