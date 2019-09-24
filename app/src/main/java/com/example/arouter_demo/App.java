package com.example.arouter_demo;

import android.app.Application;

import com.example.lib_core.ARouter;

/**
 * author:  ycl
 * date:  2019/09/24 17:26
 * desc:
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.getInstance().init(this);
    }
}
