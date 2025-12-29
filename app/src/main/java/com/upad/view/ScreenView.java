package com.upad.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.upad.R;
import com.upad.utils.Constants;


public class ScreenView extends RelativeLayout {
    private RelativeLayout mRlOpenMS;
    private LinearLayout rlyContext;
    private ImageView mIvScreen;
    private OpenMSListener mOpenMSListener;
    private Context mContext;
    private Handler mHandler = new Handler();

    public ScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_screen, this);
        initView(view);
    }

    private void initView(View view){
        rlyContext = view.findViewById(R.id.rlyContext);
        mRlOpenMS = view.findViewById(R.id.rl_open_ms);
        mIvScreen = view.findViewById(R.id.iv_screen);
        mRlOpenMS.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mOpenMSListener){
                    mOpenMSListener.openMS();
                }
            }
        });
    }

    public void setOpenMSListener(OpenMSListener openMSListener){
        mOpenMSListener = openMSListener;
    }

    public void showScreen(String url){
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, Constants.ScreenTimeout);
        setVisibility(VISIBLE);
        Glide.with(mContext).load(url).into(mIvScreen);
    }

    public void dismissScreen(){
        mHandler.removeCallbacks(mRunnable);
        setVisibility(GONE);
    }

    public interface OpenMSListener{
        public void openMS();
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            setVisibility(GONE);
        }
    };

}
