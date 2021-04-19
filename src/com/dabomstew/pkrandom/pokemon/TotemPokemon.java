package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  TotemPokemon.java - represents a Totem Pokemon encounter              --*/
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

import java.util.Map;
import java.util.TreeMap;

public class TotemPokemon extends StaticEncounter {
    public Aura aura;
    public int ally1Offset;
    public int ally2Offset;
    public Map<Integer,StaticEncounter> allies = new TreeMap<>();

    public boolean unused = false;

    public TotemPokemon() {

    }

    public TotemPokemon(Pokemon pkmn) {
        this.pkmn = pkmn;
    }

    @Override
    public String toString() {
        // The %s will be formatted to include the held item.
        String ret = pkmn.fullName() + "@%s Lv" + level + "\n    Aura: " + aura.toString() + "\n";
        int i = 1;
        for (StaticEncounter ally: allies.values()) {
            ret = ret.concat("    Ally " + i + ": " + ally.toString() + "\n");
            i++;
        }
        return ret.concat("\n");
    }
}
