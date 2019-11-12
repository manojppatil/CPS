package com.bits.cps;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class PunchActivity extends AppCompatActivity {
    Button attendance;
    Button sign_in_again;
    Context context;
    private int inserted_id;
    String currentDateandTime, currentDate;
    HashMap name, ssid, sstatus, ssdate;
    String username, id, status, login_date;
    String role;
    String addressLines;
    static TextView tv_check_connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punch);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3a3dff")));
        Intent roleintent = getIntent();
        role = roleintent.getStringExtra("role");
        L.L(role + "+++++++++++role");
        attendance = findViewById(R.id.btn_punch);
        sign_in_again = findViewById(R.id.sign_in_again_punch);
        tv_check_connection = findViewById(R.id.tv_check_connection);
        SharedPreferencesWork sharedPreferencesWork = new SharedPreferencesWork(PunchActivity.this);
        name = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, "name");
        ssid = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, "id");
        sstatus = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, ("status"));
        ssdate = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, ("login_date"));
        login_date = ssdate.get("login_date").toString();
        L.L(login_date + "------");
        SimpleDateFormat cd = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = cd.format(new Date());
        L.L(currentDate + "++++++++");
        if (login_date.equals(currentDate)) {
            attendance.setVisibility(View.GONE);
            sign_in_again.setVisibility(View.VISIBLE);
            tv_check_connection.setVisibility(View.VISIBLE);
            tv_check_connection.setText("You have Already PUNCH IN for today.");
            tv_check_connection.setBackgroundColor(Color.RED);
            tv_check_connection.setTextColor(Color.WHITE);
        }
        username = name.get("name").toString();
        id = ssid.get("id").toString();
        status = sstatus.get("status").toString();
        if (status.equals("active")) {
            if (role.equals("Team Leader")) {
                Intent intent = new Intent(PunchActivity.this, TL_activity.class);
                intent.putExtra("role","Team Leader");
                startActivity(intent);
            } else if (role.equals("Data Entry Operator")) {
                Intent intent = new Intent(PunchActivity.this, Activity_do.class);
                intent.putExtra("role","Data Entry Operator");
                startActivity(intent);
            }
        }

        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                punchAttendance();
            }
        });

        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.getIsGPSTrackingEnabled()) {
            addressLines = gpsTracker.getAddressLine(this);
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void punchAttendance() {

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        currentDateandTime = sdf.format(new Date());
        SimpleDateFormat cd = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = cd.format(new Date());

        final RequestParams requestParams = new RequestParams();
        requestParams.add("login_time", currentDateandTime);
        requestParams.add("user_id", id);
        requestParams.add("user_name", username);
        requestParams.add("login_status", "1");
        requestParams.add("login_place", addressLines);
        requestParams.add("login_date", currentDate);
        requestParams.add("status", "present");
        requestParams.add("tbname", "attendance");

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.insert2, requestParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

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
                                Toast.makeText(PunchActivity.this, "WELCOME TO CPS", Toast.LENGTH_SHORT).show();
                                SharedPreferencesWork sharedPreferencesWork = new SharedPreferencesWork(PunchActivity.this);
                                HashMap hashMap = new HashMap();
                                hashMap.put("status", "active");
                                hashMap.put("login_id", inserted_id);
                                hashMap.put("login_date", currentDate);
                                sharedPreferencesWork.insertOrReplace(hashMap, Routes.sharedPrefForLogin);
                                if (role.equals("Team Leader")) {
                                    Intent employee = new Intent(PunchActivity.this, TL_activity.class);
                                    employee.putExtra("role","Team Leader");
                                    startActivity(employee);
                                    finish();
                                }
                                if (role.equals("Data Entry Operator")) {
                                    Intent employee = new Intent(PunchActivity.this, Activity_do.class);
                                    employee.putExtra("role","Data Entry Operator");
                                    startActivity(employee);
                                    finish();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        L.L(statusCode + str);
                        new DialogBox(PunchActivity.this, str).asyncDialogBox();
                    }
                } catch (Exception ex) {
                    L.L(ex.toString());
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                new SmartDialogBuilder(PunchActivity.this)
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

    public void sign_in_again(View view){
        if (role.equals("Team Leader")) {
            Intent employee = new Intent(PunchActivity.this, TL_activity.class);
            employee.putExtra("role","Team Leader");
            startActivity(employee);
            finish();
        }
        if (role.equals("Data Entry Operator")) {
            Intent employee = new Intent(PunchActivity.this, Activity_do.class);
            employee.putExtra("role","Data Entry Operator");
            startActivity(employee);
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }

}
