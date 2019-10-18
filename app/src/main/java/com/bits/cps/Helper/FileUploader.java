package com.bits.cps.Helper;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

public class FileUploader {
    final int PICKFILE_RESULT_CODE = 100;
    private String filepath;
    public Context context;
    public Activity activity;

    public FileUploader(Context context) {
        this.context = context;
        activity = (AppCompatActivity) context;
    }

    public void selctFile() {
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    PICKFILE_RESULT_CODE);

        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            activity.startActivityForResult(intent, PICKFILE_RESULT_CODE);
        }
    }

    //    call in onActivityResult
    public String returnPath(int requestCode, int resultCode, Intent data) {
        String id01 = null;
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == activity.RESULT_OK && data != null) {
                    Uri originalUri = data.getData();
                    id01 = W_ImgFilePathUtil.getPath(context, originalUri);
                    break;
                }
        }
        return id01;

    }
}
