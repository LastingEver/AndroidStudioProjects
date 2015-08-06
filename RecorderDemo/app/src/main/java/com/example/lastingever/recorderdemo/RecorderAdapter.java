package com.example.lastingever.recorderdemo;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.lastingever.recorderdemo.MainActivity.Recorder;

import java.util.List;

public class RecorderAdapter extends ArrayAdapter<Recorder>{

    private List<Recorder>list;
    private Context context;

    private int minItemWidth;
    private int maxItemWidth;

    private LayoutInflater layoutInflater;

    public RecorderAdapter(Context context, List<Recorder> list) {
        super(context,-1,list);

        layoutInflater=LayoutInflater.from(context);

        WindowManager windowManager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics=new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        maxItemWidth= (int) (displayMetrics.widthPixels*0.7f);
        minItemWidth= (int) (displayMetrics.widthPixels*0.15f);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView==null){
            convertView=layoutInflater.inflate(R.layout.item_recorder,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.textView2=(TextView)convertView.findViewById(R.id.textView2);
            viewHolder.frameLayout=convertView.findViewById(R.id.frameLayout);

            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }

        viewHolder.textView2.setText(Math.round(getItemId(position))+"\"");
        ViewGroup.LayoutParams layoutParams=viewHolder.frameLayout.getLayoutParams();
        layoutParams.width= (int) (minItemWidth+(maxItemWidth/60f*getItem(position).time));
        return convertView;
    }

    private class ViewHolder{
        TextView textView2;
        View frameLayout;
    }
}
