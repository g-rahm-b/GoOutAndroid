package com.example.goout.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goout.R;
import com.example.goout.adapters.FriendRequestsAdapter;
import com.google.common.collect.Iterables;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.ChildrenNode;

import java.util.ArrayList;

//ToDo: When clicking the back button, the app will sometimes minimize or go to homeactivity

public class FriendRequestsActivity extends AppCompatActivity {
    private String currentUid;
    private String userId;
    private static final String TAG = "FriendRequestsActivity";
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mUids = new ArrayList<>();
    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendrequests);
        currentUid = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "onCreate: Started");
        initImageBitmaps();
    }

    private void initImageBitmaps(){
        Log.d(TAG, "initImageBitmaps: Preparing bitmaps");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //ToDo Double check that this doesn't fuck up your friend requests

                //First, set up an array to store all of the friend requests received by the user
                ArrayList<String> receivedFriendRequests = new ArrayList<>();

                for(DataSnapshot friendRequests: dataSnapshot.child(currentUid).child("receivedfriendrequests").getChildren()){
                    userId = friendRequests.getKey();
                    //Below returns the key
                    Log.d(TAG, "onDataChange: " + friendRequests.getKey());
                    //Below returns the status (I.E. "Pending" or "accepted."
                    Log.d(TAG, "onDataChange: " + friendRequests.getValue());
                    //Below returns null, usually
                    Log.d(TAG, "onDataChange: " + friendRequests.child(friendRequests.getKey()).getValue());
                    //Only want to display the "pending" resulsts. Not the accepted ones
                    if(friendRequests.getValue().equals("pending"))
                    {
                        Log.d(TAG, "onDataChange: " + friendRequests.getKey());
                        receivedFriendRequests.add(friendRequests.getKey());
                    }
                }

                //Now go through every received friend request, and pull the request-ee's
                //information and add it the recycler view.
                for(int i = 0; i < receivedFriendRequests.size(); i++)
                {
                    mUids.add(dataSnapshot.child(receivedFriendRequests.get(i)).getKey());
                    mImageUrls.add("https://i.insider.com/5de54e6f79d7571aaa0155a2?width=1136&format=jpeg");
                    mNames.add(dataSnapshot.child(receivedFriendRequests.get(i)).child("name").getValue(String.class));
                }

                initRecyclerView();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: Init RecyclerView");
        RecyclerView recyclerView = findViewById(R.id.friendRequestRecyclerView);
        FriendRequestsAdapter adapter = new FriendRequestsAdapter(this,mNames,mImageUrls,mUids);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}




