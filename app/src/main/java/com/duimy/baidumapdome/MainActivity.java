package com.duimy.baidumapdome;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;


public class MainActivity extends AppCompatActivity implements MainActivityView {
    MapView mMapView = null;
    private BaiduMap mBaiduMap;


    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener(this);
    private BitmapDescriptor mCurrentMarker;
    private String mAddress;
    private LatLng mLocation;
    private Button mBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        //        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont/iconfont.ttf");
        //        TextView like = (TextView) findViewById(R.id.like);
        //        like.setTypeface(iconfont);

        mBt = (Button) findViewById(R.id.btn);

        mBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "最新的坐标为   :   " + mLocation.toString()
                        + ", 地址是    :   " + mAddress, Toast.LENGTH_LONG).show();
            }
        });

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);



        mBaiduMap = mMapView.getMap();


        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);


        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数

        initLocation();

        mLocationClient.start();


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

    @Override
    public void setBaiduMap(double lat, double lon, String add, float radius) {


        //        // 开启定位图层
        //        mBaiduMap.setMyLocationEnabled(true);
        //        // 构造定位数据
        //        MyLocationData locData = new MyLocationData.Builder()
        //                .accuracy(radius)
        //                // 此处设置开发者获取到的方向信息，顺时针0-360
        //                .direction(100).latitude(lat)
        //                .longitude(lon).build();
        //        // 设置定位数据
        //        mBaiduMap.setMyLocationData(locData);

        //定义Maker坐标点
        LatLng point = new LatLng(lon, lat);
        //构建Marker图标
        mLocation = point;
        mAddress = add;
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.map_tag);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(mLocation)
                .icon(bitmap).draggable(true);
        //在地图上添加Marker，并显示
        final Marker marker = (Marker) mBaiduMap.addOverlay(option);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(point, 17.0f);
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


                GeoCoder geoCoder = GeoCoder.newInstance();
                geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(mapStatus.target));
                geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {


                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                        Log.e("onGetGeoCode","address  :" + geoCodeResult.getAddress());
                    }

                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                        String address = reverseGeoCodeResult.getAddress();
                        Log.e("onGetReverse","address  :" + address);
                        if (!TextUtils.isEmpty(address)) {
                            Log.e("address", "mLocation    :" + reverseGeoCodeResult.getLocation()
                                    .toString() + "\naddress  :" + address);
                            mLocation = reverseGeoCodeResult.getLocation();
                            mAddress = address;
                            return;
                        }
                        Toast.makeText(MainActivity.this, "获取位置失败,请重新获取", Toast.LENGTH_LONG).show();

                    }
                });

            }
        });


        LatLng cenpt = new LatLng(lat, lon);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(18)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);


        //         设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
        //        mCurrentMarker = BitmapDescriptorFactory
        //                .fromResource(R.drawable.icon_geo);
        //        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, mCurrentMarker);
        //        mBaiduMap.setMyLocationConfiguration(config);

        // 当不需要定位图层时关闭定位图层
        //        mBaiduMap.setMyLocationEnabled(false);

    }
}

