package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  AbstractDSRomHandler.java - a base class for DS rom handlers			--*/
/*--                              which standardises common DS functions.	--*/
/*--  																		--*/
/*--  Part of "Universal Pokemon Randomizer" by Dabomstew					--*/
/*--  Pokemon and any associated names and the like are						--*/
/*--  trademark and (C) Nintendo 1996-2012.									--*/
/*--  																		--*/
/*--  The custom code written here is licensed under the terms of the GPL:	--*/
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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.dabomstew.pkrandom.newnds.NDSRom;
import com.dabomstew.pkrandom.pokemon.Type;

public abstract class AbstractDSRomHandler extends AbstractRomHandler {

	protected String dataFolder;
	private NDSRom baseRom;
	private String loadedFN;

	@Override
	public boolean detectRom(String filename) {
		try {
			FileInputStream fis = new FileInputStream(filename);
			fis.skip(0x0C);
			byte[] sig = new byte[4];
			fis.read(sig);
			fis.close();
			String ndsCode = new String(sig, "US-ASCII");
			return detectNDSRom(ndsCode);
		} catch (Exception e) {
			return false;
		}
	}

	protected abstract boolean detectNDSRom(String ndsCode);

	@Override
	public boolean loadRom(String filename) {
		if (!detectRom(filename)) {
			return false;
		}
		// Load inner rom
		try {
			baseRom = new NDSRom(filename);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		loadedFN = filename;
		loadedROM();
		return true;
	}

	@Override
	public String loadedFilename() {
		return loadedFN;
	}

	protected byte[] get3byte(int amount) {
		byte[] ret = new byte[3];
		ret[0] = (byte) (amount & 0xFF);
		ret[1] = (byte) ((amount >> 8) & 0xFF);
		ret[2] = (byte) ((amount >> 16) & 0xFF);
		return ret;
	}

	protected abstract void loadedROM();

	protected abstract void savingROM();

	@Override
	public boolean saveRom(String filename) {
		savingROM();
		try {
			baseRom.saveTo(filename);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	public void closeInnerRom() throws IOException {
		baseRom.closeROM();
	}

	@Override
	public boolean canChangeStaticPokemon() {
		return false;
	}

	public NARCContents readNARC(String subpath) throws IOException {
		Map<String, byte[]> frames = readNitroFrames(subpath);
		if (!frames.containsKey("FATB") || !frames.containsKey("FNTB")
				|| !frames.containsKey("FIMG")) {
			System.err.println("Not a valid narc file");
			return null;
		}
		// File contents
		NARCContents narc = new NARCContents();
		byte[] fatbframe = frames.get("FATB");
		byte[] fimgframe = frames.get("FIMG");
		int fileCount = readLong(fatbframe, 0);
		for (int i = 0; i < fileCount; i++) {
			int startOffset = readLong(fatbframe, 4 + i * 8);
			int endOffset = readLong(fatbframe, 8 + i * 8);
			int length = (endOffset - startOffset);
			byte[] thisFile = new byte[length];
			try {
				System.arraycopy(fimgframe, startOffset, thisFile, 0, length);
			} catch (ArrayIndexOutOfBoundsException ex) {
				System.out.printf(
						"AIOBEX: start %d length %d size of frame %d\n",
						startOffset, length, fimgframe.length);
			}
			narc.files.add(thisFile);
		}
		// Filenames?
		byte[] fntbframe = frames.get("FNTB");
		int unk1 = readLong(fntbframe, 0);
		if (unk1 == 8) {
			// Filenames exist
			narc.hasFilenames = true;
			int offset = 8;
			for (int i = 0; i < fileCount; i++) {
				int fnLength = (fntbframe[offset] & 0xFF);
				offset++;
				byte[] filenameBA = new byte[fnLength];
				System.arraycopy(fntbframe, offset, filenameBA, 0, fnLength);
				String filename = new String(filenameBA, "US-ASCII");
				narc.filenames.add(filename);
			}
		} else {
			narc.hasFilenames = false;
			for (int i = 0; i < fileCount; i++) {
				narc.filenames.add(null);
			}
		}
		return narc;
	}

	public void writeNARC(String subpath, NARCContents narc) throws IOException {
		// Get bytes required for FIMG frame
		int bytesRequired = 0;
		for (byte[] file : narc.files) {
			bytesRequired += Math.ceil(file.length / 4.0) * 4;
		}
		// FIMG frame & FATB frame build

		// 4 for numentries, 8*size for entries, 8 for nitro header
		byte[] fatbFrame = new byte[4 + narc.files.size() * 8 + 8];
		// bytesRequired + 8 for nitro header
		byte[] fimgFrame = new byte[bytesRequired + 8];

		// Nitro headers
		fatbFrame[0] = 'B';
		fatbFrame[1] = 'T';
		fatbFrame[2] = 'A';
		fatbFrame[3] = 'F';
		writeLong(fatbFrame, 4, fatbFrame.length);

		fimgFrame[0] = 'G';
		fimgFrame[1] = 'M';
		fimgFrame[2] = 'I';
		fimgFrame[3] = 'F';
		writeLong(fimgFrame, 4, fimgFrame.length);
		int offset = 0;

		writeLong(fatbFrame, 8, narc.files.size());
		for (int i = 0; i < narc.files.size(); i++) {
			byte[] file = narc.files.get(i);
			int bytesRequiredForFile = (int) (Math.ceil(file.length / 4.0) * 4);
			System.arraycopy(file, 0, fimgFrame, offset + 8, file.length);
			for (int filler = file.length; filler < bytesRequiredForFile; filler++) {
				fimgFrame[offset + 8 + filler] = (byte) 0xFF;
			}
			writeLong(fatbFrame, 12 + i * 8, offset);
			writeLong(fatbFrame, 16 + i * 8, offset + file.length);
			offset += bytesRequiredForFile;
		}

		// FNTB Frame
		int bytesForFNTBFrame = 16;
		if (narc.hasFilenames) {
			for (String filename : narc.filenames) {
				bytesForFNTBFrame += filename.getBytes("US-ASCII").length + 1;
			}
		}
		byte[] fntbFrame = new byte[bytesForFNTBFrame];

		fntbFrame[0] = 'B';
		fntbFrame[1] = 'T';
		fntbFrame[2] = 'N';
		fntbFrame[3] = 'F';
		writeLong(fntbFrame, 4, fntbFrame.length);

		if (narc.hasFilenames) {
			writeLong(fntbFrame, 8, 8);
			writeLong(fntbFrame, 12, 0x10000);
			int fntbOffset = 16;
			for (String filename : narc.filenames) {
				byte[] fntbfilename = filename.getBytes("US-ASCII");
				fntbFrame[fntbOffset] = (byte) fntbfilename.length;
				System.arraycopy(fntbfilename, 0, fntbFrame, fntbOffset + 1,
						fntbfilename.length);
				fntbOffset += 1 + fntbfilename.length;
			}
		} else {
			writeLong(fntbFrame, 8, 4);
			writeLong(fntbFrame, 12, 0x10000);
		}

		// Now for the actual Nitro file
		int nitrolength = 16 + fatbFrame.length + fntbFrame.length
				+ fimgFrame.length;
		byte[] nitroFile = new byte[nitrolength];
		nitroFile[0] = 'N';
		nitroFile[1] = 'A';
		nitroFile[2] = 'R';
		nitroFile[3] = 'C';
		writeWord(nitroFile, 4, 0xFFFE);
		writeWord(nitroFile, 6, 0x0100);
		writeLong(nitroFile, 8, nitrolength);
		writeWord(nitroFile, 12, 0x10);
		writeWord(nitroFile, 14, 3);
		System.arraycopy(fatbFrame, 0, nitroFile, 16, fatbFrame.length);
		System.arraycopy(fntbFrame, 0, nitroFile, 16 + fatbFrame.length,
				fntbFrame.length);
		System.arraycopy(fimgFrame, 0, nitroFile, 16 + fatbFrame.length
				+ fntbFrame.length, fimgFrame.length);
		this.writeFile(subpath, nitroFile);
	}

	private Map<String, byte[]> readNitroFrames(String filename)
			throws IOException {
		byte[] wholeFile = this.readFile(filename);

		// Read the number of frames
		int frameCount = readWord(wholeFile, 0x0E);

		// each frame
		int offset = 0x10;
		Map<String, byte[]> frames = new TreeMap<String, byte[]>();
		for (int i = 0; i < frameCount; i++) {
			byte[] magic = new byte[] { wholeFile[offset + 3],
					wholeFile[offset + 2], wholeFile[offset + 1],
					wholeFile[offset] };
			String magicS = new String(magic, "US-ASCII");

			int frame_size = readLong(wholeFile, offset + 4);
			// Patch for BB/VW and other DS hacks which don't update
			// the size of their expanded NARCs correctly
			if (i == frameCount - 1 && offset + frame_size < wholeFile.length) {
				frame_size = wholeFile.length - offset;
			}
			byte[] frame = new byte[frame_size - 8];
			System.arraycopy(wholeFile, offset + 8, frame, 0, frame_size - 8);
			frames.put(magicS, frame);
			offset += frame_size;
		}
		return frames;
	}

	protected int readWord(byte[] data, int offset) {
		return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8);
	}

	protected int readLong(byte[] data, int offset) {
		return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8)
				| ((data[offset + 2] & 0xFF) << 16)
				| ((data[offset + 3] & 0xFF) << 24);
	}

	protected int readRelativePointer(byte[] data, int offset) {
		return readLong(data, offset) + offset + 4;
	}

	protected void writeWord(byte[] data, int offset, int value) {
		data[offset] = (byte) (value & 0xFF);
		data[offset + 1] = (byte) ((value >> 8) & 0xFF);
	}

	protected void writeLong(byte[] data, int offset, int value) {
		data[offset] = (byte) (value & 0xFF);
		data[offset + 1] = (byte) ((value >> 8) & 0xFF);
		data[offset + 2] = (byte) ((value >> 16) & 0xFF);
		data[offset + 3] = (byte) ((value >> 24) & 0xFF);
	}

	protected void writeRelativePointer(byte[] data, int offset, int pointer) {
		int relPointer = pointer - (offset + 4);
		writeLong(data, offset, relPointer);
	}

	protected byte[] readFile(String location) throws IOException {
		return baseRom.getFile(location);
	}

	protected void writeFile(String location, byte[] data) throws IOException {
		writeFile(location, data, 0, data.length);
	}

	protected void writeFile(String location, byte[] data, int offset,
			int length) throws IOException {
		if (offset != 0 || length != data.length) {
			byte[] newData = new byte[length];
			System.arraycopy(data, offset, newData, 0, length);
			data = newData;
		}
		baseRom.writeFile(location, data);
	}

	protected byte[] readARM9() throws IOException {
		return baseRom.getARM9();
	}

	protected void writeARM9(byte[] data) throws IOException {
		baseRom.writeARM9(data);
	}

	protected byte[] readOverlay(int number) throws IOException {
		return baseRom.getOverlay(number);
	}

	protected void writeOverlay(int number, byte[] data) throws IOException {
		baseRom.writeOverlay(number, data);
	}

	protected void readByteIntoFlags(byte[] data, boolean[] flags,
			int offsetIntoFlags, int offsetIntoData) {
		int thisByte = data[offsetIntoData] & 0xFF;
		for (int i = 0; i < 8 && (i + offsetIntoFlags) < flags.length; i++) {
			flags[offsetIntoFlags + i] = ((thisByte >> i) & 0x01) == 0x01;
		}
	}

	protected byte getByteFromFlags(boolean[] flags, int offsetIntoFlags) {
		int thisByte = 0;
		for (int i = 0; i < 8 && (i + offsetIntoFlags) < flags.length; i++) {
			thisByte |= (flags[offsetIntoFlags + i] ? 1 : 0) << i;
		}
		return (byte) thisByte;
	}

	protected int typeTMPaletteNumber(Type t) {
		if (t == null) {
			return 411; // CURSE
		}
		switch (t) {
		case FIGHTING:
			return 398;
		case DRAGON:
			return 399;
		case WATER:
			return 400;
		case PSYCHIC:
			return 401;
		case NORMAL:
			return 402;
		case POISON:
			return 403;
		case ICE:
			return 404;
		case GRASS:
			return 405;
		case FIRE:
			return 406;
		case DARK:
			return 407;
		case STEEL:
			return 408;
		case ELECTRIC:
			return 409;
		case GROUND:
			return 410;
		case GHOST:
		default:
			return 411; // for CURSE
		case ROCK:
			return 412;
		case FLYING:
			return 413;
		case BUG:
			return 610;
		}
	}

}
