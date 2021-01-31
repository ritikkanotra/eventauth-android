package com.appodex.eventauth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment {

    FloatingActionButton createEVentBtn;
    RecyclerView myEventsRecyclerView;
    ProgressBar progressBar;

    FirebaseAuth mAuth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mAuth = FirebaseAuth.getInstance();

        createEVentBtn = view.findViewById(R.id.btn_create_event);
        createEVentBtn.setOnClickListener(task -> {
            Intent intent = new Intent(getContext(), CreateEventActivity.class);
            startActivity(intent);
        });

        myEventsRecyclerView = view.findViewById(R.id.rv_my_events);
        myEventsRecyclerView.hasFixedSize();
        myEventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressBar = view.findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.VISIBLE);

        List<Event> myEventsList = new ArrayList<>();

        if (new Utils().isInternetConnected(getContext())) {
            Utils.firebaseDatabaseRef
                    .child("Events")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            Map<String, String> map = (Map<String, String>) snapshot.getValue();
                            if (map.get("owner").equals(mAuth.getCurrentUser().getEmail())) {
                                myEventsList.add(0, new Event(map.get("id"),
                                        map.get("name"),
                                        map.get("about"),
                                        map.get("date"),
                                        map.get("time"),
                                        map.get("cover_image")));
                            }
                            MyEventsAdapter myEventsAdapter = new MyEventsAdapter(getContext(), myEventsList);
                            myEventsRecyclerView.setAdapter(myEventsAdapter);
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }



        return view;
    }

}
