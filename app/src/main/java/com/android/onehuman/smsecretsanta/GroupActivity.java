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

import com.android.onehuman.smsecretsanta.adapter.GroupAdapter;
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
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

public class GroupActivity extends AppCompatActivity {

    private final int REQUEST_READ_PHONE_STATE=1;
    private RecyclerView groupRecyclerView;
    private GroupAdapter groupAdapter;
    private LinearLayoutManager linearLayoutManager;
    private DBController dbController;
    private List<Group> groupList;
    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.activity=this;

        FloatingActionButton fab = findViewById(R.id.main_button_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "Add Group", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(activity, PersonActivity.class);
                //activity.startActivity(intent);
            }
        });

        dbController = new DBController(this);
        groupRecyclerView = (RecyclerView) findViewById(R.id.group_recyclerView);

        linearLayoutManager = new LinearLayoutManager(this);
        groupAdapter = new GroupAdapter(this);
        groupRecyclerView.setLayoutManager(linearLayoutManager);
        groupRecyclerView.setAdapter(groupAdapter);

        checkForSmsPermission();

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    void loadData(){
        groupList = dbController.getAllGroups();
        Group example = new Group();
        example.setGroupID(1);
        example.setGroupName("Familia");
        example.setMaxPrice("50");
        groupList.add(example);
        groupAdapter.updateList(groupList);
    }

    private void checkForSmsPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(GroupActivity.this, Manifest.permission.SEND_SMS))
            {
                ActivityCompat.requestPermissions(GroupActivity.this, new String[] {Manifest.permission.SEND_SMS}, 1);
            } else {
                ActivityCompat.requestPermissions(GroupActivity.this, new String[] {Manifest.permission.SEND_SMS}, 1);
            }

        }

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
        }
    }





}
