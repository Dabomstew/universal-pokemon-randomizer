package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  TrainerPokemon.java - represents a Pokemon owned by a trainer.        --*/
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

public class TrainerPokemon {

    public Pokemon pokemon;
    public int level;

    // Nickname is not actually supported by any mainline games
    // This just assists with internal testing
    private String nickname;

    public int move1;
    public int move2;
    public int move3;
    public int move4;

    public int AILevel;
    public int heldItem;
    public int ability;
    
    public boolean resetMoves = false;

    public TrainerPokemon(){};

    public TrainerPokemon(TrainerPokemon tp) {
        this.pokemon = tp.pokemon;
        this.level = tp.level;
        this.nickname = tp.getNickname();
        this.move1 = tp.move1;
        this.move2 = tp.move2;
        this.move3 = tp.move3;
        this.move4 = tp.move4;
        this.AILevel = tp.AILevel;
        this.heldItem = tp.heldItem;
        this.ability = tp.ability;
    }

    public Pokemon getPokemon() {
    	return this.pokemon;
    }

    public void setPokemon(Pokemon pokemon) {
    	this.pokemon = pokemon;
    }

    public int getLevel() {
    	return this.level;
    }
    
    public void setLevel(int level) {
    	this.level = level;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getHeldItem() {
        return this.heldItem;
    }

    public void setHeldItem(int heldItem) {
        this.heldItem = heldItem;
    }

    public String toString() {
        return pokemon.name + " Lv" + level;
    }
}
