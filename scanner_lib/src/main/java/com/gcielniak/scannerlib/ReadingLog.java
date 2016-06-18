package com.gcielniak.scannerlib;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReadingLog implements OnReadingListener {
    File log_file;
    FileWriter log_file_writer;
    private static final String TAG = "ReadingLog";

    public void Start() {
        //log file
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss.ssss");
            Date current_date = new Date();
            log_file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "wifi_bt_log_" + sdf.format(current_date) + ".txt");
            log_file_writer = new FileWriter(log_file, false);
            log_file_writer.write(sdf.format(current_date) + "=" + SystemClock.elapsedRealtimeNanos() / 1000 + '\n');
        } catch (IOException exc) {
            Log.i(TAG, "Error opening file: " + log_file.getAbsolutePath());
        }
    }

    public void Stop(Context context) {
        //log file
        try {
            log_file_writer.close();
        } catch (IOException exc) {
            Log.i(TAG, "Error opening the file: " + log_file.getAbsolutePath());
        }

        MediaScannerConnection.scanFile(context,
                new String[]{log_file.getAbsolutePath()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i(TAG, "Finished scanning " + path);
                    }
                });

    }

    public void onReading(Reading reading) {

        if (log_file_writer != null) {
            try {
                log_file_writer.write(reading + "\n");
            } catch (IOException exc) {
                Log.i(TAG, "Error writing to file.");
            }
        }
    }
}