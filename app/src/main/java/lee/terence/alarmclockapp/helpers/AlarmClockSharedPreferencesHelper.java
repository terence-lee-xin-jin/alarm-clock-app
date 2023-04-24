package lee.terence.alarmclockapp.helpers;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * A Singleton helper class that allows user to access the app's shared preference
 * storage, for storage of alarm time
 *
 * Allows for storage of only single alarm time
 *
 * <pre>
 *     //to store the alarm time
 *     long alarmTimeInMillisecondsSinceEpoch = 1234567891011;
 *     AlarmClockSharedPreferencesHelper.setNextAlarmTime(getApplicationContext(),
 *                                                      alarmTimeInMillisecondsSinceEpoch);
 *
 *     //to get the stored alarm time
 *
 *     if (AlarmClockSharedPreferencesHelper.hasNextAlarmTime(getApplicationContext()){
 *         long storedAlarmTimeInMillisecondsSinceEpoch =
 *                  AlarmClockSharedPreferencesHelper.getNextAlarmTime(getApplicationContext());
 *     }
 *
 *
 *     //to delete an existing stored alarm time
 *     AlarmClockSharedPreferencesHelper.deleteNextAlarmTimeIfAny(getApplicationContext());
 * </pre>
 *
 * @author Terence Lee
 * */
public class AlarmClockSharedPreferencesHelper {


    private static final String NEXT_ALARM_CLOCK_TIME_KEY = "NEXT_ALARM_CLOCK_TIME";

    private static final String SHARED_PREFERENCE_FILE_KEY =
                                            "lee.terence.simple_alarm_app.shared_preferences";


    /**
     * All methods in this class are static, so there is no need for a constructor for this class
     * */
    private AlarmClockSharedPreferencesHelper(){

    }


    /**
     * Store the next alarm time in the app's shared preferences
     *
     * @param context to be used for accessing the app's shared preferences. May not be null
     * @param nextAlarmTimeInMillisecondsSinceEpoch next alarm time in milliseconds since epoch,
     *                                                  which is 1 January 1970 12:00:00 am
     * */
    public static void setNextAlarmTime(Context context, long nextAlarmTimeInMillisecondsSinceEpoch){

        SharedPreferences sharedPreferences = getSharedPreferencesInstance(context);

        SharedPreferences.Editor sharedPreferencesEditor =
                sharedPreferences.edit();

        sharedPreferencesEditor.putLong(NEXT_ALARM_CLOCK_TIME_KEY, nextAlarmTimeInMillisecondsSinceEpoch);

        sharedPreferencesEditor.apply();
    }


    /**
     * Delete the stored next alarm time in the app's shared preferences storage, if
     * there is next alarm time stored in the shared preferences
     *
     * @param context to be used for accessing the app's shared preferences. May not be null
     * */
    public static void deleteNextAlarmTimeIfAny(Context context){
        SharedPreferences sharedPreferences = getSharedPreferencesInstance(context);

        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        sharedPreferencesEditor.remove(NEXT_ALARM_CLOCK_TIME_KEY);

        sharedPreferencesEditor.apply();
    }


    /**
     * Retrieve the stored next alarm time in the app's shared preferences storage. The next
     * alarm time is returned in milliseconds from the epoch (1 January 1970 12:00:00a.m.)
     *
     * If there is no stored next alarm time, returns
     *
     * @param context to be used for accessing the app's shared preferences. May not be null
     * */
    public static long getNextAlarmTime(Context context){

        SharedPreferences sharedPreferences = getSharedPreferencesInstance(context);

        long DEFAULT_VALUE = -1;

        return sharedPreferences.getLong(NEXT_ALARM_CLOCK_TIME_KEY, DEFAULT_VALUE);
    }


    /**
     * Checks if there is a next alarm time stored in the shared preference storage.
     * If there is a next alarm time stored, returns true. Otherwise returns false.
     *
     * @param context to be used for accessing the app's shared preferences. May not be null
     * */
    public static boolean hasNextAlarmTime(Context context){

        long DEFAULT_VALUE = -1;

        long nextAlarmTime = getNextAlarmTime(context);

        return nextAlarmTime != DEFAULT_VALUE;
    }


    /**
     * Returns an instance of SharedPreferences
     *
     * @param context to be used for accessing the app's shared preferences. May not be null
     * */
    private static SharedPreferences getSharedPreferencesInstance(Context context){
        return context.getSharedPreferences(SHARED_PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
    }
}
