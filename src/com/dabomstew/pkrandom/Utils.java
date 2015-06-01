package com.dabomstew.pkrandom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

import javax.xml.bind.DatatypeConverter;

public class Utils {

	public static void validateRomFile(File fh) throws InvalidROMException {
		// first, check for common filetypes that aren't ROMs
		// read first 10 bytes of the file to do this
		try {
			FileInputStream fis = new FileInputStream(fh);
			byte[] sig = new byte[10];
			int sigLength = fis.read(sig);
			fis.close();
			if (sigLength < 10) {
				throw new InvalidROMException(
						InvalidROMException.Type.LENGTH,
						String.format(
								"%s appears to be a blank or nearly blank file.",
								fh.getName()));
			}
			if (sig[0] == 0x50 && sig[1] == 0x4b && sig[2] == 0x03
					&& sig[3] == 0x04) {
				throw new InvalidROMException(
						InvalidROMException.Type.ZIP_FILE,
						String.format("%s is a ZIP archive, not a ROM.",
								fh.getName()));
			}
			if (sig[0] == 0x52 && sig[1] == 0x61 && sig[2] == 0x72
					&& sig[3] == 0x21 && sig[4] == 0x1A && sig[5] == 0x07) {
				throw new InvalidROMException(
						InvalidROMException.Type.RAR_FILE,
						String.format("%s is a RAR archive, not a ROM.",
								fh.getName()));
			}
			if (sig[0] == 'P' && sig[1] == 'A' && sig[2] == 'T'
					&& sig[3] == 'C' && sig[4] == 'H') {
				throw new InvalidROMException(
						InvalidROMException.Type.IPS_FILE, String.format(
								"%s is a IPS patch, not a ROM.", fh.getName()));
			}
		} catch (IOException ex) {
			throw new InvalidROMException(InvalidROMException.Type.UNREADABLE,
					String.format("Could not read %s from disk.", fh.getName()));
		}
	}

	// RomHandlers implicitly rely on these - call this before creating settings
	// etc.
	public static void testForRequiredConfigs() throws FileNotFoundException {
		String[] required = new String[] { "gameboy_jap.tbl",
				"rby_english.tbl", "rby_freger.tbl", "rby_espita.tbl",
				"green_translation.tbl", "gsc_english.tbl", "gsc_freger.tbl",
				"gsc_espita.tbl", "gba_english.tbl", "gba_jap.tbl",
				"Generation4.tbl", "Generation5.tbl", "gen1_offsets.ini",
				"gen2_offsets.ini", "gen3_offsets.ini", "gen4_offsets.ini",
				"gen5_offsets.ini", "trainerclasses.txt", "trainernames.txt",
				"nicknames.txt" };
		for (String filename : required) {
			if (!FileFunctions.configExists(filename)) {
				throw new FileNotFoundException(filename);
			}
		}
	}

	public static void validatePresetSupplementFiles(String config,
			byte[] trainerClasses, byte[] trainerNames, byte[] nicknames)
			throws UnsupportedEncodingException,
			InvalidSupplementFilesException {
		byte[] data = DatatypeConverter.parseBase64Binary(config);

		if (data.length < Settings.LENGTH_OF_SETTINGS_DATA+17) {
			throw new InvalidSupplementFilesException(
					InvalidSupplementFilesException.Type.TOO_SHORT,
					"The preset config is too short to be valid");
		}

		// Check the checksum
		ByteBuffer buf = ByteBuffer.allocate(4).put(data, data.length - 16, 4);
		buf.rewind();
		int crc = buf.getInt();

		CRC32 checksum = new CRC32();
		checksum.update(data, 0, data.length - 16);
		if ((int) checksum.getValue() != crc) {
			throw new IllegalArgumentException("Checksum failure.");
		}

		// Check the trainerclass & trainernames & nicknames crc
		if (trainerClasses == null
				&& !FileFunctions.checkOtherCRC(data, 0, 6,
						"trainerclasses.txt", data.length - 12)) {
			throw new InvalidSupplementFilesException(
					InvalidSupplementFilesException.Type.NICKNAMES,
					"Can't use this preset because you have a different set "
							+ "of random trainer class names to the creator.");
		}
		if (trainerNames == null
				&& (!FileFunctions.checkOtherCRC(data, 0, 5,
						"trainernames.txt", data.length - 8) || !FileFunctions
						.checkOtherCRC(data, 16, 5, "trainernames.txt",
								data.length - 8))) {
			throw new InvalidSupplementFilesException(
					InvalidSupplementFilesException.Type.NICKNAMES,
					"Can't use this preset because you have a different set "
							+ "of random trainer names to the creator.");
		}
		if (nicknames == null
				&& !FileFunctions.checkOtherCRC(data, 16, 4, "nicknames.txt",
						data.length - 4)) {
			throw new InvalidSupplementFilesException(
					InvalidSupplementFilesException.Type.NICKNAMES,
					"Can't use this preset because you have a different set "
							+ "of random nicknames to the creator.");
		}
	}

	public static class InvalidROMException extends Exception {
		public enum Type {
			LENGTH, ZIP_FILE, RAR_FILE, IPS_FILE, UNREADABLE
		}

		private final Type type;

		public InvalidROMException(Type type, String message) {
			super(message);
			this.type = type;
		}

		public Type getType() {
			return type;
		}
	}
}