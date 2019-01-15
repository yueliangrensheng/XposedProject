package com.yazao.xposed;

import android.util.Log;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 类描述：
 *
 * @author zhaishaoping
 * @data 2018/6/9 9:06 PM
 */

public class XposedInit implements IXposedHookLoadPackage {
    private static final String TAG = "yazao";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {


        if ("com.iveryone.app.android".equals(loadPackageParam.packageName)) {

            final ClassLoader classLoader = loadPackageParam.classLoader;


            Class<?> clazz = classLoader.loadClass("com.wuyan.android.util.JsonParseUtil");
            //private void parseUserResultInfoJson(String userId)
            XposedHelpers.findAndHookMethod(clazz, "parseUserResultInfoJson", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    Log.i(TAG, " ==== 1. " + param.getResult().toString());
                    Object resultInfo = param.getResult();
                    Method getDataMethod = resultInfo.getClass().getMethod("getData", new Class[]{});

                    Object getDataObj = getDataMethod.invoke(resultInfo, new Object[]{});

                    if (getDataObj == null) {
                        return;
                    }

                    Method getWalletMethod = getDataObj.getClass().getMethod("getWallet", new Class[]{});
                    Object walletInfo = getWalletMethod.invoke(getDataObj, new Object[]{});

                    if (walletInfo == null) {
                        return;
                    }

                    Method getFreezeMethod = walletInfo.getClass().getMethod("getFreeze", new Class[]{});
                    String freeze = (String) getFreezeMethod.invoke(walletInfo, new Object[]{});
                    Log.i(TAG, " ----劫持前 freeze : " + freeze);

                    Method setFreezeMethod = walletInfo.getClass().getMethod("setFreeze", new Class[]{String.class});
                    setFreezeMethod.invoke(walletInfo, new Object[]{"1"});
//
                    freeze = (String) getFreezeMethod.invoke(walletInfo, new Object[]{});
                    Log.i(TAG, " ----劫持后 freeze : " + freeze);

                    Log.i(TAG, " ---- end -----");

                }
            });
        }
    }
}
