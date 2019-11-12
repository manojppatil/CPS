package com.bits.cps;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class SRO_receipt extends AppCompatActivity {
    Spinner sro_emp;
    EditText sro_date, sro_bank, sro_office, sro_amount;
    private int inserted_id;
    Context context;
    AutoCompleteTextView sro_cust_name;
    String name;
    ArrayList cust_names = new ArrayList<String>();
    ArrayList ids = new ArrayList<String>();
    String aid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sro_receipt);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#34eb49")));
        setTitle("File SRO Receipt");
        sro_cust_name = findViewById(R.id.sro_cust_name);
        RequestParams requestParams = new RequestParams();
        requestParams.add("tbname", "task");
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.selectAllByQuery, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                name = new String(responseBody);
                L.L(name);
                try {
                    JSONArray jr = new JSONArray(name);

                    for (int i = 0; i < jr.length(); i++) {
                        JSONObject jo = jr.getJSONObject(i);

                        cust_names.add(jo.getInt("id") + ": " + jo.getString("client_name"));
                        cust_names.indexOf(name);

                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(SRO_receipt.this, android.R.layout.simple_list_item_1, cust_names);
                    sro_cust_name.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
        sro_emp = findViewById(R.id.sro_emp_name);
        sro_date = findViewById(R.id.sro_date);

        sro_cust_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                String strid = parent.getItemAtPosition(position).toString();
                String selectItem = sro_cust_name.getText().toString();
                int partinamesid = cust_names.indexOf(selectItem);
            }
        });
        sro_bank = findViewById(R.id.sro_bank_name);
        sro_amount = findViewById(R.id.sro_amount);
        sro_office = findViewById(R.id.sro_office);
        getEmpID();


    }

    public void sro_receipt(View view) {

        String Sro_emp = sro_emp.getSelectedItem().toString();
        String Sro_date = sro_date.getText().toString();
        String Sro_cust = sro_cust_name.getText().toString();
        String Sro_bank = sro_bank.getText().toString();
        String Sro_amount = sro_amount.getText().toString();
        String Sro_office = sro_office.getText().toString();
        final String array[] = Sro_cust.split("\\:");
        final String array1[] = Sro_emp.split("\\:");

        RequestParams requestParams = new RequestParams();
        requestParams.add("SRO_date", Sro_date);
        requestParams.add("emp_name", array1[0]+array1[1]);
        requestParams.add("cust_name", array[1]);
        requestParams.add("bank_name", Sro_bank);
        requestParams.add("SRO_office", Sro_office);
        requestParams.add("SRO_payment", Sro_amount);
        requestParams.add("id", array[0]);
        requestParams.add("tbname", "task");

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.update, requestParams, new AsyncHttpResponseHandler() {
            AlertDialog dialog = new SpotsDialog(SRO_receipt.this, R.style.Custom);

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
                                new SmartDialogBuilder(SRO_receipt.this)
                                        .setTitle("Success")
                                        .setSubTitle("SRO receipt is Successfully Updated.")
                                        .setCancalable(false)
                                        .setNegativeButtonHide(true) //hide cancel button
                                        .setPositiveButton("OK", new SmartDialogClickListener() {
                                            @Override
                                            public void onClick(SmartDialog smartDialog) {
                                                Toast.makeText(SRO_receipt.this, "Thank you", Toast.LENGTH_SHORT).show();
                                                smartDialog.dismiss();
                                            }
                                        }).build().show();
                                sro_date.setText("");
                                sro_cust_name.setText("");
                                sro_bank.setText("");
                                sro_amount.setText("");
                                sro_office.setText("");

                            } else {
                                Toast.makeText(context, "Data not inserted " + jo.getString("status"), Toast.LENGTH_SHORT).show();
                                new DialogBox(SRO_receipt.this, jo.get("message").toString()).asyncDialogBox();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        L.L(statusCode + str);
                        new DialogBox(SRO_receipt.this, str).asyncDialogBox();
                    }
                } catch (Exception ex) {
                    L.L(ex.toString());
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                new SmartDialogBuilder(SRO_receipt.this)
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
        });
    }

    public void getEmpID() {
        RequestParams requestParams = new RequestParams();
        requestParams.add("tbname", "user");
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.selectAllEmployee, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String string = new String(responseBody);
                JSONArray jr = null;
                JSONObject jo = null;
                L.L(string + "---------------");
                ArrayList arrayList = new ArrayList();
                if (statusCode == 200) {
                    try {
                        jr = new JSONArray(string);
                        for (int i = 0; i < jr.length(); i++) {
                            jo = jr.getJSONObject(i);
                            arrayList.add(jo.getInt("id") + ": " + jo.getString("name"));
                        }
                        ArrayAdapter<String> arrayAdapterid = new ArrayAdapter<String>(SRO_receipt.this, android.R.layout.simple_spinner_item, arrayList);
                        arrayAdapterid.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sro_emp.setAdapter(arrayAdapterid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                new SmartDialogBuilder(SRO_receipt.this)
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
        });

    }

    public void selectDate(View view) {
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

                        date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}
