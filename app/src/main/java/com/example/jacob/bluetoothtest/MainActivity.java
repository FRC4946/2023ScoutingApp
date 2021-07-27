package com.example.jacob.bluetoothtest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.preference.EditTextPreference;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Selection;
import android.util.Log;import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.util.ArrayList;

import android.provider.Settings.Secure;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    //TODO : Add overwrite feature
    //TODO : Add enhanced IO
    //TODO : Add IO class
    //TODO : make not spaghetti code

    private boolean m_loadedFile = false;

    private String m_loadName;

    private ArrayList<String> m_strings = new ArrayList<String>(); //List of strings from fields

    private ArrayList<View> m_texts = new ArrayList<View>();

    public static final int WRITE_LOG = 1; //Can be any value as long as it is different from other permission request codes
    public static final int MAKE_SETTINGS_DIRECTORY = 2;

    private TextView m_activityMessage;
    private Button m_saveButton;

    /** Launches when activity starts
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        m_loadName = intent.getStringExtra(intent.EXTRA_TEXT); //Gets message from previous activity, stores in message string

        //Load Settings
        readSettings();

        //Layout Stuff

        //List fields stuff
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.TextFields); //gets linear layout containing editTexts
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            if (linearLayout.getChildAt(i).getTag().toString().equals("addable")) {
                ((Button) ((LinearLayout) linearLayout.getChildAt(i)).getChildAt(1)).setOnClickListener(new View.OnClickListener() {

                    /**This method is called when the load button is pushed
                     *
                     * @param v Android handles this view automatically
                     */
                    public void onClick(View v) {
                        EditText modifyTarget = ((EditText) ((LinearLayout) v.getParent()).getChildAt(0));
                        modifyTarget.requestFocus();
                        if (modifyTarget.getText().toString().matches("-?\\d+(\\.\\d+)?")) {
                            modifyTarget.setText(Integer.toString(Integer.parseInt(modifyTarget.getText().toString()) + 1));
                        } else {
                            modifyTarget.setText("1");
                        }

                    }


                });
            }
            if (!linearLayout.getChildAt(i).getTag().toString().equals("title")) {
                m_texts.add(linearLayout.getChildAt(i));
            }

        }

        ToggleButton defenseButton = (ToggleButton) findViewById(R.id.PlayedDefense);
        defenseButton.setChecked(false);

        if (!(m_loadName == null)) {
            String[] fields = null;
            Log.i("A", "File recieved, loading" + m_loadName);
            m_loadedFile = true;
            File loadFile = new File(m_loadName);

            m_activityMessage = (TextView) findViewById(R.id.m_ActivityMessage);
            m_saveButton = (Button) findViewById(R.id.m_SaveButton);

            m_activityMessage.setText("Editing " + loadFile.getPath());
            m_saveButton.setText("Overwrite");

            try {
                BufferedReader reader = new BufferedReader(new FileReader(new File(m_loadName))); //Creates a file reader on the file passed to this activity by the load activity

                String content = reader.readLine(); //Gets the content of the file
                fields = content.split(","); //Splits content into fields
                Log.i("A", fields.length + " fields found:");
                for (String s : fields) {
                    Log.i("A", s);
                }

                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            updateStrings();

            //Sets text fields
            if (fields.length > 0) {

                //If the file isn't blank

                for (int i = 0; i < fields.length; i++) {

                    Log.i("A", "Setting " + m_texts.get(i).getId() + " to " + fields[i]);
                    if (m_texts.get(i).getTag().toString().equals("text")) {
                        ((EditText) m_texts.get(i)).setText(fields[i]);
                    } else if (m_texts.get(i).getTag().toString().equals("bool")) {
                        ((ToggleButton) m_texts.get(i)).setChecked(!fields[i].equals("false"));
                    } else if (m_texts.get(i).getTag().toString().equals("radio")) {
                        for (int j = 0; j < ((RadioGroup) m_texts.get(i)).getChildCount(); j++) {
                            if (((TextView)((RadioGroup) m_texts.get(i)).getChildAt(j)).getText().toString().equals(fields[i])) {
                                ((RadioButton)((RadioGroup) m_texts.get(i)).getChildAt(j)).setChecked(true);
                            }
                        }
                    } else if (m_texts.get(i).getTag().toString().equals("addable")) {
                        ((EditText) ((LinearLayout) m_texts.get(i)).getChildAt(0)).setText(fields[i]);
                    }

                }

            } else {

                //if the file is blank

                clear();

            }




        }

        //Other layout features
        m_activityMessage = (TextView) findViewById(R.id.m_ActivityMessage);

        m_saveButton = (Button) findViewById(R.id.m_SaveButton);
        Button sendButton = (Button) findViewById(R.id.SendButton);
        Button loadButton = (Button) findViewById(R.id.LoadButton);
        Button clearButton = (Button) findViewById(R.id.ClearButton);

        clearButton.setOnClickListener(new View.OnClickListener() {

            /**This method is called when the clear button is pushed
             *
             * @param v Android handles this view automatically
             */
            public void onClick(View v) {

                clear();

            }


        });

        loadButton.setOnClickListener(new View.OnClickListener() {

            /**This method is called when the load button is pushed
             *
             * @param v Android handles this view automatically
             */
            public void onClick(View v) {
                loadFiles();
            }


        });

        m_saveButton.setOnClickListener(new View.OnClickListener() {

            /**This method is called when the save button is pushed
             *
             * @param v Android handles this view automatically
             */
            public void onClick(View v) {

                //TODO : Add archival system

                requestStoragePermission(WRITE_LOG);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {

            /**This method is called when the send button is pressed
             *
             * @param v Android handles this variable
             */
            public void onClick(View v) {

                sendMessage();
            }
        });


    }

    /** Called when the user taps the Send button, moves to the connect and send activity */
    public void sendMessage() {
        Intent intent = new Intent(this, SendMessageActivity.class);
        String message = updateStrings();
        intent.putExtra("MessageToSend", message);
        if (m_loadedFile) {
            intent.putExtra("LoadedFile", m_loadName);
        }
        startActivity(intent);
    }

    /**Called when the user presses the load button
     *
     */
    public void loadFiles() {
        Intent intent = new Intent(this, LoadActivity.class);
        startActivity(intent);
    }

    public void readSettings() {
        File settingsDirectory = new File(getApplicationInfo().dataDir + "/Settings");
        if (!settingsDirectory.exists()) { //If settings doesn't exist, create settings directory
            requestStoragePermission(MAKE_SETTINGS_DIRECTORY);
        } else { //If settings does exist
            File settings = new File(settingsDirectory.getAbsolutePath() + "settings.ini");
        }
    }

    /**Requests permission to access external storage, which is actually internal device storage 
     * (external as in outside of the scope of the sandbox the application runs in).
     * calls onRequestPermissionsResult after, don't worry about arguments
     */
    private void requestStoragePermission(int request) {
        Log.i("A", "Write permission has NOT been granted. Requesting permission.");

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //I don't actually want this to happen right now but I might later
            Log.i("A",
                    "Displaying write permission rationale to provide additional context.");

        } else {

            // Get storage permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    request);
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

        if (requestCode == WRITE_LOG) {

            Log.i("A", "Received response for write permission request.");

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //When write permission is granted

                Log.i("A", "Write permission has now been granted.");

                String android_id = Secure.getString(getApplicationContext().getContentResolver(),
                        Secure.ANDROID_ID);

                File home = new File(getApplicationInfo().dataDir + "/Logs"); //application directory, so: /data/data/com.whatever.otherstuff/Logs
                home.mkdirs();
                File csv;
                if (!(m_loadName == null)) {
                    csv = new File(m_loadName);
                } else {
                    csv = null;

                    boolean cont = true;
                    int i = 0;

                    while (cont) {
                        csv = new File(home, android_id + "-" + i + ".csv");

                        if (!(csv.exists())) {
                            cont = false;
                        }

                        i += 1;

                    }

                    m_loadName = csv.getAbsolutePath();
                    m_loadedFile = true;
                    m_activityMessage.setText("Editing " + new File(m_loadName).getPath());
                    m_saveButton.setText("Overwrite");

                    Log.i("A", "File created at " + csv.getAbsolutePath() + "");

                }

                try {


                    BufferedWriter writer = new BufferedWriter(new FileWriter(csv));
                    writer.write(updateStrings());
                    writer.close();

                    Log.i("A", "Finished writing to " + csv.getAbsolutePath() + "");
                    m_activityMessage.setText("CSV File Saved");

                } catch (IOException e) {
                    //IO didn't work, honestly this shouldn't happen, if it does its probably a device or permission error
                    Log.i("A", "File write to " + csv.getAbsolutePath() + " failed");
                    m_activityMessage.setText("CSV File Save Failed");
                    e.printStackTrace();
                }

            } else {
                Log.i("A", "Write permission was NOT granted.");

                m_activityMessage.setText("CSV File Save Failed, Permission Error");

                //If the user denies permission, do this stuff

            }

        } else if (requestCode == MAKE_SETTINGS_DIRECTORY) {

            try {
                Log.i("A", "Attempting To Create Settings Directory");
                if (new File(getApplicationInfo().dataDir + "/Settings").mkdirs()) {
                    Log.i("A", "Created Settings Directory");
                } else {
                    Log.i("A", "Failed To Create Settings Directory");
                    Log.i("A", "Write permission was NOT granted.");
                }
            } catch (Exception e) {
                Log.i("A", "Failed To Create Settings Directory");
                Log.i("A", "Write permission was NOT granted.");
                e.printStackTrace();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }


    public String updateStrings() {

        m_strings.clear();
        for (View e : m_texts) {
            if (e.getTag().toString().equals("text")) {
                m_strings.add(((EditText)e).getText().toString().replace(' ', '_').replace(',', '.'));
            } else if (e.getTag().toString().equals("bool")) {
                m_strings.add(Boolean.toString(((ToggleButton) e).isChecked()));
            } else if (e.getTag().toString().equals("radio")) {
                m_strings.add(((RadioButton) findViewById(((RadioGroup) e).getCheckedRadioButtonId())).getText().toString());
            } else if (e.getTag().toString().equals("addable")) {
                m_strings.add(((EditText) ((LinearLayout) e).getChildAt(0)).getText().toString());
            }
        }

        StringBuilder builder = new StringBuilder();

        for (String s : m_strings) {
            builder.append(s + ",");
        }

        return builder.toString();

    }

    public void clear() {

        //Clears fields
        for (View e : m_texts) {
            if (e.getTag().toString().equals("text")) {
                ((EditText) e).setText("");
            } else if (e.getTag().toString().equals("bool")) {
                ((ToggleButton) e).setChecked(true);
            } else if (e.getTag().toString().equals("radio")) {
                ((RadioGroup) e).check(((RadioGroup) e).getChildAt(1).getId());
            } else if (e.getTag().toString().equals("addable")) {
                ((EditText) ((LinearLayout) e).getChildAt(0)).setText("");
            }
        }

        ToggleButton defenseButton = (ToggleButton) findViewById(R.id.PlayedDefense);
        defenseButton.setChecked(false);

        updateStrings();

        m_loadedFile = false;

        m_loadName = null;

        m_activityMessage = (TextView) findViewById(R.id.m_ActivityMessage);
        m_saveButton = (Button) findViewById(R.id.m_SaveButton);

        m_activityMessage.setText("Editing New Scouting Log");
        m_saveButton.setText("Save");

    }

}
