package com.appodex.eventauth;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class Utils {

    static final StorageReference firebaseStorageRef = FirebaseStorage.getInstance().getReference();
    static final DatabaseReference firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference();
    static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    static final int TYPE_SHARE = 1;
    static final int TYPE_REGISTER = 2;

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
