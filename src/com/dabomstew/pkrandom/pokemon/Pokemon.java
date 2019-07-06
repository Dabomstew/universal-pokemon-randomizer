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

import com.dabomstew.pkrandom.romhandlers.AbstractRomHandler;

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

    public void randomizeBST(Random random, boolean dontRandomizeRatio) {
    	if(dontRandomizeRatio) {
    		if (number == 292) {
                // Shedinja is horribly broken unless we restrict him to 1HP.
                double bstRatio = bst()/(180 + (270*random.nextDouble()));

                hp = 1;
                attack = (int) Math.max(1, Math.round(attack / bstRatio));
                defense = (int) Math.max(1, Math.round(defense / bstRatio));
                spatk = (int) Math.max(1, Math.round(spatk / bstRatio));
                spdef = (int) Math.max(1, Math.round(spdef / bstRatio));
                speed = (int) Math.max(1, Math.round(speed / bstRatio));

                // Fix up special too
                special = (int) Math.ceil((spatk + spdef) / 2.0f);

            } else if(this.isLegendary()) {
                // Minimum 20 HP, 10 everything else
                double bstRatio = bst()/(480 + (240*random.nextDouble()));

                hp = (int) Math.max(1, Math.round(hp / bstRatio));
                attack = (int) Math.max(1, Math.round(attack / bstRatio));
                defense = (int) Math.max(1, Math.round(defense / bstRatio));
                spatk = (int) Math.max(1, Math.round(spatk / bstRatio));
                spdef = (int) Math.max(1, Math.round(spdef / bstRatio));
                speed = (int) Math.max(1, Math.round(speed / bstRatio));

                // Fix up special too
                special = (int) Math.ceil((spatk + spdef) / 2.0f);
            } else {
                // Minimum 20 HP, 10 everything else
                double bstRatio = bst()/(180 + (270*random.nextDouble()));

                hp = (int) Math.max(1, Math.round(hp / bstRatio));
                attack = (int) Math.max(1, Math.round(attack / bstRatio));
                defense = (int) Math.max(1, Math.round(defense / bstRatio));
                spatk = (int) Math.max(1, Math.round(spatk / bstRatio));
                spdef = (int) Math.max(1, Math.round(spdef / bstRatio));
                speed = (int) Math.max(1, Math.round(speed / bstRatio));

                // Fix up special too
                special = (int) Math.ceil((spatk + spdef) / 2.0f);
            }
    	} else {
    		if (number == 292) {
                // Shedinja is horribly broken unless we restrict him to 1HP.
                int bst = 129 + (int)(270*random.nextDouble());

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

            } else if(this.isLegendary()) {
                // Minimum 20 HP, 10 everything else
                int bst = 410 + (int)(240*random.nextDouble());

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
            } else {
                // Minimum 20 HP, 10 everything else
                int bst = 110 + (int)(270*random.nextDouble());

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
    	}
        // Check for something we can't store
        if (hp > 255 || attack > 255 || defense > 255 || spatk > 255 || spdef > 255 || speed > 255) {
            // re roll
            randomizeStatsWithinBST(random);
        }
    }
    
    public void randomizeBSTPerc(Random random, int percent, boolean dontRandomizeRatio) {
    	double modifier = 1;
    	if(random.nextBoolean()) {
    		modifier = 1 +((percent/100.0f)*random.nextDouble());
    	}
    	else {
    		modifier = 1 - ((percent/100.0f)*random.nextDouble());
    	}
		if((bst() * modifier) < 180) {
			modifier = 180/bst();
		}
    	if(modifier <= 0) {
    		modifier = 1;
    	}
    	percentRandomizedBuffPercent = modifier;
    	
    	if(dontRandomizeRatio) {
    		if (number == 292) {
                // Shedinja is horribly broken unless we restrict him to 1HP.
                hp = 1;
                attack = (int) Math.max(1, Math.round(attack*modifier));
                defense = (int) Math.max(1, Math.round(defense*modifier));
                spatk = (int) Math.max(1, Math.round(spatk*modifier));
                spdef = (int) Math.max(1, Math.round(spdef*modifier));
                speed = (int) Math.max(1, Math.round(speed*modifier));

                // Fix up special too
                special = (int) Math.ceil((spatk + spdef) / 2.0f);

            } else {
                hp = (int) Math.max(1, Math.round(hp*modifier));
                attack = (int) Math.max(1, Math.round(attack*modifier));
                defense = (int) Math.max(1, Math.round(defense*modifier));
                spatk = (int) Math.max(1, Math.round(spatk*modifier));
                spdef = (int) Math.max(1, Math.round(spdef*modifier));
                speed = (int) Math.max(1, Math.round(speed*modifier));

                // Fix up special too
                special = (int) Math.ceil((spatk + spdef) / 2.0f);
            }
    	} else {
    		if (number == 292) {
                // Shedinja is horribly broken unless we restrict him to 1HP.
                int bst = (int)(bst()*modifier) - 51;

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
                int bst = (int)(bst()*modifier) - 70;

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
    	}

        // Check for something we can't store
        if (hp > 255 || attack > 255 || defense > 255 || spatk > 255 || spdef > 255 || speed > 255) {
            // re roll
            randomizeStatsWithinBST(random);
        }
    }
    
    public void equalizeBST(Random random, boolean dontRandomizeRatio) {
    	if (number == 292) {
            // Shedinja is horribly broken unless we restrict him to 1HP.
    		if(dontRandomizeRatio) {
                double bstRatio = bst()/350f;

                hp = 1;
                attack = (int) Math.max(1, Math.round(attack/bstRatio));
                defense = (int) Math.max(1, Math.round(defense/bstRatio));
                spatk = (int) Math.max(1, Math.round(spatk/bstRatio));
                spdef = (int) Math.max(1, Math.round(spdef/bstRatio));
                speed = (int) Math.max(1, Math.round(speed/bstRatio));

                // Fix up special too
                special = (int) Math.ceil((spatk + spdef) / 2.0f);
    		} else {
                int bst = 300;

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
    		}
        } else {
            // Minimum 20 HP, 10 everything else
        	if(dontRandomizeRatio) {
        		double bstRatio = bst()/420.0f;
        		
        		hp = (int) Math.max(1, Math.round(hp/bstRatio));
                attack = (int) Math.max(1, Math.round(attack/bstRatio));
                defense = (int) Math.max(1, Math.round(defense/bstRatio));
                spatk = (int) Math.max(1, Math.round(spatk/bstRatio));
                spdef = (int) Math.max(1, Math.round(spdef/bstRatio));
                speed = (int) Math.max(1, Math.round(speed/bstRatio));

                // Fix up special too
                special = (int) Math.ceil((spatk + spdef) / 2.0f);
        	} else {
        		int bst = 350;

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
        }

        // Check for something we can't store
        if (hp > 255 || attack > 255 || defense > 255 || spatk > 255 || spdef > 255 || speed > 255) {
            // re roll
            randomizeStatsWithinBST(random);
        }
    }
    
    //TODO
    public void percentRaiseStatFloorUpEvolution(Random random, boolean dontRandomizeRatio, Pokemon evolvesFrom)
    {
    	percentRandomizedBuffPercent = evolvesFrom.percentRandomizedBuffPercent;
    	double statRatio = evolvesFrom.percentRandomizedBuffPercent;
    	if(dontRandomizeRatio) {
    		if (number == 292) {
                // Shedinja is horribly broken unless we restrict him to 1HP.
                hp = 1;
                attack = (int) Math.max(1, Math.round(attack*statRatio));
                defense = (int) Math.max(1, Math.round(defense*statRatio));
                spatk = (int) Math.max(1, Math.round(spatk*statRatio));
                spdef = (int) Math.max(1, Math.round(spdef*statRatio));
                speed = (int) Math.max(1, Math.round(speed*statRatio));

                // Fix up special too
                special = (int) Math.ceil((spatk + spdef) / 2.0f);

            } else {
                hp = (int) Math.max(1, Math.round(hp*statRatio));
                attack = (int) Math.max(1, Math.round(attack*statRatio));
                defense = (int) Math.max(1, Math.round(defense*statRatio));
                spatk = (int) Math.max(1, Math.round(spatk*statRatio));
                spdef = (int) Math.max(1, Math.round(spdef*statRatio));
                speed = (int) Math.max(1, Math.round(speed*statRatio));

                // Fix up special too
                special = (int) Math.ceil((spatk + spdef) / 2.0f);
            }
    	} else {
    		if (number == 292) {
                // Shedinja is horribly broken unless we restrict him to 1HP.
                int bst = (int)(bst()*statRatio) - 51;

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
                int bst = (int)(bst()*statRatio) - 70;

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
    
    public void copyRandomizedBaseStatsUpEvolution(Random random, Pokemon evolvesFrom, boolean evolutionSanity, boolean evosBuffStats) {
    	double theirBST = evolvesFrom.bst();
        double ourBST;
        if(evosBuffStats) {
        	ourBST = theirBST + 95;
        } else {
        	ourBST = theirBST + (40 + 150 * random.nextDouble());
        }
        if(evolutionSanity) {
            double bstRatio = ourBST / theirBST;
        	hp = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.hp * bstRatio)));
            attack = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.attack * bstRatio)));
            defense = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.defense * bstRatio)));
            speed = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.speed * bstRatio)));
            spatk = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.spatk * bstRatio)));
            spdef = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.spdef * bstRatio)));
        } else {
            double gainRatio = ourBST / bst();
        	if (!(number == 292)) {
            	hp = (int) Math.min(255, Math.max(1, Math.round(hp * gainRatio)));
            }
        	hp = (int) Math.min(255, Math.max(1, Math.round(hp * gainRatio)));
            attack = (int) Math.min(255, Math.max(1, Math.round(attack * gainRatio)));
            defense = (int) Math.min(255, Math.max(1, Math.round(defense * gainRatio)));
            speed = (int) Math.min(255, Math.max(1, Math.round(speed * gainRatio)));
            spatk = (int) Math.min(255, Math.max(1, Math.round(spatk * gainRatio)));
            spdef = (int) Math.min(255, Math.max(1, Math.round(spdef * gainRatio)));
        }
        special = (int) Math.ceil((spatk + spdef) / 2.0f);
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

}
