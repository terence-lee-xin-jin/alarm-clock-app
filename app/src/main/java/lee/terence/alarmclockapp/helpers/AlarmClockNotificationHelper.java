package lee.terence.alarmclockapp.helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;
import java.util.Locale;

import lee.terence.alarmclockapp.R;
import lee.terence.alarmclockapp.controllers.AlarmClockAlertActivity;


/**
 * A Singleton notification helper class that allows for posting and deleting alarm clock
 * notifications
 *
 * <b>Example Usage:</b>
 * <pre>
 *     long alarmTimeInMillisecondsSinceEpoch = 1234567891011;
 *
 *     //send the alarm clock notification to the android device
 *     AlarmClockNotificationHelper.postAlarmClockNotification(getApplicationContext(),
 *                                                  alarmTimeInMillisecondsSinceEpoch);
 *
 *
 *     //to delete all alarm clock notifications from this app
 *     AlarmClockNotificationHelper.deleteAllAlarmNotifications(getApplicationContext());
 * </pre>
 *
 * @author Terence Lee
 * */
public class AlarmClockNotificationHelper {

    /**
     * Channel Id used for the android alert notification channel
     **/
    private static final String ALARM_NOTIFICATION_CHANNEL_ID = "ALARM_NOTIFICATION_CHANNEL";


    /**
     * User visible name of the android alert notification channel name
     **/
    private static final String AlARM_NOTIFICATION_CHANNEL_NAME = "ALARM NOTIFICATION CHANNEL";



    /**
     * All methods in this class are static, so there is no need for a constructor
     * for this class
     * */
    private AlarmClockNotificationHelper(){

    }


    /**
     * Post alarm clock notification to the user that the alarm clock has gone off.
     * The notification, upon clicking, will bring user to the AlarmClockAlertActivity
     * for the user to turn off the alarm
     *
     * @param context  The context to use. Usually your Application or Activity object
     * */
    public static void postAlarmClockNotification(Context context,
                                                  long alarmTimeInMillisecondsSinceEpoch){

        NotificationManager notificationManager = getNotificationManager(context);

        createAlarmClockNotificationChannel(notificationManager);

        String alarmNotificationContentTitle =
                getAlarmClockNotificationContentTitle(alarmTimeInMillisecondsSinceEpoch);

        PendingIntent alarmAlertActivityPendingIntent =
                                createAlarmAlertActivityPendingIntent(context);

        postAlarmClockNotification(context, notificationManager, alarmNotificationContentTitle,
                                    alarmAlertActivityPendingIntent);
    }

    /**
     * Post an alarm clock notification to the user. Will appear as a small notification on
     * top of the screen.
     *
     * Helper method of the overloaded method of the same name
     *
     * @param context  The context to use. Usually your Application or Activity object
     * @param notificationManager the notification manager of the system
     * */
    private static void postAlarmClockNotification(Context context,
                                                   NotificationManager notificationManager,
                                                   String alarmNotificationContentTitle,
                                                   PendingIntent alarmAlertActivityPendingIntent){

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, ALARM_NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.icon_alarm_clock)
                        .setContentTitle(alarmNotificationContentTitle)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setContentIntent(alarmAlertActivityPendingIntent);

        Notification alarmNotification = notificationBuilder.build();

        final int NOTIFICATION_ID = 1;

        notificationManager.notify(NOTIFICATION_ID, alarmNotification);
    }


    /**
     * Return a string containing the alarm time, to be used for alarm clock notification
     * content title
     *
     * E.g. of string returned: "Alarm now at 02:35 p.m."
     * */
    private static String getAlarmClockNotificationContentTitle(
                                    long alarmTimeInMillisecondsSinceEpoch){


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

        return String.format(Locale.ENGLISH,
                "Alarm now at %02d:%02d %s", hour, minute, timeOfDayString);

    }


    /**
     * Create an alarm notification channel. Part of requirements of sending notifications since
     * Android 8 (all notifications must be assigned to a channel)
     *
     * @param notificationManager notification manager of the system
     * */
    private static void createAlarmClockNotificationChannel(NotificationManager notificationManager){

        NotificationChannel alarmNotificationChannel = new NotificationChannel(
                ALARM_NOTIFICATION_CHANNEL_ID, AlARM_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH);

        notificationManager.createNotificationChannel(alarmNotificationChannel);

    }


    /**
     * Create a PendingIntent to launch the AlarmAlertActivity
     *
     * @param context  The context to use. Usually your Application or Activity object
     * */
    private static PendingIntent createAlarmAlertActivityPendingIntent(Context context){

        Intent alarmAlertActivityIntent = AlarmClockAlertActivity.createIntent(context);

        final int REQUEST_CODE = 0;

        return PendingIntent.getActivity(context, REQUEST_CODE, alarmAlertActivityIntent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }


    /**
     * Delete all alarm clock app notifications
     *
     * @param context  The context to use. Usually your Application or Activity object
     * */
    public static void deleteAllAlarmNotifications(Context context){

        NotificationManager notificationManager = getNotificationManager(context);

        notificationManager.cancelAll();
    }

    /**
     * Returns an instance of the system notification manager
     *
     * @param context  The context to use. Usually your Application or Activity object
     * */
    private static NotificationManager getNotificationManager(Context context){

        return (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    }



}
