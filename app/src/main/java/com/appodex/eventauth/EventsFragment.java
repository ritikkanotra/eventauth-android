package com.appodex.eventauth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventsFragment extends Fragment {


    RecyclerView registeredEventsRecyclerView;
    ArrayList<Event> registeredEventsList;
    RegisteredEventsAdapter registeredEventsAdapter;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    LinearLayout llLottieNoEvent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        mAuth = FirebaseAuth.getInstance();


        registeredEventsRecyclerView = view.findViewById(R.id.rv_registered_events);
        registeredEventsRecyclerView.hasFixedSize();
        registeredEventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        llLottieNoEvent = view.findViewById(R.id.ll_lottie_no_event);
        llLottieNoEvent.setVisibility(View.GONE);

        registeredEventsList = new ArrayList<>();

        updateRegisteredEvents();

//        registeredEventsList.add(new Event(
//                "",
//                "PSIT IGNITIA 2020",
//                "",
//                "Date: 30-11-2020 09:15 PM",
//                "",
//                ""));
//
//        registeredEventsList.add(new Event(
//                "",
//                "PSIT IGNITIA 2021",
//                "",
//                "Date: 30-11-2021 09:15 PM",
//                "",
//                ""));
//
//        registeredEventsList.add(new Event(
//                "",
//                "PSIT IGNITIA 2022",
//                "",
//                "Date: 30-11-2022 09:15 PM",
//                "",
//                ""));




        return view;
    }

    public void updateRegisteredEvents() {

        ArrayList<String> registeredEventsId = new ArrayList<>();
        ArrayList<String> registeredEventsUCode = new ArrayList<>();

        Utils.firebaseDatabaseRef
                .child("Users")
                .orderByChild("email")
                .equalTo(mAuth.getCurrentUser().getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

//                                Log.d("rk_debug", dataSnapshot.getKey());

                                DataSnapshot eventsSnapshot = dataSnapshot.child("registeredEvents");
                                Iterable<DataSnapshot> eventChildren = eventsSnapshot.getChildren();
                                for (DataSnapshot childSnapshot : eventChildren) {
                                    Log.i("rk_debug_rg_events", childSnapshot.getKey());
                                    registeredEventsId.add(childSnapshot.getKey());
                                    registeredEventsUCode.add(childSnapshot.child("uniqueCode").getValue().toString());
                                }
                                Utils.registeredEventsUCode = registeredEventsUCode;
                                getEventInfo(registeredEventsId, registeredEventsUCode);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    void getEventInfo(ArrayList<String> registeredEventsId, ArrayList<String> registeredEventsUCode) {
        Date currentDate = new Date();
        Utils.firebaseDatabaseRef
                .child("Events")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Iterable<DataSnapshot> sp1 = snapshot.getChildren();
                            for (DataSnapshot childSnapshot: sp1) {
                                if (registeredEventsId.contains(childSnapshot.child("id").getValue().toString())) {
                                    Event event = new Event(
                                            childSnapshot.child("id").getValue().toString(),
                                            childSnapshot.child("name").getValue().toString(),
                                            childSnapshot.child("about").getValue().toString(),
                                            childSnapshot.child("date").getValue().toString(),
                                            childSnapshot.child("time").getValue().toString(),
                                            childSnapshot.child("cover_image").getValue().toString()
                                            );
                                    try {
                                        if ((new SimpleDateFormat("dd/MM/yyyy")).parse(event.getDate()).compareTo(currentDate) >= 0) {
                                            registeredEventsList.add(event);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            setRecyclerViewAdapter(registeredEventsUCode);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    void setRecyclerViewAdapter(ArrayList<String> registeredEventsUCode) {
        if (registeredEventsList.isEmpty()) {
            llLottieNoEvent.setVisibility(View.VISIBLE);
        }
        else {
            registeredEventsAdapter = new RegisteredEventsAdapter(getContext(), registeredEventsList, registeredEventsUCode);
            registeredEventsRecyclerView.setAdapter(registeredEventsAdapter);
        }
        progressBar.setVisibility(View.GONE);

    }

}
