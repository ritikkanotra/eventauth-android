package com.appodex.eventauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private MaterialButton logoutBtn;
    private FirebaseAuth mAuth;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        logoutBtn = findViewById(R.id.btn_logout);
        backBtn = findViewById(R.id.back_btn);


        logoutBtn.setOnClickListener(task -> {
            signOut();
        });

        backBtn.setOnClickListener(task -> {
            finish();
        });

    }

    void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(SettingsActivity.this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}