package com.android.onehuman.smsecretsanta;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.android.onehuman.smsecretsanta.broadcasts.DeliveredReceiver;
import com.android.onehuman.smsecretsanta.broadcasts.SentReceiver;
import com.android.onehuman.smsecretsanta.adapter.PersonAdapter;
import com.android.onehuman.smsecretsanta.database.DBController;
import com.android.onehuman.smsecretsanta.event.AlertUtils;
import com.android.onehuman.smsecretsanta.graph.Graph;
import com.android.onehuman.smsecretsanta.model.Group;
import com.android.onehuman.smsecretsanta.model.Person;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PersonsList extends AppCompatActivity {

    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    private RecyclerView personListRecyclerView;
    private PersonAdapter personAdapter;
    private LinearLayoutManager linearLayoutManager;
    private DBController dbController;
    private List<Person> personList;
    private Random random;
    private HashMap<Integer, Person> allPersonsMaps;
    private Activity activity;
    private SentReceiver sentReceiver;
    private DeliveredReceiver deliveredReceiver;
    private Group group;
    private FloatingActionButton personFAB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persons_list);

        dbController = new DBController(this);
        personListRecyclerView = (RecyclerView) findViewById(R.id.personList_recyclerView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        linearLayoutManager = new LinearLayoutManager(this);
        personAdapter = new PersonAdapter(this);
        personListRecyclerView.setLayoutManager(linearLayoutManager);
        personListRecyclerView.setAdapter(personAdapter);

        this.activity=this;
        random = new Random();

        personFAB = findViewById(R.id.personList_fab_add_person);
        personFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, EditPerson.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("group", group);
                intent.putExtras(bundle);
                activity.startActivity(intent);
            }
        });

        sentReceiver = new SentReceiver(activity);
        deliveredReceiver = new DeliveredReceiver(activity);
        registerReceiver(sentReceiver, new IntentFilter(SENT));
        registerReceiver(deliveredReceiver, new IntentFilter(DELIVERED));
    }



    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(sentReceiver);
        unregisterReceiver(deliveredReceiver);
    }

    void initData(){

        if (getIntent().getExtras() != null) {
            group = getIntent().getExtras().getParcelable("group");
        }

        if(group != null){
            group = dbController.getGroup(group.getGroupID());
            setTitle(group.getGroupName());
            personAdapter.setGroup(group);
        }

        personList = dbController.getAllPersons(group.getGroupID());
        allPersonsMaps = createMap(personList);
        personAdapter.updateList(personList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_group_action_edit) {

            Intent intent = new Intent(activity, EditGroup.class);
            intent.putExtra("group", group);
            activity.startActivityForResult(intent, 1);


            return true;
        }
        if (id == R.id.menu_edit_group_action_send) {
            if(checkValidData()) {
                showDialog();
                return true;
            } else {
                return false;
            }

        }
        if (id == R.id.menu_edit_group_action_delete) {

            AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.delete))
                    .setMessage(String.format(getResources().getString(R.string.edit_dialog_deleted_group), group.getGroupName()))
                    .setIcon(R.drawable.icon_candy)

                    .setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            List<Integer> allPersonsOfGroup = dbController.getAllPersonsOfAGroup(group.getGroupID());

                            dbController.deleteAllGroupPersons(group.getGroupID());

                            for(int personID: allPersonsOfGroup) {
                                dbController.deleteAllForbiddenRulesFromPerson(personID);
                                dbController.deletePerson(personID);
                            }
                            dbController.deleteGroup(group.getGroupID());

                            Toast.makeText(activity, getResources().getString(R.string.edit_deleted), Toast.LENGTH_SHORT).show();
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
        return super.onOptionsItemSelected(item);
    }

    private boolean checkValidData() {
        if(allPersonsMaps.size() < 2) {
            AlertUtils.showOKDialog(activity,getResources().getString(R.string.main_dialog_fail_title), getResources().getString(R.string.main_dialog_fail_only_two_candidates));
            return false;
        }

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
        Person startPerson = personList.get(random.nextInt(personList.size()));
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

            List<Person> candidates = dbController.getCandidates(person, group);
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




    public void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        SmsManager sms = SmsManager.getDefault();
        if (group.getMaxPrice() != null) {
            message = message + String.format(getResources().getString(R.string.main_dialog_sms_plus_maxPrice), group.getMaxPrice());
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        //sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
