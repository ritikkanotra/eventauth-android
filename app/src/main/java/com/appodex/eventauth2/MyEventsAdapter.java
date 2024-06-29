package com.appodex.eventauth2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

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
        Event event = mMyEventsList.get(position);
        holder.nameTextView.setText(event.getName());
        holder.nameTextView.setSelected(true);
        holder.dateTextView.setText(event.getDate());
        Glide.with(mContext)
                .load(Uri.parse(event.getCoverPicUrl()))
                .placeholder(R.drawable.placeholder_event)
                .centerCrop().into(holder.coverImageView);
        holder.scanQrBtn.setOnClickListener(task -> {
            Intent scanIntent = new Intent(mContext, ScanQRActivity.class);
            scanIntent.putExtra("event_id", event.getEventId());
            mContext.startActivity(scanIntent);
        });

        holder.itemView.setOnClickListener(task -> {
            Intent intent = new Intent(mContext, EventRegistrationActivity.class);
            intent.putExtra("type", Utils.TYPE_SHARE);
            intent.putExtra("eventId", event.getEventId());
            intent.putExtra("position", position);
            mContext.startActivity(intent);
        });

        if (event.getSharedBy() == null) {
            holder.sharedByTextView.setVisibility(View.GONE);
        }
        else {
            holder.sharedByTextView.setVisibility(View.VISIBLE);
            holder.sharedByTextView.setText("Shared by " + event.getSharedBy());
        }
    }

    @Override
    public int getItemCount() {
        return mMyEventsList.size();
    }

    public class MyEventHolder extends RecyclerView.ViewHolder{

        ImageView coverImageView;
        TextView nameTextView, dateTextView, sharedByTextView;
        ImageView scanQrBtn;

        public MyEventHolder(@NonNull View itemView) {
            super(itemView);

            coverImageView = itemView.findViewById(R.id.cover_image_view);
            nameTextView = itemView.findViewById(R.id.tv_event_name);
            dateTextView = itemView.findViewById(R.id.tv_event_date);
            scanQrBtn = itemView.findViewById(R.id.btn_scan_qr);
            sharedByTextView = itemView.findViewById(R.id.tv_shared_by);

        }
    }
}
