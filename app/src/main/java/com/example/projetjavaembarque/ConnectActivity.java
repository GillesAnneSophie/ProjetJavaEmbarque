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
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class ConnectActivity extends AppCompatActivity {
    // https://stackoverflow.com/questions/8188277/error-checking-if-bluetooth-is-enabled-in-android-request-enable-bt-cannot-be-r
    private final static int REQUEST_ENABLE_BT = 1;
    // private final static String CLASSNAME = ConnectActivity.getClass().getSimpleName();
    private final static String CLASSNAME = ConnectActivity.class.getSimpleName();
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    String dStarted = BluetoothAdapter.ACTION_DISCOVERY_STARTED;
    String dFinished = BluetoothAdapter.ACTION_DISCOVERY_FINISHED;

    BroadcastReceiver discoveryResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String remoteDeviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
            BluetoothDevice remoteDevice;
            remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Toast.makeText(getApplicationContext(), "Discovered: " + remoteDeviceName,
                    Toast.LENGTH_SHORT).show();
                    // TODO Do something with the remote Bluetooth Device.
        }
    };

    BroadcastReceiver discoveryMonitor = new BroadcastReceiver() {
        String dStarted = BluetoothAdapter.ACTION_DISCOVERY_STARTED;
        String dFinished = BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (dStarted.equals(intent.getAction())) {
                Toast.makeText(getApplicationContext(), "Discovery Started . . . ", Toast.LENGTH_SHORT).show();
                Log.i(CLASSNAME, "Discovery Monitor - Discovery Started");
            }
            else if (dFinished.equals(intent.getAction())) {
                Toast.makeText(getApplicationContext(), "Discovery Completed . . . ", Toast.LENGTH_SHORT).show();
                Log.i(CLASSNAME, "Discovery Monitor - Discovery Finished");
            }
            Log.i(CLASSNAME, "Discovery Monitor intent.getAction() : " + intent.getAction());
            Log.i(CLASSNAME, "Discovery Monitor intent.getExtras() : " + intent.getExtras());

            for (String key : intent.getExtras().keySet()) {
                Log.i(CLASSNAME, key + " " + intent.getExtras().get(key));//To Implement
            }


            Log.i(CLASSNAME, "Discovery Monitor intent.getStringExtra() : " + intent.getStringExtra(BluetoothAdapter.EXTRA_SCAN_MODE));
            Log.i(CLASSNAME, "Discovery Monitor intent.getStringExtra() : " + intent.getStringExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE));
            Log.i(CLASSNAME, "Discovery monitor - Onreceive");
        }
    };


    BroadcastReceiver bluetoothState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String prevStateExtra = BluetoothAdapter.EXTRA_PREVIOUS_STATE;
            String stateExtra = BluetoothAdapter.EXTRA_STATE;

            int state = intent.getIntExtra(stateExtra, -1);
            int previousState = intent.getIntExtra(prevStateExtra, -1);
            String tt = "";
            switch (state) {
                case (BluetoothAdapter.STATE_TURNING_ON) :
                {
                    Log.i(CLASSNAME, "BluetoothAdapter.STATE_TURNING_ON");
                    tt = "Bluetooth turning on";

                break;
                }
                case (BluetoothAdapter.STATE_ON) :
                { tt = "Bluetooth on";
                    Log.i(CLASSNAME, "BluetoothAdapter.STATE_ON");
                    // unregisterReceiver(this);
                    if (!bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.startDiscovery();
                        Log.i(CLASSNAME,"Run Discovery");
                    }
                    break;
                }
                case (BluetoothAdapter.STATE_TURNING_OFF) :
                { tt = "Bluetooth turning off";
                    Log.i(CLASSNAME, "BluetoothAdapter.STATE_TURNING_OFF");
                    break;
                }
                case (BluetoothAdapter.STATE_OFF) :
                { tt = "Bluetooth off";
                    Log.i(CLASSNAME, "BluetoothAdapter.STATE_OFF");
                    break;
                }
                default: break;
            }
            Toast.makeText(ConnectActivity.this, tt, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        Log.i(CLASSNAME, "onCreate");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
                String actionStateChanged = BluetoothAdapter.ACTION_STATE_CHANGED;
                String actionRequestEnable = BluetoothAdapter.ACTION_REQUEST_ENABLE;
                registerReceiver(bluetoothState, new IntentFilter(actionStateChanged));
                startActivityForResult(new Intent(actionRequestEnable), 0);
            } else {
                String actionStateChanged = BluetoothAdapter.ACTION_STATE_CHANGED;
                String actionRequestEnable = BluetoothAdapter.ACTION_REQUEST_ENABLE;
                registerReceiver(bluetoothState, new IntentFilter(actionStateChanged));
                startActivityForResult(new Intent(actionRequestEnable), 0);
            }
        /*
            IntentFilter discoveryMonitorIntentFilter = new IntentFilter();
            discoveryMonitorIntentFilter.addAction(dStarted);
            discoveryMonitorIntentFilter.addAction(dFinished);
            registerReceiver(discoveryMonitor, discoveryMonitorIntentFilter);
        */
            //IntentFilter discoveryMonitorIntentFilter = new IntentFilter();
            //discoveryMonitorIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            //discoveryMonitorIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            //discoveryMonitorIntentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);

            registerReceiver(discoveryMonitor, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
            registerReceiver(discoveryMonitor, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

            BluetoothAdapter.getDefaultAdapter().startDiscovery();
            //registerReceiver(discoveryMonitor, discoveryMonitorIntentFilter);


            // registerReceiver(discoveryMonitor, new IntentFilter(dStarted));
            // registerReceiver(discoveryMonitor, new IntentFilter(dFinished));
            registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));

            // Before performing device discovery, it's worth querying the set of paired devices to see if the desired device is already known.
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                Log.wtf(CLASSNAME,"Number of paired devices : "+ pairedDevices.size());
                Log.wtf(CLASSNAME,"Devices NAME and MAC : ");
                int i = 0;

                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC addressLog.i(CLASSNAME,i + " : " + deviceName + " | " + deviceHardwareAddress);
                }
            }

            Boolean test;

            if (!bluetoothAdapter.isDiscovering()) {
                test = bluetoothAdapter.startDiscovery();
                Log.wtf(CLASSNAME,test.toString());
            }

            Log.wtf(CLASSNAME,"TEST TEST TEST");
            // Log.wtf(CLASSNAME,test.toString());
            // Register for broadcasts when a device is discovered.
            /*
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver, filter);
             */
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver discovery = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(CLASSNAME,"BroadcastReceiver");
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
        //unregisterReceiver(receiver);
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