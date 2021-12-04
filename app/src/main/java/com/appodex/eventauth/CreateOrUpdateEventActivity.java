package com.appodex.eventauth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CreateOrUpdateEventActivity extends AppCompatActivity {

    private static final int EVENT_COVER_REQUEST_CODE = 1;

    Calendar eventDateCalender, eventTimeCalender;
    EditText eventDateEditText, eventTimeEditText, eventAboutEditText, eventNameEditText;
    ImageView eventCoverImageView, backBtn, doneBtn;
    TextView activityHeadingTextview;

    ProgressDialog progressDialog;


    private StorageReference firebaseStorageRef;

    String defaultCoverUrl = "https://firebasestorage.googleapis.com/v0/b/eventauth-c359b.appspot.com/o/placeholder_event.png?alt=media";
    String eventId;
    String eventKeyPoint;

    Uri eventCoverUri = Uri.parse(defaultCoverUrl);

    String eventDate, eventTime;

    private Event receivedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Event");
        progressDialog.setCancelable(false);

        firebaseStorageRef = FirebaseStorage.getInstance().getReference();

        eventAboutEditText = findViewById(R.id.et_event_about);
        eventCoverImageView = findViewById(R.id.iv_event_cover);
        eventNameEditText = findViewById(R.id.et_event_name);
        backBtn = findViewById(R.id.back_btn);
        activityHeadingTextview = findViewById(R.id.tv_heading);
        doneBtn = findViewById(R.id.btn_done);

        receivedEvent = (Event) getIntent().getSerializableExtra("event");


        backBtn.setOnClickListener(task -> {
            finish();
        });

        eventCoverImageView.setOnClickListener(task -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, EVENT_COVER_REQUEST_CODE);
        });



        eventDateCalender = Calendar.getInstance();

        eventDateEditText = findViewById(R.id.et_event_date);

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                eventDateCalender.set(Calendar.YEAR, year);
                eventDateCalender.set(Calendar.MONTH, month);
                eventDateCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

                eventDateEditText.setText(simpleDateFormat.format(eventDateCalender.getTime()));

            }
        };

        eventDateEditText.setOnClickListener(task -> {
            new DatePickerDialog(CreateOrUpdateEventActivity.this,
                    dateSetListener,
                    eventDateCalender.get(Calendar.YEAR),
                    eventDateCalender.get(Calendar.MONTH),
                    eventDateCalender.get(Calendar.DAY_OF_MONTH)).show();
        });

        eventTimeEditText = findViewById(R.id.et_event_time);

        eventTimeCalender = Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                eventTimeCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                eventTimeCalender.set(Calendar.MINUTE, minute);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.US);
                eventTimeEditText.setText(simpleDateFormat.format(eventTimeCalender.getTime()));

            }
        };

        eventTimeEditText.setOnClickListener(task -> {
            new TimePickerDialog(CreateOrUpdateEventActivity.this,
                    timeSetListener,
                    eventTimeCalender.get(Calendar.HOUR_OF_DAY),
                    eventTimeCalender.get(Calendar.MINUTE),
                    false).show();
        });

        if (receivedEvent != null) {
            eventId = receivedEvent.getEventId();
            Log.d("rk_debug", "receivedEvent: is not null");
            populateEventData();
            activityHeadingTextview.setText("Update Event");
            doneBtn.setOnClickListener(task -> {
                updateEventDone();
            });
            progressDialog.setMessage("Updating event");
            eventCoverImageView.setOnClickListener(null);
        }
        else {
            doneBtn.setOnClickListener(task -> {
                createEventDone();
            });
        }

        Glide.with(this).load(eventCoverUri).centerCrop().into(eventCoverImageView);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EVENT_COVER_REQUEST_CODE && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
                eventCoverUri = data.getData();
                Glide.with(this).load(eventCoverUri).centerCrop().into(eventCoverImageView);
        }
    }

    public void createEventDone() {


        progressDialog.show();



        Log.i("rk_debug", eventCoverUri.toString());
        Log.i("rk_debug", Uri.parse(defaultCoverUrl).toString());

        if (receivedEvent == null) {
            eventId = UUID.randomUUID().toString().replaceAll("-", "");
        }

        if (eventCoverUri.toString().equals(Uri.parse(defaultCoverUrl).toString())) {
            regToDb(defaultCoverUrl);
        }
        else {
            Utils.firebaseStorageRef
                    .child("cover_images")
                    .child(eventId + ".jpg")
                    .putFile(eventCoverUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Log.i("rk_debug_curl", task.getResult().getUploadSessionUri().toString());
                            Log.i("rk_debug_curl", task.getResult().getMetadata().getBucket());
                            Log.i("rk_debug_curl", task.getResult().getMetadata().getReference().toString());
                            regToDb("https://firebasestorage.googleapis.com/v0/b/eventauth-c359b.appspot.com/o/cover_images%2F" + eventId + ".jpg?alt=media");
                        }
                    });
        }




    }

    private void regToDb(String coverDownloadUrl) {

        String eventName = eventNameEditText.getText().toString();
        String eventDate = eventDateEditText.getText().toString();
        String eventTime = eventTimeEditText.getText().toString();
        String eventAbout = eventAboutEditText.getText().toString();


        Map<String, String> newEventInfo = new HashMap<>();

        newEventInfo.put("name", eventName);
        newEventInfo.put("date", eventDate);
        newEventInfo.put("time", eventTime);
        newEventInfo.put("about", eventAbout);
        newEventInfo.put("cover_image", coverDownloadUrl);
        newEventInfo.put("id", eventId);
        newEventInfo.put("owner", Utils.firebaseAuth.getCurrentUser().getEmail());
        newEventInfo.put("shared_with", "");

        if (receivedEvent == null) {
            Utils.firebaseDatabaseRef
                    .child("Events")
                    .push()
                    .setValue(newEventInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Event Created", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Error! Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            updateEventDoneHelper(coverDownloadUrl);
        }
    }

    private void updateEventDoneHelper(String coverDownloadUrl) {

        Utils.firebaseDatabaseRef
                .child("Events")
                .child(eventKeyPoint)
                .child("about")
                .setValue(eventAboutEditText.getText().toString());
        Utils.firebaseDatabaseRef
                .child("Events")
                .child(eventKeyPoint)
                .child("cover_image")
                .setValue(coverDownloadUrl);
        Utils.firebaseDatabaseRef
                .child("Events")
                .child(eventKeyPoint)
                .child("date")
                .setValue(eventDateEditText.getText().toString());
        Utils.firebaseDatabaseRef
                .child("Events")
                .child(eventKeyPoint)
                .child("name")
                .setValue(eventNameEditText.getText().toString());
        Utils.firebaseDatabaseRef
                .child("Events")
                .child(eventKeyPoint)
                .child("time")
                .setValue(eventTimeEditText.getText().toString());

        Toast.makeText(CreateOrUpdateEventActivity.this, "Event updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void updateEventDone() {

//        eventId = receivedEvent.getEventId();

        Utils.firebaseDatabaseRef
                .child("Events")
                .orderByChild("id")
                .equalTo(eventId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Log.d("rk_debug_update", eventId);
                            for (DataSnapshot data: snapshot.getChildren()) {

                                eventKeyPoint = data.getKey();
                                Log.d("rk_debug_update", eventKeyPoint);
//                                updateEventDoneHelper(eventKeyPoint);
                                createEventDone();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void populateEventData() {
        eventNameEditText.setText(receivedEvent.getName());
        eventAboutEditText.setText(receivedEvent.getSummary());
        eventDateEditText.setText(receivedEvent.getDate());
        eventTimeEditText.setText(receivedEvent.getTime());
        defaultCoverUrl = receivedEvent.getCoverPicUrl();
        eventCoverUri = Uri.parse(defaultCoverUrl);

    }


}