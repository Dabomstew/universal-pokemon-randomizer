package com.dabomstew.pkrandom;

import java.util.HashMap;
import java.util.Map;

public class Version {
    public static final int VERSION = 311; // Increment by 1 for new version
    public static final String VERSION_STRING = "3.2.0-dev";

    public static final Map<Integer,String> oldVersions = setupVersionsMap();

    private static Map<Integer,String> setupVersionsMap() {
        Map<Integer,String> map = new HashMap<>();

        map.put(100,"1.0.1a");
        map.put(102,"1.0.2a");
        map.put(110,"1.1.0");
        map.put(111,"1.1.1");
        map.put(112,"1.1.2");
        map.put(120,"1.2.0a");
        map.put(150,"1.5.0");
        map.put(160,"1.6.0a");
        map.put(161,"1.6.1");
        map.put(162,"1.6.2");
        map.put(163,"1.6.3b");
        map.put(170,"1.7.0b");
        map.put(171,"1.7.1");
        map.put(172,"1.7.2");

        // Latest version - when version is updated, add the old version as an explicit put
        map.put(VERSION, VERSION_STRING);

        return map;
    }
}
