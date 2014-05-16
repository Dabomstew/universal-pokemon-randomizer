package com.dabomstew.pkrandom.gui;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

import javax.xml.bind.DatatypeConverter;

public class QuickSettingsUpdater {

	private byte[] dataBlock;
	private int actualDataLength;

	/**
	 * Given a quicksettings config string from an old randomizer version,
	 * update it to be compatible with the currently running randomizer version.
	 * 
	 * @param oldVersion
	 *            The PRESET_FILE_VERSION used to generate the given string
	 * @param configString
	 *            The outdated config string
	 * @return The updated config string to be applied
	 */
	public String update(int oldVersion, String configString) {
		byte[] data = DatatypeConverter.parseBase64Binary(configString);
		this.dataBlock = new byte[100];
		this.actualDataLength = data.length;
		System.arraycopy(data, 0, this.dataBlock, 0, this.actualDataLength);

		// new field values here are written as bitwise ORs
		// this is slightly slower in execution, but it makes it clearer
		// just what values we actually want to set
		// bit fields 1 2 3 4 5 6 7 8
		// are values 0x01 0x02 0x04 0x08 0x10 0x20 0x40 0x80

		// versions prior to 120 didn't have quick settings file,
		// they're just included here for completeness' sake

		// versions < 102: add abilities set to unchanged
		if (oldVersion < 102) {
			dataBlock[1] |= 0x10;
		}

		// versions < 110: add move tutor byte (set both to unchanged)
		if (oldVersion < 110) {
			insertExtraByte(15, (byte) (0x04 | 0x10));
		}

		// version 110-111 no change (only added trainer names/classes to preset
		// files, and some checkboxes which it is safe to leave as off)

		// 111-112 no change (another checkbox we leave as off)

		// 112-120 no change (only another checkbox)

		// 120-150 new features
		if (oldVersion < 150) {
			// trades and field items: both unchanged
			insertExtraByte(16, (byte) (0x40));
			insertExtraByte(17, (byte) (0x04));
			// add a fake checksum for nicknames at the very end of the data,
			// we can leave it at 0
			actualDataLength += 4;
		}

		// 150-160 lots of re-org etc
		if (oldVersion < 160) {
			// byte 0:
			// copy "update moves" to "update legacy moves"
			// move the other 3 fields after it up one
			int firstByte = dataBlock[0] & 0xFF;
			int updateMoves = firstByte & 0x08;
			int laterFields = firstByte & (0x10 | 0x20 | 0x40);
			dataBlock[0] = (byte) ((firstByte & (0x01 | 0x02 | 0x04 | 0x08))
					| (updateMoves << 1) | (laterFields << 1));

			// byte 1:
			// leave as is (don't turn on exp standardization)

			// byte 2:
			// retrieve values of bw exp patch & held items
			// code tweaks keeps the same value as bw exp patch had
			// but turn held items off (it got replaced by pokelimit)
			int hasBWPatch = (dataBlock[2] & 0x08) >> 3;
			int hasHeldItems = (dataBlock[2] & 0x80) >> 7;
			dataBlock[2] &= (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40);

			// byte 3:
			// turn on starter held items if held items checkbox was on
			if (hasHeldItems > 0) {
				dataBlock[3] |= 0x10;
			}

			// byte 4-9 are starters
			// byte 10 adds "4 moves" but we leave it off

			// byte 11:
			// pull out value of WP no legendaries
			// replace it with TP no early shedinja
			// also get WP catch rate value
			int wpNoLegendaries = (dataBlock[11] & 0x80) >> 7;
			int tpNoEarlyShedinja = (dataBlock[13] & 0x10) >> 4;
			int wpCatchRate = (dataBlock[13] & 0x08) >> 3;
			dataBlock[11] = (byte) ((dataBlock[11] & (0x01 | 0x02 | 0x04 | 0x08
					| 0x10 | 0x20 | 0x40)) | (tpNoEarlyShedinja << 7));

			// byte 12 unchanged

			// insert a new byte for "extra" WP stuff
			// include no legendaries & catch rate
			// also include WP held items if overall held items box was on
			// leave similar strength off, there's a bugfix a little later on...
			insertExtraByte(
					13,
					(byte) ((wpCatchRate) | (wpNoLegendaries << 1) | (hasHeldItems << 3)));

			// new byte 14 (was 13 in 150):
			// switch off bits 4 and 5 (were for catch rate & no early shedinja)
			dataBlock[14] &= 0x07;

			// the rest of the config bytes are unchanged
			// but we need to add the fields for pokemon limit & code tweaks

			// no pokemon limit
			insertIntField(19, 0);

			// only possible code tweak = bw exp
			insertIntField(23, hasBWPatch);
		}

		// 160 bug:
		// check if all of the WPAdditionalRule bitfields are unset
		// (None, Type Themed, Catch Em All)
		// if they are all unset, switch "similar strength" on
		if ((dataBlock[12] & (0x01 | 0x04 | 0x08)) == 0) {
			dataBlock[13] |= 0x04;
		}

		// 160 to 161: no change
		// the only changes were in implementation, which broke presets, but
		// leaves settings files the same

		// fix checksum
		CRC32 checksum = new CRC32();
		checksum.update(dataBlock, 0, actualDataLength - 16);

		// convert crc32 to int bytes
		byte[] crcBuf = ByteBuffer.allocate(4)
				.putInt((int) checksum.getValue()).array();
		System.arraycopy(crcBuf, 0, dataBlock, actualDataLength - 16, 4);

		// have to make a new byte array to convert to base64
		byte[] finalConfigString = new byte[actualDataLength];
		System.arraycopy(dataBlock, 0, finalConfigString, 0, actualDataLength);
		return DatatypeConverter.printBase64Binary(finalConfigString);
	}

	/**
	 * Insert a 4-byte int field in the data block at the given position. Shift
	 * everything else up. Do nothing if there's no room left (should never
	 * happen)
	 * 
	 * @param position
	 *            The offset to add the field
	 * @param value
	 *            The value to give to the field
	 */
	private void insertIntField(int position, int value) {
		if (actualDataLength + 4 > dataBlock.length) {
			// can't do
			return;
		}
		for (int j = actualDataLength; j > position + 3; j--) {
			dataBlock[j] = dataBlock[j - 4];
		}
		byte[] valueBuf = ByteBuffer.allocate(4).putInt(value).array();
		System.arraycopy(valueBuf, 0, dataBlock, position, 4);
		actualDataLength += 4;
	}

	/**
	 * Insert a byte-field in the data block at the given position. Shift
	 * everything else up. Do nothing if there's no room left (should never
	 * happen)
	 * 
	 * @param position
	 *            The offset to add the field
	 * @param value
	 *            The value to give to the field
	 */
	private void insertExtraByte(int position, byte value) {
		if (actualDataLength == dataBlock.length) {
			// can't do
			return;
		}
		for (int j = actualDataLength; j > position; j--) {
			dataBlock[j] = dataBlock[j - 1];
		}
		dataBlock[position] = value;
		actualDataLength++;
	}

}
