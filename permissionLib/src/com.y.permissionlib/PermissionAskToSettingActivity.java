package com.y.permissionlib;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;

public class PermissionAskToSettingActivity extends Activity {
    private Activity activity;
    private AlertDialog dialog;
    private static final int REQUEST_CODE_SETTING = 201;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = 1;
        lp.height = 1;
        window.setAttributes(lp);

        show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTING) {
            if(AppSettingDialog.getInstance() != null){
                //权限界面，取消授予的时候，app被杀死，所以对象为空
                AppSettingDialog.getInstance().onActivityResult();
            }
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

        dialog = new AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle(titleStr)
                .setMessage(reasonStr)
                .setPositiveButton(confirmStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialog.dismiss();
                        startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.fromParts("package", activity.getPackageName(),null)),
                                REQUEST_CODE_SETTING);
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
    }
}
