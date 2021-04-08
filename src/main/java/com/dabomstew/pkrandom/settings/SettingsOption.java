package com.dabomstew.pkrandom.settings;

/*----------------------------------------------------------------------------*/
/*--  SettingsOption.java - Provides an interface to use with any Settings  --*/
/*--                        object while enabling each object to be defined --*/
/*--                        with functions that work with its value class   --*/
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import com.dabomstew.pkrandom.pokemon.GenRestrictions;

public interface SettingsOption<T> {
    public String getName();
    public T getItem();
    public void setItem(T item);
    public Boolean isChild();
    public void setIsChild(Boolean bool);
    public ArrayList<PredicatePair> getMatches();
    public void randomValue(Random random);
    public void attemptRandomValue(Random random, SettingsOption item);

    public static class Builder {
        // Required parameters
        private String name;
        private Object value;

        // Optional parameters
        private PredicatePair[] matches;
        private IntStream validInts;

        public Builder(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public Builder addMatches(PredicatePair... matches) {
            this.matches = matches;
            return this;
        }

        public Builder addValidInts(IntStream validInts) {
            this.validInts = validInts;
            return this;
        }

        public SettingsOptionComposite build() {
            if (value instanceof Boolean) {
                return new SettingsOptionComposite<Boolean>(new BooleanSettingsOption(name, (Boolean)value, matches));
            } else if (value instanceof GenRestrictions) {
                return new SettingsOptionComposite<GenRestrictions>(new GenRestrictionsSettingsOption(name, (GenRestrictions)value, matches));
            } else if (value instanceof Enum) {
                return new SettingsOptionComposite<Enum>(new EnumSettingsOption(name, (Enum)value, matches));
            } else if (value instanceof int[]) {
                return new SettingsOptionComposite<int[]>(new IntArraySettingsOption(name, (int[])value, validInts, matches));
            } else if (value instanceof Integer) {
                return new SettingsOptionComposite<Integer>(new IntSettingsOption(name, (Integer)value, validInts, matches));
            } else {
                throw new RuntimeException(value.getClass() + " has no supported factory.");
            }
        }
    }
}

abstract class AbstractSettingsOption<T> implements SettingsOption<T> {

    protected String name;
    protected T defaultValue;
    protected T value;
    protected Boolean isChild;
    protected ArrayList<PredicatePair> matches;

    protected AbstractSettingsOption(String name, T value, PredicatePair... matches) {
        this.name = name;
        this.defaultValue = value;
        this.value = value;
        this.isChild = true;
        if (matches != null) {
            this.matches = new ArrayList<PredicatePair>(Arrays.asList(matches));
        } else {
            this.matches = new ArrayList<PredicatePair>();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public T getItem() {
        return value;
    }

    @Override
    public void setItem(T item) {
        this.value = item;
    }

    @Override
    public Boolean isChild() {
        return isChild;
    }

    @Override
    public void setIsChild(Boolean value) {
        this.isChild = value;
    }

    @Override
    public ArrayList<PredicatePair> getMatches() {
        return matches;
    }

    @Override
    public void attemptRandomValue(Random random, SettingsOption item) {
        if (matches.stream().anyMatch((match) -> match.test(item))) {
            randomValue(random);
        } else {
            setItem(defaultValue);
        }
    }
}

class BooleanSettingsOption extends AbstractSettingsOption<Boolean> {

    public BooleanSettingsOption(String name, Boolean value, PredicatePair... matches) {
        super(name, value, matches);
    }

    @Override
    public void randomValue(Random random) {
        setItem(random.nextInt(2) > 0 ? true : false);    
    }    
}

class EnumSettingsOption extends AbstractSettingsOption<Enum> {

    public EnumSettingsOption(String name, Enum value, PredicatePair... matches) {
        super(name, value, matches);
    }

    /**
     * Uses Java reflection to get the values of the actual Enum
     */
    @Override
    public void randomValue(Random random) {
        Enum[] values = ((Enum)value).getClass().getEnumConstants();
        value = values[random.nextInt(values.length)];        
    }    
}

class IntArraySettingsOption extends AbstractSettingsOption<int[]> {

    private IntStream allowedValues;

    public IntArraySettingsOption(String name, int[] value, IntStream allowedValues, PredicatePair... matches) {
        super(name, value, matches);
        if (allowedValues == null) {
            throw new IllegalArgumentException("IntArraySettingsOption must contain a non-null allowedValues");
        }
        this.allowedValues = allowedValues;
    }

    @Override
    public void randomValue(Random random) {
        int[] allowedInts = allowedValues.toArray();
        for(int i = 0; i < value.length; i++) {
            value[i] = allowedInts[random.nextInt(allowedInts.length)];
        }
    }   
    
    public void setAllowedValues(IntStream validInts) {
        this.allowedValues = validInts;
    }
}

class IntSettingsOption extends AbstractSettingsOption<Integer> {

    private IntStream allowedValues;

    public IntSettingsOption(String name, int value, IntStream allowedValues, PredicatePair... matches) {
        super(name, value, matches);
        if (allowedValues == null) {
            throw new IllegalArgumentException("IntSettingsOption must contain a non-null allowedValues");
        }
        this.allowedValues = allowedValues;
    }

    @Override
    public void randomValue(Random random) {
        int[] allowedInts = allowedValues.toArray();
        value = allowedInts[random.nextInt(allowedInts.length)];
    }    
}

class GenRestrictionsSettingsOption extends AbstractSettingsOption<GenRestrictions> {

    public GenRestrictionsSettingsOption(String name, GenRestrictions value, PredicatePair... matches) {
        super(name, value, matches);
    }

    @Override
    public void randomValue(Random random) {
        GenRestrictions newRestrictions = new GenRestrictions();
        newRestrictions.allow_gen1 = random.nextInt(2) % 2 == 1 ? true : false;
        newRestrictions.allow_gen2 = random.nextInt(2) % 2 == 1 ? true : false;
        newRestrictions.allow_gen3 = random.nextInt(2) % 2 == 1 ? true : false;
        newRestrictions.allow_gen4 = random.nextInt(2) % 2 == 1 ? true : false;
        newRestrictions.allow_gen5 = random.nextInt(2) % 2 == 1 ? true : false;
        // Automatically accept all related Gen 1 options if this is true
        // Any optional associations are random
        if (newRestrictions.allow_gen1) {
            newRestrictions.assoc_g2_g1 = true;
            newRestrictions.assoc_g4_g1 = true;
            newRestrictions.assoc_g1_g2 = random.nextInt(2) % 2 == 1 ? true : false;
            newRestrictions.assoc_g1_g4 = random.nextInt(2) % 2 == 1 ? true : false;
        }
        // Automatically accept all related Gen 2 options if this is true
        // Any optional associations are random
        if (newRestrictions.allow_gen2) {
            newRestrictions.assoc_g3_g2 = true;
            newRestrictions.assoc_g4_g2 = true;
            newRestrictions.assoc_g2_g3 = random.nextInt(2) % 2 == 1 ? true : false;
            newRestrictions.assoc_g2_g4 = random.nextInt(2) % 2 == 1 ? true : false;
            if (!newRestrictions.allow_gen1) {
                newRestrictions.assoc_g2_g1 = random.nextInt(2) % 2 == 1 ? true : false;
            }
        }
        // Automatically accept all related Gen 3 options if this is true
        // Any optional associations are random
        if (newRestrictions.allow_gen3) {
            newRestrictions.assoc_g4_g3 = true;
            newRestrictions.assoc_g3_g4 = random.nextInt(2) % 2 == 1 ? true : false;
            if (!newRestrictions.allow_gen2) {
                newRestrictions.assoc_g3_g2 = random.nextInt(2) % 2 == 1 ? true : false;
            }
        }

        // Gen 4 is not automatically accepted by anything
        if (newRestrictions.allow_gen4) {
            // If Gen1 is false, then we're allowed to try to set a value
            // otherwise we'd be overriding the true from above    
            if (!newRestrictions.allow_gen1) {
                newRestrictions.assoc_g4_g1 = random.nextInt(2) % 2 == 1 ? true : false;
            }
            // Similar for gen 2
            if (!newRestrictions.allow_gen2) {
                newRestrictions.assoc_g4_g2 = random.nextInt(2) % 2 == 1 ? true : false;
            }
            // Similar for gen 3
            if (!newRestrictions.allow_gen3) {
                newRestrictions.assoc_g4_g3 = random.nextInt(2) % 2 == 1 ? true : false;
            } 
        }  

        // Set item at end to enable easier mock for unit testing
        setItem(newRestrictions);
    }
}

class SettingsOptionComposite<T> implements SettingsOption<T> { 
    private ArrayList<SettingsOption> childOptions = new ArrayList<SettingsOption>();
    
    SettingsOption<T> value;

    public SettingsOptionComposite(SettingsOption<T> value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return value.getName();
    }

    @Override
    public T getItem() {
        return value.getItem();
    }

    @Override
    public void setItem(T item) {
        value.setItem(item);
    }

    @Override
    public Boolean isChild() {
        return value.isChild();
    }

    @Override
    public void setIsChild(Boolean isChild) {
        value.setIsChild(isChild);
    }

    @Override
    public ArrayList<PredicatePair> getMatches() {
        return value.getMatches();
    }
    
    @Override
    public void randomValue(Random random) {
        value.randomValue(random);
        childOptions.forEach((option) -> option.attemptRandomValue(random, this));
    }

    @Override
    public void attemptRandomValue(Random random, SettingsOption item) {
        value.attemptRandomValue(random, item);
        childOptions.forEach((option) -> option.attemptRandomValue(random, this));
    }

    public void add(SettingsOption childOption) {
        childOptions.add(childOption);
    }

    public SettingsOption<T> getCompositeValue() {
        return value;
    }
}