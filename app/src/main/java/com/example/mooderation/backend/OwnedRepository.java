package com.example.mooderation.backend;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

/**
 * A connection to one of many lists of items in the database, identified by owner
 *
 * @param <Owner> The type of owner of each item (e.g. Participant)
 * @param <Item> The type of each item
 */
public interface OwnedRepository<Owner, Item> {
    /**
     * Adds an item to the repository of some owner. This should be idempotent.
     * @param owner Owner of repository to register to.
     * @param item Item to register.
     * @return A task which completes once the item has been added.
     */
    Task<Void> add(Owner owner, Item item);

    /**
     * Removes an item from the repository of some owner. This should be idempotent.
     * @param owner Owner of repository to remove from.
     * @param item Item to remove.
     * @return A task which completes once the item has been removed.
     */
    Task<Void> remove(Owner owner, Item item);

    /**
     * Listen for changes to the repository of a given owner.
     * @param owner Owner to listen for changes.
     * @param listener Listener which is called once immediately and once whenever data changes.
     * @return A registration allowing the listener to be closed.
     */
    ListenerRegistration addListener(Owner owner, Listener<Item> listener);

    /**
     * A listener which listens for changes to a repository
     * @param <Item> Type of item in the repository
     */
    public interface Listener<Item> {
        /**
         * Called when repository data changes.
         * @param items The list of items in the repository.
         */
        void onDataChanged(List<Item> items);
    }
}
