package com.duimy.baidumapdome;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
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


public class MainActivity extends AppCompatActivity {
    MapView mMapView = null;
    private BaiduMap mBaiduMap;


    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private BitmapDescriptor mCurrentMarker;
    private Button mBt;
    private RecyclerView mRecycMapList;
    private MapAdapter mMapAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);

        mBt = (Button) findViewById(R.id.btn);
        mRecycMapList = ((RecyclerView) findViewById(R.id.maplist));

//        mBt.setOnClickListener();


        mBaiduMap = mMapView.getMap();


        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        init();


        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数

        initLocation();

        mLocationClient.start();

        mRecycMapList.setLayoutManager(new LinearLayoutManager(this));
        mMapAdapter = new MapAdapter();
        mRecycMapList.setAdapter(mMapAdapter);
        mRecycMapList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }

    private void init() {
        LatLng cenpt = new LatLng(30.459605, 114.433097);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(18)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span = 0;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mLocationClient.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    class MyLocationListener implements BDLocationListener {


        @Override
        public void onReceiveLocation(BDLocation location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();


            LatLng latLng = new LatLng(lat, lng);
            final MarkerOptions marker = new MarkerOptions().position(latLng).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.icon_geo)
            );
            getGeoCoder(latLng);
            mBaiduMap.addOverlay(marker);


            //定义Maker坐标点
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.map_tag);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(latLng)
                    .icon(bitmap).draggable(true);
            //在地图上添加Marker，并显示
            final Marker markerTon = (Marker) mBaiduMap.addOverlay(option);
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
                    markerTon.setPosition(mapStatus.target);
                    LatLng latLng = mapStatus.target;

                    getGeoCoder(latLng);

//                    //反地理编码需要传递坐标点参数.
//                    geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(mapStatus.target));

                }
            });
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    @NonNull
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
                return;
            }

        });

        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
    }
}

