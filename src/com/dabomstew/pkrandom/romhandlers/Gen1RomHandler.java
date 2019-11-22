package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  Gen1RomHandler.java - randomizer handler for R/B/Y.                   --*/
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;

import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.GFXFunctions;
import com.dabomstew.pkrandom.MiscTweak;
import com.dabomstew.pkrandom.Utils;
import com.dabomstew.pkrandom.constants.GBConstants;
import com.dabomstew.pkrandom.constants.Gen1Constants;
import com.dabomstew.pkrandom.constants.GlobalConstants;
import com.dabomstew.pkrandom.exceptions.RandomizationException;
import com.dabomstew.pkrandom.exceptions.RandomizerIOException;
import com.dabomstew.pkrandom.pokemon.Encounter;
import com.dabomstew.pkrandom.pokemon.EncounterSet;
import com.dabomstew.pkrandom.pokemon.Evolution;
import com.dabomstew.pkrandom.pokemon.EvolutionType;
import com.dabomstew.pkrandom.pokemon.ExpCurve;
import com.dabomstew.pkrandom.pokemon.FieldTM;
import com.dabomstew.pkrandom.pokemon.IngameTrade;
import com.dabomstew.pkrandom.pokemon.ItemList;
import com.dabomstew.pkrandom.pokemon.ItemLocation;
import com.dabomstew.pkrandom.pokemon.Move;
import com.dabomstew.pkrandom.pokemon.MoveLearnt;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.TrainerPokemon;
import com.dabomstew.pkrandom.pokemon.Type;

import compressors.Gen1Decmp;

public class Gen1RomHandler extends AbstractGBCRomHandler {

    public static class Factory extends RomHandler.Factory {

        @Override
        public Gen1RomHandler create(Random random, PrintStream logStream) {
            return new Gen1RomHandler(random, logStream);
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

    public Gen1RomHandler(Random random) {
        super(random, null);
    }

    public Gen1RomHandler(Random random, PrintStream logStream) {
        super(random, logStream);
    }

    // Important RBY Data Structures

    private int[] pokeNumToRBYTable;
    private int[] pokeRBYToNumTable;
    private int[] moveNumToRomTable;
    private int[] moveRomToNumTable;
    private int pokedexCount;

    private Type idToType(int value) {
        if (Gen1Constants.typeTable[value] != null) {
            return Gen1Constants.typeTable[value];
        }
        if (romEntry.extraTypeLookup.containsKey(value)) {
            return romEntry.extraTypeLookup.get(value);
        }
        return null;
    }

    private byte typeToByte(Type type) {
        if (type == null) {
            return 0x00; // revert to normal
        }
        if (romEntry.extraTypeReverse.containsKey(type)) {
            return romEntry.extraTypeReverse.get(type).byteValue();
        }
        return Gen1Constants.typeToByte(type);
    }

    private static class RomEntry {
        private String name;
        private String romName;
        private String hash;
        private int version, nonJapanese;
        private String extraTableFile;
        private int romType;
        private int crcInHeader = -1;
        private Map<String, String> tweakFiles = new HashMap<String, String>();
        private List<TMTextEntry> tmTexts = new ArrayList<TMTextEntry>();
        private Map<String, Integer> entries = new HashMap<String, Integer>();
        private Map<String, int[]> arrayEntries = new HashMap<String, int[]>();
        private List<Integer> staticPokemonSingle = new ArrayList<Integer>();
        private List<GameCornerPokemon> staticPokemonGameCorner = new ArrayList<GameCornerPokemon>();
        private int[] ghostMarowakOffsets = new int[0];
        private Map<Integer, Type> extraTypeLookup = new HashMap<Integer, Type>();
        private Map<Type, Integer> extraTypeReverse = new HashMap<Type, Integer>();

        private int getValue(String key) {
            if (!entries.containsKey(key)) {
                entries.put(key, 0);
            }
            return entries.get(key);
        }
    }

    private static List<RomEntry> roms;

    static {
        loadROMInfo();
    }

    private static class GameCornerPokemon {
        private int[] offsets;

        public String toString() {
            return Arrays.toString(offsets);
        }
    }

    private static class TMTextEntry {
        private int number;
        private int offset;
        private String template;
    }

    private static void loadROMInfo() {
        roms = new ArrayList<RomEntry>();
        RomEntry current = null;
        try {
            Scanner sc = new Scanner(FileFunctions.openConfig("gen1_offsets.ini"), "UTF-8");
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
                            if (r[1].startsWith("[") && r[1].endsWith("]")) {
                                String[] offsets = r[1].substring(1, r[1].length() - 1).split(",");
                                int[] offs = new int[offsets.length];
                                int c = 0;
                                for (String off : offsets) {
                                    offs[c++] = parseRIInt(off);
                                }
                                GameCornerPokemon gc = new GameCornerPokemon();
                                gc.offsets = offs;
                                current.staticPokemonGameCorner.add(gc);
                            } else {
                                int offs = parseRIInt(r[1]);
                                GameCornerPokemon gc = new GameCornerPokemon();
                                gc.offsets = new int[] { offs };
                                current.staticPokemonGameCorner.add(gc);
                            }
                        } else if (r[0].equals("StaticPokemonGhostMarowak")) {
                            if (r[1].startsWith("[") && r[1].endsWith("]")) {
                                String[] offsets = r[1].substring(1, r[1].length() - 1).split(",");
                                int[] offs = new int[offsets.length];
                                int c = 0;
                                for (String off : offsets) {
                                    offs[c++] = parseRIInt(off);
                                }
                                current.ghostMarowakOffsets = offs;
                            } else {
                            }
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
                            current.romName = r[1];
                        } else if (r[0].equals("Version")) {
                            current.version = parseRIInt(r[1]);
                        } else if (r[0].equals("NonJapanese")) {
                            current.nonJapanese = parseRIInt(r[1]);
                        } else if (r[0].equals("MD5Hash")) {
                            current.hash = r[1];
                        } else if (r[0].equals("Type")) {
                            if (r[1].equalsIgnoreCase("RB")) {
                                current.romType = Gen1Constants.RomType_RB;
                            } else if (r[1].equalsIgnoreCase("Yellow")) {
                                current.romType = Gen1Constants.RomType_Yellow;
                            } else if (r[1].equalsIgnoreCase("RBSpeedchoice")) {
                                current.romType = Gen1Constants.RomType_RBSpeedchoice;
                            } else {
                                System.err.println("unrecognised rom type: " + r[1]);
                            }
                        } else if (r[0].equals("ExtraTableFile")) {
                            current.extraTableFile = r[1];
                        } else if (r[0].equals("CRCInHeader")) {
                            current.crcInHeader = parseRIInt(r[1]);
                        } else if (r[0].endsWith("Tweak")) {
                            current.tweakFiles.put(r[0], r[1]);
                        } else if (r[0].equals("ExtraTypes")) {
                            // remove the containers
                            r[1] = r[1].substring(1, r[1].length() - 1);
                            String[] parts = r[1].split(",");
                            for (String part : parts) {
                                String[] iParts = part.split("=");
                                int typeId = Integer.parseInt(iParts[0], 16);
                                String typeName = iParts[1].trim();
                                Type theType = Type.valueOf(typeName);
                                current.extraTypeLookup.put(typeId, theType);
                                current.extraTypeReverse.put(theType, typeId);
                            }
                        } else if (r[0].equals("CopyFrom")) {
                            for (RomEntry otherEntry : roms) {
                                if (r[1].equalsIgnoreCase(otherEntry.name)) {
                                    // copy from here
                                    boolean cSP = (current.getValue("CopyStaticPokemon") == 1);
                                    boolean cTT = (current.getValue("CopyTMText") == 1);
                                    current.arrayEntries.putAll(otherEntry.arrayEntries);
                                    current.entries.putAll(otherEntry.entries);
                                    if (cSP) {
                                        current.staticPokemonSingle.addAll(otherEntry.staticPokemonSingle);
                                        current.staticPokemonGameCorner.addAll(otherEntry.staticPokemonGameCorner);
                                        current.ghostMarowakOffsets = otherEntry.ghostMarowakOffsets;
                                        current.entries.put("StaticPokemonSupport", 1);
                                    } else {
                                        current.entries.put("StaticPokemonSupport", 0);
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
                                    if (r[0].startsWith("StaticPokemon")) {
                                        for (int off : offs) {
                                            current.staticPokemonSingle.add(off);
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
    private String[] itemNames;
    private String[] mapNames;
    private SubMap[] maps;
    private boolean xAccNerfed;

    @Override
    public boolean detectRom(byte[] rom) {
        return detectRomInner(rom, rom.length);
    }

    public static boolean detectRomInner(byte[] rom, int romSize) {
        if (romSize < GBConstants.minRomSize || romSize > GBConstants.maxRomSize) {
            return false; // size check
        }
        return checkRomEntry(rom, romSize) != null; // so it's OK if it's a valid ROM
    }

    @Override
    public void loadedRom() {
        romEntry = checkRomEntry(this.rom, this.rom.length);
        pokeNumToRBYTable = new int[256];
        pokeRBYToNumTable = new int[256];
        moveNumToRomTable = new int[256];
        moveRomToNumTable = new int[256];
        maps = new SubMap[256];
        xAccNerfed = false;
        clearTextTables();
        readTextTable("gameboy_jap");
        if (romEntry.extraTableFile != null && romEntry.extraTableFile.equalsIgnoreCase("none") == false) {
            readTextTable(romEntry.extraTableFile);
        }
        loadPokedexOrder();
        loadPokemonStats();
        pokemonList = Arrays.asList(pokes);
        loadMoves();
        loadItemNames();
        preloadMaps();
        loadMapNames();
    }

    private void loadPokedexOrder() {
        int pkmnCount = romEntry.getValue("InternalPokemonCount");
        int orderOffset = romEntry.getValue("PokedexOrder");
        pokedexCount = 0;
        for (int i = 1; i <= pkmnCount; i++) {
            int pokedexNum = rom[orderOffset + i - 1] & 0xFF;
            pokeRBYToNumTable[i] = pokedexNum;
            if (pokedexNum != 0 && pokeNumToRBYTable[pokedexNum] == 0) {
                pokeNumToRBYTable[pokedexNum] = i;
            }
            pokedexCount = Math.max(pokedexCount, pokedexNum);
        }
    }

    private static RomEntry checkRomEntry(byte[] rom, int romLength) {
        int version = rom[GBConstants.versionOffset] & 0xFF;
        int nonjap = rom[GBConstants.jpFlagOffset] & 0xFF;
        // Check for specific CRC first
        int crcInHeader = ((rom[GBConstants.crcOffset] & 0xFF) << 8) | (rom[GBConstants.crcOffset + 1] & 0xFF);
        for (RomEntry re : roms) {
            if (romSig(rom, re.romName) && re.version == version && re.nonJapanese == nonjap
                    && re.crcInHeader == crcInHeader) {
                return re;
            }
        }
        // Now check for non-specific-CRC entries
        for (RomEntry re : roms) {
            if (romSig(rom, re.romName) && re.version == version && re.nonJapanese == nonjap && re.crcInHeader == -1) {
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

    private String[] readMoveNames() {
        int moveCount = romEntry.getValue("MoveCount");
        int offset = romEntry.getValue("MoveNamesOffset");
        String[] moveNames = new String[moveCount + 1];
        for (int i = 1; i <= moveCount; i++) {
            moveNames[i] = readVariableLengthString(offset, false);
            offset += lengthOfStringAt(offset, false) + 1;
        }
        return moveNames;
    }

    private void loadMoves() {
        String[] moveNames = readMoveNames();
        int moveCount = romEntry.getValue("MoveCount");
        int movesOffset = romEntry.getValue("MoveDataOffset");
        // check real move count
        int trueMoveCount = 0;
        for (int i = 1; i <= moveCount; i++) {
            // temp hack for Brown
            if (rom[movesOffset + (i - 1) * 6] != 0 && moveNames[i].equals("Nothing") == false) {
                trueMoveCount++;
            }
        }
        moves = new Move[trueMoveCount + 1];
        int trueMoveIndex = 0;

        for (int i = 1; i <= moveCount; i++) {
            int anim = rom[movesOffset + (i - 1) * 6] & 0xFF;
            // another temp hack for brown
            if (anim > 0 && moveNames[i].equals("Nothing") == false) {
                trueMoveIndex++;
                moveNumToRomTable[trueMoveIndex] = i;
                moveRomToNumTable[i] = trueMoveIndex;
                moves[trueMoveIndex] = new Move();
                moves[trueMoveIndex].name = moveNames[i];
                moves[trueMoveIndex].internalId = i;
                moves[trueMoveIndex].number = trueMoveIndex;
                moves[trueMoveIndex].effectIndex = rom[movesOffset + (i - 1) * 6 + 1] & 0xFF;
                moves[trueMoveIndex].hitratio = ((rom[movesOffset + (i - 1) * 6 + 4] & 0xFF) + 0) / 255.0 * 100;
                moves[trueMoveIndex].power = rom[movesOffset + (i - 1) * 6 + 2] & 0xFF;
                moves[trueMoveIndex].pp = rom[movesOffset + (i - 1) * 6 + 5] & 0xFF;
                moves[trueMoveIndex].type = idToType(rom[movesOffset + (i - 1) * 6 + 3] & 0xFF);

                if (GlobalConstants.normalMultihitMoves.contains(i)) {
                    moves[trueMoveIndex].hitCount = 3;
                } else if (GlobalConstants.doubleHitMoves.contains(i)) {
                    moves[trueMoveIndex].hitCount = 2;
                }
            }
        }

    }

    private void saveMoves() {
        int movesOffset = romEntry.getValue("MoveDataOffset");
        for (Move m : moves) {
            if (m != null) {
                int i = m.internalId;
                rom[movesOffset + (i - 1) * 6 + 1] = (byte) m.effectIndex;
                rom[movesOffset + (i - 1) * 6 + 2] = (byte) m.power;
                rom[movesOffset + (i - 1) * 6 + 3] = typeToByte(m.type);
                int hitratio = (int) Math.round(m.hitratio * 2.55);
                if (hitratio < 0) {
                    hitratio = 0;
                }
                if (hitratio > 255) {
                    hitratio = 255;
                }
                rom[movesOffset + (i - 1) * 6 + 4] = (byte) hitratio;
                rom[movesOffset + (i - 1) * 6 + 5] = (byte) m.pp;
            }
        }
    }

    public List<Move> getMoves() {
        return Arrays.asList(moves);
    }

    private void loadPokemonStats() {
        pokes = new Pokemon[pokedexCount + 1];
        // Fetch our names
        String[] pokeNames = readPokemonNames();
        // Get base stats
        int pokeStatsOffset = romEntry.getValue("PokemonStatsOffset");
        for (int i = 1; i <= pokedexCount; i++) {
            pokes[i] = new Pokemon();
            pokes[i].number = i;
            if (i != Gen1Constants.mewIndex || romEntry.getValue("MewStatsOffset") == 0) {
                loadBasicPokeStats(pokes[i], pokeStatsOffset + (i - 1) * Gen1Constants.baseStatsEntrySize);
            }
            // Name?
            pokes[i].name = pokeNames[pokeNumToRBYTable[i]];
        }

        // Mew override for R/B
        if (romEntry.getValue("MewStatsOffset") != 0) {
            loadBasicPokeStats(pokes[Gen1Constants.mewIndex], romEntry.getValue("MewStatsOffset"));
        }

        // Evolutions
        populateEvolutions();

    }

    private void savePokemonStats() {
        // Write pokemon names
        int offs = romEntry.getValue("PokemonNamesOffset");
        int nameLength = romEntry.getValue("PokemonNamesLength");
        for (int i = 1; i <= pokedexCount; i++) {
            int rbynum = pokeNumToRBYTable[i];
            int stringOffset = offs + (rbynum - 1) * nameLength;
            writeFixedLengthString(pokes[i].name, stringOffset, nameLength);
        }
        // Write pokemon stats
        int pokeStatsOffset = romEntry.getValue("PokemonStatsOffset");
        for (int i = 1; i <= pokedexCount; i++) {
            if (i == Gen1Constants.mewIndex) {
                continue;
            }
            saveBasicPokeStats(pokes[i], pokeStatsOffset + (i - 1) * Gen1Constants.baseStatsEntrySize);
        }
        // Write MEW
        int mewOffset = romEntry.getValue("MewStatsOffset") == 0 ? pokeStatsOffset + (Gen1Constants.mewIndex - 1)
                * Gen1Constants.baseStatsEntrySize : romEntry.getValue("MewStatsOffset");
        saveBasicPokeStats(pokes[Gen1Constants.mewIndex], mewOffset);

        // Write evolutions
        writeEvosAndMovesLearnt(true, null);
    }

    private void loadBasicPokeStats(Pokemon pkmn, int offset) {
        pkmn.hp = rom[offset + Gen1Constants.bsHPOffset] & 0xFF;
        pkmn.attack = rom[offset + Gen1Constants.bsAttackOffset] & 0xFF;
        pkmn.defense = rom[offset + Gen1Constants.bsDefenseOffset] & 0xFF;
        pkmn.speed = rom[offset + Gen1Constants.bsSpeedOffset] & 0xFF;
        pkmn.special = rom[offset + Gen1Constants.bsSpecialOffset] & 0xFF;
        pkmn.spatk = pkmn.special;
        pkmn.spdef = pkmn.special;
        // Type
        pkmn.primaryType = idToType(rom[offset + Gen1Constants.bsPrimaryTypeOffset] & 0xFF);
        pkmn.secondaryType = idToType(rom[offset + Gen1Constants.bsSecondaryTypeOffset] & 0xFF);
        // Only one type?
        if (pkmn.secondaryType == pkmn.primaryType) {
            pkmn.secondaryType = null;
        }

        pkmn.catchRate = rom[offset + Gen1Constants.bsCatchRateOffset] & 0xFF;
        pkmn.expYield = rom[offset + Gen1Constants.bsExpYieldOffset] & 0xFF;
        pkmn.growthCurve = ExpCurve.fromByte(rom[offset + Gen1Constants.bsGrowthCurveOffset]);
        pkmn.frontSpritePointer = readWord(offset + Gen1Constants.bsFrontSpriteOffset);
        pkmn.extra1 = rom[offset + Gen1Constants.bsExtra1Offset] & 0xFF;

        pkmn.guaranteedHeldItem = -1;
        pkmn.commonHeldItem = -1;
        pkmn.rareHeldItem = -1;
        pkmn.darkGrassHeldItem = -1;
    }

    private void saveBasicPokeStats(Pokemon pkmn, int offset) {
        rom[offset + Gen1Constants.bsHPOffset] = (byte) pkmn.hp;
        rom[offset + Gen1Constants.bsAttackOffset] = (byte) pkmn.attack;
        rom[offset + Gen1Constants.bsDefenseOffset] = (byte) pkmn.defense;
        rom[offset + Gen1Constants.bsSpeedOffset] = (byte) pkmn.speed;
        rom[offset + Gen1Constants.bsSpecialOffset] = (byte) pkmn.special;
        rom[offset + Gen1Constants.bsPrimaryTypeOffset] = typeToByte(pkmn.primaryType);
        if (pkmn.secondaryType == null) {
            rom[offset + Gen1Constants.bsSecondaryTypeOffset] = rom[offset + Gen1Constants.bsPrimaryTypeOffset];
        } else {
            rom[offset + Gen1Constants.bsSecondaryTypeOffset] = typeToByte(pkmn.secondaryType);
        }
        rom[offset + Gen1Constants.bsCatchRateOffset] = (byte) pkmn.catchRate;
        rom[offset + Gen1Constants.bsGrowthCurveOffset] = pkmn.growthCurve.toByte();
        rom[offset + Gen1Constants.bsExpYieldOffset] = (byte) pkmn.expYield;
    }

    private String[] readPokemonNames() {
        int offs = romEntry.getValue("PokemonNamesOffset");
        int nameLength = romEntry.getValue("PokemonNamesLength");
        int pkmnCount = romEntry.getValue("InternalPokemonCount");
        String[] names = new String[pkmnCount + 1];
        for (int i = 1; i <= pkmnCount; i++) {
            names[i] = readFixedLengthString(offs + (i - 1) * nameLength, nameLength);
        }
        return names;
    }

    @Override
    public List<Pokemon> getStarters() {
        // Get the starters
        List<Pokemon> starters = new ArrayList<Pokemon>();
        starters.add(pokes[pokeRBYToNumTable[rom[romEntry.arrayEntries.get("StarterOffsets1")[0]] & 0xFF]]);
        starters.add(pokes[pokeRBYToNumTable[rom[romEntry.arrayEntries.get("StarterOffsets2")[0]] & 0xFF]]);
        if (!isYellow()) {
            starters.add(pokes[pokeRBYToNumTable[rom[romEntry.arrayEntries.get("StarterOffsets3")[0]] & 0xFF]]);
        }
        return starters;
    }

    @Override
    public boolean setStarters(List<Pokemon> newStarters) {
        // Amount?
        int starterAmount = 2;
        if (!isYellow()) {
            starterAmount = 3;
        }

        // Basic checks
        if (newStarters.size() != starterAmount) {
            return false;
        }

        // Patch starter bytes
        for (int i = 0; i < starterAmount; i++) {
            byte starter = (byte) pokeNumToRBYTable[newStarters.get(i).number];
            int[] offsets = romEntry.arrayEntries.get("StarterOffsets" + (i + 1));
            for (int offset : offsets) {
                rom[offset] = starter;
            }
        }

        // Special stuff for non-Yellow only

        if (!isYellow()) {

            // Starter text
            if (romEntry.getValue("CanChangeStarterText") > 0) {
                int[] starterTextOffsets = romEntry.arrayEntries.get("StarterTextOffsets");
                for (int i = 0; i < 3 && i < starterTextOffsets.length; i++) {
                    writeVariableLengthString(String.format("So! You want\\n%s?\\e", newStarters.get(i).name),
                            starterTextOffsets[i], true);
                }
            }

            // Patch starter pokedex routine?
            // Can only do in 1M roms because of size concerns
            if (romEntry.getValue("PatchPokedex") > 0) {

                // Starter pokedex required RAM values
                // RAM offset => value
                // Allows for multiple starters in the same RAM byte
                Map<Integer, Integer> onValues = new TreeMap<Integer, Integer>();
                for (int i = 0; i < 3; i++) {
                    int pkDexNum = newStarters.get(i).number;
                    int ramOffset = (pkDexNum - 1) / 8 + romEntry.getValue("PokedexRamOffset");
                    int bitShift = (pkDexNum - 1) % 8;
                    int writeValue = 1 << bitShift;
                    if (onValues.containsKey(ramOffset)) {
                        onValues.put(ramOffset, onValues.get(ramOffset) | writeValue);
                    } else {
                        onValues.put(ramOffset, writeValue);
                    }
                }

                // Starter pokedex offset/pointer calculations

                int pkDexOnOffset = romEntry.getValue("StarterPokedexOnOffset");
                int pkDexOffOffset = romEntry.getValue("StarterPokedexOffOffset");

                int sizeForOnRoutine = 5 * onValues.size() + 3;
                int writeOnRoutineTo = romEntry.getValue("StarterPokedexBranchOffset");
                int writeOffRoutineTo = writeOnRoutineTo + sizeForOnRoutine;
                int offsetForOnRoutine = makeGBPointer(writeOnRoutineTo);
                int offsetForOffRoutine = makeGBPointer(writeOffRoutineTo);
                int retOnOffset = makeGBPointer(pkDexOnOffset + 5);
                int retOffOffset = makeGBPointer(pkDexOffOffset + 4);

                // Starter pokedex
                // Branch to our new routine(s)

                // Turn bytes on
                rom[pkDexOnOffset] = GBConstants.gbZ80Jump;
                writeWord(pkDexOnOffset + 1, offsetForOnRoutine);
                rom[pkDexOnOffset + 3] = GBConstants.gbZ80Nop;
                rom[pkDexOnOffset + 4] = GBConstants.gbZ80Nop;

                // Turn bytes off
                rom[pkDexOffOffset] = GBConstants.gbZ80Jump;
                writeWord(pkDexOffOffset + 1, offsetForOffRoutine);
                rom[pkDexOffOffset + 3] = GBConstants.gbZ80Nop;

                // Put together the two scripts
                rom[writeOffRoutineTo] = GBConstants.gbZ80XorA;
                int turnOnOffset = writeOnRoutineTo;
                int turnOffOffset = writeOffRoutineTo + 1;
                for (int ramOffset : onValues.keySet()) {
                    int onValue = onValues.get(ramOffset);
                    // Turn on code
                    rom[turnOnOffset++] = GBConstants.gbZ80LdA;
                    rom[turnOnOffset++] = (byte) onValue;
                    // Turn on code for ram writing
                    rom[turnOnOffset++] = GBConstants.gbZ80LdAToFar;
                    rom[turnOnOffset++] = (byte) (ramOffset % 0x100);
                    rom[turnOnOffset++] = (byte) (ramOffset / 0x100);
                    // Turn off code for ram writing
                    rom[turnOffOffset++] = GBConstants.gbZ80LdAToFar;
                    rom[turnOffOffset++] = (byte) (ramOffset % 0x100);
                    rom[turnOffOffset++] = (byte) (ramOffset / 0x100);
                }
                // Jump back
                rom[turnOnOffset++] = GBConstants.gbZ80Jump;
                writeWord(turnOnOffset, retOnOffset);

                rom[turnOffOffset++] = GBConstants.gbZ80Jump;
                writeWord(turnOffOffset, retOffOffset);
            }

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

    @Override
    public List<EncounterSet> getEncounters(boolean useTimeOfDay) {
        List<EncounterSet> encounters = new ArrayList<EncounterSet>();

        Pokemon ghostMarowak = pokes[Gen1Constants.marowakIndex];
        if (canChangeStaticPokemon()) {
            ghostMarowak = pokes[pokeRBYToNumTable[rom[romEntry.ghostMarowakOffsets[0]] & 0xFF]];
        }

        // grass & water
        List<Integer> usedOffsets = new ArrayList<Integer>();
        int tableOffset = romEntry.getValue("WildPokemonTableOffset");
        int tableBank = bankOf(tableOffset);
        int mapID = -1;

        while (readWord(tableOffset) != Gen1Constants.encounterTableEnd) {
            mapID++;
            int offset = calculateOffset(tableBank, readWord(tableOffset));
            int rootOffset = offset;
            if (!usedOffsets.contains(offset)) {
                usedOffsets.add(offset);
                // grass and water are exactly the same
                for (int a = 0; a < 2; a++) {
                    int rate = rom[offset++] & 0xFF;
                    if (rate > 0) {
                        // there is data here
                        EncounterSet thisSet = new EncounterSet();
                        thisSet.rate = rate;
                        thisSet.offset = rootOffset;
                        thisSet.displayName = (a == 1 ? "Surfing" : "Grass/Cave") + " on " + mapNames[mapID];
                        if (mapID >= Gen1Constants.towerMapsStartIndex && mapID <= Gen1Constants.towerMapsEndIndex) {
                            thisSet.bannedPokemon.add(ghostMarowak);
                        }
                        int size = Gen1Constants.encounterTableSize;
                        if(romEntry.romType == Gen1Constants.RomType_RBSpeedchoice && romEntry.getValue("VanillaEncounterSlots") == 0) {
                            size = Gen1Constants.encounterTableSizeSpdc;
                        }
                        for (int slot = 0; slot < size; slot++) {
                            Encounter enc = new Encounter();
                            enc.level = rom[offset] & 0xFF;
                            enc.pokemon = pokes[pokeRBYToNumTable[rom[offset + 1] & 0xFF]];
                            thisSet.encounters.add(enc);
                            offset += 2;
                        }
                        encounters.add(thisSet);
                    }
                }
            } else {
                for (EncounterSet es : encounters) {
                    if (es.offset == offset) {
                        es.displayName += ", " + mapNames[mapID];
                    }
                }
            }
            tableOffset += 2;
        }

        // old rod
        int oldRodOffset = romEntry.getValue("OldRodOffset");
        EncounterSet oldRodSet = new EncounterSet();
        oldRodSet.displayName = "Old Rod Fishing";
        Encounter oldRodEnc = new Encounter();
        oldRodEnc.level = rom[oldRodOffset + 2] & 0xFF;
        oldRodEnc.pokemon = pokes[pokeRBYToNumTable[rom[oldRodOffset + 1] & 0xFF]];
        oldRodSet.encounters.add(oldRodEnc);
        oldRodSet.bannedPokemon.add(ghostMarowak);
        encounters.add(oldRodSet);

        // good rod
        int goodRodOffset = romEntry.getValue("GoodRodOffset");
        EncounterSet goodRodSet = new EncounterSet();
        goodRodSet.displayName = "Good Rod Fishing";
        for (int grSlot = 0; grSlot < 2; grSlot++) {
            Encounter enc = new Encounter();
            enc.level = rom[goodRodOffset + grSlot * 2] & 0xFF;
            enc.pokemon = pokes[pokeRBYToNumTable[rom[goodRodOffset + grSlot * 2 + 1] & 0xFF]];
            goodRodSet.encounters.add(enc);
        }
        goodRodSet.bannedPokemon.add(ghostMarowak);
        encounters.add(goodRodSet);

        // super rod
        if (isYellow()) {
            int superRodOffset = romEntry.getValue("SuperRodTableOffset");
            while ((rom[superRodOffset] & 0xFF) != 0xFF) {
                int map = rom[superRodOffset++] & 0xFF;
                EncounterSet thisSet = new EncounterSet();
                thisSet.displayName = "Super Rod Fishing on " + mapNames[map];
                for (int encN = 0; encN < Gen1Constants.yellowSuperRodTableSize; encN++) {
                    Encounter enc = new Encounter();
                    enc.level = rom[superRodOffset + 1] & 0xFF;
                    enc.pokemon = pokes[pokeRBYToNumTable[rom[superRodOffset] & 0xFF]];
                    thisSet.encounters.add(enc);
                    superRodOffset += 2;
                }
                thisSet.bannedPokemon.add(ghostMarowak);
                encounters.add(thisSet);
            }
        } else {
            // red/blue
            int superRodOffset = romEntry.getValue("SuperRodTableOffset");
            int superRodBank = bankOf(superRodOffset);
            List<Integer> usedSROffsets = new ArrayList<Integer>();
            while ((rom[superRodOffset] & 0xFF) != 0xFF) {
                int map = rom[superRodOffset++] & 0xFF;
                int setOffset = calculateOffset(superRodBank, readWord(superRodOffset));
                superRodOffset += 2;
                if (!usedSROffsets.contains(setOffset)) {
                    usedSROffsets.add(setOffset);
                    EncounterSet thisSet = new EncounterSet();
                    thisSet.displayName = "Super Rod Fishing on " + mapNames[map];
                    thisSet.offset = setOffset;
                    int pokesInSet = rom[setOffset++] & 0xFF;
                    for (int encN = 0; encN < pokesInSet; encN++) {
                        Encounter enc = new Encounter();
                        enc.level = rom[setOffset] & 0xFF;
                        enc.pokemon = pokes[pokeRBYToNumTable[rom[setOffset + 1] & 0xFF]];
                        thisSet.encounters.add(enc);
                        setOffset += 2;
                    }
                    thisSet.bannedPokemon.add(ghostMarowak);
                    encounters.add(thisSet);
                } else {
                    for (EncounterSet es : encounters) {
                        if (es.offset == setOffset) {
                            es.displayName += ", " + mapNames[map];
                        }
                    }
                }
            }
        }

        return encounters;
    }

    @Override
    public void setEncounters(boolean useTimeOfDay, List<EncounterSet> encounters) {
        Iterator<EncounterSet> encsetit = encounters.iterator();

        // grass & water
        List<Integer> usedOffsets = new ArrayList<Integer>();
        int tableOffset = romEntry.getValue("WildPokemonTableOffset");
        int tableBank = bankOf(tableOffset);

        while (readWord(tableOffset) != Gen1Constants.encounterTableEnd) {
            int offset = calculateOffset(tableBank, readWord(tableOffset));
            if (!usedOffsets.contains(offset)) {
                usedOffsets.add(offset);
                // grass and water are exactly the same
                for (int a = 0; a < 2; a++) {
                    int rate = rom[offset++] & 0xFF;
                    if (rate > 0) {
                        // there is data here
                        EncounterSet thisSet = encsetit.next();
                        int size = Gen1Constants.encounterTableSize;
                        if(romEntry.romType == Gen1Constants.RomType_RBSpeedchoice && romEntry.getValue("VanillaEncounterSlots") == 0) {
                            size = Gen1Constants.encounterTableSizeSpdc;
                        }
                        for (int slot = 0; slot < size; slot++) {
                            Encounter enc = thisSet.encounters.get(slot);
                            rom[offset] = (byte) enc.level;
                            rom[offset + 1] = (byte) pokeNumToRBYTable[enc.pokemon.number];
                            offset += 2;
                        }
                    }
                }
            }
            tableOffset += 2;
        }

        // old rod
        int oldRodOffset = romEntry.getValue("OldRodOffset");
        EncounterSet oldRodSet = encsetit.next();
        Encounter oldRodEnc = oldRodSet.encounters.get(0);
        rom[oldRodOffset + 2] = (byte) oldRodEnc.level;
        rom[oldRodOffset + 1] = (byte) pokeNumToRBYTable[oldRodEnc.pokemon.number];

        // good rod
        int goodRodOffset = romEntry.getValue("GoodRodOffset");
        EncounterSet goodRodSet = encsetit.next();
        for (int grSlot = 0; grSlot < 2; grSlot++) {
            Encounter enc = goodRodSet.encounters.get(grSlot);
            rom[goodRodOffset + grSlot * 2] = (byte) enc.level;
            rom[goodRodOffset + grSlot * 2 + 1] = (byte) pokeNumToRBYTable[enc.pokemon.number];
        }

        // super rod
        if (isYellow()) {
            int superRodOffset = romEntry.getValue("SuperRodTableOffset");
            while ((rom[superRodOffset] & 0xFF) != 0xFF) {
                superRodOffset++;
                EncounterSet thisSet = encsetit.next();
                for (int encN = 0; encN < Gen1Constants.yellowSuperRodTableSize; encN++) {
                    Encounter enc = thisSet.encounters.get(encN);
                    rom[superRodOffset + 1] = (byte) enc.level;
                    rom[superRodOffset] = (byte) pokeNumToRBYTable[enc.pokemon.number];
                    superRodOffset += 2;
                }
            }
        } else {
            // red/blue
            int superRodOffset = romEntry.getValue("SuperRodTableOffset");
            int superRodBank = bankOf(superRodOffset);
            List<Integer> usedSROffsets = new ArrayList<Integer>();
            while ((rom[superRodOffset] & 0xFF) != 0xFF) {
                superRodOffset++;
                int setOffset = calculateOffset(superRodBank, readWord(superRodOffset));
                superRodOffset += 2;
                if (!usedSROffsets.contains(setOffset)) {
                    usedSROffsets.add(setOffset);
                    int pokesInSet = rom[setOffset++] & 0xFF;
                    EncounterSet thisSet = encsetit.next();
                    for (int encN = 0; encN < pokesInSet; encN++) {
                        Encounter enc = thisSet.encounters.get(encN);
                        rom[setOffset] = (byte) enc.level;
                        rom[setOffset + 1] = (byte) pokeNumToRBYTable[enc.pokemon.number];
                        setOffset += 2;
                    }
                }
            }
        }
    }

    @Override
    public List<Pokemon> getPokemon() {
        return pokemonList;
    }

    public List<Trainer> getTrainers() {
        int traineroffset = romEntry.getValue("TrainerDataTableOffset");
        int traineramount = Gen1Constants.trainerClassCount;
        int[] trainerclasslimits = romEntry.arrayEntries.get("TrainerDataClassCounts");

        int[] pointers = new int[traineramount + 1];
        for (int i = 1; i <= traineramount; i++) {
            int tPointer = readWord(traineroffset + (i - 1) * 2);
            pointers[i] = calculateOffset(bankOf(traineroffset), tPointer);
        }

        List<String> tcnames = getTrainerClassesForText();

        List<Trainer> allTrainers = new ArrayList<Trainer>();
        for (int i = 1; i <= traineramount; i++) {
            int offs = pointers[i];
            int limit = trainerclasslimits[i];
            String tcname = tcnames.get(i - 1);
            for (int trnum = 0; trnum < limit; trnum++) {
                Trainer tr = new Trainer();
                tr.offset = offs;
                tr.trainerclass = i;
                tr.fullDisplayName = tcname;
                int dataType = rom[offs] & 0xFF;
                if (dataType == 0xFF) {
                    // "Special" trainer
                    tr.poketype = 1;
                    offs++;
                    while (rom[offs] != 0x0) {
                        TrainerPokemon tpk = new TrainerPokemon();
                        tpk.level = rom[offs] & 0xFF;
                        tpk.pokemon = pokes[pokeRBYToNumTable[rom[offs + 1] & 0xFF]];
                        tr.pokemon.add(tpk);
                        offs += 2;
                    }
                } else {
                    tr.poketype = 0;
                    int fixedLevel = dataType;
                    offs++;
                    while (rom[offs] != 0x0) {
                        TrainerPokemon tpk = new TrainerPokemon();
                        tpk.level = fixedLevel;
                        tpk.pokemon = pokes[pokeRBYToNumTable[rom[offs] & 0xFF]];
                        tr.pokemon.add(tpk);
                        offs++;
                    }
                }
                offs++;
                allTrainers.add(tr);
            }
        }
        Gen1Constants.tagTrainersUniversal(allTrainers);
        if (isYellow()) {
            Gen1Constants.tagTrainersYellow(allTrainers);
        } else {
            Gen1Constants.tagTrainersRB(allTrainers);
        }
        return allTrainers;
    }

    public void setTrainers(List<Trainer> trainerData) {
        int traineroffset = romEntry.getValue("TrainerDataTableOffset");
        int traineramount = Gen1Constants.trainerClassCount;
        int[] trainerclasslimits = romEntry.arrayEntries.get("TrainerDataClassCounts");

        int[] pointers = new int[traineramount + 1];
        for (int i = 1; i <= traineramount; i++) {
            int tPointer = readWord(traineroffset + (i - 1) * 2);
            pointers[i] = calculateOffset(bankOf(traineroffset), tPointer);
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
                Iterator<TrainerPokemon> tPokes = tr.pokemon.iterator();
                // Write their pokemon based on poketype
                if (tr.poketype == 0) {
                    // Regular trainer
                    int fixedLevel = tr.pokemon.get(0).level;
                    rom[offs] = (byte) fixedLevel;
                    offs++;
                    while (tPokes.hasNext()) {
                        TrainerPokemon tpk = tPokes.next();
                        rom[offs] = (byte) pokeNumToRBYTable[tpk.pokemon.number];
                        offs++;
                    }
                } else {
                    // Special trainer
                    rom[offs] = (byte) 0xFF;
                    offs++;
                    while (tPokes.hasNext()) {
                        TrainerPokemon tpk = tPokes.next();
                        rom[offs] = (byte) tpk.level;
                        rom[offs + 1] = (byte) pokeNumToRBYTable[tpk.pokemon.number];
                        offs += 2;
                    }
                }
                rom[offs] = 0;
                offs++;
            }
        }

        // Custom Moves AI Table
        // Zero it out entirely.
        rom[romEntry.getValue("ExtraTrainerMovesTableOffset")] = (byte) 0xFF;

        // Champion Rival overrides in Red/Blue
        if (romEntry.romType == Gen1Constants.RomType_RB) {
            // hacky relative offset (very likely to work but maybe not always)
            int champRivalJump = romEntry.getValue("GymLeaderMovesTableOffset")
                    - Gen1Constants.champRivalOffsetFromGymLeaderMoves;
            // nop out this jump
            rom[champRivalJump] = GBConstants.gbZ80Nop;
            rom[champRivalJump + 1] = GBConstants.gbZ80Nop;
        }
        
        // Speedchoice mark trainers as changed to disable overrides
        if(romEntry.romType == Gen1Constants.RomType_RBSpeedchoice) {
            rom[romEntry.getValue("TrainersChanged")] = 0x01;
        }

    }

    @Override
    public boolean isYellow() {
        return romEntry.romType == Gen1Constants.RomType_Yellow;
    }

    @Override
    public boolean typeInGame(Type type) {
        if (type.isHackOnly == false && (type != Type.DARK && type != Type.STEEL)) {
            return true;
        }
        if (romEntry.extraTypeReverse.containsKey(type)) {
            return true;
        }
        return false;
    }

    private void fixTypeEffectiveness() {
        // TODO rewrite to use table properly
        int base = romEntry.getValue("TypeEffectivenessOffset");
        log("--Fixing Type Effectiveness--");
        // Change Poison SE to bug (should be neutral)
        // to Ice NE to Fire (is currently neutral)
        log("Replaced: Poison super effective vs Bug => Ice not very effective vs Fire");
        rom[base + 135] = typeToByte(Type.ICE);
        rom[base + 136] = typeToByte(Type.FIRE);
        rom[base + 137] = 5; // Not very effective
        // Change BUG SE to Poison to Bug NE to Poison
        log("Changed: Bug super effective vs Poison => Bug not very effective vs Poison");
        rom[base + 203] = 5; // Not very effective
        // Change Ghost 0E to Psychic to Ghost SE to Psychic
        log("Changed: Psychic immune to Ghost => Ghost super effective vs Psychic");
        rom[base + 227] = 20; // Super effective
        logBlankLine();
    }

    @Override
    public Map<Pokemon, List<MoveLearnt>> getMovesLearnt() {
        Map<Pokemon, List<MoveLearnt>> movesets = new TreeMap<Pokemon, List<MoveLearnt>>();
        int pointersOffset = romEntry.getValue("PokemonMovesetsTableOffset");
        int pokeStatsOffset = romEntry.getValue("PokemonStatsOffset");
        int pkmnCount = romEntry.getValue("InternalPokemonCount");
        for (int i = 1; i <= pkmnCount; i++) {
            int pointer = readWord(pointersOffset + (i - 1) * 2);
            int realPointer = calculateOffset(bankOf(pointersOffset), pointer);
            if (pokeRBYToNumTable[i] != 0) {
                Pokemon pkmn = pokes[pokeRBYToNumTable[i]];
                int statsOffset = 0;
                if (pokeRBYToNumTable[i] == Gen1Constants.mewIndex && romEntry.getValue("MewStatsOffset") != 0) {
                    // Mewww
                    statsOffset = romEntry.getValue("MewStatsOffset");
                } else {
                    statsOffset = (pokeRBYToNumTable[i] - 1) * 0x1C + pokeStatsOffset;
                }
                List<MoveLearnt> ourMoves = new ArrayList<MoveLearnt>();
                for (int delta = Gen1Constants.bsLevel1MovesOffset; delta < Gen1Constants.bsLevel1MovesOffset + 4; delta++) {
                    if (rom[statsOffset + delta] != 0x00) {
                        MoveLearnt learnt = new MoveLearnt();
                        learnt.level = 1;
                        learnt.move = moveRomToNumTable[rom[statsOffset + delta] & 0xFF];
                        ourMoves.add(learnt);
                    }
                }
                // Skip over evolution data
                while (rom[realPointer] != 0) {
                    if (rom[realPointer] == 1) {
                        realPointer += 3;
                    } else if (rom[realPointer] == 2) {
                        realPointer += 4;
                    } else if (rom[realPointer] == 3) {
                        realPointer += 3;
                    }
                }
                realPointer++;
                while (rom[realPointer] != 0) {
                    MoveLearnt learnt = new MoveLearnt();
                    learnt.level = rom[realPointer] & 0xFF;
                    learnt.move = moveRomToNumTable[rom[realPointer + 1] & 0xFF];
                    ourMoves.add(learnt);
                    realPointer += 2;
                }
                movesets.put(pkmn, ourMoves);
            }
        }
        return movesets;
    }

    @Override
    public void setMovesLearnt(Map<Pokemon, List<MoveLearnt>> movesets) {
        // new method for moves learnt
        writeEvosAndMovesLearnt(false, movesets);
    }

    @Override
    public List<Pokemon> getStaticPokemon() {
        List<Pokemon> statics = new ArrayList<Pokemon>();
        if (romEntry.getValue("StaticPokemonSupport") > 0) {
            for (int offset : romEntry.staticPokemonSingle) {
                statics.add(pokes[pokeRBYToNumTable[rom[offset] & 0xFF]]);
            }
            for (GameCornerPokemon gcp : romEntry.staticPokemonGameCorner) {
                statics.add(pokes[pokeRBYToNumTable[rom[gcp.offsets[0]] & 0xFF]]);
            }
            // Ghost Marowak
            statics.add(pokes[pokeRBYToNumTable[rom[romEntry.ghostMarowakOffsets[0]] & 0xFF]]);
        }
        return statics;
    }

    @Override
    public boolean setStaticPokemon(List<Pokemon> staticPokemon) {
        if (romEntry.getValue("StaticPokemonSupport") == 0) {
            return false;
        }
        // Checks
        int singleSize = romEntry.staticPokemonSingle.size();
        int gcSize = romEntry.staticPokemonGameCorner.size();
        if (staticPokemon.size() != singleSize + gcSize + 1) {
            return false;
        }

        // Singular entries
        for (int i = 0; i < singleSize; i++) {
            rom[romEntry.staticPokemonSingle.get(i)] = (byte) pokeNumToRBYTable[staticPokemon.get(i).number];
        }

        // Game corner
        for (int i = 0; i < gcSize; i++) {
            byte pokeNum = (byte) pokeNumToRBYTable[staticPokemon.get(i + singleSize).number];
            int[] offsets = romEntry.staticPokemonGameCorner.get(i).offsets;
            for (int offset : offsets) {
                rom[offset] = pokeNum;
            }
        }

        // Ghost Marowak
        byte maroNum = (byte) pokeNumToRBYTable[staticPokemon.get(singleSize + gcSize).number];
        for (int maroOffset : romEntry.ghostMarowakOffsets) {
            rom[maroOffset] = maroNum;
        }

        return true;
    }

    @Override
    public boolean canChangeStaticPokemon() {
        return (romEntry.getValue("StaticPokemonSupport") > 0);
    }

    @Override
    public List<Integer> getTMMoves() {
        List<Integer> tms = new ArrayList<Integer>();
        int offset = romEntry.getValue("TMMovesOffset");
        for (int i = 1; i <= Gen1Constants.tmCount; i++) {
            tms.add(moveRomToNumTable[rom[offset + (i - 1)] & 0xFF]);
        }
        return tms;
    }

    @Override
    public List<Integer> getHMMoves() {
        List<Integer> hms = new ArrayList<Integer>();
        int offset = romEntry.getValue("TMMovesOffset");
        for (int i = 1; i <= Gen1Constants.hmCount; i++) {
            hms.add(moveRomToNumTable[rom[offset + Gen1Constants.tmCount + (i - 1)] & 0xFF]);
        }
        return hms;
    }

    @Override
    public void setTMMoves(List<Integer> moveIndexes) {
        int offset = romEntry.getValue("TMMovesOffset");
        for (int i = 1; i <= Gen1Constants.tmCount; i++) {
            rom[offset + (i - 1)] = (byte) moveNumToRomTable[moveIndexes.get(i - 1)];
        }

        // Gym Leader TM Moves (RB only)
        if (!isYellow()) {
            int[] tms = Gen1Constants.gymLeaderTMs;
            int glMovesOffset = romEntry.getValue("GymLeaderMovesTableOffset");
            for (int i = 0; i < tms.length; i++) {
                // Set the special move used by gym (i+1) to
                // the move we just wrote to TM tms[i]
                rom[glMovesOffset + i * 2] = (byte) moveNumToRomTable[moveIndexes.get(tms[i] - 1)];
            }
        }

        // TM Text
        String[] moveNames = readMoveNames();
        for (TMTextEntry tte : romEntry.tmTexts) {
            String moveName = moveNames[moveNumToRomTable[moveIndexes.get(tte.number - 1)]];
            String text = tte.template.replace("%m", moveName);
            writeVariableLengthString(text, tte.offset, true);
        }
    }

    @Override
    public int getTMCount() {
        return Gen1Constants.tmCount;
    }

    @Override
    public int getHMCount() {
        return Gen1Constants.hmCount;
    }

    @Override
    public Map<Pokemon, boolean[]> getTMHMCompatibility() {
        Map<Pokemon, boolean[]> compat = new TreeMap<Pokemon, boolean[]>();
        int pokeStatsOffset = romEntry.getValue("PokemonStatsOffset");
        for (int i = 1; i <= pokedexCount; i++) {
            int baseStatsOffset = (romEntry.getValue("MewStatsOffset") == 0 || i != Gen1Constants.mewIndex) ? (pokeStatsOffset + (i - 1)
                    * Gen1Constants.baseStatsEntrySize) : romEntry.getValue("MewStatsOffset");
            Pokemon pkmn = pokes[i];
            boolean[] flags = new boolean[Gen1Constants.tmCount + Gen1Constants.hmCount + 1];
            for (int j = 0; j < 7; j++) {
                readByteIntoFlags(flags, j * 8 + 1, baseStatsOffset + Gen1Constants.bsTMHMCompatOffset + j);
            }
            compat.put(pkmn, flags);
        }
        return compat;
    }

    @Override
    public void setTMHMCompatibility(Map<Pokemon, boolean[]> compatData) {
        int pokeStatsOffset = romEntry.getValue("PokemonStatsOffset");
        for (Map.Entry<Pokemon, boolean[]> compatEntry : compatData.entrySet()) {
            Pokemon pkmn = compatEntry.getKey();
            boolean[] flags = compatEntry.getValue();
            int baseStatsOffset = (romEntry.getValue("MewStatsOffset") == 0 || pkmn.number != Gen1Constants.mewIndex) ? (pokeStatsOffset + (pkmn.number - 1)
                    * Gen1Constants.baseStatsEntrySize)
                    : romEntry.getValue("MewStatsOffset");
            for (int j = 0; j < 7; j++) {
                rom[baseStatsOffset + Gen1Constants.bsTMHMCompatOffset + j] = getByteFromFlags(flags, j * 8 + 1);
            }
        }
    }

    @Override
    public boolean hasMoveTutors() {
        return false;
    }

    @Override
    public List<Integer> getMoveTutorMoves() {
        return new ArrayList<Integer>();
    }

    @Override
    public void setMoveTutorMoves(List<Integer> moves) {
        // Do nothing
    }

    @Override
    public Map<Pokemon, boolean[]> getMoveTutorCompatibility() {
        return new TreeMap<Pokemon, boolean[]>();
    }

    @Override
    public void setMoveTutorCompatibility(Map<Pokemon, boolean[]> compatData) {
        // Do nothing
    }

    @Override
    public String getROMName() {
        return "Pokemon " + romEntry.name;
    }

    @Override
    public String getROMCode() {
        return romEntry.romName + " (" + romEntry.version + "/" + romEntry.nonJapanese + ")";
    }

    @Override
    public String getSupportLevel() {
        return (romEntry.getValue("StaticPokemonSupport") > 0) ? "Complete" : "No Static Pokemon";
    }

    private void populateEvolutions() {
        for (Pokemon pkmn : pokes) {
            if (pkmn != null) {
                pkmn.evolutionsFrom.clear();
                pkmn.evolutionsTo.clear();
            }
        }

        int pointersOffset = romEntry.getValue("PokemonMovesetsTableOffset");

        int pkmnCount = romEntry.getValue("InternalPokemonCount");
        for (int i = 1; i <= pkmnCount; i++) {
            int pointer = readWord(pointersOffset + (i - 1) * 2);
            int realPointer = calculateOffset(bankOf(pointersOffset), pointer);
            if (pokeRBYToNumTable[i] != 0) {
                int thisPoke = pokeRBYToNumTable[i];
                Pokemon pkmn = pokes[thisPoke];
                while (rom[realPointer] != 0) {
                    int method = rom[realPointer];
                    EvolutionType type = EvolutionType.fromIndex(1, method);
                    int otherPoke = pokeRBYToNumTable[rom[realPointer + 2 + (type == EvolutionType.STONE ? 1 : 0)] & 0xFF];
                    int extraInfo = rom[realPointer + 1] & 0xFF;
                    Evolution evo = new Evolution(pkmn, pokes[otherPoke], true, type, extraInfo);
                    if (!pkmn.evolutionsFrom.contains(evo)) {
                        pkmn.evolutionsFrom.add(evo);
                        if (pokes[otherPoke] != null) {
                            pokes[otherPoke].evolutionsTo.add(evo);
                        }
                    }
                    realPointer += (type == EvolutionType.STONE ? 4 : 3);
                }
                // split evos don't carry stats
                if (pkmn.evolutionsFrom.size() > 1) {
                    for (Evolution e : pkmn.evolutionsFrom) {
                        e.carryStats = false;
                    }
                }
            }
        }
    }

    @Override
    public void removeTradeEvolutions(boolean changeMoveEvos) {
        // Gen 1: only regular trade evos
        // change them all to evolve at level 37
        log("--Removing Trade Evolutions--");
        for (Pokemon pkmn : pokes) {
            if (pkmn != null) {
                for (Evolution evo : pkmn.evolutionsFrom) {
                    if (evo.type == EvolutionType.TRADE) {
                        // change
                        evo.type = EvolutionType.LEVEL;
                        evo.extraInfo = 37;
                        logEvoChangeLevel(evo.from.name, evo.to.name, 37);
                    }
                }
            }
        }
        logBlankLine();
    }

    private List<String> getTrainerClassesForText() {
        int[] offsets = romEntry.arrayEntries.get("TrainerClassNamesOffsets");
        List<String> tcNames = new ArrayList<String>();
        int offset = offsets[offsets.length - 1];
        for (int j = 0; j < Gen1Constants.tclassesCounts[1]; j++) {
            String name = readVariableLengthString(offset, false);
            offset += lengthOfStringAt(offset, false) + 1;
            tcNames.add(name);
        }
        return tcNames;
    }

    @Override
    public boolean canChangeTrainerText() {
        return romEntry.getValue("CanChangeTrainerText") > 0;
    }

    @Override
    public List<Integer> getDoublesTrainerClasses() {
        return Collections.emptyList();
    }

    @Override
    public List<String> getTrainerNames() {
        int[] offsets = romEntry.arrayEntries.get("TrainerClassNamesOffsets");
        List<String> trainerNames = new ArrayList<String>();
        int offset = offsets[offsets.length - 1];
        for (int j = 0; j < Gen1Constants.tclassesCounts[1]; j++) {
            String name = readVariableLengthString(offset, false);
            offset += lengthOfStringAt(offset, false) + 1;
            if (Gen1Constants.singularTrainers.contains(j)) {
                trainerNames.add(name);
            }
        }
        return trainerNames;
    }

    @Override
    public void setTrainerNames(List<String> trainerNames) {
        if (romEntry.getValue("CanChangeTrainerText") > 0) {
            int[] offsets = romEntry.arrayEntries.get("TrainerClassNamesOffsets");
            Iterator<String> trainerNamesI = trainerNames.iterator();
            int offset = offsets[offsets.length - 1];
            for (int j = 0; j < Gen1Constants.tclassesCounts[1]; j++) {
                int oldLength = lengthOfStringAt(offset, false) + 1;
                if (Gen1Constants.singularTrainers.contains(j)) {
                    String newName = trainerNamesI.next();
                    writeFixedLengthString(newName, offset, oldLength);
                }
                offset += oldLength;
            }
        }
    }

    @Override
    public TrainerNameMode trainerNameMode() {
        return TrainerNameMode.SAME_LENGTH;
    }

    @Override
    public List<Integer> getTCNameLengthsByTrainer() {
        // not needed
        return new ArrayList<Integer>();
    }

    @Override
    public List<String> getTrainerClassNames() {
        int[] offsets = romEntry.arrayEntries.get("TrainerClassNamesOffsets");
        List<String> trainerClassNames = new ArrayList<String>();
        if (offsets.length == 2) {
            for (int i = 0; i < offsets.length; i++) {
                int offset = offsets[i];
                for (int j = 0; j < Gen1Constants.tclassesCounts[i]; j++) {
                    String name = readVariableLengthString(offset, false);
                    offset += lengthOfStringAt(offset, false) + 1;
                    if (i == 0 || !Gen1Constants.singularTrainers.contains(j)) {
                        trainerClassNames.add(name);
                    }
                }
            }
        } else {
            int offset = offsets[0];
            for (int j = 0; j < Gen1Constants.tclassesCounts[1]; j++) {
                String name = readVariableLengthString(offset, false);
                offset += lengthOfStringAt(offset, false) + 1;
                if (!Gen1Constants.singularTrainers.contains(j)) {
                    trainerClassNames.add(name);
                }
            }
        }
        return trainerClassNames;
    }

    @Override
    public void setTrainerClassNames(List<String> trainerClassNames) {
        if (romEntry.getValue("CanChangeTrainerText") > 0) {
            int[] offsets = romEntry.arrayEntries.get("TrainerClassNamesOffsets");
            Iterator<String> tcNamesIter = trainerClassNames.iterator();
            if (offsets.length == 2) {
                for (int i = 0; i < offsets.length; i++) {
                    int offset = offsets[i];
                    for (int j = 0; j < Gen1Constants.tclassesCounts[i]; j++) {
                        int oldLength = lengthOfStringAt(offset, false) + 1;
                        if (i == 0 || !Gen1Constants.singularTrainers.contains(j)) {
                            String newName = tcNamesIter.next();
                            writeFixedLengthString(newName, offset, oldLength);
                        }
                        offset += oldLength;
                    }
                }
            } else {
                int offset = offsets[0];
                for (int j = 0; j < Gen1Constants.tclassesCounts[1]; j++) {
                    int oldLength = lengthOfStringAt(offset, false) + 1;
                    if (!Gen1Constants.singularTrainers.contains(j)) {
                        String newName = tcNamesIter.next();
                        writeFixedLengthString(newName, offset, oldLength);
                    }
                    offset += oldLength;
                }
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
    public int miscTweaksAvailable() {
        int available = MiscTweak.LOWER_CASE_POKEMON_NAMES.getValue();
        available |= MiscTweak.UPDATE_TYPE_EFFECTIVENESS.getValue();

        if (romEntry.tweakFiles.get("BWXPTweak") != null) {
            available |= MiscTweak.BW_EXP_PATCH.getValue();
        }
        if (romEntry.tweakFiles.get("XAccNerfTweak") != null) {
            available |= MiscTweak.NERF_X_ACCURACY.getValue();
        }
        if (romEntry.tweakFiles.get("CritRateTweak") != null) {
            available |= MiscTweak.FIX_CRIT_RATE.getValue();
        }
        if (romEntry.getValue("TextDelayFunctionOffset") != 0) {
            available |= MiscTweak.FASTEST_TEXT.getValue();
        }
        if (romEntry.getValue("PCPotionOffset") != 0) {
            available |= MiscTweak.RANDOMIZE_PC_POTION.getValue();
        }
        if (romEntry.getValue("PikachuEvoJumpOffset") != 0) {
            available |= MiscTweak.ALLOW_PIKACHU_EVOLUTION.getValue();
        }
        if (romEntry.getValue("CatchingTutorialMonOffset") != 0) {
            available |= MiscTweak.RANDOMIZE_CATCHING_TUTORIAL.getValue();
        }
        return available;
    }

    @Override
    public void applyMiscTweak(MiscTweak tweak) {
        if (tweak == MiscTweak.BW_EXP_PATCH) {
            applyBWEXPPatch();
        } else if (tweak == MiscTweak.NERF_X_ACCURACY) {
            applyXAccNerfPatch();
        } else if (tweak == MiscTweak.FIX_CRIT_RATE) {
            applyCritRatePatch();
        } else if (tweak == MiscTweak.FASTEST_TEXT) {
            applyFastestTextPatch();
        } else if (tweak == MiscTweak.RANDOMIZE_PC_POTION) {
            randomizePCPotion();
        } else if (tweak == MiscTweak.ALLOW_PIKACHU_EVOLUTION) {
            applyPikachuEvoPatch();
        } else if (tweak == MiscTweak.LOWER_CASE_POKEMON_NAMES) {
            applyCamelCaseNames();
        } else if (tweak == MiscTweak.UPDATE_TYPE_EFFECTIVENESS) {
            fixTypeEffectiveness();
        } else if (tweak == MiscTweak.RANDOMIZE_CATCHING_TUTORIAL) {
            randomizeCatchingTutorial();
        }
    }

    private void applyBWEXPPatch() {
        genericIPSPatch("BWXPTweak");
    }

    private void applyXAccNerfPatch() {
        xAccNerfed = genericIPSPatch("XAccNerfTweak");
    }

    private void applyCritRatePatch() {
        genericIPSPatch("CritRateTweak");
    }

    private void applyFastestTextPatch() {
        if (romEntry.getValue("TextDelayFunctionOffset") != 0) {
            rom[romEntry.getValue("TextDelayFunctionOffset")] = GBConstants.gbZ80Ret;
        }
    }

    private void randomizePCPotion() {
        if (romEntry.getValue("PCPotionOffset") != 0) {
            rom[romEntry.getValue("PCPotionOffset")] = (byte) this.getNonBadItems().randomNonTM(this.random);
        }
    }

    private void applyPikachuEvoPatch() {
        if (romEntry.getValue("PikachuEvoJumpOffset") != 0) {
            rom[romEntry.getValue("PikachuEvoJumpOffset")] = GBConstants.gbZ80JumpRelative;
        }
    }

    private void randomizeCatchingTutorial() {
        if (romEntry.getValue("CatchingTutorialMonOffset") != 0) {
            rom[romEntry.getValue("CatchingTutorialMonOffset")] = (byte) pokeNumToRBYTable[this.randomPokemon().number];
        }
    }

    private boolean genericIPSPatch(String ctName) {
        String patchName = romEntry.tweakFiles.get(ctName);
        if (patchName == null) {
            return false;
        }

        try {
            FileFunctions.applyPatch(rom, patchName);
            return true;
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    public List<Integer> getGameBreakingMoves() {
        // Sonicboom & drage & OHKO moves
        // 160 add spore
        // also remove OHKO if xacc nerfed
        if (xAccNerfed) {
            return Gen1Constants.bannedMovesWithXAccBanned;
        } else {
            return Gen1Constants.bannedMovesWithoutXAccBanned;
        }
    }

    @Override
    public List<Integer> getFieldMoves() {
        // cut, fly, surf, strength, flash,
        // dig, teleport (NOT softboiled)
        return Gen1Constants.fieldMoves;
    }

    @Override
    public List<Integer> getEarlyRequiredHMMoves() {
        // just cut
        return Gen1Constants.earlyRequiredHMs;
    }

    @Override
    public void applySignature() {
        // First off, intro Pokemon
        // 160 add yellow intro random
        int introPokemon = pokeNumToRBYTable[this.randomPokemon().number];
        rom[romEntry.getValue("IntroPokemonOffset")] = (byte) introPokemon;
        rom[romEntry.getValue("IntroCryOffset")] = (byte) introPokemon;

    }

    @Override
    public ItemList getAllowedItems() {
        return Gen1Constants.allowedItems;
    }

    @Override
    public ItemList getNonBadItems() {
        // Gen 1 has no bad items Kappa
        return Gen1Constants.allowedItems;
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
        // hms override
        for (int index = Gen1Constants.hmsStartIndex; index < Gen1Constants.tmsStartIndex; index++) {
            itemNames[index] = String.format("HM%02d", index - Gen1Constants.hmsStartIndex + 1);
        }
        // tms override
        for (int index = Gen1Constants.tmsStartIndex; index < 0x100; index++) {
            itemNames[index] = String.format("TM%02d", index - Gen1Constants.tmsStartIndex + 1);
        }
    }

    @Override
    public String[] getItemNames() {
        return itemNames;
    }

    private static class SubMap {
        private int id;
        private int addr;
        private int bank;
        private MapHeader header;
        private Connection[] cons;
        private int n_cons;
        private int obj_addr;
        private List<ItemLocationInner> itemOffsets;
    }

    private static class MapHeader {
        private int tileset_id; // u8
        private int map_h, map_w; // u8
        private int map_ptr, text_ptr, script_ptr; // u16
        private int connect_byte; // u8
        // 10 bytes
    }

    private static class Connection {
        private int index; // u8
        private int connected_map; // u16
        private int current_map; // u16
        private int bigness; // u8
        private int map_width; // u8
        private int y_align; // u8
        private int x_align; // u8
        private int window; // u16
        // 11 bytes
    }
    
    private class ItemLocationInner {
        private int map;
        private int x;
        private int y;
        private int offset;
        private boolean hidden;
        public ItemLocationInner(int map, int x, int y, int offset, boolean hidden) {
            super();
            this.map = map;
            this.x = x;
            this.y = y;
            this.offset = offset;
            this.hidden = hidden;
        }
        @Override
        public String toString() {
            return String.format("%s @ %d, %d (%s)", mapNames[map], x, y, hidden ? "hidden" : "visible");
        }
    }

    private void preloadMaps() {
        int mapBanks = romEntry.getValue("MapBanks");
        int mapAddresses = romEntry.getValue("MapAddresses");

        preloadMap(mapBanks, mapAddresses, 0);
    }

    private void preloadMap(int mapBanks, int mapAddresses, int mapID) {

        if (maps[mapID] != null || mapID == 0xED || mapID == 0xFF) {
            return;
        }

        SubMap map = new SubMap();
        maps[mapID] = map;

        map.id = mapID;
        map.addr = calculateOffset(rom[mapBanks + mapID] & 0xFF, readWord(mapAddresses + mapID * 2));
        map.bank = bankOf(map.addr);

        map.header = new MapHeader();
        map.header.tileset_id = rom[map.addr] & 0xFF;
        map.header.map_h = rom[map.addr + 1] & 0xFF;
        map.header.map_w = rom[map.addr + 2] & 0xFF;
        map.header.map_ptr = calculateOffset(map.bank, readWord(map.addr + 3));
        map.header.text_ptr = calculateOffset(map.bank, readWord(map.addr + 5));
        map.header.script_ptr = calculateOffset(map.bank, readWord(map.addr + 7));
        map.header.connect_byte = rom[map.addr + 9] & 0xFF;

        int cb = map.header.connect_byte;
        map.n_cons = ((cb & 8) >> 3) + ((cb & 4) >> 2) + ((cb & 2) >> 1) + (cb & 1);

        int cons_offset = map.addr + 10;

        map.cons = new Connection[map.n_cons];
        for (int i = 0; i < map.n_cons; i++) {
            int tcon_offs = cons_offset + i * 11;
            Connection con = new Connection();
            con.index = rom[tcon_offs] & 0xFF;
            con.connected_map = readWord(tcon_offs + 1);
            con.current_map = readWord(tcon_offs + 3);
            con.bigness = rom[tcon_offs + 5] & 0xFF;
            con.map_width = rom[tcon_offs + 6] & 0xFF;
            con.y_align = rom[tcon_offs + 7] & 0xFF;
            con.x_align = rom[tcon_offs + 8] & 0xFF;
            con.window = readWord(tcon_offs + 9);
            map.cons[i] = con;
            preloadMap(mapBanks, mapAddresses, con.index);
        }
        map.obj_addr = calculateOffset(map.bank, readWord(cons_offset + map.n_cons * 11));

        // Read objects
        // +0 is the border tile (ignore)
        // +1 is warp count

        int n_warps = rom[map.obj_addr + 1] & 0xFF;
        int offs = map.obj_addr + 2;
        for (int i = 0; i < n_warps; i++) {
            // track this warp
            int to_map = rom[offs + 3] & 0xFF;
            preloadMap(mapBanks, mapAddresses, to_map);
            offs += 4;
        }

        // Now we're pointing to sign count
        int n_signs = rom[offs++] & 0xFF;
        offs += n_signs * 3;

        // Finally, entities, which contain the items
        map.itemOffsets = new ArrayList<ItemLocationInner>();
        int n_entities = rom[offs++] & 0xFF;
        for (int i = 0; i < n_entities; i++) {
            // Read text ID
            int tid = rom[offs + 5] & 0xFF;
            if ((tid & (1 << 6)) > 0) {
                // trainer
                offs += 8;
            } else if ((tid & (1 << 7)) > 0 && (rom[offs + 6] != 0x00)) {
                // item
                map.itemOffsets.add(new ItemLocationInner(map.id, (rom[offs+2]&0xFF)-4, (rom[offs+1]&0xFF)-4, offs+6, false));
                offs += 7;
            } else {
                // generic
                offs += 6;
            }
        }
    }

    private void loadMapNames() {
        mapNames = new String[256];
        int mapNameTableOffset = romEntry.getValue("MapNameTableOffset");
        int mapNameBank = bankOf(mapNameTableOffset);
        // external names
        List<Integer> usedExternal = new ArrayList<Integer>();
        for (int i = 0; i < 0x25; i++) {
            int externalOffset = calculateOffset(mapNameBank, readWord(mapNameTableOffset + 1));
            usedExternal.add(externalOffset);
            mapNames[i] = readVariableLengthString(externalOffset, false);
            mapNameTableOffset += 3;
        }

        // internal names
        int lastMaxMap = 0x25;
        Map<Integer, Integer> previousMapCounts = new HashMap<Integer, Integer>();
        while ((rom[mapNameTableOffset] & 0xFF) != 0xFF) {
            int maxMap = rom[mapNameTableOffset] & 0xFF;
            int nameOffset = calculateOffset(mapNameBank, readWord(mapNameTableOffset + 2));
            String actualName = readVariableLengthString(nameOffset, false).trim();
            if (usedExternal.contains(nameOffset)) {
                for (int i = lastMaxMap; i < maxMap; i++) {
                    if (maps[i] != null) {
                        mapNames[i] = actualName + " (Building)";
                    }
                }
            } else {
                int mapCount = 0;
                if (previousMapCounts.containsKey(nameOffset)) {
                    mapCount = previousMapCounts.get(nameOffset);
                }
                for (int i = lastMaxMap; i < maxMap; i++) {
                    if (maps[i] != null) {
                        mapCount++;
                        mapNames[i] = actualName + " (" + mapCount + ")";
                    }
                }
                previousMapCounts.put(nameOffset, mapCount);
            }
            lastMaxMap = maxMap;
            mapNameTableOffset += 4;
        }
    }

    private List<ItemLocationInner> getItemOffsets() {

        List<ItemLocationInner> itemOffs = new ArrayList<ItemLocationInner>();

        for (int i = 0; i < maps.length; i++) {
            if (maps[i] != null) {
                itemOffs.addAll(maps[i].itemOffsets);
            }
        }

        int hiRoutine = romEntry.getValue("HiddenItemRoutine");
        int spclTable = romEntry.getValue("SpecialMapPointerTable");
        int spclBank = bankOf(spclTable);

        if (!isYellow()) {

            int spclList = romEntry.getValue("SpecialMapList");

            int lOffs = spclList;
            int idx = 0;

            while ((rom[lOffs] & 0xFF) != 0xFF) {

                int spclOffset = calculateOffset(spclBank, readWord(spclTable + idx));

                while ((rom[spclOffset] & 0xFF) != 0xFF) {
                    if (calculateOffset(rom[spclOffset + 3] & 0xFF, readWord(spclOffset + 4)) == hiRoutine) {
                        itemOffs.add(new ItemLocationInner(rom[lOffs]&0xFF, rom[spclOffset+1]&0xFF, rom[spclOffset]&0xFF, spclOffset + 2, true));
                    }
                    spclOffset += 6;
                }
                lOffs++;
                idx += 2;
            }
        } else {

            int lOffs = spclTable;

            while ((rom[lOffs] & 0xFF) != 0xFF) {

                int spclOffset = calculateOffset(spclBank, readWord(lOffs + 1));

                while ((rom[spclOffset] & 0xFF) != 0xFF) {
                    if (calculateOffset(rom[spclOffset + 3] & 0xFF, readWord(spclOffset + 4)) == hiRoutine) {
                        itemOffs.add(new ItemLocationInner(rom[lOffs]&0xFF, rom[spclOffset+1]&0xFF, rom[spclOffset]&0xFF, spclOffset + 2, true));
                    }
                    spclOffset += 6;
                }
                lOffs += 3;
            }
        }

        return itemOffs;
    }

    @Override
    public List<Integer> getRequiredFieldTMs() {
        return Gen1Constants.requiredFieldTMs;
    }

    @Override
    public List<FieldTM> getCurrentFieldTMs() {
        List<ItemLocationInner> itemOffsets = getItemOffsets();
        List<FieldTM> fieldTMs = new ArrayList<FieldTM>();

        for (ItemLocationInner il : itemOffsets) {
            int itemHere = rom[il.offset] & 0xFF;
            if (Gen1Constants.allowedItems.isTM(itemHere)) {
                fieldTMs.add(new FieldTM(il.toString(), itemHere - Gen1Constants.tmsStartIndex + 1)); // TM
                                                                          // offset
            }
        }
        return fieldTMs;
    }

    @Override
    public void setFieldTMs(List<Integer> fieldTMs) {
        List<ItemLocationInner> itemOffsets = getItemOffsets();
        Iterator<Integer> iterTMs = fieldTMs.iterator();

        for (ItemLocationInner il : itemOffsets) {
            int itemHere = rom[il.offset] & 0xFF;
            if (Gen1Constants.allowedItems.isTM(itemHere)) {
                // Replace this with a TM from the list
                rom[il.offset] = (byte) (iterTMs.next() + Gen1Constants.tmsStartIndex - 1);
            }
        }
    }

    @Override
    public List<ItemLocation> getRegularFieldItems() {
        List<ItemLocationInner> itemOffsets = getItemOffsets();
        List<ItemLocation> fieldItems = new ArrayList<ItemLocation>();

        for (ItemLocationInner il : itemOffsets) {
            int itemHere = rom[il.offset] & 0xFF;
            if (Gen1Constants.allowedItems.isAllowed(itemHere) && !(Gen1Constants.allowedItems.isTM(itemHere))) {
                fieldItems.add(new ItemLocation(il.toString(), itemHere));
            }
        }
        return fieldItems;
    }

    @Override
    public void setRegularFieldItems(List<Integer> items) {
        List<ItemLocationInner> itemOffsets = getItemOffsets();
        Iterator<Integer> iterItems = items.iterator();

        for (ItemLocationInner il : itemOffsets) {
            int itemHere = rom[il.offset] & 0xFF;
            if (Gen1Constants.allowedItems.isAllowed(itemHere) && !(Gen1Constants.allowedItems.isTM(itemHere))) {
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
        int[] unused = romEntry.arrayEntries.get("TradesUnused");
        int unusedOffset = 0;
        int entryLength = nicknameLength + 3;

        for (int entry = 0; entry < tableSize; entry++) {
            if (unusedOffset < unused.length && unused[unusedOffset] == entry) {
                unusedOffset++;
                continue;
            }
            IngameTrade trade = new IngameTrade();
            int entryOffset = tableOffset + entry * entryLength;
            trade.requestedPokemon = pokes[pokeRBYToNumTable[rom[entryOffset] & 0xFF]];
            trade.givenPokemon = pokes[pokeRBYToNumTable[rom[entryOffset + 1] & 0xFF]];
            trade.nickname = readString(entryOffset + 3, nicknameLength, false);
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
        int[] unused = romEntry.arrayEntries.get("TradesUnused");
        int unusedOffset = 0;
        int entryLength = nicknameLength + 3;
        int tradeOffset = 0;

        for (int entry = 0; entry < tableSize; entry++) {
            if (unusedOffset < unused.length && unused[unusedOffset] == entry) {
                unusedOffset++;
                continue;
            }
            IngameTrade trade = trades.get(tradeOffset++);
            int entryOffset = tableOffset + entry * entryLength;
            rom[entryOffset] = (byte) pokeNumToRBYTable[trade.requestedPokemon.number];
            rom[entryOffset + 1] = (byte) pokeNumToRBYTable[trade.givenPokemon.number];
            if (romEntry.getValue("CanChangeTrainerText") > 0) {
                writeFixedLengthString(trade.nickname, entryOffset + 3, nicknameLength);
            }
        }
    }

    @Override
    public boolean hasDVs() {
        return true;
    }

    @Override
    public int generationOfPokemon() {
        return 1;
    }

    @Override
    public void removeEvosForPokemonPool() {
        // gen1 doesn't have this functionality anyway
    }

    @Override
    public boolean supportsFourStartingMoves() {
        return true;
    }

    private void writeEvosAndMovesLearnt(boolean writeEvos, Map<Pokemon, List<MoveLearnt>> movesets) {
        // we assume a few things here:
        // 1) evos & moves learnt are stored directly after their pointer table
        // 2) PokemonMovesetsExtraSpaceOffset is in the same bank, and
        // points to the start of the free space at the end of the bank
        // (if set to 0, disabled from being used)
        // 3) PokemonMovesetsDataSize is from the start of actual data to
        // the start of engine/battle/e_2.asm in pokered (aka code we can't
        // overwrite)
        // it appears that in yellow, this code is moved
        // so we can write the evos/movesets in one continuous block
        // until the end of the bank.
        // so for yellow, extraspace is disabled.
        // specify null to either argument to copy old values
        int pokeStatsOffset = romEntry.getValue("PokemonStatsOffset");
        int movesEvosStart = romEntry.getValue("PokemonMovesetsTableOffset");
        int movesEvosBank = bankOf(movesEvosStart);
        int pkmnCount = romEntry.getValue("InternalPokemonCount");
        byte[] pointerTable = new byte[pkmnCount * 2];
        int mainDataBlockSize = romEntry.getValue("PokemonMovesetsDataSize");
        int mainDataBlockOffset = movesEvosStart + pointerTable.length;
        byte[] mainDataBlock = new byte[mainDataBlockSize];
        int offsetInMainData = 0;
        int extraSpaceOffset = romEntry.getValue("PokemonMovesetsExtraSpaceOffset");
        int extraSpaceBank = bankOf(extraSpaceOffset);
        boolean extraSpaceEnabled = false;
        byte[] extraDataBlock = null;
        int offsetInExtraData = 0;
        int extraSpaceSize = 0;
        if (movesEvosBank == extraSpaceBank && extraSpaceOffset != 0) {
            extraSpaceEnabled = true;
            int startOfNextBank = ((extraSpaceOffset / GBConstants.bankSize) + 1) * GBConstants.bankSize;
            extraSpaceSize = startOfNextBank - extraSpaceOffset;
            extraDataBlock = new byte[extraSpaceSize];
        }
        int nullEntryPointer = -1;

        for (int i = 1; i <= pkmnCount; i++) {
            byte[] writeData = null;
            int oldDataOffset = calculateOffset(movesEvosBank, readWord(movesEvosStart + (i - 1) * 2));
            boolean setNullEntryPointerHere = false;
            if (pokeRBYToNumTable[i] == 0) {
                // null entry
                if (nullEntryPointer == -1) {
                    // make the null entry
                    writeData = new byte[] { 0, 0 };
                    setNullEntryPointerHere = true;
                } else {
                    writeWord(pointerTable, (i - 1) * 2, nullEntryPointer);
                }
            } else {
                int pokeNum = pokeRBYToNumTable[i];
                Pokemon pkmn = pokes[pokeNum];
                ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
                // Evolutions
                if (!writeEvos) {
                    // copy old
                    int evoOffset = oldDataOffset;
                    while (rom[evoOffset] != 0x00) {
                        int method = rom[evoOffset] & 0xFF;
                        int limiter = (method == 2) ? 4 : 3;
                        for (int b = 0; b < limiter; b++) {
                            dataStream.write(rom[evoOffset++] & 0xFF);
                        }
                    }
                } else {
                    for (Evolution evo : pkmn.evolutionsFrom) {
                        // write evos for this poke
                        dataStream.write(evo.type.toIndex(1));
                        if (evo.type == EvolutionType.LEVEL) {
                            dataStream.write(evo.extraInfo); // min lvl
                        } else if (evo.type == EvolutionType.STONE) {
                            dataStream.write(evo.extraInfo); // stone item
                            dataStream.write(1); // minimum level
                        } else if (evo.type == EvolutionType.TRADE) {
                            dataStream.write(1); // minimum level
                        }
                        int pokeIndexTo = pokeNumToRBYTable[evo.to.number];
                        dataStream.write(pokeIndexTo); // species
                    }
                }
                // write terminator for evos
                dataStream.write(0);

                // Movesets
                if (movesets == null) {
                    // copy old
                    int movesOffset = oldDataOffset;
                    // move past evos
                    while (rom[movesOffset] != 0x00) {
                        int method = rom[movesOffset] & 0xFF;
                        movesOffset += (method == 2) ? 4 : 3;
                    }
                    movesOffset++;
                    // copy moves
                    while (rom[movesOffset] != 0x00) {
                        dataStream.write(rom[movesOffset++] & 0xFF);
                        dataStream.write(rom[movesOffset++] & 0xFF);
                    }
                } else {
                    List<MoveLearnt> ourMoves = movesets.get(pkmn);
                    int statsOffset = 0;
                    if (pokeNum == Gen1Constants.mewIndex && romEntry.getValue("MewStatsOffset") != 0) {
                        // Mewww
                        statsOffset = romEntry.getValue("MewStatsOffset");
                    } else {
                        statsOffset = (pokeNum - 1) * Gen1Constants.baseStatsEntrySize + pokeStatsOffset;
                    }
                    int movenum = 0;
                    while (movenum < 4 && ourMoves.size() > movenum && ourMoves.get(movenum).level == 1) {
                        rom[statsOffset + Gen1Constants.bsLevel1MovesOffset + movenum] = (byte) moveNumToRomTable[ourMoves
                                .get(movenum).move];
                        movenum++;
                    }
                    // Write out the rest of zeroes
                    for (int mn = movenum; mn < 4; mn++) {
                        rom[statsOffset + Gen1Constants.bsLevel1MovesOffset + mn] = 0;
                    }
                    // Add the non level 1 moves to the data stream
                    while (movenum < ourMoves.size()) {
                        dataStream.write(ourMoves.get(movenum).level);
                        dataStream.write(moveNumToRomTable[ourMoves.get(movenum).move]);
                        movenum++;
                    }
                }
                // terminator
                dataStream.write(0);

                // done, set writeData
                writeData = dataStream.toByteArray();
                try {
                    dataStream.close();
                } catch (IOException e) {
                }
            }

            // write data and set pointer?
            if (writeData != null) {
                int lengthToFit = writeData.length;
                int pointerToWrite = -1;
                // compression of leading & trailing 0s:
                // every entry ends in a 0 (end of move list).
                // if a block already has data in it, and the data
                // we want to write starts with a 0 (no evolutions)
                // we can compress it into the end of the last entry
                // this saves a decent amount of space overall.
                if ((offsetInMainData + lengthToFit <= mainDataBlockSize)
                        || (writeData[0] == 0 && offsetInMainData > 0 && offsetInMainData + lengthToFit == mainDataBlockSize + 1)) {
                    // place in main storage
                    if (writeData[0] == 0 && offsetInMainData > 0) {
                        int writtenDataOffset = mainDataBlockOffset + offsetInMainData - 1;
                        pointerToWrite = makeGBPointer(writtenDataOffset);
                        System.arraycopy(writeData, 1, mainDataBlock, offsetInMainData, lengthToFit - 1);
                        offsetInMainData += lengthToFit - 1;
                    } else {
                        int writtenDataOffset = mainDataBlockOffset + offsetInMainData;
                        pointerToWrite = makeGBPointer(writtenDataOffset);
                        System.arraycopy(writeData, 0, mainDataBlock, offsetInMainData, lengthToFit);
                        offsetInMainData += lengthToFit;
                    }
                } else if (extraSpaceEnabled
                        && ((offsetInExtraData + lengthToFit <= extraSpaceSize) || (writeData[0] == 0
                                && offsetInExtraData > 0 && offsetInExtraData + lengthToFit == extraSpaceSize + 1))) {
                    // place in extra space
                    if (writeData[0] == 0 && offsetInExtraData > 0) {
                        int writtenDataOffset = extraSpaceOffset + offsetInExtraData - 1;
                        pointerToWrite = makeGBPointer(writtenDataOffset);
                        System.arraycopy(writeData, 1, extraDataBlock, offsetInExtraData, lengthToFit - 1);
                        offsetInExtraData += lengthToFit - 1;
                    } else {
                        int writtenDataOffset = extraSpaceOffset + offsetInExtraData;
                        pointerToWrite = makeGBPointer(writtenDataOffset);
                        System.arraycopy(writeData, 0, extraDataBlock, offsetInExtraData, lengthToFit);
                        offsetInExtraData += lengthToFit;
                    }
                } else {
                    // this should never happen, but if not, uh oh
                    throw new RandomizationException("Unable to save moves/evolutions, out of space");
                }
                if (pointerToWrite >= 0) {
                    writeWord(pointerTable, (i - 1) * 2, pointerToWrite);
                    if (setNullEntryPointerHere) {
                        nullEntryPointer = pointerToWrite;
                    }
                }
            }
        }

        // Done, write final results to ROM
        System.arraycopy(pointerTable, 0, rom, movesEvosStart, pointerTable.length);
        System.arraycopy(mainDataBlock, 0, rom, mainDataBlockOffset, mainDataBlock.length);
        if (extraSpaceEnabled) {
            System.arraycopy(extraDataBlock, 0, rom, extraSpaceOffset, extraDataBlock.length);
        }
    }

    @Override
    public BufferedImage getMascotImage() {
        Pokemon mascot = randomPokemon();
        int idx = pokeNumToRBYTable[mascot.number];
        int fsBank;
        // define (by index number) the bank that a pokemon's image is in
        // using pokered code
        // if this is speedchoice, bank is stored in extra1 instead
        if(romEntry.romType == Gen1Constants.RomType_RBSpeedchoice) {
            fsBank = mascot.extra1;
        }
        else {
            if (mascot.number == 151 && !isYellow()) {
                // Mew
                fsBank = 1;
            } else if (idx < 0x1F) {
                fsBank = 0x9;
            } else if (idx < 0x4A) {
                fsBank = 0xA;
            } else if (idx < 0x74 || idx == 0x74 && mascot.frontSpritePointer > 0x7000) {
                fsBank = 0xB;
            } else if (idx < 0x99 || idx == 0x99 && mascot.frontSpritePointer > 0x7000) {
                fsBank = 0xC;
            } else {
                fsBank = 0xD;
            }
        }

        int fsOffset = calculateOffset(fsBank, mascot.frontSpritePointer);
        Gen1Decmp mscSprite = new Gen1Decmp(rom, fsOffset);
        mscSprite.decompress();
        mscSprite.transpose();
        int w = mscSprite.getWidth();
        int h = mscSprite.getHeight();

        // Palette?
        int[] palette;
        if (romEntry.getValue("MonPaletteIndicesOffset") > 0 && romEntry.getValue("SGBPalettesOffset") > 0) {
            int palIndex = rom[romEntry.getValue("MonPaletteIndicesOffset") + mascot.number] & 0xFF;
            int palOffset = romEntry.getValue("SGBPalettesOffset") + palIndex * 8;
            if (isYellow() && romEntry.nonJapanese == 1) {
                // Non-japanese Yellow can use GBC palettes instead.
                // Stored directly after regular SGB palettes.
                palOffset += 320;
            }
            palette = new int[4];
            for (int i = 0; i < 4; i++) {
                palette[i] = GFXFunctions.conv16BitColorToARGB(readWord(palOffset + i * 2));
            }
        } else {
            palette = new int[] { 0xFFFFFFFF, 0xFFAAAAAA, 0xFF666666, 0xFF000000 };
        }

        byte[] data = mscSprite.getFlattenedData();

        BufferedImage bim = GFXFunctions.drawTiledImage(data, palette, w, h, 8);
        GFXFunctions.pseudoTransparency(bim, palette[0]);

        return bim;
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
