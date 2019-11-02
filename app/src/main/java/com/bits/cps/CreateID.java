package com.bits.cps;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class CreateID extends AppCompatActivity {
    EditText emp_name, emp_contact, emp_address, emp_email, emp_password, login_time, logout_time;
    private int inserted_id;
    Context context;
    Spinner role;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    Uri imageuri;
    CircleImageView emp_profile;
    RequestParams requestParams = new RequestParams();

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
        login_time = findViewById(R.id.emp_login_time);
        logout_time = findViewById(R.id.emp_logout_time);
        emp_profile = findViewById(R.id.profile_image_emp);
        role = findViewById(R.id.role_spinner);

        ArrayList<String> arrayList1 = new ArrayList<>();
        arrayList1.add("Team Leader");
        arrayList1.add("Field Executive");
        arrayList1.add("Data Entry Operator");

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList1);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        role.setAdapter(arrayAdapter1);
    }

    public void create_id(View view) {
        final String Name = emp_name.getText().toString();
        final String Contact = emp_contact.getText().toString();
        final String Address = emp_address.getText().toString();
        final String Email = emp_email.getText().toString();
        String Password = emp_password.getText().toString();
        String Login_time = login_time.getText().toString();
        String Logout_time = logout_time.getText().toString();
        String Role = role.getSelectedItem().toString();

        requestParams.add("name", Name);
        requestParams.add("contact", Contact);
        requestParams.add("address", Address);
        requestParams.add("email", Email);
        requestParams.add("role", Role);
        requestParams.add("password", Password);
        requestParams.add("in_time", Login_time);
        requestParams.add("out_time", Logout_time);
        requestParams.add("tbname", "user");

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(Routes.insert3, requestParams, new AsyncHttpResponseHandler() {
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
                                new SmartDialogBuilder(CreateID.this)
                                        .setTitle("Success")
                                        .setSubTitle("Employee ID is Successfully Created.")
                                        .setCancalable(false)
                                        .setNegativeButtonHide(true) //hide cancel button
                                        .setPositiveButton("OK", new SmartDialogClickListener() {
                                            @Override
                                            public void onClick(SmartDialog smartDialog) {
                                                Toast.makeText(CreateID.this, "Thank you", Toast.LENGTH_SHORT).show();
                                                smartDialog.dismiss();
                                            }
                                        }).build().show();
                                emp_name.setText("");
                                emp_contact.setText("");
                                emp_address.setText("");
                                emp_email.setText("");
                                emp_password.setText("");
                                login_time.setText("");
                                logout_time.setText("");
                                emp_profile.setImageResource(R.drawable.prof2);

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

    public void selectTime(View view) {


        final EditText time = (EditText) view;
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
                        String formattedDate = dateFormat.format(new Date()).toString();
                        System.out.println(formattedDate);
                        time.setText(formattedDate+"");
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void selectImage(View view) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateID.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, PICK_IMAGE_CAMERA);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, PICK_IMAGE_GALLERY);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_CAMERA) {
            if (data != null) {
                try {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    emp_profile.setImageBitmap(bitmap);
                    emp_profile.setVisibility(Button.VISIBLE);
                    Uri tempUri = getImageUri(getApplicationContext(), bitmap);
                    File finalFile = new File(getRealPathFromURI(tempUri));
                    L.L(finalFile + "");
                    requestParams.put("image", finalFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == PICK_IMAGE_GALLERY) {
            if (data != null) {
                try {
                    imageuri = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri);
                    emp_profile.setImageBitmap(bitmap);
                    Uri tempUri = getImageUri(getApplicationContext(), bitmap);
                    File finalFile = new File(getRealPathFromURI(tempUri));
                    L.L(finalFile + "");
                    requestParams.put("image", finalFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

}
