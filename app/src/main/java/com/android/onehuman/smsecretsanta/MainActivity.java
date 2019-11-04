package com.android.onehuman.smsecretsanta;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.android.onehuman.smsecretsanta.adapter.PersonAdapter;
import com.android.onehuman.smsecretsanta.database.SQLiteDB;
import com.android.onehuman.smsecretsanta.graph.Graph;
import com.android.onehuman.smsecretsanta.model.Person;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mainRecyclerView;
    private PersonAdapter personAdapter;
    private LinearLayoutManager linearLayoutManager;
    private SQLiteDB sqLiteDB;
    private List<Person> contactList;
    private HashMap<Integer, Person> allPersonsMaps;

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
        allPersonsMaps = new HashMap<Integer, Person>();
        for(Person person: contactList) {
            allPersonsMaps.put(person.getId(), person);
        }

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
        Random random = new Random();
        Graph graph = new Graph();



        HashMap<Integer, Person> allPersonsMaps = new HashMap<Integer, Person>();
        for(Person person: allPersonList) {
            allPersonsMaps.put(person.getId(), person);
        }



        for(HashMap.Entry<Integer, Person> entry : allPersonsMaps.entrySet()) {
            int id = entry.getKey();
            Person person = entry.getValue();
            List<Person> candidates = sqLiteDB.getActualCandidates(person);
            for(Person candidate : candidates) {
                Person v=allPersonsMaps.get(candidate.getId());
                person.addAdjacentNodes(v);
            }
            graph.addPerson(person);
        }

        Person startPerson = allPersonList.get(random.nextInt(allPersonList.size()-1));

        List<List<Person>> result = graph.findHamiltonianCycles(startPerson);
        TextView test_textView = (TextView) findViewById(R.id.test_textView);
        test_textView.setText(printCycles(result));

    }

    public static String printCycles(List<List<Person>> cycles) {
        String result = "";
        for(List<Person> vertexList: cycles) {
            for(Person vertex: vertexList) {
                result += vertex.getName() +"->";
            }
            result += "                  \n";
        }
        return result;
    }


}
