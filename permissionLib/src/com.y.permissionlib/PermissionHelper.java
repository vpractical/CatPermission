package com.y.permissionlib;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import java.util.List;

public class PermissionHelper {
    private static final PermissionHelper ourInstance = new PermissionHelper();

    public static PermissionHelper getInstance() {
        return ourInstance;
    }

    private PermissionHelper() {
    }

    /**
     * 判断是否允许了全部权限，没有全部允许就返回false
     * @param object
     * @param perms
     * @return
     */
    protected boolean has(Object object, String... perms){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }
        for (String perm : perms) {
            if(ActivityCompat.checkSelfPermission(target(object),perm) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    /**
     * 区分activity、fragment中操作的回调
     * @param object
     * @param perms
     */
    protected void request(Object object,String... perms){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return;
        }
        int requestCode = (int) (Math.random() * 100);
        if (object instanceof Fragment) {
            ((Fragment) object).requestPermissions(perms,requestCode);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).requestPermissions(perms,requestCode);
        } else {
            ((Activity) object).requestPermissions(perms,requestCode);
        }
    }

    /**
     * 是否对某个权限选择拒绝并不再询问
     * @param object
     * @param perms
     * @return
     */
    protected boolean noAsk(Object object,List<String> perms){
        for (String perm:perms) {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(target(object),perm)){
                return true;
            }
        }
        return false;
    }

    protected Activity target(Object object) {
        Activity activity;
        if (object instanceof Fragment) {
            activity = ((Fragment) object).getActivity();
        } else if (object instanceof android.app.Fragment) {
            activity = ((android.app.Fragment) object).getActivity();
        } else {
            activity = (Activity) object;
        }

        if (activity == null) {
            throw new IllegalStateException("the result of type conversion is null");
        }
        return activity;
    }
}
