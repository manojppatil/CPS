package com.bits.cps;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
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

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class ProfilePageTL extends AppCompatActivity {

    TextView name,contact,mintime,maxtime,address;
    HashMap hashMap;
    String id;
    private int inserted_id;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page_tl);

        SharedPreferencesWork sharedPreferencesWork = new SharedPreferencesWork(ProfilePageTL.this);
        hashMap = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin,"id");
        id = hashMap.get("id").toString();
        L.L(id+"----");
        name = findViewById(R.id.show_name_tl);
        contact = findViewById(R.id.show_contact);
        mintime = findViewById(R.id.show_min_time);
        maxtime = findViewById(R.id.show_max_time);
        address = findViewById(R.id.show_add);

        RequestParams requestParams = new RequestParams();
        requestParams.add("id",id);
        requestParams.add("tbname","user");

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.selectOne, requestParams, new AsyncHttpResponseHandler() {
            AlertDialog dialog = new SpotsDialog(ProfilePageTL.this, R.style.Custom);

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
                            jr = new JSONArray(str);
                            jo = jr.getJSONObject(0);

                               name.setText(jo.getString("name"));
                               contact.setText(jo.getString("contact"));
                               mintime.setText(jo.getString("in_time"));
                               maxtime.setText(jo.getString("out_time"));
                               address.setText(jo.getString("address"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        L.L(statusCode + str);
                        new DialogBox(ProfilePageTL.this, str).asyncDialogBox();
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
