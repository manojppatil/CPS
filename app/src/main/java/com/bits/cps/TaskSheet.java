package com.bits.cps;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

public class TaskSheet extends AppCompatActivity {
    String id;
    TextView show_name, show_add, show_contact, show_amount;
    EditText remark;
    Spinner task_status;
    private int inserted_id;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_sheet);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        L.L(id + "---+++++++");

        show_name = findViewById(R.id.show_name);
        show_add = findViewById(R.id.show_add);
        show_contact = findViewById(R.id.show_contact);
        show_amount = findViewById(R.id.task_amount);
        remark = findViewById(R.id.task_remark);
        task_status = findViewById(R.id.task_status);

        final RequestParams requestParams = new RequestParams();
        requestParams.add("id", id);
        requestParams.add("tbname", "task");

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.selectOne, requestParams, new AsyncHttpResponseHandler() {
            AlertDialog dialog = new SpotsDialog(TaskSheet.this, R.style.Custom);

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
                            show_add.setText(jo1.getString("client_address"));
                            show_contact.setText(jo1.getString("client_contact"));
                            show_amount.setText(jo1.getString("amount"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        new DialogBox(TaskSheet.this, str).asyncDialogBox();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable
                                          error) {
                new DialogBox(TaskSheet.this, responseBody.toString());
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

    public void task_sheet(View view) {
        String Remark = remark.getText().toString();
        String status = task_status.getSelectedItem().toString();

        RequestParams requestParams = new RequestParams();
        requestParams.add("remark", Remark);
        requestParams.add("status", status);
        requestParams.add("id", id);
        requestParams.add("tbname", "task");

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.update, requestParams, new AsyncHttpResponseHandler() {
            AlertDialog dialog = new SpotsDialog(TaskSheet.this, R.style.Custom);

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
                                new SmartDialogBuilder(TaskSheet.this)
                                        .setTitle("Success")
                                        .setSubTitle("Task is Updated Successfully.")
                                        .setCancalable(false)
                                        .setNegativeButtonHide(true) //hide cancel button
                                        .setPositiveButton("OK", new SmartDialogClickListener() {
                                            @Override
                                            public void onClick(SmartDialog smartDialog) {
                                                Toast.makeText(TaskSheet.this, "Thank you", Toast.LENGTH_SHORT).show();
                                                smartDialog.dismiss();
                                            }
                                        }).build().show();


                            } else {
                                Toast.makeText(context, "Data not inserted " + jo.getString("status"), Toast.LENGTH_SHORT).show();
                                new DialogBox(TaskSheet.this, jo.get("message").toString()).asyncDialogBox();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        L.L(statusCode + str);
                        new DialogBox(TaskSheet.this, str).asyncDialogBox();
                    }
                } catch (Exception ex) {
                    L.L(ex.toString());
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}
