package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  IngameTrade.java - represents an trade found in the overworld         --*/
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

public class IngameTrade {

    public int id;

    public Pokemon requestedPokemon, givenPokemon;

    public String nickname, otName;

    public int otId;

    public int[] ivs = new int[0];

    public int item = 0;

    public Pokemon getRequestedPokemon() {
    	return this.requestedPokemon;
    }
    public void setRequestedPokemon(Pokemon pokemon) {
    	this.requestedPokemon = pokemon;
    }

    public Pokemon getGivenPokemon() {
    	return this.givenPokemon;
    }
    public void setGivenPokemon(Pokemon pokemon) {
    	this.givenPokemon = pokemon;
    }

    public String getNickname() {
    	return this.nickname;
    }

    public void setNickname(String nickname) {
    	this.nickname = nickname;
    }

    public int getItem() {
        return this.item;
    }
    
    public void setItem(int item) {
        this.item = item;
    }
}
