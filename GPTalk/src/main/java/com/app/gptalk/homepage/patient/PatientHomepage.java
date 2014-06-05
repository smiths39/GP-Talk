package com.app.gptalk.homepage.patient;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.gptalk.fragment_adapter.PatientFragmentAdapter;
import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;

public class PatientHomepage extends FragmentActivity {

    private ActionBar actionBar;
    private ViewPager viewPager;

    private BaseClass baseClass = new BaseClass();
    private String patientUsername;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_homepage);

        patientUsername = baseClass.retrieveUsername(null, PatientHomepage.this);

        actionBar = getActionBar();
        viewPager = (ViewPager) findViewById(R.id.pager);

        initialiseActionBarSettings(actionBar, viewPager);
    }

    private void initialiseActionBarSettings(final ActionBar newActionBar, ViewPager newViewPager) {

        // Setting up the action bar to show tabs
        newActionBar.setDisplayHomeAsUpEnabled(true);
        newActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        newActionBar.setHomeButtonEnabled(true);

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Recognise which tab was selected
        ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener(){

            @Override
            public void onPageSelected(int position) {

                super.onPageSelected(position);
                newActionBar.setSelectedNavigationItem(position);
            }
        };

        newViewPager.setOnPageChangeListener(pageChangeListener);

        // Launch fragment corresponding to tab selection
        PatientFragmentAdapter fragmentPatientAdapter = new PatientFragmentAdapter(fragmentManager);

        newViewPager.setAdapter(fragmentPatientAdapter);
        newActionBar.setDisplayShowTitleEnabled(true);

        // Initialise fragment listener
        setActionBarTabListeners(newViewPager);
    }

    private void setActionBarTabListeners(final ViewPager newViewPager) {

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabReselected(ActionBar.Tab arg0, android.app.FragmentTransaction arg1) {

            }

            @Override
            public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

                   newViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

            }
        };

        addActionBarTabs(tabListener);
    }

    private void addActionBarTabs(ActionBar.TabListener tabListener) {

        // Define all tabs in viewpager
        ActionBar.Tab homeTab = actionBar.newTab()
                                         .setText(" Home")
                                         .setIcon(R.drawable.home_tab)
                                         .setTabListener(tabListener);

        ActionBar.Tab newBookingsTab = actionBar.newTab()
                                                .setText(" Bookings")
                                                .setIcon(R.drawable.new_bookings_tab)
                                                .setTabListener(tabListener);

        ActionBar.Tab consultationsTab = actionBar.newTab()
                                                  .setText(" Schedule")
                                                  .setIcon(R.drawable.consultations_tab)
                                                  .setTabListener(tabListener);

        ActionBar.Tab historyTab = actionBar.newTab()
                                            .setText(" History")
                                            .setIcon(R.drawable.history_tab)
                                            .setTabListener(tabListener);

        ActionBar.Tab calendarTab = actionBar.newTab()
                                             .setText(" Calendar")
                                             .setIcon(R.drawable.calendar_tab)
                                             .setTabListener(tabListener);

        ActionBar.Tab locationTab = actionBar.newTab()
                                             .setText(" Locations")
                                             .setIcon(R.drawable.location_tab)
                                             .setTabListener(tabListener);

        ActionBar.Tab websiteTab = actionBar.newTab()
                                            .setText(" Websites")
                                            .setIcon(R.drawable.websites_tab)
                                            .setTabListener(tabListener);

        actionBar.addTab(homeTab);
        actionBar.addTab(newBookingsTab);
        actionBar.addTab(consultationsTab);
        actionBar.addTab(historyTab);
        actionBar.addTab(calendarTab);
        actionBar.addTab(locationTab);
        actionBar.addTab(websiteTab);
    }

    // Launch preference menu
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.optionalmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void launchOptionActivity(String destination) {

        try {
            Intent launchIntent = new Intent("com.app.gptalk.option." + destination);
            launchIntent.putExtra("username", patientUsername);
            launchIntent.putExtra("user_type", "patient");
            startActivity(launchIntent);
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }
    }

    // Disable default back button functionality
    @Override
    public void onBackPressed() { }

    // Launch preference menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.optionLaunch:
                return true;

            case R.id.optionHome:
                Intent userHomepageIntent = new Intent("com.app.gptalk.homepage.patient.PATIENTHOMEPAGE");
                userHomepageIntent.putExtra("username", patientUsername);
                startActivity(userHomepageIntent);
                return true;

            case R.id.optionAccount:
                launchOptionActivity("OPTIONPATIENTACCOUNT");
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
