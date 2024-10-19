package com.example.myapplication.objects;

/**
 * Author: Erin-Marie
 * Facility Class contains all information about a facility
 * An organizer can only own one Facility, a Facility can only have one owner. Only the owner of that facility can organize events at the facility
 */
public class Facility {

    //QUESTION: do we need an attribute to mark a facility as unique? a facility cannot be created twice, is that just going to be based on facility name? or address? or both?
    UserProfile owner; //UserProfile instance of the facility creator
    String facilityName; //Facility name
    String location; //Facility address
    //QUESTION: do we need an array for the events being hosted at the facility? we don't need to be able to filter by facility to display events, so I don't think so?

    //Class constructor
    public Facility(String facilityName, UserProfile owner, String location) {
        this.facilityName = facilityName;
        this.owner = owner;
        this.location = location;
    }

    public UserProfile getOwner() {
        return owner;
    }

    //QUESTION: should it be possible to set the owner of a facility to a different owner?
    public void setOwner(UserProfile owner) {
        this.owner = owner;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    //QUESTION: there needs to be a function somewhere that checks if a facility already exists before creating it, but I think that will need to be within the CreateFacilityFragment

}
