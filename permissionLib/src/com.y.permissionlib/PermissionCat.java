package com.y.permissionlib;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionCat {

    private String reason;
    /**
     * 在fragment中调用时
     * 会回调fragment和activity的onRequestPermissionsResult方法
     * 存个调用对象的标记
     */
    private String objectName;

    private PermissionCallback mPermissionCallback;
    /**
     * 询问是否去设置时临时保存权限列表
     */
    private String[] askPerms;

    private PermissionHelper mHelper = PermissionHelper.getInstance();

    private static final PermissionCat instance = new PermissionCat();

    protected static PermissionCat getInstance() {
        return instance;
    }

    private PermissionCat() {
    }


    public static boolean has(Object object, String... perms) {
        if (!checkValid(object)) {
            throw new NullPointerException("PermissionCat -> object must be Activity or Fragment");
        }
        return instance.mHelper.has(object, perms);
    }

    /**
     * 重载的请求授权方法，传入监听器时，不会回调注解方法
     * @param reason 要求打开权限的原因
     * @param object 上下文，fragment调用时需要传入fragment对象V
     * @param callback
     * @param perms
     */
    public static void request(String reason, Object object, PermissionCallback callback, String... perms) {
        if (!checkValid(object)) {
            throw new NullPointerException("PermissionCat -> object must be Activity or Fragment");
        }
        instance.objectName = object.getClass().getName();
        instance.mPermissionCallback = callback;
        instance.reason = reason;
        if (has(object, perms)) {
            instance.notifyResult(object, perms);
            return;
        }

        instance.mHelper.request(object, perms);
    }

    /**
     * 权限都已经授予，就不用申请了，直接走结果通知
     */
    public void notifyResult(Object object, String[] perms) {
        int[] granted = new int[perms.length];

        for (int i = 0; i < perms.length; i++) {
            granted[i] = PackageManager.PERMISSION_GRANTED;
        }

        onRequestPermissionsResult(object, perms, granted);
    }

    /**
     * 授予/禁止权限后的回调
     *
     * @param object
     * @param permissions
     * @param grantResults
     */
    public static void onRequestPermissionsResult(Object object, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(!instance.objectName.equals(object.getClass().getName())) return;
        List<String> granted = new ArrayList<>();
        List<String> denied = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(permissions[i]);
            } else {
                denied.add(permissions[i]);
            }
        }

        if (!denied.isEmpty() && instance.mHelper.noAsk(object, denied)) {
            //有权限被拒绝并不再询问
            instance.showAskSetting(object, permissions);
        } else {
            instance.setPermissionResult(object,permissions, granted, denied);
        }

    }

    private void setPermissionResult(Object object,String[] permissions, List<String> granted, List<String> denied) {
        if (!granted.isEmpty() && mPermissionCallback != null) {
            //授予回调
            mPermissionCallback.onGranted(permissions, granted);
        }

        if (!denied.isEmpty() && mPermissionCallback != null) {
            //拒绝回调
            mPermissionCallback.onDenied(permissions, denied);
        }

        if (denied.isEmpty() && mPermissionCallback == null) {
            //全部授予，调用注解方法
            reflectMethod(object,permissions);
        }
    }

    /**
     * 反射调用注解的使用权限的方法
     *
     * @param permissions
     */
    private void reflectMethod(Object object,String[] permissions) {
        Class clz = object.getClass();
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

//                    if (!"void".equals(m.getReturnType().getName())) {
//                        throw new RuntimeException("method must return void");
//                    }

                    if (m.isAccessible()) {
                        //如果方法是私有，设置为可以访问，否则无法反射访问
                        m.setAccessible(true);
                    }

                    try {
                        m.invoke(object);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 权限被禁用,弹出询问是否去设置权限的对话框
     *
     * @param object
     * @param perms
     */
    private void showAskSetting(Object object, String... perms) {
        askPerms = perms;
        AppSettingDialog.Builder builder = new AppSettingDialog.Builder(object);
        builder
                .title("权限申请")
                .content(reason == null ? "应用需要该权限" : reason)
                .cancelStr("取消")
                .confirmStr("去设置")
                .build()
                .show();
    }

    /**
     * 设置页面退后回调
     *
     * @param object
     */
    protected void onActivityResult(Object object) {
        List<String> granted = new ArrayList<>();
        List<String> denied = new ArrayList<>();

        String[] perms = askPerms.clone();
        askPerms = null;

        for (String perm : perms) {
            if (has(object, perm)) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }
        setPermissionResult(object,perms, granted, denied);
    }

    private static boolean checkValid(Object object) {
        return object instanceof Activity || object instanceof Fragment || object instanceof android.app.Fragment;
    }

}
