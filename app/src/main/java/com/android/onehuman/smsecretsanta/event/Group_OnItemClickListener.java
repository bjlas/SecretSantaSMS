package com.android.onehuman.smsecretsanta.event;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.android.onehuman.smsecretsanta.EditPerson;
import com.android.onehuman.smsecretsanta.PersonActivity;
import com.android.onehuman.smsecretsanta.model.Group;
import com.android.onehuman.smsecretsanta.model.Person;


public class Group_OnItemClickListener implements AdapterView.OnClickListener {

    private Context context;
    private Group group;

    public Group_OnItemClickListener(Context c, Group g) {
        context = c;
        this.group=g;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, PersonActivity.class);
        intent.putExtra(PersonActivity.class.getSimpleName(), group);
        context.startActivity(intent);
    }

}
