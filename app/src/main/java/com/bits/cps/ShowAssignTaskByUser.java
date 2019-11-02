package com.bits.cps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class ShowAssignTaskByUser extends AppCompatActivity {
    HashMap name, ssid, loginid;
    String username, id;
    ArrayList arr = new ArrayList();
    RequestParams requestParams = new RequestParams();
    Context context;
    private int inserted_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_assign_task_by_user);
        SharedPreferencesWork sharedPreferencesWork = new SharedPreferencesWork(ShowAssignTaskByUser.this);
        name = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, "userid");
        ssid = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, "id");
        username = name.get("userid").toString();
        id = ssid.get("id").toString();
        requestParams.add("id", id);
        requestParams.add("column", "user_id");
        requestParams.add("tbname", "task");

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.selectOneByColumn, requestParams, new AsyncHttpResponseHandler() {
            AlertDialog dialog = new SpotsDialog(ShowAssignTaskByUser.this, R.style.Custom);

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

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                new DialogBox(ShowAssignTaskByUser.this, responseBody.toString());
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
}
