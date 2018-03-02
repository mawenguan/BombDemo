package com.mwg.bombdemo.app;

import android.app.Application;
import android.media.MediaPlayer;

import com.mwg.bombdemo.R;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

/**
 * Created by mwg on 2018/2/24.
 */

public class MyApp extends Application {

    //6b306832766d1235352dba84c799dd23
    public static MyApp CONTEXT;

    public static MediaPlayer player;

    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = this;
        //第一：默认初始化BmobSDK
        Bmob.initialize(this, "6b306832766d1235352dba84c799dd23");
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this);

        player = MediaPlayer.create(this, R.raw.notify);
    }
}
