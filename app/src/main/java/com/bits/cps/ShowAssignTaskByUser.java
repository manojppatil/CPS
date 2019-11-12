package com.bits.cps;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Toast;

import com.bits.cps.Adapter.taskAdapter;
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

import am.appwise.components.ni.NoInternetDialog;
import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class ShowAssignTaskByUser extends AppCompatActivity {
    HashMap name, ssid;
    String id;
    ArrayList arr = new ArrayList();
    RequestParams requestParams = new RequestParams();
    Context context;
    private int inserted_id;
    NoInternetDialog noInternetDialog;
    String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_assign_task_by_user);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3a3dff")));
        noInternetDialog = new NoInternetDialog.Builder(ShowAssignTaskByUser.this).build();
        SharedPreferencesWork sharedPreferencesWork = new SharedPreferencesWork(ShowAssignTaskByUser.this);
        ssid = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, "id");
        id = ssid.get("id").toString();
        SimpleDateFormat cd = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = cd.format(new Date());
        requestParams.add("id", id);
        requestParams.add("column", "date_time");
        requestParams.add("tbname", "task");
        requestParams.add("date",currentDate);
        requestParams.add("column2","user_id");

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.selectTaskByDate, requestParams, new AsyncHttpResponseHandler() {
            AlertDialog dialog = new SpotsDialog(ShowAssignTaskByUser.this, R.style.Custom);

            @Override
            public void onStart() {
                dialog.show();
            }

            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                dialog.dismiss();
                String str = new String(responseBody);
//                str = str.replace("<br>", "\n");
                if (str != null && !str.isEmpty() && !str.equals("null")) {
                    L.L(str + "--------");
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
                                        hashMap.put("address", jo.get("client_address").toString());
                                        hashMap.put("mobile", jo.get("client_contact").toString());
                                        hashMap.put("email", jo.get("client_email").toString());
                                        hashMap.put("meeting_time", jo.get("meeting_time").toString());
                                        hashMap.put("amount", jo.get("amount").toString());
                                    }

                                    arr.add(hashMap);
                                }
                                taskAdapter uadapter = new taskAdapter(arr, ShowAssignTaskByUser.this);
                                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.user_recycler);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ShowAssignTaskByUser.this));
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(uadapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            new DialogBox(ShowAssignTaskByUser.this, str).asyncDialogBox();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    new SmartDialogBuilder(ShowAssignTaskByUser.this)
                            .setTitle("Enjoy!!")
                            .setSubTitle("You have no work for today.")
                            .setCancalable(false)
                            .setNegativeButtonHide(true) //hide cancel button
                            .setPositiveButton("OK", new SmartDialogClickListener() {
                                @Override
                                public void onClick(SmartDialog smartDialog) {
                                    Toast.makeText(ShowAssignTaskByUser.this, "Thank you", Toast.LENGTH_SHORT).show();
                                    smartDialog.dismiss();
                                }
                            }).build().show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                new SmartDialogBuilder(ShowAssignTaskByUser.this)
                        .setTitle("Please Retry...")
                        .setSubTitle("Make sure your device has an active Internet Connection.")
                        .setCancalable(false)
                        .setNegativeButtonHide(true) //hide cancel button
                        .setPositiveButton("OK", new SmartDialogClickListener() {
                            @Override
                            public void onClick(SmartDialog smartDialog) {
                                Toast.makeText(ShowAssignTaskByUser.this, "", Toast.LENGTH_SHORT).show();
                                smartDialog.dismiss();
                            }
                        }).build().show();
//                new DialogBox(ShowAssignTaskByUser.this, responseBody.toString());
            }

            @Override
            public void onRetry(int retryNo) {
                dialog = ProgressDialog.show(ShowAssignTaskByUser.this, "none to say",
                        "Saving.Please wait...", true);
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }
}
