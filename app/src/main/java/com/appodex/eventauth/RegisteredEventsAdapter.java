package com.appodex.eventauth;

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

public class RegisteredEventsAdapter extends RecyclerView.Adapter<RegisteredEventsAdapter.RegisteredEventHolder> {

    private Context mContext;
    private List<Event> registeredEventsList;
    private List<String> registeredEventsUCode;

    public RegisteredEventsAdapter(Context mContext, List<Event> registeredEventsList, List<String> registeredEventsUCode) {
        this.mContext = mContext;
        this.registeredEventsList = registeredEventsList;
        this.registeredEventsUCode = registeredEventsUCode;
    }


    @NonNull
    @Override
    public RegisteredEventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.registered_event_view, parent, false);
        return new RegisteredEventHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegisteredEventHolder holder, int position) {
        holder.nameTextView.setText(registeredEventsList.get(position).getName());
        holder.dateTextView.setText(registeredEventsList.get(position).getDate());
        Glide.with(mContext)
                .load(Uri.parse(registeredEventsList.get(position).getCoverPicUrl()))
                .centerCrop().into(holder.coverImageView);

        holder.itemView.setOnClickListener(task -> {
            Intent intent = new Intent(mContext, EventRegistrationActivity.class);
            intent.putExtra("type", Utils.TYPE_REGISTER);
            intent.putExtra("event", registeredEventsList.get(position));
            intent.putExtra("position", position);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return registeredEventsList.size();
    }

    public class RegisteredEventHolder extends RecyclerView.ViewHolder{

        ImageView coverImageView;
        TextView nameTextView, dateTextView;

        public RegisteredEventHolder(@NonNull View itemView) {
            super(itemView);

            coverImageView = itemView.findViewById(R.id.cover_image_view);
            nameTextView = itemView.findViewById(R.id.tv_event_name);
            dateTextView = itemView.findViewById(R.id.tv_event_date);

        }
    }
}
