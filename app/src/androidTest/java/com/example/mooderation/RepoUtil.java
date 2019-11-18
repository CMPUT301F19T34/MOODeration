package com.example.mooderation;

/**
 * Some utilities for testing code with OwnedRepositories. Since I don't know enough about when
 * firebase event callbacks are called, this code might contain null value errors, race
 * conditions, etc. Use with caution.
 */
public class RepoUtil {
//    public static <Owner, Item> List<Item> get(OwnedRepository<Owner, Item> repo, Owner owner) throws ExecutionException, InterruptedException {
//        TaskCompletionSource<List<Item>> source = new TaskCompletionSource<>();
//        ListenerRegistration reg;
//        reg = repo.addListener(owner, items -> {
//            source.setResult(items);
//        });
//        List<Item> result = Tasks.await(source.getTask());
//        reg.remove();
//        return result;
//    }
//
//    public static <Owner, Item> boolean contains(OwnedRepository<Owner, Item> repo, Owner owner, Item item) throws ExecutionException, InterruptedException {
//        return get(repo, owner).contains(item);
//    }
}
