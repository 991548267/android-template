package gy.android.util;

import android.annotation.TargetApi;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HandlerThreadHandler extends Handler {
    private static final String TAG = "HandlerThreadHandler";
    private final long mId;

    public static final HandlerThreadHandler createHandler() {
        return createHandler("HandlerThreadHandler");
    }

    public static final HandlerThreadHandler createHandler(String name) {
        HandlerThread thread = new HandlerThread(name);
        thread.start();
        return new HandlerThreadHandler(thread.getLooper());
    }

    public static final HandlerThreadHandler createHandler(@Nullable Callback callback) {
        return createHandler("HandlerThreadHandler", callback);
    }

    public static final HandlerThreadHandler createHandler(String name, @Nullable Callback callback) {
        HandlerThread thread = new HandlerThread(name);
        thread.start();
        return new HandlerThreadHandler(thread.getLooper(), callback);
    }

    private HandlerThreadHandler(@NonNull Looper looper) {
        super(looper);
        Thread thread = looper.getThread();
        this.mId = thread != null ? thread.getId() : 0L;
    }

    private HandlerThreadHandler(@NonNull Looper looper, @Nullable Callback callback) {
        super(looper, callback);
        Thread thread = looper.getThread();
        this.mId = thread != null ? thread.getId() : 0L;
    }

    public long getId() {
        return this.mId;
    }

    @TargetApi(18)
    public void quitSafely() throws IllegalStateException {
        Looper looper = this.getLooper();
        if (looper != null) {
            looper.quitSafely();
        } else {
            throw new IllegalStateException("has no looper");
        }
    }

    public void quit() throws IllegalStateException {
        Looper looper = this.getLooper();
        if (looper != null) {
            looper.quit();
        } else {
            throw new IllegalStateException("has no looper");
        }
    }

    public boolean isCurrentThread() throws IllegalStateException {
        Looper looper = this.getLooper();
        if (looper != null) {
            return this.mId == Thread.currentThread().getId();
        } else {
            throw new IllegalStateException("has no looper");
        }
    }
}
