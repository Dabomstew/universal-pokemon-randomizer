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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.dabomstew.pkrandom.RomFunctions;

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

    public static Type randomType(Random random) {
        return VALUES.get(random.nextInt(SIZE));
    }

    public String camelCase() {
        return RomFunctions.camelCase(this.toString());
    }

    // This is a map of related type occurances
    // e.g. Rock types usually can be found with Ground or steel types in one area
    // Non reflexive relation.
    // The Doubles indicate how close they are related 1 is same type 0 is completely unrelated
    // I pulled these weights out of my arse according to my gut feeling
    // Not really reliable numbers
    @SuppressWarnings("serial")
    public static final Map<Type, Map<Type, Double>> occuranceRelations = Collections.unmodifiableMap(
        new HashMap<Type, Map<Type, Double>>() {{
            // Normal pokemon: usually grass together with everything "non
            // special" (mostly birds)
            put(NORMAL, new HashMap<Type, Double>() {{
                put(NORMAL, Double.valueOf(1));
                put(FLYING, Double.valueOf(0.6));
                put(BUG, Double.valueOf(0.4));
                put(FIRE, Double.valueOf(0.2));
                put(GRASS, Double.valueOf(0.2));
                put(ELECTRIC, Double.valueOf(0.2));
                put(PSYCHIC, Double.valueOf(0.05));
                }
            });
            // Fighting: Often in caves with rock, ground and steel
            put(FIGHTING, new HashMap<Type, Double>() {{
                put(FIGHTING, Double.valueOf(1));
                put(ROCK, Double.valueOf(0.6));
                put(GROUND, Double.valueOf(0.6));
                put(STEEL, Double.valueOf(0.6));
                put(POISON, Double.valueOf(0.2));
                put(DARK, Double.valueOf(0.1));
                put(DRAGON, Double.valueOf(0.05));
            }});
            // Flying: near normals, as well as Bugs (birds need to eat)
            put(FLYING, new HashMap<Type, Double>() {{
                put(FLYING, Double.valueOf(1));
                put(NORMAL, Double.valueOf(0.6));
                put(BUG, Double.valueOf(0.6));
            }});
            // Grass: Bug, Poison, Normal
            put(GRASS, new HashMap<Type, Double>() {{
                put(GRASS, Double.valueOf(1));
                put(NORMAL, Double.valueOf(0.6));
                put(BUG, Double.valueOf(0.4));
                put(POISON, Double.valueOf(0.4));
            }});
            // Water: Water (all ice types that occure with water types are
            // usually also water types)
            put(WATER, new HashMap<Type, Double>() {{
                put(WATER, Double.valueOf(1));
                put(DRAGON, Double.valueOf(0.05));
            }});
            // Fire: Normal
            put(FIRE, new HashMap<Type, Double>() {{
                put(FIRE, Double.valueOf(1));
                put(NORMAL, Double.valueOf(0.5));
            }});
            // Rock: Caves -> same as Fighting
            put(ROCK, new HashMap<Type, Double>() {{
                put(ROCK, Double.valueOf(1));
                put(FIGHTING, Double.valueOf(0.6));
                put(GROUND, Double.valueOf(0.6));
                put(STEEL, Double.valueOf(0.6));
                put(POISON, Double.valueOf(0.2));
                put(DARK, Double.valueOf(0.1));
                put(DRAGON, Double.valueOf(0.05));
            }});
            // Ground: Same as Rocks
            put(GROUND, new HashMap<Type, Double>() {{
                put(GROUND, Double.valueOf(1));
                put(FIGHTING, Double.valueOf(0.6));
                put(ROCK, Double.valueOf(0.6));
                put(STEEL, Double.valueOf(0.6));
                put(POISON, Double.valueOf(0.2));
                put(DARK, Double.valueOf(0.1));
                put(DRAGON, Double.valueOf(0.05));
            }});
            // Psychic: Dark, Ghost, normal
            put(PSYCHIC, new HashMap<Type, Double>() {{
                put(PSYCHIC, Double.valueOf(1));
                put(NORMAL, Double.valueOf(0.4));
                put(GHOST, Double.valueOf(0.2));
                put(DARK, Double.valueOf(0.1));
            }});
            // Bug: Same as grass
            put(BUG, new HashMap<Type, Double>() {{
                put(BUG, Double.valueOf(1));
                put(NORMAL, Double.valueOf(0.6));
                put(GRASS, Double.valueOf(0.4));
                put(POISON, Double.valueOf(0.4));
            }});
            // Dragon: Usually in caves, often also water
            put(DRAGON, new HashMap<Type, Double>() {{
                put(DRAGON, Double.valueOf(1));
                put(WATER, Double.valueOf(0.1));
                put(GROUND, Double.valueOf(0.1));
                put(ROCK, Double.valueOf(0.1));
            }});
            // Electric: Normal, Steel
            put(ELECTRIC, new HashMap<Type, Double>() {{
                put(ELECTRIC, Double.valueOf(1));
                put(NORMAL, Double.valueOf(0.4));
                put(STEEL, Double.valueOf(0.1));
            }});
            // Ghost: Poison, Dark, Psychic
            put(GHOST, new HashMap<Type, Double>() {{
                put(GHOST, Double.valueOf(1));
                put(DARK, Double.valueOf(0.6));
                put(POISON, Double.valueOf(0.1));
                put(PSYCHIC, Double.valueOf(0.1));
            }});
            // Dark: Ghost, Poison, psychic
            put(DARK, new HashMap<Type, Double>() {{
                put(DARK, Double.valueOf(1));
                put(GHOST, Double.valueOf(0.6));
                put(POISON, Double.valueOf(0.1));
                put(PSYCHIC, Double.valueOf(0.1));
            }});
            // Poison: Bugs, grass, Ghost, Caves
            put(POISON, new HashMap<Type, Double>() {{
                put(POISON, Double.valueOf(1));
                put(BUG, Double.valueOf(0.4));
                put(GRASS, Double.valueOf(0.4));
                put(GHOST, Double.valueOf(0.2));
                put(GROUND, Double.valueOf(0.1));
                put(ROCK, Double.valueOf(0.1));
            }});
            // Ice: Mostly water, sometimes in caves
            put(ICE, new HashMap<Type, Double>() {{
                put(ICE, Double.valueOf(1));
                put(WATER, Double.valueOf(0.8));
                put(GROUND, Double.valueOf(0.1));
                put(DARK, Double.valueOf(0.1));
            }});
            // Steel: same as rock
            put(STEEL, new HashMap<Type, Double>() {{
                put(STEEL, Double.valueOf(1));
                put(FIGHTING, Double.valueOf(0.6));
                put(ROCK, Double.valueOf(0.6));
                put(STEEL, Double.valueOf(0.6));
                put(POISON, Double.valueOf(0.2));
                put(DARK, Double.valueOf(0.1));
                put(DRAGON, Double.valueOf(0.05));
            }});
    }});
    
    public double getOccuranceDistance(Type rhs) {
        Map<Type, Double> rel = occuranceRelations.get(this);
        if (rel == null) {
            // TBH i have no fucking clue how to handle these types
            return 0;
        }
        // Distance is 1 - weight (so same type = distance 0)
        return 1 - (rel.getOrDefault(rhs, Double.valueOf(0)).doubleValue());
    }

}
