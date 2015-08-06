package com.example.lasting.test;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class marquee extends TextView{
    public marquee(Context context) {
        super(context);
    }

    public marquee(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public marquee(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public marquee(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
