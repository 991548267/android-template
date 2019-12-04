package gy.android.template;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import gy.android.ui.attach.DividerGridItemDecoration;
import gy.android.ui.attach.DividerListItemDecoration;
import gy.android.ui.adapter.OnRecyclerViewItemClickListener;
import gy.android.ui.adapter.SimpleRecyclerViewAdapter;
import gy.android.ui.BaseToolbarActivity;
import gy.android.ui.attach.GridItemDecoration;

public class RecyclerViewActivity extends BaseToolbarActivity {

    private RecyclerView rv_recycler;
    private List<String> recyclerView_list = new ArrayList<>();
    private SimpleRecyclerViewAdapter recyclerView_adapter;
    private LinearLayoutManager rv_recycler_manager;
    private RecyclerView.ItemDecoration rv_recycler_divider;

    private RecyclerView rv_horizontal_recycler;
    private List<String> rv_horizontal_recycler_list = new ArrayList<>();
    private SimpleRecyclerViewAdapter rv_horizontal_recycler_adapter;
    private LinearLayoutManager rv_horizontal_recycler_manager;
    private RecyclerView.ItemDecoration rv_horizontal_recycler_divider;

    private RecyclerView rv_grid_recycler;
    private List<String> rv_grid_recycler_list = new ArrayList<>();
    private SimpleRecyclerViewAdapter rv_grid_recycler_adapter;
    private GridLayoutManager rv_grid_recycler_manager;
    private RecyclerView.ItemDecoration rv_grid_recycler_divider;

    @Override
    public int getContentViewId() {
        return R.layout.activity_recycler_view;
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        recyclerView_list.add(getString(R.string.tv_recycler_demo_item));
        recyclerView_list.add(getString(R.string.tv_recycler_demo_item));
        recyclerView_list.add(getString(R.string.tv_recycler_demo_item));
        recyclerView_list.add(getString(R.string.tv_recycler_demo_item));
        recyclerView_list.add(getString(R.string.tv_recycler_demo_item));
        recyclerView_adapter = new SimpleRecyclerViewAdapter(activity, recyclerView_list);
        rv_recycler_manager = new LinearLayoutManager(activity);
        rv_recycler_divider = new DividerListItemDecoration(activity,
                rv_recycler_manager.getOrientation(), R.color.Orchid, 2);

        rv_horizontal_recycler_list.add(getString(R.string.tv_recycler_demo_item));
        rv_horizontal_recycler_list.add(getString(R.string.tv_recycler_demo_item));
        rv_horizontal_recycler_list.add(getString(R.string.tv_recycler_demo_item));
        rv_horizontal_recycler_list.add(getString(R.string.tv_recycler_demo_item));
        rv_horizontal_recycler_list.add(getString(R.string.tv_recycler_demo_item));
        rv_horizontal_recycler_adapter = new SimpleRecyclerViewAdapter(activity, rv_horizontal_recycler_list, SimpleRecyclerViewAdapter.DIVIDER_HORIZONTAL);
        rv_horizontal_recycler_manager = new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, true);
        rv_horizontal_recycler_divider = new DividerListItemDecoration(activity,
                rv_horizontal_recycler_manager.getOrientation(), R.color.Beige, 25);

        rv_grid_recycler_list.add(getString(R.string.ok));
        rv_grid_recycler_list.add(getString(R.string.ok));
        rv_grid_recycler_list.add(getString(R.string.ok));
        rv_grid_recycler_list.add(getString(R.string.ok));
        rv_grid_recycler_list.add(getString(R.string.ok));
        rv_grid_recycler_list.add(getString(R.string.ok));
        rv_grid_recycler_list.add(getString(R.string.ok));
        rv_grid_recycler_list.add(getString(R.string.ok));
        rv_grid_recycler_list.add(getString(R.string.ok));
        rv_grid_recycler_list.add(getString(R.string.ok));
        rv_grid_recycler_adapter = new SimpleRecyclerViewAdapter(activity, rv_grid_recycler_list, SimpleRecyclerViewAdapter.DIVIDER_GRID);
        rv_grid_recycler_manager = new GridLayoutManager(activity, 3);
        rv_grid_recycler_divider = new DividerGridItemDecoration(activity, rv_grid_recycler_manager.getSpanCount(),
                R.color.Orchid, 5, true);
    }

    @Override
    public void initView(View parent) {
        rv_recycler = parent.findViewById(R.id.rv_recycler);
        rv_recycler.setLayoutManager(rv_recycler_manager);
        rv_recycler.addItemDecoration(rv_recycler_divider);
        rv_recycler.setAdapter(recyclerView_adapter);

        rv_horizontal_recycler = parent.findViewById(R.id.rv_horizontal_recycler);
        rv_horizontal_recycler.setLayoutManager(rv_horizontal_recycler_manager);
        rv_horizontal_recycler.addItemDecoration(rv_horizontal_recycler_divider);
        rv_horizontal_recycler.setAdapter(rv_horizontal_recycler_adapter);

        rv_grid_recycler = parent.findViewById(R.id.rv_grid_recycler);
        rv_grid_recycler.setLayoutManager(rv_grid_recycler_manager);
        rv_grid_recycler.addItemDecoration(rv_grid_recycler_divider);
        rv_grid_recycler.setAdapter(rv_grid_recycler_adapter);
    }

    @Override
    public void initEvent() {
        rv_recycler.addOnItemTouchListener(new OnRecyclerViewItemClickListener(rv_recycler) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder) {
                showToast("onItemClick: " + viewHolder.getAdapterPosition());
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder viewHolder) {
                showToast("onItemLongClick: " + viewHolder.getAdapterPosition());
            }
        });
    }
}
