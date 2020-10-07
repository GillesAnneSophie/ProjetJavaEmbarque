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
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ShareActivity extends AppCompatActivity {
    private final static String CLASSNAME = ShareActivity.class.getSimpleName();
    private final static int REQUEST_ENABLE_BT = 1;
    private Handler handler = new Handler(); // handler that gets info from Bluetooth service

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }


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
                    ConnectedThread connectedThread = new ConnectedThread(socket);
                    connectedThread.start();
                    connectedThread.write("fezfezfef".getBytes());

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

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(CLASSNAME, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(CLASSNAME, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    Log.i(CLASSNAME, "BEFORE READ");
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Log.i(CLASSNAME, "BEFORE OBTAINMESSAGE");
                    Message readMsg = handler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    Log.i(CLASSNAME, "AFTER OBTAINMESSAGE");
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(CLASSNAME, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = handler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(CLASSNAME, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(CLASSNAME, "Could not close the connect socket", e);
            }
        }
    }
}