package lasting.travelassistant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MapPage extends SupportMapFragment implements LocationSource, AMapLocationListener {
    private AMap aMap = null;
    private UiSettings uiSettings = null;

    private AMapLocationClient aMapLocationClient = null;
    private AMapLocationClientOption aMapLocationClientOption = null;
    private LocationSource.OnLocationChangedListener onLocationChangedListener = null;

    private boolean isFirstLoc = true;

    private View view = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_map, container, false);

        initMap();

        initLoc();

        return view;
    }

    private void initMap() {
        aMap = ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        aMap.setTrafficEnabled(true);
        aMap.setLocationSource(this);

        uiSettings = aMap.getUiSettings();

        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setScaleControlsEnabled(true);
        uiSettings.setAllGesturesEnabled(true);

        aMap.setMyLocationEnabled(true);
    }

    private void initLoc() {
        aMapLocationClient = new AMapLocationClient(this.getActivity());
        aMapLocationClient.setLocationListener(this);

        aMapLocationClientOption = new AMapLocationClientOption();
        aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        aMapLocationClientOption.setNeedAddress(true);
        aMapLocationClientOption.setOnceLocation(false);
        aMapLocationClientOption.setWifiActiveScan(true);
        aMapLocationClientOption.setMockEnable(true);
        aMapLocationClientOption.setInterval(2000);

        aMapLocationClient.setLocationOption(aMapLocationClientOption);
        aMapLocationClient.startLocation();
    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        this.onLocationChangedListener = onLocationChangedListener;
        if (aMapLocationClient == null){
            initLoc();
        }
    }

    @Override
    public void deactivate() {
        this.onLocationChangedListener = null;
        if (aMapLocationClient != null){
            aMapLocationClient.stopLocation();
            aMapLocationClient.onDestroy();
            aMapLocationClient = null;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null){
            if (aMapLocation.getErrorCode() == 0){
                aMapLocation.getLocationType();
                aMapLocation.getLatitude();
                aMapLocation.getLongitude();
                aMapLocation.getAccuracy();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                sdf.format(date);

                aMapLocation.getAddress();
                aMapLocation.getCountry();
                aMapLocation.getProvince();
                aMapLocation.getCity();
                aMapLocation.getDistrict();
                aMapLocation.getStreet();
                aMapLocation.getStreetNum();
                aMapLocation.getCityCode();
                aMapLocation.getAdCode();

                if (isFirstLoc){
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    onLocationChangedListener.onLocationChanged(aMapLocation);

                    aMap.addMarker(getMarkerOption(aMapLocation));

                    StringBuffer sb = new StringBuffer();
                    sb.append(aMapLocation.getCountry() + "" + aMapLocation.getProvince() + "" + aMapLocation.getCity() + "" + aMapLocation.getProvince() + "" + aMapLocation.getDistrict() + "" + aMapLocation.getStreet() + "" + aMapLocation.getStreetNum());
                    Toast.makeText(this.getActivity(), sb.toString(), Toast.LENGTH_LONG).show();

                    isFirstLoc = false;
                }
            } else {
                Log.e("AMapError", "Location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
                Toast.makeText(this.getActivity(), "定位失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private MarkerOptions getMarkerOption(AMapLocation aMapLocation) {
        MarkerOptions mo = new MarkerOptions();
        mo.position(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
        StringBuffer sb = new StringBuffer();
        sb.append(aMapLocation.getCountry() + "" + aMapLocation.getProvince() + "" + aMapLocation.getCity() +  "" + aMapLocation.getDistrict() + "" + aMapLocation.getStreet() + "" + aMapLocation.getStreetNum());
        mo.title(sb.toString());
        return  mo;
    }
}