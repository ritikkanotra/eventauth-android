package com.appodex.eventauth;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRCodeActivity extends AppCompatActivity {

    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        getSupportActionBar().hide();

        backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener(task -> {
            finish();
        });



        try {

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(getIntent().getStringExtra("uniqueCode"), BarcodeFormat.QR_CODE, 1600, 1600);
            ImageView QRImageView = findViewById(R.id.iv_qr);
            QRImageView.setImageBitmap(bitmap);

            Log.i("rk_debug", getIntent().getStringExtra("uniqueCode"));

        }
        catch (Exception e) {

        }

    }
}