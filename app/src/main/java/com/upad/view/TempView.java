package com.upad.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.upad.R;
import com.upad.utils.LogUtil;

public class TempView extends View {
    private int mRectWidth = 10;
    private int mStartTemp = 19;
    private int mEndTemp = 30;
    private int mCurrentIndex = 14;
    private int mNum = 23;
    private int mWidth;
    private int mHeight;
    private Paint mPaint;
    private Context mContext;
    private Canvas mCanvas;
    private OnTempChangeListener mOnTempChangeListener;

    public TempView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(ContextCompat.getColor(context,R.color.textColorSel));
        mRectWidth = context.getResources().getDimensionPixelSize(R.dimen.kt_rect_width);
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
        refresh();
    }

    private void refresh(){
        mPaint.setColor(mContext.getColor(R.color.textColorSel));
        int temp = mWidth - 8;
        for(int i = 0; i < mNum; i++){
            float left = (temp/mNum)*i+4;
            float top = 12;
            float right = (temp/mNum)*i+4+mRectWidth;
            float bottom = mHeight;
            if(i == mCurrentIndex){
                top += 15;
                mCanvas.drawCircle(left+5,top-15,8,mPaint);
            }else if(i == mCurrentIndex+1 || i == mCurrentIndex-1){
                top += 10;
            }else if(i == mCurrentIndex+2 || i == mCurrentIndex-2){
                top += 5;
            }
            RectF rectF = new RectF(left,top,right,bottom);
            if(i > mCurrentIndex){
                mPaint.setColor(mContext.getColor(R.color.white_3));
            }
            mCanvas.drawRoundRect(rectF,mRectWidth,mRectWidth,mPaint);
        }
    }

    private float mDownX;
    private int mIndex;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        LogUtil.Log("TempView-onTouchEvent-action:"+action);
        if(MotionEvent.ACTION_DOWN == action){
            mDownX = event.getX();
            mIndex = mCurrentIndex;
            LogUtil.Log("TempView-onTouchEvent-mDownX:"+mDownX+"; index:"+mIndex);
        }else if(MotionEvent.ACTION_MOVE == action){
            float move = event.getX();
            float temp = move-mDownX;
            int stepIndex = (int)Math.floor(Math.abs(temp)/(mWidth/mNum));
            LogUtil.Log("TempView-ACTION_MOVE-stepIndex:"+stepIndex+"; :"+Math.abs(temp)/(mWidth/mNum)+"; temp:"+temp+"; mIndex:"+mIndex);
            if(0 < stepIndex){
                if(0 > temp){
                    mCurrentIndex = mIndex - stepIndex;
                    LogUtil.Log("TempView-ACTION_MOVE-mCurrentIndex1:"+mCurrentIndex);
                    if(0 > mCurrentIndex){
                        mCurrentIndex = 0;
                    }
                }
                if(0 < temp){
                    mCurrentIndex = mIndex + stepIndex;
                    LogUtil.Log("TempView-ACTION_MOVE-mCurrentIndex2:"+mCurrentIndex+"; :"+Math.abs(temp)/(mWidth/mNum)+"; temp:"+temp);
                    if(mNum <= mCurrentIndex){
                        mCurrentIndex = mNum-1;
                    }
                }
                if(null != mOnTempChangeListener){
                    mOnTempChangeListener.onTempChange(true,mStartTemp+(mCurrentIndex/2));
                }
                invalidate();
            }

        }else if(MotionEvent.ACTION_UP == action){
            float move = event.getX();
            float temp = move-mDownX;
            int stepIndex = (int)Math.floor(Math.abs(temp)/(mWidth/mNum));
            if(0 < stepIndex){
                if(0 > temp){
                    mCurrentIndex = mIndex - stepIndex;
                    LogUtil.Log("TempView-ACTION_UP-mCurrentIndex1:"+mCurrentIndex+"; :"+Math.abs(temp)/(mWidth/mNum)+"; temp:"+temp);
                    if(0 > mCurrentIndex){
                        mCurrentIndex = 0;
                    }
                }
                if(0 < temp){
                    mCurrentIndex = mIndex + stepIndex;
                    LogUtil.Log("TempView-ACTION_UP-mCurrentIndex2:"+mCurrentIndex+"; :"+Math.abs(temp)/(mWidth/mNum)+"; temp:"+temp);
                    if(mNum <= mCurrentIndex){
                        mCurrentIndex = mNum-1;
                    }
                }
                if(null != mOnTempChangeListener){
                    mOnTempChangeListener.onTempChange(false,mStartTemp+(mCurrentIndex/2));
                }
                invalidate();
            }


        }
        return true;
    }

    public void setCurrentTemp(int currentTemp){
        mCurrentIndex = 2 * (currentTemp-mStartTemp);
        invalidate();
    }

    public int getCurrentTemp(){
        return mStartTemp+(mCurrentIndex/2);
    }

    public void setOnTempChangeListener(OnTempChangeListener onTempChangeListener){
        mOnTempChangeListener = onTempChangeListener;
    }

    public interface OnTempChangeListener{
        void onTempChange(boolean isMoving,int temp);
    }
}
