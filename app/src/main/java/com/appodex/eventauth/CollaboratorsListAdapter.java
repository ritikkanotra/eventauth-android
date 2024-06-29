package com.appodex.eventauth;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CollaboratorsListAdapter extends ArrayAdapter<Collaborator> {

    private Context mContext;

    public CollaboratorsListAdapter(Context context, ArrayList<Collaborator> collaborators) {
        super(context, 0, collaborators);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;

        if (listView == null) {
            listView = LayoutInflater.from(mContext).inflate(R.layout.collaborators_list_item, parent, false);
        }

        Collaborator currentCollaborator = getItem(position);

        TextView emailTextView =  listView.findViewById(R.id.tv_email);
        emailTextView.setText(currentCollaborator.getEmail());

        TextView nameTextView = listView.findViewById(R.id.tv_name);
        nameTextView.setText(currentCollaborator.getName());

        ImageView removeBtn = listView.findViewById(R.id.remove_btn);
        removeBtn.setOnClickListener(task -> {
            removeCollaborator(currentCollaborator.getEmail());
        });


        return listView;
    }

    private void removeCollaborator(String email) {
        Utils.firebaseDatabaseRef
                .child("Events")
                .orderByChild("id")
                .equalTo(EventRegistrationActivity.eventId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                String shared_with_new = "";
                                String shared_with_old = "";
                                Log.d("rk_debug", "onDataChange: " + dataSnapshot.getKey());
                                if (dataSnapshot.child("shared_with").exists()) {
                                    shared_with_old = dataSnapshot.child("shared_with").getValue().toString();
                                }

                                shared_with_new = shared_with_old.replace(email + "," , "");
                                Utils.firebaseDatabaseRef
                                        .child("Events")
                                        .child(dataSnapshot.getKey())
                                        .child("shared_with")
                                        .setValue(shared_with_new);
//                                new EventRegistrationActivity().loadCollaborators(EventRegistrationActivity.eventKeyPoint);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
