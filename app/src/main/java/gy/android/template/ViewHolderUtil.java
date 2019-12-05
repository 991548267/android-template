package gy.android.template;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gy.android.ui.adapter.HeaderViewHolder;

public class ViewHolderUtil {

    public static HeaderViewHolder getHeaderViewHolder(ViewGroup parent, int layoutId) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        HeaderViewHolder holder = new HeaderViewHolder() {

            @Override
            public void onCreate(View parent) {

            }

            @Override
            public void onRefreshStateChange(int state) {

            }
        };
        return holder;
    }
}
