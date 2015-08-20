package com.sdbnet.hywy.employee.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;
import com.sdbnet.hywy.employee.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/8/7.
 */
public class LocationActivity extends BaseActivity {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private ImageView mImgBack;
    private Button mBtnOk;
    private TextView mTextTitle;
    private ListView mListLocation;
    private List<Poi> mListPoi;
    private Marker mMarker;
    private int tag_id = 1000;
    private InfoWindow.OnInfoWindowClickListener mInfoWindowListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        initUI();
        initListener();
    }

    private void initUI() {

        mMapView = (MapView) findViewById(R.id.activity_location_bd_map_view);
        mImgBack = (ImageView) findViewById(R.id.common_view_title_img);
        mBtnOk = (Button) findViewById(R.id.common_view_title_btn);
        mTextTitle = (TextView) findViewById(R.id.common_view_title_text);
        mListLocation = (ListView) findViewById(R.id.activity_location_list);
        //init data
        mTextTitle.setText(R.string.location);
        mBtnOk.setVisibility(View.VISIBLE);
        mBtnOk.setText(R.string.determine);
        mBtnOk.setTextColor(Color.WHITE);
        mBaiduMap = mMapView.getMap();
        mListLocation.setAdapter(mBaseAdapter);

    }

    private void initListener() {
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mListLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        mInfoWindowListener = new InfoWindow.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick() {

            }
        };
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                //创建InfoWindow展示的view
                TextView txtView = new TextView(getApplicationContext());
                txtView.setBackgroundResource(R.drawable.popup);
                txtView.setId(tag_id);
//定义用于显示该InfoWindow的坐标点
//                LatLng pt = new LatLng(39.86923, 116.397428);
//创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
                InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory
                        .fromView(txtView), marker.getPosition(), -47, mInfoWindowListener);
//显示InfoWindow
                mBaiduMap.showInfoWindow(mInfoWindow);

                return false;
            }
        });
        mBaiduMap.setOnMyLocationClickListener(new BaiduMap.OnMyLocationClickListener() {
            @Override
            public boolean onMyLocationClick() {
                return false;
            }
        });

        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }
        });
    }

    private void addMarker() {
        if (mMarker != null) {
            mMarker.remove();
        }
        //定义Maker坐标点
        LatLng point = new LatLng(39.963175, 116.400244);
//构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_drawer);
//构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)  //设置marker的位置
                .icon(bitmap)  //设置marker图标
                .zIndex(9)  //设置marker所在层级
                .draggable(true);  //设置手势拖拽;
//在地图上添加Marker，并显示
        mMarker = (Marker) mBaiduMap.addOverlay(option);
    }

    private BaseAdapter mBaseAdapter = new BaseAdapter() {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HolderView holderView;
            Poi poi = (Poi) getItem(position);
            if (convertView == null) {
                holderView = new HolderView();
                convertView = LayoutInflater.from(LocationActivity.this).inflate(R.layout.item_locaiton, null);
                holderView.tvName = (TextView) convertView.findViewById(R.id.item_location_name);
                holderView.tvDetailed = (TextView) convertView.findViewById(R.id.item_location_name_detailed);
                holderView.tvSelected = (TextView) convertView.findViewById(R.id.item_location_text_selected);
                convertView.setTag(holderView);
            } else {
                holderView = (HolderView) convertView.getTag();
            }
            if (position == 0) {

            } else if (poi != null) {
//                holderView.tvName.setText(poi.);
            }
            return convertView;
        }

        @Override
        public int getCount() {
            if (mListPoi == null) {
                mListPoi = new ArrayList<Poi>();
            }
            return mListPoi.size();
        }

        @Override
        public Object getItem(int position) {
            if (mListPoi == null) {
                return null;
            }
            return mListPoi.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class HolderView {
            TextView tvName;
            TextView tvDetailed;
            TextView tvSelected;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
}
