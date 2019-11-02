package com.bits.cps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bits.cps.Helper.DialogBox;
import com.bits.cps.Helper.L;
import com.bits.cps.Helper.Routes;
import com.bits.cps.Helper.SharedPreferencesWork;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class PunchActivity extends AppCompatActivity {
    Button attendance;
    Context context;
    private int inserted_id;
    String currentDateandTime, currentDate;
    HashMap name, ssid, sstatus;
    String username, id, status;
    String role;
    String addressLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punch);
        Intent roleintent = getIntent();
        role = roleintent.getStringExtra("role");
        L.L(role+"+++++++++++role");

        SharedPreferencesWork sharedPreferencesWork = new SharedPreferencesWork(PunchActivity.this);
        name = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, "name");
        ssid = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, "id");
        sstatus = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, ("status"));
        username = name.get("name").toString();
        id = ssid.get("id").toString();
        status = sstatus.get("status").toString();
        if (status.equals("active")) {
            if (role.equals("Team Leader")) {
                Intent intent = new Intent(PunchActivity.this, TL_activity.class);
                startActivity(intent);
            } else if (role.equals("Data Entry Operator")) {
                Intent intent = new Intent(PunchActivity.this, Activity_do.class);
                startActivity(intent);
            }
        }
        attendance = findViewById(R.id.btn_punch);
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
                                sharedPreferencesWork.insertOrReplace(hashMap, Routes.sharedPrefForLogin);
                                if (role.equals("Team Leader")) {
                                    Intent employee = new Intent(PunchActivity.this, TL_activity.class);
                                    startActivity(employee);
                                    finish();
                                }
                                if (role.equals("Data Entry Operator")) {
                                    Intent employee = new Intent(PunchActivity.this, Activity_do.class);
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

            }
        });
    }
}
