package com.csg.zhong.demo_baidumapsdk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;

    private LocationClient mLocationClient = null;
    private MyLocationListener mLocationListener = null;

    private boolean isFirstIn = true;

    private double mlatitude = 0.0d;
    private double mlongitude = 0.0d;
    private float mRadius;

    private BitmapDescriptor mBitmapDescriptor = null;
    private MyOrientationListener mOrientationListener = null;
    private float mCurrentX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.activity_main);
        initView();
        initLocation();
    }

    private void initLocation() {
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(500);
        mLocationClient.setLocOption(option);

        mLocationClient.registerLocationListener(mLocationListener);

        mBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.icon_location);

        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mBitmapDescriptor);
        mBaiduMap.setMyLocationConfiguration(config);

        mOrientationListener = new MyOrientationListener(this);
        mOrientationListener.setOnOrientationChangedListener(new MyOrientationListener.OnOrientationChangedListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
                MyLocationData data = new MyLocationData.Builder()//
                        .direction(mCurrentX)//
                        .accuracy(mRadius)//
                        .latitude(mlatitude)//
                        .longitude(mlongitude)//
                        .build();
                mBaiduMap.setMyLocationData(data);
            }
        });

    }

    private void init() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        //        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
    }

    private void initView() {
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bdmapView);
        //得到BaiduMap对象
        mBaiduMap = mMapView.getMap();
        //MapStatusUpdateFactory工厂模式得到放大比例为15.0的MapStatusUpdate对象
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(15.0f);
        //刚刚得到的MapStatusUpdate对象设置给BaiduMap对象
        mBaiduMap.setMapStatus(mapStatusUpdate);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
        mOrientationListener.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false);
        if (mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        mOrientationListener.stop();
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
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    //普通地图和卫星地图切换
    public void common(View view) {
        Button btn = (Button) view;
        if (TextUtils.equals("当前：普通地图", btn.getText().toString())) {
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
            btn.setText("当前：卫星地图");
        } else {
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            btn.setText("当前：普通地图");
        }
    }

    //打开&关闭实时交通
    public void transport(View view) {
        Button btn = (Button) view;
        if (mBaiduMap.isTrafficEnabled()) {
            mBaiduMap.setTrafficEnabled(false);
            btn.setText("实时交通：Off");
        } else {
            mBaiduMap.setTrafficEnabled(true);
            btn.setText("实时交通：On");
        }
    }

    public void location(View view) {
        LatLng mLatLng = new LatLng(mlatitude, mlongitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(mLatLng);
        mBaiduMap.setMapStatus(msu);
    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            mRadius = bdLocation.getRadius();
            mlatitude = bdLocation.getLatitude();
            mlongitude = bdLocation.getLongitude();
            MyLocationData data = new MyLocationData.Builder()//
                    .direction(mCurrentX)//
                    .accuracy(mRadius)//
                    .latitude(mlatitude)//
                    .longitude(mlongitude)//
                    .build();
            mBaiduMap.setMyLocationData(data);
            //            Toast.makeText(MainActivity.this, bdLocation.getAddrStr(), Toast.LENGTH_LONG).show();
            if (isFirstIn) {
                LatLng latLng = new LatLng(mlatitude, mlongitude);
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(msu);
                isFirstIn = false;
            }

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

    }

}


