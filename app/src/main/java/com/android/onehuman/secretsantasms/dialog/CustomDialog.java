package com.android.onehuman.secretsantasms.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;

import com.android.onehuman.secretsantasms.R;


public class CustomDialog
{
        public static void showOKDialog(Context context, String title, String message)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setIcon(R.drawable.icon_tree);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
        }

}
