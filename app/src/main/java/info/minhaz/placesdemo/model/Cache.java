package info.minhaz.placesdemo.model;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by mc7101 on 10/21/2016.
 */

public class Cache {

    static void insert(Context context, String phone, boolean send, String place){
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
            return null;
        if (cursor.moveToNext() ){
            return cursor;
        }
        return null;
    }
    static String get(Context context, String phone){
        String where= AppObject.Columns.PHONE+"='"+phone+"'";
        final String[] projection = {AppObject.Columns.PHONE};
        Cursor cursor=context.getContentResolver().query(AppContentProvider.AppTable.CONTENT_URI, projection, where, null, null);
        if (cursor==null )
            return null;
        if (cursor!=null && cursor.getCount()==0){
            cursor.close();
            return null;
        }
        String phone_number=null;
        if (cursor.moveToNext()){
            phone_number=cursor.getString(cursor.getColumnIndex(AppObject.Columns.PHONE));
            cursor.close();
        }
        return phone_number;
    }

    static int update(Context context, String phone, boolean send, String place){
        ContentValues values=new ContentValues();
        if (send){
            values.put(AppObject.Columns.SEND, place);
        }else{
            values.put(AppObject.Columns.RECEIVED, place);
        }
        values.put(AppObject.Columns.TIME, System.currentTimeMillis());
        String where= AppObject.Columns.PHONE+"='"+phone+"'";
        return context.getContentResolver().update(AppContentProvider.AppTable.CONTENT_URI, values, where, null);

    }
    static int delete(Context context, String phone){
        String where= AppObject.Columns.PHONE+"='"+phone+"'";
        return context.getContentResolver().delete(AppContentProvider.AppTable.CONTENT_URI,  where, null);
    }
}
