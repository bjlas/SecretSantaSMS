package com.android.onehuman.secretsantasms;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.onehuman.secretsantasms.database.DBController;
import com.android.onehuman.secretsantasms.filter.EmojiExcludeFilter;
import com.android.onehuman.secretsantasms.model.Group;

public class EditGroup extends AppCompatActivity {

    private EditText name;
    private EditText maxprice;
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

        name = (EditText) findViewById(R.id.edit_group_edittext_name);
        maxprice = (EditText) findViewById(R.id.edit_group_edittext_maxprice);
        dbController = new DBController(this);
        activity=this;

        setTitle(getResources().getString(R.string.edit_group_title));

        name.setFilters(new InputFilter[]{new EmojiExcludeFilter(activity)});
        maxprice.setFilters(new InputFilter[]{new EmojiExcludeFilter(activity)});

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
            name.setText(group.getGroupName());
            maxprice.setText(group.getMaxPrice());
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
            group.setGroupName(name.getText().toString());
            group.setMaxPrice(maxprice.getText().toString());

            if (!validations()) {
                return false;
            }

            dbController.insertGroup(group);
            Toast.makeText(this, getResources().getString(R.string.edit_created), Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }

        if (id == R.id.menu_edit_person_action_update) {
            group.setGroupName(name.getText().toString());
            group.setMaxPrice(maxprice.getText().toString());

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
        if (!validateRequiredField(name) || !validateDuplicateNameField(group) ) {
            return false;
        }
        return true;
    }


    public boolean validateDuplicateNameField(Group group) {
        if(dbController.existGroupName(group)) {
            name.setError(getResources().getString(R.string.edit_validation_name));
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }




}
