package com.lza.pad.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.lza.pad.support.utils.Consts;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 14-9-18.
 */
public abstract class AbstractLoader<D> extends AsyncTaskLoader<D> implements Consts {

    protected D mData;
    protected Context mContext;
    protected Object[] mArgs;

    public AbstractLoader(Context context, Object... args) {
        super(context);
        mContext = context;
        if (args != null) {
            mArgs = args;
        }
    }

    @Override
    public abstract D loadInBackground();

    @Override
    public void deliverResult(D data) {
        if (isReset()) {
            if (data != null) {
                onReleaseResources(data);
            }
        }
        D oldData = mData;
        mData = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        if (oldData != null) {
            onReleaseResources(oldData);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        }

        if (mData == null) {
            forceLoad();
        }

        super.onStartLoading();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(D data) {
        super.onCanceled(data);
        onReleaseResources(data);
    }

    protected void onReleaseResources(D apps) {
        if (apps != null) {
            apps = null;
        }
    }
}
