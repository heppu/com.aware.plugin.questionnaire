package com.aware.plugin.questionnaire;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;

import com.aware.Aware;
import com.aware.utils.DatabaseHelper;

public class Provider extends ContentProvider {

    public static final int DATABASE_VERSION = 1;

    /**
     * Provider authority: com.aware.plugin.questionnaire.provider.questionnaire
     */

    public static String AUTHORITY = "com.aware.plugin.questionnaire.provider.questionnaire";

    private static final int QUESTIONNAIRE = 1;
    private static final int QUESTIONNAIRE_ID = 2;

    public static final String DATABASE_NAME = Environment.getExternalStorageDirectory() + "/AWARE/plugin_questionnaire.db";

    public static final String[] DATABASE_TABLES = {
            "plugin_questionnaire"
    };

    public static final String[] TABLES_FIELDS = {
                    Questionnaire_Data._ID + " integer primary key autoincrement," +
                    Questionnaire_Data.TIMESTAMP + " real default 0," +
                    Questionnaire_Data.DEVICE_ID + " text default ''," +
                    "UNIQUE("+Questionnaire_Data.TIMESTAMP+","+Questionnaire_Data.DEVICE_ID+")"
    };

    public static final class Questionnaire_Data implements BaseColumns {
        private Questionnaire_Data(){};

        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/plugin_questionnaire");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.plugin.questionnaire";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.plugin.questionnaire";

        public static final String _ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";
    }

    private static UriMatcher URIMatcher;
    private static HashMap<String, String> databaseMap;
    private static DatabaseHelper databaseHelper;
    private static SQLiteDatabase database;

    @Override
    public boolean onCreate() {

        AUTHORITY = getContext().getPackageName() + ".provider.questionnaire";

        URIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        URIMatcher.addURI(AUTHORITY, DATABASE_TABLES[0], QUESTIONNAIRE);
        URIMatcher.addURI(AUTHORITY, DATABASE_TABLES[0]+"/#", QUESTIONNAIRE_ID);

        databaseMap = new HashMap<String, String>();
        databaseMap.put(Questionnaire_Data._ID, Questionnaire_Data._ID);
        databaseMap.put(Questionnaire_Data.TIMESTAMP, Questionnaire_Data.TIMESTAMP);
        databaseMap.put(Questionnaire_Data.DEVICE_ID, Questionnaire_Data.DEVICE_ID);

        return true;
    }

    private boolean initializeDB() {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper( getContext(), DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLES, TABLES_FIELDS );
        }
        if( databaseHelper != null && ( database == null || ! database.isOpen() )) {
            database = databaseHelper.getWritableDatabase();
        }
        return( database != null && databaseHelper != null);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if( ! initializeDB() ) {
            Log.w(AUTHORITY,"Database unavailable...");
            return 0;
        }

        int count = 0;
        switch (URIMatcher.match(uri)) {
            case QUESTIONNAIRE:
                count = database.delete(DATABASE_TABLES[0], selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (URIMatcher.match(uri)) {
            case QUESTIONNAIRE:
                return Questionnaire_Data.CONTENT_TYPE;
            case QUESTIONNAIRE_ID:
                return Questionnaire_Data.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if( ! initializeDB() ) {
            Log.w(AUTHORITY,"Database unavailable...");
            return null;
        }

        ContentValues values = (initialValues != null) ? new ContentValues(
                initialValues) : new ContentValues();

        switch (URIMatcher.match(uri)) {
            case QUESTIONNAIRE:
                long weather_id = database.insert(DATABASE_TABLES[0], Questionnaire_Data.DEVICE_ID, values);

                if (weather_id > 0) {
                    Uri new_uri = ContentUris.withAppendedId(
                            Questionnaire_Data.CONTENT_URI,
                            weather_id);
                    getContext().getContentResolver().notifyChange(new_uri,
                            null);
                    return new_uri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if( ! initializeDB() ) {
            Log.w(AUTHORITY,"Database unavailable...");
            return null;
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (URIMatcher.match(uri)) {
            case QUESTIONNAIRE:
                qb.setTables(DATABASE_TABLES[0]);
                qb.setProjectionMap(databaseMap);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        try {
            Cursor c = qb.query(database, projection, selection, selectionArgs,
                    null, null, sortOrder);
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        } catch (IllegalStateException e) {
            if (Aware.DEBUG)
                Log.e(Aware.TAG, e.getMessage());

            return null;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        if( ! initializeDB() ) {
            Log.w(AUTHORITY,"Database unavailable...");
            return 0;
        }

        int count = 0;
        switch (URIMatcher.match(uri)) {
            case QUESTIONNAIRE:
                count = database.update(DATABASE_TABLES[0], values, selection,
                        selectionArgs);
                break;
            default:

                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
