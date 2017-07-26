package com.silence.baidumaplibrary;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.model.LatLng;

public class LocationListener implements BDLocationListener {

    private BaiduMapListener mBaiduMapListener;

    public LocationListener(BaiduMapListener baiduMapListener) {
        mBaiduMapListener = baiduMapListener;
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();


        LatLng latLng = new LatLng(lat, lng);
        mBaiduMapListener.getLatLng(latLng);
//        final MarkerOptions marker = new MarkerOptions().position(latLng).icon(
//                BitmapDescriptorFactory.fromResource(R.drawable.icon_geo)
//        );
//        getGeoCoder(latLng);
//        mBaiduMap.addOverlay(marker);



    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }
}
