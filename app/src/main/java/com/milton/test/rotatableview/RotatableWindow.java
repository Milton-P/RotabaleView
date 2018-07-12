package com.milton.test.rotatableview;

import android.content.Context;
import android.view.Gravity;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

/**
 * Created by data on 18-7-12.
 */

public class RotatableWindow extends PopupWindow  {
    private Context mContext;
    private int mCurrentOritation = -1;
    private int mLastOritation = -1;
    private View mRoot;
    private FrameLayout mContent;
    private ViewPropertyAnimator mViewPropertyAnimator;


    public RotatableWindow(Context context) {
        super(context);
        init(context);
    }

    public void show(View customView,View parent) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mContent.addView(customView, lp);
        setContentView(mRoot);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        showAtLocation(parent, Gravity.CENTER,0,0);
    }


    private void init(Context context) {
        mContext = context;
        mRoot = View.inflate(context, R.layout.rotatable_view, null);
        mContent = mRoot.findViewById(R.id.content);
        mViewPropertyAnimator = mContent.animate();
        OrientationListener orientationListener = new OrientationListener(context);
        orientationListener.enable();
    }

    private int getOritention(int orientation) {
        if (orientation < 20 || orientation >= 340) {
            return 0;
        } else if (orientation >= 70 && orientation < 110) {
            return 1;
        } else if (orientation >= 160 && orientation < 200) {
            return 2;
        } else if (orientation >= 250 && orientation < 290) {
            return 3;
        } else {
            return -1;
        }
    }

    private void updateView(int oritation) {
        mViewPropertyAnimator.cancel();
        switch (oritation) {
            case 0:
                mViewPropertyAnimator.rotation(0).setDuration(1000);
                break;
            case 1:
                mViewPropertyAnimator.rotation(-90).setDuration(1000);
                break;
            case 2:
                mViewPropertyAnimator.rotation(180).setDuration(1000);
            case 3:
                mViewPropertyAnimator.rotation(90).setDuration(1000);
                break;
        }
    }

    private class OrientationListener
            extends OrientationEventListener {
        public OrientationListener(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            mCurrentOritation = getOritention(orientation);
            if (mCurrentOritation != -1 && mCurrentOritation != mLastOritation) {
                mLastOritation = mCurrentOritation;
                updateView(mCurrentOritation);
            }
        }
    }

}
