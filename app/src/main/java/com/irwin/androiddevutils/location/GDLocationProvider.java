//package com.irwin.androiddevutils.location;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.amap.api.location.AMapLocation;
//import com.amap.api.location.AMapLocationClient;
//import com.amap.api.location.AMapLocationClientOption;
//import com.amap.api.location.AMapLocationListener;
//import com.amap.api.location.CoordinateConverter;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import cn.rrkd.common.location.LocOptions;
//import cn.rrkd.common.location.Location;
//import cn.rrkd.common.location.LocationProvider;
//import cn.rrkd.common.modules.logger.Logger;
//import cn.rrkd.courier.RrkdConfig;
//import cn.rrkd.courier.session.RrkdLocationManager;
//
///**
// * Created by Irwin on 2017/9/1.
// */
//
//public class GDLocationProvider extends LocationProvider {
//    public static final double X_PI = 3.14159265358979324 * 3000.0 / 180.0;
//    private AMapLocationClient mLocationClient;
//    private AMapLocationClientOption mOptions;
//
//    /**
//     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法 将 GCJ-02 坐标转换成 BD-09 坐标
//     *
//     * @param gg_lat
//     * @param gg_lon
//     * @return An double array which [0] is latitude and [1] is longitude
//     */
//    public static double[] GCJ2BD09(double gg_lat, double gg_lon) {
//        double bd_lat, bd_lon;
//        double x = gg_lon, y = gg_lat;
//        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * X_PI);
//        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * X_PI);
//        bd_lon = z * Math.cos(theta) + 0.0065;
//        bd_lat = z * Math.sin(theta) + 0.006;
//        return new double[]{bd_lat, bd_lon};
//    }
//
//
//    @Override
//    public void startLocate() {
//        prepare();
//        if (mLocationClient != null) {
//            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//            // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
//            // 在定位结束后，在合适的生命周期调用onDestroy()方法
//            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//            //启动定位
//            mLocationClient.startLocation();
//        }
//    }
//
//    private void prepare() {
//        synchronized (this) {
//            if (mLocationClient == null) {
//                mLocationClient = new AMapLocationClient(mContext);
//                mLocationClient.setLocationListener(mInnerListener);
//            }
//            if (mOptions == null) {
//                mOptions = new AMapLocationClientOption();
//            }
//        }
//        LocOptions source = getOptions();
//        AMapLocationClientOption options = mOptions;
//        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
//        options.setLocationMode(source == null || source.Mode == LocOptions.MODE_FINE ?
//                AMapLocationClientOption.AMapLocationMode.Hight_Accuracy : AMapLocationClientOption.AMapLocationMode.Battery_Saving);
//        if (source == null || source.Interval <= 0) {
//            options.setOnceLocation(true);
//        } else {
//            //设置定位间隔,单位毫秒,默认为2000ms
//            options.setInterval(source.Interval);
//        }
//        options.setMockEnable(source == null ? false : source.MockEnable);
//        options.setLocationCacheEnable(source == null ? true : source.UseCache);
//        options.setNeedAddress(true);
//        //设置定位参数
//        mLocationClient.setLocationOption(options);
//
//    }
//
//    @Override
//    public void stopLocate() {
//        if (mLocationClient != null) {
//            mLocationClient.stopLocation();
//        }
//    }
//
//    @Override
//    public void release() {
//        stopLocate();
//        synchronized (this) {
//            if (mLocationClient != null) {
//                mLocationClient.unRegisterLocationListener(mInnerListener);
//                mLocationClient.onDestroy();
//                mLocationClient = null;
//            }
//        }
//        super.release();
//    }
//
//    protected Location valueOf(AMapLocation location) {
//        Location ret = new Location();
//        String description;
//        String provider = null;
//        if (location != null) {
//            if (location.getErrorCode() == 0) {
//                switch (location.getLocationType()) {
//                    case AMapLocation.LOCATION_TYPE_GPS:
//                        provider = Location.GPS_PROVIDER;
//                        break;
//                    case 0:
//                        break;
//                    case AMapLocation.LOCATION_TYPE_LAST_LOCATION_CACHE:
//                        provider = Location.NETWORK_PROVIDER;
//                        ret.setCache(true);
//                        break;
//                    case AMapLocation.LOCATION_TYPE_OFFLINE:
//                        provider = Location.NETWORK_PROVIDER;
//                        ret.setOffline(true);
//                        break;
//                    default:
//                        provider = Location.NETWORK_PROVIDER;
//                        break;
//                }
//                //定位成功回调信息，设置相关消息
//                double coord[];
//                try {
//                    coord = GCJ2BD09(location.getLatitude(), location.getLongitude());
//                } catch (Exception e) {
//                    coord = new double[]{location.getLatitude(), location.getLongitude()};
//                    e.printStackTrace();
//                }
//                ret.setLatitude(coord[0]);
//                ret.setLongitude(coord[1]);
//                ret.setCountry(location.getCountry());
//                ret.setProvince(location.getProvince());
//                ret.setCity(location.getCity());
//                ret.setDistrict(location.getDistrict());
//                ret.setAddress(location.getAddress());
//                ret.setStreet(location.getStreet());
//                ret.setStreetNumber(location.getStreetNum());
//                ret.setTime(location.getTime());
//                if (location.hasSpeed()) {
//                    ret.setHasSpeed(true);
//                    ret.setSpeed(location.getSpeed());
//                }
//                if (location.hasAltitude()) {
//                    ret.setHasAltitude(true);
//                    ret.setAltitude(location.getAltitude());
//                }
//                if (location.hasAccuracy()) {
//                    ret.setHasAccuracy(true);
//                    ret.setAccuracy(location.getAccuracy());
//                }
//                if (location.hasBearing()) {
//                    ret.setHasBearing(true);
//                    ret.setBearing(location.getBearing());
//                }
//                description = location.getAddress();
//            } else {
//                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
//                description = "定位失败: ErrCode:"
//                        + location.getErrorCode() + ", errInfo:"
//                        + location.getErrorInfo();
//            }
//        } else {
//            description = "定位失败:Null location source";
//        }
//        ret.setProvider(provider);
//        if (provider == null) {
//            ret.setCode(Location.CODE_ERROR);
//            ret.setTime(System.currentTimeMillis());
//        }
//        ret.setDescription(description);
//        return ret;
//    }
//
//
//    private AMapLocationListener mInnerListener = new AMapLocationListener() {
//        @Override
//        public void onLocationChanged(AMapLocation amapLocation) {
//            Location location = valueOf(amapLocation);
//            notifyLocationChanged(location);
//        }
//    };
//}
//
