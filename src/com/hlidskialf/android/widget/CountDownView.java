package com.hlidskialf.android.widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;
import java.lang.Runnable;


public class CountDownView extends TextView
{
  private long mUntil;
  private Handler mHandler;
  private Runnable mCallback;
  private boolean mPaused,mRunning;

  public CountDownView(Context context, AttributeSet attrs)
  {
    super(context,attrs);

    mHandler = new Handler();
    mCallback = new Runnable() { 
      public void run() {
        if (mRunning && !mPaused) {
          CountDownView.this.tick();
          mHandler.postDelayed(mCallback, 1000);
        }
      }
    };
  }

  public void start(long durationMillis) { start(durationMillis,0); }
  public void start(long durationMillis, long startTime)
  {
    startTime = (startTime == 0) ? System.currentTimeMillis() : startTime;
    mUntil = startTime + durationMillis;

    mPaused = false;
    mRunning = true;
    mHandler.postDelayed(mCallback, 0);
  }

  public void stop()
  {
    setText("0:0");
    mRunning = false;
  }
  public void pause()
  {
    mPaused = true;
  }
  public void unpause()
  {
    mPaused = false;
    mHandler.postDelayed(mCallback, 0);
  }

  protected void tick() 
  {
    long left = mUntil - System.currentTimeMillis();
    long min = Math.abs((long)(left / 60000));
    long sec = Math.abs((long)((left - min) / 1000) % 60);
    setText((left < 0 ? "-" : "" )+ min+":"+sec);
  }
}
