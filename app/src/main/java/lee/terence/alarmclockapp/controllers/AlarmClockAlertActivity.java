package lee.terence.alarmclockapp.controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;


import lee.terence.alarmclockapp.helpers.AlarmClockHelper;
import lee.terence.alarmclockapp.helpers.AlarmClockNotificationHelper;
import lee.terence.alarmclockapp.helpers.AlarmClockSharedPreferencesHelper;
import lee.terence.alarmclockapp.R;
import lee.terence.alarmclockapp.mediaplayers.AlarmSoundMediaPlayer;


/**
 * An activity that displays an alert to inform the user of a currently ringing alarm, and
 * provides a button for the user to stop the alarm
 *
 * This class is declared as a "singleTask" activity in the Manifest, so there can be
 * no two instances of this activity running at the same time
 *
 * @author Terence Lee
 * */
public class AlarmClockAlertActivity extends AppCompatActivity {

    /**
     * Create an intent to start a alarm alert activity
     *
     * @param context The context to use. Usually your Application or Activity object
     * */
    public static Intent createIntent(Context context){
        Intent intent = new Intent(context, AlarmClockAlertActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_clock_alert);

        initializeStopAlarmButton();
        initializeAlarmTimeTextView();
    }


    private void initializeStopAlarmButton(){

        Button stopAlarmButton = findViewById(R.id.stop_alarm_button);

        stopAlarmButton.setOnClickListener(view -> {
            AlarmSoundMediaPlayer.stopAlarmRingingSoundIfPlaying();
            AlarmClockHelper.deleteExistingAlarmIfAny(getApplicationContext());
            AlarmClockSharedPreferencesHelper.deleteNextAlarmTimeIfAny(getApplicationContext());

            AlarmClockNotificationHelper.deleteAllAlarmNotifications(getApplicationContext());
            AlarmClockAlertActivity.this.finish();
        });
    }

    /**
     * Initialize the alarm time textview to display the alarm time
     * */
    private void initializeAlarmTimeTextView(){

        TextView alarmTimeTextView = findViewById(R.id.alarm_time_text_view);

        long alarmTimeInMillisecondsSinceEpoch =
                AlarmClockSharedPreferencesHelper.getNextAlarmTime(getApplicationContext());

        Calendar alarmTimeCalendar = Calendar.getInstance();
        alarmTimeCalendar.setTimeInMillis(alarmTimeInMillisecondsSinceEpoch);

        int hour = alarmTimeCalendar.get(Calendar.HOUR);
        int minute = alarmTimeCalendar.get(Calendar.MINUTE);
        int timeOfDay = alarmTimeCalendar.get(Calendar.AM_PM);

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

        alarmTimeTextView.setText(timeToDisplay);
    }
}