package se.frand.app.todo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Date;

/**
 * Created by victorfrandsen on 10/11/15.
 */
public class FilterSettingsActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.filter_prefs);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(FilterSettingsActivity.this);

        Preference pShowComplete = findPreference(getString(R.string.pref_show_complete_key));
        pShowComplete.setOnPreferenceChangeListener(FilterSettingsActivity.this);
        onPreferenceChange(
                pShowComplete,
                sharedPrefs.getBoolean(
                        pShowComplete.getKey(),
                        Boolean.getBoolean(pShowComplete.getContext().getString(R.string.pref_show_complete_default))
                )
        );

        Preference pFilterDate = findPreference(getString(R.string.pref_date_filter_select_key));
        pFilterDate.setOnPreferenceClickListener(FilterSettingsActivity.this);
        pFilterDate.setOnPreferenceChangeListener(FilterSettingsActivity.this);
        onPreferenceChange(pFilterDate, sharedPrefs.getLong(
                pFilterDate.getKey(),
                System.currentTimeMillis()
        ));

        Preference pDateCheck = findPreference(getString(R.string.pref_date_filter_key));
        pDateCheck.setOnPreferenceChangeListener(FilterSettingsActivity.this);

        onPreferenceChange(pDateCheck, sharedPrefs.getBoolean(
                pDateCheck.getKey(),
                Boolean.getBoolean(pDateCheck.getContext().getString(R.string.pref_date_filter_key)))
        );
    }



    @Override
    public boolean onPreferenceClick(final Preference preference) {

        String prefKey = preference.getKey();
        Log.v("onPreferenceClick()", "clicked on " + prefKey);

        if(prefKey.compareTo(getApplicationContext().getString(R.string.pref_date_filter_select_key)) == 0) {
            // TODO create dialog picker
            DatePreference dpref = (DatePreference) preference;
            dpref.showPicker();
        }
        return false;
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        Context context = preference.getContext();

        if(key.compareTo(context.getString(R.string.pref_show_complete_key)) == 0) {
            Boolean checked = (Boolean) newValue;
            if(checked) {
                preference.setSummary(R.string.pref_show_complete_summary);
            } else {
                preference.setSummary(R.string.pref_show_complete_summary2);
            }
        }
        if(key.compareTo(context.getString(R.string.pref_date_filter_select_key)) == 0) {
            Log.v("onPreferenceChange()", "preference changed " + key);
            Long date = (Long) newValue;
            preference.setSummary(Util.getDate(new Date(date),Util.MDY_DATE_FORMAT));
        }
        if(key.compareTo(context.getString(R.string.pref_date_filter_key)) == 0) {
            Boolean isChecked = (Boolean) newValue;
            Preference datePref = findPreference(context.getString(R.string.pref_date_filter_select_key));
            datePref.setEnabled(isChecked);
            datePref.setPersistent(isChecked);
        }
        return true;
    }
}
