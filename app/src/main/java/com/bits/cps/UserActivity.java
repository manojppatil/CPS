package com.bits.cps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

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
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import am.appwise.components.ni.NoInternetDialog;
import cz.msebera.android.httpclient.Header;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class UserActivity extends AppCompatActivity {
    static Button attendance;
    Button sign_in_again;
    String username;
    String id;
    String status;
    static String login_date;
    HashMap name, ssid, sstatus, ssdate;
    Context context;
    private int inserted_id;
    String currentDateandTime;
    static String currentDate;
    NoInternetDialog noInternetDialog;

    private static final int PERMISSION_REQUEST_CODE = 200;

    private static final String TAG = "LocationUpdate";
    String addressLines;
    static TextView tv_check_connection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        tv_check_connection = findViewById(R.id.tv_check_connection);
        noInternetDialog = new NoInternetDialog.Builder(UserActivity.this).build();
        attendance = findViewById(R.id.btn_id);
        sign_in_again = findViewById(R.id.sign_in_again);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3a3dff")));
        SharedPreferencesWork sharedPreferencesWork = new SharedPreferencesWork(UserActivity.this);
        name = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, "userid");
        ssid = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, "id");
        sstatus = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, ("status"));
        ssdate = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, ("login_date"));
        login_date = ssdate.get("login_date").toString();
        L.L(login_date+"------");
        username = name.get("userid").toString();
        id = ssid.get("id").toString();
        status = sstatus.get("status").toString();
        if (status.equals("active")) {
            Intent intent = new Intent(UserActivity.this, Employee_activity.class);
            startActivity(intent);
        }
        if (!checkLocationPermission()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
        SimpleDateFormat cd = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = cd.format(new Date());
        L.L(currentDate+"++++++++");
        if (login_date.equals(currentDate)){
            attendance.setVisibility(View.GONE);
            sign_in_again.setVisibility(View.VISIBLE);
            tv_check_connection.setVisibility(View.VISIBLE);
            tv_check_connection.setText("You have Already PUNCH IN for today.");
            tv_check_connection.setBackgroundColor(Color.RED);
            tv_check_connection.setTextColor(Color.WHITE);

        }
        try {
            if (isWorkScheduled(WorkManager.getInstance().getWorkInfosByTag(TAG).get())) {

            } else {

            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (attendance.getText().toString().equalsIgnoreCase(getString(R.string.button_text_start))) {
                    // START Worker
                    PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(MyWorker.class, 15, TimeUnit.MINUTES)
                            .addTag(TAG)
                            .build();
                    punchAttendance();
                    WorkManager.getInstance().enqueueUniquePeriodicWork("Location", ExistingPeriodicWorkPolicy.REPLACE, periodicWork);

                    Toast.makeText(UserActivity.this, "Location Worker Started : " + periodicWork.getId(), Toast.LENGTH_SHORT).show();

                    attendance.setText(getString(R.string.button_text_stop));

                } else {

                    WorkManager.getInstance().cancelAllWorkByTag(TAG);

                    attendance.setText(getString(R.string.button_text_start));
                }
            }
        });

        // check if GPS enabled
        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.getIsGPSTrackingEnabled()) {
            addressLines = gpsTracker.getAddressLine(this);
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private boolean isWorkScheduled(List<WorkInfo> workInfos) {
        boolean running = false;
        if (workInfos == null || workInfos.size() == 0) return false;
        for (WorkInfo workStatus : workInfos) {
            running = workStatus.getState() == WorkInfo.State.RUNNING | workStatus.getState() == WorkInfo.State.ENQUEUED;
        }
        return running;
    }

    private boolean checkLocationPermission() {
        int result3 = ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION);
        int result4 = ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION);
        return result3 == PackageManager.PERMISSION_GRANTED &&
                result4 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean coarseLocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean fineLocation = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (coarseLocation && fineLocation)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void punchAttendance() {

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
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
                                Toast.makeText(UserActivity.this, "WELCOME TO CPS", Toast.LENGTH_SHORT).show();
                                SharedPreferencesWork sharedPreferencesWork = new SharedPreferencesWork(UserActivity.this);
                                HashMap hashMap = new HashMap();
                                hashMap.put("status", "active");
                                hashMap.put("login_id", inserted_id);
                                hashMap.put("login_date", currentDate);
                                sharedPreferencesWork.insertOrReplace(hashMap, Routes.sharedPrefForLogin);
                                Intent intent = new Intent(UserActivity.this, Employee_activity.class);
                                intent.putExtra("login_id", inserted_id);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        L.L(statusCode + str);
                        new DialogBox(UserActivity.this, str).asyncDialogBox();
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

    public void sign_in_again(View view){
        Intent intent = new Intent(UserActivity.this,Employee_activity.class);
        intent.putExtra("login_id", inserted_id);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }


}
