package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  Gen3RomHandler.java - randomizer handler for R/S/E/FR/LG.             --*/
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.CRC32;

import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.GFXFunctions;
import com.dabomstew.pkrandom.MiscTweak;
import com.dabomstew.pkrandom.RomFunctions;
import com.dabomstew.pkrandom.constants.Gen3Constants;
import com.dabomstew.pkrandom.constants.GlobalConstants;
import com.dabomstew.pkrandom.exceptions.RandomizationException;
import com.dabomstew.pkrandom.exceptions.RandomizerIOException;
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

import compressors.DSDecmp;

public class Gen3RomHandler extends AbstractGBRomHandler {

    public static class Factory extends RomHandler.Factory {

        @Override
        public Gen3RomHandler create(Random random) {
            return new Gen3RomHandler(random);
        }

        public boolean isLoadable(String filename) {
            long fileLength = new File(filename).length();
            if (fileLength > 32 * 1024 * 1024) {
                return false;
            }
            byte[] loaded = loadFilePartial(filename, 0x100000);
            if (loaded.length == 0) {
                // nope
                return false;
            }
            return detectRomInner(loaded, (int) fileLength);
        }
    }

    public Gen3RomHandler(Random random) {
        super(random);
    }

    private static class RomEntry {
        private String name;
        private String romCode;
        private String tableFile;
        private int version;
        private int romType;
        private boolean copyStaticPokemon;
        private Map<String, Integer> entries = new HashMap<String, Integer>();
        private Map<String, int[]> arrayEntries = new HashMap<String, int[]>();
        private List<StaticPokemon> staticPokemon = new ArrayList<StaticPokemon>();
        private List<TMOrMTTextEntry> tmmtTexts = new ArrayList<TMOrMTTextEntry>();

        public RomEntry() {

        }

        public RomEntry(RomEntry toCopy) {
            this.name = toCopy.name;
            this.romCode = toCopy.romCode;
            this.tableFile = toCopy.tableFile;
            this.version = toCopy.version;
            this.romType = toCopy.romType;
            this.copyStaticPokemon = toCopy.copyStaticPokemon;
            this.entries.putAll(toCopy.entries);
            this.arrayEntries.putAll(toCopy.arrayEntries);
            this.staticPokemon.addAll(toCopy.staticPokemon);
            this.tmmtTexts.addAll(toCopy.tmmtTexts);
        }

        private int getValue(String key) {
            if (!entries.containsKey(key)) {
                entries.put(key, 0);
            }
            return entries.get(key);
        }
    }

    private static class TMOrMTTextEntry {
        private int number;
        private int mapBank, mapNumber;
        private int personNum;
        private int offsetInScript;
        private int actualOffset;
        private String template;
        private boolean isMoveTutor;
    }

    private static List<RomEntry> roms;

    static {
        loadROMInfo();
    }

    private static void loadROMInfo() {
        roms = new ArrayList<RomEntry>();
        RomEntry current = null;
        try {
            Scanner sc = new Scanner(FileFunctions.openConfig("gen3_offsets.ini"), "UTF-8");
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
                        // Static Pokemon?
                        if (r[0].equals("StaticPokemon[]")) {
                            if (r[1].startsWith("[") && r[1].endsWith("]")) {
                                String[] offsets = r[1].substring(1, r[1].length() - 1).split(",");
                                int[] offs = new int[offsets.length];
                                int c = 0;
                                for (String off : offsets) {
                                    offs[c++] = parseRIInt(off);
                                }
                                current.staticPokemon.add(new StaticPokemon(offs));
                            } else {
                                int offs = parseRIInt(r[1]);
                                current.staticPokemon.add(new StaticPokemon(offs));
                            }
                        } else if (r[0].equals("TMText[]")) {
                            if (r[1].startsWith("[") && r[1].endsWith("]")) {
                                String[] parts = r[1].substring(1, r[1].length() - 1).split(",", 6);
                                TMOrMTTextEntry tte = new TMOrMTTextEntry();
                                tte.number = parseRIInt(parts[0]);
                                tte.mapBank = parseRIInt(parts[1]);
                                tte.mapNumber = parseRIInt(parts[2]);
                                tte.personNum = parseRIInt(parts[3]);
                                tte.offsetInScript = parseRIInt(parts[4]);
                                tte.template = parts[5];
                                tte.isMoveTutor = false;
                                current.tmmtTexts.add(tte);
                            }
                        } else if (r[0].equals("MoveTutorText[]")) {
                            if (r[1].startsWith("[") && r[1].endsWith("]")) {
                                String[] parts = r[1].substring(1, r[1].length() - 1).split(",", 6);
                                TMOrMTTextEntry tte = new TMOrMTTextEntry();
                                tte.number = parseRIInt(parts[0]);
                                tte.mapBank = parseRIInt(parts[1]);
                                tte.mapNumber = parseRIInt(parts[2]);
                                tte.personNum = parseRIInt(parts[3]);
                                tte.offsetInScript = parseRIInt(parts[4]);
                                tte.template = parts[5];
                                tte.isMoveTutor = true;
                                current.tmmtTexts.add(tte);
                            }
                        } else if (r[0].equals("Game")) {
                            current.romCode = r[1];
                        } else if (r[0].equals("Version")) {
                            current.version = parseRIInt(r[1]);
                        } else if (r[0].equals("Type")) {
                            if (r[1].equalsIgnoreCase("Ruby")) {
                                current.romType = Gen3Constants.RomType_Ruby;
                            } else if (r[1].equalsIgnoreCase("Sapp")) {
                                current.romType = Gen3Constants.RomType_Sapp;
                            } else if (r[1].equalsIgnoreCase("Em")) {
                                current.romType = Gen3Constants.RomType_Em;
                            } else if (r[1].equalsIgnoreCase("FRLG")) {
                                current.romType = Gen3Constants.RomType_FRLG;
                            } else {
                                System.err.println("unrecognised rom type: " + r[1]);
                            }
                        } else if (r[0].equals("TableFile")) {
                            current.tableFile = r[1];
                        } else if (r[0].equals("CopyStaticPokemon")) {
                            int csp = parseRIInt(r[1]);
                            current.copyStaticPokemon = (csp > 0);
                        } else if (r[0].equals("CopyFrom")) {
                            for (RomEntry otherEntry : roms) {
                                if (r[1].equalsIgnoreCase(otherEntry.name)) {
                                    // copy from here
                                    current.arrayEntries.putAll(otherEntry.arrayEntries);
                                    current.entries.putAll(otherEntry.entries);
                                    boolean cTT = (current.getValue("CopyTMText") == 1);
                                    if (current.copyStaticPokemon) {
                                        current.staticPokemon.addAll(otherEntry.staticPokemon);
                                        current.entries.put("StaticPokemonSupport", 1);
                                    } else {
                                        current.entries.put("StaticPokemonSupport", 0);
                                    }
                                    if (cTT) {
                                        current.tmmtTexts.addAll(otherEntry.tmmtTexts);
                                    }
                                    current.tableFile = otherEntry.tableFile;
                                }
                            }
                        } else {
                            if (r[1].startsWith("[") && r[1].endsWith("]")) {
                                String[] offsets = r[1].substring(1, r[1].length() - 1).split(",");
                                if (offsets.length == 1 && offsets[0].trim().isEmpty()) {
                                    current.arrayEntries.put(r[0], new int[0]);
                                } else {
                                    int[] offs = new int[offsets.length];
                                    int c = 0;
                                    for (String off : offsets) {
                                        offs[c++] = parseRIInt(off);
                                    }
                                    current.arrayEntries.put(r[0], offs);
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

    private void loadTextTable(String filename) {
        try {
            Scanner sc = new Scanner(FileFunctions.openConfig(filename + ".tbl"), "UTF-8");
            while (sc.hasNextLine()) {
                String q = sc.nextLine();
                if (!q.trim().isEmpty()) {
                    String[] r = q.split("=", 2);
                    if (r[1].endsWith("\r\n")) {
                        r[1] = r[1].substring(0, r[1].length() - 2);
                    }
                    tb[Integer.parseInt(r[0], 16)] = r[1];
                    d.put(r[1], (byte) Integer.parseInt(r[0], 16));
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
        }

    }
    
    public static RomEntry getRomFromSupportedRom(String romName) {
        for(RomEntry re : roms) {
            if(romName.equals(re.name)) {
                return re;
            }
        }
        return null;
    }

    // This ROM's data
    private Pokemon[] pokes, pokesInternal;
    private List<Pokemon> pokemonList;
    private int numRealPokemon;
    private Move[] moves;
    private boolean jamboMovesetHack;
    private RomEntry romEntry;
    private boolean havePatchedObedience;
    public String[] tb;
    public Map<String, Byte> d;
    private String[] abilityNames;
    private String[] itemNames;
    private boolean mapLoadingDone;
    private List<Integer> itemOffs;
    private String[][] mapNames;
    private boolean isRomHack;
    private boolean isGen1;
    private int[] internalToPokedex, pokedexToInternal;
    private int pokedexCount;
    private String[] pokeNames;

    @Override
    public boolean detectRom(byte[] rom) {
        return detectRomInner(rom, rom.length);
    }

    private static boolean detectRomInner(byte[] rom, int romSize) {
        if (romSize != Gen3Constants.size8M && romSize != Gen3Constants.size16M && romSize != Gen3Constants.size32M) {
            return false; // size check
        }
        // Special case for Emerald unofficial translation
        if (romName(rom, Gen3Constants.unofficialEmeraldROMName)) {
            // give it a rom code so it can be detected
            rom[Gen3Constants.romCodeOffset] = 'B';
            rom[Gen3Constants.romCodeOffset + 1] = 'P';
            rom[Gen3Constants.romCodeOffset + 2] = 'E';
            rom[Gen3Constants.romCodeOffset + 3] = 'T';
            rom[Gen3Constants.headerChecksumOffset] = 0x66;
        }
        // Wild Pokemon header
        if (find(rom, Gen3Constants.wildPokemonPointerPrefix) == -1) {
            return false;
        }
        // Map Banks header
        if (find(rom, Gen3Constants.mapBanksPointerPrefix) == -1) {
            return false;
        }
        // Pokedex Order header
        if (findMultiple(rom, Gen3Constants.pokedexOrderPointerPrefix).size() != 3) {
            return false;
        }
        for (RomEntry re : roms) {
            if (romCode(rom, re.romCode) && (rom[Gen3Constants.romVersionOffset] & 0xFF) == re.version) {
                return true; // match
            }
        }
        return false; // GBA rom we don't support yet
    }

    @Override
    public void loadedRom() {
        for (RomEntry re : roms) {
            if (romCode(rom, re.romCode) && (rom[0xBC] & 0xFF) == re.version) {
                romEntry = new RomEntry(re); // clone so we can modify
                break;
            }
        }

        tb = new String[256];
        d = new HashMap<String, Byte>();
        isRomHack = false;
        jamboMovesetHack = false;
        Gen3Constants.allowedItems.allowSingles(Gen3Constants.luckyEggIndex);
        Gen3Constants.nonBadItems.allowSingles(Gen3Constants.luckyEggIndex);

        // Pokemon count stuff, needs to be available first
        List<Integer> pokedexOrderPrefixes = findMultiple(rom, Gen3Constants.pokedexOrderPointerPrefix);
        getRomEntry().entries.put("PokedexOrder", readPointer(pokedexOrderPrefixes.get(1) + 16));

        // Pokemon names offset
        if (getRomEntry().romType == Gen3Constants.RomType_Ruby || getRomEntry().romType == Gen3Constants.RomType_Sapp) {
            int baseNomOffset = find(rom, Gen3Constants.rsPokemonNamesPointerSuffix);
            getRomEntry().entries.put("PokemonNames", readPointer(baseNomOffset - 4));
            getRomEntry().entries.put(
                    "FrontSprites",
                    readPointer(findPointerPrefixAndSuffix(Gen3Constants.rsFrontSpritesPointerPrefix,
                            Gen3Constants.rsFrontSpritesPointerSuffix)));
            getRomEntry().entries.put(
                    "PokemonPalettes",
                    readPointer(findPointerPrefixAndSuffix(Gen3Constants.rsPokemonPalettesPointerPrefix,
                            Gen3Constants.rsPokemonPalettesPointerSuffix)));
        } else {
            getRomEntry().entries.put("PokemonNames", readPointer(Gen3Constants.efrlgPokemonNamesPointer));
            getRomEntry().entries.put("MoveNames", readPointer(Gen3Constants.efrlgMoveNamesPointer));
            getRomEntry().entries.put("AbilityNames", readPointer(Gen3Constants.efrlgAbilityNamesPointer));
            getRomEntry().entries.put("ItemData", readPointer(Gen3Constants.efrlgItemDataPointer));
            getRomEntry().entries.put("MoveData", readPointer(Gen3Constants.efrlgMoveDataPointer));
            getRomEntry().entries.put("PokemonStats", readPointer(Gen3Constants.efrlgPokemonStatsPointer));
            getRomEntry().entries.put("FrontSprites", readPointer(Gen3Constants.efrlgFrontSpritesPointer));
            getRomEntry().entries.put("PokemonPalettes", readPointer(Gen3Constants.efrlgPokemonPalettesPointer));
            getRomEntry().entries.put("MoveTutorCompatibility",
                    getRomEntry().getValue("MoveTutorData") + getRomEntry().getValue("MoveTutorMoves") * 2);
        }

        loadTextTable(getRomEntry().tableFile);

        if (getRomEntry().romCode.equals("BPRE") && getRomEntry().version == 0) {
            basicBPRE10HackSupport();
        }

        loadPokemonNames();
        loadPokedex();
        loadPokemonStats();
        constructPokemonList();
        populateEvolutions();
        loadMoves();

        // Get wild Pokemon offset
        int baseWPOffset = findMultiple(rom, Gen3Constants.wildPokemonPointerPrefix).get(0);
        getRomEntry().entries.put("WildPokemon", readPointer(baseWPOffset + 12));

        // map banks
        int baseMapsOffset = findMultiple(rom, Gen3Constants.mapBanksPointerPrefix).get(0);
        getRomEntry().entries.put("MapHeaders", readPointer(baseMapsOffset + 12));
        this.determineMapBankSizes();

        // map labels
        if (getRomEntry().romType == Gen3Constants.RomType_FRLG) {
            int baseMLOffset = find(rom, Gen3Constants.frlgMapLabelsPointerPrefix);
            getRomEntry().entries.put("MapLabels", readPointer(baseMLOffset + 12));
        } else {
            int baseMLOffset = find(rom, Gen3Constants.rseMapLabelsPointerPrefix);
            getRomEntry().entries.put("MapLabels", readPointer(baseMLOffset + 12));
        }

        mapLoadingDone = false;
        loadAbilityNames();
        loadItemNames();

        // Mark as FRLG
        if (getRomEntry().romType == Gen3Constants.RomType_FRLG) {
            isGen1 = true;
        } else {
            isGen1 = false;
        }
    }

    private int findPointerPrefixAndSuffix(String prefix, String suffix) {
        if (prefix.length() % 2 != 0 || suffix.length() % 2 != 0) {
            return -1;
        }
        byte[] searchPref = new byte[prefix.length() / 2];
        for (int i = 0; i < searchPref.length; i++) {
            searchPref[i] = (byte) Integer.parseInt(prefix.substring(i * 2, i * 2 + 2), 16);
        }
        byte[] searchSuff = new byte[suffix.length() / 2];
        for (int i = 0; i < searchSuff.length; i++) {
            searchSuff[i] = (byte) Integer.parseInt(suffix.substring(i * 2, i * 2 + 2), 16);
        }
        if (searchPref.length >= searchSuff.length) {
            // Prefix first
            List<Integer> offsets = RomFunctions.search(rom, searchPref);
            if (offsets.size() == 0) {
                return -1;
            }
            for (int prefOffset : offsets) {
                if (prefOffset + 4 + searchSuff.length > rom.length) {
                    continue; // not enough room for this to be valid
                }
                int ptrOffset = prefOffset + searchPref.length;
                int pointerValue = readPointer(ptrOffset);
                if (pointerValue < 0 || pointerValue >= rom.length) {
                    // Not a valid pointer
                    continue;
                }
                boolean suffixMatch = true;
                for (int i = 0; i < searchSuff.length; i++) {
                    if (rom[ptrOffset + 4 + i] != searchSuff[i]) {
                        suffixMatch = false;
                        break;
                    }
                }
                if (suffixMatch) {
                    return ptrOffset;
                }
            }
            return -1; // No match
        } else {
            // Suffix first
            List<Integer> offsets = RomFunctions.search(rom, searchSuff);
            if (offsets.size() == 0) {
                return -1;
            }
            for (int suffOffset : offsets) {
                if (suffOffset - 4 - searchPref.length < 0) {
                    continue; // not enough room for this to be valid
                }
                int ptrOffset = suffOffset - 4;
                int pointerValue = readPointer(ptrOffset);
                if (pointerValue < 0 || pointerValue >= rom.length) {
                    // Not a valid pointer
                    continue;
                }
                boolean prefixMatch = true;
                for (int i = 0; i < searchPref.length; i++) {
                    if (rom[ptrOffset - searchPref.length + i] != searchPref[i]) {
                        prefixMatch = false;
                        break;
                    }
                }
                if (prefixMatch) {
                    return ptrOffset;
                }
            }
            return -1; // No match
        }
    }

    private void basicBPRE10HackSupport() {
        if (basicBPRE10HackDetection()) {
            this.isRomHack = true;
            // NUMBER OF POKEMON DETECTION

            // this is the most annoying bit
            // we'll try to get it from the pokemon names,
            // and sanity check it using other things
            // this of course means we can't support
            // any hack with extended length names

            int iPokemonCount = 0;
            int namesOffset = getRomEntry().getValue("PokemonNames");
            int nameLen = getRomEntry().getValue("PokemonNameLength");
            while (true) {
                int nameOffset = namesOffset + (iPokemonCount + 1) * nameLen;
                int nameStrLen = lengthOfStringAt(nameOffset);
                if (nameStrLen > 0 && nameStrLen < nameLen && rom[nameOffset] != 0) {
                    iPokemonCount++;
                } else {
                    break;
                }
            }

            // Is there an unused egg slot at the end?
            String lastName = readVariableLengthString(namesOffset + iPokemonCount * nameLen);
            if (lastName.equals("?") || lastName.equals("-")) {
                iPokemonCount--;
            }

            // Jambo's Moves Learnt table hack?
            // need to check this before using moveset pointers
            int movesetsTable;
            if (readLong(0x3EB20) == 0x47084918) {
                // Hack applied, adjust accordingly
                int firstRoutinePtr = readPointer(0x3EB84);
                movesetsTable = readPointer(firstRoutinePtr + 75);
                jamboMovesetHack = true;
            } else {
                movesetsTable = readPointer(0x3EA7C);
                jamboMovesetHack = false;
            }

            // secondary check: moveset pointers
            // if a slot has an invalid moveset pointer, it's not a real slot
            // Before that, grab the moveset table from a known pointer to it.
            getRomEntry().entries.put("PokemonMovesets", movesetsTable);
            while (iPokemonCount >= 0) {
                int movesetPtr = readPointer(movesetsTable + iPokemonCount * 4);
                if (movesetPtr < 0 || movesetPtr >= rom.length) {
                    iPokemonCount--;
                } else {
                    break;
                }
            }

            // sanity check: pokedex order
            // pokedex entries have to be within 0-1023
            // even after extending the dex
            // (at least with conventional methods)
            // so if we run into an invalid one
            // then we can cut off the count
            int pdOffset = getRomEntry().getValue("PokedexOrder");
            for (int i = 1; i <= iPokemonCount; i++) {
                int pdEntry = readWord(pdOffset + (i - 1) * 2);
                if (pdEntry > 1023) {
                    iPokemonCount = i - 1;
                    break;
                }
            }

            // write new pokemon count
            getRomEntry().entries.put("PokemonCount", iPokemonCount);

            // update some key offsets from known pointers
            getRomEntry().entries.put("PokemonTMHMCompat", readPointer(0x43C68));
            getRomEntry().entries.put("PokemonEvolutions", readPointer(0x42F6C));
            getRomEntry().entries.put("MoveTutorCompatibility", readPointer(0x120C30));
            int descsTable = readPointer(0xE5440);
            getRomEntry().entries.put("MoveDescriptions", descsTable);
            int trainersTable = readPointer(0xFC00);
            getRomEntry().entries.put("TrainerData", trainersTable);

            // try to detect number of moves using the descriptions
            int moveCount = 0;
            while (true) {
                int descPointer = readPointer(descsTable + (moveCount) * 4);
                if (descPointer >= 0 && descPointer < rom.length) {
                    int descStrLen = lengthOfStringAt(descPointer);
                    if (descStrLen > 0 && descStrLen < 100) {
                        // okay, this does seem fine
                        moveCount++;
                        continue;
                    }
                }
                break;
            }
            getRomEntry().entries.put("MoveCount", moveCount);

            // attempt to detect number of trainers using various tells
            int trainerCount = 1;
            int tEntryLen = getRomEntry().getValue("TrainerEntrySize");
            int tNameLen = getRomEntry().getValue("TrainerNameLength");
            while (true) {
                int trOffset = trainersTable + tEntryLen * trainerCount;
                int pokeDataType = rom[trOffset] & 0xFF;
                if (pokeDataType >= 4) {
                    // only allowed 0-3
                    break;
                }
                int numPokes = rom[trOffset + (tEntryLen - 8)] & 0xFF;
                if (numPokes == 0 || numPokes > 6) {
                    break;
                }
                int pointerToPokes = readPointer(trOffset + (tEntryLen - 4));
                if (pointerToPokes < 0 || pointerToPokes >= rom.length) {
                    break;
                }
                int nameLength = lengthOfStringAt(trOffset + 4);
                if (nameLength >= tNameLen) {
                    break;
                }
                // found a valid trainer entry, recognize it
                trainerCount++;
            }
            getRomEntry().entries.put("TrainerCount", trainerCount);

            // disable static pokemon & move tutor/tm text
            getRomEntry().entries.put("StaticPokemonSupport", 0);
            getRomEntry().staticPokemon.clear();
            getRomEntry().tmmtTexts.clear();

        }

    }

    private boolean basicBPRE10HackDetection() {
        if (rom.length != Gen3Constants.size16M) {
            return true;
        }
        CRC32 checksum = new CRC32();
        checksum.update(rom);
        long csum = checksum.getValue();
        return csum != 3716707868L;
    }

    @Override
    public void savingRom() {
        savePokemonStats();
        saveMoves();
    }

    private void loadPokedex() {
        int pdOffset = getRomEntry().getValue("PokedexOrder");
        int numInternalPokes = getRomEntry().getValue("PokemonCount");
        int maxPokedex = 0;
        internalToPokedex = new int[numInternalPokes + 1];
        pokedexToInternal = new int[numInternalPokes + 1];
        for (int i = 1; i <= numInternalPokes; i++) {
            int dexEntry = readWord(rom, pdOffset + (i - 1) * 2);
            if (dexEntry != 0) {
                internalToPokedex[i] = dexEntry;
                // take the first pokemon only for each dex entry
                if (getPokedexToInternal()[dexEntry] == 0) {
                    getPokedexToInternal()[dexEntry] = i;
                }
                maxPokedex = Math.max(maxPokedex, dexEntry);
            }
        }
        if (maxPokedex == Gen3Constants.unhackedMaxPokedex) {
            // see if the slots between johto and hoenn are in use
            // old rom hacks use them instead of expanding pokes
            int offs = getRomEntry().getValue("PokemonStats");
            int usedSlots = 0;
            for (int i = 0; i < Gen3Constants.unhackedMaxPokedex - Gen3Constants.unhackedRealPokedex; i++) {
                int pokeSlot = Gen3Constants.hoennPokesStart + i;
                int pokeOffs = offs + pokeSlot * Gen3Constants.baseStatsEntrySize;
                String lowerName = pokeNames[pokeSlot].toLowerCase();
                if (!this.matches(rom, pokeOffs, Gen3Constants.emptyPokemonSig) && !lowerName.contains("unused")
                        && !lowerName.equals("?") && !lowerName.equals("-")) {
                    usedSlots++;
                    getPokedexToInternal()[Gen3Constants.unhackedRealPokedex + usedSlots] = pokeSlot;
                    internalToPokedex[pokeSlot] = Gen3Constants.unhackedRealPokedex + usedSlots;
                } else {
                    internalToPokedex[pokeSlot] = 0;
                }
            }
            // remove the fake extra slots
            for (int i = usedSlots + 1; i <= Gen3Constants.unhackedMaxPokedex - Gen3Constants.unhackedRealPokedex; i++) {
                getPokedexToInternal()[Gen3Constants.unhackedRealPokedex + i] = 0;
            }
            // if any slots were used at all, this is a rom hack
            if (usedSlots > 0) {
                this.isRomHack = true;
            }
            this.pokedexCount = Gen3Constants.unhackedRealPokedex + usedSlots;
        } else {
            this.isRomHack = true;
            this.pokedexCount = maxPokedex;
        }

    }

    private void constructPokemonList() {
        if (!this.isRomHack) {
            // simple behavior: all pokes in the dex are valid
            pokemonList = Arrays.asList(pokes);
        } else {
            // only include "valid" pokes
            pokemonList = new ArrayList<Pokemon>();
            pokemonList.add(null);
            for (int i = 1; i < pokes.length; i++) {
                Pokemon pk = pokes[i];
                if (pk != null) {
                    String lowerName = pk.name.toLowerCase();
                    if (!lowerName.contains("unused") && !lowerName.equals("?")) {
                        pokemonList.add(pk);
                    }
                }
            }
        }
        numRealPokemon = pokemonList.size() - 1;

    }

    private void loadPokemonStats() {
        pokes = new Pokemon[this.pokedexCount + 1];
        int numInternalPokes = getRomEntry().getValue("PokemonCount");
        pokesInternal = new Pokemon[numInternalPokes + 1];
        int offs = getRomEntry().getValue("PokemonStats");
        for (int i = 1; i <= numInternalPokes; i++) {
            Pokemon pk = new Pokemon();
            pk.name = pokeNames[i];
            pk.number = internalToPokedex[i];
            if (pk.number != 0) {
                pokes[pk.number] = pk;
            }
            pokesInternal[i] = pk;
            int pkoffs = offs + i * Gen3Constants.baseStatsEntrySize;
            loadBasicPokeStats(pk, pkoffs);
        }
    }

    private void savePokemonStats() {
        // Write pokemon names & stats
        int offs = getRomEntry().getValue("PokemonNames");
        int nameLen = getRomEntry().getValue("PokemonNameLength");
        int offs2 = getRomEntry().getValue("PokemonStats");
        int numInternalPokes = getRomEntry().getValue("PokemonCount");
        for (int i = 1; i <= numInternalPokes; i++) {
            Pokemon pk = pokesInternal[i];
            int stringOffset = offs + i * nameLen;
            writeFixedLengthString(pk.name, stringOffset, nameLen);
            saveBasicPokeStats(pk, offs2 + i * Gen3Constants.baseStatsEntrySize);
        }
        writeEvolutions();
    }

    private void loadMoves() {
        int moveCount = getRomEntry().getValue("MoveCount");
        moves = new Move[moveCount + 1];
        int offs = getRomEntry().getValue("MoveData");
        int nameoffs = getRomEntry().getValue("MoveNames");
        int namelen = getRomEntry().getValue("MoveNameLength");
        for (int i = 1; i <= moveCount; i++) {
            moves[i] = new Move();
            moves[i].name = readFixedLengthString(nameoffs + i * namelen, namelen);
            moves[i].number = i;
            moves[i].internalId = i;
            moves[i].effectIndex = rom[offs + i * 0xC] & 0xFF;
            moves[i].hitratio = ((rom[offs + i * 0xC + 3] & 0xFF) + 0);
            moves[i].power = rom[offs + i * 0xC + 1] & 0xFF;
            moves[i].pp = rom[offs + i * 0xC + 4] & 0xFF;
            moves[i].type = Gen3Constants.typeTable[rom[offs + i * 0xC + 2]];

            if (GlobalConstants.normalMultihitMoves.contains(i)) {
                moves[i].hitCount = 3;
            } else if (GlobalConstants.doubleHitMoves.contains(i)) {
                moves[i].hitCount = 2;
            } else if (i == GlobalConstants.TRIPLE_KICK_INDEX) {
                moves[i].hitCount = 2.71; // this assumes the first hit lands
            }
        }

    }

    private void saveMoves() {
        int moveCount = getRomEntry().getValue("MoveCount");
        int offs = getRomEntry().getValue("MoveData");
        for (int i = 1; i <= moveCount; i++) {
            rom[offs + i * 0xC] = (byte) moves[i].effectIndex;
            rom[offs + i * 0xC + 1] = (byte) moves[i].power;
            rom[offs + i * 0xC + 2] = Gen3Constants.typeToByte(moves[i].type);
            int hitratio = (int) Math.round(moves[i].hitratio);
            if (hitratio < 0) {
                hitratio = 0;
            }
            if (hitratio > 100) {
                hitratio = 100;
            }
            rom[offs + i * 0xC + 3] = (byte) hitratio;
            rom[offs + i * 0xC + 4] = (byte) moves[i].pp;
        }
    }

    public List<Move> getMoves() {
        return Arrays.asList(moves);
    }

    private void loadBasicPokeStats(Pokemon pkmn, int offset) {
        pkmn.hp = rom[offset + Gen3Constants.bsHPOffset] & 0xFF;
        pkmn.attack = rom[offset + Gen3Constants.bsAttackOffset] & 0xFF;
        pkmn.defense = rom[offset + Gen3Constants.bsDefenseOffset] & 0xFF;
        pkmn.speed = rom[offset + Gen3Constants.bsSpeedOffset] & 0xFF;
        pkmn.spatk = rom[offset + Gen3Constants.bsSpAtkOffset] & 0xFF;
        pkmn.spdef = rom[offset + Gen3Constants.bsSpDefOffset] & 0xFF;
        // Type
        pkmn.primaryType = Gen3Constants.typeTable[rom[offset + Gen3Constants.bsPrimaryTypeOffset] & 0xFF];
        pkmn.secondaryType = Gen3Constants.typeTable[rom[offset + Gen3Constants.bsSecondaryTypeOffset] & 0xFF];
        // Only one type?
        if (pkmn.secondaryType == pkmn.primaryType) {
            pkmn.secondaryType = null;
        }
        pkmn.catchRate = rom[offset + Gen3Constants.bsCatchRateOffset] & 0xFF;
        pkmn.growthCurve = ExpCurve.fromByte(rom[offset + Gen3Constants.bsGrowthCurveOffset]);
        // Abilities
        pkmn.ability1 = rom[offset + Gen3Constants.bsAbility1Offset] & 0xFF;
        pkmn.ability2 = rom[offset + Gen3Constants.bsAbility2Offset] & 0xFF;

        // Held Items?
        int item1 = readWord(offset + Gen3Constants.bsCommonHeldItemOffset);
        int item2 = readWord(offset + Gen3Constants.bsRareHeldItemOffset);

        if (item1 == item2) {
            // guaranteed
            pkmn.guaranteedHeldItem = item1;
            pkmn.commonHeldItem = 0;
            pkmn.rareHeldItem = 0;
        } else {
            pkmn.guaranteedHeldItem = 0;
            pkmn.commonHeldItem = item1;
            pkmn.rareHeldItem = item2;
        }
        pkmn.darkGrassHeldItem = -1;

        pkmn.genderRatio = rom[offset + Gen3Constants.bsGenderRatioOffset] & 0xFF;
    }

    private void saveBasicPokeStats(Pokemon pkmn, int offset) {
        rom[offset + Gen3Constants.bsHPOffset] = (byte) pkmn.hp;
        rom[offset + Gen3Constants.bsAttackOffset] = (byte) pkmn.attack;
        rom[offset + Gen3Constants.bsDefenseOffset] = (byte) pkmn.defense;
        rom[offset + Gen3Constants.bsSpeedOffset] = (byte) pkmn.speed;
        rom[offset + Gen3Constants.bsSpAtkOffset] = (byte) pkmn.spatk;
        rom[offset + Gen3Constants.bsSpDefOffset] = (byte) pkmn.spdef;
        rom[offset + Gen3Constants.bsPrimaryTypeOffset] = Gen3Constants.typeToByte(pkmn.primaryType);
        if (pkmn.secondaryType == null) {
            rom[offset + Gen3Constants.bsSecondaryTypeOffset] = rom[offset + Gen3Constants.bsPrimaryTypeOffset];
        } else {
            rom[offset + Gen3Constants.bsSecondaryTypeOffset] = Gen3Constants.typeToByte(pkmn.secondaryType);
        }
        rom[offset + Gen3Constants.bsCatchRateOffset] = (byte) pkmn.catchRate;
        rom[offset + Gen3Constants.bsGrowthCurveOffset] = pkmn.growthCurve.toByte();

        rom[offset + Gen3Constants.bsAbility1Offset] = (byte) pkmn.ability1;
        if (pkmn.ability2 == 0) {
            // required to not break evos with random ability
            rom[offset + Gen3Constants.bsAbility2Offset] = (byte) pkmn.ability1;
        } else {
            rom[offset + Gen3Constants.bsAbility2Offset] = (byte) pkmn.ability2;
        }

        // Held items
        if (pkmn.guaranteedHeldItem > 0) {
            writeWord(offset + Gen3Constants.bsCommonHeldItemOffset, pkmn.guaranteedHeldItem);
            writeWord(offset + Gen3Constants.bsRareHeldItemOffset, pkmn.guaranteedHeldItem);
        } else {
            writeWord(offset + Gen3Constants.bsCommonHeldItemOffset, pkmn.commonHeldItem);
            writeWord(offset + Gen3Constants.bsRareHeldItemOffset, pkmn.rareHeldItem);
        }

        rom[offset + Gen3Constants.bsGenderRatioOffset] = (byte) pkmn.genderRatio;
    }

    private void loadPokemonNames() {
        int offs = getRomEntry().getValue("PokemonNames");
        int nameLen = getRomEntry().getValue("PokemonNameLength");
        int numInternalPokes = getRomEntry().getValue("PokemonCount");
        pokeNames = new String[numInternalPokes + 1];
        for (int i = 1; i <= numInternalPokes; i++) {
            pokeNames[i] = readFixedLengthString(offs + i * nameLen, nameLen);
        }
    }

    private String readString(int offset, int maxLength) {
        StringBuilder string = new StringBuilder();
        for (int c = 0; c < maxLength; c++) {
            int currChar = rom[offset + c] & 0xFF;
            if (tb[currChar] != null) {
                string.append(tb[currChar]);
            } else {
                if (currChar == Gen3Constants.textTerminator) {
                    break;
                } else if (currChar == Gen3Constants.textVariable) {
                    int nextChar = rom[offset + c + 1] & 0xFF;
                    string.append("\\v" + String.format("%02X", nextChar));
                    c++;
                } else {
                    string.append("\\x" + String.format("%02X", currChar));
                }
            }
        }
        return string.toString();
    }

    private byte[] translateString(String text) {
        List<Byte> data = new ArrayList<Byte>();
        while (text.length() != 0) {
            int i = Math.max(0, 4 - text.length());
            if (text.charAt(0) == '\\' && text.charAt(1) == 'x') {
                data.add((byte) Integer.parseInt(text.substring(2, 4), 16));
                text = text.substring(4);
            } else if (text.charAt(0) == '\\' && text.charAt(1) == 'v') {
                data.add((byte) Gen3Constants.textVariable);
                data.add((byte) Integer.parseInt(text.substring(2, 4), 16));
                text = text.substring(4);
            } else {
                while (!(d.containsKey(text.substring(0, 4 - i)) || (i == 4))) {
                    i++;
                }
                if (i == 4) {
                    text = text.substring(1);
                } else {
                    data.add(d.get(text.substring(0, 4 - i)));
                    text = text.substring(4 - i);
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

    private void writeFixedLengthString(String str, int offset, int length) {
        byte[] translated = translateString(str);
        int len = Math.min(translated.length, length);
        System.arraycopy(translated, 0, rom, offset, len);
        if (len < length) {
            rom[offset + len] = (byte) Gen3Constants.textTerminator;
            len++;
        }
        while (len < length) {
            rom[offset + len] = 0;
            len++;
        }
    }

    private void writeVariableLengthString(String str, int offset) {
        byte[] translated = translateString(str);
        System.arraycopy(translated, 0, rom, offset, translated.length);
        rom[offset + translated.length] = (byte) 0xFF;
    }

    private int lengthOfStringAt(int offset) {
        int len = 0;
        while ((rom[offset + (len++)] & 0xFF) != 0xFF) {
        }
        return len - 1;
    }

    public byte[] traduire(String str) {
        return translateString(str);
    }

    private static boolean romName(byte[] rom, String name) {
        try {
            int sigOffset = Gen3Constants.romNameOffset;
            byte[] sigBytes = name.getBytes("US-ASCII");
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

    private static boolean romCode(byte[] rom, String codeToCheck) {
        try {
            int sigOffset = Gen3Constants.romCodeOffset;
            byte[] sigBytes = codeToCheck.getBytes("US-ASCII");
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

    private int readPointer(int offset) {
        return readLong(offset) - 0x8000000;
    }

    private int readLong(int offset) {
        return (rom[offset] & 0xFF) + ((rom[offset + 1] & 0xFF) << 8) + ((rom[offset + 2] & 0xFF) << 16)
                + (((rom[offset + 3] & 0xFF)) << 24);
    }

    private void writePointer(int offset, int pointer) {
        writeLong(offset, pointer + 0x8000000);
    }

    private void writeLong(int offset, int value) {
        rom[offset] = (byte) (value & 0xFF);
        rom[offset + 1] = (byte) ((value >> 8) & 0xFF);
        rom[offset + 2] = (byte) ((value >> 16) & 0xFF);
        rom[offset + 3] = (byte) (((value >> 24) & 0xFF));
    }

    @Override
    public List<Pokemon> getStarters() {
        List<Pokemon> starters = new ArrayList<Pokemon>();
        int baseOffset = getRomEntry().getValue("StarterPokemon");
        if (getRomEntry().romType == Gen3Constants.RomType_Ruby || getRomEntry().romType == Gen3Constants.RomType_Sapp
                || getRomEntry().romType == Gen3Constants.RomType_Em) {
            // do something
            Pokemon starter1 = pokesInternal[readWord(baseOffset)];
            Pokemon starter2 = pokesInternal[readWord(baseOffset + Gen3Constants.rseStarter2Offset)];
            Pokemon starter3 = pokesInternal[readWord(baseOffset + Gen3Constants.rseStarter3Offset)];
            starters.add(starter1);
            starters.add(starter2);
            starters.add(starter3);
        } else {
            // do something else
            Pokemon starter1 = pokesInternal[readWord(baseOffset)];
            Pokemon starter2 = pokesInternal[readWord(baseOffset + Gen3Constants.frlgStarter2Offset)];
            Pokemon starter3 = pokesInternal[readWord(baseOffset + Gen3Constants.frlgStarter3Offset)];
            starters.add(starter1);
            starters.add(starter2);
            starters.add(starter3);
        }
        return starters;
    }

    @Override
    public boolean setStarters(List<Pokemon> newStarters) {
        if (newStarters.size() != 3) {
            return false;
        }

        // Support Deoxys/Mew starters in E/FR/LG
        attemptObedienceEvolutionPatches();
        int baseOffset = getRomEntry().getValue("StarterPokemon");

        int starter0 = getPokedexToInternal()[newStarters.get(0).number];
        int starter1 = getPokedexToInternal()[newStarters.get(1).number];
        int starter2 = getPokedexToInternal()[newStarters.get(2).number];
        if (getRomEntry().romType == Gen3Constants.RomType_Ruby || getRomEntry().romType == Gen3Constants.RomType_Sapp
                || getRomEntry().romType == Gen3Constants.RomType_Em) {

            // US
            // order: 0, 1, 2
            writeWord(baseOffset, starter0);
            writeWord(baseOffset + Gen3Constants.rseStarter2Offset, starter1);
            writeWord(baseOffset + Gen3Constants.rseStarter3Offset, starter2);

        } else {

            // frlg:

            // US
            // order: 0, 1, 2
            writeWord(baseOffset, starter0);
            writeWord(baseOffset + Gen3Constants.frlgStarterRepeatOffset, starter1);

            writeWord(baseOffset + Gen3Constants.frlgStarter2Offset, starter1);
            writeWord(baseOffset + Gen3Constants.frlgStarter2Offset + Gen3Constants.frlgStarterRepeatOffset, starter2);

            writeWord(baseOffset + Gen3Constants.frlgStarter3Offset, starter2);
            writeWord(baseOffset + Gen3Constants.frlgStarter3Offset + Gen3Constants.frlgStarterRepeatOffset, starter0);

            if (getRomEntry().romCode.charAt(3) != 'J' && getRomEntry().romCode.charAt(3) != 'B') {
                // Update PROF. Oak's descriptions for each starter
                // First result for each STARTERNAME is the text we need
                writeFRLGStarterText(pokes[Gen3Constants.frlgBaseStarter1].name, newStarters.get(0),
                        "you want to go with\\nthe ");
                writeFRLGStarterText(pokes[Gen3Constants.frlgBaseStarter2].name, newStarters.get(1),
                        "you’re claiming the\\n");
                writeFRLGStarterText(pokes[Gen3Constants.frlgBaseStarter3].name, newStarters.get(2),
                        "you’ve decided on the\\n");
            }
        }
        return true;

    }

    @Override
    public List<Integer> getStarterHeldItems() {
        List<Integer> sHeldItems = new ArrayList<Integer>();
        if (getRomEntry().romType == Gen3Constants.RomType_FRLG) {
            // offset from normal starter offset as a word
            int baseOffset = getRomEntry().getValue("StarterPokemon");
            sHeldItems.add(readWord(baseOffset + Gen3Constants.frlgStarterItemsOffset));
        } else {
            int baseOffset = getRomEntry().getValue("StarterItems");
            int i1 = rom[baseOffset] & 0xFF;
            int i2 = rom[baseOffset + 2] & 0xFF;
            if (i2 == 0) {
                sHeldItems.add(i1);
            } else {
                sHeldItems.add(i2 + 0xFF);
            }
        }
        return sHeldItems;
    }

    @Override
    public void setStarterHeldItems(List<Integer> items) {
        if (items.size() != 1) {
            return;
        }
        int item = items.get(0);
        if (getRomEntry().romType == Gen3Constants.RomType_FRLG) {
            // offset from normal starter offset as a word
            int baseOffset = getRomEntry().getValue("StarterPokemon");
            writeWord(baseOffset + Gen3Constants.frlgStarterItemsOffset, item);
        } else {
            int baseOffset = getRomEntry().getValue("StarterItems");
            if (item <= 0xFF) {
                rom[baseOffset] = (byte) item;
                rom[baseOffset + 2] = 0;
                rom[baseOffset + 3] = Gen3Constants.gbaAddRxOpcode | Gen3Constants.gbaR2;
            } else {
                rom[baseOffset] = (byte) 0xFF;
                rom[baseOffset + 2] = (byte) (item - 0xFF);
                rom[baseOffset + 3] = Gen3Constants.gbaAddRxOpcode | Gen3Constants.gbaR2;
            }
        }
    }

    private void writeFRLGStarterText(String findName, Pokemon pkmn, String oakText) {
        List<Integer> foundTexts = RomFunctions.search(rom, traduire(findName));
        if (foundTexts.size() > 0) {
            int offset = foundTexts.get(0);
            String pokeName = pkmn.name;
            String pokeType = pkmn.primaryType == null ? "???" : pkmn.primaryType.toString();
            if (pokeType.equals("NORMAL") && pkmn.secondaryType != null) {
                pokeType = pkmn.secondaryType.toString();
            }
            String speech = pokeName + " is your choice.\\pSo, \\v01, " + oakText + pokeType + " POKéMON " + pokeName
                    + "?";
            writeFixedLengthString(speech, offset, lengthOfStringAt(offset) + 1);
        }
    }

    @Override
    public List<EncounterSet> getEncounters(boolean useTimeOfDay) {
        if (!mapLoadingDone) {
            preprocessMaps();
            mapLoadingDone = true;
        }

        int startOffs = getRomEntry().getValue("WildPokemon");
        List<EncounterSet> encounterAreas = new ArrayList<EncounterSet>();
        Set<Integer> seenOffsets = new TreeSet<Integer>();
        int offs = startOffs;
        while (true) {
            // Read pointers
            int bank = rom[offs] & 0xFF;
            int map = rom[offs + 1] & 0xFF;
            if (bank == 0xFF && map == 0xFF) {
                break;
            }

            String mapName = mapNames[bank][map];

            int grassPokes = readPointer(offs + 4);
            int waterPokes = readPointer(offs + 8);
            int treePokes = readPointer(offs + 12);
            int fishPokes = readPointer(offs + 16);

            // Add pokemanz
            if (grassPokes >= 0 && grassPokes < rom.length && rom[grassPokes] != 0
                    && !seenOffsets.contains(readPointer(grassPokes + 4))) {
                encounterAreas.add(readWildArea(grassPokes, Gen3Constants.grassSlots, mapName + " Grass/Cave"));
                seenOffsets.add(readPointer(grassPokes + 4));
            }
            if (waterPokes >= 0 && waterPokes < rom.length && rom[waterPokes] != 0
                    && !seenOffsets.contains(readPointer(waterPokes + 4))) {
                encounterAreas.add(readWildArea(waterPokes, Gen3Constants.surfingSlots, mapName + " Surfing"));
                seenOffsets.add(readPointer(waterPokes + 4));
            }
            if (treePokes >= 0 && treePokes < rom.length && rom[treePokes] != 0
                    && !seenOffsets.contains(readPointer(treePokes + 4))) {
                encounterAreas.add(readWildArea(treePokes, Gen3Constants.rockSmashSlots, mapName + " Rock Smash"));
                seenOffsets.add(readPointer(treePokes + 4));
            }
            if (fishPokes >= 0 && fishPokes < rom.length && rom[fishPokes] != 0
                    && !seenOffsets.contains(readPointer(fishPokes + 4))) {
                encounterAreas.add(readWildArea(fishPokes, Gen3Constants.fishingSlots, mapName + " Fishing"));
                seenOffsets.add(readPointer(fishPokes + 4));
            }

            offs += 20;
        }
        if (getRomEntry().arrayEntries.containsKey("BattleTrappersBanned")) {
            // Some encounter sets aren't allowed to have Pokemon
            // with Arena Trap, Shadow Tag etc.
            int[] bannedAreas = getRomEntry().arrayEntries.get("BattleTrappersBanned");
            Set<Pokemon> battleTrappers = new HashSet<Pokemon>();
            for (Pokemon pk : getPokemon()) {
                if (hasBattleTrappingAbility(pk)) {
                    battleTrappers.add(pk);
                }
            }
            for (int areaIdx : bannedAreas) {
                encounterAreas.get(areaIdx).bannedPokemon.addAll(battleTrappers);
            }
        }
        return encounterAreas;
    }

    private boolean hasBattleTrappingAbility(Pokemon pokemon) {
        return pokemon != null
                && (GlobalConstants.battleTrappingAbilities.contains(pokemon.ability1) || GlobalConstants.battleTrappingAbilities
                        .contains(pokemon.ability2));
    }

    private EncounterSet readWildArea(int offset, int numOfEntries, String setName) {
        EncounterSet thisSet = new EncounterSet();
        thisSet.rate = rom[offset];
        thisSet.displayName = setName;
        // Grab the *real* pointer to data
        int dataOffset = readPointer(offset + 4);
        // Read the entries
        for (int i = 0; i < numOfEntries; i++) {
            // min, max, species, species
            Encounter enc = new Encounter();
            enc.level = rom[dataOffset + i * 4];
            enc.maxLevel = rom[dataOffset + i * 4 + 1];
            try {
                enc.pokemon = pokesInternal[readWord(dataOffset + i * 4 + 2)];
            } catch (ArrayIndexOutOfBoundsException ex) {
                throw ex;
            }
            thisSet.encounters.add(enc);
        }
        return thisSet;
    }

    @Override
    public void setEncounters(boolean useTimeOfDay, List<EncounterSet> encounters) {
        // Support Deoxys/Mew catches in E/FR/LG
        attemptObedienceEvolutionPatches();

        int startOffs = getRomEntry().getValue("WildPokemon");
        Iterator<EncounterSet> encounterAreas = encounters.iterator();
        Set<Integer> seenOffsets = new TreeSet<Integer>();
        int offs = startOffs;
        while (true) {
            // Read pointers
            int bank = rom[offs] & 0xFF;
            int map = rom[offs + 1] & 0xFF;
            if (bank == 0xFF && map == 0xFF) {
                break;
            }

            int grassPokes = readPointer(offs + 4);
            int waterPokes = readPointer(offs + 8);
            int treePokes = readPointer(offs + 12);
            int fishPokes = readPointer(offs + 16);

            // Add pokemanz
            if (grassPokes >= 0 && grassPokes < rom.length && rom[grassPokes] != 0
                    && !seenOffsets.contains(readPointer(grassPokes + 4))) {
                writeWildArea(grassPokes, Gen3Constants.grassSlots, encounterAreas.next());
                seenOffsets.add(readPointer(grassPokes + 4));
            }
            if (waterPokes >= 0 && waterPokes < rom.length && rom[waterPokes] != 0
                    && !seenOffsets.contains(readPointer(waterPokes + 4))) {
                writeWildArea(waterPokes, Gen3Constants.surfingSlots, encounterAreas.next());
                seenOffsets.add(readPointer(waterPokes + 4));
            }
            if (treePokes >= 0 && treePokes < rom.length && rom[treePokes] != 0
                    && !seenOffsets.contains(readPointer(treePokes + 4))) {
                writeWildArea(treePokes, Gen3Constants.rockSmashSlots, encounterAreas.next());
                seenOffsets.add(readPointer(treePokes + 4));
            }
            if (fishPokes >= 0 && fishPokes < rom.length && rom[fishPokes] != 0
                    && !seenOffsets.contains(readPointer(fishPokes + 4))) {
                writeWildArea(fishPokes, Gen3Constants.fishingSlots, encounterAreas.next());
                seenOffsets.add(readPointer(fishPokes + 4));
            }

            offs += 20;
        }
    }

    @Override
    public List<Pokemon> bannedForWildEncounters() {
        return Arrays.asList(pokes[Gen3Constants.unownIndex]); // Unown banned
    }

    @Override
    public List<Trainer> getTrainers() {
        int baseOffset = getRomEntry().getValue("TrainerData");
        int amount = getRomEntry().getValue("TrainerCount");
        int entryLen = getRomEntry().getValue("TrainerEntrySize");
        List<Trainer> theTrainers = new ArrayList<Trainer>();
        List<String> tcnames = this.getTrainerClassNames();
        for (int i = 1; i < amount; i++) {
            int trOffset = baseOffset + i * entryLen;
            Trainer tr = new Trainer();
            tr.offset = trOffset;
            int trainerclass = rom[trOffset + 1] & 0xFF;
            tr.trainerclass = (rom[trOffset + 2] & 0x80) > 0 ? 1 : 0;

            int pokeDataType = rom[trOffset] & 0xFF;
            int numPokes = rom[trOffset + (entryLen - 8)] & 0xFF;
            int pointerToPokes = readPointer(trOffset + (entryLen - 4));
            tr.poketype = pokeDataType;
            tr.name = this.readVariableLengthString(trOffset + 4);
            tr.fullDisplayName = tcnames.get(trainerclass) + " " + tr.name;
            // Pokemon data!
            if (pokeDataType == 0) {
                // blocks of 8 bytes
                for (int poke = 0; poke < numPokes; poke++) {
                    TrainerPokemon thisPoke = new TrainerPokemon();
                    thisPoke.AILevel = readWord(pointerToPokes + poke * 8);
                    thisPoke.level = readWord(pointerToPokes + poke * 8 + 2);
                    thisPoke.pokemon = pokesInternal[readWord(pointerToPokes + poke * 8 + 4)];
                    tr.pokemon.add(thisPoke);
                }
            } else if (pokeDataType == 2) {
                // blocks of 8 bytes
                for (int poke = 0; poke < numPokes; poke++) {
                    TrainerPokemon thisPoke = new TrainerPokemon();
                    thisPoke.AILevel = readWord(pointerToPokes + poke * 8);
                    thisPoke.level = readWord(pointerToPokes + poke * 8 + 2);
                    thisPoke.pokemon = pokesInternal[readWord(pointerToPokes + poke * 8 + 4)];
                    thisPoke.heldItem = readWord(pointerToPokes + poke * 8 + 6);
                    tr.pokemon.add(thisPoke);
                }
            } else if (pokeDataType == 1) {
                // blocks of 16 bytes
                for (int poke = 0; poke < numPokes; poke++) {
                    TrainerPokemon thisPoke = new TrainerPokemon();
                    thisPoke.AILevel = readWord(pointerToPokes + poke * 16);
                    thisPoke.level = readWord(pointerToPokes + poke * 16 + 2);
                    thisPoke.pokemon = pokesInternal[readWord(pointerToPokes + poke * 16 + 4)];
                    thisPoke.move1 = readWord(pointerToPokes + poke * 16 + 6);
                    thisPoke.move2 = readWord(pointerToPokes + poke * 16 + 8);
                    thisPoke.move3 = readWord(pointerToPokes + poke * 16 + 10);
                    thisPoke.move4 = readWord(pointerToPokes + poke * 16 + 12);
                    tr.pokemon.add(thisPoke);
                }
            } else if (pokeDataType == 3) {
                // blocks of 16 bytes
                for (int poke = 0; poke < numPokes; poke++) {
                    TrainerPokemon thisPoke = new TrainerPokemon();
                    thisPoke.AILevel = readWord(pointerToPokes + poke * 16);
                    thisPoke.level = readWord(pointerToPokes + poke * 16 + 2);
                    thisPoke.pokemon = pokesInternal[readWord(pointerToPokes + poke * 16 + 4)];
                    thisPoke.heldItem = readWord(pointerToPokes + poke * 16 + 6);
                    thisPoke.move1 = readWord(pointerToPokes + poke * 16 + 8);
                    thisPoke.move2 = readWord(pointerToPokes + poke * 16 + 10);
                    thisPoke.move3 = readWord(pointerToPokes + poke * 16 + 12);
                    thisPoke.move4 = readWord(pointerToPokes + poke * 16 + 14);
                    tr.pokemon.add(thisPoke);
                }
            }
            theTrainers.add(tr);
        }

        if (getRomEntry().romType == Gen3Constants.RomType_Ruby || getRomEntry().romType == Gen3Constants.RomType_Sapp) {
            Gen3Constants.trainerTagsRS(theTrainers, getRomEntry().romType);
        } else if (getRomEntry().romType == Gen3Constants.RomType_Em) {
            Gen3Constants.trainerTagsE(theTrainers);
        } else {
            Gen3Constants.trainerTagsFRLG(theTrainers);
        }
        return theTrainers;
    }

    @Override
    public void setTrainers(List<Trainer> trainerData) {
        int baseOffset = getRomEntry().getValue("TrainerData");
        int amount = getRomEntry().getValue("TrainerCount");
        int entryLen = getRomEntry().getValue("TrainerEntrySize");
        Iterator<Trainer> theTrainers = trainerData.iterator();
        int fso = getRomEntry().getValue("FreeSpace");

        // Get current movesets in case we need to reset them for certain
        // trainer mons.
        Map<Pokemon, List<MoveLearnt>> movesets = this.getMovesLearnt();

        for (int i = 1; i < amount; i++) {
            int trOffset = baseOffset + i * entryLen;
            Trainer tr = theTrainers.next();
            // Do we need to repoint this trainer's data?
            int oldPokeType = rom[trOffset] & 0xFF;
            int oldPokeCount = rom[trOffset + (entryLen - 8)] & 0xFF;
            int newPokeCount = tr.pokemon.size();
            int newDataSize = newPokeCount * ((tr.poketype & 1) == 1 ? 16 : 8);
            int oldDataSize = oldPokeCount * ((oldPokeType & 1) == 1 ? 16 : 8);

            // write out new data first...
            rom[trOffset] = (byte) tr.poketype;
            rom[trOffset + (entryLen - 8)] = (byte) newPokeCount;

            // now, do we need to repoint?
            int pointerToPokes;
            if (newDataSize > oldDataSize) {
                int writeSpace = RomFunctions.freeSpaceFinder(rom, Gen3Constants.freeSpaceByte, newDataSize, fso, true);
                if (writeSpace < fso) {
                    throw new RandomizerIOException("ROM is full");
                }
                writePointer(trOffset + (entryLen - 4), writeSpace);
                pointerToPokes = writeSpace;
            } else {
                pointerToPokes = readPointer(trOffset + (entryLen - 4));
            }

            Iterator<TrainerPokemon> pokes = tr.pokemon.iterator();

            // Write out Pokemon data!
            if ((tr.poketype & 1) == 1) {
                // custom moves, blocks of 16 bytes
                for (int poke = 0; poke < newPokeCount; poke++) {
                    TrainerPokemon tp = pokes.next();
                    writeWord(pointerToPokes + poke * 16, tp.AILevel);
                    writeWord(pointerToPokes + poke * 16 + 2, tp.level);
                    writeWord(pointerToPokes + poke * 16 + 4, getPokedexToInternal()[tp.pokemon.number]);
                    int movesStart;
                    if ((tr.poketype & 2) == 2) {
                        writeWord(pointerToPokes + poke * 16 + 6, tp.heldItem);
                        movesStart = 8;
                    } else {
                        movesStart = 6;
                        writeWord(pointerToPokes + poke * 16 + 14, 0);
                    }
                    if (tp.resetMoves) {
                        int[] pokeMoves = RomFunctions.getMovesAtLevel(tp.pokemon, movesets, tp.level);
                        for (int m = 0; m < 4; m++) {
                            writeWord(pointerToPokes + poke * 16 + movesStart + m * 2, pokeMoves[m]);
                        }
                    } else {
                        writeWord(pointerToPokes + poke * 16 + movesStart, tp.move1);
                        writeWord(pointerToPokes + poke * 16 + movesStart + 2, tp.move2);
                        writeWord(pointerToPokes + poke * 16 + movesStart + 4, tp.move3);
                        writeWord(pointerToPokes + poke * 16 + movesStart + 6, tp.move4);
                    }
                }
            } else {
                // no moves, blocks of 8 bytes
                for (int poke = 0; poke < newPokeCount; poke++) {
                    TrainerPokemon tp = pokes.next();
                    writeWord(pointerToPokes + poke * 8, tp.AILevel);
                    writeWord(pointerToPokes + poke * 8 + 2, tp.level);
                    writeWord(pointerToPokes + poke * 8 + 4, getPokedexToInternal()[tp.pokemon.number]);
                    if ((tr.poketype & 2) == 2) {
                        writeWord(pointerToPokes + poke * 8 + 6, tp.heldItem);
                    } else {
                        writeWord(pointerToPokes + poke * 8 + 6, 0);
                    }
                }
            }
        }

    }

    private void writeWildArea(int offset, int numOfEntries, EncounterSet encounters) {
        // Grab the *real* pointer to data
        int dataOffset = readPointer(offset + 4);
        // Write the entries
        for (int i = 0; i < numOfEntries; i++) {
            Encounter enc = encounters.encounters.get(i);
            // min, max, species, species
            writeWord(dataOffset + i * 4 + 2, getPokedexToInternal()[enc.pokemon.number]);
        }
    }

    @Override
    public List<Pokemon> getPokemon() {
        return pokemonList;
    }

    @Override
    public Map<Pokemon, List<MoveLearnt>> getMovesLearnt() {
        Map<Pokemon, List<MoveLearnt>> movesets = new TreeMap<Pokemon, List<MoveLearnt>>();
        int baseOffset = getRomEntry().getValue("PokemonMovesets");
        for (int i = 1; i <= numRealPokemon; i++) {
            Pokemon pkmn = pokemonList.get(i);
            int offsToPtr = baseOffset + (getPokedexToInternal()[pkmn.number]) * 4;
            int moveDataLoc = readPointer(offsToPtr);
            List<MoveLearnt> moves = new ArrayList<MoveLearnt>();
            if (jamboMovesetHack) {
                while ((rom[moveDataLoc] & 0xFF) != 0x00 || (rom[moveDataLoc + 1] & 0xFF) != 0x00
                        || (rom[moveDataLoc + 2] & 0xFF) != 0xFF) {
                    MoveLearnt ml = new MoveLearnt();
                    ml.level = rom[moveDataLoc + 2] & 0xFF;
                    ml.move = readWord(moveDataLoc);
                    moves.add(ml);
                    moveDataLoc += 3;
                }
            } else {
                while ((rom[moveDataLoc] & 0xFF) != 0xFF || (rom[moveDataLoc + 1] & 0xFF) != 0xFF) {
                    int move = (rom[moveDataLoc] & 0xFF);
                    int level = (rom[moveDataLoc + 1] & 0xFE) >> 1;
                    if ((rom[moveDataLoc + 1] & 0x01) == 0x01) {
                        move += 0x100;
                    }
                    MoveLearnt ml = new MoveLearnt();
                    ml.level = level;
                    ml.move = move;
                    moves.add(ml);
                    moveDataLoc += 2;
                }
            }
            movesets.put(pkmn, moves);
        }
        return movesets;
    }

    @Override
    public void setMovesLearnt(Map<Pokemon, List<MoveLearnt>> movesets) {
        int baseOffset = getRomEntry().getValue("PokemonMovesets");
        int fso = getRomEntry().getValue("FreeSpace");
        for (int i = 1; i <= numRealPokemon; i++) {
            Pokemon pkmn = pokemonList.get(i);
            int offsToPtr = baseOffset + (getPokedexToInternal()[pkmn.number]) * 4;
            int moveDataLoc = readPointer(offsToPtr);
            List<MoveLearnt> moves = movesets.get(pkmn);
            int newMoveCount = moves.size();
            int mloc = moveDataLoc;
            int entrySize;
            if (jamboMovesetHack) {
                while ((rom[mloc] & 0xFF) != 0x00 || (rom[mloc + 1] & 0xFF) != 0x00 || (rom[mloc + 2] & 0xFF) != 0xFF) {
                    mloc += 3;
                }
                entrySize = 3;
            } else {
                while ((rom[mloc] & 0xFF) != 0xFF || (rom[mloc + 1] & 0xFF) != 0xFF) {
                    mloc += 2;
                }
                entrySize = 2;
            }
            int currentMoveCount = (mloc - moveDataLoc) / entrySize;

            if (newMoveCount > currentMoveCount) {
                // Repoint for more space
                int newBytesNeeded = newMoveCount * entrySize + entrySize * 2;
                int writeSpace = RomFunctions.freeSpaceFinder(rom, Gen3Constants.freeSpaceByte, newBytesNeeded, fso);
                if (writeSpace < fso) {
                    throw new RandomizerIOException("ROM is full");
                }
                writePointer(offsToPtr, writeSpace);
                moveDataLoc = writeSpace;
            }

            // Write new moveset now that space is ensured.
            for (int mv = 0; mv < newMoveCount; mv++) {
                MoveLearnt ml = moves.get(mv);
                moveDataLoc += writeMLToOffset(moveDataLoc, ml);
            }

            // If move count changed, new terminator is required
            // In the repoint enough space was reserved to add some padding to
            // make sure the terminator isn't detected as free space.
            // If no repoint, the padding goes over the old moves/terminator.
            if (newMoveCount != currentMoveCount) {
                if (jamboMovesetHack) {
                    rom[moveDataLoc] = 0x00;
                    rom[moveDataLoc + 1] = 0x00;
                    rom[moveDataLoc + 2] = (byte) 0xFF;
                    rom[moveDataLoc + 3] = 0x00;
                    rom[moveDataLoc + 4] = 0x00;
                    rom[moveDataLoc + 5] = 0x00;
                } else {
                    rom[moveDataLoc] = (byte) 0xFF;
                    rom[moveDataLoc + 1] = (byte) 0xFF;
                    rom[moveDataLoc + 2] = 0x00;
                    rom[moveDataLoc + 3] = 0x00;
                }
            }

        }

    }

    private int writeMLToOffset(int offset, MoveLearnt ml) {
        if (jamboMovesetHack) {
            writeWord(offset, ml.move);
            rom[offset + 2] = (byte) ml.level;
            return 3;
        } else {
            rom[offset] = (byte) (ml.move & 0xFF);
            int levelPart = (ml.level << 1) & 0xFE;
            if (ml.move > 255) {
                levelPart++;
            }
            rom[offset + 1] = (byte) levelPart;
            return 2;
        }
    }

    private static class StaticPokemon {
        private int[] offsets;

        public StaticPokemon(int... offsets) {
            this.offsets = offsets;
        }

        public Pokemon getPokemon(Gen3RomHandler parent) {
            return parent.getPokesInternal()[parent.readWord(offsets[0])];
        }

        public void setPokemon(Gen3RomHandler parent, Pokemon pkmn) {
            int value = parent.getPokedexToInternal()[pkmn.number];
            for (int offset : offsets) {
                parent.writeWord(offset, value);
            }
        }
    }

    @Override
    public List<Pokemon> getStaticPokemon() {
        List<Pokemon> statics = new ArrayList<Pokemon>();
        List<StaticPokemon> staticsHere = getRomEntry().staticPokemon;
        for (StaticPokemon staticPK : staticsHere) {
            statics.add(staticPK.getPokemon(this));
        }
        return statics;
    }

    @Override
    public boolean setStaticPokemon(List<Pokemon> staticPokemon) {
        // Support Deoxys/Mew gifts/catches in E/FR/LG
        attemptObedienceEvolutionPatches();

        List<StaticPokemon> staticsHere = getRomEntry().staticPokemon;
        if (staticPokemon.size() != staticsHere.size()) {
            return false;
        }

        for (int i = 0; i < staticsHere.size(); i++) {
            staticsHere.get(i).setPokemon(this, staticPokemon.get(i));
        }
        return true;
    }

    @Override
    public List<Integer> getTMMoves() {
        List<Integer> tms = new ArrayList<Integer>();
        int offset = getRomEntry().getValue("TmMoves");
        for (int i = 1; i <= Gen3Constants.tmCount; i++) {
            tms.add(readWord(offset + (i - 1) * 2));
        }
        return tms;
    }

    @Override
    public List<Integer> getHMMoves() {
        return Gen3Constants.hmMoves;
    }

    @Override
    public void setTMMoves(List<Integer> moveIndexes) {
        if (!mapLoadingDone) {
            preprocessMaps();
            mapLoadingDone = true;
        }
        int offset = getRomEntry().getValue("TmMoves");
        for (int i = 1; i <= Gen3Constants.tmCount; i++) {
            writeWord(offset + (i - 1) * 2, moveIndexes.get(i - 1));
        }
        int otherOffset = getRomEntry().getValue("TmMovesDuplicate");
        if (otherOffset > 0) {
            // Emerald/FR/LG have *two* TM tables
            System.arraycopy(rom, offset, rom, otherOffset, Gen3Constants.tmCount * 2);
        }

        int iiOffset = getRomEntry().getValue("ItemImages");
        if (iiOffset > 0) {
            int[] pals = getRomEntry().arrayEntries.get("TmPals");
            // Update the item image palettes
            // Gen3 TMs are 289-338
            for (int i = 0; i < 50; i++) {
                Move mv = moves[moveIndexes.get(i)];
                int typeID = Gen3Constants.typeToByte(mv.type);
                writePointer(iiOffset + (Gen3Constants.tmItemOffset + i) * 8 + 4, pals[typeID]);
            }
        }

        int fsOffset = getRomEntry().getValue("FreeSpace");

        // Item descriptions
        if (getRomEntry().getValue("MoveDescriptions") > 0) {
            // JP blocked for now - uses different item structure anyway
            int idOffset = getRomEntry().getValue("ItemData");
            int mdOffset = getRomEntry().getValue("MoveDescriptions");
            int entrySize = getRomEntry().getValue("ItemEntrySize");
            int limitPerLine = (getRomEntry().romType == Gen3Constants.RomType_FRLG) ? Gen3Constants.frlgItemDescCharsPerLine
                    : Gen3Constants.rseItemDescCharsPerLine;
            for (int i = 0; i < Gen3Constants.tmCount; i++) {
                int itemBaseOffset = idOffset + (i + Gen3Constants.tmItemOffset) * entrySize;
                int moveBaseOffset = mdOffset + (moveIndexes.get(i) - 1) * 4;
                int moveTextPointer = readPointer(moveBaseOffset);
                String moveDesc = readVariableLengthString(moveTextPointer);
                String newItemDesc = RomFunctions.rewriteDescriptionForNewLineSize(moveDesc, "\\n", limitPerLine, ssd);
                // Find freespace
                int fsBytesNeeded = translateString(newItemDesc).length + 1;
                int newItemDescOffset = RomFunctions.freeSpaceFinder(rom, Gen3Constants.freeSpaceByte, fsBytesNeeded,
                        fsOffset);
                if (newItemDescOffset < fsOffset) {
                    this.getTemplateData().put("TMTextFailure", "Couldn't insert new item description.");
                    return;
                }
                writeVariableLengthString(newItemDesc, newItemDescOffset);
                writePointer(itemBaseOffset + Gen3Constants.itemDataDescriptionOffset, newItemDescOffset);
            }
        }

        // TM Text?
        for (TMOrMTTextEntry tte : getRomEntry().tmmtTexts) {
            if (tte.actualOffset > 0 && !tte.isMoveTutor) {
                // create the new TM text
                int oldPointer = readPointer(tte.actualOffset);
                if (oldPointer < 0 || oldPointer >= rom.length) {
                    this.getTemplateData().put("TMTextFailure", "Couldn't insert new TM text. Skipping remaining TM text updates." );
                    return;
                }
                String moveName = this.moves[moveIndexes.get(tte.number - 1)].name;
                // temporarily use underscores to stop the move name being split
                String tmpMoveName = moveName.replace(' ', '_');
                String unformatted = tte.template.replace("[move]", tmpMoveName);
                String newText = RomFunctions.formatTextWithReplacements(unformatted, null, "\\n", "\\l", "\\p",
                        Gen3Constants.regularTextboxCharsPerLine, ssd);
                // get rid of the underscores
                newText = newText.replace(tmpMoveName, moveName);
                // insert the new text into free space
                int fsBytesNeeded = translateString(newText).length + 1;
                int newOffset = RomFunctions.freeSpaceFinder(rom, (byte) 0xFF, fsBytesNeeded, fsOffset);
                if (newOffset < fsOffset) {
                    this.getTemplateData().put("TMTextFailure", "Couldn't insert new TM text.");
                    return;
                }
                writeVariableLengthString(newText, newOffset);
                // search for copies of the pointer:
                // make a needle of the pointer
                byte[] searchNeedle = new byte[4];
                System.arraycopy(rom, tte.actualOffset, searchNeedle, 0, 4);
                // find copies within 500 bytes either way of actualOffset
                int minOffset = Math.max(0, tte.actualOffset - Gen3Constants.pointerSearchRadius);
                int maxOffset = Math.min(rom.length, tte.actualOffset + Gen3Constants.pointerSearchRadius);
                List<Integer> pointerLocs = RomFunctions.search(rom, minOffset, maxOffset, searchNeedle);
                for (int pointerLoc : pointerLocs) {
                    // write the new pointer
                    writePointer(pointerLoc, newOffset);
                }
            }
        }
    }

    private RomFunctions.StringSizeDeterminer ssd = new RomFunctions.StringSizeDeterminer() {

        @Override
        public int lengthFor(String encodedText) {
            return translateString(encodedText).length;
        }
    };

    @Override
    public int getTMCount() {
        return Gen3Constants.tmCount;
    }

    @Override
    public int getHMCount() {
        return Gen3Constants.hmCount;
    }

    @Override
    public Map<Pokemon, boolean[]> getTMHMCompatibility() {
        Map<Pokemon, boolean[]> compat = new TreeMap<Pokemon, boolean[]>();
        int offset = getRomEntry().getValue("PokemonTMHMCompat");
        for (int i = 1; i <= numRealPokemon; i++) {
            Pokemon pkmn = pokemonList.get(i);
            int compatOffset = offset + (getPokedexToInternal()[pkmn.number]) * 8;
            boolean[] flags = new boolean[Gen3Constants.tmCount + Gen3Constants.hmCount + 1];
            for (int j = 0; j < 8; j++) {
                readByteIntoFlags(flags, j * 8 + 1, compatOffset + j);
            }
            compat.put(pkmn, flags);
        }
        return compat;
    }

    @Override
    public void setTMHMCompatibility(Map<Pokemon, boolean[]> compatData) {
        int offset = getRomEntry().getValue("PokemonTMHMCompat");
        for (Map.Entry<Pokemon, boolean[]> compatEntry : compatData.entrySet()) {
            Pokemon pkmn = compatEntry.getKey();
            boolean[] flags = compatEntry.getValue();
            int compatOffset = offset + (getPokedexToInternal()[pkmn.number]) * 8;
            for (int j = 0; j < 8; j++) {
                rom[compatOffset + j] = getByteFromFlags(flags, j * 8 + 1);
            }
        }
    }

    @Override
    public boolean hasMoveTutors() {
        return (getRomEntry().romType == Gen3Constants.RomType_Em || getRomEntry().romType == Gen3Constants.RomType_FRLG);
    }

    @Override
    public List<Integer> getMoveTutorMoves() {
        if (!hasMoveTutors()) {
            return new ArrayList<Integer>();
        }
        List<Integer> mts = new ArrayList<Integer>();
        int moveCount = getRomEntry().getValue("MoveTutorMoves");
        int offset = getRomEntry().getValue("MoveTutorData");
        for (int i = 0; i < moveCount; i++) {
            mts.add(readWord(offset + i * 2));
        }
        return mts;
    }

    @Override
    public void setMoveTutorMoves(List<Integer> moves) {
        if (!hasMoveTutors()) {
            return;
        }
        int moveCount = getRomEntry().getValue("MoveTutorMoves");
        int offset = getRomEntry().getValue("MoveTutorData");
        if (moveCount != moves.size()) {
            return;
        }
        for (int i = 0; i < moveCount; i++) {
            writeWord(offset + i * 2, moves.get(i));
        }
        int fsOffset = getRomEntry().getValue("FreeSpace");

        // Move Tutor Text?
        for (TMOrMTTextEntry tte : getRomEntry().tmmtTexts) {
            if (tte.actualOffset > 0 && tte.isMoveTutor) {
                // create the new MT text
                int oldPointer = readPointer(tte.actualOffset);
                if (oldPointer < 0 || oldPointer >= rom.length) {
                    throw new RandomizationException(
                            "Move Tutor Text update failed: couldn't read a move tutor text pointer.");
                }
                String moveName = this.moves[moves.get(tte.number)].name;
                // temporarily use underscores to stop the move name being split
                String tmpMoveName = moveName.replace(' ', '_');
                String unformatted = tte.template.replace("[move]", tmpMoveName);
                String newText = RomFunctions.formatTextWithReplacements(unformatted, null, "\\n", "\\l", "\\p",
                        Gen3Constants.regularTextboxCharsPerLine, ssd);
                // get rid of the underscores
                newText = newText.replace(tmpMoveName, moveName);
                // insert the new text into free space
                int fsBytesNeeded = translateString(newText).length + 1;
                int newOffset = RomFunctions.freeSpaceFinder(rom, Gen3Constants.freeSpaceByte, fsBytesNeeded, fsOffset);
                if (newOffset < fsOffset) {
                    this.getTemplateData().put("MoveTutorTextFailure", "Couldn't insert new Move Tutor text.");
                    return;
                }
                writeVariableLengthString(newText, newOffset);
                // search for copies of the pointer:
                // make a needle of the pointer
                byte[] searchNeedle = new byte[4];
                System.arraycopy(rom, tte.actualOffset, searchNeedle, 0, 4);
                // find copies within 500 bytes either way of actualOffset
                int minOffset = Math.max(0, tte.actualOffset - Gen3Constants.pointerSearchRadius);
                int maxOffset = Math.min(rom.length, tte.actualOffset + Gen3Constants.pointerSearchRadius);
                List<Integer> pointerLocs = RomFunctions.search(rom, minOffset, maxOffset, searchNeedle);
                for (int pointerLoc : pointerLocs) {
                    // write the new pointer
                    writePointer(pointerLoc, newOffset);
                }
            }
        }
    }

    @Override
    public Map<Pokemon, boolean[]> getMoveTutorCompatibility() {
        if (!hasMoveTutors()) {
            return new TreeMap<Pokemon, boolean[]>();
        }
        Map<Pokemon, boolean[]> compat = new TreeMap<Pokemon, boolean[]>();
        int moveCount = getRomEntry().getValue("MoveTutorMoves");
        int offset = getRomEntry().getValue("MoveTutorCompatibility");
        int bytesRequired = ((moveCount + 7) & ~7) / 8;
        for (int i = 1; i <= numRealPokemon; i++) {
            Pokemon pkmn = pokemonList.get(i);
            int compatOffset = offset + getPokedexToInternal()[pkmn.number] * bytesRequired;
            boolean[] flags = new boolean[moveCount + 1];
            for (int j = 0; j < bytesRequired; j++) {
                readByteIntoFlags(flags, j * 8 + 1, compatOffset + j);
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
        int moveCount = getRomEntry().getValue("MoveTutorMoves");
        int offset = getRomEntry().getValue("MoveTutorCompatibility");
        int bytesRequired = ((moveCount + 7) & ~7) / 8;
        for (Map.Entry<Pokemon, boolean[]> compatEntry : compatData.entrySet()) {
            Pokemon pkmn = compatEntry.getKey();
            boolean[] flags = compatEntry.getValue();
            int compatOffset = offset + getPokedexToInternal()[pkmn.number] * bytesRequired;
            for (int j = 0; j < bytesRequired; j++) {
                rom[compatOffset + j] = getByteFromFlags(flags, j * 8 + 1);
            }
        }
    }

    @Override
    public String getROMName() {
        return getRomEntry().name + (this.isRomHack ? " (ROM Hack)" : "");
    }

    @Override
    public String getROMCode() {
        return getRomEntry().romCode;
    }

    @Override
    public String getSupportLevel() {
        return (getRomEntry().getValue("StaticPokemonSupport") > 0) ? "Complete" : "No Static Pokemon";
    }

    // For dynamic offsets later
    private int find(String hexString) {
        return find(rom, hexString);
    }

    private static int find(byte[] haystack, String hexString) {
        if (hexString.length() % 2 != 0) {
            return -3; // error
        }
        byte[] searchFor = new byte[hexString.length() / 2];
        for (int i = 0; i < searchFor.length; i++) {
            searchFor[i] = (byte) Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16);
        }
        List<Integer> found = RomFunctions.search(haystack, searchFor);
        if (found.size() == 0) {
            return -1; // not found
        } else if (found.size() > 1) {
            return -2; // not unique
        } else {
            return found.get(0);
        }
    }

    private List<Integer> findMultiple(String hexString) {
        return findMultiple(rom, hexString);
    }

    private static List<Integer> findMultiple(byte[] haystack, String hexString) {
        if (hexString.length() % 2 != 0) {
            return new ArrayList<Integer>(); // error
        }
        byte[] searchFor = new byte[hexString.length() / 2];
        for (int i = 0; i < searchFor.length; i++) {
            searchFor[i] = (byte) Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16);
        }
        List<Integer> found = RomFunctions.search(haystack, searchFor);
        return found;
    }

    private void writeHexString(String hexString, int offset) {
        if (hexString.length() % 2 != 0) {
            return; // error
        }
        for (int i = 0; i < hexString.length() / 2; i++) {
            rom[offset + i] = (byte) Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16);
        }
    }

    private void attemptObedienceEvolutionPatches() {
        if (havePatchedObedience) {
            return;
        }

        havePatchedObedience = true;
        // This routine *appears* to only exist in E/FR/LG...
        // Look for the deoxys part which is
        // MOVS R1, 0x19A
        // CMP R0, R1
        // BEQ <mew/deoxys case>
        // Hex is CD214900 8842 0FD0
        int deoxysObOffset = find(Gen3Constants.deoxysObeyCode);
        if (deoxysObOffset > 0) {
            // We found the deoxys check...
            // Replacing it with MOVS R1, 0x0 would work fine.
            // This would make it so species 0x0 (glitch only) would disobey.
            // But MOVS R1, 0x0 (the version I know) is 2-byte
            // So we just use it twice...
            // the equivalent of nop'ing the second time.
            rom[deoxysObOffset] = 0x00;
            rom[deoxysObOffset + 1] = Gen3Constants.gbaSetRxOpcode | Gen3Constants.gbaR1;
            rom[deoxysObOffset + 2] = 0x00;
            rom[deoxysObOffset + 3] = Gen3Constants.gbaSetRxOpcode | Gen3Constants.gbaR1;
            // Look for the mew check too... it's 0x16 ahead
            if (readWord(deoxysObOffset + Gen3Constants.mewObeyOffsetFromDeoxysObey) == (((Gen3Constants.gbaCmpRxOpcode | Gen3Constants.gbaR0) << 8) | (Gen3Constants.mewIndex))) {
                // Bingo, thats CMP R0, 0x97
                // change to CMP R0, 0x0
                writeWord(deoxysObOffset + Gen3Constants.mewObeyOffsetFromDeoxysObey,
                        (((Gen3Constants.gbaCmpRxOpcode | Gen3Constants.gbaR0) << 8) | (0)));
            }
        }

        // Look for evolutions too
        if (getRomEntry().romType == Gen3Constants.RomType_FRLG) {
            int evoJumpOffset = find(Gen3Constants.levelEvoKantoDexCheckCode);
            if (evoJumpOffset > 0) {
                // This currently compares species to 0x97 and then allows
                // evolution if it's <= that.
                // Allow it regardless by using an unconditional jump instead
                writeWord(evoJumpOffset, Gen3Constants.gbaNopOpcode);
                writeWord(evoJumpOffset + 2,
                        ((Gen3Constants.gbaUnconditionalJumpOpcode << 8) | (Gen3Constants.levelEvoKantoDexJumpAmount)));
            }

            int stoneJumpOffset = find(Gen3Constants.stoneEvoKantoDexCheckCode);
            if (stoneJumpOffset > 0) {
                // same as the above, but for stone evos
                writeWord(stoneJumpOffset, Gen3Constants.gbaNopOpcode);
                writeWord(stoneJumpOffset + 2,
                        ((Gen3Constants.gbaUnconditionalJumpOpcode << 8) | (Gen3Constants.stoneEvoKantoDexJumpAmount)));
            }
        }
    }

    private void patchForNationalDex() {
        int fso = getRomEntry().getValue("FreeSpace");
        if (getRomEntry().romType == Gen3Constants.RomType_Ruby || getRomEntry().romType == Gen3Constants.RomType_Sapp) {
            // Find the original pokedex script
            int pkDexOffset = find(Gen3Constants.rsPokedexScriptIdentifier);
            if (pkDexOffset < 0) {
                this.getTemplateData().put("natDexPatch", "noDexOffset");
                return;
            }
            int textPointer = readPointer(pkDexOffset - 4);
            int realScriptLocation = pkDexOffset - 8;
            int pointerLocToScript = find(pointerToHexString(realScriptLocation));
            if (pointerLocToScript < 0) {
                this.getTemplateData().put("natDexPatch", "noScriptPtr");
                return;
            }
            // Find free space for our new routine
            int writeSpace = RomFunctions.freeSpaceFinder(rom, Gen3Constants.freeSpaceByte, 44, fso);
            if (writeSpace < fso) {
                this.getTemplateData().put("natDexPatch", "fullRom");
                // Somehow this ROM is full
                return;
            }
            writePointer(pointerLocToScript, writeSpace);
            writeHexString(Gen3Constants.rsNatDexScriptPart1, writeSpace);
            writePointer(writeSpace + 4, textPointer);
            writeHexString(Gen3Constants.rsNatDexScriptPart2, writeSpace + 8);

        } else if (getRomEntry().romType == Gen3Constants.RomType_FRLG) {
            // Find the original pokedex script
            int pkDexOffset = find(Gen3Constants.frlgPokedexScriptIdentifier);
            if (pkDexOffset < 0) {
                this.getTemplateData().put("natDexPatch", "noDexOffset");
                return;
            }
            // Find free space for our new routine
            int writeSpace = RomFunctions.freeSpaceFinder(rom, Gen3Constants.freeSpaceByte, 10, fso);
            if (writeSpace < fso) {
                this.getTemplateData().put("natDexPatch", "fullRom");
                // Somehow this ROM is full
                return;
            }
            rom[pkDexOffset] = 4;
            writePointer(pkDexOffset + 1, writeSpace);
            rom[pkDexOffset + 5] = 0; // NOP

            // Now write our new routine
            writeHexString(Gen3Constants.frlgNatDexScript, writeSpace);

            // Fix people using the national dex flag
            List<Integer> ndexChecks = findMultiple(Gen3Constants.frlgNatDexFlagChecker);
            for (int ndexCheckOffset : ndexChecks) {
                // change to a flag-check
                // 82C = "beaten e4/gary once"
                writeHexString(Gen3Constants.frlgE4FlagChecker, ndexCheckOffset);
            }

            // Fix oak in his lab
            int oakLabCheckOffs = find(Gen3Constants.frlgOaksLabKantoDexChecker);
            if (oakLabCheckOffs > 0) {
                // replace it
                writeHexString(Gen3Constants.frlgOaksLabFix, oakLabCheckOffs);
            }

            // Fix oak outside your house
            int oakHouseCheckOffs = find(Gen3Constants.frlgOakOutsideHouseCheck);
            if (oakHouseCheckOffs > 0) {
                // fix him to use ndex count
                writeHexString(Gen3Constants.frlgOakOutsideHouseFix, oakHouseCheckOffs);
            }
        } else {
            // Find the original pokedex script
            int pkDexOffset = find(Gen3Constants.ePokedexScriptIdentifier);
            if (pkDexOffset < 0) {
                this.getTemplateData().put("natDexPatch", "noDexOffset");
                return;
            }
            int textPointer = readPointer(pkDexOffset - 4);
            int realScriptLocation = pkDexOffset - 8;
            int pointerLocToScript = find(pointerToHexString(realScriptLocation));
            if (pointerLocToScript < 0) {
                this.getTemplateData().put("natDexPatch", "noScriptPtr");
                return;
            }
            // Find free space for our new routine
            int writeSpace = RomFunctions.freeSpaceFinder(rom, Gen3Constants.freeSpaceByte, 27, fso);
            if (writeSpace < fso) {
                // Somehow this ROM is full
                this.getTemplateData().put("natDexPatch", "fullRom");
                return;
            }
            writePointer(pointerLocToScript, writeSpace);
            writeHexString(Gen3Constants.eNatDexScriptPart1, writeSpace);
            writePointer(writeSpace + 4, textPointer);
            writeHexString(Gen3Constants.eNatDexScriptPart2, writeSpace + 8);
        }
        this.getTemplateData().put("natDexPatch", "success");
    }

    public String pointerToHexString(int pointer) {
        String hex = String.format("%08X", pointer + 0x08000000);
        return new String(new char[] { hex.charAt(6), hex.charAt(7), hex.charAt(4), hex.charAt(5), hex.charAt(2),
                hex.charAt(3), hex.charAt(0), hex.charAt(1) });
    }

    private void populateEvolutions() {
        for (Pokemon pkmn : pokes) {
            if (pkmn != null) {
                pkmn.evolutionsFrom.clear();
                pkmn.evolutionsTo.clear();
            }
        }

        int baseOffset = getRomEntry().getValue("PokemonEvolutions");
        int numInternalPokes = getRomEntry().getValue("PokemonCount");
        for (int i = 1; i <= numRealPokemon; i++) {
            Pokemon pk = pokemonList.get(i);
            int idx = getPokedexToInternal()[pk.number];
            int evoOffset = baseOffset + (idx) * 0x28;
            for (int j = 0; j < 5; j++) {
                int method = readWord(evoOffset + j * 8);
                int evolvingTo = readWord(evoOffset + j * 8 + 4);
                if (method >= 1 && method <= Gen3Constants.evolutionMethodCount && evolvingTo >= 1
                        && evolvingTo <= numInternalPokes) {
                    int extraInfo = readWord(evoOffset + j * 8 + 2);
                    EvolutionType et = EvolutionType.fromIndex(3, method);
                    Evolution evo = new Evolution(pk, pokesInternal[evolvingTo], true, et, extraInfo);
                    if (!pk.evolutionsFrom.contains(evo)) {
                        pk.evolutionsFrom.add(evo);
                        pokesInternal[evolvingTo].evolutionsTo.add(evo);
                    }
                }
            }
            // split evos don't carry stats
            if (pk.evolutionsFrom.size() > 1) {
                for (Evolution e : pk.evolutionsFrom) {
                    e.carryStats = false;
                }
            }
        }
    }

    private void writeEvolutions() {
        int baseOffset = getRomEntry().getValue("PokemonEvolutions");
        for (int i = 1; i <= numRealPokemon; i++) {
            Pokemon pk = pokemonList.get(i);
            int idx = getPokedexToInternal()[pk.number];
            int evoOffset = baseOffset + (idx) * 0x28;
            int evosWritten = 0;
            for (Evolution evo : pk.evolutionsFrom) {
                writeWord(evoOffset, evo.type.toIndex(3));
                writeWord(evoOffset + 2, evo.extraInfo);
                writeWord(evoOffset + 4, getPokedexToInternal()[evo.to.number]);
                writeWord(evoOffset + 6, 0);
                evoOffset += 8;
                evosWritten++;
                if (evosWritten == 5) {
                    break;
                }
            }
            while (evosWritten < 5) {
                writeWord(evoOffset, 0);
                writeWord(evoOffset + 2, 0);
                writeWord(evoOffset + 4, 0);
                writeWord(evoOffset + 6, 0);
                evoOffset += 8;
                evosWritten++;
            }
        }
    }

    @Override
    public void removeTradeEvolutions(boolean changeMoveEvos, boolean changeMethodEvos) {
        attemptObedienceEvolutionPatches();
        List<Evolution> tradeEvoFixed = new ArrayList<Evolution>();
        Set<Evolution> extraEvolutions = new HashSet<Evolution>();
        for (Pokemon pkmn : getPokemon()) {
            if (pkmn != null) {
                for (Evolution evo : pkmn.evolutionsFrom) {
                    // Not trades, but impossible without trading
                    if (evo.type == EvolutionType.HAPPINESS_DAY && getRomEntry().romType == Gen3Constants.RomType_FRLG) {
                        // happiness day change to Sun Stone
                        evo.type = EvolutionType.STONE;
                        evo.extraInfo = Gen3Constants.sunStoneIndex; // sun stone
                        tradeEvoFixed.add(evo);
                    }
                    if (evo.type == EvolutionType.HAPPINESS_NIGHT && getRomEntry().romType == Gen3Constants.RomType_FRLG) {
                        // happiness night change to Moon Stone
                        evo.type = EvolutionType.STONE;
                        evo.extraInfo = Gen3Constants.moonStoneIndex; // moon stone
                        tradeEvoFixed.add(evo);
                    }
                    if (evo.type == EvolutionType.LEVEL_HIGH_BEAUTY) {
                        // beauty add alternate of happiness
                        Evolution extraEntry = new Evolution(evo.from, evo.to, true,
                        EvolutionType.HAPPINESS, 0);
                        extraEvolutions.add(extraEntry);
                        tradeEvoFixed.add(extraEntry);
                    }
                    // Pure Trade
                    if (evo.type == EvolutionType.TRADE) {
                        // Haunter, Machoke, Kadabra, Graveler
                        // Make it into level 37, we're done.
                        evo.type = EvolutionType.LEVEL;
                        evo.extraInfo = 37;
                        tradeEvoFixed.add(evo);
                    }
                    // Trade w/ Held Item
                    if (evo.type == EvolutionType.TRADE_ITEM) {
                        if (evo.from.number == Gen3Constants.poliwhirlIndex) {
                            // Poliwhirl: Lv 37
                            evo.type = EvolutionType.LEVEL;
                            evo.extraInfo = 37;
                        } else if (evo.from.number == Gen3Constants.slowpokeIndex) {
                            // Slowpoke: Water Stone
                            evo.type = EvolutionType.STONE;
                            evo.extraInfo = Gen3Constants.waterStoneIndex; // water stone
                        } else if (evo.from.number == Gen3Constants.seadraIndex) {
                            // Seadra: Lv 40
                            evo.type = EvolutionType.LEVEL;
                            evo.extraInfo = 40;
                        } else if (evo.from.number == Gen3Constants.clamperlIndex
                                && evo.to.number == Gen3Constants.huntailIndex) {
                            // Clamperl -> Huntail: Lv30
                            evo.type = EvolutionType.LEVEL;
                            evo.extraInfo = 30;
                        } else if (evo.from.number == Gen3Constants.clamperlIndex
                                && evo.to.number == Gen3Constants.gorebyssIndex) {
                            // Clamperl -> Gorebyss: Water Stone
                            evo.type = EvolutionType.STONE;
                            evo.extraInfo = Gen3Constants.waterStoneIndex; // water stone
                        } else {
                            // Onix, Scyther or Porygon: Lv30
                            evo.type = EvolutionType.LEVEL;
                            evo.extraInfo = 30;
                        }
                        tradeEvoFixed.add(evo);
                    }
                }
                pkmn.evolutionsFrom.addAll(extraEvolutions);
                for (Evolution ev : extraEvolutions) {
                    ev.to.evolutionsTo.add(ev);
                }
            }
        }
        this.getTemplateData().put("removeTradeEvo", tradeEvoFixed);
    }

    @Override
    public boolean canChangeTrainerText() {
        return true;
    }

    @Override
    public List<String> getTrainerNames() {
        int baseOffset = getRomEntry().getValue("TrainerData");
        int amount = getRomEntry().getValue("TrainerCount");
        int entryLen = getRomEntry().getValue("TrainerEntrySize");
        List<String> theTrainers = new ArrayList<String>();
        for (int i = 1; i < amount; i++) {
            theTrainers.add(readVariableLengthString(baseOffset + i * entryLen + 4));
        }
        return theTrainers;
    }

    @Override
    public void setTrainerNames(List<String> trainerNames) {
        int baseOffset = getRomEntry().getValue("TrainerData");
        int amount = getRomEntry().getValue("TrainerCount");
        int entryLen = getRomEntry().getValue("TrainerEntrySize");
        int nameLen = getRomEntry().getValue("TrainerNameLength");
        Iterator<String> theTrainers = trainerNames.iterator();
        for (int i = 1; i < amount; i++) {
            String newName = theTrainers.next();
            writeFixedLengthString(newName, baseOffset + i * entryLen + 4, nameLen);
        }

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
    public int maxTrainerNameLength() {
        return getRomEntry().getValue("TrainerNameLength") - 1;
    }

    @Override
    public List<String> getTrainerClassNames() {
        int baseOffset = getRomEntry().getValue("TrainerClassNames");
        int amount = getRomEntry().getValue("TrainerClassCount");
        int length = getRomEntry().getValue("TrainerClassNameLength");
        List<String> trainerClasses = new ArrayList<String>();
        for (int i = 0; i < amount; i++) {
            trainerClasses.add(readVariableLengthString(baseOffset + i * length));
        }
        return trainerClasses;
    }

    @Override
    public void setTrainerClassNames(List<String> trainerClassNames) {
        int baseOffset = getRomEntry().getValue("TrainerClassNames");
        int amount = getRomEntry().getValue("TrainerClassCount");
        int length = getRomEntry().getValue("TrainerClassNameLength");
        Iterator<String> trainerClasses = trainerClassNames.iterator();
        for (int i = 0; i < amount; i++) {
            writeFixedLengthString(trainerClasses.next(), baseOffset + i * length, length);
        }
    }

    @Override
    public int maxTrainerClassNameLength() {
        return getRomEntry().getValue("TrainerClassNameLength") - 1;
    }

    @Override
    public boolean fixedTrainerClassNamesLength() {
        return false;
    }

    @Override
    public List<Integer> getDoublesTrainerClasses() {
        int[] doublesClasses = getRomEntry().arrayEntries.get("DoublesTrainerClasses");
        List<Integer> doubles = new ArrayList<Integer>();
        for (int tClass : doublesClasses) {
            doubles.add(tClass);
        }
        return doubles;
    }

    @Override
    public boolean canChangeStaticPokemon() {
        return (getRomEntry().getValue("StaticPokemonSupport") > 0);
    }

    @Override
    public String getDefaultExtension() {
        return "gba";
    }

    @Override
    public int abilitiesPerPokemon() {
        return 2;
    }

    @Override
    public int highestAbilityIndex() {
        return Gen3Constants.highestAbilityIndex;
    }

    private void loadAbilityNames() {
        int nameoffs = getRomEntry().getValue("AbilityNames");
        int namelen = getRomEntry().getValue("AbilityNameLength");
        abilityNames = new String[Gen3Constants.highestAbilityIndex + 1];
        for (int i = 0; i <= Gen3Constants.highestAbilityIndex; i++) {
            abilityNames[i] = readFixedLengthString(nameoffs + namelen * i, namelen);
        }
    }

    @Override
    public String abilityName(int number) {
        return abilityNames[number];
    }

    @Override
    public int internalStringLength(String string) {
        return translateString(string).length;
    }

    @Override
    public void applySignature() {
        // FRLG
        if (getRomEntry().romType == Gen3Constants.RomType_FRLG) {
            // intro sprites : first 255 only due to size
            Pokemon introPk = randomPokemonLimited(255, false);
            if (introPk == null) {
                return;
            }
            int introPokemon = getPokedexToInternal()[introPk.number];
            int frontSprites = getRomEntry().getValue("FrontSprites");
            int palettes = getRomEntry().getValue("PokemonPalettes");

            rom[getRomEntry().getValue("IntroCryOffset")] = (byte) introPokemon;
            rom[getRomEntry().getValue("IntroOtherOffset")] = (byte) introPokemon;

            int spriteBase = getRomEntry().getValue("IntroSpriteOffset");
            writePointer(spriteBase, frontSprites + introPokemon * 8);
            writePointer(spriteBase + 4, palettes + introPokemon * 8);
        } else if (getRomEntry().romType == Gen3Constants.RomType_Ruby || getRomEntry().romType == Gen3Constants.RomType_Sapp) {
            // intro sprites : any pokemon in the range 0-510 except bulbasaur
            int introPokemon = getPokedexToInternal()[randomPokemon().number];
            while (introPokemon == 1 || introPokemon > 510) {
                introPokemon = getPokedexToInternal()[randomPokemon().number];
            }
            int frontSprites = getRomEntry().getValue("PokemonFrontSprites");
            int palettes = getRomEntry().getValue("PokemonNormalPalettes");
            int cryCommand = getRomEntry().getValue("IntroCryOffset");
            int otherCommand = getRomEntry().getValue("IntroOtherOffset");

            if (introPokemon > 255) {
                rom[cryCommand] = (byte) 0xFF;
                rom[cryCommand + 1] = Gen3Constants.gbaSetRxOpcode | Gen3Constants.gbaR0;

                rom[cryCommand + 2] = (byte) (introPokemon - 0xFF);
                rom[cryCommand + 3] = Gen3Constants.gbaAddRxOpcode | Gen3Constants.gbaR0;

                rom[otherCommand] = (byte) 0xFF;
                rom[otherCommand + 1] = Gen3Constants.gbaSetRxOpcode | Gen3Constants.gbaR4;

                rom[otherCommand + 2] = (byte) (introPokemon - 0xFF);
                rom[otherCommand + 3] = Gen3Constants.gbaAddRxOpcode | Gen3Constants.gbaR4;
            } else {
                rom[cryCommand] = (byte) introPokemon;
                rom[cryCommand + 1] = Gen3Constants.gbaSetRxOpcode | Gen3Constants.gbaR0;

                writeWord(cryCommand + 2, Gen3Constants.gbaNopOpcode);

                rom[otherCommand] = (byte) introPokemon;
                rom[otherCommand + 1] = Gen3Constants.gbaSetRxOpcode | Gen3Constants.gbaR4;

                writeWord(otherCommand + 2, Gen3Constants.gbaNopOpcode);
            }

            writePointer(getRomEntry().getValue("IntroSpriteOffset"), frontSprites + introPokemon * 8);
            writePointer(getRomEntry().getValue("IntroPaletteOffset"), palettes + introPokemon * 8);
        } else {
            // Emerald, intro sprite: any Pokemon.
            int introPokemon = getPokedexToInternal()[randomPokemon().number];
            writeWord(getRomEntry().getValue("IntroSpriteOffset"), introPokemon);
            writeWord(getRomEntry().getValue("IntroCryOffset"), introPokemon);
        }

    }

    private Pokemon randomPokemonLimited(int maxValue, boolean blockNonMales) {
        checkPokemonRestrictions();
        List<Pokemon> validPokemon = new ArrayList<Pokemon>();
        for (Pokemon pk : this.mainPokemonList) {
            if (!blockNonMales || pk.genderRatio <= 0xFD) {
                validPokemon.add(pk);
            }
            if (getPokedexToInternal()[pk.number] == maxValue) {
                //Stop looping once we max out
                break;
            }
        }
        if (validPokemon.size() == 0) {
            return null;
        } else {
            return validPokemon.get(random.nextInt(validPokemon.size()));
        }
    }

    private void determineMapBankSizes() {
        int mbpsOffset = getRomEntry().getValue("MapHeaders");
        List<Integer> mapBankOffsets = new ArrayList<Integer>();

        int offset = mbpsOffset;

        // find map banks
        while (true) {
            boolean valid = true;
            for (int mbOffset : mapBankOffsets) {
                if (mbpsOffset < mbOffset && offset >= mbOffset) {
                    valid = false;
                    break;
                }
            }
            if (!valid) {
                break;
            }
            int newMBOffset = readPointer(offset);
            if (newMBOffset < 0 || newMBOffset >= rom.length) {
                break;
            }
            mapBankOffsets.add(newMBOffset);
            offset += 4;
        }
        int bankCount = mapBankOffsets.size();
        int[] bankMapCounts = new int[bankCount];
        for (int bank = 0; bank < bankCount; bank++) {
            int baseBankOffset = mapBankOffsets.get(bank);
            int count = 0;
            offset = baseBankOffset;
            while (true) {
                boolean valid = true;
                for (int mbOffset : mapBankOffsets) {
                    if (baseBankOffset < mbOffset && offset >= mbOffset) {
                        valid = false;
                        break;
                    }
                }
                if (!valid) {
                    break;
                }
                if (baseBankOffset < mbpsOffset && offset >= mbpsOffset) {
                    break;
                }
                int newMapOffset = readPointer(offset);
                if (newMapOffset < 0 || newMapOffset >= rom.length) {
                    break;
                }
                count++;
                offset += 4;
            }
            bankMapCounts[bank] = count;
        }

        getRomEntry().entries.put("MapBankCount", bankCount);
        getRomEntry().arrayEntries.put("MapBankSizes", bankMapCounts);
    }

    private void preprocessMaps() {
        itemOffs = new ArrayList<Integer>();
        int bankCount = getRomEntry().getValue("MapBankCount");
        int[] bankMapCounts = getRomEntry().arrayEntries.get("MapBankSizes");
        int itemBall = getRomEntry().getValue("ItemBallPic");
        mapNames = new String[bankCount][];
        int mbpsOffset = getRomEntry().getValue("MapHeaders");
        int mapLabels = getRomEntry().getValue("MapLabels");
        Map<Integer, String> mapLabelsM = new HashMap<Integer, String>();
        for (int bank = 0; bank < bankCount; bank++) {
            int bankOffset = readPointer(mbpsOffset + bank * 4);
            mapNames[bank] = new String[bankMapCounts[bank]];
            for (int map = 0; map < bankMapCounts[bank]; map++) {
                int mhOffset = readPointer(bankOffset + map * 4);

                // map name
                int mapLabel = rom[mhOffset + 0x14] & 0xFF;
                if (mapLabelsM.containsKey(mapLabel)) {
                    mapNames[bank][map] = mapLabelsM.get(mapLabel);
                } else {
                    if (getRomEntry().romType == Gen3Constants.RomType_FRLG) {
                        mapNames[bank][map] = readVariableLengthString(readPointer(mapLabels
                                + (mapLabel - Gen3Constants.frlgMapLabelsStart) * 4));
                    } else {
                        mapNames[bank][map] = readVariableLengthString(readPointer(mapLabels + mapLabel * 8 + 4));
                    }
                    mapLabelsM.put(mapLabel, mapNames[bank][map]);
                }

                // events
                int eventOffset = readPointer(mhOffset + 4);
                if (eventOffset >= 0 && eventOffset < rom.length) {

                    int pCount = rom[eventOffset] & 0xFF;
                    int spCount = rom[eventOffset + 3] & 0xFF;

                    if (pCount > 0) {
                        int peopleOffset = readPointer(eventOffset + 4);
                        for (int p = 0; p < pCount; p++) {
                            int pSprite = rom[peopleOffset + p * 24 + 1];
                            if (pSprite == itemBall && readPointer(peopleOffset + p * 24 + 16) >= 0) {
                                // Get script and look inside
                                int scriptOffset = readPointer(peopleOffset + p * 24 + 16);
                                if (rom[scriptOffset] == 0x1A && rom[scriptOffset + 1] == 0x00
                                        && (rom[scriptOffset + 2] & 0xFF) == 0x80 && rom[scriptOffset + 5] == 0x1A
                                        && rom[scriptOffset + 6] == 0x01 && (rom[scriptOffset + 7] & 0xFF) == 0x80
                                        && rom[scriptOffset + 10] == 0x09
                                        && (rom[scriptOffset + 11] == 0x00 || rom[scriptOffset + 11] == 0x01)) {
                                    // item ball script
                                    itemOffs.add(scriptOffset + 3);
                                }
                            }
                        }
                        // TM Text?
                        for (TMOrMTTextEntry tte : getRomEntry().tmmtTexts) {
                            if (tte.mapBank == bank && tte.mapNumber == map) {
                                // process this one
                                int scriptOffset = readPointer(peopleOffset + (tte.personNum - 1) * 24 + 16);
                                if (scriptOffset >= 0) {
                                    if (getRomEntry().romType == Gen3Constants.RomType_FRLG && tte.isMoveTutor
                                            && (tte.number == 5 || (tte.number >= 8 && tte.number <= 11))) {
                                        scriptOffset = readPointer(scriptOffset + 1);
                                    } else if (getRomEntry().romType == Gen3Constants.RomType_FRLG && tte.isMoveTutor
                                            && tte.number == 7) {
                                        scriptOffset = readPointer(scriptOffset + 0x1F);
                                    }
                                    int lookAt = scriptOffset + tte.offsetInScript;
                                    // make sure this actually looks like a text
                                    // pointer
                                    if (lookAt >= 0 && lookAt < rom.length - 2) {
                                        if (rom[lookAt + 3] == 0x08 || rom[lookAt + 3] == 0x09) {
                                            // okay, it passes the basic test
                                            tte.actualOffset = lookAt;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (spCount > 0) {
                        int signpostsOffset = readPointer(eventOffset + 16);
                        for (int sp = 0; sp < spCount; sp++) {
                            int spType = rom[signpostsOffset + sp * 12 + 5];
                            if (spType >= 5 && spType <= 7) {
                                // hidden item
                                int itemHere = readWord(signpostsOffset + sp * 12 + 8);
                                if (itemHere != 0) {
                                    // itemid 0 is coins
                                    itemOffs.add(signpostsOffset + sp * 12 + 8);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public ItemList getAllowedItems() {
        return Gen3Constants.allowedItems;
    }

    @Override
    public ItemList getNonBadItems() {
        return Gen3Constants.nonBadItems;
    }

    private void loadItemNames() {
        int nameoffs = getRomEntry().getValue("ItemData");
        int structlen = getRomEntry().getValue("ItemEntrySize");
        int maxcount = getRomEntry().getValue("ItemCount");
        itemNames = new String[maxcount + 1];
        for (int i = 0; i <= maxcount; i++) {
            itemNames[i] = readVariableLengthString(nameoffs + structlen * i);
        }
    }

    @Override
    public String[] getItemNames() {
        return itemNames;
    }

    @Override
    public List<Integer> getRequiredFieldTMs() {
        if (getRomEntry().romType == Gen3Constants.RomType_FRLG) {
            return Gen3Constants.frlgRequiredFieldTMs;
        } else if (getRomEntry().romType == Gen3Constants.RomType_Ruby || getRomEntry().romType == Gen3Constants.RomType_Sapp) {
            return Gen3Constants.rsRequiredFieldTMs;
        } else {
            // emerald has a few TMs from pickup
            return Gen3Constants.eRequiredFieldTMs;
        }
    }

    @Override
    public List<Integer> getCurrentFieldTMs() {
        if (!mapLoadingDone) {
            preprocessMaps();
            mapLoadingDone = true;
        }
        List<Integer> fieldTMs = new ArrayList<Integer>();

        for (int offset : itemOffs) {
            int itemHere = readWord(offset);
            if (Gen3Constants.allowedItems.isTM(itemHere)) {
                int thisTM = itemHere - Gen3Constants.tmItemOffset + 1;
                // hack for repeat TMs
                if (fieldTMs.contains(thisTM) == false) {
                    fieldTMs.add(thisTM);
                }
            }
        }
        return fieldTMs;
    }

    @Override
    public void setFieldTMs(List<Integer> fieldTMs) {
        if (!mapLoadingDone) {
            preprocessMaps();
            mapLoadingDone = true;
        }
        Iterator<Integer> iterTMs = fieldTMs.iterator();
        int[] givenTMs = new int[512];

        for (int offset : itemOffs) {
            int itemHere = readWord(offset);
            if (Gen3Constants.allowedItems.isTM(itemHere)) {
                // Cache replaced TMs to duplicate repeats
                if (givenTMs[itemHere] != 0) {
                    rom[offset] = (byte) givenTMs[itemHere];
                } else {
                    // Replace this with a TM from the list
                    int tm = iterTMs.next();
                    tm += Gen3Constants.tmItemOffset - 1;
                    givenTMs[itemHere] = tm;
                    writeWord(offset, tm);
                }
            }
        }
    }

    @Override
    public List<Integer> getRegularFieldItems() {
        if (!mapLoadingDone) {
            preprocessMaps();
            mapLoadingDone = true;
        }
        List<Integer> fieldItems = new ArrayList<Integer>();

        for (int offset : itemOffs) {
            int itemHere = readWord(offset);
            if (Gen3Constants.allowedItems.isAllowed(itemHere) && !(Gen3Constants.allowedItems.isTM(itemHere))) {
                fieldItems.add(itemHere);
            }
        }
        return fieldItems;
    }

    @Override
    public void setRegularFieldItems(List<Integer> items) {
        if (!mapLoadingDone) {
            preprocessMaps();
            mapLoadingDone = true;
        }
        Iterator<Integer> iterItems = items.iterator();

        for (int offset : itemOffs) {
            int itemHere = readWord(offset);
            if (Gen3Constants.allowedItems.isAllowed(itemHere) && !(Gen3Constants.allowedItems.isTM(itemHere))) {
                // Replace it
                writeWord(offset, iterItems.next());
            }
        }

    }

    @Override
    public List<IngameTrade> getIngameTrades() {
        List<IngameTrade> trades = new ArrayList<IngameTrade>();

        // info
        int tableOffset = getRomEntry().getValue("TradeTableOffset");
        int tableSize = getRomEntry().getValue("TradeTableSize");
        int[] unused = getRomEntry().arrayEntries.get("TradesUnused");
        int unusedOffset = 0;
        int entryLength = 60;

        for (int entry = 0; entry < tableSize; entry++) {
            if (unusedOffset < unused.length && unused[unusedOffset] == entry) {
                unusedOffset++;
                continue;
            }
            IngameTrade trade = new IngameTrade();
            int entryOffset = tableOffset + entry * entryLength;
            trade.nickname = readVariableLengthString(entryOffset);
            trade.givenPokemon = pokesInternal[readWord(entryOffset + 12)];
            trade.ivs = new int[6];
            for (int i = 0; i < 6; i++) {
                trade.ivs[i] = rom[entryOffset + 14 + i] & 0xFF;
            }
            trade.otId = readWord(entryOffset + 24);
            trade.item = readWord(entryOffset + 40);
            trade.otName = readVariableLengthString(entryOffset + 43);
            trade.requestedPokemon = pokesInternal[readWord(entryOffset + 56)];
            trades.add(trade);
        }

        return trades;

    }

    @Override
    public void setIngameTrades(List<IngameTrade> trades) {
        // info
        int tableOffset = getRomEntry().getValue("TradeTableOffset");
        int tableSize = getRomEntry().getValue("TradeTableSize");
        int[] unused = getRomEntry().arrayEntries.get("TradesUnused");
        int unusedOffset = 0;
        int entryLength = 60;
        int tradeOffset = 0;

        for (int entry = 0; entry < tableSize; entry++) {
            if (unusedOffset < unused.length && unused[unusedOffset] == entry) {
                unusedOffset++;
                continue;
            }
            IngameTrade trade = trades.get(tradeOffset++);
            int entryOffset = tableOffset + entry * entryLength;
            writeFixedLengthString(trade.nickname, entryOffset, 12);
            writeWord(entryOffset + 12, getPokedexToInternal()[trade.givenPokemon.number]);
            for (int i = 0; i < 6; i++) {
                rom[entryOffset + 14 + i] = (byte) trade.ivs[i];
            }
            writeWord(entryOffset + 24, trade.otId);
            writeWord(entryOffset + 40, trade.item);
            writeFixedLengthString(trade.otName, entryOffset + 43, 11);
            writeWord(entryOffset + 56, getPokedexToInternal()[trade.requestedPokemon.number]);
        }
    }

    @Override
    public boolean hasDVs() {
        return false;
    }

    @Override
    public int generationOfPokemon() {
        return 3;
    }

    @Override
    public void removeEvosForPokemonPool() {
        List<Pokemon> pokemonIncluded = this.mainPokemonList;
        Set<Evolution> keepEvos = new HashSet<Evolution>();
        for (Pokemon pk : pokes) {
            if (pk != null) {
                keepEvos.clear();
                for (Evolution evol : pk.evolutionsFrom) {
                    if (pokemonIncluded.contains(evol.from) && pokemonIncluded.contains(evol.to)) {
                        keepEvos.add(evol);
                    } else {
                        evol.to.evolutionsTo.remove(evol);
                    }
                }
                pk.evolutionsFrom.retainAll(keepEvos);
            }
        }
    }

    @Override
    public boolean supportsFourStartingMoves() {
        return true;
    }

    @Override
    public List<Integer> getFieldMoves() {
        // cut, fly, surf, strength, flash,
        // dig, teleport, waterfall,
        // rock smash, sweet scent
        // not softboiled or milk drink
        // dive and secret power in RSE only
        if (getRomEntry().romType == Gen3Constants.RomType_FRLG) {
            return Gen3Constants.frlgFieldMoves;
        } else {
            return Gen3Constants.rseFieldMoves;
        }
    }

    @Override
    public List<Integer> getEarlyRequiredHMMoves() {
        // RSE: rock smash
        // FRLG: cut
        if (getRomEntry().romType == Gen3Constants.RomType_FRLG) {
            return Gen3Constants.frlgEarlyRequiredHMMoves;
        } else {
            return Gen3Constants.rseEarlyRequiredHMMoves;
        }
    }

    @Override
    public int miscTweaksAvailable() {
        int available = MiscTweak.LOWER_CASE_POKEMON_NAMES.getValue();
        available |= MiscTweak.NATIONAL_DEX_AT_START.getValue();
        if (getRomEntry().getValue("RunIndoorsTweakOffset") > 0) {
            available |= MiscTweak.RUNNING_SHOES_INDOORS.getValue();
        }
        if (getRomEntry().getValue("TextSpeedValuesOffset") > 0) {
            available |= MiscTweak.FASTEST_TEXT.getValue();
        }
        if (getRomEntry().getValue("CatchingTutorialOpponentMonOffset") > 0
                || getRomEntry().getValue("CatchingTutorialPlayerMonOffset") > 0) {
            available |= MiscTweak.RANDOMIZE_CATCHING_TUTORIAL.getValue();
        }
        if (getRomEntry().getValue("PCPotionOffset") != 0) {
            available |= MiscTweak.RANDOMIZE_PC_POTION.getValue();
        }
        available |= MiscTweak.BAN_LUCKY_EGG.getValue();
        return available;
    }

    @Override
    public void applyMiscTweak(MiscTweak tweak) {
        if (tweak == MiscTweak.RUNNING_SHOES_INDOORS) {
            applyRunningShoesIndoorsPatch();
        } else if (tweak == MiscTweak.FASTEST_TEXT) {
            applyFastestTextPatch();
        } else if (tweak == MiscTweak.LOWER_CASE_POKEMON_NAMES) {
            applyCamelCaseNames();
        } else if (tweak == MiscTweak.NATIONAL_DEX_AT_START) {
            patchForNationalDex();
        } else if (tweak == MiscTweak.RANDOMIZE_CATCHING_TUTORIAL) {
            randomizeCatchingTutorial();
        } else if (tweak == MiscTweak.BAN_LUCKY_EGG) {
            getAllowedItems().banSingles(Gen3Constants.luckyEggIndex);
            getNonBadItems().banSingles(Gen3Constants.luckyEggIndex);
            ((Map<String, Boolean>) this.getTemplateData().get("tweakMap")).put(MiscTweak.BAN_LUCKY_EGG.getTweakName(), true);
        } else if (tweak == MiscTweak.RANDOMIZE_PC_POTION) {
            randomizePCPotion();
        }
    }

    private void randomizeCatchingTutorial() {
        if (getRomEntry().getValue("CatchingTutorialOpponentMonOffset") > 0) {
            int oppOffset = getRomEntry().getValue("CatchingTutorialOpponentMonOffset");
            if (getRomEntry().romType == Gen3Constants.RomType_FRLG) {
                Pokemon opponent = randomPokemonLimited(255, true);
                if (opponent != null) {

                    int oppValue = getPokedexToInternal()[opponent.number];
                    writeByte(oppOffset, (byte) oppValue);
                    writeByte(oppOffset + 1, (byte) (Gen3Constants.gbaSetRxOpcode | Gen3Constants.gbaR1));
                    ((Map<String, Boolean>) this.getTemplateData().get("tweakMap")).put(MiscTweak.RANDOMIZE_CATCHING_TUTORIAL.getTweakName(), true);
                } 
            } else {
                Pokemon opponent = randomPokemonLimited(510, true);
                if (opponent != null) {
                    int oppValue = getPokedexToInternal()[opponent.number];
                    if (oppValue > 255) {
                        writeByte(oppOffset, (byte) 0xFF);
                        writeByte(oppOffset + 1, (byte) (Gen3Constants.gbaSetRxOpcode | Gen3Constants.gbaR1));

                        writeByte(oppOffset + 2, (byte) (oppValue - 0xFF));
                        writeByte(oppOffset + 3, (byte) (Gen3Constants.gbaAddRxOpcode | Gen3Constants.gbaR1));
                    } else {
                        writeByte(oppOffset, (byte) oppValue);
                        writeByte(oppOffset + 1, (byte) (Gen3Constants.gbaSetRxOpcode | Gen3Constants.gbaR1));

                        writeWord(oppOffset + 2, Gen3Constants.gbaNopOpcode);
                    }
                    ((Map<String, Boolean>) this.getTemplateData().get("tweakMap")).put(MiscTweak.RANDOMIZE_CATCHING_TUTORIAL.getTweakName(), true);
                }
            }
        }

        if (getRomEntry().getValue("CatchingTutorialPlayerMonOffset") > 0) {
            int playerOffset = getRomEntry().getValue("CatchingTutorialPlayerMonOffset");
            Pokemon playerMon = randomPokemonLimited(510, false);
            if (playerMon != null) {
                int plyValue = getPokedexToInternal()[playerMon.number];
                if (plyValue > 255) {
                    writeByte(playerOffset, (byte) 0xFF);
                    writeByte(playerOffset + 1, (byte) (Gen3Constants.gbaSetRxOpcode | Gen3Constants.gbaR1));

                    writeByte(playerOffset + 2, (byte) (plyValue - 0xFF));
                    writeByte(playerOffset + 3, (byte) (Gen3Constants.gbaAddRxOpcode | Gen3Constants.gbaR1));
                } else {
                    writeByte(playerOffset, (byte) plyValue);
                    writeByte(playerOffset + 1, (byte) (Gen3Constants.gbaSetRxOpcode | Gen3Constants.gbaR1));

                    writeWord(playerOffset + 2, Gen3Constants.gbaNopOpcode);
                }
                ((Map<String, Boolean>) this.getTemplateData().get("tweakMap")).put(MiscTweak.RANDOMIZE_CATCHING_TUTORIAL.getTweakName(), true);
            }
        }

    }

    private void applyRunningShoesIndoorsPatch() {
        if (getRomEntry().getValue("RunIndoorsTweakOffset") != 0) {
            writeByte(getRomEntry().getValue("RunIndoorsTweakOffset"), (byte) 0x00);
            ((Map<String, Boolean>) this.getTemplateData().get("tweakMap")).put(MiscTweak.RUNNING_SHOES_INDOORS.getTweakName(), true);
        }
    }

    private void applyFastestTextPatch() {
        if (getRomEntry().getValue("TextSpeedValuesOffset") > 0) {
            int tsvOffset = getRomEntry().getValue("TextSpeedValuesOffset");
            writeByte(tsvOffset, (byte) 4); // slow = medium
            writeByte(tsvOffset + 1, (byte) 1); // medium = fast
            writeByte(tsvOffset + 2, (byte) 0); // fast = instant
            ((Map<String, Boolean>) this.getTemplateData().get("tweakMap")).put(MiscTweak.FASTEST_TEXT.getTweakName(), true);
        }
    }

    private void randomizePCPotion() {
        if (getRomEntry().getValue("PCPotionOffset") != 0) {
            writeWord(getRomEntry().getValue("PCPotionOffset"), this.getNonBadItems().randomNonTM(this.random));
            ((Map<String, Boolean>) this.getTemplateData().get("tweakMap")).put(MiscTweak.RANDOMIZE_PC_POTION.getTweakName(), true);
        }
    }

    @Override
    public boolean isROMHack() {
        return this.isRomHack;
    }

    @Override
    public boolean isGen1() {
        return isGen1;
    }

    @Override
    public BufferedImage getMascotImage() {
        Pokemon mascotPk = randomPokemon();
        int mascotPokemon = getPokedexToInternal()[mascotPk.number];
        int frontSprites = getRomEntry().getValue("FrontSprites");
        int palettes = getRomEntry().getValue("PokemonPalettes");
        int fsOffset = readPointer(frontSprites + mascotPokemon * 8);
        int palOffset = readPointer(palettes + mascotPokemon * 8);

        byte[] trueFrontSprite = DSDecmp.Decompress(rom, fsOffset);
        byte[] truePalette = DSDecmp.Decompress(rom, palOffset);

        // Convert palette into RGB
        int[] convPalette = new int[16];
        // Leave palette[0] as 00000000 for transparency
        for (int i = 0; i < 15; i++) {
            int palValue = readWord(truePalette, i * 2 + 2);
            convPalette[i + 1] = GFXFunctions.conv16BitColorToARGB(palValue);
        }

        // Make image, 4bpp
        BufferedImage bim = GFXFunctions.drawTiledImage(trueFrontSprite, convPalette, 64, 64, 4);
        return bim;
    }
    
    protected RomEntry getRomEntry() {
        return romEntry;
    }
    
    protected int[] getPokedexToInternal() {
        return pokedexToInternal;
    }

    protected Pokemon[] getPokesInternal() {
        return pokesInternal;
    }
}
