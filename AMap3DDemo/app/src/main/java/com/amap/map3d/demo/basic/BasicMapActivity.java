package com.amap.map3d.demo.basic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.CancelableCallback;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.map3d.demo.R;


/**
 * AMapV2地图中介绍如何显示一个基本地图
 */
public class BasicMapActivity extends Activity implements OnClickListener, OnMapClickListener{
	private MapView mapView;
	private AMap aMap;
	private Button basicmap;
	private Button rsmap;
	private Button nightmap;
	private Boolean firsttouch = true;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.basicmap_activity);
	    /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
	    //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
      //  MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
		
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();

	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		aMap.setOnMapClickListener(this);
		basicmap = (Button)findViewById(R.id.basicmap);
		basicmap.setOnClickListener(this);
		rsmap = (Button)findViewById(R.id.rsmap);
		rsmap.setOnClickListener(this);
		nightmap = (Button)findViewById(R.id.nightmap);
		nightmap.setOnClickListener(this);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.basicmap:
			aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
			break;
		case R.id.rsmap:
			aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
			break;
		case R.id.nightmap:
			aMap.setMapType(AMap.MAP_TYPE_NIGHT);//夜景地图模式
			break;
		}
		
	}

	@Override
	public void onMapClick(LatLng arg0) {
//		aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.92463,116.389139),18), 1000, null);
//		aMap.moveCamera(CameraUpdateFactory.changeTilt(60));
		if (firsttouch) {
			firsttouch = false;
			aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(39.92463,116.389139), 18, 0, 0)), 1500, new CancelableCallback() {
			
			@Override
			public void onFinish() {
				aMap.moveCamera(CameraUpdateFactory.changeTilt(60));
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				aMap.animateCamera(CameraUpdateFactory.changeBearing(90), 2000, null);
				
			}
			
			@Override
			public void onCancel() {
				
			}
		});
			
		}
		
		
//		aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(39.92463,116.389139), 18, 60, 0)),1500,null);

		
	}

}
