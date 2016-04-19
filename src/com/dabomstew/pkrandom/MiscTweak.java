package com.dabomstew.pkrandom;

/*----------------------------------------------------------------------------*/
/*--  MiscTweak.java - represents a miscellaneous tweak that can be applied --*/
/*--                   to some or all games that the randomizer supports.   --*/
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
import java.util.List;
import java.util.ResourceBundle;

public class MiscTweak implements Comparable<MiscTweak> {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("com/dabomstew/pkrandom/gui/Bundle");

    public static List<MiscTweak> allTweaks = new ArrayList<MiscTweak>();

    /* @formatter:off */
    // Higher priority value (third argument) = run first
    public static final MiscTweak BW_EXP_PATCH = new MiscTweak(1, "bwPatch", 0);
    public static final MiscTweak NERF_X_ACCURACY = new MiscTweak(2, "nerfXAcc", 0);
    public static final MiscTweak FIX_CRIT_RATE = new MiscTweak(4, "critRateFix", 0);
    public static final MiscTweak FASTEST_TEXT = new MiscTweak(8, "fastestText", 0);
    public static final MiscTweak RUNNING_SHOES_INDOORS = new MiscTweak(16, "runningShoes", 0);
    public static final MiscTweak RANDOMIZE_PC_POTION = new MiscTweak(32, "pcPotion", 0);
    public static final MiscTweak ALLOW_PIKACHU_EVOLUTION = new MiscTweak(64, "pikachuEvo", 0);
    public static final MiscTweak NATIONAL_DEX_AT_START = new MiscTweak(128, "nationalDex", 0);
    public static final MiscTweak UPDATE_TYPE_EFFECTIVENESS = new MiscTweak(256, "typeEffectiveness", 0);
    public static final MiscTweak RANDOMIZE_HIDDEN_HOLLOWS = new MiscTweak(512, "hiddenHollows", 0);
    public static final MiscTweak LOWER_CASE_POKEMON_NAMES = new MiscTweak(1024, "lowerCaseNames", 0);
    public static final MiscTweak RANDOMIZE_CATCHING_TUTORIAL = new MiscTweak(2048, "catchingTutorial", 0);
    public static final MiscTweak BAN_LUCKY_EGG = new MiscTweak(4096, "luckyEgg", 1);
    /* @formatter:on */

    private final int value;
    private final String tweakName;
    private final String tooltipText;
    private final int priority;

    private MiscTweak(int value, String tweakID, int priority) {
        this.value = value;
        this.tweakName = bundle.getString("CodeTweaks." + tweakID + ".name");
        this.tooltipText = bundle.getString("CodeTweaks." + tweakID + ".toolTipText");
        this.priority = priority;
        allTweaks.add(this);
    }

    public int getValue() {
        return value;
    }

    public String getTweakName() {
        return tweakName;
    }

    public String getTooltipText() {
        return tooltipText;
    }

    @Override
    public int compareTo(MiscTweak o) {
        // Order according to reverse priority, so higher priority = earlier in
        // ordering
        return o.priority - priority;
    }

}
