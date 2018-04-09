package com.irwin.androiddevutils.location.geocoding;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Irwin on 2017/8/9.
 */

public class Poi implements Parcelable {

    public static final int TYPE_POINT = 0;
    public static final int TYPE_BUS_STATION = 1;
    public static final int TYPE_BUS_LINE = 2;
    public static final int TYPE_SUBWAY_STATION = 3;
    public static final int TYPE_SUBWAY_LINE = 4;

    public String name;
    public String address;
    public String city;
    public int type;
    public double longitude;
    public double latitude;

    @Override
    public String toString() {
        return "Poi{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", type=" + type +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeString(this.city);
        dest.writeInt(this.type);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
    }

    public Poi() {
    }

    protected Poi(Parcel in) {
        this.name = in.readString();
        this.address = in.readString();
        this.city = in.readString();
        this.type = in.readInt();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
    }

    public static final Creator<Poi> CREATOR = new Creator<Poi>() {
        @Override
        public Poi createFromParcel(Parcel source) {
            return new Poi(source);
        }

        @Override
        public Poi[] newArray(int size) {
            return new Poi[size];
        }
    };
}
