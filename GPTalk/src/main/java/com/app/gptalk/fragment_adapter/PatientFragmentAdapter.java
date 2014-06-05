package com.app.gptalk.fragment_adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.app.gptalk.homepage.both.FragmentHomepage;
import com.app.gptalk.homepage.patient.PatientFragmentCalendar;
import com.app.gptalk.homepage.patient.PatientFragmentGPSessions;
import com.app.gptalk.homepage.patient.PatientFragmentGPLocations;
import com.app.gptalk.homepage.patient.PatientFragmentGPWebsites;
import com.app.gptalk.homepage.patient.PatientFragmentHistory;
import com.app.gptalk.homepage.patient.PatientFragmentNewBookings;

public class PatientFragmentAdapter extends FragmentStatePagerAdapter {

    // Representing the number of tabs present in ViewPager
    final int PAGE_COUNT = 7;

    public PatientFragmentAdapter(FragmentManager fragmentAdapter) {

        super(fragmentAdapter);
    }

    // Initiate selected tab on ViewPager
    @Override
    public Fragment getItem(int id) {

        Bundle data = new Bundle();

        switch (id) {

            case 0:
                FragmentHomepage homePage = new FragmentHomepage();
                return setFragmentArguments(homePage, data, id);

            case 1:
                PatientFragmentNewBookings newBookings = new PatientFragmentNewBookings();
                return setFragmentArguments(newBookings, data, id);

            case 2:
                PatientFragmentGPSessions consultations = new PatientFragmentGPSessions();
                return setFragmentArguments(consultations, data, id);

            case 3:
                PatientFragmentHistory history = new PatientFragmentHistory();
                return setFragmentArguments(history, data, id);

            case 4:
                PatientFragmentCalendar calendar = new PatientFragmentCalendar();
                return setFragmentArguments(calendar, data, id);

            case 5:
                PatientFragmentGPLocations gpLocations = new PatientFragmentGPLocations();
                return setFragmentArguments(gpLocations, data, id);

            case 6:
                PatientFragmentGPWebsites gpWebsites = new PatientFragmentGPWebsites();
                return setFragmentArguments(gpWebsites, data, id);
        }

        return null;
    }

    private Fragment setFragmentArguments(Fragment newFragment, Bundle newData, int newID) {

        newData.putInt("current_page", newID + 1);
        newFragment.setArguments(newData);
        return newFragment;
    }

    @Override
    public int getCount() {





        return PAGE_COUNT;
    }
}