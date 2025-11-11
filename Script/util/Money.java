package util;

public final class Money {
    private Money() {}

    public static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
