package com.ugosmoothie.ugocontrolapp.USB_COMM;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.ugosmoothie.ugocontrolapp.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;

/**
 * Created by Yuyao Lian on 2016-10-01.
 */

public class ArduinoComm {

    private static UsbSerialPort sPort = null;
    private UsbManager mUsbManager;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;

    private final long StartOfMessage = 0x557E;
    private final long EndOfMessage = 0x557F;

    private Activity activity;

    public ArduinoComm(Activity activity) {
        this.activity = activity;

        mUsbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
        RefreshDeviceList();


    }

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {

                }

                @Override
                public void onNewData(final byte[] data) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateReceivedData(data);
                        }
                    });
                }
            };

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (sPort != null) {
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    private byte[] inBuffer = new byte[255];
    private int read = 0;

    private void updateReceivedData(byte[] data) {
        final String message = "Read " + data.length + " bytes: \n"
                + HexDump.dumpHexString(data) + "\n\n";
        // read it to in buffer
        for (int i = 0; i < data.length; i++) {
            inBuffer[read++]= data[i];
        }

        try {
            checkForCompleteMessage();
        }
        catch(Exception e) {
        }
        //mDumpTextView.append(message);
        //mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
    }

    private final int MinimumPacketSize = 12;

    private void checkForCompleteMessage() {
        for (int i = 0; i < read;i++) {
            // check for start
            if (inBuffer[i] == 0x7E) {
                // possible message start
                if (read - i > 1) {
                    if (inBuffer[i + 1] == 0x55)
                        System.arraycopy(inBuffer, i, inBuffer, 0, read - i);
                    read = read - i;
                }
            }
        }
        if (read >= MinimumPacketSize) { // 12 is the minimum message size
            long sof = (inBuffer[1] << 8) + inBuffer[0];

            if (sof == StartOfMessage) {
                // valid message, good start
                int messageLength = ((inBuffer[3] << 8) & 0xFF) + (inBuffer[2] & 0xFF);
                // TODO: check CRC16 to make sure pack valid
                if (read > messageLength) {
                    // print entire in buffer
                    int messageIntId = ((inBuffer[7] << 8) + inBuffer[6]);
                    ArduinoMessageId messageId = ArduinoMessageId.find(messageIntId);
                    switch (messageId) {
                        case GetFirmwareVersionReply:
                            read = 0;
                            break;

                        case AutoCycleReply:
                            break;
                        case Heartbeat:
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((MainActivity)activity).ToggleIndication();
                                }
                            });
                            break;

                        case StatusMessage:
                            byte[] messageToPrint2 = new byte[messageLength - 12];
                            System.arraycopy(inBuffer, 8, messageToPrint2, 0, messageLength - 13);
                            final String print2 = new String(messageToPrint2);

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText (activity, print2, Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;


                        case log:
                            byte[] messageToPrint = new byte[messageLength - 12];
                            System.arraycopy(inBuffer, 8, messageToPrint, 0, messageLength - 13);
                            String print = new String(messageToPrint);
                            break;
                        default:
                            break;
                    }

                    System.arraycopy(inBuffer, messageLength, inBuffer, 0, read - messageLength);
                    read = read - messageLength;
                }
            } else {
            }
        }
    }

    public void RefreshDeviceList() {

        new AsyncTask<Void, Void, List<UsbSerialPort>>() {
            @Override
            protected List<UsbSerialPort> doInBackground(Void... params) {
                SystemClock.sleep(1000);

                final List<UsbSerialDriver> drivers =
                        UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);

                final List<UsbSerialPort> result = new ArrayList<UsbSerialPort>();
                for (final UsbSerialDriver driver : drivers) {
                    final List<UsbSerialPort> ports = driver.getPorts();
                    result.addAll(ports);
                }

                return result;
            }

            @Override
            protected void onPostExecute(List<UsbSerialPort> result) {
                if (result.size() > 0) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText (activity, "Connected To Device", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // we have a device connected. let's control it.
                    fireUpTheArduino(result.get(0));

                    stopIoManager();
                    startIoManager();
                }
                else{
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText (activity, "NOT Connected To Device", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }.execute((Void) null);
    }

    private void fireUpTheArduino(UsbSerialPort port) {
        if (port != null) {
            sPort = port;
            final UsbManager usbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);

            UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
            if (connection == null) {
                //mTitleTextView.setText("Opening device failed");
                return;
            }

            try {
                sPort.open(connection);
                sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            } catch (IOException e) {
                //mTitleTextView.setText("Error opening device: " + e.getMessage());
                try {
                    sPort.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                sPort = null;
            }
        }
    }

    public enum ArduinoMessageId {
        Heartbeat(0x0000),
        AutoCycle(0x0001),
        AutoCycleReply(0x0A01),
        MoveUp(0x0002),
        MoveDown(0x0003),
        JogTop(0x0004),
        GetFirmwareVersionReply(0x0A04),
        JogBottom(0x0005),
        GetActuatorState(0x0006),
        Sanitize(0x0007),
        log(0x0008),
        Initialize(0x009),
        Stop(0x00A),
        ToggleActuatorState(0x000B),
        StatusMessage(0x000D),
        Reblend(0x000C),
        DisableKeyPad(0x000E);

        private int value;

        private ArduinoMessageId(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static ArduinoMessageId find (int value) {
            ArduinoMessageId stuff[] = ArduinoMessageId.values();
            for (int i =  0; i < stuff.length; i++) {
                if (stuff[i].getValue() == value) {
                    return stuff[i];
                }
            }
            return null;
        }
    }

    public void WriteMessageToArduino(short message_id, byte[] message, short length) {

        byte toSend[];
        toSend = new byte[MinimumPacketSize + message.length];

        int i = 0;

        toSend[i++] = (byte)(StartOfMessage & 0xFF);
        toSend[i++] = (byte)((StartOfMessage >> 0x8) & 0xFF);
        toSend[i++] = (byte)(toSend.length & 0xFF);
        toSend[i++] = (byte)((toSend.length >> 0x8) & 0xFF);
        toSend[i++] = 0; // source address
        toSend[i++] = 0; // destination address
        toSend[i++] = (byte)(message_id & 0xFF);
        toSend[i++] = (byte)((message_id >> 0x8) & 0xFF);

        for (int j = 0; j < message.length; j++) {
            toSend[i++] = message[j];
        }

        toSend[i++] = 0; // CRC16
        toSend[i++] = 0; // CRC16

        toSend[i++] = (byte)(EndOfMessage & 0xFF);
        toSend[i++] = (byte)((EndOfMessage >> 0x8) & 0xFF);

        // send the message to Arduino
        if (sPort != null) {
            try {
                sPort.write(toSend, 500);
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText (activity, "Message Written", Toast.LENGTH_SHORT).show();
//                    }
//                });
            } catch (Exception ex) {
                final String msg = ex.getMessage();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText (activity, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
        }
    }

    public void SendAutoCycleMessage() {
        byte autoCycle[] = new byte[3];
        autoCycle[0] = 0;//(byte)CurrentSelection.getInstance().getCurrentSmoothie();
        autoCycle[1] = 0;//(byte)CurrentSelection.getInstance().getCurrentLiquid();
        autoCycle[2] = 0;//(byte)CurrentSelection.getInstance().getCurrentSupplement();
        WriteMessageToArduino((short) ArduinoMessageId.AutoCycle.getValue(), autoCycle, (short) autoCycle.length);
    }
}
