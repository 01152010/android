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
import com.upad.voice.VoiceFileReader;
import com.upad.voice.EchoSynchronizer;
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
    private AEC[] aecs = null; // one AEC instance per mic channel when enabled
    private VoiceRecorder voiceRecorder;
    private VoiceFileReader voiceFileReader;
    private VoicePlayer voicePlayer;
    private EchoSynchronizer echoSync;
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
        // If AEC is enabled, create one AEC instance per mic channel (4 mics)
        if (enableAecm) {
            aecs = new AEC[4];
            for (int i = 0; i < 4; i++) {
                aecs[i] = new AEC();
                aecs[i].setSampFreq(seekBarSampleRate.getProgress() == 0 ? AEC.SamplingFrequency.FS_8000Hz : AEC.SamplingFrequency.FS_16000Hz);
                aecs[i].setAecmMode(getAggressiveMode());
            }
        }

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
        // try to open local PCM file from assets named "input6ch.pcm" (interleaved 6ch int16 little-endian)
        boolean usingFile = false;
        try {
            voiceFileReader = new VoiceFileReader(getAssets().open("input6ch.pcm"));
            voiceFileReader.start(SAMPLE_RATE, FRAME_SIZE, 6);
            usingFile = true;
        } catch (Exception ignored) {}

        if (!usingFile) {
            // start recorder in 6-channel mode: 4 mic channels + 2 playback (render) channels
            voiceRecorder.start(SAMPLE_RATE, FRAME_SIZE, 6);
        }
        voicePlayer.start(SAMPLE_RATE);
        // initialize echo synchronizer: maxDelayMs=200ms, history=1000ms
        echoSync = new EchoSynchronizer(SAMPLE_RATE, FRAME_SIZE, 200, 1000);
        stop = false;
        new Thread(() -> {
            while (!stop) {
                // read interleaved 6-channel frame (from file or recorder)
                short[][] channels = usingFile ? voiceFileReader.frameMulti() : voiceRecorder.frameMulti();
                if (channels == null) { // EOF for file
                    stop = true;
                    break;
                }
                // layout assumption: [mic0, mic1, mic2, mic3, play0, play1]
                short[] farend = new short[FRAME_SIZE];
                for (int i = 0; i < FRAME_SIZE; i++) {
                    int s0 = channels.length > 4 ? channels[4][i] : 0;
                    int s1 = channels.length > 5 ? channels[5][i] : 0;
                    farend[i] = (short) ((s0 + s1) / ( (s0 != 0 && s1 != 0) ? 2 : 1));
                }

                if (echoSync != null) echoSync.bufferFarend(farend);

                short[][] processed = new short[4][];
                for (int m = 0; m < 4; m++) {
                    short[] mic = channels.length > m ? channels[m] : new short[FRAME_SIZE];
                    if (enableAecm && aecs != null && aecs[m] != null) {
                        aecs[m].farendBuffer(farend, FRAME_SIZE);
                        int delayMs = 0;
                        if (echoSync != null) delayMs = echoSync.estimateDelayMs(mic);
                        short[] out = aecs[m].echoCancellation(mic, FRAME_SIZE, delayMs);
                        processed[m] = out != null ? out : mic;
                    } else {
                        processed[m] = mic;
                    }
                }

                // mix down processed mic channels to mono for playback
                short[] mix = new short[FRAME_SIZE];
                for (int i = 0; i < FRAME_SIZE; i++) {
                    int sum = 0;
                    for (int m = 0; m < 4; m++) sum += processed[m][i];
                    mix[i] = (short) (sum / 4);
                }
                voicePlayer.write(mix);
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
        if (voiceRecorder != null) voiceRecorder.release();
        if (voiceFileReader != null) {
            voiceFileReader.close();
            voiceFileReader = null;
        }
        voicePlayer.stopPlaying();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
        if (aecs != null) {
            for (int i = 0; i < aecs.length; i++) if (aecs[i] != null) aecs[i].close();
        }
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
