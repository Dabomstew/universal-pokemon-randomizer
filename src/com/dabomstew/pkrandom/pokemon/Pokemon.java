package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  Pokemon.java - represents an individual Pokemon, and contains         --*/
/*--                 common Pokemon-related functions.                      --*/
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Pokemon implements Comparable<Pokemon> {

    public String name;
    public int number;

    public Type primaryType, secondaryType;

    public int hp, attack, defense, spatk, spdef, speed, special;

    public int ability1, ability2, ability3;

    public int catchRate, expYield;

    public int guaranteedHeldItem, commonHeldItem, rareHeldItem, darkGrassHeldItem;

    public int genderRatio;

    public int frontSpritePointer, picDimensions;

    public ExpCurve growthCurve;

    public double percentRandomizedBuffPercent = 1;

    public List<Evolution> evolutionsFrom = new ArrayList<Evolution>();
    public List<Evolution> evolutionsTo = new ArrayList<Evolution>();

    public List<Integer> shuffledStatsOrder = null;
    
    public int extra1;

    // A flag to use for things like recursive stats copying.
    // Must not rely on the state of this flag being preserved between calls.
    public boolean temporaryFlag;

    public Pokemon() {
        shuffledStatsOrder = Arrays.asList(0, 1, 2, 3, 4, 5);
    }

    public void shuffleStats(Random random) {
        Collections.shuffle(shuffledStatsOrder, random);
        applyShuffledOrderToStats();
    }

    public void copyShuffledStatsUpEvolution(Pokemon evolvesFrom) {
        shuffledStatsOrder = evolvesFrom.shuffledStatsOrder;
        applyShuffledOrderToStats();
    }

    private void applyShuffledOrderToStats() {
        List<Integer> stats = Arrays.asList(hp, attack, defense, spatk, spdef, speed);

        // Copy in new stats
        hp = stats.get(shuffledStatsOrder.get(0));
        attack = stats.get(shuffledStatsOrder.get(1));
        defense = stats.get(shuffledStatsOrder.get(2));
        spatk = stats.get(shuffledStatsOrder.get(3));
        spdef = stats.get(shuffledStatsOrder.get(4));
        speed = stats.get(shuffledStatsOrder.get(5));

        // make special the average of spatk and spdef
        special = (int) Math.ceil((spatk + spdef) / 2.0f);
    }

    public void randomizeStatsWithinBST(Random random) {
        if (number == 292) {
            // Shedinja is horribly broken unless we restrict him to 1HP.
            int bst = bst() - 51;

            // Make weightings
            double atkW = random.nextDouble(), defW = random.nextDouble();
            double spaW = random.nextDouble(), spdW = random.nextDouble(), speW = random.nextDouble();

            double totW = atkW + defW + spaW + spdW + speW;

            hp = 1;
            attack = (int) Math.max(1, Math.round(atkW / totW * bst)) + 10;
            defense = (int) Math.max(1, Math.round(defW / totW * bst)) + 10;
            spatk = (int) Math.max(1, Math.round(spaW / totW * bst)) + 10;
            spdef = (int) Math.max(1, Math.round(spdW / totW * bst)) + 10;
            speed = (int) Math.max(1, Math.round(speW / totW * bst)) + 10;

            // Fix up special too
            special = (int) Math.ceil((spatk + spdef) / 2.0f);

        } else {
            // Minimum 20 HP, 10 everything else
            int bst = bst() - 70;

            // Make weightings
            double hpW = random.nextDouble(), atkW = random.nextDouble(), defW = random.nextDouble();
            double spaW = random.nextDouble(), spdW = random.nextDouble(), speW = random.nextDouble();

            double totW = hpW + atkW + defW + spaW + spdW + speW;

            hp = (int) Math.max(1, Math.round(hpW / totW * bst)) + 20;
            attack = (int) Math.max(1, Math.round(atkW / totW * bst)) + 10;
            defense = (int) Math.max(1, Math.round(defW / totW * bst)) + 10;
            spatk = (int) Math.max(1, Math.round(spaW / totW * bst)) + 10;
            spdef = (int) Math.max(1, Math.round(spdW / totW * bst)) + 10;
            speed = (int) Math.max(1, Math.round(speW / totW * bst)) + 10;

            // Fix up special too
            special = (int) Math.ceil((spatk + spdef) / 2.0f);
        }

        // Check for something we can't store
        if (hp > 255 || attack > 255 || defense > 255 || spatk > 255 || spdef > 255 || speed > 255) {
            // re roll
            randomizeStatsWithinBST(random);
        }

    }

    private int pickNewBST(Random random) {
        int minBST, maxBST;
        int fromDepth = this.evosFromDepth();
        int toDepth = this.evosToDepth();
        // pick new bst based on observed ranges for different poke types
        if (isLegendary()) {
            minBST = 580;
            maxBST = 720;
        } else if (fromDepth == 0 && toDepth == 0) {
            // solo poke
            minBST = 175;
            maxBST = 600;
        } else if (fromDepth >= 2 && toDepth == 0) {
            // first stage of 3+
            minBST = 175;
            maxBST = 365;
        } else if (fromDepth == 1 && toDepth == 0) {
            // first stage of 2
            minBST = 175;
            maxBST = 435;
        } else if (toDepth >= 1 && fromDepth >= 1) {
            // middle stage of 3+
            minBST = 205;
            maxBST = 465;
        } else {
            // last stage of 2+
            minBST = 395;
            maxBST = 600;
        }
        return (int) Math.round(minBST + random.nextDouble() * (maxBST - minBST));
    }

    private void scaleStatsToNewBST(int newBST) {
        double bstMult = (newBST - 70.0) / (bst() - 70.0);
        if (number == 292) {
            bstMult = (newBST * 5.0 / 6 - 50.0) / (bst() - 51.0);
        } else {
            hp = (int) Math.min(255, (20 + Math.round((hp - 20) * bstMult)));
        }

        attack = (int) Math.min(255, (10 + Math.round((attack - 10) * bstMult)));
        defense = (int) Math.min(255, (10 + Math.round((defense - 10) * bstMult)));
        spatk = (int) Math.min(255, (10 + Math.round((spatk - 10) * bstMult)));
        spdef = (int) Math.min(255, (10 + Math.round((spdef - 10) * bstMult)));
        speed = (int) Math.min(255, (10 + Math.round((speed - 10) * bstMult)));

        special = (int) Math.ceil((spatk + spdef) / 2.0f);
    }

    private void randomizeStatsWithinNewBST(Random random, int newBST) {
        if (number == 292) {
            int allocatablePoints = newBST * 5 / 6 - 50;

            // Make weightings
            do {
                double atkW = random.nextDouble(), defW = random.nextDouble();
                double spaW = random.nextDouble(), spdW = random.nextDouble(), speW = random.nextDouble();

                double totW = atkW + defW + spaW + spdW + speW;

                hp = 1;
                attack = (int) Math.max(1, Math.round(atkW / totW * allocatablePoints)) + 10;
                defense = (int) Math.max(1, Math.round(defW / totW * allocatablePoints)) + 10;
                spatk = (int) Math.max(1, Math.round(spaW / totW * allocatablePoints)) + 10;
                spdef = (int) Math.max(1, Math.round(spdW / totW * allocatablePoints)) + 10;
                speed = (int) Math.max(1, Math.round(speW / totW * allocatablePoints)) + 10;
            } while (hp > 255 || attack > 255 || defense > 255 || spatk > 255 || spdef > 255 || speed > 255);
        } else {
            // Minimum 20 HP, 10 everything else
            int allocatablePoints = newBST - 70;

            do {
                // Make weightings
                double hpW = random.nextDouble(), atkW = random.nextDouble(), defW = random.nextDouble();
                double spaW = random.nextDouble(), spdW = random.nextDouble(), speW = random.nextDouble();

                double totW = hpW + atkW + defW + spaW + spdW + speW;

                hp = (int) Math.max(1, Math.round(hpW / totW * allocatablePoints)) + 20;
                attack = (int) Math.max(1, Math.round(atkW / totW * allocatablePoints)) + 10;
                defense = (int) Math.max(1, Math.round(defW / totW * allocatablePoints)) + 10;
                spatk = (int) Math.max(1, Math.round(spaW / totW * allocatablePoints)) + 10;
                spdef = (int) Math.max(1, Math.round(spdW / totW * allocatablePoints)) + 10;
                speed = (int) Math.max(1, Math.round(speW / totW * allocatablePoints)) + 10;
            } while (hp > 255 || attack > 255 || defense > 255 || spatk > 255 || spdef > 255 || speed > 255);
        }

        special = (int) Math.ceil((spatk + spdef) / 2.0f);
    }

    public void randomizeBST(Random random, boolean dontRandomizeRatio) {
        int newBST = pickNewBST(random);
        if (dontRandomizeRatio) {
            scaleStatsToNewBST(newBST);
        } else {
            randomizeStatsWithinNewBST(random, newBST);
        }
    }

    public void randomizeBSTPerc(Random random, int percent, boolean dontRandomizeRatio) {
        double modifier = 1;
        if (random.nextBoolean()) {
            modifier = 1 + ((percent / 100.0f) * random.nextDouble());
        } else {
            modifier = 1 - ((percent / 100.0f) * random.nextDouble());
        }
        if ((bst() * modifier) < 180) {
            modifier = 180 / bst();
        }
        if (modifier <= 0) {
            modifier = 1;
        }
        percentRandomizedBuffPercent = modifier;
        int effectiveNewBST = (int) Math.round(bstForPowerLevels() * modifier);

        if (dontRandomizeRatio) {
            scaleStatsToNewBST(effectiveNewBST);
        } else {
            randomizeStatsWithinNewBST(random, effectiveNewBST);
        }
    }

    public void equalizeBST(Random random, boolean dontRandomizeRatio) {
        if (dontRandomizeRatio) {
            scaleStatsToNewBST(420);
        } else {
            randomizeStatsWithinNewBST(random, 420);
        }
    }

    public void percentRaiseStatFloorUpEvolution(Random random, boolean dontRandomizeRatio, Pokemon evolvesFrom) {
        percentRandomizedBuffPercent = evolvesFrom.percentRandomizedBuffPercent;
        double statRatio = evolvesFrom.percentRandomizedBuffPercent;

        int effectiveNewBST = (int) Math.round(bstForPowerLevels() * statRatio);

        if (dontRandomizeRatio) {
            scaleStatsToNewBST(effectiveNewBST);
        } else {
            randomizeStatsWithinNewBST(random, effectiveNewBST);
        }
    }

    public void copyRandomizedStatsUpEvolution(Pokemon evolvesFrom) {
        double ourBST = bst();
        double theirBST = evolvesFrom.bst();

        double bstRatio = ourBST / theirBST;

        hp = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.hp * bstRatio)));
        attack = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.attack * bstRatio)));
        defense = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.defense * bstRatio)));
        speed = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.speed * bstRatio)));
        spatk = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.spatk * bstRatio)));
        spdef = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.spdef * bstRatio)));

        special = (int) Math.ceil((spatk + spdef) / 2.0f);
    }
    
    public void randomizeBSTSetAmountAbovePreevo(Random random, Pokemon evolvesFrom, boolean dontRandomizeRatio) {
        int newBST = evolvesFrom.bstForPowerLevels() + 95;
        if (dontRandomizeRatio) {
            scaleStatsToNewBST(newBST);
        } else {
            randomizeStatsWithinNewBST(random, newBST);
        }
    }
    
    public void copyRandomizedBSTUpEvolution(Random random, Pokemon evolvesFrom, boolean fixedAmount) {
        int newBST = fixedAmount ? evolvesFrom.bstForPowerLevels() + 95 : pickNewBST(random);
        // quick and easy method to copy preevo's stat ratios with a new BST
        scaleStatsToNewBST(newBST);
        copyRandomizedStatsUpEvolution(evolvesFrom);
    }

    public void copyEqualizedStatsUpEvolution(Pokemon evolvesFrom) {
        hp = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.hp)));
        attack = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.attack)));
        defense = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.defense)));
        speed = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.speed)));
        spatk = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.spatk)));
        spdef = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.spdef)));

        special = (int) Math.ceil((spatk + spdef) / 2.0f);
    }

    public int bst() {
        return hp + attack + defense + spatk + spdef + speed;
    }

    public int bstForPowerLevels() {
        // Take into account Shedinja's purposefully nerfed HP
        if (number == 292) {
            return (attack + defense + spatk + spdef + speed) * 6 / 5;
        } else {
            return hp + attack + defense + spatk + spdef + speed;
        }
    }

    @Override
    public String toString() {
        return "Pokemon [name=" + name + ", number=" + number + ", primaryType=" + primaryType + ", secondaryType="
                + secondaryType + ", hp=" + hp + ", attack=" + attack + ", defense=" + defense + ", spatk=" + spatk
                + ", spdef=" + spdef + ", speed=" + speed + "]";
    }

    public String toStringRBY() {
        return "Pokemon [name=" + name + ", number=" + number + ", primaryType=" + primaryType + ", secondaryType="
                + secondaryType + ", hp=" + hp + ", attack=" + attack + ", defense=" + defense + ", special=" + special
                + ", speed=" + speed + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + number;
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
        Pokemon other = (Pokemon) obj;
        if (number != other.number)
            return false;
        return true;
    }

    @Override
    public int compareTo(Pokemon o) {
        return number - o.number;
    }

    private static final List<Integer> legendaries = Arrays.asList(144, 145, 146, 150, 151, 243, 244, 245, 249, 250,
            251, 377, 378, 379, 380, 381, 382, 383, 384, 385, 386, 479, 480, 481, 482, 483, 484, 485, 486, 487, 488,
            489, 490, 491, 492, 493, 494, 638, 639, 640, 641, 642, 643, 644, 645, 646, 647, 648, 649);

    public boolean isLegendary() {
        return legendaries.contains(this.number);
    }

    public int evosFromDepth() {
        if (evolutionsFrom.isEmpty()) {
            return 0;
        }
        int md = 0;
        for (Evolution ef : evolutionsFrom) {
            md = Math.max(md, ef.to.evosFromDepth());
        }
        return md + 1;
    }

    public int evosToDepth() {
        if (evolutionsTo.isEmpty()) {
            return 0;
        }
        int md = 0;
        for (Evolution ef : evolutionsTo) {
            md = Math.max(md, ef.from.evosToDepth());
        }
        return md + 1;
    }

}
