package com.android.onehuman.secretsantasms.event;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.android.onehuman.secretsantasms.PersonsList;
import com.android.onehuman.secretsantasms.model.Group;


public class Group_OnItemClickListener implements AdapterView.OnClickListener {

    private Context context;
    private Group group;

    public Group_OnItemClickListener(Context c, Group g) {
        context = c;
        this.group=g;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, PersonsList.class);
        intent.putExtra("group", group);
        context.startActivity(intent);
    }

}
