package com.upad.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class WScaleHRelativeLayout extends RelativeLayout {

    public WScaleHRelativeLayout(Context context) {
        super(context);
    }

    public WScaleHRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WScaleHRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(2*heightMeasureSpec, heightMeasureSpec);
    }
}
