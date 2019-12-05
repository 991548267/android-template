package gy.android.ui.attach;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Chron on 2017/12/28.
 */

@Deprecated
public class GridItemDecoration extends RecyclerView.ItemDecoration {
    private static final boolean D = true;

    private int spanCount;
    private int space;
    private boolean includeEdge;

    public GridItemDecoration(int spanCount, int space, boolean includeEdge) {
        this.spanCount = spanCount;
        this.space = space;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column

        if (includeEdge) {
            outRect.left = space - column * space / spanCount; // space - column * ((1f / spanCount) * space)
            outRect.right = (column + 1) * space / spanCount; // (column + 1) * ((1f / spanCount) * space)

            if (position < spanCount) { // top edge
                outRect.top = space;
            }
            outRect.bottom = space; // item bottom
        } else {
            outRect.left = column * space / spanCount; // column * ((1f / spanCount) * space)
            outRect.right = space - (column + 1) * space / spanCount; // space - (column + 1) * ((1f /    spanCount) * space)
            if (position >= spanCount) {
                outRect.top = space; // item top
            }
        }
    }
}
