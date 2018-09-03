package com.example.yuanting.msgpushandcall;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import com.example.yuanting.msgpushandcall.service.ComeMessage;
import com.example.yuanting.msgpushandcall.service.IComeMessage;
import com.example.yuanting.msgpushandcall.utils.PhoneCallUtil;

public class MainActivity extends AppCompatActivity implements IComeMessage{

    private TelephonyManager telephonyManager;

    private PhoneCallListener callListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telephonyManager.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        });
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CALL_PHONE},1000);
        }

        findViewById(R.id.tv_push_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComeMessage comeMessage = new ComeMessage(MainActivity.this,MainActivity.this);
                if(!comeMessage.isEnabled()){
                   comeMessage.openSetting();
                   comeMessage.toggleNotificationListenerService();
                }
            }
        });

        telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        callListener = new PhoneCallListener();
    }

    @Override
    public void comeShortMessage(String msg) {
        Log.d("comeShortMessage",msg);
    }

    @Override
    public void comeWxMessage(String msg) {
        Log.d("comeWxMessage",msg);
    }

    @Override
    public void comeQQmessage(String msg) {
        Log.d("comeQQmessage",msg);
    }


    /**
     * 监听来电状态
     */
    public class PhoneCallListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_OFFHOOK:                   //电话通话的状态
                    break;

                case TelephonyManager.CALL_STATE_RINGING:                   //电话响铃的状态
                    PhoneCallUtil.endPhone(MainActivity.this);
                    break;

            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }
}
