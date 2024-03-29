package com.bits.cps;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.bits.cps.Adapter.SROAdapter;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class ShowSRO_task extends AppCompatActivity {
    HashMap name, ssid, loginid;
    String username, id;
    ArrayList arr = new ArrayList();
    RequestParams requestParams = new RequestParams();
    Context context;
    String currentDateandTime;
    String addressLines;
    private int inserted_id;
    String login_id;
    String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_sro_task);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3a3dff")));
        SharedPreferencesWork sharedPreferencesWork = new SharedPreferencesWork(ShowSRO_task.this);
        ssid = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, "id");
        name = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, "name");
        id = ssid.get("id").toString();
        username = name.get("name").toString();
        SimpleDateFormat cd = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = cd.format(new Date());
        String value = id + " " + username;
        L.L(value+"-------------vakl");
        requestParams.add("column", "SRO_date");
        requestParams.add("date",currentDate);
        requestParams.add("id", value);
        requestParams.add("column2","emp_name");
        requestParams.add("tbname", "task");

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.selectTaskByDate, requestParams, new AsyncHttpResponseHandler() {
            AlertDialog dialog = new SpotsDialog(ShowSRO_task.this, R.style.Custom);

            @Override
            public void onStart() {
                dialog.show();
            }

            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                dialog.dismiss();
                String str = new String(responseBody);
//                str = str.replace("<br>", "\n");
                L.L(str + "");
                JSONArray jr = null;
                JSONObject jo = null;
                try {
                    if (statusCode == 200) {
                        try {
                            jr = new JSONArray(str);
                            for (int i = 0; i < jr.length(); i++) {
                                jo = jr.getJSONObject(i);
                                HashMap hashMap = new HashMap();
                                if (jo != null) {
                                    hashMap.put("id", jo.get("id").toString());
                                    hashMap.put("name", jo.get("client_name").toString());
                                    hashMap.put("address", jo.get("SRO_office").toString());
                                    hashMap.put("mobile", jo.get("bank_name").toString());
                                    hashMap.put("meeting_time", jo.get("SRO_date").toString());
                                    hashMap.put("amount", jo.get("SRO_payment").toString());
                                }
                                arr.add(hashMap);
                            }
                            SROAdapter uadapter = new SROAdapter(arr, ShowSRO_task.this);
                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.sro_recycler);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ShowSRO_task.this));
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(uadapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        new DialogBox(ShowSRO_task.this, str).asyncDialogBox();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                new SmartDialogBuilder(ShowSRO_task.this)
                        .setTitle("Please Retry...")
                        .setSubTitle("Make sure your device has an active Internet Connection.")
                        .setCancalable(false)
                        .setNegativeButtonHide(true) //hide cancel button
                        .setPositiveButton("OK", new SmartDialogClickListener() {
                            @Override
                            public void onClick(SmartDialog smartDialog) {
                                Intent intent = new Intent(ShowSRO_task.this, Employee_activity.class);
                                startActivity(intent);
                                smartDialog.dismiss();
                            }
                        }).build().show();
            }

            @Override
            public void onRetry(int retryNo) {
                dialog = ProgressDialog.show(ShowSRO_task.this, "none to say",
                        "Saving.Please wait...", true);
            }

        });
    }
}
