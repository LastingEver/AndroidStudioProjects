package lasting.travelassistant;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.route.WalkStep;

import java.util.ArrayList;
import java.util.List;

public class WalkSegmentListAdapter extends BaseAdapter {
    private Context context = null;
    private List<WalkStep> walkStepList = new ArrayList<WalkStep>();

    public WalkSegmentListAdapter(Context c, List<WalkStep> l) {
        context = c;
        walkStepList.add(new WalkStep());
        for (WalkStep walkStep : l) {
            walkStepList.add(walkStep);
        }
        walkStepList.add(new WalkStep());
    }

    @Override
    public int getCount() {
        return walkStepList.size();
    }

    @Override
    public Object getItem(int i) {
        return walkStepList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh = null;
        if (view == null) {
            vh = new ViewHolder();
            view = View.inflate(context, R.layout.bus_segment, null);
            vh.lineName = (TextView) view.findViewById(R.id.bus_line_name);
            vh.dirIcon = (ImageView) view.findViewById(R.id.bus_dir_icon);
            vh.dirUp = (ImageView) view.findViewById(R.id.bus_dir_icon_up);
            vh.dirDown = (ImageView) view.findViewById(R.id.bus_dir_icon_down);
            vh.splitLine = (ImageView) view.findViewById(R.id.bus_seg_split_line);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }

        final WalkStep walkStep = walkStepList.get(i);

        if (i == 0) {
            vh.dirIcon.setImageResource(R.drawable.dir_start);
            vh.lineName.setText("出发");
            vh.dirUp.setVisibility(View.INVISIBLE);
            vh.dirDown.setVisibility(View.VISIBLE);
            vh.splitLine.setVisibility(View.INVISIBLE);
            return view;
        } else if (i == walkStepList.size() - 1) {
            vh.dirIcon.setImageResource(R.drawable.dir_end);
            vh.lineName.setText("到达终点");
            vh.dirUp.setVisibility(View.VISIBLE);
            vh.dirDown.setVisibility(View.INVISIBLE);
            return view;
        } else {
            vh.splitLine.setVisibility(View.VISIBLE);
            vh.dirUp.setVisibility(View.VISIBLE);
            vh.dirDown.setVisibility(View.VISIBLE);
            String actionName = walkStep.getAction();
            int resID = AMapUtil.getWalkActionID(actionName);
            vh.dirIcon.setImageResource(resID);
            vh.lineName.setText(walkStep.getInstruction());
            return view;
        }
    }

    private class ViewHolder {
        TextView lineName;
        ImageView dirIcon;
        ImageView dirUp;
        ImageView dirDown;
        ImageView splitLine;
    }
}
