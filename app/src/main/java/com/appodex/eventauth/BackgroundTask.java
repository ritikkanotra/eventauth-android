package com.appodex.eventauth;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.ArrayList;

public class BackgroundTask extends AsyncTaskLoader<ArrayList<Collaborator>> {

    int mTask;

    public BackgroundTask(@NonNull Context context, int task) {
        super(context);
        mTask = task;
    }

    @Nullable
    @Override
    public ArrayList<Collaborator> loadInBackground() {

        ArrayList<Collaborator> temp = new ArrayList<>();
        temp.add(
                new Collaborator("A", "a@mail.com")
        );
        return temp;

    }
}
