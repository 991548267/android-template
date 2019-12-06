package gy.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.ContentLoadingProgressBar;

import gy.android.R;

public class DefaultPeruRecyclerFooterView extends PeruRecyclerFooterView {

    public DefaultPeruRecyclerFooterView(@NonNull Context context) {
        super(context);
    }

    public DefaultPeruRecyclerFooterView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private ContentLoadingProgressBar pb_loading;
    private AppCompatTextView tv_toast;

    @Override
    public void onCreate(@NonNull Context context) {
        View view = inflaterFrom(context, R.layout.default_recycler_footer_view);
        pb_loading = view.findViewById(R.id.pb_loading);
        tv_toast = view.findViewById(R.id.tv_toast);
    }

    @Override
    public void loading() {
        pb_loading.setVisibility(VISIBLE);
        tv_toast.setVisibility(GONE);
    }

    @Override
    public void loadingCompleted() {
        pb_loading.setVisibility(GONE);
        tv_toast.setVisibility(GONE);
    }

    @Override
    public void loadingEnd() {
        pb_loading.setVisibility(GONE);
        tv_toast.setVisibility(VISIBLE);
        tv_toast.setText("没有更多了！");
    }

    @Override
    public void loadingError() {
        pb_loading.setVisibility(GONE);
        tv_toast.setVisibility(VISIBLE);
        tv_toast.setText("加载失败！");
    }
}
