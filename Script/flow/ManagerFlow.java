package flow;

import model.*;
import service.Store;
import util.Formatter;
import util.InputHelper;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ManagerFlow {

    private final Scanner sc;
    private final Store store;
    private Manager currentManager;

    public ManagerFlow(Scanner sc, Store store, Manager manager) {
        this.sc = sc;
        this.store = store;
        this.currentManager = manager;
    }

    // เผื่อมีที่เรียกแบบเดิม
    public ManagerFlow(Scanner sc, Store store) {
        this(sc, store, null);
    }

    // ===================== เมนูหลัก (ผู้จัดการ) =====================
    public void menu() {
        while (true) {
            System.out.println("========== เมนูหลัก (ผู้จัดการ) ==========");
            System.out.println("[1] ดูรายการคำสั่งซื้อ");
            System.out.println("[2] ตรวจสอบ/ยืนยันการชำระเงิน");
            System.out.println("[3] อัปเดตสถานะการจัดส่ง");
            System.out.println("[4] จัดการคูปอง (ดู/สร้าง/ลบ)");
            System.out.println("[5] จัดการสินค้า");
            System.out.println("[6] รายงานการขายสินค้า");
            System.out.println("[7] ข้อมูลสมาชิก");
            System.out.println("[0] ออกจากเมนูผู้จัดการ");
            int c = InputHelper.askInt("กรุณาเลือกเมนูที่คุณต้องการ : ");
            switch (c) {
                case 1 -> showOrdersMenu();
                case 2 -> verifyPaymentMenu();
                case 3 -> updateShippingMenu();
                case 4 -> couponsAdminMenu();
                case 5 -> productsMenu();
                case 6 -> salesReportMenu();
                case 7 -> viewMembers();
                case 0 -> {
                    return;
                }
                default -> System.out.println("ตัวเลือกไม่ถูกต้อง");
            }
        }
    }

    // ===================== [1] ดูรายการคำสั่งซื้อ (และดูรายละเอียด)
    private void showOrdersMenu() {
        System.out.println("รายการคำสั่งซื้อทั้งหมด");

        List<Order> snapshot = new ArrayList<>(store.listOrdersSortedByCreated());
        if (snapshot.isEmpty()) {
            System.out.println("(ยังไม่มีคำสั่งซื้อ)");
            return;
        }

        for (int i = 0; i < snapshot.size(); i++) {
            Order o = snapshot.get(i);
            String pay = (o.getPayment() == null) ? "UNPAID" : o.getPayment().getStatus().name();
            String ship = (o.getShipping() == null) ? "NONE" : o.getShipping().getStatus().name();
            String when = Formatter.fmt(o.getCreatedAt());
            System.out.printf("[%d] %s | User:%s | รวม %.2f | ชำระ:%s | จัดส่ง:%s | เวลา:%s%n",
                    i + 1, o.getId(), o.getMemberUsername(), o.getTotal(), pay, ship, when);
        }
        System.out.println("[0] กลับ");

        int sel = InputHelper.askInt("กรุณาระบุหมายเลขคำสั่งซื้อเพื่อดูรายละเอียด : ");
        if (sel <= 0 || sel > snapshot.size())
            return;

        printOrderDetail(snapshot.get(sel - 1));
    }

    private void printOrderDetail(Order o) {
        System.out.println();
        System.out.println("-----------------------------");
        System.out.println("รายละเอียดของคำสั่งซื้อ");

        String username = o.getMemberUsername();
        Member m = store.members.get(username);

        System.out.printf("Username : %s%n", username);
        System.out.printf("หมายเลขคำสั่งซื้อ : %s%n", o.getId());
        System.out.printf("วันที่สั่งซื้อ : %s%n", Formatter.fmt(o.getCreatedAt()));
        System.out.printf("วันรับสินค้า : %s%n", (o.getDeliveryDate() == null ? "-" : o.getDeliveryDate().toString()));
        System.out.printf("ชื่อผู้รับ : %s%n", (m == null ? "-" : m.getFullName()));
        System.out.printf("เบอร์โทร : %s%n", (m == null ? "-" : m.getPhone()));
        System.out.printf("ที่อยู่จัดส่ง : %s%n",
                (o.getShipping() == null ? (o.getAddress() == null ? "-" : o.getAddress())
                        : o.getShipping().getAddress()));

        String payMethod = (o.getPayment() == null) ? "-" : o.getPayment().getMethod().name();
        String payRef = (o.getPayment() == null || o.getPayment().getRef() == null) ? "-" : o.getPayment().getRef();
        String payStat = (o.getPayment() == null) ? "UNPAID" : o.getPayment().getStatus().name();

        System.out.printf("ช่องทางการชำระเงิน : %s  Ref=%s%n", payMethod, payRef);
        System.out.printf("สถานะชำระเงิน : %s%n", payStat);

        String shipStat = (o.getShipping() == null) ? "NONE" : o.getShipping().getStatus().name();
        System.out.printf("สถานะการจัดส่ง : %s%n", shipStat);
        System.out.printf("สถานะคำสั่งซื้อ : %s%n", o.getOrderStatus().name());
        System.out.println();

        System.out.println("รายการสั่งซื้อ :");
        for (OrderItem it : o.getItems()) {
            String name = (it.getProduct() != null) ? it.getProduct().getName() : "-";
            System.out.printf("- %s  x%d  = %.2f (฿%.2f)%n",
                    name, it.getQty(), it.lineTotal(), it.getUnitPrice());
        }

        double discount = o.getDiscount();
        if (discount <= 0.0) {
            System.out.println("ส่วนลด : ไม่ได้ใช้ส่วนลด (0.00฿)");
        } else {
            System.out.printf("ส่วนลด : %.2f฿%n", discount);
        }
        System.out.printf("ค่าจัดส่ง : %.2f฿%n", o.getShippingFee());
        System.out.printf("ภาษี 7%% : %.2f฿%n", o.getVat());
        System.out.printf("รวมทั้งหมด : %.2f฿%n", o.getTotal());
        System.out.println("-----------------------------");
    }

    // ===================== [2] ตรวจสอบ/ยืนยันการชำระเงิน =====================
    private void verifyPaymentMenu() {
        List<Order> pending = store.listOrdersSortedByCreated().stream()
                .filter(o -> o.getPayment() != null
                        && (o.getPayment().getStatus() == PaymentStatus.PENDING_VERIFY))
                .collect(Collectors.toList());

        if (pending.isEmpty()) {
            System.out.println("ไม่มีรายการรอตรวจสอบการชำระเงิน");
            return;
        }

        System.out.println("รายการรอตรวจสอบการชำระเงิน");
        for (int i = 0; i < pending.size(); i++) {
            Order o = pending.get(i);
            System.out.printf("[%d] %s | User:%s | ยอด %.2f | Ref=%s | เวลา:%s%n",
                    i + 1, o.getId(), o.getMemberUsername(), o.getTotal(),
                    (o.getPayment().getRef() == null ? "-" : o.getPayment().getRef()),
                    Formatter.fmt(o.getCreatedAt()));
        }
        System.out.println("[0] กลับ");
        int pick = InputHelper.askInt("เลือกหมายเลขรายการ (0=กลับ) : ");
        if (pick <= 0 || pick > pending.size())
            return;

        Order target = pending.get(pick - 1);
        System.out.printf("ตรวจรายการ %s | ยอด %.2f | Ref=%s%n",
                target.getId(), target.getTotal(),
                (target.getPayment().getRef() == null ? "-" : target.getPayment().getRef()));

        System.out.println("[1] อนุมัติการชำระเงิน");
        System.out.println("[2] ปฏิเสธ/ยกเลิกการชำระเงิน");
        System.out.println("[0] ยกเลิก");
        int act = InputHelper.askInt("เลือก : ");
        if (act == 1) {
            target.getPayment().markApproved();
            target.markPaid();
            System.out.println("อนุมัติการชำระเงินเรียบร้อย");
        } else if (act == 2) {
            target.getPayment().markRejected();
            System.out.println("ทำรายการปฏิเสธการชำระเงินเรียบร้อย");
        }
    }

    // ===================== [3] อัปเดตสถานะการจัดส่ง =====================
    private void updateShippingMenu() {
        // โหลดออเดอร์ทั้งหมด (เรียงเวลาสร้าง)
        List<Order> orders = new ArrayList<>(store.listOrdersSortedByCreated());
        if (orders.isEmpty()) {
            System.out.println("(ยังไม่มีคำสั่งซื้อ)");
            return;
        }

        // แสดงรายการสรุปให้เลือก
        System.out.println("รายการคำสั่งซื้อสำหรับอัปเดตสถานะจัดส่ง");
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            String pay = (o.getPayment() == null) ? "UNPAID" : o.getPayment().getStatus().name();
            String ship = (o.getShipping() == null) ? "NONE" : o.getShipping().getStatus().name();
            System.out.printf("[%d] %s | User:%s | รวม %.2f | ชำระ:%s | จัดส่ง:%s%n",
                    i + 1, o.getId(), o.getMemberUsername(), o.getTotal(), pay, ship);
        }
        System.out.println("[0] กลับ");
        int sel = InputHelper.askInt("เลือกหมายเลขรายการที่ต้องการอัปเดตสถานะจัดส่ง : ");
        if (sel == 0)
            return;
        if (sel < 1 || sel > orders.size()) {
            System.out.println("หมายเลขไม่ถูกต้อง");
            return;
        }

        Order o = orders.get(sel - 1);

        // ให้แน่ใจว่ามี Shipping object
        Shipping s = o.getShipping();
        if (s == null) {
            // แนบ shipping ใหม่เบื้องต้น (ใช้ที่อยู่เดิมจากออเดอร์ถ้ามี)
            String addr = (o.getAddress() == null || o.getAddress().isBlank()) ? "-" : o.getAddress();
            s = new Shipping(store.nextShippingId(), addr, o.getMemberUsername(), "");
            // ตั้งค่าเริ่มต้นเป็นรอจัดส่ง
            try {
                s.markPending();
            } catch (Throwable t) {
                // fallback ถ้าไม่มี markPending() ในรุ่นนี้ ให้ใช้ setStatus โดยตรง
                try {
                    s.setStatus(ShippingStatus.PENDING);
                } catch (Throwable ignore) {
                }
            }
            o.attachShipping(s);
            store.upsertOrder(o);
        }

        // แสดงรายละเอียดปัจจุบัน
        System.out.println("\n=== รายละเอียดการจัดส่งปัจจุบัน ===");
        System.out.printf("Order : %s%n", o.getId());
        System.out.printf("ที่อยู่จัดส่ง : %s%n", (s.getAddress() == null ? "-" : s.getAddress()));
        System.out.printf("ผู้ติดต่อ : %s  โทร: %s%n",
                (s.getContactName() == null ? "-" : s.getContactName()),
                (s.getContactPhone() == null ? "-" : s.getContactPhone()));
        System.out.printf("บริษัทขนส่ง : %s  เลขพัสดุ/Tracking : %s%n",
                (s.getCarrier() == null ? "-" : s.getCarrier()),
                (s.getTrackingNo() == null ? "-" : s.getTrackingNo()));
        System.out.printf("สถานะจัดส่ง : %s%n",
                (s.getStatus() == null ? "NONE" : s.getStatus().name()));
        System.out.println();

        // เมนูอัปเดต
        System.out.println("[1] ตั้งสถานะ: รอจัดส่ง (PENDING)");
        System.out.println("[2] ตั้งสถานะ: กำลังจัดส่ง (IN_TRANSIT)");
        System.out.println("[3] ตั้งสถานะ: จัดส่งสำเร็จ (DELIVERED)");
        System.out.println("[4] แก้ไขที่อยู่จัดส่ง");
        System.out.println("[5] ระบุบริษัทขนส่ง (Carrier) / เลขติดตามพัสดุ (Tracking)");
        System.out.println("[0] ยกเลิก");
        int act = InputHelper.askInt("เลือก : ");

        switch (act) {
            case 1 -> {
                // PENDING
                try {
                    s.markPending();
                } catch (Throwable t) {
                    try {
                        s.setStatus(ShippingStatus.PENDING);
                    } catch (Throwable ignore) {
                    }
                }
                System.out.println("อัปเดตเป็น 'รอจัดส่ง (PENDING)' เรียบร้อย");
            }
            case 2 -> {
                // IN_TRANSIT
                try {
                    s.markInTransit();
                } catch (Throwable t) {
                    try {
                        s.setStatus(ShippingStatus.IN_TRANSIT);
                    } catch (Throwable ignore) {
                    }
                }
                System.out.println("อัปเดตเป็น 'กำลังจัดส่ง (IN_TRANSIT)' เรียบร้อย");
            }
            case 3 -> {
                // DELIVERED
                try {
                    s.markDelivered();
                } catch (Throwable t) {
                    try {
                        s.setStatus(ShippingStatus.DELIVERED);
                    } catch (Throwable ignore) {
                    }
                }
                System.out.println("อัปเดตเป็น 'จัดส่งสำเร็จ (DELIVERED)' เรียบร้อย");
            }
            case 4 -> {
                String newAddr = InputHelper.ask("กรอกที่อยู่จัดส่งใหม่ : ");
                if (newAddr != null && !newAddr.isBlank()) {
                    s.setAddress(newAddr.trim());
                    System.out.println("บันทึกที่อยู่จัดส่งใหม่เรียบร้อย");
                } else {
                    System.out.println("ยกเลิก (ที่อยู่ว่าง)");
                }
            }
            case 5 -> {
                String carrier = InputHelper.ask("บริษัทขนส่ง (เช่น Kerry/J&T/ไปรษณีย์ไทย) : ");
                String tracking = InputHelper.ask("เลขติดตามพัสดุ (tracking no.) : ");
                try {
                    s.setCarrier(carrier == null ? "" : carrier.trim());
                } catch (Throwable ignore) {
                }
                try {
                    s.setTrackingNo(tracking == null ? "" : tracking.trim());
                } catch (Throwable ignore) {
                }
                System.out.println("บันทึก Carrier/Tracking เรียบร้อย");
            }
            case 0 -> {
                System.out.println("ยกเลิก");
                return;
            }
            default -> System.out.println("ตัวเลือกไม่ถูกต้อง");
        }

        // เซฟกลับสโตร์
        store.upsertOrder(o);
    }

    // ===================== [4] จัดการคูปอง (ดู/สร้าง/ลบ) =====================
    // ===================== [4] จัดการคูปอง (ดู/สร้าง/ลบ/รายชื่อผู้ใช้)
    private void couponsAdminMenu() {
        while (true) {
            System.out.println("----- จัดการคูปอง -----");
            System.out.println("[1] แสดงคูปองทั้งหมด (พร้อมสถิติ)");
            System.out.println("[2] สร้างคูปองใหม่");
            System.out.println("[3] ลบคูปองตามรหัส");
            System.out.println("[4] ดูรายชื่อสมาชิกที่ใช้คูปองตามรหัส");
            System.out.println("[0] กลับ");
            int sel = InputHelper.askInt("กรุณา เลือก เมนู : ");
            switch (sel) {
                case 1 -> renderCouponsTable(); // ตารางสวยงามตามต้นแบบ
                case 2 -> createCoupon();
                case 3 -> deleteCoupon();
                case 4 -> printCouponUsedBy();
                case 0 -> {
                    return;
                }
                default -> System.out.println("ตัวเลือกไม่ถูกต้อง");
            }
        }
    }

    /** ตารางคูปอง (หัว/เส้นคั่น/จัดคอลัมน์) */
    /** ตารางคูปอง (หัว/เส้นคั่น/จัดคอลัมน์ + แก้เปอร์เซ็นต์) */
    private void renderCouponsTable() {
        if (store.coupons.isEmpty()) {
            System.out.println("(ยังไม่มีคูปอง)");
            return;
        }

        java.time.format.DateTimeFormatter DMY = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // หัวตาราง
        String h1 = pad("รหัสคูปอง", 12);
        String h2 = pad("ชื่อคูปอง", 28);
        String h3 = pad("ส่วนลด", 10);
        String h4 = pad("ขั้นต่ำ", 10);
        String h5 = pad("หมดอายุ", 12);
        String h6 = "ใช้งานแล้ว (สถิติ)";
        System.out.printf("%s | %s | %s | %s | %s | %s%n", h1, h2, h3, h4, h5, h6);
        System.out.println(
                repeat('-', 12) + "-+-" +
                        repeat('-', 28) + "-+-" +
                        repeat('-', 10) + "-+-" +
                        repeat('-', 10) + "-+-" +
                        repeat('-', 12) + "-+-" +
                        repeat('-', 18));

        // เรียงตามรหัสให้อ่านง่าย
        List<Coupon> list = new ArrayList<>(store.coupons.values());
        list.sort(Comparator.comparing(Coupon::getCode, String.CASE_INSENSITIVE_ORDER));

        for (Coupon c : list) {
            String code = pad(c.getCode(), 12);
            String name = pad(nullToDash(c.getName()), 28);

            // ส่วนลด: ทำให้มี "%" เป็น "ค่า" เอง เพื่อไม่ให้ printf กินเครื่องหมาย
            String discountTxt = (c.getDiscountType() == DiscountType.PERCENT)
                    ? (c.getValue() + "%")
                    : (String.format("%,d บาท", c.getValue()));
            String disc = pad(discountTxt, 10);

            String minTxt = (c.getMinSpend() <= 0 ? "-" : String.format("%,d", c.getMinSpend()));
            String min = pad(minTxt, 10);

            String expTxt = (c.getExpiry() == null ? "-" : c.getExpiry().format(DMY));
            String exp = pad(expTxt, 12);

            // === สถิติผู้ใช้: usedUsers / totalMembers (xx.x%) ===
            int totalMembers = 0;
            try {
                totalMembers = (store.members == null ? 0 : store.members.size());
            } catch (Throwable ignore) {
            }

            int usedUsers = 0;
            try {
                java.util.Set<String> users = store.couponUsers(c.getCode());
                usedUsers = (users == null ? 0 : users.size());
            } catch (Throwable ignore) {
                usedUsers = 0;
            }

            // คิดอัตรา % จาก "จำนวนสมาชิกทั้งหมด"
            double pct = (totalMembers > 0) ? (usedUsers * 100.0 / totalMembers) : 0.0;
            // ใช้ %% เพื่อพิมพ์เครื่องหมายเปอร์เซ็นต์
            String stat = String.format("%d / %d (%.1f%%)", usedUsers, totalMembers, pct);

            // พิมพ์แถว
            System.out.printf("%s | %s | %s | %s | %s | %s%n", code, name, disc, min, exp, stat);

        }
    }

    /** เมนูแสดงรายชื่อสมาชิกที่ใช้คูปอง */
    private void printCouponUsedBy() {
        String code = InputHelper.ask("กรุณาใส่รหัสคูปอง: ").trim();
        Coupon c = store.getCoupon(code);
        if (c == null) {
            System.out.println("ไม่พบรหัสคูปอง");
            return;
        }
        Set<String> users = store.couponUsers(code); // ต้องมีใน Store
        System.out.println();
        System.out.printf("คูปอง  %s  ถูกใช้โดยสมาชิกจำนวน %d คน%n", code, users.size());
        int i = 1;
        for (String u : users) {
            System.out.printf("%d) %s%n", i++, u);
        }
    }

    /** (คงเดิม) สร้างคูปองใหม่ */
    private void createCoupon() {
        String code = InputHelper.ask("รหัสคูปอง: ").trim();
        if (code.isEmpty()) {
            System.out.println("รหัสว่าง");
            return;
        }
        if (store.coupons.containsKey(code)) {
            System.out.println("มีรหัสนี้แล้ว");
            return;
        }

        String name = InputHelper.ask("ชื่อคูปอง: ").trim();
        System.out.println("ประเภทส่วนลด: [1] เปอร์เซ็นต์  [2] จำนวนเงินคงที่");
        int t = InputHelper.askInt("เลือก: ");
        DiscountType type = (t == 2 ? DiscountType.AMOUNT : DiscountType.PERCENT);

        int value = InputHelper.askInt(type == DiscountType.PERCENT ? "ส่วนลด (%): " : "ส่วนลด (บาท): ");
        int min = InputHelper.askInt("ยอดขั้นต่ำ (บาท): ");
        String expStr = InputHelper.ask("วันหมดอายุ (yyyy-MM-dd) เว้นว่าง=ไม่กำหนด: ").trim();
        LocalDate exp = null;
        if (!expStr.isEmpty()) {
            try {
                exp = LocalDate.parse(expStr);
            } catch (Exception ignore) {
                System.out.println("รูปแบบวันที่ไม่ถูกต้อง (ข้าม)");
            }
        }

        String allow = InputHelper.ask("จำกัดเฉพาะเซ็ต (เช่น SET01,SET02) เว้นว่าง=ทั้งหมด: ").trim();
        List<String> allowedSets = new ArrayList<>();
        if (!allow.isEmpty()) {
            for (String s : allow.split(",")) {
                String x = s.trim();
                if (!x.isEmpty())
                    allowedSets.add(x);
            }
        }

        Coupon cpn = new Coupon(code, name, value, min, exp);
        if (exp != null)
            cpn.setExpiry(exp);
        if (!allowedSets.isEmpty())
            cpn.setAllowedSets(allowedSets);

        store.addCoupon(cpn);
        System.out.println("สร้างคูปองเรียบร้อย");
    }

    /** (คงเดิม) ลบคูปอง */
    private void deleteCoupon() {
        String code = InputHelper.ask("ระบุรหัสคูปองที่ต้องการลบ: ").trim();
        if (code.isEmpty())
            return;
        boolean ok = store.deleteCoupon(code);
        System.out.println(ok ? "ลบคูปองเรียบร้อย" : "ไม่พบรหัสคูปอง");
    }

    /* ===================== helpers สำหรับตาราง ===================== */
    private static String repeat(char ch, int n) {
        if (n <= 0)
            return "";
        char[] a = new char[n];
        Arrays.fill(a, ch);
        return new String(a);
    }



    /** pad ข้อความทางขวา (เติม space) */
    private static String pad(String s, int width) {
        if (s == null)
            s = "-";
        if (s.length() >= width)
            return s.substring(0, width);
        return s + repeat(' ', width - s.length());
    }

    private static String nullToDash(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    // ===================== [5] จัดการสินค้า =====================
    private void productsMenu() {
        while (true) {
            System.out.println("===== จัดการสินค้า =====");
            System.out.println("[1] แสดงรายการสินค้า");
            System.out.println("[2] เพิ่มสินค้าใหม่");
            System.out.println("[3] แก้ไขข้อมูลสินค้า");
            System.out.println("[4] ลบสินค้า");
            System.out.println("[5] ปรับจำนวนสินค้าคงเหลือ (รับเข้า/ตัดออก)");
            System.out.println("[6] คลังวัตถุดิบ");
            System.out.println("[7] จัดการเซ็ตสินค้า");
            System.out.println("[0] กลับ");
            int sel = InputHelper.askInt("กรุณาเลือกเมนูที่คุณต้องการ : ");

            switch (sel) {
                case 1 -> showProductList();
                case 2 -> addNewProduct();
                case 3 -> editProductInfo();
                case 4 -> deleteProduct();
                case 5 -> adjustProductStock();
                case 6 -> manageMaterials();
                case 7 -> manageProductSets();
                case 0 -> {
                    return;
                }
                default -> System.out.println("ตัวเลือกไม่ถูกต้อง");
            }
        }
    }

    // [5.1] แสดงสินค้า
    private void showProductList() {
        System.out.println("----- รายการสินค้า -----");
        if (store.products.isEmpty()) {
            System.out.println("(ยังไม่มีสินค้า)");
            return;
        }
        List<String> ids = new ArrayList<>(store.products.keySet());
        Collections.sort(ids);
        for (String id : ids) {
            Product p = store.products.get(id);
            String cat = (p.getCategory() != null ? p.getCategory().name() : "-");
            System.out.printf("- [%s] %s | หมวด: %s | ราคา: %.2f | คงเหลือ: %d | วัสดุ: %s%n",
                    p.getId(), p.getName(), cat, p.getPrice(), p.getStock(),
                    (p.getMaterial() == null ? "-" : p.getMaterial()));
        }
    }

    // [5.2] เพิ่มสินค้า
    private void addNewProduct() {
        System.out.println("----- เพิ่มสินค้าใหม่ -----");
        String id = InputHelper.ask("รหัสสินค้า : ").trim();
        if (id.isEmpty()) {
            System.out.println("ยกเลิก (รหัสว่าง)");
            return;
        }

        String name = InputHelper.ask("ชื่อสินค้า : ").trim();
        ProductCategory cat = askProductCategory();
        double price = askDouble("ราคา : ");
        int stock = InputHelper.askInt("จำนวนเริ่มต้น : ");
        String material = InputHelper.ask("วัสดุ/ชนิดดอก (เช่น สด/ประดิษฐ์) : ").trim();

        try {
            store.createProduct(id, name, cat, price, stock, material);
            System.out.println("เพิ่มสินค้าเรียบร้อย");
        } catch (Exception e) {
            System.out.println("ผิดพลาด: " + e.getMessage());
        }
    }

    // [5.3] แก้ไขสินค้า
    private void editProductInfo() {
        System.out.println("----- แก้ไขข้อมูลสินค้า -----");
        String id = InputHelper.ask("รหัสสินค้าที่ต้องการแก้ไข : ").trim();
        Product p = store.getProduct(id);
        if (p == null) {
            System.out.println("ไม่พบรหัสสินค้านี้");
            return;
        }

        System.out.printf("ชื่อสินค้าเดิม: %s%n", p.getName());
        String name = InputHelper.ask("ชื่อสินค้าใหม่ (เว้นว่าง = ไม่เปลี่ยน) : ").trim();
        if (name.isEmpty())
            name = null;

        System.out.printf("หมวดเดิม: %s%n", (p.getCategory() == null ? "-" : p.getCategory().name()));
        System.out.println("เปลี่ยนหมวดหรือไม่ ?");
        System.out.println("[1] เปลี่ยน");
        System.out.println("[0] ไม่เปลี่ยน");
        int chCat = InputHelper.askInt("เลือก : ");
        ProductCategory cat = null;
        if (chCat == 1) {
            cat = askProductCategory();
        }

        System.out.printf("ราคาเดิม: %.2f%n", p.getPrice());
        String priceStr = InputHelper.ask("ราคาใหม่ (เว้นว่าง = ไม่เปลี่ยน) : ").trim();
        Double price = null;
        if (!priceStr.isEmpty()) {
            try {
                price = Double.parseDouble(priceStr);
            } catch (Exception ignored) {
            }
        }

        System.out.printf("สต็อกเดิม: %d%n", p.getStock());
        String stockStr = InputHelper.ask("จำนวนใหม่ (เว้นว่าง = ไม่เปลี่ยน) : ").trim();
        Integer stock = null;
        if (!stockStr.isEmpty()) {
            try {
                stock = Integer.parseInt(stockStr);
            } catch (Exception ignored) {
            }
        }

        System.out.printf("วัสดุเดิม: %s%n", (p.getMaterial() == null ? "-" : p.getMaterial()));
        String material = InputHelper.ask("วัสดุใหม่ (เว้นว่าง = ไม่เปลี่ยน) : ").trim();
        if (material.isEmpty())
            material = null;

        boolean ok = store.updateProduct(id, name, cat, price, stock, material);
        System.out.println(ok ? "อัปเดตสินค้าเรียบร้อย" : "อัปเดตไม่สำเร็จ");
    }

    // [5.4] ลบสินค้า
    private void deleteProduct() {
        System.out.println("----- ลบสินค้า -----");
        String id = InputHelper.ask("รหัสสินค้าที่ต้องการลบ : ").trim();
        boolean ok = store.deleteProduct(id);
        System.out.println(ok ? "ลบสินค้าเรียบร้อย" : "ไม่พบรหัสสินค้านี้");
    }

    // [5.5] ปรับสต็อก (ไม่พึ่ง store.adjustStock)
    private void adjustProductStock() {
        System.out.println("----- ปรับจำนวนสินค้าคงเหลือ -----");
        String id = InputHelper.ask("รหัสสินค้าที่ต้องการปรับ : ").trim();
        Product p = store.getProduct(id);
        if (p == null) {
            System.out.println("ไม่พบรหัสสินค้านี้");
            return;
        }

        System.out.printf("คงเหลือปัจจุบัน: %d%n", p.getStock());
        System.out.println("หมายเหตุ: ใส่เลขบวก = รับเข้า, เลขลบ = ตัดออก");
        int delta = InputHelper.askInt("จำนวนที่ต้องการปรับ (เช่น +10 หรือ -10) : ");

        int newQty = p.getStock() + delta;
        if (newQty < 0) {
            System.out.println("ปรับจำนวนไม่สำเร็จ (สต็อกจะติดลบ)");
            return;
        }

        p.setStock(newQty);
        System.out.printf("ปรับเรียบร้อย คงเหลือใหม่: %d%n", p.getStock());
    }

    // ===================== [5.6] คลังวัตถุดิบ =====================
    private void manageMaterials() {
        while (true) {
            System.out.println("===== คลังวัตถุดิบ =====");
            System.out.println("[1] แสดงรายการวัตถุดิบ");
            System.out.println("[2] เพิ่มวัตถุดิบใหม่");
            System.out.println("[3] ปรับจำนวน (รับเข้า/ตัดออก)");
            System.out.println("[4] ลบวัตถุดิบ");
            System.out.println("[0] กลับ");
            int sel = InputHelper.askInt("กรุณาเลือกเมนูที่คุณต้องการ : ");
            switch (sel) {
                case 1 -> showMaterials();
                case 2 -> addMaterial();
                case 3 -> adjustMaterial();
                case 4 -> deleteMaterial();
                case 0 -> {
                    return;
                }
                default -> System.out.println("ตัวเลือกไม่ถูกต้อง");
            }
        }
    }

    private void showMaterials() {
        System.out.println("--- รายการวัตถุดิบ ---");
        if (store.materials.isEmpty()) {
            System.out.println("(ยังไม่มีวัตถุดิบในคลัง)");
            return;
        }

        // แบ่งหมวด: ดอกไม้ (สด / ประดิษฐ์), กระดาษห่อ, ของตกแต่ง/อื่นๆ
        List<String> fresh = new ArrayList<>();
        List<String> artificial = new ArrayList<>();
        List<String> wrapping = new ArrayList<>();
        List<String> decor = new ArrayList<>();

        List<String> codes = new ArrayList<>(store.materials.keySet());
        Collections.sort(codes, String.CASE_INSENSITIVE_ORDER);

        for (String code : codes) {
            String display = store.materialNames.getOrDefault(code, code);
            String probe = display == null ? "" : display;
            // กำหนดกฎง่ายๆ โดยตรวจคำในชื่อแสดงผล
            if (probe.contains("สด") || (probe.contains("ดอก") && probe.contains("สด"))) {
                fresh.add(code);
            } else if (probe.contains("ประดิษฐ์")) {
                artificial.add(code);
            } else if (probe.contains("กระดาษ") || probe.toLowerCase().contains("wrap") || probe.toLowerCase().contains("paper")) {
                wrapping.add(code);
            } else {
                // ตกลงเป็นของตกแต่งหรืออื่น ๆ
                decor.add(code);
            }
        }

        // พิมพ์หมวด ดอกไม้ (แยกสด/ประดิษฐ์)
        System.out.println("1) ดอกไม้");
        if (!fresh.isEmpty()) {
            Collections.sort(fresh, String.CASE_INSENSITIVE_ORDER);
            System.out.println("  - สด:");
            for (String k : fresh) {
                String display = store.materialNames.getOrDefault(k, k);
                int q = store.materials.getOrDefault(k, 0);
                System.out.printf("    - %s (%s) : %d%n", display, k, q);
            }
        }
        if (!artificial.isEmpty()) {
            Collections.sort(artificial, String.CASE_INSENSITIVE_ORDER);
            System.out.println("  - ประดิษฐ์:");
            for (String k : artificial) {
                String display = store.materialNames.getOrDefault(k, k);
                int q = store.materials.getOrDefault(k, 0);
                System.out.printf("    - %s (%s) : %d%n", display, k, q);
            }
        }

        // พิมพ์หมวด กระดาษห่อ/อุปกรณ์
        if (!wrapping.isEmpty()) {
            Collections.sort(wrapping, String.CASE_INSENSITIVE_ORDER);
            System.out.println("2) กระดาษห่อช่อดอกไม้:");
            for (String k : wrapping) {
                String display = store.materialNames.getOrDefault(k, k);
                int q = store.materials.getOrDefault(k, 0);
                System.out.printf("  - %s (%s) : %d%n", display, k, q);
            }
        }

        // พิมพ์หมวด ของตกแต่ง/อื่นๆ
        if (!decor.isEmpty()) {
            Collections.sort(decor, String.CASE_INSENSITIVE_ORDER);
            System.out.println("3) ของตกแต่ง / อื่น ๆ:");
            for (String k : decor) {
                String display = store.materialNames.getOrDefault(k, k);
                int q = store.materials.getOrDefault(k, 0);
                System.out.printf("  - %s (%s) : %d%n", display, k, q);
            }
        }
    }

    private void addMaterial() {
        System.out.println("----- เพิ่มวัตถุดิบใหม่ -----");
        System.out.println("กรุณาเลือกหมวดของวัตถุดิบ:");
        System.out.println("[1] ดอกไม้ — สด");
        System.out.println("[2] ดอกไม้ — ประดิษฐ์");
        System.out.println("[3] กระดาษห่อช่อดอกไม้");
        System.out.println("[4] ของตกแต่ง / อื่น ๆ");
        int cat = InputHelper.askInt("เลือกหมวด (1-4) : ");

        String categoryLabel;
        switch (cat) {
            case 1 -> categoryLabel = "ดอกไม้ (สด)";
            case 2 -> categoryLabel = "ดอกไม้ (ประดิษฐ์)";
            case 3 -> categoryLabel = "กระดาษห่อช่อดอกไม้";
            case 4 -> categoryLabel = "ของตกแต่ง/อื่นๆ";
            default -> {
                System.out.println("ยกเลิก: หมวดไม่ถูกต้อง");
                return;
            }
        }

        String displayName = InputHelper.ask("ชื่อวัตถุดิบ (ชื่อแสดงผล): ").trim();
        if (displayName.isEmpty()) {
            System.out.println("ยกเลิก (ชื่อว่าง)");
            return;
        }

        int qty = InputHelper.askInt("จำนวนเริ่มต้น : ");

        String code = InputHelper.ask("ระบุรหัสวัตถุดิบ (เว้นว่าง = ใช้ชื่อเป็นรหัส): ").trim();
        if (code.isEmpty()) {
            code = displayName; // use display name as key by default
        }

        try {
            store.upsertMaterial(code, displayName + " [" + categoryLabel + "]", Math.max(0, qty));
            System.out.println("เพิ่มวัตถุดิบเรียบร้อย");
        } catch (Exception e) {
            System.out.println("ผิดพลาด: " + e.getMessage());
        }
    }

    private void adjustMaterial() {
        String code = InputHelper.ask("ชื่อวัตถุดิบที่ต้องการปรับ : ").trim();
        if (!store.materials.containsKey(code)) {
            System.out.println("ไม่พบรหัสวัตถุดิบนี้");
            return;
        }
        System.out.printf("คงเหลือปัจจุบัน: %d%n", store.materials.get(code));
        System.out.println("หมายเหตุ: ใส่เลขบวก = รับเข้า, เลขลบ = ตัดออก");
        int delta = InputHelper.askInt("จำนวนที่ต้องการปรับ (เช่น 50 หรือ -10) : ");
        boolean ok = store.adjustMaterial(code, delta);
        if (ok) {
            System.out.printf("ปรับเรียบร้อย คงเหลือใหม่: %d%n", store.materials.get(code));
        } else {
            System.out.println("ปรับจำนวนไม่สำเร็จ (อาจติดลบ)");
        }
    }

    private void deleteMaterial() {
        String code = InputHelper.ask("รหัสวัตถุดิบที่ต้องการลบ : ").trim();
        if (!store.materials.containsKey(code)) {
            System.out.println("ไม่พบรหัสวัตถุดิบนี้");
            return;
        }
        boolean ok = store.deleteMaterial(code);
        System.out.println(ok ? "ลบวัตถุดิบเรียบร้อย" : "ไม่สามารถลบได้");
    }

    // ===================== [5.7] จัดการเซ็ตสินค้า =====================
    private void manageProductSets() {
        while (true) {
            System.out.println("===== จัดการเซ็ตสินค้า =====");
            System.out.println("[1] แสดงรายการเซ็ต");
            System.out.println("[2] สร้างเซ็ตใหม่");
            System.out.println("[3] แก้ไขชื่อ/ราคาเซ็ต");
            System.out.println("[4] ลบเซ็ต");
            System.out.println("[5] จัดการโปรโมชันของเซ็ต");
            System.out.println("[0] กลับ");
            int sel = InputHelper.askInt("กรุณาเลือกเมนูที่คุณต้องการ : ");
            switch (sel) {
                case 1 -> listSets();
                case 2 -> createSet();
                case 3 -> editSet();
                case 4 -> deleteSet();
                case 5 -> manageSetPromotion();
                case 0 -> {
                    return;
                }
                default -> System.out.println("ตัวเลือกไม่ถูกต้อง");
            }
        }
    }

    private void listSets() {
        if (store.sets.isEmpty()) {
            System.out.println("(ยังไม่มีเซ็ตสินค้า)");
            return;
        }
        LocalDate today = LocalDate.now();
        List<String> ids = new ArrayList<>(store.sets.keySet());
        Collections.sort(ids);
        for (String id : ids) {
            ProductSet s = store.sets.get(id);
            boolean active = (s.promoDescription != null)
                    && (s.promoStart == null || !today.isBefore(s.promoStart))
                    && (s.promoEnd == null || !today.isAfter(s.promoEnd));
            String status = "พร้อมจำหน่าย";
            if (s.promoEnd != null && today.isAfter(s.promoEnd))
                status = "ชั่วคราวหมด";
            String promoFlag = active ? " | *** โปรโมชัน ***" : "";
            System.out.printf("- [%s] %s | ราคา: %.2f | %s%s%n", s.id, s.name, s.price, status, promoFlag);
        }
    }

    private void createSet() {
        String id = InputHelper.ask("รหัสเซ็ต : ").trim();
        if (id.isEmpty()) {
            System.out.println("ยกเลิก (รหัสว่าง)");
            return;
        }
        String name = InputHelper.ask("ชื่อเซ็ต : ").trim();
        double price = askDouble("ราคาขายเซ็ต : ");
        try {
            store.createProductSet(id, name, price);
            System.out.println("สร้างเซ็ตเรียบร้อย");
        } catch (Exception e) {
            System.out.println("ผิดพลาด: " + e.getMessage());
        }
    }

    private void editSet() {
        String id = InputHelper.ask("ระบุรหัสเซ็ตที่ต้องการแก้ไข : ").trim();
        ProductSet s = store.getProductSet(id);
        if (s == null) {
            System.out.println("ไม่พบรหัสเซ็ตนี้");
            return;
        }

        System.out.printf("ชื่อเดิม: %s%n", s.getName());
        String name = InputHelper.ask("ชื่อใหม่ (เว้นว่าง = ไม่เปลี่ยน) : ").trim();
        if (name.isEmpty())
            name = null;

        System.out.printf("ราคาเดิม: %.2f%n", s.getPrice());
        String priceStr = InputHelper.ask("ราคาใหม่ (เว้นว่าง = ไม่เปลี่ยน) : ").trim();
        Double price = null;
        if (!priceStr.isEmpty()) {
            try {
                price = Double.parseDouble(priceStr);
            } catch (Exception ignored) {
            }
        }
        boolean ok = store.updateProductSet(id, name, price);
        System.out.println(ok ? "อัปเดตเซ็ตเรียบร้อย" : "อัปเดตไม่สำเร็จ");
    }

    private void deleteSet() {
        String id = InputHelper.ask("ระบุรหัสเซ็ตที่ต้องการลบ : ").trim();
        boolean ok = store.deleteProductSet(id);
        System.out.println(ok ? "ลบเซ็ตเรียบร้อย" : "ไม่พบรหัสเซ็ตนี้");
    }

    private void manageSetPromotion() {
        String id = InputHelper.ask("ระบุรหัสเซ็ตที่ต้องตั้งค่าโปรโมชัน : ").trim();
        ProductSet s = store.getProductSet(id);
        if (s == null) {
            System.out.println("ไม่พบรหัสเซ็ตนี้");
            return;
        }

        System.out.println("[1] ตั้งค่าโปรโมชัน");
        System.out.println("[2] ลบโปรโมชันออกจากเซ็ต");
        System.out.println("[0] ยกเลิก");
        int c = InputHelper.askInt("เลือก : ");
        switch (c) {
            case 1 -> {
                String desc = InputHelper.ask("รายละเอียดโปรโมชัน : ").trim();
                String start = InputHelper.ask("วันเริ่ม (yyyy-MM-dd) : ").trim();
                String end = InputHelper.ask("วันสิ้นสุด (yyyy-MM-dd) : ").trim();
                try {
                    LocalDate ds = LocalDate.parse(start);
                    LocalDate de = LocalDate.parse(end);
                    store.setProductSetPromo(id, desc, ds, de);
                    System.out.println("ตั้งค่าโปรโมชันเรียบร้อย");
                } catch (Exception e) {
                    System.out.println("รูปแบบวันที่ไม่ถูกต้อง");
                }
            }
            case 2 -> {
                store.clearProductSetPromo(id);
                System.out.println("ลบโปรโมชันเรียบร้อย");
            }
            default -> {
            }
        }
    }

    // ===================== [6] รายงานการขายสินค้า =====================
    // ===================== รายงานการขายสินค้า (รับ dd/MM/yyyy)
    private void salesReportMenu() {
        final java.time.format.DateTimeFormatter DMY = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        final java.time.format.DateTimeFormatter DMY_HM = java.time.format.DateTimeFormatter
                .ofPattern("dd/MM/yyyy HH:mm");

        while (true) {
            System.out.println("----- รายงานการขายสินค้า -----");
            System.out.println("[1] แสดงรายการขายสินค้าทั้งหมด");
            System.out.println("[2] แสดงรายการขายสินค้าตามวันที่กำหนด");
            System.out.println("[0] กลับ");
            int sel = util.InputHelper.askInt("เลือก : ");
            if (sel == 0)
                return;

            // ดึงออเดอร์ทั้งหมด (เรียงใหม่สุดก่อนก็ได้)
            java.util.List<Order> all = new java.util.ArrayList<>(store.listOrdersSortedByCreated());

            if (sel == 1) {
                double sum = 0.0;
                System.out.println("----- รายการทั้งหมด -----");
                for (Order o : all) {
                    double total = (o.getTotal() > 0 ? o.getTotal() : o.totalAfterDiscount());
                    sum += total;
                    String when = o.getCreatedAt().format(DMY_HM);
                    System.out.printf("- %s | User:%s | รวมสุทธิ %,.2f | เวลา:%s%n",
                            o.getId(), o.getMemberUsername(), total, when);
                }
                System.out.printf("รวมยอดขายทั้งหมด: %,.2f บาท%n", sum);
                continue;
            }

            if (sel == 2) {
                // ===== รับช่วงวันแบบ dd/MM/yyyy =====
                java.time.LocalDate start = null, end = null;
                while (true) {
                    String s = util.InputHelper.ask("วันเริ่ม (dd/MM/yyyy): ").trim();
                    String e = util.InputHelper.ask("วันสิ้นสุด (dd/MM/yyyy): ").trim();
                    try {
                        start = java.time.LocalDate.parse(s, DMY);
                        end = java.time.LocalDate.parse(e, DMY);
                        if (end.isBefore(start)) {
                            System.out.println("วันสิ้นสุดต้องไม่ก่อนวันเริ่ม");
                            continue;
                        }
                        break;
                    } catch (Exception ex) {
                        System.out.println("รูปแบบวันที่ไม่ถูกต้อง กรุณากรอกใหม่ตามรูปแบบ dd/MM/yyyy");
                    }
                }

                // กรองออเดอร์ตามช่วงวัน (รวมวันเริ่ม/สิ้นสุด)
                double sum = 0.0;
                System.out.printf("----- รายการระหว่าง %s ถึง %s -----\n",
                        start.format(DMY), end.format(DMY));

                for (Order o : all) {
                    java.time.LocalDate d = o.getCreatedAt().toLocalDate();
                    if ((d.isEqual(start) || d.isAfter(start)) && (d.isEqual(end) || d.isBefore(end))) {
                        double total = (o.getTotal() > 0 ? o.getTotal() : o.totalAfterDiscount());
                        sum += total;
                        String when = o.getCreatedAt().format(DMY_HM);
                        System.out.printf("- %s | User:%s | รวมสุทธิ %,.2f | เวลา:%s%n",
                                o.getId(), o.getMemberUsername(), total, when);
                    }
                }
                System.out.printf("รวมยอดขายช่วงดังกล่าว: %,.2f บาท%n", sum);
                continue;
            }

            System.out.println("ตัวเลือกไม่ถูกต้อง");
        }
    }

    // ===================== [7] ข้อมูลสมาชิก =====================
    private void viewMembers() {
        if (store.members.isEmpty()) {
            System.out.println("(ยังไม่มีสมาชิก)");
            return;
        }
        List<Member> list = new ArrayList<>(store.members.values());
        for (int i = 0; i < list.size(); i++) {
            Member m = list.get(i);
            System.out.printf("[%d] %s | %s | %s%n", i + 1, m.getUsername(), m.getFullName(), m.getPhone());
        }
        System.out.println("[0] กลับ");
        int pick = InputHelper.askInt("ระบุหมายเลขสมาชิกเพื่อดูรายละเอียด: ");
        if (pick <= 0 || pick > list.size())
            return;

        Member m = list.get(pick - 1);
        System.out.println("\n----- รายละเอียดสมาชิก -----");
        System.out.println("Username : " + m.getUsername());
        System.out.println("ชื่อ-นามสกุล : " + m.getFullName());
        System.out.println("เบอร์โทร : " + m.getPhone());
        System.out.println("ที่อยู่ : " + m.getAddress());
        System.out.println();
    }

    // ===================== Helpers =====================
    private ProductCategory askProductCategory() {
        while (true) {
            System.out.println("เลือกหมวดสินค้า:");
            System.out.println("[1] SMALL");
            System.out.println("[2] MEDIUM");
            System.out.println("[3] LARGE");
            System.out.println("[4] WREATH");
            System.out.println("[5] VASE");
            int c = InputHelper.askInt("เลือก : ");
            switch (c) {
                case 1:
                    return ProductCategory.SMALL;
                case 2:
                    return ProductCategory.MEDIUM;
                case 3:
                    return ProductCategory.LARGE;
                case 4:
                    return ProductCategory.WREATH;
                case 5:
                    return ProductCategory.VASE;
                default:
                    System.out.println("ตัวเลือกไม่ถูกต้อง");
            }
        }
    }

    private double askDouble(String prompt) {
        while (true) {
            String s = InputHelper.ask(prompt);
            try {
                return Double.parseDouble(s.trim());
            } catch (Exception e) {
                System.out.println("กรุณากรอกตัวเลขให้ถูกต้อง");
            }
        }
    }

}
