package com.example.mooderation.backend;

import com.google.android.gms.tasks.Task;

import java.util.List;

/**
 * Represents a connection to a list of items in the database
 *
 * @param <Item> The type of item
 */
public interface Repository<Item> {
    /**
     * Adds an item to the repository. This should be idempotent.
     * @param item The item to add.
     * @return A task which completes once the item has been added.
     */
    Task<Void> add(Item item);

    /**
     * Removes an item from the repository. This should be idempotent.
     * @param item The item to remove
     * @return A task which completes once the item has been removed.
     */
    Task<Void> remove(Item item);
}
