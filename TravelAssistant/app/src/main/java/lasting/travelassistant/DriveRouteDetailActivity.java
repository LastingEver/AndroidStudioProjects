package lasting.travelassistant;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;

public class DriveRouteDetailActivity extends Activity {
    private DrivePath drivePath = null;
    private DriveRouteResult driveRouteResult = null;
    private TextView detailTitle = null;
    private TextView driveRouteTitle = null;
    private TextView driveRouteDes = null;
    private ListView driveSegmentList = null;
    private DriveSegmentListAdapter dsla = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_detail);
        ActivityManager.getInstance().addActivity(this);

        init();
    }

    private void init() {
        drivePath = getIntent().getParcelableExtra("drivePath");
        driveRouteResult = getIntent().getParcelableExtra("driveResult");

        detailTitle = (TextView) findViewById(R.id.title_center);
        driveRouteTitle = (TextView) findViewById(R.id.firstline);
        driveRouteDes = (TextView) findViewById(R.id.secondline);
        detailTitle.setText("驾车路线详情");
        String dur = AMapUtil.getFriendlyTime((int) drivePath.getDuration());
        String dis = AMapUtil.getFriendlyLength((int) drivePath.getDistance());
        driveRouteTitle.setText(dur + "(" + dis + ")");
        int taxiCost = (int) driveRouteResult.getTaxiCost();
        driveRouteDes.setText("打车约"+taxiCost+"元");
        driveRouteDes.setVisibility(View.VISIBLE);
        configureListView();
    }

    private void configureListView() {
        driveSegmentList = (ListView) findViewById(R.id.bus_segment_list);
        dsla = new DriveSegmentListAdapter(getApplicationContext(), drivePath.getSteps());
        driveSegmentList.setAdapter(dsla);
    }

    public void onBackClick(View view) {
        this.finish();
    }
}
