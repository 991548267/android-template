package gy.android.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


import gy.android.R;
import gy.android.ui.util.FragmentManagerUtil;
import gy.android.ui.util.HandlerUtil;
import gy.android.ui.util.ProgressDialogUtil;
import gy.android.ui.util.ToastUtil;
import gy.android.util.LogUtil;

public abstract class BaseToolbarActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppCompatTextView tv_toolbar_title;
    private FrameLayout fl_content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        enterAndExitAnimation(true);
        super.onCreate(savedInstanceState);
        LogUtil.v(this, "onCreate");
        init();

        setContentView(R.layout.activity_base_toolbar);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        tv_toolbar_title.setText(getTitle());
        toolbar.setVisibility(hasToolbar() ? View.VISIBLE : View.GONE);

        fl_content = findViewById(R.id.fl_content);
        LayoutInflater.from(this).inflate(getContentViewId(), fl_content);

        initData(savedInstanceState);
        initView(fl_content);
        initEvent();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.v(this, "onStart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.v(this, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.v(this, "onResume");
    }

    @Override
    protected void onPause() {
        LogUtil.v(this, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        LogUtil.v(this, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unInit();
        LogUtil.v(this, "onDestroy");
        super.onDestroy();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                activity.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public abstract int getContentViewId();

    public abstract void initData(@Nullable Bundle bundle);

    public abstract void initView(View parent);

    public abstract void initEvent();

    // { toolbar
    protected void setToolbarTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return;
        }
        tv_toolbar_title.setText(title);
    }

    protected void setToolbarBackButton(boolean visible, int drawableId) {
        if (visible) {
            toolbar.setNavigationIcon(drawableId);
        } else {
            toolbar.setNavigationIcon(null);
        }
    }
    // } toolbar

    // { extra

    protected BaseToolbarActivity activity;
    protected Resources resources;

    protected HandlerUtil handlerUtil;
    protected FragmentManagerUtil fragmentManagerUtil;

    private void init() {
        activity = this;
        resources = activity.getResources();
        handlerUtil = new HandlerUtil();
        fragmentManagerUtil = new FragmentManagerUtil(getSupportFragmentManager());
    }

    private void unInit() {
        dismissProgressDialog();
        if (handlerUtil != null) {
            handlerUtil.release();
            handlerUtil = null;
        }
        fragmentManagerUtil = null;
        resources = null;
        activity = null;
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

    protected boolean useEnterAndExitAnimation() {
        return true;
    }

    protected boolean hasToolbar() {
        return true;
    }

    protected void enterAndExitAnimation(boolean enter) {
        if (!useEnterAndExitAnimation()) {
            return;
        }
        if (enter) {
            overridePendingTransition(R.anim.set_translate_alpha_right_in_fast, R.anim.set_translate_stay);
        } else {
            overridePendingTransition(0, R.anim.set_translate_alpha_right_out_fast);
        }
    }
    // } extra
}
