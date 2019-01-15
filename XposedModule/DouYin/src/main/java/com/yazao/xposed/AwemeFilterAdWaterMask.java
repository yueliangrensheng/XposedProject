package com.yazao.xposed;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 类描述：过滤 抖音 广告和视频水印
 *
 * @author zhaishaoping
 * @data 2018/6/7 11:53 AM
 */

public class AwemeFilterAdWaterMask {

    private static final String TAG = "yazao";
    private static Activity mActivity;

    public static void hook(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if ("com.ss.android.ugc.aweme".equals(loadPackageParam.packageName)) {
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    Context context = (Context) param.args[0];
                    ClassLoader classLoader = context.getClassLoader();

                    Class<?> clazz = null;
                    try {

                        //1. 获取Activity 实例对象
                        clazz = classLoader.loadClass("com.ss.android.ugc.aweme.main.MainActivity");
                        if (clazz != null) {
                            XposedHelpers.findAndHookMethod(clazz, "onResume", new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    super.beforeHookedMethod(param);
                                    mActivity = (Activity) param.thisObject;
                                    Log.i(TAG, mActivity.toString());
                                }
                            });
                        }


                        //2. 获取解析 Feed 流 -- 拦截广告相关数据
                        clazz = classLoader.loadClass("com.ss.android.ugc.aweme.feed.api.FeedApi");
                        if (clazz != null) {
                            //  public static FeedItemList a(int i, long j, long j2, int i2, Integer num, String str, int i3)
                            XposedHelpers.findAndHookMethod(clazz, "a", int.class, long.class, long.class, int.class, Integer.class, String.class, int.class,
                                    new XC_MethodHook() {
                                        @Override
                                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                            super.afterHookedMethod(param);

                                            Class<?> clazzs = param.getResult().getClass();// com.ss.android.ugc.aweme.feed.model.FeedItemList
                                            if (clazzs == null) {
                                                return;
                                            }

                                            Method getItemsMethod = clazzs.getMethod("getItems", new Class<?>[]{});

                                            List items = (List) getItemsMethod.invoke(param.getResult(), new Object[]{});

                                            List filterItems = new ArrayList();
                                            int adCount = 0;//记录广告的数量
                                            for (Object obj : items) {
                                                if (isAd(obj)) {
                                                    Log.i(TAG, "filter ad info successful : " + obj);
                                                    adCount++;
                                                    continue;
                                                }

                                                //非广告数据
                                                filterItems.add(obj);
                                            }

                                            //将无广告的数据 重新set 回 数据流中
                                            Method setItemsMethod = clazzs.getMethod("setItems", List.class);
                                            setItemsMethod.invoke(param.getResult(), filterItems);

                                            if (adCount == 0) {
                                                showToast("未发现广告！！！");
                                            } else {
                                                showToast("哎呀， 发现了" + adCount + "条广告");
                                            }

                                        }
                                    });
                        }


                        //3.去除水印
                        clazz = classLoader.loadClass("com.ss.android.ugc.aweme.feed.f.a.c");
                        Class<?> paramClazz = classLoader.loadClass("com.ss.android.ugc.aweme.feed.model.Aweme");
                        Log.i(TAG, " ----- 1 ");
                        if (clazz != null && paramClazz != null) {
                            //  public final void a(Aweme aweme, String str)
                            Log.i(TAG, " ----- 2 ");
                            XposedHelpers.findAndHookMethod(clazz, "a", paramClazz, String.class, new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    super.beforeHookedMethod(param);

                                    try {
                                        Log.i(TAG, " ----- 3 ");
                                        Log.i(TAG, "Aweme = " + param.args[0].toString());

                                        Class<?> awemeClazz = param.args[0].getClass();
                                        Method getVideoMethod = awemeClazz.getMethod("getVideo", new Class[]{});
                                        Object videoObj = getVideoMethod.invoke(param.args[0], new Object[]{});

                                        //水印
                                        Method isHasWaterMethod = videoObj.getClass().getMethod("isHasWaterMark", new Class[]{});
                                        boolean isWaterMark = (boolean) isHasWaterMethod.invoke(videoObj, new Object[]{});
                                        Log.i(TAG, "isWaterMark = " + isWaterMark);

                                        //下载地址
                                        Method getDownloadAddrMethod = videoObj.getClass().getMethod("getDownloadAddr", new Class[]{});
                                        Object downloadAddObj = getDownloadAddrMethod.invoke(videoObj, new Object[]{});
                                        Method getUrlListmethod = downloadAddObj.getClass().getMethod("getUrlList", new Class[]{});
                                        List<String> downLoadUrlList = (List<String>) getUrlListmethod.invoke(downloadAddObj, new Object[]{});
                                        Log.i(TAG, "load url list = " + downLoadUrlList.toString());


                                        //播放地址
                                        Method getPlayAddrMethod = videoObj.getClass().getMethod("getPlayAddr", new Class[]{});
                                        Object getPlayAddrObj = getPlayAddrMethod.invoke(videoObj, new Object[]{});
                                        getUrlListmethod = getPlayAddrObj.getClass().getMethod("getUrlList", new Class[]{});
                                        List<String> playUrlList = (List<String>) getUrlListmethod.invoke(getPlayAddrObj, new Object[]{});
                                        Log.i(TAG, "play url list = " + playUrlList.toString());


                                        //将播放地址的值 设置到下载地址中。
                                        downLoadUrlList.add(0, playUrlList.remove(playUrlList.size() - 1));


                                    } catch (Exception e) {
                                    }

                                }

                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    super.afterHookedMethod(param);
                                }
                            });
                        }

                    } catch (Exception e) {
                        Log.i(TAG, e.getMessage().toString());
                    }

                }
            });
        }
    }

    /**
     * 判断 数据是否为广告数据
     *
     * @param awemeObj
     * @return
     */
    private static boolean isAd(Object awemeObj) {
        //awemeObj :  com.ss.android.ugc.aweme.feed.model.Aweme
        try {

            if (awemeObj == null) {
                return false;
            }

            Class<?> clazz = awemeObj.getClass();

            //广点通广告
            Method isAdMethod = clazz.getMethod("isAd", new Class<?>[]{});
            boolean isAd = (boolean) isAdMethod.invoke(awemeObj, new Object[]{});
            Log.i(TAG, "isAd = " + isAd);
            if (isAd) {
                return true;
            }

            //原生广告
            Method getAwemeRawAdMethod = clazz.getMethod("getAwemeRawAd", new Class[]{});
            Object invoke = getAwemeRawAdMethod.invoke(awemeObj, new Object[]{});
            Log.i(TAG, "isRawAd = " + isAd);
            if (invoke != null) {
                return true;
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage().toString());
        }

        return false;
    }

    private static void showToast(final String message) {
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
