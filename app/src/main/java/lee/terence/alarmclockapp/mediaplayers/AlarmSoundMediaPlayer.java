package lee.terence.alarmclockapp.mediaplayers;

import android.content.Context;
import android.media.MediaPlayer;

import lee.terence.alarmclockapp.R;


/**
 * A Singleton simple media player that allows the playing of a simple alarm ringing sound
 *
 * <b>Example Usage:</b>
 * <pre>
 *     //Start playing the alarm ring sound
 *     //assuming currently in an activity
 *     AlarmSoundMediaPlayer.playAlarmRingingSound(getApplicationContext())
 *     ....
 *     //stop alarm ringing sound if no longer needed
 *     AlarmSoundMediaPlayer.stopAlarmRingingSoundIfPlaying();
 * </pre>
 *
 * @author Terence Lee
 * */
public class AlarmSoundMediaPlayer {

    private static MediaPlayer mediaPlayer;


    /**
     * All methods in this class are static, so there is no need for a constructor for this class
     * */
    private AlarmSoundMediaPlayer(){

    }


    /**
     * Play the alarm ringing sound indefinitely.
     *
     * To stop the alarm ring sound, call stopAlarmRingingSoundIfPlaying
     *
     * @see AlarmSoundMediaPlayer#stopAlarmRingingSoundIfPlaying()
     * */
    public static void playAlarmRingingSound(Context context){

        mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound);
        mediaPlayer.setLooping(true);

        mediaPlayer.start();
    }


    /**
     * Stop the alarm sound from playing, and release relevant system resources,
     * if it is playing.
     *
     * Otherwise do nothing if the alarm sound is not playing
     * */
    public static void stopAlarmRingingSoundIfPlaying(){

        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
