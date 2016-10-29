package lasting.travelassistant;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.RailwayStationItem;

import java.util.ArrayList;
import java.util.List;

public class BusSegmentListAdapter extends BaseAdapter {
    private Context context;
    private List<SchemeBusStep> busStepList = new ArrayList<SchemeBusStep>();

    public BusSegmentListAdapter(Context c, List<BusStep> l) {
        context = c;
        SchemeBusStep start = new SchemeBusStep(null);
        start.setStart(true);
        busStepList.add(start);
        for (BusStep busStep : l) {
            if (busStep.getWalk() != null && busStep.getWalk().getDistance() > 0) {
                SchemeBusStep walk = new SchemeBusStep(busStep);
                walk.setWalk(true);
                busStepList.add(walk);
            }

            if (busStep.getBusLine() != null) {
                SchemeBusStep bus = new SchemeBusStep(busStep);
                bus.setBus(true);
                busStepList.add(bus);
            }

            if (busStep.getRailway() != null) {
                SchemeBusStep rail = new SchemeBusStep(busStep);
                rail.setRailway(true);
                busStepList.add(rail);
            }

            if (busStep.getTaxi() != null) {
                SchemeBusStep taxi = new SchemeBusStep(busStep);
                taxi.setTaxi(true);
                busStepList.add(taxi);
            }
        }

        SchemeBusStep end = new SchemeBusStep(null);
        end.setEnd(true);
        busStepList.add(end);
    }

    @Override
    public int getCount() {
        return busStepList.size();
    }

    @Override
    public Object getItem(int position) {
        return busStepList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = View.inflate(context, R.layout.route_segment, null);
            vh.parent = (RelativeLayout) convertView.findViewById(R.id.bus_item);
            vh.busLineName = (TextView) convertView.findViewById(R.id.bus_line_name);
            vh.busDirIcon = (ImageView) convertView.findViewById(R.id.bus_dir_icon);
            vh.busStationNum = (TextView) convertView.findViewById(R.id.bus_station_num);
            vh.busExpandImage = (ImageView) convertView.findViewById(R.id.bus_expand_image);
            vh.busDirUp = (ImageView) convertView.findViewById(R.id.bus_dir_icon_up);
            vh.busDirDown = (ImageView) convertView.findViewById(R.id.bus_dir_icon_down);
            vh.splitLine = (ImageView) convertView.findViewById(R.id.bus_seg_split_line);
            vh.expandContent = (LinearLayout) convertView.findViewById(R.id.expand_content);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        SchemeBusStep item = busStepList.get(position);
        if (position == 0) {
            vh.busDirIcon.setImageResource(R.drawable.dir_start);
            vh.busLineName.setText("出发");
            vh.busDirUp.setVisibility(View.INVISIBLE);
            vh.busDirDown.setVisibility(View.VISIBLE);
            vh.splitLine.setVisibility(View.GONE);
            vh.busStationNum.setVisibility(View.GONE);
            vh.busExpandImage.setVisibility(View.GONE);
            return convertView;
        } else if (position == busStepList.size() - 1) {
            vh.busDirIcon.setImageResource(R.drawable.dir_end);
            vh.busLineName.setText("到达终点");
            vh.busDirUp.setVisibility(View.VISIBLE);
            vh.busDirDown.setVisibility(View.INVISIBLE);
            vh.busStationNum.setVisibility(View.INVISIBLE);
            vh.busExpandImage.setVisibility(View.INVISIBLE);
            return convertView;
        } else {
            if (item.isWalk() && item.getWalk() != null && item.getWalk().getDistance() > 0) {
                vh.busDirIcon.setImageResource(R.drawable.dir13);
                vh.busDirUp.setVisibility(View.VISIBLE);
                vh.busDirDown.setVisibility(View.VISIBLE);
                vh.busLineName.setText("步行" + (int) item.getWalk().getDistance() + "米");
                vh.busStationNum.setVisibility(View.GONE);
                vh.busExpandImage.setVisibility(View.GONE);
                return convertView;
            } else if (item.isBus() && item.getBusLines().size() > 0) {
                vh.busDirIcon.setImageResource(R.drawable.dir14);
                vh.busDirUp.setVisibility(View.VISIBLE);
                vh.busDirDown.setVisibility(View.VISIBLE);
                vh.busLineName.setText(item.getBusLines().get(0).getBusLineName());
                vh.busStationNum.setVisibility(View.VISIBLE);
                vh.busStationNum.setText((item.getBusLines().get(0).getPassStationNum() + 1) + "站");
                vh.busExpandImage.setVisibility(View.VISIBLE);
                ArrowClick arrowClick = new ArrowClick(vh, item);
                vh.parent.setTag(position);
                vh.parent.setOnClickListener(arrowClick);
                return convertView;
            } else if (item.isRailway() && item.getRailway() != null) {
                vh.busDirIcon.setImageResource(R.drawable.dir16);
                vh.busDirUp.setVisibility(View.VISIBLE);
                vh.busDirDown.setVisibility(View.VISIBLE);
                vh.busLineName.setText(item.getRailway().getName());
                vh.busStationNum.setVisibility(View.VISIBLE);
                vh.busStationNum.setText((item.getRailway().getViastops().size() + 1) + "站");
                vh.busExpandImage.setVisibility(View.VISIBLE);
                ArrowClick arrowClick = new ArrowClick(vh, item);
                vh.parent.setTag(position);
                vh.parent.setOnClickListener(arrowClick);
                return convertView;
            } else if (item.isTaxi() && item.getTaxi() != null) {
                vh.busDirIcon.setImageResource(R.drawable.dir14);
                vh.busDirUp.setVisibility(View.VISIBLE);
                vh.busDirDown.setVisibility(View.VISIBLE);
                vh.busLineName.setText("打车到终点");
                vh.busStationNum.setVisibility(View.GONE);
                vh.busExpandImage.setVisibility(View.GONE);
                return convertView;
            }
        }

        return convertView;
    }

    private class ViewHolder {
        public RelativeLayout parent;
        TextView busLineName;
        ImageView busDirIcon;
        TextView busStationNum;
        ImageView busExpandImage;
        ImageView busDirUp;
        ImageView busDirDown;
        ImageView splitLine;
        LinearLayout expandContent;
        boolean arrowExpand = false;
    }

    private class ArrowClick implements View.OnClickListener {
        private ViewHolder mHolder;
        private SchemeBusStep mItem;

        public ArrowClick(final ViewHolder holder, final SchemeBusStep item) {
            mHolder = holder;
            mItem = item;
        }

        @Override
        public void onClick(View v) {
            int position = Integer.parseInt(String.valueOf(v.getTag()));
            mItem = busStepList.get(position);
            if (mItem.isBus()) {
                if (mHolder.arrowExpand == false) {
                    mHolder.arrowExpand = true;
                    mHolder.busExpandImage.setImageResource(R.drawable.up);
                    addBusStation(mItem.getBusLine().getDepartureBusStation());
                    for (BusStationItem station : mItem.getBusLine().getPassStations()) {
                        addBusStation(station);
                    }
                    addBusStation(mItem.getBusLine().getArrivalBusStation());

                } else {
                    mHolder.arrowExpand = false;
                    mHolder.busExpandImage
                            .setImageResource(R.drawable.down);
                    mHolder.expandContent.removeAllViews();
                }
            } else if (mItem.isRailway()) {
                if (mHolder.arrowExpand == false) {
                    mHolder.arrowExpand = true;
                    mHolder.busExpandImage
                            .setImageResource(R.drawable.up);
                    addRailwayStation(mItem.getRailway().getDeparturestop());
                    for (RailwayStationItem station : mItem.getRailway().getViastops()) {
                        addRailwayStation(station);
                    }
                    addRailwayStation(mItem.getRailway().getArrivalstop());

                } else {
                    mHolder.arrowExpand = false;
                    mHolder.busExpandImage
                            .setImageResource(R.drawable.down);
                    mHolder.expandContent.removeAllViews();
                }
            }
        }

        private void addBusStation(BusStationItem station) {
            LinearLayout ll = (LinearLayout) View.inflate(context,
                    R.layout.bus_extension, null);
            TextView tv = (TextView) ll
                    .findViewById(R.id.bus_line_station_name);
            tv.setText(station.getBusStationName());
            mHolder.expandContent.addView(ll);
        }

        private void addRailwayStation(RailwayStationItem station) {
            LinearLayout ll = (LinearLayout) View.inflate(context,
                    R.layout.bus_extension, null);
            TextView tv = (TextView) ll
                    .findViewById(R.id.bus_line_station_name);
            tv.setText(station.getName() + " " + getRailwayTime(station.getTime()));
            mHolder.expandContent.addView(ll);
        }
    }

    public static String getRailwayTime(String time) {
        return time.substring(0, 2) + ":" + time.substring(2, time.length());
    }
}
