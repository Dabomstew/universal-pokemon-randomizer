package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  TrainerPokemon.java - represents a Pokemon owned by a trainer.        --*/
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

public class TrainerPokemon {

    public Pokemon pokemon;
    public int level;

    public int move1;
    public int move2;
    public int move3;
    public int move4;

    public int AILevel;
    public int heldItem = 0;
    public int ability;
    public int forme;
    public String formeSuffix = "";
    public int absolutePokeNumber = 0;

    public int mysteryFlag;
    public byte nature;
    public byte hpEVs;
    public byte atkEVs;
    public byte defEVs;
    public byte spatkEVs;
    public byte spdefEVs;
    public byte speedEVs;
    public int IVs;
    
    public boolean resetMoves = false;

    public String toString() {
        return pokemon.name + formeSuffix + " Lv" + level;
    }

    public boolean canMegaEvolve() {
        if (heldItem != 0) {
            for (MegaEvolution mega: pokemon.megaEvolutionsFrom) {
                if (mega.argument == heldItem) {
                    return true;
                }
            }
        }
        return false;
    }

    public TrainerPokemon copy() {
        TrainerPokemon tpk = new TrainerPokemon();
        tpk.pokemon = pokemon;
        tpk.level = level;

        tpk.move1 = move1;
        tpk.move2 = move2;
        tpk.move3 = move3;
        tpk.move4 = move4;

        tpk.AILevel = AILevel;
        tpk.heldItem = heldItem;
        tpk.ability = ability;
        tpk.forme = forme;
        tpk.formeSuffix = formeSuffix;
        tpk.absolutePokeNumber = absolutePokeNumber;

        tpk.resetMoves = resetMoves;

        return tpk;
    }
}
