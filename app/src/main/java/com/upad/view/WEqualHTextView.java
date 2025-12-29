package com.upad.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class WEqualHTextView extends TextView {
    public WEqualHTextView(Context context) {
        super(context);
    }

    public WEqualHTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WEqualHTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        if(width > height){
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        }else{
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        }

    }
}
