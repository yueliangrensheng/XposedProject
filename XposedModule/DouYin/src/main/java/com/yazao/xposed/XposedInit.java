package com.yazao.xposed;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 类描述：
 *
 * @author zhaishaoping
 * @data 2018/6/7 11:51 AM
 */

public class XposedInit implements IXposedHookLoadPackage{

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        //去除广告
        AwemeFilterAdWaterMask.hook(loadPackageParam);
    }
}
