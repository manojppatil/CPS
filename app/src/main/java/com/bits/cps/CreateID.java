package com.bits.cps;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class CreateID extends AppCompatActivity {
    EditText emp_name, emp_contact, emp_address, emp_email, emp_password;
    private int inserted_id;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_id);
        setTitle("Create Employee ID");
        emp_name = findViewById(R.id.emp_name);
        emp_contact = findViewById(R.id.emp_contact);
        emp_address = findViewById(R.id.emp_address);
        emp_email = findViewById(R.id.emp_email);
        emp_password = findViewById(R.id.emp_password);
    }

    public void create_id(View view) {
        final String Name = emp_name.getText().toString();
        final String Contact = emp_contact.getText().toString();
        final String Address = emp_address.getText().toString();
        final String Email = emp_email.getText().toString();
        String Password = emp_password.getText().toString();

        RequestParams requestParams = new RequestParams();
        requestParams.add("name", Name);
        requestParams.add("contact", Contact);
        requestParams.add("address", Address);
        requestParams.add("email", Email);
        requestParams.add("password", Password);
        requestParams.add("tbname", "user");

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.insert2, requestParams, new AsyncHttpResponseHandler() {
            AlertDialog dialog = new SpotsDialog(CreateID.this, R.style.Custom);

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
                                Toast.makeText(CreateID.this, "Employee ID Created", Toast.LENGTH_SHORT).show();
                                emp_name.setText("");
                                emp_contact.setText("");
                                emp_address.setText("");
                                emp_email.setText("");
                                emp_password.setText("");

                            } else {
                                Toast.makeText(context, "Data not inserted " + jo.getString("status"), Toast.LENGTH_SHORT).show();
                                new DialogBox(CreateID.this, jo.get("message").toString()).asyncDialogBox();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        L.L(statusCode + str);
                        new DialogBox(CreateID.this, str).asyncDialogBox();
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
