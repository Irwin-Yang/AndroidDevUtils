//package com.irwin.androiddevutils.location.geocoding;
//
//import com.baidu.mapapi.model.LatLng;
//import com.baidu.mapapi.search.core.PoiInfo;
//import com.baidu.mapapi.search.core.SearchResult;
//import com.baidu.mapapi.search.geocode.GeoCodeOption;
//import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
//import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
//import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.List;
//
//
///**
// * Created by Irwin on 2017/3/16.
// */
//
//public class BDGeoCoder extends GeoCoder {
//
//    private com.baidu.mapapi.search.geocode.GeoCoder mGeoCoder;
//
//    private double mLongitude;
//    private double mLatitude;
//
//    BDGeoCoder() {
//        mGeoCoder = com.baidu.mapapi.search.geocode.GeoCoder.newInstance();
//    }
//
//    @Override
//    public void getPoi(double longitude, double latitude) {
//        mLongitude = longitude;
//        mLatitude = latitude;
//        LatLng latLng = new LatLng(latitude, longitude);
//        mGeoCoder.setOnGetGeoCodeResultListener(new Listener(longitude, latitude));
//        mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
//    }
//
//    @Override
//    public void decode(String address) {
//        mGeoCoder.setOnGetGeoCodeResultListener(new Listener());
//        mGeoCoder.geocode(new GeoCodeOption().address(address));
//    }
//
//    @Override
//    public void decodeByCity(String cityName) {
//        mGeoCoder.setOnGetGeoCodeResultListener(new Listener());
//        mGeoCoder.geocode(new GeoCodeOption().city(cityName).address(cityName));
//    }
//
//    @Override
//    public void release() {
//        if (mGeoCoder != null) {
//            mGeoCoder.destroy();
//        }
//        super.release();
//    }
//
//    private List<Poi> convertPois(Collection<PoiInfo> collection) {
//        if (collection != null && collection.size() > 0) {
//            Iterator<PoiInfo> iterator = collection.iterator();
//            ArrayList<Poi> list = new ArrayList<>(collection.size());
//            while (iterator.hasNext()) {
//                list.add(convertPoi(iterator.next()));
//            }
//            return list;
//        }
//        return Collections.EMPTY_LIST;
//    }
//
//    private Poi convertPoi(PoiInfo poi) {
//        Poi ret = new Poi();
//        ret.name = poi.name;
//        ret.address = poi.address;
//        ret.city = poi.city;
//        if (poi.location != null) {
//            ret.latitude = poi.location.latitude;
//            ret.longitude = poi.location.longitude;
//        }
//        switch (poi.type) {
//            case BUS_LINE:
//                ret.type = Poi.TYPE_BUS_LINE;
//                break;
//            case BUS_STATION:
//                ret.type = Poi.TYPE_BUS_STATION;
//                break;
//            case SUBWAY_LINE:
//                ret.type = Poi.TYPE_SUBWAY_LINE;
//                break;
//            case SUBWAY_STATION:
//                ret.type = Poi.TYPE_SUBWAY_STATION;
//                break;
//            default:
//                ret.type = Poi.TYPE_POINT;
//                break;
//        }
//        return ret;
//    }
//
//    @Override
//    protected void notifyDecodeSuccess(GeoCodeResult geoCodeResult) {
//        if ((mLongitude <= 0D && mLatitude <= 0D) ||
//                (Double.compare(mLongitude, geoCodeResult.Longitude) == 0 &&
//                        Double.compare(mLatitude, geoCodeResult.Latitude) == 0)) {
//            notifyDecodeSuccess(geoCodeResult);
//        }
//    }
//
//
//    private class Listener implements OnGetGeoCoderResultListener {
//
//        private double mLongitude;
//        private double mLatitude;
//
//        public Listener() {
//        }
//
//        public Listener(double longitude, double latitude) {
//            this.mLongitude = longitude;
//            this.mLatitude = latitude;
//        }
//
//        @Override
//        public void onGetGeoCodeResult(com.baidu.mapapi.search.geocode.GeoCodeResult geoCodeResult) {
//            LatLng latLng = geoCodeResult == null ? null : geoCodeResult.getLocation();
//            if (latLng != null) {
//                GeoCodeResult ret = new GeoCodeResult(latLng.longitude, latLng.latitude);
//                notifyDecodeSuccess(ret);
//                return;
//            }
//            //Ignore error details.
//            notifyDecodeFail(new Exception("Fail to geo code."));
//        }
//
//        @Override
//        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
//            if (reverseGeoCodeResult != null && reverseGeoCodeResult.error == SearchResult.ERRORNO.NO_ERROR) {
//                GeoCodeResult ret = new GeoCodeResult(mLongitude, mLatitude);
//                ReverseGeoCodeResult.AddressComponent addr = reverseGeoCodeResult.getAddressDetail();
//                if (addr != null) {
//                    ret.Country = addr.countryName;
//                    ret.Province = addr.province;
//                    ret.City = addr.city;
//                    ret.District = addr.district;
//                    ret.Street = addr.street;
//                    ret.StreetNumber = addr.streetNumber;
//                }
//                ret.Address = reverseGeoCodeResult.getAddress();
//                if (reverseGeoCodeResult.getPoiList() != null && reverseGeoCodeResult.getPoiList().size() > 0) {
//                    ret.PoiList = convertPois(reverseGeoCodeResult.getPoiList());
//                }
//                notifyDecodeSuccess(ret);
//                return;
//            }
//            //Ignore error details.
//            notifyDecodeFail(new Exception("Fail to geo code."));
//        }
//    }
//}
//
