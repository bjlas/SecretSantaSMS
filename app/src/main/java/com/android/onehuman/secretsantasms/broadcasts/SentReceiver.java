package com.android.onehuman.secretsantasms.broadcasts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.android.onehuman.secretsantasms.R;
import com.android.onehuman.secretsantasms.dialog.DialogUtils;

public class SentReceiver extends BroadcastReceiver {

    private Context context;
    private DialogUtils dialogUtils;

    public SentReceiver(Context c) {
        this.context = c;
        this.dialogUtils = DialogUtils.getInstance(c);
    }

    @Override
    public void onReceive(Context arg0, Intent intent) {

        String personName = intent.getStringExtra("package.DeliveryReport.name");
        String phoneNumber = intent.getStringExtra("package.DeliveryReport.phoneNumber");
        String message = "";

        switch (getResultCode()) {
            case Activity.RESULT_OK:
                message = String.format(context.getResources().getString(R.string.sms_dialog_ok_delivered), phoneNumber, personName);
                dialogUtils.okDialog(context, context.getResources().getString(R.string.main_dialog_bySMSButton), message);
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
            case SmsManager.RESULT_ERROR_NO_SERVICE:
            case SmsManager.RESULT_ERROR_NULL_PDU:
            case SmsManager.RESULT_ERROR_RADIO_OFF:

                message = String.format(context.getResources().getString(R.string.sms_dialog_error_delivered), phoneNumber, personName);
                dialogUtils.okDialog(context, context.getResources().getString(R.string.main_dialog_validation_error), message);
                break;
        }





    }


}
