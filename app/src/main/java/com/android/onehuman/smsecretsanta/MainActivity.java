package com.android.onehuman.smsecretsanta;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.android.onehuman.smsecretsanta.adapter.PersonAdapter;
import com.android.onehuman.smsecretsanta.database.SQLiteDB;
import com.android.onehuman.smsecretsanta.model.Person;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mainRecyclerView;
    private PersonAdapter personAdapter;
    private LinearLayoutManager linearLayoutManager;
    private SQLiteDB sqLiteDB;
    private List<Person> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mainRecyclerView = (RecyclerView) findViewById(R.id.main_recyclerView);

        linearLayoutManager = new LinearLayoutManager(this);
        personAdapter = new PersonAdapter(this);
        mainRecyclerView.setLayoutManager(linearLayoutManager);
        mainRecyclerView.setAdapter(personAdapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    void loadData(){
        sqLiteDB = new SQLiteDB(this);
        contactList = sqLiteDB.getAllPersons();
        personAdapter.updateList(contactList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_action_add) {
            Intent intent = new Intent(this, EditActivity.class);
            this.startActivity(intent);
            return true;
        }

        if (id == R.id.main_action_send) {
            showDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Send SMS's?");
        alertDialogBuilder
                .setMessage("Are your sure you want to send to people the Secret Santa?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        draw();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void draw(){
        List<Person> allPersonList = sqLiteDB.getAllPersons();
        Random rand = new Random();
        int random;
        Person candidate;


        for(int index=0; index<contactList.size(); index++) {
            Person person = contactList.get(index);
            random = rand.nextInt(contactList.size());
            do {
                random++;
                candidate=contactList.get(random);
            } while ((!person.getForbbidenList().contains(candidate.getName()) && person.getName().equals(candidate.getName())) || random<contactList.size() );
        }


        

    }


}
