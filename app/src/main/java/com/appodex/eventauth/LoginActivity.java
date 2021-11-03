package com.appodex.eventauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    GoogleSignInAccount account;
    private boolean isRedirected;
    private Intent intent;
    private MaterialCardView progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        intent = getIntent();

        isRedirected =intent.getBooleanExtra("isRedirected", false);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

    }

    public void loginGoogle(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                account = task.getResult(ApiException.class);
                Log.d("rk_debug", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());

                Log.d("rk_debug", "1this");




            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("rk_debug", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseWork() {
        if (new Utils().isInternetConnected(getApplicationContext())) {
            FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .orderByChild("email")
                .equalTo(account.getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("rk_debug", "2this");
                        if (!snapshot.exists()) {
                            Log.d("rk_debug", "3this");
                            Map<String, String> infoMap = new HashMap<>();
                            infoMap.put("email", account.getEmail());
                            infoMap.put("name", account.getDisplayName());
                            infoMap.put("uid", mAuth.getCurrentUser().getUid());

                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("Users")
                                    .push()
                                    .setValue(infoMap)
                                    .addOnSuccessListener(task -> {
                                        Toast.makeText(getApplicationContext(), "Registered Successfully",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                                            @Override
                                            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                                if (!isRedirected) {
                                                    startActivity(intent);
                                                }
                                                finish();
                                            }
                                        });
                                    });

                        }
                        else {
                            Log.d("rk_debug", "4this");
                            Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
                            mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                                @Override
                                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                    Toast.makeText(getApplicationContext(), "Login Successful",
                                            Toast.LENGTH_SHORT).show();
                                    if (!isRedirected) {
                                        startActivity(intentMain);
                                    }
                                    else {
                                        Intent intentRedirect = new Intent(LoginActivity.this,
                                                EventRegistrationActivity.class);
                                        intentRedirect.putExtra("eventId", intent.getStringExtra("eventId"));
                                        startActivity(intentRedirect);
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    finish();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            new Utils().retrieveInfo();
                            progressBar.setVisibility(View.VISIBLE);
                            Log.d("rk_debug", "signInWithCredential:success");
                            firebaseWork();
//                            Toast.makeText(getApplicationContext(), R.string.login_success,
//                                    Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("rk_debug", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), R.string.login_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }



}