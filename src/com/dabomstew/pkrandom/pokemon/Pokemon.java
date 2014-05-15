package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  Pokemon.java - represents an individual Pokemon, and contains         --*/
/*--				 common Pokemon-related functions.                      --*/
/*--  																		--*/
/*--  Part of "Universal Pokemon Randomizer" by Dabomstew					--*/
/*--  Pokemon and any associated names and the like are						--*/
/*--  trademark and (C) Nintendo 1996-2012.									--*/
/*--  																		--*/
/*--  The custom code written here is licensed under the terms of the GPL:	--*/
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.dabomstew.pkrandom.RandomSource;

public class Pokemon implements Comparable<Pokemon> {

	public String name;
	public int number;

	public Type primaryType, secondaryType;

	public int hp, attack, defense, spatk, spdef, speed, special;

	public int ability1, ability2, ability3;

	public int catchRate;

	public int guaranteedHeldItem, commonHeldItem, rareHeldItem,
			darkGrassHeldItem;
	
	public ExpCurve growthCurve;

	public Pokemon() {
	}

	public void shuffleStats() {
		List<Integer> stats = Arrays.asList(hp, attack, defense, spatk, spdef,
				speed);
		Collections.shuffle(stats, RandomSource.instance());

		// Copy in new stats
		hp = stats.get(0);
		attack = stats.get(1);
		defense = stats.get(2);
		spatk = stats.get(3);
		spdef = stats.get(4);
		speed = stats.get(5);
		
		// make special the average of spatk and spdef
		special = (int) Math.ceil((spatk + spdef) / 2.0f);

		// Copy special from a random one of spatk or spdef
//		if (RandomSource.random() < 0.5) {
//			special = spatk;
//		} else {
//			special = spdef;
//		}
	}

	public void randomizeStatsWithinBST() {
		if (number == 292) {
			// Shedinja is horribly broken unless we restrict him to 1HP.
			int bst = bst() - 51;

			// Make weightings
			double atkW = RandomSource.random(), defW = RandomSource.random();
			double spaW = RandomSource.random(), spdW = RandomSource.random(), speW = RandomSource
					.random();

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
			double hpW = RandomSource.random(), atkW = RandomSource.random(), defW = RandomSource
					.random();
			double spaW = RandomSource.random(), spdW = RandomSource.random(), speW = RandomSource
					.random();

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
		if (hp > 255 || attack > 255 || defense > 255 || spatk > 255
				|| spdef > 255 || speed > 255) {
			// re roll
			randomizeStatsWithinBST();
		}

	}

	public void copyRandomizedStatsUpEvolution(Pokemon evolvesFrom) {
		double ourBST = bst();
		double theirBST = evolvesFrom.bst();

		double bstRatio = ourBST / theirBST;

		hp = (int) Math.min(255,
				Math.max(1, Math.round(evolvesFrom.hp * bstRatio)));
		attack = (int) Math.min(255,
				Math.max(1, Math.round(evolvesFrom.attack * bstRatio)));
		defense = (int) Math.min(255,
				Math.max(1, Math.round(evolvesFrom.defense * bstRatio)));
		speed = (int) Math.min(255,
				Math.max(1, Math.round(evolvesFrom.speed * bstRatio)));
		spatk = (int) Math.min(255,
				Math.max(1, Math.round(evolvesFrom.spatk * bstRatio)));
		spdef = (int) Math.min(255,
				Math.max(1, Math.round(evolvesFrom.spdef * bstRatio)));

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
		return "Pokemon [name=" + name + ", number=" + number
				+ ", primaryType=" + primaryType + ", secondaryType="
				+ secondaryType + ", hp=" + hp + ", attack=" + attack
				+ ", defense=" + defense + ", spatk=" + spatk + ", spdef="
				+ spdef + ", speed=" + speed + "]";
	}

	public String toStringRBY() {
		return "Pokemon [name=" + name + ", number=" + number
				+ ", primaryType=" + primaryType + ", secondaryType="
				+ secondaryType + ", hp=" + hp + ", attack=" + attack
				+ ", defense=" + defense + ", special=" + special + ", speed="
				+ speed + "]";
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

	private static final List<Integer> legendaries = Arrays.asList(144, 145,
			146, 150, 151, 243, 244, 245, 249, 250, 251, 377, 378, 379, 380,
			381, 382, 383, 384, 385, 386, 479, 480, 481, 482, 483, 484, 485,
			486, 487, 488, 489, 490, 491, 492, 493, 494, 638, 639, 640, 641,
			642, 643, 644, 645, 646, 647, 648, 649);

	public boolean isLegendary() {
		return legendaries.contains(this.number);
	}

}
