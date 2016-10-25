package lasting.travelassistant;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;

public class RoutePage extends Activity implements AMap.OnMapClickListener, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, RouteSearch.OnRouteSearchListener {
    private AMap aMap = null;
    private TextureMapView tmv = null;

    private LatLng startPoint = null;
    private LatLng endPoint = null;
    private String currentCity = null;

    private final int ROUTE_TYPE_BUS = 1;
    private final int ROUTE_TYPE_DRIVE = 2;
    private final int ROUTE_TYPE_WALK = 3;

    private RouteSearch rs = null;

    private RelativeLayout bottom = null;
    private LinearLayout busResult = null;
    private TextView routeTime = null;
    private TextView routeDetail = null;
    private ImageView routeDrive = null;
    private ImageView routeBus = null;
    private ImageView routeWalk = null;
    private ListView busList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        tmv = (TextureMapView) findViewById(R.id.route_map);
        tmv.onCreate(savedInstanceState);

        initMap();

        initRoute();
    }

    private void initRoute() {
        bottom = (RelativeLayout) findViewById(R.id.bottom_layout);
        busResult = (LinearLayout) findViewById(R.id.bus_result);
        routeTime = (TextView) findViewById(R.id.firstline);
        routeDetail = (TextView) findViewById(R.id.secondline);
        routeDrive = (ImageView) findViewById(R.id.route_drive);
        routeBus = (ImageView) findViewById(R.id.route_bus);
        routeWalk = (ImageView) findViewById(R.id.route_walk);
        busList = (ListView) findViewById(R.id.bus_result_list);
        rs = new RouteSearch(this);
        rs.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
                bottom.setVisibility(View.GONE);
                aMap.clear();
                if (i == 1000) {
                    if (busRouteResult != null && busRouteResult.getPaths() != null) {
                        if (busRouteResult.getPaths().size() > 0) {
                            BusResultListAdapter brla = new BusResultListAdapter(getApplicationContext(), busRouteResult);
                            busList.setAdapter(brla);
                        } else if (busRouteResult != null && busRouteResult.getPaths() == null) {
                            Toast.makeText(getApplicationContext(), "对不起，没有搜索到相关数据", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "对不起，没有搜索到相关数据", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error:" + i, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

            }
        });
    }

    private void initMap() {
        if (aMap == null) {
            aMap = tmv.getMap();
        }

        startPoint = new LatLng(getIntent().getDoubleExtra("startLat", 30.67), getIntent().getDoubleExtra("startLng", 104.06));
        endPoint = new LatLng(getIntent().getDoubleExtra("endLat", 30.67), getIntent().getDoubleExtra("endLng", 104.06));
        currentCity = getIntent().getStringExtra("currentCity");

        aMap.moveCamera(CameraUpdateFactory.changeLatLng(startPoint));
        aMap.addMarker(new MarkerOptions().position(startPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
        aMap.addMarker(new MarkerOptions().position(endPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));

        aMap.setOnMapClickListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tmv.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tmv.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tmv.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        tmv.onSaveInstanceState(outState);
    }

    public void onBusClick(View view) {
        searchRouteResult(ROUTE_TYPE_BUS, RouteSearch.BusDefault);
        routeDrive.setImageResource(R.drawable.route_drive_normal);
        routeBus.setImageResource(R.drawable.route_bus_select);
        routeWalk.setImageResource(R.drawable.route_walk_normal);
        tmv.setVisibility(View.GONE);
        busResult.setVisibility(View.VISIBLE);
    }

    public void onDriveClick(View view) {
        searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
        routeDrive.setImageResource(R.drawable.route_drive_select);
        routeBus.setImageResource(R.drawable.route_bus_normal);
        routeWalk.setImageResource(R.drawable.route_walk_normal);
        tmv.setVisibility(View.VISIBLE);
        busList.setVisibility(View.GONE);
    }

    public void onWalkClick(View view) {
        searchRouteResult(ROUTE_TYPE_WALK, RouteSearch.WalkDefault);
        routeDrive.setImageResource(R.drawable.route_drive_normal);
        routeBus.setImageResource(R.drawable.route_bus_normal);
        routeWalk.setImageResource(R.drawable.route_walk_select);
        tmv.setVisibility(View.VISIBLE);
        busList.setVisibility(View.GONE);
    }

    private void searchRouteResult(int routeType, int mode) {
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(startPoint.latitude, startPoint.longitude), new LatLonPoint(endPoint.latitude, endPoint.longitude));
        switch (routeType) {
            case ROUTE_TYPE_BUS:
                RouteSearch.BusRouteQuery brq = new RouteSearch.BusRouteQuery(fromAndTo, mode, currentCity, 0);
                rs.calculateBusRouteAsyn(brq);
                break;

            case ROUTE_TYPE_DRIVE:
                RouteSearch.DriveRouteQuery drq = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null, null, "");
                rs.calculateDriveRouteAsyn(drq);
                break;

            case ROUTE_TYPE_WALK:
                RouteSearch.WalkRouteQuery wrq = new RouteSearch.WalkRouteQuery(fromAndTo, mode);
                rs.calculateWalkRouteAsyn(wrq);
                break;

            default:
                break;
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }
}