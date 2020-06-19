package com.example.goout.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.goout.adapters.friendsActivityFriendsListAdapter;

import com.example.goout.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {
    private String currentUid;
    private String userId;
    private static final String TAG = "FriendsActivity";
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mUids = new ArrayList<>();
    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);



        //Get the reference to the current userID
        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser().getUid();

        Log.d("friendsactivity","Going into initItemData");
        initItemData();
    }

    private void initItemData() {
        //Only want to add in current user's accepted friends
        Log.d(TAG, "initItemData: Building Array");
        final ArrayList<String> acceptedRequests = new ArrayList<>();

        Log.d(TAG, "initItemData: Setting up addValueEventListener");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Entering for loop");
                //for loop will return all of the children of the current users friends
                for (DataSnapshot friends: dataSnapshot.child(currentUid).child("friends").getChildren()){

                    if(friends.getValue().equals(true))
                    {
                        Log.d(TAG, "onDataChange: " + friends.getKey());
                        acceptedRequests.add(friends.getKey());
                    }
                }


                //now we have to go through the array of all "friend Ids" and populate their cards
                for (int i = 0; i < acceptedRequests.size(); i++){
                    mUids.add(dataSnapshot.child(acceptedRequests.get(i)).getKey());
                    mImageUrls.add("https://i.insider.com/5de54e6f79d7571aaa0155a2?width=1136&format=jpeg");
                    mNames.add(dataSnapshot.child(acceptedRequests.get(i)).child("name").getValue(String.class));
                }
                initRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: Initializing the recyclerView");
        RecyclerView recyclerView = findViewById(R.id.friendsActivityRecyclerView);
        friendsActivityFriendsListAdapter adapter = new friendsActivityFriendsListAdapter(this, mNames,mImageUrls,mUids);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void goToAddFriends(View view) {
        Intent intent = new Intent(FriendsActivity.this, addFriendsActivity.class);
        startActivity(intent);


    }

    public void goToFriendRequests(View view) {
        Intent intent = new Intent(FriendsActivity.this, FriendRequestsActivity.class);
        startActivity(intent);

    }
}
