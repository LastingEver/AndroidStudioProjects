package lasting.travelassistant;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nhaarman.listviewanimations.ArrayAdapter;

public class SceneryNoticeAdapter extends ArrayAdapter<String> {
    private Context context = null;

    public SceneryNoticeAdapter(Context c) {
        context = c;
        for (int i = 0; i < 100; i++) {
            add(context.getString(R.string.scenery_notice, i + 1));
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView tv = (TextView) view;
        if (tv == null) {
            tv = (TextView) View.inflate(context, R.layout.list_row, null);
        }
        tv.setText(getItem(i));
        return tv;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }
}
