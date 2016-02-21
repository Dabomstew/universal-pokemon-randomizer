package com.dabomstew.pkrandom.pokemon;

import java.util.Random;

public class ItemList {

    private boolean[] items;
    private boolean[] tms;

    public ItemList(int highestIndex) {
        items = new boolean[highestIndex + 1];
        tms = new boolean[highestIndex + 1];
        for (int i = 1; i <= highestIndex; i++) {
            items[i] = true;
        }
    }

    public boolean isTM(int index) {
        if (index < 0 || index >= tms.length) {
            return false;
        }
        return tms[index];
    }

    public boolean isAllowed(int index) {
        if (index < 0 || index >= tms.length) {
            return false;
        }
        return items[index];
    }

    public void banSingles(int... indexes) {
        for (int index : indexes) {
            items[index] = false;
        }
    }

    public void banRange(int startIndex, int length) {
        for (int i = 0; i < length; i++) {
            items[i + startIndex] = false;
        }
    }

    public void tmRange(int startIndex, int length) {
        for (int i = 0; i < length; i++) {
            tms[i + startIndex] = true;
        }
    }

    public int randomItem(Random random) {
        int chosen = 0;
        while (!items[chosen]) {
            chosen = random.nextInt(items.length);
        }
        return chosen;
    }

    public int randomNonTM(Random random) {
        int chosen = 0;
        while (!items[chosen] || tms[chosen]) {
            chosen = random.nextInt(items.length);
        }
        return chosen;
    }

    public int randomTM(Random random) {
        int chosen = 0;
        while (!tms[chosen]) {
            chosen = random.nextInt(items.length);
        }
        return chosen;
    }

    public ItemList copy() {
        ItemList other = new ItemList(items.length - 1);
        System.arraycopy(items, 0, other.items, 0, items.length);
        System.arraycopy(tms, 0, other.tms, 0, tms.length);
        return other;
    }

}
