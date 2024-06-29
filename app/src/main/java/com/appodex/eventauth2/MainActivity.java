package com.appodex.eventauth2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private TextView headingTextView;
    private ImageView userPhotoImageView;
    private ImageView btnCreateEvent;


    private String CURRENT_VERSION;
    private String realVersion;
    private String updateChanges;
    private String realVersionDownloadUrl;
    private String editedChanges;
    private ProgressDialog updateProgressDialog;

    private DatabaseReference mDatabaseRef;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

//        CURRENT_VERSION = getCurrentVersion();
//        realVersion = null;
        checkPermissions();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();


//        mDatabaseRef.child("app_version").child("version_no").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                realVersion = dataSnapshot.getValue().toString();
//                Log.i("testing", realVersion);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        final String[][] newChanges = new String[1][1];
//
//        mDatabaseRef.child("app_version").child("version_change").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                updateChanges = dataSnapshot.getValue().toString();
//                Log.i("testing", updateChanges);
//
//                newChanges[0] = updateChanges.split("///");
//                editedChanges = "";
//                for(int i = 0; i < newChanges[0].length; i++) {
//                    editedChanges += (i + 1) + ". " +  newChanges[0][i] + "\n";
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });





//        realVersionDownloadUrl = getRealVersionDownloadUrl();

        mAuth = FirebaseAuth.getInstance();

        headingTextView = findViewById(R.id.tv_heading);
        userPhotoImageView = findViewById(R.id.iv_user_photo);

        btnCreateEvent = findViewById(R.id.btn_create_event);
        btnCreateEvent.setOnClickListener(task -> {
            Intent intent = new Intent(this, CreateOrUpdateEventActivity.class);
            startActivity(intent);
        });
        hideBtnCreateEvent();

        bottomNav = findViewById(R.id.bottom_nav_view);
        bottomNav.setOnNavigationItemSelectedListener(bottomNavItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment_container, new EventsFragment()).commit();
        headingTextView.setText(R.string.registered_events);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Glide.with(MainActivity.this)
                        .load(mAuth.getCurrentUser().getPhotoUrl())
                        .placeholder(R.drawable.profile_avatar_placeholder_large)
                        .circleCrop()
                        .into(userPhotoImageView);
            }
        }, 1000);

//        Log.i("rk_debug_main", mAuth.getCurrentUser().getEmail());
//        Glide.with(MainActivity.this)
//                .load(mAuth.getCurrentUser().getPhotoUrl())
//                .circleCrop()
//                .into(userPhotoImageView);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                checkVersion();
//
//            }
//        }, 5000);


    }

    BottomNavigationView.OnNavigationItemSelectedListener bottomNavItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {

                Fragment selectedFragment = null;
                int selectedHeading;

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.item_events:
                            selectedFragment = new EventsFragment();
                            selectedHeading = R.string.registered_events;
                            changeFragment();
                            break;
                        case R.id.item_dashboard:
                            selectedFragment = new DashboardFragment();
                            selectedHeading = R.string.my_events;
                            changeFragment();
                            break;
                        case R.id.item_settings:
                            selectedFragment = new SettingsFragment();
                            selectedHeading = R.string.settings;
                            changeFragment();
                            break;

                    }

                    return true;
                }

                void changeFragment() {
                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.fragment_container, selectedFragment).commit();

//                    getSupportFragmentManager().beginTransaction().hide(
//                            R.id.fragment_container, selectedFragment).commit();

                    if (selectedHeading == R.string.my_events) {
                        showBtnCreateEvent();
                    }
                    else {
                        hideBtnCreateEvent();
                    }

                    headingTextView.setText(selectedHeading);
                }
            };

    private void checkVersion() {

        if(realVersion != null && !realVersion.equals(CURRENT_VERSION)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Update Available").setMessage(editedChanges);

            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    downloadAndUpdateApp();

                }
            });

            builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        }

    }

    private void downloadAndUpdateApp() {


        downloadUpdate();
        updateApp();



    }

    private void downloadUpdate() {

        updateProgressDialog = new ProgressDialog(MainActivity.this);
        updateProgressDialog.setMessage("Downloading Update");
        updateProgressDialog.setCancelable(false);
        updateProgressDialog.setCanceledOnTouchOutside(false);
        updateProgressDialog.show();

        String destination = Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/.updates/";
        String fileName = "CoronavirusTracker.apk";
        destination += fileName;

        //Delete update file if exists
        File file = new File(destination);
        if (file.exists())
            //file.delete() - test this, I think sometimes it doesnt work
            file.delete();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(realVersionDownloadUrl));
        request.setDestinationInExternalPublicDir("/Android/data/" + getPackageName() + "/.updates", "CoronavirusTracker.apk");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);

    }

    private void updateApp() {

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {

                updateProgressDialog.dismiss();

                Intent install = new Intent(Intent.ACTION_VIEW);
                File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/.updates/CoronavirusTracker.apk");
                Uri data = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID +".provider",file);
                Log.i("testing", data.getPath());
                install.setDataAndType(data,"application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(install);

                unregisterReceiver(this);
                finish();
            }
        };
        //register receiver for when .apk download is compete
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    private String getRealVersionDownloadUrl() {

        mDatabaseRef.child("app_version").child("version_url").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                realVersionDownloadUrl = dataSnapshot.getValue().toString();
                Log.i("testing", realVersionDownloadUrl);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return realVersionDownloadUrl;

    }

    private void showBtnCreateEvent() {
        btnCreateEvent.setVisibility(View.VISIBLE);
    }

    private void hideBtnCreateEvent() {
        btnCreateEvent.setVisibility(View.GONE);
    }

    private String getCurrentVersion() {

        String currentVersion = null;

        try {
            PackageInfo packageInfo = MainActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return currentVersion;

    }

    private void checkPermissions() {

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    },
                    1);

        }

//        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{
//                            Manifest.permission.CAMERA
//                    },
//                    2);
//
//        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}