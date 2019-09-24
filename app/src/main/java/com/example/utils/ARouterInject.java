package com.example.utils;

import com.example.lib_core.ARouter;
import com.example.lib_core.IRoute;
import com.example.module1.Module1Activity;

/**
 * author:  ycl
 * date:  2019/09/24 18:03
 * desc:  编译成的文件就是这种类型
 */
@Deprecated
public class ARouterInject implements IRoute {
    @Override
    public void putActivity() {
        ARouter.getInstance().putActivity("/module1/module1main", Module1Activity.class);
    }
}
