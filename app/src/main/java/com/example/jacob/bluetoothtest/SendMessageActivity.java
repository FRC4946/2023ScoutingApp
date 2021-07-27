package com.example.jacob.bluetoothtest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.WidgetContainer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class SendMessageActivity extends AppCompatActivity {

    //TODO : Add unique message/CSV identifier

    //TODO : Take connect off of ui thread

    public static BluetoothSocket socket;

    BluetoothDevice hostComputer;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();;

    private OutputStream outputStream; //Stream data is written to
    private InputStream inStream; //Stream data is read from, currently unused

    File fileToSend = null;

    String computerAddress = "3C:F8:62:C5:8D:C4";
    final String stringUUID = "39675b0d-6dd8-4622-847f-3e5acc607e27";
    UUID ConnectToUUID = UUID.fromString(stringUUID);

    boolean connected = false;

    String message;

    EditText connectionMAC;
    TextView connectionInfo;
    ToggleButton saveButton, sendSavedButton;
    Button sendButton;
    ProgressBar sendingBar;

    boolean sending = false;



    /** Launches when activity starts
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        message = intent.getStringExtra("MessageToSend"); //Gets message from previous activity, stores in message string

        //Deals with loaded files
        String loadName = intent.getStringExtra("LoadedFile"); //Gets name of loaded file if it exists
        final boolean loadedFile = (!(loadName == null));//Finds out if the file is loaded
        if (loadedFile) {
            fileToSend = new File(loadName);
        }

        connectionMAC = (EditText) findViewById(R.id.MacText);
        connectionMAC.setText(computerAddress);

        connectionInfo = (TextView) findViewById(R.id.EsablishingConnection);
        connectionInfo.setText("Not Connected");

        final TextView messageInfo = (TextView) findViewById(R.id.ActivityMessage);
        messageInfo.setText(message);

        sendingBar = (ProgressBar) findViewById(R.id.Sending);
        sendingBar.setVisibility(View.INVISIBLE);

        saveButton = (ToggleButton) findViewById(R.id.SaveOnSend);
        saveButton.setChecked(true);

        sendSavedButton = (ToggleButton) findViewById(R.id.SendSaved);

        final TextView saveOnSendDescription = (TextView) findViewById(R.id.SaveOnSendDescription);

        if (loadedFile) {
            Log.i("A", "The file to be sent was loaded");
            saveButton.setVisibility(View.INVISIBLE);
            saveOnSendDescription.setVisibility(View.INVISIBLE);
            saveButton.setChecked(false);
        }

        sendButton = (Button) findViewById(R.id.SendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {


            /**
             * This method is called when the send button is pushed
             *
             * @param v ignore this, android handles this variable automatically
             */
            public void onClick(View v) {
                sendingBar.setVisibility(View.VISIBLE);
                sendButton.setVisibility(View.INVISIBLE);
                connectionInfo.setText("Sending Message");
                sendMessage();
            }

        });

    }

    void sendMessage() {
        if (!sending) {
            sending = true;
            new Thread(
                    new Runnable() {
                        public void run() {
                            //when the send button is pushed

                            connect();

                            try {
                                Thread.sleep(250);
                            } catch (Exception e) {
                                Log.i("A", "Error Waiting");
                            }

                            if (connected) {
                                try {
                                    if (!sendSavedButton.isChecked()) {
                                        write("" + message + "\n");
                                    }

                                    if (sendSavedButton.isChecked()) {

                                        //if the send saved option is selected

                                        StringBuilder toSend = new StringBuilder();
                                        File[] existingFiles = new File(getApplicationInfo().dataDir + "/Logs").listFiles(); //List of saved scouting logs
                                        for (File f : existingFiles) {

                                            BufferedReader reader = new BufferedReader(new FileReader(f));

                                            toSend.append(reader.readLine()).append("\n");

                                            reader.close();
                                        }

                                        write(toSend.toString());
                                    }

                                    if (saveButton.isChecked()) {

                                        //if the save on send option is selected, get storage permission
                                        requestStoragePermission();
                                    }

                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            connectionInfo.setText("Sending Message");
                                        }
                                    });

                                    write("end"); //causes the computer to stop listening for messages and send its own end message

                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            connectionInfo.setText("Message Sent");
                                        }
                                    });

                                    try {
                                        Thread.sleep(500);
                                    } catch (Exception e) {
                                        Log.i("A", "Error Waiting");
                                    }
                                    boolean received = waitForEnd(); //waits until it times out or receives the end message

                                    //anonymously extend instead of using
                                    //lambda here to maintain compatibility with older versions of android
                                    Runnable showCancelAlert = new Runnable() {
                                        public void run() {
                                            connectionInfo.setText("Error Sending Message");
                                            AlertDialog.Builder builder = new AlertDialog.Builder(SendMessageActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                                            builder.setMessage("The Message Failed To Send. Please Try Again.").setTitle("Not Sent").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            }).setCancelable(false).setIcon(R.mipmap.alpha_dogs_logo);
                                            builder.create();
                                            builder.show();
                                        }
                                    };

                                    if (!received) {
                                        //Shows error sending alert if does not receive acknowledgement from server
                                        Log.i("A", "Error Sending Message");
                                        runOnUiThread(showCancelAlert);
                                    }

                                    socket.close();
                                } catch (IOException e) {
                                    //Shows error alert because of IOException
                                    Log.i("A", "Error Sending Message");
                                    runOnUiThread(showCancelAlert);
                                    e.printStackTrace();
                                }
                                connected = false;
                            }
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    sendingBar.setVisibility(View.INVISIBLE);
                                    sendButton.setVisibility(View.VISIBLE);
                                }
                            });
                            sending = false;
                        }
                    }).start();
        }
    }

    public void connect() {
        //When the connect button is pushed
        if (!connected) {
            computerAddress = connectionMAC.getText().toString();
            runOnUiThread(new Runnable() {
                public void run() {
                    connectionInfo.setText("Connecting...");
                }
            });
            try {
                init();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connected) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        connectionInfo.setText("Connected");
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        connectionInfo.setText("Error: Connection Failed. Do You Have The Right MAC Address?");
                        AlertDialog.Builder builder = new AlertDialog.Builder(SendMessageActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        builder.setMessage("The Message Failed To Send. Please Try Again.").setTitle("Not Sent").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setCancelable(false).setIcon(R.mipmap.alpha_dogs_logo);
                        builder.create();
                        builder.show();
                    }
                });
            }
        }
    }

    /**
     *  @return true if end is recieved in the time limit, false otherwise
     *  Waits until it recieves end or times out
     */
    public boolean waitForEnd() {

        boolean result = false;

        Log.i("A", "Waiting for end");
        try {
            byte[] bytes = new byte[3];
            Log.i("A", "Checking For End");
            int bytesRead = inStream.read(bytes, 0, bytes.length);
            String recieved = new String(bytes, "UTF-8");

            if (recieved.equals("end")) { //check if the message is end
                result = true;
                Log.i("A", "Received End");
            }
        } catch (Exception e) {
            Log.i("A", "Error While Waiting For End, Aborting");
            for (StackTraceElement s : e.getStackTrace()) {
                Log.i("A", s.toString());
            }
        }

        return result;

    }

    /**Creates the connection between the client and server apps, requires that the two devices are paired
     *
     * @throws IOException never gonna happen hopefully
     */
    private void init() throws IOException {

        int REQUEST_ENABLE_BT = 1;

        if (mBluetoothAdapter == null) {
            /* Device doesn't support Bluetooth
            app currently crashes if this is true, put error handling code in here


             */
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); //Enables bluetooth, gets permissions
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceHardwareAddress = device.getAddress(); // MAC addresses of paired devices

                if (device.getAddress().toString().equals(computerAddress)) {
                    hostComputer = device;
                }


            }

        }

        try {
            if (!(hostComputer == null)) {
                socket = hostComputer.createRfcommSocketToServiceRecord(ConnectToUUID);
                socket.connect();
                connected = true;
                outputStream = socket.getOutputStream();
                inStream = socket.getInputStream();
                write(mBluetoothAdapter.getName().toString());
            } else {
                connected = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**Writes a string to the output stream between this and the paired device using the bluetooth connection
     *
     * @param s String to write to output stream
     * @throws IOException
     */
    public void write(String s) throws IOException {
        outputStream.write(s.getBytes());
    }

    /**Requests permission to access external storage, which is actually internal device storage 
     * (external as in outside of the scope of the sandbox the application runs in).
     * calls onRequestPermissionsResult after, don't worry about arguments
     */
    private void requestStoragePermission() {
        Log.i("A", "Write permission has NOT been granted. Requesting permission.");

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //I don't actually want this to happen right now but I might later
            Log.i("A",
                    "Displaying write permission rationale to provide additional context.");

        } else {

            // Get storage permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MainActivity.WRITE_LOG);
        }

    }

    /**Called after any permissions are requested
     * Any other methods don't have access to external/internal storage so don't try IO anywhere else
     *
     * @param requestCode All these arguments are handled by android
     * @param permissions All these arguments are handled by android
     * @param grantResults All these arguments are handled by android
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == MainActivity.WRITE_LOG) {

            Log.i("A", "Received response for write permission request.");

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //When write permission is granted

                //Save file

                String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                File home = new File(getApplicationInfo().dataDir + "/Logs"); //application directory, so: /data/data/com.whatever.otherstuff/Logs
                home.mkdirs();
                File csv = null;

                boolean cont = true;
                int i = 0;

                while (cont) {
                    csv = new File(home, android_id + "-" + i + ".csv");

                    if (!(csv.exists())) {
                        cont = false;
                    }

                    i += 1;

                }

                Log.i("A", "File created at " + csv.getAbsolutePath() + "");

                try {


                    BufferedWriter writer = new BufferedWriter(new FileWriter(csv));
                    writer.write(message);
                    writer.close();

                    Log.i("A", "Finished writing to " + csv.getAbsolutePath() + "");

                    //IO crap for testing
                    //BufferedReader reader = new BufferedReader(new FileReader(csv));
                } catch (IOException e) {
                    //IO didn't work, honestly this shouldn't happen, if it does its probably a device or permission error
                    Log.i("A", "File write to " + csv.getAbsolutePath() + " failed");
                    e.printStackTrace();
                }



            } else {
                Log.i("A", "Write permission was NOT granted.");

                //If the user denies permission, do this stuff

            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }
}
