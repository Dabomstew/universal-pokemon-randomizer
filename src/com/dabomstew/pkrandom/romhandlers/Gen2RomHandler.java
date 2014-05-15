package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  Gen2RomHandler.java - randomizer handler for G/S/C.					--*/
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import com.dabomstew.pkrandom.CodeTweaks;
import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.RandomSource;
import com.dabomstew.pkrandom.RomFunctions;
import com.dabomstew.pkrandom.pokemon.Encounter;
import com.dabomstew.pkrandom.pokemon.EncounterSet;
import com.dabomstew.pkrandom.pokemon.Evolution;
import com.dabomstew.pkrandom.pokemon.EvolutionType;
import com.dabomstew.pkrandom.pokemon.ExpCurve;
import com.dabomstew.pkrandom.pokemon.IngameTrade;
import com.dabomstew.pkrandom.pokemon.ItemList;
import com.dabomstew.pkrandom.pokemon.Move;
import com.dabomstew.pkrandom.pokemon.MoveLearnt;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.TrainerPokemon;
import com.dabomstew.pkrandom.pokemon.Type;

public class Gen2RomHandler extends AbstractGBRomHandler {

	private static final Type[] typeTable = constructTypeTable();

	private static Type[] constructTypeTable() {
		Type[] table = new Type[256];
		table[0x00] = Type.NORMAL;
		table[0x01] = Type.FIGHTING;
		table[0x02] = Type.FLYING;
		table[0x03] = Type.POISON;
		table[0x04] = Type.GROUND;
		table[0x05] = Type.ROCK;
		table[0x07] = Type.BUG;
		table[0x08] = Type.GHOST;
		table[0x09] = Type.STEEL;
		table[0x14] = Type.FIRE;
		table[0x15] = Type.WATER;
		table[0x16] = Type.GRASS;
		table[0x17] = Type.ELECTRIC;
		table[0x18] = Type.PSYCHIC;
		table[0x19] = Type.ICE;
		table[0x1A] = Type.DRAGON;
		table[0x1B] = Type.DARK;
		return table;
	}

	private static byte typeToByte(Type type) {
		if (type == null) {
			return 0x13; // ???-type
		}
		switch (type) {
		case NORMAL:
			return 0x00;
		case FIGHTING:
			return 0x01;
		case FLYING:
			return 0x02;
		case POISON:
			return 0x03;
		case GROUND:
			return 0x04;
		case ROCK:
			return 0x05;
		case BUG:
			return 0x07;
		case GHOST:
			return 0x08;
		case FIRE:
			return 0x14;
		case WATER:
			return 0x15;
		case GRASS:
			return 0x16;
		case ELECTRIC:
			return 0x17;
		case PSYCHIC:
			return 0x18;
		case ICE:
			return 0x19;
		case DRAGON:
			return 0x1A;
		case STEEL:
			return 0x09;
		case DARK:
			return 0x1B;
		default:
			return 0; // normal by default
		}
	}

	private static class RomEntry {
		private String name;
		private String romCode;
		private int version, nonJapanese;
		private String extraTableFile;
		private boolean isCrystal;
		private int crcInHeader = -1;
		private Map<String, String> codeTweaks = new HashMap<String, String>();
		private List<TMTextEntry> tmTexts = new ArrayList<TMTextEntry>();
		private Map<String, Integer> entries = new HashMap<String, Integer>();
		private Map<String, int[]> arrayEntries = new HashMap<String, int[]>();
		private List<Integer> staticPokemonSingle = new ArrayList<Integer>();
		private Map<Integer, Integer> staticPokemonGameCorner = new TreeMap<Integer, Integer>();
		private Map<Integer, Integer> staticPokemonCopy = new TreeMap<Integer, Integer>();

		private int getValue(String key) {
			if (!entries.containsKey(key)) {
				entries.put(key, 0);
			}
			return entries.get(key);
		}
	}

	private static class TMTextEntry {
		private int number;
		private int offset;
		private String template;
	}

	private static List<RomEntry> roms;
	private static ItemList allowedItems;

	static {
		loadROMInfo();
		setupAllowedItems();
	}

	private static void loadROMInfo() {
		roms = new ArrayList<RomEntry>();
		RomEntry current = null;
		try {
			Scanner sc = new Scanner(
					FileFunctions.openConfig("gen2_offsets.ini"), "UTF-8");
			while (sc.hasNextLine()) {
				String q = sc.nextLine().trim();
				if (q.contains("//")) {
					q = q.substring(0, q.indexOf("//")).trim();
				}
				if (!q.isEmpty()) {
					if (q.startsWith("[") && q.endsWith("]")) {
						// New rom
						current = new RomEntry();
						current.name = q.substring(1, q.length() - 1);
						roms.add(current);
					} else {
						String[] r = q.split("=", 2);
						if (r.length == 1) {
							System.err.println("invalid entry " + q);
							continue;
						}
						if (r[1].endsWith("\r\n")) {
							r[1] = r[1].substring(0, r[1].length() - 2);
						}
						r[1] = r[1].trim();
						r[0] = r[0].trim();
						// Static Pokemon?
						if (r[0].equals("StaticPokemonGameCorner[]")) {
							String[] offsets = r[1].substring(1,
									r[1].length() - 1).split(",");
							if (offsets.length != 2) {
								continue;
							}
							int[] offs = new int[offsets.length];
							int c = 0;
							for (String off : offsets) {
								offs[c++] = parseRIInt(off);
							}
							current.staticPokemonGameCorner.put(offs[0],
									offs[1]);
						} else if (r[0].equals("StaticPokemonCopy[]")) {
							String[] offsets = r[1].substring(1,
									r[1].length() - 1).split(",");
							if (offsets.length != 2) {
								continue;
							}
							int[] offs = new int[offsets.length];
							int c = 0;
							for (String off : offsets) {
								offs[c++] = parseRIInt(off);
							}
							current.staticPokemonCopy.put(offs[0], offs[1]);
						} else if (r[0].equals("TMText[]")) {
							if (r[1].startsWith("[") && r[1].endsWith("]")) {
								String[] parts = r[1].substring(1,
										r[1].length() - 1).split(",", 3);
								TMTextEntry tte = new TMTextEntry();
								tte.number = parseRIInt(parts[0]);
								tte.offset = parseRIInt(parts[1]);
								tte.template = parts[2];
								current.tmTexts.add(tte);
							}
						} else if (r[0].equals("Game")) {
							current.romCode = r[1];
						} else if (r[0].equals("Version")) {
							current.version = parseRIInt(r[1]);
						} else if (r[0].equals("NonJapanese")) {
							current.nonJapanese = parseRIInt(r[1]);
						} else if (r[0].equals("Type")) {
							if (r[1].equalsIgnoreCase("Crystal")) {
								current.isCrystal = true;
							} else {
								current.isCrystal = false;
							}
						} else if (r[0].equals("ExtraTableFile")) {
							current.extraTableFile = r[1];
						} else if (r[0].equals("CRCInHeader")) {
							current.crcInHeader = parseRIInt(r[1]);
						} else if (r[0].endsWith("Tweak")) {
							current.codeTweaks.put(r[0], r[1]);
						} else if (r[0].equals("CopyFrom")) {
							for (RomEntry otherEntry : roms) {
								if (r[1].equalsIgnoreCase(otherEntry.name)) {
									// copy from here
									boolean cSP = (current
											.getValue("CopyStaticPokemon") == 1);
									boolean cTT = (current
											.getValue("CopyTMText") == 1);
									current.arrayEntries
											.putAll(otherEntry.arrayEntries);
									current.entries.putAll(otherEntry.entries);
									if (cSP) {
										current.staticPokemonSingle
												.addAll(otherEntry.staticPokemonSingle);
										current.staticPokemonGameCorner
												.putAll(otherEntry.staticPokemonGameCorner);
										current.staticPokemonCopy
												.putAll(otherEntry.staticPokemonCopy);
										current.entries.put(
												"StaticPokemonSupport", 1);
									} else {
										current.entries.put(
												"StaticPokemonSupport", 0);
									}
									if (cTT) {
										current.tmTexts
												.addAll(otherEntry.tmTexts);
									}
									current.extraTableFile = otherEntry.extraTableFile;
								}
							}
						} else {
							if (r[1].startsWith("[") && r[1].endsWith("]")) {
								String[] offsets = r[1].substring(1,
										r[1].length() - 1).split(",");
								if (offsets.length == 1
										&& offsets[0].trim().isEmpty()) {
									current.arrayEntries.put(r[0], new int[0]);
								} else {
									int[] offs = new int[offsets.length];
									int c = 0;
									for (String off : offsets) {
										offs[c++] = parseRIInt(off);
									}
									if (r[0].startsWith("StaticPokemon")) {
										for (int off : offs) {
											current.staticPokemonSingle
													.add(off);
										}
									} else {
										current.arrayEntries.put(r[0], offs);
									}
								}
							} else {
								int offs = parseRIInt(r[1]);
								current.entries.put(r[0], offs);
							}
						}
					}
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
		}

	}

	private static void setupAllowedItems() {
		allowedItems = new ItemList(249); // 250-255 are junk and cancel
		// Assorted key items
		allowedItems.banSingles(6, 54, 55, 58, 59, 61, 115, 116, 133, 134, 175,
				178);
		allowedItems.banRange(66, 6);
		allowedItems.banRange(127, 4);
		// HMs
		allowedItems.banRange(243, 7);
		// Unused items (Teru-Samas and dummy TMs)
		allowedItems.banSingles(7, 25, 45, 50, 56, 90, 100, 120, 135, 136, 137);
		allowedItems.banSingles(141, 142, 145, 147, 148, 149, 153, 154, 155,
				162, 171);
		allowedItems.banSingles(176, 179, 190, 220, 195);
		// Real TMs
		allowedItems.tmRange(191, 4);
		allowedItems.tmRange(196, 24);
		allowedItems.tmRange(221, 22);
	}

	private static int parseRIInt(String off) {
		int radix = 10;
		off = off.trim().toLowerCase();
		if (off.startsWith("0x") || off.startsWith("&h")) {
			radix = 16;
			off = off.substring(2);
		}
		try {
			return Integer.parseInt(off, radix);
		} catch (NumberFormatException ex) {
			System.err.println("invalid base " + radix + "number " + off);
			return 0;
		}
	}

	// This ROM's data
	private Pokemon[] pokes;
	private List<Pokemon> pokemonList;
	private RomEntry romEntry;
	private Move[] moves;
	private String[] tb;
	private Map<String, Byte> d;
	private int longestTableToken;
	private boolean havePatchedFleeing;
	private String[] itemNames;
	private List<Integer> itemOffs;
	private String[][] mapNames;
	private String[] landmarkNames;
	private boolean isVietCrystal;

	@Override
	public boolean detectRom(byte[] rom) {
		if (rom.length > 2097152) {
			return false; // size check
		}
		return checkRomEntry(rom) != null; // so it's OK if it's a valid ROM
	}

	@Override
	public void loadedRom() {
		romEntry = checkRomEntry(this.rom);
		tb = new String[256];
		d = new HashMap<String, Byte>();
		clearTextTables();
		readTextTable("gameboy_jap");
		if (romEntry.extraTableFile != null
				&& romEntry.extraTableFile.equalsIgnoreCase("none") == false) {
			readTextTable(romEntry.extraTableFile);
		}
		// VietCrystal override
		if (romEntry.name.equals("Crystal (J)") && (rom[0x63] & 0xFF) == 0xF5) {
			readTextTable("vietcrystal");
			isVietCrystal = true;
		} else {
			isVietCrystal = false;
		}
		havePatchedFleeing = false;
		loadPokemonStats();
		pokemonList = Arrays.asList(pokes);
		loadMoves();
		loadLandmarkNames();
		preprocessMaps();
		loadItemNames();
	}

	private RomEntry checkRomEntry(byte[] rom) {
		int version = rom[0x14C] & 0xFF;
		int nonjap = rom[0x14A] & 0xFF;
		// Check for specific CRC first
		int crcInHeader = ((rom[0x14E] & 0xFF) << 8) | (rom[0x14F] & 0xFF);
		for (RomEntry re : roms) {
			if (romSig(rom, re.romCode) && re.version == version
					&& re.nonJapanese == nonjap
					&& re.crcInHeader == crcInHeader) {
				return re;
			}
		}
		// Now check for non-specific-CRC entries
		for (RomEntry re : roms) {
			if (romSig(rom, re.romCode) && re.version == version
					&& re.nonJapanese == nonjap && re.crcInHeader == -1) {
				return re;
			}
		}
		// Not found
		return null;
	}

	private void clearTextTables() {
		tb = new String[256];
		d.clear();
		longestTableToken = 0;
	}

	private void readTextTable(String name) {
		try {
			Scanner sc = new Scanner(FileFunctions.openConfig(name + ".tbl"),
					"UTF-8");
			while (sc.hasNextLine()) {
				String q = sc.nextLine();
				if (!q.trim().isEmpty()) {
					String[] r = q.split("=", 2);
					if (r[1].endsWith("\r\n")) {
						r[1] = r[1].substring(0, r[1].length() - 2);
					}
					int hexcode = Integer.parseInt(r[0], 16);
					if (tb[hexcode] != null) {
						String oldMatch = tb[hexcode];
						tb[hexcode] = null;
						d.remove(oldMatch);
					}
					tb[hexcode] = r[1];
					longestTableToken = Math.max(longestTableToken,
							r[1].length());
					d.put(r[1], (byte) hexcode);
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
		}

	}

	@Override
	public void savingRom() {
		savePokemonStats();
		saveMoves();
	}

	private void loadPokemonStats() {
		pokes = new Pokemon[252];
		// Fetch our names
		String[] pokeNames = readPokemonNames();
		int offs = romEntry.getValue("PokemonStatsOffset");
		// Get base stats
		for (int i = 1; i <= 251; i++) {
			pokes[i] = new Pokemon();
			pokes[i].number = i;
			loadBasicPokeStats(pokes[i], offs + (i - 1) * 0x20);
			// Name?
			pokes[i].name = pokeNames[i];
		}

	}

	private void savePokemonStats() {
		// Write pokemon names
		int offs = romEntry.getValue("PokemonNamesOffset");
		int len = romEntry.getValue("PokemonNamesLength");
		for (int i = 1; i <= 251; i++) {
			int stringOffset = offs + (i - 1) * len;
			writeFixedLengthString(pokes[i].name, stringOffset, len);
		}
		// Write pokemon stats
		int offs2 = romEntry.getValue("PokemonStatsOffset");
		for (int i = 1; i <= 251; i++) {
			saveBasicPokeStats(pokes[i], offs2 + (i - 1) * 0x20);
		}
	}

	private String[] readMoveNames() {
		int offset = romEntry.getValue("MoveNamesOffset");
		String[] moveNames = new String[252];
		for (int i = 1; i <= 251; i++) {
			moveNames[i] = readVariableLengthString(offset);
			offset += lengthOfStringAt(offset) + 1;
		}
		return moveNames;
	}

	private void loadMoves() {
		moves = new Move[252];
		String[] moveNames = readMoveNames();
		int offs = romEntry.getValue("MoveDataOffset");
		for (int i = 1; i <= 251; i++) {
			moves[i] = new Move();
			moves[i].name = moveNames[i];
			moves[i].number = i;
			moves[i].effectIndex = rom[offs + (i - 1) * 7] & 0xFF;
			moves[i].hitratio = ((rom[offs + (i - 1) * 7 + 3] & 0xFF) + 0) / 255.0 * 100;
			moves[i].power = rom[offs + (i - 1) * 7 + 1] & 0xFF;
			moves[i].pp = rom[offs + (i - 1) * 7 + 4] & 0xFF;
			moves[i].type = typeTable[rom[offs + (i - 1) * 7 + 2]];
		}

	}

	private void saveMoves() {
		int offs = romEntry.getValue("MoveDataOffset");
		for (int i = 1; i <= 251; i++) {
			rom[offs + (i - 1) * 7] = (byte) moves[i].effectIndex;
			rom[offs + (i - 1) * 7 + 1] = (byte) moves[i].power;
			rom[offs + (i - 1) * 7 + 2] = typeToByte(moves[i].type);
			int hitratio = (int) Math.round(moves[i].hitratio * 2.55);
			if (hitratio < 0) {
				hitratio = 0;
			}
			if (hitratio > 255) {
				hitratio = 255;
			}
			rom[offs + (i - 1) * 7 + 3] = (byte) hitratio;
			rom[offs + (i - 1) * 7 + 4] = (byte) moves[i].pp;
		}
	}

	public List<Move> getMoves() {
		return Arrays.asList(moves);
	}

	private void loadBasicPokeStats(Pokemon pkmn, int offset) {
		pkmn.hp = rom[offset + 1] & 0xFF;
		pkmn.attack = rom[offset + 2] & 0xFF;
		pkmn.defense = rom[offset + 3] & 0xFF;
		pkmn.speed = rom[offset + 4] & 0xFF;
		pkmn.spatk = rom[offset + 5] & 0xFF;
		pkmn.spdef = rom[offset + 6] & 0xFF;
		// Type
		pkmn.primaryType = typeTable[rom[offset + 7] & 0xFF];
		pkmn.secondaryType = typeTable[rom[offset + 8] & 0xFF];
		// Only one type?
		if (pkmn.secondaryType == pkmn.primaryType) {
			pkmn.secondaryType = null;
		}
		pkmn.catchRate = rom[offset + 9] & 0xFF;
		pkmn.guaranteedHeldItem = -1;
		pkmn.commonHeldItem = rom[offset + 11] & 0xFF;
		pkmn.rareHeldItem = rom[offset + 12] & 0xFF;
		pkmn.darkGrassHeldItem = -1;
		pkmn.growthCurve = ExpCurve.fromByte(rom[offset + 22]);

	}

	private void saveBasicPokeStats(Pokemon pkmn, int offset) {
		rom[offset + 1] = (byte) pkmn.hp;
		rom[offset + 2] = (byte) pkmn.attack;
		rom[offset + 3] = (byte) pkmn.defense;
		rom[offset + 4] = (byte) pkmn.speed;
		rom[offset + 5] = (byte) pkmn.spatk;
		rom[offset + 6] = (byte) pkmn.spdef;
		rom[offset + 7] = typeToByte(pkmn.primaryType);
		if (pkmn.secondaryType == null) {
			rom[offset + 8] = rom[offset + 7];
		} else {
			rom[offset + 8] = typeToByte(pkmn.secondaryType);
		}
		rom[offset + 9] = (byte) pkmn.catchRate;

		rom[offset + 11] = (byte) pkmn.commonHeldItem;
		rom[offset + 12] = (byte) pkmn.rareHeldItem;
		rom[offset + 22] = pkmn.growthCurve.toByte();
	}

	private String[] readPokemonNames() {
		int offs = romEntry.getValue("PokemonNamesOffset");
		int len = romEntry.getValue("PokemonNamesLength");
		String[] names = new String[252];
		for (int i = 1; i <= 251; i++) {
			names[i] = readFixedLengthString(offs + (i - 1) * len, len);
		}
		return names;
	}

	private String readString(int offset, int maxLength) {
		StringBuilder string = new StringBuilder();
		for (int c = 0; c < maxLength; c++) {
			int currChar = rom[offset + c] & 0xFF;
			if (tb[currChar] != null) {
				string.append(tb[currChar]);
			} else {
				if (currChar == 0x50 || currChar == 0x00) {
					break;
				} else {
					string.append("\\x" + String.format("%02X", currChar));
				}
			}
		}
		return string.toString();
	}

	public byte[] translateString(String text) {
		List<Byte> data = new ArrayList<Byte>();
		while (text.length() != 0) {
			int i = Math.max(0, longestTableToken - text.length());
			if (text.charAt(0) == '\\' && text.charAt(1) == 'x') {
				data.add((byte) Integer.parseInt(text.substring(2, 4), 16));
				text = text.substring(4);
			} else {
				while (!(d
						.containsKey(text.substring(0, longestTableToken - i)) || (i == longestTableToken))) {
					i++;
				}
				if (i == longestTableToken) {
					text = text.substring(1);
				} else {
					data.add(d.get(text.substring(0, longestTableToken - i)));
					text = text.substring(longestTableToken - i);
				}
			}
		}
		byte[] ret = new byte[data.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = data.get(i);
		}
		return ret;
	}

	private String readFixedLengthString(int offset, int length) {
		return readString(offset, length);
	}

	public String readVariableLengthString(int offset) {
		return readString(offset, Integer.MAX_VALUE);
	}

	public String readVariableLengthScriptString(int offset) {
		return readString(offset, Integer.MAX_VALUE);
	}

	public byte[] traduire(String str) {
		return translateString(str);
	}

	private void writeFixedLengthString(String str, int offset, int length) {
		byte[] translated = translateString(str);
		int len = Math.min(translated.length, length);
		System.arraycopy(translated, 0, rom, offset, len);
		while (len < length) {
			rom[offset + len] = 0x50;
			len++;
		}
	}

	private void writeFixedLengthScriptString(String str, int offset, int length) {
		byte[] translated = translateString(str);
		int len = Math.min(translated.length, length);
		System.arraycopy(translated, 0, rom, offset, len);
		while (len < length) {
			rom[offset + len] = 0x00;
			len++;
		}
	}

	private int lengthOfStringAt(int offset) {
		int len = 0;
		while (rom[offset + len] != 0x50 && rom[offset + len] != 0x00) {
			len++;
		}
		return len;
	}

	private int makeGBPointer(int offset) {
		if (offset < 0x4000) {
			return offset;
		} else {
			return (offset % 0x4000) + 0x4000;
		}
	}

	private int bankOf(int offset) {
		return (offset / 0x4000);
	}

	private int calculateOffset(int bank, int pointer) {
		if (pointer < 0x4000) {
			return pointer;
		} else {
			return (pointer % 0x4000) + bank * 0x4000;
		}
	}

	private boolean romSig(byte[] rom, String sig) {
		try {
			int sigOffset = 0x13F;
			byte[] sigBytes = sig.getBytes("US-ASCII");
			for (int i = 0; i < sigBytes.length; i++) {
				if (rom[sigOffset + i] != sigBytes[i]) {
					return false;
				}
			}
			return true;
		} catch (UnsupportedEncodingException ex) {
			return false;
		}

	}

	@Override
	public boolean isInGame(Pokemon pkmn) {
		return (pkmn.number >= 1 && pkmn.number <= 251);
	}

	@Override
	public boolean isInGame(int pokemonNumber) {
		return (pokemonNumber >= 1 && pokemonNumber <= 251);
	}

	@Override
	public List<Pokemon> getStarters() {
		// Get the starters
		List<Pokemon> starters = new ArrayList<Pokemon>();
		starters.add(pokes[rom[romEntry.arrayEntries.get("StarterOffsets1")[0]] & 0xFF]);
		starters.add(pokes[rom[romEntry.arrayEntries.get("StarterOffsets2")[0]] & 0xFF]);
		starters.add(pokes[rom[romEntry.arrayEntries.get("StarterOffsets3")[0]] & 0xFF]);
		return starters;
	}

	@Override
	public boolean setStarters(List<Pokemon> newStarters) {
		if (newStarters.size() != 3) {
			return false;
		}
		for (Pokemon pkmn : newStarters) {
			if (!isInGame(pkmn)) {
				return false;
			}
		}

		// Actually write

		for (int i = 0; i < 3; i++) {
			byte starter = (byte) newStarters.get(i).number;
			int[] offsets = romEntry.arrayEntries.get("StarterOffsets"
					+ (i + 1));
			for (int offset : offsets) {
				rom[offset] = starter;
			}
		}

		// Attempt to replace text
		if (romEntry.getValue("CanChangeStarterText") > 0) {
			List<Integer> cyndaTexts = RomFunctions.search(rom,
					traduire("CYNDAQUIL"));
			int offset = cyndaTexts.get(romEntry.isCrystal ? 1 : 0);
			String pokeName = newStarters.get(0).name;
			writeFixedLengthScriptString(pokeName + "?\\e", offset,
					lengthOfStringAt(offset) + 1);

			List<Integer> totoTexts = RomFunctions.search(rom,
					traduire("TOTODILE"));
			offset = totoTexts.get(romEntry.isCrystal ? 1 : 0);
			pokeName = newStarters.get(1).name;
			writeFixedLengthScriptString(pokeName + "?\\e", offset,
					lengthOfStringAt(offset) + 1);

			List<Integer> chikoTexts = RomFunctions.search(rom,
					traduire("CHIKORITA"));
			offset = chikoTexts.get(romEntry.isCrystal ? 1 : 0);
			pokeName = newStarters.get(2).name;
			writeFixedLengthScriptString(pokeName + "?\\e", offset,
					lengthOfStringAt(offset) + 1);
		}
		return true;
	}

	@Override
	public List<Integer> getStarterHeldItems() {
		List<Integer> sHeldItems = new ArrayList<Integer>();
		int[] shiOffsets = romEntry.arrayEntries.get("StarterHeldItems");
		for (int offset : shiOffsets) {
			sHeldItems.add(rom[offset] & 0xFF);
		}
		return sHeldItems;
	}

	@Override
	public void setStarterHeldItems(List<Integer> items) {
		int[] shiOffsets = romEntry.arrayEntries.get("StarterHeldItems");
		if (items.size() != shiOffsets.length) {
			return;
		}
		Iterator<Integer> sHeldItems = items.iterator();
		for (int offset : shiOffsets) {
			rom[offset] = sHeldItems.next().byteValue();
		}
	}

	@Override
	public void shufflePokemonStats() {
		for (int i = 1; i <= 251; i++) {
			pokes[i].shuffleStats();
		}
	}

	@Override
	public List<EncounterSet> getEncounters(boolean useTimeOfDay) {
		int offset = romEntry.getValue("WildPokemonOffset");
		List<EncounterSet> areas = new ArrayList<EncounterSet>();
		offset = readLandEncounters(offset, areas, useTimeOfDay); // Johto
		offset = readSeaEncounters(offset, areas); // Johto
		offset = readLandEncounters(offset, areas, useTimeOfDay); // Kanto
		offset = readSeaEncounters(offset, areas); // Kanto
		offset = readLandEncounters(offset, areas, useTimeOfDay); // Specials
		offset = readSeaEncounters(offset, areas); // Specials

		// Fishing Data
		offset = romEntry.getValue("FishingWildsOffset");
		int rootOffset = offset;
		for (int k = 0; k < 12; k++) {
			EncounterSet es = new EncounterSet();
			es.displayName = "Fishing Group " + (k + 1);
			for (int i = 0; i < 11; i++) {
				offset++;
				int pokeNum = rom[offset++] & 0xFF;
				int level = rom[offset++] & 0xFF;
				if (pokeNum == 0) {
					if (!useTimeOfDay) {
						// read the encounter they put here for DAY
						int specialOffset = rootOffset + 33 * 12 + level * 4
								+ 2;
						Encounter enc = new Encounter();
						enc.pokemon = pokes[rom[specialOffset] & 0xFF];
						enc.level = rom[specialOffset + 1] & 0xFF;
						es.encounters.add(enc);
					}
					// else will be handled by code below
				} else {
					Encounter enc = new Encounter();
					enc.pokemon = pokes[pokeNum];
					enc.level = level;
					es.encounters.add(enc);
				}
			}
			areas.add(es);
		}
		if (useTimeOfDay) {
			for (int k = 0; k < 11; k++) {
				EncounterSet es = new EncounterSet();
				es.displayName = "Time-Specific Fishing " + (k + 1);
				for (int i = 0; i < 4; i++) {
					int pokeNum = rom[offset++] & 0xFF;
					int level = rom[offset++] & 0xFF;
					Encounter enc = new Encounter();
					enc.pokemon = pokes[pokeNum];
					enc.level = level;
					es.encounters.add(enc);
				}
				areas.add(es);
			}
		}

		// Headbutt Data
		offset = romEntry.getValue("HeadbuttWildsOffset");
		int limit = romEntry.getValue("HeadbuttTableSize");
		for (int i = 0; i < limit; i++) {
			EncounterSet es = new EncounterSet();
			es.displayName = "Headbutt Trees Set " + (i + 1);
			while ((rom[offset] & 0xFF) != 0xFF) {
				offset++;
				int pokeNum = rom[offset++] & 0xFF;
				int level = rom[offset++] & 0xFF;
				Encounter enc = new Encounter();
				enc.pokemon = pokes[pokeNum];
				enc.level = level;
				es.encounters.add(enc);
			}
			offset++;
			areas.add(es);
		}

		// Bug Catching Contest Data
		offset = romEntry.getValue("BCCWildsOffset");
		EncounterSet bccES = new EncounterSet();
		bccES.displayName = "Bug Catching Contest";
		while ((rom[offset] & 0xFF) != 0xFF) {
			offset++;
			Encounter enc = new Encounter();
			enc.pokemon = pokes[rom[offset++] & 0xFF];
			enc.level = rom[offset++] & 0xFF;
			enc.maxLevel = rom[offset++] & 0xFF;
			bccES.encounters.add(enc);
		}
		areas.add(bccES);

		return areas;
	}

	private int readLandEncounters(int offset, List<EncounterSet> areas,
			boolean useTimeOfDay) {
		String[] todNames = new String[] { "Morning", "Day", "Night" };
		while ((rom[offset] & 0xFF) != 0xFF) {
			int mapBank = rom[offset] & 0xFF;
			int mapNumber = rom[offset + 1] & 0xFF;
			String mapName = mapNames[mapBank][mapNumber];
			if (useTimeOfDay) {
				for (int i = 0; i < 3; i++) {
					EncounterSet encset = new EncounterSet();
					encset.rate = rom[offset + 2 + i] & 0xFF;
					encset.displayName = mapName + " Grass/Cave ("
							+ todNames[i] + ")";
					for (int j = 0; j < 7; j++) {
						Encounter enc = new Encounter();
						enc.level = rom[offset + 5 + (i * 14) + (j * 2)] & 0xFF;
						enc.maxLevel = 0;
						enc.pokemon = pokes[rom[offset + 5 + (i * 14) + (j * 2)
								+ 1] & 0xFF];
						encset.encounters.add(enc);
					}
					areas.add(encset);
				}
			} else {
				// Use Day only
				EncounterSet encset = new EncounterSet();
				encset.rate = rom[offset + 3] & 0xFF;
				encset.displayName = mapName + " Grass/Cave";
				for (int j = 0; j < 7; j++) {
					Encounter enc = new Encounter();
					enc.level = rom[offset + 5 + 14 + (j * 2)] & 0xFF;
					enc.maxLevel = 0;
					enc.pokemon = pokes[rom[offset + 5 + 14 + (j * 2) + 1] & 0xFF];
					encset.encounters.add(enc);
				}
				areas.add(encset);
			}
			offset += 47;
		}
		return offset + 1;
	}

	private int readSeaEncounters(int offset, List<EncounterSet> areas) {
		while ((rom[offset] & 0xFF) != 0xFF) {
			int mapBank = rom[offset] & 0xFF;
			int mapNumber = rom[offset + 1] & 0xFF;
			String mapName = mapNames[mapBank][mapNumber];
			EncounterSet encset = new EncounterSet();
			encset.rate = rom[offset + 2] & 0xFF;
			encset.displayName = mapName + " Surfing";
			for (int j = 0; j < 3; j++) {
				Encounter enc = new Encounter();
				enc.level = rom[offset + 3 + (j * 2)] & 0xFF;
				enc.maxLevel = 0;
				enc.pokemon = pokes[rom[offset + 3 + (j * 2) + 1] & 0xFF];
				encset.encounters.add(enc);
			}
			areas.add(encset);
			offset += 9;
		}
		return offset + 1;
	}

	@Override
	public void setEncounters(boolean useTimeOfDay,
			List<EncounterSet> encounters) {
		if (!havePatchedFleeing) {
			patchFleeing();
		}
		int offset = romEntry.getValue("WildPokemonOffset");
		Iterator<EncounterSet> areas = encounters.iterator();
		offset = writeLandEncounters(offset, areas, useTimeOfDay); // Johto
		offset = writeSeaEncounters(offset, areas); // Johto
		offset = writeLandEncounters(offset, areas, useTimeOfDay); // Kanto
		offset = writeSeaEncounters(offset, areas); // Kanto
		offset = writeLandEncounters(offset, areas, useTimeOfDay); // Specials
		offset = writeSeaEncounters(offset, areas); // Specials

		// Fishing Data
		offset = romEntry.getValue("FishingWildsOffset");
		for (int k = 0; k < 12; k++) {
			EncounterSet es = areas.next();
			Iterator<Encounter> encs = es.encounters.iterator();
			for (int i = 0; i < 11; i++) {
				offset++;
				if (rom[offset] == 0) {
					if (!useTimeOfDay) {
						// overwrite with a static encounter
						Encounter enc = encs.next();
						rom[offset++] = (byte) enc.pokemon.number;
						rom[offset++] = (byte) enc.level;
					} else {
						// else handle below
						offset += 2;
					}
				} else {
					Encounter enc = encs.next();
					rom[offset++] = (byte) enc.pokemon.number;
					rom[offset++] = (byte) enc.level;
				}
			}
		}
		if (useTimeOfDay) {
			for (int k = 0; k < 11; k++) {
				EncounterSet es = areas.next();
				Iterator<Encounter> encs = es.encounters.iterator();
				for (int i = 0; i < 4; i++) {
					Encounter enc = encs.next();
					rom[offset++] = (byte) enc.pokemon.number;
					rom[offset++] = (byte) enc.level;
				}
			}
		}

		// Headbutt Data
		offset = romEntry.getValue("HeadbuttWildsOffset");
		int limit = romEntry.getValue("HeadbuttTableSize");
		for (int i = 0; i < limit; i++) {
			EncounterSet es = areas.next();
			Iterator<Encounter> encs = es.encounters.iterator();
			while ((rom[offset] & 0xFF) != 0xFF) {
				Encounter enc = encs.next();
				offset++;
				rom[offset++] = (byte) enc.pokemon.number;
				rom[offset++] = (byte) enc.level;
			}
			offset++;
		}

		// Bug Catching Contest Data
		offset = romEntry.getValue("BCCWildsOffset");
		EncounterSet bccES = areas.next();
		Iterator<Encounter> bccEncs = bccES.encounters.iterator();
		while ((rom[offset] & 0xFF) != 0xFF) {
			offset++;
			Encounter enc = bccEncs.next();
			rom[offset++] = (byte) enc.pokemon.number;
			rom[offset++] = (byte) enc.level;
			rom[offset++] = (byte) enc.maxLevel;
		}

	}

	private int writeLandEncounters(int offset, Iterator<EncounterSet> areas,
			boolean useTimeOfDay) {
		while ((rom[offset] & 0xFF) != 0xFF) {
			if (useTimeOfDay) {
				for (int i = 0; i < 3; i++) {
					EncounterSet encset = areas.next();
					Iterator<Encounter> encountersHere = encset.encounters
							.iterator();
					for (int j = 0; j < 7; j++) {
						rom[offset + 5 + (i * 14) + (j * 2) + 1] = (byte) encountersHere
								.next().pokemon.number;
					}
				}
			} else {
				// Write the set to all 3 equally
				EncounterSet encset = areas.next();
				for (int i = 0; i < 3; i++) {
					Iterator<Encounter> encountersHere = encset.encounters
							.iterator();
					for (int j = 0; j < 7; j++) {
						rom[offset + 5 + (i * 14) + (j * 2) + 1] = (byte) encountersHere
								.next().pokemon.number;
					}
				}
			}
			offset += 47;
		}
		return offset + 1;
	}

	private int writeSeaEncounters(int offset, Iterator<EncounterSet> areas) {
		while ((rom[offset] & 0xFF) != 0xFF) {
			EncounterSet encset = areas.next();
			Iterator<Encounter> encountersHere = encset.encounters.iterator();
			for (int j = 0; j < 3; j++) {
				rom[offset + 3 + (j * 2) + 1] = (byte) encountersHere.next().pokemon.number;
			}
			offset += 9;
		}
		return offset + 1;
	}

	@Override
	public List<Trainer> getTrainers() {
		int traineroffset = romEntry.getValue("TrainerDataTableOffset");
		int traineramount = romEntry.getValue("TrainerClassAmount");
		int[] trainerclasslimits = romEntry.arrayEntries
				.get("TrainerDataClassCounts");

		int[] pointers = new int[traineramount + 1];
		for (int i = 1; i <= traineramount; i++) {
			int pointer = readWord(traineroffset + (i - 1) * 2);
			pointers[i] = calculateOffset(bankOf(traineroffset), pointer);
		}

		List<String> tcnames = this.getTrainerClassNames();

		List<Trainer> allTrainers = new ArrayList<Trainer>();
		for (int i = 1; i <= traineramount; i++) {
			int offs = pointers[i];
			int limit = trainerclasslimits[i];
			for (int trnum = 0; trnum < limit; trnum++) {
				Trainer tr = new Trainer();
				tr.offset = offs;
				tr.trainerclass = i;
				String name = readVariableLengthString(offs);
				tr.name = name;
				tr.fullDisplayName = tcnames.get(i - 1) + " " + name;
				int len = lengthOfStringAt(offs);
				offs += len + 1;
				int dataType = rom[offs] & 0xFF;
				tr.poketype = dataType;
				offs++;
				while ((rom[offs] & 0xFF) != 0xFF) {
					TrainerPokemon tp = new TrainerPokemon();
					tp.level = rom[offs] & 0xFF;
					tp.pokemon = pokes[rom[offs + 1] & 0xFF];
					offs += 2;
					if (dataType == 2 || dataType == 3) {
						tp.heldItem = rom[offs] & 0xFF;
						offs++;
					}
					if (dataType % 2 == 1) {
						tp.move1 = rom[offs] & 0xFF;
						tp.move2 = rom[offs + 1] & 0xFF;
						tp.move3 = rom[offs + 2] & 0xFF;
						tp.move4 = rom[offs + 3] & 0xFF;
						offs += 4;
					}
					tr.pokemon.add(tp);
				}
				allTrainers.add(tr);
				offs++;
			}
		}
		universalTrainerTags(allTrainers);
		if (romEntry.isCrystal) {
			crystalTags(allTrainers);
		} else {
			goldSilverTags(allTrainers);
		}
		return allTrainers;
	}

	@Override
	public void setTrainers(List<Trainer> trainerData) {
		int traineroffset = romEntry.getValue("TrainerDataTableOffset");
		int traineramount = romEntry.getValue("TrainerClassAmount");
		int[] trainerclasslimits = romEntry.arrayEntries
				.get("TrainerDataClassCounts");

		int[] pointers = new int[traineramount + 1];
		for (int i = 1; i <= traineramount; i++) {
			int pointer = readWord(traineroffset + (i - 1) * 2);
			pointers[i] = calculateOffset(bankOf(traineroffset), pointer);
		}

		Iterator<Trainer> allTrainers = trainerData.iterator();
		for (int i = 1; i <= traineramount; i++) {
			int offs = pointers[i];
			int limit = trainerclasslimits[i];
			for (int trnum = 0; trnum < limit; trnum++) {
				Trainer tr = allTrainers.next();
				if (tr.trainerclass != i) {
					System.err.println("Trainer mismatch: " + tr.name);
				}
				// Write their name
				int trnamelen = internalStringLength(tr.name);
				writeFixedLengthString(tr.name, offs, trnamelen + 1);
				offs += trnamelen + 1;
				// Poketype
				tr.poketype = 0; // remove held items and moves
				rom[offs++] = (byte) tr.poketype;
				Iterator<TrainerPokemon> tPokes = tr.pokemon.iterator();
				for (int tpnum = 0; tpnum < tr.pokemon.size(); tpnum++) {
					TrainerPokemon tp = tPokes.next();
					rom[offs] = (byte) tp.level;
					rom[offs + 1] = (byte) tp.pokemon.number;
					offs += 2;
					if (tr.poketype == 2 || tr.poketype == 3) {
						rom[offs] = (byte) tp.heldItem;
						offs++;
					}
					if (tr.poketype % 2 == 1) {
						rom[offs] = (byte) tp.move1;
						rom[offs + 1] = (byte) tp.move2;
						rom[offs + 2] = (byte) tp.move3;
						rom[offs + 3] = (byte) tp.move4;
						offs += 4;
					}
				}
				rom[offs] = (byte) 0xFF;
				offs++;
			}
		}

	}

	private void universalTrainerTags(List<Trainer> allTrainers) {
		// Gym Leaders
		tbc(allTrainers, 1, 0, "GYM1");
		tbc(allTrainers, 3, 0, "GYM2");
		tbc(allTrainers, 2, 0, "GYM3");
		tbc(allTrainers, 4, 0, "GYM4");
		tbc(allTrainers, 7, 0, "GYM5");
		tbc(allTrainers, 6, 0, "GYM6");
		tbc(allTrainers, 5, 0, "GYM7");
		tbc(allTrainers, 8, 0, "GYM8");
		tbc(allTrainers, 17, 0, "GYM9");
		tbc(allTrainers, 18, 0, "GYM10");
		tbc(allTrainers, 19, 0, "GYM11");
		tbc(allTrainers, 21, 0, "GYM12");
		tbc(allTrainers, 26, 0, "GYM13");
		tbc(allTrainers, 35, 0, "GYM14");
		tbc(allTrainers, 46, 0, "GYM15");
		tbc(allTrainers, 64, 0, "GYM16");

		// Elite 4 & Red
		tbc(allTrainers, 11, 0, "ELITE1");
		tbc(allTrainers, 15, 0, "ELITE2");
		tbc(allTrainers, 13, 0, "ELITE3");
		tbc(allTrainers, 14, 0, "ELITE4");
		tbc(allTrainers, 16, 0, "CHAMPION");
		tbc(allTrainers, 63, 0, "UBER");

		// Silver
		// Order in rom is BAYLEEF, QUILAVA, CROCONAW teams
		// Starters go CYNDA, TOTO, CHIKO
		// So we want 0=CROCONAW/FERALI, 1=BAYLEEF/MEGAN, 2=QUILAVA/TYPHLO
		tbc(allTrainers, 9, 0, "RIVAL1-1");
		tbc(allTrainers, 9, 1, "RIVAL1-2");
		tbc(allTrainers, 9, 2, "RIVAL1-0");

		tbc(allTrainers, 9, 3, "RIVAL2-1");
		tbc(allTrainers, 9, 4, "RIVAL2-2");
		tbc(allTrainers, 9, 5, "RIVAL2-0");

		tbc(allTrainers, 9, 6, "RIVAL3-1");
		tbc(allTrainers, 9, 7, "RIVAL3-2");
		tbc(allTrainers, 9, 8, "RIVAL3-0");

		tbc(allTrainers, 9, 9, "RIVAL4-1");
		tbc(allTrainers, 9, 10, "RIVAL4-2");
		tbc(allTrainers, 9, 11, "RIVAL4-0");

		tbc(allTrainers, 9, 12, "RIVAL5-1");
		tbc(allTrainers, 9, 13, "RIVAL5-2");
		tbc(allTrainers, 9, 14, "RIVAL5-0");

		tbc(allTrainers, 42, 0, "RIVAL6-1");
		tbc(allTrainers, 42, 1, "RIVAL6-2");
		tbc(allTrainers, 42, 2, "RIVAL6-0");

		tbc(allTrainers, 42, 3, "RIVAL7-1");
		tbc(allTrainers, 42, 4, "RIVAL7-2");
		tbc(allTrainers, 42, 5, "RIVAL7-0");

		// Female Rocket Executive (Ariana)
		tbc(allTrainers, 55, 0, "THEMED:ARIANA");
		tbc(allTrainers, 55, 1, "THEMED:ARIANA");

		// others (unlabeled in this game, using HGSS names)
		tbc(allTrainers, 51, 2, "THEMED:PETREL");
		tbc(allTrainers, 51, 3, "THEMED:PETREL");

		tbc(allTrainers, 51, 1, "THEMED:PROTON");
		tbc(allTrainers, 31, 0, "THEMED:PROTON");

		// Sprout Tower
		tbc(allTrainers, 56, 0, "THEMED:SPROUTTOWER");
		tbc(allTrainers, 56, 1, "THEMED:SPROUTTOWER");
		tbc(allTrainers, 56, 2, "THEMED:SPROUTTOWER");
		tbc(allTrainers, 56, 3, "THEMED:SPROUTTOWER");
		tbc(allTrainers, 56, 6, "THEMED:SPROUTTOWER");
		tbc(allTrainers, 56, 7, "THEMED:SPROUTTOWER");
		tbc(allTrainers, 56, 8, "THEMED:SPROUTTOWER");
	}

	private void goldSilverTags(List<Trainer> allTrainers) {
		tbc(allTrainers, 24, 0, "GYM1");
		tbc(allTrainers, 24, 1, "GYM1");
		tbc(allTrainers, 36, 4, "GYM2");
		tbc(allTrainers, 36, 5, "GYM2");
		tbc(allTrainers, 36, 6, "GYM2");
		tbc(allTrainers, 61, 0, "GYM2");
		tbc(allTrainers, 61, 3, "GYM2");
		tbc(allTrainers, 25, 0, "GYM3");
		tbc(allTrainers, 25, 1, "GYM3");
		tbc(allTrainers, 29, 0, "GYM3");
		tbc(allTrainers, 29, 1, "GYM3");
		tbc(allTrainers, 56, 4, "GYM4");
		tbc(allTrainers, 56, 5, "GYM4");
		tbc(allTrainers, 57, 0, "GYM4");
		tbc(allTrainers, 57, 1, "GYM4");
		tbc(allTrainers, 50, 1, "GYM5");
		tbc(allTrainers, 50, 3, "GYM5");
		tbc(allTrainers, 50, 4, "GYM5");
		tbc(allTrainers, 50, 6, "GYM5");
		tbc(allTrainers, 58, 0, "GYM7");
		tbc(allTrainers, 58, 1, "GYM7");
		tbc(allTrainers, 58, 2, "GYM7");
		tbc(allTrainers, 33, 0, "GYM7");
		tbc(allTrainers, 33, 1, "GYM7");
		tbc(allTrainers, 27, 2, "GYM8");
		tbc(allTrainers, 27, 4, "GYM8");
		tbc(allTrainers, 27, 3, "GYM8");
		tbc(allTrainers, 28, 2, "GYM8");
		tbc(allTrainers, 28, 3, "GYM8");
		tbc(allTrainers, 54, 17, "GYM9");
		tbc(allTrainers, 38, 20, "GYM10");
		tbc(allTrainers, 39, 17, "GYM10");
		tbc(allTrainers, 39, 18, "GYM10");
		tbc(allTrainers, 49, 2, "GYM11");
		tbc(allTrainers, 43, 1, "GYM11");
		tbc(allTrainers, 32, 2, "GYM11");
		tbc(allTrainers, 61, 4, "GYM12");
		tbc(allTrainers, 61, 5, "GYM12");
		tbc(allTrainers, 25, 8, "GYM12");
		tbc(allTrainers, 53, 18, "GYM12");
		tbc(allTrainers, 29, 13, "GYM12");
		tbc(allTrainers, 25, 2, "GYM13");
		tbc(allTrainers, 25, 5, "GYM13");
		tbc(allTrainers, 53, 4, "GYM13");
		tbc(allTrainers, 54, 4, "GYM13");
		tbc(allTrainers, 57, 5, "GYM14");
		tbc(allTrainers, 57, 6, "GYM14");
		tbc(allTrainers, 52, 1, "GYM14");
		tbc(allTrainers, 52, 10, "GYM14");
	}

	private void crystalTags(List<Trainer> allTrainers) {
		tbc(allTrainers, 24, 0, "GYM1");
		tbc(allTrainers, 24, 1, "GYM1");
		tbc(allTrainers, 36, 4, "GYM2");
		tbc(allTrainers, 36, 5, "GYM2");
		tbc(allTrainers, 36, 6, "GYM2");
		tbc(allTrainers, 61, 0, "GYM2");
		tbc(allTrainers, 61, 3, "GYM2");
		tbc(allTrainers, 25, 0, "GYM3");
		tbc(allTrainers, 25, 1, "GYM3");
		tbc(allTrainers, 29, 0, "GYM3");
		tbc(allTrainers, 29, 1, "GYM3");
		tbc(allTrainers, 56, 4, "GYM4");
		tbc(allTrainers, 56, 5, "GYM4");
		tbc(allTrainers, 57, 0, "GYM4");
		tbc(allTrainers, 57, 1, "GYM4");
		tbc(allTrainers, 50, 1, "GYM5");
		tbc(allTrainers, 50, 3, "GYM5");
		tbc(allTrainers, 50, 4, "GYM5");
		tbc(allTrainers, 50, 6, "GYM5");
		tbc(allTrainers, 58, 0, "GYM7");
		tbc(allTrainers, 58, 1, "GYM7");
		tbc(allTrainers, 58, 2, "GYM7");
		tbc(allTrainers, 33, 0, "GYM7");
		tbc(allTrainers, 33, 1, "GYM7");
		tbc(allTrainers, 27, 2, "GYM8");
		tbc(allTrainers, 27, 4, "GYM8");
		tbc(allTrainers, 27, 3, "GYM8");
		tbc(allTrainers, 28, 2, "GYM8");
		tbc(allTrainers, 28, 3, "GYM8");
		tbc(allTrainers, 54, 17, "GYM9");
		tbc(allTrainers, 38, 20, "GYM10");
		tbc(allTrainers, 39, 17, "GYM10");
		tbc(allTrainers, 39, 18, "GYM10");
		tbc(allTrainers, 49, 2, "GYM11");
		tbc(allTrainers, 43, 1, "GYM11");
		tbc(allTrainers, 32, 2, "GYM11");
		tbc(allTrainers, 61, 4, "GYM12");
		tbc(allTrainers, 61, 5, "GYM12");
		tbc(allTrainers, 25, 8, "GYM12");
		tbc(allTrainers, 53, 18, "GYM12");
		tbc(allTrainers, 29, 13, "GYM12");
		tbc(allTrainers, 25, 2, "GYM13");
		tbc(allTrainers, 25, 5, "GYM13");
		tbc(allTrainers, 53, 4, "GYM13");
		tbc(allTrainers, 54, 4, "GYM13");
		tbc(allTrainers, 57, 5, "GYM14");
		tbc(allTrainers, 57, 6, "GYM14");
		tbc(allTrainers, 52, 1, "GYM14");
		tbc(allTrainers, 52, 10, "GYM14");
	}

	private void tbc(List<Trainer> allTrainers, int classNum, int number,
			String tag) {
		int currnum = -1;
		for (Trainer t : allTrainers) {
			if (t.trainerclass == classNum) {
				currnum++;
				if (currnum == number) {
					t.tag = tag;
					return;
				}
			}
		}
	}

	@Override
	public List<Pokemon> getPokemon() {
		return pokemonList;
	}

	@Override
	public Map<Pokemon, List<MoveLearnt>> getMovesLearnt() {
		Map<Pokemon, List<MoveLearnt>> movesets = new TreeMap<Pokemon, List<MoveLearnt>>();
		int pointersOffset = romEntry.getValue("PokemonMovesetsTableOffset");
		for (int i = 1; i <= 251; i++) {
			int pointer = readWord(pointersOffset + (i - 1) * 2);
			int realPointer = calculateOffset(bankOf(pointersOffset), pointer);
			Pokemon pkmn = pokes[i];
			// Skip over evolution data
			while (rom[realPointer] != 0) {
				if (rom[realPointer] == 5) {
					realPointer += 4;
				} else {
					realPointer += 3;
				}
			}
			List<MoveLearnt> ourMoves = new ArrayList<MoveLearnt>();
			realPointer++;
			while (rom[realPointer] != 0) {
				MoveLearnt learnt = new MoveLearnt();
				learnt.level = rom[realPointer] & 0xFF;
				learnt.move = rom[realPointer + 1] & 0xFF;
				ourMoves.add(learnt);
				realPointer += 2;
			}
			movesets.put(pkmn, ourMoves);
		}
		return movesets;
	}

	@Override
	public void setMovesLearnt(Map<Pokemon, List<MoveLearnt>> movesets) {
		writeEvosAndMovesLearnt(null, movesets);
	}

	@Override
	public List<Pokemon> getStaticPokemon() {
		List<Pokemon> statics = new ArrayList<Pokemon>();
		if (romEntry.getValue("StaticPokemonSupport") > 0) {
			for (int offset : romEntry.staticPokemonSingle) {
				statics.add(pokes[rom[offset] & 0xFF]);
			}
			// Game Corner
			for (int offset : romEntry.staticPokemonGameCorner.keySet()) {
				statics.add(pokes[rom[offset] & 0xFF]);
			}
		}
		return statics;
	}

	@Override
	public boolean setStaticPokemon(List<Pokemon> staticPokemon) {
		if (romEntry.getValue("StaticPokemonSupport") == 0) {
			return false;
		}
		if (!havePatchedFleeing) {
			patchFleeing();
		}
		if (staticPokemon.size() != romEntry.staticPokemonSingle.size()
				+ romEntry.staticPokemonGameCorner.size()) {
			return false;
		}
		for (Pokemon pkmn : staticPokemon) {
			if (!isInGame(pkmn)) {
				return false;
			}
		}

		Iterator<Pokemon> statics = staticPokemon.iterator();
		for (int offset : romEntry.staticPokemonSingle) {
			rom[offset] = (byte) statics.next().number;
		}

		int gcNameLength = romEntry.getValue("GameCornerPokemonNameLength");

		// Sort out static Pokemon
		for (int offset : romEntry.staticPokemonGameCorner.keySet()) {
			rom[offset] = (byte) statics.next().number;
			rom[offset + 0x11] = rom[offset];
			rom[offset + 0x16] = rom[offset];
			writePaddedPokemonName(pokes[rom[offset] & 0xFF].name,
					gcNameLength, romEntry.staticPokemonGameCorner.get(offset));
		}

		// Copies?
		for (int offset : romEntry.staticPokemonCopy.keySet()) {
			int copyTo = romEntry.staticPokemonCopy.get(offset);
			rom[copyTo] = rom[offset];
		}
		return true;
	}

	@Override
	public boolean canChangeStaticPokemon() {
		return (romEntry.getValue("StaticPokemonSupport") > 0);
	}

	@Override
	public List<Pokemon> bannedForStaticPokemon() {
		return Arrays.asList(pokes[201]); // Unown banned
	}

	private void writePaddedPokemonName(String name, int length, int offset) {
		String paddedName = String.format("%-" + length + "s", name);
		byte[] rawData = traduire(paddedName);
		for (int i = 0; i < length; i++) {
			rom[offset + i] = rawData[i];
		}
	}

	@Override
	public List<Integer> getTMMoves() {
		List<Integer> tms = new ArrayList<Integer>();
		int offset = romEntry.getValue("TMMovesOffset");
		for (int i = 1; i <= 50; i++) {
			tms.add(rom[offset + (i - 1)] & 0xFF);
		}
		return tms;
	}

	@Override
	public List<Integer> getHMMoves() {
		List<Integer> hms = new ArrayList<Integer>();
		int offset = romEntry.getValue("TMMovesOffset");
		for (int i = 1; i <= 7; i++) {
			hms.add(rom[offset + 50 + (i - 1)] & 0xFF);
		}
		return hms;
	}

	@Override
	public void setTMMoves(List<Integer> moveIndexes) {
		int offset = romEntry.getValue("TMMovesOffset");
		for (int i = 1; i <= 50; i++) {
			rom[offset + (i - 1)] = moveIndexes.get(i - 1).byteValue();
		}

		// TM Text
		String[] moveNames = readMoveNames();
		for (TMTextEntry tte : romEntry.tmTexts) {
			String moveName = moveNames[moveIndexes.get(tte.number - 1)];
			String text = tte.template.replace("%m", moveName);
			writeFixedLengthScriptString(text, tte.offset,
					lengthOfStringAt(tte.offset));
		}
	}

	@Override
	public int getTMCount() {
		return 50;
	}

	@Override
	public int getHMCount() {
		return 7;
	}

	@Override
	public Map<Pokemon, boolean[]> getTMHMCompatibility() {
		Map<Pokemon, boolean[]> compat = new TreeMap<Pokemon, boolean[]>();
		for (int i = 1; i <= 251; i++) {
			int baseStatsOffset = romEntry.getValue("PokemonStatsOffset")
					+ (i - 1) * 0x20;
			Pokemon pkmn = pokes[i];
			boolean[] flags = new boolean[58];
			for (int j = 0; j < 8; j++) {
				readByteIntoFlags(flags, j * 8 + 1, baseStatsOffset + 0x18 + j);
			}
			compat.put(pkmn, flags);
		}
		return compat;
	}

	@Override
	public void setTMHMCompatibility(Map<Pokemon, boolean[]> compatData) {
		for (Map.Entry<Pokemon, boolean[]> compatEntry : compatData.entrySet()) {
			Pokemon pkmn = compatEntry.getKey();
			boolean[] flags = compatEntry.getValue();
			int baseStatsOffset = romEntry.getValue("PokemonStatsOffset")
					+ (pkmn.number - 1) * 0x20;
			for (int j = 0; j < 8; j++) {
				if (!romEntry.isCrystal || j != 7) {
					rom[baseStatsOffset + 0x18 + j] = getByteFromFlags(flags,
							j * 8 + 1);
				} else {
					// Move tutor data
					// bits 1,2,3 of byte 7
					int changedByte = getByteFromFlags(flags, j * 8 + 1) & 0xFF;
					int currentByte = rom[baseStatsOffset + 0x18 + j];
					changedByte |= ((currentByte >> 1) & 0x01) << 1;
					changedByte |= ((currentByte >> 2) & 0x01) << 2;
					changedByte |= ((currentByte >> 3) & 0x01) << 3;
					rom[baseStatsOffset + 0x18 + j] = (byte) changedByte;
				}
			}
		}
	}

	@Override
	public boolean hasMoveTutors() {
		return romEntry.isCrystal;
	}

	@Override
	public List<Integer> getMoveTutorMoves() {
		if (romEntry.isCrystal) {
			List<Integer> mtMoves = new ArrayList<Integer>();
			for (int offset : romEntry.arrayEntries.get("MoveTutorMoves")) {
				mtMoves.add(rom[offset] & 0xFF);
			}
			return mtMoves;
		}
		return new ArrayList<Integer>();
	}

	@Override
	public void setMoveTutorMoves(List<Integer> moves) {
		if (!romEntry.isCrystal) {
			return;
		}
		if (moves.size() != 3) {
			return;
		}
		Iterator<Integer> mvList = moves.iterator();
		for (int offset : romEntry.arrayEntries.get("MoveTutorMoves")) {
			rom[offset] = mvList.next().byteValue();
		}

		// Construct a new menu
		if (romEntry.getValue("MoveTutorMenuOffset") > 0
				&& romEntry.getValue("MoveTutorMenuNewSpace") > 0) {
			String[] moveNames = readMoveNames();
			String[] names = new String[] { moveNames[moves.get(0)],
					moveNames[moves.get(1)], moveNames[moves.get(2)], "CANCEL" };
			int menuOffset = romEntry.getValue("MoveTutorMenuNewSpace");
			rom[menuOffset++] = (byte) 0x80;
			rom[menuOffset++] = 0x4;
			for (int i = 0; i < 4; i++) {
				byte[] trans = traduire(names[i]);
				System.arraycopy(trans, 0, rom, menuOffset, trans.length);
				menuOffset += trans.length;
				rom[menuOffset++] = 0x50;
			}
			int pointerOffset = romEntry.getValue("MoveTutorMenuOffset");
			writeWord(pointerOffset,
					makeGBPointer(romEntry.getValue("MoveTutorMenuNewSpace")));
		}
	}

	@Override
	public Map<Pokemon, boolean[]> getMoveTutorCompatibility() {
		if (!romEntry.isCrystal) {
			return new TreeMap<Pokemon, boolean[]>();
		}
		Map<Pokemon, boolean[]> compat = new TreeMap<Pokemon, boolean[]>();
		for (int i = 1; i <= 251; i++) {
			int baseStatsOffset = romEntry.getValue("PokemonStatsOffset")
					+ (i - 1) * 0x20;
			Pokemon pkmn = pokes[i];
			boolean[] flags = new boolean[4];
			int mtByte = rom[baseStatsOffset + 0x1F] & 0xFF;
			for (int j = 1; j <= 3; j++) {
				flags[j] = ((mtByte >> j) & 0x01) > 0;
			}
			compat.put(pkmn, flags);
		}
		return compat;
	}

	@Override
	public void setMoveTutorCompatibility(Map<Pokemon, boolean[]> compatData) {
		if (!romEntry.isCrystal) {
			return;
		}
		for (Map.Entry<Pokemon, boolean[]> compatEntry : compatData.entrySet()) {
			Pokemon pkmn = compatEntry.getKey();
			boolean[] flags = compatEntry.getValue();
			int baseStatsOffset = romEntry.getValue("PokemonStatsOffset")
					+ (pkmn.number - 1) * 0x20;
			int origMtByte = rom[baseStatsOffset + 0x1F] & 0xFF;
			int mtByte = origMtByte & 0x01;
			for (int j = 1; j <= 3; j++) {
				mtByte |= flags[j] ? (1 << j) : 0;
			}
			rom[baseStatsOffset + 0x1F] = (byte) mtByte;
		}
	}

	@Override
	public String getROMName() {
		if (isVietCrystal) {
			return "Pokemon VietCrystal";
		}
		return "Pokemon " + romEntry.name;
	}

	@Override
	public String getROMCode() {
		return romEntry.romCode;
	}

	@Override
	public String getSupportLevel() {
		return "Complete";
	}

	@Override
	public boolean hasTimeBasedEncounters() {
		return true; // All GSC do
	}

	@Override
	public List<Evolution> getEvolutions() {
		List<Evolution> evos = new ArrayList<Evolution>();
		int pointersOffset = romEntry.getValue("PokemonMovesetsTableOffset");
		List<Evolution> evosForThisPoke = new ArrayList<Evolution>();
		for (int i = 1; i <= 251; i++) {
			int pointer = readWord(pointersOffset + (i - 1) * 2);
			int realPointer = calculateOffset(bankOf(pointersOffset), pointer);
			evosForThisPoke.clear();
			int thisPoke = i;
			while (rom[realPointer] != 0) {
				int method = rom[realPointer] & 0xFF;
				int otherPoke = rom[realPointer + 2 + (method == 5 ? 1 : 0)] & 0xFF;
				EvolutionType type = EvolutionType.fromIndex(2, method);
				int extraInfo = 0;
				if (type == EvolutionType.TRADE) {
					int itemNeeded = rom[realPointer + 1] & 0xFF;
					if (itemNeeded != 0xFF) {
						type = EvolutionType.TRADE_ITEM;
						extraInfo = itemNeeded;
					}
				} else if (type == EvolutionType.LEVEL_ATTACK_HIGHER) {
					int tyrogueCond = rom[realPointer + 2] & 0xFF;
					if (tyrogueCond == 2) {
						type = EvolutionType.LEVEL_DEFENSE_HIGHER;
					} else if (tyrogueCond == 3) {
						type = EvolutionType.LEVEL_ATK_DEF_SAME;
					}
					extraInfo = rom[realPointer + 1] & 0xFF;
				} else if (type == EvolutionType.HAPPINESS) {
					int happCond = rom[realPointer + 1] & 0xFF;
					if (happCond == 2) {
						type = EvolutionType.HAPPINESS_DAY;
					} else if (happCond == 3) {
						type = EvolutionType.HAPPINESS_NIGHT;
					}
				} else {
					extraInfo = rom[realPointer + 1] & 0xFF;
				}
				Evolution evo = new Evolution(thisPoke, otherPoke, true, type,
						extraInfo);
				if (!evos.contains(evo)) {
					evos.add(evo);
					evosForThisPoke.add(evo);
				}
				realPointer += (method == 5 ? 4 : 3);
			}
			// split evos don't carry stats
			if (evosForThisPoke.size() > 1) {
				for (Evolution e : evosForThisPoke) {
					e.carryStats = false;
				}
			}
		}
		return evos;
	}

	@Override
	public void removeTradeEvolutions(boolean changeMoveEvos) {
		// no move evos, so no need to check for those
		log("--Removing Trade Evolutions--");
		List<Evolution> evos = this.getEvolutions();
		for (Evolution evol : evos) {
			if (evol.type == EvolutionType.TRADE
					|| evol.type == EvolutionType.TRADE_ITEM) {
				// change
				if (evol.from == 79) {
					// Slowpoke: Make water stone => Slowking
					evol.type = EvolutionType.STONE;
					evol.extraInfo = 24; // water stone
					logEvoChangeStone(pokes[evol.from].name,
							pokes[evol.to].name, itemNames[24]);
				} else if (evol.from == 117) {
					// Seadra: level 40
					evol.type = EvolutionType.LEVEL;
					evol.extraInfo = 40; // level
					logEvoChangeLevel(pokes[evol.from].name,
							pokes[evol.to].name, 40);
				} else if (evol.from == 61 || evol.type == EvolutionType.TRADE) {
					// Poliwhirl or any of the original 4 trade evos
					// Level 37
					evol.type = EvolutionType.LEVEL;
					evol.extraInfo = 37; // level
					logEvoChangeLevel(pokes[evol.from].name,
							pokes[evol.to].name, 37);
				} else {
					// A new trade evo of a single stage Pokemon
					// level 30
					evol.type = EvolutionType.LEVEL;
					evol.extraInfo = 30; // level
					logEvoChangeLevel(pokes[evol.from].name,
							pokes[evol.to].name, 30);
				}
			}
		}
		writeEvosAndMovesLearnt(evos, null);
		logBlankLine();

	}

	@Override
	public List<String> getTrainerNames() {
		int traineroffset = romEntry.getValue("TrainerDataTableOffset");
		int traineramount = romEntry.getValue("TrainerClassAmount");
		int[] trainerclasslimits = romEntry.arrayEntries
				.get("TrainerDataClassCounts");

		int[] pointers = new int[traineramount + 1];
		for (int i = 1; i <= traineramount; i++) {
			int pointer = readWord(traineroffset + (i - 1) * 2);
			pointers[i] = calculateOffset(bankOf(traineroffset), pointer);
		}

		List<String> allTrainers = new ArrayList<String>();
		for (int i = 1; i <= traineramount; i++) {
			int offs = pointers[i];
			int limit = trainerclasslimits[i];
			for (int trnum = 0; trnum < limit; trnum++) {
				String name = readVariableLengthString(offs);
				allTrainers.add(name);
				offs += name.length() + 1;
				int dataType = rom[offs] & 0xFF;
				offs++;
				while ((rom[offs] & 0xFF) != 0xFF) {
					offs += 2;
					if (dataType == 2 || dataType == 3) {
						offs++;
					}
					if (dataType % 2 == 1) {
						offs += 4;
					}
				}
				offs++;
			}
		}
		return allTrainers;
	}

	@Override
	public void setTrainerNames(List<String> trainerNames) {
		if (romEntry.getValue("CanChangeTrainerText") != 0) {
			int traineroffset = romEntry.getValue("TrainerDataTableOffset");
			int traineramount = romEntry.getValue("TrainerClassAmount");
			int[] trainerclasslimits = romEntry.arrayEntries
					.get("TrainerDataClassCounts");

			int[] pointers = new int[traineramount + 1];
			for (int i = 1; i <= traineramount; i++) {
				int pointer = readWord(traineroffset + (i - 1) * 2);
				pointers[i] = calculateOffset(bankOf(traineroffset), pointer);
			}
			// Build up new trainer data using old as a guideline.
			int[] offsetsInNew = new int[traineramount + 1];
			int oInNewCurrent = 0;
			Iterator<String> allTrainers = trainerNames.iterator();
			ByteArrayOutputStream newData = new ByteArrayOutputStream();
			try {
				for (int i = 1; i <= traineramount; i++) {
					int offs = pointers[i];
					int limit = trainerclasslimits[i];
					offsetsInNew[i] = oInNewCurrent;
					for (int trnum = 0; trnum < limit; trnum++) {
						String name = readVariableLengthString(offs);
						String newName = allTrainers.next();
						byte[] newNameStr = translateString(newName);
						newData.write(newNameStr);
						newData.write(0x50);
						oInNewCurrent += newNameStr.length + 1;
						offs += internalStringLength(name) + 1;
						int dataType = rom[offs] & 0xFF;
						offs++;
						newData.write(dataType);
						oInNewCurrent++;
						while ((rom[offs] & 0xFF) != 0xFF) {
							newData.write(rom, offs, 2);
							oInNewCurrent += 2;
							offs += 2;
							if (dataType == 2 || dataType == 3) {
								newData.write(rom, offs, 1);
								oInNewCurrent += 1;
								offs++;
							}
							if (dataType % 2 == 1) {
								newData.write(rom, offs, 4);
								oInNewCurrent += 4;
								offs += 4;
							}
						}
						newData.write(0xFF);
						oInNewCurrent++;
						offs++;
					}
				}

				// Copy new data into ROM
				byte[] newTrainerData = newData.toByteArray();
				int tdBase = pointers[1];
				System.arraycopy(newTrainerData, 0, rom, pointers[1],
						newTrainerData.length);

				// Finally, update the pointers
				for (int i = 2; i <= traineramount; i++) {
					int newOffset = tdBase + offsetsInNew[i];
					writeWord(traineroffset + (i - 1) * 2,
							makeGBPointer(newOffset));
				}
			} catch (IOException ex) {
				// This should never happen, but abort if it does.
			}
		}

	}

	@Override
	public TrainerNameMode trainerNameMode() {
		return TrainerNameMode.MAX_LENGTH_WITH_CLASS;
	}

	@Override
	public int maxTrainerNameLength() {
		// line size minus one for space
		return 17;
	}

	@Override
	public List<Integer> getTCNameLengthsByTrainer() {
		int traineramount = romEntry.getValue("TrainerClassAmount");
		int[] trainerclasslimits = romEntry.arrayEntries
				.get("TrainerDataClassCounts");
		List<String> tcNames = this.getTrainerClassNames();
		List<Integer> tcLengthsByT = new ArrayList<Integer>();

		for (int i = 1; i <= traineramount; i++) {
			int len = internalStringLength(tcNames.get(i - 1));
			for (int k = 0; k < trainerclasslimits[i]; k++) {
				tcLengthsByT.add(len);
			}
		}

		return tcLengthsByT;
	}

	@Override
	public List<String> getTrainerClassNames() {
		int amount = romEntry.getValue("TrainerClassAmount");
		int offset = romEntry.getValue("TrainerClassNamesOffset");
		List<String> trainerClassNames = new ArrayList<String>();
		for (int j = 0; j < amount; j++) {
			String name = readVariableLengthString(offset);
			offset += lengthOfStringAt(offset) + 1;
			trainerClassNames.add(name);
		}
		return trainerClassNames;
	}

	@Override
	public void setTrainerClassNames(List<String> trainerClassNames) {
		if (romEntry.getValue("CanChangeTrainerText") != 0) {
			int amount = romEntry.getValue("TrainerClassAmount");
			int offset = romEntry.getValue("TrainerClassNamesOffset");
			Iterator<String> trainerClassNamesI = trainerClassNames.iterator();
			for (int j = 0; j < amount; j++) {
				int len = lengthOfStringAt(offset) + 1;
				String newName = trainerClassNamesI.next();
				writeFixedLengthString(newName, offset, len);
				offset += len;
			}
		}
	}

	@Override
	public boolean fixedTrainerClassNamesLength() {
		return true;
	}

	@Override
	public String getDefaultExtension() {
		return "gbc";
	}

	@Override
	public int abilitiesPerPokemon() {
		return 0;
	}

	@Override
	public int highestAbilityIndex() {
		return 0;
	}

	@Override
	public int internalStringLength(String string) {
		return translateString(string).length;
	}

	@Override
	public int codeTweaksAvailable() {
		int available = 0;
		if (romEntry.codeTweaks.get("BWXPTweak") != null) {
			available |= CodeTweaks.BW_EXP_PATCH;
		}
		return available;
	}

	@Override
	public void applyBWEXPPatch() {
		String patchName = romEntry.codeTweaks.get("BWXPTweak");
		if (patchName == null) {
			return;
		}

		try {
			FileFunctions.applyPatch(rom, patchName);
		} catch (IOException e) {

		}
	}

	@Override
	public void applySignature() {
		// Intro sprite

		// Pick a pokemon
		int pokemon = RandomSource.nextInt(251) + 1;
		while (pokemon == 201) {
			// Unown is banned
			pokemon = RandomSource.nextInt(251) + 1;
		}

		rom[romEntry.getValue("IntroSpriteOffset")] = (byte) pokemon;
		rom[romEntry.getValue("IntroCryOffset")] = (byte) pokemon;

	}

	@Override
	public ItemList getAllowedItems() {
		return allowedItems;
	}

	private void loadItemNames() {
		itemNames = new String[256];
		itemNames[0] = "glitch";
		// trying to emulate pretty much what the game does here
		// normal items
		int origOffset = romEntry.getValue("ItemNamesOffset");
		int itemNameOffset = origOffset;
		for (int index = 1; index <= 0x100; index++) {
			if (itemNameOffset / 0x4000 > origOffset / 0x4000) {
				// the game would continue making its merry way into VRAM here,
				// but we don't have VRAM to simulate.
				// just give up.
				break;
			}
			int startOfText = itemNameOffset;
			while ((rom[itemNameOffset] & 0xFF) != 0x50) {
				itemNameOffset++;
			}
			itemNameOffset++;
			itemNames[index % 256] = readFixedLengthString(startOfText, 20);
		}
	}

	@Override
	public String[] getItemNames() {
		return itemNames;
	}

	private void patchFleeing() {
		havePatchedFleeing = true;
		int offset = romEntry.getValue("FleeingDataOffset");
		rom[offset] = (byte) 0xFF;
		rom[offset + 0xE] = (byte) 0xFF;
		rom[offset + 0x17] = (byte) 0xFF;
	}

	private void loadLandmarkNames() {

		int lmOffset = romEntry.getValue("LandmarkTableOffset");
		int lmBank = bankOf(lmOffset);
		int lmCount = romEntry.getValue("LandmarkCount");

		landmarkNames = new String[lmCount];

		for (int i = 0; i < lmCount; i++) {
			int lmNameOffset = calculateOffset(lmBank, readWord(lmOffset + i
					* 4 + 2));
			landmarkNames[i] = readVariableLengthString(lmNameOffset).replace(
					"\\x1F", " ");
		}

	}

	private void preprocessMaps() {
		itemOffs = new ArrayList<Integer>();

		int mhOffset = romEntry.getValue("MapHeaders");
		int mgCount = 26;
		int mLastGroup = 11;
		int mhBank = bankOf(mhOffset);
		mapNames = new String[mgCount + 1][100];

		int[] groupOffsets = new int[mgCount];
		for (int i = 0; i < mgCount; i++) {
			groupOffsets[i] = calculateOffset(mhBank,
					readWord(mhOffset + i * 2));
		}

		// Read maps
		for (int mg = 0; mg < mgCount; mg++) {
			int offset = groupOffsets[mg];
			int maxOffset = (mg == mgCount - 1) ? (mhBank + 1) * 0x4000
					: groupOffsets[mg + 1];
			int map = 0;
			int maxMap = (mg == mgCount - 1) ? mLastGroup : 999;
			while (offset < maxOffset && map < maxMap) {
				processMapAt(offset, mg + 1, map + 1);
				offset += 9;
				map++;
			}
		}
	}

	private void processMapAt(int offset, int mapBank, int mapNumber) {

		// second map header
		int smhBank = rom[offset] & 0xFF;
		int smhPointer = readWord(offset + 3);
		int smhOffset = calculateOffset(smhBank, smhPointer);

		// map name
		int mapLandmark = rom[offset + 5] & 0xFF;
		mapNames[mapBank][mapNumber] = landmarkNames[mapLandmark];

		// event header
		// event header is in same bank as script header
		int ehBank = rom[smhOffset + 6] & 0xFF;
		int ehPointer = readWord(smhOffset + 9);
		int ehOffset = calculateOffset(ehBank, ehPointer);

		// skip over filler
		ehOffset += 2;

		// warps
		int warpCount = rom[ehOffset++] & 0xFF;
		// warps are skipped
		ehOffset += warpCount * 5;

		// xy triggers
		int triggerCount = rom[ehOffset++] & 0xFF;
		// xy triggers are skipped
		ehOffset += triggerCount * 8;

		// signposts
		int signpostCount = rom[ehOffset++] & 0xFF;
		// we do care about these
		for (int sp = 0; sp < signpostCount; sp++) {
			// type=7 are hidden items
			int spType = rom[ehOffset + sp * 5 + 2] & 0xFF;
			if (spType == 7) {
				// get event pointer
				int spPointer = readWord(ehOffset + sp * 5 + 3);
				int spOffset = calculateOffset(ehBank, spPointer);
				// item is at spOffset+2 (first two bytes are the flag id)
				itemOffs.add(spOffset + 2);
			}
		}
		// now skip past them
		ehOffset += signpostCount * 5;

		// visible objects/people
		int peopleCount = rom[ehOffset++] & 0xFF;
		// we also care about these
		for (int p = 0; p < peopleCount; p++) {
			// color_function & 1 = 1 if itemball
			int pColorFunction = rom[ehOffset + p * 13 + 7];
			if ((pColorFunction & 1) == 1) {
				// get event pointer
				int pPointer = readWord(ehOffset + p * 13 + 9);
				int pOffset = calculateOffset(ehBank, pPointer);
				// item is at the pOffset for non-hidden items
				itemOffs.add(pOffset);
			}
		}

	}

	@Override
	public List<Integer> getRequiredFieldTMs() {
		return Arrays.asList(new Integer[] { 4, 20, 22, 26, 28, 34, 35, 39, 40,
				43, 44, 46 });
	}

	@Override
	public List<Integer> getCurrentFieldTMs() {
		List<Integer> fieldTMs = new ArrayList<Integer>();

		for (int offset : itemOffs) {
			int itemHere = rom[offset] & 0xFF;
			if (allowedItems.isTM(itemHere)) {
				int thisTM = 0;
				if (itemHere >= 191 && itemHere <= 194) {
					thisTM = itemHere - 190; // TM block 1 offset
				} else if (itemHere >= 196 && itemHere <= 219) {
					thisTM = itemHere - 191; // TM block 2 offset
				} else {
					thisTM = itemHere - 192; // TM block 3 offset
				}
				// hack for the bug catching contest repeat TM28
				if (fieldTMs.contains(thisTM) == false) {
					fieldTMs.add(thisTM);
				}
			}
		}
		return fieldTMs;
	}

	@Override
	public void setFieldTMs(List<Integer> fieldTMs) {
		Iterator<Integer> iterTMs = fieldTMs.iterator();
		int[] givenTMs = new int[256];

		for (int offset : itemOffs) {
			int itemHere = rom[offset] & 0xFF;
			if (allowedItems.isTM(itemHere)) {
				// Cache replaced TMs to duplicate bug catching contest TM
				if (givenTMs[itemHere] != 0) {
					rom[offset] = (byte) givenTMs[itemHere];
				} else {
					// Replace this with a TM from the list
					int tm = iterTMs.next();
					if (tm >= 1 && tm <= 4) {
						tm += 190;
					} else if (tm >= 5 && tm <= 28) {
						tm += 191;
					} else {
						tm += 192;
					}
					givenTMs[itemHere] = tm;
					rom[offset] = (byte) tm;
				}
			}
		}
	}

	@Override
	public List<Integer> getRegularFieldItems() {
		List<Integer> fieldItems = new ArrayList<Integer>();

		for (int offset : itemOffs) {
			int itemHere = rom[offset] & 0xFF;
			if (allowedItems.isAllowed(itemHere)
					&& !(allowedItems.isTM(itemHere))) {
				fieldItems.add(itemHere);
			}
		}
		return fieldItems;
	}

	@Override
	public void setRegularFieldItems(List<Integer> items) {
		Iterator<Integer> iterItems = items.iterator();

		for (int offset : itemOffs) {
			int itemHere = rom[offset] & 0xFF;
			if (allowedItems.isAllowed(itemHere)
					&& !(allowedItems.isTM(itemHere))) {
				// Replace it
				rom[offset] = (byte) (iterItems.next().intValue());
			}
		}

	}

	@Override
	public List<IngameTrade> getIngameTrades() {
		List<IngameTrade> trades = new ArrayList<IngameTrade>();

		// info
		int tableOffset = romEntry.getValue("TradeTableOffset");
		int tableSize = romEntry.getValue("TradeTableSize");
		int nicknameLength = romEntry.getValue("TradeNameLength");
		int otLength = romEntry.getValue("TradeOTLength");
		int[] unused = romEntry.arrayEntries.get("TradesUnused");
		int unusedOffset = 0;
		int entryLength = nicknameLength + otLength + 10;

		for (int entry = 0; entry < tableSize; entry++) {
			if (unusedOffset < unused.length && unused[unusedOffset] == entry) {
				unusedOffset++;
				continue;
			}
			IngameTrade trade = new IngameTrade();
			int entryOffset = tableOffset + entry * entryLength;
			trade.requestedPokemon = pokes[rom[entryOffset + 1] & 0xFF];
			trade.givenPokemon = pokes[rom[entryOffset + 2] & 0xFF];
			trade.nickname = readString(entryOffset + 3, nicknameLength);
			int atkdef = rom[entryOffset + 3 + nicknameLength] & 0xFF;
			int spdspc = rom[entryOffset + 4 + nicknameLength] & 0xFF;
			trade.ivs = new int[] { (atkdef >> 4) & 0xF, atkdef & 0xF,
					(spdspc >> 4) & 0xF, spdspc & 0xF };
			trade.item = rom[entryOffset + 5 + nicknameLength] & 0xFF;
			trade.otId = readWord(entryOffset + 6 + nicknameLength);
			trade.otName = readString(entryOffset + 8 + nicknameLength,
					otLength);
			trades.add(trade);
		}

		return trades;

	}

	@Override
	public void setIngameTrades(List<IngameTrade> trades) {
		// info
		int tableOffset = romEntry.getValue("TradeTableOffset");
		int tableSize = romEntry.getValue("TradeTableSize");
		int nicknameLength = romEntry.getValue("TradeNameLength");
		int otLength = romEntry.getValue("TradeOTLength");
		int[] unused = romEntry.arrayEntries.get("TradesUnused");
		int unusedOffset = 0;
		int entryLength = nicknameLength + otLength + 9;
		if (entryLength % 2 != 0) {
			entryLength++;
		}
		int tradeOffset = 0;

		for (int entry = 0; entry < tableSize; entry++) {
			if (unusedOffset < unused.length && unused[unusedOffset] == entry) {
				unusedOffset++;
				continue;
			}
			IngameTrade trade = trades.get(tradeOffset++);
			int entryOffset = tableOffset + entry * entryLength;
			rom[entryOffset + 1] = (byte) trade.requestedPokemon.number;
			rom[entryOffset + 2] = (byte) trade.givenPokemon.number;
			if (romEntry.getValue("CanChangeTrainerText") > 0) {
				writeFixedLengthString(trade.nickname, entryOffset + 3,
						nicknameLength);
			}
			rom[entryOffset + 3 + nicknameLength] = (byte) (trade.ivs[0] << 4 | trade.ivs[1]);
			rom[entryOffset + 4 + nicknameLength] = (byte) (trade.ivs[2] << 4 | trade.ivs[3]);
			rom[entryOffset + 5 + nicknameLength] = (byte) trade.item;
			writeWord(entryOffset + 6 + nicknameLength, trade.otId);
			if (romEntry.getValue("CanChangeTrainerText") > 0) {
				writeFixedLengthString(trade.otName, entryOffset + 8
						+ nicknameLength, otLength);
			}
			// remove gender req
			rom[entryOffset + 8 + nicknameLength + otLength] = 0;

		}
	}

	@Override
	public boolean hasDVs() {
		return true;
	}

	@Override
	public int generationOfPokemon() {
		return 2;
	}

	@Override
	public void removeEvosForPokemonPool() {
		List<Pokemon> pokemonIncluded = this.mainPokemonList;
		List<Evolution> currentEvos = this.getEvolutions();
		List<Evolution> keepEvos = new ArrayList<Evolution>();
		for (Evolution evol : currentEvos) {
			if (pokemonIncluded.contains(pokes[evol.from])
					&& pokemonIncluded.contains(pokes[evol.to])) {
				keepEvos.add(evol);
			}
		}
		writeEvosAndMovesLearnt(keepEvos, null);
	}

	private void writeEvosAndMovesLearnt(List<Evolution> evos,
			Map<Pokemon, List<MoveLearnt>> movesets) {
		// this assumes that the evo/attack pointers & data
		// are at the end of the bank
		// which, in every clean G/S/C rom supported, they are
		// specify null to either argument to copy old values
		int movesEvosStart = romEntry.getValue("PokemonMovesetsTableOffset");
		int movesEvosBank = bankOf(movesEvosStart);
		byte[] pointerTable = new byte[251 * 2];
		int startOfNextBank = ((movesEvosStart / 0x4000) + 1) * 0x4000;
		int dataBlockSize = startOfNextBank
				- (movesEvosStart + pointerTable.length);
		int dataBlockOffset = movesEvosStart + pointerTable.length;
		byte[] dataBlock = new byte[dataBlockSize];
		int offsetInData = 0;
		for (int i = 1; i <= 251; i++) {
			// determine pointer
			int oldDataOffset = calculateOffset(movesEvosBank,
					readWord(movesEvosStart + (i - 1) * 2));
			int offsetStart = dataBlockOffset + offsetInData;
			boolean evoWritten = false;
			if (evos == null) {
				// copy old
				int evoOffset = oldDataOffset;
				while (rom[evoOffset] != 0x00) {
					int method = rom[evoOffset] & 0xFF;
					int limiter = (method == 5) ? 4 : 3;
					for (int b = 0; b < limiter; b++) {
						dataBlock[offsetInData++] = rom[evoOffset++];
					}
					evoWritten = true;
				}
			} else {
				for (Evolution evo : evos) {
					// write evos
					if (evo.from == i) {
						// write this one
						dataBlock[offsetInData++] = (byte) evo.type.toIndex(2);
						if (evo.type == EvolutionType.LEVEL
								|| evo.type == EvolutionType.STONE
								|| evo.type == EvolutionType.TRADE_ITEM) {
							// simple types
							dataBlock[offsetInData++] = (byte) evo.extraInfo;
						} else if (evo.type == EvolutionType.TRADE) {
							// non-item trade
							dataBlock[offsetInData++] = (byte) 0xFF;
						} else if (evo.type == EvolutionType.HAPPINESS) {
							// cond 01
							dataBlock[offsetInData++] = 0x01;
						} else if (evo.type == EvolutionType.HAPPINESS_DAY) {
							// cond 02
							dataBlock[offsetInData++] = 0x02;
						} else if (evo.type == EvolutionType.HAPPINESS_NIGHT) {
							// cond 03
							dataBlock[offsetInData++] = 0x03;
						} else if (evo.type == EvolutionType.LEVEL_ATTACK_HIGHER) {
							dataBlock[offsetInData++] = (byte) evo.extraInfo;
							dataBlock[offsetInData++] = 0x01;
						} else if (evo.type == EvolutionType.LEVEL_DEFENSE_HIGHER) {
							dataBlock[offsetInData++] = (byte) evo.extraInfo;
							dataBlock[offsetInData++] = 0x02;
						} else if (evo.type == EvolutionType.LEVEL_ATK_DEF_SAME) {
							dataBlock[offsetInData++] = (byte) evo.extraInfo;
							dataBlock[offsetInData++] = 0x03;
						}
						dataBlock[offsetInData++] = (byte) evo.to;
						evoWritten = true;
					}
				}
			}
			// can we reuse a terminator?
			if (!evoWritten && offsetStart != dataBlockOffset) {
				// reuse last pokemon's move terminator for our evos
				offsetStart -= 1;
			} else {
				// write a terminator
				dataBlock[offsetInData++] = 0x00;
			}
			// write table entry now that we're sure of its location
			int pointerNow = makeGBPointer(offsetStart);
			writeWord(pointerTable, (i - 1) * 2, pointerNow);
			// moveset
			if (movesets == null) {
				// copy old
				int movesOffset = oldDataOffset;
				// move past evos
				while (rom[movesOffset] != 0x00) {
					int method = rom[movesOffset] & 0xFF;
					movesOffset += (method == 5) ? 4 : 3;
				}
				movesOffset++;
				// copy moves
				while (rom[movesOffset] != 0x00) {
					dataBlock[offsetInData++] = rom[movesOffset++];
					dataBlock[offsetInData++] = rom[movesOffset++];
				}
			} else {
				List<MoveLearnt> moves = movesets.get(pokes[i]);
				for (MoveLearnt ml : moves) {
					dataBlock[offsetInData++] = (byte) ml.level;
					dataBlock[offsetInData++] = (byte) ml.move;
				}
			}
			// terminator
			dataBlock[offsetInData++] = 0x00;
		}
		// write new data
		System.arraycopy(pointerTable, 0, rom, movesEvosStart,
				pointerTable.length);
		System.arraycopy(dataBlock, 0, rom, dataBlockOffset, dataBlock.length);
	}

	@Override
	public boolean supportsFourStartingMoves() {
		return (romEntry.getValue("SupportsFourStartingMoves") > 0);
	}
}
