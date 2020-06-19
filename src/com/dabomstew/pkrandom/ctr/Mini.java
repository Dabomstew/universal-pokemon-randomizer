package com.dabomstew.pkrandom.ctr;

/*----------------------------------------------------------------------------*/
/*--  Mini.java - class for packing/unpacking Mini archives                 --*/
/*--                                                                        --*/
/*--  Code based on "pk3DS", copyright (C) Kaphotics                        --*/
/*--                                                                        --*/
/*--  Ported to Java by UPR-ZX Team under the terms of the GPL:             --*/
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Mini {
    public static byte[] PackMini(byte[][] fileData, String identifier) throws IOException {
        // Create new Binary with the relevant header bytes
        byte[] data = new byte[4];
        data[0] = (byte) identifier.charAt(0);
        data[1] = (byte) identifier.charAt(1);
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putShort((short) fileData.length);
        System.arraycopy(buf.array(), 0, data, 2, 2);

        int count = fileData.length;
        int dataOffset = 4 + 4 + (count * 4);

        // Start the data filling
        ByteArrayOutputStream dataOut = new ByteArrayOutputStream();
        ByteArrayOutputStream offsetMap = new ByteArrayOutputStream();
        // For each file...
        for (int i = 0; i < count; i++) {
            int fileOffset = dataOut.size() + dataOffset;
            buf = ByteBuffer.allocate(4);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.putInt(fileOffset);
            offsetMap.write(buf.array());
            dataOut.write(fileData[i]);

            // Pad with zeroes until len % 4 == 0
            while (dataOut.size() % 4 != 0) {
                dataOut.write((byte) 0);
            }
        }
        // Cap the file
        buf = ByteBuffer.allocate(4);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(dataOut.size() + dataOffset);
        offsetMap.write(buf.array());

        ByteArrayOutputStream newPack = new ByteArrayOutputStream();
        ByteArrayOutputStream header = new ByteArrayOutputStream();
        header.write(data);
        header.writeTo(newPack);
        offsetMap.writeTo(newPack);
        dataOut.writeTo(newPack);
        return newPack.toByteArray();
    }

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
