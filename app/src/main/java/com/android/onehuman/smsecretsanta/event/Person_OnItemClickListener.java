package com.android.onehuman.smsecretsanta.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.android.onehuman.smsecretsanta.EditPerson;
import com.android.onehuman.smsecretsanta.PersonsList;
import com.android.onehuman.smsecretsanta.model.Group;
import com.android.onehuman.smsecretsanta.model.Person;


public class Person_OnItemClickListener implements AdapterView.OnClickListener {

    private Context context;
    private Person person;
    private Group group;

    public Person_OnItemClickListener(Context c, Person p, Group g) {
        context = c;
        this.person=p;
        this.group=g;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, EditPerson.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("person", person);
        bundle.putParcelable("group", group);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

}
