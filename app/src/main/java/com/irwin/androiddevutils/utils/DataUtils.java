package com.irwin.androiddevutils.utils;

/**
 * Created by Irwin on 2018/2/22.
 */

public class DataUtils {

    private DataUtils() {
    }

    /**
     * Set a flag
     * @param base
     * @param flag
     * @return
     */
    public static int setFlag(int base, int flag) {
        return base | flag;
    }

    /**
     * Remove a flag.
     * @param base
     * @param flag
     * @return
     */
    public static int removeFlag(int base, int flag) {
        return base & ~flag;
    }

    /**
     * Remove or set a flag.
     * @param base
     * @param flag
     * @param enable
     * @return
     */
    public static int enableFlag(int base, int flag, boolean enable) {
        return enable ? base | flag : base & ~flag;
    }

    /**
     * Tell if has flag.
     * @param base
     * @param flag
     * @return
     */
    public static boolean hasFlag(int base, int flag) {
        return (base && flag) == flag;
    }
}
