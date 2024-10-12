
# Class
| Reponsibilities | Collaborators |
| --- | --- |
|  |  |



# UserProfile
| Reponsibilities | Collaborators |
| --- | --- |
| - have attributes for all of a users profile information <br> - have getters and setters for all of a users profile info <br> - keep track of what app permissions a user has  <br> - have a method to generate a profile picture | - Every instantiation of the Event class must have one associated UserProfile user who organized it <br> - Every institation of the Event class can have 0..* users it is associated with, who are the entrants <br> - When MainActivity is launched on a device, there is one UserProfile that should be fetched and represents the current user of that device  <br> - CreateProfileFragment class will create an instance of the UserProfile class |


# CreateProfileFragment
| Reponsibilities | Collaborators |
| --- | --- |
|- constructs the dialog that appears when the user enters the app for the first time, and it prompted to fill out some profile information <br> - take in the users input and create a new instance of the UserProfile class, and adds the profile to firebase | - creates a UserProfile object <br> - will have an associated view create_profile_fragment that contains the XML to format the dialog |

# EditProfileFragment
| Reponsibilities | Collaborators |
| --- | --- |
| - constructs the dialog that appears when the user wants to edit their profile information <br> - take in the users input and updates the given instance of the UserProfile class <br> - allow a user to delete their profile, and must have a method to carry out that deletion in firebase | - accesses a UserProfile object <br> - will have an associated view edit_profile_fragment that contains the XML to format the dialog |

# Event
| Reponsibilities | Collaborators |
| --- | --- |
| - have attributes for all event information, including the instance of UserProfile who organized it <br> - have getters and setters for all event info and entrant lists <br> - method to end lottery, which will need methods to select winners, notify winners, notify losers <br> - method that responds to a declined invitation and picks a new winner | - Every instantiation of the Event class must have one associated UserProfile who organized it <br> - Every institation of the Event class can have 0..* users it is associated with, who are the entrants <br> -  Every instantiation of the Event class must have one associated Facility object where it is being hosted |

# CreateEventFragment
| Reponsibilities | Collaborators |
| --- | --- |
| - constructs the dialog that appears for the user to enter their events information <br> - take in the users input and creates a new instance of the Event class, and adds it to firebase <br> - creates a QR code | - creates an Event object <br> - will have an associated view create_event_fragment that contains the XML to format the dialog |

# EditEventFragment
| Reponsibilities | Collaborators |
| --- | --- |
| - constructs the dialog that appears when the user wants to edit their event information <br> - take in the users input and updates the given instance of the Event class <br> - allow a user to delete their event, and must have a method to carry out that deletion in firebase | - accesses an Event object <br> - will have an associated view edit_event_fragment that contains the XML to format the dialog <br> - Will be accessed when a user selects an event from list displayed by MyEventListArrayAdapter class, or EventListArrayAdapter class |

# SendNotificationFragment
| Reponsibilities | Collaborators |
| --- | --- |
| - constructs the dialog that appears when an organizer wants to construct a message to send to an entrant <br> - take in the users input of a string message, and an entrant they select | - accesses 2 UserProfile objects, an entrant recipient and an organizer sender <br> - is triggered by user response to the AttendeesListArrayAdapter class and EntrantsListArrayAdapter class <br> - will have an associated view send_notif_fragment that contains the XML to format the dialog |

# Facility
| Reponsibilities | Collaborators |
| --- | --- |
| - facility must be unique and "owned" by one organizer <br> - if an event is deleted, all events hosted by that facility must also be deleted | - a user with admin privileges can delete a facility, can select facility from list displayed by FacilityListAdapter class <br> - Every instantiation of the Event class must have one associated Facility object where it is being hosted, a facility can have 0..* events |
