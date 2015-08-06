package com.example.lasting.gallerydemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

public class MainActivity extends Activity implements ViewSwitcher.ViewFactory, OnItemSelectedListener {
    private int[] resourceId = {R.drawable.pic1, R.drawable.pic2, R.drawable.pic3, R.drawable.pic4};
    private Gallery gallery1;
    private ImageSwitcher imageSwitcher1;
    private MyAdapter myAdapter;
    private ImageView imageView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gallery1 = (Gallery) findViewById(R.id.gallery1);
        imageSwitcher1 = (ImageSwitcher) findViewById(R.id.imageSwitcher1);
        myAdapter = new MyAdapter(resourceId, this);
        gallery1.setAdapter(myAdapter);
        gallery1.setOnItemSelectedListener(this);
        imageSwitcher1.setFactory(this);
        imageSwitcher1.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        imageSwitcher1.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        imageSwitcher1.setBackgroundResource(resourceId[i % resourceId.length]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public View makeView() {
        imageView1 = new ImageView(this);
        imageView1.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return imageView1;
    }
}
