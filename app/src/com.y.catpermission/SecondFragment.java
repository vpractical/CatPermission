package com.y.catpermission;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.y.catpermission.base.BaseFragment;
import com.y.permissionlib.LLog;
import com.y.permissionlib.PermCat;
import com.y.permissionlib.PermissionCallback;
import com.y.permissionlib.PermissionCat;

import java.util.List;

public class SecondFragment extends BaseFragment {

    private TextView tv;
    private Button btn11,btn12;
    private Activity activity;

    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_second,null);
        tv = view.findViewById(R.id.tv);
        btn11 = view.findViewById(R.id.btn11);
        btn12 = view.findViewById(R.id.btn12);
        btn11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn11();
            }
        });

        btn12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn12();
            }
        });

        return view;
    }

    @PermCat(value = PERMISSION_CAMERA)
    public void btn11() {
        if(PermissionCat.has(this,PERMISSION_CAMERA)){
            LLog.e("do some thing");
        }else{
            PermissionCat.request("拍照用",this,null,PERMISSION_CAMERA);
        }
    }

    public void btn12() {
        if(PermissionCat.has(this,PERMISSION_CAMERA)){
            LLog.e("do some thing");
        }else{
            PermissionCat.request("拍照用", this, new PermissionCallback() {
                @Override
                public void onGranted(String[] permissions, List<String> granted) {
                    LLog.e("PermissionCallback.onGranted()");
                    if(!granted.isEmpty() && PERMISSION_CAMERA.equals(granted.get(0))){
                        btn12();
                    }
                }

                @Override
                public void onDenied(String[] permissions, List<String> denied) {
                    LLog.e("PermissionCallback.onDenied()");
                }
            }, PERMISSION_CAMERA);
        }
    }
}
