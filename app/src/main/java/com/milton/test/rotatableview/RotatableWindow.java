package com.milton.test.rotatableview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import java.lang.ref.WeakReference;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public abstract class RotatableWindow implements View.OnTouchListener {
    protected WeakReference<Activity> mActivity;
    private View mActivityDecorView;
    private int mCurrentOritation = -1;
    private int mLastOritation = -1;
    private View mRootView;
    protected FrameLayout mCustomContent;
    private View mCustomView;
    private ViewPropertyAnimator mViewPropertyAnimator;

    private static final int ORITENTION_0 = 0;
    private static final int ORITENTION_90 = -90;
    private static final int ORITENTION_180 = 180;
    private static final int ORITENTION_270 = 90;
    private static final int ORITENTION_UNKNOW = -1;
    private static final int mAnimDuration = 330;

    private Context mContext;
    private WindowManager mWindowManager;

    private boolean mIsShowing;
    private boolean mIsTransitioningToDismiss;
    private boolean mIsDropdown;
    private RotatableWindowDecorView mDecorView;
    private View mBackgroundView;
    private boolean mBackEventEnabled = false;//是否监听back键
    private View.OnTouchListener mTouchInterceptor;
    private int mWidth = WindowManager.LayoutParams.WRAP_CONTENT;
    private int mHeight = WindowManager.LayoutParams.WRAP_CONTENT;
    private Drawable mBackground;
    private int mGravity = Gravity.NO_GRAVITY;
    // private OnDismissListener mOnDismissListener = () -> {
    //     onDestroy();
    // };

    private OnDismissListener mOnDismissListener = new OnDismissListener() {
        @Override
        public void onDismiss() {
            onDestroy();
        }
    };

    public void setTouchInterceptor(View.OnTouchListener l) {
        mTouchInterceptor = l;
    }

    public void setBackEventEnabled(boolean backEventEnabled) {
        this.mBackEventEnabled = backEventEnabled;
    }

    public boolean isShowing() {
        return mIsShowing;
    }

    private boolean isTransitioningToDismiss() {
        return mIsTransitioningToDismiss;
    }

    private void showWindow(View parent, int gravity) {
        if (isShowing() || mRootView == null) {
            return;
        }
        TransitionManager.endTransitions(mDecorView);
        mIsShowing = true;
        mIsDropdown = false;
        mGravity = gravity;
        final WindowManager.LayoutParams p = createLayoutParams(parent.getWindowToken());
        prepareWindow(p);
        p.x = 0;
        p.y = 0;
        invokeWindow(p);
    }

    private void prepareWindow(WindowManager.LayoutParams p) {
        if (mRootView == null || mContext == null || mWindowManager == null) {
            throw new IllegalStateException("You must specify a valid content view by "
                    + "calling setContentView() before attempting to show the popup.");
        }

        // The old decor view may be transitioning out. Make sure it finishes
        // and cleans up before we try to create another one.
        if (mDecorView != null) {
            mDecorView.cancelTransitions();
        }

        mBackgroundView = createBackgroundView(mRootView);
        mBackgroundView.setBackground(mBackground);

        mDecorView = createDecorView(mBackgroundView);

        // We may wrap that in another view, so we'll need to manually specify
        // the surface insets.
        //p.setSurfaceInsets(mBackgroundView, true /*manual*/, true /*preservePrevious*/);
    }

    private FrameLayout createBackgroundView(View contentView) {
        final ViewGroup.LayoutParams layoutParams = mRootView.getLayoutParams();
        final int height;
        if (layoutParams != null && layoutParams.height == WRAP_CONTENT) {
            height = WRAP_CONTENT;
        } else {
            height = MATCH_PARENT;
        }

        final FrameLayout backgroundView = new FrameLayout(mContext);
        final FrameLayout.LayoutParams listParams = new FrameLayout.LayoutParams(
                MATCH_PARENT, height);
        backgroundView.addView(contentView, listParams);

        return backgroundView;
    }

    private RotatableWindowDecorView createDecorView(View contentView) {
        final ViewGroup.LayoutParams layoutParams = mRootView.getLayoutParams();
        final int height;
        if (layoutParams != null && layoutParams.height == WRAP_CONTENT) {
            height = WRAP_CONTENT;
        } else {
            height = MATCH_PARENT;
        }
        final RotatableWindowDecorView decorView = new RotatableWindowDecorView(mContext);
        decorView.addView(contentView, MATCH_PARENT, height);
        decorView.setClipChildren(false);
        decorView.setClipToPadding(false);
        return decorView;
    }

    private void invokeWindow(WindowManager.LayoutParams p) {
        if (mContext != null) {
            p.packageName = mContext.getPackageName();
        }
        final RotatableWindowDecorView decorView = mDecorView;
        decorView.setFitsSystemWindows(false);
        mWindowManager.addView(decorView, p);
    }

    private int computeGravity() {
        int gravity = mGravity == Gravity.NO_GRAVITY ? Gravity.START | Gravity.TOP : mGravity;
        if (mIsDropdown) {
            gravity |= Gravity.DISPLAY_CLIP_VERTICAL;
        }
        return gravity;
    }

    private WindowManager.LayoutParams createLayoutParams(IBinder token) {
        final WindowManager.LayoutParams p = new WindowManager.LayoutParams();

        // These gravity settings put the view at the top left corner of the
        // screen. The view is then positioned to the appropriate location by
        // setting the x and y offsets to match the anchor's bottom-left
        // corner.
        p.gravity = computeGravity();
        p.flags = computeFlags(p.flags);
        p.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        p.token = token;
        p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED;
        p.windowAnimations = -1;
        p.format = mBackground.getOpacity();
        p.height = mHeight;
        p.width = mWidth;
        p.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        // p.privateFlags = PRIVATE_FLAG_WILL_NOT_REPLACE_ON_RELAUNCH
        //         | PRIVATE_FLAG_LAYOUT_CHILD_WINDOW_IN_PARENT_FRAME;
        return p;
    }

    private int computeFlags(int curFlags) {
        curFlags &= ~(
                WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
                        WindowManager.LayoutParams.FLAG_SPLIT_TOUCH);

        return curFlags;
    }

    public void dismiss() {
        if (!isShowing() || isTransitioningToDismiss()) {
            return;
        }
        final RotatableWindowDecorView decorView = mDecorView;
        final View contentView = mRootView;

        final ViewGroup contentHolder;
        final ViewParent contentParent = contentView.getParent();
        if (contentParent instanceof ViewGroup) {
            contentHolder = ((ViewGroup) contentParent);
        } else {
            contentHolder = null;
        }
        decorView.cancelTransitions();
        mIsShowing = false;
        mIsTransitioningToDismiss = true;
        dismissImmediate(decorView, contentHolder, contentView);
        mOnDismissListener.onDismiss();
    }

    private void dismissImmediate(View decorView, ViewGroup contentHolder, View contentView) {
        // If this method gets called and the decor view doesn't have a parent,
        // then it was either never added or was already removed. That should
        // never happen, but it's worth checking to avoid potential crashes.
        if (decorView.getParent() != null) {
            mWindowManager.removeViewImmediate(decorView);
        }

        if (contentHolder != null) {
            contentHolder.removeView(contentView);
        }

        // This needs to stay until after all transitions have ended since we
        // need the reference to cancel transitions in preparePopup().
        mDecorView = null;
        mBackgroundView = null;
        mIsTransitioningToDismiss = false;
    }

    private class RotatableWindowDecorView extends FrameLayout {

        public RotatableWindowDecorView(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                if (getKeyDispatcherState() == null) {
                    return super.dispatchKeyEvent(event);
                }

                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                    final KeyEvent.DispatcherState state = getKeyDispatcherState();
                    if (state != null) {
                        state.startTracking(event, this);
                    }
                    return true;
                } else if (event.getAction() == KeyEvent.ACTION_UP) {
                    final KeyEvent.DispatcherState state = getKeyDispatcherState();
                    if (state != null && state.isTracking(event) && !event.isCanceled()) {
                        if (mBackEventEnabled) {
                            dismiss();
                        }
                        return true;
                    }
                }
                return super.dispatchKeyEvent(event);
            } else {
                return super.dispatchKeyEvent(event);
            }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (mTouchInterceptor != null && mTouchInterceptor.onTouch(this, ev)) {
                return true;
            }
            return super.dispatchTouchEvent(ev);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            final int x = (int) event.getX();
            final int y = (int) event.getY();

            if ((event.getAction() == MotionEvent.ACTION_DOWN)
                    && ((x < 0) || (x >= getWidth()) || (y < 0) || (y >= getHeight()))) {
                dismiss();
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                dismiss();
                return true;
            } else {
                return super.onTouchEvent(event);
            }
        }

        public void cancelTransitions() {
            TransitionManager.endTransitions(this);
        }
    }

    public RotatableWindow(Activity context) {
        mActivity = new WeakReference<>(context);
        mContext = mActivity.get();
        initWindow(context);
    }

    public void show() {
        if (isShowing()) {
            return;
        }
        Window window = mActivity.get().getWindow();
        mActivityDecorView = window.getDecorView();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mWidth = mActivityDecorView.getWidth();
        mHeight = (int) (mActivityDecorView.getHeight() * 0.8);
        mCustomView = getCustomViewView();
        mCustomContent.addView(mCustomView, lp);
        setAdditionalFeature();
        showWindow(mActivityDecorView, Gravity.CENTER);
    }

    protected abstract View getCustomViewView();

    private void initWindow(Context context) {
        mRootView = View.inflate(context, R.layout.rotatable_view, null);
        mCustomContent = mRootView.findViewById(R.id.content);
        mViewPropertyAnimator = mCustomContent.animate();
        if (mWindowManager == null && mRootView != null) {
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        mBackground = new ColorDrawable(Color.TRANSPARENT);
        OrientationListener orientationListener = new OrientationListener(context);
        orientationListener.enable();
    }

    private int getOritention(int orientation) {
        orientation = (orientation + 360) % 360;
        if (orientation < 45 || orientation >= 315) {
            return ORITENTION_0;
        } else if (orientation >= 45 && orientation < 135) {
            return ORITENTION_90;
        } else if (orientation >= 135 && orientation < 225) {
            return ORITENTION_180;
        } else if (orientation >= 225 && orientation < 315) {
            return ORITENTION_270;
        } else {
            return ORITENTION_UNKNOW;
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
            if (mCurrentOritation != ORITENTION_UNKNOW && mCurrentOritation != mLastOritation) {
                mLastOritation = mCurrentOritation;
                mViewPropertyAnimator.rotation(mCurrentOritation).setDuration(mAnimDuration);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Rect rect = new Rect();
        // int x = (int) event.getRawX();
        // int y = (int) event.getRawY();
        // mCustomView.getBoundsOnScreen(rect);
        // if (rect.contains(x, y)) {
        //     return false;
        // }
        // return true;
        return false;
    }

    //设置额外配置,显示大小,是否拦截keyevent,touchEvent事件,是否监听back键
    protected void setAdditionalFeature() {
        mCustomContent.setPaddingRelative(47, 0, 47, 0);
        //setTouchInterceptor(this);
    }

    protected void onDestroy() {
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    protected final OnDismissListener getOnDismissListener() {
        return mOnDismissListener;
    }

    public interface OnDismissListener {
        void onDismiss();
    }

}
