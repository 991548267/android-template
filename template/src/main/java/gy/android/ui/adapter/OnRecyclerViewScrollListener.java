package gy.android.ui.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            int firstItemPosition = manager.findFirstCompletelyVisibleItemPosition();
            int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
            int itemCount = manager.getItemCount();
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

        isSlidingUpward = dy > 0;
        isSlidingLeft = dx > 0;
    }

    public abstract void onLoadMore();

    public abstract void onRefresh();
}
