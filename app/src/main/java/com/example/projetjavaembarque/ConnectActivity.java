package com.example.projetjavaembarque;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

    ArrayList<String> devicesName = new ArrayList<String>();
    ArrayList<String> devicesMac = new ArrayList<String>();

    HashMap<String,String> spinnerMap;
    HashMap<String,BluetoothDevice> devicesMap = new HashMap<String,BluetoothDevice>();;

    String[] spinnerArray;

    Spinner connectionSelectionSpinner;

    private Handler handler = new Handler(); // handler that gets info from Bluetooth service

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }

    BroadcastReceiver discoveryResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String remoteDeviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
            BluetoothDevice remoteDevice;
            remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            /*
            Toast.makeText(getApplicationContext(), "Discovered: " + remoteDeviceName,
                    Toast.LENGTH_SHORT).show();
                    Log.i(CLASSNAME,"Discovered device : Name = " + remoteDeviceName + " | Mac = " + remoteDevice.getAddress());

             */
                    if(!(remoteDeviceName == null || remoteDeviceName.equals("null"))  && (!devicesMap.containsValue(remoteDevice))) {
                        devicesName.add(remoteDeviceName);
                        devicesMac.add(remoteDevice.getAddress());
                        devicesMap.put(remoteDevice.getAddress(), remoteDevice);
                        // (remoteDevice.getAddress(), remoteDevice);
                        Log.i(CLASSNAME,"Discovereeeeed device : Name = " + remoteDeviceName + " | Mac = " + remoteDevice.getAddress());
                        // Log.i(CLASSNAME,"Mapped device : Mac = " + remoteDevice.getAddress() + " | Mapped Mac = " + devicesMap.get(remoteDevice.getAddress()).getAddress());
                    }

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
                //Toast.makeText(getApplicationContext(), "Discovery Started . . . ", Toast.LENGTH_SHORT).show();
                Log.i(CLASSNAME, "Discovery Monitor - Discovery Started");
            }
            else if (dFinished.equals(intent.getAction())) {
                Toast.makeText(getApplicationContext(), "Discovery Completed . . . ", Toast.LENGTH_SHORT).show();
                spinnerMap = new HashMap<String, String>();
                spinnerArray = new String[devicesName.size()];
                for (int i = 0; i < devicesName.size(); i++)
                {
                    Log.i(CLASSNAME, "FOR I : "+i);
                    spinnerMap.put(devicesName.get(i),devicesMac.get(i));
                    spinnerArray[i] = devicesName.get(i);
                }


                Log.i(CLASSNAME,"ArrayAdapter-1");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, spinnerArray);
                Log.i(CLASSNAME,"ArrayAdapter-2");
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                connectionSelectionSpinner.setAdapter(null);
                connectionSelectionSpinner.setAdapter(adapter);

                Log.i(CLASSNAME, "Discovery Monitor - Discovery Finished");
            }
            /*
            Log.i(CLASSNAME, "Discovery Monitor intent.getAction() : " + intent.getAction());
            Log.i(CLASSNAME, "Discovery Monitor intent.getExtras() : " + intent.getExtras());

            Log.i(CLASSNAME, "Discovery Monitor intent.getStringExtra() : " + intent.getStringExtra(BluetoothAdapter.EXTRA_SCAN_MODE));
            Log.i(CLASSNAME, "Discovery Monitor intent.getStringExtra() : " + intent.getStringExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE));

            Log.i(CLASSNAME, "Discovery monitor - onReceive");
             */
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
                    /*
                    if (!bluetoothAdapter.isDiscovering()) {
                        ActivityCompat.requestPermissions(ConnectActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);
                        if (bluetoothAdapter!=null) {
                            bluetoothAdapter.startDiscovery();
                            Log.i(CLASSNAME, "Run Discovery");
                        }
                    }
                     */
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

        final Button refreshButton = findViewById(R.id.refreshButton);
        connectionSelectionSpinner = findViewById(R.id.connectionSelect);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //if (!bluetoothAdapter.isDiscovering()) {
                    ActivityCompat.requestPermissions(ConnectActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);
                    if (bluetoothAdapter!=null) {
                        bluetoothAdapter.startDiscovery();
                        Log.i(CLASSNAME, "Run Discovery");
                    }
                //}
                // Code here executes on main thread after user presses button
            }
        });

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

            registerReceiver(discoveryMonitor, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
            registerReceiver(discoveryMonitor, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

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
/*
            Boolean test;

            if (!bluetoothAdapter.isDiscovering()) {
                test = bluetoothAdapter.startDiscovery();
                Log.wtf(CLASSNAME,test.toString());
            }
*/
            Log.wtf(CLASSNAME,"TEST TEST TEST");
            // Log.wtf(CLASSNAME,test.toString());
            // Register for broadcasts when a device is discovered.
            /*
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver, filter);
             */
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        //unregisterReceiver(receiver);
    }


    public void onClickEventConnectButton(View view){
        Bundle newBundle = new Bundle();

        String name = connectionSelectionSpinner.getSelectedItem().toString();

        newBundle.putString("deviceName",name);
        newBundle.putString("deviceMac",spinnerMap.get(name));

        Log.i(CLASSNAME, "deviceName : "+name);
        Log.i(CLASSNAME, "deviceMac : "+spinnerMap.get(name));
        Log.i(CLASSNAME, "MappeddeviceMac : "+devicesMap.get(spinnerMap.get(name)).getAddress());
        ConnectThread thread = new ConnectThread(devicesMap.get(spinnerMap.get(name)));
        thread.run();

        //Log.i(name);
    }

    public void onClickEventStartVideoButton(View view){
        Intent launchActivityVideo = new Intent(ConnectActivity.this, VideoActivity.class);
        //launchActivityVideo.putExtras(launchActivityVideo);
        //launchActivityVideo.putExtras(newBundle);
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
                //String uniqueID = "TEST";
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("10feabf6-c971-42d5-aeb8-00b15e40672f"));
                Log.i(CLASSNAME, "UUID : "+uniqueID);
            } catch (IOException e) {
                Log.e(CLASSNAME, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            // bluetoothAdapter.cancelDiscovery();
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

            Log.i(CLASSNAME, "ConnectThread - run");
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                Log.i(CLASSNAME, "ConnectThread - run : connect()");
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                Log.i(CLASSNAME, "ConnectThread - run : unable to connect");
                Log.i(CLASSNAME, "ConnectThread - run : Exception -> "+ connectException);
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(CLASSNAME, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            ConnectedThread connectedThread = new ConnectedThread(mmSocket);
            connectedThread.start();

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

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private FileOutputStream mmFileOutStream;
        private BufferedOutputStream mmBufferedOutStream;

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

            File file = new File(getExternalFilesDir(null),"video.mp4");
            if(file.exists()) {
                file.delete();
            }

            try {
                mmFileOutStream = new FileOutputStream(file, true);
                mmBufferedOutStream = new BufferedOutputStream(mmFileOutStream);
            } catch (IOException e) {
                e.printStackTrace();
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
                    /*
                    Message readMsg = handler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);

                    */
                    Message readMsg;
                    if(numBytes>0){
                        readMsg = handler.obtainMessage(MessageConstants.MESSAGE_READ, numBytes, -1, Arrays.copyOf(mmBuffer,numBytes));
                        byte[] readBuf = (byte[]) readMsg.obj;
                        mmBufferedOutStream.write(readBuf);
                        //mmBufferedOutStream.write(numBytes);
                        mmBuffer.toString();
                        Log.i(CLASSNAME, "BYTES :" + mmBuffer.toString() );
                        Log.i(CLASSNAME, "SIZE :" + readBuf.length );
                        //mmFileOutStream.write(numBytes);
                        //mmFileOutStream.append(numBytes);
                        //mmFileOutStream.close();
                        readMsg.sendToTarget();

                    }
                    else {
                        try {
                            sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    Log.i(CLASSNAME, "AFTER OBTAINMESSAGE");
                    //Log.i(CLASSNAME, readMsg.toString());
                    // Log.i(CLASSNAME, "RECEIVING DATA : "+readMsg.obj.toString());

                    //byte[] readBuf = (byte[]) readMsg.obj;

                    //String readMessage = new String(readBuf, 0, readMsg.arg1);
                    //Log.i(CLASSNAME, "RECEIVING DATA : "+readMessage);
                    // Log.i(CLASSNAME, "MESSAGE obg readMessage : "+readMessage);
/*
                    FileOutputStream out = new FileOutputStream("video.mp4");
                    out.write(videoData);
                    out.close();
*/
                    int length;
//socketInputStream never returns -1 unless connection is broken
/*
                    while ((length = mmInStream.read(mmBuffer)) != -1) {
                        //mmFileOutStream.write(mmBuffer, 0, length);
                        //mmBuffer.toString();
                        mmFileOutStream.write(readBuf);
                        Log.i(CLASSNAME, "BYTES :" + mmBuffer.toString() );
                    }
*/
                    //mmFileOutStream.write(mmBuffer);
                    // mmFileOutStream.write(readBuf);
                    //mmFileOutStream.write(readBuf);
                    /*
                    mmBufferedOutStream.write(readBuf);
                    //mmBufferedOutStream.write(numBytes);
                    mmBuffer.toString();
                    Log.i(CLASSNAME, "BYTES :" + mmBuffer.toString() );
                    Log.i(CLASSNAME, "SIZE :" + readBuf.length );
                    //mmFileOutStream.write(numBytes);
                    //mmFileOutStream.append(numBytes);
                    //mmFileOutStream.close();
                    readMsg.sendToTarget();
                     */
                } catch (IOException e) {
                    Log.d(CLASSNAME, "Input stream was disconnected", e);
                    break;
                }
            }
            try {
                mmFileOutStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = handler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);

                Log.e(CLASSNAME, "SENDING DATA : " + writtenMsg.obj.toString());

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