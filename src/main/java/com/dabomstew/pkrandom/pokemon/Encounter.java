package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  Encounter.java - contains one wild Pokemon slot                       --*/
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

public class Encounter {

    public int level;
    public int maxLevel;
    public Pokemon pokemon;

    public int maximumLevel() {
        return Math.max(level, maxLevel);
    }

    public int getLevel() {
    	return this.level;
    }

    public void setLevel(int level) {
    	this.level = level;
    }

    public int getMaxLevel() {
    	return this.maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
    	this.maxLevel = maxLevel;
    }

    public Pokemon getPokemon() {
    	return this.pokemon;
    }

    public void setPokemon(Pokemon pokemon) {
    	this.pokemon = pokemon;
    }

    public String toString() {
        if (pokemon == null) {
            return "ERROR";
        }
        if (maxLevel == 0) {
            return pokemon.name + " Lv" + level;
        } else {
            return pokemon.name + " Lvs " + level + "-" + maxLevel;
        }
    }
}
