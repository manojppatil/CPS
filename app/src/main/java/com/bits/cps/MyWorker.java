package com.bits.cps;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bits.cps.Helper.DialogBox;
import com.bits.cps.Helper.L;
import com.bits.cps.Helper.NetworkConnection;
import com.bits.cps.Helper.Routes;
import com.bits.cps.Helper.SharedPreferencesWork;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class MyWorker extends Worker {

    private static final String DEFAULT_START_TIME = "09:00";
    private static final String DEFAULT_END_TIME = "20:00";

    private static final String TAG = "MyWorker";

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    /**
     * The current location.
     */
    private Location mLocation;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    private Context mContext;
    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;

    private int inserted_id;
    HashMap ssid;
    String id;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
        SharedPreferencesWork sharedPreferencesWork = new SharedPreferencesWork(mContext);
        ssid = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, "id");
        id = ssid.get("id").toString();
    }

    @NonNull
    @Override
    public Result doWork() {

        int d = Log.d(TAG, "doWork: Done");

        Log.d(TAG, "onStartJob: STARTING JOB..");

        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        final String currentDateandTime = sdf.format(new Date());

        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        String formattedDate = dateFormat.format(date);

        final EasyDB easyDB = EasyDB.init(mContext, "CPS") // "TEST" is the name of the DATABASE
                .setTableName("location")  // You can ignore this line if you want
                .addColumn(new Column("user_id", new String[]{"text", "unique"}))
                .addColumn(new Column("locality", new String[]{"text", "not null"}))
                .addColumn(new Column("date_time", new String[]{"text"}))
                .doneTableColumn();
        try {
            final Date currentDate = dateFormat.parse(formattedDate);
            Date startDate = dateFormat.parse(DEFAULT_START_TIME);
            Date endDate = dateFormat.parse(DEFAULT_END_TIME);

            if (currentDate.after(startDate) && currentDate.before(endDate)) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
                mLocationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                    }
                };

                final LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
                mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                try {
                    mFusedLocationClient
                            .getLastLocation()
                            .addOnCompleteListener(new OnCompleteListener<Location>() {
                                @Override
                                public void onComplete(@NonNull Task<Location> task) {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        mLocation = task.getResult();
                                        Log.d(TAG, "Location : " + mLocation);

                                        // Create the NotificationChannel, but only on API 26+ because
                                        // the NotificationChannel class is new and not in the support library
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            CharSequence name = mContext.getString(R.string.app_name);
                                            String description = mContext.getString(R.string.app_name);
                                            int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                            NotificationChannel channel = new NotificationChannel(mContext.getString(R.string.app_name), name, importance);
                                            channel.setDescription(description);
                                            // Register the channel with the system; you can't change the importance
                                            // or other notification behaviors after this
                                            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
                                            notificationManager.createNotificationChannel(channel);
                                        }

                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, mContext.getString(R.string.app_name))
                                                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                                                .setContentTitle("New Location Update")
                                                .setContentText("You are at " + getCompleteAddressString(mLocation.getLatitude(), mLocation.getLongitude()))
                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                .setStyle(new NotificationCompat.BigTextStyle().bigText("You are at " + getCompleteAddressString(mLocation.getLatitude(), mLocation.getLongitude())));

                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

                                        // notificationId is a unique int for each notification that you must define
                                        notificationManager.notify(1001, builder.build());

                                        if (NetworkConnection.checkNetworkConnection(mContext)) {
                                            Log.d(TAG, "onConnected");
                                            RequestParams requestParams1 = new RequestParams();
                                            requestParams1.add("locality", getCompleteAddressString(mLocation.getLatitude(), mLocation.getLongitude()));
                                            requestParams1.add("user_id", id);
                                            requestParams1.add("date_time", currentDateandTime);
                                            requestParams1.add("tbname", "live_location");

                                            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                                            asyncHttpClient.post(Routes.insert2, requestParams1, new AsyncHttpResponseHandler() {

                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
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

                                                                } else {
                                                                    Toast.makeText(mContext, "Data not inserted " + jo.getString("status"), Toast.LENGTH_SHORT).show();
                                                                    new DialogBox(mContext, jo.get("message").toString()).asyncDialogBox();

                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }

                                                        } else {
                                                            L.L(statusCode + str);
                                                            new DialogBox(mContext, str).asyncDialogBox();
                                                        }
                                                    } catch (Exception ex) {
                                                        L.L(ex.toString());
                                                    }

                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                                }
                                            });
                                        } else {
                                            boolean done = easyDB.addData("user_id", id)
                                                    .addData("locality", getCompleteAddressString(mLocation.getLatitude(), mLocation.getLongitude()))
                                                    .addData("date_time", currentDateandTime)
                                                    .doneDataAdding();
                                            new DialogBox(mContext, "Check your network connnection").asyncDialogBox();
                                            Log.d(TAG, "onDisconnected");
                                        }
                                        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                                    } else {
                                        Log.w(TAG, "Failed to get location.");
                                    }
                                }
                            });
                } catch (SecurityException unlikely) {
                    Log.e(TAG, "Lost location permission." + unlikely);
                }

                try {
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, null);
                } catch (SecurityException unlikely) {
                    //Utils.setRequestingLocationUpdates(this, false);
                    Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
                }
            } else {
                Log.d(TAG, "Time up to get location. Your time is : " + DEFAULT_START_TIME + " to " + DEFAULT_END_TIME);
            }
        } catch (ParseException ignored) {

        }
        return Result.success();
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                L.L(strAdd + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }


}
