package com.example.myapplication.ui.metrics;

import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.UserProfile;
import com.example.myapplication.database.EventDB;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Helper class to provide various metrics related to events, users, and their performance.
 */
public class MetricsHelper {
    /**
     * Author Apoorv
     * Returns the total number of events the user has entered in their lifetime.
     * @param user current user
     * @param eventDB the EventDB instance to access event data
     * @return the number of events the user has entered in their lifetime
     */
    public int getNumberOfLiftimeEvents(UserProfile user, EventDB eventDB) {
        ArrayList<Event> myEvents = eventDB.getUserEnteredEvents(user);
        if (myEvents != null) { return myEvents.size();}
        else { return 0;}
    }

    /**
     * Author Apoorv
     * Returns the total number of events the user has organized.
     * @param user current user
     * @param eventDB the EventDB instance to access event data
     * @return the number of events the user has organized
     */
    public int getNumberOfCreatedEvents(UserProfile user, EventDB eventDB) {
        ArrayList<Event> myEvents = eventDB.getUserOrgEvents(user);
        if (myEvents != null) { return myEvents.size();}
        else { return 0;}
    }

    /**
     * Author Apoorv
     * Calculates the success rate of the user based on the number of events they have entered and won.
     * @param user current user
     * @param eventDB the EventDB instance to access event data
     * @return the success rate as a percentage of events entered vs. won
     */
    public double getSuccessRate(UserProfile user, EventDB eventDB){
        ArrayList<Event> myEvents = eventDB.getUserEnteredEvents(user);
        ArrayList<Event> wonEvents = eventDB.getUserWinnerEvents(user);
        if (myEvents != null && wonEvents != null && myEvents.size() > 0) {
            // Calculate the success rate
            double successRate = (double) wonEvents.size() / myEvents.size() * 100;
            return successRate;}
        else {
            // Return 0% if the user hasn't entered any events or any events are missing
            return 0;
        }
    }

    /**
     * Author Apoorv
     * Returns the total number of entrants across all events the user has organized.
     * @param user current user
     * @param eventDB the EventDB instance to access event data
     * @return the total number of entrants in all the events organized by the user
     */
    public static int getTotalEntrants(UserProfile user, EventDB eventDB) {
        int totalEntrants = 0;
        ArrayList<Event> myEvents = eventDB.getUserOrgEvents(user);

        for (Event event : myEvents) {
            ArrayList<DocumentReference> entrantsList = event.getEntrantsList();
            ArrayList<DocumentReference> winnersList = event.getWinnersList();
            ArrayList<DocumentReference> declineList = event.getDeclinedList();

            if (entrantsList != null) {
                totalEntrants += entrantsList.size();
            }
            if (winnersList != null){
                totalEntrants += winnersList.size();
            }
            if (declineList != null){
                totalEntrants += declineList.size();
            }
        }
        return totalEntrants;
    }

    /**
     * Author Apoorv
     * Returns the total number of winners across all events the user has organized.
     * @param user current user
     * @param eventDB the EventDB instance to access event data
     * @return the total number of winners in all the events organized by the user
     */
    public static int getTotalWinners(UserProfile user, EventDB eventDB) {
        int totalWinners = 0;
        ArrayList<Event> myEvents = eventDB.getUserOrgEvents(user);

        for (Event event : myEvents) {
            ArrayList<DocumentReference> winnersList = event.getWinnersList();
            if (winnersList != null) {
                totalWinners += winnersList.size();
            }
        }
        return totalWinners;
    }

    /**
     * Author Apoorv
     * Calculates the average number of entrants across all events the user has organized.
     * @param user current user
     * @param eventDB the EventDB instance to access event data
     * @return the average number of entrants per event organized by the user
     */
    public static double getAverageEntrants(UserProfile user, EventDB eventDB) {
        double averageEntrants = 0;
        ArrayList<Event> myEvents = eventDB.getUserOrgEvents(user);
        int events = myEvents.size();

        for (Event event : myEvents) {
            ArrayList<DocumentReference> entrantList = event.getEntrantsList();
            ArrayList<DocumentReference> winnersList = event.getWinnersList();
            ArrayList<DocumentReference> declineList = event.getDeclinedList();
            if (entrantList != null) {
                averageEntrants += entrantList.size();
            }
            if (winnersList != null){
                averageEntrants += winnersList.size();
            }
            if (declineList != null){
                averageEntrants += declineList.size();
            }
        }
        averageEntrants = averageEntrants/events;
        return averageEntrants;
    }

    /**
     * Author Apoorv
     * Calculates the average number of declines (both declined and losers) across all events the user has organized.
     * @param user current user
     * @param eventDB the EventDB instance to access event data
     * @return the average number of declines per event organized by the user
     */
    public static double getAverageDeclines(UserProfile user, EventDB eventDB) {
        double averageDeclines = 0;
        ArrayList<Event> myEvents = eventDB.getUserOrgEvents(user);
        int events = myEvents.size();

        for (Event event : myEvents) {
            ArrayList<DocumentReference> declineList = event.getDeclinedList();
            ArrayList<DocumentReference> loserList = event.getLosersList();
            if (declineList != null || loserList != null) {
                averageDeclines += declineList.size() + loserList.size();
            }
        }
        averageDeclines = averageDeclines/events;
        return averageDeclines;
    }

    /**
     * Author Apoorv
     * Calculates the average decline rate (percentage of declines and losers) per event organized by the user.
     * @param user current user
     * @param eventDB the EventDB instance to access event data
     * @return the average decline rate as a percentage across all events organized by the user
     */
    public static double getDeclineRate(UserProfile user, EventDB eventDB) {
        double totalDeclineRate = 0;
        ArrayList<Event> myEvents = eventDB.getUserOrgEvents(user);
        int events = myEvents.size();

        for (Event event : myEvents) {
            ArrayList<DocumentReference> declineList = event.getDeclinedList();
            ArrayList<DocumentReference> loserList = event.getLosersList();
            ArrayList<DocumentReference> entrantsList = event.getEntrantsList();
            if (entrantsList != null && !entrantsList.isEmpty()) {
                // Sum the number of declines
                int totalDeclines = 0;
                if (declineList != null) totalDeclines += declineList.size();
                if (loserList != null) totalDeclines += loserList.size();

                // Calculate the decline rate for this event
                double eventDeclineRate = (double) totalDeclines / (entrantsList.size()+totalDeclines);
                totalDeclineRate += eventDeclineRate;
            }
        }
        // Calculate the average decline rate across all events
        double averageDeclineRate = (events > 0) ? (totalDeclineRate / events) * 100 : 0; // Convert to percentage
        return averageDeclineRate;
    }

    /**
     * Author Apoorv
     * Calculates the pasta ratio for the user based on their name similarity to a list of pasta names.
     * @param user current user
     * @return the highest similarity percentage between the user's name and a list of pasta names
     */
        public double calculatePastaRatio(UserProfile user) {
        String fullName = user.getName();
        if (fullName == null){
            return 0.0;
        }

        //Making a list of noodle names
        ArrayList<String> noodleNames = new ArrayList<>(Arrays.asList(
                "Spaghetti", "Macaroni", "Fettuccine", "Linguine", "Penne", "Rigatoni",
                "Rotini", "Orzo", "Fusilli", "Tagliatelle", "Ravioli", "Tortellini",
                "Cavatappi", "Capellini", "Ziti", "Lasagna", "Gnocchi", "Bucatini", "Vermicelli",
                "Manicotti", "Ditalini", "Mafaldine", "Cavatelli", "Pappardelle", "Cannelloni",
                "Orecchiette", "Strozzapreti", "Fagottini", "Fiori", "Sagnarelli", "Trofie",
                "Gigli", "Pici", "Penne rigate", "Casarecce", "Margherita", "Baked Ziti",
                "Fregola", "Chitarra", "Soba", "Udon", "Ramen", "Pho", "Mie", "Laksa", "Bánh phở",
                "Jajangmyeon", "Wonton", "Egg noodles", "Chow Mein", "Lo Mein", "Siu Mai"
        ));

        // Variable to track the highest similarity percentage
        double highestPercentage = 0.0;

        // Loop through each noodle name and calculate the similarity percentage
        for (String noodle : noodleNames) {
            double similarity = calculateSimilarity(fullName, noodle);
            if (similarity > highestPercentage) {
                highestPercentage = similarity;
            }
        }

        return highestPercentage; // Return the highest pasta ratio
    }

    // Function to calculate similarity using Levenshtein distance
    private double calculateSimilarity(String str1, String str2) {
        int distance = getLevenshteinDistance(str1.toLowerCase(), str2.toLowerCase());
        int maxLength = Math.max(str1.length(), str2.length());
        return (1.0 - (double) distance / maxLength) * 100.0; // Return similarity as percentage
    }

    // Function to compute the Levenshtein distance between two strings
    private int getLevenshteinDistance(String str1, String str2) {
        int lenStr1 = str1.length();
        int lenStr2 = str2.length();
        int[][] dp = new int[lenStr1 + 1][lenStr2 + 1];

        for (int i = 0; i <= lenStr1; i++) {
            for (int j = 0; j <= lenStr2; j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + (str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1));
                }
            }
        }
        return dp[lenStr1][lenStr2]; // Return the Levenshtein distance
    }

}
