package com.dabomstew.pkrandom;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MiscTweak {

	private static final ResourceBundle bundle = ResourceBundle
			.getBundle("com/dabomstew/pkrandom/gui/Bundle");

	public static List<MiscTweak> allTweaks = new ArrayList<MiscTweak>();

	/* @formatter:off */
	public static final MiscTweak BW_EXP_PATCH = new MiscTweak(1, "bwPatch");
	public static final MiscTweak NERF_X_ACCURACY = new MiscTweak(2, "nerfXAcc");
	public static final MiscTweak FIX_CRIT_RATE = new MiscTweak(4, "critRateFix");
	public static final MiscTweak FASTEST_TEXT = new MiscTweak(8, "fastestText");
	public static final MiscTweak RUNNING_SHOES_INDOORS = new MiscTweak(16, "runningShoes");
	public static final MiscTweak RANDOMIZE_PC_POTION = new MiscTweak(32, "pcPotion");
	public static final MiscTweak ALLOW_PIKACHU_EVOLUTION = new MiscTweak(64, "pikachuEvo");
	public static final MiscTweak NATIONAL_DEX_AT_START = new MiscTweak(128, "nationalDex");
	public static final MiscTweak UPDATE_TYPE_EFFECTIVENESS = new MiscTweak(256, "typeEffectiveness");
	public static final MiscTweak RANDOMIZE_HIDDEN_HOLLOWS = new MiscTweak(512, "hiddenHollows");
	public static final MiscTweak LOWER_CASE_POKEMON_NAMES = new MiscTweak(1024, "lowerCaseNames");
	public static final MiscTweak RANDOMIZE_CATCHING_TUTORIAL = new MiscTweak(2048, "catchingTutorial");
	/* @formatter:on */

	private final int value;
	private final String tweakName;
	private final String tooltipText;

	private MiscTweak(int value, String tweakID) {
		this.value = value;
		this.tweakName = bundle.getString("CodeTweaks." + tweakID + ".name");
		this.tooltipText = bundle.getString("CodeTweaks." + tweakID
				+ ".toolTipText");
		allTweaks.add(this);
	}

	public int getValue() {
		return value;
	}

	public String getTweakName() {
		return tweakName;
	}

	public String getTooltipText() {
		return tooltipText;
	}

}
