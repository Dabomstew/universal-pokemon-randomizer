package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  Move.java - represents a move usable by Pokemon.                      --*/
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

public class Move {

    public String name;
    public int number;
    public int internalId;
    public int power;
    public int pp;
    public double hitratio;
    public Type type;
    public int effectIndex;
    public MoveCategory category;
    public double hitCount = 1; // not saved, only used in randomized move powers.
    
    public Move() {}
    
    public Move(Move m) {
        super();
        this.name = m.name;
        this.number = m.number;
        this.internalId = m.internalId;
        this.power = m.power;
        this.pp = m.pp;
        this.hitratio = m.hitratio;
        this.type = m.type;
        this.effectIndex = m.effectIndex;
        this.category = m.category;
        this.hitCount = m.hitCount;
    }



    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }



    public int getNumber() {
        return number;
    }



    public void setNumber(int number) {
        this.number = number;
    }



    public int getInternalId() {
        return internalId;
    }



    public void setInternalId(int internalId) {
        this.internalId = internalId;
    }



    public int getPower() {
        return power;
    }



    public void setPower(int power) {
        this.power = power;
    }



    public int getPp() {
        return pp;
    }



    public void setPp(int pp) {
        this.pp = pp;
    }



    public double getHitratio() {
        return hitratio;
    }



    public void setHitratio(double hitratio) {
        this.hitratio = hitratio;
    }



    public Type getType() {
        return type;
    }



    public void setType(Type type) {
        this.type = type;
    }



    public int getEffectIndex() {
        return effectIndex;
    }



    public void setEffectIndex(int effectIndex) {
        this.effectIndex = effectIndex;
    }



    public MoveCategory getCategory() {
        return category;
    }



    public void setCategory(MoveCategory category) {
        this.category = category;
    }



    public double getHitCount() {
        return hitCount;
    }



    public void setHitCount(double hitCount) {
        this.hitCount = hitCount;
    }

    public boolean isBigMove() {
        return power > 95;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Move other = (Move) obj;
        if (number != other.number) {
            return false;
        }
        if (power != other.power) {
            return false;
        }
        if (pp != other.pp) {
            return false;
        }
        if (hitratio != other.hitratio) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        if (effectIndex != other.effectIndex) {
            return false;
        }
        if (category != other.category) {
            return false;
        }
        if (hitCount != other.hitCount) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "#" + number + " " + name + " - Power: " + power + ", Base PP: " + pp + ", Type: " + type + ", Hit%: "
                + (hitratio) + ", Effect: " + effectIndex;
    }

}
