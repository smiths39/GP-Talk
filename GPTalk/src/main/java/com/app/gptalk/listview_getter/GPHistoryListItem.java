package com.app.gptalk.listview_getter;

public class GPHistoryListItem {

    private String bookingDate, bookingTime, bookingStatus;

    public GPHistoryListItem(String bookingDate, String bookingTime, String bookingStatus) {

        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.bookingStatus = bookingStatus;
    }

    public String getBookingDate() { return bookingDate; }
    public String getBookingTime() { return bookingTime; }
    public String getBookingStatus() { return bookingStatus; }

    @Override
    public String toString() {
        return bookingDate + " " + bookingTime + " " + bookingStatus;
    }
}