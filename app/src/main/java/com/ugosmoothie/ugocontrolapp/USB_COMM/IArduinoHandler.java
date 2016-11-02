package com.ugosmoothie.ugocontrolapp.USB_COMM;

/**
 * Created by Yuyao Lian on 2016-10-01.
 */

public interface IArduinoHandler {
    void onUsbStopped();

    void onErrorLooperRunningAlready();

    void onDeviceNotFound();

}
