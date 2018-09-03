package com.example.yuanting.msgpushandcall.service;

/**
 * Created by yuanting on 2018/8/10.
 */

public interface IComeMessage {

    /**
     * 短信来了
     */
    void  comeShortMessage(String msg);

    /**
     * 微信消息
     */
    void  comeWxMessage(String msg);

    /**
     * qq消息
     */
    void comeQQmessage(String msg);

}
