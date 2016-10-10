package com.feicuiedu.treasure.treasure.home.map;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.feicuiedu.treasure.R;
import com.feicuiedu.treasure.commons.ActivityUtils;
import com.feicuiedu.treasure.components.TreasureView;
import com.feicuiedu.treasure.treasure.Area;
import com.feicuiedu.treasure.treasure.Treasure;
import com.feicuiedu.treasure.treasure.TreasureRepo;
import com.feicuiedu.treasure.treasure.home.detail.TreasureDetailActivity;
import com.feicuiedu.treasure.treasure.home.hide.HideTreasureActivity;
import com.hannesdorfmann.mosby.mvp.MvpFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 */
public class MapFragment extends MvpFragment<MapMvpView, MapPresenter> implements MapMvpView {

    @Bind(R.id.map_frame)
    FrameLayout mapFrame;
    @Bind(R.id.centerLayout)
    RelativeLayout centerLayout;
    @Bind(R.id.treasureView)
    TreasureView treasureView;
    @Bind(R.id.layout_bottom)
    FrameLayout layoutBottom;
    @Bind(R.id.hide_treasure)
    RelativeLayout hideTreasure;
    @Bind(R.id.btn_HideHere)
    Button btnHideHere;
    @Bind(R.id.tv_currentLocation)
    TextView tvCurrentLocation;
    @Bind(R.id.iv_located)
    ImageView ivLocated;
    @Bind(R.id.et_treasureTitle)
    EditText etTreasureTitle;
    private MapView mapView;
    private BaiduMap baiduMap;
    private LocationClient locationClient;
    private static String myAddress;
    private static LatLng myLocation;
    private String address;

    private ActivityUtils activityUtils;

    private BitmapDescriptor dot = BitmapDescriptorFactory.fromResource(R.drawable.treasure_dot);
    private BitmapDescriptor dot_click = BitmapDescriptorFactory.fromResource(R.drawable.treasure_expanded);
    private GeoCoder geoCoder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        activityUtils = new ActivityUtils(this);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public MapPresenter createPresenter() {
        return new MapPresenter();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化百度地图
        initBaiduMap();

        // 初始化定位
        initLocation();

        // 地理编码
        initGeoCoder();
    }

    private void initGeoCoder() {
        geoCoder = GeoCoder.newInstance();

        // 进行地理编码监听
        geoCoder.setOnGetGeoCodeResultListener(getGeoCoderResultListener);
    }

    private OnGetGeoCoderResultListener getGeoCoderResultListener = new OnGetGeoCoderResultListener() {

        // 地理编码（地址---> 经纬度）
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        // 反地理编码(经纬度--> 地址信息)
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            if (reverseGeoCodeResult==null)return;
            if (reverseGeoCodeResult.error== SearchResult.ERRORNO.NO_ERROR){
                address = "未知";
            }
            address = reverseGeoCodeResult.getAddress();
            // 地址得到了，要把信息放到标题信息录入卡片上面
            tvCurrentLocation.setText(address);
        }
    };

    private void initLocation() {
        // 激活定位图层
        baiduMap.setMyLocationEnabled(true);
        // 定位实例化
        locationClient = new LocationClient(getActivity().getApplicationContext());

        // 进行一些定位的一般常规性设置
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开GPS
        option.setScanSpan(60000);// 扫描周期,设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setCoorType("bd09ll");// 百度坐标类型
        option.setLocationNotify(true);//设置是否当gps有效时按照1S1次频率输出GPS结果
        option.SetIgnoreCacheException(false);//设置是否收集CRASH信息，默认收集
        option.setIsNeedAddress(true);// 设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true);//设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationClient.setLocOption(option);

        // 注册定位监听
        locationClient.registerLocationListener(locationListener);
        // 开始定位
        locationClient.start();
        locationClient.requestLocation(); // 请求位置(解决部分机器,初始定位不成功问题)
    }

    private void initBaiduMap() {

        // 查看百度地图的ＡＰＩ

        // 百度地图状态
        MapStatus mapStatus = new MapStatus.Builder()
                .overlook(0)// 0--(-45) 地图的俯仰角度
                .zoom(15)// 3--21 缩放级别
                .build();

        BaiduMapOptions options = new BaiduMapOptions()
                .mapStatus(mapStatus)// 设置地图的状态
                .compassEnabled(true)// 指南针
                .zoomGesturesEnabled(true)// 设置允许缩放手势
                .rotateGesturesEnabled(true)// 旋转
                .scaleControlEnabled(false)// 不显示比例尺控件
                .zoomControlsEnabled(false)// 不显示缩放控件
                ;

        // 创建一个MapView
        mapView = new MapView(getContext(), options);

        // 在当前的Layout上面添加MapView
        mapFrame.addView(mapView, 0);

        // MapView 的控制器
        baiduMap = mapView.getMap();

        // 怎么对地图状态进行监听？
        baiduMap.setOnMapStatusChangeListener(mapStatusChangeListener);

        // 设置对Marker的点击监听
        baiduMap.setOnMarkerClickListener(markerClickListener);

    }

    public static LatLng getMyLocation() {
        return myLocation;
    }

    public static String getMyAddress(){
        return myAddress;
    }


    private boolean isFirstLocated = true;// 用来判断是不是首次定位

    private BDLocationListener locationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            // 定位有没有成功
            if (bdLocation == null) {
                locationClient.requestLocation();
                return;
            }

            double lng = bdLocation.getLongitude();// 经度
            double lat = bdLocation.getLatitude();// 维度

            // 获取地址信息
            myAddress = bdLocation.getAddrStr();

            // 获取经纬度
            myLocation = new LatLng(lat, lng);

            // 拿到定位的信息（经纬度）
            MyLocationData myLocationData = new MyLocationData.Builder()
                    .longitude(lng)
                    .latitude(lat)
                    .accuracy(100f)//精度，定位圈的大小
                    .build();
            //设置到地图上
            baiduMap.setMyLocationData(myLocationData);

            if (isFirstLocated) {
                animateMoveToMyLocation();
                isFirstLocated = false;
            }
        }
    };

    // 移动到定位的地方
    @OnClick(R.id.tv_located)
    public void animateMoveToMyLocation() {
        MapStatus mapStatus = new MapStatus.Builder()
                .target(myLocation)
                .rotate(0)// 地图位置摆正
                .zoom(19)
                .build();

        // 状态(新的位置信息)进行更新
        MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(mapStatus);
        baiduMap.animateMapStatus(update);
    }

    // 地图类型的切换（普通视图--卫星视图）
    @OnClick(R.id.tv_satellite)
    public void switchMapType() {

        // 先获得当前的类型
        int type = baiduMap.getMapType();
        type = type == BaiduMap.MAP_TYPE_NORMAL ? BaiduMap.MAP_TYPE_SATELLITE : BaiduMap.MAP_TYPE_NORMAL;
        baiduMap.setMapType(type);
    }

    // 指南针
    @OnClick(R.id.tv_compass)
    public void switchCompass() {
        boolean isCompass = baiduMap.getUiSettings().isCompassEnabled();
        baiduMap.getUiSettings().setCompassEnabled(!isCompass);
    }

    // 地图缩放
    @OnClick({R.id.iv_scaleUp, R.id.iv_scaleDown})
    public void scaleMap(View view) {
        switch (view.getId()) {
            case R.id.iv_scaleUp:
                baiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                break;
            case R.id.iv_scaleDown:
                baiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                break;
        }
    }

    @OnClick(R.id.treasureView)
    public void clickTreasureView() {
        int id = currentMarker.getExtraInfo().getInt("id");
        Treasure treasure = TreasureRepo.getInstance().getTreasure(id);
        TreasureDetailActivity.open(getContext(), treasure);
    }

    @OnClick(R.id.hide_treasure)
    public void clickHideTreasure(){
        //处理埋藏宝藏标题的录入和页面跳转
        activityUtils.hideSoftKeyboard();
        String title = etTreasureTitle.getText().toString();
        if (TextUtils.isEmpty(title)){
            activityUtils.showToast("请输入宝藏标题");
            return;
        }
        // 跳转页面，标题，经纬度，地址信息
        LatLng latLng = baiduMap.getMapStatus().target;
        HideTreasureActivity.open(getContext(),title,address,latLng,0);
    }


    private LatLng target;

    // 百度地图状态的监听
    private BaiduMap.OnMapStatusChangeListener mapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            // 状态改变完成
            LatLng target = mapStatus.target;

            // 判断位置有没有变化
            if (target != MapFragment.this.target) {
                updateMapArea();
                // 在宝藏埋藏模式下
                if (uiMode==UI_MODE_HIDE){

                    // 进行反地理编码
                    ReverseGeoCodeOption option = new ReverseGeoCodeOption();
                    option.location(target);
                    geoCoder.reverseGeoCode(option);

                    // 反弹动画
                    YoYo.with(Techniques.Bounce).duration(1000).playOn(btnHideHere);
                    YoYo.with(Techniques.Bounce).duration(1000).playOn(ivLocated);
                    YoYo.with(Techniques.FadeIn).duration(1000).playOn(btnHideHere);
                }

                MapFragment.this.target = target;
            }
        }
    };

    private void updateMapArea() {
        // 获取当前的位置
        MapStatus mapStatus = baiduMap.getMapStatus();

        // 获取经纬度
        double lng = mapStatus.target.longitude;
        double lat = mapStatus.target.latitude;

        // 计算出你的Area  23.999  15.130
        //                  24,23  ,  16,15去确定Area

        Area area = new Area();
        area.setMaxLat(Math.ceil(lat));  // lat向上取整
        area.setMaxLng(Math.ceil(lng));  // lng向上取整
        area.setMinLat(Math.floor(lat));  // lat向下取整
        area.setMinLng(Math.floor(lng));  // lng向下取整

        // 根据区域来进行数据获取
        getPresenter().getTreasure(area);
    }

    public void switchToHideTreasure(){
        changeUiMode(UI_MODE_HIDE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private static final int UI_MODE_NORMAL = 0;// 普通视图
    private static final int UI_MODE_SELECT = 1;// 宝藏选中视图
    private static final int UI_MODE_HIDE = 2;// 埋藏宝藏视图

    private int uiMode = UI_MODE_NORMAL;

    // 切换不同的视图模式
    private void changeUiMode(int uiMode) {

        if (this.uiMode == uiMode) {
            return;
        }
        this.uiMode = uiMode;
        switch (uiMode) {

            // 普通模式视图
            case UI_MODE_NORMAL: {
                if (currentMarker != null) {
                    currentMarker.setVisible(true);
                }
                baiduMap.hideInfoWindow();
                layoutBottom.setVisibility(View.GONE);
                centerLayout.setVisibility(View.GONE);
            }
            break;

            // 选中模式视图
            case UI_MODE_SELECT: {
                layoutBottom.setVisibility(View.VISIBLE);
                treasureView.setVisibility(View.VISIBLE);
                centerLayout.setVisibility(View.GONE);
                hideTreasure.setVisibility(View.GONE);
            }
            break;

            // 埋藏模式视图
            case UI_MODE_HIDE: {

                /**
                 * VISIBLE 显示
                 * GONE 隐藏  不再占用空间
                 * INVISIBLE隐藏  占用原来的位置空间
                 */
                centerLayout.setVisibility(View.VISIBLE);
                layoutBottom.setVisibility(View.GONE);
                // 按下藏在这里按钮的时候会显示
                btnHideHere.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutBottom.setVisibility(View.VISIBLE);
                        hideTreasure.setVisibility(View.VISIBLE);
                        treasureView.setVisibility(View.GONE);
                    }
                });

            }
            break;
        }

    }


    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    @Override
    public void setData(List<Treasure> datas) {
        for (Treasure treasure : datas) {
            LatLng position = new LatLng(treasure.getLatitude(), treasure.getLongitude());
            // 根据经纬度来添加覆盖物（Marker）,同时要将宝藏传入
            addMarker(position, treasure.getId());
        }
    }

    // 添加Marker覆盖物
    private void addMarker(LatLng position, int treasureId) {

        // Marker的一些设置
        MarkerOptions options = new MarkerOptions();
        options.position(position);// 设置位置
        options.icon(dot);// 设置Marker 的图标
        options.anchor(0.5f, 0.5f);// 设置锚点
        // 将id放到Marker里面去
        Bundle bundle = new Bundle();
        bundle.putInt("id", treasureId);
        options.extraInfo(bundle);
        baiduMap.addOverlay(options);// 添加覆盖物

    }

    private Marker currentMarker;

    private BaiduMap.OnMarkerClickListener markerClickListener = new BaiduMap.OnMarkerClickListener() {

        // 当点击Marker时候回触发
        @Override
        public boolean onMarkerClick(Marker marker) {

            // 点击事件
            if (currentMarker != null) {
                currentMarker.setVisible(true);
            }
            currentMarker = marker;
            currentMarker.setVisible(false);

            InfoWindow infoWindow = new InfoWindow(dot_click, marker.getPosition(), 0, infowindowListener);
            baiduMap.showInfoWindow(infoWindow);

            int id = marker.getExtraInfo().getInt("id");
            Treasure treasure = TreasureRepo.getInstance().getTreasure(id);
            treasureView.bindTreasure(treasure);

            // 切换到宝藏选中视图模式
            changeUiMode(UI_MODE_SELECT);

            return false;
        }
    };

    // 点击InfoWindow的监听
    private InfoWindow.OnInfoWindowClickListener infowindowListener = new InfoWindow.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick() {
            //  显示为普通的视图模式
            changeUiMode(UI_MODE_NORMAL);
        }
    };

    // 按下Back键来调用
    public boolean clickBackPressed(){
        if (this.uiMode!=UI_MODE_NORMAL){
            changeUiMode(UI_MODE_NORMAL);
            return false;
        }
        return true;
    }
}
