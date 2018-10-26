package com.y.permissionlib;

import android.app.Activity;

import java.util.List;

public interface PermissionCallback {

    void onGranted(Activity activity, String[] permissions, List<String> granted);

    void onDenied(Activity activity, String[] permissions, List<String> denied);
}
