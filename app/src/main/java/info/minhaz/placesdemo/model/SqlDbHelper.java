package info.minhaz.placesdemo.model;
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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import info.minhaz.placesdemo.R;

/**
 * Created by minhaz on 5/28/15.
 */
public class SqlDbHelper extends SQLiteOpenHelper {
    private static final String TAG=SqlDbHelper.class.getCanonicalName();
    private static final String DATABASE_NAME = "app.db";
    public static final String APP_TABLE = "location";

    private final StringBuilder shred_location_table=new StringBuilder("CREATE TABLE IF NOT EXISTS "+ APP_TABLE +"( ")
            .append(AppObject.Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ")
            .append(AppObject.Columns.PHONE + "  CHAR, ")
            .append(AppObject.Columns.RECEIVED +"  CHAR, ")
            .append(AppObject.Columns.TIME +"  INTEGER, ")
            .append(AppObject.Columns.SEND +"  CHAR , ")
            .append("  UNIQUE ")
            .append("  ( ")
                .append(AppObject.Columns.PHONE)
            .append("  ) ")
            .append("  ON CONFLICT IGNORE ")
            .append(");");

    public SqlDbHelper(Context context) {
        super(context, DATABASE_NAME, null, context.getResources().getInteger(R.integer.DB_VERSION));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, shred_location_table.toString());
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", APP_TABLE));
        db.execSQL(shred_location_table.toString());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", APP_TABLE));

    }


}
