package com.android.onehuman.smsecretsanta;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import com.android.onehuman.smsecretsanta.adapter.PersonAdapter;
import com.android.onehuman.smsecretsanta.database.SQLiteDB;
import com.android.onehuman.smsecretsanta.event.AlertUtils;
import com.android.onehuman.smsecretsanta.graph.Graph;
import com.android.onehuman.smsecretsanta.model.Person;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private RecyclerView mainRecyclerView;
    private PersonAdapter personAdapter;
    private LinearLayoutManager linearLayoutManager;
    private SQLiteDB sqLiteDB;
    private List<Person> contactList;
    private Random random;
    private HashMap<Integer, Person> allPersonsMaps;
    private Activity activity;
    private MenuItem sendButton;


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

        this.activity=this;
        random = new Random();
        checkForSmsPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    void loadData(){
        sqLiteDB = new SQLiteDB(this);
        contactList = sqLiteDB.getAllPersons();
        allPersonsMaps = createMap(contactList);
        personAdapter.updateList(contactList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        sendButton = (MenuItem) menu.findItem(R.id.main_action_send);
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
        alertDialogBuilder.setTitle(getResources().getString(R.string.main_dialog_title));
        alertDialogBuilder
                .setMessage(getResources().getString(R.string.main_dialog_message))
                .setCancelable(false)
                .setIcon(R.drawable.icon_ball)
                .setPositiveButton(getResources().getString(R.string.main_dialog_bySMSButton),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                        List<List<Person>> allPosiblesSolutions = draw();
                        if(allPosiblesSolutions.size()==0) {
                            AlertUtils.showOKDialog(activity,getResources().getString(R.string.main_dialog_fail_title),getResources().getString(R.string.main_dialog_fail_message));

                        } else {
                            List<Person> randomSolution = selectSolution(allPosiblesSolutions);
                            sendSMS(randomSolution);
                            AlertUtils.showOKDialog(activity,getResources().getString(R.string.main_dialog_success_title),getResources().getString(R.string.main_dialog_success_message));
                        }

                    }
                })
                .setNegativeButton(getResources().getString(R.string.main_dialog_byMAILButton) ,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                        List<List<Person>> allPosiblesSolutions = draw();
                        if(allPosiblesSolutions.size()==0) {
                            AlertUtils.showOKDialog(activity,getResources().getString(R.string.main_dialog_fail_title),getResources().getString(R.string.main_dialog_fail_message));
                        } else {
                            AlertUtils.showOKDialog(activity,getResources().getString(R.string.main_dialog_success_title),getResources().getString(R.string.main_dialog_success_message));
                        }


                        dialog.cancel();
                    }
                })
                .setNeutralButton(getResources().getString(R.string.cancel),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public List<List<Person>> draw(){
        Graph graph = createGraph(allPersonsMaps);
        Person startPerson = contactList.get(random.nextInt(contactList.size()));
        return graph.findHamiltonianCycles(startPerson);
    }

    public List<Person> selectSolution(List<List<Person>> cycles) {
        int randomSolution = random.nextInt(cycles.size());
        return cycles.get(randomSolution);
    }


    public HashMap<Integer, Person> createMap(List<Person> personList) {

        HashMap<Integer, Person> resultMap = new HashMap<Integer, Person>();
        for(Person person: personList) {
            resultMap.put(person.getId(), person);
        }

        for(HashMap.Entry<Integer, Person> entry : resultMap.entrySet()) {
            Person person = entry.getValue();
            List<Integer> candidates = sqLiteDB.getActualCandidates(person);
            for(Integer idCandidate : candidates) {
                person.addCandidates(resultMap.get(idCandidate));
            }
        }

        return resultMap;
    }

    public Graph createGraph(HashMap<Integer, Person> map) {
        Graph graph = new Graph();
        for(HashMap.Entry<Integer, Person> entry : map.entrySet()) {
            graph.addPerson(entry.getValue());
        }
        return graph;
    }

    public void sendSMS(List<Person> resultList) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            for(int index=0; index<resultList.size(); index++) {
                String sms = String.format(getResources().getString(R.string.main_dialog_smsTemplate), resultList.get(index).getName(), resultList.get(index+1).getName());
                smsManager.sendTextMessage(resultList.get(index).getPhone(), null, sms, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void checkForSmsPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.SEND_SMS))
            {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.SEND_SMS}, 1);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.SEND_SMS}, 1);
            }

        }
    }



}
