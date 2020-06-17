package com.dabomstew.pkrandom.ctr;

/*----------------------------------------------------------------------------*/
/*--  Mini.java - class for packing/unpacking Mini archives                 --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer" by Dabomstew                   --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2012.                                 --*/
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

import com.dabomstew.pkrandom.FileFunctions;

public class Mini {
    public static byte[][] UnpackMini(byte[] fileData, String identifier) {
        if (fileData == null || fileData.length < 4) {
            return null;
        }

        if (identifier.charAt(0) != fileData[0] || identifier.charAt(1) != fileData[1]) {
            return null;
        }

        int count = FileFunctions.read2ByteInt(fileData, 2);
        int ctr = 4;
        int start = FileFunctions.readFullIntLittleEndian(fileData, ctr);
        ctr += 4;
        byte[][] returnData = new byte[count][];
        for (int i = 0; i < count; i++) {
            int end = FileFunctions.readFullIntLittleEndian(fileData, ctr);
            ctr += 4;
            int len = end - start;
            byte[] data = new byte[len];
            System.arraycopy(fileData, start, data, 0, len);
            returnData[i] = data;
            start = end;
        }
        return returnData;
    }
}
