package com.android.onehuman.smsecretsanta.event;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.android.onehuman.smsecretsanta.EditPerson;
import com.android.onehuman.smsecretsanta.model.Person;


public class Person_OnItemClickListener implements AdapterView.OnClickListener {

    private Context context;
    private Person person;

    public Person_OnItemClickListener(Context c, Person p) {
        context = c;
        this.person=p;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, EditPerson.class);
        intent.putExtra(EditPerson.class.getSimpleName(), person);
        context.startActivity(intent);
    }

}
