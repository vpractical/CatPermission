package com.y.permissionlib;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class PermissionAskToSettingActivity extends AppCompatActivity {
    private Context context;
    private AlertDialog dialog;
    private static final int REQUESTCODE_SETTING = 201;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LLog.e("设置页回来a");
        if (requestCode == REQUESTCODE_SETTING) {
            LLog.e("设置页回来a");
            if (AppSettingDialog.getInstance() == null) {
                //权限界面，取消授予的时候，app被杀死，所以对象为空
                LLog.e("设置页回来a-AppSettingDialog.getInstance()是null");
                return;
            }
            AppSettingDialog.getInstance().onActivityResult();
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            finish();
        }
    }

    private void show() {
        Intent intent = getIntent();
        String titleStr = intent.getStringExtra("titleStr");
        String reasonStr = intent.getStringExtra("reasonStr");
        String confirmStr = intent.getStringExtra("confirmStr");
        String cancelStr = intent.getStringExtra("cancelStr");

        dialog = new AlertDialog.Builder(context)
                .setTitle(titleStr)
                .setMessage(reasonStr)
                .setPositiveButton(confirmStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.fromParts("package", context.getPackageName(),null)),
                                REQUESTCODE_SETTING);
                    }
                })
                .setNegativeButton(cancelStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .create();
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
    }
}
