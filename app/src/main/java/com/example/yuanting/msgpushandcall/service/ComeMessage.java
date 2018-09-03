package com.example.yuanting.msgpushandcall.service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;

/**
 * Created by yuanting on 2018/8/10.
 */

public class ComeMessage {
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    public static final String QQ="com.tencent.mobileqq";
    public static final String WX="com.tencent.mm";
    public static final String MMS="com.android.mms";
    private IComeMessage myMessage;
    private Context context;

    public ComeMessage(IComeMessage myMessage, Context context) {
        this.myMessage = myMessage;
        this.context = context;
        registBroadCast();
    }

    private BroadcastReceiver  receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle=intent.getExtras();
            String packageName = bundle.getString("packageName");
            String msg = bundle.getString("content");

            if(packageName.contains(WX)){
                myMessage.comeWxMessage(msg);
            }else if(packageName.contains(QQ)){
                myMessage.comeQQmessage(msg);
            }else if(packageName.contains(MMS)){
                myMessage.comeShortMessage(msg);
            }
        }
    };
    private void registBroadCast() {
        IntentFilter filter=new IntentFilter(NotifyService.SEND_MSG_BROADCAST);
        context.registerReceiver(receiver,filter);
    }

    public void unRegistBroadcast(){
        context.unregisterReceiver(receiver);
    }

    public void openSetting(){
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        if(!(context instanceof Activity)){
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public boolean isEnabled() {
        String pkgName = context.getPackageName();
        final String flat = Settings.Secure.getString(context.getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void toggleNotificationListenerService() {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName(context,NotifyService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(
                new ComponentName(context,NotifyService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}
