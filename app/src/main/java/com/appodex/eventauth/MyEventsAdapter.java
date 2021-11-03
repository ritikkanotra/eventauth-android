package com.appodex.eventauth;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.zip.Inflater;

public class MyEventsAdapter extends RecyclerView.Adapter<MyEventsAdapter.MyEventHolder> {

    Context mContext;
    List<Event> mMyEventsList;

    public MyEventsAdapter(Context context, List<Event> myEventsList) {
        mContext = context;
        mMyEventsList = myEventsList;
    }


    @NonNull
    @Override
    public MyEventsAdapter.MyEventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.my_event_view, parent, false);
        return new MyEventHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyEventsAdapter.MyEventHolder holder, int position) {
        holder.nameTextView.setText(mMyEventsList.get(position).getName());
        holder.nameTextView.setSelected(true);
        holder.dateTextView.setText(mMyEventsList.get(position).getDate());
        Glide.with(mContext)
                .load(Uri.parse(mMyEventsList.get(position).getCoverPicUrl()))
                .placeholder(R.drawable.placeholder_event)
                .centerCrop().into(holder.coverImageView);
        holder.scanQrBtn.setOnClickListener(task -> {
            Intent scanIntent = new Intent(mContext, ScanQRActivity.class);
            scanIntent.putExtra("event_id", mMyEventsList.get(position).getEventId());
            mContext.startActivity(scanIntent);
        });

        holder.itemView.setOnClickListener(task -> {
            Intent intent = new Intent(mContext, EventRegistrationActivity.class);
            intent.putExtra("type", Utils.TYPE_SHARE);
            intent.putExtra("eventId", mMyEventsList.get(position).getEventId());
            intent.putExtra("position", position);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mMyEventsList.size();
    }

    public class MyEventHolder extends RecyclerView.ViewHolder{

        ImageView coverImageView;
        TextView nameTextView, dateTextView;
        ImageView scanQrBtn;

        public MyEventHolder(@NonNull View itemView) {
            super(itemView);

            coverImageView = itemView.findViewById(R.id.cover_image_view);
            nameTextView = itemView.findViewById(R.id.tv_event_name);
            dateTextView = itemView.findViewById(R.id.tv_event_date);
            scanQrBtn = itemView.findViewById(R.id.btn_scan_qr);

        }
    }
}
