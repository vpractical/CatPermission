package com.y.catpermission.base;

import android.content.Intent;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
