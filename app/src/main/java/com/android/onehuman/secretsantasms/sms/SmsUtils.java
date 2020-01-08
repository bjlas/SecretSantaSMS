package com.android.onehuman.secretsantasms.sms;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.android.onehuman.secretsantasms.R;

public class SmsUtils {

    private Context context;

    public SmsUtils(Context c) {
        this.context=c;
    }
    public void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED), 0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        //Toast.makeText(context, phoneNumber+" "+message, Toast.LENGTH_SHORT).show();
    }

}
