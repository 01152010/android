package com.upad;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.k2fsa.sherpa.ncnn.SherpaNcnn;
import com.upad.voice.VoicePlayer;
import com.upad.voice.VoiceRecorder;
import com.webrtc.AEC;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;


public class IndexActivity extends AppCompatActivity {
    private int SAMPLE_RATE = 8000;
    private int FRAME_SIZE = 160;

    private Button playBtn;
    private Button stopBtn;
    private SeekBar seekBarSampleRate;
    private TextView textViewSeekBarSampleRate;
    private TextView textViewSeekBarFrameSizeLabel;
    private SeekBar seekBarAggressiveMode;
    private TextView textViewSeekBarAggressiveMode;
    private SeekBar seekBarEchoLength;
    private TextView textViewSeekBarEchoLength;
    private AEC aec = new AEC();
    private VoiceRecorder voiceRecorder;
    private VoicePlayer voicePlayer;
    private boolean stop;
    private boolean enableAecm;

    private TextView textViewIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        voiceRecorder = new VoiceRecorder();
        voicePlayer = new VoicePlayer();

        textViewIP = findViewById(R.id.ip);
        textViewIP.setText(getEthernetIpAddress());

        playBtn = findViewById(R.id.playBtn);
        playBtn.setOnClickListener(v -> { if (hasRecAudioPermission()) startPlay(); });

        stopBtn = findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(v -> {
            stopBtn.setVisibility(View.GONE);
            playBtn.setVisibility(View.VISIBLE);
            stop();
        });

        Switch switchAecm = findViewById(R.id.switch_aecm);
        switchAecm.setOnCheckedChangeListener((buttonView, isChecked) -> enableAecm = isChecked);

        textViewSeekBarSampleRate = findViewById(R.id.text_view_seek_bar_sample_rate_label);
        seekBarSampleRate = findViewById(R.id.seek_bar_sample_rate);
        seekBarSampleRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= 8000) {
                    progress = 0;
                    seekBar.setProgress(progress);
                }
                if (progress > 8000) {
                    progress = 16000;
                    seekBar.setProgress(progress);
                }
                String s = progress == 0 ? "8000hz" : progress + "hz";
                textViewSeekBarSampleRate.setText(s);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                SAMPLE_RATE = progress == 0 ? 8000 : progress;
            }
        });
        seekBarSampleRate.setProgress(SAMPLE_RATE);

        textViewSeekBarFrameSizeLabel = findViewById(R.id.text_view_seek_bar_frame_size_label);
        SeekBar seekBarFrameSize = findViewById(R.id.seek_bar_frame_size);
        seekBarFrameSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= 80) {
                    progress = 0;
                    seekBar.setProgress(progress);
                }
                if (progress > 80) {
                    progress = 160;
                    seekBar.setProgress(160);
                }
                String s = progress == 0 ? "80" : progress + "";
                textViewSeekBarFrameSizeLabel.setText(s);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                FRAME_SIZE = progress == 0 ? 80 : progress;
            }
        });
        seekBarFrameSize.setProgress(FRAME_SIZE);

        textViewSeekBarAggressiveMode = findViewById(R.id.text_view_seek_bar_aggressive_mode_label);
        seekBarAggressiveMode = findViewById(R.id.seek_bar_aggressive_mode);
        seekBarAggressiveMode.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewSeekBarAggressiveMode.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        seekBarAggressiveMode.setProgress(4);

        textViewSeekBarEchoLength = findViewById(R.id.text_view_seek_bar_echo_length_label);
        seekBarEchoLength = findViewById(R.id.seek_bar_echo_length);
        seekBarEchoLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 1) {
                    seekBarEchoLength.setProgress(1);
                    return;
                }
                textViewSeekBarEchoLength.setText(progress + "ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        seekBarEchoLength.setProgress(20);
    }

    private void startPlay() {
        playBtn.setVisibility(View.GONE);
        stopBtn.setVisibility(View.VISIBLE);
        play();
    }

    @RequiresApi(23)
    private boolean hasRecAudioPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        else if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[] {Manifest.permission.RECORD_AUDIO}, 1);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && permissions[0].equals(Manifest.permission.RECORD_AUDIO)) startPlay();
    }

    private void play() {
        if (enableAecm) aec.setSampFreq(seekBarSampleRate.getProgress() == 0 ? AEC.SamplingFrequency.FS_8000Hz : AEC.SamplingFrequency.FS_16000Hz);
        if (enableAecm) aec.setAecmMode(getAggressiveMode());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        voiceRecorder.start(SAMPLE_RATE, FRAME_SIZE);
        voicePlayer.start(SAMPLE_RATE);
        stop = false;
        new Thread(() -> {
            while (!stop) {
                short[] frame = voiceRecorder.frame();
                if (enableAecm) aec.farendBuffer(frame, FRAME_SIZE);
                short[] resultFrame = new short[FRAME_SIZE];
                if (enableAecm) resultFrame = aec.echoCancellation(frame, null, FRAME_SIZE, seekBarEchoLength.getProgress());
                voicePlayer.write(enableAecm ? resultFrame : frame);
            }
        }).start();
    }

    private AEC.AggressiveMode getAggressiveMode() {
        int progress = seekBarAggressiveMode.getProgress();
        switch (progress) {
            case 0:
                return AEC.AggressiveMode.MILD;
            case 1:
                return AEC.AggressiveMode.MEDIUM;
            case 2:
                return AEC.AggressiveMode.HIGH;
            case 3:
                return AEC.AggressiveMode.AGGRESSIVE;
            case 4:
                return AEC.AggressiveMode.MOST_AGGRESSIVE;
        }
        return AEC.AggressiveMode.AGGRESSIVE;
    }

    private void stop() {
        stop = true;
        voiceRecorder.release();
        voicePlayer.stopPlaying();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
        aec.close(); // completely destroys aecm instance, to continue work you need to create a new instance
    }

    public static String getEthernetIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if ("eth0".equals(networkInterface.getName())) { // 判断是否为有线网络
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress inetAddress = addresses.nextElement();
                        if (inetAddress instanceof Inet4Address) {
                            return inetAddress.getHostAddress(); // 返回IPv4地址
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.getStackTraceString(e);
        }
        return "0.0.0.0"; // 默认返回
    }
}
