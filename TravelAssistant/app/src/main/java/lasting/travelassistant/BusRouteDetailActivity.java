package lasting.travelassistant;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;

public class BusRouteDetailActivity extends Activity implements AMap.OnMapLoadedListener, AMap.OnMapClickListener, AMap.OnMarkerClickListener, AMap.InfoWindowAdapter, AMap.OnInfoWindowClickListener {
    private AMap aMap = null;
    private TextureMapView tmv = null;

    private BusPath busPath = null;
    private BusRouteResult busRouteResult = null;

    private TextView detailTitle = null;
    private TextView busRouteTitle = null;
    private TextView busRouteDes = null;
    private LinearLayout busMap = null;
    private LinearLayout busView = null;
    private ListView busList = null;

    private BusSegmentListAdapter busSegmentListAdapter;

    private BusRouteOverlay bro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);
        tmv = (TextureMapView) findViewById(R.id.route_map);
        tmv.onCreate(savedInstanceState);

        initMap();

        initRoute();
    }

    private void initRoute() {
        detailTitle = (TextView) findViewById(R.id.title_center);
        detailTitle.setText("公交路线详情");
        busRouteTitle = (TextView) findViewById(R.id.firstline);
        busRouteDes = (TextView) findViewById(R.id.secondline);
        String dur = AMapUtil.getFriendlyTime((int) busPath.getDuration());
        String dis = AMapUtil.getFriendlyLength((int) busPath.getDistance());
        busRouteTitle.setText(dur + "(" + dis + ")");
        int taxiCost = (int) busRouteResult.getTaxiCost();
        busRouteDes.setText("打车约" + taxiCost + "元");
        busRouteDes.setVisibility(View.VISIBLE);
        busMap = (LinearLayout) findViewById(R.id.title_map);
        busMap.setVisibility(View.VISIBLE);
        busView = (LinearLayout) findViewById(R.id.bus_path);
        configureListView();
    }

    private void configureListView() {
        busList = (ListView) findViewById(R.id.bus_segment_list);
        busSegmentListAdapter = new BusSegmentListAdapter(getApplicationContext(), busPath.getSteps());
        busList.setAdapter(busSegmentListAdapter);
    }

    private void initMap() {
        if (aMap == null) {
            aMap = tmv.getMap();
        }

        aMap.setOnMapLoadedListener(this);
        aMap.setOnMapClickListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(this);

        busPath = getIntent().getParcelableExtra("busPath");
        busRouteResult = getIntent().getParcelableExtra("busResult");
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

    public void onBackClick(View view) {
        this.finish();
    }

    public void onMapClick(View view) {
        busView.setVisibility(View.GONE);
        busMap.setVisibility(View.GONE);
        tmv.setVisibility(View.VISIBLE);
        aMap.clear();
        bro = new BusRouteOverlay(this, aMap, busPath, busRouteResult.getStartPos(), busRouteResult.getTargetPos());
        bro.removeFromMap();
    }

    @Override
    public void onMapLoaded() {
        if (bro != null) {
            bro.addToMap();
            bro.zoomToSpan();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
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
}
