package com.irwin.androiddevutils.location;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Irwin on 2017/8/8.
 */

public class LocOptions implements Parcelable {

    /**
     * Locating mode for high accuracy result. such as from GPS.
     */
    public static final int MODE_FINE = 0;

    /**
     * Locating mode for low accuracy result. such as from WIFI, Cell etc.
     */
    public static final int MODE_COARSE = 0;

    /**
     * Locating mode.
     */
    public int Mode = MODE_FINE;

    /**
     * Continuing-Locating interval. O means locating once.
     */
    public long Interval = 0L;

    /**
     * If enable location mock.
     */
    public boolean MockEnable = false;

    /**
     * If use cache result.
     */
    public boolean UseCache = true;


    public LocOptions() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.Mode);
        dest.writeLong(this.Interval);
        dest.writeByte(this.MockEnable ? (byte) 1 : (byte) 0);
        dest.writeByte(this.UseCache ? (byte) 1 : (byte) 0);
    }

    protected LocOptions(Parcel in) {
        this.Mode = in.readInt();
        this.Interval = in.readLong();
        this.MockEnable = in.readByte() != 0;
        this.UseCache = in.readByte() != 0;
    }

    public static final Creator<LocOptions> CREATOR = new Creator<LocOptions>() {
        @Override
        public LocOptions createFromParcel(Parcel source) {
            return new LocOptions(source);
        }

        @Override
        public LocOptions[] newArray(int size) {
            return new LocOptions[size];
        }
    };
}
