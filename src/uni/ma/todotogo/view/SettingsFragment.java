package uni.ma.todotogo.view;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceFragment;


public  class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
     // set Action Bar
     		ActionBar actionBar = getActivity().getActionBar();
     		actionBar.setHomeButtonEnabled(true);
     		actionBar.setDisplayUseLogoEnabled(false);
     		actionBar.setDisplayHomeAsUpEnabled(true);
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

}
