package com.gcielniak.scannerlib;

import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Read from the log created by the ReadingLog class.
 *
 * Currently reads from the Downloads folder only.
 */
public class FileScanner {
    OnReadingListener listener;
    FileScanReceiver receiver;
    boolean stop;
    BufferedReader reader;
    File file;

    public FileScanner(OnReadingListener listener) {
        this.listener = listener;
    }

    public void SetFile(String file_name) {
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    file_name);
            reader = new BufferedReader(new FileReader(file));
            reader.readLine();
        } catch (IOException exc) {
            Log.i("FileScanner", "Error opening file: " + file.getAbsolutePath());
        }
    }

    /**
     * Start scanning.
     */
    public void Start() {
        stop = false;
        try {
            if (reader != null)
                reader.reset();
            else
                return;
        } catch (IOException exc) {
            return;
        }
        new FileScanReceiver(listener).execute();
    }

    /**
     * Stop scanning.
     */
    public void Stop() {
        try {
            if (reader != null)
                reader.mark(0);
        } catch (IOException exc) {
        }
        stop = true;
    }

    /**
     * Keep reading file in asynchronous fashion in its own thread.
     * The function waits between consecutive readings depending on time stamp difference
     *
     */
    private class FileScanReceiver extends AsyncTask<Void, Void, Void> {

        OnReadingListener listener;

        FileScanReceiver(OnReadingListener listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String str;

                long start_system_nanos = SystemClock.elapsedRealtimeNanos();
                long start_file_nanos = 0;

                while (!stop && (str = reader.readLine()) != null && str.length() != 0) {
                    String[] res = str.split(" ");
                    Reading reading = new Reading();
                    if (res[0].equals("BT:"))
                        reading.device_type = Reading.DeviceType.BT_BEACON;
                    else if (res[0].equals("WF:")) {
                        reading.device_type = Reading.DeviceType.WIFI_AP;
                    } else {
                        continue;
                    }

                    reading.timestamp = Long.parseLong(res[1].substring(2));
                    reading.name = res[2].substring(2, res[2].length() - 1);
                    reading.setMacAddress(res[3].substring(2));
                    reading.value = Double.parseDouble(res[4].substring(2));
                    //Tango stuff
                    reading.translation[0] = Double.parseDouble(res[5].substring(2));
                    reading.translation[1] = Double.parseDouble(res[6]);
                    reading.translation[2] = Double.parseDouble(res[7]);
                    reading.rotation[0] = Double.parseDouble(res[8].substring(2));
                    reading.rotation[1] = Double.parseDouble(res[9]);
                    reading.rotation[2] = Double.parseDouble(res[10]);
                    reading.rotation[3] = Double.parseDouble(res[11]);

                    if (start_file_nanos == 0)
                        start_file_nanos = reading.timestamp * 1000;

                    while ((SystemClock.elapsedRealtimeNanos() - start_system_nanos) < (reading.timestamp * 1000 - start_file_nanos))
                        Thread.sleep(10);

                    listener.onReading(reading);
                }
            }
            catch (IOException exc) {}
            catch (InterruptedException exc) {}
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
        }
    }
}
