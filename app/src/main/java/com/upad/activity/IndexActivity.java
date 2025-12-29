package com.upad.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.k2fsa.sherpa.ncnn.SherpaNcnn;
import com.upad.R;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.Buddy;
import org.pjsip.pjsua2.BuddyConfig;
import org.pjsip.pjsua2.BuddyInfo;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.ContainerNode;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;
import org.pjsip.pjsua2.IpChangeParam;
import org.pjsip.pjsua2.JsonDocument;
import org.pjsip.pjsua2.LogConfig;
import org.pjsip.pjsua2.LogEntry;
import org.pjsip.pjsua2.LogWriter;
import org.pjsip.pjsua2.OnCallMediaEventParam;
import org.pjsip.pjsua2.OnCallMediaStateParam;
import org.pjsip.pjsua2.OnCallStateParam;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnInstantMessageParam;
import org.pjsip.pjsua2.OnRegStateParam;
import org.pjsip.pjsua2.OnTimerParam;
import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.UaConfig;
import org.pjsip.pjsua2.pj_log_decoration;
import org.pjsip.pjsua2.pjmedia_srtp_use;
import org.pjsip.pjsua2.pjmedia_vid_dev_std_index;
import org.pjsip.pjsua2.pjsip_evsub_state;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_role_e;
import org.pjsip.pjsua2.pjsip_transport_type_e;
import org.pjsip.pjsua2.pjsua2;
import org.pjsip.pjsua2.pjsua_buddy_status;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Objects;

import org.pjsip.pjsua2.*;


public class IndexActivity extends AppCompatActivity {
    private static final String TAG = "SIP";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private final String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};

    // If there is a GPU and useGPU is true, we will use GPU
    // If there is no GPU and useGPU is true, we won't use GPU
    private final boolean useGPU = true;

    private SherpaNcnn model;
    private AudioRecord audioRecord;
    private TextView textView;

    private TextView ip;
    private Thread recordingThread;

    private final int audioSource = MediaRecorder.AudioSource.MIC;
    private final int sampleRateInHz = 16000;
    private final int channelConfig = AudioFormat.CHANNEL_IN_MONO;

    // Note: We don't use AudioFormat.ENCODING_PCM_FLOAT
    // since the AudioRecord.read(float[]) needs API level >= 23
    // but we are targeting API level >= 21
    private final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int idx = 0;
    private String lastText = "";

    private volatile boolean isCalling = false;

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionToRecordAccepted = false;
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }

        if (!permissionToRecordAccepted) {
            Log.e(TAG, "Audio record is disallowed");
            finish();
        }

        Log.i(TAG, "Audio record is permitted");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        ip = findViewById(R.id.my_ip);
        ip.setText(getEthernetIpAddress());

        textView = findViewById(R.id.my_call);
        Button dialButton = findViewById(R.id.dial_button);
        dialButton.setOnClickListener(v -> {
            if(!isCalling){
                try {
                    isCalling = true;
                    dialButton.setText("挂断电话");
                } catch (Exception e) {
                    Log.e("call",e.toString());
                }
            } else{
                try {
                    isCalling = false;
                    dialButton.setText("拨打电话");
                } catch (Exception e) {
                    Log.e("call",e.toString());
                }
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
