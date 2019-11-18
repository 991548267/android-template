package gy.android.ui;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gy.android.ui.util.FragmentManagerUtil;
import gy.android.ui.util.HandlerUtil;
import gy.android.ui.util.ProgressDialogUtil;
import gy.android.ui.util.ToastUtil;
import gy.android.util.LogUtil;

public abstract class BaseFragment extends Fragment implements FragmentInitInterface {

    protected static final String TAG = BaseFragment.class.getSimpleName();
    private static final boolean D = true;

    private HandlerUtil handlerUtil;
    private FragmentManagerUtil fragmentManagerUtil;

    protected FragmentActivity activity;
    protected Fragment fragment;
    protected Context context;
    protected Resources resources;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (D) LogUtil.v(BaseFragment.this, "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (D) LogUtil.v(BaseFragment.this, "onCreate");
        handlerUtil = new HandlerUtil();
        fragmentManagerUtil = new FragmentManagerUtil(getFragmentManager());
        activity = getActivity();
        fragment = this;
        resources = getResources();
        initData(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (D) LogUtil.v(BaseFragment.this, "onCreateView");
        View parent = initContentView(inflater, container);
        initView(parent);
        return parent;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (D) LogUtil.v(BaseFragment.this, "onViewCreated");
        initEvent();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (D) LogUtil.v(BaseFragment.this, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (D) LogUtil.v(BaseFragment.this, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (D) LogUtil.v(BaseFragment.this, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (D) LogUtil.v(BaseFragment.this, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (D) LogUtil.v(BaseFragment.this, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        handlerUtil.release();
        super.onDestroy();
        if (D) LogUtil.v(BaseFragment.this, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (D) LogUtil.v(BaseFragment.this, "onDetach");
    }

    protected final void runOnUIThread(Runnable task) {
        handlerUtil.runOnUIThread(task);
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

    protected void replaceFragment(int containerID, Fragment fragment, boolean isAddToBackStack) {
        fragmentManagerUtil.replaceFragment(containerID, fragment, isAddToBackStack);
    }

    protected void showToast(String message) {
        ToastUtil.show(activity, message);
    }

    protected void showProgressDialog(String title, String message) {
        ProgressDialogUtil.synchronizedShow(activity, title, message);
    }

    protected void dismissProgressDialog() {
        ProgressDialogUtil.synchronizedDismiss(activity);
    }
}
