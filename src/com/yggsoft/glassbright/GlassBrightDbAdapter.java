// Copyright 2014 Lucas Dummitt https://plus.google.com/+LucasDummitt

package com.yggsoft.glassbright;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class GlassBrightDbAdapter {
    // Database configuration
    private static final String DATABASE_TABLE = "GlassBrightSettingsTable";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE =
        "create table " + DATABASE_TABLE + " (_id integer primary key autoincrement, " + "name text not null, value text not null);";

    // Table configuration
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_VALUE = "value";
    
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;

    public GlassBrightDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public GlassBrightDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx, DATABASE_TABLE, null, DATABASE_VERSION);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }

    public long createSetting(String name, String value) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_VALUE, value);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    public boolean deleteAll()
    {
        return mDb.delete(DATABASE_TABLE, null, null) > 0;    	
    }

    public String getSetting(String name) throws SQLException
    {
        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
        		KEY_NAME, KEY_VALUE}, KEY_NAME + "='" + name + "'", null,
                         null, null, null, null);
        String strReturn = null;
        if (mCursor != null) {
        	if(mCursor.moveToFirst())
        	{
        		strReturn = mCursor.getString(mCursor.getColumnIndexOrThrow(GlassBrightDbAdapter.KEY_VALUE));
        	}
        }
        return strReturn;
    }

    public boolean updateSetting(String name, String value) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_VALUE, value);
        return mDb.update(DATABASE_TABLE, args, KEY_NAME + "='" + name + "'", null) > 0;
    }
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

    	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
    		super(context, name, factory, version);
    	}
    	@Override
    	public void onCreate(SQLiteDatabase db) {

    		db.execSQL(DATABASE_CREATE);    		
    	}

    	@Override
    	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    		db.execSQL("DROP TABLE IF EXISTS " + GlassBrightDbAdapter.DATABASE_TABLE);
    		onCreate(db);
    	}
    }
}