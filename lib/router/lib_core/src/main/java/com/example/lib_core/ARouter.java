package com.example.lib_core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.example.lib_core.utils.ClassUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * author:  ycl
 * date:  2019/09/24 10:24
 * desc:
 */
public class ARouter {
    private static final String TAG = "ARouter";

    private Map<String, Class<? extends Activity>> activityMap;
    private Context context;

    private static class Holder {
        private static ARouter aRouter = new ARouter();
    }

    private ARouter() {
        activityMap = new HashMap<>();
    }

    public static ARouter getInstance() {
        return Holder.aRouter;
    }

    public void putActivity(String path, Class<? extends Activity> clazz) {
        if (TextUtils.isEmpty(path) || clazz == null) {
            return;
        }
        activityMap.put(path, clazz);
    }

    public void jumpActivity(String path, Bundle bundle) {
        Class<? extends Activity> aClass = activityMap.get(path);
        if (aClass == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(context, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (bundle != null) {
            intent.putExtra("bundle", bundle);
        }
        context.startActivity(intent);
    }

    // 注册所有的注解，从包管理器->dex->allClass
    public void init(Application application) {
        this.context = application;
        try {
            Set<String> className = ClassUtils.getFileNameByPackageName(context, ARouterCons.APT_PACKAGE_NAME);
            for (String name : className) {
                Log.i(TAG, "init: name "+name);
                Class<?> aClass = Class.forName(name);
                //判断当前类是否是IRouter的实现类
                if (IRoute.class.isAssignableFrom(aClass)) {
                    IRoute iRoute= (IRoute) aClass.newInstance();
                    iRoute.putActivity();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
