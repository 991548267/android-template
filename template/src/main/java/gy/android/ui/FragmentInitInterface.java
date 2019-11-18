package gy.android.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface FragmentInitInterface {

    void initData(@Nullable Bundle bundle);

    View initContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);

    void initView(View parent);

    void initEvent();
}
