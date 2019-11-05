package com.android.onehuman.smsecretsanta.database;

import android.provider.BaseColumns;

public class DBContract {


    public static class PersonEntry implements BaseColumns {

        public static final String TABLE_NAME = "person";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_MAIL = "mail";


        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_NAME + " TEXT NOT NULL UNIQUE," +
                        COLUMN_PHONE + " TEXT NOT NULL," +
                        COLUMN_MAIL + " TEXT NOT NULL)";


        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    public static class CandidateEntry implements BaseColumns {

        public static final String TABLE_NAME = "candidate";
        public static final String COLUMN_PERSON = "idPerson";
        public static final String COLUMN_CANDIDATE = "idCandidate";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_PERSON + " INTEGER NOT NULL, " +
                        COLUMN_CANDIDATE + " INTEGER NOT NULL, " +
                        "PRIMARY KEY ("+COLUMN_PERSON+", "+COLUMN_CANDIDATE+"), "+
                        "FOREIGN KEY ("+COLUMN_PERSON+") REFERENCES "+PersonEntry.TABLE_NAME+"("+PersonEntry.COLUMN_ID+"), "+
                        "FOREIGN KEY ("+COLUMN_CANDIDATE+") REFERENCES "+PersonEntry.TABLE_NAME+"("+PersonEntry.COLUMN_ID+"));";


        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

    }


}