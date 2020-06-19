package com.example.goout.ui;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.goout.MainActivity;
import com.example.goout.R;
import com.example.goout.models.User;
import com.example.goout.UserClient;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import static android.text.TextUtils.isEmpty;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    //Widgets
    private Button mLogin;
    private EditText mEmail, mPassword;
    private ProgressBar mProgressBar;

    //Firebase authentications
    private FirebaseAuth mAuth;

    //ToDO Integerate the below into the "check if you are already logged in method.
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        /**
         * Check if the user is already logged in, in which case, we will move to the 'home' screen
         */

        Log.d("login","Checking if user is already signed in");
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    //This means that the user is already logged in.
                    Toast.makeText(LoginActivity.this, "User is already logged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else{
                    Log.d("login","No user is logged in. Continue");
                }
            }
        };

        mLogin = (Button) findViewById(R.id.login);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mProgressBar = findViewById(R.id.progressBar);

        //ToDo: Ensure that erroneous entries (such as no email or password) don't crash the app
        //ToDo: Perhaps lock the button off (the click state) until there is something in both
        setupFirebaseAuth();
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.link_register).setOnClickListener(this);

        hideSoftKeyboard();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        Log.d("login","onStop method");
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

    /*
        ----------------------------- QOL Items ---------------------------------
     */

    private void showDialog(){
        Log.d("login","showDialog Method");
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideDialog(){
        Log.d("login","hideDialog method");
        if(mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void hideSoftKeyboard(){
        Log.d("login","hideSoftKeyboard method");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

        /*
        ----------------------------- Firebase Setup ---------------------------------
     */
    public void setupFirebaseAuth(){
        Log.d("login","In Firebase Authentication method");

        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged (@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    Log.d("login", "onAuthStateChanged:signed_in: " + user.getUid());
                    Toast.makeText(LoginActivity.this, "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                            .setTimestampsInSnapshotsEnabled(true)
                            .build();
                    db.setFirestoreSettings(settings);

                    DocumentReference userRef = db.collection(getString(R.string.collection_users)).document(user.getUid());

                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                Log.d("login","onComplete: successfully set the user client.");

                                //ToDo There's a crash happening here for some reason

                                User user = task.getResult().toObject(User.class);
                                Log.d("login","User Set.");

                                //ToDo It's probably this line below
                                //((UserClient)(getApplicationContext())).setUser(user);
                                Log.d("login","setUser complete");
                            }
                        }
                    });

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    Log.d("login","onAuthStateChanged: signed_out");
                }

            }
        };
    }

    private void signIn(){
        //check if the fields are filled out
        if(!isEmpty(mEmail.getText().toString())
                && !isEmpty(mPassword.getText().toString())){
            Log.d("login", "onClick: attempting to authenticate.");

            showDialog();

            FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail.getText().toString(),
                    mPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            hideDialog();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    hideDialog();
                }
            });
        }else{
            Toast.makeText(LoginActivity.this, "You didn't fill in all the fields.", Toast.LENGTH_SHORT).show();
        }
    }

    //@Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.link_register:{
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.email_sign_in_button:{
                signIn();
                break;
            }
        }
    }

}