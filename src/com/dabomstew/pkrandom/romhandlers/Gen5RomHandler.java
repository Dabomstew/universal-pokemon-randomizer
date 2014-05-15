package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  Gen5RomHandler.java - randomizer handler for B/W/B2/W2.				--*/
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

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import pptxt.PPTxtHandler;

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

import dsdecmp.HexInputStream;
import dsdecmp.JavaDSDecmp;

public class Gen5RomHandler extends AbstractDSRomHandler {

	// Statics
	private static final Type[] typeTable = constructTypeTable();

	private static Type[] constructTypeTable() {
		Type[] table = new Type[256];
		table[0x00] = Type.NORMAL;
		table[0x01] = Type.FIGHTING;
		table[0x02] = Type.FLYING;
		table[0x03] = Type.POISON;
		table[0x04] = Type.GROUND;
		table[0x05] = Type.ROCK;
		table[0x06] = Type.BUG;
		table[0x07] = Type.GHOST;
		table[0x08] = Type.STEEL;
		table[0x09] = Type.FIRE;
		table[0x0A] = Type.WATER;
		table[0x0B] = Type.GRASS;
		table[0x0C] = Type.ELECTRIC;
		table[0x0D] = Type.PSYCHIC;
		table[0x0E] = Type.ICE;
		table[0x0F] = Type.DRAGON;
		table[0x10] = Type.DARK;
		return table;
	}

	private static byte typeToByte(Type type) {
		if (type == null) {
			return 0x00; // normal?
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
			return 0x06;
		case GHOST:
			return 0x07;
		case FIRE:
			return 0x09;
		case WATER:
			return 0x0A;
		case GRASS:
			return 0x0B;
		case ELECTRIC:
			return 0x0C;
		case PSYCHIC:
			return 0x0D;
		case ICE:
			return 0x0E;
		case DRAGON:
			return 0x0F;
		case STEEL:
			return 0x08;
		case DARK:
			return 0x10;
		default:
			return 0; // normal by default
		}
	}

	private static class OffsetWithinEntry {
		private int entry;
		private int offset;
	}

	private static class RomEntry {
		private String name;
		private String romCode;
		private int romType;
		private boolean staticPokemonSupport = false,
				copyStaticPokemon = false;
		private Map<String, String> strings = new HashMap<String, String>();
		private Map<String, Integer> numbers = new HashMap<String, Integer>();
		private Map<String, int[]> arrayEntries = new HashMap<String, int[]>();
		private Map<String, OffsetWithinEntry[]> offsetArrayEntries = new HashMap<String, OffsetWithinEntry[]>();
		private List<StaticPokemon> staticPokemon = new ArrayList<StaticPokemon>();

		private int getInt(String key) {
			if (!numbers.containsKey(key)) {
				numbers.put(key, 0);
			}
			return numbers.get(key);
		}

		private String getString(String key) {
			if (!strings.containsKey(key)) {
				strings.put(key, "");
			}
			return strings.get(key);
		}
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
					FileFunctions.openConfig("gen5_offsets.ini"), "UTF-8");
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
						if (r[0].equals("Game")) {
							current.romCode = r[1];
						} else if (r[0].equals("Type")) {
							if (r[1].equalsIgnoreCase("BW2")) {
								current.romType = Type_BW2;
							} else {
								current.romType = Type_BW;
							}
						} else if (r[0].equals("CopyFrom")) {
							for (RomEntry otherEntry : roms) {
								if (r[1].equalsIgnoreCase(otherEntry.romCode)) {
									// copy from here
									current.arrayEntries
											.putAll(otherEntry.arrayEntries);
									current.numbers.putAll(otherEntry.numbers);
									current.strings.putAll(otherEntry.strings);
									current.offsetArrayEntries
											.putAll(otherEntry.offsetArrayEntries);
									if (current.copyStaticPokemon) {
										current.staticPokemon
												.addAll(otherEntry.staticPokemon);
										current.staticPokemonSupport = true;
									} else {
										current.staticPokemonSupport = false;
									}
								}
							}
						} else if (r[0].equals("StaticPokemon[]")) {
							if (r[1].startsWith("[") && r[1].endsWith("]")) {
								String[] offsets = r[1].substring(1,
										r[1].length() - 1).split(",");
								int[] offs = new int[offsets.length];
								int[] files = new int[offsets.length];
								int c = 0;
								for (String off : offsets) {
									String[] parts = off.split("\\:");
									files[c] = parseRIInt(parts[0]);
									offs[c++] = parseRIInt(parts[1]);
								}
								StaticPokemon sp = new StaticPokemon();
								sp.files = files;
								sp.offsets = offs;
								current.staticPokemon.add(sp);
							} else {
								String[] parts = r[1].split("\\:");
								int files = parseRIInt(parts[0]);
								int offs = parseRIInt(parts[1]);
								StaticPokemon sp = new StaticPokemon();
								sp.files = new int[] { files };
								sp.offsets = new int[] { offs };
							}
						} else if (r[0].equals("StaticPokemonSupport")) {
							int spsupport = parseRIInt(r[1]);
							current.staticPokemonSupport = (spsupport > 0);
						} else if (r[0].equals("CopyStaticPokemon")) {
							int csp = parseRIInt(r[1]);
							current.copyStaticPokemon = (csp > 0);
						} else if (r[0].startsWith("StarterOffsets")
								|| r[0].equals("StaticPokemonFormValues")) {
							String[] offsets = r[1].substring(1,
									r[1].length() - 1).split(",");
							OffsetWithinEntry[] offs = new OffsetWithinEntry[offsets.length];
							int c = 0;
							for (String off : offsets) {
								String[] parts = off.split("\\:");
								OffsetWithinEntry owe = new OffsetWithinEntry();
								owe.entry = parseRIInt(parts[0]);
								owe.offset = parseRIInt(parts[1]);
								offs[c++] = owe;
							}
							current.offsetArrayEntries.put(r[0], offs);
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
									current.arrayEntries.put(r[0], offs);
								}
							} else if (r[0].endsWith("Offset")
									|| r[0].endsWith("Count")
									|| r[0].endsWith("Number")) {
								int offs = parseRIInt(r[1]);
								current.numbers.put(r[0], offs);
							} else {
								current.strings.put(r[0], r[1]);
							}
						}
					}
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
		}

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

	private static void setupAllowedItems() {
		allowedItems = new ItemList(638);
		// Key items + version exclusives
		allowedItems.banRange(428, 109);
		allowedItems.banRange(621, 18);
		allowedItems.banSingles(574, 578, 579, 616, 617);
		// Unknown blank items or version exclusives
		allowedItems.banRange(113, 3);
		allowedItems.banRange(120, 14);
		// TMs & HMs - tms cant be held in gen5
		allowedItems.tmRange(328, 92);
		allowedItems.tmRange(618, 3);
		allowedItems.banRange(328, 100);
		allowedItems.banRange(618, 3);
		// Battle Launcher exclusives
		allowedItems.banRange(592, 24);
	}

	// This ROM
	private Pokemon[] pokes;
	private List<Pokemon> pokemonList;
	private Move[] moves;
	private RomEntry romEntry;
	private byte[] arm9;
	private List<String> abilityNames;
	private List<String> itemNames;

	private static final int Type_BW = 0;
	private static final int Type_BW2 = 1;

	private NARCContents pokeNarc, moveNarc, stringsNarc, storyTextNarc,
			scriptNarc;

	@Override
	protected boolean detectNDSRom(String ndsCode) {
		for (RomEntry re : roms) {
			if (ndsCode.equals(re.romCode)) {
				this.romEntry = re;
				return true; // match
			}
		}
		return false;
	}

	@Override
	protected void loadedROM() {
		try {
			arm9 = readARM9();
		} catch (IOException e) {
			arm9 = new byte[0];
		}
		try {
			stringsNarc = readNARC(romEntry.getString("TextStrings"));
			storyTextNarc = readNARC(romEntry.getString("TextStory"));
		} catch (IOException e) {
			stringsNarc = null;
			storyTextNarc = null;
		}

		try {
			scriptNarc = readNARC(romEntry.getString("Scripts"));
		} catch (IOException e) {
			scriptNarc = null;
		}
		loadPokemonStats();
		pokemonList = Arrays.asList(pokes);
		loadMoves();

		abilityNames = getStrings(false,
				romEntry.getInt("AbilityNamesTextOffset"));
		itemNames = getStrings(false, romEntry.getInt("ItemNamesTextOffset"));
	}

	private void loadPokemonStats() {
		try {
			pokeNarc = this.readNARC(romEntry.getString("PokemonStats"));
			String[] pokeNames = readPokemonNames();
			pokes = new Pokemon[650];
			for (int i = 1; i <= 649; i++) {
				pokes[i] = new Pokemon();
				pokes[i].number = i;
				loadBasicPokeStats(pokes[i], pokeNarc.files.get(i));
				// Name?
				pokes[i].name = pokeNames[i];
			}
		} catch (IOException e) {
			// uh-oh?
			e.printStackTrace();
		}

	}

	private void loadMoves() {
		try {
			moveNarc = this.readNARC(romEntry.getString("MoveData"));
			moves = new Move[560];
			List<String> moveNames = getStrings(false,
					romEntry.getInt("MoveNamesTextOffset"));
			for (int i = 1; i <= 559; i++) {
				byte[] moveData = moveNarc.files.get(i);
				moves[i] = new Move();
				moves[i].name = moveNames.get(i);
				moves[i].number = i;
				moves[i].hitratio = (moveData[4] & 0xFF);
				moves[i].power = moveData[3] & 0xFF;
				moves[i].pp = moveData[5] & 0xFF;
				moves[i].type = typeTable[moveData[0] & 0xFF];
				moves[i].effectIndex = moveData[2] & 0xFF;
			}
		} catch (IOException e) {
			// change this later
			e.printStackTrace();
		}

	}

	private void loadBasicPokeStats(Pokemon pkmn, byte[] stats) {
		pkmn.hp = stats[0] & 0xFF;
		pkmn.attack = stats[1] & 0xFF;
		pkmn.defense = stats[2] & 0xFF;
		pkmn.speed = stats[3] & 0xFF;
		pkmn.spatk = stats[4] & 0xFF;
		pkmn.spdef = stats[5] & 0xFF;
		// Type
		pkmn.primaryType = typeTable[stats[6] & 0xFF];
		pkmn.secondaryType = typeTable[stats[7] & 0xFF];
		// Only one type?
		if (pkmn.secondaryType == pkmn.primaryType) {
			pkmn.secondaryType = null;
		}
		pkmn.catchRate = stats[8] & 0xFF;
		pkmn.growthCurve = ExpCurve.fromByte(stats[21]);
		// Abilities for debugging later
		pkmn.ability1 = stats[24] & 0xFF;
		pkmn.ability2 = stats[25] & 0xFF;
		pkmn.ability3 = stats[26] & 0xFF;

		// Held Items?
		int item1 = readWord(stats, 12);
		int item2 = readWord(stats, 14);

		if (item1 == item2) {
			// guaranteed
			pkmn.guaranteedHeldItem = item1;
			pkmn.commonHeldItem = 0;
			pkmn.rareHeldItem = 0;
			pkmn.darkGrassHeldItem = 0;
		} else {
			pkmn.guaranteedHeldItem = 0;
			pkmn.commonHeldItem = item1;
			pkmn.rareHeldItem = item2;
			pkmn.darkGrassHeldItem = readWord(stats, 16);
		}
	}

	private String[] readPokemonNames() {
		String[] pokeNames = new String[650];
		List<String> nameList = getStrings(false,
				romEntry.getInt("PokemonNamesTextOffset"));
		for (int i = 1; i <= 649; i++) {
			pokeNames[i] = nameList.get(i);
		}
		return pokeNames;
	}

	@Override
	protected void savingROM() {
		savePokemonStats();
		saveMoves();
		try {
			writeARM9(arm9);
		} catch (IOException e) {
		}
		try {
			writeNARC(romEntry.getString("TextStrings"), stringsNarc);
			writeNARC(romEntry.getString("TextStory"), storyTextNarc);
		} catch (IOException e) {
		}

		try {
			writeNARC(romEntry.getString("Scripts"), scriptNarc);
		} catch (IOException e) {
		}
	}

	private void saveMoves() {
		for (int i = 1; i <= 559; i++) {
			byte[] data = moveNarc.files.get(i);
			data[3] = (byte) moves[i].power;
			data[0] = typeToByte(moves[i].type);
			int hitratio = (int) Math.round(moves[i].hitratio);
			if (hitratio < 0) {
				hitratio = 0;
			}
			if (hitratio > 101) {
				hitratio = 100;
			}
			data[4] = (byte) hitratio;
			data[5] = (byte) moves[i].pp;
		}

		try {
			this.writeNARC(romEntry.getString("MoveData"), moveNarc);
		} catch (IOException e) {
			// // change this later
			e.printStackTrace();
		}

	}

	private void savePokemonStats() {
		List<String> nameList = getStrings(false,
				romEntry.getInt("PokemonNamesTextOffset"));
		for (int i = 1; i <= 649; i++) {
			saveBasicPokeStats(pokes[i], pokeNarc.files.get(i));
			nameList.set(i, pokes[i].name);
		}
		setStrings(false, romEntry.getInt("PokemonNamesTextOffset"), nameList);
		try {
			this.writeNARC(romEntry.getString("PokemonStats"), pokeNarc);
		} catch (IOException e) {
			// uh-oh?
			e.printStackTrace();
		}

	}

	private void saveBasicPokeStats(Pokemon pkmn, byte[] stats) {
		stats[0] = (byte) pkmn.hp;
		stats[1] = (byte) pkmn.attack;
		stats[2] = (byte) pkmn.defense;
		stats[3] = (byte) pkmn.speed;
		stats[4] = (byte) pkmn.spatk;
		stats[5] = (byte) pkmn.spdef;
		stats[6] = typeToByte(pkmn.primaryType);
		if (pkmn.secondaryType == null) {
			stats[7] = stats[6];
		} else {
			stats[7] = typeToByte(pkmn.secondaryType);
		}
		stats[8] = (byte) pkmn.catchRate;
		stats[21] = pkmn.growthCurve.toByte();

		stats[24] = (byte) pkmn.ability1;
		stats[25] = (byte) pkmn.ability2;
		stats[26] = (byte) pkmn.ability3;

		// Held items
		if (pkmn.guaranteedHeldItem > 0) {
			writeWord(stats, 12, pkmn.guaranteedHeldItem);
			writeWord(stats, 14, pkmn.guaranteedHeldItem);
			writeWord(stats, 16, 0);
		} else {
			writeWord(stats, 12, pkmn.commonHeldItem);
			writeWord(stats, 14, pkmn.rareHeldItem);
			writeWord(stats, 16, pkmn.darkGrassHeldItem);
		}
	}

	@Override
	public boolean isInGame(Pokemon pkmn) {
		return isInGame(pkmn.number);
	}

	@Override
	public boolean isInGame(int pokemonNumber) {
		return pokemonNumber >= 1 && pokemonNumber <= 649;
	}

	@Override
	public List<Pokemon> getPokemon() {
		return pokemonList;
	}

	@Override
	public List<Pokemon> getStarters() {
		NARCContents scriptNARC = scriptNarc;
		List<Pokemon> starters = new ArrayList<Pokemon>();
		for (int i = 0; i < 3; i++) {
			OffsetWithinEntry[] thisStarter = romEntry.offsetArrayEntries
					.get("StarterOffsets" + (i + 1));
			starters.add(pokes[readWord(
					scriptNARC.files.get(thisStarter[0].entry),
					thisStarter[0].offset)]);
		}
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

		// Fix up starter offsets
		try {
			NARCContents scriptNARC = scriptNarc;
			for (int i = 0; i < 3; i++) {
				int starter = newStarters.get(i).number;
				OffsetWithinEntry[] thisStarter = romEntry.offsetArrayEntries
						.get("StarterOffsets" + (i + 1));
				for (OffsetWithinEntry entry : thisStarter) {
					writeWord(scriptNARC.files.get(entry.entry), entry.offset,
							starter);
				}
			}
			// GIVE ME BACK MY PURRLOIN
			if (romEntry.romType == Type_BW2) {
				byte[] newScript = new byte[] { 0x28, 0x00, (byte) 0xA1, 0x40,
						0x04, 0x00, (byte) 0xDE, 0x00, 0x00, 0x00, (byte) 0xFD,
						0x01, 0x05, 0x00 };
				if (romEntry.romCode.charAt(3) == 'J') {
					newScript[0x6] -= 4;
				}
				byte[] oldFile = scriptNARC.files.get(romEntry
						.getInt("PokedexGivenFileOffset"));
				byte[] newFile = new byte[oldFile.length + newScript.length];
				int offset = find(oldFile, "2800A1400400");
				if (offset > 0) {
					System.arraycopy(oldFile, 0, newFile, 0, oldFile.length);
					System.arraycopy(newScript, 0, newFile, oldFile.length,
							newScript.length);
					newFile[offset++] = 0x1E;
					newFile[offset++] = 0x0;
					writeRelativePointer(newFile, offset, oldFile.length);
					scriptNARC.files.set(
							romEntry.getInt("PokedexGivenFileOffset"), newFile);
				}
			} else {
				byte[] newScript = new byte[] { 0x24, 0x00, (byte) 0xA7, 0x02,
						(byte) 0xE7, 0x00, 0x00, 0x00, (byte) 0xDE, 0x00, 0x00,
						0x00, (byte) 0xF8, 0x01, 0x05, 0x00 };
				if (romEntry.romCode.charAt(3) == 'J') {
					newScript[0x4] -= 4;
					newScript[0x8] -= 4;
				}
				byte[] oldFile = scriptNARC.files.get(romEntry
						.getInt("PokedexGivenFileOffset"));
				byte[] newFile = new byte[oldFile.length + newScript.length];
				int offset = find(oldFile, "2400A702");
				if (offset > 0) {
					System.arraycopy(oldFile, 0, newFile, 0, oldFile.length);
					System.arraycopy(newScript, 0, newFile, oldFile.length,
							newScript.length);
					newFile[offset++] = 0x04;
					newFile[offset++] = 0x0;
					writeRelativePointer(newFile, offset, oldFile.length);
					scriptNARC.files.set(
							romEntry.getInt("PokedexGivenFileOffset"), newFile);
				}
			}

			// Starter sprites
			NARCContents starterNARC = this.readNARC(romEntry
					.getString("StarterGraphics"));
			NARCContents pokespritesNARC = this.readNARC(romEntry
					.getString("PokemonGraphics"));
			replaceStarterFiles(starterNARC, pokespritesNARC, 0,
					newStarters.get(0).number);
			replaceStarterFiles(starterNARC, pokespritesNARC, 1,
					newStarters.get(1).number);
			replaceStarterFiles(starterNARC, pokespritesNARC, 2,
					newStarters.get(2).number);
			writeNARC(romEntry.getString("StarterGraphics"), starterNARC);
		} catch (IOException ex) {
			return false;
		} catch (InterruptedException e) {
			return false;
		}
		// Fix text depending on version
		if (romEntry.romType == Type_BW) {
			List<String> yourHouseStrings = getStrings(true,
					romEntry.getInt("StarterLocationTextOffset"));
			for (int i = 0; i < 3; i++) {
				yourHouseStrings.set(18 - i, "\\xF000\\xBD02\\x0000The "
						+ newStarters.get(i).primaryType.camelCase()
						+ "-type Pok\\x00E9mon\\xFFFE\\xF000\\xBD02\\x0000"
						+ newStarters.get(i).name);
			}
			// Update what the friends say
			yourHouseStrings
					.set(26,
							"Cheren: Hey, how come you get to pick\\xFFFEout my Pok\\x00E9mon?"
									+ "\\xF000\\xBE01\\x0000\\xFFFEOh, never mind. I wanted this one\\xFFFEfrom the start, anyway."
									+ "\\xF000\\xBE01\\x0000");
			yourHouseStrings
					.set(53,
							"It's decided. You'll be my opponent...\\xFFFEin our first Pok\\x00E9mon battle!"
									+ "\\xF000\\xBE01\\x0000\\xFFFELet's see what you can do, \\xFFFEmy Pok\\x00E9mon!"
									+ "\\xF000\\xBE01\\x0000");

			// rewrite
			setStrings(true, romEntry.getInt("StarterLocationTextOffset"),
					yourHouseStrings);
		} else {
			List<String> starterTownStrings = getStrings(true,
					romEntry.getInt("StarterLocationTextOffset"));
			for (int i = 0; i < 3; i++) {
				starterTownStrings.set(37 - i, "\\xF000\\xBD02\\x0000The "
						+ newStarters.get(i).primaryType.camelCase()
						+ "-type Pok\\x00E9mon\\xFFFE\\xF000\\xBD02\\x0000"
						+ newStarters.get(i).name);
			}
			// Update what the rival says
			starterTownStrings
					.set(60,
							"\\xF000\\x0100\\x0001\\x0001: Let's see how good\\xFFFEa Trainer you are!"
									+ "\\xF000\\xBE01\\x0000\\xFFFEI'll use my Pok\\x00E9mon"
									+ "\\xFFFEthat I raised from an Egg!\\xF000\\xBE01\\x0000");

			// rewrite
			setStrings(true, romEntry.getInt("StarterLocationTextOffset"),
					starterTownStrings);
		}
		return true;
	}

	@Override
	public List<Integer> getStarterHeldItems() {
		// do nothing
		return new ArrayList<Integer>();
	}

	@Override
	public void setStarterHeldItems(List<Integer> items) {
		// do nothing
	}

	private void replaceStarterFiles(NARCContents starterNARC,
			NARCContents pokespritesNARC, int starterIndex, int pokeNumber)
			throws IOException, InterruptedException {
		starterNARC.files.set(starterIndex * 2,
				pokespritesNARC.files.get(pokeNumber * 20 + 18));
		// Get the picture...
		byte[] compressedPic = pokespritesNARC.files.get(pokeNumber * 20);
		// Decompress it with JavaDSDecmp
		int[] ucp = JavaDSDecmp.Decompress(new HexInputStream(
				new ByteArrayInputStream(compressedPic)));
		byte[] uncompressedPic = convIntArrToByteArr(ucp);
		starterNARC.files.set(12 + starterIndex, uncompressedPic);
	}

	private byte[] convIntArrToByteArr(int[] arg) {
		byte[] out = new byte[arg.length];
		for (int i = 0; i < arg.length; i++) {
			out[i] = (byte) arg[i];
		}
		return out;
	}

	@Override
	public void shufflePokemonStats() {
		for (int i = 1; i <= 649; i++) {
			pokes[i].shuffleStats();
		}

	}

	@Override
	public List<Move> getMoves() {
		return Arrays.asList(moves);
	}

	@Override
	public List<EncounterSet> getEncounters(boolean useTimeOfDay) {
		try {
			NARCContents encounterNARC = readNARC(romEntry
					.getString("WildPokemon"));
			List<EncounterSet> encounters = new ArrayList<EncounterSet>();
			int idx = -1;
			for (byte[] entry : encounterNARC.files) {
				idx++;
				if (entry.length > 232 && useTimeOfDay) {
					for (int i = 0; i < 4; i++) {
						processEncounterEntry(encounters, entry, i * 232);
					}
				} else {
					processEncounterEntry(encounters, entry, 0);
				}
				for (EncounterSet es : encounters) {
					if (es.rate == -1) {
						es.rate = idx;
					}
				}
			}
			return encounters;
		} catch (IOException e) {
			// whuh-oh
			e.printStackTrace();
			return new ArrayList<EncounterSet>();
		}
	}

	private void processEncounterEntry(List<EncounterSet> encounters,
			byte[] entry, int startOffset) {
		int[] amounts = new int[] { 12, 12, 12, 5, 5, 5, 5 };

		int offset = 8;
		for (int i = 0; i < 7; i++) {
			int rate = entry[startOffset + i] & 0xFF;
			if (rate != 0) {
				List<Encounter> encs = readEncounters(entry, startOffset
						+ offset, amounts[i]);
				EncounterSet area = new EncounterSet();
				area.rate = -1;
				area.encounters = encs;
				encounters.add(area);
			}
			offset += amounts[i] * 4;
		}

	}

	private List<Encounter> readEncounters(byte[] data, int offset, int number) {
		List<Encounter> encs = new ArrayList<Encounter>();
		for (int i = 0; i < number; i++) {
			Encounter enc1 = new Encounter();
			enc1.pokemon = pokes[((data[offset + i * 4] & 0xFF) + ((data[offset
					+ 1 + i * 4] & 0x03) << 8))];
			enc1.level = data[offset + 2 + i * 4] & 0xFF;
			enc1.maxLevel = data[offset + 3 + i * 4] & 0xFF;
			encs.add(enc1);
		}
		return encs;
	}

	/* @formatter:off */
	@SuppressWarnings("unused")
	private static final int[][] habitatListEntries = new int[][] {
		{ 104, 105 }, // Route 4
		{ 124 }, // Route 15
		{ 134 }, // Route 21
		{ 84, 85, 86 }, // Clay Tunnel
		{ 23, 24, 25, 26 }, // Twist Mountain
		{ 97 }, // Village Bridge
		{ 27, 28, 29, 30 }, // Dragonspiral Tower
		{ 81, 82, 83 }, // Relic Passage
		{ 106 }, // Route 5*
		{ 125 }, // Route 16*
		{ 98 }, // Marvelous Bridge
		{ 123 }, // Abundant Shrine
		{ 132 }, // Undella Town
		{ 107 }, // Route 6
		{ 43 }, // Undella Bay
		{ 102, 103 }, // Wellspring Cave
		{ 95 }, // Nature Preserve
		{ 127 }, // Route 18
		{ 32, 33, 34, 35, 36 }, // Giant Chasm
		{ 111 }, // Route 7
		{ 31, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80 }, // Victory Road
		{ 12, 13, 14, 15, 16, 17, 18, 19 }, // Relic Castle
		{ 0 }, // Striation City
		{ 128 }, // Route 19
		{ 3 }, // Aspertia City
		{ 116 }, // Route 8*
		{ 44, 45 }, // Floccesy Ranch
		{ 61, 62, 63, 64, 65, 66, 67, 68, 69, 70 }, // Strange House
		{ 129 }, // Route 20
		{ 4 }, // Virbank City
		{ 37, 38, 39, 40, 41 }, // Castelia Sewers
		{ 118 }, // Route 9
		{ 46, 47 }, // Virbank Complex
		{ 42 }, // P2 Laboratory
		{ 1 }, // Castelia City
		{ 8, 9 }, // Pinwheel Forest
		{ 5 }, // Humilau City
		{ 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60 }, // Reversal Mountain
		{ 6, 7 }, // Dreamyard
		{ 112, 113, 114, 115 }, // Celestial Tower
		{ 130 }, // Route 22
		{ 10, 11 }, // Desert Resort
		{ 119 }, // Route 11
		{ 133 }, // Route 17
		{ 99 }, // Route 1
		{ 131 }, // Route 23
		{ 2 }, // Icirrus City*
		{ 120 }, // Route 12
		{ 100 }, // Route 2
		{ 108, 109 }, // Mistralton Cave
		{ 121 }, // Route 13
		{ 101 }, // Route 3
		{ 117 }, // Moor of Icirrus*
		{ 96 }, // Driftveil Drawbridge
		{ 93, 94 }, // Seaside Cave
		{ 126 }, // Lostlorn Forest
		{ 122 }, // Route 14
		{ 20, 21, 22 }, // Chargestone Cave
	};
	
	private static final int[] wildFileToAreaMap = new int[] {
		2,
		4,
		8,
		59,
		61,
		63,
		19, 19,
		20, 20,
		21, 21,
		22, 22, 22, 22, 22, 22, 22, 22,
		24, 24, 24,
		25, 25, 25, 25,
		26, 26, 26, 26,
		76,
		27, 27, 27, 27, 27,
		70, 70, 70, 70, 70,
		29,
		35,
		71, 71,
		72, 72,
		73, 73, 73, 73, 73, 73, 73, 73, 73, 73, 73, 73, 73,
		74, 74, 74, 74, 74, 74, 74, 74, 74, 74,
		76, 76, 76, 76, 76, 76, 76, 76, 76, 76,
		77, 77, 77,
		79, 79, 79, 79, 79, 79, 79, 79, 79,
		78, 78,
		-1, // Nature Preserve (not on map)
		55,
		57,
		58,
		37,
		38,
		39,
		30, 30,
		40, 40,
		41,
		42,
		31, 31, 31,
		43,
		32, 32, 32, 32,
		44,
		33,
		45,
		46,
		47,
		48,
		49,
		34,
		50,
		51,
		36,
		53,
		66,
		67,
		69,
		75,
		12,
		52,
		68,
	};
	/* @formatter:on */

	@Override
	public void setEncounters(boolean useTimeOfDay,
			List<EncounterSet> encountersList) {
		try {
			NARCContents encounterNARC = readNARC(romEntry
					.getString("WildPokemon"));
			Iterator<EncounterSet> encounters = encountersList.iterator();
			for (byte[] entry : encounterNARC.files) {
				writeEncounterEntry(encounters, entry, 0);
				if (entry.length > 232) {
					if (useTimeOfDay) {
						for (int i = 1; i < 4; i++) {
							writeEncounterEntry(encounters, entry, i * 232);
						}
					} else {
						// copy for other 3 seasons
						System.arraycopy(entry, 0, entry, 232, 232);
						System.arraycopy(entry, 0, entry, 464, 232);
						System.arraycopy(entry, 0, entry, 696, 232);
					}
				}
			}

			// Save
			writeNARC(romEntry.getString("WildPokemon"), encounterNARC);

			// Habitat List / Area Data?
			if (romEntry.romType == Type_BW2) {
				// disabled: habitat list changes cause a crash if too many
				// entries for now.

				// NARCContents habitatNARC = readNARC(romEntry
				// .getString("HabitatList"));
				// for (int i = 0; i < habitatNARC.files.size(); i++) {
				// byte[] oldEntry = habitatNARC.files.get(i);
				// int[] encounterFiles = habitatListEntries[i];
				// Map<Pokemon, byte[]> pokemonHere = new TreeMap<Pokemon,
				// byte[]>();
				// for (int encFile : encounterFiles) {
				// byte[] encEntry = encounterNARC.files.get(encFile);
				// if (encEntry.length > 232) {
				// for (int s = 0; s < 4; s++) {
				// addHabitats(encEntry, s * 232, pokemonHere, s);
				// }
				// } else {
				// for (int s = 0; s < 4; s++) {
				// addHabitats(encEntry, 0, pokemonHere, s);
				// }
				// }
				// }
				// // Make the new file
				// byte[] habitatEntry = new byte[10 + pokemonHere.size() * 28];
				// System.arraycopy(oldEntry, 0, habitatEntry, 0, 10);
				// habitatEntry[8] = (byte) pokemonHere.size();
				// // 28-byte entries for each pokemon
				// int num = -1;
				// for (Pokemon pkmn : pokemonHere.keySet()) {
				// num++;
				// writeWord(habitatEntry, 10 + num * 28, pkmn.number);
				// byte[] slots = pokemonHere.get(pkmn);
				// System.arraycopy(slots, 0, habitatEntry, 12 + num * 28,
				// 12);
				// }
				// // Save
				// habitatNARC.files.set(i, habitatEntry);
				// }
				// // Save habitat
				// this.writeNARC(romEntry.getString("HabitatList"),
				// habitatNARC);

				// Area Data
				NARCContents areaNARC = this.readNARC(romEntry
						.getString("PokemonAreaData"));
				List<byte[]> newFiles = new ArrayList<byte[]>();
				for (int i = 0; i < 649; i++) {
					byte[] nf = new byte[345];
					nf[0] = 1;
					newFiles.add(nf);
				}
				// Get data now
				for (int i = 0; i < encounterNARC.files.size(); i++) {
					byte[] encEntry = encounterNARC.files.get(i);
					if (encEntry.length > 232) {
						for (int s = 0; s < 4; s++) {
							parseAreaData(encEntry, s * 232, newFiles, s, i);
						}
					} else {
						for (int s = 0; s < 4; s++) {
							parseAreaData(encEntry, 0, newFiles, s, i);
						}
					}
				}
				// Now update unobtainables & save
				for (int i = 0; i < 649; i++) {
					byte[] file = newFiles.get(i);
					for (int s = 0; s < 4; s++) {
						boolean unobtainable = true;
						for (int e = 0; e < 85; e++) {
							if (file[s * 86 + e + 2] != 0) {
								unobtainable = false;
								break;
							}
						}
						if (unobtainable) {
							file[s * 86 + 1] = 1;
						}
					}
					areaNARC.files.set(i, file);
				}
				// Save
				this.writeNARC(romEntry.getString("PokemonAreaData"), areaNARC);
			}
		} catch (IOException e) {
			// whuh-oh
			e.printStackTrace();
		}

	}

	private void parseAreaData(byte[] entry, int startOffset,
			List<byte[]> areaData, int season, int fileNumber) {
		int[] amounts = new int[] { 12, 12, 12, 5, 5, 5, 5 };

		int offset = 8;
		for (int i = 0; i < 7; i++) {
			int rate = entry[startOffset + i] & 0xFF;
			if (rate != 0) {
				for (int e = 0; e < amounts[i]; e++) {
					Pokemon pkmn = pokes[((entry[startOffset + offset + e * 4] & 0xFF) + ((entry[startOffset
							+ offset + 1 + e * 4] & 0x03) << 8))];
					byte[] pokeFile = areaData.get(pkmn.number - 1);
					int areaIndex = wildFileToAreaMap[fileNumber];
					// Route 4?
					if (areaIndex == 40) {
						if ((fileNumber == 104 && romEntry.romCode.charAt(2) == 'D')
								|| (fileNumber == 105 && romEntry.romCode
										.charAt(2) == 'E')) {
							areaIndex = -1; // wrong version
						}
					}
					// Victory Road?
					if (areaIndex == 76) {
						if (romEntry.romCode.charAt(2) == 'D') {
							// White 2
							if (fileNumber == 71 || fileNumber == 73) {
								areaIndex = -1; // wrong version
							}
						} else {
							// Black 2
							if (fileNumber == 78 || fileNumber == 79) {
								areaIndex = -1; // wrong version
							}
						}
					}
					// Reversal Mountain?
					if (areaIndex == 73) {
						if (romEntry.romCode.charAt(2) == 'D') {
							// White 2
							if (fileNumber >= 49 && fileNumber <= 54) {
								areaIndex = -1; // wrong version
							}
						} else {
							// Black 2
							if (fileNumber >= 55 && fileNumber <= 60) {
								areaIndex = -1; // wrong version
							}
						}
					}
					// Skip stuff that isn't on the map or is wrong version
					if (areaIndex != -1) {
						pokeFile[season * 86 + 2 + areaIndex] |= (1 << i);
					}
				}
			}
			offset += amounts[i] * 4;
		}
	}

	@SuppressWarnings("unused")
	private void addHabitats(byte[] entry, int startOffset,
			Map<Pokemon, byte[]> pokemonHere, int season) {
		int[] amounts = new int[] { 12, 12, 12, 5, 5, 5, 5 };
		int[] type = new int[] { 0, 0, 0, 1, 1, 2, 2 };

		int offset = 8;
		for (int i = 0; i < 7; i++) {
			int rate = entry[startOffset + i] & 0xFF;
			if (rate != 0) {
				for (int e = 0; e < amounts[i]; e++) {
					Pokemon pkmn = pokes[((entry[startOffset + offset + e * 4] & 0xFF) + ((entry[startOffset
							+ offset + 1 + e * 4] & 0x03) << 8))];
					if (pokemonHere.containsKey(pkmn)) {
						pokemonHere.get(pkmn)[type[i] + season * 3] = 1;
					} else {
						byte[] locs = new byte[12];
						locs[type[i] + season * 3] = 1;
						pokemonHere.put(pkmn, locs);
					}
				}
			}
			offset += amounts[i] * 4;
		}
	}

	private void writeEncounterEntry(Iterator<EncounterSet> encounters,
			byte[] entry, int startOffset) {
		int[] amounts = new int[] { 12, 12, 12, 5, 5, 5, 5 };

		int offset = 8;
		for (int i = 0; i < 7; i++) {
			int rate = entry[startOffset + i] & 0xFF;
			if (rate != 0) {
				EncounterSet area = encounters.next();
				for (int j = 0; j < amounts[i]; j++) {
					Encounter enc = area.encounters.get(j);
					writeWord(entry, startOffset + offset + j * 4,
							enc.pokemon.number);
					entry[startOffset + offset + j * 4 + 2] = (byte) enc.level;
					entry[startOffset + offset + j * 4 + 3] = (byte) enc.maxLevel;
				}
			}
			offset += amounts[i] * 4;
		}
	}

	@Override
	public List<Trainer> getTrainers() {
		List<Trainer> allTrainers = new ArrayList<Trainer>();
		try {
			NARCContents trainers = this.readNARC(romEntry
					.getString("TrainerData"));
			NARCContents trpokes = this.readNARC(romEntry
					.getString("TrainerPokemon"));
			int trainernum = trainers.files.size();
			List<String> tclasses = this.getTrainerClassNames();
			List<String> tnames = this.getTrainerNames();
			for (int i = 1; i < trainernum; i++) {
				byte[] trainer = trainers.files.get(i);
				byte[] trpoke = trpokes.files.get(i);
				Trainer tr = new Trainer();
				tr.poketype = trainer[0] & 0xFF;
				tr.offset = trainer[1] & 0xFF;
				tr.trainerclass = trainer[1] & 0xFF;
				int numPokes = trainer[3] & 0xFF;
				int pokeOffs = 0;
				tr.fullDisplayName = tclasses.get(tr.trainerclass) + " "
						+ tnames.get(i - 1);
				// printBA(trpoke);
				for (int poke = 0; poke < numPokes; poke++) {
					// Structure is
					// AI SB LV LV SP SP FRM FRM
					// (HI HI)
					// (M1 M1 M2 M2 M3 M3 M4 M4)
					// where SB = 0 0 Ab Ab 0 0 Fm Ml
					// Ab Ab = ability number, 0 for random
					// Fm = 1 for forced female
					// Ml = 1 for forced male
					// There's also a trainer flag to force gender, but
					// this allows fixed teams with mixed genders.

					int ailevel = trpoke[pokeOffs] & 0xFF;
					// int secondbyte = trpoke[pokeOffs + 1] & 0xFF;
					int level = readWord(trpoke, pokeOffs + 2);
					int species = readWord(trpoke, pokeOffs + 4);
					// int formnum = readWord(trpoke, pokeOffs + 6);
					TrainerPokemon tpk = new TrainerPokemon();
					tpk.level = level;
					tpk.pokemon = pokes[species];
					tpk.AILevel = ailevel;
					tpk.ability = trpoke[pokeOffs + 1] & 0xFF;
					pokeOffs += 8;
					if (tr.poketype >= 2) {
						int heldItem = readWord(trpoke, pokeOffs);
						tpk.heldItem = heldItem;
						pokeOffs += 2;
					}
					if (tr.poketype % 2 == 1) {
						int attack1 = readWord(trpoke, pokeOffs);
						int attack2 = readWord(trpoke, pokeOffs + 2);
						int attack3 = readWord(trpoke, pokeOffs + 4);
						int attack4 = readWord(trpoke, pokeOffs + 6);
						tpk.move1 = attack1;
						tpk.move2 = attack2;
						tpk.move3 = attack3;
						tpk.move4 = attack4;
						pokeOffs += 8;
					}
					tr.pokemon.add(tpk);
				}
				allTrainers.add(tr);
			}
			if (romEntry.romType == Type_BW) {
				tagTrainersBW(allTrainers);
			} else {
				if (!romEntry.getString("DriftveilPokemon").isEmpty()) {
					NARCContents driftveil = this.readNARC(romEntry
							.getString("DriftveilPokemon"));
					for (int trno = 0; trno < 2; trno++) {
						Trainer tr = new Trainer();
						tr.poketype = 3;
						tr.offset = 0;
						for (int poke = 0; poke < 3; poke++) {
							byte[] pkmndata = driftveil.files.get(trno * 3
									+ poke + 1);
							TrainerPokemon tpk = new TrainerPokemon();
							tpk.level = 25;
							tpk.pokemon = pokes[readWord(pkmndata, 0)];
							tpk.AILevel = 255;
							tpk.heldItem = readWord(pkmndata, 12);
							tpk.move1 = readWord(pkmndata, 2);
							tpk.move2 = readWord(pkmndata, 2);
							tpk.move3 = readWord(pkmndata, 2);
							tpk.move4 = readWord(pkmndata, 2);
							tr.pokemon.add(tpk);
						}
						allTrainers.add(tr);
					}
				}
				tagTrainersBW2(allTrainers);
			}
		} catch (IOException ex) {
			// change this later
			ex.printStackTrace();
		}
		return allTrainers;
	}

	private void tagTrainersBW(List<Trainer> trs) {
		// We use different Gym IDs to cheat the system for the 3 n00bs
		// Chili, Cress, and Cilan
		// Cilan can be GYM1, then Chili is GYM9 and Cress GYM10
		// Also their *trainers* are GYM11 lol

		// Gym Trainers
		tag(trs, "GYM11", 0x09, 0x0A);
		tag(trs, "GYM2", 0x56, 0x57, 0x58);
		tag(trs, "GYM3", 0xC4, 0xC6, 0xC7, 0xC8);
		tag(trs, "GYM4", 0x42, 0x43, 0x44, 0x45);
		tag(trs, "GYM5", 0xC9, 0xCA, 0xCB, 0x5F, 0xA8);
		tag(trs, "GYM6", 0x7D, 0x7F, 0x80, 0x46, 0x47);
		tag(trs, "GYM7", 0xD7, 0xD8, 0xD9, 0xD4, 0xD5, 0xD6);
		tag(trs, "GYM8", 0x109, 0x10A, 0x10F, 0x10E, 0x110, 0x10B, 0x113, 0x112);

		// Gym Leaders
		tag(trs, 0x0C, "GYM1"); // Cilan
		tag(trs, 0x0B, "GYM9"); // Chili
		tag(trs, 0x0D, "GYM10"); // Cress
		tag(trs, 0x15, "GYM2"); // Lenora
		tag(trs, 0x16, "GYM3"); // Burgh
		tag(trs, 0x17, "GYM4"); // Elesa
		tag(trs, 0x18, "GYM5"); // Clay
		tag(trs, 0x19, "GYM6"); // Skyla
		tag(trs, 0x83, "GYM7"); // Brycen
		tag(trs, 0x84, "GYM8"); // Iris or Drayden
		tag(trs, 0x85, "GYM8"); // Iris or Drayden

		// Elite 4
		tag(trs, 0xE4, "ELITE1"); // Shauntal
		tag(trs, 0xE6, "ELITE2"); // Grimsley
		tag(trs, 0xE7, "ELITE3"); // Caitlin
		tag(trs, 0xE5, "ELITE4"); // Marshal

		// Elite 4 R2
		tag(trs, 0x233, "ELITE1"); // Shauntal
		tag(trs, 0x235, "ELITE2"); // Grimsley
		tag(trs, 0x236, "ELITE3"); // Caitlin
		tag(trs, 0x234, "ELITE4"); // Marshal
		tag(trs, 0x197, "CHAMPION"); // Alder

		// Ubers?
		tag(trs, 0x21E, "UBER"); // Game Freak Guy
		tag(trs, 0x237, "UBER"); // Cynthia
		tag(trs, 0xE8, "UBER"); // Ghetsis
		tag(trs, 0x24A, "UBER"); // N-White
		tag(trs, 0x24B, "UBER"); // N-Black

		// Rival - Cheren
		tagRivalBW(trs, "RIVAL1", 0x35);
		tagRivalBW(trs, "RIVAL2", 0x11F);
		tagRivalBW(trs, "RIVAL3", 0x38); // used for 3rd battle AND tag battle
		tagRivalBW(trs, "RIVAL4", 0x193);
		tagRivalBW(trs, "RIVAL5", 0x5A); // 5th battle & 2nd tag battle
		tagRivalBW(trs, "RIVAL6", 0x21B);
		tagRivalBW(trs, "RIVAL7", 0x24C);
		tagRivalBW(trs, "RIVAL8", 0x24F);

		// Rival - Bianca
		tagRivalBW(trs, "FRIEND1", 0x3B);
		tagRivalBW(trs, "FRIEND2", 0x1F2);
		tagRivalBW(trs, "FRIEND3", 0x1FB);
		tagRivalBW(trs, "FRIEND4", 0x1EB);
		tagRivalBW(trs, "FRIEND5", 0x1EE);
		tagRivalBW(trs, "FRIEND6", 0x252);
	}

	private void tagTrainersBW2(List<Trainer> trs) {
		// Use GYM9/10/11 for the retired Chili/Cress/Cilan.
		// Lenora doesn't have a team, or she'd be 12.
		// Likewise for Brycen

		// Some trainers have TWO teams because of Challenge Mode
		// I believe this is limited to Gym Leaders, E4, Champ...
		// The "Challenge Mode" teams have levels at similar to regular,
		// but have the normal boost applied too.

		// Gym Trainers
		tag(trs, "GYM1", 0xab, 0xac);
		tag(trs, "GYM2", 0xb2, 0xb3);
		tag(trs, "GYM3", 0x2de, 0x2df, 0x2e0, 0x2e1);
		// GYM4: old gym site included to give the city a theme
		tag(trs, "GYM4", 0x26d, 0x94, 0xcf, 0xd0, 0xd1); // 0x94 might be 0x324
		tag(trs, "GYM5", 0x13f, 0x140, 0x141, 0x142, 0x143, 0x144, 0x145);
		tag(trs, "GYM6", 0x95, 0x96, 0x97, 0x98, 0x14c);
		tag(trs, "GYM7", 0x17d, 0x17e, 0x17f, 0x180, 0x181);
		tag(trs, "GYM8", 0x15e, 0x15f, 0x160, 0x161, 0x162, 0x163);

		// Gym Leaders
		// Order: Normal, Challenge Mode
		// All the challenge mode teams are near the end of the ROM
		// which makes things a bit easier.
		tag(trs, "GYM1", 0x9c, 0x2fc); // Cheren
		tag(trs, "GYM2", 0x9d, 0x2fd); // Roxie
		tag(trs, "GYM3", 0x9a, 0x2fe); // Burgh
		tag(trs, "GYM4", 0x99, 0x2ff); // Elesa
		tag(trs, "GYM5", 0x9e, 0x300); // Clay
		tag(trs, "GYM6", 0x9b, 0x301); // Skyla
		tag(trs, "GYM7", 0x9f, 0x302); // Drayden
		tag(trs, "GYM8", 0xa0, 0x303); // Marlon

		// Elite 4 / Champion
		// Order: Normal, Challenge Mode, Rematch, Rematch Challenge Mode
		tag(trs, "ELITE1", 0x26, 0x304, 0x8f, 0x309);
		tag(trs, "ELITE2", 0x28, 0x305, 0x91, 0x30a);
		tag(trs, "ELITE3", 0x29, 0x307, 0x92, 0x30c);
		tag(trs, "ELITE4", 0x27, 0x306, 0x90, 0x30b);
		tag(trs, "CHAMPION", 0x155, 0x308, 0x218, 0x30d);

		// Rival - Hugh
		tagRivalBW(trs, "RIVAL1", 0xa1); // Start
		tagRivalBW(trs, "RIVAL2", 0xa6); // Floccessy Ranch
		tagRivalBW(trs, "RIVAL3", 0x24c); // Tag Battles in the sewers
		tagRivalBW(trs, "RIVAL4", 0x170); // Tag Battle on the Plasma Frigate
		tagRivalBW(trs, "RIVAL5", 0x17a); // Undella Town 1st visit
		tagRivalBW(trs, "RIVAL6", 0x2bd); // Lacunosa Town Tag Battle
		tagRivalBW(trs, "RIVAL7", 0x31a); // 2nd Plasma Frigate Tag Battle
		tagRivalBW(trs, "RIVAL8", 0x2ac); // Victory Road
		tagRivalBW(trs, "RIVAL9", 0x2b5); // Undella Town Post-E4
		tagRivalBW(trs, "RIVAL10", 0x2b8); // Driftveil Post-Undella-Battle

		// Tag Battle with Opposite Gender Hero
		tagRivalBW(trs, "FRIEND1", 0x168);
		tagRivalBW(trs, "FRIEND1", 0x16b);

		// Tag/PWT Battles with Cheren
		tag(trs, "GYM1", 0x173, 0x278, 0x32E);

		// The Restaurant Brothers
		tag(trs, "GYM9", 0x1f0); // Cilan
		tag(trs, "GYM10", 0x1ee); // Chili
		tag(trs, "GYM11", 0x1ef); // Cress

		// Themed Trainers
		tag(trs, "THEMED:ZINZOLIN", 0x2c0, 0x248, 0x15b);
		tag(trs, "THEMED:COLRESS", 0x166, 0x158, 0x32d, 0x32f);
		tag(trs, "THEMED:SHADOW1", 0x247, 0x15c, 0x2af);
		tag(trs, "THEMED:SHADOW2", 0x1f2, 0x2b0);
		tag(trs, "THEMED:SHADOW3", 0x1f3, 0x2b1);

		// Uber-Trainers
		// There are *fourteen* ubers of 17 allowed (incl. the champion)
		// It's a rather stacked game...
		tag(trs, 0x246, "UBER"); // Alder
		tag(trs, 0x1c8, "UBER"); // Cynthia
		tag(trs, 0xca, "UBER"); // Benga/BlackTower
		tag(trs, 0xc9, "UBER"); // Benga/WhiteTreehollow
		tag(trs, 0x5, "UBER"); // N/Zekrom
		tag(trs, 0x6, "UBER"); // N/Reshiram
		tag(trs, 0x30e, "UBER"); // N/Spring
		tag(trs, 0x30f, "UBER"); // N/Summer
		tag(trs, 0x310, "UBER"); // N/Autumn
		tag(trs, 0x311, "UBER"); // N/Winter
		tag(trs, 0x159, "UBER"); // Ghetsis
		tag(trs, 0x8c, "UBER"); // Game Freak Guy
		tag(trs, 0x24f, "UBER"); // Game Freak Leftovers Guy

	}

	private void tagRivalBW(List<Trainer> allTrainers, String tag, int offset) {
		allTrainers.get(offset - 1).tag = tag + "-0";
		allTrainers.get(offset).tag = tag + "-1";
		allTrainers.get(offset + 1).tag = tag + "-2";

	}

	private void tag(List<Trainer> allTrainers, int number, String tag) {
		if (allTrainers.size() > (number - 1)) {
			allTrainers.get(number - 1).tag = tag;
		}
	}

	private void tag(List<Trainer> allTrainers, String tag, int... numbers) {
		for (int num : numbers) {
			if (allTrainers.size() > (num - 1)) {
				allTrainers.get(num - 1).tag = tag;
			}
		}
	}

	@Override
	public void setTrainers(List<Trainer> trainerData) {
		Iterator<Trainer> allTrainers = trainerData.iterator();
		try {
			NARCContents trainers = this.readNARC(romEntry
					.getString("TrainerData"));
			NARCContents trpokes = new NARCContents();
			// empty entry
			trpokes.files.add(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 });
			int trainernum = trainers.files.size();
			for (int i = 1; i < trainernum; i++) {
				byte[] trainer = trainers.files.get(i);
				Trainer tr = allTrainers.next();
				tr.poketype = 0; // write as type 0 for no item/moves
				trainer[0] = (byte) tr.poketype;
				int numPokes = tr.pokemon.size();
				trainer[3] = (byte) numPokes;

				int bytesNeeded = 8 * numPokes;
				if (tr.poketype % 2 == 1) {
					bytesNeeded += 8 * numPokes;
				}
				if (tr.poketype >= 2) {
					bytesNeeded += 2 * numPokes;
				}
				byte[] trpoke = new byte[bytesNeeded];
				int pokeOffs = 0;
				Iterator<TrainerPokemon> tpokes = tr.pokemon.iterator();
				for (int poke = 0; poke < numPokes; poke++) {
					TrainerPokemon tpk = tpokes.next();
					trpoke[pokeOffs] = (byte) tpk.AILevel;
					// no gender or ability info, so no byte 1
					writeWord(trpoke, pokeOffs + 2, tpk.level);
					writeWord(trpoke, pokeOffs + 4, tpk.pokemon.number);
					// no form info, so no byte 6/7
					pokeOffs += 8;
					if (tr.poketype >= 2) {
						writeWord(trpoke, pokeOffs, tpk.heldItem);
						pokeOffs += 2;
					}
					if (tr.poketype % 2 == 1) {
						writeWord(trpoke, pokeOffs, tpk.move1);
						writeWord(trpoke, pokeOffs + 2, tpk.move2);
						writeWord(trpoke, pokeOffs + 4, tpk.move3);
						writeWord(trpoke, pokeOffs + 6, tpk.move4);
						pokeOffs += 8;
					}
				}
				trpokes.files.add(trpoke);
			}
			this.writeNARC(romEntry.getString("TrainerData"), trainers);
			this.writeNARC(romEntry.getString("TrainerPokemon"), trpokes);
			// Deal with PWT
			if (romEntry.romType == Type_BW2
					&& !romEntry.getString("DriftveilPokemon").isEmpty()) {
				NARCContents driftveil = this.readNARC(romEntry
						.getString("DriftveilPokemon"));
				Map<Pokemon, List<MoveLearnt>> movesets = this.getMovesLearnt();
				for (int trno = 0; trno < 2; trno++) {
					Trainer tr = allTrainers.next();
					Iterator<TrainerPokemon> tpks = tr.pokemon.iterator();
					for (int poke = 0; poke < 3; poke++) {
						byte[] pkmndata = driftveil.files.get(trno * 3 + poke
								+ 1);
						TrainerPokemon tpk = tpks.next();
						// pokemon and held item
						writeWord(pkmndata, 0, tpk.pokemon.number);
						writeWord(pkmndata, 12, tpk.heldItem);
						// pick 4 moves, based on moveset@25
						int[] moves = new int[4];
						int moveCount = 0;
						List<MoveLearnt> set = movesets.get(tpk.pokemon);
						for (int i = 0; i < set.size(); i++) {
							MoveLearnt ml = set.get(i);
							if (ml.level > 25) {
								break;
							}
							// unconditional learn?
							if (moveCount < 4) {
								moves[moveCount++] = ml.move;
							} else {
								// already knows?
								boolean doTeach = true;
								for (int j = 0; j < 4; j++) {
									if (moves[j] == ml.move) {
										doTeach = false;
										break;
									}
								}
								if (doTeach) {
									// shift up
									for (int j = 0; j < 3; j++) {
										moves[j] = moves[j + 1];
									}
									moves[3] = ml.move;
								}
							}
						}
						// write the moveset we calculated
						for (int i = 0; i < 4; i++) {
							writeWord(pkmndata, 2 + i * 2, moves[i]);
						}
					}
				}
				this.writeNARC(romEntry.getString("DriftveilPokemon"),
						driftveil);
			}
		} catch (IOException ex) {
			// change this later
			ex.printStackTrace();
		}
	}

	@Override
	public Map<Pokemon, List<MoveLearnt>> getMovesLearnt() {
		Map<Pokemon, List<MoveLearnt>> movesets = new TreeMap<Pokemon, List<MoveLearnt>>();
		try {
			NARCContents movesLearnt = this.readNARC(romEntry
					.getString("PokemonMovesets"));
			for (int i = 1; i <= 649; i++) {
				Pokemon pkmn = pokes[i];
				byte[] movedata = movesLearnt.files.get(i);
				int moveDataLoc = 0;
				List<MoveLearnt> learnt = new ArrayList<MoveLearnt>();
				while (readWord(movedata, moveDataLoc) != 0xFFFF
						|| readWord(movedata, moveDataLoc + 2) != 0xFFFF) {
					int move = readWord(movedata, moveDataLoc);
					int level = readWord(movedata, moveDataLoc + 2);
					MoveLearnt ml = new MoveLearnt();
					ml.level = level;
					ml.move = move;
					learnt.add(ml);
					moveDataLoc += 4;
				}
				movesets.put(pkmn, learnt);
			}
		} catch (IOException e) {
			// change this later
			e.printStackTrace();
		}
		return movesets;
	}

	@Override
	public void setMovesLearnt(Map<Pokemon, List<MoveLearnt>> movesets) {
		// Backup of movesets for later
		Map<Pokemon, List<MoveLearnt>> oldSets = this.getMovesLearnt();
		try {
			NARCContents movesLearnt = readNARC(romEntry
					.getString("PokemonMovesets"));
			for (int i = 1; i <= 649; i++) {
				Pokemon pkmn = pokes[i];
				List<MoveLearnt> learnt = movesets.get(pkmn);
				int sizeNeeded = learnt.size() * 4 + 4;
				byte[] moveset = new byte[sizeNeeded];
				int j = 0;
				for (; j < learnt.size(); j++) {
					MoveLearnt ml = learnt.get(j);
					writeWord(moveset, j * 4, ml.move);
					writeWord(moveset, j * 4 + 2, ml.level);
				}
				writeWord(moveset, j * 4, 0xFFFF);
				writeWord(moveset, j * 4 + 2, 0xFFFF);
				movesLearnt.files.set(i, moveset);
			}
			// Save
			this.writeNARC(romEntry.getString("PokemonMovesets"), movesLearnt);
		} catch (IOException e) {
			// change this later
			e.printStackTrace();
		}

	}

	private static class StaticPokemon {
		private int[] files;
		private int[] offsets;

		public Pokemon getPokemon(Gen5RomHandler parent, NARCContents scriptNARC) {
			return parent.pokes[parent.readWord(scriptNARC.files.get(files[0]),
					offsets[0])];
		}

		public void setPokemon(Gen5RomHandler parent, NARCContents scriptNARC,
				Pokemon pkmn) {
			int value = pkmn.number;
			for (int i = 0; i < offsets.length; i++) {
				byte[] file = scriptNARC.files.get(files[i]);
				parent.writeWord(file, offsets[i], value);
			}
		}
	}

	@Override
	public boolean canChangeStaticPokemon() {
		return romEntry.staticPokemonSupport;
	}

	@Override
	public List<Pokemon> getStaticPokemon() {
		List<Pokemon> sp = new ArrayList<Pokemon>();
		if (!romEntry.staticPokemonSupport) {
			return sp;
		}
		NARCContents scriptNARC = scriptNarc;
		for (StaticPokemon statP : romEntry.staticPokemon) {
			sp.add(statP.getPokemon(this, scriptNARC));
		}
		return sp;
	}

	@Override
	public boolean setStaticPokemon(List<Pokemon> staticPokemon) {
		if (!romEntry.staticPokemonSupport) {
			return false;
		}
		if (staticPokemon.size() != romEntry.staticPokemon.size()) {
			return false;
		}
		Iterator<Pokemon> statics = staticPokemon.iterator();
		NARCContents scriptNARC = scriptNarc;
		for (StaticPokemon statP : romEntry.staticPokemon) {
			statP.setPokemon(this, scriptNARC, statics.next());
		}
		if (romEntry.offsetArrayEntries.containsKey("StaticPokemonFormValues")) {
			OffsetWithinEntry[] formValues = romEntry.offsetArrayEntries
					.get("StaticPokemonFormValues");
			for (OffsetWithinEntry owe : formValues) {
				writeWord(scriptNARC.files.get(owe.entry), owe.offset, 0);
			}
		}

		return true;
	}

	@Override
	public boolean hasHiddenHollowPokemon() {
		return romEntry.romType == Type_BW2;
	}

	@Override
	public void randomizeHiddenHollowPokemon() {
		if (romEntry.romType != Type_BW2) {
			return;
		}
		int[] allowedUnovaPokemon = new int[] { 505, 507, 510, 511, 513, 515,
				519, 523, 525, 527, 529, 531, 533, 535, 538, 539, 542, 545,
				546, 548, 550, 553, 556, 558, 559, 561, 564, 569, 572, 575,
				578, 580, 583, 587, 588, 594, 596, 601, 605, 607, 610, 613,
				616, 618, 619, 621, 622, 624, 626, 628, 630, 631, 632, };
		int randomSize = 493 + allowedUnovaPokemon.length;
		try {
			NARCContents hhNARC = this.readNARC(romEntry
					.getString("HiddenHollows"));
			for (byte[] hhEntry : hhNARC.files) {
				for (int version = 0; version < 2; version++) {
					for (int rarityslot = 0; rarityslot < 3; rarityslot++) {
						for (int group = 0; group < 4; group++) {
							int pokeChoice = RandomSource.nextInt(randomSize) + 1;
							if (pokeChoice > 493) {
								pokeChoice = allowedUnovaPokemon[pokeChoice - 494];
							}
							writeWord(hhEntry, version * 78 + rarityslot * 26
									+ group * 2, pokeChoice);
							int genderRatio = RandomSource.nextInt(101);
							hhEntry[version * 78 + rarityslot * 26 + 16 + group] = (byte) genderRatio;
							hhEntry[version * 78 + rarityslot * 26 + 20 + group] = 0; // forme
						}
					}
				}
				// the rest of the file is items
			}
			this.writeNARC(romEntry.getString("HiddenHollows"), hhNARC);
		} catch (IOException e) {
		}
	}

	@Override
	public List<Integer> getTMMoves() {
		String tmDataPrefix = "87038803";
		int offset = find(arm9, tmDataPrefix);
		if (offset > 0) {
			offset += 4; // because it was a prefix
			List<Integer> tms = new ArrayList<Integer>();
			for (int i = 0; i < 92; i++) {
				tms.add(readWord(arm9, offset + i * 2));
			}
			// Skip past first 92 TMs and 6 HMs
			offset += 196;
			for (int i = 0; i < 3; i++) {
				tms.add(readWord(arm9, offset + i * 2));
			}
			return tms;
		} else {
			return null;
		}
	}

	@Override
	public List<Integer> getHMMoves() {
		String tmDataPrefix = "87038803";
		int offset = find(arm9, tmDataPrefix);
		if (offset > 0) {
			offset += 4; // because it was a prefix
			offset += 184; // TM data
			List<Integer> hms = new ArrayList<Integer>();
			for (int i = 0; i < 6; i++) {
				hms.add(readWord(arm9, offset + i * 2));
			}
			return hms;
		} else {
			return null;
		}
	}

	@Override
	public void setTMMoves(List<Integer> moveIndexes) {
		String tmDataPrefix = "87038803";
		int offset = find(arm9, tmDataPrefix);
		if (offset > 0) {
			offset += 4; // because it was a prefix
			for (int i = 0; i < 92; i++) {
				writeWord(arm9, offset + i * 2, moveIndexes.get(i));
			}
			// Skip past those 92 TMs and 6 HMs
			offset += 196;
			for (int i = 0; i < 3; i++) {
				writeWord(arm9, offset + i * 2, moveIndexes.get(i + 92));
			}

			// Update TM item descriptions
			List<String> itemDescriptions = getStrings(false,
					romEntry.getInt("ItemDescriptionsTextOffset"));
			List<String> moveDescriptions = getStrings(false,
					romEntry.getInt("MoveDescriptionsTextOffset"));
			// TM01 is item 328 and so on
			for (int i = 0; i < 92; i++) {
				itemDescriptions.set(i + 328,
						moveDescriptions.get(moveIndexes.get(i)));
			}
			// TM93-95 are 618-620
			for (int i = 0; i < 3; i++) {
				itemDescriptions.set(i + 618,
						moveDescriptions.get(moveIndexes.get(i + 92)));
			}
			// Save the new item descriptions
			setStrings(false, romEntry.getInt("ItemDescriptionsTextOffset"),
					itemDescriptions);
			// Palettes
			String baseOfPalettes;
			if (romEntry.romType == Type_BW) {
				baseOfPalettes = "E903EA03020003000400050006000700";
			} else {
				baseOfPalettes = "FD03FE03020003000400050006000700";
			}
			int offsPals = find(arm9, baseOfPalettes);
			if (offsPals > 0) {
				// Write pals
				for (int i = 0; i < 92; i++) {
					int itmNum = 328 + i;
					Move m = this.moves[moveIndexes.get(i)];
					int pal = this.typeTMPaletteNumber(m.type);
					writeWord(arm9, offsPals + itmNum * 4 + 2, pal);
				}
				for (int i = 0; i < 3; i++) {
					int itmNum = 618 + i;
					Move m = this.moves[moveIndexes.get(i + 92)];
					int pal = this.typeTMPaletteNumber(m.type);
					writeWord(arm9, offsPals + itmNum * 4 + 2, pal);
				}
			}
		} else {
		}
	}

	private static RomFunctions.StringSizeDeterminer ssd = new RomFunctions.StringSizeDeterminer() {

		@Override
		public int lengthFor(String encodedText) {
			int offs = 0;
			int len = encodedText.length();
			while (encodedText.indexOf("\\x", offs) != -1) {
				len -= 5;
				offs = encodedText.indexOf("\\x", offs) + 1;
			}
			return len;
		}
	};

	@Override
	public int getTMCount() {
		return 95;
	}

	@Override
	public int getHMCount() {
		return 6;
	}

	@Override
	public Map<Pokemon, boolean[]> getTMHMCompatibility() {
		Map<Pokemon, boolean[]> compat = new TreeMap<Pokemon, boolean[]>();
		for (int i = 1; i <= 649; i++) {
			byte[] data = pokeNarc.files.get(i);
			Pokemon pkmn = pokes[i];
			boolean[] flags = new boolean[102];
			for (int j = 0; j < 13; j++) {
				readByteIntoFlags(data, flags, j * 8 + 1, 0x28 + j);
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
			byte[] data = pokeNarc.files.get(pkmn.number);
			for (int j = 0; j < 13; j++) {
				data[0x28 + j] = getByteFromFlags(flags, j * 8 + 1);
			}
		}
	}

	@Override
	public boolean hasMoveTutors() {
		return romEntry.romType == Type_BW2;
	}

	@Override
	public List<Integer> getMoveTutorMoves() {
		if (!hasMoveTutors()) {
			return new ArrayList<Integer>();
		}
		int baseOffset = romEntry.getInt("MoveTutorDataOffset");
		int amount = 60;
		int bytesPer = 12;
		List<Integer> mtMoves = new ArrayList<Integer>();
		try {
			byte[] mtFile = readOverlay(romEntry.getInt("MoveTutorOvlNumber"));
			for (int i = 0; i < amount; i++) {
				mtMoves.add(readWord(mtFile, baseOffset + i * bytesPer));
			}
		} catch (IOException e) {
		}
		return mtMoves;
	}

	@Override
	public void setMoveTutorMoves(List<Integer> moves) {
		if (!hasMoveTutors()) {
			return;
		}
		int baseOffset = romEntry.getInt("MoveTutorDataOffset");
		int amount = 60;
		int bytesPer = 12;
		if (moves.size() != amount) {
			return;
		}
		try {
			byte[] mtFile = readOverlay(romEntry.getInt("MoveTutorOvlNumber"));
			for (int i = 0; i < amount; i++) {
				writeWord(mtFile, baseOffset + i * bytesPer, moves.get(i));
			}
			writeOverlay(romEntry.getInt("MoveTutorOvlNumber"), mtFile);
		} catch (IOException e) {
		}
	}

	@Override
	public Map<Pokemon, boolean[]> getMoveTutorCompatibility() {
		if (!hasMoveTutors()) {
			return new TreeMap<Pokemon, boolean[]>();
		}
		Map<Pokemon, boolean[]> compat = new TreeMap<Pokemon, boolean[]>();
		int[] countsPersonalOrder = new int[] { 15, 17, 13, 15 };
		int[] countsMoveOrder = new int[] { 13, 15, 15, 17 };
		int[] personalToMoveOrder = new int[] { 1, 3, 0, 2 };
		for (int i = 1; i <= 649; i++) {
			byte[] data = pokeNarc.files.get(i);
			Pokemon pkmn = pokes[i];
			boolean[] flags = new boolean[61];
			for (int mt = 0; mt < 4; mt++) {
				boolean[] mtflags = new boolean[countsPersonalOrder[mt] + 1];
				for (int j = 0; j < 4; j++) {
					readByteIntoFlags(data, mtflags, j * 8 + 1, 0x3C + mt * 4
							+ j);
				}
				int offsetOfThisData = 0;
				for (int cmoIndex = 0; cmoIndex < personalToMoveOrder[mt]; cmoIndex++) {
					offsetOfThisData += countsMoveOrder[cmoIndex];
				}
				System.arraycopy(mtflags, 1, flags, offsetOfThisData + 1,
						countsPersonalOrder[mt]);
			}
			compat.put(pkmn, flags);
		}
		return compat;
	}

	@Override
	public void setMoveTutorCompatibility(Map<Pokemon, boolean[]> compatData) {
		if (!hasMoveTutors()) {
			return;
		}
		// BW2 move tutor flags aren't using the same order as the move tutor
		// move data.
		// We unscramble them from move data order to personal.narc flag order.
		int[] countsPersonalOrder = new int[] { 15, 17, 13, 15 };
		int[] countsMoveOrder = new int[] { 13, 15, 15, 17 };
		int[] personalToMoveOrder = new int[] { 1, 3, 0, 2 };
		for (Map.Entry<Pokemon, boolean[]> compatEntry : compatData.entrySet()) {
			Pokemon pkmn = compatEntry.getKey();
			boolean[] flags = compatEntry.getValue();
			byte[] data = pokeNarc.files.get(pkmn.number);
			for (int mt = 0; mt < 4; mt++) {
				int offsetOfThisData = 0;
				for (int cmoIndex = 0; cmoIndex < personalToMoveOrder[mt]; cmoIndex++) {
					offsetOfThisData += countsMoveOrder[cmoIndex];
				}
				boolean[] mtflags = new boolean[countsPersonalOrder[mt] + 1];
				System.arraycopy(flags, offsetOfThisData + 1, mtflags, 1,
						countsPersonalOrder[mt]);
				for (int j = 0; j < 4; j++) {
					data[0x3C + mt * 4 + j] = getByteFromFlags(mtflags,
							j * 8 + 1);
				}
			}
		}
	}

	private int find(byte[] data, String hexString) {
		if (hexString.length() % 2 != 0) {
			return -3; // error
		}
		byte[] searchFor = new byte[hexString.length() / 2];
		for (int i = 0; i < searchFor.length; i++) {
			searchFor[i] = (byte) Integer.parseInt(
					hexString.substring(i * 2, i * 2 + 2), 16);
		}
		List<Integer> found = RomFunctions.search(data, searchFor);
		if (found.size() == 0) {
			return -1; // not found
		} else if (found.size() > 1) {
			return -2; // not unique
		} else {
			return found.get(0);
		}
	}

	private List<String> getStrings(boolean isStoryText, int index) {
		NARCContents baseNARC = isStoryText ? storyTextNarc : stringsNarc;
		byte[] rawFile = baseNARC.files.get(index);
		return new ArrayList<String>(PPTxtHandler.readTexts(rawFile));
	}

	private void setStrings(boolean isStoryText, int index, List<String> strings) {
		NARCContents baseNARC = isStoryText ? storyTextNarc : stringsNarc;
		byte[] oldRawFile = baseNARC.files.get(index);
		byte[] newRawFile = PPTxtHandler.saveEntry(oldRawFile, strings);
		baseNARC.files.set(index, newRawFile);
	}

	@Override
	public String getROMName() {
		return "Pokemon " + romEntry.name;
	}

	@Override
	public String getROMCode() {
		return romEntry.romCode;
	}

	@Override
	public String getSupportLevel() {
		return romEntry.staticPokemonSupport ? "Complete" : "No Static Pokemon";
	}

	@Override
	public boolean hasTimeBasedEncounters() {
		return true; // All BW/BW2 do [seasons]
	}

	@Override
	public List<Evolution> getEvolutions() {
		// Read NARC
		List<Evolution> evos = new ArrayList<Evolution>();
		List<Evolution> evosForThisPoke = new ArrayList<Evolution>();
		try {
			NARCContents evoNARC = readNARC(romEntry
					.getString("PokemonEvolutions"));
			for (int i = 1; i <= 649; i++) {
				evosForThisPoke.clear();
				byte[] evoEntry = evoNARC.files.get(i);
				for (int evo = 0; evo < 7; evo++) {
					int method = readWord(evoEntry, evo * 6);
					int species = readWord(evoEntry, evo * 6 + 4);
					if (method >= 1 && method <= 27 && species >= 1) {
						EvolutionType et = EvolutionType.fromIndex(5, method);
						int extraInfo = readWord(evoEntry, evo * 6 + 2);
						Evolution evol = new Evolution(i, species, true, et,
								extraInfo);
						if (!evos.contains(evol)) {
							evos.add(evol);
							evosForThisPoke.add(evol);
						}
					}
				}
				// split evos don't carry stats
				if (evosForThisPoke.size() > 1) {
					for (Evolution e : evosForThisPoke) {
						e.carryStats = false;
					}
				}
			}
			logBlankLine();
		} catch (IOException e) {
			// can't do anything
		}
		return evos;
	}

	@Override
	public void removeTradeEvolutions(boolean changeMoveEvos) {
		// Read NARC
		try {
			NARCContents evoNARC = readNARC(romEntry
					.getString("PokemonEvolutions"));
			Map<Pokemon, List<MoveLearnt>> movesets = this.getMovesLearnt();
			log("--Removing Trade Evolutions--");
			for (int i = 1; i <= 649; i++) {
				byte[] evoEntry = evoNARC.files.get(i);
				for (int evo = 0; evo < 7; evo++) {
					int evoType = readWord(evoEntry, evo * 6);
					int evolvingTo = readWord(evoEntry, evo * 6 + 4);
					if (changeMoveEvos && evoType == 21) {
						// read move
						int move = readWord(evoEntry, evo * 6 + 2);
						int levelLearntAt = 1;
						for (MoveLearnt ml : movesets.get(pokes[i])) {
							if (ml.move == move) {
								levelLearntAt = ml.level;
								break;
							}
						}
						if (levelLearntAt == 1) {
							// override for piloswine
							levelLearntAt = 45;
						}
						// change to pure level evo
						writeWord(evoEntry, evo * 6, 4);
						writeWord(evoEntry, evo * 6 + 2, levelLearntAt);
						logEvoChangeLevel(pokes[i].name,
								pokes[evolvingTo].name, levelLearntAt);
					}
					if (evoType == 5) {
						// Replace w/ level 37
						writeWord(evoEntry, evo * 6, 4);
						writeWord(evoEntry, evo * 6 + 2, 37);
						logEvoChangeLevel(pokes[i].name,
								pokes[evolvingTo].name, 37);
					} else if (evoType == 6) {
						// Get the current item & evolution
						int item = readWord(evoEntry, evo * 6 + 2);

						if (i == 79) {
							// Slowpoke is awkward - he already has a level evo
							// So we can't do Level up w/ Held Item for him
							// Put Water Stone instead
							writeWord(evoEntry, evo * 6, 8);
							writeWord(evoEntry, evo * 6 + 2, 84);
							logEvoChangeStone(pokes[i].name,
									pokes[evolvingTo].name, itemNames.get(84));
						} else {
							logEvoChangeLevelWithItem(pokes[i].name,
									pokes[evolvingTo].name, itemNames.get(item));
							// Replace, for this entry, w/
							// Level up w/ Held Item at Day
							writeWord(evoEntry, evo * 6, 19);
							// Now look for a free slot to put
							// Level up w/ Held Item at Night
							for (int evo2 = evo + 1; evo2 < 7; evo2++) {
								if (readWord(evoEntry, evo2 * 6) == 0) {
									// Bingo, blank entry
									writeWord(evoEntry, evo2 * 6, 20);
									writeWord(evoEntry, evo2 * 6 + 2, item);
									writeWord(evoEntry, evo2 * 6 + 4,
											evolvingTo);
									break;
								}
							}
						}
					} else if (evoType == 7) {
						// This is the karrablast <-> shelmet trade
						// Replace it with Level up w/ Other Species in Party
						// (22)
						// Based on what species we're currently dealing with
						writeWord(evoEntry, evo * 6, 22);
						writeWord(evoEntry, evo * 6 + 2, (i == 588 ? 616 : 588));
						logEvoChangeLevelWithPkmn(pokes[i].name,
								pokes[evolvingTo].name, pokes[(i == 588 ? 616
										: 588)].name);
					}
				}
			}
			writeNARC(romEntry.getString("PokemonEvolutions"), evoNARC);
			logBlankLine();
		} catch (IOException e) {
			// can't do anything
		}

	}

	@Override
	public List<String> getTrainerNames() {
		List<String> tnames = getStrings(false,
				romEntry.getInt("TrainerNamesTextOffset"));
		tnames.remove(0); // blank one
		// Tack the mugshot names on the end
		List<String> mnames = getStrings(false,
				romEntry.getInt("TrainerMugshotsTextOffset"));
		for (String mname : mnames) {
			if (!mname.isEmpty()
					&& (mname.charAt(0) >= 'A' && mname.charAt(0) <= 'Z')) {
				tnames.add(mname);
			}
		}
		return tnames;
	}

	@Override
	public int maxTrainerNameLength() {
		return 10;// based off the english ROMs
	}

	@Override
	public void setTrainerNames(List<String> trainerNames) {
		List<String> tnames = getStrings(false,
				romEntry.getInt("TrainerNamesTextOffset"));
		// Grab the mugshot names off the back of the list of trainer names
		// we got back
		List<String> mnames = getStrings(false,
				romEntry.getInt("TrainerMugshotsTextOffset"));
		int trNamesSize = trainerNames.size();
		for (int i = mnames.size() - 1; i >= 0; i--) {
			String origMName = mnames.get(i);
			if (!origMName.isEmpty()
					&& (origMName.charAt(0) >= 'A' && origMName.charAt(0) <= 'Z')) {
				// Grab replacement
				String replacement = trainerNames.remove(--trNamesSize);
				mnames.set(i, replacement);
			}
		}
		// Save back mugshot names
		setStrings(false, romEntry.getInt("TrainerMugshotsTextOffset"), mnames);

		// Now save the rest of trainer names
		List<String> newTNames = new ArrayList<String>(trainerNames);
		newTNames.add(0, tnames.get(0)); // the 0-entry, preserve it
		setStrings(false, romEntry.getInt("TrainerNamesTextOffset"), newTNames);

	}

	@Override
	public TrainerNameMode trainerNameMode() {
		return TrainerNameMode.MAX_LENGTH;
	}

	@Override
	public List<Integer> getTCNameLengthsByTrainer() {
		// not needed
		return new ArrayList<Integer>();
	}

	@Override
	public List<String> getTrainerClassNames() {
		return getStrings(false, romEntry.getInt("TrainerClassesTextOffset"));
	}

	@Override
	public void setTrainerClassNames(List<String> trainerClassNames) {
		setStrings(false, romEntry.getInt("TrainerClassesTextOffset"),
				trainerClassNames);
	}

	@Override
	public int maxTrainerClassNameLength() {
		return 12;// based off the english ROMs
	}

	@Override
	public boolean fixedTrainerClassNamesLength() {
		return false;
	}

	@Override
	public String getDefaultExtension() {
		return "nds";
	}

	@Override
	public int abilitiesPerPokemon() {
		return 3;
	}

	@Override
	public int highestAbilityIndex() {
		return 164;
	}

	@Override
	public int internalStringLength(String string) {
		return ssd.lengthFor(string);
	}

	@Override
	public void applySignature() {
		// For now, do nothing.

	}

	@Override
	public ItemList getAllowedItems() {
		return allowedItems;
	}

	@Override
	public String[] getItemNames() {
		return itemNames.toArray(new String[0]);
	}

	@Override
	public String abilityName(int number) {
		return abilityNames.get(number);
	}

	private List<Integer> getFieldItems() {
		List<Integer> fieldItems = new ArrayList<Integer>();
		// normal items
		int scriptFileNormal = romEntry.getInt("ItemBallsScriptOffset");
		int scriptFileHidden = romEntry.getInt("HiddenItemsScriptOffset");
		int[] skipTable = romEntry.arrayEntries.get("ItemBallsSkip");
		int[] skipTableH = romEntry.arrayEntries.get("HiddenItemsSkip");
		int setVarNormal = 0x28;
		int setVarHidden = 0x2A;

		byte[] itemScripts = scriptNarc.files.get(scriptFileNormal);
		int offset = 0;
		int skipTableOffset = 0;
		while (true) {
			int part1 = readWord(itemScripts, offset);
			if (part1 == 0xFD13) {
				// done
				break;
			}
			int offsetInFile = readRelativePointer(itemScripts, offset);
			offset += 4;
			if (offsetInFile > itemScripts.length) {
				break;
			}
			if (skipTableOffset < skipTable.length
					&& (skipTable[skipTableOffset] == (offset / 4) - 1)) {
				skipTableOffset++;
				continue;
			}
			int command = readWord(itemScripts, offsetInFile + 2);
			int variable = readWord(itemScripts, offsetInFile + 4);
			if (command == setVarNormal && variable == 0x800C) {
				int item = readWord(itemScripts, offsetInFile + 6);
				fieldItems.add(item);
			}

		}

		// hidden items
		byte[] hitemScripts = scriptNarc.files.get(scriptFileHidden);
		offset = 0;
		skipTableOffset = 0;
		while (true) {
			int part1 = readWord(hitemScripts, offset);
			if (part1 == 0xFD13) {
				// done
				break;
			}
			int offsetInFile = readRelativePointer(hitemScripts, offset);
			if (offsetInFile > hitemScripts.length) {
				break;
			}
			offset += 4;
			if (skipTableOffset < skipTable.length
					&& (skipTableH[skipTableOffset] == (offset / 4) - 1)) {
				skipTableOffset++;
				continue;
			}
			int command = readWord(hitemScripts, offsetInFile + 2);
			int variable = readWord(hitemScripts, offsetInFile + 4);
			if (command == setVarHidden && variable == 0x8000) {
				int item = readWord(hitemScripts, offsetInFile + 6);
				fieldItems.add(item);
			}

		}

		return fieldItems;
	}

	private void setFieldItems(List<Integer> fieldItems) {
		Iterator<Integer> iterItems = fieldItems.iterator();

		// normal items
		int scriptFileNormal = romEntry.getInt("ItemBallsScriptOffset");
		int scriptFileHidden = romEntry.getInt("HiddenItemsScriptOffset");
		int[] skipTable = romEntry.arrayEntries.get("ItemBallsSkip");
		int[] skipTableH = romEntry.arrayEntries.get("HiddenItemsSkip");
		int setVarNormal = 0x28;
		int setVarHidden = 0x2A;

		byte[] itemScripts = scriptNarc.files.get(scriptFileNormal);
		int offset = 0;
		int skipTableOffset = 0;
		while (true) {
			int part1 = readWord(itemScripts, offset);
			if (part1 == 0xFD13) {
				// done
				break;
			}
			int offsetInFile = readRelativePointer(itemScripts, offset);
			offset += 4;
			if (offsetInFile > itemScripts.length) {
				break;
			}
			if (skipTableOffset < skipTable.length
					&& (skipTable[skipTableOffset] == (offset / 4) - 1)) {
				skipTableOffset++;
				continue;
			}
			int command = readWord(itemScripts, offsetInFile + 2);
			int variable = readWord(itemScripts, offsetInFile + 4);
			if (command == setVarNormal && variable == 0x800C) {
				int item = iterItems.next();
				writeWord(itemScripts, offsetInFile + 6, item);
			}

		}

		// hidden items
		byte[] hitemScripts = scriptNarc.files.get(scriptFileHidden);
		offset = 0;
		skipTableOffset = 0;
		while (true) {
			int part1 = readWord(hitemScripts, offset);
			if (part1 == 0xFD13) {
				// done
				break;
			}
			int offsetInFile = readRelativePointer(hitemScripts, offset);
			offset += 4;
			if (offsetInFile > hitemScripts.length) {
				break;
			}
			if (skipTableOffset < skipTable.length
					&& (skipTableH[skipTableOffset] == (offset / 4) - 1)) {
				skipTableOffset++;
				continue;
			}
			int command = readWord(hitemScripts, offsetInFile + 2);
			int variable = readWord(hitemScripts, offsetInFile + 4);
			if (command == setVarHidden && variable == 0x8000) {
				int item = iterItems.next();
				writeWord(hitemScripts, offsetInFile + 6, item);
			}

		}
	}

	private int tmFromIndex(int index) {
		if (index >= 328 && index <= 419) {
			return index - 327;
		} else {
			return (index + 93) - 618;
		}
	}

	private int indexFromTM(int tm) {
		if (tm >= 1 && tm <= 92) {
			return tm + 327;
		} else {
			return tm + 525;
		}
	}

	@Override
	public List<Integer> getCurrentFieldTMs() {
		List<Integer> fieldItems = this.getFieldItems();
		List<Integer> fieldTMs = new ArrayList<Integer>();

		for (int item : fieldItems) {
			if (allowedItems.isTM(item)) {
				fieldTMs.add(tmFromIndex(item));
			}
		}

		return fieldTMs;
	}

	@Override
	public void setFieldTMs(List<Integer> fieldTMs) {
		List<Integer> fieldItems = this.getFieldItems();
		int fiLength = fieldItems.size();
		Iterator<Integer> iterTMs = fieldTMs.iterator();

		for (int i = 0; i < fiLength; i++) {
			int oldItem = fieldItems.get(i);
			if (allowedItems.isTM(oldItem)) {
				int newItem = indexFromTM(iterTMs.next());
				fieldItems.set(i, newItem);
			}
		}

		this.setFieldItems(fieldItems);
	}

	@Override
	public List<Integer> getRegularFieldItems() {
		List<Integer> fieldItems = this.getFieldItems();
		List<Integer> fieldRegItems = new ArrayList<Integer>();

		for (int item : fieldItems) {
			if (allowedItems.isAllowed(item) && !(allowedItems.isTM(item))) {
				fieldRegItems.add(item);
			}
		}

		return fieldRegItems;
	}

	@Override
	public void setRegularFieldItems(List<Integer> items) {
		List<Integer> fieldItems = this.getFieldItems();
		int fiLength = fieldItems.size();
		Iterator<Integer> iterNewItems = items.iterator();

		for (int i = 0; i < fiLength; i++) {
			int oldItem = fieldItems.get(i);
			if (!(allowedItems.isTM(oldItem))
					&& allowedItems.isAllowed(oldItem)) {
				int newItem = iterNewItems.next();
				fieldItems.set(i, newItem);
			}
		}

		this.setFieldItems(fieldItems);
	}

	@Override
	public List<Integer> getRequiredFieldTMs() {
		if (romEntry.romType == Type_BW) {
			return Arrays.asList(new Integer[] { 2, 3, 5, 6, 9, 12, 13, 19, 22,
					24, 26, 29, 30, 35, 36, 39, 41, 46, 47, 50, 52, 53, 55, 58,
					61, 63, 65, 66, 71, 80, 81, 84, 85, 86, 90, 91, 92, 93 });
		} else {
			return Arrays
					.asList(new Integer[] { 1, 2, 3, 5, 6, 12, 13, 19, 22, 26,
							28, 29, 30, 36, 39, 41, 46, 47, 50, 52, 53, 56, 58,
							61, 63, 65, 66, 67, 69, 71, 80, 81, 84, 85, 86, 90,
							91, 92, 93 });
		}
	}

	@Override
	public List<IngameTrade> getIngameTrades() {
		List<IngameTrade> trades = new ArrayList<IngameTrade>();
		try {
			NARCContents tradeNARC = this.readNARC(romEntry
					.getString("InGameTrades"));
			List<String> tradeStrings = getStrings(false,
					romEntry.getInt("IngameTradesTextOffset"));
			int[] unused = romEntry.arrayEntries.get("TradesUnused");
			int unusedOffset = 0;
			int tableSize = tradeNARC.files.size();

			for (int entry = 0; entry < tableSize; entry++) {
				if (unusedOffset < unused.length
						&& unused[unusedOffset] == entry) {
					unusedOffset++;
					continue;
				}
				IngameTrade trade = new IngameTrade();
				byte[] tfile = tradeNARC.files.get(entry);
				trade.nickname = tradeStrings.get(entry * 2);
				trade.givenPokemon = pokes[readLong(tfile, 4)];
				trade.ivs = new int[6];
				for (int iv = 0; iv < 6; iv++) {
					trade.ivs[iv] = readLong(tfile, 0x10 + iv * 4);
				}
				trade.otId = readWord(tfile, 0x34);
				trade.item = readLong(tfile, 0x4C);
				trade.otName = tradeStrings.get(entry * 2 + 1);
				trade.requestedPokemon = pokes[readLong(tfile, 0x5C)];
				trades.add(trade);
			}
		} catch (Exception ex) {
		}

		return trades;

	}

	@Override
	public void setIngameTrades(List<IngameTrade> trades) {
		// info
		int tradeOffset = 0;
		try {
			NARCContents tradeNARC = this.readNARC(romEntry
					.getString("InGameTrades"));
			List<String> tradeStrings = getStrings(false,
					romEntry.getInt("IngameTradesTextOffset"));
			int tradeCount = tradeNARC.files.size();
			int[] unused = romEntry.arrayEntries.get("TradesUnused");
			int unusedOffset = 0;
			for (int i = 0; i < tradeCount; i++) {
				if (unusedOffset < unused.length && unused[unusedOffset] == i) {
					unusedOffset++;
					continue;
				}
				byte[] tfile = tradeNARC.files.get(i);
				IngameTrade trade = trades.get(tradeOffset++);
				tradeStrings.set(i * 2, trade.nickname);
				tradeStrings.set(i * 2 + 1, trade.otName);
				writeLong(tfile, 4, trade.givenPokemon.number);
				writeLong(tfile, 8, 0); // disable forme
				for (int iv = 0; iv < 6; iv++) {
					writeLong(tfile, 0x10 + iv * 4, trade.ivs[iv]);
				}
				writeLong(tfile, 0x2C, 0xFF); // random nature
				writeWord(tfile, 0x34, trade.otId);
				writeLong(tfile, 0x4C, trade.item);
				writeLong(tfile, 0x5C, trade.requestedPokemon.number);
			}
			this.writeNARC(romEntry.getString("InGameTrades"), tradeNARC);
			this.setStrings(false, romEntry.getInt("IngameTradesTextOffset"),
					tradeStrings);
		} catch (IOException ex) {
		}
	}

	@Override
	public boolean hasDVs() {
		return false;
	}

	@Override
	public int generationOfPokemon() {
		return 5;
	}

	@Override
	public void removeEvosForPokemonPool() {
		// slightly more complicated than gen2/3
		// we have to update a "baby table" too
		List<Pokemon> pokemonIncluded = this.mainPokemonList;
		List<Evolution> evolsIncluded = new ArrayList<Evolution>();
		try {
			NARCContents evoNARC = readNARC(romEntry
					.getString("PokemonEvolutions"));
			NARCContents babyNARC = readNARC(romEntry.getString("BabyPokemon"));
			for (int i = 1; i <= 649; i++) {
				boolean included = pokemonIncluded.contains(pokes[i]);
				byte[] evoEntry = evoNARC.files.get(i);
				for (int evo = 0; evo < 7; evo++) {
					int method = readWord(evoEntry, evo * 6);
					int species = readWord(evoEntry, evo * 6 + 4);
					if (method >= 1 && method <= 27 && species >= 1) {
						Pokemon evolvingInto = pokes[species];
						if (!included
								|| !pokemonIncluded.contains(evolvingInto)) {
							// remove this evolution
							writeWord(evoEntry, evo * 6, 0);
							writeWord(evoEntry, evo * 6 + 2, 0);
							writeWord(evoEntry, evo * 6 + 4, 0);
						} else {
							EvolutionType et = EvolutionType.fromIndex(5,
									method);
							int extraInfo = readWord(evoEntry, evo * 6 + 2);
							Evolution evol = new Evolution(i, species, true,
									et, extraInfo);
							evolsIncluded.add(evol);
						}
					}
				}
			}
			// baby pokemon
			for (int i = 1; i <= 493; i++) {
				int oldBaby = i;
				while (true) {
					int currentBaby = oldBaby;
					for (Evolution evol : evolsIncluded) {
						if (evol.to == oldBaby) {
							currentBaby = evol.from;
							break;
						}
					}
					if (currentBaby == oldBaby) {
						break;
					}
					oldBaby = currentBaby;
				}
				writeWord(babyNARC.files.get(i), 0, oldBaby);
			}
			// finish up
			writeNARC(romEntry.getString("PokemonEvolutions"), evoNARC);
			writeNARC(romEntry.getString("BabyPokemon"), babyNARC);
		} catch (IOException e) {
			// can't do anything
		}
	}

	@Override
	public boolean supportsFourStartingMoves() {
		return true;
	}
}
