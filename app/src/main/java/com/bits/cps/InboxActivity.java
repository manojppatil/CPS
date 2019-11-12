package com.bits.cps;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bits.cps.Adapter.InboxAdapter;
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
import java.util.Calendar;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class InboxActivity extends AppCompatActivity {
    ArrayList arr = new ArrayList();
    Context context;
    private int inserted_id;
    EditText inboxdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3a3dff")));
        inboxdate = findViewById(R.id.inbox_date);

        RequestParams requestParams = new RequestParams();
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.multipleTablesData, requestParams, new AsyncHttpResponseHandler() {
            AlertDialog dialog = new SpotsDialog(InboxActivity.this, R.style.Custom);

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
                                    hashMap.put("name", jo.get("name").toString());
                                    hashMap.put("ctotal", jo.get("ctotal").toString());
                                    hashMap.put("ptotal", jo.get("ptotal").toString());
                                    hashMap.put("login_time", jo.get("login_time").toString());
                                    hashMap.put("logout_time", jo.get("logout_time").toString());
                                }
                                arr.add(hashMap);
                            }
                            InboxAdapter inadapter = new InboxAdapter(arr, InboxActivity.this);
                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.inbox_recycler);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(InboxActivity.this));
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(inadapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        new DialogBox(InboxActivity.this, str).asyncDialogBox();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                new SmartDialogBuilder(InboxActivity.this)
                        .setTitle("Please Retry...")
                        .setSubTitle("Make sure your device has an active Internet Connection.")
                        .setCancalable(false)
                        .setNegativeButtonHide(true) //hide cancel button
                        .setPositiveButton("OK", new SmartDialogClickListener() {
                            @Override
                            public void onClick(SmartDialog smartDialog) {
                                Intent intent = new Intent(InboxActivity.this, MainActivity.class);
                                startActivity(intent);
                                smartDialog.dismiss();
                            }
                        }).build().show();
            }

            @Override
            public void onRetry(int retryNo) {
                dialog = ProgressDialog.show(InboxActivity.this, "none to say",
                        "Saving.Please wait...", true);
            }

        });

    }

    public void InboxselectDate(View view) {
        final EditText date = (EditText) view;
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void sendDate(View view) {
        String inbox_date = inboxdate.getText().toString();
        RequestParams requestParams = new RequestParams();
        requestParams.add("search_date", inbox_date);

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.multipleTablesData, requestParams, new AsyncHttpResponseHandler() {
            AlertDialog dialog = new SpotsDialog(InboxActivity.this, R.style.Custom);

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
                                    hashMap.put("name", jo.get("name").toString());
                                    hashMap.put("ctotal", jo.get("ctotal").toString());
                                    hashMap.put("ptotal", jo.get("ptotal").toString());
                                    hashMap.put("login_time", jo.get("login_time").toString());
                                    hashMap.put("logout_time", jo.get("logout_time").toString());
                                }
                                arr.add(hashMap);
                            }
                            InboxAdapter inadapter = new InboxAdapter(arr, InboxActivity.this);
                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.inbox_recycler);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(InboxActivity.this));
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(inadapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        new DialogBox(InboxActivity.this, str).asyncDialogBox();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                new SmartDialogBuilder(InboxActivity.this)
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

            @Override
            public void onRetry(int retryNo) {
                dialog = ProgressDialog.show(InboxActivity.this, "none to say",
                        "Saving.Please wait...", true);
            }

        });

    }
}
