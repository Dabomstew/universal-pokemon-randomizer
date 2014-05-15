package com.dabomstew.pkrandom.pokemon;

public enum ExpCurve {

	SLOW, MEDIUM_SLOW, MEDIUM_FAST, FAST, ERRATIC, FLUCTUATING;

	public static ExpCurve fromByte(byte curve) {
		switch (curve) {
		case 0:
			return MEDIUM_FAST;
		case 1:
			return ERRATIC;
		case 2:
			return FLUCTUATING;
		case 3:
			return MEDIUM_SLOW;
		case 4:
			return FAST;
		case 5:
			return SLOW;
		}
		return null;
	}

	public byte toByte() {
		switch (this) {
		case SLOW:
			return 5;
		case MEDIUM_SLOW:
			return 3;
		case MEDIUM_FAST:
			return 0;
		case FAST:
			return 4;
		case ERRATIC:
			return 1;
		case FLUCTUATING:
			return 2;
		}
		return 0; // default
	}

}
