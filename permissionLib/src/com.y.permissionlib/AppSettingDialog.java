package com.y.permissionlib;

import android.app.Activity;
import android.content.Intent;

public class AppSettingDialog {

    private static AppSettingDialog instance = null;

    public static AppSettingDialog getInstance() {
        return instance;
    }

    private Object object;
    private String titleStr, reasonStr;
    private String cancelStr, confirmStr;

    private AppSettingDialog(Object object,
                             String titleStr,
                             String reasonStr,
                             String cancelStr,
                             String confirmStr) {
        this.object = object;
        this.titleStr = titleStr;
        this.reasonStr = reasonStr;
        this.cancelStr = cancelStr;
        this.confirmStr = confirmStr;
    }

    public AppSettingDialog show() {
        Activity activity = PermissionHelper.getInstance().target(object);
        Intent intent = new Intent(activity, PermissionAskToSettingActivity.class);
        intent.putExtra("titleStr", titleStr);
        intent.putExtra("reasonStr", reasonStr);
        intent.putExtra("cancelStr", cancelStr);
        intent.putExtra("confirmStr", confirmStr);
        activity.startActivity(intent);
        return this;
    }


    public void onActivityResult() {
        PermissionCat.getInstance().onActivityResult(object);
    }

    public static class Builder {

        private Object object;
        private String titleStr, reasonStr;
        private String cancelStr, confirmStr;

        public Builder(Object object) {
            this.object = object;
        }

        public Builder title(String titleStr) {
            this.titleStr = titleStr;
            return this;
        }

        public Builder content(String reasonStr) {
            this.reasonStr = reasonStr;
            return this;
        }

        public Builder cancelStr(String cancelStr) {
            this.cancelStr = cancelStr;
            return this;
        }

        public Builder confirmStr(String confirmStr) {
            this.confirmStr = confirmStr;
            return this;
        }

        public AppSettingDialog build() {
            instance = new AppSettingDialog(object, titleStr, reasonStr, cancelStr, confirmStr);
            return instance;
        }

    }
}
