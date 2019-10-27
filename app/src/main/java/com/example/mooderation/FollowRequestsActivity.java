package com.example.mooderation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FollowRequestsActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_requests);

        ListView listView = findViewById(R.id.follow_request_list);
        List<Participant> participantList = new ArrayList<>();

        ArrayAdapter listAdapter = new FollowRequestAdapter(this, participantList);
        listView.setAdapter(listAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        FollowRequests followRequests = new FollowRequests(this, db, user);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            Participant participant = (Participant) listAdapter.getItem(i);
            builder.setTitle("Accept " + participant.getUsername() + "'s follow request?")
                    .setMessage("The user " + participant.getUsername() + " wants to follow your mood history. If you accept, they will be able to see the most recent mood event of your mood history in their feed.")
                    .setPositiveButton("Accept", (dialogInterface, i1) -> followRequests.acceptRequest(participant))
                    .setNegativeButton("Deny", (dialogInterface, i12) -> followRequests.denyRequest(participant))
                    .show();
        });

        followRequests.addOnDataChangedListener(follow_requests -> {
            participantList.clear();
            for (Participant p: follow_requests) {
                participantList.add(p);
            }
            listAdapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}


class FollowRequests {

    private static final String TAG = "FollowRequests";
    private FirebaseFirestore db;
    private FirebaseUser user;
    private DocumentReference userDocument;
    private CollectionReference userFollowRequests;
    private Context context;

    FollowRequests(@NonNull Context context, @NonNull FirebaseFirestore db, @NonNull FirebaseUser user) {
        this.context = context;
        this.db = db;
        this.user = user;
        userDocument = db.collection("users").document(user.getUid());
        userFollowRequests = userDocument.collection("follow_requests");
    }

    public void acceptRequest(Participant other) {
        WriteBatch batch = db.batch();
        batch.delete(userFollowRequests.document(other.getUid()));
        batch.set(
                userDocument
                        .collection("followers")
                        .document(other.getUid()),
                new HashMap<String, Object>()
        );
        batch.commit().addOnFailureListener(e -> {
            Toast.makeText(context, "Error accepting request!", Toast.LENGTH_LONG).show();
        });
    }

    public void denyRequest(Participant other) {
        userFollowRequests
                .document(other.getUid())
                .delete()
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error denying request!", Toast.LENGTH_LONG).show();
                });
    }

    public interface DataChangedListener {
        void onDataChanged(List<Participant> follow_requests);
    }

    public void addOnDataChangedListener(DataChangedListener listener) {
        userFollowRequests.addSnapshotListener((queryDocumentSnapshots, e) -> {
            List<String> ids = new ArrayList<>();
            for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                ids.add(doc.getId());
            }

            List<Participant> participants = new ArrayList<>();

            participants.clear();
            listener.onDataChanged(participants);

            for (String id: ids) {
                db.collection("users").document(id).get().addOnSuccessListener(documentSnapshot -> {
                    String username = documentSnapshot.getString("username");
                    synchronized (participants) {
                        participants.add(new Participant(id, username));
                        listener.onDataChanged(participants);
                    }
                }).addOnFailureListener(e1 -> Log.e(TAG, "Failed to load request: " + e1.toString()));
            }
        });
    }
}