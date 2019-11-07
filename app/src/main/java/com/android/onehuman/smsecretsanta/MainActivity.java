package com.android.onehuman.smsecretsanta;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.android.onehuman.smsecretsanta.BroadCastRecivers.DeliveredReceiver;
import com.android.onehuman.smsecretsanta.BroadCastRecivers.SentReceiver;
import com.android.onehuman.smsecretsanta.adapter.PersonAdapter;
import com.android.onehuman.smsecretsanta.database.DBController;
import com.android.onehuman.smsecretsanta.event.AlertUtils;
import com.android.onehuman.smsecretsanta.graph.Graph;
import com.android.onehuman.smsecretsanta.model.Person;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    private final int REQUEST_READ_PHONE_STATE=1;
    private RecyclerView mainRecyclerView;
    private PersonAdapter personAdapter;
    private LinearLayoutManager linearLayoutManager;
    private DBController dbController;
    private List<Person> contactList;
    private Random random;
    private HashMap<Integer, Person> allPersonsMaps;
    private Activity activity;
    private SentReceiver sentReceiver;
    private DeliveredReceiver deliveredReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.main_button_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, EditActivity.class);
                activity.startActivity(intent);
            }
        });

        dbController = new DBController(this);
        mainRecyclerView = (RecyclerView) findViewById(R.id.main_recyclerView);

        linearLayoutManager = new LinearLayoutManager(this);
        personAdapter = new PersonAdapter(this);
        mainRecyclerView.setLayoutManager(linearLayoutManager);
        mainRecyclerView.setAdapter(personAdapter);

        this.activity=this;
        random = new Random();
        checkForSmsPermission();

        sentReceiver = new SentReceiver(activity);
        deliveredReceiver = new DeliveredReceiver(activity);
        registerReceiver(sentReceiver, new IntentFilter(SENT));
        registerReceiver(deliveredReceiver, new IntentFilter(DELIVERED));


    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(sentReceiver);
        unregisterReceiver(deliveredReceiver);
    }

    void loadData(){
        contactList = dbController.getAllPersons();
        allPersonsMaps = createMap(contactList);
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
        if (id == R.id.main_action_send) {
            if(checkValidData()) {
                showDialog();
                return true;
            } else {
                return false;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkValidData() {
        for(HashMap.Entry<Integer, Person> entry : allPersonsMaps.entrySet()) {
            if(entry.getValue().getCandidates().size() == 0) {
                AlertUtils.showOKDialog(activity,getResources().getString(R.string.main_dialog_fail_title), String.format(getResources().getString(R.string.main_dialog_fail_number_of_candidates), entry.getValue().getName()));
                return false;
            }
        }
        return true;
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
                            for(int index=0; index<randomSolution.size()-1; index++) {
                                String message = String.format(getResources().getString(R.string.main_dialog_smsTemplate), randomSolution.get(index).getName(), randomSolution.get(index+1).getName());
                                sendSMS(randomSolution.get(index).getPhone(), message);
                            }

                            AlertUtils.showOKDialog(activity,getResources().getString(R.string.main_dialog_success_title),getResources().getString(R.string.main_dialog_success_message));
                        }

                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel),new DialogInterface.OnClickListener() {
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

            List<Integer> forbiddens = dbController.getForbiddens(person);
            for(Integer idForbidden : forbiddens) {
                person.addForbidden(resultMap.get(idForbidden));
            }

            List<Person> candidates = dbController.getCandidates(person);
            for(Person candidate : candidates) {
                if(!forbiddens.contains(candidate.getId())) {
                    person.addCandidates(resultMap.get(candidate.getId()));
                }
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

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
        }
    }

    public void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }



}
