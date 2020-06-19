package com.example.goout.ui;

import com.example.goout.models.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import static android.text.TextUtils.isEmpty;

import com.example.goout.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
//import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mRegister;
    private EditText mEmail,mPassword,mName, mConfirmPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private ProgressBar mProgressBar;
    private FirebaseFirestore mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    //if we are here, then the user exists. Send them somewhere.
                    Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        //Set up the data within the activity_registration buttons
        mRegister = (Button) findViewById(R.id.register);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mName = (EditText) findViewById(R.id.name);
        mConfirmPassword = (EditText) findViewById(R.id.confirmPassword);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mRegister.setOnClickListener(this);
        mDb = FirebaseFirestore.getInstance();
        hideSoftKeyboard();
    }

    public void registerNewEmail(final String email, String password, final String name){

        showDialog();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("register", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()){
                            Log.d("register", "onComplete: AuthState: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

                            //insert some default data
                            User user = new User();
                            user.setEmail(email);
                            user.setUsername(email.substring(0, email.indexOf("@")));
                            user.setUser_id(FirebaseAuth.getInstance().getUid());
                            user.setUsername(name);

                            realTimeDatabase(FirebaseAuth.getInstance().getUid(),name);

                            //ToDo Just adding all users with the same random image right now
                            user.setAvatar("https://i.imgur.com/ZcLLrkY.jpg");

                            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                    .setTimestampsInSnapshotsEnabled(true)
                                    .build();
                            mDb.setFirestoreSettings(settings);

                            DocumentReference newUserRef = mDb
                                    .collection(getString(R.string.collection_users))
                                    .document(FirebaseAuth.getInstance().getUid());

                            newUserRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    hideDialog();

                                    if(task.isSuccessful()){
                                        redirectLoginScreen();
                                    }else{
                                        View parentLayout = findViewById(android.R.id.content);
                                        Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                        else {
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                            hideDialog();
                        }

                        // ...
                    }
                });
    }

    //adding the User ID to the real-time database for friends lists and stuff
    private void realTimeDatabase(String uid, String name) {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.child(uid).child("name").setValue(name);
    }

    /**
     * Redirects the user to the Login screen
     */
    private void redirectLoginScreen(){
        Log.d("register", "redirectLoginScreen: redirecting to login screen.");

        Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * QOL items
     */
    private void showDialog(){
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideDialog(){
        if(mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    /**
     * onClick checks to ensure proper usage.
     */

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register:{
                Log.d("register", "onClick: attempting to register.");

                //check for null valued EditText fields
                Log.d("registration", "Going into email/pass/name check");

                //check for null valued EditText fields
                if (!isEmpty(mEmail.getText().toString())
                        && !isEmpty(mPassword.getText().toString())
                        && !isEmpty(mConfirmPassword.getText().toString())) {

                    //check if passwords match
                    if ((mPassword.getText().toString().equals(mConfirmPassword.getText().toString()))) {
                        //Initiate registration task
                        registerNewEmail(mEmail.getText().toString(), mPassword.getText().toString(), mName.getText().toString());
                        Log.d("registration", "Everything checks out");
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Passwords do not Match", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(RegistrationActivity.this, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}