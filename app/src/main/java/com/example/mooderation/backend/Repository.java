package com.example.mooderation.backend;

import com.google.android.gms.tasks.Task;

import java.util.List;

public interface Repository<Item> {
    Task<Void> add(Item item);
    Task<Void> remove(Item item);

    interface Listener<Item> {
        void onDataChanged(List<Item> items);
    }
}
