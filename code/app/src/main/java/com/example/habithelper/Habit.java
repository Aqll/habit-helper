/*
Copyright 2021 CMPUT301F21T10

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.example.habithelper;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is a class that represents a Habit object
 * */
public class Habit implements Serializable {

    private String title;
    private String reason;
    private String dateStarted;
    private Boolean publicStatus;

    /**
     * This constructor takes in data from the database and converts it into a Habit object
     * @param doc
     *  the data pulled from the DB document
     */
    public Habit(DocumentSnapshot doc) {
        ArrayList<String> DBData = new ArrayList<>();

        this.title = (String) doc.get("habit_title");
        this.reason = (String) doc.get("habit_reason");
        this.dateStarted = (String) doc.get("habit_date");

        String publicStatus_str = (String) doc.get("publicStatus");
        if (publicStatus_str == "true"){
            this.publicStatus = true;
        }
        else{
            this.publicStatus = false;
        }

    }

    /**
     * Empty constructor for the Habit class
     * */
    public Habit() {
    }

    /**
     * Constructor for the Habit class that has all the attributes as parameters
     * @param title
     * The habit title/name
     * @param reason
     * The reason of doing the habit
     * @param dateStarted
     * The date the habit started
     * @param publicStatus
     * Denotes whether the habit is public or not
     */
    public Habit(String title, String reason, String dateStarted, Boolean publicStatus) {
        this.title = title;
        this.reason = reason;
        this.dateStarted = dateStarted;
        this.publicStatus = publicStatus;
    }

    /**
     * Constructor for Habit class, with only title as parameter
     * @param title
     * The habit title/name
     */
    public Habit(String title) {
        this.title = title;
    }

    /**
     * Returns the title of a habit object
     * @return
     * title of habit
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title of a habit object to title
     * @param title
     * title of habit
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the reason of a habit object
     * @return
     * The reason of a habit
     */
    public String getReason() {
        return reason;
    }

    /**
     * Set the reason for a habit object
     * @param reason
     * reason of a habit
     *
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Get the date the habit started
     * @return
     * The date started of a habit
     */
    public String getDateStarted() {
        return dateStarted;
    }

    /**
     * Set the date started for a habit
     * @param dateStarted
     * The date started of a habit
     */
    public void setDateStarted(String dateStarted) {
        this.dateStarted = dateStarted;
    }

    /**
     * Returns true if the habit is public, false otherwise
     * @return
     * true if the habit is public, false otherwise
     */
    public Boolean getPublicStatus() {
        return publicStatus;
    }

    /**
     * Set the privacy of a habit
     * @param publicStatus
     * true if habit is public, false is habit is private
     */
    public void setPublicStatus(Boolean publicStatus) {
        this.publicStatus = publicStatus;
    }



    /**
     * Translate all the data into a form usable by the DB
     * @return
     * a hashmap of all the data for this habit
     */
    public HashMap<String, String> generateAllHabitDBData(){
        HashMap<String, String> newHabitData = new HashMap<>();
        newHabitData.put("habit_title", this.title);
        newHabitData.put("habit_reason", this.reason);
        newHabitData.put("habit_date", this.dateStarted);
        newHabitData.put("publicStatus", this.publicStatus.toString());

        return newHabitData;
    }


    /**
     * Add a habit object to the database
     * @param email
     *      the email of the user for which the habit is to be added
     * @param db
     *      the firestore instance
     */
    public void addHabitToDB(String email, FirebaseFirestore db){
        DocumentReference docRefAdd = db.collection("Habits")
                .document(email)
                .collection(email + "_habits")
                .document(this.title);
        // write to db
        docRefAdd.set(this.generateAllHabitDBData())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // These are a method which gets executed when the task is succeeded
                        Log.d("DATA_ADDED", "Data has been added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // These are a method which gets executed if there’s any problem
                        Log.d(TAG, "Data could not be added!" + e.toString());
                    }
                });
    }

    /**
     *
     * @param email
     *      the email of the user for which the habit is to be deleted
     * @param db
     *      the firestore instance
     */
    public void deleteHabitFromDB(String email, FirebaseFirestore db){
        DocumentReference docRefDelete = db.collection("Habits")
                .document(email)
                .collection(email + "_habits")
                .document(this.title);

        docRefDelete.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("DELETED", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ERROR_DELETE", "Error deleting document", e);
                    }
                });
    }

}
