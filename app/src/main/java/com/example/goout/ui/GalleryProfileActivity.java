package com.example.goout.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.goout.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GalleryProfileActivity extends AppCompatActivity {
    private static final String TAG = "GalleryProfileActivity";
    private String userId;
    private FirebaseAuth mAuth;
    private String currentUid;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_gallery);
        Log.d(TAG, "onCreate: Started");
        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser().getUid();
        getIncomingIntent();
        
    }
    
    private void getIncomingIntent(){
        Log.d(TAG, "getIncomingIntent: Checking for incoming intents");
        if(getIntent().hasExtra("image_url") && getIntent().hasExtra("image_name")){
            Log.d(TAG, "getIncomingIntent: Found intent extras");

            String imageUrl = getIntent().getStringExtra("image_url");
            String imageName = getIntent().getStringExtra("image_name");
            userId = getIntent().getStringExtra("user_id");
            checkButton();
            setImage(imageUrl,imageName);
        }
    }

    private void setImage(String imageUrl, String imageName){
        Log.d(TAG, "setImage: Setting the image and name to widgets");

        TextView name = findViewById(R.id.image_description);
        name.setText(imageName);

        ImageView image = findViewById(R.id.profile_image);
        Glide.with(this).asBitmap().load(imageUrl).into(image);
    }

    public void sendFriendRequest(View view) {
        //Set up the button, and check if it should be clickable or not
        Log.d(TAG, "sendFriendRequest: Inside the friend request button-press");
        Button requestButton = (Button) findViewById(R.id.send_friend_request);


        //check if the current user is requesting him/herself
        if(!userId.equals(currentUid)) {
            // Set up reading from the data base
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.

                    Log.d(TAG, "Target for friend invite is: " + String.valueOf(dataSnapshot.child(userId).child("name").getValue()));
                    Log.d(TAG, "Sender of invite is: " + String.valueOf(dataSnapshot.child(currentUid).child("name").getValue()));
                    Log.d(TAG, "onDataChange: Prepare to write to database");
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Users");

                    myRef.child(userId).child("receivedfriendrequests").child(currentUid).setValue("pending");

                    myRef.child(currentUid).child("sentfriendrequests").child(userId).setValue("pending");


                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

        }//end check on user IDs
        else{
            Log.d(TAG, "sendFriendRequest: You are requesting yourself as a friend");
        }

        Intent intent = new Intent(GalleryProfileActivity.this, FriendsActivity.class);
        startActivity(intent);

    }

    public void checkButton(){
        //Set up the button, and check if it should be clickable or not
        Log.d(TAG, "checkButton: Inside of checkButton");
        final Button requestButton = (Button) findViewById(R.id.send_friend_request);

        Log.d(TAG, "checkButton: Setting up the read access to the database");
        //take a snapshot and check if this user has a pending request already
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                Log.d(TAG, "onDataChange: Checking if there are pending results");
                if(String.valueOf(dataSnapshot.child(userId).child("receivedfriendrequests").child(currentUid).getValue()).equals("pending"))
                {
                    requestButton.setTextColor(Color.RED);
                    requestButton.setText("Friend Request has already been sent");
                    requestButton.setEnabled(false);
                }
                else if (String.valueOf(dataSnapshot.child(currentUid).child("sentfriendresults").child(currentUid).getValue()).equals("pending"))
                {
                    requestButton.setTextColor(Color.RED);
                    requestButton.setText("Friend Request has already been sent");
                    requestButton.setEnabled(false);
                }

                Log.d(TAG, "checkButton: " + dataSnapshot.child(userId).child("receivedfriendrequests").child("pending").getValue());
                Log.d(TAG, "checkButton: " + dataSnapshot.child(currentUid).child("sentfriendrequests").child("pending").getValue());

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }
}
