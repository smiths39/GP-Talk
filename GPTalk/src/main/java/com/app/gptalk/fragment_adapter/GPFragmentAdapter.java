package com.app.gptalk.fragment_adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.app.gptalk.homepage.both.FragmentHomepage;
import com.app.gptalk.homepage.gp.GPFragmentCalendar;
import com.app.gptalk.homepage.gp.GPFragmentHistory;
import com.app.gptalk.homepage.gp.GPFragmentSchedule;

public class GPFragmentAdapter extends FragmentStatePagerAdapter {

    // Representing the number of tabs present in ViewPager
    final int PAGE_COUNT = 4;

    public GPFragmentAdapter(FragmentManager fragmentAdapter) {

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
                GPFragmentSchedule schedule = new GPFragmentSchedule();
                return setFragmentArguments(schedule, data, id);

            case 2:
                GPFragmentHistory history = new GPFragmentHistory();
                return setFragmentArguments(history, data, id);

            case 3:
                GPFragmentCalendar calendar = new GPFragmentCalendar();
                return setFragmentArguments(calendar, data, id);
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