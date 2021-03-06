package info.minhaz.placesdemo;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.content.CursorLoader;
import android.util.Log;


import info.minhaz.placesdemo.model.AppContentProvider;
import info.minhaz.placesdemo.model.Cache;

/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <minhaz@minhaz.info> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Minhaz Rafi Chowdhury
 *
 * You may obtain a copy of the License at
 *
 * https://fedoraproject.org/wiki/Licensing/Beerware
 * ----------------------------------------------------------------------------
 */
public class LocationLoader extends CursorLoader {
    public static final String TAG=LocationLoader.class.getSimpleName();
    private ConversationObserver mObserver;
    private Cursor result;

    public LocationLoader(Context context){
        super(context);

    }

    @Override
    public Cursor loadInBackground() {

        Cursor cursor= Cache.get(getContext());
        if (cursor!=null){
            cursor.registerContentObserver(mObserver);
            cursor.setNotificationUri(getContext().getContentResolver(), AppContentProvider.AppTable.CONTENT_URI);
        }
        return cursor;
    }
    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();

        if (mObserver!=null && result!=null){
            result.unregisterContentObserver(mObserver);
            mObserver=null;
        }
        result=null;

    }
    @Override
    public void deliverResult(Cursor data) {
        Log.d("minhaz", "Deliver result");
        if (isReset()) {
            releaseResources(result);
            return;
        }
        Cursor oldData = result;
        result=data;
        if (isStarted())
            super.deliverResult(data);
        if ((oldData != null) && (oldData != data)) {
            releaseResources(oldData);
        }

    }
    @Override
    protected void onStartLoading() {
        if (result!=null) {
            deliverResult(result);
        }
        if (mObserver==null)
            mObserver=new ConversationObserver();

        forceLoad();
    }
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    private void releaseResources(Cursor data) {
        data.close();
    }



    private class ConversationObserver extends ContentObserver {

        public ConversationObserver() {
            super(new Handler());
            // TODO Auto-generated constructor stub
        }
        @Override
        public void onChange(boolean selfChange) {
            onContentChanged();
        }

    }

}
