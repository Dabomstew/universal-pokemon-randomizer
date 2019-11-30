package com.dabomstew.pkrandom.constants;

/*----------------------------------------------------------------------------*/
/*--  GBConstants.java - standard GB values.                                --*/
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

public class GBConstants {

    public static final int minRomSize = 0x80000, maxRomSize = 0x200000;

    public static final int jpFlagOffset = 0x14A, versionOffset = 0x14C, crcOffset = 0x14E, romSigOffset = 0x134,
            isGBCOffset = 0x143, romCodeOffset = 0x13F;

    public static final int stringTerminator = 0x50, stringPrintedTextEnd = 0x57, stringPrintedTextPromptEnd = 0x58;

    public static final int bankSize = 0x4000;

    public static final byte gbZ80Jump = (byte) 0xC3, gbZ80Nop = 0x00, gbZ80XorA = (byte) 0xAF, gbZ80LdA = 0x3E,
            gbZ80LdAToFar = (byte) 0xEA, gbZ80Ret = (byte) 0xC9, gbZ80JumpRelative = (byte) 0x18;

}
