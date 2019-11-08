package com.android.onehuman.smsecretsanta.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.onehuman.smsecretsanta.model.Group;
import com.android.onehuman.smsecretsanta.model.Person;

import java.util.ArrayList;
import java.util.List;

public class DBController {

    private DBHelper dbHelper;

    public DBController(Context context)
    {
        dbHelper = DBHelper.getInstance(context);
    }


    //COMMON
    public List<Person> getAllPersons() {
        List<Person> personList = new ArrayList<>();
        Person person;
        int id;
        String selectQuery = "SELECT " +
                "person."+DBContract.PersonEntry.COLUMN_PERSON_ID+", " +
                "person."+DBContract.PersonEntry.COLUMN_PERSON_NAME+", " +
                "person."+DBContract.PersonEntry.COLUMN_PERSON_PHONE+", " +
                "person."+DBContract.PersonEntry.COLUMN_PERSON_MAIL+" " +
                "FROM " +
                ""+DBContract.PersonEntry.TABLE_NAME+" person " +
                "ORDER BY person."+DBContract.PersonEntry.COLUMN_PERSON_NAME+" ASC";



        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {

                person = new Person();
                id=cursor.getInt(0);

                person.setId(id);
                person.setName(cursor.getString(1));
                person.setPhone(cursor.getString(2));
                person.setMail(cursor.getString(3));

                personList.add(person);


            } while (cursor.moveToNext());
        }
        cursor.close();
        return personList;
    }
    public List<Person> getCandidates(Person original) {
        List<Person> personList = new ArrayList<>();
        Person person;
        String selectQuery = "SELECT " +
                "person."+DBContract.PersonEntry.COLUMN_PERSON_ID+", " +
                "person."+DBContract.PersonEntry.COLUMN_PERSON_NAME+", " +
                "person."+DBContract.PersonEntry.COLUMN_PERSON_PHONE+", " +
                "person."+DBContract.PersonEntry.COLUMN_PERSON_MAIL+" " +
                "FROM " +
                ""+DBContract.PersonEntry.TABLE_NAME+" person ";

        if(original!=null) {
            selectQuery += " WHERE person." + DBContract.PersonEntry.COLUMN_PERSON_ID + " IS NOT '" + original.getId() + "'";
        }

        selectQuery +=" ORDER BY person."+DBContract.PersonEntry.COLUMN_PERSON_NAME+" ASC";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
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
    public List<Integer> getForbiddens(Person original) {
        List<Integer> idList = new ArrayList<>();
        String selectQuery = "SELECT " +
                "can."+DBContract.ForbiddenEntry.COLUMN_FORBIDDEN_ID+" " +
                "FROM "+DBContract.ForbiddenEntry.TABLE_NAME+" can " +
                "WHERE can."+DBContract.ForbiddenEntry.COLUMN_PERSON_ID+"='"+original.getId()+"'";


        SQLiteDatabase db  = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                idList.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return idList;

    }

    //PERSON TABLE
    public boolean existName(Person person) {

        String selectQuery = "SELECT COUNT(*) " +
                "FROM " +
                ""+DBContract.PersonEntry.TABLE_NAME+" person " +
                "WHERE person."+DBContract.PersonEntry.COLUMN_PERSON_NAME+" ='" + person.getName() + "'"+
                " AND person."+DBContract.PersonEntry.COLUMN_PERSON_ID+" is not '"+person.getId()+"' ";

        SQLiteDatabase db  = dbHelper.getReadableDatabase();
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
        SQLiteDatabase db  = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.PersonEntry.COLUMN_PERSON_NAME, person.getName());
        values.put(DBContract.PersonEntry.COLUMN_PERSON_PHONE, person.getPhone());
        values.put(DBContract.PersonEntry.COLUMN_PERSON_MAIL, person.getMail());
        //values.put(DBContract.PersonEntry.COLUMN_FORBIDDENLIST, convertArrayToString(person.getForbbidenList()));

        return db.insert(DBContract.PersonEntry.TABLE_NAME, null, values);
    }
    public int updatePerson(Person person) {
        SQLiteDatabase db  = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.PersonEntry.COLUMN_PERSON_NAME, person.getName());
        values.put(DBContract.PersonEntry.COLUMN_PERSON_PHONE, person.getPhone());
        values.put(DBContract.PersonEntry.COLUMN_PERSON_MAIL, person.getMail());

        String selection = DBContract.PersonEntry.COLUMN_PERSON_ID + " = ?";
        String[] selectionArgs = { String.valueOf(person.getId()) };
        return db.update(DBContract.PersonEntry.TABLE_NAME, values, selection, selectionArgs);
    }
    public int deletePerson(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DBContract.PersonEntry.COLUMN_PERSON_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        return db.delete(DBContract.PersonEntry.TABLE_NAME, selection, selectionArgs);
    }

    //FORBIDDEN TABLE
    public long insertForbidden(int idPerson, int idCandidate) {
        SQLiteDatabase db  = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.ForbiddenEntry.COLUMN_PERSON_ID, idPerson);
        values.put(DBContract.ForbiddenEntry.COLUMN_FORBIDDEN_ID, idCandidate);

        return db.insert(DBContract.ForbiddenEntry.TABLE_NAME, null, values);
    }
    public int deleteAllForbiddenRulesFromPerson(int idPerson) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DBContract.ForbiddenEntry.COLUMN_PERSON_ID + " = ? ";
        String[] selectionArgs = { String.valueOf(idPerson) };
        return db.delete(DBContract.ForbiddenEntry.TABLE_NAME, selection, selectionArgs);
    }
    public int deletePersonAsForbiddenOfOtherPersons(int idPerson) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DBContract.ForbiddenEntry.COLUMN_FORBIDDEN_ID + " = ? ";
        String[] selectionArgs = { String.valueOf(idPerson) };
        return db.delete(DBContract.ForbiddenEntry.TABLE_NAME, selection, selectionArgs);
    }

    //GROUP TABLE
    public List<Group> getAllGroups() {
        List<Group> groupList = new ArrayList<>();
        Group group;

        String selectQuery = "SELECT " +
                "person."+DBContract.GroupEntry.COLUMN_GROUP_ID+", " +
                "person."+DBContract.GroupEntry.COLUMN_GROUP_NAME+", " +
                "person."+DBContract.GroupEntry.COLUMN_MAXPRICE+" " +
                "FROM " +
                ""+DBContract.GroupEntry.TABLE_NAME+" person " +
                "ORDER BY person."+DBContract.GroupEntry.COLUMN_GROUP_NAME+" ASC";


        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {

                group = new Group();

                group.setGroupID(cursor.getInt(0));
                group.setGroupName(cursor.getString(1));
                group.setMaxPrice(cursor.getString(2));

                groupList.add(group);


            } while (cursor.moveToNext());
        }
        cursor.close();
        return groupList;
    }


    public long insertGroup(Group group) {
        SQLiteDatabase db  = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.GroupEntry.COLUMN_GROUP_ID, group.getGroupID());
        values.put(DBContract.GroupEntry.COLUMN_GROUP_NAME, group.getGroupName());
        values.put(DBContract.GroupEntry.COLUMN_MAXPRICE, group.getMaxPrice());

        return db.insert(DBContract.GroupEntry.TABLE_NAME, null, values);
    }
    public int deleteGroup(int groupID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DBContract.GroupEntry.COLUMN_GROUP_ID + " = ?";
        String[] selectionArgs = { String.valueOf(groupID) };
        return db.delete(DBContract.GroupEntry.TABLE_NAME, selection, selectionArgs);
    }


}

