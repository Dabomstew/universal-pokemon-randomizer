package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  Trainer.java - represents a Trainer's pokemon set/other details.      --*/
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

public class Trainer implements Comparable<Trainer> {
    private int offset;
    private List<TrainerPokemon> pokemon = new ArrayList<TrainerPokemon>();
    private String tag;
    private boolean isDoubleBattle;
    private boolean importantTrainer;
    private int poketype;
    private String name;
    private int trainerclass;
    private String fullDisplayName;

    public Trainer(){};

    public Trainer(Trainer t) {
        this.offset = t.offset;
        this.tag = t.tag;
        this.isDoubleBattle = t.isDoubleBattle;
        this.importantTrainer = t.importantTrainer;
        this.poketype = t.poketype;
        this.name = t.name;
        this.trainerclass = t.trainerclass;
        this.fullDisplayName = t.fullDisplayName;

        for(TrainerPokemon tpk : t.pokemon) {
            pokemon.add(new TrainerPokemon(tpk));
        }
    }

    public String stringOffset() {
        return String.format("%X", this.offset);
    }

    public String getLogName() {
        if (this.fullDisplayName != null) {
            return this.fullDisplayName;
        }
        if (this.name != null) {
            return this.name;
        }
        return "No_Name_Found";
    }

    public int getOffset() {
        return this.offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public List<TrainerPokemon> getPokemon() {
        return this.pokemon;
    }

    public void setPokemon(List<TrainerPokemon> pokemon) {
        this.pokemon = pokemon;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isIsDoubleBattle() {
        return this.isDoubleBattle;
    }

    public boolean getIsDoubleBattle() {
        return this.isDoubleBattle;
    }

    public void setIsDoubleBattle(boolean isDoubleBattle) {
        this.isDoubleBattle = isDoubleBattle;
    }

    public boolean isImportantTrainer() {
        return this.importantTrainer;
    }

    public boolean getImportantTrainer() {
        return this.importantTrainer;
    }

    public void setImportantTrainer(boolean importantTrainer) {
        this.importantTrainer = importantTrainer;
    }

    public int getPoketype() {
        return this.poketype;
    }

    public void setPoketype(int poketype) {
        this.poketype = poketype;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTrainerclass() {
        return this.trainerclass;
    }

    public void setTrainerclass(int trainerclass) {
        this.trainerclass = trainerclass;
    }

    public String getFullDisplayName() {
        return this.fullDisplayName;
    }

    public void setFullDisplayName(String fullDisplayName) {
        this.fullDisplayName = fullDisplayName;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        if (fullDisplayName != null) {
            sb.append(fullDisplayName + " ");
        } else if (name != null) {
            sb.append(name + " ");
        }
        if (trainerclass != 0) {
            sb.append("(" + trainerclass + ") - ");
        }
        sb.append(String.format("%x", offset));
        sb.append(" => ");
        boolean first = true;
        for (TrainerPokemon p : pokemon) {
            if (!first) {
                sb.append(',');
            }
            sb.append(p.pokemon.name + " Lv" + p.level);
            first = false;
        }
        sb.append(']');
        if (tag != null) {
            sb.append(" (" + tag + ")");
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + offset;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Trainer other = (Trainer) obj;
        if (offset != other.offset)
            return false;
        return true;
    }

    @Override
    public int compareTo(Trainer o) {
        return offset - o.offset;
    }
}
