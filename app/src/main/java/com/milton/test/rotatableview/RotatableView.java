package com.milton.test.rotatableview;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by data on 18-7-11.
 */

public class RotatableView extends LinearLayout {
    private Context mContext;
    protected WindowManager mWindowManager;
    private int mCurrentOritation = -1;
    private int mLastOritation = -1;
    private View mRoot;
    private FrameLayout mContent;
    private ViewPropertyAnimator mViewPropertyAnimator;
    private static RotatableView self;

    public static RotatableView getInstance(Context context) {
        if (self == null) {
            self = new RotatableView(context);
        }
        return self;
    }

    private RotatableView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mRoot = View.inflate(context, R.layout.rotatable_view, this);
        mContent = mRoot.findViewById(R.id.content);
        mViewPropertyAnimator = mContent.animate();
        OrientationListener orientationListener = new OrientationListener(context);
        orientationListener.enable();
    }


    public boolean show(View customView) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setGravity(Gravity.CENTER);
        if (mRoot.getParent() == null) {
            mContent.addView(customView, lp);
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_TOAST);
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        mWindowManager.addView(this, layoutParams);
        return true;
    }

    public void dismiss() {
        if (null != mWindowManager) {
            mWindowManager.removeViewImmediate(this);
        }
        self = null;
        mWindowManager = null;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        android.util.Log.d("milton", "teset");
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

}
