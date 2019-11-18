package gy.android.ui.adapter;

import android.view.View;

public interface ItemClickListener {
    void onItemClick(View itemView, int position);

    void onItemLongClick(View itemView, int position);

    void onMultipleItemClick(View itemView, int position);

    void onMultipleItemLongClick(View itemView, int position);

    void onMultipleItemAllCheck(boolean check);
}
