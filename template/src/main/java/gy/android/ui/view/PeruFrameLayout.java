package gy.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PeruFrameLayout extends FrameLayout {

    public PeruFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public PeruFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    protected View inflaterFrom(Context context, int layoutId) {
        return LayoutInflater.from(context).inflate(layoutId, this, false);
    }
}
