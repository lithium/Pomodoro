
package com.hlidskialf.android.pomodoro;
import android.preference.PreferenceActivity;
import android.preference.Preference;
import android.content.SharedPreferences;
import android.os.Bundle;
import java.util.HashMap;

import com.hlidskialf.android.preference.SliderPreference;

public class PomodoroPreferences extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
  private SharedPreferences mPrefs;
  private SliderPreference mTomatoDuration, mRestDuration;

  private HashMap<String, Preference> mPreferences;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    addPreferencesFromResource(R.xml.preferences);

    mPrefs = getPreferenceScreen().getSharedPreferences();

    mPreferences = new HashMap<String,Preference>();

    Preference pref;
    pref = findPreference(Pomodoro.PREF_REST_DURATION);
    fillInValue(pref, String.valueOf( mPrefs.getInt(Pomodoro.PREF_REST_DURATION, Pomodoro.PREF_REST_DURATION_DEFAULT) ));
    mPreferences.put(Pomodoro.PREF_REST_DURATION, pref);
    pref = findPreference(Pomodoro.PREF_REST_VOLUME);
    fillInValue(pref, String.valueOf( mPrefs.getInt(Pomodoro.PREF_REST_VOLUME, Pomodoro.PREF_REST_VOLUME_DEFAULT) ));
    mPreferences.put(Pomodoro.PREF_REST_VOLUME, pref);
    pref = findPreference(Pomodoro.PREF_REST_DELAY);
    fillInValue(pref, String.valueOf( mPrefs.getInt(Pomodoro.PREF_REST_DELAY, Pomodoro.PREF_REST_DELAY_DEFAULT) ));
    mPreferences.put(Pomodoro.PREF_REST_DELAY, pref);
    pref = findPreference(Pomodoro.PREF_TOMATO_DURATION);
    fillInValue(pref, String.valueOf( mPrefs.getInt(Pomodoro.PREF_TOMATO_DURATION, Pomodoro.PREF_TOMATO_DURATION_DEFAULT) ));
    mPreferences.put(Pomodoro.PREF_TOMATO_DURATION, pref);
    pref = findPreference(Pomodoro.PREF_TOMATO_VOLUME);
    fillInValue(pref, String.valueOf( mPrefs.getInt(Pomodoro.PREF_TOMATO_VOLUME, Pomodoro.PREF_TOMATO_VOLUME_DEFAULT) ));
    mPreferences.put(Pomodoro.PREF_TOMATO_VOLUME, pref);
    pref = findPreference(Pomodoro.PREF_TOMATO_DELAY);
    fillInValue(pref, String.valueOf( mPrefs.getInt(Pomodoro.PREF_TOMATO_DELAY, Pomodoro.PREF_TOMATO_DELAY_DEFAULT) ));
    mPreferences.put(Pomodoro.PREF_TOMATO_DELAY, pref);

  }
  @Override
  protected void onResume() {
    super.onResume();
    mPrefs.registerOnSharedPreferenceChangeListener(this);
  }
  @Override
  protected void onPause() {
    super.onPause();
    mPrefs.unregisterOnSharedPreferenceChangeListener(this);
  }
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
  {
    Preference pref = mPreferences.get(key);

    if (pref != null)
      fillInValue(pref, String.valueOf( sharedPreferences.getInt(key, 0) ));
  }




  private static void fillInValue(Preference pref, String value)
  {
    String summary = pref.getSummary().toString();
    int idx = summary.lastIndexOf('=');
    if (idx != -1) {
      summary = summary.substring(0,idx+1).concat( " "+value );
    }
    pref.setSummary(summary);
  }
}
