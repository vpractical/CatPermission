package com.y.permissionlib;

import android.app.Activity;
import android.content.Intent;

public class AppSettingDialog {

    private static AppSettingDialog instance = null;

    public static AppSettingDialog getInstance(){
        return instance;
    }

    private Activity context;
    private String titleStr, reasonStr;
    private String cancelStr, confirmStr;

    private AppSettingDialog(Activity context,
                            String titleStr,
                            String reasonStr,
                            String cancelStr,
                            String confirmStr) {
        this.context = context;
        this.titleStr = titleStr;
        this.reasonStr = reasonStr;
        this.cancelStr = cancelStr;
        this.confirmStr = confirmStr;
    }

    public AppSettingDialog show() {

        Intent intent = new Intent(context,PermissionAskToSettingActivity.class);
        intent.putExtra("titleStr",titleStr);
        intent.putExtra("reasonStr",reasonStr);
        intent.putExtra("cancelStr",cancelStr);
        intent.putExtra("confirmStr",confirmStr);
        context.startActivity(intent);

        return this;
    }


    public void onActivityResult(){
        PermissionCat.getInstance().onActivityResult(context);
    }

    public static class Builder {

        private Activity context;
        private String titleStr, reasonStr;
        private String cancelStr, confirmStr;

        public Builder(Activity context) {
            this.context = context;
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
            instance = new AppSettingDialog(context, titleStr, reasonStr, cancelStr, confirmStr);
            return instance;
        }

    }
}
