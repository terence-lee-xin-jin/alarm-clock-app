package lee.terence.alarmclockapp.controllers;



import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Calendar;
import java.util.Locale;

import lee.terence.alarmclockapp.helpers.AlarmClockHelper;
import lee.terence.alarmclockapp.helpers.AlarmClockNotificationHelper;
import lee.terence.alarmclockapp.helpers.AlarmClockSharedPreferencesHelper;
import lee.terence.alarmclockapp.R;
import lee.terence.alarmclockapp.mediaplayers.AlarmSoundMediaPlayer;



/**
 * The main activity of the app
 *
 * Displays an existing set alarm if there is any
 *
 * Allows user to set a new alarm/ overwrite an existing alarm
 *
 * Allows user to delete an existing alarm
 *
 * @author Terence Lee
 * */
public class MainActivity extends AppCompatActivity {

    private static final int SCHEDULE_EXACT_ALARM_PERMISSION_REQUEST_CODE = 0;
    private static final int POST_NOTIFICATIONS_PERMISSION_REQUEST_CODE = 1;


    /**
     * Initialize all the views of the activity
     *
     * Also delete and stop a lapsed old alarm if there is any
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeOrUpdateAllViews();

        deleteAndStopAlarmIfAlarmHasAlreadyLapsed();


    }


    /**
     * Have the app check for various necessary permissions and request these
     * permissions from the user as the app UI become visible
     * */
    @Override
    protected void onStart() {
        super.onStart();
        checkAndRequestForVariousPermissions();
    }

    /**
     * Upon the resumption of this activity (sometimes after the dismissal of the alarm that has
     * went off, update the views of the activity to reflect that there is no more alarm
     *
     * Also delete and stop a lapsed alarm if the app happened to be paused by the user, and
     * resumed after the alarm has lapsed
     * */
    @Override
    protected void onResume() {
        super.onResume();

        initializeOrUpdateAllViews();

        deleteAndStopAlarmIfAlarmHasAlreadyLapsed();

        checkAndRequestForVariousPermissions();
    }



    /**
     * Initialize or update all the views in the activity
     * */
    private void initializeOrUpdateAllViews(){

        initializeOrUpdateSetOrEditAlarmButton();
        initializeOrUpdateExistingAlarmTextView();
        initializeOrUpdateDeleteAlarmButton();


    }


    /**
     * Initialize the setOrEditAlarmButton to allow user to set/edit an alarm
     *
     * If there is no existing alarm, then display the button text as "Set Alarm"
     *
     * If there is an existing alarm, then display the button text as "Edit Alarm"
     * */
    private void initializeOrUpdateSetOrEditAlarmButton(){

        Button setOrEditAlarmButton = findViewById(R.id.set_or_edit_alarm_button);

        setOrEditAlarmButton.setOnClickListener(view -> {
            Intent intent = SetAlarmClockActivity.createIntent(getApplicationContext());

            startActivity(intent);

        });

        if (!AlarmClockSharedPreferencesHelper.hasNextAlarmTime(getApplicationContext())){
            setOrEditAlarmButton.setText(R.string.activity_main_set_alarm_text);
        }
        else{
            setOrEditAlarmButton.setText(R.string.activity_main_edit_alarm_text);
        }
    }



    /**
     * Initialize the delete alarm button to delete the alarm
     *
     * If there is no existing alarm to delete, hide this button
     * */
    private void initializeOrUpdateDeleteAlarmButton(){

        Button deleteAlarmButton = findViewById(R.id.delete_alarm_button);

        deleteAlarmButton.setOnClickListener(view -> deleteAlarm());


        if (AlarmClockSharedPreferencesHelper.getNextAlarmTime(getApplicationContext()) == -1){
            deleteAlarmButton.setVisibility(View.INVISIBLE);
        }
        else{
            deleteAlarmButton.setVisibility(View.VISIBLE);
        }

    }


    /**
     * Initialize the textview that displays the existing alarm, if any
     *
     * If there is no existing alarm, will display a text stating "No alarm has been set"
     *
     * If there is an existing alarm, will display a text stating the existing alarm time
     * */
    private void initializeOrUpdateExistingAlarmTextView(){

        TextView existingAlarmTextView = findViewById(R.id.existing_alarm_text_view);

        //if there is no existing alarm
        if (!AlarmClockSharedPreferencesHelper.hasNextAlarmTime(getApplicationContext())){

            existingAlarmTextView.setText(R.string.activity_main_no_alarm_has_been_set_text);
        }
        else{
            //there is an existing alarm

            long nextAlarmTime =
                    AlarmClockSharedPreferencesHelper.getNextAlarmTime(getApplicationContext());

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(nextAlarmTime);

            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            int timeOfDay = calendar.get(Calendar.AM_PM);

            String timeOfDayString;

            if (hour == 0){
                hour = 12;
            }

            if (timeOfDay == Calendar.AM){
                timeOfDayString = "a.m.";
            }
            else{
                timeOfDayString = "p.m.";
            }

            String timeToDisplay = String.format(Locale.ENGLISH,
                    "%02d:%02d %s", hour, minute, timeOfDayString);

            existingAlarmTextView.setText(timeToDisplay);
        }
    }


    /**
     * Delete alarm
     *
     * Does the following things:
     *  (a) Delete the alarm from the alarm manager
     *  (b) Delete the stored alarm time from the app's shared preferences storage
     *  (c) Update the existing alarm textview to state the there is no existing alarm
     *  (d) Hide the delete alarm button
     *  (e) Update the text of the setOrUpdateAlarm button text from "Update Alarm" to "Set Alarm"
     *
     * */
    private void deleteAlarm(){

        AlarmClockHelper.deleteExistingAlarmIfAny(getApplicationContext());

        AlarmClockSharedPreferencesHelper.deleteNextAlarmTimeIfAny(getApplicationContext());

        initializeOrUpdateAllViews();
    }



    /**
     * Check if there is an old alarm which has already lapsed but not been fired yet
     *
     * If there is such an old alarm, do the following things:
     *      (a) Delete the old alarm through the alarm manager
     *      (b) Delete the old stored alarm time from the app's shared preferences storage
     *      (c) Stop the alarm ring sound from playing (if it is playing)
     *      (d) Update all the views in this activity to reflect the deleted old alarm
     * */
    private void deleteAndStopAlarmIfAlarmHasAlreadyLapsed(){

        if (AlarmClockSharedPreferencesHelper.hasNextAlarmTime(getApplicationContext())){

            long alarmTimeMillisecondsSinceEpoch =
                    AlarmClockSharedPreferencesHelper.getNextAlarmTime(getApplicationContext());

            Calendar alarmTimeCalendar = Calendar.getInstance();
            alarmTimeCalendar.setTimeInMillis(alarmTimeMillisecondsSinceEpoch);

            Calendar currentTimeCalendar = Calendar.getInstance();

            if (alarmTimeCalendar.before(currentTimeCalendar)){

                AlarmClockHelper.deleteExistingAlarmIfAny(getApplicationContext());
                AlarmClockSharedPreferencesHelper.deleteNextAlarmTimeIfAny(getApplicationContext());
                AlarmSoundMediaPlayer.stopAlarmRingingSoundIfPlaying();

                AlarmClockNotificationHelper.deleteAllAlarmNotifications(getApplicationContext());

                initializeOrUpdateAllViews();

            }
        }
    }


    /**
     * Check whether the app has access to the various permissions, and request for
     * the various permissions if necessary
     * */
    private void checkAndRequestForVariousPermissions(){

        if (!hasScheduleExactAlarmPermission()){
            requestForScheduleExactAlarmPermission();
        }

        if (!hasPostNotificationsPermission()){
            requestForPostNotificationsPermission();
        }
    }


    /**
     * Checks if the device has permissions to schedule exact alarm
     *
     * @return true if it has permission to schedule exact alarm, and false if
     * otherwise
     * */
    private boolean hasScheduleExactAlarmPermission()
    {
        return (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Checks if the device has permissions to post notifications
     *
     * @return true if it has permission to schedule exact alarm, and false if
     * otherwise
     * */
    private boolean hasPostNotificationsPermission()
    {
        return (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED);
    }


    /**
     * Request the app to have the permission to schedule exact alarm
     * */
    private void requestForScheduleExactAlarmPermission()
    {
        String [] permissions = new String[]{
                Manifest.permission.SCHEDULE_EXACT_ALARM
        };


        ActivityCompat.requestPermissions(this, permissions,
                SCHEDULE_EXACT_ALARM_PERMISSION_REQUEST_CODE);
    }


    /**
     * Request the app to have the permission to schedule exact alarm
     * */
    private void requestForPostNotificationsPermission()
    {
        String [] permissions = new String[]{
                Manifest.permission.POST_NOTIFICATIONS
        };


        ActivityCompat.requestPermissions(this, permissions,
                POST_NOTIFICATIONS_PERMISSION_REQUEST_CODE);
    }



    /**
     * Handle the permission request results for request of
     *      (a) SCHEDULE_EXACT_ALARM permission
     *      (b) POST_NOTIFICATIONS permission
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SCHEDULE_EXACT_ALARM_PERMISSION_REQUEST_CODE){
            handleRequestForScheduleAlarmPermissionResult(grantResults);
        }
        else{
            handleRequestForPostNotificationsPermissionResult(grantResults);
        }
    }


    /**
     * Handles the results of permission requests for accessing schedule exact alarm
     * permission
     *
     * If permission is granted, then will allow user to continue. Otherwise will
     * display an error message and close the app
     *
     * @param grantResults results of permissions granted
     *
     * */
    private void handleRequestForScheduleAlarmPermissionResult(int[] grantResults)
    {
        if (!(grantResults.length == 1 &&
             grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            Toast.makeText(getApplicationContext(), "App cannot work without " +
                    "permission to schedule alarm.", Toast.LENGTH_LONG).show();

            finish();
        }
    }


    /**
     * Handles the results of permission requests for posting notifications
     *
     * If permission is granted, then will allow user to continue. Otherwise will
     * display an error message and close the app
     *
     * @param grantResults results of permissions granted
     *
     * */
    private void handleRequestForPostNotificationsPermissionResult(int[] grantResults)
    {
        if (!(grantResults.length == 1 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            Log.i("info", "grant results length is "  + grantResults.length);
            Log.i("info", "grant results[0] granted is " + (grantResults[0] == PackageManager.PERMISSION_GRANTED ));


            Toast.makeText(getApplicationContext(), "App cannot work without permission " +
                    "to post notifications.", Toast.LENGTH_LONG).show();

            finish();
        }
    }
}