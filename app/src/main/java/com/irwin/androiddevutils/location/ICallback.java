package com.irwin.androiddevutils.location;

/**
 * Created by Irwin on 2017/8/9.
 * <p>Callback for asynchronous task.
 */

public interface ICallback<RESULT> {

    /**
     * Called back on asynchronous task success.
     *
     * @param result
     */
    void onSuccess(RESULT result);

    /**
     * Called back on asynchronous task fail.
     *
     * @param info
     */
    void onFail(Throwable info);
}

