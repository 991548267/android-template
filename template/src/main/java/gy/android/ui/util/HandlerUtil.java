package gy.android.ui.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class HandlerUtil {
    private final Handler mUIHandler = new Handler(Looper.getMainLooper());
    private final Thread mUIThread = mUIHandler.getLooper().getThread();

    private Handler mWorkHandler;
    private HandlerThread mWorkThread;

    public HandlerUtil() {
        initWorkHandler();
    }

    public void release() {
        releaseWorkHandler();
        removeAllFromUIThread();
    }

    public final void runOnUIThread(Runnable task) {
        runOnUIThread(task, 0);
    }

    public final void runOnUIThread(Runnable task, final long duration) {
        if (task == null) {
            return;
        }
        if (duration > 0 || Thread.currentThread() != mUIThread) {
            mUIHandler.postDelayed(task, duration);
        } else {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final void removeAllFromUIThread() {
        mUIHandler.removeCallbacksAndMessages(null);
    }


    public final void removeFromUIThread(Runnable task) {
        if (task == null) {
            return;
        }
        mUIHandler.removeCallbacks(task);
    }

    private void initWorkHandler() {
        if (mWorkHandler == null) {
            mWorkThread = new HandlerThread(HandlerUtil.class.getSimpleName());
            mWorkThread.start();
            mWorkHandler = new Handler(mWorkThread.getLooper());
        }
    }

    private void releaseWorkHandler() {
        if (mWorkHandler != null) {
            mWorkHandler.removeCallbacksAndMessages(null);
        }
        mWorkHandler = null;
        if (mWorkThread != null) {
            mWorkThread.quit();
        }
        mWorkThread = null;
    }

    public final synchronized void queueEvent(Runnable task) {
        queueEvent(task, 0);
    }

    public final synchronized void queueEvent(Runnable task, long duration) {
        if ((task == null) || (mWorkHandler == null)) {
            return;
        }
        try {
            mWorkHandler.removeCallbacks(task);
            if (duration > 0) {
                mWorkHandler.postDelayed(task, duration);
            } else if (mWorkThread.getThreadId() == Thread.currentThread().getId()) {
                task.run();
            } else {
                mWorkHandler.post(task);
            }
        } catch (Exception e) {
            // ignore
        }
    }

    public final synchronized void removeEvent(Runnable task) {
        if ((task == null) || (mWorkHandler == null)) {
            return;
        }
        try {
            mWorkHandler.removeCallbacks(task);
        } catch (Exception e) {
            // ignore
        }
    }
}
