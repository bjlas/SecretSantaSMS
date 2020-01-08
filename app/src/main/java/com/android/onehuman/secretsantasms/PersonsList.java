package com.android.onehuman.secretsantasms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.android.onehuman.secretsantasms.broadcasts.DeliveredReceiver;
import com.android.onehuman.secretsantasms.broadcasts.SentReceiver;
import com.android.onehuman.secretsantasms.adapter.PersonAdapter;
import com.android.onehuman.secretsantasms.database.DBController;
import com.android.onehuman.secretsantasms.dialog.DialogUtils;
import com.android.onehuman.secretsantasms.graph.GraphUtils;
import com.android.onehuman.secretsantasms.model.Group;
import com.android.onehuman.secretsantasms.model.Person;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
    private DialogUtils dialogUtils;


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

        dialogUtils = DialogUtils.getInstance(activity);

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(sentReceiver, new IntentFilter(SENT));
        registerReceiver(deliveredReceiver, new IntentFilter(DELIVERED));
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
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
                showSendDialog();
                return true;
            } else {
                return false;
            }

        }
        if (id == R.id.menu_edit_group_action_delete) {

            dialogUtils.deleteGroupDialog(activity, group);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkValidData() {
        //There is less than two persons in the group
        if(allPersonsMaps.size() < 2) {
            dialogUtils.okDialog(activity,getResources().getString(R.string.main_dialog_fail_title), getResources().getString(R.string.main_dialog_fail_only_two_candidates));
            return false;
        }

        //One candidate cannot gift anyone
        for(HashMap.Entry<Integer, Person> entry : allPersonsMaps.entrySet()) {
            if(entry.getValue().getCandidates().size() == 0) {
                dialogUtils.okDialog(activity,getResources().getString(R.string.main_dialog_fail_title), String.format(getResources().getString(R.string.main_dialog_fail_number_of_candidates), entry.getValue().getName()));
                return false;
            }
        }


        return true;
    }

    public void showSendDialog() {

        List<Person> solution = GraphUtils.draw(allPersonsMaps, personList.get(random.nextInt(personList.size())));
        if(solution.size()==0) {
            dialogUtils.okDialog(activity,getResources().getString(R.string.main_dialog_fail_title),getResources().getString(R.string.main_dialog_fail_message));
        } else {
            dialogUtils.sendDialog(activity, group, solution);
        }

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




    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
