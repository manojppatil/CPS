package com.bits.cps;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bits.cps.Helper.DialogBox;
import com.bits.cps.Helper.L;
import com.bits.cps.Helper.Routes;
import com.bits.cps.Helper.SharedPreferencesWork;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rahman.dialog.Activity.SmartDialog;
import com.rahman.dialog.ListenerCallBack.SmartDialogClickListener;
import com.rahman.dialog.Utilities.SmartDialogBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class ProfilePageTL extends AppCompatActivity {

    TextView name, contact, mintime, maxtime, address;
    HashMap hashMap;
    String id;
    private int inserted_id;
    Context context;
    CircleImageView show_profile;
    String profile_pic;
//    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
//    Uri imageuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page_tl);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        SharedPreferencesWork sharedPreferencesWork = new SharedPreferencesWork(ProfilePageTL.this);
        hashMap = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, "id");
        id = hashMap.get("id").toString();
        L.L(id + "----");
        name = findViewById(R.id.show_name_tl);
        contact = findViewById(R.id.show_contact);
        mintime = findViewById(R.id.show_min_time);
        maxtime = findViewById(R.id.show_max_time);
        address = findViewById(R.id.show_add);
        show_profile = findViewById(R.id.show_profile_image);

        RequestParams requestParams = new RequestParams();
        requestParams.add("id", id);
        requestParams.add("tbname", "user");

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.selectOne, requestParams, new AsyncHttpResponseHandler() {
            AlertDialog dialog = new SpotsDialog(ProfilePageTL.this, R.style.Custom);

            @Override
            public void onStart() {
                dialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                String str = new String(responseBody);
                str = str.replace("<br>", "\n");
                JSONArray jr = null;
                JSONObject jo = null;

                L.L(str);
                try {
                    if (statusCode == 200) {
                        try {
                            jr = new JSONArray(str);
                            jo = jr.getJSONObject(0);

                            name.setText(jo.getString("name"));
                            contact.setText(jo.getString("contact"));
                            mintime.setText(jo.getString("in_time"));
                            maxtime.setText(jo.getString("out_time"));
                            address.setText(jo.getString("address"));
                            profile_pic = jo.getString("image");
                            Picasso.get().load("http://cpsgroups.in/img/user/" + profile_pic).placeholder(R.drawable.prof2).into(show_profile);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        L.L(statusCode + str);
                        new DialogBox(ProfilePageTL.this, str).asyncDialogBox();
                    }
                } catch (Exception ex) {
                    L.L(ex.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                new SmartDialogBuilder(ProfilePageTL.this)
                        .setTitle("Please Retry...")
                        .setSubTitle("Make sure your device has an active Internet Connection.")
                        .setCancalable(false)
                        .setNegativeButtonHide(true) //hide cancel button
                        .setPositiveButton("OK", new SmartDialogClickListener() {
                            @Override
                            public void onClick(SmartDialog smartDialog) {
                                smartDialog.dismiss();
                                dialog.dismiss();
                            }
                        }).build().show();
            }
        });
    }

//    public void selectImage(View view) {
//        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(ProfilePageTL.this);
//        builder.setTitle("Add Photo!");
//        builder.setItems(options, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int item) {
//                if (options[item].equals("Take Photo")) {
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(intent, PICK_IMAGE_CAMERA);
//                } else if (options[item].equals("Choose from Gallery")) {
//                    Intent intent = new Intent();
//                    intent.setType("image/*");
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    startActivityForResult(intent, PICK_IMAGE_GALLERY);
//                } else if (options[item].equals("Cancel")) {
//                    dialog.dismiss();
//                }
//            }
//        });
//        builder.show();
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_CAMERA) {
//            if (data != null) {
//                try {
//                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//                    show_profile.setImageBitmap(bitmap);
//                    show_profile.setVisibility(Button.VISIBLE);
//                    Uri tempUri = getImageUri(getApplicationContext(), bitmap);
//                    final File finalFile = new File(getRealPathFromURI(tempUri));
//                    L.L(finalFile + "");
//                    L.L(id+"-------------");
//                    RequestParams requestParams = new RequestParams();
//                    requestParams.put("image", finalFile);
//                    requestParams.add("id", id);
//                    requestParams.add("tbname", "user");
//
//                    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
//                    asyncHttpClient.post(Routes.update, requestParams, new AsyncHttpResponseHandler() {
//                        AlertDialog dialog = new SpotsDialog(ProfilePageTL.this, R.style.Custom);
//
//                        @Override
//                        public void onStart() {
//                            dialog.show();
//                        }
//
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                            dialog.dismiss();
//                            String str = new String(responseBody);
//                            str = str.replace("<br>", "\n");
//                            JSONArray jr = null;
//                            JSONObject jo = null;
//
//                            L.L(str);
//                            try {
//                                if (statusCode == 200) {
//                                    try {
//                                        jo = new JSONObject(str);
//                                        if (jo.getString("status").equals("success")) {
//                                            inserted_id = Integer.parseInt(jo.getString("recentinsertedid"));
//                                            HashMap hm = new HashMap();
//                                            hm.put("profile_pic", finalFile);
//                                            new SharedPreferencesWork(ProfilePageTL.this).insertOrReplace(hm, Routes.sharedPrefForLogin);
//                                            Toast.makeText(ProfilePageTL.this, "Profile picture is updated Successfully", Toast.LENGTH_SHORT).show();
//                                        } else {
//                                            Toast.makeText(context, "Data not inserted " + jo.getString("status"), Toast.LENGTH_SHORT).show();
//                                            new DialogBox(ProfilePageTL.this, jo.get("message").toString()).asyncDialogBox();
//
//                                        }
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                } else {
//                                    L.L(statusCode + str);
//                                    new DialogBox(ProfilePageTL.this, str).asyncDialogBox();
//                                }
//                            } catch (Exception ex) {
//                                L.L(ex.toString());
//                            }
//
//                        }
//
//                        @Override
//                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                            new SmartDialogBuilder(ProfilePageTL.this)
//                                    .setTitle("Please Retry...")
//                                    .setSubTitle("Make sure your device has an active Internet Connection.")
//                                    .setCancalable(false)
//                                    .setNegativeButtonHide(true) //hide cancel button
//                                    .setPositiveButton("OK", new SmartDialogClickListener() {
//                                        @Override
//                                        public void onClick(SmartDialog smartDialog) {
//                                            smartDialog.dismiss();
//                                        }
//                                    }).build().show();
//                        }
//                    });
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        } else if (requestCode == PICK_IMAGE_GALLERY) {
//            if (data != null) {
//                try {
//                    imageuri = data.getData();
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri);
//                    show_profile.setImageBitmap(bitmap);
//                    Uri tempUri = getImageUri(getApplicationContext(), bitmap);
//                    final File finalFile = new File(getRealPathFromURI(tempUri));
//                    L.L(finalFile + "");
//
//                    RequestParams requestParams = new RequestParams();
//                    requestParams.put("image", finalFile);
//                    requestParams.put("id", id);
//                    requestParams.put("tbname", "user");
//
//                    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
//                    asyncHttpClient.post(Routes.update, requestParams, new AsyncHttpResponseHandler() {
//                        AlertDialog dialog = new SpotsDialog(ProfilePageTL.this, R.style.Custom);
//
//                        @Override
//                        public void onStart() {
//                            dialog.show();
//                        }
//
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                            dialog.dismiss();
//                            String str = new String(responseBody);
//                            str = str.replace("<br>", "\n");
//                            JSONArray jr = null;
//                            JSONObject jo = null;
//
//                            L.L(str);
//                            try {
//                                if (statusCode == 200) {
//                                    try {
//                                        jo = new JSONObject(str);
//                                        if (jo.getString("status").equals("success")) {
//                                            inserted_id = Integer.parseInt(jo.getString("recentinsertedid"));
//                                            HashMap hm = new HashMap();
//                                            hm.put("profile_pic", finalFile);
//                                            new SharedPreferencesWork(ProfilePageTL.this).insertOrReplace(hm, Routes.sharedPrefForLogin);
//                                            Toast.makeText(ProfilePageTL.this, "Profile picture is updated Successfully", Toast.LENGTH_SHORT).show();
//                                        } else {
//                                            Toast.makeText(context, "Data not inserted " + jo.getString("status"), Toast.LENGTH_SHORT).show();
//                                            new DialogBox(ProfilePageTL.this, jo.get("message").toString()).asyncDialogBox();
//
//                                        }
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                } else {
//                                    L.L(statusCode + str);
//                                    new DialogBox(ProfilePageTL.this, str).asyncDialogBox();
//                                }
//                            } catch (Exception ex) {
//                                L.L(ex.toString());
//                            }
//
//                        }
//
//                        @Override
//                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                            new SmartDialogBuilder(ProfilePageTL.this)
//                                    .setTitle("Please Retry...")
//                                    .setSubTitle("Make sure your device has an active Internet Connection.")
//                                    .setCancalable(false)
//                                    .setNegativeButtonHide(true) //hide cancel button
//                                    .setPositiveButton("OK", new SmartDialogClickListener() {
//                                        @Override
//                                        public void onClick(SmartDialog smartDialog) {
//                                            smartDialog.dismiss();
//                                        }
//                                    }).build().show();
//                        }
//                    });
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    public Uri getImageUri(Context inContext, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
//        return Uri.parse(path);
//    }
//
//    public String getRealPathFromURI(Uri uri) {
//        String path = "";
//        if (getContentResolver() != null) {
//            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//            if (cursor != null) {
//                cursor.moveToFirst();
//                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//                path = cursor.getString(idx);
//                cursor.close();
//            }
//        }
//        return path;
//    }

}
