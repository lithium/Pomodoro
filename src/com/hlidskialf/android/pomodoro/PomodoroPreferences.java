
package com.hlidskialf.android.pomodoro;
import android.preference.PreferenceActivity;
import android.os.Bundle;

public class PomodoroPreferences extends PreferenceActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    addPreferencesFromResource(R.xml.preferences);
  }
}
