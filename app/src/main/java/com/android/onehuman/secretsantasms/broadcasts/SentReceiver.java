package com.android.onehuman.secretsantasms.broadcasts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import com.android.onehuman.secretsantasms.event.AlertUtils;

public class SentReceiver extends BroadcastReceiver {

    private Context context;

    public SentReceiver(Context c) {
        this.context = c;
    }

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                AlertUtils.showOKDialog(context, "ERROR", "SMS error");
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                AlertUtils.showOKDialog(context, "ERROR", "No service");
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                AlertUtils.showOKDialog(context, "ERROR", "SMS error");
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                AlertUtils.showOKDialog(context, "ERROR", "SMS error");
                break;
        }
    }


}
