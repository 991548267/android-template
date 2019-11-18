package gy.android.ui.util;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import gy.android.R;

/**
 * R.layout.toolbar
 */

public class ToolbarUtil {

    public void initToolbar(AppCompatActivity activity, String title) {

    }

    private AppCompatTextView tvToolbarTitle;
    private AppCompatTextView tvToolbarRight;
    private AppCompatImageView ivToolbarRight;

    private void initToolbar(Toolbar toolbar, String title) {
        if (toolbar == null) {
            return;
        }
        tvToolbarTitle = toolbar.findViewById(R.id.tv_toolbar_title);
        tvToolbarRight = toolbar.findViewById(R.id.tv_toolbar_right);
        ivToolbarRight = toolbar.findViewById(R.id.iv_toolbar_right);
    }
}
