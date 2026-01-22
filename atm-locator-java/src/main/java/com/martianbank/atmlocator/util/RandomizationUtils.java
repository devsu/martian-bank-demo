package com.martianbank.atmlocator.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for randomization operations.
 */
public final class RandomizationUtils {

    private RandomizationUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Selects a random subset of items from the given list.
     *
     * @param items    the list of items to select from
     * @param maxCount the maximum number of items to return
     * @param <T>      the type of elements in the list
     * @return a new list containing up to maxCount randomly selected items,
     *         or an empty list if input is null or empty
     */
    public static <T> List<T> selectRandom(List<T> items, int maxCount) {
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }

        if (maxCount <= 0) {
            return new ArrayList<>();
        }

        // Create a copy to avoid modifying the original list
        List<T> shuffled = new ArrayList<>(items);
        Collections.shuffle(shuffled);

        // Return up to maxCount items
        int count = Math.min(maxCount, shuffled.size());
        return new ArrayList<>(shuffled.subList(0, count));
    }
}
