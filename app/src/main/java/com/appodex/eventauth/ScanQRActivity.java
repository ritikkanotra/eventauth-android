package com.appodex.eventauth;

import android.animation.Animator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanQRActivity extends Activity {

    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;

    private String eventId;

    private LottieAnimationView lottieViewQRCheck;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }

            lastText = result.getText();
            barcodeView.setStatusText(result.getText());

            beepManager.playBeepSoundAndVibrate();

            //Added preview of scanned barcode
            ImageView imageView = findViewById(R.id.barcodePreview);
            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));

            barcodeView.pause();
            checkAuthenticity(result.getText());
//            showCheckQRSuccess();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan_qr);

        barcodeView = findViewById(R.id.barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);

        eventId = getIntent().getStringExtra("event_id");

        beepManager = new BeepManager(this);

        lottieViewQRCheck = findViewById(R.id.lottie_view_qr_check);
        lottieViewQRCheck.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    public void showCheckQRSuccess() {
        lottieViewQRCheck.setAnimationFromUrl("https://assets8.lottiefiles.com/temp/lf20_5tgmik.json");
        lottieViewQRCheck.setVisibility(View.VISIBLE);
        lottieViewQRCheck.playAnimation();
//        lottieViewQRCheck.loop(false);
        lottieViewQRCheck.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                lottieViewQRCheck.cancelAnimation();
                lottieViewQRCheck.setVisibility(View.GONE);
                barcodeView.resume();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void showCheckQRFailure() {
        lottieViewQRCheck.setAnimationFromUrl("https://assets8.lottiefiles.com/temp/lf20_yYJhpG.json");
        lottieViewQRCheck.setVisibility(View.VISIBLE);
        lottieViewQRCheck.playAnimation();
//        lottieViewQRCheck.loop(false);
        lottieViewQRCheck.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                lottieViewQRCheck.cancelAnimation();
                lottieViewQRCheck.setVisibility(View.GONE);
                barcodeView.resume();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void showCheckQRWarning() {
        lottieViewQRCheck.setAnimationFromUrl("https://assets2.lottiefiles.com/packages/lf20_R8U2gr.json");
        lottieViewQRCheck.setVisibility(View.VISIBLE);
        lottieViewQRCheck.playAnimation();
//        lottieViewQRCheck.loop(false);
        lottieViewQRCheck.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                lottieViewQRCheck.cancelAnimation();
                lottieViewQRCheck.setVisibility(View.GONE);
                barcodeView.resume();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }



    private void checkAuthenticity(String scannedCode) {
        String uid = scannedCode.replace(eventId, "");
        Log.i("rk_debug_event_id", eventId);
        Log.i("rk_debug_uid", uid);
        Log.i("rk_debug_uid", Utils.firebaseAuth.getCurrentUser().getUid());
        Utils.firebaseDatabaseRef
                .child("Users")
                .orderByChild("uid")
                .equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot data: snapshot.getChildren()) {
                                Log.i("rk_debug", data.child("email").getValue().toString());
                                DataSnapshot event = data.child("registeredEvents").child(eventId);
                                if ( event.exists() ) {
                                    Log.i("rk_debug", "exists");

                                    if (event.child("attendance").getValue().toString().equals("0")) {

                                        Map map = new HashMap<>();
                                        map.put("attendance", 1);


                                        Utils.firebaseDatabaseRef
                                                .child("Users")
                                                .child(data.getKey())
                                                .child("registeredEvents")
                                                .child(eventId)
                                                .updateChildren(map);

                                        showCheckQRSuccess();

                                    }
                                    else {
                                        showCheckQRWarning();
                                        Toast.makeText(getApplicationContext(), "User already checked in", Toast.LENGTH_LONG).show();
                                    }

                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "User not registered", Toast.LENGTH_LONG).show();
                                    showCheckQRFailure();
                                }
                            }
                        }
                        else {
                            Log.i("rk_debug", "no_snapshot");
                            for (String id: Utils.registeredEventsUCode) {
                                Log.i("rk_debug_re_ids", id);
                            }
                            showCheckQRFailure();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}
