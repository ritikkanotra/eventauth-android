package com.appodex.eventauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EventRegistrationActivity extends AppCompatActivity {

    private ImageView eventCoverImageView, backBtn;
    private MaterialButton actionBtn, delButton;
    private TextView eventNameTextView, dateTextView, timeTextView, aboutTextView;
    private ProgressBar actionBtnProgressBar;
    private View mainProgressBarLayout;
//    private ShimmerFrameLayout shimmerFrameEventCover, shimmerFrameEventDetails;
    int intentType;
    private AlertDialog deleteAlertDialog;
    private ProgressDialog progressDialog;


    Intent receivedIntent;

    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration);

        getSupportActionBar().hide();



        eventNameTextView = findViewById(R.id.tv_event_name);
        dateTextView = findViewById(R.id.tv_event_date);
        timeTextView = findViewById(R.id.tv_event_time);
        aboutTextView = findViewById(R.id.tv_event_about);
        eventCoverImageView = findViewById(R.id.iv_event_cover);
        actionBtnProgressBar = findViewById(R.id.pb_action_btn);
        mainProgressBarLayout = findViewById(R.id.pb_layout);
        delButton = findViewById(R.id.del_event_btn);
//        shimmerFrameEventCover = findViewById(R.id.shimmer_frame_event_cover);
//        shimmerFrameEventDetails = findViewById(R.id.shimmer_frame_event_details);

//        eventCoverImageView.setVisibility(View.INVISIBLE);
//        shimmerFrameEventCover.startShimmer();
//        shimmerFrameEventDetails.startShimmer();

        backBtn = findViewById(R.id.back_btn);

        mainProgressBarLayout.setVisibility(View.VISIBLE);

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
        event = (Event) receivedIntent.getSerializableExtra("event");

        Log.i("rk_debug", Integer.toString(intentType));


        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null) {
                        String eventId = Objects.requireNonNull(pendingDynamicLinkData
                                .getLink()).getQueryParameter("event_id");
                        Utils.firebaseDatabaseRef
                                .child("Events")
                                .orderByChild("id")
                                .equalTo(eventId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot data: snapshot.getChildren()) {

                                                event = new Event(data.child("id").getValue().toString(),
                                                        data.child("name").getValue().toString(),
                                                        data.child("about").getValue().toString(),
                                                        data.child("date").getValue().toString(),
                                                        data.child("time").getValue().toString(),
                                                        data.child("cover_image").getValue().toString());

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
                });



        
        actionBtn = findViewById(R.id.action_btn);

        actionBtn.setVisibility(View.INVISIBLE);
        actionBtnProgressBar.setVisibility(View.VISIBLE);

        if (event != null) {
            displayEventData(event);
            updateRegisterStatus();
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
                .into(eventCoverImageView);


        eventNameTextView.setText(event.getName()); ;
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


                                Utils.firebaseDatabaseRef
                                        .child("Users")
                                        .child(data.getKey())
                                        .child("registeredEvents")
                                        .child(event.getEventId())
                                        .updateChildren(map);

                                updateRegisterStatus();
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
                                        intent.putExtra("uniqueCode",
                                                Utils.registeredEventsUCode
                                                        .get(receivedIntent.getIntExtra("position", 0)));
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
                            intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
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

}