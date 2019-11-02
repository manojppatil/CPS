package com.bits.cps;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bits.cps.Helper.DialogBox;
import com.bits.cps.Helper.L;
import com.bits.cps.Helper.NetworkConnection;
import com.bits.cps.Helper.Routes;
import com.bits.cps.Helper.SharedPreferencesWork;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import am.appwise.components.ni.NoInternetDialog;
import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;


public class LoginActivity extends AppCompatActivity {
    Button button;
    EditText username, password;
    String user, pass;
    NoInternetDialog noInternetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferencesWork sharedPreferencesWork = new SharedPreferencesWork(LoginActivity.this);
        sharedPreferencesWork.checkForLogin();
        noInternetDialog = new NoInternetDialog.Builder(LoginActivity.this).build();

        button = findViewById(R.id.login_button);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
    }

    public void signin(View view) {
        user = username.getText().toString();
        pass = password.getText().toString();
        if (NetworkConnection.checkNetworkConnection(this)) {
            ArrayList validationMessage = new ArrayList();
            if (username.getText().toString().length() > 0) {
            } else
                validationMessage.add("Name field is not valid");

            if (password.getText().toString().length() > 0) {
            } else
                validationMessage.add("password field id empty");

            if (validationMessage.size() > 0) {
                String allmessage = "";
                for (int i = 0; i < validationMessage.size(); i++) {
                    allmessage += "\n" + validationMessage.get(i);
                    new DialogBox(this, allmessage).asyncDialogBox();
                    validationMessage.clear();
                }
            } else {
                RequestParams params = new RequestParams();
                params.add("userid", user);
                params.add("password", pass);
                params.add("tbname", "user");
                AsyncHttpClient client = new AsyncHttpClient();
                client.post(Routes.LoginByApp, params, new AsyncHttpResponseHandler() {
                    AlertDialog dialog = new SpotsDialog(LoginActivity.this, R.style.Custom);

                    @Override
                    public void onStart() {
                        dialog.show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        dialog.dismiss();
                        String str = new String(responseBody);
                        L.L(str);
                        JSONArray jr = null;
                        JSONObject jo = null;
                        if (statusCode == 200) {
                            try {
                                jo = new JSONObject(str);
                                if (jo.getString("status").equals("success")) {
                                    HashMap hm = new HashMap();
                                    hm.put("apikey", jo.getString("api_key"));
                                    hm.put("userid", user);
                                    hm.put("password", pass);
                                    hm.put("status", "deactivate");
                                    hm.put("role", jo.getString("role"));
                                    hm.put("id", jo.getString("userid"));
                                    hm.put("name", jo.getString("name"));

                                    new SharedPreferencesWork(LoginActivity.this).insertOrReplace(hm, Routes.sharedPrefForLogin);

                                    if (jo.getString("role").equals("admin")) {
                                        Intent admin = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(admin);
                                        finish();
                                        Toast.makeText(LoginActivity.this, "Admin Logged in", Toast.LENGTH_SHORT).show();
                                    }

                                    if (jo.getString("role").equals("employee")) {
                                        Intent employee = new Intent(LoginActivity.this, UserActivity.class);
                                        startActivity(employee);
                                        finish();
                                        Toast.makeText(LoginActivity.this, "Employee Logged in", Toast.LENGTH_SHORT).show();
                                    }
                                    if (jo.getString("role").equals("Team Leader")) {
                                        Intent employee = new Intent(LoginActivity.this, PunchActivity.class);
                                        employee.putExtra("role", "Team Leader");
                                        startActivity(employee);
                                        finish();
                                        Toast.makeText(LoginActivity.this, "Punch Your Attendance Here", Toast.LENGTH_SHORT).show();
                                    }
                                    if (jo.getString("role").equals("Data Entry Operator")) {
                                        Intent employee = new Intent(LoginActivity.this, PunchActivity.class);
                                        employee.putExtra("role", "Data Entry Operator");
                                        startActivity(employee);
                                        finish();
                                        Toast.makeText(LoginActivity.this, "Punch Your Attendance Here", Toast.LENGTH_SHORT).show();
                                    }
                                    finish();
                                } else {
                                    new DialogBox(LoginActivity.this, jo.get("status_message").toString()).asyncDialogBox();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            L.L(statusCode + str);
                            new DialogBox(LoginActivity.this, str).asyncDialogBox();

                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        new DialogBox(LoginActivity.this, responseBody.toString());

                    }

                    @Override
                    public void onRetry(int retryNo) {
                        dialog = ProgressDialog.show(LoginActivity.this, "none to say",
                                "Saving.Please wait...", true);
                    }
                });
            }
        } else {
            new DialogBox(this, "Check your network connnection").asyncDialogBox();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }
}
