package com.irwin.androiddevutils.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

;

/**
 * Created by Irwin on 2017/8/11.
 */

public class ThreadExecutor {

    private static ThreadExecutor INSTANCE;

    private int mPoolSize = 2;

    private Handler mHandler;

    private ScheduledExecutorService mExecutor;

    public static ThreadExecutor getInstance() {
        if (INSTANCE == null) {
            synchronized (ThreadExecutor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ThreadExecutor();
                }
            }
        }
        return INSTANCE;
    }

    private ThreadExecutor() {
    }

    /**
     * Set pool size of thread.
     *
     * @param poolSize
     */
    public void setPoolSize(int poolSize) {
        if (poolSize > 0) {
            mPoolSize = poolSize;
        }
    }

    /**
     * Execute a runnable.
     *
     * @param task
     */
    public void execute(Runnable task) {
        ensureExecutor();
        mExecutor.execute(task);
    }

    /**
     * Submit a runnable.
     *
     * @param task
     * @return a Future representing pending completion of the task
     */
    public Future<?> submit(Runnable task) {
        ensureExecutor();
        return mExecutor.submit(task);
    }

    /**
     * Execute a runnable on UI thread.
     *
     * @param task
     */
    public void executeOnUI(Runnable task) {
        ensureHandler();
        mHandler.post(task);
    }

    /**
     * Execute a runnable on UI thread after a specify time.
     *
     * @param task
     * @param delay Time in milliseconds to before execution.
     */
    public void executeOnUIDelay(Runnable task, long delay) {
        ensureHandler();
        mHandler.postDelayed(task, delay);
    }

    /**
     * Execute a runnable on UI thread after a specify time.
     *
     * @param task
     * @param delay Time in milliseconds to before execution.
     */
    public void schedule(Runnable task, long delay) {
        schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Execute a runnable on UI thread after a specify time.
     *
     * @param task
     * @param delay
     * @param timeUnit
     */
    public void schedule(Runnable task, long delay, TimeUnit timeUnit) {
        ensureExecutor();
        mExecutor.schedule(task, delay, timeUnit);
    }

    public Executor getExecutor() {
        ensureExecutor();
        return mExecutor;
    }

    public Handler getHandler() {
        ensureHandler();
        return mHandler;
    }

    private void ensureExecutor() {
        if (mExecutor == null) {
            synchronized (ThreadExecutor.class) {
                if (mExecutor == null) {
                    mExecutor = Executors.newScheduledThreadPool(mPoolSize);
                }
            }
        }
    }

    private void ensureHandler() {
        if (mHandler == null) {
            synchronized (ThreadExecutor.class) {
                if (mHandler == null) {
                    mHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
    }


}
