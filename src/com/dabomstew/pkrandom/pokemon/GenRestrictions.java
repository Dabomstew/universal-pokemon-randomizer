package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  GenRestrictions.java - stores what generations the user has limited.  --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
/*--  Originally part of "Universal Pokemon Randomizer" by Dabomstew        --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2020.                                 --*/
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

public class GenRestrictions {

    public boolean allow_gen1, allow_gen2, allow_gen3, allow_gen4, allow_gen5, allow_gen6, allow_gen7;

    public boolean assoc_g1_g2, assoc_g1_g4, assoc_g1_g6;
    public boolean assoc_g2_g1, assoc_g2_g3, assoc_g2_g4;
    public boolean assoc_g3_g2, assoc_g3_g4;
    public boolean assoc_g4_g1, assoc_g4_g2, assoc_g4_g3;
    public boolean assoc_g6_g1;

    public GenRestrictions() {
    }

    public GenRestrictions(int state) {
        allow_gen1 = (state & 1) > 0;
        allow_gen2 = (state & 2) > 0;
        allow_gen3 = (state & 4) > 0;
        allow_gen4 = (state & 8) > 0;
        allow_gen5 = (state & 16) > 0;
        allow_gen6 = (state & 32) > 0;
        allow_gen7 = (state & 64) > 0;

        assoc_g1_g2 = (state & 128) > 0;
        assoc_g1_g4 = (state & 256) > 0;
        assoc_g1_g6 = (state & 512) > 0;

        assoc_g2_g1 = (state & 1024) > 0;
        assoc_g2_g3 = (state & 2048) > 0;
        assoc_g2_g4 = (state & 4096) > 0;

        assoc_g3_g2 = (state & 8192) > 0;
        assoc_g3_g4 = (state & 16384) > 0;

        assoc_g4_g1 = (state & 32768) > 0;
        assoc_g4_g2 = (state & 65536) > 0;
        assoc_g4_g3 = (state & 131072) > 0;

        assoc_g6_g1 = (state & 262144) > 0;
    }

    public boolean nothingSelected() {
        return !allow_gen1 && !allow_gen2 && !allow_gen3 && !allow_gen4 && !allow_gen5 && !allow_gen6 && !allow_gen7;
    }

    public int toInt() {
        return makeIntSelected(allow_gen1, allow_gen2, allow_gen3, allow_gen4, allow_gen5, allow_gen6, allow_gen7,
                assoc_g1_g2, assoc_g1_g4, assoc_g1_g6, assoc_g2_g1, assoc_g2_g3, assoc_g2_g4, assoc_g3_g2, assoc_g3_g4,
                assoc_g4_g1, assoc_g4_g2, assoc_g4_g3, assoc_g6_g1);
    }

    public void limitToGen(int generation) {
        if (generation < 2) {
            allow_gen2 = false;
            assoc_g1_g2 = false;
            assoc_g2_g1 = false;
        }
        if (generation < 3) {
            allow_gen3 = false;
            assoc_g2_g3 = false;
            assoc_g3_g2 = false;
        }
        if (generation < 4) {
            allow_gen4 = false;
            assoc_g1_g4 = false;
            assoc_g2_g4 = false;
            assoc_g3_g4 = false;
            assoc_g4_g1 = false;
            assoc_g4_g2 = false;
            assoc_g4_g3 = false;
        }
        if (generation < 5) {
            allow_gen5 = false;
        }
        if (generation < 6) {
            allow_gen6 = false;
            assoc_g1_g6 = false;
            assoc_g6_g1 = false;
        }
        if (generation < 7) {
            allow_gen7 = false;
        }
    }

    public boolean megaEvolutionsAreInPool(boolean isXY) {
        if (isXY) {
            return allow_gen1 || allow_gen2 || allow_gen3 || allow_gen4;
        } else {
            return allow_gen1 || allow_gen2 || allow_gen3 || allow_gen4 || allow_gen5 || allow_gen6;
        }
    }

    private int makeIntSelected(boolean... switches) {
        if (switches.length > 32) {
            // No can do
            return 0;
        }
        int initial = 0;
        int state = 1;
        for (boolean b : switches) {
            initial |= b ? state : 0;
            state *= 2;
        }
        return initial;
    }

}
