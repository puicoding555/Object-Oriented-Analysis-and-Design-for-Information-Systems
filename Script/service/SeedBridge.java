package service;

public final class SeedBridge {
    private SeedBridge() {}

    // เรียก seed หลัก โดยรับ Store ปัจจุบันเข้ามา
    public static void apply(service.Store store) {
        Seed.apply(store);
    }
}
