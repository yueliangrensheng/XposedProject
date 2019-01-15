package com.yazao.xposed;

import android.content.Intent;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 类描述：Hook 系统所有的广播
 *
 * @author zhaishaoping
 * @data 03/01/2018 7:02 PM
 */

public class HookBroadcast implements IXposedHookLoadPackage,IXposedHookZygoteInit {
    /**
     * IntentFirewall 这个类，126行代码开始有一些以check开头的方法，说明如下:
     * <p>
     * This is called from ActivityManager to check if a start activity intent should be allowed.
     * It is assumed the caller is already holding the global ActivityManagerService lock.
     * <p>
     * public boolean checkBroadcast(Intent intent, int callerUid, int callerPid,
     * String resolvedType, int receivingUid) {
     * return checkIntent(mBroadcastResolver, intent.getComponent(), TYPE_BROADCAST, intent,
     * callerUid, callerPid, resolvedType, receivingUid);
     * }
     * <p>
     * 这里，我通过hook该方法并获取到第一个参数Intent intent，然后就可以获取到广播类型，同时也可以获取到该广播的
     * 发送者(callerUid)和接受者(receivingUid)
     * <p>
     * 说明很清晰的告诉我们，所有activity启动时，会到这里做相应的检测是否被允许，因此我们hook掉checkBroadcast方法，
     * 就可以控制所有的广播包走向。checkBroadcast代码如下。
     */
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("======== Hook Broadcast ========");
        String classname = "com.android.server.firewall.IntentFirewall";
        ClassLoader classloader = lpparam.classLoader;
        String methodname = "checkBroadcast";
        XposedHelpers.findAndHookMethod(classname, classloader, methodname,

                Intent.class,   // intent
                int.class,  // callerUid
                int.class,  // callerPid
                String.class,   // resolvedType
                int.class,  // receivingUid

                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        int callerUid = (int) param.args[1];
                        int receivingUid = (int) param.args[4];
                        XposedBridge.log("Hook IntentFirewall.checkBroadcast : " + "broadcast from " + callerUid + " to " + receivingUid);

                        Intent intent = (Intent) param.args[0];
                        String action = intent.getAction();

                        if (action == null)
                            return;
                        if (action.equals("android.intent.action.SCREEN_OFF"))
                            XposedBridge.log("hook IntentFirewall.checkBroadcast : " + "screen off");
                        if (action.equals("android.intent.action.SCREEN_ON"))
                            XposedBridge.log("hook IntentFirewall.checkBroadcast : " + "screen on");
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    }
                });
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {

    }
}
