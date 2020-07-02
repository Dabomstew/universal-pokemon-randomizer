package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  Gen7RomHandler.java - randomizer handler for Su/Mo/US/UM.             --*/
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

import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.MiscTweak;
import com.dabomstew.pkrandom.RomFunctions;
import com.dabomstew.pkrandom.constants.Gen7Constants;
import com.dabomstew.pkrandom.constants.GlobalConstants;
import com.dabomstew.pkrandom.ctr.GARCArchive;
import com.dabomstew.pkrandom.ctr.Mini;
import com.dabomstew.pkrandom.exceptions.RandomizerIOException;
import com.dabomstew.pkrandom.pokemon.*;
import pptxt.N3DSTxtHandler;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class Gen7RomHandler extends Abstract3DSRomHandler {

    public static class Factory extends RomHandler.Factory {

        @Override
        public Gen7RomHandler create(Random random, PrintStream logStream) {
            return new Gen7RomHandler(random, logStream);
        }

        public boolean isLoadable(String filename) {
            return detect3DSRomInner(getProductCodeFromFile(filename), getTitleIdFromFile(filename));
        }
    }

    public Gen7RomHandler(Random random) {
        super(random, null);
    }

    public Gen7RomHandler(Random random, PrintStream logStream) {
        super(random, logStream);
    }

    private static class OffsetWithinEntry {
        private int entry;
        private int offset;
    }

    private static class RomEntry {
        private String name;
        private String romCode;
        private String titleId;
        private String acronym;
        private int romType;
        private Map<String, String> strings = new HashMap<>();
        private Map<String, Integer> numbers = new HashMap<>();
        private Map<String, int[]> arrayEntries = new HashMap<>();
        private Map<String, OffsetWithinEntry[]> offsetArrayEntries = new HashMap<>();

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

    static {
        loadROMInfo();
    }

    private static void loadROMInfo() {
        roms = new ArrayList<>();
        RomEntry current = null;
        try {
            Scanner sc = new Scanner(FileFunctions.openConfig("gen7_offsets.ini"), "UTF-8");
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
                            if (r[1].equalsIgnoreCase("USUM")) {
                                current.romType = Gen7Constants.Type_USUM;
                            } else {
                                current.romType = Gen7Constants.Type_SM;
                            }
                        } else if (r[0].equals("TitleId")) {
                            current.titleId = r[1];
                        } else if (r[0].equals("Acronym")) {
                            current.acronym = r[1];
                        } else if (r[0].endsWith("Offset") || r[0].endsWith("Count") || r[0].endsWith("Number")) {
                            int offs = parseRIInt(r[1]);
                            current.numbers.put(r[0], offs);
                        } else if (r[0].equals("CopyFrom")) {
                            for (RomEntry otherEntry : roms) {
                                if (r[1].equalsIgnoreCase(otherEntry.romCode)) {
                                    // copy from here
                                    current.arrayEntries.putAll(otherEntry.arrayEntries);
                                    current.numbers.putAll(otherEntry.numbers);
                                    current.strings.putAll(otherEntry.strings);
                                    current.offsetArrayEntries.putAll(otherEntry.offsetArrayEntries);
                                }
                            }
                        } else {
                            current.strings.put(r[0],r[1]);
                        }
                    }
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
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

    // This ROM
    private Pokemon[] pokes;
    private Map<Integer,FormeInfo> formeMappings = new TreeMap<>();
    private Map<Integer,Map<Integer,Integer>> absolutePokeNumByBaseForme;
    private Map<Integer,Integer> dummyAbsolutePokeNums;
    private List<Pokemon> pokemonList;
    private List<Pokemon> pokemonListInclFormes;
    private List<MegaEvolution> megaEvolutions;
    private Move[] moves;
    private RomEntry romEntry;
    private byte[] code;
    private List<String> itemNames;
    private List<String> abilityNames;

    private GARCArchive pokeGarc, moveGarc, stringsGarc, storyTextGarc;

    @Override
    protected boolean detect3DSRom(String productCode, String titleId) {
        return detect3DSRomInner(productCode, titleId);
    }

    private static boolean detect3DSRomInner(String productCode, String titleId) {
        return entryFor(productCode, titleId) != null;
    }

    private static RomEntry entryFor(String productCode, String titleId) {
        if (productCode == null || titleId == null) {
            return null;
        }

        for (RomEntry re : roms) {
            if (productCode.equals(re.romCode) && titleId.equals(re.titleId)) {
                return re;
            }
        }
        return null;
    }

    @Override
    protected void loadedROM(String productCode, String titleId) {
        this.romEntry = entryFor(productCode, titleId);

        try {
            code = readCode();
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

        try {
            stringsGarc = readGARC(romEntry.getString("TextStrings"), true);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

        loadPokemonStats();
        loadMoves();

        pokemonListInclFormes = Arrays.asList(pokes);
        pokemonList = Arrays.asList(Arrays.copyOfRange(pokes,0,Gen7Constants.getPokemonCount(romEntry.romType) + 1));

        itemNames = getStrings(false,romEntry.getInt("ItemNamesTextOffset"));
        abilityNames = getStrings(false,romEntry.getInt("AbilityNamesTextOffset"));
    }

    private List<String> getStrings(boolean isStoryText, int index) {
        GARCArchive baseGARC = isStoryText ? storyTextGarc : stringsGarc;
        byte[] rawFile = baseGARC.files.get(index).get(0);
        return new ArrayList<>(N3DSTxtHandler.readTexts(rawFile,true,romEntry.romType));
    }

    private void setStrings(boolean isStoryText, int index, List<String> strings) {
        GARCArchive baseGARC = isStoryText ? storyTextGarc : stringsGarc;
        byte[] oldRawFile = baseGARC.files.get(index).get(0);
        try {
            byte[] newRawFile = N3DSTxtHandler.saveEntry(oldRawFile, strings, romEntry.romType);
            baseGARC.setFile(index, newRawFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPokemonStats() {
        try {
            pokeGarc = this.readGARC(romEntry.getString("PokemonStats"),true);
            String[] pokeNames = readPokemonNames();
            int pokemonCount = Gen7Constants.getPokemonCount(romEntry.romType);
            int formeCount = Gen7Constants.getFormeCount(romEntry.romType);
            pokes = new Pokemon[pokemonCount + formeCount + 1];
            for (int i = 1; i <= pokemonCount; i++) {
                pokes[i] = new Pokemon();
                pokes[i].number = i;
                loadBasicPokeStats(pokes[i],pokeGarc.files.get(i).get(0),formeMappings);
                pokes[i].name = pokeNames[i];
            }

            absolutePokeNumByBaseForme = new HashMap<>();
            dummyAbsolutePokeNums = new HashMap<>();
            dummyAbsolutePokeNums.put(0,0);

            int i = pokemonCount + 1;
            int formNum = 1;
            int prevSpecies = 0;
            Map<Integer,Integer> currentMap = new HashMap<>();
            for (int k: formeMappings.keySet()) {
                pokes[i] = new Pokemon();
                pokes[i].number = i;
                loadBasicPokeStats(pokes[i], pokeGarc.files.get(k).get(0),formeMappings);
                FormeInfo fi = formeMappings.get(k);
                int realBaseForme = pokes[fi.baseForme].baseForme == null ? fi.baseForme : pokes[fi.baseForme].baseForme.number;
                pokes[i].name = pokeNames[realBaseForme];
                pokes[i].baseForme = pokes[fi.baseForme];
                pokes[i].formeNumber = fi.formeNumber;
                pokes[i].formeSuffix = Gen7Constants.getFormeSuffixByBaseForme(fi.baseForme,fi.formeNumber);
                if (realBaseForme == prevSpecies) {
                    formNum++;
                    currentMap.put(formNum,i);
                } else {
                    if (prevSpecies != 0) {
                        absolutePokeNumByBaseForme.put(prevSpecies,currentMap);
                    }
                    prevSpecies = realBaseForme;
                    formNum = 1;
                    currentMap = new HashMap<>();
                    currentMap.put(formNum,i);
                }
                i++;
            }
            if (prevSpecies != 0) {
                absolutePokeNumByBaseForme.put(prevSpecies,currentMap);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
//        populateEvolutions();
//        populateMegaEvolutions();
    }

    private void loadBasicPokeStats(Pokemon pkmn, byte[] stats, Map<Integer,FormeInfo> altFormes) {
        pkmn.hp = stats[Gen7Constants.bsHPOffset] & 0xFF;
        pkmn.attack = stats[Gen7Constants.bsAttackOffset] & 0xFF;
        pkmn.defense = stats[Gen7Constants.bsDefenseOffset] & 0xFF;
        pkmn.speed = stats[Gen7Constants.bsSpeedOffset] & 0xFF;
        pkmn.spatk = stats[Gen7Constants.bsSpAtkOffset] & 0xFF;
        pkmn.spdef = stats[Gen7Constants.bsSpDefOffset] & 0xFF;
        // Type
        pkmn.primaryType = Gen7Constants.typeTable[stats[Gen7Constants.bsPrimaryTypeOffset] & 0xFF];
        pkmn.secondaryType = Gen7Constants.typeTable[stats[Gen7Constants.bsSecondaryTypeOffset] & 0xFF];
        // Only one type?
        if (pkmn.secondaryType == pkmn.primaryType) {
            pkmn.secondaryType = null;
        }
        pkmn.catchRate = stats[Gen7Constants.bsCatchRateOffset] & 0xFF;
        pkmn.growthCurve = ExpCurve.fromByte(stats[Gen7Constants.bsGrowthCurveOffset]);

        pkmn.ability1 = stats[Gen7Constants.bsAbility1Offset] & 0xFF;
        pkmn.ability2 = stats[Gen7Constants.bsAbility2Offset] & 0xFF;
        pkmn.ability3 = stats[Gen7Constants.bsAbility3Offset] & 0xFF;

        // Held Items?
        int item1 = FileFunctions.read2ByteInt(stats, Gen7Constants.bsCommonHeldItemOffset);
        int item2 = FileFunctions.read2ByteInt(stats, Gen7Constants.bsRareHeldItemOffset);

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
            pkmn.darkGrassHeldItem = FileFunctions.read2ByteInt(stats, Gen7Constants.bsDarkGrassHeldItemOffset);
        }

        int formeCount = stats[Gen7Constants.bsFormeCountOffset] & 0xFF;
        if (formeCount > 1) {
            if (!altFormes.keySet().contains(pkmn.number)) {
                int firstFormeOffset = FileFunctions.read2ByteInt(stats, Gen7Constants.bsFormeOffset);
                if (firstFormeOffset != 0) {
                    int j = 0;
                    int jMax = 0;
                    int theAltForme = 0;
                    Set<Integer> altFormesWithCosmeticForms = Gen7Constants.getAltFormesWithCosmeticForms(romEntry.romType).keySet();
                    for (int i = 1; i < formeCount; i++) {
                        if (j == 0 || j > jMax) {
                            altFormes.put(firstFormeOffset + i - 1,new FormeInfo(pkmn.number,i,FileFunctions.read2ByteInt(stats,Gen7Constants.bsFormeSpriteOffset))); // Assumes that formes are in memory in the same order as their numbers
                            if (Gen7Constants.getActuallyCosmeticForms(romEntry.romType).contains(firstFormeOffset+i-1)) {
                                if (!Gen7Constants.getIgnoreForms(romEntry.romType).contains(firstFormeOffset+i-1)) { // Skip ignored forms (identical or confusing cosmetic forms)
                                    pkmn.cosmeticForms += 1;
                                    pkmn.realCosmeticFormNumbers.add(i);
                                }
                            }
                        } else {
                            altFormes.put(firstFormeOffset + i - 1,new FormeInfo(theAltForme,j,FileFunctions.read2ByteInt(stats,Gen7Constants.bsFormeSpriteOffset)));
                            j++;
                        }
                        if (altFormesWithCosmeticForms.contains(firstFormeOffset + i - 1)) {
                            j = 1;
                            jMax = Gen7Constants.getAltFormesWithCosmeticForms(romEntry.romType).get(firstFormeOffset + i - 1);
                            theAltForme = firstFormeOffset + i - 1;
                        }
                    }
                } else {
                    if (pkmn.number != 493 && pkmn.number != 649 && pkmn.number != 716 && pkmn.number != 773) {
                        // Reason for exclusions:
                        // Arceus/Genesect/Silvally: to avoid confusion
                        // Xerneas: Should be handled automatically?
                        pkmn.cosmeticForms = formeCount;
                    }
                }
            } else {
                pkmn.cosmeticForms = Gen7Constants.getAltFormesWithCosmeticForms(romEntry.romType).getOrDefault(pkmn.number,0);
                if (Gen7Constants.getActuallyCosmeticForms(romEntry.romType).contains(pkmn.number)) {
                    pkmn.actuallyCosmetic = true;
                }
            }
        }
    }

    private String[] readPokemonNames() {
        int pokemonCount = Gen7Constants.getPokemonCount(romEntry.romType);
        String[] pokeNames = new String[pokemonCount + 1];
        List<String> nameList = getStrings(false, romEntry.getInt("PokemonNamesTextOffset"));
        for (int i = 1; i <= pokemonCount; i++) {
            pokeNames[i] = nameList.get(i);
        }
        return pokeNames;
    }

    private void loadMoves() {
        try {
            moveGarc = this.readGARC(romEntry.getString("MoveData"),true);
            int moveCount = Gen7Constants.getMoveCount(romEntry.romType);
            moves = new Move[moveCount + 1];
            List<String> moveNames = getStrings(false, romEntry.getInt("MoveNamesTextOffset"));
            byte[][] movesData = Mini.UnpackMini(moveGarc.files.get(0).get(0), "WD");
            for (int i = 1; i <= moveCount; i++) {
                byte[] moveData = movesData[i];
                moves[i] = new Move();
                moves[i].name = moveNames.get(i);
                moves[i].number = i;
                moves[i].internalId = i;
                moves[i].hitratio = (moveData[4] & 0xFF);
                moves[i].power = moveData[3] & 0xFF;
                moves[i].pp = moveData[5] & 0xFF;
                moves[i].type = Gen7Constants.typeTable[moveData[0] & 0xFF];
                moves[i].category = Gen7Constants.moveCategoryIndices[moveData[2] & 0xFF];

                if (GlobalConstants.normalMultihitMoves.contains(i)) {
                    moves[i].hitCount = 19 / 6.0;
                } else if (GlobalConstants.doubleHitMoves.contains(i)) {
                    moves[i].hitCount = 2;
                } else if (i == GlobalConstants.TRIPLE_KICK_INDEX) {
                    moves[i].hitCount = 2.71; // this assumes the first hit lands
                }
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    protected void savingROM() {
        saveMoves();
        try {
            writeCode(code);
            writeGARC(romEntry.getString("TextStrings"), stringsGarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void saveMoves() {
        int moveCount = Gen7Constants.getMoveCount(romEntry.romType);
        byte[][] movesData = Mini.UnpackMini(moveGarc.files.get(0).get(0), "WD");
        for (int i = 1; i <= moveCount; i++) {
            byte[] moveData = movesData[i];
            moveData[2] = Gen7Constants.moveCategoryToByte(moves[i].category);
            moveData[3] = (byte) moves[i].power;
            moveData[0] = Gen7Constants.typeToByte(moves[i].type);
            int hitratio = (int) Math.round(moves[i].hitratio);
            if (hitratio < 0) {
                hitratio = 0;
            }
            if (hitratio > 101) {
                hitratio = 100;
            }
            moveData[4] = (byte) hitratio;
            moveData[5] = (byte) moves[i].pp;
        }
        try {
            moveGarc.setFile(0, Mini.PackMini(movesData, "WD"));
            this.writeGARC(romEntry.getString("MoveData"), moveGarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    @Override
    protected String getGameAcronym() {
        return romEntry.acronym;
    }

    @Override
    public List<Pokemon> getPokemon() {
        return pokemonList;
    }

    @Override
    public List<Pokemon> getPokemonInclFormes() {
        return pokemonListInclFormes;
    }

    @Override
    public List<Pokemon> getAltFormes() {
        return new ArrayList<>();
    }

    @Override
    public List<MegaEvolution> getMegaEvolutions() {
        return new ArrayList<>(); // To be implemented
    }

    @Override
    public Pokemon getAltFormeOfPokemon(Pokemon pk, int forme) {
        return pk;
    }

    @Override
    public List<Pokemon> getStarters() {
        // TODO: Actually make this work by loading it from the ROM. Only doing it this
        // way temporarily so the randomizer won't crash
        List<Pokemon> starters = new ArrayList<>();
        starters.add(pokes[722]);
        starters.add(pokes[725]);
        starters.add(pokes[728]);
        return starters;
    }

    @Override
    public boolean setStarters(List<Pokemon> newStarters) {
        return false;
    }

    @Override
    public boolean hasStarterAltFormes() {
        return true;
    }

    @Override
    public int starterCount() {
        return 3;
    }

    @Override
    public List<Integer> getStarterHeldItems() {
        return new ArrayList<>();
    }

    @Override
    public void setStarterHeldItems(List<Integer> items) {
        // do nothing for now
    }

    @Override
    public List<Move> getMoves() {
        return Arrays.asList(moves);
    }

    @Override
    public List<EncounterSet> getEncounters(boolean useTimeOfDay) {
        return new ArrayList<>();
    }

    @Override
    public void setEncounters(boolean useTimeOfDay, List<EncounterSet> encountersList) {
        // do nothing for now
    }

    @Override
    public List<Trainer> getTrainers() {
        return new ArrayList<>();
    }

    @Override
    public List<Integer> getMainPlaythroughTrainers() {
        return new ArrayList<>();
    }

    @Override
    public List<Integer> getEvolutionItems() {
        return new ArrayList<>();
    }

    @Override
    public void setTrainers(List<Trainer> trainerData, boolean doubleBattleMode) {
        // do nothing for now
    }

    @Override
    public Map<Integer, List<MoveLearnt>> getMovesLearnt() {
        return new TreeMap<>();
    }

    @Override
    public void setMovesLearnt(Map<Integer, List<MoveLearnt>> movesets) {
        // do nothing for now
    }

    @Override
    public boolean canChangeStaticPokemon() {
        return false;
    }

    @Override
    public boolean hasStaticAltFormes() {
        return true;
    }

    @Override
    public List<StaticEncounter> getStaticPokemon() {
        return new ArrayList<>();
    }

    @Override
    public boolean setStaticPokemon(List<StaticEncounter> staticPokemon) {
        return false;
    }

    @Override
    public int miscTweaksAvailable() {
        int available = 0;
        available |= MiscTweak.FASTEST_TEXT.getValue();
        return available;
    }

    @Override
    public void applyMiscTweak(MiscTweak tweak) {
        if (tweak == MiscTweak.FASTEST_TEXT) {
            applyFastestText();
        }
    }

    private void applyFastestText() {
        int offset = find(code, Gen7Constants.fastestTextPrefixes[0]);
        if (offset > 0) {
            offset += Gen7Constants.fastestTextPrefixes[0].length() / 2; // because it was a prefix
            code[offset] = 0x03;
            code[offset + 1] = 0x40;
            code[offset + 2] = (byte) 0xA0;
            code[offset + 3] = (byte) 0xE3;
        }
        offset = find(code, Gen7Constants.fastestTextPrefixes[1]);
        if (offset > 0) {
            offset += Gen7Constants.fastestTextPrefixes[1].length() / 2; // because it was a prefix
            code[offset] = 0x03;
            code[offset + 1] = 0x50;
            code[offset + 2] = (byte) 0xA0;
            code[offset + 3] = (byte) 0xE3;
        }
    }

    @Override
    public List<Integer> getTMMoves() {
        String tmDataPrefix = Gen7Constants.getTmDataPrefix(romEntry.romType);
        int offset = find(code, tmDataPrefix);
        if (offset != 0) {
            offset += tmDataPrefix.length() / 2; // because it was a prefix
            List<Integer> tms = new ArrayList<>();
            for (int i = 0; i < Gen7Constants.tmCount; i++) {
                tms.add(readWord(code, offset + i * 2));
            }
            return tms;
        } else {
            return null;
        }
    }

    @Override
    public List<Integer> getHMMoves() {
        // Gen 7 does not have any HMs
        return new ArrayList<>();
    }

    @Override
    public void setTMMoves(List<Integer> moveIndexes) {
        String tmDataPrefix = Gen7Constants.getTmDataPrefix(romEntry.romType);
        int offset = find(code, tmDataPrefix);
        if (offset > 0) {
            offset += tmDataPrefix.length() / 2; // because it was a prefix
            for (int i = 0; i < Gen7Constants.tmCount; i++) {
                writeWord(code, offset + i * 2, moveIndexes.get(i));
            }

            // Update TM item descriptions
            List<String> itemDescriptions = getStrings(false, romEntry.getInt("ItemDescriptionsTextOffset"));
            List<String> moveDescriptions = getStrings(false, romEntry.getInt("MoveDescriptionsTextOffset"));
            // TM01 is item 328 and so on
            for (int i = 0; i < Gen7Constants.tmBlockOneCount; i++) {
                itemDescriptions.set(i + Gen7Constants.tmBlockOneOffset, moveDescriptions.get(moveIndexes.get(i)));
            }
            // TM93-95 are 618-620
            for (int i = 0; i < Gen7Constants.tmBlockTwoCount; i++) {
                itemDescriptions.set(i + Gen7Constants.tmBlockTwoOffset,
                        moveDescriptions.get(moveIndexes.get(i + Gen7Constants.tmBlockOneCount)));
            }
            // TM96-100 are 690 and so on
            for (int i = 0; i < Gen7Constants.tmBlockThreeCount; i++) {
                itemDescriptions.set(i + Gen7Constants.tmBlockThreeOffset,
                        moveDescriptions.get(moveIndexes.get(i + Gen7Constants.tmBlockOneCount + Gen7Constants.tmBlockTwoCount)));
            }
            // Save the new item descriptions
            setStrings(false, romEntry.getInt("ItemDescriptionsTextOffset"), itemDescriptions);
            // Palettes
            String palettePrefix = Gen7Constants.itemPalettesPrefix;
            int offsPals = find(code, palettePrefix);
            if (offsPals > 0) {
                offsPals += Gen7Constants.itemPalettesPrefix.length() / 2; // because it was a prefix
                // Write pals
                for (int i = 0; i < Gen7Constants.tmBlockOneCount; i++) {
                    int itmNum = Gen7Constants.tmBlockOneOffset + i;
                    Move m = this.moves[moveIndexes.get(i)];
                    int pal = this.typeTMPaletteNumber(m.type, true);
                    writeWord(code, offsPals + itmNum * 4, pal);
                }
                for (int i = 0; i < (Gen7Constants.tmBlockTwoCount); i++) {
                    int itmNum = Gen7Constants.tmBlockTwoOffset + i;
                    Move m = this.moves[moveIndexes.get(i + Gen7Constants.tmBlockOneCount)];
                    int pal = this.typeTMPaletteNumber(m.type, true);
                    writeWord(code, offsPals + itmNum * 4, pal);
                }
                for (int i = 0; i < (Gen7Constants.tmBlockThreeCount); i++) {
                    int itmNum = Gen7Constants.tmBlockThreeOffset + i;
                    Move m = this.moves[moveIndexes.get(i + Gen7Constants.tmBlockOneCount + Gen7Constants.tmBlockTwoCount)];
                    int pal = this.typeTMPaletteNumber(m.type, true);
                    writeWord(code, offsPals + itmNum * 4, pal);
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
            searchFor[i] = (byte) Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16);
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

    @Override
    public int getTMCount() {
        return Gen7Constants.tmCount;
    }

    @Override
    public int getHMCount() {
        // Gen 7 does not have any HMs
        return 0;
    }

    @Override
    public Map<Pokemon, boolean[]> getTMHMCompatibility() {
        return new TreeMap<>();
    }

    @Override
    public void setTMHMCompatibility(Map<Pokemon, boolean[]> compatData) {
        // do nothing for now
    }

    @Override
    public boolean hasMoveTutors() {
        return false;
    }

    @Override
    public List<Integer> getMoveTutorMoves() {
        return new ArrayList<>();
    }

    @Override
    public void setMoveTutorMoves(List<Integer> moves) {
        // do nothing for now
    }

    @Override
    public Map<Pokemon, boolean[]> getMoveTutorCompatibility() {
        return new TreeMap<>();
    }

    @Override
    public void setMoveTutorCompatibility(Map<Pokemon, boolean[]> compatData) {
        // do nothing for now
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
        return "None";
    }

    @Override
    public boolean hasTimeBasedEncounters() {
        return false;
    }

    @Override
    public boolean hasWildAltFormes() {
        return true;
    }

    @Override
    public void removeTradeEvolutions(boolean changeMoveEvos) {
        // do nothing for now
    }

    @Override
    public void removePartyEvolutions() {
        // do nothing for now
    }

    @Override
    public boolean hasShopRandomization() {
        return false;
    }

    @Override
    public boolean canChangeTrainerText() {
        return false;
    }

    @Override
    public List<String> getTrainerNames() {
        return new ArrayList<>();
    }

    @Override
    public int maxTrainerNameLength() {
        return 0;
    }

    @Override
    public void setTrainerNames(List<String> trainerNames) {
        // do nothing for now
    }

    @Override
    public TrainerNameMode trainerNameMode() {
        return TrainerNameMode.MAX_LENGTH;
    }

    @Override
    public List<Integer> getTCNameLengthsByTrainer() {
        return new ArrayList<>();
    }

    @Override
    public List<String> getTrainerClassNames() {
        return new ArrayList<>();
    }

    @Override
    public void setTrainerClassNames(List<String> trainerClassNames) {
        // do nothing for now
    }

    @Override
    public int maxTrainerClassNameLength() {
        return 0;
    }

    @Override
    public boolean fixedTrainerClassNamesLength() {
        return false;
    }

    @Override
    public List<Integer> getDoublesTrainerClasses() {
        return new ArrayList<>();
    }

    @Override
    public String getDefaultExtension() {
        return "cxi";
    }

    @Override
    public int abilitiesPerPokemon() {
        return 3;
    }

    @Override
    public int highestAbilityIndex() {
        return Gen7Constants.getHighestAbilityIndex(romEntry.romType);
    }

    @Override
    public int internalStringLength(String string) {
        return 0;
    }

    @Override
    public void applySignature() {
        // For now, do nothing.
    }

    @Override
    public ItemList getAllowedItems() {
        return null;
    }

    @Override
    public ItemList getNonBadItems() {
        return null;
    }

    @Override
    public List<Integer> getRegularShopItems() {
        return null;
    }

    @Override
    public List<Integer> getOPShopItems() {
        return null;
    }

    @Override
    public String[] getItemNames() {
        return itemNames.toArray(new String[0]);
    }

    @Override
    public String[] getShopNames() {
        return new String[0];
    }

    @Override
    public String abilityName(int number) {
        return abilityNames.get(number);
    }

    @Override
    public boolean hasMegaEvolutions() {
        return true;
    }

    @Override
    public List<Integer> getCurrentFieldTMs() {
        return new ArrayList<>();
    }

    @Override
    public void setFieldTMs(List<Integer> fieldTMs) {
        // do nothing for now
    }

    @Override
    public List<Integer> getRegularFieldItems() {
        return new ArrayList<>();
    }

    @Override
    public void setRegularFieldItems(List<Integer> items) {
        // do nothing for now
    }

    @Override
    public List<Integer> getRequiredFieldTMs() {
        return new ArrayList<>();
    }

    @Override
    public List<IngameTrade> getIngameTrades() {
        return new ArrayList<>();
    }

    @Override
    public void setIngameTrades(List<IngameTrade> trades) {
        // do nothing for now
    }

    @Override
    public boolean hasDVs() {
        return false;
    }

    @Override
    public int generationOfPokemon() {
        return 7;
    }

    @Override
    public void removeEvosForPokemonPool() {
        // do nothing for now
    }

    @Override
    public boolean supportsFourStartingMoves() {
        return false;
    }

    @Override
    public List<Integer> getFieldMoves() {
        return new ArrayList<>();
    }

    @Override
    public List<Integer> getEarlyRequiredHMMoves() {
        return new ArrayList<>();
    }

    @Override
    public Map<Integer, List<Integer>> getShopItems() {
        return new TreeMap<>();
    }

    @Override
    public void setShopItems(Map<Integer, List<Integer>> shopItems) {
        // do nothing for now
    }

    @Override
    public void setShopPrices() {
        // do nothing for now
    }

    @Override
    public List<Integer> getMainGameShops() {
        return new ArrayList<>();
    }

    @Override
    public BufferedImage getMascotImage() {
        return null;
    }
}
