package com.android.onehuman.secretsantasms;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.android.onehuman.secretsantasms.adapter.GroupAdapter;
import com.android.onehuman.secretsantasms.database.DBController;
import com.android.onehuman.secretsantasms.model.Group;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.util.List;

public class GroupsList extends AppCompatActivity {

    private final int REQUEST_READ_PHONE_STATE=1;
    private RecyclerView groupRecyclerView;
    private GroupAdapter groupAdapter;
    private LinearLayoutManager linearLayoutManager;
    private DBController dbController;
    private List<Group> groupList;
    private Activity activity;
    private FloatingActionButton groupFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        this.activity=this;

        groupFAB = findViewById(R.id.groupList_fab_add_group);
        groupFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, EditGroup.class);
                activity.startActivity(intent);
            }
        });

        dbController = new DBController(this);
        groupRecyclerView = (RecyclerView) findViewById(R.id.groupList_recyclerView);

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
        groupAdapter.updateList(groupList);
    }

    private void checkForSmsPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(GroupsList.this, Manifest.permission.SEND_SMS))
            {
                ActivityCompat.requestPermissions(GroupsList.this, new String[] {Manifest.permission.SEND_SMS}, 1);
            } else {
                ActivityCompat.requestPermissions(GroupsList.this, new String[] {Manifest.permission.SEND_SMS}, 1);
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
