package com.hlidskialf.android.pomodoro;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;

public class Pomodoro 
{
  public final static String ACTION_TOMATO_ALERT="com.hlidskialf.android.pomodoro.action.TOMATO_ALERT";

  public final static String EXTRA_ALARM_START="com.hlidskialf.android.pomodoro.extra.ALARM_START";
  public final static String EXTRA_ALARM_DURATION="com.hlidskialf.android.pomodoro.extra.ALARM_DURATION";
  public final static String EXTRA_ALARM_TYPE="com.hlidskialf.android.pomodoro.extra.ALARM_TYPE";
  public final static int ALARM_TYPE_NONE=0;
  public final static int ALARM_TYPE_TOMATO=1;
  public final static int ALARM_TYPE_REST=2;

  public final static String PREFERENCES="com.hlidskialf.android.pomodoro_preferences";
  public final static String PREF_ALARM_TYPE="alarm_type";
  public final static String PREF_ALARM_START="alarm_start";
  public final static String PREF_ALARM_DURATION="alarm_duration";
  public final static String PREF_TOMATO_COUNT="tomato_count";

  public final static String PREF_TOMATO_DURATION="tomato_duration";
  public final static int PREF_TOMATO_DURATION_DEFAULT=1;
  public final static String PREF_TOMATO_RINGTONE="tomato_ringtone";
  public final static String PREF_TOMATO_RINGTONE_DEFAULT=null;
  public final static String PREF_TOMATO_VIBRATE="tomato_vibrate";
  public final static boolean PREF_TOMATO_VIBRATE_DEFAULT=false;

  public final static String PREF_REST_DURATION="rest_duration";
  public final static int PREF_REST_DURATION_DEFAULT=1;
  public final static String PREF_REST_RINGTONE="rest_ringtone";
  public final static String PREF_REST_RINGTONE_DEFAULT=null;
  public final static String PREF_REST_VIBRATE="rest_vibrate";
  public final static boolean PREF_REST_VIBRATE_DEFAULT=false;

  public static void startTomato(Context context)
  {
    Pomodoro.pendingAlert(context, Pomodoro.ALARM_TYPE_TOMATO, 0, 0);
  }
  public static void startRest(Context context)
  {
    Pomodoro.pendingAlert(context, Pomodoro.ALARM_TYPE_REST, 0, 0);
  }
  public static void stopTomato(Context context)
  {
    SharedPreferences pref = context.getSharedPreferences(Pomodoro.PREFERENCES, 0);
    pref.edit()
      .putInt(Pomodoro.PREF_ALARM_TYPE, 0)
      .putLong(Pomodoro.PREF_ALARM_DURATION, 0)
      .putLong(Pomodoro.PREF_ALARM_START, 0)
      .commit();

    AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    Intent intent = new Intent(ACTION_TOMATO_ALERT);
    PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    am.cancel(sender);
  }

  static void pendingAlert(Context context, int type, long durationMillis, long startTime)
  {
    AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    SharedPreferences pref = context.getSharedPreferences(Pomodoro.PREFERENCES, 0);

    if (durationMillis == 0) {
      if (type == Pomodoro.ALARM_TYPE_REST)
        durationMillis = pref.getInt(Pomodoro.PREF_REST_DURATION, Pomodoro.PREF_REST_DURATION_DEFAULT)*60000;
      else
        durationMillis = pref.getInt(Pomodoro.PREF_TOMATO_DURATION, Pomodoro.PREF_TOMATO_DURATION_DEFAULT)*60000;
    }
    if (startTime == 0)
      startTime = System.currentTimeMillis();

    Intent intent = new Intent(ACTION_TOMATO_ALERT);
    intent.putExtra(EXTRA_ALARM_TYPE, type);
    intent.putExtra(EXTRA_ALARM_START, startTime);
    intent.putExtra(EXTRA_ALARM_DURATION, durationMillis);
    PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

    long when = startTime + durationMillis;
    am.set(AlarmManager.RTC_WAKEUP, when, sender);


    pref.edit()
      .putInt(Pomodoro.PREF_ALARM_TYPE, type)
      .putLong(Pomodoro.PREF_ALARM_START, startTime)
      .putLong(Pomodoro.PREF_ALARM_DURATION, durationMillis)
      .commit();

  }
  static int getTomatoCount(Context context)
  {
    SharedPreferences pref = context.getSharedPreferences(Pomodoro.PREFERENCES, 0);
    return pref.getInt(Pomodoro.PREF_TOMATO_COUNT, 0);
  }
  static int setTomatoCount(Context context, int value) // if value < 0; count += abs(value)
  {
    SharedPreferences pref = context.getSharedPreferences(Pomodoro.PREFERENCES, 0);
    int tomato_count = pref.getInt(Pomodoro.PREF_TOMATO_COUNT, 0);
    tomato_count = value < 0 ? Math.abs(value) + tomato_count : value;
    pref.edit().putInt(Pomodoro.PREF_TOMATO_COUNT, tomato_count).commit();
    return tomato_count;
  }



  public static class WakeLock
  {
    private static PowerManager.WakeLock sWakeLock;
    public static void acquire(Context context) {
      if (sWakeLock != null) {
        sWakeLock.release();
      }
      PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

      sWakeLock = pm.newWakeLock(
          PowerManager.FULL_WAKE_LOCK |
          PowerManager.ACQUIRE_CAUSES_WAKEUP |
          PowerManager.ON_AFTER_RELEASE, "Pomodoro");
      sWakeLock.acquire();
    }

    static void release() {
      if (sWakeLock != null) {
        sWakeLock.release();
        sWakeLock = null;
      }
    }
  };

    
}
