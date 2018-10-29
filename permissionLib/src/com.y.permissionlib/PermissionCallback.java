package com.y.permissionlib;

import java.util.List;

public interface PermissionCallback {

    void onGranted(String[] permissions, List<String> granted);

    void onDenied(String[] permissions, List<String> denied);
}
