package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  Gen2RomHandler.java - randomizer handler for G/S/C.                   --*/
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.GFXFunctions;
import com.dabomstew.pkrandom.MiscTweak;
import com.dabomstew.pkrandom.RomFunctions;
import com.dabomstew.pkrandom.Utils;
import com.dabomstew.pkrandom.constants.GBConstants;
import com.dabomstew.pkrandom.constants.Gen2Constants;
import com.dabomstew.pkrandom.constants.GlobalConstants;
import com.dabomstew.pkrandom.exceptions.RandomizerIOException;
import com.dabomstew.pkrandom.pokemon.Encounter;
import com.dabomstew.pkrandom.pokemon.EncounterSet;
import com.dabomstew.pkrandom.pokemon.Evolution;
import com.dabomstew.pkrandom.pokemon.EvolutionType;
import com.dabomstew.pkrandom.pokemon.ExpCurve;
import com.dabomstew.pkrandom.pokemon.FieldTM;
import com.dabomstew.pkrandom.pokemon.HoldItem;
import com.dabomstew.pkrandom.pokemon.IngameTrade;
import com.dabomstew.pkrandom.pokemon.ItemList;
import com.dabomstew.pkrandom.pokemon.ItemLocation;
import com.dabomstew.pkrandom.pokemon.Move;
import com.dabomstew.pkrandom.pokemon.MoveLearnt;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.TrainerPokemon;
import com.dabomstew.pkrandom.pokemon.Type;

import compressors.Gen2Decmp;

public class Gen2RomHandler extends AbstractGBCRomHandler {

    public static class Factory extends RomHandler.Factory {

        @Override
        public Gen2RomHandler create(Random random, PrintStream logStream) {
            return new Gen2RomHandler(random, logStream);
        }

        public boolean isLoadable(String filename) {
            long fileLength = new File(filename).length();
            if (fileLength > 8 * 1024 * 1024) {
                return false;
            }
            byte[] loaded = loadFilePartial(filename, 0x1000);
            if (loaded.length == 0) {
                // nope
                return false;
            }
            return detectRomInner(loaded, (int) fileLength);
        }
    }

    public Gen2RomHandler(Random random) {
        super(random, null);
    }

    public Gen2RomHandler(Random random, PrintStream logStream) {
        super(random, logStream);
    }

    private static class RomEntry {
        private String name;
        private String romCode;
        private String hash;
        private int version, nonJapanese;
        private String extraTableFile;
        private boolean isCrystal;
        private int crcInHeader = -1;
        private Map<String, String> codeTweaks = new HashMap<String, String>();
        private List<TMTextEntry> tmTexts = new ArrayList<TMTextEntry>();
        private Map<String, Integer> entries = new HashMap<String, Integer>();
        private Map<String, int[]> arrayEntries = new HashMap<String, int[]>();
        private List<StaticPokemon> staticPokemon = new ArrayList<StaticPokemon>();

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

    static {
        loadROMInfo();
    }

    private static void loadROMInfo() {
        roms = new ArrayList<RomEntry>();
        RomEntry current = null;
        try {
            Scanner sc = new Scanner(FileFunctions.openConfig("gen2_offsets.ini"), "UTF-8");
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
                        } else if (r[0].equals("StaticPokemonGameCorner[]")) {
                            String[] offsets = r[1].substring(1, r[1].length() - 1).split(",");
                            int[] offs = new int[offsets.length];
                            int c = 0;
                            for (String off : offsets) {
                                offs[c++] = parseRIInt(off);
                            }
                            current.staticPokemon.add(new StaticPokemonGameCorner(offs));
                        } else if (r[0].equals("TMText[]")) {
                            if (r[1].startsWith("[") && r[1].endsWith("]")) {
                                String[] parts = r[1].substring(1, r[1].length() - 1).split(",", 3);
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
                        } else if (r[0].equals("MD5Hash")) {
                            current.hash = r[1];
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
                                    boolean cSP = (current.getValue("CopyStaticPokemon") == 1);
                                    boolean cTT = (current.getValue("CopyTMText") == 1);
                                    current.arrayEntries.putAll(otherEntry.arrayEntries);
                                    current.entries.putAll(otherEntry.entries);
                                    if (cSP) {
                                        current.staticPokemon.addAll(otherEntry.staticPokemon);
                                        current.entries.put("StaticPokemonSupport", 1);
                                    } else {
                                        current.entries.put("StaticPokemonSupport", 0);
                                        current.entries.remove("StaticPokemonOddEggOffset");
                                        current.entries.remove("StaticPokemonOddEggDataSize");
                                    }
                                    if (cTT) {
                                        current.tmTexts.addAll(otherEntry.tmTexts);
                                    }
                                    current.extraTableFile = otherEntry.extraTableFile;
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

    // This ROM's data
    private Pokemon[] pokes;
    private List<Pokemon> pokemonList;
    private RomEntry romEntry;
    private Move[] moves;
    private boolean havePatchedFleeing;
    private String[] itemNames;
    private List<ItemLocationInner> itemOffs;
    private String[][] mapNames;
    private String[] landmarkNames;
    private boolean isVietCrystal;
    private ItemList allowedItems, nonBadItems;

    @Override
    public boolean detectRom(byte[] rom) {
        return detectRomInner(rom, rom.length);
    }

    private static boolean detectRomInner(byte[] rom, int romSize) {
        if (romSize < GBConstants.minRomSize || romSize > GBConstants.maxRomSize) {
            return false; // size check
        }
        return checkRomEntry(rom, romSize) != null; // so it's OK if it's a
                                                    // valid ROM
    }

    @Override
    public void loadedRom() {
        romEntry = checkRomEntry(this.rom, this.rom.length);
        clearTextTables();
        readTextTable("gameboy_jap");
        if (romEntry.extraTableFile != null && romEntry.extraTableFile.equalsIgnoreCase("none") == false) {
            readTextTable(romEntry.extraTableFile);
        }
        // VietCrystal override
        if (romEntry.name.equals("Crystal (J)")
                && rom[Gen2Constants.vietCrystalCheckOffset] == Gen2Constants.vietCrystalCheckValue) {
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
        allowedItems = Gen2Constants.allowedItems.copy();
        nonBadItems = Gen2Constants.nonBadItems.copy();
    }

    private static RomEntry checkRomEntry(byte[] rom, int romLength) {
        int version = rom[GBConstants.versionOffset] & 0xFF;
        int nonjap = rom[GBConstants.jpFlagOffset] & 0xFF;
        // Check for specific CRC first
        int crcInHeader = ((rom[GBConstants.crcOffset] & 0xFF) << 8) | (rom[GBConstants.crcOffset + 1] & 0xFF);
        for (RomEntry re : roms) {
            if (romCode(rom, re.romCode) && re.version == version && re.nonJapanese == nonjap
                    && re.crcInHeader == crcInHeader) {
                return re;
            }
        }
        // Now check for non-specific-CRC entries
        for (RomEntry re : roms) {
            if (romCode(rom, re.romCode) && re.version == version && re.nonJapanese == nonjap && re.crcInHeader == -1) {
                if (re.hash != null && rom.length == romLength) {
                    try {
                        MessageDigest md = MessageDigest.getInstance("MD5");
                        byte[] digest = md.digest(rom);
                        String hash = Utils.toHexString(digest);
                        if (hash.equalsIgnoreCase(re.hash)) {
                            return re;
                        }
                    } catch (NoSuchAlgorithmException e) {
                    }
                } else {
                    return re;
                }
            }
        }
        // Not found
        return null;
    }

    @Override
    public void savingRom() {
        savePokemonStats();
        saveMoves();
    }

    private void loadPokemonStats() {
        pokes = new Pokemon[Gen2Constants.pokemonCount + 1];
        // Fetch our names
        String[] pokeNames = readPokemonNames();
        int offs = romEntry.getValue("PokemonStatsOffset");
        // Get base stats
        for (int i = 1; i <= Gen2Constants.pokemonCount; i++) {
            pokes[i] = new Pokemon();
            pokes[i].number = i;
            loadBasicPokeStats(pokes[i], offs + (i - 1) * Gen2Constants.baseStatsEntrySize);
            // Name?
            pokes[i].name = pokeNames[i];
        }

        // Get evolutions
        populateEvolutions();

    }

    private void savePokemonStats() {
        // Write pokemon names
        int offs = romEntry.getValue("PokemonNamesOffset");
        int len = romEntry.getValue("PokemonNamesLength");
        for (int i = 1; i <= Gen2Constants.pokemonCount; i++) {
            int stringOffset = offs + (i - 1) * len;
            writeFixedLengthString(pokes[i].name, stringOffset, len);
        }
        // Write pokemon stats
        int offs2 = romEntry.getValue("PokemonStatsOffset");
        for (int i = 1; i <= Gen2Constants.pokemonCount; i++) {
            saveBasicPokeStats(pokes[i], offs2 + (i - 1) * Gen2Constants.baseStatsEntrySize);
        }
        // Write evolutions
        writeEvosAndMovesLearnt(true, null);
    }

    private String[] readMoveNames() {
        int offset = romEntry.getValue("MoveNamesOffset");
        String[] moveNames = new String[Gen2Constants.moveCount + 1];
        for (int i = 1; i <= Gen2Constants.moveCount; i++) {
            moveNames[i] = readVariableLengthString(offset, false);
            offset += lengthOfStringAt(offset, false) + 1;
        }
        return moveNames;
    }

    private void loadMoves() {
        moves = new Move[Gen2Constants.moveCount + 1];
        String[] moveNames = readMoveNames();
        int offs = romEntry.getValue("MoveDataOffset");
        for (int i = 1; i <= Gen2Constants.moveCount; i++) {
            moves[i] = new Move();
            moves[i].name = moveNames[i];
            moves[i].number = i;
            moves[i].internalId = i;
            moves[i].effectIndex = rom[offs + (i - 1) * 7 + 1] & 0xFF;
            moves[i].hitratio = ((rom[offs + (i - 1) * 7 + 4] & 0xFF) + 0) / 255.0 * 100;
            moves[i].power = rom[offs + (i - 1) * 7 + 2] & 0xFF;
            moves[i].pp = rom[offs + (i - 1) * 7 + 5] & 0xFF;
            moves[i].type = Gen2Constants.typeTable[rom[offs + (i - 1) * 7 + 3]];

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
        int offs = romEntry.getValue("MoveDataOffset");
        for (int i = 1; i <= 251; i++) {
            rom[offs + (i - 1) * 7 + 1] = (byte) moves[i].effectIndex;
            rom[offs + (i - 1) * 7 + 2] = (byte) moves[i].power;
            rom[offs + (i - 1) * 7 + 3] = Gen2Constants.typeToByte(moves[i].type);
            int hitratio = (int) Math.round(moves[i].hitratio * 2.55);
            if (hitratio < 0) {
                hitratio = 0;
            }
            if (hitratio > 255) {
                hitratio = 255;
            }
            rom[offs + (i - 1) * 7 + 4] = (byte) hitratio;
            rom[offs + (i - 1) * 7 + 5] = (byte) moves[i].pp;
        }
    }

    public List<Move> getMoves() {
        return Arrays.asList(moves);
    }

    private void loadBasicPokeStats(Pokemon pkmn, int offset) {
        pkmn.hp = rom[offset + Gen2Constants.bsHPOffset] & 0xFF;
        pkmn.attack = rom[offset + Gen2Constants.bsAttackOffset] & 0xFF;
        pkmn.defense = rom[offset + Gen2Constants.bsDefenseOffset] & 0xFF;
        pkmn.speed = rom[offset + Gen2Constants.bsSpeedOffset] & 0xFF;
        pkmn.spatk = rom[offset + Gen2Constants.bsSpAtkOffset] & 0xFF;
        pkmn.spdef = rom[offset + Gen2Constants.bsSpDefOffset] & 0xFF;
        // Type
        pkmn.primaryType = Gen2Constants.typeTable[rom[offset + Gen2Constants.bsPrimaryTypeOffset] & 0xFF];
        pkmn.secondaryType = Gen2Constants.typeTable[rom[offset + Gen2Constants.bsSecondaryTypeOffset] & 0xFF];
        // Only one type?
        if (pkmn.secondaryType == pkmn.primaryType) {
            pkmn.secondaryType = null;
        }
        pkmn.catchRate = rom[offset + Gen2Constants.bsCatchRateOffset] & 0xFF;
        pkmn.guaranteedHeldItem = -1;
        pkmn.commonHeldItem = rom[offset + Gen2Constants.bsCommonHeldItemOffset] & 0xFF;
        pkmn.rareHeldItem = rom[offset + Gen2Constants.bsRareHeldItemOffset] & 0xFF;
        pkmn.darkGrassHeldItem = -1;
        pkmn.growthCurve = ExpCurve.fromByte(rom[offset + Gen2Constants.bsGrowthCurveOffset]);
        pkmn.picDimensions = rom[offset + Gen2Constants.bsPicDimensionsOffset] & 0xFF;

    }

    private void saveBasicPokeStats(Pokemon pkmn, int offset) {
        rom[offset + Gen2Constants.bsHPOffset] = (byte) pkmn.hp;
        rom[offset + Gen2Constants.bsAttackOffset] = (byte) pkmn.attack;
        rom[offset + Gen2Constants.bsDefenseOffset] = (byte) pkmn.defense;
        rom[offset + Gen2Constants.bsSpeedOffset] = (byte) pkmn.speed;
        rom[offset + Gen2Constants.bsSpAtkOffset] = (byte) pkmn.spatk;
        rom[offset + Gen2Constants.bsSpDefOffset] = (byte) pkmn.spdef;
        rom[offset + Gen2Constants.bsPrimaryTypeOffset] = Gen2Constants.typeToByte(pkmn.primaryType);
        if (pkmn.secondaryType == null) {
            rom[offset + Gen2Constants.bsSecondaryTypeOffset] = rom[offset + Gen2Constants.bsPrimaryTypeOffset];
        } else {
            rom[offset + Gen2Constants.bsSecondaryTypeOffset] = Gen2Constants.typeToByte(pkmn.secondaryType);
        }
        rom[offset + Gen2Constants.bsCatchRateOffset] = (byte) pkmn.catchRate;

        rom[offset + Gen2Constants.bsCommonHeldItemOffset] = (byte) pkmn.commonHeldItem;
        rom[offset + Gen2Constants.bsRareHeldItemOffset] = (byte) pkmn.rareHeldItem;
        rom[offset + Gen2Constants.bsGrowthCurveOffset] = pkmn.growthCurve.toByte();
    }

    private String[] readPokemonNames() {
        int offs = romEntry.getValue("PokemonNamesOffset");
        int len = romEntry.getValue("PokemonNamesLength");
        String[] names = new String[Gen2Constants.pokemonCount + 1];
        for (int i = 1; i <= Gen2Constants.pokemonCount; i++) {
            names[i] = readFixedLengthString(offs + (i - 1) * len, len);
        }
        return names;
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

        // Actually write

        for (int i = 0; i < 3; i++) {
            byte starter = (byte) newStarters.get(i).number;
            int[] offsets = romEntry.arrayEntries.get("StarterOffsets" + (i + 1));
            for (int offset : offsets) {
                rom[offset] = starter;
            }
        }

        // Attempt to replace text
        if (romEntry.getValue("CanChangeStarterText") > 0) {
            int[] starterTextOffsets = romEntry.arrayEntries.get("StarterTextOffsets");
            for (int i = 0; i < 3 && i < starterTextOffsets.length; i++) {
                writeVariableLengthString(String.format("%s?\\e", newStarters.get(i).name), starterTextOffsets[i],
                        true);
            }
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
        for (int k = 0; k < Gen2Constants.fishingGroupCount; k++) {
            EncounterSet es = new EncounterSet();
            es.displayName = "Fishing Group " + (k + 1);
            for (int i = 0; i < Gen2Constants.pokesPerFishingGroup; i++) {
                offset++;
                int pokeNum = rom[offset++] & 0xFF;
                int level = rom[offset++] & 0xFF;
                if (pokeNum == 0) {
                    if (!useTimeOfDay) {
                        // read the encounter they put here for DAY
                        int specialOffset = rootOffset + Gen2Constants.fishingGroupEntryLength
                                * Gen2Constants.pokesPerFishingGroup * Gen2Constants.fishingGroupCount + level * 4 + 2;
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
            for (int k = 0; k < Gen2Constants.timeSpecificFishingGroupCount; k++) {
                EncounterSet es = new EncounterSet();
                es.displayName = "Time-Specific Fishing " + (k + 1);
                for (int i = 0; i < Gen2Constants.pokesPerTSFishingGroup; i++) {
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
        // Unown is banned for Bug Catching Contest (5/8/2016)
        bccES.bannedPokemon.add(pokes[Gen2Constants.unownIndex]);
        areas.add(bccES);

        return areas;
    }

    private int readLandEncounters(int offset, List<EncounterSet> areas, boolean useTimeOfDay) {
        String[] todNames = new String[] { "Morning", "Day", "Night" };
        while ((rom[offset] & 0xFF) != 0xFF) {
            int mapBank = rom[offset] & 0xFF;
            int mapNumber = rom[offset + 1] & 0xFF;
            String mapName = mapNames[mapBank][mapNumber];
            if (useTimeOfDay) {
                for (int i = 0; i < 3; i++) {
                    EncounterSet encset = new EncounterSet();
                    encset.rate = rom[offset + 2 + i] & 0xFF;
                    encset.displayName = mapName + " Grass/Cave (" + todNames[i] + ")";
                    for (int j = 0; j < Gen2Constants.landEncounterSlots; j++) {
                        Encounter enc = new Encounter();
                        enc.level = rom[offset + 5 + (i * Gen2Constants.landEncounterSlots * 2) + (j * 2)] & 0xFF;
                        enc.maxLevel = 0;
                        enc.pokemon = pokes[rom[offset + 5 + (i * Gen2Constants.landEncounterSlots * 2) + (j * 2) + 1]
                                & 0xFF];
                        encset.encounters.add(enc);
                    }
                    areas.add(encset);
                }
            } else {
                // Use Day only
                EncounterSet encset = new EncounterSet();
                encset.rate = rom[offset + 3] & 0xFF;
                encset.displayName = mapName + " Grass/Cave";
                for (int j = 0; j < Gen2Constants.landEncounterSlots; j++) {
                    Encounter enc = new Encounter();
                    enc.level = rom[offset + 5 + Gen2Constants.landEncounterSlots * 2 + (j * 2)] & 0xFF;
                    enc.maxLevel = 0;
                    enc.pokemon = pokes[rom[offset + 5 + Gen2Constants.landEncounterSlots * 2 + (j * 2) + 1] & 0xFF];
                    encset.encounters.add(enc);
                }
                areas.add(encset);
            }
            offset += 5 + 6 * Gen2Constants.landEncounterSlots;
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
            for (int j = 0; j < Gen2Constants.seaEncounterSlots; j++) {
                Encounter enc = new Encounter();
                enc.level = rom[offset + 3 + (j * 2)] & 0xFF;
                enc.maxLevel = 0;
                enc.pokemon = pokes[rom[offset + 3 + (j * 2) + 1] & 0xFF];
                encset.encounters.add(enc);
            }
            areas.add(encset);
            offset += 3 + Gen2Constants.seaEncounterSlots * 2;
        }
        return offset + 1;
    }

    @Override
    public void setEncounters(boolean useTimeOfDay, List<EncounterSet> encounters) {
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
        for (int k = 0; k < Gen2Constants.fishingGroupCount; k++) {
            EncounterSet es = areas.next();
            Iterator<Encounter> encs = es.encounters.iterator();
            for (int i = 0; i < Gen2Constants.pokesPerFishingGroup; i++) {
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
            for (int k = 0; k < Gen2Constants.timeSpecificFishingGroupCount; k++) {
                EncounterSet es = areas.next();
                Iterator<Encounter> encs = es.encounters.iterator();
                for (int i = 0; i < Gen2Constants.pokesPerTSFishingGroup; i++) {
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

    private int writeLandEncounters(int offset, Iterator<EncounterSet> areas, boolean useTimeOfDay) {
        while ((rom[offset] & 0xFF) != 0xFF) {
            if (useTimeOfDay) {
                for (int i = 0; i < 3; i++) {
                    EncounterSet encset = areas.next();
                    Iterator<Encounter> encountersHere = encset.encounters.iterator();
                    for (int j = 0; j < Gen2Constants.landEncounterSlots; j++) {
                        rom[offset + 5 + (i * Gen2Constants.landEncounterSlots * 2) + (j * 2)
                                + 1] = (byte) encountersHere.next().pokemon.number;
                    }
                }
            } else {
                // Write the set to all 3 equally
                EncounterSet encset = areas.next();
                for (int i = 0; i < 3; i++) {
                    Iterator<Encounter> encountersHere = encset.encounters.iterator();
                    for (int j = 0; j < Gen2Constants.landEncounterSlots; j++) {
                        rom[offset + 5 + (i * Gen2Constants.landEncounterSlots * 2) + (j * 2)
                                + 1] = (byte) encountersHere.next().pokemon.number;
                    }
                }
            }
            offset += 5 + 6 * Gen2Constants.landEncounterSlots;
        }
        return offset + 1;
    }

    private int writeSeaEncounters(int offset, Iterator<EncounterSet> areas) {
        while ((rom[offset] & 0xFF) != 0xFF) {
            EncounterSet encset = areas.next();
            Iterator<Encounter> encountersHere = encset.encounters.iterator();
            for (int j = 0; j < Gen2Constants.seaEncounterSlots; j++) {
                rom[offset + 3 + (j * 2) + 1] = (byte) encountersHere.next().pokemon.number;
            }
            offset += 3 + Gen2Constants.seaEncounterSlots * 2;
        }
        return offset + 1;
    }

    @Override
    public List<Trainer> getTrainers() {
        int traineroffset = romEntry.getValue("TrainerDataTableOffset");
        int traineramount = romEntry.getValue("TrainerClassAmount");
        int[] trainerclasslimits = romEntry.arrayEntries.get("TrainerDataClassCounts");

        int[] pointers = new int[traineramount];
        for (int i = 0; i < traineramount; i++) {
            int pointer = readWord(traineroffset + i * 2);
            pointers[i] = calculateOffset(bankOf(traineroffset), pointer);
        }

        List<String> tcnames = this.getTrainerClassNames();

        List<Trainer> allTrainers = new ArrayList<Trainer>();
        for (int i = 0; i < traineramount; i++) {
            int offs = pointers[i];
            int limit = trainerclasslimits[i];
            for (int trnum = 0; trnum < limit; trnum++) {
                Trainer tr = new Trainer();
                tr.offset = offs;
                tr.trainerclass = i;
                String name = readVariableLengthString(offs, false);
                tr.name = name;
                tr.fullDisplayName = tcnames.get(i) + " " + name;
                offs += lengthOfStringAt(offs, false) + 1;
                int dataType = rom[offs] & 0xFF;
                tr.poketype = dataType;
                offs++;
                while ((rom[offs] & 0xFF) != 0xFF) {
                    TrainerPokemon tp = new TrainerPokemon();
                    tp.level = rom[offs] & 0xFF;
                    tp.pokemon = pokes[rom[offs + 1] & 0xFF];
                    offs += 2;
                    if ((dataType & 2) == 2) {
                        tp.heldItem = rom[offs] & 0xFF;
                        offs++;
                    }
                    if ((dataType & 1) == 1) {
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

        Gen2Constants.universalTrainerTags(allTrainers);
        if (romEntry.isCrystal) {
            Gen2Constants.crystalTags(allTrainers);
        } else {
            Gen2Constants.goldSilverTags(allTrainers);
        }

        return allTrainers;
    }

    @Override
    public void setTrainers(List<Trainer> trainerData) {
        int traineroffset = romEntry.getValue("TrainerDataTableOffset");
        int traineramount = romEntry.getValue("TrainerClassAmount");
        int[] trainerclasslimits = romEntry.arrayEntries.get("TrainerDataClassCounts");

        int[] pointers = new int[traineramount];
        for (int i = 0; i < traineramount; i++) {
            int pointer = readWord(traineroffset + i * 2);
            pointers[i] = calculateOffset(bankOf(traineroffset), pointer);
        }

        // Get current movesets in case we need to reset them for certain
        // trainer mons.
        Map<Pokemon, List<MoveLearnt>> movesets = this.getMovesLearnt();

        Iterator<Trainer> allTrainers = trainerData.iterator();
        for (int i = 0; i < traineramount; i++) {
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
                // Write out new trainer data
                rom[offs++] = (byte) tr.poketype;
                Iterator<TrainerPokemon> tPokes = tr.pokemon.iterator();
                for (int tpnum = 0; tpnum < tr.pokemon.size(); tpnum++) {
                    TrainerPokemon tp = tPokes.next();
                    rom[offs] = (byte) tp.level;
                    rom[offs + 1] = (byte) tp.pokemon.number;
                    offs += 2;
                    if ((tr.poketype & 2) == 2) {
                        rom[offs] = (byte) tp.heldItem;
                        offs++;
                    }
                    if ((tr.poketype & 1) == 1) {
                        if (tp.resetMoves) {
                            int[] pokeMoves = RomFunctions.getMovesAtLevel(tp.pokemon, movesets, tp.level);
                            for (int m = 0; m < 4; m++) {
                                rom[offs + m] = (byte) pokeMoves[m];
                            }
                        } else {
                            rom[offs] = (byte) tp.move1;
                            rom[offs + 1] = (byte) tp.move2;
                            rom[offs + 2] = (byte) tp.move3;
                            rom[offs + 3] = (byte) tp.move4;
                        }
                        offs += 4;
                    }
                }
                rom[offs] = (byte) 0xFF;
                offs++;
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
        for (int i = 1; i <= Gen2Constants.pokemonCount; i++) {
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
        writeEvosAndMovesLearnt(false, movesets);
    }

    @Override
    public List<Integer> getMovesBannedFromLevelup() {
        // ban thief because trainers are broken with it
        return Gen2Constants.bannedLevelupMoves;
    }

    private static class StaticPokemon {
        protected int[] offsets;

        public StaticPokemon(int... offsets) {
            this.offsets = offsets;
        }

        public Pokemon getPokemon(Gen2RomHandler rh) {
            return rh.pokes[rh.rom[offsets[0]] & 0xFF];
        }

        public void setPokemon(Gen2RomHandler rh, Pokemon pkmn) {
            for (int offset : offsets) {
                rh.rom[offset] = (byte) pkmn.number;
            }
        }
    }

    private static class StaticPokemonGameCorner extends StaticPokemon {

        public StaticPokemonGameCorner(int... offsets) {
            super(offsets);
        }

        @Override
        public void setPokemon(Gen2RomHandler rh, Pokemon pkmn) {
            // Last offset is a pointer to the name
            int offsetSize = offsets.length;
            for (int i = 0; i < offsetSize - 1; i++) {
                rh.rom[offsets[i]] = (byte) pkmn.number;
            }
            rh.writePaddedPokemonName(pkmn.name, rh.romEntry.getValue("GameCornerPokemonNameLength"),
                    offsets[offsetSize - 1]);
        }
    }

    @Override
    public List<Pokemon> getStaticPokemon() {
        List<Pokemon> statics = new ArrayList<Pokemon>();
        if (romEntry.getValue("StaticPokemonSupport") > 0) {
            for (StaticPokemon sp : romEntry.staticPokemon) {
                statics.add(sp.getPokemon(this));
            }
        }
        if (romEntry.getValue("StaticPokemonOddEggOffset") > 0) {
            int oeOffset = romEntry.getValue("StaticPokemonOddEggOffset");
            int oeSize = romEntry.getValue("StaticPokemonOddEggDataSize");
            for (int i = 0; i < Gen2Constants.oddEggPokemonCount; i++) {
                statics.add(pokes[rom[oeOffset + i * oeSize] & 0xFF]);
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

        int desiredSize = romEntry.staticPokemon.size();
        if (romEntry.getValue("StaticPokemonOddEggOffset") > 0) {
            desiredSize += Gen2Constants.oddEggPokemonCount;
        }

        if (staticPokemon.size() != desiredSize) {
            return false;
        }

        Iterator<Pokemon> statics = staticPokemon.iterator();
        for (StaticPokemon sp : romEntry.staticPokemon) {
            sp.setPokemon(this, statics.next());
        }

        if (romEntry.getValue("StaticPokemonOddEggOffset") > 0) {
            int oeOffset = romEntry.getValue("StaticPokemonOddEggOffset");
            int oeSize = romEntry.getValue("StaticPokemonOddEggDataSize");
            for (int i = 0; i < Gen2Constants.oddEggPokemonCount; i++) {
                rom[oeOffset + i * oeSize] = (byte) statics.next().number;
            }
        }

        return true;
    }

    @Override
    public boolean canChangeStaticPokemon() {
        return (romEntry.getValue("StaticPokemonSupport") > 0);
    }

    @Override
    public List<Pokemon> bannedForStaticPokemon() {
        return Arrays.asList(pokes[Gen2Constants.unownIndex]); // Unown banned
    }

    private void writePaddedPokemonName(String name, int length, int offset) {
        String paddedName = String.format("%-" + length + "s", name);
        byte[] rawData = translateString(paddedName);
        for (int i = 0; i < length; i++) {
            rom[offset + i] = rawData[i];
        }
    }

    @Override
    public List<Integer> getTMMoves() {
        List<Integer> tms = new ArrayList<Integer>();
        int offset = romEntry.getValue("TMMovesOffset");
        for (int i = 1; i <= Gen2Constants.tmCount; i++) {
            tms.add(rom[offset + (i - 1)] & 0xFF);
        }
        return tms;
    }

    @Override
    public List<Integer> getHMMoves() {
        List<Integer> hms = new ArrayList<Integer>();
        int offset = romEntry.getValue("TMMovesOffset");
        for (int i = 1; i <= Gen2Constants.hmCount; i++) {
            hms.add(rom[offset + Gen2Constants.tmCount + (i - 1)] & 0xFF);
        }
        return hms;
    }

    @Override
    public void setTMMoves(List<Integer> moveIndexes) {
        int offset = romEntry.getValue("TMMovesOffset");
        for (int i = 1; i <= Gen2Constants.tmCount; i++) {
            rom[offset + (i - 1)] = moveIndexes.get(i - 1).byteValue();
        }

        // TM Text
        String[] moveNames = readMoveNames();
        for (TMTextEntry tte : romEntry.tmTexts) {
            String moveName = moveNames[moveIndexes.get(tte.number - 1)];
            String text = tte.template.replace("%m", moveName);
            writeVariableLengthString(text, tte.offset, true);
        }
    }

    @Override
    public int getTMCount() {
        return Gen2Constants.tmCount;
    }

    @Override
    public int getHMCount() {
        return Gen2Constants.hmCount;
    }

    @Override
    public Map<Pokemon, boolean[]> getTMHMCompatibility() {
        Map<Pokemon, boolean[]> compat = new TreeMap<Pokemon, boolean[]>();
        for (int i = 1; i <= Gen2Constants.pokemonCount; i++) {
            int baseStatsOffset = romEntry.getValue("PokemonStatsOffset") + (i - 1) * Gen2Constants.baseStatsEntrySize;
            Pokemon pkmn = pokes[i];
            boolean[] flags = new boolean[Gen2Constants.tmCount + Gen2Constants.hmCount + 1];
            for (int j = 0; j < 8; j++) {
                readByteIntoFlags(flags, j * 8 + 1, baseStatsOffset + Gen2Constants.bsTMHMCompatOffset + j);
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
                    + (pkmn.number - 1) * Gen2Constants.baseStatsEntrySize;
            for (int j = 0; j < 8; j++) {
                if (!romEntry.isCrystal || j != 7) {
                    rom[baseStatsOffset + Gen2Constants.bsTMHMCompatOffset + j] = getByteFromFlags(flags, j * 8 + 1);
                } else {
                    // Move tutor data
                    // bits 1,2,3 of byte 7
                    int changedByte = getByteFromFlags(flags, j * 8 + 1) & 0xFF;
                    int currentByte = rom[baseStatsOffset + Gen2Constants.bsTMHMCompatOffset + j];
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
        if (romEntry.getValue("MoveTutorMenuOffset") > 0 && romEntry.getValue("MoveTutorMenuNewSpace") > 0) {
            String[] moveNames = readMoveNames();
            String[] names = new String[] { moveNames[moves.get(0)], moveNames[moves.get(1)], moveNames[moves.get(2)],
                    Gen2Constants.mtMenuCancelString };
            int menuOffset = romEntry.getValue("MoveTutorMenuNewSpace");
            rom[menuOffset++] = Gen2Constants.mtMenuInitByte;
            rom[menuOffset++] = 0x4;
            for (int i = 0; i < 4; i++) {
                byte[] trans = translateString(names[i]);
                System.arraycopy(trans, 0, rom, menuOffset, trans.length);
                menuOffset += trans.length;
                rom[menuOffset++] = GBConstants.stringTerminator;
            }
            int pointerOffset = romEntry.getValue("MoveTutorMenuOffset");
            writeWord(pointerOffset, makeGBPointer(romEntry.getValue("MoveTutorMenuNewSpace")));
        }
    }

    @Override
    public Map<Pokemon, boolean[]> getMoveTutorCompatibility() {
        if (!romEntry.isCrystal) {
            return new TreeMap<Pokemon, boolean[]>();
        }
        Map<Pokemon, boolean[]> compat = new TreeMap<Pokemon, boolean[]>();
        for (int i = 1; i <= Gen2Constants.pokemonCount; i++) {
            int baseStatsOffset = romEntry.getValue("PokemonStatsOffset") + (i - 1) * Gen2Constants.baseStatsEntrySize;
            Pokemon pkmn = pokes[i];
            boolean[] flags = new boolean[4];
            int mtByte = rom[baseStatsOffset + Gen2Constants.bsMTCompatOffset] & 0xFF;
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
                    + (pkmn.number - 1) * Gen2Constants.baseStatsEntrySize;
            int origMtByte = rom[baseStatsOffset + Gen2Constants.bsMTCompatOffset] & 0xFF;
            int mtByte = origMtByte & 0x01;
            for (int j = 1; j <= 3; j++) {
                mtByte |= flags[j] ? (1 << j) : 0;
            }
            rom[baseStatsOffset + Gen2Constants.bsMTCompatOffset] = (byte) mtByte;
        }
    }

    @Override
    public String getROMName() {
        if (isVietCrystal) {
            return Gen2Constants.vietCrystalROMName;
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

    private void populateEvolutions() {
        for (Pokemon pkmn : pokes) {
            if (pkmn != null) {
                pkmn.evolutionsFrom.clear();
                pkmn.evolutionsTo.clear();
            }
        }

        int pointersOffset = romEntry.getValue("PokemonMovesetsTableOffset");
        for (int i = 1; i <= Gen2Constants.pokemonCount; i++) {
            int pointer = readWord(pointersOffset + (i - 1) * 2);
            int realPointer = calculateOffset(bankOf(pointersOffset), pointer);
            Pokemon pkmn = pokes[i];
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
                Evolution evo = new Evolution(pokes[thisPoke], pokes[otherPoke], true, type, extraInfo);
                if (!pkmn.evolutionsFrom.contains(evo)) {
                    pkmn.evolutionsFrom.add(evo);
                    pokes[otherPoke].evolutionsTo.add(evo);
                }
                realPointer += (method == 5 ? 4 : 3);
            }
            // split evos don't carry stats
            if (pkmn.evolutionsFrom.size() > 1) {
                for (Evolution e : pkmn.evolutionsFrom) {
                    e.carryStats = false;
                }
            }
        }
    }

    @Override
    public void removeTradeEvolutions(boolean changeMoveEvos) {
        // no move evos, so no need to check for those
        log("--Removing Trade Evolutions--");
        for (Pokemon pkmn : pokes) {
            if (pkmn != null) {
                for (Evolution evol : pkmn.evolutionsFrom) {
                    if (evol.type == EvolutionType.TRADE || evol.type == EvolutionType.TRADE_ITEM) {
                        // change
                        if (evol.from.number == Gen2Constants.slowpokeIndex) {
                            // Slowpoke: Make water stone => Slowking
                            evol.type = EvolutionType.STONE;
                            evol.extraInfo = 24; // water stone
                            logEvoChangeStone(evol.from.name, evol.to.name, itemNames[24]);
                        } else if (evol.from.number == Gen2Constants.seadraIndex) {
                            // Seadra: level 40
                            evol.type = EvolutionType.LEVEL;
                            evol.extraInfo = 40; // level
                            logEvoChangeLevel(evol.from.name, evol.to.name, 40);
                        } else if (evol.from.number == Gen2Constants.poliwhirlIndex
                                || evol.type == EvolutionType.TRADE) {
                            // Poliwhirl or any of the original 4 trade evos
                            // Level 37
                            evol.type = EvolutionType.LEVEL;
                            evol.extraInfo = 37; // level
                            logEvoChangeLevel(evol.from.name, evol.to.name, 37);
                        } else {
                            // A new trade evo of a single stage Pokemon
                            // level 30
                            evol.type = EvolutionType.LEVEL;
                            evol.extraInfo = 30; // level
                            logEvoChangeLevel(evol.from.name, evol.to.name, 30);
                        }
                    }
                }
            }
        }
        logBlankLine();

    }

    @Override
    public boolean canChangeTrainerText() {
        return romEntry.getValue("CanChangeTrainerText") > 0;
    }

    @Override
    public List<String> getTrainerNames() {
        int traineroffset = romEntry.getValue("TrainerDataTableOffset");
        int traineramount = romEntry.getValue("TrainerClassAmount");
        int[] trainerclasslimits = romEntry.arrayEntries.get("TrainerDataClassCounts");

        int[] pointers = new int[traineramount];
        for (int i = 0; i < traineramount; i++) {
            int pointer = readWord(traineroffset + i * 2);
            pointers[i] = calculateOffset(bankOf(traineroffset), pointer);
        }

        List<String> allTrainers = new ArrayList<String>();
        for (int i = 0; i < traineramount; i++) {
            int offs = pointers[i];
            int limit = trainerclasslimits[i];
            for (int trnum = 0; trnum < limit; trnum++) {
                String name = readVariableLengthString(offs, false);
                allTrainers.add(name);
                offs += lengthOfStringAt(offs, false) + 1;
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
            int[] trainerclasslimits = romEntry.arrayEntries.get("TrainerDataClassCounts");

            int[] pointers = new int[traineramount];
            for (int i = 0; i < traineramount; i++) {
                int pointer = readWord(traineroffset + i * 2);
                pointers[i] = calculateOffset(bankOf(traineroffset), pointer);
            }

            // Build up new trainer data using old as a guideline.
            int[] offsetsInNew = new int[traineramount];
            int oInNewCurrent = 0;
            Iterator<String> allTrainers = trainerNames.iterator();
            ByteArrayOutputStream newData = new ByteArrayOutputStream();
            try {
                for (int i = 0; i < traineramount; i++) {
                    int offs = pointers[i];
                    int limit = trainerclasslimits[i];
                    offsetsInNew[i] = oInNewCurrent;
                    for (int trnum = 0; trnum < limit; trnum++) {
                        String newName = allTrainers.next();
                        byte[] newNameStr = translateString(newName);
                        newData.write(newNameStr);
                        newData.write(GBConstants.stringTerminator);
                        oInNewCurrent += newNameStr.length + 1;
                        offs += lengthOfStringAt(offs, false) + 1;
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
                                oInNewCurrent++;
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
                int tdBase = pointers[0];
                System.arraycopy(newTrainerData, 0, rom, pointers[0], newTrainerData.length);

                // Finally, update the pointers
                for (int i = 1; i < traineramount; i++) {
                    int newOffset = tdBase + offsetsInNew[i];
                    writeWord(traineroffset + i * 2, makeGBPointer(newOffset));
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
        return Gen2Constants.maxTrainerNameLength;
    }

    @Override
    public int maxSumOfTrainerNameLengths() {
        return romEntry.getValue("MaxSumOfTrainerNameLengths");
    }

    @Override
    public List<Character> getBannedTrainerNameCharacters() {
        return Arrays.asList(new Character[] { '9' });
    }

    @Override
    public List<Integer> getTCNameLengthsByTrainer() {
        int traineramount = romEntry.getValue("TrainerClassAmount");
        int[] trainerclasslimits = romEntry.arrayEntries.get("TrainerDataClassCounts");
        List<String> tcNames = this.getTrainerClassNames();
        List<Integer> tcLengthsByT = new ArrayList<Integer>();

        for (int i = 0; i < traineramount; i++) {
            int len = internalStringLength(tcNames.get(i));
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
            String name = readVariableLengthString(offset, false);
            offset += lengthOfStringAt(offset, false) + 1;
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
                int len = lengthOfStringAt(offset, false) + 1;
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
    public List<Integer> getDoublesTrainerClasses() {
        int[] doublesClasses = romEntry.arrayEntries.get("DoublesTrainerClasses");
        List<Integer> doubles = new ArrayList<Integer>();
        for (int tClass : doublesClasses) {
            doubles.add(tClass);
        }
        return doubles;
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
    public int miscTweaksAvailable() {
        int available = MiscTweak.LOWER_CASE_POKEMON_NAMES.getValue();
        if (romEntry.codeTweaks.get("BWXPTweak") != null) {
            available |= MiscTweak.BW_EXP_PATCH.getValue();
        }
        if (romEntry.getValue("TextDelayFunctionOffset") != 0) {
            available |= MiscTweak.FASTEST_TEXT.getValue();
        }
        if (romEntry.arrayEntries.containsKey("CatchingTutorialOffsets")) {
            available |= MiscTweak.RANDOMIZE_CATCHING_TUTORIAL.getValue();
        }
        available |= MiscTweak.BAN_LUCKY_EGG.getValue();
        return available;
    }

    @Override
    public void applyMiscTweak(MiscTweak tweak) {
        if (tweak == MiscTweak.BW_EXP_PATCH) {
            applyBWEXPPatch();
        } else if (tweak == MiscTweak.FASTEST_TEXT) {
            applyFastestTextPatch();
        } else if (tweak == MiscTweak.LOWER_CASE_POKEMON_NAMES) {
            applyCamelCaseNames();
        } else if (tweak == MiscTweak.RANDOMIZE_CATCHING_TUTORIAL) {
            randomizeCatchingTutorial();
        } else if (tweak == MiscTweak.BAN_LUCKY_EGG) {
            allowedItems.banSingles(Gen2Constants.luckyEggIndex);
            nonBadItems.banSingles(Gen2Constants.luckyEggIndex);
        }
    }

    private void randomizeCatchingTutorial() {
        if (romEntry.arrayEntries.containsKey("CatchingTutorialOffsets")) {
            // Pick a pokemon
            int pokemon = this.random.nextInt(Gen2Constants.pokemonCount) + 1;
            while (pokemon == Gen2Constants.unownIndex) {
                // Unown is banned
                pokemon = this.random.nextInt(Gen2Constants.pokemonCount) + 1;
            }

            int[] offsets = romEntry.arrayEntries.get("CatchingTutorialOffsets");
            for (int offset : offsets) {
                rom[offset] = (byte) pokemon;
            }
        }

    }

    private void applyBWEXPPatch() {
        String patchName = romEntry.codeTweaks.get("BWXPTweak");
        if (patchName == null) {
            return;
        }

        try {
            FileFunctions.applyPatch(rom, patchName);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void applyFastestTextPatch() {
        if (romEntry.getValue("TextDelayFunctionOffset") != 0) {
            rom[romEntry.getValue("TextDelayFunctionOffset")] = (byte) GBConstants.gbZ80Ret;
        }
    }

    @Override
    public void applySignature() {
        // Intro sprite

        // Pick a pokemon
        int pokemon = this.random.nextInt(Gen2Constants.pokemonCount) + 1;
        while (pokemon == Gen2Constants.unownIndex) {
            // Unown is banned
            pokemon = this.random.nextInt(Gen2Constants.pokemonCount) + 1;
        }

        rom[romEntry.getValue("IntroSpriteOffset")] = (byte) pokemon;
        rom[romEntry.getValue("IntroCryOffset")] = (byte) pokemon;

    }

    @Override
    public ItemList getAllowedItems() {
        return allowedItems;
    }

    @Override
    public ItemList getNonBadItems() {
        return nonBadItems;
    }

    private void loadItemNames() {
        itemNames = new String[256];
        itemNames[0] = "glitch";
        // trying to emulate pretty much what the game does here
        // normal items
        int origOffset = romEntry.getValue("ItemNamesOffset");
        int itemNameOffset = origOffset;
        for (int index = 1; index <= 0x100; index++) {
            if (itemNameOffset / GBConstants.bankSize > origOffset / GBConstants.bankSize) {
                // the game would continue making its merry way into VRAM here,
                // but we don't have VRAM to simulate.
                // just give up.
                break;
            }
            int startOfText = itemNameOffset;
            while ((rom[itemNameOffset] & 0xFF) != GBConstants.stringTerminator) {
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
        rom[offset + Gen2Constants.fleeingSetTwoOffset] = (byte) 0xFF;
        rom[offset + Gen2Constants.fleeingSetThreeOffset] = (byte) 0xFF;
    }

    private void loadLandmarkNames() {

        int lmOffset = romEntry.getValue("LandmarkTableOffset");
        int lmBank = bankOf(lmOffset);
        int lmCount = romEntry.getValue("LandmarkCount");

        landmarkNames = new String[lmCount];

        for (int i = 0; i < lmCount; i++) {
            int lmNameOffset = calculateOffset(lmBank, readWord(lmOffset + i * 4 + 2));
            landmarkNames[i] = readVariableLengthString(lmNameOffset, false).replace("\\x1F", " ");
        }

    }

    private void preprocessMaps() {
        itemOffs = new ArrayList<ItemLocationInner>();

        int mhOffset = romEntry.getValue("MapHeaders");
        int mapGroupCount = Gen2Constants.mapGroupCount;
        int mapsInLastGroup = Gen2Constants.mapsInLastGroup;
        int mhBank = bankOf(mhOffset);
        mapNames = new String[mapGroupCount + 1][100];

        int[] groupOffsets = new int[mapGroupCount];
        for (int i = 0; i < mapGroupCount; i++) {
            groupOffsets[i] = calculateOffset(mhBank, readWord(mhOffset + i * 2));
        }

        // Read maps
        for (int mg = 0; mg < mapGroupCount; mg++) {
            int offset = groupOffsets[mg];
            int maxOffset = (mg == mapGroupCount - 1) ? (mhBank + 1) * GBConstants.bankSize : groupOffsets[mg + 1];
            int map = 0;
            int maxMap = (mg == mapGroupCount - 1) ? mapsInLastGroup : Integer.MAX_VALUE;
            while (offset < maxOffset && map < maxMap) {
                processMapAt(offset, mg + 1, map + 1);
                offset += 9;
                map++;
            }
        }
    }
    
    private class ItemLocationInner {
        private int mapBank;
        private int mapNumber;
        private int x;
        private int y;
        private int offset;
        private boolean hidden;
        public ItemLocationInner(int mapBank, int mapNumber, int x, int y, int offset, boolean hidden) {
            super();
            this.mapBank = mapBank;
            this.mapNumber = mapNumber;
            this.x = x;
            this.y = y;
            this.offset = offset;
            this.hidden = hidden;
        }
        @Override
        public String toString() {
            return String.format("%s (%d.%d) @ %d, %d (%s)", mapNames[mapBank][mapNumber], mapBank, mapNumber, x, y, hidden ? "hidden" : "visible");
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
                itemOffs.add(new ItemLocationInner(mapBank, mapNumber, rom[ehOffset + sp*5 + 1]&0xFF, rom[ehOffset + sp*5]&0xFF, spOffset + 2, true));
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
                itemOffs.add(new ItemLocationInner(mapBank, mapNumber, (rom[ehOffset + p*13 + 2]&0xFF)-4, (rom[ehOffset + p*13 + 1]&0xFF)-4, pOffset, false));
            }
        }

    }

    @Override
    public List<Integer> getRequiredFieldTMs() {
        return Gen2Constants.requiredFieldTMs;
    }

    @Override
    public List<FieldTM> getCurrentFieldTMs() {
        List<FieldTM> fieldTMs = new ArrayList<FieldTM>();

        for (ItemLocationInner il : itemOffs) {
            int itemHere = rom[il.offset] & 0xFF;
            if (Gen2Constants.allowedItems.isTM(itemHere)) {
                int thisTM = 0;
                if (itemHere >= Gen2Constants.tmBlockOneIndex
                        && itemHere < Gen2Constants.tmBlockOneIndex + Gen2Constants.tmBlockOneSize) {
                    thisTM = itemHere - Gen2Constants.tmBlockOneIndex + 1;
                } else if (itemHere >= Gen2Constants.tmBlockTwoIndex
                        && itemHere < Gen2Constants.tmBlockTwoIndex + Gen2Constants.tmBlockTwoSize) {
                    thisTM = itemHere - Gen2Constants.tmBlockTwoIndex + 1 + Gen2Constants.tmBlockOneSize; // TM
                                                                                                          // block
                                                                                                          // 2
                                                                                                          // offset
                } else {
                    thisTM = itemHere - Gen2Constants.tmBlockThreeIndex + 1 + Gen2Constants.tmBlockOneSize
                            + Gen2Constants.tmBlockTwoSize; // TM block 3 offset
                }
                FieldTM tmObj = new FieldTM(il.toString(), thisTM);
                // hack for the bug catching contest repeat TM28
                if (fieldTMs.contains(tmObj) == false) {
                    fieldTMs.add(tmObj);
                }
            }
        }
        return fieldTMs;
    }

    @Override
    public void setFieldTMs(List<Integer> fieldTMs) {
        Iterator<Integer> iterTMs = fieldTMs.iterator();
        int[] givenTMs = new int[256];

        for (ItemLocationInner il : itemOffs) {
            int itemHere = rom[il.offset] & 0xFF;
            if (Gen2Constants.allowedItems.isTM(itemHere)) {
                // Cache replaced TMs to duplicate bug catching contest TM
                if (givenTMs[itemHere] != 0) {
                    rom[il.offset] = (byte) givenTMs[itemHere];
                } else {
                    // Replace this with a TM from the list
                    int tm = iterTMs.next();
                    if (tm >= 1 && tm <= Gen2Constants.tmBlockOneSize) {
                        tm += Gen2Constants.tmBlockOneIndex - 1;
                    } else if (tm >= Gen2Constants.tmBlockOneSize + 1
                            && tm <= Gen2Constants.tmBlockOneSize + Gen2Constants.tmBlockTwoSize) {
                        tm += Gen2Constants.tmBlockTwoIndex - 1 - Gen2Constants.tmBlockOneSize;
                    } else {
                        tm += Gen2Constants.tmBlockThreeIndex - 1 - Gen2Constants.tmBlockOneSize
                                - Gen2Constants.tmBlockTwoSize;
                    }
                    givenTMs[itemHere] = tm;
                    rom[il.offset] = (byte) tm;
                }
            }
        }
    }

    @Override
    public List<ItemLocation> getRegularFieldItems() {
        List<ItemLocation> fieldItems = new ArrayList<ItemLocation>();

        for (ItemLocationInner il : itemOffs) {
            int itemHere = rom[il.offset] & 0xFF;
            if (Gen2Constants.allowedItems.isAllowed(itemHere) && !(Gen2Constants.allowedItems.isTM(itemHere))) {
                fieldItems.add(new ItemLocation(il.toString(), itemHere));
            }
        }
        return fieldItems;
    }

    @Override
    public void setRegularFieldItems(List<Integer> items) {
        Iterator<Integer> iterItems = items.iterator();

        for (ItemLocationInner il : itemOffs) {
            int itemHere = rom[il.offset] & 0xFF;
            if (Gen2Constants.allowedItems.isAllowed(itemHere) && !(Gen2Constants.allowedItems.isTM(itemHere))) {
                // Replace it
                rom[il.offset] = (byte) (iterItems.next().intValue());
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
        int entryLength = nicknameLength + otLength + 9;
        if (entryLength % 2 != 0) {
            entryLength++;
        }

        for (int entry = 0; entry < tableSize; entry++) {
            if (unusedOffset < unused.length && unused[unusedOffset] == entry) {
                unusedOffset++;
                continue;
            }
            IngameTrade trade = new IngameTrade();
            int entryOffset = tableOffset + entry * entryLength;
            trade.requestedPokemon = pokes[rom[entryOffset + 1] & 0xFF];
            trade.givenPokemon = pokes[rom[entryOffset + 2] & 0xFF];
            trade.nickname = readString(entryOffset + 3, nicknameLength, false);
            int atkdef = rom[entryOffset + 3 + nicknameLength] & 0xFF;
            int spdspc = rom[entryOffset + 4 + nicknameLength] & 0xFF;
            trade.ivs = new int[] { (atkdef >> 4) & 0xF, atkdef & 0xF, (spdspc >> 4) & 0xF, spdspc & 0xF };
            trade.item = rom[entryOffset + 5 + nicknameLength] & 0xFF;
            trade.otId = readWord(entryOffset + 6 + nicknameLength);
            trade.otName = readString(entryOffset + 8 + nicknameLength, otLength, false);
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
                writeFixedLengthString(trade.nickname, entryOffset + 3, nicknameLength);
            }
            rom[entryOffset + 3 + nicknameLength] = (byte) (trade.ivs[0] << 4 | trade.ivs[1]);
            rom[entryOffset + 4 + nicknameLength] = (byte) (trade.ivs[2] << 4 | trade.ivs[3]);
            rom[entryOffset + 5 + nicknameLength] = (byte) trade.item;
            writeWord(entryOffset + 6 + nicknameLength, trade.otId);
            if (romEntry.getValue("CanChangeTrainerText") > 0) {
                writeFixedLengthString(trade.otName, entryOffset + 8 + nicknameLength, otLength);
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

    private void writeEvosAndMovesLearnt(boolean writeEvos, Map<Pokemon, List<MoveLearnt>> movesets) {
        // this assumes that the evo/attack pointers & data
        // are at the end of the bank
        // which, in every clean G/S/C rom supported, they are
        // specify null to either argument to copy old values
        int movesEvosStart = romEntry.getValue("PokemonMovesetsTableOffset");
        int movesEvosBank = bankOf(movesEvosStart);
        byte[] pointerTable = new byte[Gen2Constants.pokemonCount * 2];
        int startOfNextBank = ((movesEvosStart / GBConstants.bankSize) + 1) * GBConstants.bankSize;
        int dataBlockSize = startOfNextBank - (movesEvosStart + pointerTable.length);
        int dataBlockOffset = movesEvosStart + pointerTable.length;
        byte[] dataBlock = new byte[dataBlockSize];
        int offsetInData = 0;
        for (int i = 1; i <= Gen2Constants.pokemonCount; i++) {
            // determine pointer
            int oldDataOffset = calculateOffset(movesEvosBank, readWord(movesEvosStart + (i - 1) * 2));
            int offsetStart = dataBlockOffset + offsetInData;
            boolean evoWritten = false;
            if (!writeEvos) {
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
                for (Evolution evo : pokes[i].evolutionsFrom) {
                    // write evos
                    dataBlock[offsetInData++] = (byte) evo.type.toIndex(2);
                    if (evo.type == EvolutionType.LEVEL || evo.type == EvolutionType.STONE
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
                    dataBlock[offsetInData++] = (byte) evo.to.number;
                    evoWritten = true;
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
        System.arraycopy(pointerTable, 0, rom, movesEvosStart, pointerTable.length);
        System.arraycopy(dataBlock, 0, rom, dataBlockOffset, dataBlock.length);
    }

    @Override
    public boolean supportsFourStartingMoves() {
        return (romEntry.getValue("SupportsFourStartingMoves") > 0);
    }

    @Override
    public List<Integer> getGameBreakingMoves() {
        // add OHKO moves for gen2 because x acc is still broken
        return Gen2Constants.brokenMoves;
    }

    @Override
    public List<Integer> getFieldMoves() {
        // cut, fly, surf, strength, flash,
        // dig, teleport, whirlpool, waterfall,
        // rock smash, headbutt, sweet scent
        // not softboiled or milk drink
        return Gen2Constants.fieldMoves;
    }

    @Override
    public List<Integer> getEarlyRequiredHMMoves() {
        // just cut
        return Gen2Constants.earlyRequiredHMMoves;
    }

    @Override
    public BufferedImage getMascotImage() {
        Pokemon mascot = randomPokemon();
        while (mascot.number == Gen2Constants.unownIndex) {
            // Unown is banned as handling it would add a ton of extra effort.
            mascot = randomPokemon();
        }

        // Each Pokemon has a front and back pic with a bank and a pointer
        // (3*2=6)
        // There is no zero-entry.
        int picPointer = romEntry.getValue("PicPointers") + (mascot.number - 1) * 6;
        int picWidth = mascot.picDimensions & 0x0F;
        int picHeight = (mascot.picDimensions >> 4) & 0x0F;

        int picBank = (rom[picPointer] & 0xFF);
        if (romEntry.isCrystal) {
            // Crystal pic banks are offset by x36 for whatever reason.
            picBank += 0x36;
        } else {
            // Hey, G/S are dumb too! Arbitrarily redirected bank numbers.
            if (picBank == 0x13) {
                picBank = 0x1F;
            } else if (picBank == 0x14) {
                picBank = 0x20;
            } else if (picBank == 0x1F) {
                picBank = 0x2E;
            }
        }
        int picOffset = calculateOffset(picBank, readWord(picPointer + 1));

        Gen2Decmp mscSprite = new Gen2Decmp(rom, picOffset, picWidth, picHeight);
        int w = picWidth * 8;
        int h = picHeight * 8;

        // Palette?
        // Two colors per Pokemon + two more for shiny, unlike pics there is a
        // zero-entry.
        // Black and white are left alone at the start and end of the palette.
        int[] palette = new int[] { 0xFFFFFFFF, 0xFFAAAAAA, 0xFF666666, 0xFF000000 };
        int paletteOffset = romEntry.getValue("PokemonPalettes") + mascot.number * 8;
        if (random.nextInt(10) == 0) {
            // Use shiny instead
            paletteOffset += 4;
        }
        for (int i = 0; i < 2; i++) {
            palette[i + 1] = GFXFunctions.conv16BitColorToARGB(readWord(paletteOffset + i * 2));
        }

        byte[] data = mscSprite.getFlattenedData();

        BufferedImage bim = GFXFunctions.drawTiledImage(data, palette, w, h, 8);
        GFXFunctions.pseudoTransparency(bim, palette[0]);

        return bim;
    }
    
    @Override
    public List<Type> getVanillaTrainerTyping(Trainer trainer) {
        List<Type> typing = new ArrayList<Type>();
        //types added multiple times for weighting
        if(trainer.tag != null)
        {
            if(trainer.tag.equals("GYM1")) {
                typing.add(Type.FLYING);
            } else if(trainer.tag.equals("GYM2")) {
                typing.add(Type.BUG);
            } else if(trainer.tag.equals("GYM3")) {
                typing.add(Type.NORMAL);
            } else if(trainer.tag.equals("GYM4")) {
                typing.add(Type.GHOST);
            } else if(trainer.tag.equals("GYM5")) {
                typing.add(Type.FIGHTING);
            } else if(trainer.tag.equals("GYM6")) {
                typing.add(Type.STEEL);
            } else if(trainer.tag.equals("GYM7")) {
                typing.add(Type.ICE);
                typing.add(Type.ICE);
                typing.add(Type.ICE);
                typing.add(Type.WATER);
            } else if(trainer.tag.equals("GYM8")) {
                typing.add(Type.DRAGON);
            } else if(trainer.tag.equals("GYM9")) {
                typing.add(Type.ROCK);
            } else if(trainer.tag.equals("GYM10")) {
                typing.add(Type.WATER);
            } else if(trainer.tag.equals("GYM11")) {
                typing.add(Type.ELECTRIC);
            } else if(trainer.tag.equals("GYM12")) {
                typing.add(Type.GRASS);
            } else if(trainer.tag.equals("GYM13")) {
                typing.add(Type.POISON);
            } else if(trainer.tag.equals("GYM14")) {
                typing.add(Type.PSYCHIC);
            } else if(trainer.tag.equals("GYM15")) {
                typing.add(Type.FIRE);
            } else if(trainer.tag.equals("GYM16")) {
                //blue does not have a typing
            } else if(trainer.tag.equals("ELITE1")) {
                typing.add(Type.PSYCHIC);
            } else if(trainer.tag.equals("ELITE2")) {
                typing.add(Type.POISON);
                typing.add(Type.POISON);
                typing.add(Type.POISON);
                typing.add(Type.BUG);
                typing.add(Type.STEEL);
            } else if(trainer.tag.equals("ELITE3")) {
                typing.add(Type.FIGHTING);
                typing.add(Type.FIGHTING);
                typing.add(Type.FIGHTING);
                typing.add(Type.GROUND);
            } else if(trainer.tag.equals("ELITE4")) {
                typing.add(Type.DARK);
                typing.add(Type.DARK);
                typing.add(Type.DARK);
                typing.add(Type.GHOST);
                typing.add(Type.POISON);
            }
        }
        return typing;
    }

    @Override
    public int getOranIndex() {
        //no oran berry, return equivalent: berry instead
        return 173;
    }
    
    @Override
    public int getSitrusIndex() {
        //no sitrus berry, return equivalent: gold berry instead
        return 174;
    }
    
    @Override
    public int getRandomHoldItem(TrainerPokemon tpk) {
        //Quick Claw, King's Rock, Focus Band, Scope Lens, Leftovers, Berserk Gene, Gold Berry, Bright Powder, Miracle Berry
        int[] competitiveHeldItemsIndexesPreInit = new int[] {73, 82, 119, 140, 146, 152, 174, 3, 109};
        List<Integer> competitiveHeldItemsIndexes = new ArrayList<Integer>();
        for(int i : competitiveHeldItemsIndexesPreInit) {
            competitiveHeldItemsIndexes.add(new Integer(i));
        }
        List<HoldItem> competitiveHeldItems = new ArrayList<HoldItem>();
        for(Integer i : competitiveHeldItemsIndexes)
        {
            competitiveHeldItems.add(new HoldItem(i));
        }
        
        List<HoldItem> restrictedHeldItems = new ArrayList<HoldItem>();
        
        restrictedHeldItems.add(new HoldItem(98, Type.FIGHTING)); //Blackbelt
        restrictedHeldItems.add(new HoldItem(102, Type.DARK)); //Blackglasses
        restrictedHeldItems.add(new HoldItem(138, Type.FIRE)); //Charcoal
        restrictedHeldItems.add(new HoldItem(144, Type.DRAGON)); //Dragon Fang
        restrictedHeldItems.add(new HoldItem(125, Type.ROCK)); //Hard Stone
        restrictedHeldItems.add(new HoldItem(108, Type.ELECTRIC)); //Magnet
        restrictedHeldItems.add(new HoldItem(143, Type.STEEL)); //Metal Coat
        restrictedHeldItems.add(new HoldItem(117, Type.GRASS)); //Miracle Seed
        restrictedHeldItems.add(new HoldItem(95, Type.WATER)); //Mystic Water
        restrictedHeldItems.add(new HoldItem(107, Type.ICE)); //Nevermeltice
        restrictedHeldItems.add(new HoldItem(104, Type.NORMAL)); //Pink Bow
        restrictedHeldItems.add(new HoldItem(81, Type.POISON)); //Poison Barb
        restrictedHeldItems.add(new HoldItem(77, Type.FLYING)); //Sharp Beak
        restrictedHeldItems.add(new HoldItem(88, Type.BUG)); //Silver Powder
        restrictedHeldItems.add(new HoldItem(76, Type.GROUND)); //Soft Sand
        restrictedHeldItems.add(new HoldItem(113, Type.GHOST)); //Spell Tag
        restrictedHeldItems.add(new HoldItem(96, Type.PSYCHIC)); //Twisted Spoon
        
        restrictedHeldItems.add(new HoldItem(163, 25)); //Light Ball, Pikachu
        restrictedHeldItems.add(new HoldItem(30, 113)); //Lucky Punch, Chansey
        restrictedHeldItems.add(new HoldItem(35, 132)); //Metal Powder, Ditto
        restrictedHeldItems.add(new HoldItem(105, 83)); //Leek, Farfetchd
        restrictedHeldItems.add(new HoldItem(118, 104)); //Thick Club, Cubone
        restrictedHeldItems.add(new HoldItem(118, 105)); //Thick Club, Marowak
        
        for(HoldItem hi : restrictedHeldItems)
        {
            if(hi.restrictedType != null && (tpk.pokemon.primaryType == hi.restrictedType || tpk.pokemon.secondaryType == hi.restrictedType))
            {
                if((hi.restrictedpokemon != 0 && tpk.pokemon.number == hi.restrictedpokemon) || hi.restrictedpokemon == 0)
                {
                    competitiveHeldItems.add(hi);
                }
            }
            else if(hi.restrictedpokemon != 0 && tpk.pokemon.number == hi.restrictedpokemon)
            {
                competitiveHeldItems.add(hi);
            }
        }
        
        int item = competitiveHeldItems.get(this.random.nextInt(competitiveHeldItems.size())).itemIndex;
        return item;
    }
    
    @Override
    public void writeCheckValueToROM(int value) {
        if (romEntry.getValue("CheckValueOffset") > 0) {
            int cvOffset = romEntry.getValue("CheckValueOffset");
            for (int i = 0; i < 4; i++) {
                rom[cvOffset + i] = (byte) ((value >> (3 - i) * 8) & 0xFF);
            }
        }
    }
}
