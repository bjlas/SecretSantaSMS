package com.android.onehuman.secretsantasms.event;

import android.app.AlertDialog;
import android.content.Context;

import com.android.onehuman.secretsantasms.R;

public class AlertUtils
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