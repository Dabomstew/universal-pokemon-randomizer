package com.dabomstew.pkrandom.settings;

/*----------------------------------------------------------------------------*/
/*--  PredicatePair.java - represents a pair of SettingsOptionComposite and --*/
/*--                       a Predicate representing that option state.      --*/
/*--                       Enables a child SettingsOption object to define  --*/
/*--                       when its state is allowed to be changed          --*/
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

import java.util.function.Predicate;

import com.dabomstew.pkrandom.settings.Settings.MovesetsMod;
import com.dabomstew.pkrandom.settings.Settings.StartersMod;
import com.dabomstew.pkrandom.settings.Settings.TrainersMod;
import com.dabomstew.pkrandom.settings.Settings.TypesMod;

public class PredicatePair {
    // These are used if the setting depends on a boolean (like checkbox)
    public static final Predicate<Boolean> BOOLEAN_TRUE = (item) -> item.equals(true);
    public static final Predicate<Boolean> BOOLEAN_FALSE = (item) -> item.equals(false);

    // Used for any Enum (radio button) where it can be anything except the default (unchanged)
    public static final Predicate<Enum> ENUM_NOT_UNCHANGED = (item) -> !isEnumDefault(item);

    // Specific enum states. More selective than "anything but unchanged"
    // Add more as needed. Do not change existing ones unless a new radio button
    // option is added that should be part of that group
    public static final Predicate<StartersMod> STARTERS_MOD_CUSTOM = (item) ->
        item == StartersMod.CUSTOM;
    public static final Predicate<StartersMod> STARTERS_MOD_RANDOM = (item) ->
        item == StartersMod.RANDOM;
    public static final Predicate<TypesMod> TYPES_MOD_RANDOM = (item) -> 
        item == TypesMod.RANDOM_RETAIN || item == TypesMod.COMPLETELY_RANDOM;
    public static final Predicate<MovesetsMod> MOVESETS_MOD_RANDOM = (item) ->
        item == MovesetsMod.RANDOM_PREFER_SAME_TYPE || item == MovesetsMod.COMPLETELY_RANDOM;
    public static final Predicate<TrainersMod> TRAINERS_MOD_RANDOM = (item) -> 
        item == TrainersMod.RANDOM;

    private SettingsOptionComposite parent;
    private Predicate parentState;
    
    public PredicatePair(SettingsOptionComposite parent, Predicate parentState) {
        this.parent = parent;
        this.parentState = parentState;
    }

    public Boolean test(SettingsOption item) {
        // Parent is allowed to be null, but if declared then
        // item must match it
        if (parent != null && item != parent) {
            return false;
        }
        return parentState.test(item.getItem());
    }

    public SettingsOptionComposite getParent() {
        return parent;
    }

    /** Determine if the enum is the same value as the first value of the enum.
    *** The first value is assumed to always be "UNCHANGED"
    */
    private static boolean isEnumDefault(Enum value) {
        Enum[] values = value.getClass().getEnumConstants();
        return value == values[0];
    }
}
