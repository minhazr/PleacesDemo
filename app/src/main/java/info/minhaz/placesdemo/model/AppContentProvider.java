package info.minhaz.placesdemo.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import static info.minhaz.placesdemo.model.SqlDbHelper.AppColumns;

import java.util.HashMap;

/**
 * Created by minhaz on 5/28/15.
 */
public class AppContentProvider extends ContentProvider {
    private static final String TAG=AppContentProvider.class.getSimpleName();
    public static final String AUTHORITY = AppContentProvider.class.getCanonicalName();
    private static final int SHARED_DATA =0;
    private static final int SHARED_DATA_ID =1;

    private static HashMap<String, String> sSharedDataProjectionMap;

    private static final UriMatcher uriMatcher;
    private SqlDbHelper dbHelper;
    @Override
    public boolean onCreate() {
        dbHelper=new SqlDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "Query Content provider with uri "+uri.toString());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)) {
            case SHARED_DATA:
                qb.setTables(SqlDbHelper.APP_TABLE);
                qb.setProjectionMap(sSharedDataProjectionMap);
                break;
            case SHARED_DATA_ID:
                qb.setTables(SqlDbHelper.APP_TABLE);
                qb.setProjectionMap(sSharedDataProjectionMap);
                qb.appendWhere("_id" + "=" + uri.getPathSegments().get(1));
                break;


        }
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;

    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * In case of listing if insert fails it will update
     * @param uri
     * @param initialValues
     * @return
     */
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        Log.d(TAG, "Inserting item in Content provider with Uri"+uri.toString());
        ContentValues values;
        Uri wordUri = null;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        long rowId=-1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case SHARED_DATA:
                rowId = db.insert(SqlDbHelper.APP_TABLE, AppColumns.PHONE, values);
                if (rowId > 0) {
                    wordUri = ContentUris.withAppendedId(AppTable.CONTENT_URI, rowId);
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (wordUri!=null) {
            notifyChange(uri);;
        }


        return wordUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db =  dbHelper.getWritableDatabase();;

        int count=0;
        String wordId=null;
        switch (uriMatcher.match(uri)) {
            case SHARED_DATA:
                count = db.delete(SqlDbHelper.APP_TABLE, selection, selectionArgs);
                break;
            case SHARED_DATA_ID:
                wordId = uri.getPathSegments().get(1);
                count = db.delete(SqlDbHelper.APP_TABLE, AppColumns._ID + "=" + wordId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        notifyChange(uri);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "updating item in Content provider with Uri"+uri.toString());
        SQLiteDatabase db =  dbHelper.getWritableDatabase();
        int count=0;
        String sql=null;
        switch (uriMatcher.match(uri)) {
            case SHARED_DATA:
                count = db.update(SqlDbHelper.APP_TABLE, values, selection, selectionArgs);
                break;
            case SHARED_DATA_ID:
                String wordId = uri.getPathSegments().get(1);
                count = db.update(SqlDbHelper.APP_TABLE, values, AppColumns._ID + "=" + wordId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                db.endTransaction();
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        notifyChange(uri);
        return count;
    }

    private void notifyChange(Uri uri){
        getContext().getContentResolver().notifyChange(uri, null);
    }


    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, SqlDbHelper.APP_TABLE, SHARED_DATA);
        uriMatcher.addURI(AUTHORITY, SqlDbHelper.APP_TABLE + "/#", SHARED_DATA_ID);

        sSharedDataProjectionMap = new HashMap<String, String>();
        sSharedDataProjectionMap.put(AppColumns._ID, AppColumns._ID);
        sSharedDataProjectionMap.put(AppColumns.PHONE, AppColumns.PHONE);
        sSharedDataProjectionMap.put(AppColumns.SEND, AppColumns.SEND);
        sSharedDataProjectionMap.put(AppColumns.RECEIVED, AppColumns.RECEIVED);

    }
    public static final class AppTable implements AppColumns {
        private static final String URL = "content://" + AUTHORITY + "/" + SqlDbHelper.APP_TABLE;
        public static final Uri CONTENT_URI = Uri.parse(URL);

    }

}
