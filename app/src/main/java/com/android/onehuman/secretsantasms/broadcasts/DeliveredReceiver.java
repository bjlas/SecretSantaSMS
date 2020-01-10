package com.android.onehuman.secretsantasms.broadcasts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.android.onehuman.secretsantasms.R;
import com.android.onehuman.secretsantasms.dialog.DialogUtils;

public class DeliveredReceiver extends BroadcastReceiver {

    private DialogUtils dialogUtils;

    public DeliveredReceiver(Context c) {
        this.dialogUtils = DialogUtils.getInstance(c);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String personName = intent.getStringExtra("package.DeliveryReport.name");
        String phoneNumber = intent.getStringExtra("package.DeliveryReport.phoneNumber");
        String message = "";
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                message = String.format(context.getResources().getString(R.string.sms_dialog_ok_delivered), phoneNumber, personName);
                dialogUtils.okDialog(context, context.getResources().getString(R.string.main_dialog_bySMSButton), message);
                break;

            case Activity.RESULT_CANCELED:
                message = String.format(context.getResources().getString(R.string.sms_dialog_error_delivered), phoneNumber, personName);
                dialogUtils.okDialog(context, context.getResources().getString(R.string.main_dialog_validation_error), message);
                break;
        }
    }


}
