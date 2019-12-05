package gy.android.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class FooterViewHolder extends RecyclerView.ViewHolder {

    public FooterViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public View inflaterFrom(ViewGroup parent, int layoutId) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }

    /**
     * {@link RecyclerViewAdapter}
     *
     * @param state
     */
    public abstract void onLoadStateChange(int state);
}
