package com.example.mooderation.backend;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

/**
 * Represents a connection to a list of items in the database
 *
 * @param <Item> The type of item
 */
public interface Repository<Item> {
    /**
     * Adds an item to the repository. This should be idempotent.
     * @param item The item to register.
     * @return A task which completes once the item has been added.
     */
    Task<Void> add(Item item);

    /**
     * Removes an item from the repository. This should be idempotent.
     * @param item The item to remove
     * @return A task which completes once the item has been removed.
     */
    Task<Void> remove(Item item);

    /**
     * Listen for changes to the repository.
     * @param listener Listener which is called once immediately and once whenever data changes.
     * @return A registration allowing the listener to be closed.
     */
    ListenerRegistration addListener(Listener<Item> listener);

    /**
     * A listener which listens for changes to a repository
     * @param <Item> Type of item in the repository
     */
    public interface Listener<Item> {
        /**
         * Called when repository data changes.
         *
         * @param items The list of items in the repository.
         */
        void onDataChanged(List<Item> items);
    }
}
