package com.gcielniak.scannerlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Use Wifi access points as location beacons.
 */
public class WifiScanner {
    Pose current_pose;
    WifiManager wifi;
    WifiReceiver receiver;
    Context context;

    public WifiScanner(Context context, OnReadingListener listener) {
        this.context = context;
        wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        receiver = new WifiReceiver(listener);
        current_pose = new Pose();
    }

    public void UpdatePose(Pose current_pose) {
        this.current_pose = current_pose;
    }

    public boolean IsEnabled() {
        return wifi.isWifiEnabled();
    }

    public void Start() {
        context.registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifi.startScan();
    }

    public void Stop() {
        context.unregisterReceiver(receiver);
    }

    private class WifiReceiver extends BroadcastReceiver {

        OnReadingListener listener;

        WifiReceiver(OnReadingListener listener) {
            this.listener = listener;
        }

        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = wifi.getScanResults();

            for (int i = 0; i < wifiScanList.size(); i++) {
                ScanResult result = wifiScanList.get(i);

                Reading reading = new Reading();
                reading.device_type = Reading.DeviceType.WIFI_AP;
                reading.setMacAddress(result.BSSID.toUpperCase());
                reading.name = result.SSID;
                reading.timestamp = result.timestamp;
                reading.value = (double) result.level;
                reading.translation = current_pose.translation;
                reading.rotation = current_pose.rotation;

                listener.onReading(reading);
            }
            wifi.startScan();
        }
    }
}
