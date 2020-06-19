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

public class GalleryFriendActivity extends AppCompatActivity {
    private static final String TAG = "GalleryFriendActivity";
    private String userId;
    private FirebaseAuth mAuth;
    private String currentUid;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galleryfriendactivity);
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
            setImage(imageUrl,imageName);
        }
    }

    private void setImage(String imageUrl, String imageName){
        Log.d(TAG, "setImage: Setting the image and name to widgets");

        TextView name = findViewById(R.id.image_description_friendView);
        name.setText(imageName);

        ImageView image = findViewById(R.id.profile_image_friendView);
        Glide.with(this).asBitmap().load(imageUrl).into(image);
    }

}
