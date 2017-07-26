package com.silence.baidumaplibrary;

import android.app.Application;
import android.location.LocationListener;
import android.util.Log;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import java.util.List;

public class BaiduMapManage {

    private BaiduMap mBaiduMap;
    private static BaiduMapManage mInstance;
    private LocationClient mLocationClient = null;
    private Application mApplication;
    private int mMarkerBitmap;
    private BDLocationListener myListener = new LocationListener();



    public static BaiduMapManage getInstance() {
        if (mInstance == null) {
            mInstance = new BaiduMapManage();
        }
        return mInstance;
    }

    public BaiduMapManage setApplication(Application application) {
        mApplication = application;
        return this;
    }

    public BaiduMapManage setMarkerBitMap(int markerBitmap) {
        mMarkerBitmap = markerBitmap;
        return this;
    }

    public BaiduMap getBaiduMap() {
        if (mBaiduMap == null) {
            throw new RuntimeException("请先传入mBaiduMap的实例");
        }
        return mBaiduMap;
    }

    public BaiduMapManage setBaiduMap(BaiduMap baiduMap) {
        if (mBaiduMap == null) {
            mBaiduMap = baiduMap;
        }
        return this;
    }

    private BaiduMapManage init() {
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .zoom(18)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);

        mLocationClient = new LocationClient(mApplication);
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);

        initmarker();

        //注册监听函数
        return this;
    }

    private void initmarker() {
        //定义Maker坐标点
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(mMarkerBitmap);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(latLng)
                .icon(bitmap).draggable(true);
        //在地图上添加Marker，并显示
        final Marker marker = (Marker) mBaiduMap.addOverlay(option);

        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng, 17.0f);
        mBaiduMap.animateMapStatus(mapStatusUpdate);

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                marker.setPosition(mapStatus.target);
                LatLng latLng = mapStatus.target;

                getGeoCoder(latLng);
            }
        });
    }


    private void getGeoCoder(LatLng latLng) {
        GeoCoder geoCoder = GeoCoder.newInstance();

        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                Log.e("onGetGeoCode", "address  :" + geoCodeResult.getAddress());
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

                List<PoiInfo> poiList = reverseGeoCodeResult.getPoiList();
                mMapAdapter.setPoiData(poiList);
                mMapAdapter.notifyDataSetChanged();
                return poiList;
            }

        });

        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
    }

}
