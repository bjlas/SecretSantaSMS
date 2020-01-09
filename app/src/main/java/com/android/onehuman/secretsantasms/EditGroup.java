package com.android.onehuman.secretsantasms;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.onehuman.secretsantasms.database.DBController;
import com.android.onehuman.secretsantasms.model.Group;
import com.android.onehuman.secretsantasms.utils.ValidationHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditGroup extends AppCompatActivity {

    private TextInputEditText nameEditText,maxpriceEditText;
    private TextInputLayout nameErrorEditText,maxpriceErrorEditText;
    private ValidationHelper validationHelper;

    MenuItem addMenuItem;
    MenuItem deleteMenuItem;
    MenuItem updateMenuItem;
    private DBController dbController;
    private Group group;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        nameEditText = (TextInputEditText) findViewById(R.id.edit_group_edittext_name);
        maxpriceEditText = (TextInputEditText) findViewById(R.id.edit_group_edittext_maxprice);

        nameErrorEditText = (TextInputLayout) findViewById(R.id.edit_group_errorlayout_name);
        maxpriceErrorEditText = (TextInputLayout) findViewById(R.id.edit_group_errorlayout_maxprice);

        dbController = new DBController(this);
        activity=this;

        setTitle(getResources().getString(R.string.edit_group_title));

        validationHelper = new ValidationHelper(activity);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    void initData(){
        if (getIntent().getExtras() != null) {
            group = getIntent().getExtras().getParcelable("group");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_person, menu);
        addMenuItem = menu.findItem(R.id.menu_edit_person_action_add);
        deleteMenuItem = menu.findItem(R.id.menu_edit_person_action_delete);
        updateMenuItem = menu.findItem(R.id.menu_edit_person_action_update);


        if(group != null){
            addMenuItem.setVisible(false);
            deleteMenuItem.setVisible(false);
            nameEditText.setText(group.getGroupName());
            maxpriceEditText.setText(group.getMaxPrice());
        } else {
            deleteMenuItem.setVisible(false);
            updateMenuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.menu_edit_person_action_add) {

            group = new Group();
            group.setGroupName(nameEditText.getText().toString());
            group.setMaxPrice(maxpriceEditText.getText().toString());

            if (!validations()) {
                return false;
            }

            dbController.insertGroup(group);
            Toast.makeText(this, getResources().getString(R.string.edit_created), Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }

        if (id == R.id.menu_edit_person_action_update) {
            group.setGroupName(nameEditText.getText().toString());
            group.setMaxPrice(maxpriceEditText.getText().toString());

            if (!validations()) {
                return false;
            }

            dbController.updateGroup(group);
            Toast.makeText(this, getResources().getString(R.string.edit_edited), Toast.LENGTH_SHORT).show();
            
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean validations() {

        if (!validationHelper.validateRequiredField(nameEditText, nameErrorEditText, nameEditText.getHint()+" "+getResources().getString(R.string.edit_validation_field_requited))) { return false; }

        if (!validationHelper.validateEmoji(nameEditText, nameErrorEditText, getResources().getString(R.string.edit_validation_emojis))) { return false; }

        if (!validationHelper.validateEmoji(maxpriceEditText, maxpriceErrorEditText, getResources().getString(R.string.edit_validation_emojis))) { return false; }

        if (!validationHelper.validateDuplicateGroupNameField(group, nameErrorEditText, getResources().getString(R.string.edit_validation_name))) { return false; }


        return true;
    }







    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }






}
