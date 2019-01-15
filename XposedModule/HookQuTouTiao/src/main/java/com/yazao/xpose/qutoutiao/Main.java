package com.yazao.xpose.qutoutiao;

import android.content.Context;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 类描述：Hook 趣头条 防抓包
 *
 * @author zhaishaoping
 * @data 2019/1/2 11:19 PM
 */
public class Main implements IXposedHookLoadPackage {


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        if ("com.jifen.qukan".equals(loadPackageParam.packageName)){
            XposedBridge.log("--------- hook 趣头条 begin -----------");

            //com.loc.bq.
            XposedHelpers.findAndHookMethod("com.loc.bq", loadPackageParam.classLoader, "b", Context.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("------- hook 趣头条 -- com.loc.bq.b(Context)-----");
                    param.setResult(false);
                }
            });

        }
    }
}
