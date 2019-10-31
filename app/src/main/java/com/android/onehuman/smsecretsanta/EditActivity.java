package com.android.onehuman.smsecretsanta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.onehuman.smsecretsanta.database.SQLiteDB;
import com.android.onehuman.smsecretsanta.model.Person;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {


    private static final int SELECT_PHONE_NUMBER = 1;
    private EditText name;
    private EditText phone;
    private EditText mail;
    private EditText chips;

    MenuItem addMenuItem;
    MenuItem deleteMenuItem;
    MenuItem updateMenuItem;

    private SQLiteDB sqLiteDB;
    private Person person;
    private ChipGroup chipGroup;
    private List<Person> candidates;
    private List<Integer> seletedChipsIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        setTitle("Add Person");

        name = (EditText) findViewById(R.id.edit_edittext_name);
        phone = (EditText) findViewById(R.id.edit_edittext_phone);
        mail = (EditText) findViewById(R.id.edit_edittext_mail);
        chips = (EditText) findViewById(R.id.edit_edittext_chips);
        chipGroup = (ChipGroup) findViewById(R.id.tag_group);
        sqLiteDB = new SQLiteDB(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        addMenuItem = menu.findItem(R.id.edit_action_add);
        deleteMenuItem = menu.findItem(R.id.edit_action_delete);
        updateMenuItem = menu.findItem(R.id.edit_action_update);

        person = getIntent().getParcelableExtra(EditActivity.class.getSimpleName());

        if(person != null){
            addMenuItem.setVisible(false);
            name.setText(person.getName());
            phone.setText(person.getPhone());
            mail.setText(person.getMail());
            candidates = sqLiteDB.getAllCandidates(person);
            showChips(candidates, person.getForbbidenList());

        } else {
            deleteMenuItem.setVisible(false);
            updateMenuItem.setVisible(false);
            candidates = sqLiteDB.getAllCandidates(person);
            showChips(candidates, new ArrayList<Person>());

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        seletedChipsIDs = getForbiddenListFromChips();


        if (id == R.id.edit_action_add) {

            person = new Person();
            person.setName(name.getText().toString());
            person.setPhone(PhoneNumberUtils.formatNumber(phone.getText().toString().replace(" ", "")));
            person.setMail(mail.getText().toString());

            if (!validateNameField(person) || !validateRequiredField(name) || !validateRequiredField(phone) || !validateRequiredField(mail) || !validateChips() || !validateMail(person.getMail())) {
                return false;
            }

            int addedPersonID = (int) sqLiteDB.insertPerson(person);

            for(int forbiddenId: seletedChipsIDs) {
                sqLiteDB.insertForbidden(addedPersonID, forbiddenId);
            }

            Toast.makeText(this, "Inserted! ", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }
        if (id == R.id.edit_action_delete) {
            sqLiteDB.deletePerson(person.getId());
            Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }
        if (id == R.id.edit_action_update) {
            person.setName(name.getText().toString());
            person.setPhone(phone.getText().toString().replace(" ", ""));
            person.setMail(mail.getText().toString());

            if (!validateNameField(person) || !validateRequiredField(name) || !validateRequiredField(phone) || !validateRequiredField(mail) || !validateChips() || !validateMail(person.getMail())) {
                return false;
            }

            sqLiteDB.updatePerson(person);
            sqLiteDB.deleteForbidden(person.getId());
            for(int forbiddenId: seletedChipsIDs) {
                sqLiteDB.insertForbidden(person.getId(), forbiddenId);
            }

            Toast.makeText(this, "Edited!", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void showChips(final List<Person> candidates, List<Person> forbiddenList) {
        for (int index = 0; index < candidates.size(); index++) {
                final String tagName = candidates.get(index).getName();
                final Chip chip = new Chip(this);
                int id=candidates.get(index).getId();
                chip.setClickable(true);
                chip.setFocusable(true);
                chip.setCheckable(true);
                chip.setId(id);

                int paddingDp = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 10,
                        getResources().getDisplayMetrics()
                );
                chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
                chip.setText(tagName);

                if(forbiddenList.contains(new Person(id))) {
                    chip.setChecked(true);
                }

                chipGroup.addView(chip);
            }

    }

    public boolean validateNameField(Person person) {
        if(sqLiteDB.existName(person)) {
            name.setError("Name already exist");
            return false;
        }
        return true;
    }

    public boolean validateChips() {

        if(seletedChipsIDs.size() != 0 & seletedChipsIDs.size() == candidates.size()) {
            chips.setError("This person cannot gift anyone");
            return false;
        }
        return true;
    }


    public boolean validateMail(CharSequence target) {
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches() ) {
            mail.setError("Invalid email address");
            return false;
        }
        return true;

    }


    public boolean validateRequiredField(EditText field) {
        if(field.getText().toString().length() == 0 ) {
            field.setError(field.getHint()+" is required");
            return false;
        }
        return true;
    }

    public List<Integer> getForbiddenListFromChips() {
        List<Integer> result=new ArrayList<>();
        for(int index = 0; index < chipGroup.getChildCount(); index++){
            Chip chip = (Chip) chipGroup.getChildAt(index);
            if (chip.isChecked() ) {
                result.add( chip.getId());
            }
        }
        return result;
    }

    public void loadContactList(View view) {
        Intent i=new Intent(Intent.ACTION_PICK);
        i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(i, SELECT_PHONE_NUMBER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_PHONE_NUMBER:
                    Cursor cursor = null;
                    try {
                        String phoneNo,ContactName, email;

                        Uri uri = data.getData();
                        cursor = getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();

                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        ContactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                        name.setText(ContactName);
                        phone.setText(phoneNo);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
        }
    }










}
