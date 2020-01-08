package com.android.onehuman.secretsantasms.event;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import com.android.onehuman.secretsantasms.dialog.DialogUtils;
import com.android.onehuman.secretsantasms.model.Group;
import com.android.onehuman.secretsantasms.model.Person;


public class Resend_OnItemClickListener implements AdapterView.OnClickListener {

    private Context context;
    private Group group;
    private Person person;
    private DialogUtils dialogUtils;

    public Resend_OnItemClickListener(Context c, Group g, Person p) {
        context = c;
        this.group=g;
        this.person=p;
        this.dialogUtils = DialogUtils.getInstance(c);
    }

    @Override
    public void onClick(View view) {
        dialogUtils.resendDialog(context, group, person);
    }

}
