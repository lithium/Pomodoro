package com.hlidskialf.android.pomodoro;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.net.Uri;

public class PomodoroReceiver extends BroadcastReceiver {

  public void onReceive(Context context, Intent intent) {
    long now = System.currentTimeMillis();
    int alarm_type = intent.getIntExtra(Pomodoro.EXTRA_ALARM_TYPE, 0);
    long duration = intent.getLongExtra(Pomodoro.EXTRA_ALARM_DURATION, 0);
    long start = intent.getLongExtra(Pomodoro.EXTRA_ALARM_START, 0);

    //load settings
    SharedPreferences prefs = context.getSharedPreferences(Pomodoro.PREFERENCES, 0);
    Uri uri = null;
    int timeout = 1;
    boolean vibrate = false;
    String tone;
    int volume = 100;
    int delay = 0;

    if (alarm_type == Pomodoro.ALARM_TYPE_TOMATO) {
      vibrate = prefs.getBoolean(Pomodoro.PREF_TOMATO_VIBRATE, Pomodoro.PREF_TOMATO_VIBRATE_DEFAULT);
      tone = prefs.getString(Pomodoro.PREF_TOMATO_RINGTONE, Pomodoro.PREF_TOMATO_RINGTONE_DEFAULT);
      if (tone != null) uri = Uri.parse(tone);
      volume = prefs.getInt(Pomodoro.PREF_TOMATO_VOLUME, Pomodoro.PREF_TOMATO_VOLUME_DEFAULT);
      delay = prefs.getInt(Pomodoro.PREF_TOMATO_DELAY, Pomodoro.PREF_TOMATO_DELAY_DEFAULT);
    }
    else
    if (alarm_type == Pomodoro.ALARM_TYPE_REST) {
      vibrate = prefs.getBoolean(Pomodoro.PREF_REST_VIBRATE, Pomodoro.PREF_REST_VIBRATE_DEFAULT);
      tone = prefs.getString(Pomodoro.PREF_REST_RINGTONE, Pomodoro.PREF_REST_RINGTONE_DEFAULT);
      if (tone != null) uri = Uri.parse(tone);
      volume = prefs.getInt(Pomodoro.PREF_REST_VOLUME, Pomodoro.PREF_REST_VOLUME_DEFAULT);
      delay = prefs.getInt(Pomodoro.PREF_REST_DELAY, Pomodoro.PREF_REST_DELAY_DEFAULT);
    }

    if (uri == null)
      uri = android.provider.Settings.System.DEFAULT_RINGTONE_URI;
  
    //wake device
    Pomodoro.WakeLock.acquire(context);

    //start alert
    Klaxon klaxon = Klaxon.instance(context);
    klaxon.play(uri, vibrate, timeout * 60000, volume, delay);

    //launch ui
    Intent fireAlarm = new Intent(context, PomodoroAlert.class);
    fireAlarm.putExtra(Pomodoro.EXTRA_ALARM_TYPE, alarm_type);
    fireAlarm.putExtra(Pomodoro.EXTRA_ALARM_DURATION, duration);
    fireAlarm.putExtra(Pomodoro.EXTRA_ALARM_START, start);
    fireAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(fireAlarm);
  }  
}

