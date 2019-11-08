package com.android.onehuman.smsecretsanta.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper dbHelper;
    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "SMSecretSanta.db";

    public static synchronized DBHelper getInstance(Context c) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(c.getApplicationContext());
        }
        return dbHelper;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.PersonEntry.SQL_CREATE_TABLE);
        db.execSQL(DBContract.ForbiddenEntry.SQL_CREATE_TABLE);
        db.execSQL(DBContract.GroupEntry.SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContract.PersonEntry.SQL_DELETE_ENTRIES);
        db.execSQL(DBContract.ForbiddenEntry.SQL_DELETE_ENTRIES);
        db.execSQL(DBContract.GroupEntry.SQL_DELETE_ENTRIES);

        onCreate(db);
    }

}

