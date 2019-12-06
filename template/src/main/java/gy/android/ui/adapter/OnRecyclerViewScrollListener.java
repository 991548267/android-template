package gy.android.ui.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import gy.android.util.LogUtil;

public abstract class OnRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    private boolean isSlidingUpward = false;
    private boolean isSlidingLeft = false;

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (manager == null) {
            return;
        }
        LogUtil.v(OnRecyclerViewScrollListener.this, "onScrollStateChanged newState:" + newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            int firstItemPosition = manager.findFirstCompletelyVisibleItemPosition();
            int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
            int itemCount = manager.getItemCount();
            LogUtil.v(OnRecyclerViewScrollListener.this, "onScrollStateChanged first:" + firstItemPosition +
                    " last:" + lastItemPosition + " itemCount:" + itemCount);
            LogUtil.v(OnRecyclerViewScrollListener.this, "onScrollStateChanged isSlidingUpward:" + isSlidingUpward
                    + " isSlidingLeft:" + isSlidingLeft);
            if (lastItemPosition + 1 == itemCount && isSlidingUpward) {
                onLoadMore();
            } else if (firstItemPosition == 0 && isSlidingLeft) {
                onRefresh();
            }
        }
    }

    /**
     * dx > 0  -- > left
     * dx < 0  -- > right
     * dy > 0  -- > top
     * dy < 0  -- > bottom
     *
     * @param recyclerView
     * @param dx
     * @param dy
     */
    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        LogUtil.v(OnRecyclerViewScrollListener.this, "onScrolled dx:" + dx + " dy:" + dy);

        isSlidingUpward = dy > 0;
        isSlidingLeft = dx > 0;
    }

    public abstract void onLoadMore();

    public abstract void onRefresh();
}
