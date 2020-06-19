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
import com.example.goout.MainActivity;
import com.example.goout.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendRequestProfile extends AppCompatActivity {
    private static final String TAG = "FriendRequestProfile";
    private String userId;
    private FirebaseAuth mAuth;
    private String currentUid;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_gallery_friendrequests);
        Log.d(TAG, "onCreate: Started");
        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser().getUid();
        getIncomingIntent();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getIncomingIntent(){
        Log.d(TAG, "getIncomingIntent: Checking for incoming intents");
        if(getIntent().hasExtra("image_url") && getIntent().hasExtra("image_name")){
            Log.d(TAG, "getIncomingIntent: Found intent extras");
            String imageUrl = getIntent().getStringExtra("image_url");
            String imageName = getIntent().getStringExtra("image_name");
            userId = getIntent().getStringExtra("user_id");
            setImage(imageUrl,imageName);
        }
    }

    private void setImage(String imageUrl, String imageName){
        Log.d(TAG, "setImage: Setting the image and name to widgets");

        TextView name = findViewById(R.id.image_description_friendrequest);
        name.setText(imageName);

        ImageView image = findViewById(R.id.profile_image_friendrequest);
        Glide.with(this).asBitmap().load(imageUrl).into(image);
    }

    //ToDo You are just removing the received and sent requests. Might need to handle "blocking" a user
    //Todo Would be easy enough. Just need to add a "blocked" portion to the database, and check for
    //ToDo that specific user being blocked before refreshing the "add friends" list.
    public void declineFriendRequest(View view) {

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //want to remove the received and removed friend requests from each
                myRef.child(currentUid).child("receivedfriendrequests").child(userId).removeValue();
                myRef.child(userId).child("sentfriendrequests").child(currentUid).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        Intent intent = new Intent(FriendRequestProfile.this, FriendsActivity.class);
        startActivity(intent);
    }

    public void acceptFriendRequest(View view) {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Want to go to both the current, and the target user and set each other as friends

                //set the friend on the current user
                myRef.child(currentUid).child("friends").child(userId).setValue(true);

                //set the friend on the other user
                myRef.child(userId).child("friends").child(currentUid).setValue(true);

                //set the friendRequest statuses from "pending" to accepted
                myRef.child(currentUid).child("receivedfriendrequests").child(userId).setValue("accepted");
                myRef.child(userId).child("sentfriendrequests").child(currentUid).setValue("accepted");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        Intent intent = new Intent(FriendRequestProfile.this, FriendsActivity.class);
        startActivity(intent);
    }
}