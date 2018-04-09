package com.irwin.androiddevutils.location.geocoding;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Irwin on 2017/8/9.
 */

public class GeoCodeResult implements Parcelable {
    public double Longitude;
    public double Latitude;
    public String Province;
    public String Country;
    public String City;
    public String District;
    public String Street;
    public String StreetNumber;
    public String Address;
    public List<Poi> PoiList;

    public GeoCodeResult(double longitude, double latitude) {
        this.Longitude = longitude;
        this.Latitude = latitude;
    }

    public GeoCodeResult(double longitude, double latitude, List<Poi> poiList) {
        Longitude = longitude;
        Latitude = latitude;
        PoiList = poiList;
    }

    @Override
    public String toString() {
        return "GeoCodeResult{" +
                "Longitude=" + Longitude +
                ", Latitude=" + Latitude +
                ", Province='" + Province + '\'' +
                ", Country='" + Country + '\'' +
                ", City='" + City + '\'' +
                ", District='" + District + '\'' +
                ", Street='" + Street + '\'' +
                ", StreetNumber='" + StreetNumber + '\'' +
                ", Address='" + Address + '\'' +
                ", PoiList=" + PoiList +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.Longitude);
        dest.writeDouble(this.Latitude);
        dest.writeString(this.Province);
        dest.writeString(this.Country);
        dest.writeString(this.City);
        dest.writeString(this.District);
        dest.writeString(this.Street);
        dest.writeString(this.StreetNumber);
        dest.writeString(this.Address);
        dest.writeTypedList(this.PoiList);
    }

    protected GeoCodeResult(Parcel in) {
        this.Longitude = in.readDouble();
        this.Latitude = in.readDouble();
        this.Province = in.readString();
        this.Country = in.readString();
        this.City = in.readString();
        this.District = in.readString();
        this.Street = in.readString();
        this.StreetNumber = in.readString();
        this.Address = in.readString();
        this.PoiList = in.createTypedArrayList(Poi.CREATOR);
    }

    public static final Creator<GeoCodeResult> CREATOR = new Creator<GeoCodeResult>() {
        @Override
        public GeoCodeResult createFromParcel(Parcel source) {
            return new GeoCodeResult(source);
        }

        @Override
        public GeoCodeResult[] newArray(int size) {
            return new GeoCodeResult[size];
        }
    };
}
