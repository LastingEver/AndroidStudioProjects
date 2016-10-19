package overlay;

import java.util.List;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideStep;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.AMapUtil;

/**
 * 骑行路线图层类。在高德地图API里，如果要显示步行路线规划，可以用此类来创建骑行路线图层。如不满足需求，也可以自己创建自定义的骑行路线图层。
 * @since V3.5.0
 */
public class RideRouteOverlay extends RouteOverlay {

	private PolylineOptions mPolylineOptions;
	
	private BitmapDescriptor walkStationDescriptor= null;

	private RidePath ridePath;
	/**
	 * 通过此构造函数创建骑行路线图层。
	 * @param context 当前activity。
	 * @param amap 地图对象。
	 * @param path 骑行路线规划的一个方案。详见搜索服务模块的路径查询包（com.amap.api.services.route）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/route/WalkStep.html" title="com.amap.api.services.route中的类">WalkStep</a></strong>。
	 * @param start 起点。详见搜索服务模块的核心基础包（com.amap.api.services.core）中的类<strong><a href="../../../../../../Search/com/amap/api/services/core/LatLonPoint.html" title="com.amap.api.services.core中的类">LatLonPoint</a></strong>。
	 * @param end 终点。详见搜索服务模块的核心基础包（com.amap.api.services.core）中的类<strong><a href="../../../../../../Search/com/amap/api/services/core/LatLonPoint.html" title="com.amap.api.services.core中的类">LatLonPoint</a></strong>。
	 * @since V3.5.0
	 */
	public RideRouteOverlay(Context context, AMap amap, RidePath path,
							LatLonPoint start, LatLonPoint end) {
		super(context);
		this.mAMap = amap;
		this.ridePath = path;
		startPoint = AMapUtil.convertToLatLng(start);
		endPoint = AMapUtil.convertToLatLng(end);
	}
	/**
	 * 添加骑行路线到地图中。
	 * @since V3.5.0
	 */
	public void addToMap() {
		
		initPolylineOptions();
		try {
			List<RideStep> ridePaths = ridePath.getSteps();
			for (int i = 0; i < ridePaths.size(); i++) {
				RideStep rideStep = ridePaths.get(i);
				LatLng latLng = AMapUtil.convertToLatLng(rideStep
						.getPolyline().get(0));
				if (i < ridePaths.size() - 1) {
					if (i == 0) {
						addRidePolyLine(startPoint, latLng);
					}
//					checkDistanceToNextStep(walkStep, walkPaths.get(i + 1));
				} else {
					LatLng latLng1 = AMapUtil
							.convertToLatLng(getLastWalkPoint(rideStep));
					addRidePolyLine(latLng1, endPoint);
				}

				addRideStationMarkers(rideStep, latLng);
				addRidePolyLines(rideStep);
			}
			addStartAndEndMarker();
			
			showPolyline();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
//	/**
//	 * 检查这一步的最后一点和下一步的起始点之间是否存在空隙
//	 * @param rideStep
//	 * @param rideStep1
//	 */
//	private void checkDistanceToNextStep(RideStep rideStep,
//										 RideStep rideStep1) {
//		LatLonPoint lastPoint = getLastWalkPoint(rideStep);
//		LatLonPoint nextFirstPoint = getFirstWalkPoint(rideStep1);
//		if (!(lastPoint.equals(nextFirstPoint))) {
//			addRidePolyLine(lastPoint, nextFirstPoint);
//		}
//	}
	/**
	 * @param rideStep
	 * @return
	 */
	private LatLonPoint getLastWalkPoint(RideStep rideStep) {
		return rideStep.getPolyline().get(rideStep.getPolyline().size() - 1);
	}
	/**
	 * @param rideStep
	 * @return
	 */
	private LatLonPoint getFirstWalkPoint(RideStep rideStep) {
		return rideStep.getPolyline().get(0);
	}

	private void addRidePolyLine(LatLonPoint pointFrom, LatLonPoint pointTo) {
		addRidePolyLine(AMapUtil.convertToLatLng(pointFrom), AMapUtil.convertToLatLng(pointTo));
	}
	
	private void addRidePolyLine(LatLng latLngFrom, LatLng latLngTo) {
		mPolylineOptions.add(latLngFrom, latLngTo);
	}

	/**
	 * @param rideStep
	 */
	private void addRidePolyLines(RideStep rideStep) {
		mPolylineOptions.addAll(AMapUtil.convertArrList(rideStep.getPolyline()));
	}
	/**
	 * @param rideStep
	 * @param position
	 */
	private void addRideStationMarkers(RideStep rideStep, LatLng position) {
		addStationMarker(new MarkerOptions()
				.position(position)
				.title("\u65B9\u5411:" + rideStep.getAction()
						+ "\n\u9053\u8DEF:" + rideStep.getRoad())
				.snippet(rideStep.getInstruction()).visible(nodeIconVisible)
				.anchor(0.5f, 0.5f).icon(walkStationDescriptor));
	}
	
	 /**
     * 初始化线段属性
     */
    private void initPolylineOptions() {
    	
    	if(walkStationDescriptor == null) {
    		walkStationDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.amap_ride);
    	}
        mPolylineOptions = null;
        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(getDriveColor()).width(getRouteWidth());
    }
	 private void showPolyline() {
	        addPolyLine(mPolylineOptions);
	    }
}
