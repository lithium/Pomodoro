package com.hlidskialf.android.pomodoro;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;

import com.hlidskialf.android.widget.CountDownView;

public class PomodoroActivity extends Activity
{
  private CountDownView mTimerView;
  private TextView mStatusText;
  private ViewGroup mTomatoBar;
  private Button mStartButton, mStopButton;

  private SharedPreferences mPrefs;
  
  private int mCurAlarmType,mTomatoCount;

  private static final int REQUEST_PREFERENCES=1;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    mTimerView = (CountDownView)findViewById(R.id.tomato_clock);
    mStatusText = (TextView)findViewById(R.id.tomato_status);
    mTomatoBar = (ViewGroup)findViewById(R.id.tomato_bar);

    mStartButton = (Button)findViewById(R.id.tomato_start);
    mStartButton.setOnClickListener(new Button.OnClickListener() {
      public void onClick(View b) { 
        mPrefs = getSharedPreferences(Pomodoro.PREFERENCES, 0);
        int duration = mPrefs.getInt(Pomodoro.PREF_TOMATO_DURATION, Pomodoro.PREF_TOMATO_DURATION_DEFAULT);
        
        Pomodoro.startTomato(PomodoroActivity.this);
        mTimerView.start(duration*60000);
        mCurAlarmType = Pomodoro.ALARM_TYPE_TOMATO;

        update_tomato_bar();
        update_status();
      }
    });


    mStopButton = (Button)findViewById(R.id.tomato_stop);
    mStopButton.setOnClickListener(new Button.OnClickListener() {
      public void onClick(View b) { 
        Pomodoro.stopTomato(PomodoroActivity.this);
        mTimerView.stop();

        if (mCurAlarmType == Pomodoro.ALARM_TYPE_REST)
          mTomatoCount = Pomodoro.setTomatoCount(PomodoroActivity.this, -1);

        mCurAlarmType = Pomodoro.ALARM_TYPE_NONE;

        update_tomato_bar();
        update_status();
      }
    });

  }
  @Override
  public void onResume()
  {
    super.onResume();

    mPrefs = getSharedPreferences(Pomodoro.PREFERENCES, 0);
    long start = mPrefs.getLong(Pomodoro.PREF_ALARM_START,0);
    long duration = mPrefs.getLong(Pomodoro.PREF_ALARM_DURATION,0);
    mCurAlarmType = mPrefs.getInt(Pomodoro.PREF_ALARM_TYPE,0); 
    mTomatoCount = mPrefs.getInt(Pomodoro.PREF_TOMATO_COUNT,0);

    mTimerView.stop();
    if (mCurAlarmType == Pomodoro.ALARM_TYPE_TOMATO) {
      mTimerView.start(duration,start);
    }
    else
    if (mCurAlarmType == Pomodoro.ALARM_TYPE_REST) {
      mTimerView.start(duration,start);
    }

    update_tomato_bar();
    update_status();
  }
  @Override
  public void onPause()
  {
    super.onPause();
    mTimerView.pause();
  }
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.options, menu);

    return true;
  }
  @Override
  public boolean onPrepareOptionsMenu(Menu menu)
  {
    MenuItem reset_item = menu.findItem(R.id.options_menu_reset);
    reset_item.setVisible(Pomodoro.getTomatoCount(this) > 0);
    return super.onPrepareOptionsMenu(menu);
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId()) {
      case R.id.options_menu_preferences:
        startActivityForResult( new Intent(this, PomodoroPreferences.class), REQUEST_PREFERENCES );
        return true;
      case R.id.options_menu_about:
        View layout = getLayoutInflater().inflate(R.layout.about, null);
        AlertDialog dia = new AlertDialog.Builder(this)
                                .setTitle(R.string.about_title)
                                .setView(layout)
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
        return true;
      case R.id.options_menu_reset:
        Pomodoro.stopTomato(PomodoroActivity.this);
        mTomatoCount = Pomodoro.setTomatoCount(PomodoroActivity.this, 0);
        update_tomato_bar();
        update_status();
        return true;
    }    
    return super.onOptionsItemSelected(item);
  }
  

  private void update_status()
  {
    if (mCurAlarmType == Pomodoro.ALARM_TYPE_TOMATO) {
      mStatusText.setText(R.string.status_tomato);
      mStartButton.setEnabled(false);
      mStopButton.setVisibility(View.VISIBLE);
    }
    else
    if (mCurAlarmType == Pomodoro.ALARM_TYPE_REST) {
      mStatusText.setText(R.string.status_rest);
      mStartButton.setEnabled(false);
      mStopButton.setVisibility(View.VISIBLE);
    }
    else {
      mStatusText.setText(R.string.status_none);
      mStartButton.setEnabled(true);
      mStopButton.setVisibility(View.GONE);
    }

  }

  private void update_tomato_bar()
  {
    mTomatoBar.removeAllViews();
    int i;
    int c=0;
    LinearLayout holder = null;
    for (i=0; i < mTomatoCount; i++) {
      if (c++ % 4 == 0) {
        holder = new LinearLayout(this);
        holder.setGravity(Gravity.CENTER);
        mTomatoBar.addView(holder);
      }
      ImageView iv = new ImageView(this);
      iv.setImageResource(R.drawable.tomato);
      holder.addView(iv);
    }
    if (c++ % 4 == 0) {
      holder = new LinearLayout(this);
      holder.setGravity(Gravity.CENTER);
      mTomatoBar.addView(holder);
    }
    if (mCurAlarmType == Pomodoro.ALARM_TYPE_TOMATO) {
      ImageView iv = new ImageView(this);
      iv.setImageResource(R.drawable.greentomato);
      holder.addView(iv);
    }
    if (mCurAlarmType == Pomodoro.ALARM_TYPE_REST) {
      ImageView iv = new ImageView(this);
      iv.setImageResource(R.drawable.tomato);
      holder.addView(iv);
    }
  }
}
