package com.bits.cps;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bits.cps.Adapter.SROAdapter;
import com.bits.cps.Helper.DialogBox;
import com.bits.cps.Helper.L;
import com.bits.cps.Helper.Routes;
import com.bits.cps.Helper.SharedPreferencesWork;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rahman.dialog.Activity.SmartDialog;
import com.rahman.dialog.ListenerCallBack.SmartDialogClickListener;
import com.rahman.dialog.Utilities.SmartDialogBuilder;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class Employee_activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Context context;
    private int inserted_id;
    String currentDateandTime;
    String addressLines;
    String login_id;
    HashMap loginid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("CPS Employee");

        SharedPreferencesWork sharedPreferencesWork = new SharedPreferencesWork(Employee_activity.this);
        loginid = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, "login_id");
        login_id = loginid.get("login_id").toString();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // check if GPS enabled
        GPSTracker gpsTracker = new GPSTracker(this);

        if (gpsTracker.getIsGPSTrackingEnabled()) {
            addressLines = gpsTracker.getAddressLine(this);
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finishAffinity();
            System.exit(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logout, menu);

        MenuItem menuItem = menu.findItem(R.id.mySwitch);
        menuItem.setActionView(R.layout.use_logout);
        Button logout = menu.findItem(R.id.mySwitch).getActionView().findViewById(R.id.action_switch);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                currentDateandTime = sdf.format(new Date());

                RequestParams requestParams = new RequestParams();
                requestParams.add("logout_time", currentDateandTime);
                requestParams.add("logout_place", addressLines);
                requestParams.add("login_status", "0");
                requestParams.add("id", login_id);
                requestParams.add("tbname", "attendance");

                AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                asyncHttpClient.post(Routes.update, requestParams, new AsyncHttpResponseHandler() {
                    AlertDialog dialog = new SpotsDialog(Employee_activity.this, R.style.Custom);

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
                                        new SmartDialogBuilder(Employee_activity.this)
                                                .setTitle("Thank You")
                                                .setSubTitle("Have a Good night.")
                                                .setCancalable(false)
                                                .setNegativeButtonHide(true) //hide cancel button
                                                .setPositiveButton("OK", new SmartDialogClickListener() {
                                                    @Override
                                                    public void onClick(SmartDialog smartDialog) {
                                                        SharedPreferencesWork sharedPreferencesWork = new SharedPreferencesWork(Employee_activity.this);
                                                        HashMap hashMap = new HashMap();
                                                        hashMap.put("status", "deactive");
                                                        sharedPreferencesWork.insertOrReplace(hashMap, Routes.sharedPrefForLogin);
                                                        Intent intent = new Intent(Employee_activity.this, UserActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                        smartDialog.dismiss();
                                                    }
                                                }).build().show();


                                    } else {
                                        Toast.makeText(context, "Data not inserted " + jo.getString("status"), Toast.LENGTH_SHORT).show();
                                        new DialogBox(Employee_activity.this, jo.get("message").toString()).asyncDialogBox();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                L.L(statusCode + str);
                                new DialogBox(Employee_activity.this, str).asyncDialogBox();
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
        });
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.emp_profile) {
            Intent intent = new Intent(Employee_activity.this, ProfilePageTL.class);
            startActivity(intent);
        } else if (id == R.id.emp_assign_task) {
            Intent intent = new Intent(Employee_activity.this, ShowAssignTaskByUser.class);
            startActivity(intent);
        } else if (id == R.id.emp_sro_task) {
            Intent intent = new Intent(Employee_activity.this, ShowSRO_task.class);
            startActivity(intent);

        } else if (id == R.id.emp_nav_logout) {
            if (new SharedPreferencesWork(Employee_activity.this).eraseData(Routes.sharedPrefForLogin)) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
