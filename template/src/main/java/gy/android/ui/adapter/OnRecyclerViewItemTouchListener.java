package gy.android.ui.adapter;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView 梳理：点击&长按事件、分割线、拖曳排序、滑动删除 - 掘金
 * https://juejin.im/post/5a320ffcf265da43200342a3
 */

public abstract class OnRecyclerViewItemTouchListener implements RecyclerView.OnItemTouchListener {
    private RecyclerView recyclerView;
    private float distanceX, distanceY;
    private GestureDetectorCompat gestureDetector;
    private GestureDetector.SimpleOnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (view != null) {
                RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
                OnRecyclerViewItemTouchListener.this.onItemClick(viewHolder);
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (view != null) {
                RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
                OnRecyclerViewItemTouchListener.this.onItemLongClick(viewHolder);
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            OnRecyclerViewItemTouchListener.this.distanceX = distanceX;
            OnRecyclerViewItemTouchListener.this.distanceY = distanceY;
            OnRecyclerViewItemTouchListener.this.onScroll(distanceX, distanceY, e2.getAction());
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };

    public OnRecyclerViewItemTouchListener(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.gestureDetector = new GestureDetectorCompat(recyclerView.getContext(), onGestureListener);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        if (e.getAction() == MotionEvent.ACTION_UP) {
            OnRecyclerViewItemTouchListener.this.onScroll(distanceX, distanceY, e.getAction());
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        gestureDetector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // can not be call
    }

    public abstract void onItemClick(RecyclerView.ViewHolder viewHolder);

    public abstract void onItemLongClick(RecyclerView.ViewHolder viewHolder);

    public abstract void onScroll(float distanceX, float distanceY, int action);
}
