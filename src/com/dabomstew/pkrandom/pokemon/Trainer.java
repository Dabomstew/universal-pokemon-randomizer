package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  Trainer.java - represents a Trainer's pokemon set/other details.		--*/
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

import java.util.ArrayList;
import java.util.List;

public class Trainer implements Comparable<Trainer> {
	public int offset;
	public List<TrainerPokemon> pokemon = new ArrayList<TrainerPokemon>();
	public String tag;
	public boolean importantTrainer;
	public int poketype;
	public String name;
	public int trainerclass;
	public String fullDisplayName;

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
