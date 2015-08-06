package com.example.lasting.viewflipperdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class MainActivity extends Activity {
    private ViewFlipper viewFlipper1;
    private float startX;
    private int[]resourceId={R.drawable.pic1,R.drawable.pic2,R.drawable.pic3,R.drawable.pic4};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewFlipper1=(ViewFlipper)findViewById(R.id.viewFlipper1);
        for (int i=0;i<resourceId.length;i++){
            viewFlipper1.addView(getImageView(resourceId[i]));
        }
//        viewFlipper1.setInAnimation(this,R.anim.left_in);
//        viewFlipper1.setOutAnimation(this,R.anim.left_out);
//        viewFlipper1.setFlipInterval(3000);
//        viewFlipper1.startFlipping();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                startX=event.getX();
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                if (event.getX()-startX>100){
                    viewFlipper1.setInAnimation(this,R.anim.left_in);
                    viewFlipper1.setOutAnimation(this,R.anim.left_out);
                    viewFlipper1.showPrevious();
                }
                if(startX-event.getX()>100){
                    viewFlipper1.setInAnimation(this,R.anim.right_in);
                    viewFlipper1.setOutAnimation(this,R.anim.right_out);
                    viewFlipper1.showNext();
                }
                break;
            }
            case MotionEvent.ACTION_UP:{
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    private ImageView getImageView(int resourceId){
        ImageView imageView=new ImageView(this);
        imageView.setBackgroundResource(resourceId);
        return imageView;
    }
}
