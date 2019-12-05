package gy.android.ui.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FOOTER = -2;
    private static final int TYPE_HEADER = -1;
    private static final int TYPE_ITEM = 0;

    // 正在加载
    public static final int LOADING = 1;
    // 加载完成
    public static final int LOADING_COMPLETE = 2;
    // 加载到底
    public static final int LOADING_END = 3;
    // 加载失败
    public static final int LOADING_FAILED = 4;

    // 正在刷新
    public static final int REFRESHING = 1;
    // 刷新完成
    public static final int REFRESHING_COMPLETE = 2;
    // 刷新错误
    public static final int REFRESHING_FAILED = 3;

    // 当前加载状态，默认为加载完成
    private int loadState = LOADING_COMPLETE;
    private FooterViewHolder footerViewHolder;

    // 当前刷新状态，默认为刷新完成
    private int refreshState = REFRESHING_COMPLETE;
    private HeaderViewHolder headerViewHolder;

    private RecyclerView.Adapter adapter;

    public RecyclerViewAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return headerViewHolder;
        } else if (viewType == TYPE_FOOTER) {
            return footerViewHolder;
        } else {
            return adapter.createViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                if (holder instanceof HeaderViewHolder) {
                    ((HeaderViewHolder) holder).onRefreshStateChange(refreshState);
                }
                break;
            case TYPE_FOOTER:
                if (holder instanceof FooterViewHolder) {
                    ((FooterViewHolder) holder).onLoadStateChange(loadState);
                }
                break;
            case TYPE_ITEM:
            default:
                adapter.onBindViewHolder(holder, position);
                break;
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    // 如果当前是footer的位置，那么该item占据2个单元格，正常情况下占据1个单元格
                    return getItemViewType(position) == TYPE_FOOTER ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        // 增加头布局和尾布局
        int count = adapter.getItemCount();
        if (headerViewHolder != null) {
            count++;
        }
        if (footerViewHolder != null) {
            count++;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            if (headerViewHolder == null) {
                return TYPE_ITEM;
            }
            return TYPE_HEADER;
        } else if (position + 1 == getItemCount()) {
            if (footerViewHolder == null) {
                return TYPE_ITEM;
            }
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }

    public void setFooterViewHolder(FooterViewHolder footerViewHolder) {
        this.footerViewHolder = footerViewHolder;
    }

    public void setRefreshState(int refreshState) {
        this.refreshState = refreshState;
        notifyDataSetChanged();
    }

    public void setHeaderViewHolder(HeaderViewHolder headerViewHolder) {
        this.headerViewHolder = headerViewHolder;
    }
}
