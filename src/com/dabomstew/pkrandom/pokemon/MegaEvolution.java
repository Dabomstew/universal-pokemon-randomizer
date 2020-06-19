package com.dabomstew.pkrandom.pokemon;

public class MegaEvolution {
    public Pokemon from;
    public Pokemon to;
    public int method;
    public int argument;
    public boolean carryStats = true;

    public MegaEvolution(Pokemon from, Pokemon to, int method, int argument) {
        this.from = from;
        this.to = to;
        this.method = method;
        this.argument = argument;
    }
}
