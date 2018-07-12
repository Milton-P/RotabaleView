package com.milton.test.rotatableview;

import android.content.Context;
import android.view.Gravity;
import android.view.OrientationEventListener;
import android.view.OrientationListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
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
    private static final int ANIMATION_SPEED = 270; // 270 deg/sec

    private int mCurrentDegree = 0; // [0, 359]
    private int mStartDegree = 0;
    private int mTargetDegree = 0;

    private boolean mClockwise = false, mEnableAnimation = true;

    private long mAnimationStartTime = 0;
    private long mAnimationEndTime = 0;

    private Runnable mRotateRunnable = new Runnable() {
        @Override
        public void run() {
            /*if (mCurrentDegree != mTargetDegree) {
                long time = AnimationUtils.currentAnimationTimeMillis();
                if (time < mAnimationEndTime) {
                    int deltaTime = (int) (time - mAnimationStartTime);
                    int degree = mStartDegree + ANIMATION_SPEED
                            * (mClockwise ? deltaTime : -deltaTime) / 1000;
                    degree = degree >= 0 ? degree % 360 : degree % 360 + 360;
                    mCurrentDegree = degree;
                    mContent.post(mRotateRunnable);
                } else {
                    mCurrentDegree = mTargetDegree;
                }
            }*/

            mContent.setRotation(-mCurrentDegree);
        }
    };


    public void setOrientation(int degree, boolean animation) {
        mEnableAnimation = animation;
        // make sure in the range of [0, 359]
        degree = degree >= 0 ? degree % 360 : degree % 360 + 360;
        if (degree == mTargetDegree) {
            return;
        }

        mTargetDegree = degree;
        /*if (mEnableAnimation) {
            mStartDegree = mCurrentDegree;
            mAnimationStartTime = AnimationUtils.currentAnimationTimeMillis();

            int diff = mTargetDegree - mCurrentDegree;
            diff = diff >= 0 ? diff : 360 + diff; // make it in range [0, 359]

            // Make it in range [-179, 180]. That's the shorted distance between the
            // two angles
            diff = diff > 180 ? diff - 360 : diff;

            mClockwise = diff >= 0;
            mAnimationEndTime = mAnimationStartTime
                    + Math.abs(diff) * 1000 / ANIMATION_SPEED;
        } else {
            mCurrentDegree = mTargetDegree;
        }*/
        mCurrentDegree = mTargetDegree;
        mContent.setRotation(-mCurrentDegree);
        //mContent.post(mRotateRunnable);
    }



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
        this.mContext = context;
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
                //setOrientation(0,true);
                mViewPropertyAnimator.rotation(0).setDuration(1000);
                break;
            case 1:
                //setOrientation(90,true);
                mViewPropertyAnimator.rotation(-90).setDuration(1000);
                break;
            case 2:
                //setOrientation(-180,true);
                mViewPropertyAnimator.rotation(180).setDuration(1000);
            case 3:
                //setOrientation(-90,true);
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
            android.util.Log.d("milton","kkkk orientation =  " +orientation);
            //setOrientation(orientation,false);
            mCurrentOritation = getOritention(orientation);
            if (mCurrentOritation != -1 && mCurrentOritation != mLastOritation) {
                mLastOritation = mCurrentOritation;
                updateView(mCurrentOritation);
            }
        }
    }


}
