package gy.android.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gy.android.R;

public class SimpleRecyclerViewAdapter extends BaseRecyclerViewAdapter<String> {

    public static final int DIVIDER_VERTICAL = 0;
    public static final int DIVIDER_HORIZONTAL = 1;
    public static final int DIVIDER_GRID = 2;

    private int mDivider = DIVIDER_VERTICAL;

    public SimpleRecyclerViewAdapter(Context context, List<String> list) {
        this(context, list, DIVIDER_VERTICAL);
    }

    public SimpleRecyclerViewAdapter(Context context, List<String> list, int divider) {
        super(context, list);
        mDivider = divider;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (mDivider) {
            case DIVIDER_GRID:
                return new ViewHolder(inflaterFrom(parent, R.layout.item_simple_grid_recycler));
            case DIVIDER_HORIZONTAL:
                return new ViewHolder(inflaterFrom(parent, R.layout.item_simple_horizontal_recycler));
            case DIVIDER_VERTICAL:
            default:
                return new ViewHolder(inflaterFrom(parent, R.layout.item_simple_recycler));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).tv_text.setText(list.get(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView tv_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_text = itemView.findViewById(R.id.tv_text);
        }
    }
}
