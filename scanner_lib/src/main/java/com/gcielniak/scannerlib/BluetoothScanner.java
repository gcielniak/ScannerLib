package com.gcielniak.scannerlib;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.SystemClock;

import java.util.Arrays;

/**
 * Use BLE beacons
 */
public class BluetoothScanner {
    Pose current_pose;
    BluetoothAdapter adapter;
    BluetoothReceiver receiver;

    public BluetoothScanner(OnReadingListener listener) {
        adapter = BluetoothAdapter.getDefaultAdapter();
        receiver = new BluetoothReceiver(listener);
        current_pose = new Pose();
    }

    public boolean IsEnabled() {
        return adapter.isEnabled();
    }

    public void UpdatePose(Pose current_pose) {
        this.current_pose = current_pose;
    }

    public boolean Start() {
        if (adapter.isEnabled()) {
            adapter.startLeScan(receiver);//old call to make it compatible with Tango Android version
            return true;
        }
        else {
            return false;
        }
    }

    public void Stop() {
        adapter.stopLeScan(receiver);
    }

    private class BluetoothReceiver implements BluetoothAdapter.LeScanCallback {

        OnReadingListener listener;

        BluetoothReceiver(OnReadingListener listener) {
            this.listener = listener;
        }

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

            Reading reading = new Reading();
            reading.device_type = Reading.DeviceType.BT_BEACON;
            reading.setMacAddress(device.getAddress());
            reading.name = device.getName();
            reading.timestamp = SystemClock.elapsedRealtimeNanos() / 1000;
            reading.value = (double) rssi;
            reading.translation = current_pose.translation;
            reading.rotation = current_pose.rotation;
            reading.uuid = new UUID(Arrays.copyOfRange(scanRecord, 9, 29));

            listener.onReading(reading);
        }
    }
}
