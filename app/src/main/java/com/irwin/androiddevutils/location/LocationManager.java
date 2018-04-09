package com.irwin.androiddevutils.location;

import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Irwin on 2017/8/9.
 */

public class LocationManager extends LocationProvider {
    public static final int MODE_PARALLEL = 0;
    public static final int MODE_SWITCHING = 1;
    private static LocationManager INSTANCE;
    private static final String TAG = "LocationManager";
    private Location mLocation;
    private ArrayList<Object> mProviders = new ArrayList<>();
    private HashSet<ListenerRef> mListeners = new HashSet<>();
    private LocationWatcher mWatcher;
    private int mMode = MODE_PARALLEL;
    private int mCurIndex = 0;
    private ArrayList<LocationProvider> mApplicableProviders = new ArrayList<>();
    private int mErrorCount = 0;
    //Retry count while locating fail, valid only in <code>MODE_SWITCHING</code>
    private int mRetryCount = 3;
    //Tell if to switch provider while locating fail.
    private boolean mSwitching = true;

    public static LocationManager getInstance() {
        if (INSTANCE == null) {
            synchronized (LocationManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocationManager();
                }
            }
        }
        return INSTANCE;
    }

    private LocationManager() {
    }

    /**
     * Get last locating result.
     *
     * @return May be null.
     */
    public Location getLastLocation() {
        return mLocation;
    }


    /**
     * Set location provider.
     * @param providers
     * @return
     */
    public LocationManager setProvider(Class<? extends LocationProvider>... providers) {
        disposeProviders();
        if (providers.length > 0) {
            synchronized (mProviders) {
                for (Object item : providers) {
                    mProviders.add(item);
                }
            }
        }
        return this;
    }

    /**
     * Set locating mode, see {@link #MODE_PARALLEL}, {@link #MODE_SWITCHING}etc.
     * Note that the new mode will take effect after call {@link #startLocate()} or {@link #requestLocation()}.
     *
     * @param mode
     * @return
     */
    public LocationManager setMode(int mode) {
        if (mMode != mode) {
            mMode = mode;
        }
        return this;
    }

    /**
     * Set retry count while locating failed before switching provider.
     *
     * @param count
     * @return
     */
    public LocationManager setRetryCount(int count) {
        mRetryCount = count;
        return this;
    }

    /**
     * Unlike implementation in {@link LocationProvider},This method is used to give you an opportunity of handling first-hand
     * locating result, invalid  results included.
     *
     * @param watcher
     */
    public LocationManager setLocationWatcher(LocationWatcher watcher) {
        mWatcher = watcher;
        return this;
    }

    /**
     * Unsupported operation. use {@link #registerListener(LocationListener)} instead, you will get an <code>UnsupportedOperationException</code> otherwise.
     *
     * @param listener
     */
    @Override
    public LocationProvider setListener(LocationListener listener) {
        throw new UnsupportedOperationException("Use registerListener() instead.");
    }

    /**
     * Register locating listener.Note that we just hold weak reference to listeners, Callers should maintain listeners by themselves
     * or they may miss the callback.
     *
     * @param listener
     */
    public LocationManager registerListener(LocationListener listener) {
        if (listener != null) {
            synchronized (mListeners) {
                mListeners.add(new ListenerRef(listener));
            }
        }
        return this;
    }

    /**
     * Unregister location listener.
     * @param listener
     */
    public void unregisterListener(LocationListener listener) {
        synchronized (mListeners) {
            Iterator<ListenerRef> iterator = mListeners.iterator();
            LocationListener target;
            while (iterator.hasNext()) {
                target = iterator.next().get();
                if (target == null) {
                    iterator.remove();
                    continue;
                }
                if (target == listener) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    @Override
    protected void notifyLocationChanged(Location location) {
        synchronized (mListeners) {
            Iterator<ListenerRef> iterator = mListeners.iterator();
            LocationListener target;
            while (iterator.hasNext()) {
                target = iterator.next().get();
                if (target == null) {
                    iterator.remove();
                    continue;
                }
                target.onLocationChanged(location);
            }
        }
    }

    @Override
    public void startLocate() {
//        mErrorCount = 0;
//        mCurIndex = 0;
        mSwitching = true;
        requestLocation();
    }

    /**
     * Request location.
     */
    public void requestLocation() {
        List<LocationProvider> providers = getApplicableProvider();
        synchronized (providers) {
            if (providers.size() == 0) {
                Log.e(TAG, "Fail to start locating, please specify LocationProviders.");
                return;
            }
            Iterator<LocationProvider> iterator = providers.iterator();
            LocationProvider provider;
            LocOptions options = getOptions();
            while (iterator.hasNext()) {
                provider = iterator.next();
                provider.setListener(mMyListener);
                provider.setOptions(options);
                provider.startLocate();
            }
        }
    }

    protected List<LocationProvider> getApplicableProvider() {
        Map<Class, LocationProvider> map = new HashMap<>(1);
        LocationProvider provider;
        if (mMode == MODE_SWITCHING) {
            provider = getProviderInPriority();
            if (provider != null) {
                map.put(provider.getClass(), provider);
            }
        } else {
            synchronized (mProviders) {
                int size = mProviders.size();
                for (int i = 0; i < size; i++) {
                    try {
                        provider = checkInitProvider(i);
                        if (provider != null) {
                            map.put(provider.getClass(), provider);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //Merge providers.
        List<LocationProvider> providers = mApplicableProviders;
        synchronized (providers) {
            Iterator<LocationProvider> iterator = providers.iterator();
            while (iterator.hasNext()) {
                provider = iterator.next();
                //Not used any more. stop it.
                if (map.get(provider.getClass()) == null) {
                    provider.setListener(null).stopLocate();
                }
                iterator.remove();
            }
            providers.addAll(map.values());
        }
        return providers;
    }

    protected void stopLocate(List<LocationProvider> list) {
        Iterator<LocationProvider> iterator = list.iterator();
        while (iterator.hasNext()) {
            iterator.next().setListener(null).stopLocate();
        }
    }


    private LocationProvider getProviderInPriority() {
        synchronized (mProviders) {
            int size = mProviders.size();
            LocationProvider provider = null;
            if (size > 0) {
                int index = mCurIndex < size ? mCurIndex : 0;
                provider = checkInitProvider(index);
            }
            return provider;
        }
    }

    private LocationProvider checkInitProvider(int index) {
        synchronized (mProviders) {
            Object target = mProviders.get(index);
            LocationProvider provider = checkInitProvider(target);
            if (provider != null && provider != target) {
                //Cache provider instance.
                mProviders.set(index, provider);
            }
            return provider;
        }
    }

    private LocationProvider checkInitProvider(Object target) {
        if (LocationProvider.class.isInstance(target)) {
            return (LocationProvider) target;
        }
        Class<? extends LocationProvider> clazz = (Class<? extends LocationProvider>) target;
        try {
            LocationProvider ret = clazz.newInstance();
            ret.setContext(getContext());
            return ret;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void stopLocate() {
        synchronized (mProviders) {
            Iterator<Object> iterator = mProviders.iterator();
            Object item;
            while (iterator.hasNext()) {
                item = iterator.next();
                if (item instanceof LocationProvider) {
                    ((LocationProvider) item).setListener(null).stopLocate();
                }
            }
        }
    }

    private void disposeProviders() {
        synchronized (mProviders) {
            Iterator<Object> iterator = mProviders.iterator();
            Object item;
            while (iterator.hasNext()) {
                item = iterator.next();
                if (item instanceof LocationProvider) {
                    ((LocationProvider) item).setListener(null).release();
                }
                iterator.remove();
            }
        }
    }

    @Override
    public void release() {
        disposeProviders();
        synchronized (mListeners) {
            mListeners.clear();
        }
        super.release();
    }

    public void checkProviders(Location newLocation, Location current) {
        if (mSwitching && mMode == MODE_SWITCHING) {
            if (newLocation == null || !newLocation.isValid()) {
                mErrorCount++;
                Log.d(TAG, "Error count: " + mErrorCount);
                if (mErrorCount >= mRetryCount) {
                    //Switch provider and reset error count.
                    mErrorCount = 0;
                    mCurIndex++;
                    synchronized (mProviders) {
                        int size = mProviders.size();
                        if (mCurIndex >= size) {
                            Log.d(TAG, "Reset");
                            //Use first provider.
                            mCurIndex = 0;
                            //Stop switch after one loop finished.
                            mSwitching = false;
                        }
                    }
                }
                //Switch provider;
//                stopLocate();
                requestLocation();
            }
        }
    }

    public interface LocationWatcher {
        /**
         * Called back on new location get.
         *
         * @param newLocation New location.
         * @param current     Current location.
         * @param isBetter    true if new location is better than current location.
         */
        void onNewLocation(@Nullable Location newLocation, @Nullable Location current, boolean isBetter);
    }

    private LocationListener mMyListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            boolean isBetter = location != null && location.isValid() && Location.isBetterLocation(location, mLocation);
            checkProviders(location, mLocation);
            LocationWatcher watcher = mWatcher;
            if (watcher != null) {
                watcher.onNewLocation(location, mLocation, isBetter);
            }
            if (isBetter) {
                mLocation = location;
                notifyLocationChanged(location);
            }
        }
    };


    class ListenerRef extends WeakReference<LocationListener> {

        public ListenerRef(LocationListener r) {
            super(r);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LocationListener my = get();
            LocationListener his = ((ListenerRef) o).get();

            return my != null ? my.equals(his) : his == null;

        }

        @Override
        public int hashCode() {
            LocationListener target = get();
            return target != null ? target.hashCode() : 0;
        }
    }
}
