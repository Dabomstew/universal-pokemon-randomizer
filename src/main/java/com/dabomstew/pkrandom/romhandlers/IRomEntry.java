package com.dabomstew.pkrandom.romhandlers;

/**
 * Represents a RomEntry. Since each RomEntry is a private class, this gives
 * a hook that can call functions on it without having the concrete class
 */
public interface IRomEntry {
    public int getInt(String key);
    public String getRomCode();
}
