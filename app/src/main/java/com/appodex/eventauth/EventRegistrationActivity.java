package com.appodex.eventauth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EventRegistrationActivity extends AppCompatActivity {

    private ImageView eventCoverImageView, backBtn, collabBtn;
    private MaterialButton actionBtn, delButton;
    private TextView eventNameTextView, dateTextView, timeTextView, aboutTextView, headingTextView;
    private ProgressBar actionBtnProgressBar;
    private View mainProgressBarLayout;
//    private ShimmerFrameLayout shimmerFrameEventCover, shimmerFrameEventDetails;
    int intentType;
    private AlertDialog deleteAlertDialog;
    private ProgressDialog progressDialog;
    private ArrayList<Collaborator> collaborators;
    private CollaboratorsListAdapter adapter;
    private ListView listView;
    static String eventKeyPoint;


    Intent receivedIntent;

    Event event;
    public static String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration);

        getSupportActionBar().hide();


//        if (Utils.firebaseAuth.getCurrentUser() == null) {
//            Intent intent = new Intent(this, LoginActivity.class);
//            Log.i("rk_debug", "isRedirected");
//            intent.putExtra("isRedirected", true);
//            intent.putExtra("eventId", event.getEventId());
//            startActivity(intent);
//            finish();
//
//        }

        Log.d("rk_debug", "now");

//        loadCollaborators();



        eventNameTextView = findViewById(R.id.tv_event_name);
        dateTextView = findViewById(R.id.tv_event_date);
        timeTextView = findViewById(R.id.tv_event_time);
        aboutTextView = findViewById(R.id.tv_event_about);
        headingTextView = findViewById(R.id.tv_heading);
        eventCoverImageView = findViewById(R.id.iv_event_cover);
        actionBtnProgressBar = findViewById(R.id.pb_action_btn);
        mainProgressBarLayout = findViewById(R.id.pb_layout);
        delButton = findViewById(R.id.del_event_btn);
        collabBtn = findViewById(R.id.iv_collab_btn);
//        shimmerFrameEventCover = findViewById(R.id.shimmer_frame_event_cover);
//        shimmerFrameEventDetails = findViewById(R.id.shimmer_frame_event_details);

//        eventCoverImageView.setVisibility(View.INVISIBLE);
//        shimmerFrameEventCover.startShimmer();
//        shimmerFrameEventDetails.startShimmer();

        backBtn = findViewById(R.id.back_btn);

        mainProgressBarLayout.setVisibility(View.VISIBLE);

        collaborators = new ArrayList<>();

        collabBtn.setVisibility(View.GONE);

        adapter = new CollaboratorsListAdapter(this, collaborators);


        collabBtn.setOnClickListener(task -> {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View customView = layoutInflater.inflate(R.layout.add_collab_popup, null);

//            ArrayList<String> data = new ArrayList<>();
//            data.add("Ritik");
//            data.add("Kanotra");

//            collaborators.add(
//                    new Collaborator("Ritik Kanotra", "rk@mail.com")
//            );

            Log.d("rk_debug", "this: " + collaborators.size());

            listView = customView.findViewById(R.id.collaborators_list_view);
            EditText addEmailET = customView.findViewById(R.id.et_add_email);
            TextView closePopupBtn = customView.findViewById(R.id.close_popup_btn);
            ImageView addCollabBtn = customView.findViewById(R.id.add_collab_btn);
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, );
//            adapter = new CollaboratorsListAdapter(this, collaborators);
//            adapter.addAll(collaborators);
            listView.setAdapter(adapter);

            addCollabBtn.setOnClickListener(addCollabTask -> {
                String newEmail = addEmailET.getText().toString();
                Log.d("rk_debug", "newEmail: " + newEmail + collaborators.size());
                ArrayList<String> collabEmails = new ArrayList<>();
                for (Collaborator c: collaborators) {
                    collabEmails.add(c.getEmail());
                }
                if (collabEmails.contains(newEmail)) {
                    Toast.makeText(this, "Already a collaborator", Toast.LENGTH_SHORT).show();
                }
                else if (newEmail.equals(Utils.firebaseAuth.getCurrentUser().getEmail())) {
                    Toast.makeText(this, "Can't add owner", Toast.LENGTH_SHORT).show();
                }
                else {
                    addCollaborator(newEmail);
                }
            });



            final PopupWindow popupWindow = new PopupWindow(
                    customView,
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT
            );

            popupWindow.setOutsideTouchable(true);
            popupWindow.showAtLocation(collabBtn, Gravity.CENTER, 0, 0);
            closePopupBtn.setOnClickListener(closeTask -> {
                popupWindow.dismiss();
            });
        });

        backBtn.setOnClickListener(task -> {
            finish();
        });

        delButton.setOnClickListener(task -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_baseline_delete_outline_24);
            builder.setTitle("Delete Event");
            builder.setMessage("Do you want to delete this event permanently?");
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteEvent();
//                    finish();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteAlertDialog.cancel();
                }
            });
            deleteAlertDialog = builder.create();
            deleteAlertDialog.show();

        });

        receivedIntent = getIntent();

        intentType = receivedIntent.getIntExtra("type", 2);
//        event = (Event) receivedIntent.getSerializableExtra("event");
        eventId = receivedIntent.getStringExtra("eventId");

        Log.i("rk_debug", Integer.toString(intentType));

//        while (Utils.firebaseAuth.getCurrentUser() == null) {
//            Log.d("rk_debug", "while");
//        }


        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null) {
                        Log.d("rk_debug", "not_null");
                        String eventId = Objects.requireNonNull(pendingDynamicLinkData
                                .getLink()).getQueryParameter("event_id");
                        Log.d("rk_debug", eventId);
                        checkLogin(eventId);
                        getEvent(eventId);
                    }
                    else  {
                        Log.d("rk_debug", "is_null");
                    }
                });



        
        actionBtn = findViewById(R.id.action_btn);

        actionBtn.setVisibility(View.INVISIBLE);
        actionBtnProgressBar.setVisibility(View.VISIBLE);

        if (eventId != null) {
            Log.i("rk_debug", "isNotNULL");
//            displayEventData(event);
//            updateRegisterStatus();
            getEvent(eventId);
//            actionBtn.setVisibility(View.VISIBLE);
        }

        if (intentType == Utils.TYPE_SHARE) {
            actionBtn.setText(R.string.share);
        }
        else if (intentType == Utils.TYPE_REGISTER) {
            actionBtn.setText(R.string.register);
            delButton.setVisibility(View.GONE);

        }

        actionBtn.setOnClickListener(task -> {
            switch (intentType) {
                case Utils.TYPE_SHARE:
                    shareEvent();
                    break;

                case Utils.TYPE_REGISTER:
                    registerEvent();
                    break;

            }
        });

    }

    private void displayEventData(Event event) {



//        shimmerFrameEventDetails.hideShimmer();
//        shimmerFrameEventCover.hideShimmer();
//        shimmerFrameEventCover.setVisibility(View.INVISIBLE);
//        eventCoverImageView.setVisibility(View.VISIBLE);


//        eventCoverImageView.setImageURI(Uri.parse(event.getCoverPicUrl()));

        Glide.with(getApplicationContext())
                .load(event.getCoverPicUrl())
                .centerCrop()
                .placeholder(R.drawable.placeholder_event)
                .into(eventCoverImageView);


        eventNameTextView.setText(event.getName());
        headingTextView.setText(event.getName());
        dateTextView.setText(event.getDate());
        timeTextView.setText(event.getTime());
        aboutTextView.setText(event.getSummary());

        mainProgressBarLayout.setVisibility(View.GONE);

    }

    private void registerEvent() {




        Utils.firebaseDatabaseRef
                .child("Users")
                .orderByChild("email")
                .equalTo(Utils.firebaseAuth.getCurrentUser().getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot data: snapshot.getChildren()) {
                                Map map = new HashMap<>();
                                map.put("attendance", 0);
                                String uniqueCode = Utils.firebaseAuth.getCurrentUser().getUid()
                                        + event.getEventId();
                                map.put("uniqueCode", uniqueCode);

                                Log.d("rk_debug UC", uniqueCode);


                                Utils.firebaseDatabaseRef
                                        .child("Users")
                                        .child(data.getKey())
                                        .child("registeredEvents")
                                        .child(event.getEventId())
                                        .updateChildren(map);

                                updateRegisterStatus();

                                Utils.registeredEventsUCode.add(uniqueCode);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void updateRegisterStatus() {

//        int intentType = in;

        Utils.firebaseDatabaseRef
                .child("Users")
                .orderByChild("email")
                .equalTo(Utils.firebaseAuth.getCurrentUser().getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot data: snapshot.getChildren()) {
                                if (data.child("registeredEvents")
                                        .child(event.getEventId()).exists() && intentType != Utils.TYPE_SHARE) {
                                    actionBtn.setText("My QR Code");
                                    actionBtn.setBackgroundColor(getColor(R.color.dark_grey));
                                    actionBtn.setTextColor(getColor(R.color.white));
                                    actionBtn.setOnClickListener(task -> {
                                        Intent intent = new Intent(EventRegistrationActivity.this, QRCodeActivity.class);
                                        Log.d("rk_debug", "pos" + Utils.registeredEventsUCode.size());
                                        String uCode = Utils.firebaseAuth.getCurrentUser().getUid() + eventId;
                                        Log.d("rk_debug", "onDataChange-ucode: " + uCode);
//                                        intent.putExtra("uniqueCode",
//                                                Utils.registeredEventsUCode
//                                                        .get(receivedIntent.getIntExtra("position", 0)));
                                        intent.putExtra("uniqueCode", uCode);
                                        startActivity(intent);
                                    });
//                                    actionBtn.setEnabled(false);

                                }
                                actionBtnProgressBar.setVisibility(View.GONE);
                                actionBtn.setVisibility(View.VISIBLE);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void shareEvent() {
        actionBtn.setVisibility(View.INVISIBLE);
        actionBtnProgressBar.setVisibility(View.VISIBLE);
        Task<ShortDynamicLink> shortDynamicLinkTask = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setLink(Uri.parse("https://eventauth.page.link?event_id=" + event.getEventId()))
                .setDomainUriPrefix("https://eventauth.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            assert shortLink != null;
                            Log.i("rk_debug", shortLink.toString());
                            Intent intent = new Intent(new Intent(Intent.ACTION_SEND));
                            intent.setType("text/plain");
                            String messageToSend = "Register in \"" + event.getName() + "\" Event.\n\n"
                                    + event.getSummary() + "\n\nRegister Now - " + shortLink.toString();
                            intent.putExtra(Intent.EXTRA_TEXT, messageToSend);
                            startActivity(Intent.createChooser(intent,"Share").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            actionBtn.setVisibility(View.VISIBLE);
                            actionBtnProgressBar.setVisibility(View.GONE);
                        } else {
                            // Error
                            // ...
                        }
                    }
                });

//        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
//                .setLink(Uri.parse("https://eventauth.page.link?event_id=" + event.getEventId()))
//                .setDomainUriPrefix("https://eventauth.page.link")
//                // Open links with this app on Android
//                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
//                .buildDynamicLink();
//
//        Uri dynamicLinkUri = dynamicLink.getUri();
//        Log.i("rk_debug", dynamicLinkUri.toString());


    }

    private void deleteEvent() {
        progressDialog = new ProgressDialog(EventRegistrationActivity.this);
        progressDialog.setMessage("Deleting Event");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String eventId = event.getEventId();
        Utils.firebaseDatabaseRef
                .child("Events")
                .orderByChild("id")
                .equalTo(eventId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                Log.i("rk_debug", dataSnapshot.getKey());
                                Utils.firebaseDatabaseRef
                                        .child("Events")
                                        .child(dataSnapshot.getKey())
                                        .removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(EventRegistrationActivity.this, "Event Deleted", Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("rk_debug", "pauseER");
    }

    private void checkLogin(String eventId) {
        if (Utils.firebaseAuth.getCurrentUser() == null) {
            Intent loginIntent = new Intent(EventRegistrationActivity.this, LoginActivity.class);
            loginIntent.putExtra("eventId", eventId);
            loginIntent.putExtra("isRedirected", true);
            startActivity(loginIntent);
            finish();
        }
        else {
            Log.d("rk_debug", "kartik");
        }
        return;
    }

    private void getEvent(String eventId) {
        Utils.firebaseDatabaseRef
                .child("Events")
                .orderByChild("id")
                .equalTo(eventId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Log.d("rk_debug", eventId);
                            for (DataSnapshot data: snapshot.getChildren()) {

                                eventKeyPoint = data.getKey();

                                event = new Event(data.child("id").getValue().toString(),
                                        data.child("name").getValue().toString(),
                                        data.child("about").getValue().toString(),
                                        data.child("date").getValue().toString(),
                                        data.child("time").getValue().toString(),
                                        data.child("cover_image").getValue().toString());

                                if (data.child("owner").getValue().toString().equals(Utils.firebaseAuth.getCurrentUser().getEmail())) {
                                    collabBtn.setVisibility(View.VISIBLE);
                                }

//                                checkLogin(event);
                                if (data.child("shared_with").exists()) {
                                    loadCollaborators(eventKeyPoint);
                                }
                                displayEventData(event);
                                updateRegisterStatus();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void addCollaborator(String email) {
        Utils.firebaseDatabaseRef
                .child("Events")
                .orderByChild("id")
                .equalTo(eventId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                String shared_with_new = "";
                                String shared_with_old = "";
                                Log.d("rk_debug", "onDataChange: " + eventKeyPoint);
                                if (dataSnapshot.child("shared_with").exists()) {
                                    shared_with_old = dataSnapshot.child("shared_with").getValue().toString();
                                }
                                shared_with_new = shared_with_old + email + ",";
                                Utils.firebaseDatabaseRef
                                        .child("Events")
                                        .child(eventKeyPoint)
                                        .child("shared_with")
                                        .setValue(shared_with_new);
//                                loadCollaborators(eventKeyPoint);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadCollaboratorsHelper(String data) {
        adapter.clear();
        collaborators.clear();
        Log.d("rk_debug", "loadCollaborators: " + data);
        List<String> emails = Arrays.asList(data.split(","));
        Utils.firebaseDatabaseRef
                .child("Users")
                .orderByChild("email")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Log.d("rk_debug", "onChildAdded_1: ");
                        Map<String, String> map = (Map<String, String>) snapshot.getValue();
                        if (emails.contains(map.get("email"))) {
                            collaborators.add(
                                    new Collaborator(map.get("name"), map.get("email"))
                            );
                        }
                        adapter.notifyDataSetChanged();
//                        if (adapter != null) {
//                            Log.d("rk_debug", "onChildAdded_2: ");
//                            adapter.notifyDataSetChanged();
//                        }
//                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        Log.d("rk_debug", "onChildRemoved: ");
                        int index = collaborators.indexOf(snapshot.getKey());
                        collaborators.remove(index);

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }



                });
    }

    public void loadCollaborators(String eventKeyPoint) {

        Log.d("rk_debug", "loadCollaborators: ");

        Utils.firebaseDatabaseRef
                .child("Events")
                .child(eventKeyPoint)
                .child("shared_with")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("rk_debug", "onDataChange: " + snapshot.getValue());
                        loadCollaboratorsHelper(snapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

//    private void loadCollaborators(String data) {
////        adapter.clear();
//        Log.d("rk_debug", "loadCollaborators: " + data);
//        List<String> emails = Arrays.asList(data.split(","));
//        Utils.firebaseDatabaseRef
//                .child("Users")
//                .orderByChild("email")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//    }
}