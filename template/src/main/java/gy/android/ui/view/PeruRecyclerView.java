package gy.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import gy.android.ui.adapter.OnRecyclerViewItemTouchListener;
import gy.android.ui.adapter.RecyclerViewAdapter;

/**
 * 20191206 created by Chron
 * setOnItemClickListener --20191206
 * setOnItemLongClickListener --20191206
 */
public class PeruRecyclerView extends RecyclerView {

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnRefreshListener onRefreshListener;

    private PeruRecyclerHeaderView headerView;
    private PeruRecyclerFooterView footerView;

    public PeruRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public PeruRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PeruRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        RecyclerViewAdapter viewAdapter = new RecyclerViewAdapter(adapter);
        super.setAdapter(viewAdapter);
        // setAdapter后进行listener添加操作
        OnRecyclerViewItemTouchListener itemTouchListener = new OnRecyclerViewItemTouchListener(this) {
            @Override
            public void onItemClick(ViewHolder viewHolder) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(viewHolder);
                }
            }

            @Override
            public void onItemLongClick(ViewHolder viewHolder) {
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onItemLongClick(viewHolder);
                }
            }

            @Override
            public void onScroll(float distanceX, float distanceY, int action) {
                onScrollChange(distanceX, distanceY, action);
            }
        };
        addOnItemTouchListener(itemTouchListener);
    }

    private void onScrollChange(float distanceX, float distanceY, int action) {
        // 不存在头布局和尾布局时，不进行操作
        if ((headerView == null && footerView == null) || getLayoutManager() == null) {
            return;
        }
        if (getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager manager = (LinearLayoutManager) getLayoutManager();
            if (manager.getOrientation() == LinearLayoutManager.VERTICAL) {
                int firstItemPosition = manager.findFirstCompletelyVisibleItemPosition();
                int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
                int itemCount = manager.getItemCount();
                if (distanceY < 0 && firstItemPosition == 0 && headerView != null) {
                    switch (action) {
                        case MotionEvent.ACTION_MOVE:
                            headerView.refreshingReady();
                            break;
                        case MotionEvent.ACTION_UP:
                            if (distanceY < -1) {
                                headerView.refreshing();
                            } else {
                                headerView.refreshingCancel();
                            }
                            break;
                    }
                } else if (distanceY > 1 && lastItemPosition + 1 == itemCount && footerView != null) {
                    footerView.loading();
                }
            }
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager manager = (GridLayoutManager) getLayoutManager();
            // GridLayoutManager not completed
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public interface OnItemClickListener {
        void onItemClick(RecyclerView.ViewHolder viewHolder);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(RecyclerView.ViewHolder viewHolder);
    }

    public interface OnRefreshListener {
        void refreshingReady();

        void refreshing();

        void refreshingCancel();
    }
}
