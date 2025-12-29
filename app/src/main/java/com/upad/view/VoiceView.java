package com.upad.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.upad.R;
import com.upad.model.WeatherBean;
import com.upad.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import androidx.cardview.widget.CardView;

public class VoiceView extends RelativeLayout {
    private TextView mTvCenter;
    private VolumeWaveView mVWV;

    public VoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_voice_wave, this);
        initView(view);
    }

    private void initView(View view){
        mTvCenter = view.findViewById(R.id.tv_voice_center);
        mVWV = view.findViewById(R.id.vwv);
    }

    public boolean isWakeUp(){
        return VISIBLE == getVisibility();
    }

    public void silence(){
        setVisibility(GONE);
        mTvCenter.setVisibility(INVISIBLE);
        mTvCenter.setText("我能帮您什么？");
    }



    public void wakeUp(){
        LogUtil.Log("VoiceView-wakeUp");
        setVisibility(VISIBLE);
        mVWV.setVisibility(VISIBLE);
        mTvCenter.setVisibility(VISIBLE);
        mTvCenter.setText("我能帮您什么？");
    }

    public void speaking(String msg){
        LogUtil.Log("VoiceView-speaking-msg:"+msg);
        mTvCenter.setVisibility(VISIBLE);
        mTvCenter.setText(msg);
    }

    public void showResult(boolean show,String msg){
        LogUtil.Log("VoiceView-showResult");
        if(!show){
            setVisibility(GONE);
            return;
        }
    }
}
