package com.app.gptalk.homepage.gp;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;
import com.app.gptalk.fragment_adapter.GPFragmentAdapter;

public class GPHomepage extends FragmentActivity {

    private ActionBar actionBar;
    private ViewPager viewPager;

    private BaseClass baseClass = new BaseClass();
    private String gpUsername;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.gp_homepage);

        gpUsername = baseClass.retrieveUsername(null, GPHomepage.this);

        actionBar = getActionBar();
        viewPager = (ViewPager) findViewById(R.id.gpPager);
        initialiseActionBarSettings(actionBar, viewPager);
    }

    private void initialiseActionBarSettings(final ActionBar newActionBar, ViewPager newViewPager) {

        // Setting up the action bar to show tabs
        newActionBar.setDisplayHomeAsUpEnabled(true);
        newActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Recognise which tab was selected
        ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                super.onPageSelected(position);
                newActionBar.setSelectedNavigationItem(position);
            }
        };

        newViewPager.setOnPageChangeListener(pageChangeListener);

        // Launch fragment corresponding to tab selection
        GPFragmentAdapter fragmentGPAdapter = new GPFragmentAdapter(fragmentManager);

        newViewPager.setAdapter(fragmentGPAdapter);
        newActionBar.setDisplayShowTitleEnabled(true);

        // Initialise fragment listener
        setActionBarTabListeners(newViewPager);
    }

    public void switchFragmentTab(int selection) {

        viewPager.setCurrentItem(selection);
    }

    private void setActionBarTabListeners(final ViewPager newViewPager) {

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction arg1) {

                newViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

            }
        };

        addActionBarTabs(tabListener);
    }

    private void addActionBarTabs(ActionBar.TabListener tabListener) {

        // Define all tabs in viewpager
        ActionBar.Tab homePageTab = actionBar.newTab()
                .setText(" Home")
                .setIcon(R.drawable.home_tab)
                .setTabListener(tabListener);

        ActionBar.Tab scheduleTab = actionBar.newTab()
                .setText(" Schedule")
                .setIcon(R.drawable.consultations_tab)
                .setTabListener(tabListener);

        ActionBar.Tab historyTab = actionBar.newTab()
                .setText(" History ")
                .setIcon(R.drawable.history_tab)
                .setTabListener(tabListener);

        ActionBar.Tab gpCalendarTab = actionBar.newTab()
                .setText(" Calendar")
                .setIcon(R.drawable.calendar_tab)
                .setTabListener(tabListener);

        actionBar.addTab(homePageTab);
        actionBar.addTab(scheduleTab);
        actionBar.addTab(historyTab);
        actionBar.addTab(gpCalendarTab);
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
            launchIntent.putExtra("username", gpUsername);
            launchIntent.putExtra("user_type", "gp");
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
                Intent userHomepageIntent = new Intent("com.app.gptalk.homepage.gp.GPHOMEPAGE");
                userHomepageIntent.putExtra("username", gpUsername);
                startActivity(userHomepageIntent);
                return true;

            case R.id.optionAccount:
                launchOptionActivity("OPTIONGPACCOUNT");
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
