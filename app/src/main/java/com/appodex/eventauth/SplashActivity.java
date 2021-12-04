
package com.appodex.eventauth;

import androidx.annotation.LongDef;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Utils utils;
    private ImageView logoImageView, ritikkanotraImageView;
    private String currentVersion;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        sharedPreferences = getSharedPreferences(Utils.PREFS_NAME, 0);
        String currentTheme = sharedPreferences.getString("theme", "dark");
        Log.d("rk_debug_", "onCreate: " + currentTheme);
        if (currentTheme.equals(Utils.DARK_THEME)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//            Log.d("rk_debug", "onCreate: " + "dark");
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//            Log.d("rk_debug", "onCreate: " + "light");
        }

//        logoImageView = findViewById(R.id.iv_logo);
        ritikkanotraImageView = findViewById(R.id.iv_ritikkanotra_logo);

        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.themeName, outValue, true);
        if ("light".equals(outValue.string)) {
//            logoImageView.setImageResource(R.drawable.eventauth_logo_light);
            ritikkanotraImageView.setImageResource(R.drawable.ritikkanotra_logo_dark);
        }

        getSupportActionBar().hide();
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        try {
            PackageInfo packageInfo = SplashActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mAuth = FirebaseAuth.getInstance();

        Log.i("rk_debug", String.valueOf(mAuth.getCurrentUser()));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                if (mAuth.getCurrentUser() == null) {
//
//                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                }
//                else {
//                    Log.i("rk_debug", utils.userEmail);
//                    Log.i("rk_debug", mAuth.getCurrentUser().getDisplayName());
//                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                    startActivity(intent);
//                }
                if (mAuth.getCurrentUser() == null) {

                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else {
                    Log.i("rk_debug", mAuth.getCurrentUser().getEmail());
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }, 1000);

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);



    }
}