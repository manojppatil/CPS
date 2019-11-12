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

import com.bits.cps.Adapter.AdminTaskAdapter;
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
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class ShoeTasktoTL extends AppCompatActivity {
    ArrayList arr = new ArrayList();
    RequestParams requestParams = new RequestParams();
    Context context;
    HashMap ssid;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoe_taskto_tl);
        setTitle("Show Task");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3a3dff")));
        SharedPreferencesWork sharedPreferencesWork = new SharedPreferencesWork(ShoeTasktoTL.this);
        ssid = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, "id");
        id = ssid.get("id").toString();

        requestParams.add("tbname", "task");
        requestParams.add("column", "team_leader_id");
        requestParams.add("id", id);
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.selectOneByColumn, requestParams, new AsyncHttpResponseHandler() {
            AlertDialog dialog = new SpotsDialog(ShoeTasktoTL.this, R.style.Custom);

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
                                    hashMap.put("address", jo.get("client_address").toString());
                                    hashMap.put("mobile", jo.get("client_contact").toString());
                                    hashMap.put("email", jo.get("client_email").toString());
                                    hashMap.put("username", jo.get("user_name").toString());
                                    hashMap.put("meeting_time", jo.get("meeting_time").toString());
                                    hashMap.put("amount", jo.get("amount").toString());
                                }
                                arr.add(hashMap);
                            }
                            AdminTaskAdapter uadapter = new AdminTaskAdapter(arr, ShoeTasktoTL.this);
                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.tl_task_recycler);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ShoeTasktoTL.this));
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(uadapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        new DialogBox(ShoeTasktoTL.this, str).asyncDialogBox();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                new SmartDialogBuilder(ShoeTasktoTL.this)
                        .setTitle("Please Retry...")
                        .setSubTitle("Make sure your device has an active Internet Connection.")
                        .setCancalable(false)
                        .setNegativeButtonHide(true) //hide cancel button
                        .setPositiveButton("OK", new SmartDialogClickListener() {
                            @Override
                            public void onClick(SmartDialog smartDialog) {
                                smartDialog.dismiss();
                                dialog.dismiss();
                                Intent intent = new Intent(ShoeTasktoTL.this, TL_activity.class);
                                startActivity(intent);
                            }
                        }).build().show();
//                new DialogBox(ShoeTasktoTL.this, responseBody.toString());
            }

            @Override
            public void onRetry(int retryNo) {
                dialog = ProgressDialog.show(ShoeTasktoTL.this, "none to say",
                        "Saving.Please wait...", true);
            }

        });

    }
}
