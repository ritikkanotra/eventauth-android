package com.appodex.eventauth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.util.ArrayList;

public class CollaboratorsFragment extends Fragment {


    private CollaboratorsListAdapter mAdapter;
    private ListView listView;

    public CollaboratorsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_collab_popup, container, false);

        listView = view.findViewById(R.id.collaborators_list_view);

//        LoaderManager loaderManager = getLoaderManager();
//        loaderManager.initLoader(1, null, CollaboratorsFragment.this);

//        ArrayList<Collaborator> collaborators = new ArrayList<>();

        Log.d("rk_debug", "count_this : " + 13234);

        ArrayList<String> data = new ArrayList<>();
        data.add("Ritik");
        data.add("Kanotra");

//        mAdapter = new CollaboratorsListAdapter(getContext(), collaborators);
//        mAdapter.addAll(collaborators);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, data);

//        collaborators.add(
//                new Collaborator("a", "a@gmail.com")
//        );
        listView.setAdapter(adapter);
//        Log.d("rk_debug", Integer.toString(collaborators.size()));
//        mAdapter.addAll(collaborators);

        Log.d("rk_debug", "count: " + listView.getAdapter().getCount());

        return view;
    }

//    @NonNull
//    @Override
//    public Loader<ArrayList<Collaborator>> onCreateLoader(int id, @Nullable Bundle args) {
//
//        return new BackgroundTask(getContext(), 1);
//    }
//
//    @Override
//    public void onLoadFinished(@NonNull Loader<ArrayList<Collaborator>> loader, ArrayList<Collaborator> data) {
//        mAdapter.clear();
//        if (data != null && !data.isEmpty()) {
//            mAdapter.addAll(data);
//        }
//    }
//
//    @Override
//    public void onLoaderReset(@NonNull Loader<ArrayList<Collaborator>> loader) {
//        mAdapter.clear();
//    }
}
