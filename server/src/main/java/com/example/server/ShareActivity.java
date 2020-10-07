package com.example.server;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ShareActivity extends AppCompatActivity {
    private final static String CLASSNAME = ShareActivity.class.getSimpleName();
    private final static int REQUEST_ENABLE_BT = 1;
    private final BroadcastReceiver bluetoothStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.i(CLASSNAME, "Bluetooth State BroadReceiver - Bluetooth State - Off");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.i(CLASSNAME, "Bluetooth State BroadReceiver - Bluetooth State - Turning Off");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        enableDiscovery();
                        Log.i(CLASSNAME, "Bluetooth State BroadReceiver - Bluetooth State - On");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.i(CLASSNAME, "Bluetooth State BroadReceiver - Bluetooth State - Turning On");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver bluetoothDiscoveryStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch(mode){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.i(CLASSNAME, "Bluetooth Discovery BroadReceiver - Discovery State - SCAN_MODE_CONNECTABLE_DISCOVERABLE");
                        AcceptThread thread = new AcceptThread();
                        thread.start();
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.i(CLASSNAME, "Bluetooth Discovery BroadReceiver - Discovery State - SCAN_MODE_CONNECTABLE");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.i(CLASSNAME, "Bluetooth Discovery BroadReceiver - Discovery State - SCAN_MODE_NONE");
                        break;
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothStateBroadcastReceiver);
        unregisterReceiver(bluetoothDiscoveryStateBroadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_share);

        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateBroadcastReceiver, filter1);
/*

*/
        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter2.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter2.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter2.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(bluetoothDiscoveryStateBroadcastReceiver, filter2);
/*
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String prevScanMode = BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE;
                String scanModeStr = BluetoothAdapter.EXTRA_SCAN_MODE;
                int scanMode = intent.getIntExtra(scanModeStr, -1);
                int prevMode = intent.getIntExtra(prevScanMode, -1);
            }
        }, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
*/
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Log.i(CLASSNAME,"onCreate - Bluetooth is available on this device");
            // Toast or FragmentDialog
        } else if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Log.i(CLASSNAME,"onCreate - Bluetooth isn't enabled");

            /*
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
             */
        }
        enableDiscovery();
        //Log.i(CLASSNAME,"onCreate - Processing bluetooth data");
    }

    public void enableDiscovery() {
        /*
        String aDiscoverable = BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE;
        startActivityForResult(new Intent(aDiscoverable), REQUEST_ENABLE_BT);
        */


        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            boolean isDiscoverable = resultCode > 0;
            int discoverableDuration = resultCode;
        }
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            String uniqueId = UUID.randomUUID().toString();
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord("NAME", UUID.fromString("10feabf6-c971-42d5-aeb8-00b15e40672f"));
            } catch (IOException e) {
                Log.e(CLASSNAME, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket;

            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                Log.i(CLASSNAME, "ThreadAccept run() - Thread running ...");
                try {
                    socket = mmServerSocket.accept();
                    Log.i(CLASSNAME, "ThreadAccept run() - Accept Connection");
                    //Log.i(CLASSNAME, "ThreadAccept run() - accept");
                } catch (IOException e) {
                    Log.e(CLASSNAME, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    Log.i(CLASSNAME, "ThreadAccept run() - Connection accepted");
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    //manageMyConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(CLASSNAME, "Could not close the connect socket", e);
            }
        }
    }

}