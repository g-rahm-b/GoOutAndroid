package com.example.goout.ui;

import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goout.R;
import com.example.goout.adapters.addFriendsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class addFriendsActivity extends AppCompatActivity {
    private String currentUid;
    private static final String TAG = "addFriendsActivity";
    //vars
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mUids = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //ToDo: When clicking the back button, the app will sometimes minimize or go to homeactivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriends);
        currentUid = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "onCreate: Started");
        initImageBitmaps();
    }

    private void initImageBitmaps(){
        Log.d(TAG, "initImageBitmaps: Preparing bitmaps");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //Object value = dataSnapshot.getValue();

                for (DataSnapshot child: dataSnapshot.getChildren()) {

                    //The storage arrays get populated within this if statement
                    //These results will be shown in the recyclerView
                    //Don't want to show: Current User. User's that have sent or receieved a request
                    //Users that area already friends.
                    Log.d(TAG, "onDataChange: " + dataSnapshot.child(currentUid).child("receivedfriendrequests").child(child.getKey()).getValue());

                    Log.d(TAG, "Received friendrequests child exists: " + String.valueOf(dataSnapshot.child(currentUid).child("receivedfriendrequests").child(child.getKey())));

                    if(!currentUid.equals(String.valueOf(child.getKey()))
                            && dataSnapshot.child(currentUid).child("receivedfriendrequests").child(child.getKey()).getValue() == null
                            && dataSnapshot.child(currentUid).child("sentfriendrequests").child(child.getKey()).getValue() == null)
                    {

                        //Retrieve and input the name
                        mNames.add(String.valueOf(child.child("name").getValue(String.class)));
                        //ToDo Insert individual user profile images when you're feeling brave
                        mImageUrls.add("https://i.insider.com/5de54e6f79d7571aaa0155a2?width=1136&format=jpeg");
                        //Store all of the unique UIDs
                        mUids.add(String.valueOf(child.getKey()));

                        //child.getKey() returns the unique ID
                        //child.getRef() returns the URL to the Firebase structure
                        //child.getValue() returns the full row {name=Graeme Bernier}
                        //String.valueOf(child.getKey()); returns the unique user ID
                        //String.valueOf(dataSnapshot.getKey())); returns Users (the root of the directory)

                        Log.d(TAG, "Firebase Step 1: " + String.valueOf(dataSnapshot));
                        Log.d(TAG, "Firebase Step 2: " + String.valueOf(child.child("name").getValue(String.class)));
                    }
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
        RecyclerView recyclerView = findViewById(R.id.addFriendsRecyclerView);
        addFriendsAdapter adapter = new addFriendsAdapter(this,mNames,mImageUrls,mUids);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
