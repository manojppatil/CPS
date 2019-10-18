package com.bits.cps.Helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class DialogBox {
    Context context;
    String message;
    String Information;

    public DialogBox(Context context, String Information) {
        this.context = context;
        this.Information = Information;
    }

    public String asyncDialogBox() {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!((Activity) context).isFinishing()) {
                    new AlertDialog.Builder(context)
                            .setTitle("Message")
                            .setMessage(Information)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    message = "Accepted";

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    message = "Rejected";
                                }
                            })
                            .show();
                }
            }
        });
        return message;
    }
    public String asyncDialogBox(final Intent intent) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!((Activity) context).isFinishing()) {
                    new AlertDialog.Builder(context)
                            .setTitle("Message")
                            .setMessage(Information)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    message = "Accepted";
                                    ((Activity) context).finish();
                                    context.startActivity(intent);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    message = "Rejected";
                                }
                            })
                            .show();
                }
            }
        });
        return message;
    }

}
