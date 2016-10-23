package info.minhaz.placesdemo.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by mc7101 on 10/21/2016.
 */

public class Cache {

    public static void insert(Context context, String phone, boolean send, String place){
        ContentValues values=new ContentValues();
        values.put(AppObject.Columns.PHONE, phone);
        if (send){
            values.put(AppObject.Columns.SEND, place);
        }else{
            values.put(AppObject.Columns.RECEIVED, place);
        }
        values.put(AppObject.Columns.TIME, System.currentTimeMillis());

        context.getContentResolver().insert(AppContentProvider.AppTable.CONTENT_URI, values);
    }

    public static Cursor get(Context context){
        String sort= AppObject.Columns.TIME+ " ASC";
        Cursor cursor=context.getContentResolver().query(AppContentProvider.AppTable.CONTENT_URI, null, null, null, sort);
        if (cursor==null )
            return cursor;
        if (cursor.moveToNext()){
            return cursor;
        }
        return null;
    }

    public static int update(Context context, String phone, boolean send, String place){
        ContentValues values=new ContentValues();
        if (send){
            values.put(AppObject.Columns.SEND, place);
        }else{
            values.put(AppObject.Columns.RECEIVED, place);
        }
        String where= AppObject.Columns.PHONE+"='"+phone+"'";
        return context.getContentResolver().update(AppContentProvider.AppTable.CONTENT_URI, values, where, null);

    }
    public static int delete(Context context, String phone){
        String where= AppObject.Columns.PHONE+"='"+phone+"'";
        return context.getContentResolver().delete(AppContentProvider.AppTable.CONTENT_URI,  where, null);
    }
}
