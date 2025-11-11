package service;

import model.*;
import java.time.LocalDate;

public final class Seed {
        private Seed() {
        }

        public static void apply(Store store) {
                // ---------- Members ----------
                Member m1 = new Member("Chanapha", "1111", "Chanapha Artdon", "081-234-5678", "หอพักนิสิต มน");
                Member m2 = new Member("Chayutphong", "2222", "Chayutphong phumtub", "089-000-1111", "NU Village C-203");
                store.upsertMember(m1);
                store.upsertMember(m2);

                // ---------- Manager ----------
                // Manager(username, password, displayName, employeeNo)
                Manager mg = new Manager("manager", "12345", "GM Manager", "M-001");
                store.upsertManager(mg);

                // ---------- Products ----------
                // createProduct(id, name, category, price, stock, material)
                // --- SMALL ---
                store.createProduct("S1", "ช่อดอกไม้สด ดอกกุหลาบ (เล็ก)", ProductCategory.SMALL, 300, 20, "สด");
                store.createProduct("S2", "ช่อดอกไม้สด ดอกไฮเดรนเยีย (เล็ก)", ProductCategory.SMALL, 300, 15, "สด");
                store.createProduct("S3", "ช่อดอกไม้สด ดอกทานตะวัน (เล็ก)", ProductCategory.SMALL, 300, 18, "สด");
                store.createProduct("S4", "ช่อดอกไม้ประดิษฐ์ ดอกกุหลาบ (เล็ก)", ProductCategory.SMALL, 250, 18,
                                "ประดิษฐ์");
                store.createProduct("S5", "ช่อดอกไม้ประดิษฐ์ ดอกไฮเดรนเยีย (เล็ก)", ProductCategory.SMALL, 250, 20,
                                "ประดิษฐ์");
                store.createProduct("S6", "ช่อดอกไม้ประดิษฐ์ ดอกทานตะวัน (เล็ก)", ProductCategory.SMALL, 250, 17,
                                "ประดิษฐ์");

                // --- MEDIUM ---
                store.createProduct("M1", "ช่อดอกไม้สด ดอกลิลลี่ (กลาง)", ProductCategory.MEDIUM, 400, 12, "สด");
                store.createProduct("M2", "ช่อดอกไม้สด ดอกทิวลิป (กลาง)", ProductCategory.MEDIUM, 400, 10, "สด");
                store.createProduct("M3", "ช่อดอกไม้สด ดอกคาร์เนชัน (กลาง)", ProductCategory.MEDIUM, 400, 10, "สด");
                store.createProduct("M4", "ช่อดอกไม้ประดิษฐ์ ดอกลิลลี่ (กลาง)", ProductCategory.MEDIUM, 350, 10,
                                "ประดิษฐ์");
                store.createProduct("M5", "ช่อดอกไม้ประดิษฐ์ ดอกทิวลิป (กลาง)", ProductCategory.MEDIUM, 350, 10,
                                "ประดิษฐ์");
                store.createProduct("M6", "ช่อดอกไม้ประดิษฐ์ ดอกคาร์เนชัน (กลาง)", ProductCategory.MEDIUM, 350, 10,
                                "ประดิษฐ์");

                // --- LARGE ---
                store.createProduct("L1", "ช่อดอกไม้สด ดอกเดซี่ (ใหญ่)", ProductCategory.LARGE, 500, 8, "สด");
                store.createProduct("L2", "ช่อดอกไม้สด ดอกไลเซนทัส (ใหญ่)", ProductCategory.LARGE, 500, 8, "สด");
                store.createProduct("L3", "ช่อดอกไม้สด ดอกเบบี้เบรธ (ใหญ่)", ProductCategory.LARGE, 500, 8, "สด");
                store.createProduct("L4", "ช่อดอกไม้ประดิษฐ์ ดอกเดซี่ (ใหญ่)", ProductCategory.LARGE, 400, 8,
                                "ประดิษฐ์");
                store.createProduct("L5", "ช่อดอกไม้ประดิษฐ์ ดอกไลเซนทัส (ใหญ่)", ProductCategory.LARGE, 400, 8,
                                "ประดิษฐ์");
                store.createProduct("L6", "ช่อดอกไม้ประดิษฐ์ ดอกเบบี้เบรธ (ใหญ่)", ProductCategory.LARGE, 400, 8,
                                "ประดิษฐ์");

                // --- WREATH ---
                store.createProduct("W1", "พวงหรีด ดอกคาร์เนชัน", ProductCategory.WREATH, 900, 7, "สด");
                store.createProduct("W2", "พวงหรีด ดอกลิลลี่", ProductCategory.WREATH, 900, 7, "สด");
                store.createProduct("W3", "พวงหรีด ดอกกล้วยไม้", ProductCategory.WREATH, 1000, 7, "สด");
                store.createProduct("W4", "พวงหรีด ดอกคาร์เนชันประดิษฐ์", ProductCategory.WREATH, 800, 7, "ประดิษฐ์");
                store.createProduct("W5", "พวงหรีด ดอกลิลลี่ประดิษฐ์", ProductCategory.WREATH, 800, 7, "ประดิษฐ์");
                store.createProduct("W6", "พวงหรีด ดอกกล้วยไม้ประดิษฐ์", ProductCategory.WREATH, 900, 7, "ประดิษฐ์");
                store.createProduct("W7", "พวงหรีด พัดลมขนาดกลาง", ProductCategory.WREATH, 1000, 7, "ตัว");
                store.createProduct("W8", "พวงหรีด นาฬิกาแขวนผนัง", ProductCategory.WREATH, 800, 7, "เรือน");

                // --- VASE ---
                store.createProduct("V1", "แจกัน ดอกทิวลิป 15 ดอก", ProductCategory.VASE, 390, 9, "สด");
                store.createProduct("V2", "แจกัน ดอกไฮเดรนเยีย 15 ดอก", ProductCategory.VASE, 450, 9, "สด");
                store.createProduct("V3", "แจกัน ดอกทานตะวัน 15 ดอก", ProductCategory.VASE, 490, 9, "สด");
                store.createProduct("V4", "แจกัน ดอกทิวลิปประดิษฐ์ 15 ดอก", ProductCategory.VASE, 300, 9, "ประดิษฐ์");
                store.createProduct("V5", "แจกัน ดอกไฮเดรนเยียประดิษฐ์ 15 ดอก", ProductCategory.VASE, 400, 9,
                                "ประดิษฐ์");
                store.createProduct("V6", "แจกัน ดอกทานตะวันประดิษฐ์ 15 ดอก", ProductCategory.VASE, 420, 9, "ประดิษฐ์");

                // ===== วัตถุดิบเริ่มต้น (คลังวัสดุ) =====
                // โค้ด = คำอธิบาย -> จำนวนเริ่มต้น
                // store.upsertMaterial("WRAP_WHITE", "กระดาษห่อสีขาวคลาสสิก", 200);
                // store.upsertMaterial("WRAP_NEUTRAL", "กระดาษห่อโทนกลาง", 180);
                // store.upsertMaterial("WRAP_PASTEL", "กระดาษห่อโทนพาสเทล", 160);
                // store.upsertMaterial("RIBBON_RED", "โบว์สีแดง", 150);
                // store.upsertMaterial("RIBBON_PINK", "โบว์สีชมพู", 150);
                // store.upsertMaterial("RIBBON_WHITE", "โบว์สีขาว", 150);
                // store.upsertMaterial("CARD_GREETING", "การ์ดอวยพร", 300);
                // store.upsertMaterial("TEDDY_S", "ตุ๊กตาหมีขนาดเล็ก", 80);
                // store.upsertMaterial("CHOC_BOX", "ช็อกโกแลตแบบกล่อง", 90);
                // store.upsertMaterial("FOAM_OASIS", "โอเอซิส/ฐานจัดดอก", 120);
                // store.upsertMaterial("STICK_FLORAL", "ก้าน/ลวดจัดดอก", 500);

                // ตัวอย่างวัตถุดิบดอกไม้สด/ประดิษฐ์ (ถ้าคุณใช้ materials เป็นสต็อกรวม)
                // store.upsertMaterial("FRESH_ROSE", "ดอกกุหลาบสด (ก้าน)", 400);
                // store.upsertMaterial("FRESH_TULIP", "ดอกทิวลิปสด (ก้าน)", 300);
                // store.upsertMaterial("FRESH_LILY", "ดอกลิลลี่สด (ก้าน)", 220);
                // store.upsertMaterial("FRESH_DAISY", "ดอกเดซี่สด (ก้าน)", 260);
                // store.upsertMaterial("ART_ROSE", "ดอกกุหลาบประดิษฐ์ (ก้าน)", 180);
                // store.upsertMaterial("ART_TULIP", "ดอกทิวลิปประดิษฐ์ (ก้าน)", 170);

                // ---------- Product Sets (โปรโมชัน) ----------
                // ใช้ map สาธารณะของ Store ตามโครงสร้างปัจจุบัน
                // ===== สร้างเซ็ตสินค้า (ถ้ายังไม่มี) =====
                
                if (!store.sets.containsKey("SET01"))
                        store.sets.put("SET01", new ProductSet("SET01", "ชุดเปิดเทอมสดใส", 499.00));
                if (!store.sets.containsKey("SET02"))
                        store.sets.put("SET02",
                                        new ProductSet("SET02", "เซ็ตช่อดอกไม้ประดิษฐ์โรแมนติก(โทนสีแดง)", 459.00));
                if (!store.sets.containsKey("SET03"))
                        store.sets.put("SET03", new ProductSet("SET03", "เซ็ตแจกันดอกไม้สด", 799.00));
                if (!store.sets.containsKey("SET04"))
                        store.sets.put("SET04", new ProductSet("SET04", "เซ็ตแจกันสุดหรูหราหมาเห่า", 1099.00));
                if (!store.sets.containsKey("SET05"))
                        store.sets.put("SET05", new ProductSet("SET05", "เซ็ตของขวัญวาเลนไทน์", 899.00));
                if (!store.sets.containsKey("SET06"))
                        store.sets.put("SET06", new ProductSet("SET06", "เซ็ตเทศกาลปีใหม่", 599.00));

                // ========================= MATERIALS (วัตถุดิบ) =========================
                // 1) ดอกไม้ — สด
                store.materials.put("ดอกกุหลาบสด", 120);
                store.materials.put("ดอกไฮเดรนเยียสด", 80);
                store.materials.put("ดอกทานตะวันสด", 60);
                store.materials.put("ดอกลิลลี่สด", 70);
                store.materials.put("ดอกทิวลิปสด", 70);
                store.materials.put("ดอกคาร์เนชันสด", 70);
                store.materials.put("ดอกเดซี่สด", 50);
                store.materials.put("ดอกไลเซนทัสสด", 50);
                store.materials.put("ดอกเบบี้เบรธสด", 50);
                store.materials.put("ดอกกล้วยไม้สด", 40);

                // 2) ดอกไม้ — ประดิษฐ์
                store.materials.put("ดอกกุหลาบประดิษฐ์", 90);
                store.materials.put("ดอกไฮเดรนเยียประดิษฐ์", 90);
                store.materials.put("ดอกทานตะวันประดิษฐ์", 80);
                store.materials.put("ดอกลิลลี่ประดิษฐ์", 80);
                store.materials.put("ดอกทิวลิปประดิษฐ์", 80);
                store.materials.put("ดอกคาร์เนชันประดิษฐ์", 80);

                // 3) กระดาษห่อ/อุปกรณ์ตกแต่ง
                store.materials.put("กระดาษคราฟต์สีน้ำตาล", 100);
                store.materials.put("กระดาษโฮโลแกรม", 60);
                store.materials.put("กระดาษเนื้อด้าน สีขาว", 80);
                store.materials.put("กระดาษเนื้อด้าน สีชมพูอ่อน", 70);
                store.materials.put("กระดาษเนื้อด้าน ฟ้าอ่อน", 70);
                store.materials.put("กระดาษเนื้อด้าน ม่วง", 60);
                store.materials.put("กระดาษเนื้อด้าน ขาวครีม", 80);
                store.materials.put("กระดาษเนื้อด้าน ดำ", 60);

                store.materials.put("ตุ๊กตาหมีขนาดเล็ก", 40);
                store.materials.put("การ์ดอวยพร", 150);
                store.materials.put("ช็อกโกแลตแบบกล่อง", 35);

                // ========================= PRODUCT SETS (ส่วนประกอบเซ็ต)
                // =========================
                // หมายเหตุ: ProductSet มี fields: id, name, price, List<SetItem> items,
                // และฟิลด์โปรโมชัน promoDescription/promoStart/promoEnd
                // SetItem คาดว่าใช้งานแบบ new SetItem(String name, int qty)
                // ถ้า constructor ในโปรเจกต์คุณต่างจากนี้ ให้ปรับให้ตรงโครงสร้างเดิม (เช่น set
                // name/qty ทีละฟิลด์)

                // SET01 : ชุดเปิดเทอมสดใส *** โปรโมชัน ***
                {
                        ProductSet set = new ProductSet("SET01", "ชุดเปิดเทอมสดใส", 499.00);
                        set.addItem(new SetItem("ดอกกุหลาบสด", 12));
                        set.addItem(new SetItem("กระดาษคราฟต์สีน้ำตาล", 1));
                        set.addItem(new SetItem("การ์ดอวยพร", 1));
                        set.addItem(new SetItem("ตุ๊กตาหมีขนาดเล็ก", 1));
                        set.promoDescription = "ซื้อครบ 499 แถมตุ๊กตาหมีขนาดเล็ก 1 ตัว";
                        set.promoStart = java.time.LocalDate.of(2025, 11, 9);
                        set.promoEnd = java.time.LocalDate.of(2025, 11, 23);
                        store.sets.put(set.id, set);
                }

                // SET02 : เซ็ตช่อดอกไม้ประดิษฐ์โรแมนติก(โทนสีแดง)
                {
                        ProductSet set = new ProductSet("SET02", "เซ็ตช่อดอกไม้ประดิษฐ์โรแมนติก(โทนสีแดง)", 459.00);
                        set.addItem(new SetItem("ดอกทิวลิปประดิษฐ์", 12));
                        set.addItem(new SetItem("กระดาษเนื้อด้าน สีชมพูอ่อน", 1));
                        set.addItem(new SetItem("การ์ดอวยพร", 1));
                        store.sets.put(set.id, set);
                }

                // SET03 : เซ็ตแจกันดอกไม้สด
                {
                        ProductSet set = new ProductSet("SET03", "เซ็ตแจกันดอกไม้สด", 799.00);
                        set.addItem(new SetItem("ดอกไฮเดรนเยียสด", 8));
                        set.addItem(new SetItem("ดอกทิวลิปสด", 7));
                        set.addItem(new SetItem("ดอกเบบี้เบรธสด", 3));
                        set.addItem(new SetItem("กระดาษเนื้อด้าน ขาวครีม", 1));
                        store.sets.put(set.id, set);
                }

                // SET04 : เซ็ตแจกันสุดหรูหราหมาเห่า *** โปรโมชัน ***
                {
                        ProductSet set = new ProductSet("SET04", "เซ็ตแจกันสุดหรูหราหมาเห่า", 1099.00);
                        set.addItem(new SetItem("ดอกลิลลี่สด", 10));
                        set.addItem(new SetItem("ดอกไลเซนทัสสด", 6));
                        set.addItem(new SetItem("กระดาษโฮโลแกรม", 1));
                        set.addItem(new SetItem("ช็อกโกแลตแบบกล่อง", 1)); // ของแถม/ประกอบเซ็ต
                        set.promoDescription = "ซื้อครบ 1099 แถมช็อกโกแลตแบบกล่อง 1 กล่อง";
                        set.promoStart = java.time.LocalDate.of(2025, 11, 9);
                        set.promoEnd = java.time.LocalDate.of(2025, 12, 9);
                        store.sets.put(set.id, set);
                }

                // SET05 : เซ็ตของขวัญวาเลนไทน์
                {
                        ProductSet set = new ProductSet("SET05", "เซ็ตของขวัญวาเลนไทน์", 899.00);
                        set.addItem(new SetItem("ดอกกุหลาบสด", 12));
                        set.addItem(new SetItem("กระดาษเนื้อด้าน สีชมพูอ่อน", 1));
                        set.addItem(new SetItem("การ์ดอวยพร", 1));
                        store.sets.put(set.id, set);
                }

                // SET06 : เซ็ตเทศกาลปีใหม่
                {
                        ProductSet set = new ProductSet("SET06", "เซ็ตเทศกาลปีใหม่", 599.00);
                        set.addItem(new SetItem("ดอกเดซี่สด", 10));
                        set.addItem(new SetItem("กระดาษเนื้อด้าน ฟ้าอ่อน", 1));
                        set.addItem(new SetItem("การ์ดอวยพร", 1));
                        store.sets.put(set.id, set);
                }

                // SET01: 2025-11-09 – 2025-11-23
                {
                        ProductSet s = store.sets.get("SET01");
                        s.promoDescription = "ซื้อครบ 499 แถมตุ๊กตาหมีขนาดเล็ก 1 ตัว";
                        s.promoStart = java.time.LocalDate.of(2025, 11, 9);
                        s.promoEnd = java.time.LocalDate.of(2025, 11, 23);
                }
                // SET04: 2025-11-09 – 2025-12-09
                {
                        ProductSet s = store.sets.get("SET04");
                        s.promoDescription = "ซื้อครบ 1099 แถมช็อกโกแลตแบบกล่อง 1 กล่อง";
                        s.promoStart = java.time.LocalDate.of(2025, 11, 9);
                        s.promoEnd = java.time.LocalDate.of(2025, 12, 9);
                }

                // ===== คูปองตามตัวอย่าง =====
                // หมายเหตุ: ใช้ลายเซ็นที่มีอยู่จริงในโปรเจ็กต์คุณ เช่น
                // new Coupon(code, name, percent, minSpend, expiry) หรือถ้าเป็น AMOUNT
                // ให้เปลี่ยน type ตามคลาสคุณ
                store.upsertCoupon(new Coupon("seeu123", "คูปองส่วนลด 10% ขั้นต่ำ 499 บาท", 10, 499,
                                java.time.LocalDate.of(2026, 3, 9)));
                store.upsertCoupon(
                                new Coupon("lnwza007", "คูปองโปรโมชัน 5%", 5, 0, java.time.LocalDate.of(2025, 12, 9)));
                store.upsertCoupon(new Coupon("vip2000", "ส่วนลด 15% ขั้นต่ำ 2,000 บาท", 15, 2000,
                                java.time.LocalDate.of(2026, 1, 9)));

                // ถ้าคลาส Coupon รองรับ allowedSets:
                {
                        Coupon c = new Coupon("set99only", "ใช้ได้เฉพาะ SET99 เท่านั้น", 20, 0,
                                        java.time.LocalDate.of(2025, 12, 9));
                        try {
                                // สมมติว่ามีเมธอดให้กำหนด allowedSets เป็น List<String>
                                c.allowOnlySets(java.util.List.of("SET99"));
                        } catch (Throwable ignore) {
                                /* ถ้าคลาสยังไม่รองรับ ก็ข้ามได้ */ }
                        store.upsertCoupon(c);
                }

                // ---------- Coupons ----------
                // ใช้ signature ที่มีอยู่ในโปรเจกต์ของคุณตอนนี้ (จากภาพล่าสุด)
                store.upsertCoupon(new Coupon("lnwza007", "คูปองโปรโมชัน 5%", 5, 0, LocalDate.now().plusMonths(1)));
                store.upsertCoupon(new Coupon("vip2000", "ส่วนลด 15% ขั้นต่ำ 2,000", 15, 2000,
                                LocalDate.now().plusMonths(2)));
                store.upsertCoupon(new Coupon("seeu123", "คูปองส่วนลด 10% ขั้นต่ำ 499", 10, 499,
                                LocalDate.now().plusMonths(16)));
                // เซ็ตสำหรับเฉพาะเซ็ตถ้าคลาส Coupon ของคุณรองรับ allowedSets:
                // Coupon c = new Coupon("set99only","ใช้ได้เฉพาะ SET99 เท่านั้น",20,0,
                // LocalDate.now().plusMonths(1));
                // c.allowOnlySets(java.util.List.of("SET99"));
                // store.upsertCoupon(c);

        }
}
