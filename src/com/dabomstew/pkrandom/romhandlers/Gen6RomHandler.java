package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  Gen6RomHandler.java - randomizer handler for X/Y/OR/AS.               --*/
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
import com.dabomstew.pkrandom.constants.Gen6Constants;
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

public class Gen6RomHandler extends Abstract3DSRomHandler {

    public static class Factory extends RomHandler.Factory {

        @Override
        public Gen6RomHandler create(Random random, PrintStream logStream) {
            return new Gen6RomHandler(random, logStream);
        }

        public boolean isLoadable(String filename) {
            return detect3DSRomInner(getProductCodeFromFile(filename), getTitleIdFromFile(filename));
        }
    }

    public Gen6RomHandler(Random random) {
        super(random, null);
    }

    public Gen6RomHandler(Random random, PrintStream logStream) {
        super(random, logStream);
    }

    private static class RomEntry {
        private String name;
        private String romCode;
        private String titleId;
        private int romType;
        private Map<String, String> strings = new HashMap<>();
        private Map<String, Integer> numbers = new HashMap<>();

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
            Scanner sc = new Scanner(FileFunctions.openConfig("gen6_offsets.ini"), "UTF-8");
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
                            if (r[1].equalsIgnoreCase("ORAS")) {
                                current.romType = Gen6Constants.Type_ORAS;
                            } else {
                                current.romType = Gen6Constants.Type_XY;
                            }
                        } else if (r[0].equals("TitleId")) {
                            current.titleId = r[1];
                        } else if (r[0].endsWith("Offset") || r[0].endsWith("Count") || r[0].endsWith("Number")) {
                            int offs = parseRIInt(r[1]);
                            current.numbers.put(r[0], offs);
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
    private List<Pokemon> pokemonList;
    private Move[] moves;
    private RomEntry romEntry;
    private byte[] code;
    private List<String> abilityNames;
    private List<String> itemNames;

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
            stringsGarc = readGARC(romEntry.getString("TextStrings"),true);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

        // TODO: Actually make this work by loading it from the ROM. Only doing it this
        // way temporarily so the randomizer won't crash
//        pokes = new Pokemon[Gen6Constants.pokemonCount + 1];
//        for (int i = 1; i <= Gen6Constants.pokemonCount; i++) {
//            pokes[i] = new Pokemon();
//            pokes[i].number = i;
//        }
        loadPokemonStats();
        loadMoves();

        abilityNames = getStrings(false,romEntry.getInt("AbilityNamesTextOffset"));
        itemNames = getStrings(false,romEntry.getInt("ItemNamesTextOffset"));

        pokemonList = Arrays.asList(Arrays.copyOfRange(pokes,0,Gen6Constants.pokemonCount + 1));
    }

    private void loadPokemonStats() {
        try {
            pokeGarc = this.readGARC(romEntry.getString("PokemonStats"),true);
            String[] pokeNames = readPokemonNames();
            int formeCount = 0;
            pokes = new Pokemon[Gen6Constants.pokemonCount + formeCount + 1];
            for (int i = 1; i <= Gen6Constants.pokemonCount; i++) {
                pokes[i] = new Pokemon();
                pokes[i].number = i;
                loadBasicPokeStats(pokes[i],pokeGarc.files.get(i).get(0),formeMappings);
                pokes[i].name = pokeNames[i];
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void loadBasicPokeStats(Pokemon pkmn, byte[] stats, Map<Integer,FormeInfo> altFormes) {
        pkmn.hp = stats[Gen6Constants.bsHPOffset] & 0xFF;
        pkmn.attack = stats[Gen6Constants.bsAttackOffset] & 0xFF;
        pkmn.defense = stats[Gen6Constants.bsDefenseOffset] & 0xFF;
        pkmn.speed = stats[Gen6Constants.bsSpeedOffset] & 0xFF;
        pkmn.spatk = stats[Gen6Constants.bsSpAtkOffset] & 0xFF;
        pkmn.spdef = stats[Gen6Constants.bsSpDefOffset] & 0xFF;
        // Type
        pkmn.primaryType = Gen6Constants.typeTable[stats[Gen6Constants.bsPrimaryTypeOffset] & 0xFF];
        pkmn.secondaryType = Gen6Constants.typeTable[stats[Gen6Constants.bsSecondaryTypeOffset] & 0xFF];
        // Only one type?
        if (pkmn.secondaryType == pkmn.primaryType) {
            pkmn.secondaryType = null;
        }
        pkmn.catchRate = stats[Gen6Constants.bsCatchRateOffset] & 0xFF;
        pkmn.growthCurve = ExpCurve.fromByte(stats[Gen6Constants.bsGrowthCurveOffset]);

        pkmn.ability1 = stats[Gen6Constants.bsAbility1Offset] & 0xFF;
        pkmn.ability2 = stats[Gen6Constants.bsAbility2Offset] & 0xFF;
        pkmn.ability3 = stats[Gen6Constants.bsAbility3Offset] & 0xFF;

        // Held Items?
        int item1 = FileFunctions.read2ByteInt(stats, Gen6Constants.bsCommonHeldItemOffset);
        int item2 = FileFunctions.read2ByteInt(stats, Gen6Constants.bsRareHeldItemOffset);

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
            pkmn.darkGrassHeldItem = FileFunctions.read2ByteInt(stats, Gen6Constants.bsDarkGrassHeldItemOffset);
        }

//        int formeCount = stats[Gen6Constants.bsFormeCountOffset] & 0xFF;
//        if (formeCount > 1) {
//            int firstFormeOffset = FileFunctions.read2ByteInt(stats, Gen6Constants.bsFormeOffset);
//            if (firstFormeOffset != 0) {
//                for (int i = 1; i < formeCount; i++) {
//                    altFormes.put(firstFormeOffset + i - 1,new FormeInfo(pkmn.number,i,FileFunctions.read2ByteInt(stats,Gen6Constants.bsFormeSpriteOffset))); // Assumes that formes are in memory in the same order as their numbers
//                }
//            } else {
//                if (pkmn.number != 421 && pkmn.number != 493 && pkmn.number != 585 && pkmn.number != 586 && pkmn.number < 649) {
//                    // Reason for exclusions:
//                    // Cherrim/Arceus/Genesect: to avoid confusion
//                    // Deerling/Sawsbuck: handled automatically in gen 5
//                    pkmn.cosmeticForms = formeCount;
//                }
//            }
//        }
    }

    private void savePokemonStats() {
        for (int i = 1; i <= Gen6Constants.pokemonCount; i++) {
            saveBasicPokeStats(pokes[i], pokeGarc.files.get(i).get(0));
        }
        try {
            this.writeGARC(romEntry.getString("PokemonStats"),pokeGarc);
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
    }

    private void saveBasicPokeStats(Pokemon pkmn, byte[] stats) {
        stats[Gen6Constants.bsHPOffset] = (byte) pkmn.hp;
        stats[Gen6Constants.bsAttackOffset] = (byte) pkmn.attack;
        stats[Gen6Constants.bsDefenseOffset] = (byte) pkmn.defense;
        stats[Gen6Constants.bsSpeedOffset] = (byte) pkmn.speed;
        stats[Gen6Constants.bsSpAtkOffset] = (byte) pkmn.spatk;
        stats[Gen6Constants.bsSpDefOffset] = (byte) pkmn.spdef;
        stats[Gen6Constants.bsPrimaryTypeOffset] = Gen6Constants.typeToByte(pkmn.primaryType);
        if (pkmn.secondaryType == null) {
            stats[Gen6Constants.bsSecondaryTypeOffset] = stats[Gen6Constants.bsPrimaryTypeOffset];
        } else {
            stats[Gen6Constants.bsSecondaryTypeOffset] = Gen6Constants.typeToByte(pkmn.secondaryType);
        }
        stats[Gen6Constants.bsCatchRateOffset] = (byte) pkmn.catchRate;
        stats[Gen6Constants.bsGrowthCurveOffset] = pkmn.growthCurve.toByte();

        stats[Gen6Constants.bsAbility1Offset] = (byte) pkmn.ability1;
        stats[Gen6Constants.bsAbility2Offset] = (byte) pkmn.ability2;
        stats[Gen6Constants.bsAbility3Offset] = (byte) pkmn.ability3;

        // Held items
        if (pkmn.guaranteedHeldItem > 0) {
            FileFunctions.write2ByteInt(stats, Gen6Constants.bsCommonHeldItemOffset, pkmn.guaranteedHeldItem);
            FileFunctions.write2ByteInt(stats, Gen6Constants.bsRareHeldItemOffset, pkmn.guaranteedHeldItem);
            FileFunctions.write2ByteInt(stats, Gen6Constants.bsDarkGrassHeldItemOffset, 0);
        } else {
            FileFunctions.write2ByteInt(stats, Gen6Constants.bsCommonHeldItemOffset, pkmn.commonHeldItem);
            FileFunctions.write2ByteInt(stats, Gen6Constants.bsRareHeldItemOffset, pkmn.rareHeldItem);
            FileFunctions.write2ByteInt(stats, Gen6Constants.bsDarkGrassHeldItemOffset, pkmn.darkGrassHeldItem);
        }
    }

    private String[] readPokemonNames() {
        String[] pokeNames = new String[Gen6Constants.pokemonCount + 1];
        List<String> nameList = getStrings(false, romEntry.getInt("PokemonNamesTextOffset"));
        for (int i = 1; i <= Gen6Constants.pokemonCount; i++) {
            pokeNames[i] = nameList.get(i);
        }
        return pokeNames;
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

    private void loadMoves() {
        try {
            moveGarc = this.readGARC(romEntry.getString("MoveData"),true);
            int moveCount = Gen6Constants.getMoveCount(romEntry.romType);
            moves = new Move[moveCount + 1];
            List<String> moveNames = getStrings(false, romEntry.getInt("MoveNamesTextOffset"));
            for (int i = 1; i <= moveCount; i++) {
                byte[] moveData;
                if (romEntry.romType == Gen6Constants.Type_ORAS) {
                    moveData = Mini.UnpackMini(moveGarc.files.get(0).get(0), "WD")[i];
                } else {
                    moveData = moveGarc.files.get(i).get(0);
                }
                moves[i] = new Move();
                moves[i].name = moveNames.get(i);
                moves[i].number = i;
                moves[i].internalId = i;
                moves[i].hitratio = (moveData[4] & 0xFF);
                moves[i].power = moveData[3] & 0xFF;
                moves[i].pp = moveData[5] & 0xFF;
                moves[i].type = Gen6Constants.typeTable[moveData[0] & 0xFF];
                moves[i].category = Gen6Constants.moveCategoryIndices[moveData[2] & 0xFF];

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
    protected void savingROM() throws IOException {

        savePokemonStats();
    }

    @Override
    public List<Pokemon> getPokemon() {
        return pokemonList;
    }

    @Override
    public List<Pokemon> getPokemonInclFormes() {
        // TODO: Actually make this work by loading it from the ROM. Only doing it this
        // way temporarily so the randomizer won't crash when trying to write an output ROM.
        return pokemonList;
    }

    @Override
    public List<Pokemon> getAltFormes() {
        return new ArrayList<>();
    }

    @Override
    public List<Pokemon> getStarters() {
        // TODO: Actually make this work by loading it from the ROM. Only doing it this
        // way temporarily so the randomizer won't crash
        List<Pokemon> starters = new ArrayList<>();
        if (romEntry.romType == Gen6Constants.Type_XY) {
            starters.add(pokes[650]);
            starters.add(pokes[653]);
            starters.add(pokes[656]);
        } else {
            starters.add(pokes[252]);
            starters.add(pokes[255]);
            starters.add(pokes[258]);
        }
        return starters;
    }

    @Override
    public boolean setStarters(List<Pokemon> newStarters) {
        return false;
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
    public void setTrainers(List<Trainer> trainerData) {
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
    public List<Pokemon> getStaticPokemon() {
        return new ArrayList<>();
    }

    @Override
    public boolean setStaticPokemon(List<Pokemon> staticPokemon) {
        return false;
    }

    @Override
    public int miscTweaksAvailable() {
        return 0;
    }

    @Override
    public void applyMiscTweak(MiscTweak tweak) {
        // do nothing for now
    }

    @Override
    public List<Integer> getTMMoves() {
        String tmDataPrefix = Gen6Constants.tmDataPrefix;
        int offset = find(code, tmDataPrefix);
        if (offset != 0) {
            offset += Gen6Constants.tmDataPrefix.length() / 2; // because it was a prefix
            List<Integer> tms = new ArrayList<>();
            for (int i = 0; i < Gen6Constants.tmBlockOneCount; i++) {
                tms.add(readWord(code, offset + i * 2));
            }
            int blockTwoStartingOffset = Gen6Constants.getTMBlockTwoStartingOffset(romEntry.romType);
            for (int i = blockTwoStartingOffset; i < blockTwoStartingOffset + Gen6Constants.tmBlockTwoCount; i++) {
                tms.add(readWord(code, offset + i * 2));
            }
            return tms;
        } else {
            return null;
        }
    }

    @Override
    public List<Integer> getHMMoves() {
        String tmDataPrefix = Gen6Constants.tmDataPrefix;
        int offset = find(code, tmDataPrefix);
        if (offset != 0) {
            offset += Gen6Constants.tmDataPrefix.length() / 2; // because it was a prefix
            offset += Gen6Constants.tmBlockOneCount * 2; // TM data
            List<Integer> hms = new ArrayList<>();
            for (int i = 0; i < Gen6Constants.hmBlockOneCount; i++) {
                hms.add(readWord(code, offset + i * 2));
            }
            if (romEntry.romType == Gen6Constants.Type_ORAS) {
                hms.add(readWord(code, offset + Gen6Constants.rockSmashOffsetORAS));
                hms.add(readWord(code, offset + Gen6Constants.diveOffsetORAS));
            }
            return hms;
        } else {
            return null;
        }
    }

    @Override
    public void setTMMoves(List<Integer> moveIndexes) {
        // do nothing for now
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
        return Gen6Constants.tmCount;
    }

    @Override
    public int getHMCount() {
        return Gen6Constants.getHMCount(romEntry.romType);
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
        return Gen6Constants.getHighestAbilityIndex(romEntry.romType);
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
        return 6;
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
