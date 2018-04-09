//package com.irwin.androiddevutils.location;
//
//
//import android.content.Context;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.baidu.location.BDLocation;
//import com.baidu.location.BDLocationListener;
//import com.baidu.location.LocationClient;
//import com.baidu.location.LocationClientOption;
//import com.baidu.mapapi.SDKInitializer;
//
//import java.text.SimpleDateFormat;
//
///**
// * Created by Irwin on 2017/8/8.
// */
//
//public class BDLocationProvider extends LocationProvider {
//    protected LocationClient mLocationClient;
//    private LocationClientOption mOptions;
//    public static final SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    public BDLocationProvider() {
//    }
//
//    public BDLocationProvider(Context context) {
//        setContext(context);
//    }
//
//    @Override
//    public void startLocate() {
//        prepare();
//        if (mLocationClient != null) {
//            if (!mLocationClient.isStarted()) {
//                mLocationClient.start();
//                return;
//            }
//            mLocationClient.requestLocation();
//        }
//    }
//
//    private void prepare() {
//        synchronized (this) {
//            if (mLocationClient == null) {
//                SDKInitializer.initialize(mContext);
//                mLocationClient = new LocationClient(mContext);
//                mLocationClient.registerLocationListener(mCallback);
//            }
//            if (mOptions == null) {
//                mOptions = new LocationClientOption();
//            }
//        }
//        LocOptions config = getOptions();
//        LocationClientOption option = mOptions;
//        boolean isFine = config == null || config.Mode == LocOptions.MODE_FINE;
//        option.setLocationMode(isFine ? LocationClientOption.LocationMode.Hight_Accuracy : LocationClientOption.LocationMode.Battery_Saving);
//        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//
//        option.setCoorType("bd09ll");
//        //可选，默认gcj02，设置返回的定位结果坐标系
//
//        int span = config == null ? 0 : (int) config.Interval;
//        option.setScanSpan(span < 0 ? 0 : span);
//        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//
//        option.setIsNeedAddress(true);
//        //可选，设置是否需要地址信息，默认不需要
//
//        option.setOpenGps(isFine);
//        //可选，默认false,设置是否使用gps
//
//
//        option.setIsNeedLocationDescribe(true);
//        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//
////        option.setIsNeedLocationPoiList(true);
//        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//
//        option.setIgnoreKillProcess(false);
//        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//
//        option.SetIgnoreCacheException(false);
//        //可选，默认false，设置是否收集CRASH信息，默认收集
//        option.setEnableSimulateGps(config == null ? false : config.MockEnable);
//        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
//        option.disableCache(config == null ? false : !config.UseCache);
//
//        mLocationClient.setLocOption(option);
//
//    }
//
//    @Override
//    public void stopLocate() {
//        if (mLocationClient != null) {
//            mLocationClient.stop();
//        }
//    }
//
//    @Override
//    public void release() {
//        stopLocate();
//        synchronized (this) {
//            if (mLocationClient != null) {
//                mLocationClient.unRegisterLocationListener(mCallback);
//                mLocationClient = null;
//            }
//        }
//        super.release();
//    }
//
//    private void showLocation(BDLocation location) {
//        //获取定位结果
//        StringBuffer sb = new StringBuffer(256);
//
//        sb.append("time : ");
//        sb.append(location.getTime());    //获取定位时间
//
//        sb.append("\nerror code : ");
//        sb.append(location.getLocType());    //获取类型类型
//
//        sb.append("\nlatitude : ");
//        sb.append(location.getLatitude());    //获取纬度信息
//
//        sb.append("\nlontitude : ");
//        sb.append(location.getLongitude());    //获取经度信息
//
//        sb.append("\nradius : ");
//        sb.append(location.getRadius());    //获取定位精准度
//
//        if (location.getLocType() == BDLocation.TypeGpsLocation) {
//
//            // GPS定位结果
//            sb.append("\nspeed : ");
//            sb.append(location.getSpeed());    // 单位：公里每小时
//
//            sb.append("\nsatellite : ");
//            sb.append(location.getSatelliteNumber());    //获取卫星数
//
//            sb.append("\nheight : ");
//            sb.append(location.getAltitude());    //获取海拔高度信息，单位米
//
//            sb.append("\ndirection : ");
//            sb.append(location.getDirection());    //获取方向信息，单位度
//
//            sb.append("\naddr : ");
//            sb.append(location.getAddrStr());    //获取地址信息
//
//            sb.append("\ndescribe : ");
//            sb.append("gps定位成功");
//
//        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
//
//            // 网络定位结果
//            sb.append("\naddr : ");
//            sb.append(location.getAddrStr());    //获取地址信息
//
//            sb.append("\noperationers : ");
//            sb.append(location.getOperators());    //获取运营商信息
//
//            sb.append("\ndescribe : ");
//            sb.append("网络定位成功");
//
//        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
//
//            // 离线定位结果
//            sb.append("\ndescribe : ");
//            sb.append("离线定位成功，离线定位结果也是有效的");
//
//        } else if (location.getLocType() == BDLocation.TypeServerError) {
//
//            sb.append("\ndescribe : ");
//            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
//
//        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
//
//            sb.append("\ndescribe : ");
//            sb.append("网络不同导致定位失败，请检查网络是否通畅");
//
//        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
//
//            sb.append("\ndescribe : ");
//            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
//
//        }
//
//        sb.append("\nlocationdescribe : ");
//        sb.append(location.getLocationDescribe());    //位置语义化信息
//        Log.i("BDLocationProvider", sb.toString());
//    }
//
//    /**
//     * 61 ： GPS定位结果，GPS定位成功。
//     * 62 ： 无法获取有效定位依据，定位失败，请检查运营商网络或者WiFi网络是否正常开启，尝试重新请求定位。
//     * 63 ： 网络异常，没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位。
//     * 65 ： 定位缓存的结果。
//     * 66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果。
//     * 67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果。
//     * 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果。
//     * 161： 网络定位结果，网络定位成功。
//     * 162： 请求串密文解析失败，一般是由于客户端SO文件加载失败造成，请严格参照开发指南或demo开发，放入对应SO文件。
//     * 167： 服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位。
//     * 502： AK参数错误，请按照说明文档重新申请AK。
//     * 505：AK不存在或者非法，请按照说明文档重新申请AK。
//     * 601： AK服务被开发者自己禁用，请按照说明文档重新申请AK。
//     * 602： key mcode不匹配，您的AK配置过程中安全码设置有问题，请确保：SHA1正确，“;”分号是英文状态；且包名是您当前运行应用的包名，请按照说明文档重新申请AK。
//     * 501～700：AK验证失败，请按照说明文档重新申请AK。
//     * 如果不能定位，请记住这个返回值，并到百度LBS开放平台论坛Andriod定位SDK版块中进行交流，网址：http://bbs.lbsyun.baidu.com/forum.php?mod=forumdisplay&fid=10 。若返回值是162~167，请将错误码、IMEI和定位时间反馈至邮箱loc-bugs@baidu.com，以便我们跟进追查问题。
//     *
//     * @param location
//     * @return
//     */
//    public static Location valueOf(BDLocation location) {
//        Location ret = new Location();
//
//        if (location != null) {
//            String provider = null;
//            String description = null;
//            switch (location.getLocType()) {
//                case BDLocation.TypeGpsLocation:
//                    provider = Location.GPS_PROVIDER;
//                    description = location.getAddrStr();
//                    break;
//                case BDLocation.TypeNetWorkLocation:
//                    provider = Location.NETWORK_PROVIDER;
//                    description = location.getAddrStr();
//                    break;
//                case BDLocation.TypeOffLineLocation:
//                    provider = Location.NETWORK_PROVIDER;
//                    description = "Offline location: " + location.getAddrStr();
//                    ret.setOffline(true);
//                    break;
//                case BDLocation.TypeCacheLocation:
//                    provider = Location.NETWORK_PROVIDER;
//                    description = location.getAddrStr();
//                    ret.setCache(true);
//                    break;
//                case BDLocation.TypeOffLineLocationNetworkFail:
//                case BDLocation.TypeOffLineLocationFail:
//                    description = "离线定位失败";
//                    break;
//                case BDLocation.TypeServerError:
//                    description = "定位失败：服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因";
//                    break;
//                case BDLocation.TypeNetWorkException:
//                    description = "定位失败：网络不同导致定位失败，请检查网络是否通畅";
//                    break;
//                case BDLocation.TypeCriteriaException:
//                    description = "定位失败：无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机";
//                    break;
//                default:
//                    description = "定位失败：错误码: " + location.getLocType();
//                    break;
//
//            }
//            ret.setCode(TextUtils.isEmpty(provider) ? Location.CODE_ERROR : Location.CODE_NORMAL);
//            ret.setProvider(provider);
//            ret.setDescription(description);
//            long time;
//            try {
//                time = DateFormat.parse(location.getTime()).getTime();
//            } catch (Exception e) {
//                e.printStackTrace();
//                time = System.currentTimeMillis();
//            }
//            ret.setTime(time);
//            if (provider != null) {
//                ret.setLongitude(location.getLongitude());
//                ret.setLatitude(location.getLatitude());
//                if (location.hasAltitude()) {
//                    ret.setHasAltitude(true);
//                    ret.setAltitude(location.getAltitude());
//                }
//                ret.setCountry(location.getCountry());
//                ret.setProvince(location.getProvince());
//                ret.setCity(location.getCity());
//                ret.setDistrict(location.getDistrict());
//                ret.setStreet(location.getStreet());
//                ret.setStreetNumber(location.getStreetNumber());
//                ret.setAddress(location.getAddrStr());
//                if (location.hasRadius()) {
//                    ret.setHasAccuracy(true);
//                    ret.setAccuracy(location.getRadius());
//                }
//                if (location.hasSpeed()) {
//                    ret.setHasSpeed(true);
//                    ret.setSpeed(location.getSpeed());
//                }
//            }
//        } else {
//            ret.setCode(Location.CODE_ERROR);
//            ret.setTime(System.currentTimeMillis());
//            ret.setDescription("定位失败：Null location source");
//        }
//        return ret;
//    }
//
//
//    private BDLocationListener mCallback = new BDLocationListener() {
//        @Override
//        public void onReceiveLocation(BDLocation bdLocation) {
//            Location location = valueOf(bdLocation);
//            notifyLocationChanged(location);
//        }
//
//        @Override
//        public void onConnectHotSpotMessage(String s, int i) {
//
//        }
//    };
//}
