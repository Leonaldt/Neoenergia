package com.summerjob.neoenergia3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.summerjob.neoenergia3.Util.Util;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String refreshedToken;
    private String locationCurrent;

    SharedPreferences sharedPreferences;

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);

            switch (wifiStateExtra) {
                case WifiManager.WIFI_STATE_ENABLED:
                    Toast.makeText(context, "WiFi is ON", Toast.LENGTH_LONG).show();
                    // TODO we should ask if there is a shared preference if not, CREATE and ADD LOCATION
                    String savedNetwork = getSavedNetwork();
                    if(savedNetwork == null && getActualNetworkName() != null){
                        saveNetwork(getActualNetworkName());
                        Toast.makeText(context, "SAVING Network: "+getActualNetworkName(), Toast.LENGTH_SHORT).show();
                    } else if(savedNetwork != null){
                        Toast.makeText(context, "SAVED Network: "+savedNetwork, Toast.LENGTH_SHORT).show();
                    }
                    // TODO ADD network name, ADD deviceId, ADD timestamp
                    Toast.makeText(context, "Network: "+getActualNetworkName(), Toast.LENGTH_SHORT).show();

                    break;

                case WifiManager.WIFI_STATE_DISABLED:
                    Toast.makeText(context, "WiFi is OFF", Toast.LENGTH_LONG).show();
                    // TODO ask if shared preference exist, if yes DELETE sp, and device from devices list
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(getString(R.string.saved_network), Context.MODE_PRIVATE);
        this.refreshedToken = FirebaseInstanceId.getInstance().getToken();
        this.locationCurrent = Util.getLocation(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(wifiStateReceiver);
    }

    public String getSavedNetwork(){
        String result = sharedPreferences.getString(getString(R.string.saved_network), null);
        return result;
    }

    public void saveNetwork(String networkName){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.saved_network), networkName);
        editor.commit();
    }

    public String getActualNetworkName(){

        ConnectivityManager conectivtyManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                return wifiInfo.getSSID();
            }
        }

        return null;
    }

}
