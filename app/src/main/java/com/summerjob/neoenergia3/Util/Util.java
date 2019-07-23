package com.summerjob.neoenergia3.Util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.summerjob.neoenergia3.MainActivity;
import com.summerjob.neoenergia3.model.WiFi;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static List<WiFi> verifyConection(Context context) {

        Log.d("REDES", "verifyConection");

        List<WiFi> wifiList = new ArrayList<>();

        ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {

            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> scanResults = wifiManager.getScanResults();

            if (scanResults != null){

                for (ScanResult sr : scanResults) {
                    WiFi wiFi = new WiFi();
                    wiFi.setName(sr.SSID);
                    wiFi.setFrequency(String.valueOf(sr.frequency));
                    wiFi.setStrength(String.valueOf(sr.level));
                    wifiList.add(wiFi);
                }
            }
        }



        return wifiList;
    }

    public static String getLocation(Activity activity) {

        ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        LocationManager locationManager = (LocationManager) activity.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        String locationCurrent = "";

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(activity, "Por favor habilite GPS e Internet", Toast.LENGTH_LONG).show();
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                if (location != null) {
                    locationCurrent = location.getLatitude() + ", " + location.getLongitude();
                } else if (location1 != null) {
                    locationCurrent = location1.getLatitude() + ", " + location1.getLongitude();
                } else if (location2 != null) {
                    locationCurrent = location2.getLatitude() + ", " + location2.getLongitude();
                } else {
                    Toast.makeText(activity, "Não é possível rastrear sua localização", Toast.LENGTH_SHORT).show();
                }
            }

        }

        return locationCurrent;
    }
}
