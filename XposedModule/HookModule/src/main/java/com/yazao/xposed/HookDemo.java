package com.yazao.xposed;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 类描述：Hook Demo.apk MainActivity中的 checkLegacy(String,String)方法
 *
 * @author zhaishaoping
 * @data 03/01/2018 4:20 PM
 */

public class HookDemo implements IXposedHookLoadPackage {

    //加载包时候的回调
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("Loaded app  : " + lpparam.packageName);
        if ("com.yazao.xposed.demo".equals(lpparam.packageName)) {
            XposedBridge.log("============ Xposed loaded target App ============");
            final String className = "com.yazao.xposed.demo.MainActivity";
            final ClassLoader classLoader = lpparam.classLoader;
            final String methodName = "checkLegacy";

            // Hook Demo中的 MainActivity中的 checkLegacy(String,String)方法
            XposedHelpers.findAndHookMethod(className, classLoader, methodName, String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("开始劫持了。。。。。");
                    XposedBridge.log("开始劫持 参数1 = " + param.args[0]);
                    XposedBridge.log("开始劫持 参数2 = " + param.args[1]);

                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("劫持前返回的result：" + param.getResult());
                    param.setResult(true);
                    XposedBridge.log("劫持后返回的result：" + param.getResult());
                    XposedBridge.log("hook finish!!!");
                }
            });

        }


    }
}
