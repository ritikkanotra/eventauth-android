package com.appodex.eventauth;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    private LinearLayout logoutBtn;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragments_settings, container, false);


        mAuth = FirebaseAuth.getInstance();

        logoutBtn = view.findViewById(R.id.ll_logout);


        logoutBtn.setOnClickListener(task -> {
            signOut();
        });

        return view;

    }

    void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}