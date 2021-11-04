package com.example.habithelper;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;


public class User {
    public String email;
    public String password;
    public String name;
    public ArrayList<Habit> habits;
    public ArrayList<String> followers;
    public ArrayList<String> following;
    public ArrayList<String> requests;

    /**
     * Basic constructor for when a user does not yet have an ID defined in the database
     * @param email
     *  User's email, used for sign in
     * @param password
     *  User's password, only used for user creation purposes
     *  Not actually stored anywhere in the DB
     * @param name
     *  The user's preferred name
     */
    public User(String name, String email, String password) {
        this.email = email;
        this.password = password;
        this.name = name;

        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.habits = new ArrayList<Habit>();
        this.requests = new ArrayList<>();
    }

    /**
     * This constructor takes in data from the database and converts it into a User object
     * @param doc
     *  the data pulled from the DB document
     */
    public User(DocumentSnapshot doc){

        ArrayList<String> DBData = new ArrayList<>();
        DBData = (ArrayList<String>) doc.get("UserData");
        //Log.d("GETTASK", "DBDATA: " + DBData.get(0));
        if (DBData.size() == 2){
            this.name = DBData.get(0);
            this.email = DBData.get(1);
        }else{
            this.name = "FAIL";
            this.email = "FAIL";
        }

        this.followers = (ArrayList<String>) doc.get("Followers");
        this.following = (ArrayList<String>) doc.get("Following");
        this.habits = new ArrayList<Habit>();
        this.requests = (ArrayList<String>) doc.get("Requests");

    }

    /**
     * Add a follower to this user's follower list
     * @param userEmail
     *  Email of a user following the current user
     */
    public void addFollower(String userEmail){
        this.followers.add(userEmail);
    }

    /**
     * Add a user this user is following to the following arraylist
     * @param userEmail
     *  Email of the user the current user is following
     */
    public void addFollowing(String userEmail){
        this.following.add(userEmail);
    }

    /**
     * Add a habit to this user's habit list
     * @param newHabit
     *  A new habit to attach to this user
     */
    public void addUserHabit(Habit newHabit){
        this.habits.add(newHabit);
    }

    /**
     * Add a follow request to this user
     * Follow request is sent from another user, to be accepted or rejected
     * @param userEmail
     *  The email of a user attempting to follow the current user
     */
    public void addRequest(String userEmail){
        this.requests.add(userEmail);
    }

    /**
     * Translate all the data into a form usable by the DB
     * @return a hashmap of all the data for this user
     */
    public HashMap<String, ArrayList<String>> generateAllDBData(){
        HashMap<String, ArrayList<String>> newUserData = new HashMap<>();
        newUserData.put("UserData", this.generateDBData());
        newUserData.put("Requests", this.generateRequestList());
        newUserData.put("Followers", this.generateFollowersList());
        newUserData.put("Following", this.generateFollowingList());
        //newUserData.put("Habits", this.generateHabitList());

        return newUserData;
    }
    /**
     * Format the User data in a way usable in Firestore
     * @return An array list with all the necessary data to construct a User object
     */
    public ArrayList<String> generateDBData(){
        ArrayList<String> DBData = new ArrayList<>();
        DBData.add(this.name);
        DBData.add(this.email);

        return DBData;
    }

    /**
     * Format the Request data in a way usable in Firestore
     * @return An array list of the requests to this user
     */
    public ArrayList<String> generateRequestList(){
        return this.requests;
    }

    /**
     * Format the followers data in a way usable in Firestore
     * @return An array list of the followers of this user
     */
    public ArrayList<String> generateFollowersList(){
        ArrayList<String> DBData = new ArrayList<>();
        DBData.add("Raj");
        DBData.add("Yevhen");
        DBData.add("Emily");

        return DBData;
    }

    /**
     * Format the following data in a way usable in Firestore
     * @return An array list of the user that user is following
     */
    public ArrayList<String> generateFollowingList(){

        return this.following;
    }


    /**
     * @param FollowingId: Id of the user who sent the follow request
     * Sends request by adding FollowingId to list of requests of the receiver's Requests list.
     */
    public void sendRequest(String FollowingId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(FollowingId);
        docRef.update("Requests", FieldValue.arrayUnion(FollowingId));
    }

    /**
     * @param FollowerId
     *  Id of the user who received the request
     * @param accept
     *  True if the current user is accepting the request, false otherwise
     */
    public void acceptRequest(String FollowerId, boolean accept){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(FollowerId);
        docRef.update("Requests", FieldValue.arrayRemove(FollowerId));
        if(accept){
            docRef.update("Following", FieldValue.arrayUnion(FollowerId));
            docRef.update("Followers", FieldValue.arrayUnion(this.email));
        }
    }

    /**
     * Get the list of followers for this user
     * @return an arraylist containing the emails of this user's followers
     */
    public ArrayList<String> getFollowers() {
        return followers;
    }

    /**
     * Count how many followers this user has
     * @return the integer count of followers
     */
    public Integer countFollowers(){
        return this.followers.size();
    }

    /**
     * Count how many other users this user is following
     * @return the integer count of users being followed
     */
    public Integer countFollowing(){
        return this.following.size();
    }
    /**
     * Count how many habits this user is associated with
     * @return the integer count of habits for this user
     */
    public Integer countHabits(){
        return this.habits.size();
    }
}