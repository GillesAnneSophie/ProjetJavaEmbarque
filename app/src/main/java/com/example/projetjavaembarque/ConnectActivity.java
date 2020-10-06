package com.example.projetjavaembarque;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Set;

public class ConnectActivity extends AppCompatActivity {
    // https://stackoverflow.com/questions/8188277/error-checking-if-bluetooth-is-enabled-in-android-request-enable-bt-cannot-be-r
    private final static int REQUEST_ENABLE_BT = 1;
    // private final static String CLASSNAME = ConnectActivity.getClass().getSimpleName();
    private final static String CLASSNAME = ConnectActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.wtf(CLASSNAME,"Device doesn't support Bluetooth");

            // TODO - Determine app's behaviour
        } else {
            // TODO - Determine app's behaviour

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
}