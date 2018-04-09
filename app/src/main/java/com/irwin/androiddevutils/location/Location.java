package com.irwin.androiddevutils.location;

import android.location.LocationManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.irwin.androiddevutils.utils.DataUtils;


/**
 * Created by Irwin on 2017/8/9.
 */

public class Location implements Parcelable {

    /**
     * Tell if offline result.
     */
    private static final int FLAG_OFFLINE = 1;

    /**
     * Tell if cache result.
     */
    private static final int FLAG_CACHE = 1 << 1;

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    public static final String GPS_PROVIDER = LocationManager.GPS_PROVIDER;

    public static final String NETWORK_PROVIDER = LocationManager.NETWORK_PROVIDER;

    public static final int CODE_NORMAL = 0;

    public static final int CODE_ERROR = -1;

    private double mLongitude = 0.0F;

    private double mLatitude = 0.0F;

    private double mAltitude = 0.0F;

    private boolean mHasAltitude = false;

    private String mProvince;

    private String mCountry;

    private String mCity;

    private String mDistrict;

    private String mStreet;

    private String mStreetNumber;

    private String mAddress;

    private String mProvider;

    private long mTime;

    private boolean mHasSpeed = false;
    private float mSpeed = 0.0f;

    private boolean mHasBearing = false;
    private float mBearing = 0.0f;

    private boolean mHasAccuracy = false;
    private float mAccuracy = 0.0f;

    private int mCode;

    private String mDescription;

    private String mFrom;

    private int mFlag = 0;

    /**
     * Determines whether one Location reading is better than the current
     * Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new
     *                            one
     */
    protected static boolean isBetterLocation(Location location,
                                              Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }
        if (location == null || !location.isValid()) {
            return false;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use
        // the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be
            // worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
                .getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and
        // accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate
                && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        this.mCity = city;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        this.mCountry = country;
    }

    public String getProvince() {
        return mProvince;
    }

    public void setProvince(String province) {
        this.mProvince = province;
    }

    public String getDistrict() {
        return mDistrict;
    }

    public void setDistrict(String district) {
        this.mDistrict = district;
    }

    public String getStreet() {
        return mStreet;
    }

    public void setStreet(String street) {
        this.mStreet = street;
    }

    public String getStreetNumber() {
        return mStreetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.mStreetNumber = streetNumber;
    }

    public String getProvider() {
        return mProvider;
    }

    public void setProvider(String provider) {
        this.mProvider = provider;
    }


    /**
     * Indicate whether has altitude.
     *
     * @return
     */
    public boolean hasAltitude() {
        return mHasAltitude;
    }


    public void setHasAltitude(boolean hasAltitude) {
        this.mHasAltitude = hasAltitude;
    }

    public void setAltitude(double altitude) {
        this.mAltitude = altitude;
    }

    /**
     * Valid only if {@link #hasAltitude()} returns true.
     *
     * @return
     */
    public double getAltitude() {
        return mAltitude;
    }

    public boolean hasSpeed() {
        return mHasSpeed;
    }

    public void setHasSpeed(boolean hasSpeed) {
        this.mHasSpeed = hasSpeed;
    }

    /**
     * Valid only if {@link #hasSpeed()} returns true.
     *
     * @return
     */
    public float getSpeed() {
        return mSpeed;
    }

    public void setSpeed(float speed) {
        this.mSpeed = speed;
    }

    /**
     * @return True if this location has a bearing.
     */
    public boolean hasBearing() {
        return mHasBearing;
    }


    public void setHasBearing(boolean hasBearing) {
        this.mHasBearing = hasBearing;
    }

    /**
     * <p>Bearing is the horizontal direction of travel of this device,
     * and is not related to the device orientation. It is guaranteed to
     * be in the range (0.0, 360.0] if the device has a bearing.
     * <p>
     * <p>If this location does not have a bearing then 0.0 is returned.
     * <p>Valid only if {@link #hasBearing()} returns true.
     *
     * @return
     */
    public float getBearing() {
        return mBearing;
    }

    public void setBearing(float bearing) {
        this.mBearing = bearing;
    }

    public boolean hasAccuracy() {
        return mHasAccuracy;
    }

    public void setHasAccuracy(boolean hasAccuracy) {
        this.mHasAccuracy = hasAccuracy;
    }

    /**
     * Valid only if {@link #hasAccuracy()} returns true.
     *
     * @return
     */
    public float getAccuracy() {
        return mAccuracy;
    }

    public void setAccuracy(float accuracy) {
        this.mAccuracy = accuracy;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        this.mTime = time;
    }


    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        this.mCode = code;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getFrom() {
        return mFrom;
    }

    public void setFrom(String from) {
        mFrom = from;
    }

    public boolean isValid() {
        return mCode == CODE_NORMAL && (!TextUtils.isEmpty(mProvider));
    }

    public int getFlag() {
        return mFlag;
    }

    public void setFlag(int flag) {
        this.mFlag = flag;
    }

    public boolean isOffline() {
        return DataUtils.hasFlag(mFlag, FLAG_OFFLINE);
    }

    public Location setOffline(boolean value) {
        mFlag = DataUtils.enableFlag(mFlag, FLAG_OFFLINE, value);
        return this;
    }

    public boolean isCache() {
        return DataUtils.hasFlag(mFlag, FLAG_CACHE);
    }

    public Location setCache(boolean value) {
        mFlag = DataUtils.enableFlag(mFlag, FLAG_CACHE, value);
        return this;
    }


    @Override
    public String toString() {
        return "Location{" +
                "mLongitude=" + mLongitude +
                ", mLatitude=" + mLatitude +
                ", mAltitude=" + mAltitude +
                ", mHasAltitude=" + mHasAltitude +
                ", mProvince='" + mProvince + '\'' +
                ", mCountry='" + mCountry + '\'' +
                ", mCity='" + mCity + '\'' +
                ", mDistrict='" + mDistrict + '\'' +
                ", mStreet='" + mStreet + '\'' +
                ", mStreetNumber='" + mStreetNumber + '\'' +
                ", mAddress='" + mAddress + '\'' +
                ", mProvider='" + mProvider + '\'' +
                ", mTime=" + mTime +
                ", mHasSpeed=" + mHasSpeed +
                ", mSpeed=" + mSpeed +
                ", mHasBearing=" + mHasBearing +
                ", mBearing=" + mBearing +
                ", mHasAccuracy=" + mHasAccuracy +
                ", mAccuracy=" + mAccuracy +
                ", mCode=" + mCode +
                ", mDescription='" + mDescription + '\'' +
                ", mFrom='" + mFrom + '\'' +
                '}';
    }

    public Location() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.mLongitude);
        dest.writeDouble(this.mLatitude);
        dest.writeDouble(this.mAltitude);
        dest.writeByte(this.mHasAltitude ? (byte) 1 : (byte) 0);
        dest.writeString(this.mProvince);
        dest.writeString(this.mCountry);
        dest.writeString(this.mCity);
        dest.writeString(this.mDistrict);
        dest.writeString(this.mStreet);
        dest.writeString(this.mStreetNumber);
        dest.writeString(this.mAddress);
        dest.writeString(this.mProvider);
        dest.writeLong(this.mTime);
        dest.writeByte(this.mHasSpeed ? (byte) 1 : (byte) 0);
        dest.writeFloat(this.mSpeed);
        dest.writeByte(this.mHasBearing ? (byte) 1 : (byte) 0);
        dest.writeFloat(this.mBearing);
        dest.writeByte(this.mHasAccuracy ? (byte) 1 : (byte) 0);
        dest.writeFloat(this.mAccuracy);
        dest.writeInt(this.mCode);
        dest.writeString(this.mDescription);
        dest.writeString(this.mFrom);
        dest.writeInt(this.mFlag);
    }

    protected Location(Parcel in) {
        this.mLongitude = in.readDouble();
        this.mLatitude = in.readDouble();
        this.mAltitude = in.readDouble();
        this.mHasAltitude = in.readByte() != 0;
        this.mProvince = in.readString();
        this.mCountry = in.readString();
        this.mCity = in.readString();
        this.mDistrict = in.readString();
        this.mStreet = in.readString();
        this.mStreetNumber = in.readString();
        this.mAddress = in.readString();
        this.mProvider = in.readString();
        this.mTime = in.readLong();
        this.mHasSpeed = in.readByte() != 0;
        this.mSpeed = in.readFloat();
        this.mHasBearing = in.readByte() != 0;
        this.mBearing = in.readFloat();
        this.mHasAccuracy = in.readByte() != 0;
        this.mAccuracy = in.readFloat();
        this.mCode = in.readInt();
        this.mDescription = in.readString();
        this.mFrom = in.readString();
        this.mFlag = in.readInt();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}
