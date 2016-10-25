package lasting.travelassistant;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.RouteBusLineItem;
import com.amap.api.services.route.RouteRailwayItem;

import java.util.List;

public class BusResultListAdapter extends BaseAdapter{
    private Context context;
    private BusRouteResult busRouteResult;
    private List<BusPath> busPathList;

    public BusResultListAdapter(Context c, BusRouteResult brr) {
        context = c;
        busRouteResult = brr;
        busPathList = brr.getPaths();
    }

    @Override
    public int getCount() {
        return busPathList.size();
    }

    @Override
    public Object getItem(int position) {
        return busPathList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null){
            vh = new ViewHolder();
            convertView = View.inflate(context, R.layout.bus_result, null);
            vh.title = (TextView) convertView.findViewById(R.id.bus_path_title);
            vh.des = (TextView) convertView.findViewById(R.id.bus_path_des);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final BusPath item = busPathList.get(position);
        vh.title.setText(getBusPathTitle(item));
        vh.des.setText(getBusPathDes(item));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), BusRouteDetailActivity.class);
                intent.putExtra("busPath", item);
                intent.putExtra("busResult", busRouteResult);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView title;
        TextView des;
    }

    public static String getBusPathTitle(BusPath busPath) {
        if (busPath == null) {
            return String.valueOf("");
        }
        List<BusStep> busSetps = busPath.getSteps();
        if (busSetps == null) {
            return String.valueOf("");
        }
        StringBuffer sb = new StringBuffer();
        for (BusStep busStep : busSetps) {
            StringBuffer title = new StringBuffer();
            if (busStep.getBusLines().size() > 0) {
                for (RouteBusLineItem busline : busStep.getBusLines()) {
                    if (busline == null) {
                        continue;
                    }

                    String buslineName = AMapUtil.getSimpleBusLineName(busline.getBusLineName());
                    title.append(buslineName);
                    title.append(" / ");
                }

                sb.append(title.substring(0, title.length() - 3));
                sb.append(" > ");
            }
            if (busStep.getRailway() != null) {
                RouteRailwayItem railway = busStep.getRailway();
                sb.append(railway.getTrip()+"("+railway.getDeparturestop().getName()
                        +" - "+railway.getArrivalstop().getName()+")");
                sb.append(" > ");
            }
        }
        return sb.substring(0, sb.length() - 3);
    }

    public static String getBusPathDes(BusPath busPath) {
        if (busPath == null) {
            return String.valueOf("");
        }
        long second = busPath.getDuration();
        String time = AMapUtil.getFriendlyTime((int) second);
        float subDistance = busPath.getDistance();
        String subDis = AMapUtil.getFriendlyLength((int) subDistance);
        float walkDistance = busPath.getWalkDistance();
        String walkDis = AMapUtil.getFriendlyLength((int) walkDistance);
        return String.valueOf(time + " | " + subDis + " | 步行" + walkDis);
    }
}