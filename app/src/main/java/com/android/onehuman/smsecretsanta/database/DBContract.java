package com.android.onehuman.smsecretsanta.database;

import android.provider.BaseColumns;

public class DBContract {


    public static class PersonEntry implements BaseColumns {

        public static final String TABLE_NAME = "person";
        public static final String COLUMN_PERSON_ID = "personID";
        public static final String COLUMN_PERSON_NAME = "personName";
        public static final String COLUMN_PERSON_PHONE = "personPhone";
        public static final String COLUMN_PERSON_MAIL = "personMail";


        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_PERSON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_PERSON_NAME + " TEXT NOT NULL," +
                        COLUMN_PERSON_PHONE + " TEXT NOT NULL," +
                        COLUMN_PERSON_MAIL + " TEXT)";


        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    public static class ForbiddenEntry implements BaseColumns {

        public static final String TABLE_NAME = "ForbiddenTable";
        public static final String COLUMN_PERSON_ID = "personID";
        public static final String COLUMN_FORBIDDEN_ID = "forbiddenID";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_PERSON_ID + " INTEGER NOT NULL, " +
                        COLUMN_FORBIDDEN_ID + " INTEGER NOT NULL, " +
                        "PRIMARY KEY ("+COLUMN_PERSON_ID+", "+COLUMN_FORBIDDEN_ID+"), "+
                        "FOREIGN KEY ("+COLUMN_PERSON_ID+") REFERENCES "+PersonEntry.TABLE_NAME+"("+PersonEntry.COLUMN_PERSON_ID+"), "+
                        "FOREIGN KEY ("+COLUMN_FORBIDDEN_ID+") REFERENCES "+PersonEntry.TABLE_NAME+"("+PersonEntry.COLUMN_PERSON_ID+"));";


        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    public static class GroupEntry implements BaseColumns {

        public static final String TABLE_NAME = "GroupTable";
        public static final String COLUMN_GROUP_ID = "groupID";
        public static final String COLUMN_GROUP_NAME = "groupName";
        public static final String COLUMN_MAXPRICE = "maxPrice";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_GROUP_NAME + " TEXT NOT NULL UNIQUE," +
                        COLUMN_MAXPRICE + " TEXT)";


        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    public static class PersonsInGroupEntry implements BaseColumns {

        public static final String TABLE_NAME = "PersonsInGroupTable";
        public static final String COLUMN_GROUP_ID = "groupID";
        public static final String COLUMN_PERSON_ID = "personID";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_GROUP_ID + " INTEGER NOT NULL, " +
                        COLUMN_PERSON_ID + " INTEGER NOT NULL, " +
                        "PRIMARY KEY ("+COLUMN_GROUP_ID+", "+COLUMN_PERSON_ID+"), "+
                        "FOREIGN KEY ("+COLUMN_GROUP_ID+") REFERENCES "+GroupEntry.TABLE_NAME+"("+GroupEntry.COLUMN_GROUP_ID+"), "+
                        "FOREIGN KEY ("+COLUMN_PERSON_ID+") REFERENCES "+PersonEntry.TABLE_NAME+"("+PersonEntry.COLUMN_PERSON_ID+"));";


        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

    }


}
