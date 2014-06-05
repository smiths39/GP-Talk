package com.app.gptalk.homepage.patient;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.gptalk.custom_adapters.CustomCalendarAdapter;
import com.app.gptalk.database.GPDatabaseHelper;
import com.app.gptalk.R;
import com.app.gptalk.base.BaseClass;
import com.app.gptalk.database.PatientDatabaseHelper;
import com.app.gptalk.homepage.both.CalendarService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;

public class PatientFragmentCalendar extends Fragment {

    private GregorianCalendar month, itemMonth;
    private TextView tvTitle, tvCalendarTitle;
    private LinearLayout calendarLinearLayout;
    private RelativeLayout previousMonth, nextMonth;
    private GridView gridView;
    private CustomCalendarAdapter adapter;
    private Handler handler;
    private View view;

    private ArrayList<String> eventMarkers, event, date, patientName, consultationTime;
    private ArrayList<String> calendarEventFirstName = new ArrayList<String>();
    private ArrayList<String> calendarEventLastName = new ArrayList<String>();
    private ArrayList<String> calendarEventBookingDate = new ArrayList<String>();
    private ArrayList<String> calendarEventBookingTime = new ArrayList<String>();

    private String firstName, lastName, bookDate, bookTime;
    private long eventCount = 0;

    private BaseClass baseClass = new BaseClass();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getFragmentManager().popBackStack();

        // Resolves recreating the same view when performing tab switching (avoiding application crash)
        if (view == null) {
            view = new View(getActivity());
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        view = inflater.inflate(R.layout.gp_calendar, container, false);

        Locale.setDefault(Locale.US);

        initialiseWidgets(view);

        PatientDatabaseHelper dbHelper = new PatientDatabaseHelper(PatientFragmentCalendar.this.getActivity());

        try {
            // Retrieve last row of consultations with confirmed status
            eventCount = dbHelper.getLastRowId("confirmed");
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }
        retrieveConsultationEventDetails(eventCount, dbHelper);
        dbHelper.close();

        // Get default timezone of GregorianCalendar
        month = (GregorianCalendar) GregorianCalendar.getInstance();
        itemMonth = (GregorianCalendar) month.clone();

        adapter = new CustomCalendarAdapter(PatientFragmentCalendar.this.getActivity(), month);
        gridView.setAdapter(adapter);

        // Handler used to send and process the runnable object
        handler = new Handler();
        handler.post(calendarUpdater);

        eventMarkers = new ArrayList<String>();

        // Format displayed date
        tvCalendarTitle.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

        // If previous month is selected
        previousMonth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View clickView) {

                setPreviousMonth();
                refreshCalendar();
            }
        });

        // If next month is selected
        nextMonth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View clickView) {

                setNextMonth();
                refreshCalendar();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View newView, int position, long id) {

                if ((calendarLinearLayout).getChildCount() > 0) {
                    calendarLinearLayout.removeAllViews();
                }

                initialiseArrayLists();

                ((CustomCalendarAdapter) adapterView.getAdapter()).setSelectedCell(newView);

                // Set new grid layout if before / after month is selected
                String selectedGridDate = CustomCalendarAdapter.dayList.get(position);
                String [] separatedTime = selectedGridDate.split("-");
                String gridValueString = separatedTime[2].replaceFirst("^0*", "");

                int gridValue = Integer.parseInt(gridValueString);

                // If grid column indicating previous / next month is pressed
                if ((gridValue > 10) && (position < 8)) {
                    setPreviousMonth();
                    refreshCalendar();
                } else if ((gridValue < 7) && (position > 28)) {
                    setNextMonth();
                    refreshCalendar();
                }

                ((CustomCalendarAdapter) adapterView.getAdapter()).setSelectedCell(newView);

                // Populate list with consultation details
                for (int index = 0; index < CalendarService.startDate.size(); index++) {

                    if (CalendarService.startDate.get(index).equals(selectedGridDate)) {
                        patientName.add(CalendarService.patientName.get(index));
                        consultationTime.add(CalendarService.consultationTime.get(index));
                    }
                }

                // Display all stored consultation details
                if (patientName.size() > 0 && consultationTime.size() > 0) {

                    for (int index = 0; index < patientName.size(); index++) {

                        TextView tvPatientName = new TextView(PatientFragmentCalendar.this.getActivity());
                        TextView tvConsultationTime = new TextView(PatientFragmentCalendar.this.getActivity());

                        tvPatientName.setText("GP: " + patientName.get(index));
                        tvConsultationTime.setText("Consultation Time: " + consultationTime.get(index));

                        tvPatientName.setTextColor(Color.BLACK);
                        tvConsultationTime.setTextColor(Color.BLACK);

                        calendarLinearLayout.addView(tvPatientName);
                        calendarLinearLayout.addView(tvConsultationTime);
                    }
                }
                patientName = null;
                consultationTime = null;
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialiseArrayLists() {

        date = new ArrayList<String>();
        patientName = new ArrayList<String>();
        consultationTime = new ArrayList<String>();
    }

    private void initialiseWidgets(View view) {

        calendarLinearLayout = (LinearLayout) view.findViewById(R.id.calendarLayout);
        previousMonth = (RelativeLayout) view.findViewById(R.id.previousMonth);
        nextMonth = (RelativeLayout) view.findViewById(R.id.nextMonth);
        gridView = (GridView) view.findViewById(R.id.calendarGridview);

        tvTitle = (TextView) view.findViewById(R.id.calendarTitle);
        tvCalendarTitle = (TextView) view.findViewById(R.id.calendarTitle);
    }

    // Extract details of all confirmed consultations and store in list
    private void retrieveConsultationEventDetails(long eventCount, PatientDatabaseHelper dbHelper) {

        try {
            for (int index = 1; index <= eventCount; index++) {

                firstName = dbHelper.getConsultationDetail(index, baseClass.confirmedStatus, dbHelper.KEY_GP_FIRST_NAME);
                lastName = dbHelper.getConsultationDetail(index, baseClass.confirmedStatus, dbHelper.KEY_GP_LAST_NAME);
                bookDate = dbHelper.getConsultationDetail(index, baseClass.confirmedStatus, dbHelper.KEY_BOOKING_DATE);
                bookTime = dbHelper.getConsultationDetail(index, baseClass.confirmedStatus, dbHelper.KEY_BOOKING_TIME);

                if (!firstName.equals("") || firstName.isEmpty()) {

                    calendarEventFirstName.add(firstName.toString());
                    calendarEventLastName.add(lastName.toString());
                    calendarEventBookingDate.add(bookDate.toString());
                    calendarEventBookingTime.add(bookTime.toString());
                }
            }
        } catch (Exception e) {
            baseClass.errorHandler(e);
        }
    }

    // Setting values of next month
    protected void setNextMonth() {

        if (month.get(GregorianCalendar.MONTH) == month.getActualMaximum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) + 1), month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH, month.get(GregorianCalendar.MONTH) + 1);
        }
    }

    // Setting values of previous month
    protected void setPreviousMonth() {

        if (month.get(GregorianCalendar.MONTH) == month.getActualMinimum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) - 1), month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH, month.get(GregorianCalendar.MONTH) - 1);
        }
    }

    // Get up-to-date calendar
    public void refreshCalendar() {

        adapter.refreshDays();
        adapter.notifyDataSetChanged();
        handler.post(calendarUpdater);

        tvTitle.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
    }

    // Clear all previous markers and reset with up-to-date event markers
    public Runnable calendarUpdater = new Runnable() {

        @Override
        public void run() {

            eventMarkers.clear();

            event = CalendarService.readCalendarEvent(calendarEventFirstName, calendarEventLastName, calendarEventBookingDate, calendarEventBookingTime);

            for (int index = 0; index < CalendarService.startDate.size(); index++) {

                itemMonth.add(GregorianCalendar.DATE, 1);
                eventMarkers.add(CalendarService.startDate.get(index).toString());
            }

            adapter.setMarkers(eventMarkers);
            adapter.notifyDataSetChanged();
        }
    };
}