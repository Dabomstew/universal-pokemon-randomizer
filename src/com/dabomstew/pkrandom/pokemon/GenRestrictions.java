package com.dabomstew.pkrandom.pokemon;

public class GenRestrictions {

	public boolean allow_gen1, allow_gen2, allow_gen3, allow_gen4, allow_gen5;

	public boolean assoc_g1_g2, assoc_g1_g4;
	public boolean assoc_g2_g1, assoc_g2_g3, assoc_g2_g4;
	public boolean assoc_g3_g2, assoc_g3_g4;
	public boolean assoc_g4_g1, assoc_g4_g2, assoc_g4_g3;

	public GenRestrictions() {
	}

	public GenRestrictions(int state) {
		allow_gen1 = (state & 1) > 0;
		allow_gen2 = (state & 2) > 0;
		allow_gen3 = (state & 4) > 0;
		allow_gen4 = (state & 8) > 0;
		allow_gen5 = (state & 16) > 0;

		assoc_g1_g2 = (state & 32) > 0;
		assoc_g1_g4 = (state & 64) > 0;

		assoc_g2_g1 = (state & 128) > 0;
		assoc_g2_g3 = (state & 256) > 0;
		assoc_g2_g4 = (state & 512) > 0;

		assoc_g3_g2 = (state & 1024) > 0;
		assoc_g3_g4 = (state & 2048) > 0;

		assoc_g4_g1 = (state & 4096) > 0;
		assoc_g4_g2 = (state & 8192) > 0;
		assoc_g4_g3 = (state & 16384) > 0;
	}

	public boolean nothingSelected() {
		return !allow_gen1 && !allow_gen2 && !allow_gen3 && !allow_gen4
				&& !allow_gen5;
	}

	public int toInt() {
		return makeIntSelected(allow_gen1, allow_gen2, allow_gen3, allow_gen4,
				allow_gen5, assoc_g1_g2, assoc_g1_g4, assoc_g2_g1, assoc_g2_g3,
				assoc_g2_g4, assoc_g3_g2, assoc_g3_g4, assoc_g4_g1,
				assoc_g4_g2, assoc_g4_g3);
	}

	public void limitToGen(int generation) {
		if (generation < 2) {
			allow_gen2 = false;
			assoc_g1_g2 = false;
			assoc_g2_g1 = false;
		}
		if (generation < 3) {
			allow_gen3 = false;
			assoc_g2_g3 = false;
			assoc_g3_g2 = false;
		}
		if (generation < 4) {
			allow_gen4 = false;
			assoc_g1_g4 = false;
			assoc_g2_g4 = false;
			assoc_g3_g4 = false;
			assoc_g4_g1 = false;
			assoc_g4_g2 = false;
			assoc_g4_g3 = false;
		}
		if (generation < 5) {
			allow_gen5 = false;
		}
	}

	private int makeIntSelected(boolean... switches) {
		if (switches.length > 32) {
			// No can do
			return 0;
		}
		int initial = 0;
		int state = 1;
		for (boolean b : switches) {
			initial |= b ? state : 0;
			state *= 2;
		}
		return initial;
	}

}
