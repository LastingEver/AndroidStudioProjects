package lasting.travelassistant;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.WalkPath;

public class WalkRouteDetailActivity extends Activity {
    private WalkPath walkPath = null;
    private TextView detailTitle = null;
    private TextView walkRouteTitle = null;
    private ListView walkSegmentList = null;
    private WalkSegmentListAdapter wsla = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_detail);

        init();
    }

    private void init() {
        walkPath = getIntent().getParcelableExtra("walkPath");

        detailTitle = (TextView) findViewById(R.id.title_center);
        detailTitle.setText("步行路线详情");
        walkRouteTitle = (TextView) findViewById(R.id.firstline);
        String dur = AMapUtil.getFriendlyTime((int) walkPath.getDuration());
        String dis = AMapUtil.getFriendlyLength((int) walkPath.getDistance());
        walkRouteTitle.setText(dur + "(" + dis + ")");
        walkSegmentList = (ListView) findViewById(R.id.bus_segment_list);
        wsla = new WalkSegmentListAdapter(getApplicationContext(), walkPath.getSteps());
        walkSegmentList.setAdapter(wsla);
    }

    public void onBackClick(View view) {
        this.finish();
    }
}
