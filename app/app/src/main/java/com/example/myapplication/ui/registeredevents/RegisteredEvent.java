package com.example.myapplication.ui.registeredevents;


public class RegisteredEvent {
    private String event_name;
    private String subheading1;
    private String subheading2;
    private String subheading3;
    private String status;

    public RegisteredEvent(String heading, String status, String subheading1, String subheading2, String subheading3) {
        this.event_name = heading;
        this.subheading1 = subheading1;
        this.subheading2 = subheading2;
        this.subheading3 = subheading3;
        this.status = status;
    }

    public String getHeading() {
        return event_name;
    }

    public String getSubheading1() {
        return subheading1;
    }

    public String getSubheading2() {
        return subheading2;
    }

    public String getSubheading3() {
        return subheading3;
    }

    public String getheadStatus() {
        return event_name+":"+status;
    }
}
