package gy.android.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class HeaderViewHolder extends RecyclerView.ViewHolder {

    public HeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        onCreate(itemView);
    }

    public View inflaterFrom(ViewGroup parent, int layoutId) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }

    public abstract void onCreate(View parent);

    public abstract void onRefreshStateChange(int state);
}
