package com.upad.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.View;

import com.upad.R;

public class CommonClick implements View.OnClickListener {
    private final Context mContext;
    private final SoundPool mSoundPool;

    private final float mVolume;
    private final OnItemClickListener mOnItemClickListener;


    public CommonClick(Context context,OnItemClickListener onItemClickListener){
        mContext = context;
        mOnItemClickListener = onItemClickListener;
        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        //获取当前音量
        float streamVolumeCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        //获取系统最大音量
        float streamVolumeMax = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //计算得到播放音量
        mVolume = streamVolumeCurrent / streamVolumeMax;
    }

    @Override
    public void onClick(View view) {
        play();
        if(null != mOnItemClickListener){
            mOnItemClickListener.OnitemClick(view);
        }
    }

    private void play(){
        //调用SoundPool的play方法来播放声音文件
        int musicId = mSoundPool.load(mContext, R.raw.click, 1);
        mSoundPool.play(musicId, mVolume, mVolume, 1, 0, 1.0f);
    }

    public interface OnItemClickListener{
        void OnitemClick(View view);
    }
}
