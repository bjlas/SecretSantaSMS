package com.android.onehuman.smsecretsanta.event;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.android.onehuman.smsecretsanta.R;

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