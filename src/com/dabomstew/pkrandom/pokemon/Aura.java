package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  Aura.java - handles the Aura that Totem Pokemon have                  --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
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

import com.dabomstew.pkrandom.RomFunctions;

import java.util.Random;

public class Aura {

    private enum AuraStat {
        NONE, ATTACK, DEFENSE, SPECIAL_ATTACK, SPECIAL_DEFENSE, SPEED, ALL
    }

    public AuraStat stat;

    public int stages;

    public Aura(byte b) {
        if (b == 0) {
            stat = AuraStat.NONE;
            stages = 0;
        } else {
            stat = AuraStat.values()[((b - 1) / 3) + 1];
            stages = ((b - 1) % 3) + 1;
        }
    }

    private Aura(AuraStat stat, int stages) {
        this.stat = stat;
        this.stages = stages;
    }

    public byte toByte() {
        if (stat == AuraStat.NONE) {
            return 0;
        } else {
            return (byte)(((stat.ordinal() - 1) * 3) + (stages));
        }
    }

    public static Aura randomAura(Random random) {
        return new Aura((byte)(random.nextInt(18) + 1));
    }

    public static Aura randomAuraSimilarStrength(Random random, Aura old) {
        if (old.stat == AuraStat.NONE || old.stat == AuraStat.ALL) {
            return old;
        } else {
            return new Aura(AuraStat.values()[random.nextInt(5) + 1], old.stages);
        }
    }

    @Override
    public String toString() {
        String ret = RomFunctions.camelCase(stat.toString()).replace("_"," ");
        return stat == AuraStat.NONE ? ret : ret + " +" + stages;
    }
}
