package com.upad.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.upad.R;
import com.upad.utils.LogUtil;


public class CLView extends View {
    private static final int ALLOFFNUM = 9;
    private static final long SPEED = 180;//毫秒
    private static final long ACCELERATION = 20;//ms
    private int mRectWidth = 15;
    private int mWidth;
    private int mHeight;
    private Paint mPaint;
    private Context mContext;
    private Canvas mCanvas;
    private int mNum = 20;
    private int mCurrentIndex = 9;
    private Handler mHandler;
    private long mCurrentSpeed = SPEED;



    public CLView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mHandler = new Handler();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(ContextCompat.getColor(context,R.color.textColorSel));
        mRectWidth = context.getResources().getDimensionPixelSize(R.dimen.cl_rect_width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;
        RectF rectF = new RectF(0,0,(mWidth/mNum)*(mNum-1)+mRectWidth,mRectWidth);
        mPaint.setColor(ContextCompat.getColor(mContext,R.color.white_25));
        mCanvas.drawRoundRect(rectF,mRectWidth,mRectWidth,mPaint);
        refresh();
        mPaint.setColor(ContextCompat.getColor(mContext,R.color.white_3));
        //mCanvas.drawRect((mWidth/mNum)*(mCurrentIndex-1)+10, 40, (mWidth/mNum)*(mNum - mCurrentIndex)+10, mHeight, mPaint);
        rectF = new RectF(0,30,(mWidth/mNum)*(mNum-1)+mRectWidth,mHeight);
        mCanvas.drawRoundRect(rectF,mRectWidth,mRectWidth,mPaint);
    }

    public void setOn(){
        mCurrentSpeed = SPEED;
        mCurrentIndex = 2;
        mHandler.removeCallbacks(mONRunnable);
        mHandler.removeCallbacks(mOFFRunnable);
        invalidate();
    }

    public void setOff(){
        mCurrentSpeed = SPEED;
        mCurrentIndex = ALLOFFNUM;
        mHandler.removeCallbacks(mONRunnable);
        mHandler.removeCallbacks(mOFFRunnable);
        invalidate();
    }

    public void on(){
        if(2 >= mCurrentIndex){
            return;
        }
        mCurrentSpeed = SPEED;
        mHandler.removeCallbacks(mONRunnable);
        mHandler.removeCallbacks(mOFFRunnable);
        mHandler.postDelayed(mONRunnable,SPEED);
    }

    public void off(){
        if(mCurrentIndex >= ALLOFFNUM){
            return;
        }
        mCurrentSpeed = SPEED;
        mHandler.removeCallbacks(mONRunnable);
        mHandler.removeCallbacks(mOFFRunnable);
        mHandler.postDelayed(mOFFRunnable,SPEED);
    }

    public void pause(){
        mHandler.removeCallbacks(mONRunnable);
        mHandler.removeCallbacks(mOFFRunnable);
    }

    private void refresh(){
        for(int i = 0; i < mNum; i++){
            RectF rectF = new RectF((mWidth/mNum)*i,30,(mWidth/mNum)*i+mRectWidth,mHeight);
            if(i > (mCurrentIndex-1) && i < (mNum - mCurrentIndex)){
                mPaint.setColor(mContext.getColor(R.color.cardColor));
            }else{
                mPaint.setColor(mContext.getColor(R.color.textColorSel));
            }
            mCanvas.drawRoundRect(rectF,mRectWidth,mRectWidth,mPaint);
        }
    }

    private Runnable mONRunnable = new Runnable() {
        @Override
        public void run() {
            if(2 < mCurrentIndex){
                mCurrentIndex--;
                mCurrentSpeed -= ACCELERATION;
                invalidate();
                mHandler.postDelayed(mONRunnable,mCurrentSpeed);
            }/*else if(2 == mCurrentIndex){
                mCurrentSpeed = SPEED;
                mCurrentIndex--;
                invalidate();
                mHandler.postDelayed(mONRunnable,30);
            }else{
                mCurrentSpeed = SPEED;
                mCurrentIndex++;
                invalidate();
            }*/
        }
    };

    private Runnable mOFFRunnable = new Runnable() {
        @Override
        public void run() {
            if(mCurrentIndex < ALLOFFNUM){
                mCurrentIndex++;
                mCurrentSpeed -= ACCELERATION;
                invalidate();
                mHandler.postDelayed(mOFFRunnable,mCurrentSpeed);
            }/*else if(mCurrentIndex == ALLOFFNUM){
                mCurrentSpeed = SPEED;
                mCurrentIndex++;
                invalidate();
                mHandler.postDelayed(mOFFRunnable,30);
            }else{
                mCurrentSpeed = SPEED;
                mCurrentIndex--;
                invalidate();
            }*/
        }
    };
}
