package com.dabomstew.pkrandom.ctr;

/*----------------------------------------------------------------------------*/
/*--  NCCH.java - a base class for dealing with 3DS SMDH (icon.bin) files.  --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2020.                                 --*/
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

import java.nio.charset.StandardCharsets;

public class SMDH {

    private byte[] data;
    private String[] shortDescriptions = new String[12];
    private String[] longDescriptions = new String[12];
    private String[] publishers = new String[12];

    private static final int smdh_magic = 0x48444D53;
    private static final int length_of_title = 0x200;
    private static final int short_description_length = 0x80;
    private static final int long_description_length = 0x100;
    private static final int publisher_length = 0x80;

    public SMDH(byte[] smdhData) {
        data = smdhData;
        if (this.isValid()) {
            readDescriptionsAndPublishers();
        }
    }

    public byte[] getBytes() {
        return data;
    }

    public void setAllDescriptions(String newDescription) {
        byte[] newDescriptionBytes = newDescription.getBytes(StandardCharsets.UTF_16LE);
        if (newDescriptionBytes.length <= short_description_length) {
            for (int i = 0; i < 12; i++) {
                shortDescriptions[i] = newDescription;
                longDescriptions[i] = newDescription;
            }
            writeDescriptionsAndPublishers();
        }
    }

    public void setAllPublishers(String newPublisher) {
        byte[] newPublisherBytes = newPublisher.getBytes(StandardCharsets.UTF_16LE);
        if (newPublisherBytes.length <= publisher_length) {
            for (int i = 0; i < 12; i++) {
                publishers[i] = newPublisher;
            }
            writeDescriptionsAndPublishers();
        }
    }

    private boolean isValid() {
        int magic = FileFunctions.readFullIntLittleEndian(data, 0x0);
        return magic == smdh_magic;
    }

    private void readDescriptionsAndPublishers() {
        for (int i = 0; i < 12; i++) {
            int shortDescriptionOffset = 0x08 + (length_of_title * i);
            byte[] shortDescriptionBytes = new byte[short_description_length];
            System.arraycopy(data, shortDescriptionOffset, shortDescriptionBytes, 0, short_description_length);
            shortDescriptions[i] = new String(shortDescriptionBytes, StandardCharsets.UTF_16LE).trim();

            int longDescriptionOffset = 0x88 + (length_of_title * i);
            byte[] longDescriptionBytes = new byte[long_description_length];
            System.arraycopy(data, longDescriptionOffset, longDescriptionBytes, 0, long_description_length);
            longDescriptions[i] = new String(longDescriptionBytes, StandardCharsets.UTF_16LE).trim();

            int publisherOffset = 0x188 + (length_of_title * i);
            byte[] publisherBytes = new byte[publisher_length];
            System.arraycopy(data, publisherOffset, publisherBytes, 0, publisher_length);
            publishers[i] = new String(publisherBytes, StandardCharsets.UTF_16LE).trim();
        }
    }

    private void writeDescriptionsAndPublishers() {
        for (int i = 0; i < 12; i++) {
            byte[] emptyShortDescription = new byte[short_description_length];
            int shortDescriptionOffset = 0x08 + (length_of_title * i);
            byte[] shortDescriptionBytes = shortDescriptions[i].getBytes(StandardCharsets.UTF_16LE);
            System.arraycopy(emptyShortDescription, 0, data, shortDescriptionOffset, short_description_length);
            System.arraycopy(shortDescriptionBytes, 0, data, shortDescriptionOffset, shortDescriptionBytes.length);

            byte[] emptyLongDescription = new byte[long_description_length];
            int longDescriptionOffset = 0x88 + (length_of_title * i);
            byte[] longDescriptionBytes = longDescriptions[i].getBytes(StandardCharsets.UTF_16LE);
            System.arraycopy(emptyLongDescription, 0, data, longDescriptionOffset, long_description_length);
            System.arraycopy(longDescriptionBytes, 0, data, longDescriptionOffset, longDescriptionBytes.length);

            byte[] emptyPublisher = new byte[publisher_length];
            int publisherOffset = 0x188 + (length_of_title * i);
            byte[] publisherBytes = publishers[i].getBytes(StandardCharsets.UTF_16LE);
            System.arraycopy(emptyPublisher, 0, data, publisherOffset, publisher_length);
            System.arraycopy(publisherBytes, 0, data, publisherOffset, publisherBytes.length);
        }
    }
}
