package com.example.mooderation.backend;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public interface OwnedRepository<Owner, Item> {
    Task<Void> add(Owner owner, Item item);
    Task<Void> remove(Owner owner, Item item);
    ListenerRegistration addListener(Owner owner, Listener<Item> listener);

    public interface Listener<Item> {
        void onDataChanged(List<Item> items);
    }
}
