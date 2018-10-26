package com.y.permissionlib;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

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
     * @param activity
     * @param perms
     * @return
     */
    protected boolean has(Activity activity, String... perms){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }
        for (String perm : perms) {
            if(ActivityCompat.checkSelfPermission(activity,perm) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param activity
     * @param perms
     */
    protected void request(Activity activity,String... perms){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return;
        }
        int requestCode = (int) (Math.random() * 100);
        ActivityCompat.requestPermissions(activity,perms,requestCode);
    }

    /**
     * 是否对某个权限选择拒绝并不再询问
     * @param activity
     * @param perms
     * @return
     */
    protected boolean noAsk(Activity activity,List<String> perms){
        for (String perm:perms) {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(activity,perm)){
                return true;
            }
        }
        return false;
    }
}
