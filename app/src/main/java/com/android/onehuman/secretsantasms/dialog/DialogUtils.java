package com.android.onehuman.secretsantasms.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onehuman.secretsantasms.R;
import com.android.onehuman.secretsantasms.database.DBController;
import com.android.onehuman.secretsantasms.model.Group;
import com.android.onehuman.secretsantasms.model.Person;
import com.android.onehuman.secretsantasms.sms.SmsUtils;

import java.util.List;


public class DialogUtils
{
    private static DialogUtils dialogUtils;
    private DBController dbController;
    private SmsUtils smsUtils;


    public static synchronized DialogUtils getInstance(Context c) {
        if (dialogUtils == null) {
            dialogUtils = new DialogUtils(c);
        }
        return dialogUtils;
    }

    public DialogUtils(Context c) {
        this.dbController = new DBController(c);
        this.smsUtils = new SmsUtils(c);
    }

    public void okDialog(Context context, String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(R.drawable.icon_tree);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

    public void deletePersonDialog(final Context context, final Group group, final Person person){

        AlertDialog.Builder deletePersonDialog = new AlertDialog.Builder(context);

        deletePersonDialog.setTitle(context.getResources().getString(R.string.delete));
        deletePersonDialog.setMessage(String.format(context.getResources().getString(R.string.edit_dialog_deleted_person), person.getName()));
        deletePersonDialog.setIcon(R.drawable.icon_candy);

        deletePersonDialog.setPositiveButton(context.getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                dbController.deletePerson(person.getId());
                dbController.deleteAllForbiddenRulesFromPerson(person.getId());
                dbController.deletePersonAsForbiddenOfOtherPersons(person.getId());
                dbController.deleteAPersonsOfAGroup(person.getId());

                if(dbController.existSolution(person.getId())) {
                    dbController.deleteSolution(group.getGroupID());
                }

                Toast.makeText(context, context.getResources().getString(R.string.edit_deleted), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                ((Activity) context).finish();
            }

        });

        deletePersonDialog.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        deletePersonDialog.create();
        deletePersonDialog.show();

    }


    public void deleteGroupDialog(final Context context, final Group group){

        AlertDialog.Builder deleteGroupDialog = new AlertDialog.Builder(context);
        deleteGroupDialog.setTitle(context.getResources().getString(R.string.delete));
        deleteGroupDialog.setMessage(String.format(context.getResources().getString(R.string.edit_dialog_deleted_group), group.getGroupName()));
        deleteGroupDialog.setIcon(R.drawable.icon_candy);

        deleteGroupDialog.setPositiveButton(context.getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                List<Integer> allPersonsOfGroup = dbController.getAllPersonsOfAGroup(group.getGroupID());

                dbController.deleteAllGroupPersons(group.getGroupID());
                dbController.deleteSolution(group.getGroupID());

                for(int personID: allPersonsOfGroup) {
                    dbController.deleteAllForbiddenRulesFromPerson(personID);
                    dbController.deletePerson(personID);
                }
                dbController.deleteGroup(group.getGroupID());

                Toast.makeText(context, context.getResources().getString(R.string.edit_deleted), Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                ((Activity) context).finish();
            }

        });

        deleteGroupDialog.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        deleteGroupDialog.create();
        deleteGroupDialog.show();

    }

    public void sendDialog(final Context context, final Group group, final List<Person> solution){

        AlertDialog.Builder sendDialog = new AlertDialog.Builder(context);
        sendDialog.setTitle(context.getResources().getString(R.string.main_dialog_title));
        sendDialog.setMessage(context.getResources().getString(R.string.main_dialog_message));
        sendDialog.setIcon(R.drawable.icon_ball);


        sendDialog.setPositiveButton(context.getResources().getString(R.string.main_dialog_bySMSButton),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                dbController.deleteSolution(group.getGroupID());
                for(int index=0; index<solution.size()-1; index++) {

                    smsUtils.sendSMS(solution.get(index).getPhone(), generateSMSMessage(context, group, solution.get(index).getName(), solution.get(index+1).getName()));
                    dbController.insertSolution(group.getGroupID(), solution.get(index).getId(), solution.get(index+1).getId());
                }
                dialogUtils.okDialog(context,context.getResources().getString(R.string.main_dialog_success_title),context.getResources().getString(R.string.main_dialog_success_message));
            }
        });

        sendDialog.setNegativeButton(context.getResources().getString(R.string.cancel),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                dialog.cancel();
            }
        });

        sendDialog.create();
        sendDialog.show();
    }

    public void resendDialog(final Context context, final Group group, final Person person){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_resend);

        TextView text = (TextView) dialog.findViewById(R.id.dialog_resend_text);
        text.setText(String.format(context.getResources().getString(R.string.resend_dialog_text), person.getName()));

        final EditText editTextPhone = (EditText) dialog.findViewById(R.id.dialog_resend_phone);
        editTextPhone.setText(person.getPhone());

        Button resendButton = (Button) dialog.findViewById(R.id.dialog_resend_resend_button);

        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(android.util.Patterns.PHONE.matcher(editTextPhone.getText().toString()).matches() ) {
                    smsUtils.sendSMS(editTextPhone.getText().toString(), generateSMSMessage(context, group, person.getName(), dbController.getPerson(person.getGiftTo()).getName()));
                    dialog.dismiss();
                } else {
                    dialogUtils.okDialog(context,context.getResources().getString(R.string.main_dialog_validation_error),context.getResources().getString(R.string.edit_validation_phone));
                }
            }
        });

        Button cancelButton = (Button) dialog.findViewById(R.id.dialog_resend_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public String generateSMSMessage(Context context, Group group, String personName, String giftTo) {
        String message = String.format(context.getResources().getString(R.string.main_dialog_smsTemplate), personName, giftTo);
        if (!"".equals(group.getMaxPrice())) {
            message = message + String.format(context.getResources().getString(R.string.main_dialog_sms_plus_maxPrice), group.getMaxPrice());
        }

        return message;
    }

}
