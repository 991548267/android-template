package gy.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.ContentLoadingProgressBar;

import gy.android.R;

public class DefaultPeruRecyclerHeaderView extends PeruRecyclerHeaderView {

    public DefaultPeruRecyclerHeaderView(@NonNull Context context) {
        super(context);
    }

    public DefaultPeruRecyclerHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private ContentLoadingProgressBar pb_refreshing;
    private AppCompatTextView tv_toast;

    @Override
    public void onCreate(@NonNull Context context) {
        View view = inflaterFrom(context, R.layout.default_recycler_header_view);
        pb_refreshing = view.findViewById(R.id.pb_refreshing);
        tv_toast = view.findViewById(R.id.tv_toast);
    }

    @Override
    public void refreshingReady() {
        pb_refreshing.setVisibility(View.VISIBLE);
        tv_toast.setVisibility(View.GONE);
        pb_refreshing.setIndeterminate(false);
    }

    @Override
    public void refreshingCancel() {
        pb_refreshing.setVisibility(View.GONE);
        tv_toast.setVisibility(View.GONE);
    }

    @Override
    public void refreshing() {
        pb_refreshing.setVisibility(View.VISIBLE);
        tv_toast.setVisibility(View.GONE);
        pb_refreshing.setIndeterminate(true);
    }

    @Override
    public void refreshCompleted() {
        pb_refreshing.setVisibility(View.GONE);
        tv_toast.setVisibility(View.GONE);
    }

    @Override
    public void refreshError() {
        pb_refreshing.setVisibility(View.GONE);
        tv_toast.setVisibility(View.VISIBLE);
        tv_toast.setText("刷新失败！");
    }
}
