package com.android.onehuman.smsecretsanta;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import java.util.regex.Pattern;

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
    private List<Person> allCandidates;
    private List<Integer> selectedCandidates;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        setTitle(getResources().getString(R.string.edit_view_title));

        name = (EditText) findViewById(R.id.edit_edittext_name);
        phone = (EditText) findViewById(R.id.edit_edittext_phone);
        mail = (EditText) findViewById(R.id.edit_edittext_mail);
        chips = (EditText) findViewById(R.id.edit_edittext_chips);
        chipGroup = (ChipGroup) findViewById(R.id.tag_group);
        sqLiteDB = new SQLiteDB(this);
        activity=this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        addMenuItem = menu.findItem(R.id.edit_action_add);
        deleteMenuItem = menu.findItem(R.id.edit_action_delete);
        updateMenuItem = menu.findItem(R.id.edit_action_update);

        person = getIntent().getParcelableExtra(EditActivity.class.getSimpleName());
        allCandidates = sqLiteDB.getAllCandidates(person);

        if(person != null){
            addMenuItem.setVisible(false);
            name.setText(person.getName());
            phone.setText(person.getPhone());
            mail.setText(person.getMail());
            showChips(allCandidates, sqLiteDB.getActualCandidates(person));

        } else {
            deleteMenuItem.setVisible(false);
            updateMenuItem.setVisible(false);
            showChips(allCandidates, null);

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        selectedCandidates = getNonSelectedChips();

        if (id == R.id.edit_action_add) {

            person = new Person();
            person.setName(name.getText().toString());
            person.setPhone(PhoneNumberUtils.formatNumber(phone.getText().toString().replace(" ", "")));
            person.setMail(mail.getText().toString());

            if (!validateNameField(person) || !validateRequiredField(name) || !validateRequiredField(phone) || !validateRequiredField(mail) || !validateChips() || !validateMail(person.getMail()) || !validatePhone(person.getPhone())) {
                return false;
            }

            int addedPersonID = (int) sqLiteDB.insertPerson(person);
            for(int candidate: selectedCandidates) {
                sqLiteDB.insertCandidate(addedPersonID, candidate);
            }

            Toast.makeText(this, getResources().getString(R.string.edit_person_created), Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }
        if (id == R.id.edit_action_delete) {

            AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.delete))
                    .setMessage(String.format(getResources().getString(R.string.edit_dialog_deleted), person.getName()))
                    .setIcon(R.drawable.icon_candy)

                    .setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            sqLiteDB.deletePerson(person.getId());
                            Toast.makeText(activity, getResources().getString(R.string.edit_person_deleted), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();
                        }

                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    })
                    .create();

            myQuittingDialogBox.show();
            return true;
        }
        if (id == R.id.edit_action_update) {
            person.setName(name.getText().toString());
            person.setPhone(phone.getText().toString().replace(" ", ""));
            person.setMail(mail.getText().toString());

            if (!validateNameField(person) || !validateRequiredField(name) || !validateRequiredField(phone) || !validateRequiredField(mail) || !validateChips() || !validateMail(person.getMail()) || !validatePhone(person.getPhone())) {
                return false;
            }

            sqLiteDB.updatePerson(person);
            sqLiteDB.deleteAllCandidates(person.getId());
            for(int candidateID: selectedCandidates) {
                sqLiteDB.insertCandidate(person.getId(), candidateID);
            }

            Toast.makeText(this, getResources().getString(R.string.edit_person_edited), Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void showChips(final List<Person> candidates, List<Integer> candidatesList) {
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

                if(candidatesList != null && !candidatesList.contains(id)) {
                    chip.setChecked(true);
                }

                chipGroup.addView(chip);
            }

    }

    public boolean validateNameField(Person person) {
        if(sqLiteDB.existName(person)) {
            name.setError(getResources().getString(R.string.edit_validation_name));
            return false;
        }
        return true;
    }

    public boolean validateChips() {

        if(allCandidates.size() !=0 && selectedCandidates.size() == 0) {
            //All chips selected means this person can gift to anyone. ()
            chips.setError(getResources().getString(R.string.edit_validation_chips));
            return false;
        }
        return true;
    }


    public boolean validateMail(CharSequence target) {
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches() ) {
            mail.setError(getResources().getString(R.string.edit_validation_mail));
            return false;
        }
        return true;

    }


    public boolean validateRequiredField(EditText field) {
        if(field.getText().toString().length() == 0 ) {
            field.setError(field.getHint()+" "+getResources().getString(R.string.edit_validation_field_requited));
            return false;
        }
        return true;
    }

    private boolean validatePhone(String number) {
        if(!android.util.Patterns.PHONE.matcher(number).matches() ) {
            phone.setError(getResources().getString(R.string.edit_validation_phone));
            return false;
        }
        return true;
    }

    public List<Integer> getNonSelectedChips() {
        List<Integer> nonSelectedChips=new ArrayList<>();
        for(int index = 0; index < chipGroup.getChildCount(); index++){
            Chip chip = (Chip) chipGroup.getChildAt(index);
            if (!chip.isChecked()) {
                nonSelectedChips.add(chip.getId());
            }
        }
        return nonSelectedChips;
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