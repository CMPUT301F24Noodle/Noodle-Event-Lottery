package com.example.myapplication.objects.facilityClasses;

import com.example.myapplication.objects.userProfileClasses.UserProfile;
import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;

/**
 * Author: Erin-Marie
 * Facility Class contains all information about a facility
 * An organizer can only own one Facility, a Facility can only have one owner. Only the owner of that facility can organize events at the facility
 */
public class Facility implements Serializable {

    //QUESTION: do we need an attribute to mark a facility as unique? a facility cannot be created twice, is that just going to be based on facility name? or address? or both?
    UserProfile owner; //UserProfile instance of the facility creator
    String facilityName; //Facility name
    String location; //Facility address
    String ownerID;
    //QUESTION: do we need an array for the events being hosted at the facility? we don't need to be able to filter by facility to display events, so I don't think so?


    public Facility(){}
    /**
     * Erin-Marie
     * Constructor for Facility Class
     *
     * @param facilityName the name of the new facility
     * @param location     the address of the facility
     * @param owner
     */
    public Facility(String facilityName, String location, UserProfile owner) {
        this.facilityName = facilityName;
        this.ownerID = owner.getUuid();
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    //TODO: update facility db
    public void setLocation(String location) {
        this.location = location;
    }

    public String getFacilityName() {
        return facilityName;
    }

    //TODO: update facility db
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    //QUESTION: there needs to be a function somewhere that checks if a facility already exists before creating it, but I think that will need to be within the CreateFacilityFragment

}
