package com.bits.cps;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class AssignTask extends AppCompatActivity {
    Spinner emp_spinner;
    EditText client_name, client_contact, client_address, client_meeting_time, client_remark;
    private int inserted_id;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_task);
        setTitle("Assign Task");
        emp_spinner = findViewById(R.id.select_emp);
        client_name = findViewById(R.id.client_name);
        client_contact = findViewById(R.id.client_contact);
        client_address = findViewById(R.id.client_address);
        client_meeting_time = findViewById(R.id.client_meeting_time);
        client_remark = findViewById(R.id.client_remark);
        getEmpID();
    }

    public void assign_task(View view) {
        String Select_emp = emp_spinner.getSelectedItem().toString();
        String Name = client_name.getText().toString();
        String Contact = client_contact.getText().toString();
        String Address = client_address.getText().toString();
        String Meeting_time = client_meeting_time.getText().toString();
        String Remark = client_remark.getText().toString();

        RequestParams requestParams = new RequestParams();
        requestParams.add("user_id", Select_emp);
        requestParams.add("client_name", Name);
        requestParams.add("contact", Contact);
        requestParams.add("address", Address);
        requestParams.add("meeting_time", Meeting_time);
        requestParams.add("remark", Remark);
        requestParams.add("tbname", "Assigned_Task");

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
                                client_name.setText("");
                                client_address.setText("");
                                client_contact.setText("");
                                client_meeting_time.setText("");
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

            }
        });
    }

    public void getEmpID() {
        RequestParams requestParams = new RequestParams();
        requestParams.add("tbname", "user");
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.selectAll, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String string = new String(responseBody);
                JSONArray jr = null;
                JSONObject jo = null;
                L.L(string+"---------------");
                ArrayList arrayList = new ArrayList();
                if (statusCode == 200) {
                    try {
                        jr = new JSONArray(string);
                        for (int i = 0; i < jr.length(); i++) {
                            jo = jr.getJSONObject(i);
                            arrayList.add(jo.getString("name"));
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

            }
        });

    }
    public void selectTime(View view) {


        final EditText time = (EditText) view;
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        time.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

}
