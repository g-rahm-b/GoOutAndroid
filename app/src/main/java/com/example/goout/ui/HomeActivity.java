package com.example.goout.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.goout.MainActivity;
import com.example.goout.R;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeactivity);
    }

    public void logOutUser(View view)
    {
        mAuth.signOut();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    public void makePlans(View view){
        Intent intent = new Intent(HomeActivity.this, makePlansActivity.class);
        startActivity(intent);

    }

    public void goToFriends(View view) {
        Log.d("HomeActivity","Setting intent");
        Intent intent = new Intent(HomeActivity.this, FriendsActivity.class);
        Log.d("HomeActivity","Starting Activity");
        startActivity(intent);
        Log.d("HomeActivity","Finishing");

    }
}