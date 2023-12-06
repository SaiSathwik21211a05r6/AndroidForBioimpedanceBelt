package com.example.eegreader.USB;

import android.app.Activity;
import android.content.ComponentName;
import android.os.IBinder;

import androidx.annotation.NonNull;

import java.util.ArrayDeque;
//Responding to USB Conncetion events
interface SerialListener {
    void onSerialConnect      ();
    void onSerialConnectError (Exception e);
    void onSerialRead         (byte[] data);                // socket -> service
    void onSerialRead         (ArrayDeque<byte[]> datas);   // service -> UI thread
    void onSerialIoError      (Exception e);

    void onServiceConnected(ComponentName name, IBinder binder);

    void onServiceDisconnected(ComponentName name);

    void onBackStackChanged();
}
