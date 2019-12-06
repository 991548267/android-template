package gy.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class PeruRecyclerFooterView extends PeruFrameLayout {
    public PeruRecyclerFooterView(@NonNull Context context) {
        this(context, null);
    }

    public PeruRecyclerFooterView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onCreate(context);
    }

    public abstract void onCreate(@NonNull Context context);

    public abstract void loading();

    public abstract void loadingCompleted();

    public abstract void loadingEnd();

    public abstract void loadingError();
}
