package com.app.gptalk.option;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuItem;

import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;

public class OptionSettings extends PreferenceActivity {

    private String username, userType;
    private BaseClass baseClass = new BaseClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getIntentDetails();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
    }

    private void getIntentDetails() {

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        userType = intent.getStringExtra("user_type");
    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            // Define all clickable destinations in XML file
            addPreferencesFromResource(R.xml.settings);
        }
    }

    // Launch preference menu
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.optionalmenu, menu);
        return true;
    }

    public void launchOptionActivity(String destination) {

        try {
            Intent launchIntent = new Intent("com.app.gptalk.option." + destination);
            launchIntent.putExtra("username", username);
            launchIntent.putExtra("user_type", userType);
            startActivity(launchIntent);
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }
    }

    // Launch preference menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.optionLaunch:
                return true;

            case R.id.optionHome:

                if (userType.equals("patient")) {
                    Intent userHomepageIntent = new Intent("com.app.gptalk.homepage.patient.PATIENTHOMEPAGE");
                    userHomepageIntent.putExtra("username", username);
                    startActivity(userHomepageIntent);
                } else {
                    Intent gpHomepageIntent = new Intent("com.app.gptalk.homepage.gp.GPHOMEPAGE");
                    gpHomepageIntent.putExtra("username", username);
                    startActivity(gpHomepageIntent);
                }
                return true;

            case R.id.optionAccount:

                if (userType.equals("patient")) {
                    launchOptionActivity("OPTIONPATIENTACCOUNT");
                } else {
                    launchOptionActivity("OPTIONGPACCOUNT");
                }
                return true;

            case R.id.optionFAQ:
                launchOptionActivity("OPTIONFAQ");
                return true;

            case R.id.optionSupport:
                launchOptionActivity("OPTIONSUPPORT");
                return true;

            case R.id.optionSettings:
                launchOptionActivity("OPTIONSETTINGS");
                return true;

            case R.id.optionWebsite:
                launchOptionActivity("OPTIONWEBSITE");
                return true;

            case R.id.exit:
                Intent loginIntent = new Intent("com.app.gptalk.main.LOGIN");
                startActivity(loginIntent);
                return true;
        }

        return false;
    }
}
