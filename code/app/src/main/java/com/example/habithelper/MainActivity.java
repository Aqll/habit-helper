package com.example.habithelper;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public FloatingActionButton floatingActionButton;

    FirebaseFirestore db;
//    Intent loginIntent;

    //This should only be used for the collectUserData method
    private User userToReturn;
    private Boolean getUserDone = false;
    //This can be used for other purposes
    User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
//        loginIntent = new Intent(this, LoginActivity.class);

        //Initialize the database
        db = FirebaseFirestore.getInstance();
        final CollectionReference userCollectionReference = db.collection("Users");


//        //If the user has not been logged in yet
//        if (!intent.hasExtra("currentUser")){
//            //Open up the login screen
//            startActivity(loginIntent);
//        }
//        //if a user has been logged in, access their information
//        else{
//            FirebaseUser user = (FirebaseUser) intent.getExtras().get("currentUser");
//            if (user != null) {
//                //All data will be attached to the user's email
//                String email = user.getEmail();
//                collectUserData(email);
//            }else{
//                throw new NullPointerException("There is no FirebaseUser!");
//            }
//        }

        FirebaseUser user = (FirebaseUser) intent.getExtras().get("currentUser");
        if (user != null) {
            //All data will be attached to the user's email
            String email = user.getEmail();
            collectUserData(email);
        }else{
            throw new NullPointerException("There is no FirebaseUser!");
        }


        setUpInterface();

    }

    /**
     * Get the document information from the DB on the user passed to the function as an email
     * And convert it into a user object
     * @param email
     * @return
     */
    public void collectUserData(String email){
        DocumentReference docRef = db.collection("Users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    User newUser = null;
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            newUser = new User(document);
                        } else {
                            throw new RuntimeException("Invalid Firestore ID Given");
                        }
                    } else {
                        throw new RuntimeException("Firestore retrieval failed");
                    }
                    afterUserLoad(newUser);
                }
            });
        return;
    }

    /**
     * Everything to be done after the user has logged in should go here
     * This will only happen after a user has been loaded in
     * @param newUser
     */
    public void afterUserLoad(User newUser){
        //Ensure we do actually have a user
        if (newUser == null){
            throw new NullPointerException("There is no user logged in!");
        }
    }

    /**
     * Set up the bottom interface bar
     */
    public void setUpInterface(){
        //NOTE: What does this do
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        //Draw a notification badge over the friends icon
        bottomNavigationView.setBackground(null);
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.friends_fragment);
        badge.setVisible(true);
        badge.setNumber(10);
        badge.setBadgeGravity(BadgeDrawable.TOP_END);

        //Set the floating action button to open up new habit
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateHabitActivity.class);
                startActivity(intent);
            }
        });
    }
}