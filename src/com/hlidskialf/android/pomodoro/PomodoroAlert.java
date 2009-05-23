package com.hlidskialf.android.pomodoro;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Button;
import android.view.KeyEvent;
import android.view.View;

import com.hlidskialf.android.widget.CountDownView;

public class PomodoroAlert extends Activity
{
  private CountDownView mTimerView;
  private Klaxon mKlaxon;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.alert);


    Intent intent = getIntent();
    final int alarm_type = intent.getIntExtra(Pomodoro.EXTRA_ALARM_TYPE, Pomodoro.ALARM_TYPE_TOMATO);
    long start = intent.getLongExtra(Pomodoro.EXTRA_ALARM_START, 0L);
    long duration = intent.getLongExtra(Pomodoro.EXTRA_ALARM_DURATION, 0L);

    TextView tv = (TextView)findViewById(R.id.alert_message);
    Button b1 = (Button)findViewById(android.R.id.button1);
    Button b2 = (Button)findViewById(android.R.id.button2);
    Button b3 = (Button)findViewById(android.R.id.button3);

    mTimerView = (CountDownView)findViewById(R.id.tomato_clock);
    if (mTimerView != null)
      mTimerView.start(0,0);


    if (alarm_type == Pomodoro.ALARM_TYPE_TOMATO) {
      tv.setText(R.string.alarm_message_tomato);
      b1.setText(R.string.start_rest);
      b2.setText(R.string.stop_working);
    } 
    else if (alarm_type == Pomodoro.ALARM_TYPE_REST) {
      tv.setText(R.string.alarm_message_rest);
      b1.setText(R.string.start_tomato);
      b2.setText(R.string.stop_working);
    }

    mKlaxon = Klaxon.instance(PomodoroAlert.this);

    b1.setOnClickListener(new Button.OnClickListener() {
      public void onClick(View b) { //keep working
        mKlaxon.stop();
        if (alarm_type == Pomodoro.ALARM_TYPE_TOMATO) 
          Pomodoro.startRest(PomodoroAlert.this);
        else {
          Pomodoro.startTomato(PomodoroAlert.this);
          Pomodoro.setTomatoCount(PomodoroAlert.this, -1);
        }
        finish();
      }
    });
    b2.setOnClickListener(new Button.OnClickListener() {
      public void onClick(View b) { //stop working
        mKlaxon.stop();
        Pomodoro.stopTomato(PomodoroAlert.this);
        Pomodoro.setTomatoCount(PomodoroAlert.this, -1);
        finish();
      }
    });
    b3.setOnClickListener(new Button.OnClickListener() {
      public void onClick(View b) { //silence
        mKlaxon.stop();
        b.setEnabled(false);
      }
    });
  }
  @Override public boolean onKeyDown(int code, KeyEvent event)
  {
    if (code == KeyEvent.KEYCODE_BACK) {
      mKlaxon.stop();
      Pomodoro.stopTomato(PomodoroAlert.this);
      Pomodoro.setTomatoCount(PomodoroAlert.this, -1);
    }
    return super.onKeyDown(code, event);
  }

}
