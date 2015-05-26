package com.dabomstew.pkrandom.constants;

public class GBConstants {
	
	public static final int minRomSize = 0x80000, maxRomSize = 0x200000;

	public static final int jpFlagOffset = 0x14A, versionOffset = 0x14C,
			crcOffset = 0x14E, romSigOffset = 0x134, isGBCOffset = 0x143, romCodeOffset = 0x13F;
	
	public static final int stringTerminator = 0x50, stringNull = 0x00;

	public static final int bankSize = 0x4000;

	public static final byte gbZ80Jump = (byte) 0xC3, gbZ80Nop = 0x00,
			gbZ80XorA = (byte) 0xAF, gbZ80LdA = 0x3E,
			gbZ80LdAToFar = (byte) 0xEA, gbZ80Ret = (byte) 0xC9;

}
