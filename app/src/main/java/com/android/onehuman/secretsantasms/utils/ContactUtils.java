package com.android.onehuman.secretsantasms.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactUtils {


    private Context context;

    public ContactUtils(Context c) {
        this.context=c;
    }

    public void getNameUsingContactId(String contactId){

        String cContactIdString = ContactsContract.Contacts._ID;
        Uri cCONTACT_CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String cDisplayNameColumn = ContactsContract.Contacts.DISPLAY_NAME;

        String selection = cContactIdString + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(contactId)};

        Cursor cursor = context.getContentResolver().query(cCONTACT_CONTENT_URI, null, selection, selectionArgs, null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            while ((cursor != null) && (cursor.isAfterLast() == false)) {
                if (cursor.getColumnIndex(cContactIdString) >= 0) {
                    if (contactId.equals(cursor.getString(cursor.getColumnIndex(cContactIdString)))) {
                        String name = cursor.getString(cursor.getColumnIndex(cDisplayNameColumn));
                        break;
                    }
                }
                cursor.moveToNext();
            }
        }
        if (cursor != null)
            cursor.close();
    }

}
