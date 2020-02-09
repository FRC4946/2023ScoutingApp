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

/**WARNING TO WHOEVER IS READING THIS CODE, THIS WAS A LEARNING EXPERIENCE FOR ME
 * IF I WAS GOING TO DO IT AGAIN I WOULD DO IT VERY DIFFERENTLY AND MUCH BETTER
 * I DON'T HAVE TIME TO REDO EVERYTHING SO MOST OF THIS WILL PROBABLY NEVER BE FIXED
 * MOST OF THIS IS REALLY DUMB SPAGHETTI CODE AND I'M SORRY YOU NEED TO READ THROUGH IT
 */
public class MainActivity extends AppCompatActivity {

    //TODO : Add overwrite feature
    //TODO : Add enhanced IO
    //TODO : Add IO class
    //TODO : make not spaghetti code

    boolean loadedFile = false;

    String loadName;

    ArrayList<String> strings = new ArrayList<String>(); //List of strings from fields

    ArrayList<View> texts = new ArrayList<View>();

    static final int WRITE_LOG = 1; //Can be any value as long as it is different from other permission request codes
    static final int MAKE_SETTINGS_DIRECTORY = 2;

    TextView activityMessage;
    Button saveButton;

    /** Launches when activity starts
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        loadName = intent.getStringExtra(intent.EXTRA_TEXT); //Gets message from previous activity, stores in message string

        //Load Settings
        readSettings();

        //LAyout Stuff

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
                texts.add(linearLayout.getChildAt(i));
            }

        }

        ToggleButton defenseButton = (ToggleButton) findViewById(R.id.PlayedDefense);
        defenseButton.setChecked(false);

        if (!(loadName == null)) {
            String[] fields = null;
            Log.i("A", "File recieved, loading" + loadName);
            loadedFile = true;
            File loadFile = new File(loadName);

            activityMessage = (TextView) findViewById(R.id.ActivityMessage);
            saveButton = (Button) findViewById(R.id.SaveButton);

            activityMessage.setText("Editing " + loadFile.getPath());
            saveButton.setText("Overwrite");

            try {
                BufferedReader reader = new BufferedReader(new FileReader(new File(loadName))); //Creates a file reader on the file passed to this activity by the load activity

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

                    Log.i("A", "Setting " + texts.get(i).getId() + " to " + fields[i]);
                    if (texts.get(i).getTag().toString().equals("text")) {
                        ((EditText) texts.get(i)).setText(fields[i]);
                    } else if (texts.get(i).getTag().toString().equals("bool")) {
                        ((ToggleButton) texts.get(i)).setChecked(!fields[i].equals("false"));
                    } else if (texts.get(i).getTag().toString().equals("radio")) {
                        for (int j = 0; j < ((RadioGroup) texts.get(i)).getChildCount(); j++) {
                            if (((TextView)((RadioGroup) texts.get(i)).getChildAt(j)).getText().toString().equals(fields[i])) {
                                ((RadioButton)((RadioGroup) texts.get(i)).getChildAt(j)).setChecked(true);
                            }
                        }
                    } else if (texts.get(i).getTag().toString().equals("addable")) {
                        ((EditText) ((LinearLayout) texts.get(i)).getChildAt(0)).setText(fields[i]);
                    }

                }

            } else {

                //if the file is blank

                clear();

            }




        }

        //Other layout features
        activityMessage = (TextView) findViewById(R.id.ActivityMessage);

        saveButton = (Button) findViewById(R.id.SaveButton);
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

        saveButton.setOnClickListener(new View.OnClickListener() {

            /**This method is called when the save button is pushed
             *
             * @param v Android handles this view automatically
             */
            public void onClick(View v) {

                //TODO : Add archival system

                requestStoragePermission(WRITE_LOG);

                //Some irrelevant IO crap
                /*

                try {
                    FileWriter writer = new FileWriter(csv);
                    FileReader reader = new FileReader(csv);

                    writer.write("hi");
                    writer.close();

                    teamName.setText(reader.read());
                    reader.close();

                } catch (IOException e) {
                    e.printStackTrace();
               }
            */

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
        if (loadedFile) {
            intent.putExtra("LoadedFile", loadName);
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
        if (!settingsDirectory.exists()) { //If settings doesn't exist
            //settings.mkdirs();
            requestStoragePermission(MAKE_SETTINGS_DIRECTORY);
        } else { //If settings does exist
            File settings = new File(settingsDirectory.getAbsolutePath() + "settings.ini");



        }
    }

    /**Requests permission to access external storage, which is actually internal storage because android is retarded
     * Calls onRequestPermissionsResult after, don't worry about arguments
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
                if (!(loadName == null)) {
                    csv = new File(loadName);
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

                    loadName = csv.getAbsolutePath();
                    loadedFile = true;
                    activityMessage.setText("Editing " + new File(loadName).getPath());
                    saveButton.setText("Overwrite");

                    Log.i("A", "File created at " + csv.getAbsolutePath() + "");

                }

                try {


                    BufferedWriter writer = new BufferedWriter(new FileWriter(csv));
                    writer.write(updateStrings());
                    writer.close();

                    Log.i("A", "Finished writing to " + csv.getAbsolutePath() + "");
                    activityMessage.setText("CSV File Saved");

                    //IO crap for testing
                    //BufferedReader reader = new BufferedReader(new FileReader(csv));
                    //reader.close();
                } catch (IOException e) {
                    //IO didn't work, honestly this shouldn't happen, if it does its probably a device or permission error
                    Log.i("A", "File write to " + csv.getAbsolutePath() + " failed");
                    activityMessage.setText("CSV File Save Failed");
                    e.printStackTrace();
                }

            } else {
                Log.i("A", "Write permission was NOT granted.");

                activityMessage.setText("CSV File Save Failed, Permission Error");

                //If the user is AcOuStIc and denies permission, do this stuff ...oOo...

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

        strings.clear();
        for (View e : texts) {
            if (e.getTag().toString().equals("text")) {
                strings.add(((EditText)e).getText().toString().replace(' ', '_').replace(',', '.'));
            } else if (e.getTag().toString().equals("bool")) {
                strings.add(Boolean.toString(((ToggleButton) e).isChecked()));
            } else if (e.getTag().toString().equals("radio")) {
                strings.add(((RadioButton) findViewById(((RadioGroup) e).getCheckedRadioButtonId())).getText().toString());
            } else if (e.getTag().toString().equals("addable")) {
                strings.add(((EditText) ((LinearLayout) e).getChildAt(0)).getText().toString());
            }
        }

        StringBuilder builder = new StringBuilder();

        for (String s : strings) {
            builder.append(s + ",");
        }

        return builder.toString();

    }

    public void clear() {

        //Clears fields
        for (View e : texts) {
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

        loadedFile = false;

        loadName = null;

        activityMessage = (TextView) findViewById(R.id.ActivityMessage);
        saveButton = (Button) findViewById(R.id.SaveButton);

        activityMessage.setText("Editing New Scouting Log");
        saveButton.setText("Save");

    }

}


