package com.app.gptalk.listview_getter;

public class GPAvailableTimeItem {

    private String time, status;

    public GPAvailableTimeItem(String time, String status) {

        this.time = time;
        this.status = status;
    }

    // Getter methods
    public String getTime() { return time; }
    public String getStatus() { return status; }
}
