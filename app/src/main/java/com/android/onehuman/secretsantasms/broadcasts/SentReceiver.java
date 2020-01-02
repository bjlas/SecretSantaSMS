package com.android.onehuman.secretsantasms.broadcasts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import com.android.onehuman.secretsantasms.dialog.DialogUtils;

public class SentReceiver extends BroadcastReceiver {

    private Context context;
    private DialogUtils dialogUtils;

    public SentReceiver(Context c) {
        this.context = c;
        this.dialogUtils = DialogUtils.getInstance(c);
    }

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                dialogUtils.okDialog(context, "ERROR", "SMS Generic failure");
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                dialogUtils.okDialog(context, "ERROR", "SMS No service");
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                dialogUtils.okDialog(context, "ERROR", "SMS Null PDU");
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                dialogUtils.okDialog(context, "ERROR", "SMS Radio off");
                break;
        }
    }


}
