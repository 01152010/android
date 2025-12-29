package com.upad.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class WHLayout extends RelativeLayout {
    public WHLayout(Context context) {
        super(context);
    }

    public WHLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WHLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(MeasureSpec.makeMeasureSpec((int) (height * 1.3f), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }
}
