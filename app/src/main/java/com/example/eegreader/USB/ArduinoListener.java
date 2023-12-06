package com.example.eegreader.USB;

import android.hardware.usb.UsbDevice;

/**
 * Created by Omar on 21/05/2017.
 */
//Old library used for lightweight apps
public interface ArduinoListener {
    void onArduinoAttached(UsbDevice device);
    void onArduinoDetached();
    void onArduinoMessage(byte[] bytes);
    void onArduinoOpened();
    void onUsbPermissionDenied();
}
