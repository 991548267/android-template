package gy.android.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;

public interface ActivityInitInterface {

    void initData(@Nullable Bundle bundle);

    void initContentView();

    void initView();

    void initEvent();
}
