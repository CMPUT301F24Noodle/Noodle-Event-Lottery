
# Class
| Reponsibilities | Collaborators |
| --- | --- |
|  |  |




# UserProfile
| Reponsibilities | Collaborators |
| --- | --- |
| - have attributes for all of a users profile information <br> - have getters and setters for all of a users profile info  <br>  - keep track of what app permissions a user has  <br> - have a method to generate a profile picture | - Every instantiation of the Event class must have one associated UserProfile user who organized it  <br> - Every institation of the Event class can have 0..* users it is associated with, who are the entrants  <br> - When MainActivity is launched on a device, there is one UserProfile that should be fetched and represents the current user of that device  <br> - CreateProfileFragment class will create an instance of the UserProfile class |


# Class
| Reponsibilities | Collaborators |
| --- | --- |
|- constructs the dialog that appears when the user enters the app for the first time, and it prompted to fill out some profile information <br> - take in the users input and create a new instance of the UserProfile class, and adds the profile to firebase | - creates a UserProfile object <br> - will have an associated view create_profile_fragment that contains the XML to format the dialog |