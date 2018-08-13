package com.zhys.protobufdemo.base;

import android.app.Application;

import com.hdl.elog.ELog;
import com.zhys.protobufdemo.http.HttpConfiger;
import com.zhys.protobufdemo.http.HttpSend;
import com.zhys.protobufdemo.utils.HUtils;

/**
 * Created by HDL on 2018/8/10.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HttpSend.getInstance().initContext(this);
        //release版本不打印日志
        ELog.setIsDebug(HUtils.isDebug(this));
    }
}
