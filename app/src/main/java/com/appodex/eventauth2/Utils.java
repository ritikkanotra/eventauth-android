package com.appodex.eventauth2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Utils {

    static final StorageReference firebaseStorageRef = FirebaseStorage.getInstance().getReference();
    static final DatabaseReference firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference();
    static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    static final int TYPE_SHARE = 1;
    static final int TYPE_REGISTER = 2;

    static final String DARK_THEME = "Dark";
    static final String LIGHT_THEME = "Light";

    static final String PREFS_NAME = "com.appodex.eventauth2.PREFS_MAIN";

    static ArrayList<String> registeredEventsUCode = new ArrayList<>();

    Utils() {
        //constructor
    }

    public boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
