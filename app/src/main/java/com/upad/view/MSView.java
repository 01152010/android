package com.upad.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.upad.R;
import com.upad.utils.LogUtil;

public class MSView extends View {
    private int mPercent;
    private Paint mBgArcPaint;
    private Paint mArcPaint;
    private final Context mContext;
    private final int mProgressWidth;
    private float mSweepAngle;
    private RectF mRectF;
    private ValueAnimator mAnimator;
    private OnOpenMSListener mOnOpenMSListener;


    public MSView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mProgressWidth = (int)mContext.getResources().getDimension(R.dimen.progress_width);
        initPaint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF = new RectF(mProgressWidth / 2, mProgressWidth / 2, w - mProgressWidth / 2, h - mProgressWidth / 2);
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        LogUtil.Log("MSView-onTouchEvent-action:"+action);
        if(MotionEvent.ACTION_DOWN == action){
            startAnimator(0,100,800);
        }else if(MotionEvent.ACTION_UP == action){
            LogUtil.Log("MSView-onTouchEvent-ACTION_UP");
            mSweepAngle = 0;
            invalidate();
            mAnimator.cancel();
        }
        return true;
    }

    private void initPaint(){
        mBgArcPaint = new Paint();
        mBgArcPaint.setAntiAlias(true);
        mBgArcPaint.setStyle(Paint.Style.STROKE);
        mBgArcPaint.setStrokeWidth(mProgressWidth);
        mBgArcPaint.setStrokeCap(Paint.Cap.ROUND);
        mBgArcPaint.setColor(ContextCompat.getColor(mContext,R.color.white_25));

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mProgressWidth);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        mArcPaint.setColor(mContext.getResources().getColor(R.color.textColorSel));
    }

    public void setOnOpenMSListener(OnOpenMSListener onOpenMSListener){
        mOnOpenMSListener = onOpenMSListener;
    }


    private void drawArc(Canvas canvas) {
        // 绘制背景圆弧
        // 从进度圆弧结束的地方开始重新绘制，优化性能
        canvas.save();
        canvas.drawArc(mRectF, 0, 360, false, mBgArcPaint);
        // 第一个参数 oval 为 RectF 类型，即圆弧显示区域
        // startAngle 和 sweepAngle  均为 float 类型，分别表示圆弧起始角度和圆弧度数
        // 3点钟方向为0度，顺时针递增
        // 如果 startAngle < 0 或者 > 360,则相当于 startAngle % 360
        // useCenter:如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形

        canvas.drawArc(mRectF, -90, mSweepAngle, false, mArcPaint);
        canvas.restore();
    }

    private void startAnimator(int start, int end, long animTime) {
        mAnimator = ValueAnimator.ofInt(start, end);
        mAnimator.setDuration(animTime);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPercent = (int)animation.getAnimatedValue();
                mSweepAngle = (mPercent/100f) * 360;
                if(100 == mPercent && null != mOnOpenMSListener){
                    LogUtil.Log("MSView-startAnimator-finish");
                    mOnOpenMSListener.open();
                }
                invalidate();
            }
        });
        mAnimator.start();
    }

    public interface OnOpenMSListener{
        void open();
    }
}
