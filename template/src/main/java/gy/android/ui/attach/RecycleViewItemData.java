package gy.android.ui.attach;

public class RecycleViewItemData<T> {
    T t;
    int type;

    public RecycleViewItemData(T t) {
        this(t, 0);
    }

    public RecycleViewItemData(T t, int type) {
        this.t = t;
        this.type = type;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
