package com.example.yuanting.msgpushandcall.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.widget.RemoteViews;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanting on 2018/9/2.
 */

@SuppressLint("Registered")
public class NotifyService extends NotificationListenerService {

    public static final String SEND_MSG_BROADCAST = "SEND_MSG_BROADCAST";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        String packageName = sbn.getPackageName();
        if (!packageName.contains(ComeMessage.MMS) && !packageName.contains(ComeMessage.QQ) && !packageName.contains(ComeMessage.WX)) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(SEND_MSG_BROADCAST);
        Bundle bundle = new Bundle();
        bundle.putString("packageName", packageName);
        String content = null;
        if (sbn.getNotification().tickerText != null) {
            content = sbn.getNotification().tickerText.toString();
        }
        if (content == null) {
            Map<String, Object> info = getNotiInfo(sbn.getNotification());
            if (info != null) {
                content = info.get("title") + ":" + info.get("text");
            }
        }
        if(content == null || content.length() == 1){
            return;
        }
        intent.putExtra("content", content);
        intent.putExtras(bundle);
        this.sendBroadcast(intent);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
    }

    @Override
    public StatusBarNotification[] getActiveNotifications() {
        return super.getActiveNotifications();
    }

    /**
     * 反射取通知栏信息
     *
     * @param notification
     * @return 返回短信内容
     */
    private Map<String, Object> getNotiInfo(Notification notification) {
        int key = 0;
        if (notification == null)
            return null;
        RemoteViews views = notification.contentView;
        if (views == null)
            return null;
        Class secretClass = views.getClass();

        try {
            Map<String, Object> text = new HashMap<>();

            Field outerFields[] = secretClass.getDeclaredFields();
            for (int i = 0; i < outerFields.length; i++) {
                if (!outerFields[i].getName().equals("mActions"))
                    continue;

                outerFields[i].setAccessible(true);

                ArrayList<Object> actions = (ArrayList<Object>) outerFields[i].get(views);
                for (Object action : actions) {
                    Field innerFields[] = action.getClass().getDeclaredFields();
                    Object value = null;
                    Integer type = null;
                    for (Field field : innerFields) {
                        field.setAccessible(true);
                        if (field.getName().equals("value")) {
                            value = field.get(action);
                        } else if (field.getName().equals("type")) {
                            type = field.getInt(action);
                        }
                    }
                    // 经验所得 type 等于9 10为短信title和内容，不排除其他厂商拿不到的情况
                    if (type != null && (type == 9 || type == 10)) {
                        if (key == 0) {
                            text.put("title", value != null ? value.toString() : "");
                        } else if (key == 1) {
                            text.put("text", value != null ? value.toString() : "");
                        } else {
                            text.put(Integer.toString(key), value != null ? value.toString() : null);
                        }
                        key++;
                    }
                }
                key = 0;

            }
            return text;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
