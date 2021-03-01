package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  Evolution.java - represents an evolution between 2 Pokemon.           --*/
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

public class Evolution implements Comparable<Evolution> {

    public Pokemon from;
    public Pokemon to;
    public boolean carryStats;
    public int typesDiffer;
    public EvolutionType type;
    public int extraInfo;

    public Evolution(Pokemon from, Pokemon to, boolean carryStats, EvolutionType type, int extra) {
        this.from = from;
        this.to = to;
        this.carryStats = carryStats;
        this.type = type;
        this.extraInfo = extra;
        this.typesDiffer = 0;
        // 1 == 1
        if(from.primaryType != to.primaryType) {
            this.typesDiffer = 1;
        } 
        // 2 == 2
        if(from.secondaryType != to.secondaryType) {
            this.typesDiffer = 2;
        } 
        // 1 == 2
        if (from.primaryType == to.secondaryType && from.secondaryType != to.primaryType) {
            this.typesDiffer = 3;
        } 
        // 2 == 1
        if (from.secondaryType == to.primaryType && from.primaryType != to.secondaryType) {
            this.typesDiffer = 4;
        } 
        // 1 == 2 && 2 == 1 (Grass/Bug vs Bug/Grass)
        if (from.primaryType == to.secondaryType && from.secondaryType == to.primaryType) {
            this.typesDiffer = 0;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + from.number;
        result = prime * result + to.number;
        result = prime * result + type.ordinal();
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
        Evolution other = (Evolution) obj;
        if (from != other.from)
            return false;
        if (to != other.to)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public int compareTo(Evolution o) {
        if (this.from.number < o.from.number) {
            return -1;
        } else if (this.from.number > o.from.number) {
            return 1;
        } else if (this.to.number < o.to.number) {
            return -1;
        } else if (this.to.number > o.to.number) {
            return 1;
        } else if (this.type.ordinal() < o.type.ordinal()) {
            return -1;
        } else if (this.type.ordinal() > o.type.ordinal()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Evolution [from=" + this.from.name + ", to=" + this.to.name + ", method=" + this.type + ", extraInfo=" + this.extraInfo + "]";
    }

    public Pokemon getFrom() {
        return from;
    }

    public void setFrom(Pokemon from) {
        this.from = from;
    }

    public Pokemon getTo() {
        return to;
    }

    public void setTo(Pokemon to) {
        this.to = to;
    }

    public EvolutionType getType() {
        return this.type;
    }
    public void setType(EvolutionType type) {
        this.type = type;
    }

    public int getExtraInfo() {
    	return this.extraInfo;
    }
    
    public void setExtraInfo(int extraInfo) {
    	this.extraInfo = extraInfo;
    }

}
