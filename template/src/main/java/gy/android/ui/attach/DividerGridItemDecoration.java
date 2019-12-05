package gy.android.ui.attach;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import gy.android.util.LogUtil;

/**
 * RecyclerView 梳理：点击&长按事件、分割线、拖曳排序、滑动删除 - 掘金
 * https://juejin.im/post/5a320ffcf265da43200342a3
 * <pre>
 *     mDividerGridItemDecoration = new DividerGridItemDecoration(recyclerView.getContext(), mGridLayoutManager.getSpanCount());
 *     recyclerView.addItemDecoration(mDividerGridItemDecoration);
 * </pre>
 */

public class DividerGridItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private Drawable divider;

    private int spanCount;
    private int offset;
    private boolean includeEdge;

    public DividerGridItemDecoration(Context context, int spanCount) {
        this(context, spanCount, false);
    }

    public DividerGridItemDecoration(Context context, int spanCount, boolean includeEdge) {
        this(context, spanCount, 0, 0, includeEdge);
    }

    /**
     * @param context
     * @param spanCount   分割线的列数
     * @param colorId     分割线颜色ID
     * @param offset      分割线宽度
     * @param includeEdge 是否包含边沿
     */
    public DividerGridItemDecoration(Context context, int spanCount, int colorId, int offset, boolean includeEdge) {
        this.spanCount = spanCount;
        this.offset = offset;
        this.includeEdge = includeEdge;
        try {
            divider = new ColorDrawable(ContextCompat.getColor(context, colorId));
        } catch (Resources.NotFoundException e) {
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            divider = a.getDrawable(0);
            if (divider == null) {
                LogUtil.w(DividerGridItemDecoration.this, "@android:attr/listDivider was not set in the theme used for this "
                        + "DividerItemDecoration. Please set that attribute all call setDrawable()");
            }
            this.offset = divider.getIntrinsicHeight();
            a.recycle();
        }
    }

    /**
     * onDraw 在 ItemView 内容之下绘制图形
     *
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    /**
     * onDrawOver 在 ItemView 内容之上绘制图形
     *
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    /**
     * getItemOffsets 撑开 ItemView 上、下、左、右四个方向的空间
     *
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column

        if (includeEdge) {
            outRect.left = offset - column * offset / spanCount; // offset - column * ((1f / spanCount) * offset)
            outRect.right = (column + 1) * offset / spanCount; // (column + 1) * ((1f / spanCount) * offset)

            if (position < spanCount) { // top edge
                outRect.top = offset;
            }
            outRect.bottom = offset; // item bottom
        } else {
            outRect.left = column * offset / spanCount; // column * ((1f / spanCount) * offset)
            outRect.right = offset - (column + 1) * offset / spanCount; // offset - (column + 1) * ((1f /    spanCount) * offset)
            if (position >= spanCount) {
                outRect.top = offset; // item top
            }
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin + offset;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + offset;
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);

            if (includeEdge) {
                int position = parent.getChildAdapterPosition(child);
                int column = position / spanCount; // item column

                if (column == 0) {
                    final int topLeft = child.getLeft() - params.leftMargin;
                    final int topRight = child.getRight() + params.rightMargin + offset;
                    final int topBottom = child.getTop() - params.topMargin;
                    final int topTop = topBottom - offset;
                    divider.setBounds(topLeft, topTop, topRight, topBottom);
                    divider.draw(c);
                }
            }
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + offset;

            divider.setBounds(left, top, right, bottom);
            divider.draw(c);

            if (includeEdge) {
                int position = parent.getChildAdapterPosition(child);
                int column = position % spanCount; // item column

                if (column == 0) {
                    final int leftTop = child.getTop() - params.topMargin - offset;
                    final int leftBottom = child.getBottom() + params.bottomMargin + offset;

                    final int leftRight = child.getLeft() - params.leftMargin;
                    final int leftLeft = leftRight - offset;
                    divider.setBounds(leftLeft, leftTop, leftRight, leftBottom);
                    divider.draw(c);
                }
            }
        }
    }
}
