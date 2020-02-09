package com.example.jacob.bluetoothtest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LoadActivity extends AppCompatActivity {

    //TODO : add image to file icon radiobutton thing


    //Stuff for updating selected files
    static int selected; //the selected radiobutton
    static File[] existingFiles;
    RadioGroup filesGroup;
    ArrayList<RadioButton> fileButtons;

    /** Run when the activity is launched
     *
     * @param savedInstanceState IDK android handles it
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        updateFiles();

        //Layout stuff

        Button loadButton = (Button) findViewById(R.id.LoadButton);
        Button deleteButton = (Button) findViewById(R.id.DeleteButton);

        loadButton.setOnClickListener(new View.OnClickListener() {

            /**This method is called when the save button is pushed
             *
             * @param v Android handles this view automatically
             */
            public void onClick(View v) {

                //Launches main activity with file loaded
                Log.i("A", "Commencing load");

                if (existingFiles.length > 0) {

                    for (int i = 0; i < ((RadioGroup) findViewById(R.id.Files)).getChildCount(); i++) {
                        if (((RadioButton) ((RadioGroup) findViewById(R.id.Files)).getChildAt(i)).isChecked()) {
                            selected = i;
                            Log.i("A", selected + " is selected: " + existingFiles[selected]);
                        }
                    }

                    loadFile(existingFiles[selected]);


                } else {

                    //If there are no files
                    Log.i("A", "No files found, returning to main activity");

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                }

            }


        });

        deleteButton.setOnClickListener(new View.OnClickListener() {

            /**This method is called when the delete button is pressed
             *
             * @param view Android handles this automatically
             */
            public void onClick(View view) {

                //Deletes selected file

                Log.i("A", "Commencing delete");

                for (int i = 0; i < ((RadioGroup) findViewById(R.id.Files)).getChildCount(); i++) {
                    if (((RadioButton) ((RadioGroup) findViewById(R.id.Files)).getChildAt(i)).isChecked()) {
                        selected = i;
                        Log.i("A", selected + " is selected: " + existingFiles[selected]);
                    }
                }

                if (existingFiles.length > 0) {
                    Log.i("A", "Requesting permissions");
                    requestStoragePermission();
                } else {
                    Log.i("A", "No files found");
                }

            }
        });

    }

    /**Takes a scouting log and returns the match number present in the csv file
     *
     * @param file CSV file to read from
     * @return Name of team in scouting log
     */
    public static String getMatchNumber(File file) {

        String content = null;
        BufferedReader reader = null; //To avoid compiler errors

        try { //Creates a buffered reader, reads File file, result is saved in content
            reader = new BufferedReader(new FileReader(file));
            content = reader.readLine();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] splitString = content.split(","); //Splits file into different field (assumes file is an alpha dogs formatted CSV)

        if (splitString.length > 1) {
            return splitString[1]; //returns team name
        } else {
            return "<invalid>";
        }


    }

    /**Takes a scouting log and returns the team name present in the csv file
     *
     * @param file CSV file to read from
     * @return Name of team in scouting log
     */
    public static String getTeamNumber(File file) {

        String content = null;
        BufferedReader reader = null; //To avoid compiler errors

        try { //Creates a buffered reader, reads File file, result is saved in content
            reader = new BufferedReader(new FileReader(file));
            content = reader.readLine();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] splitString = content.split(","); //Splits file into different field (assumes file is an alpha dogs formatted CSV)

        if (splitString.length > 0) {
            return splitString[0]; //returns team name
        } else {
            return "<invalid>";
        }


    }

    /**Starts the main activity while sending the main activity the filename of the file to load
     *
     * @param file the file to load
     */
    public void loadFile(File file) {

        Intent intent = new Intent(this, MainActivity.class);
        String fileName = file.getAbsolutePath();
        intent.putExtra(Intent.EXTRA_TEXT, fileName);
        startActivity(intent);

    }

    /**Requests permission to access external storage, which is actually internal storage because android is retarded
     * Calls onRequestPermissionsResult after, don't worry about arguments
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


                //Deletes file
                if(existingFiles[selected].delete()) {
                    Log.i("A", "File deleted");
                } else {
                    Log.i("A", "Delete failed");
                }
                updateFiles();


            } else {
                Log.i("A", "Write permission was NOT granted.");


                //If the user is AcOuStIc and denies permission, do this stuff ...oOo...

            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    /**Updates and redraws the list of files
     *
     */
    public void updateFiles() {
        //Scroll View Stuff

        filesGroup = (RadioGroup) findViewById(R.id.Files); //Radiogroup containing all files to be displayed in scroll view
        fileButtons = new ArrayList<RadioButton>(); //Arraylist containing all Radiobuttons to be added to filesGroup
        existingFiles = new File(getApplicationInfo().dataDir + "/Logs").listFiles(); //List of saved scouting logs

        filesGroup.removeAllViews();

        if (!(existingFiles == null) && (existingFiles.length > 0) && new File(getApplicationInfo().dataDir + "/Logs").exists()) { //Makes sure there are no errors
            for (int i = 0; i < existingFiles.length; i++) {

                RadioButton file = new RadioButton(this); //Creates a new radiobutton

                file.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT)); //Sets layout parameters for radiobutton

                file.setText((i+1) + " - Match " + getMatchNumber(existingFiles[i]).replace('_', ' ') + ", Team " + getTeamNumber(existingFiles[i]).replace('_', ' ')); //Sets text for radiobutton

                fileButtons.add(file); //adds file to fileButtons arraylist

                filesGroup.addView(fileButtons.get(i)); //adds arraylist content to radiogroup
            }

            if (filesGroup.getChildCount() > 0) {
                ((RadioButton) filesGroup.getChildAt(0)).setChecked(true);
            }

        }

    }


}
