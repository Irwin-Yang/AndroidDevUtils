package com.irwin.androiddevutils.location.geocoding;


import com.irwin.androiddevutils.location.ICallback;

/**
 * Created by Irwin on 2017/8/9.
 */

public abstract class GeoCoder {

    private ICallback<GeoCodeResult> mCallback;

    public static GeoCoder create() {
        return /*new BDGeoCoder()*/null;
    }

    /**
     * Set callback for decode result.
     *
     * @param cb
     * @return
     */
    public GeoCoder setCallback(ICallback<GeoCodeResult> cb) {
        mCallback = cb;
        return this;
    }

    /**
     * get poi list by location.
     *
     * @param longitude
     * @param latitude
     */
    public abstract void getPoi(double longitude, double latitude);

    /**
     * Decode location by address.
     *
     * @param address
     */
    public abstract void decode(String address);

    /**
     * Decode location by city.
     *
     * @param cityName
     */
    public abstract void decodeByCity(String cityName);

    public void release() {
        mCallback = null;
    }

    protected void notifyDecodeSuccess(GeoCodeResult result) {
        ICallback<GeoCodeResult> cb = mCallback;
        if (cb != null) {
            cb.onSuccess(result);
        }
    }

    protected void notifyDecodeFail(Throwable info) {
        ICallback<GeoCodeResult> cb = mCallback;
        if (cb != null) {
            cb.onFail(info);
        }
    }
}
