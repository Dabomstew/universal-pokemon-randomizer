package com.dabomstew.pkrandom;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.dabomstew.pkrandom.romhandlers.RomHandler;

public class CodeTweaks {
	
	public static final int BW_EXP_PATCH = 1;
	public static final int NERF_X_ACCURACY = 2;
	public static final int FIX_CRIT_RATE = 4;
	public static final int FASTEST_TEXT = 8;
	public static final int RUNNING_SHOES_INDOORS = 16;
	public static final int RANDOMIZE_PC_POTION = 32;
	public static final int ALLOW_PIKACHU_EVOLUTION = 64;
	
	private static ResourceBundle bundle = ResourceBundle
			.getBundle("com/dabomstew/pkrandom/gui/Bundle");
	
	private int value;
	private String tweakName;
	private String tooltipText;
	private CodeTweakApplier applier;
	
	
	public CodeTweaks(int value, String tweakID, CodeTweakApplier applier) {
		this.value = value;
		this.tweakName = bundle.getString("CodeTweaks."+tweakID+".name");
		this.tooltipText = bundle.getString("CodeTweaks."+tweakID+".toolTipText");
		this.applier = applier;
	}
	
	public void applyTo(RomHandler rom) {
		applier.applyTo(rom);
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

	public static interface CodeTweakApplier {
		public void applyTo(RomHandler rom);
	}
	
	public static List<CodeTweaks> allTweaks = new ArrayList<CodeTweaks>();
	
	static {
		allTweaks.add(new CodeTweaks(BW_EXP_PATCH, "bwPatch", new CodeTweakApplier() {

			@Override
			public void applyTo(RomHandler rom) {
				rom.applyBWEXPPatch();
				
			}}));
		allTweaks.add(new CodeTweaks(NERF_X_ACCURACY, "nerfXAcc", new CodeTweakApplier() {

			@Override
			public void applyTo(RomHandler rom) {
				rom.applyXAccNerfPatch();
				
			}}));
		allTweaks.add(new CodeTweaks(FIX_CRIT_RATE, "critRateFix", new CodeTweakApplier() {

			@Override
			public void applyTo(RomHandler rom) {
				rom.applyCritRatePatch();
				
			}}));
		allTweaks.add(new CodeTweaks(FASTEST_TEXT, "fastestText", new CodeTweakApplier() {

			@Override
			public void applyTo(RomHandler rom) {
				rom.applyFastestTextPatch();
				
			}}));
		allTweaks.add(new CodeTweaks(RUNNING_SHOES_INDOORS, "runningShoes", new CodeTweakApplier() {

			@Override
			public void applyTo(RomHandler rom) {
				rom.applyRunningShoesIndoorsPatch();
				
			}}));
		allTweaks.add(new CodeTweaks(RANDOMIZE_PC_POTION, "pcPotion", new CodeTweakApplier() {

			@Override
			public void applyTo(RomHandler rom) {
				rom.randomizePCPotion();
				
			}}));
		allTweaks.add(new CodeTweaks(ALLOW_PIKACHU_EVOLUTION, "pikachuEvo", new CodeTweakApplier() {

			@Override
			public void applyTo(RomHandler rom) {
				rom.applyPikachuEvoPatch();
				
			}}));
	}

}
