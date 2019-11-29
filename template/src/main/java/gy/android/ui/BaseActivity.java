package gy.android.ui;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import gy.android.R;
import gy.android.ui.util.FragmentManagerUtil;
import gy.android.ui.util.HandlerUtil;
import gy.android.ui.util.ProgressDialogUtil;
import gy.android.ui.util.ToastUtil;
import gy.android.util.LogUtil;

@Deprecated
public abstract class BaseActivity extends AppCompatActivity implements ActivityInitInterface {
    private static final boolean D = true;

    protected BaseActivity activity;
    protected Context context;
    protected Resources resources;

    protected HandlerUtil handlerUtil;
    private FragmentManagerUtil fragmentManagerUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        enterAndExitAnimation(true);
        super.onCreate(savedInstanceState);
        if (D) LogUtil.v(this, "onCreate");
        activity = this;
        context = activity;
        init();

        initContentView();
        initData(savedInstanceState);
        initView();
        initEvent();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (D) LogUtil.v(this, "onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (D) LogUtil.v(this, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (D) LogUtil.v(this, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (D) LogUtil.v(this, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (D) LogUtil.v(this, "onStop");
    }

    @Override
    protected void onDestroy() {
        unInit();
        super.onDestroy();
        if (D) LogUtil.v(this, "onDestroy");
    }

    @Override
    public void finish() {
        super.finish();
        enterAndExitAnimation(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        enterAndExitAnimation(false);
    }

    private void init() {
        fragmentManagerUtil = new FragmentManagerUtil(getSupportFragmentManager());
        resources = getResources();
        handlerUtil = new HandlerUtil();
    }

    private void unInit() {
        dismissProgressDialog();
        if (fragmentManagerUtil != null) {
            fragmentManagerUtil = null;
        }
        if (handlerUtil != null) {
            handlerUtil.release();
            handlerUtil = null;
        }
    }

    protected final void runOnUIThread(Runnable task) {
        runOnUIThread(task, 0);
    }

    protected final void runOnUIThread(Runnable task, final long duration) {
        handlerUtil.runOnUIThread(task, duration);
    }

    protected final void removeFromUIThread(Runnable task) {
        handlerUtil.removeFromUIThread(task);
    }

    protected final synchronized void queueEvent(Runnable task, long duration) {
        handlerUtil.queueEvent(task, duration);
    }

    protected final synchronized void removeEvent(Runnable task) {
        handlerUtil.removeEvent(task);
    }

    public final void replaceFragment(@IdRes int containerViewId, Fragment fragment, boolean isAdd2BackStack) {
        fragmentManagerUtil.replaceFragment(containerViewId, fragment, isAdd2BackStack);
    }

    public void showToast(String message) {
        ToastUtil.show(activity, message);
    }

    public void showProgressDialog(String title, String message) {
        ProgressDialogUtil.synchronizedShow(activity, title, message);
    }

    public void dismissProgressDialog() {
        ProgressDialogUtil.synchronizedDismiss(activity);
    }

    protected void enterAndExitAnimation(boolean enter) {
        if (enter) {
            overridePendingTransition(R.anim.set_translate_alpha_right_in_fast, R.anim.set_translate_stay);
        } else {
            overridePendingTransition(0, R.anim.set_translate_alpha_right_out_fast);
        }
    }
}
