package com.example.lasting.gallerydemo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class MyAdapter extends BaseAdapter{
    private int[]resourceId;
    private ImageView imageView1;
    private Context context;
    public MyAdapter(int[]resourceId,Context context){
        this.resourceId=resourceId;
        this.context=context;
    }
    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object getItem(int i) {
        return resourceId[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        imageView1=new ImageView(context);
        imageView1.setBackgroundResource(resourceId[i%resourceId.length]);
        imageView1.setLayoutParams(new Gallery.LayoutParams(300, 300));
        imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView1;
    }
}
