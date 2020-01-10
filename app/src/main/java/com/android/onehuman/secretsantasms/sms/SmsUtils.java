package com.android.onehuman.secretsantasms.sms;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.android.onehuman.secretsantasms.R;
import com.android.onehuman.secretsantasms.model.Group;
import com.android.onehuman.secretsantasms.model.Person;

import java.util.List;

public class SmsUtils {

    private Context context;

    public SmsUtils(Context c) {
        this.context = c;
    }


    public void sendMultiple(List<Person> solution, Group group) {

        for(int index=0; index<solution.size()-1; index++) {
            sendSMS(solution.get(index).getPhone(), generateSMSMessage(context, group, solution.get(index).getName(), solution.get(index+1).getName()), solution.get(index).getName());
        }
    }

    public void sendSMS(String phoneNumber, String message, String name) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        SmsManager sms = SmsManager.getDefault();

        Intent sentIntent = new Intent(SENT);
        sentIntent.putExtra("package.DeliveryReport.name", name);
        sentIntent.putExtra("package.DeliveryReport.phoneNumber", phoneNumber);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, sentIntent, PendingIntent.FLAG_ONE_SHOT);

        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }



    public String generateSMSMessage(Context context, Group group, String personName, String giftTo) {
        String message = String.format(context.getResources().getString(R.string.main_dialog_smsTemplate), personName, giftTo);
        if (!"".equals(group.getMaxPrice())) {
            message = message + String.format(context.getResources().getString(R.string.main_dialog_sms_plus_maxPrice), group.getMaxPrice());
        }

        return message;
    }



}

