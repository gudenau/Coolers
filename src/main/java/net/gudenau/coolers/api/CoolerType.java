package net.gudenau.coolers.api;

/**
 * Created by gudenau on 1/5/2017.
 * <p>
 * coolers
 */
public enum CoolerType {
    NORMAL,
    TRANSPARENT;

    public static CoolerType getType(int type) {
        return values()[Math.max(0, Math.min(type, CoolerType.values().length - 1))];
    }
}
