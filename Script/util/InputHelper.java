package util;

import java.util.Scanner;
import java.time.format.DateTimeFormatter;

public final class InputHelper {
    private static final Scanner SC = new Scanner(System.in);
    public static String ask(String prompt) {
        System.out.print(prompt);
        return SC.nextLine();
    }
    public static int askInt(String prompt) {
        while (true) {
            try {
                String s = ask(prompt);
                return Integer.parseInt(s.trim());
            } catch (Exception e) {
                System.out.println("กรุณาใส่ตัวเลขที่ถูกต้อง");
            }
        }
    }
    public static double askDouble(String prompt) {
        while (true) {
            try {
                String s = ask(prompt);
                return Double.parseDouble(s.trim());
            } catch (Exception e) {
                System.out.println("กรุณาใส่จำนวนที่ถูกต้อง");
            }
        }
    }
}
