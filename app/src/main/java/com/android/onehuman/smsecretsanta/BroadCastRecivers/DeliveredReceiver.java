package com.android.onehuman.smsecretsanta.BroadCastRecivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.onehuman.smsecretsanta.event.AlertUtils;

public class DeliveredReceiver extends BroadcastReceiver {

    private Context context;

    public DeliveredReceiver(Context c) {
        this.context = c;
    }

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                break;
            case Activity.RESULT_CANCELED:
                AlertUtils.showOKDialog(context, "ERROR", "SMS error");
                break;
        }
    }


}
