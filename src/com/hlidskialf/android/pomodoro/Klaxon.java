package com.hlidskialf.android.pomodoro;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;

public class Klaxon
{
  private static Klaxon sInstance = null;

  private Context mContext;
  private Handler mTimeout;
  private boolean mPlaying = false;

  private Vibrator mVibrator;
  private MediaPlayer mMediaPlayer;
  private static long[] sVibratePattern = new long[] { 500, 500 };

  interface KillerCallback {
      public void onKilled();
  }
  private KillerCallback mKillerCallback;


  public static synchronized Klaxon instance(Context context) {
    if (sInstance == null) sInstance = new Klaxon(context);
    return sInstance;

  }
  private Klaxon(Context context) {
    mVibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
    mContext = context;
  }

  public void play(Uri uri, boolean vibrate, int timeout) {
    if (mPlaying) stop();

    //vibrate
    if (vibrate) {
      mVibrator.vibrate(sVibratePattern, 0);
    }
    else {
      mVibrator.cancel();
    }

    //play audio
    mMediaPlayer = new MediaPlayer();
    if (mMediaPlayer != null) {
      mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
        public boolean onError(MediaPlayer mp, int what, int extra) {
          mMediaPlayer.stop();
          mMediaPlayer.release();
          mMediaPlayer = null;
          return true;
        }
      });
      try {

        mMediaPlayer.setDataSource(mContext, uri);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        //mMediaPlayer.setVolume(100,100);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.prepare();
        
      } catch (Exception ex) {
        return;
      }
      mMediaPlayer.start();
      mPlaying = true;
      enableKiller(timeout);
    }
  }
  public void stop()
  {
    if (mPlaying) {
      mPlaying = false;
      if (mMediaPlayer != null) 
        mMediaPlayer.stop();

      mVibrator.cancel();
    }
    disableKiller();
  }

  void setKillerCallback(KillerCallback killerCallback) {
      mKillerCallback = killerCallback;
  }
  private void enableKiller(int duration) {
    mTimeout = new Handler();
    mTimeout.postDelayed(new Runnable() {
      public void run() {
          if (mKillerCallback != null) mKillerCallback.onKilled();
      }
    }, duration);
  }
  private void disableKiller() {
    if (mTimeout != null) {
      mTimeout.removeCallbacksAndMessages(null);
      mTimeout = null;
    }
  }
  
  
};
