package com.android.onehuman.smsecretsanta.event;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.android.onehuman.smsecretsanta.EditActivity;
import com.android.onehuman.smsecretsanta.model.Person;


public class Main_OnItemClickListener implements AdapterView.OnClickListener {

    private Context context;
    private Person person;

    public Main_OnItemClickListener(Context c, Person p) {
        context = c;
        this.person=p;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra(EditActivity.class.getSimpleName(), person);
        context.startActivity(intent);
    }

}
