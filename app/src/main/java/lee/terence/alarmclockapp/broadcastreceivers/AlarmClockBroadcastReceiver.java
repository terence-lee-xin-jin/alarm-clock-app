package lee.terence.alarmclockapp.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import lee.terence.alarmclockapp.controllers.AlarmClockAlertActivity;
import lee.terence.alarmclockapp.helpers.AlarmClockNotificationHelper;
import lee.terence.alarmclockapp.helpers.AlarmClockSharedPreferencesHelper;
import lee.terence.alarmclockapp.mediaplayers.AlarmSoundMediaPlayer;


/**
 * A broadcast receiver that handles the broadcasts from alarms that
 * are set by this app
 *
 * @author Terence Lee
 * */
public class AlarmClockBroadcastReceiver extends BroadcastReceiver {

    /**
     * Channel Id used for the android alert notification
     **/
    private static final String ALARM_NOTIFICATION_CHANNEL_ID = "ALARM_NOTIFICATION_CHANNEL";

    /**
     * User visible name of the android alert notification
     **/
    private static final String AlARM_NOTIFICATION_CHANNEL_NAME = "ALARM NOTIFICATION CHANNEL";


    /**
     * Create an intent to start AlarmClockBroadcastReceiver
     *
     * @param context The context to use
     * */
   public static Intent createIntent(Context context){

       return new Intent(context, AlarmClockBroadcastReceiver.class);

   }


   /**
    * The method that is called when the broadcast receiver receives an intent
    * */
    @Override
    public void onReceive(Context context, Intent intent) {

        startAlarmClockAlertActivity(context);

        long alarmTimeInMillisecondsSinceEpoch =
                        AlarmClockSharedPreferencesHelper.getNextAlarmTime(context);

        AlarmClockNotificationHelper.postAlarmClockNotification(context,
                                                alarmTimeInMillisecondsSinceEpoch);

        AlarmSoundMediaPlayer.playAlarmRingingSound(context);
    }


    /**
     * Start an alarm clock alert activity (to display the alarm clock alert
     * if the user has the app opened in foreground)
     *
     * @param context The context to use
     * */
    private void startAlarmClockAlertActivity(Context context){

        Intent alarmClockAlertIntent = AlarmClockAlertActivity.createIntent(context);

        context.startActivity(alarmClockAlertIntent);
    }




}
