package com.appodex.eventauth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    private LinearLayout logoutBtn;
    private FirebaseAuth mAuth;
    private TextView appVersionTV;
    private String versionName;
    private Spinner themeSpinner;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEditor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragments_settings, container, false);


        mAuth = FirebaseAuth.getInstance();

        logoutBtn = view.findViewById(R.id.ll_logout);
        appVersionTV = view.findViewById(R.id.tv_app_version);
        themeSpinner = view.findViewById(R.id.theme_spinner);

        sharedPreferences = getActivity().getSharedPreferences(Utils.PREFS_NAME, 0);
        spEditor = sharedPreferences.edit();

        String themeItems[] = new String[] {
                Utils.DARK_THEME,
                Utils.LIGHT_THEME
        };

        ArrayAdapter<String> themeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, themeItems);

        themeSpinner.setAdapter(themeAdapter);
        themeSpinner.setSelection(sharedPreferences.getString("theme", Utils.DARK_THEME).equals(Utils.DARK_THEME) ? 0 : 1, true);

        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("rk_debug", "onItemSelected: " + i);
                String currentTheme = null;
                if (themeItems[i].equals(Utils.DARK_THEME)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    currentTheme = Utils.DARK_THEME;
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    currentTheme = Utils.LIGHT_THEME;
                }
                spEditor.putString("theme", currentTheme);
                spEditor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        themeSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.d("rk_debug", "onItemClick: " + i);
//            }
//        });

        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            versionName = packageInfo.versionName;
            appVersionTV.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


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