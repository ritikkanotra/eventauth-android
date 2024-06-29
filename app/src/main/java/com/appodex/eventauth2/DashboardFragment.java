package com.appodex.eventauth2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment {

    RecyclerView myEventsRecyclerView;
    LottieAnimationView progressBar;
    MyEventsAdapter myEventsAdapter;

    FirebaseAuth mAuth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mAuth = FirebaseAuth.getInstance();

        myEventsRecyclerView = view.findViewById(R.id.rv_my_events);
        myEventsRecyclerView.hasFixedSize();
        myEventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressBar = view.findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.VISIBLE);

        List<Event> myEventsList = new ArrayList<>();
        List<String> keyList = new ArrayList<>();

        if (new Utils().isInternetConnected(getContext())) {
            Utils.firebaseDatabaseRef
                    .child("Events")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                            myEventsList.clear();
                            Log.d("rk_debug", "onChildAdded: ");
                            Map<String, String> map = (Map<String, String>) snapshot.getValue();
                            List<String> sharedWith = new ArrayList<>();
                            String sharedBy = null;
                            if (map.containsKey("shared_with")) {
                                sharedWith = Arrays.asList(map.get("shared_with").split(","));
                                if (sharedWith.contains(mAuth.getCurrentUser().getEmail())) {
                                    sharedBy = map.get("owner");
                                }
                            }
                            if (map.get("owner").equals(mAuth.getCurrentUser().getEmail()) ||
                                sharedWith.contains(mAuth.getCurrentUser().getEmail())) {
                                myEventsList.add(0, new Event(map.get("id"),
                                        map.get("name"),
                                        map.get("about"),
                                        map.get("date"),
                                        map.get("time"),
                                        map.get("cover_image"), sharedBy));
                                keyList.add(0, snapshot.getKey());
                            }

                            myEventsAdapter = new MyEventsAdapter(getContext(), myEventsList);
                            myEventsRecyclerView.setAdapter(myEventsAdapter);
                            progressBar.setVisibility(View.GONE);
                            myEventsAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            Map<String, String> map = (Map<String, String>) snapshot.getValue();
                            int index = keyList.indexOf(snapshot.getKey());
                            Event event = myEventsList.get(index);
                            event.setName(map.get("name"));
                            event.setSummary(map.get("summary"));
                            event.setTime(map.get("time"));
                            event.setCoverPicUrl(map.get("cover_image"));

                            myEventsAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                            int index = keyList.indexOf(snapshot.getKey());
                            myEventsList.remove(index);
                            myEventsAdapter.notifyDataSetChanged();
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
