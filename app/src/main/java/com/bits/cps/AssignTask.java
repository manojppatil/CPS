package com.bits.cps;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class AssignTask extends AppCompatActivity {
    Spinner emp_spinner;
    EditText client_name, client_contact, client_email, client_address, client_meeting_time, client_amount, client_remark;
    private int inserted_id;
    Context context;
    String format;
    HashMap ssid;
    String tl_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_task);
        setTitle("Assign Task");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3a3dff")));
        SharedPreferencesWork sharedPreferencesWork = new SharedPreferencesWork(AssignTask.this);
        ssid = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, "id");
        tl_id = ssid.get("id").toString();
        emp_spinner = findViewById(R.id.select_emp);
        client_name = findViewById(R.id.client_name);
        client_contact = findViewById(R.id.client_contact);
        client_email = findViewById(R.id.client_email);
        client_address = findViewById(R.id.client_address);
        client_meeting_time = findViewById(R.id.client_meeting_time);
        client_amount = findViewById(R.id.client_amount);
        client_remark = findViewById(R.id.client_remark);
        getEmpID();
    }

    public void assign_task(View view) {
        String Select_emp = emp_spinner.getSelectedItem().toString();
        String Name = client_name.getText().toString();
        String Contact = client_contact.getText().toString();
        String Email = client_email.getText().toString();
        String Address = client_address.getText().toString();
        String Meeting_time = client_meeting_time.getText().toString();
        String Amount = client_amount.getText().toString();
        String Remark = client_remark.getText().toString();
        final String array[] = Select_emp.split("\\:");

        RequestParams requestParams = new RequestParams();
        requestParams.add("user_id", array[0]);
        requestParams.add("user_name", array[1]);
        requestParams.add("client_name", Name);
        requestParams.add("client_contact", Contact);
        requestParams.add("client_email", Email);
        requestParams.add("client_address", Address);
        requestParams.add("meeting_time", Meeting_time);
        requestParams.add("amount", Amount);
        requestParams.add("remark", Remark);
        requestParams.add("team_leader_id",tl_id);
        requestParams.add("tbname", "task");

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.insert2, requestParams, new AsyncHttpResponseHandler() {
            AlertDialog dialog = new SpotsDialog(AssignTask.this, R.style.Custom);

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
                                new SmartDialogBuilder(AssignTask.this)
                                        .setTitle("Success")
                                        .setSubTitle("Task is Successfully assigned to Employee:"+array[1])
                                        .setCancalable(false)
                                        .setNegativeButtonHide(true) //hide cancel button
                                        .setPositiveButton("OK", new SmartDialogClickListener() {
                                            @Override
                                            public void onClick(SmartDialog smartDialog) {
                                                Toast.makeText(AssignTask.this, "Thank you", Toast.LENGTH_SHORT).show();
                                                smartDialog.dismiss();
                                            }
                                        }).build().show();
                                Toast.makeText(AssignTask.this, "Successfully Assigned", Toast.LENGTH_SHORT).show();
                                client_name.setText("");
                                client_address.setText("");
                                client_contact.setText("");
                                client_email.setText("");
                                client_meeting_time.setText("");
                                client_amount.setText("");
                                client_remark.setText("");

                            } else {
                                Toast.makeText(context, "Data not inserted " + jo.getString("status"), Toast.LENGTH_SHORT).show();
                                new DialogBox(AssignTask.this, jo.get("message").toString()).asyncDialogBox();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        L.L(statusCode + str);
                        new DialogBox(AssignTask.this, str).asyncDialogBox();
                    }
                } catch (Exception ex) {
                    L.L(ex.toString());
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                new SmartDialogBuilder(AssignTask.this)
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

    public void getEmpID() {
        RequestParams requestParams = new RequestParams();
        requestParams.add("tbname", "user");
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.selectAllEmployee, requestParams, new AsyncHttpResponseHandler() {
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
                            arrayList.add(jo.getInt("id") + ": " + jo.getString("name"));
                        }
                        ArrayAdapter<String> arrayAdapterid = new ArrayAdapter<String>(AssignTask.this, android.R.layout.simple_spinner_item, arrayList);
                        arrayAdapterid.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        emp_spinner.setAdapter(arrayAdapterid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                new SmartDialogBuilder(AssignTask.this)
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

    public void selectTime(View view) {
        final EditText time = (EditText) view;
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        final int mHour = c.get(Calendar.HOUR_OF_DAY);
        final int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if (hourOfDay == 0) {
                            hourOfDay += 12;
                            format = "AM";
                        } else if (hourOfDay == 12) {
                            format = "PM";
                        } else if (hourOfDay > 12) {
                            hourOfDay -= 12;
                            format = "PM";
                        } else {
                            format = "AM";
                        }
                        time.setText(hourOfDay + ":" + minute + format);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

}
