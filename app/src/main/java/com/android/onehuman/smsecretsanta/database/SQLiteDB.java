package com.android.onehuman.smsecretsanta.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.onehuman.smsecretsanta.model.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLiteDB extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "SMSecretSanta.db";


    public SQLiteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.PersonEntry.SQL_CREATE_TABLE);
        db.execSQL(DBContract.ForbiddenEntry.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContract.PersonEntry.SQL_DELETE_ENTRIES);
        db.execSQL(DBContract.ForbiddenEntry.SQL_DELETE_ENTRIES);

        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    //COMMON
    public List<Person> getAllPersons() {
        List<Person> personList = new ArrayList<>();
        Person person;
        int id;
        String selectQuery = "SELECT " +
                "person."+DBContract.PersonEntry.COLUMN_ID+", " +
                "person."+DBContract.PersonEntry.COLUMN_NAME+", " +
                "person."+DBContract.PersonEntry.COLUMN_PHONE+", " +
                "person."+DBContract.PersonEntry.COLUMN_MAIL+" " +
                "FROM " +
                ""+DBContract.PersonEntry.TABLE_NAME+" person " +
                "ORDER BY person."+DBContract.PersonEntry.COLUMN_NAME+" ASC";



        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {

                person = new Person();
                id=cursor.getInt(0);

                person.setId(id);
                person.setName(cursor.getString(1));
                person.setPhone(cursor.getString(2));
                person.setMail(cursor.getString(3));
                person.setForbbidenList(getForbiddenPersonInfo(id));

                personList.add(person);


            } while (cursor.moveToNext());
        }
        cursor.close();
        return personList;
    }
    public List<Person> getPosibleCandidates(Person original) {
        List<Person> personList = new ArrayList<>();
        Person person;
        String selectQuery = "SELECT " +
                "person."+DBContract.PersonEntry.COLUMN_ID+", " +
                "person."+DBContract.PersonEntry.COLUMN_NAME+", " +
                "person."+DBContract.PersonEntry.COLUMN_PHONE+", " +
                "person."+DBContract.PersonEntry.COLUMN_MAIL+" " +
                "FROM " +
                ""+DBContract.PersonEntry.TABLE_NAME+" person ";

        if(original!=null) {
            selectQuery += " WHERE person." + DBContract.PersonEntry.COLUMN_ID + " IS NOT '" + original.getId() + "'";
        }

        selectQuery +=" ORDER BY person."+DBContract.PersonEntry.COLUMN_NAME+" ASC";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {

                person = new Person();

                person.setId(cursor.getInt(0));
                person.setName(cursor.getString(1));
                person.setPhone(cursor.getString(2));
                person.setMail(cursor.getString(3));

                personList.add(person);


            } while (cursor.moveToNext());
        }
        cursor.close();
        return personList;

    }
    public List<Person> getActualCandidates(Person original) {
        List<Person> personList = new ArrayList<>();
        Person person;
        String selectQuery = "SELECT " +
                "p."+DBContract.PersonEntry.COLUMN_ID+", " +
                "p."+DBContract.PersonEntry.COLUMN_NAME+", " +
                "p."+DBContract.PersonEntry.COLUMN_PHONE+", " +
                "p."+DBContract.PersonEntry.COLUMN_MAIL+" " +
                "FROM "+DBContract.PersonEntry.TABLE_NAME+" p " +
                "WHERE p."+DBContract.PersonEntry.COLUMN_ID+"!="+original.getId()+" and p."+DBContract.PersonEntry.COLUMN_ID+" not in " +
                "(SELECT f."+DBContract.ForbiddenEntry.COLUMN_DONTGIFTTO+" " +
                "FROM "+DBContract.ForbiddenEntry.TABLE_NAME+" f " +
                "WHERE f."+DBContract.ForbiddenEntry.COLUMN_PERSON+"="+original.getId()+")";


        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {

                person = new Person();

                person.setId(cursor.getInt(0));
                person.setName(cursor.getString(1));
                person.setPhone(cursor.getString(2));
                person.setMail(cursor.getString(3));

                personList.add(person);


            } while (cursor.moveToNext());
        }
        cursor.close();
        return personList;

    }
    public List<Person> getForbiddenPersonInfo(int id) {
        List<Person> forbiddenList = new ArrayList<>();
        Person person;
        String selectQuery = "SELECT " +
                "per."+DBContract.PersonEntry.COLUMN_ID+", " +
                "per."+DBContract.PersonEntry.COLUMN_NAME+", " +
                "per."+DBContract.PersonEntry.COLUMN_PHONE+", " +
                "per."+DBContract.PersonEntry.COLUMN_MAIL+" " +
                "FROM "+DBContract.ForbiddenEntry.TABLE_NAME+" forbi, "+DBContract.PersonEntry.TABLE_NAME+" per " +
                "WHERE forbi."+DBContract.ForbiddenEntry.COLUMN_PERSON+" = "+id+" AND forbi."+DBContract.ForbiddenEntry.COLUMN_DONTGIFTTO+"=per.id";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {

                person = new Person();

                person.setId(cursor.getInt(0));
                person.setName(cursor.getString(1));
                person.setPhone(cursor.getString(2));
                person.setMail(cursor.getString(3));

                forbiddenList.add(person);


            } while (cursor.moveToNext());
        }
        cursor.close();
        return forbiddenList;
    }

    //PERSON TABLE
    public boolean existName(Person person) {

        String selectQuery = "SELECT COUNT(*) " +
                "FROM " +
                ""+DBContract.PersonEntry.TABLE_NAME+" person " +
                "WHERE person."+DBContract.PersonEntry.COLUMN_NAME+" ='" + person.getName() + "'"+
                " AND person."+DBContract.PersonEntry.COLUMN_ID+" is not '"+person.getId()+"' ";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        int count=cursor.getInt(0);
        cursor.close();

        if (count>0) {
            return true;
        } else {
            return false;
        }
    }
    public long insertPerson(Person person) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.PersonEntry.COLUMN_NAME, person.getName());
        values.put(DBContract.PersonEntry.COLUMN_PHONE, person.getPhone());
        values.put(DBContract.PersonEntry.COLUMN_MAIL, person.getMail());
        //values.put(DBContract.PersonEntry.COLUMN_FORBIDDENLIST, convertArrayToString(person.getForbbidenList()));

        return db.insert(DBContract.PersonEntry.TABLE_NAME, null, values);
    }
    public int updatePerson(Person person) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.PersonEntry.COLUMN_NAME, person.getName());
        values.put(DBContract.PersonEntry.COLUMN_PHONE, person.getPhone());
        values.put(DBContract.PersonEntry.COLUMN_MAIL, person.getMail());
        //values.put(DBContract.PersonEntry.COLUMN_FORBIDDENLIST, convertArrayToString(person.getForbbidenList()));

        String selection = DBContract.PersonEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(person.getId()) };
        return db.update(DBContract.PersonEntry.TABLE_NAME, values, selection, selectionArgs);
    }
    public int deletePerson(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String selection = DBContract.PersonEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        return db.delete(DBContract.PersonEntry.TABLE_NAME, selection, selectionArgs);
    }


    //FORBIDDEN TABLE
    public long insertForbidden(int idPerson, int idForgibben) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.ForbiddenEntry.COLUMN_PERSON, idPerson);
        values.put(DBContract.ForbiddenEntry.COLUMN_DONTGIFTTO, idForgibben);

        return db.insert(DBContract.ForbiddenEntry.TABLE_NAME, null, values);
    }
    public int deleteForbidden(int idPerson) {
        SQLiteDatabase db = getReadableDatabase();
        String selection = DBContract.ForbiddenEntry.COLUMN_PERSON + " = ? ";
        String[] selectionArgs = { String.valueOf(idPerson) };
        return db.delete(DBContract.ForbiddenEntry.TABLE_NAME, selection, selectionArgs);
    }



}

