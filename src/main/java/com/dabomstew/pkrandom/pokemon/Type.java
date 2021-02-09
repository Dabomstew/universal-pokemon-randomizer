package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  Type.java - represents a Pokemon or move type.                        --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer" by Dabomstew                   --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2012.                                 --*/
/*--                                                                        --*/
/*--  The custom code written here is licensed under the terms of the GPL:  --*/
/*--                                                                        --*/
/*--  This program is free software: you can redistribute it and/or modify  --*/
/*--  it under the terms of the GNU General Public License as published by  --*/
/*--  the Free Software Foundation, either version 3 of the License, or     --*/
/*--  (at your option) any later version.                                   --*/
/*--                                                                        --*/
/*--  This program is distributed in the hope that it will be useful,       --*/
/*--  but WITHOUT ANY WARRANTY; without even the implied warranty of        --*/
/*--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          --*/
/*--  GNU General Public License for more details.                          --*/
/*--                                                                        --*/
/*--  You should have received a copy of the GNU General Public License     --*/
/*--  along with this program. If not, see <http://www.gnu.org/licenses/>.  --*/
/*----------------------------------------------------------------------------*/

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.dabomstew.pkrandom.RomFunctions;
import com.dabomstew.pkrandom.exceptions.RandomizationException;

public enum Type {

    NORMAL, FIGHTING, FLYING, GRASS, WATER, FIRE, ROCK, GROUND, PSYCHIC, BUG, DRAGON, ELECTRIC, GHOST, POISON, ICE, STEEL, DARK, GAS(
            true), FAIRY(true), WOOD(true), ABNORMAL(true), WIND(true), SOUND(true), LIGHT(true), TRI(true);

    public boolean isHackOnly;

    private Type() {
        this.isHackOnly = false;
    }

    private Type(boolean isHackOnly) {
        this.isHackOnly = isHackOnly;
    }

    private static final List<Type> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static List<Type> shuffledList;

    public static final List<Type> STRONG_AGAINST_NORMAL = Arrays.asList(Type.FIGHTING);
    public static final List<Type> RESISTANT_TO_NORMAL = Arrays.asList(Type.ROCK, Type.STEEL, Type.GHOST);
    public static final List<Type> STRONG_AGAINST_FIGHTING = Arrays.asList(Type.FLYING, Type.PSYCHIC);
    public static final List<Type> RESISTANT_TO_FIGHTING = Arrays.asList(Type.POISON, Type.FLYING, Type.PSYCHIC, Type.BUG, Type.GHOST);
    public static final List<Type> STRONG_AGAINST_FLYING = Arrays.asList(Type.ELECTRIC, Type.ICE, Type.ROCK);
    public static final List<Type> RESISTANT_TO_FLYING = Arrays.asList(Type.ELECTRIC, Type.ROCK, Type.STEEL);
    public static final List<Type> STRONG_AGAINST_GRASS = Arrays.asList(Type.FIRE, Type.ICE, Type.POISON, Type.FLYING, Type.BUG);
    public static final List<Type> RESISTANT_TO_GRASS = Arrays.asList(Type.FIRE, Type.GRASS, Type.POISON, Type.FLYING, Type.BUG, Type.DRAGON, Type.STEEL);
    public static final List<Type> STRONG_AGAINST_WATER = Arrays.asList(Type.ELECTRIC, Type.GRASS);
    public static final List<Type> RESISTANT_TO_WATER = Arrays.asList(Type.WATER, Type.GRASS, Type.DRAGON);
    public static final List<Type> STRONG_AGAINST_FIRE = Arrays.asList(Type.WATER, Type.GROUND, Type.ROCK);
    public static final List<Type> RESISTANT_TO_FIRE = Arrays.asList(Type.FIRE, Type.WATER, Type.ROCK, Type.DRAGON);
    public static final List<Type> STRONG_AGAINST_ROCK = Arrays.asList(Type.WATER, Type.GRASS, Type.FIGHTING, Type.GROUND, Type.STEEL);
    public static final List<Type> RESISTANT_TO_ROCK = Arrays.asList(Type.FIGHTING, Type.GROUND, Type.STEEL);
    public static final List<Type> STRONG_AGAINST_GROUND = Arrays.asList(Type.WATER, Type.GRASS, Type.ICE);
    public static final List<Type> RESISTANT_TO_GROUND = Arrays.asList(Type.GRASS, Type.FLYING, Type.BUG);
    public static final List<Type> STRONG_AGAINST_PSYCHIC = Arrays.asList(Type.BUG, Type.GHOST, Type.DARK);
    public static final List<Type> RESISTANT_TO_PSYCHIC = Arrays.asList(Type.PSYCHIC, Type.DARK, Type.STEEL);
    public static final List<Type> STRONG_AGAINST_BUG = Arrays.asList(Type.FIRE, Type.FLYING, Type.ROCK);
    public static final List<Type> RESISTANT_TO_BUG = Arrays.asList(Type.FIRE, Type.FIGHTING, Type.POISON, Type.FLYING, Type.GHOST, Type.STEEL);
    public static final List<Type> STRONG_AGAINST_DRAGON = Arrays.asList(Type.ICE, Type.DRAGON);
    public static final List<Type> RESISTANT_TO_DRAGON = Arrays.asList(Type.STEEL);
    public static final List<Type> STRONG_AGAINST_ELECTRIC = Arrays.asList(Type.GROUND);
    public static final List<Type> RESISTANT_TO_ELECTRIC = Arrays.asList(Type.ELECTRIC, Type.GRASS, Type.GROUND, Type.DRAGON);
    public static final List<Type> STRONG_AGAINST_GHOST = Arrays.asList(Type.GHOST, Type.DARK);
    public static final List<Type> RESISTANT_TO_GHOST = Arrays.asList(Type.NORMAL, Type.DARK);
    public static final List<Type> STRONG_AGAINST_POISON = Arrays.asList(Type.GROUND, Type.PSYCHIC);
    public static final List<Type> RESISTANT_TO_POISON = Arrays.asList(Type.POISON, Type.GROUND, Type.ROCK, Type.GHOST, Type.STEEL);
    public static final List<Type> STRONG_AGAINST_ICE = Arrays.asList(Type.FIRE, Type.FIGHTING, Type.ROCK, Type.STEEL);
    public static final List<Type> RESISTANT_TO_ICE = Arrays.asList(Type.FIRE, Type.WATER, Type.ICE, Type.STEEL);
    public static final List<Type> STRONG_AGAINST_STEEL = Arrays.asList(Type.FIRE, Type.FIGHTING, Type.GROUND);
    public static final List<Type> RESISTANT_TO_STEEL = Arrays.asList(Type.FIRE, Type.WATER, Type.ELECTRIC, Type.STEEL);
    public static final List<Type> STRONG_AGAINST_DARK = Arrays.asList(Type.FIGHTING, Type.BUG);
    public static final List<Type> RESISTANT_TO_DARK = Arrays.asList(Type.FIGHTING, Type.DARK);
    // Ordering in this list must match ordering in the VALUES array
    public static final List<List<Type>> STRONG_AGAINST = Arrays.asList(
        STRONG_AGAINST_NORMAL,
        STRONG_AGAINST_FIGHTING,
        STRONG_AGAINST_FLYING,
        STRONG_AGAINST_GRASS,
        STRONG_AGAINST_WATER,
        STRONG_AGAINST_FIRE,
        STRONG_AGAINST_ROCK,
        STRONG_AGAINST_GROUND,
        STRONG_AGAINST_PSYCHIC,
        STRONG_AGAINST_BUG,
        STRONG_AGAINST_DRAGON,
        STRONG_AGAINST_ELECTRIC, 
        STRONG_AGAINST_GHOST,
        STRONG_AGAINST_POISON,
        STRONG_AGAINST_ICE,
        STRONG_AGAINST_STEEL,
        STRONG_AGAINST_DARK
    );
    // Ordering in this list must match ordering in the VALUES array
    public static final List<List<Type>> RESISTANT_TO = Arrays.asList(
        RESISTANT_TO_NORMAL,
        RESISTANT_TO_FIGHTING,
        RESISTANT_TO_FLYING,
        RESISTANT_TO_GRASS,
        RESISTANT_TO_WATER,
        RESISTANT_TO_FIRE,
        RESISTANT_TO_ROCK,
        RESISTANT_TO_GROUND,
        RESISTANT_TO_PSYCHIC,
        RESISTANT_TO_BUG,
        RESISTANT_TO_DRAGON,
        RESISTANT_TO_ELECTRIC, 
        RESISTANT_TO_GHOST,
        RESISTANT_TO_POISON,
        RESISTANT_TO_ICE,
        RESISTANT_TO_STEEL,
        RESISTANT_TO_DARK
    );

    public static Type randomType(Random random) {
        return VALUES.get(random.nextInt(SIZE));
    }

    public String camelCase() {
        return RomFunctions.camelCase(this.toString());
    }

    public static List<Type> getTypes(int size) {
        return VALUES.subList(0, size);
    }

    public static void setShuffledList(List<Type> list) {
        shuffledList = list;
    }

    public static List<Type> getShuffledList() {
        return shuffledList;
    }

    public static Type randomWeakness(Random random, boolean useResistantType, Type... checkTypes) {
        // Safety check since varargs allow zero arguments
        if (checkTypes.length < 1) {
            throw new RandomizationException("Must provide at least 1 type to obtain a weakness");
        }

        if (useResistantType) {
            return getWeaknessFromList(random, RESISTANT_TO, checkTypes);
        } else {
            return getWeaknessFromList(random, STRONG_AGAINST, checkTypes);           
        }
    }

    private static Type getWeaknessFromList(Random random, List<List<Type>> checkList, Type[] checkTypes) {
        List<Type> pickList = new ArrayList<Type>();
        boolean initialized = false;

        // Loop through all given types to reduce to a list of common weaknesses
        for (Type checkType: checkTypes) {
            // Make sure the type is not null
            if (checkType == null) {
                continue;
            }
            // Initialize the list
            // This can happen multiple times if "retainAll" clears the list
            // due to no shared weakness, such as Ghost/Dark
            if (pickList.size() < 1) {
                pickList.addAll(checkList.get(checkType.ordinal()));
                initialized = true;
            } 
            // Otherwise only keep types shared in both lists
            else {
                pickList.retainAll(checkList.get(checkType.ordinal()));
            }                
        }
        // If the list has elements in it still, pick one
        if (pickList.size() > 0) {
            return pickList.get(random.nextInt(pickList.size()));
        } 
        // Otherwise pick a random weakness for any of the types given
        else if (initialized) {
            Type randomType = checkTypes[random.nextInt(checkTypes.length)];
            List<Type> resistantList = checkList.get(randomType.ordinal());
            return resistantList.get(random.nextInt(resistantList.size()));
        }

        // No type was able to be returned so throw an exception
        throw new RandomizationException("No type weakness found for " + Arrays.toString(checkTypes));
    }

    public static List<Type> getWeaknesses(Type checkType, int maxNum) {    
        List<Type> checkList = STRONG_AGAINST.get(checkType.ordinal());
        return checkList.subList(0, maxNum > checkList.size() ? checkList.size() : maxNum);
    } 
}
