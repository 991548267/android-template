package gy.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class PeruRecyclerHeaderView extends PeruFrameLayout {
    public PeruRecyclerHeaderView(@NonNull Context context) {
        this(context, null);
    }

    public PeruRecyclerHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onCreate(context);
    }

    public abstract void onCreate(@NonNull Context context);

    public abstract void refreshingReady();

    public abstract void refreshing();

    public abstract void refreshingCancel();

    public abstract void refreshCompleted();

    public abstract void refreshError();
}
