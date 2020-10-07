package com.example.projetjavaembarque;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class ConnectActivity extends AppCompatActivity {
    // https://stackoverflow.com/questions/8188277/error-checking-if-bluetooth-is-enabled-in-android-request-enable-bt-cannot-be-r
    private final static int REQUEST_ENABLE_BT = 1;
    // private final static String CLASSNAME = ConnectActivity.getClass().getSimpleName();
    private final static String CLASSNAME = ConnectActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        Log.i(CLASSNAME, "onCreate");
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.i(CLASSNAME, "onCreate");
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.i(CLASSNAME,"Device doesn't support Bluetooth");

            // TODO - Determine app's behaviour
        } else {
            // TODO - Determine app's behaviour
            Log.i(CLASSNAME,"Device support Bluetooth");
            // If bluetooth is not enabled, ask user input for enabling
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            // Before performing device discovery, it's worth querying the set of paired devices to see if the desired device is already known.
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                Log.wtf(CLASSNAME,"Number of paired devices : "+ pairedDevices.size());
                Log.wtf(CLASSNAME,"Devices NAME and MAC : ");
                int i = 0;

                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    Log.i(CLASSNAME,i + " : " + deviceName + " | " + deviceHardwareAddress);
                }

                // Register for broadcasts when a device is discovered.
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, filter);

            }
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                Log.i(CLASSNAME,"Discovered device : Name = " + deviceName + " | Mac = " + deviceHardwareAddress);

                ConnectThread thread = new ConnectThread(device);
                thread.start();

                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();


            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }


    public void onClickEventConnectButton(View view){
        Intent launchActivityVideo = new Intent(ConnectActivity.this, VideoActivity.class);
        startActivity(launchActivityVideo);
    }
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final String CLASSNAME = ConnectThread.class.getSimpleName();

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                String uniqueID = UUID.randomUUID().toString();

                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(uniqueID));
            } catch (IOException e) {
                Log.e(CLASSNAME, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            // bluetoothAdapter.cancelDiscovery();
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(CLASSNAME, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            //manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(CLASSNAME, "Could not close the client socket", e);
            }
        }
    }
}