package gy.android.ui.adapter;

import android.content.Context;
import android.content.res.Resources;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Chron on 2017/12/29.
 */

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter implements ItemClickListener {
    protected static boolean D = true;

    protected static final int VIEW_TYPE_DEFAULT = 0;

    protected Context context;
    protected Resources resources;
    protected List<T> list;
    protected int onItemClickPosition = -1;
    protected HashMap<Integer, Boolean> onMultipleItemClickPosition = new HashMap<>();
    protected OnListSizeChangeListener sizeChangeListener;
    protected OnMultipleCheckSizeChangeListener checkSizeChangeListener;

    public BaseRecyclerViewAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
        resources = context.getResources();
        registerAdapterDataObserver(mObserver);
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DEFAULT;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected boolean isDoubleCancel() {
        return false;
    }

    @Override
    public void onItemClick(View itemView, int position) {
        if (position < list.size()) {
            if (isDoubleCancel()) {
                if (position == onItemClickPosition) {
                    onItemClickPosition = -1;
                } else {
                    onItemClickPosition = position;
                }
            } else {
                onItemClickPosition = position;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onItemLongClick(View itemView, int position) {
        onItemClick(itemView, position);
    }

    @Override
    public void onMultipleItemClick(View itemView, int position) {
        if (isDoubleCancel()) {
            if (onMultipleItemClickPosition.containsKey(position)) {
                onMultipleItemClickPosition.remove(position);
            } else {
                onMultipleItemClickPosition.put(position, true);
            }
        } else {
            onMultipleItemClickPosition.put(position, true);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onMultipleItemLongClick(View itemView, int position) {
        onMultipleItemClick(itemView, position);
    }

    @Override
    public void onMultipleItemAllCheck(boolean check) {
        for (int i = 0; i < list.size(); i++) {
            if (check) {
                onMultipleItemClickPosition.put(i, true);
            } else {
                if (onMultipleItemClickPosition.containsKey(i)) {
                    onMultipleItemClickPosition.remove(i);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setCheckSizeChangeListener(OnMultipleCheckSizeChangeListener checkSizeChangeListener) {
        this.checkSizeChangeListener = checkSizeChangeListener;
    }

    public int getOnItemClickPosition() {
        return onItemClickPosition;
    }

    public HashMap<Integer, Boolean> getOnMultipleItemClickPosition() {
        return onMultipleItemClickPosition;
    }

    protected View inflaterFrom(ViewGroup parent, int layoutId) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }

    public void setSizeChangeListener(OnListSizeChangeListener sizeChangeListener) {
        this.sizeChangeListener = sizeChangeListener;
    }

    protected void itemCountChange(int count) {
        if (sizeChangeListener != null) {
            sizeChangeListener.onCountChange(count);
        }
    }

    protected void itemSizeAdd(int position, int count) {
        if (sizeChangeListener != null) {
            sizeChangeListener.onItemAdd(position, count);
        }
    }

    protected void itemSizeRemove(int position, int count) {
        if (sizeChangeListener != null) {
            sizeChangeListener.onItemRemove(position, count);
        }
    }

    private RecyclerView.AdapterDataObserver mObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            itemCountChange(getItemCount());
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
            onChanged();
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            onChanged();
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            onChanged();
            itemSizeRemove(positionStart, itemCount);
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            onChanged();
            itemSizeAdd(positionStart, itemCount);
        }

        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            onChanged();
        }
    };

    protected boolean isEmpty(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }
        return false;
    }

    public interface OnListSizeChangeListener {
        void onCountChange(int count);

        void onItemAdd(int position, int count);

        void onItemRemove(int position, int count);
    }

    public interface OnMultipleCheckSizeChangeListener {
        void onMultipleCheckSizeChange(int size);
    }
}
