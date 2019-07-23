package com.summerjob.neoenergia3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.summerjob.neoenergia3.Util.Util;
import com.summerjob.neoenergia3.model.Device;
import com.summerjob.neoenergia3.model.WiFi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String ROOT = "";

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String refreshedToken;
    private String locationCurrent;
    private List<WiFi> wiFiList;

    private Device device;

    private WifiManager wifiManager;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.databaseReference.getRef();
        this.refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //this.refreshDataDevice();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        this.wiFiList = verifyConection();
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

    public void refreshDataDevice() {

        this.databaseReference.child(ROOT).orderByChild("token").equalTo(this.refreshedToken).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Device device = null;

                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    device = noteDataSnapshot.getValue(Device.class);
                    setDeviceCurrent(device);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void setDeviceCurrent(Device device){
        this.device = device;
    }

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);

            switch (wifiStateExtra) {
                case WifiManager.WIFI_STATE_ENABLED:
                    Toast.makeText(context, "WiFi is ON", Toast.LENGTH_LONG).show();
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    Toast.makeText(context, "WiFi is OFF", Toast.LENGTH_LONG).show();

                    String format = simpleDateFormat.format(new Date());

                    if (device == null) {



                        Device device = new Device();
                        device.setLocation(locationCurrent);
                        device.setToken(refreshedToken);
                        device.setTimestamp(format);

                        db.collection("entries").add(device).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getApplicationContext(), "Succes", Toast.LENGTH_LONG).show();

                                ROOT = documentReference.getId();

                                Log.d("FIRESTORE", "root: "+ROOT);

                                for (WiFi w : wiFiList){

                                    Log.d("FIRESTORE", "Wifi:  "+w);

                                    db.collection("entries").document(ROOT).collection("wifiList").add(w).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(getApplicationContext(), "WiFi Succes", Toast.LENGTH_LONG).show();
                                            Log.d("FIRESTORE", "documentReference: "+documentReference);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "WiFi Failer", Toast.LENGTH_LONG).show();
                                            Log.d("FIRESTORE", "Wifi Exception: "+e.getMessage());
                                        }
                                    });

                                }

                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Failer", Toast.LENGTH_LONG).show();
                            }
                        });



                    }

                    break;
            }
        }
    };

    public List<WiFi> verifyConection() {

        List<WiFi> wifiList = new ArrayList<>();

        ConnectivityManager conectivtyManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            ArrayList<ScanResult> list = (ArrayList<ScanResult>) wifiManager.getScanResults();

            for(int i = 0; i < list.size(); i++) {
                for(int j = i + 1; j < list.size(); j++) {

                    if(list.get(i).SSID.equals(list.get(j).SSID)){
                        list.remove(j);
                        j--;
                    }

                }
            }

            if (list != null){

                for (ScanResult sr : list) {
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

}
