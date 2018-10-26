package com.y.permissionlib;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionCat {

    private String reason;//要求打开权限的原因
    private PermissionCallback mPermissionCallback;

    /**
     * 询问是否去设置时临时保存权限列表
     */
    private String[] askPerms;

    private PermissionHelper mHelper = PermissionHelper.getInstance();

    private static final PermissionCat instance = new PermissionCat();

    public static PermissionCat getInstance() {
        return instance;
    }

    private PermissionCat() {
    }


    public static boolean has(Activity activity, String... perms) {
        if (activity == null) {
            throw new NullPointerException("PermissionCat -> method params activity is null");
        }
        return instance.mHelper.has(activity, perms);
    }


    public static void request(String reason, Activity activity, String... perms) {
        if (activity == null) {
            throw new NullPointerException("PermissionCat -> method params activity is null");
        }
        instance.reason = reason;
        if (has(activity, perms)) {
            instance.notifyResult(activity, perms);
            return;
        }

        instance.mHelper.request(activity, perms);
    }

    /**
     * 重载的请求授权方法，传入监听器时，不会回调注解方法
     *
     * @param reason
     * @param activity
     * @param callback
     * @param perms
     */
    public static void request(String reason, Activity activity, PermissionCallback callback, String... perms) {
        instance.mPermissionCallback = callback;
        request(reason, activity, perms);
    }

    /**
     * 权限都已经授予，就不用申请了，直接走结果通知
     */
    public void notifyResult(Activity activity, String[] perms) {
        int[] granteds = new int[perms.length];

        for (int i = 0; i < perms.length; i++) {
            granteds[i] = PackageManager.PERMISSION_GRANTED;
        }

        onRequestPermissionsResult(activity, perms, granteds);
    }

    /**
     * 授予/禁止权限后的回调
     *
     * @param activity
     * @param permissions
     * @param grantResults
     */
    public static void onRequestPermissionsResult(Activity activity, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LLog.e("申请/禁止权限后的回调");
        List<String> granted = new ArrayList<>();
        List<String> denied = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(permissions[i]);
            } else {
                denied.add(permissions[i]);
            }
        }

        if (!denied.isEmpty() && instance.mHelper.noAsk(activity, permissions)) {
            //有权限被拒绝并不再询问
            instance.showAskSetting(activity, permissions);
        } else {
            instance.setPermissionResult(activity, permissions, granted, denied);
        }

    }

    private void setPermissionResult(Activity activity, String[] permissions, List<String> granted, List<String> denied) {
        LLog.e("权限申请/设置后的结果分发，成功、失败、回调注解方法");

        if (!granted.isEmpty() && mPermissionCallback != null) {
            //授予回调
            mPermissionCallback.onGranted(activity, permissions, granted);
        }

        if (!denied.isEmpty() && mPermissionCallback != null) {
            //拒绝回调
            mPermissionCallback.onDenied(activity, permissions, denied);
        }

        if (denied.isEmpty() && mPermissionCallback == null) {
            //全部授予，调用注解方法
            reflectMethod(activity, permissions);
        }
    }

    /**
     * 反射调用注解的使用权限的方法
     *
     * @param activity
     * @param permissions
     */
    private void reflectMethod(Activity activity, String[] permissions) {
        LLog.e("回调注解方法");
        Class<? extends Activity> clz = activity.getClass();
        Method[] methods = clz.getDeclaredMethods();

        for (Method m : methods) {
            if (m.isAnnotationPresent(PermCat.class)) {
                Annotation[] annotations = m.getDeclaredAnnotations();
                PermCat permCat = null;
                for (Annotation a : annotations) {
                    if (a instanceof PermCat) {
                        permCat = (PermCat) a;
                        break;
                    }
                }
                if (permCat == null) continue;
                //检测注解
                if (Arrays.asList(permissions).contains(permCat.value())) {
                    //方法必须是返回void

                    if (!"void".equals(m.getReturnType().getName())) {
                        throw new RuntimeException("method must return void");
                    }

                    if (m.isAccessible()) {
                        //如果方法是私有，设置为可以访问，否则无法反射访问
                        m.setAccessible(true);
                    }

                    try {
                        m.invoke(activity);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 权限被禁用
     *
     * @param activity
     * @param perms
     */
    private void showAskSetting(Activity activity, String... perms) {
        askPerms = perms;
        LLog.e("有权限被不再询问，弹出去设置询问");
        AppSettingDialog.Builder builder = new AppSettingDialog.Builder(activity);
        builder
                .title("去设置")
                .content(reason == null ? "应用需要该权限" : reason)
                .cancelStr("取消.")
                .confirmStr("去设置.")
                .build()
                .show();

    }

    /**
     * 设置页面退后回调
     *
     * @param activity
     */
    protected void onActivityResult(Activity activity) {
        LLog.e("从设置页面回来的回调");
        List<String> granted = new ArrayList<>();
        List<String> denied = new ArrayList<>();

        String[] perms = askPerms.clone();
        askPerms = null;

        for (String perm : perms) {
            if (has(activity, perm)) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }

        setPermissionResult(activity, perms, granted, denied);
    }

}
