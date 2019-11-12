package com.bits.cps;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class SRO_TaskSheet extends AppCompatActivity {
    String id;
    TextView show_name, show_office, show_bank, show_amount;
    Spinner task_status;
    private int inserted_id;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sro__task_sheet);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3a3dff")));
        final Intent intent = getIntent();
        id = intent.getStringExtra("id");
        L.L(id + "---+++++++");

        show_name = findViewById(R.id.show_sro_name);
        show_office = findViewById(R.id.show_sro_office);
        show_bank = findViewById(R.id.show_sro_bank);
        show_amount = findViewById(R.id.sro_sheet_amount);
        task_status = findViewById(R.id.sro_status);

        final RequestParams requestParams = new RequestParams();
        requestParams.add("id", id);
        requestParams.add("tbname", "task");

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.selectOne, requestParams, new AsyncHttpResponseHandler() {
            AlertDialog dialog = new SpotsDialog(SRO_TaskSheet.this, R.style.Custom);

            @Override
            public void onStart() {
                dialog.show();
            }

            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                L.L(responseBody + "");
                String str = new String(responseBody);
                L.L(str);
//                str = str.replace("<br>", "\n");
                JSONArray jr1 = null;
                JSONObject jo1 = null;
                try {
                    if (statusCode == 200) {
                        try {
                            jr1 = new JSONArray(str);
                            jo1 = jr1.getJSONObject(0);

                            show_name.setText(jo1.getString("client_name"));
                            show_office.setText(jo1.getString("SRO_office"));
                            show_bank.setText(jo1.getString("bank_name"));
                            show_amount.setText(jo1.getString("SRO_payment"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        new DialogBox(SRO_TaskSheet.this, str).asyncDialogBox();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                new SmartDialogBuilder(SRO_TaskSheet.this)
                        .setTitle("Please Retry...")
                        .setSubTitle("Make sure your device has an active Internet Connection.")
                        .setCancalable(false)
                        .setNegativeButtonHide(true) //hide cancel button
                        .setPositiveButton("OK", new SmartDialogClickListener() {
                            @Override
                            public void onClick(SmartDialog smartDialog) {
                                Intent intent1 = new Intent(SRO_TaskSheet.this,ShowAssignTaskByUser.class);
                                startActivity(intent1);
                                smartDialog.dismiss();
                            }
                        }).build().show();
            }

        });

        ArrayList<String> arrayList1 = new ArrayList<>();
        arrayList1.add("Completed");
        arrayList1.add("Pending");
        arrayList1.add("Cancelled");

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList1);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        task_status.setAdapter(arrayAdapter1);
    }

    public void sro_task_sheet(View view) {
        String status = task_status.getSelectedItem().toString();

        RequestParams requestParams = new RequestParams();
        requestParams.add("sro_status", status);
        requestParams.add("id", id);
        requestParams.add("tbname", "task");

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.update, requestParams, new AsyncHttpResponseHandler() {
            AlertDialog dialog = new SpotsDialog(SRO_TaskSheet.this, R.style.Custom);

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
                                new SmartDialogBuilder(SRO_TaskSheet.this)
                                        .setTitle("Success")
                                        .setSubTitle("Task is Updated Successfully.")
                                        .setCancalable(false)
                                        .setNegativeButtonHide(true) //hide cancel button
                                        .setPositiveButton("OK", new SmartDialogClickListener() {
                                            @Override
                                            public void onClick(SmartDialog smartDialog) {
                                                Intent intent = new Intent(SRO_TaskSheet.this,ShowSRO_task.class);
                                                startActivity(intent);
                                                finish();
                                                Toast.makeText(SRO_TaskSheet.this, "Thank you", Toast.LENGTH_SHORT).show();
                                                smartDialog.dismiss();
                                            }
                                        }).build().show();


                            } else {
                                Toast.makeText(context, "Data not inserted " + jo.getString("status"), Toast.LENGTH_SHORT).show();
                                new DialogBox(SRO_TaskSheet.this, jo.get("message").toString()).asyncDialogBox();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        L.L(statusCode + str);
                        new DialogBox(SRO_TaskSheet.this, str).asyncDialogBox();
                    }
                } catch (Exception ex) {
                    L.L(ex.toString());
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                new SmartDialogBuilder(SRO_TaskSheet.this)
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
