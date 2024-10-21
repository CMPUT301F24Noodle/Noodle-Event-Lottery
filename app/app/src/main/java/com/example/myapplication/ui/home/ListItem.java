package com.example.myapplication.ui.home;

public class ListItem {
    private String heading;
    private String subheading1;
    private String subheading2;
    private String subheading3;

    public ListItem(String heading, String subheading1, String subheading2, String subheading3) {
        this.heading = heading;
        this.subheading1 = subheading1;
        this.subheading2 = subheading2;
        this.subheading3 = subheading3;
    }

    public String getHeading() {
        return heading;
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
}
