import java.util.Scanner;

import flow.MemberFlow;
import flow.ManagerFlow;
import model.Manager;
import model.Member;
import service.Store;
import service.SeedBridge;   // ★ เพิ่มบรรทัดนี้

/**
 * App: เมนูหลักหน้าแรกของระบบ (Text Mode)
 */
public class App {

    private final Store store = Store.get();
    private final Scanner sc = new Scanner(System.in);

    /** ถูกเรียกจาก Main.java -> new App().run(); */
    public void run() {
        // ★ Seed ข้อมูลจำลอง (สมาชิก/ผู้จัดการ/สินค้า/เซ็ต/คูปอง) ให้พร้อมใช้งาน
        SeedBridge.apply(store);

        while (true) {
            printWelcome();
            System.out.println("[1]  เข้าสู่ระบบ (สมาชิก)");
            System.out.println("[2]  เข้าสู่ระบบ (ผู้จัดการ)");
            System.out.println("[0]  ออกจากโปรแกรม");
            int sel = askIntLine("กรุณาเลือกเมนูที่ต้องการ : ");

            switch (sel) {
                case 1 -> {
                    Member m = loginMember();
                    if (m != null) {
                        // ถ้า MemberFlow มี constructor 3 ตัวแปร ให้ใช้บรรทัดนี้
                        new MemberFlow(sc, store, m).menu();
                        // ถ้าโปรเจ็กต์คุณมีแค่ 2 ตัวแปร ให้ใช้:
                        // new MemberFlow(sc, store).menu();
                    }
                }
                case 2 -> {
                    Manager mg = loginManager();
                    if (mg != null) {
                        // เช่นเดียวกัน ถ้ามี 3 ตัวแปร:
                        new ManagerFlow(sc, store, mg).menu();
                        // ถ้ามีแค่ 2 ตัวแปร:
                        // new ManagerFlow(sc, store).menu();
                    }
                }
                case 0 -> {
                    System.out.println("ลาก่อนครับ");
                    return;
                }
                default -> System.out.println("ตัวเลือกไม่ถูกต้อง");
            }
        }
    }

    // ================= Login =================
    private Member loginMember() {
        System.out.println("===== สมาชิกเข้าสู่ระบบ =====");
        System.out.print("Username: ");
        String u = sc.nextLine().trim();
        System.out.print("Password: ");
        String p = sc.nextLine().trim();

        Member m = store.authMember(u, p);
        if (m == null) {
            System.out.println("ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง");
            return null;
        }
        System.out.println("เข้าสู่ระบบสำเร็จ (สมาชิก)");
        return m;
    }

    private Manager loginManager() {
        System.out.println("===== ผู้จัดการเข้าสู่ระบบ =====");
        System.out.print("Username: ");
        String u = sc.nextLine().trim();
        System.out.print("Password: ");
        String p = sc.nextLine().trim();

        Manager mg = store.authManager(u, p);
        if (mg == null) {
            System.out.println("ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง");
            return null;
        }
        System.out.println("เข้าสู่ระบบสำเร็จ (ผู้จัดการ)");
        return mg;
    }

    // ================= Helpers =================
    private int askIntLine(String prompt) {
        System.out.print(prompt);
        String s = sc.nextLine().trim();
        try {
            return Integer.parseInt(s);
        } catch (Exception ignored) {
            return -1;
        }
    }

    private void printWelcome() {
        System.out.println("==============================================");
        System.out.println("===   ระบบร้านดอกไม้ GM (Text Mode)       ===");
        System.out.println("==============================================");
    }
}
