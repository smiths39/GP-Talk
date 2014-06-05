package com.app.gptalk.custom_adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.gptalk.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class CustomCalendarAdapter extends BaseAdapter {

    private Context context;

    private java.util.Calendar month;
    private GregorianCalendar selectedDate;
    private ArrayList<String> items;
    private View previousView;

    public GregorianCalendar maximumPreviousMonthSet, previousMonth;
    public int firstDay, maxWeek, maxPreviousWeek, calculateMaxPreviousWeek, lastWeekDay, remainingDays, monthLength;
    public String itemValue, currentDateValue;
    public DateFormat formatter;

    public static List<String> dayList;

    public CustomCalendarAdapter(Context newContext, GregorianCalendar monthCalendar) {

        CustomCalendarAdapter.dayList = new ArrayList<String>();
        Locale.setDefault(Locale.US);

        month = monthCalendar;
        selectedDate = (GregorianCalendar) monthCalendar.clone();
        context = newContext;

        month.set(GregorianCalendar.DAY_OF_MONTH, 1);
        this.items = new ArrayList<String>();

        // Format set date according to selected month
        formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        currentDateValue = formatter.format(selectedDate.getTime());

        refreshDays();
    }

    // Set markers on all confirmed consultations dates
    public void setMarkers(ArrayList<String> items) {

        for (int index = 0; index != items.size(); index++) {

            if (items.get(index).length() == 1) {
                items.set(index, "0" + items.get(index));
            }
        }
        this.items = items;
    }

    public int getCount() {

        return dayList.size();
    }

    public Object getItem(int index) {

        return dayList.get(index);
    }

    public long getItemId(int index) {

        return 0;
    }

    public View getView(int index, View newView, ViewGroup viewGroup) {

        View view = newView;
        TextView tvDay;

        if (newView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.gp_calendar_item, null);
        }

        tvDay = (TextView) view.findViewById(R.id.tvCalendarDate);

        String[] separateTime = dayList.get(index).split("-");
        String gridValue = separateTime[2].replaceFirst("^0*", "");

        // Set previous / next month dates in grid cell on current date grid to white
        if ((Integer.parseInt(gridValue) > 1) && (index < firstDay)) {

            tvDay.setTextColor(Color.BLACK);
            tvDay.setClickable(false);
            tvDay.setFocusable(false);
        } else if ((Integer.parseInt(gridValue) < 7) && (index > 28)) {

            tvDay.setTextColor(Color.BLACK);
            tvDay.setClickable(false);
            tvDay.setFocusable(false);
        } else {

            // Set dates of current month in grid cell
            tvDay.setTextColor(Color.WHITE);
        }

        if (dayList.get(index).equals(currentDateValue)) {

            setSelectedCell(view);
            previousView = view;
        } else {
            view.setBackgroundResource(R.drawable.calendar_item_background);
        }
        tvDay.setText(gridValue);

        String date = dayList.get(index);

        if (date.length() == 1) {
            date = "0" + date;
        }

        String monthValue = "" + (month.get(GregorianCalendar.MONTH) + 1);

        if (monthValue.length() == 1) {
            monthValue = "0" + monthValue;
        }

        ImageView ivIcon = (ImageView) view.findViewById(R.id.date_icon);

        if (date.length() > 0 && items != null && items.contains(date)) {
            ivIcon.setVisibility(View.VISIBLE);
        } else {
            ivIcon.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    // If cell is selected, grid cell's background will change
    public View setSelectedCell(View view) {

        if (previousView != null) {
            previousView.setBackgroundResource(R.drawable.calendar_item_background);
        }

        previousView = view;
        view.setBackgroundResource(R.drawable.calendar_cell_select);

        return view;
    }

    public void refreshDays() {

        // Clear all previously stored events
        items.clear();
        dayList.clear();
        Locale.setDefault(Locale.US);

        previousMonth = (GregorianCalendar) month.clone();

        // Get first day of week of the previous month
        firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);

        // Get maximum value of the week in the previous month
        maxWeek = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
        monthLength = maxWeek * 7;

        maxPreviousWeek = getMaximumValue();
        calculateMaxPreviousWeek = maxPreviousWeek - (firstDay - 1);

        // Set correct dates of the selected month
        maximumPreviousMonthSet = (GregorianCalendar) previousMonth.clone();
        maximumPreviousMonthSet.set(GregorianCalendar.DAY_OF_MONTH, calculateMaxPreviousWeek + 1);

        // Add all available dates of the month
        for (int index = 0; index < monthLength; index++) {

            itemValue = formatter.format(maximumPreviousMonthSet.getTime());
            maximumPreviousMonthSet.add(GregorianCalendar.DATE, 1);
            dayList.add(itemValue);
        }
    }

    // Get maximum day value that the month could have
    private int getMaximumValue() {

        int maxValue;

        if (month.get(GregorianCalendar.MONTH) == month.getActualMinimum(GregorianCalendar.MONTH)) {
            previousMonth.set((month.get(GregorianCalendar.YEAR) - 1), month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            previousMonth.set(GregorianCalendar.MONTH, month.get(GregorianCalendar.MONTH) - 1);
        }
        maxValue = previousMonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        return maxValue;
    }
}
