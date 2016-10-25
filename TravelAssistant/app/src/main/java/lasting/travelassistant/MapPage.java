package lasting.travelassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.melnykov.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapPage extends SupportMapFragment implements LocationSource, AMapLocationListener {
    private TextureMapView tmv = null;
    private AMap aMap = null;
    private UiSettings uiSettings = null;

    private AMapLocationClient aMapLocationClient = null;
    private AMapLocationClientOption aMapLocationClientOption = null;
    private LocationSource.OnLocationChangedListener onLocationChangedListener = null;

    private boolean isFirstLoc = true;

    private View view = null;

    private SearchView sv = null;

    private PoiSearch ps = null;

    private PoiSearch.Query query = null;

    private String currentCity = null;

    private int currentPage = 0;

    private PoiOverlay po = null;

    private SearchView.SearchAutoComplete sac = null;

    private List<String> tipList = null;

    private FloatingActionButton fab = null;

    private LatLng startPoint = null;
    private LatLng endPoint = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_map, container, false);
        tmv = (TextureMapView) view.findViewById(R.id.map);
        tmv.onCreate(savedInstanceState);

        initMap();

        initLoc();

        initSearch();

        submit();

        return view;
    }

    private void submit() {
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if (endPoint == null){
                    Toast.makeText(getActivity(), "请在地图上选择一个标识后重试", Toast.LENGTH_SHORT).show();
                } else {
                    intent.putExtra("startLat", startPoint.latitude);
                    intent.putExtra("startLng", startPoint.longitude);
                    intent.putExtra("endLat", endPoint.latitude);
                    intent.putExtra("endLng", endPoint.longitude);
                    intent.putExtra("currentCity", currentCity);
                    intent.setClass(getActivity(), RoutePage.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void initPoi() {
        query = new PoiSearch.Query(sv.getQuery().toString(), "", currentCity);
        query.setPageSize(10);
        query.setPageNum(currentPage);

        ps = new PoiSearch(this.getActivity(), query);
        ps.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int i) {
                if (i == 1000) {
                    if (poiResult != null && poiResult.getQuery() != null) {
                        if (poiResult.getQuery().equals(query)) {
                            List<PoiItem> poiItems = poiResult.getPois();
                            List<SuggestionCity> suggestionCitis = poiResult.getSearchSuggestionCitys();

                            if (poiItems != null && poiItems.size() > 0) {
                                aMap.clear();
                                po = new PoiOverlay(aMap, poiItems);
                                po.removeFromMap();
                                po.addToMap();
                                po.zoomToSpan();
                            } else if (suggestionCitis != null && suggestionCitis.size() > 0) {
                                Toast.makeText(getActivity(), "推荐城市：" + suggestionCitis.get(0).getCityName(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "没有搜索到数据", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        });
        ps.searchPOIAsyn();
    }

    private void initSearch() {
        sv = (SearchView) view.findViewById(R.id.searchView);
        sac = (SearchView.SearchAutoComplete) sv.findViewById(R.id.search_src_text);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                initPoi();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && !newText.isEmpty()) {
                    InputtipsQuery iq = new InputtipsQuery(newText, currentCity);
                    Inputtips inputtips = new Inputtips(getActivity(), iq);
                    inputtips.setInputtipsListener(new Inputtips.InputtipsListener() {
                        @Override
                        public void onGetInputtips(List<Tip> list, int i) {
                            if (i == 1000) {
                                tipList = new ArrayList<String>();
                                for (Tip tip : list) {
                                    tipList.add(tip.getName());
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, tipList);
                                sac.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            } else {

                            }
                        }
                    });
                    inputtips.requestInputtipsAsyn();
                }
                return false;
            }
        });
        sv.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                sac.setText(tipList.get(position));
                return true;
            }
        });
    }

    private void initMap() {
        if (aMap == null){
            aMap = tmv.getMap();
        }

        aMap.setTrafficEnabled(true);
        aMap.setLocationSource(this);

        uiSettings = aMap.getUiSettings();

        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setScaleControlsEnabled(true);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setAllGesturesEnabled(true);

        aMap.setMyLocationEnabled(true);

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                endPoint = marker.getPosition();
                return false;
            }
        });
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
        if (aMapLocationClient == null) {
            initLoc();
        }
    }

    @Override
    public void deactivate() {
        this.onLocationChangedListener = null;
        if (aMapLocationClient != null) {
            aMapLocationClient.stopLocation();
            aMapLocationClient.onDestroy();
            aMapLocationClient = null;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
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
                currentCity = aMapLocation.getCity();
                aMapLocation.getDistrict();
                aMapLocation.getStreet();
                aMapLocation.getStreetNum();
                aMapLocation.getCityCode();
                aMapLocation.getAdCode();

                startPoint = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

                if (isFirstLoc) {
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    onLocationChangedListener.onLocationChanged(aMapLocation);

                    aMap.addMarker(getMarkerOption(aMapLocation));

                    StringBuffer sb = new StringBuffer();
                    sb.append(aMapLocation.getCountry() + "" + aMapLocation.getProvince() + "" + aMapLocation.getCity() + "" + aMapLocation.getProvince() + "" + aMapLocation.getDistrict() + "" + aMapLocation.getStreet() + "" + aMapLocation.getStreetNum());
                    Toast.makeText(this.getActivity(), sb.toString(), Toast.LENGTH_LONG).show();

                    isFirstLoc = false;
                }
            } else {
                Toast.makeText(this.getActivity(), "定位失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private MarkerOptions getMarkerOption(AMapLocation aMapLocation) {
        MarkerOptions mo = new MarkerOptions();
        mo.position(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
        StringBuffer sb = new StringBuffer();
        sb.append(aMapLocation.getCountry() + "" + aMapLocation.getProvince() + "" + aMapLocation.getCity() + "" + aMapLocation.getDistrict() + "" + aMapLocation.getStreet() + "" + aMapLocation.getStreetNum());
        mo.title(sb.toString());
        return mo;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tmv.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        tmv.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        tmv.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        tmv.onSaveInstanceState(bundle);
    }
}