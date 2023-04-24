package lee.terence.alarmclockapp.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import lee.terence.alarmclockapp.broadcastreceivers.AlarmClockBroadcastReceiver;


/**
 * A Singleton alarm clock helper that allows users to set alarms
 * Only allows the setting of a single alarm time. If attempt to set a second alarm time,
 * will overwrite the existing alarm
 *
 * Internally, this class interacts with the Android system AlarmManager
 *
 * <b>Example Usage:</b>
 * <pre>
 *     //alarm time to set is 5:25 pm
 *     int hourOfDay= 17;
 *     int minute = 25;
 *
 *     //set the alarm for 5:25pm, and delete any existing alarm if there is any
 *     AlarmClockHelper.setAlarmTime(getApplicationContext(), hourOfDay, minute);
 *
 *
 *     //to delete the existing alarm if any
 *     AlarmClockHelper.deleteExistingAlarmIfAny(getApplicationContext());
 * </pre>
 *
 * @author Terence Lee
 * */
public class AlarmClockHelper {

    /**
     * All methods in this class are static, so there is no need for a constructor for this class
     * */
    private AlarmClockHelper(){

    }


    /**
     * Set the alarm clock. If there is any existing alarm used by this app, the alarm will be
     * overwritten.
     *
     * @param context e context to use. Usually your Application or Activity object. May not be null
     * @param hourOfDay hour of the day to set the alarm (value from 0 (inclusive) to 23 (inclusive))
     * @param minute minute of the hour to set the alarm (value from 0 (inclusive) to 59 (inclusive))
     * */
    public static long setAlarmClock(Context context, int hourOfDay, int minute){

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = AlarmClockBroadcastReceiver.createIntent(context);

        PendingIntent alarmPendingIntent = createPendingIntent(context, intent);

        Calendar alarmCalendar = convertHourOfDayAndMinuteToCalendar(hourOfDay, minute);

        addOneDayToCalendarIfCalendarTimeAlreadyLapsed(alarmCalendar);

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                alarmCalendar.getTimeInMillis(), alarmPendingIntent);

        return alarmCalendar.getTimeInMillis();
    }


    /**
     * Converts an instance of hour of day and minute to an instance of calendar
     * This helper method is used to create the calendar instance used for setting alarm time
     *
     * @param hourOfDay hour of the day to set the alarm (value from 0 (inclusive) to 23 (inclusive))
     * @param minute minute of the hour to set the alarm (value from 0 (inclusive) to 59 (inclusive))
     * */
    private static Calendar convertHourOfDayAndMinuteToCalendar(int hourOfDay, int minute){

        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }


    /**
     * Add a single day to the alarm calendar (calendar representing alarm time) if the
     * alarm calendar represents a date and time that has already lapsed.
     *
     * This allows the time represented in the alarm calendar to represent a time that
     * is in the future (within 24 hours)
     *
     * @param alarmCalendar the calendar representing alarm time. This instance will be
     *                      modified and be the return value of this method
     * */
    private static void addOneDayToCalendarIfCalendarTimeAlreadyLapsed(Calendar alarmCalendar){

        Calendar currentCalendar = Calendar.getInstance();

        final long ONE_DAY_IN_MILLISECONDS = 86400000;

        if (alarmCalendar.before(currentCalendar)){
            alarmCalendar.setTimeInMillis(alarmCalendar.getTimeInMillis() + ONE_DAY_IN_MILLISECONDS);
        }
    }


    /**
     * Delete an existing pending alarm if there is any. Will not throw exception if there
     * is no existing alarm
     *
     * @param context the context to use. Usually your Application or Activity object. May not be
     *                null
     * */
    public static void deleteExistingAlarmIfAny(Context context){

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = AlarmClockBroadcastReceiver.createIntent(context);
        PendingIntent alarmPendingIntent = createPendingIntent(context, intent);

        alarmManager.cancel(alarmPendingIntent);
    }


    /**
     * A helper method to create a pending intent
     *
     * @param context the context to use. Usually your Application or Activity object. May not be
     *                null
     * @param intent the intent representing the intent of a broadcast receiver to be run when
     *               the broadcast of the pending intent is fired in the future
     * */
    private static PendingIntent createPendingIntent(Context context, Intent intent){

        final int REQUEST_CODE = 0;

        return PendingIntent.getBroadcast(context, REQUEST_CODE,
                intent, PendingIntent.FLAG_IMMUTABLE );

    }

}
