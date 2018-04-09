package com.irwin.androiddevutils.location;


import android.content.Context;

/**
 * Created by Irwin on 2017/8/8.
 */

public abstract class LocationProvider {
    protected LocationListener mListener;

    private LocOptions mOptions;

    protected Context mContext;

    public LocationProvider() {
    }

    public LocationProvider setContext(Context context) {
        mContext = context.getApplicationContext();
        return this;
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * Set listener to receive locating result.
     *
     * @param listener
     */
    public LocationProvider setListener(LocationListener listener) {
        mListener = listener;
        return this;
    }

    /**
     * Set locating options. The options will take effect while call {@link #startLocate()} next time.
     *
     * @param options
     */
    public void setOptions(LocOptions options) {
        mOptions = options;
    }

    public LocOptions getOptions() {
        return mOptions;
    }

    /**
     * Start locating.
     */
    public abstract void startLocate();

    /**
     * Stop locating.
     */
    public abstract void stopLocate();

    protected void notifyLocationChanged(Location location) {
        handleLocation(location);
        if (mListener != null) {
            mListener.onLocationChanged(location);
        }
    }

    protected void handleLocation(Location location) {
        location.setFrom(getClass().getSimpleName());
    }

    public void release() {
        stopLocate();
        mListener = null;
    }
}
