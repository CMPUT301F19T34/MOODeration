package com.example.mooderation;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mooderation.backend.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Shows a list of follow requests and allows the user to accept or deny them.
 */
public class FollowRequestsActivity extends AppCompatActivity {
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_requests);

        List<FollowRequest> requestList = new ArrayList<>();
        ListView listView = findViewById(R.id.follow_request_list);
        ArrayAdapter<FollowRequest> listAdapter = new FollowRequestAdapter(this, requestList);
        listView.setAdapter(listAdapter);

        database = new Database();

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            FollowRequest request = listAdapter.getItem(i);
            if (request == null) {
                return;
            }
            builder.setTitle("Accept " + request.getUsername() + "'s follow request?")
                    .setMessage("The user " + request.getUsername() + " wants to follow your mood history. If you accept, they will be able to see the most recent mood event of your mood history in their feed.")
                    .setPositiveButton("Accept", (dialogInterface, i1) -> database.acceptFollowRequest(request))
                    .setNegativeButton("Deny", (dialogInterface, i12) -> database.denyFollowRequest(request))
                    .show();
        });

        database.addFollowRequestsListener(requests -> {
            requestList.clear();
            for (FollowRequest request : requests) {
                requestList.add(request);
            }
            Collections.sort(requestList, (lhs, rhs) -> {
                return -lhs.getCreateTimestamp().compareTo(rhs.getCreateTimestamp());
            });
            listAdapter.notifyDataSetChanged();
        });
    }
}