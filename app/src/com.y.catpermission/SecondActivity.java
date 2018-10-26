package com.y.catpermission;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.y.catpermission.base.BaseActivity;

public class SecondActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

    }

    public void btn1(View view){
        finish();
    }
}
