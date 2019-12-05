package com.android.onehuman.secretsantasms.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.onehuman.secretsantasms.model.Group;
import com.android.onehuman.secretsantasms.model.Person;

import java.util.ArrayList;
import java.util.List;

public class DBController {

    private DBHelper dbHelper;

    public DBController(Context context)
    {
        dbHelper = DBHelper.getInstance(context);
    }


    //COMMON
    public List<Person> getAllPersons(int GroupID) {
        List<Person> personList = new ArrayList<>();
        Person person;
        String selectQuery = "SELECT " +
                "per."+DBContract.PersonEntry.COLUMN_PERSON_ID+", " +
                "per."+DBContract.PersonEntry.COLUMN_PERSON_NAME+", " +
                "per."+DBContract.PersonEntry.COLUMN_PERSON_PHONE+", " +
                "per."+DBContract.PersonEntry.COLUMN_PERSON_MAIL+" " +
                "FROM "+DBContract.PersonEntry.TABLE_NAME+" per " +
                "LEFT JOIN "+DBContract.PersonsInGroupEntry.TABLE_NAME+" pig " +
                "ON pig."+DBContract.PersonsInGroupEntry.COLUMN_PERSON_ID+"=per."+DBContract.PersonEntry.COLUMN_PERSON_ID+" " +
                "WHERE pig."+DBContract.PersonsInGroupEntry.COLUMN_GROUP_ID+"="+GroupID;

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
    public List<Person> getCandidates(Person original, Group group) {
        List<Person> personList = new ArrayList<>();
        Person person;
        String selectQuery = "SELECT " +
                "per."+DBContract.PersonEntry.COLUMN_PERSON_ID+", " +
                "per."+DBContract.PersonEntry.COLUMN_PERSON_NAME+", " +
                "per."+DBContract.PersonEntry.COLUMN_PERSON_PHONE+", " +
                "per."+DBContract.PersonEntry.COLUMN_PERSON_MAIL+" " +
                "FROM "+DBContract.PersonEntry.TABLE_NAME+" per, "+DBContract.PersonsInGroupEntry.TABLE_NAME+" pig " +
                " WHERE pig."+DBContract.PersonsInGroupEntry.COLUMN_GROUP_ID+" = "+group.getGroupID()+" AND per."+DBContract.PersonEntry.COLUMN_PERSON_ID+"=pig."+DBContract.PersonsInGroupEntry.COLUMN_PERSON_ID+"";

        if(original!=null) {
            selectQuery += " AND per."+DBContract.PersonEntry.COLUMN_PERSON_ID+" is not "+original.getId()+" ";
        }

        selectQuery +=" ORDER BY per."+DBContract.PersonEntry.COLUMN_PERSON_NAME+" ASC";

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
    public long insertPerson(Person person) {
        SQLiteDatabase db  = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.PersonEntry.COLUMN_PERSON_NAME, person.getName());
        values.put(DBContract.PersonEntry.COLUMN_PERSON_PHONE, person.getPhone());
        values.put(DBContract.PersonEntry.COLUMN_PERSON_MAIL, person.getMail());

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
    public boolean existPersonName(Person person, Group group) {

        String selectQuery = "SELECT COUNT(*) " +
                "FROM "+DBContract.PersonEntry.TABLE_NAME+" per " +
                "LEFT JOIN "+DBContract.PersonsInGroupEntry.TABLE_NAME+" pig " +
                "ON pig."+DBContract.PersonEntry.COLUMN_PERSON_ID+"=per."+DBContract.PersonsInGroupEntry.COLUMN_PERSON_ID+" " +
                "WHERE pig."+DBContract.PersonsInGroupEntry.COLUMN_GROUP_ID+"= "+group.getGroupID() +" "+
                "AND per."+DBContract.PersonEntry.COLUMN_PERSON_NAME+" = '"+person.getName()+"' " +
                "AND per."+DBContract.PersonEntry.COLUMN_PERSON_ID+" IS NOT "+person.getId();

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
                "gro."+DBContract.GroupEntry.COLUMN_GROUP_ID+", " +
                "gro."+DBContract.GroupEntry.COLUMN_GROUP_NAME+", " +
                "gro."+DBContract.GroupEntry.COLUMN_MAXPRICE+" " +
                "FROM " +
                ""+DBContract.GroupEntry.TABLE_NAME+" gro " +
                "ORDER BY gro."+DBContract.GroupEntry.COLUMN_GROUP_NAME+" ASC";


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
    public int updateGroup(Group group) {
        SQLiteDatabase db  = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.GroupEntry.COLUMN_GROUP_NAME, group.getGroupName());
        values.put(DBContract.GroupEntry.COLUMN_MAXPRICE, group.getMaxPrice());

        String selection = DBContract.GroupEntry.COLUMN_GROUP_ID + " = ?";
        String[] selectionArgs = { String.valueOf(group.getGroupID()) };
        return db.update(DBContract.GroupEntry.TABLE_NAME, values, selection, selectionArgs);
    }
    public boolean existGroupName(Group group) {

        String selectQuery = "SELECT COUNT(*) " +
                "FROM " +
                ""+DBContract.GroupEntry.TABLE_NAME+" person " +
                "WHERE person."+DBContract.GroupEntry.COLUMN_GROUP_NAME+" ='" + group.getGroupName() + "'"+
                " AND person."+DBContract.GroupEntry.COLUMN_GROUP_ID+" is not '"+group.getGroupID()+"' ";

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
    public Group getGroup(int groupID) {
        Group group=new Group();
        String selectQuery = "SELECT " +
                "gru."+DBContract.GroupEntry.COLUMN_GROUP_ID+", " +
                "gru."+DBContract.GroupEntry.COLUMN_GROUP_NAME+", " +
                "gru."+DBContract.GroupEntry.COLUMN_MAXPRICE+" " +
                "FROM " +
                ""+DBContract.GroupEntry.TABLE_NAME+" gru " +
                "WHERE gru."+DBContract.GroupEntry.COLUMN_GROUP_ID+" ="+groupID;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {



                group.setGroupID(cursor.getInt(0));
                group.setGroupName(cursor.getString(1));
                group.setMaxPrice(cursor.getString(2));



            } while (cursor.moveToNext());
        }
        cursor.close();
        return group;
    }


    //PERSONSINGROUP TABLE
    public long insertPersonInGroup(int idGroup, int idPerson) {
        SQLiteDatabase db  = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.PersonsInGroupEntry.COLUMN_GROUP_ID, idGroup);
        values.put(DBContract.PersonsInGroupEntry.COLUMN_PERSON_ID, idPerson);

        return db.insert(DBContract.PersonsInGroupEntry.TABLE_NAME, null, values);
    }
    public List<Integer> getAllPersonsOfAGroup(int groupID) {
        List<Integer> personIDList = new ArrayList<>();

        String selectQuery = "SELECT "+DBContract.PersonsInGroupEntry.COLUMN_PERSON_ID+" " +
                "FROM "+DBContract.PersonsInGroupEntry.TABLE_NAME+" " +
                "WHERE "+DBContract.PersonsInGroupEntry.COLUMN_GROUP_ID+" = " +groupID;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {

                personIDList.add(cursor.getInt(0));


            } while (cursor.moveToNext());
        }
        cursor.close();
        return personIDList;
    }

    public int deleteAllGroupPersons(int idGroup) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DBContract.PersonsInGroupEntry.COLUMN_GROUP_ID + " = ? ";
        String[] selectionArgs = { String.valueOf(idGroup) };
        return db.delete(DBContract.PersonsInGroupEntry.TABLE_NAME, selection, selectionArgs);
    }
    public int deleteAPersonsOfAGroup(int idPerson) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DBContract.PersonsInGroupEntry.COLUMN_PERSON_ID + " = ? ";
        String[] selectionArgs = { String.valueOf(idPerson) };
        return db.delete(DBContract.PersonsInGroupEntry.TABLE_NAME, selection, selectionArgs);
    }
}

