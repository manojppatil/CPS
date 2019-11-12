package com.bits.cps;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.bits.cps.Helper.DialogBox;
import com.bits.cps.Helper.L;
import com.bits.cps.Helper.Routes;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rahman.dialog.Activity.SmartDialog;
import com.rahman.dialog.ListenerCallBack.SmartDialogClickListener;
import com.rahman.dialog.Utilities.SmartDialogBuilder;
import com.rey.material.widget.CheckBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class Stamp_duty extends AppCompatActivity {
    Spinner stamp_client;
    CheckBox stamp100, stamp300, stamp1000;
    String str_100, str_300, str_1000;
    private int inserted_id;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp_duty);
        setTitle("Stamp Duty");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#34eb49")));
        stamp_client = findViewById(R.id.stamp_client);
        stamp100 = findViewById(R.id.stamp_100);
        stamp300 = findViewById(R.id.stamp_300);
        stamp1000 = findViewById(R.id.stamp_1000);

        getTaskID();
    }

    public void stamp_duty(View view) {
        String stamp_client_name = stamp_client.getSelectedItem().toString();
        final String array[] = stamp_client_name.split("\\:");

        if (stamp100.isChecked()) {
            str_100 = stamp100.getText().toString();
            RequestParams requestParams = new RequestParams();
            requestParams.add("stamp_duty_100", "Taken");
            requestParams.add("id", array[0]);
            requestParams.add("tbname", "task");

            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.post(Routes.update, requestParams, new AsyncHttpResponseHandler() {
                AlertDialog dialog = new SpotsDialog(Stamp_duty.this, R.style.Custom);

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
                                jo = new JSONObject(str);
                                if (jo.getString("status").equals("success")) {
                                    inserted_id = Integer.parseInt(jo.getString("recentinsertedid"));
                                    new SmartDialogBuilder(Stamp_duty.this)
                                            .setTitle("Success")
                                            .setSubTitle("Stamp Duty 100 is Taken.")
                                            .setCancalable(false)
                                            .setNegativeButtonHide(true) //hide cancel button
                                            .setPositiveButton("OK", new SmartDialogClickListener() {
                                                @Override
                                                public void onClick(SmartDialog smartDialog) {
                                                    Toast.makeText(Stamp_duty.this, "Thank you", Toast.LENGTH_SHORT).show();
                                                    smartDialog.dismiss();

                                                }
                                            }).build().show();


                                } else {
                                    Toast.makeText(context, "Data not inserted " + jo.getString("status"), Toast.LENGTH_SHORT).show();
                                    new DialogBox(Stamp_duty.this, jo.get("message").toString()).asyncDialogBox();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            L.L(statusCode + str);
                            new DialogBox(Stamp_duty.this, str).asyncDialogBox();
                        }
                    } catch (Exception ex) {
                        L.L(ex.toString());
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    new SmartDialogBuilder(Stamp_duty.this)
                            .setTitle("Please Retry...")
                            .setSubTitle("Make sure your device has an active Internet Connection.")
                            .setCancalable(false)
                            .setNegativeButtonHide(true) //hide cancel button
                            .setPositiveButton("OK", new SmartDialogClickListener() {
                                @Override
                                public void onClick(SmartDialog smartDialog) {
                                    smartDialog.dismiss();
                                }
                            }).build().show();
                }
            });
        }
        if (stamp300.isChecked()) {
            str_300 = stamp300.getText().toString();
            RequestParams requestParams = new RequestParams();
            requestParams.add("stamp_duty_300", "Taken");
            requestParams.add("id", array[0]);
            requestParams.add("tbname", "task");

            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.post(Routes.update, requestParams, new AsyncHttpResponseHandler() {
                AlertDialog dialog = new SpotsDialog(Stamp_duty.this, R.style.Custom);

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
                                jo = new JSONObject(str);
                                if (jo.getString("status").equals("success")) {
                                    inserted_id = Integer.parseInt(jo.getString("recentinsertedid"));
                                    new SmartDialogBuilder(Stamp_duty.this)
                                            .setTitle("Success")
                                            .setSubTitle("Stamp Duty 300 is Taken.")
                                            .setCancalable(false)
                                            .setNegativeButtonHide(true) //hide cancel button
                                            .setPositiveButton("OK", new SmartDialogClickListener() {
                                                @Override
                                                public void onClick(SmartDialog smartDialog) {
                                                    Toast.makeText(Stamp_duty.this, "Thank you", Toast.LENGTH_SHORT).show();
                                                    smartDialog.dismiss();
                                                }
                                            }).build().show();


                                } else {
                                    Toast.makeText(context, "Data not inserted " + jo.getString("status"), Toast.LENGTH_SHORT).show();
                                    new DialogBox(Stamp_duty.this, jo.get("message").toString()).asyncDialogBox();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            L.L(statusCode + str);
                            new DialogBox(Stamp_duty.this, str).asyncDialogBox();
                        }
                    } catch (Exception ex) {
                        L.L(ex.toString());
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    new SmartDialogBuilder(Stamp_duty.this)
                            .setTitle("Please Retry...")
                            .setSubTitle("Make sure your device has an active Internet Connection.")
                            .setCancalable(false)
                            .setNegativeButtonHide(true) //hide cancel button
                            .setPositiveButton("OK", new SmartDialogClickListener() {
                                @Override
                                public void onClick(SmartDialog smartDialog) {
                                    smartDialog.dismiss();
                                }
                            }).build().show();
                }
            });
        }
        if (stamp1000.isChecked()) {
            str_1000 = stamp1000.getText().toString();
            RequestParams requestParams = new RequestParams();
            requestParams.add("stamp_duty_1000", "Taken");
            requestParams.add("id", array[0]);
            requestParams.add("tbname", "task");

            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.post(Routes.update, requestParams, new AsyncHttpResponseHandler() {
                AlertDialog dialog = new SpotsDialog(Stamp_duty.this, R.style.Custom);

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
                                jo = new JSONObject(str);
                                if (jo.getString("status").equals("success")) {
                                    inserted_id = Integer.parseInt(jo.getString("recentinsertedid"));
                                    new SmartDialogBuilder(Stamp_duty.this)
                                            .setTitle("Success")
                                            .setSubTitle("Stamp Duty 1000 is Taken.")
                                            .setCancalable(false)
                                            .setNegativeButtonHide(true) //hide cancel button
                                            .setPositiveButton("OK", new SmartDialogClickListener() {
                                                @Override
                                                public void onClick(SmartDialog smartDialog) {
                                                    Toast.makeText(Stamp_duty.this, "Thank you", Toast.LENGTH_SHORT).show();
                                                    smartDialog.dismiss();
                                                }
                                            }).build().show();


                                } else {
                                    Toast.makeText(context, "Data not inserted " + jo.getString("status"), Toast.LENGTH_SHORT).show();
                                    new DialogBox(Stamp_duty.this, jo.get("message").toString()).asyncDialogBox();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            L.L(statusCode + str);
                            new DialogBox(Stamp_duty.this, str).asyncDialogBox();
                        }
                    } catch (Exception ex) {
                        L.L(ex.toString());
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    new SmartDialogBuilder(Stamp_duty.this)
                            .setTitle("Please Retry...")
                            .setSubTitle("Make sure your device has an active Internet Connection.")
                            .setCancalable(false)
                            .setNegativeButtonHide(true) //hide cancel button
                            .setPositiveButton("OK", new SmartDialogClickListener() {
                                @Override
                                public void onClick(SmartDialog smartDialog) {
                                    smartDialog.dismiss();
                                }
                            }).build().show();
                }
            });
        }
    }

    public void getTaskID() {
        RequestParams requestParams = new RequestParams();
        requestParams.add("tbname", "task");
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.selectAll, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String string = new String(responseBody);
                JSONArray jr = null;
                JSONObject jo = null;
                L.L(string + "---------------");
                ArrayList arrayList = new ArrayList();
                if (statusCode == 200) {
                    try {
                        jr = new JSONArray(string);
                        for (int i = 0; i < jr.length(); i++) {
                            jo = jr.getJSONObject(i);
                            arrayList.add(jo.getInt("id") + ": " + jo.getString("client_name"));
                        }
                        ArrayAdapter<String> arrayAdapterid = new ArrayAdapter<String>(Stamp_duty.this, android.R.layout.simple_spinner_item, arrayList);
                        arrayAdapterid.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        stamp_client.setAdapter(arrayAdapterid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }
}
