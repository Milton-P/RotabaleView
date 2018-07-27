package com.milton.test.rotatableview;


import android.app.Activity;
import android.content.Context;
import android.view.View;


/**
 * Created by milton on 2018/7/12.
 */

public class testRotableWindow extends RotatableWindow{

    private Context mContext;

    public testRotableWindow(Activity context) {
        super(context);
    }

    @Override
    protected View getCustomViewView() {
        return View.inflate(mActivity.get(), R.layout.custom_view, null);
    }
}
