package com.dabomstew.pkrandom.pokemon;

import com.dabomstew.pkrandom.RandomSource;

public class ItemList {

	private boolean[] items;
	private boolean[] tms;

	public ItemList(int highestIndex) {
		items = new boolean[highestIndex + 1];
		tms = new boolean[highestIndex + 1];
		for (int i = 1; i <= highestIndex; i++) {
			items[i] = true;
		}
	}

	public boolean isTM(int index) {
		if (index < 0 || index >= tms.length) {
			return false;
		}
		return tms[index];
	}

	public boolean isAllowed(int index) {
		if (index < 0 || index >= tms.length) {
			return false;
		}
		return items[index];
	}

	public void banSingles(int... indexes) {
		for (int index : indexes) {
			items[index] = false;
		}
	}

	public void banRange(int startIndex, int length) {
		for (int i = 0; i < length; i++) {
			items[i + startIndex] = false;
		}
	}

	public void tmRange(int startIndex, int length) {
		for (int i = 0; i < length; i++) {
			tms[i + startIndex] = true;
		}
	}

	public int randomItem() {
		int chosen = 0;
		while (!items[chosen]) {
			chosen = RandomSource.nextInt(items.length);
		}
		return chosen;
	}

	public int randomNonTM() {
		int chosen = 0;
		while (!items[chosen] || tms[chosen]) {
			chosen = RandomSource.nextInt(items.length);
		}
		return chosen;
	}
	
	public int randomTM() {
		int chosen = 0;
		while (!tms[chosen]) {
			chosen = RandomSource.nextInt(items.length);
		}
		return chosen;
	}

}
