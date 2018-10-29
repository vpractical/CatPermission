package com.y.catpermission.base;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.y.permissionlib.PermissionCat;

public class BaseFragment extends Fragment {

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionCat.onRequestPermissionsResult(this,permissions, grantResults);
    }
}
