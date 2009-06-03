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
  private static long[] sVibratePattern = new long[] { 500, 1000 };

  interface KillerCallback {
      public void onKilled();
  }
  private KillerCallback mKillerCallback;

  private Uri mUri;
  private int mDelay, mVolume;
  private Handler mHandler;
  private Runnable mLoopCallback;
  private MediaPlayer.OnCompletionListener mCompletionListener;
  private MediaPlayer.OnErrorListener mErrorListener;

  public static synchronized Klaxon instance(Context context) {
    if (sInstance == null) sInstance = new Klaxon(context);
    return sInstance;

  }
  private Klaxon(Context context) {
    mVibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
    mContext = context;

    mHandler = new Handler();
    mLoopCallback = new Runnable() {
      public void run() { 
        if (mPlaying)
          startplaying();
      }
    };
    mCompletionListener = new MediaPlayer.OnCompletionListener() {
      public void onCompletion(MediaPlayer mp) {
        mMediaPlayer.stop();
        if (mPlaying)
          mHandler.postDelayed(mLoopCallback, mDelay);
      }
    };
    mErrorListener = new MediaPlayer.OnErrorListener() {
      public boolean onError(MediaPlayer mp, int what, int extra) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
        return true;
      }
    };
  }

  synchronized public void startplaying()
  {
    mMediaPlayer = new MediaPlayer();
    if (mMediaPlayer == null) return;

    mMediaPlayer.setOnErrorListener(mErrorListener);
    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
    mMediaPlayer.setVolume((float)mVolume/100F,(float)mVolume/100F);

    if (mDelay < 1) {
      mMediaPlayer.setLooping(true);
    } else {
      mMediaPlayer.setLooping(false);
      mMediaPlayer.setOnCompletionListener(mCompletionListener);
    }

    try {
      mMediaPlayer.setDataSource(mContext, mUri);
      mMediaPlayer.prepare();
    } catch (Exception ex) {
      return;
    }

    mMediaPlayer.start();
  }

  synchronized public void play(Uri uri, boolean vibrate, int timeout, int volume, int delay) {
    if (mPlaying) stop();

    if (vibrate) {
      mVibrator.vibrate(sVibratePattern, 0);
    }
    else {
      mVibrator.cancel();
    }

    mUri = uri;
    mDelay = delay;
    mVolume = volume; 
    startplaying();

    mPlaying = true;
    enableKiller(timeout);
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
