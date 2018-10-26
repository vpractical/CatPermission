package com.y.catpermission;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.y.catpermission.base.BaseActivity;
import com.y.permissionlib.LLog;
import com.y.permissionlib.PermCat;
import com.y.permissionlib.PermissionCat;

public class MainActivity extends BaseActivity {

    TextView tv;

    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_CONTACTS_READ = Manifest.permission.READ_CONTACTS;
    public static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.tv);
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn1();
            }
        });
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn2();
            }
        });

    }

    @PermCat(PERMISSION_CAMERA)
    public void btn1() {
        LLog.e("1执行按钮方法");
        if(PermissionCat.has(mActivity,PERMISSION_CAMERA)){
            LLog.e("1已经授予权限了");
            tv.append("持有相机权限");
        }else{
            LLog.e("1没有权限，开始申请");
            PermissionCat.request(mActivity,"想拍照",PERMISSION_CAMERA);
        }
    }

    @PermCat(PERMISSION_LOCATION)
    public void btn2() {
        LLog.e("2执行按钮方法");
        String[] perms = {PERMISSION_CONTACTS_READ,PERMISSION_LOCATION};
        if(PermissionCat.has(mActivity,perms)){
            LLog.e("2已经授予权限了");
            tv.append("持有通讯录、位置权限");
        }else{
            LLog.e("2没有权限，开始申请");
            PermissionCat.request(mActivity,"想要权限",perms);
        }
    }

    public void btn3(View v) {
        Intent intent = new Intent(mActivity,SecondActivity.class);
        startActivity(intent);
    }
}
