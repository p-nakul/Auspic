package com.example.auspic;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static String LOG = "auspic_log";

    private static final String PREFS_NAME = "LocationPrefs";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    private TextView sunriseText;

    private Location mLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sunriseText = (TextView)findViewById(R.id.sunriseTime);

        // request permissions
        requestLocationPermission();
        // Check if the notification permission is needed (Android 13 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission();
        }

        // get location if stored
        mLocation = getLocationFromPrefs();

        if(mLocation.getLatitude() == 0 && mLocation.getLongitude() == 0){
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            getLastLocation();
        }

        // final get the latest location
        mLocation = getLocationFromPrefs();

        // fetch sunrise time
        // Create Data object to pass to Worker
        Data inputData = new Data.Builder()
                .putDouble("latitude", mLocation.getLatitude())
                .putDouble("longitude", mLocation.getLongitude())
                .build();

        // Build OneTimeWorkRequest with input data
        OneTimeWorkRequest getRequest = new OneTimeWorkRequest.Builder(GetRequestWorker.class)
                .setInputData(inputData)
                .build();

        // Enqueue the work
        WorkManager.getInstance(this).enqueue(getRequest);

        // Observe the work status and output data
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(getRequest.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            // Get output data
                            Data outputData = workInfo.getOutputData();
                            String response = outputData.getString("response");

                            // Use the response as needed
                            Log.d(LOG, "Response from Worker: " + response);
                            setSunriseTime(response);
                        }
                    }
                });

        // Set up a periodic work request
        PeriodicWorkRequest notificationWork = new PeriodicWorkRequest.Builder(NotificationWorker.class, 1, TimeUnit.MINUTES)
                .build();

        // Enqueue the work request
        WorkManager.getInstance(this).enqueue(notificationWork);
    }

    private void setSunriseTime(String jsonResponse){
        try{
            JSONObject jsonObject = new JSONObject(jsonResponse);
            String sunrise = jsonObject.getJSONObject("results").getString("sunrise");
            String today_date = jsonObject.getJSONObject("results").getString("date");
            String sunset = jsonObject.getJSONObject("results").getString("sunset");

            String utcOffsetString = jsonObject.getJSONObject("results").getString("utc_offset");

            // Create the complete time string with UTC offset
            String completeTimeString = today_date + " " + sunrise;
            Log.d(LOG, completeTimeString);
            // Parse the sunrise time
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date sunriseTime = null;
            try{
                sunriseTime = sdf.parse(completeTimeString);
                SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss");

                // Format the Date
                String formattedTime = sdf1.format(sunriseTime);
                // Format the LocalDateTime
                sunriseText.setText(formattedTime);

                Date date = new Date();

                // Define the SimpleDateFormat
                SimpleDateFormat current_sdf = new SimpleDateFormat("hh:mm:ss a");

                // Format the current time
                String currentTime = current_sdf.format(date);

                Calendar calendar = Calendar.getInstance();

                // Define the SimpleDateFormat to get the day name
                SimpleDateFormat day_sdf = new SimpleDateFormat("EEEE");

                // Format the current date to get the day name
                String dayName = day_sdf.format(calendar.getTime());

                // Print the name of today's day
                //_currentTimeText.setText(dayName + " " + currentTime);

            }
            catch (Exception e){
                Log.d(LOG, "" + e);
            }
        }
        catch (Exception e){

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // if fails then request for permission
            requestLocationPermission();
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            Log.d(LOG, "Latitude: " + location.getLatitude());
                            Log.d(LOG, "Longitude: " + location.getLongitude());
                            saveLocationInPrefs(location.getLatitude(), location.getLongitude());
                        }
                    }
                });
    }

    // Handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can send notifications
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Notification Permission Required")
                        .setMessage("Without notification permission, this app will not be able to send you notifications.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .show();
            }
        }

        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can send notifications
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Location permission required")
                        .setMessage("Without location permission, this app will not be able to get the sunrise time")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .show();
            }
        }
    }

    private void saveLocationInPrefs(double latitude, double longitude) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(KEY_LATITUDE, (float) latitude);
        editor.putFloat(KEY_LONGITUDE, (float) longitude);
        editor.apply();
    }

    // Optional method to retrieve location from SharedPreferences
    private Location getLocationFromPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        float latitude = sharedPreferences.getFloat(KEY_LATITUDE, 0);
        float longitude = sharedPreferences.getFloat(KEY_LONGITUDE, 0);

        Location location = new Location("storedLocation");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }


}