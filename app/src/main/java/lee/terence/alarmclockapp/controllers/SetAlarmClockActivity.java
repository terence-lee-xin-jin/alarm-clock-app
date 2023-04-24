package lee.terence.alarmclockapp.controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

import lee.terence.alarmclockapp.helpers.AlarmClockHelper;
import lee.terence.alarmclockapp.helpers.AlarmClockSharedPreferencesHelper;
import lee.terence.alarmclockapp.R;


/**
 * An activity that allows users to set a new alarm/ overwrite an existing alarm if
 * there is an existing alarm
 *
 * @author Terence Lee
 * */
public class SetAlarmClockActivity extends AppCompatActivity {

    /**
     * Create an intent to start a SetAlarmClockActivity
     *
     * @param context The context to use. Usually your Application or Activity object
     * */
    public static Intent createIntent(Context context){

        return new Intent(context, SetAlarmClockActivity.class);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm_clock);
        initializeAllViews();
    }


    /**
     * Initialize all the views in this activity
     * */
    private void initializeAllViews(){
        initializeCancelButton();
        initializeSetAlarmButton();
    }


    /**
     * Initialize the cancel button the close the activity when it is clicked
     * */
    private void initializeCancelButton(){

        Button cancelButton = findViewById(R.id.cancel_button);

        cancelButton.setOnClickListener(view -> finish());
    }


    /**
     * Initialize the set alarm button to set the alarm time to what the user has selected
     * */
    private void initializeSetAlarmButton(){

        Button setAlarmButton = findViewById(R.id.set_alarm_button);

        setAlarmButton.setOnClickListener(view -> setAlarm());
    }


    /**
     * Set the alarm time according to the alarm time that the user has selected
     *
     * Also stores the set alarm time in the app's shared preferences storage
     *
     * Will also display a toast of the amount of time left before the alarm rings
     *
     * Will close (finish) this activity once the alarm time is set
     * */
    private void setAlarm(){

        TimePicker timePicker = findViewById(R.id.alarm_time_picker);

        int hourOfDay = timePicker.getHour();
        int minute = timePicker.getMinute();

        long nextAlarmTimeMillisecondsSinceEpoch =
                    AlarmClockHelper.setAlarmClock(getApplicationContext(), hourOfDay, minute);

        AlarmClockSharedPreferencesHelper.setNextAlarmTime(getApplicationContext(),
                                                            nextAlarmTimeMillisecondsSinceEpoch);

        displayToastOfTimeLeftBeforeAlarmRings(nextAlarmTimeMillisecondsSinceEpoch);

        finish();
    }



    /**
     * Displays a toast of the amount of time remaining before the alarm rings in
     * hours, minutes and seconds
     *
     * E.g of output: "Alarm will ring in 0 hours, 6 minutes and 45 seconds"
     *
     * @param alarmTimeInMillisecondsSinceEpoch the alarm time in milliseconds since epoch, where
     *                                          the epoch refers to 1 Jan 1980, 12:00:00 am
     * */
    private void displayToastOfTimeLeftBeforeAlarmRings(long alarmTimeInMillisecondsSinceEpoch){

        long currentTimeInMillisecondsSinceEpoch = Calendar.getInstance().getTimeInMillis();

        long timeDifference = alarmTimeInMillisecondsSinceEpoch - currentTimeInMillisecondsSinceEpoch;

        long ONE_HOUR_IN_MILLISECONDS = 3600000;
        long ONE_MINUTE_IN_MILLISECONDS = 60000;
        long ONE_SECOND_IN_MILLISECONDS = 1000;

        long numberOfHours = timeDifference/ONE_HOUR_IN_MILLISECONDS;

        long remainingTimeInMilliseconds = timeDifference%ONE_HOUR_IN_MILLISECONDS;
        long numberOfMinutes = remainingTimeInMilliseconds/ONE_MINUTE_IN_MILLISECONDS;

        remainingTimeInMilliseconds = remainingTimeInMilliseconds%ONE_MINUTE_IN_MILLISECONDS;
        long numberOfSeconds = remainingTimeInMilliseconds/ONE_SECOND_IN_MILLISECONDS;

        String toastTextToDisplay = String.format(Locale.ENGLISH,
               "Alarm will ring in %d hours, %d minutes and %d seconds",
               numberOfHours, numberOfMinutes, numberOfSeconds);

        Toast.makeText(getApplicationContext(), toastTextToDisplay, Toast.LENGTH_LONG).show();

    }

}