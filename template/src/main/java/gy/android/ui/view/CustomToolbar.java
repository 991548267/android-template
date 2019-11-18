package gy.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;

import androidx.annotation.IdRes;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import gy.android.R;

public class CustomToolbar extends Toolbar {

    private Resources resources;

    public CustomToolbar(Context context) {
        this(context, null);
    }

    public CustomToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resources = getResources();
        setBackground(resources.getDrawable(R.drawable.shape_rectangle_gradient_dodger_deepsky));
    }

    private AppCompatTextView mTitleTextView;

    @Override
    public void setTitle(@IdRes int resId) {
        setTitle(resources.getString(resId));
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }
}
