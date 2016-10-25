package lasting.travelassistant;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.route.DriveStep;

import java.util.ArrayList;
import java.util.List;

public class DriveSegmentListAdapter extends BaseAdapter {
    private Context context = null;
    private List<DriveStep> driveStepList = new ArrayList<DriveStep>();

    public DriveSegmentListAdapter(Context c, List<DriveStep> l) {
        context = c;
        driveStepList.add(new DriveStep());
        for (DriveStep driveStep : l) {
            driveStepList.add(driveStep);
        }
        driveStepList.add(new DriveStep());
    }

    @Override
    public int getCount() {
        return driveStepList.size();
    }

    @Override
    public Object getItem(int i) {
        return driveStepList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh = new ViewHolder();
        if (view == null) {
            view = View.inflate(context, R.layout.bus_segment, null);
            vh.driveDirIcon = (ImageView) view.findViewById(R.id.bus_dir_icon);
            vh.driveLineName = (TextView) view.findViewById(R.id.bus_line_name);
            vh.driveDirUp = (ImageView) view.findViewById(R.id.bus_dir_icon_up);
            vh.driveDirDown = (ImageView) view.findViewById(R.id.bus_dir_icon_down);
            vh.splitLine = (ImageView) view.findViewById(R.id.bus_seg_split_line);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }

        DriveStep item = driveStepList.get(i);

        if (i == 0) {
            vh.driveDirIcon.setImageResource(R.drawable.dir_start);
            vh.driveLineName.setText("出发");
            vh.driveDirUp.setVisibility(View.GONE);
            vh.driveDirDown.setVisibility(View.VISIBLE);
            vh.splitLine.setVisibility(View.GONE);
            return view;
        } else if (i == driveStepList.size() - 1) {
            vh.driveDirIcon.setImageResource(R.drawable.dir_end);
            vh.driveLineName.setText("到达终点");
            vh.driveDirUp.setVisibility(View.VISIBLE);
            vh.driveDirDown.setVisibility(View.GONE);
            vh.splitLine.setVisibility(View.VISIBLE);
            return view;
        } else {
            String actionName = item.getAction();
            int resID = AMapUtil.getDriveActionID(actionName);
            vh.driveDirIcon.setImageResource(resID);
            vh.driveLineName.setText(item.getInstruction());
            vh.driveDirUp.setVisibility(View.VISIBLE);
            vh.driveDirDown.setVisibility(View.VISIBLE);
            vh.splitLine.setVisibility(View.VISIBLE);
            return view;
        }
    }

    private class ViewHolder {
        TextView driveLineName;
        ImageView driveDirIcon;
        ImageView driveDirUp;
        ImageView driveDirDown;
        ImageView splitLine;
    }
}
