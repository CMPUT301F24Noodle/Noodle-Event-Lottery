

### MainActivity
| Reponsibilities | Collaborators |
| --- | --- |
| - Run the app <br> - onCreate will ensure deviceID is stored in firebase <br> - Cache the users deviceID and profile information <br> - have listeners for all interactive fragments | - almost all classes with collaborate directly or indirectly with MainActivity, either by a listener, an arrayadapter, or a view update |

### EntrantScannedEventFragment
| Reponsibilities | Collaborators |
| --- | --- |
| - Displays the event when a user scans an event QR code <br> - Gives entrant the option to enter the lottery for the event | - accesses an Event object <br> - will have an associated view view_event_fragment |


# Profile Classes
### UserProfile
| Reponsibilities | Collaborators |
| --- | --- |
| - have attributes for all of a users profile information <br> - have getters and setters for all of a users profile info <br> - keep track of what app permissions a user has  <br> - have a method to generate a profile picture | - Every instantiation of the Event class must have one associated UserProfile user who organized it <br> - Every institation of the Event class can have 0..* users it is associated with, who are the entrants <br> - When MainActivity is launched on a device, there is one UserProfile that should be fetched and represents the current user of that device  <br> - CreateProfileFragment class will create an instance of the UserProfile class |

### CreateProfileFragment
| Reponsibilities | Collaborators |
| --- | --- |
|- constructs the dialog that appears when the user enters the app for the first time, and it prompted to fill out some profile information <br> - take in the users input and create a new instance of the UserProfile class, and adds the profile to firebase | - creates a UserProfile object <br> - will have an associated view create_profile_fragment that contains the XML to format the dialog <br> - updates Firebase db |

### EditProfileFragment
| Reponsibilities | Collaborators |
| --- | --- |
| - constructs the dialog that appears when the user wants to edit their profile information <br> - take in the users input and updates the given instance of the UserProfile class <br> - allow a user to delete their profile, and must have a method to carry out that deletion in firebase | - accesses a UserProfile object <br> - will have an associated view edit_profile_fragment that contains the XML to format the dialog <br> - updates Firebase db |


# Organizers Event Creation Class
### Facility
| Reponsibilities | Collaborators |
| --- | --- |
| - facility must be unique and "owned" by one and only one organizer <br> - if a facility is deleted, all events hosted by that facility must also be deleted | - a user with admin privileges can delete a facility, can select facility from list displayed by FacilityListAdapter class <br> - Every instantiation of the Event class must have one associated Facility object where it is being hosted, a facility can have 0..* events |

### CreateFacilityFragment
| Reponsibilities | Collaborators |
| --- | --- |
| - constructs the dialog for a user to create a Facility <br> - facility cannot already be created <br> - user assumes organizer privilges once they create a facility | - creates a Facility object <br> - updates Firebase db |

### Event
| Reponsibilities | Collaborators |
| --- | --- |
| - have attributes for all event information, including the instance of UserProfile who organized it <br> - have getters and setters for all event info and entrant lists <br> - method to end lottery, which will need methods to select winners, notify winners, notify losers <br> - method that responds to a declined invitation and picks a new winner | - Every instantiation of the Event class must have one associated UserProfile who organized it <br> - Every institation of the Event class can have 0..* users it is associated with, who are the entrants <br> -  Every instantiation of the Event class must have one associated Facility object where it is being hosted |

### CreateEventFragment
| Reponsibilities | Collaborators |
| --- | --- |
| - constructs the dialog that appears for the user to enter their events information <br> - take in the users input and creates a new instance of the Event class, and adds it to firebase <br> - creates a QR code | - creates an Event object <br> - will have an associated view create_event_fragment that contains the XML to format the dialog <br> - updates Firebase db |

### EditEventFragment
| Reponsibilities | Collaborators |
| --- | --- |
| - constructs the dialog that appears when the user wants to edit their event information <br> - take in the users input and updates the given instance of the Event class <br> - allow a user to delete their event, and must have a method to carry out that deletion in firebase | - accesses an Event object <br> - will have an associated view edit_event_fragment that contains the XML to format the dialog <br> - Will be accessed when a user selects an event from list displayed by MyEventListArrayAdapter class, or EventListArrayAdapter class <br> - updates Firebase db |


# Organizer Event Listing Classes
### MyOrganizedEventsFragment
| Reponsibilities | Collaborators |
| --- | --- |
| - display the list of all events the current user has organized <br> - all events are selectable so the organzier can view the waiting list or attendee list | AttendeeListArrayAdapter, WaitingListArrayAdapter,  MyOrganizedEventsListArrayAdapter |

### MyOrganizedEventsListArrayAdapter
| Reponsibilities | Collaborators |
| --- | --- |
| - adapter for the list of all events the current user has organized <br> | - used by MainActivity <br> - will have an associated view edit_event_fragment <br> |

### WaitingListArrayAdapter
| Reponsibilities | Collaborators |
| --- | --- |
| - adapter for the list of entrants for one event <br> only the organizer can view the list of entrants for their event <br>  | - will collaborate with a view to display the array of entrants <br> |

### AttendeeListArrayAdapter
| Reponsibilities | Collaborators |
| --- | --- |
| - adapter for the list of chosen entrants for one event <br> only the organizer can view the list of entrants for their event <br> - entrants must be selectable, so the organizer can remove them if they want to <br> - will also display whether the attendee has accepted their invitation or not |- used by MainActivity <br> - will need to be updated everytime a attendee accepts or declines an invitation <br> - will have a view to format it's display <br> - if the organzier decides to remove an attendee, a method of the event class must be called to select a replacement |


# Entrant Event Listing Classes
### MyEnteredEventsFragment
| Reponsibilities | Collaborators |
| --- | --- |
| - display the list of all events current user has entered the lottery for <br> - will include entry status for each event <br> - each event is selectable so the user could unjoin if they want | - used by MainActivity <br> - will have an associated view to format display <br> this will be the home page|

### MyEnteredEventsListArrayAdapter
| Reponsibilities | Collaborators |
| --- | --- |
| - adapter for the list of all events current user has entered the lottery for <br>  | - used by MainActivity <br> - will access each Event object <br> |


# Notifcation Classes
### Notification
| Reponsibilities | Collaborators |
| --- | --- |
| - Knows content of message, reciepient and sender | - instantiated by a SendNotificationFragment |

### MyNotificationsFragment
| Reponsibilities | Collaborators |
| --- | --- |
| - display a users list of notifications | - MyNotificationsArrayAdapter |

### SendNotificationFragment
| Reponsibilities | Collaborators |
| --- | --- |
| - fragment for sending a notifcation - constructs the dialog that appears when an organizer wants to send to entrant(s) <br> - take in the users input of a string message, and entrant(s) they select - creates Notification object | - used by MainActivity - accesses 2..* UserProfile objects, entrant recipient(s) and an organizer sender <br> - will have an associated view send_notif_fragment that formats the dialog |

### MyNotificationsArrayAdapter
| Reponsibilities | Collaborators |
| --- | --- |
|- Adapter for the list of notifcations a user has received | - used by MainActivity <br> - will access each Notification object for that user |


# Admin Privlege Classes
### AllUsersArrayAdapter
| Reponsibilities | Collaborators |
| --- | --- |
| - adapter for the list of all app users <br> - available only to admin users <br> | - used by MainActivity <br> - will access UserProfile objects |

### AllImagesArrayAdapter
| Reponsibilities | Collaborators |
| --- | --- |
| - adapter for the list of all Event poster images <br> - available only to admin users <br> | - used by MainActivity <br> - will access Event objects |

### FacilitiesArrayAdapter
| Reponsibilities | Collaborators |
| --- | --- |
| - adapter for the list of all Facilities <br> - available only to admin users <br> | - used by MainActivity <br> - will access Facility objects |

### AllEventListArrayAdapter
| Reponsibilities | Collaborators |
| --- | --- |
| - adapter for the list of all Events <br> - available only to admin users <br> |- used by MainActivity <br> - will access Event objects |

