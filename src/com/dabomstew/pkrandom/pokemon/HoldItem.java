package com.dabomstew.pkrandom.pokemon;

public class HoldItem {
    public int itemIndex;
    public Type restrictedType; //item should only be used with a pokemon of this type
    public int restrictedpokemon; // item should only be used with this pokemon
    
    public HoldItem(int index) {
        this.itemIndex = index;
    }
    
    public HoldItem(int index, Type type) {
        this.itemIndex = index;
        this.restrictedType = type;
    }
    
    public HoldItem(int index, int pokemon) {
        this.itemIndex = index;
        this.restrictedpokemon = pokemon;
    }
    
}
