package com.example.auspic;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class GetRequestWorker extends Worker {

    private String LOG_TAG = "auspic_log";
    public GetRequestWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        double latitude = getInputData().getDouble("latitude", 0);
        double longitude = getInputData().getDouble("longitude", 0);
        String jsonResponse = getRequest(latitude,longitude);
        // Create output data
        Data outputData = new Data.Builder()
                .putString("response", jsonResponse)
                .build();

        // Return success with output data
        return Result.success(outputData);
    }

    private String getRequest(double latitude, double longitude) {
        String apiUrl = "https://api.sunrisesunset.io/json?lat=" + latitude + "&lng=" + longitude;
        Log.d(LOG_TAG, apiUrl);
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Here you can parse the response
                String jsonResponse = response.toString();
                Log.d(LOG_TAG, jsonResponse);
                return jsonResponse;
            } else {
                Log.d(LOG_TAG, "Error in get request");
                return "";
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString());
            return "";
        }
    }
}
