package com.dabomstew.pkrandom.romhandlers;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import com.dabomstew.pkrandom.CustomNamesSet;
import com.dabomstew.pkrandom.MiscTweak;
import com.dabomstew.pkrandom.pokemon.EncounterSet;
import com.dabomstew.pkrandom.pokemon.Evolution;
import com.dabomstew.pkrandom.pokemon.GenRestrictions;
import com.dabomstew.pkrandom.pokemon.IngameTrade;
import com.dabomstew.pkrandom.pokemon.ItemList;
import com.dabomstew.pkrandom.pokemon.Move;
import com.dabomstew.pkrandom.pokemon.MoveLearnt;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.Type;

import freemarker.template.Template;

/**
 * Exists to assist with AssertJ Swing GUI Unit Testing
 * NOT FUNCTIONAL FOR RANDOMIZATION PURPOSES
 */
public class TestRomHandler implements RomHandler {

    @Override
    public boolean loadRom(String filename) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean saveRom(String filename) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String loadedFilename() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTemplate(Template template, Map templateData) {
        // TODO Auto-generated method stub

    }

    @Override
    public Map getTemplateData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Template getTemplate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Pokemon> getPokemon() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPokemonPool(GenRestrictions restrictions) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeEvosForPokemonPool() {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Pokemon> getStarters() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean setStarters(List<Pokemon> newStarters) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canChangeStarters() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void shufflePokemonStats(boolean evolutionSanity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void shuffleAllPokemonBSTs(boolean evolutionSanity, boolean randomVariance) {
        // TODO Auto-generated method stub

    }

    @Override
    public void randomizePokemonStatsWithinBST(boolean evolutionSanity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void randomizePokemonStatsUnrestricted(boolean evolutionSanity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void randomizeCompletelyPokemonStats(boolean evolutionSanity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updatePokemonStats() {
        // TODO Auto-generated method stub

    }

    @Override
    public Pokemon randomPokemon() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Pokemon randomNonLegendaryPokemon() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Pokemon randomLegendaryPokemon() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int evolutionChainSize(Pokemon pk) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Pokemon randomStarterPokemon(boolean noSplitEvos, boolean uniqueTypes, boolean baseOnly, int bstLimit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Pokemon random1or2EvosPokemon(boolean noSplitEvos, boolean uniqueTypes, boolean baseOnly, int bstLimit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Pokemon random2EvosPokemon(boolean noSplitEvos, boolean uniqueTypes, boolean baseOnly, int bstLimit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Type randomType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getTypeSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean typeInGame(Type type) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void randomizePokemonTypes(boolean evolutionSanity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void shufflePokemonTypes() {
        // TODO Auto-generated method stub

    }

    @Override
    public void randomizeRetainPokemonTypes(boolean evolutionSanity) {
        // TODO Auto-generated method stub

    }

    @Override
    public int abilitiesPerPokemon() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int highestAbilityIndex() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String abilityName(int number) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void randomizeAbilities(boolean evolutionSanity, boolean allowWonderGuard, boolean banTrappingAbilities,
            boolean banNegativeAbilities) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<EncounterSet> getEncounters(boolean useTimeOfDay) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setEncounters(boolean useTimeOfDay, List<EncounterSet> encounters) {
        // TODO Auto-generated method stub

    }

    @Override
    public void randomEncounters(boolean useTimeOfDay, boolean catchEmAll, boolean typeThemed, boolean usePowerLevels,
            boolean matchTypingDistribution, boolean noLegendaries, boolean allowLowLevelEvolvedTypes) {
        // TODO Auto-generated method stub

    }

    @Override
    public void area1to1Encounters(boolean useTimeOfDay, boolean catchEmAll, boolean typeThemed, boolean usePowerLevels,
            boolean matchTypingDistribution, boolean noLegendaries, boolean allowLowLevelEvolvedTypes) {
        // TODO Auto-generated method stub

    }

    @Override
    public void game1to1Encounters(boolean useTimeOfDay, boolean usePowerLevels, boolean noLegendaries) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean hasTimeBasedEncounters() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Pokemon> bannedForWildEncounters() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Trainer> getTrainers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTrainers(List<Trainer> trainerData) {
        // TODO Auto-generated method stub

    }

    @Override
    public void levelUpTrainerPokes(int levelModifier) {
        // TODO Auto-generated method stub

    }

    @Override
    public void randomizeTrainerPokes(boolean usePowerLevels, boolean noLegendaries, boolean noEarlyWonderGuard,
            int levelModifier) {
        // TODO Auto-generated method stub

    }

    @Override
    public void typeThemeTrainerPokes(boolean usePowerLevels, boolean weightByFrequency, boolean noLegendaries,
            boolean noEarlyWonderGuard, boolean useResistantType, int levelModifier) {
        // TODO Auto-generated method stub

    }

    @Override
    public void rivalCarriesStarter(boolean noLegendaries) {
        // TODO Auto-generated method stub

    }

    @Override
    public void rivalCarriesTeam() {
        // TODO Auto-generated method stub

    }

    @Override
    public void forceFullyEvolvedTrainerPokes(int minLevel) {
        // TODO Auto-generated method stub

    }

    @Override
    public void randomizeMovePowers() {
        // TODO Auto-generated method stub

    }

    @Override
    public void randomizeMovePPs() {
        // TODO Auto-generated method stub

    }

    @Override
    public void randomizeMoveAccuracies() {
        // TODO Auto-generated method stub

    }

    @Override
    public void randomizeMoveTypes() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean hasPhysicalSpecialSplit() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void randomizeMoveCategory() {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateMovesToGen5() {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateMovesToGen6() {
        // TODO Auto-generated method stub

    }

    @Override
    public void initMoveModernization() {
        // TODO Auto-generated method stub

    }

    @Override
    public void printMoveModernization() {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Move> getMoves() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<Pokemon, List<MoveLearnt>> getMovesLearnt() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setMovesLearnt(Map<Pokemon, List<MoveLearnt>> movesets) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Integer> getMovesBannedFromLevelup() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void randomizeMovesLearnt(boolean typeThemed, boolean noBroken, boolean forceStartingMoves,
            int forceStartingMoveCount, double goodDamagingProbability) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeBrokenMoves() {
        // TODO Auto-generated method stub

    }

    @Override
    public void orderDamagingMovesByDamage() {
        // TODO Auto-generated method stub

    }

    @Override
    public void metronomeOnlyMode() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean supportsFourStartingMoves() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Pokemon> getStaticPokemon() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean setStaticPokemon(List<Pokemon> staticPokemon) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void randomizeStaticPokemon(boolean legendForLegend) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean canChangeStaticPokemon() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Pokemon> bannedForStaticPokemon() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Integer> getTMMoves() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Integer> getHMMoves() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTMMoves(List<Integer> moveIndexes) {
        // TODO Auto-generated method stub

    }

    @Override
    public void randomizeTMMoves(boolean noBroken, boolean preserveField, double goodDamagingProbability) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getTMCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getHMCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Map<Pokemon, boolean[]> getTMHMCompatibility() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTMHMCompatibility(Map<Pokemon, boolean[]> compatData) {
        // TODO Auto-generated method stub

    }

    @Override
    public void randomizeTMHMCompatibility(boolean preferSameType) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fullTMHMCompatibility() {
        // TODO Auto-generated method stub

    }

    @Override
    public void ensureTMCompatSanity() {
        // TODO Auto-generated method stub

    }

    @Override
    public void fullHMCompatibility() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean hasMoveTutors() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Integer> getMoveTutorMoves() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setMoveTutorMoves(List<Integer> moves) {
        // TODO Auto-generated method stub

    }

    @Override
    public void randomizeMoveTutorMoves(boolean noBroken, boolean preserveField, double goodDamagingProbability) {
        // TODO Auto-generated method stub

    }

    @Override
    public Map<Pokemon, boolean[]> getMoveTutorCompatibility() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setMoveTutorCompatibility(Map<Pokemon, boolean[]> compatData) {
        // TODO Auto-generated method stub

    }

    @Override
    public void randomizeMoveTutorCompatibility(boolean preferSameType) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fullMoveTutorCompatibility() {
        // TODO Auto-generated method stub

    }

    @Override
    public void ensureMoveTutorCompatSanity() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean canChangeTrainerText() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<String> getTrainerNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTrainerNames(List<String> trainerNames) {
        // TODO Auto-generated method stub

    }

    @Override
    public TrainerNameMode trainerNameMode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int maxTrainerNameLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int maxSumOfTrainerNameLengths() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<Integer> getTCNameLengthsByTrainer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void randomizeTrainerNames(CustomNamesSet customNames) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<String> getTrainerClassNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTrainerClassNames(List<String> trainerClassNames) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean fixedTrainerClassNamesLength() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int maxTrainerClassNameLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void randomizeTrainerClassNames(CustomNamesSet customNames) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Integer> getDoublesTrainerClasses() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ItemList getAllowedItems() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ItemList getNonBadItems() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void randomizeWildHeldItems(boolean banBadItems) {
        // TODO Auto-generated method stub

    }

    @Override
    public String[] getItemNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Integer> getStarterHeldItems() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setStarterHeldItems(List<Integer> items) {
        // TODO Auto-generated method stub

    }

    @Override
    public void randomizeStarterHeldItems(boolean banBadItems) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Integer> getRequiredFieldTMs() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Integer> getCurrentFieldTMs() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setFieldTMs(List<Integer> fieldTMs) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Integer> getRegularFieldItems() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setRegularFieldItems(List<Integer> items) {
        // TODO Auto-generated method stub

    }

    @Override
    public void shuffleFieldItems() {
        // TODO Auto-generated method stub

    }

    @Override
    public void randomizeFieldItems(boolean banBadItems) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<IngameTrade> getIngameTrades() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setIngameTrades(List<IngameTrade> trades) {
        // TODO Auto-generated method stub

    }

    @Override
    public void randomizeIngameTrades(boolean randomizeRequest, boolean randomNickname, boolean randomOT,
            boolean randomStats, boolean randomItem, CustomNamesSet customNames) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean hasDVs() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int maxTradeNicknameLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int maxTradeOTNameLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void removeTradeEvolutions(boolean changeMoveEvos, boolean changeMethodEvos) {
        // TODO Auto-generated method stub

    }

    @Override
    public void condenseLevelEvolutions(int maxLevel, int maxIntermediateLevel) {
        // TODO Auto-generated method stub

    }

    @Override
    public void randomizeEvolutions(boolean similarStrength, boolean sameType, boolean changeMethods,
            boolean limitToThreeStages, boolean forceChange, boolean noConverge, boolean forceGrowth) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateExtraInfo(Evolution ev) {
        // TODO Auto-generated method stub

    }

    @Override
    public void minimumCatchRate(int rateNonLegendary, int rateLegendary) {
        // TODO Auto-generated method stub

    }

    @Override
    public void standardizeEXPCurves() {
        // TODO Auto-generated method stub

    }

    @Override
    public Map<String, Integer> getGameBreakingMoves() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Integer> getFieldMoves() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Integer> getEarlyRequiredHMMoves() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isYellow() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isGen1() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getROMName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getROMCode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getSupportLevel() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDefaultExtension() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int internalStringLength(String string) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void applySignature() {
        // TODO Auto-generated method stub

    }

    @Override
    public BufferedImage getMascotImage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isROMHack() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int generationOfPokemon() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeCheckValueToROM(int value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void generateTableOfContents() {
        // TODO Auto-generated method stub

    }

    @Override
    public void modifyTrainerText(Map taggedTypes) {
        // TODO Auto-generated method stub

    }

    @Override
    public int miscTweaksAvailable() {
        int available = 0;
        for (MiscTweak mt : MiscTweak.allTweaks) {
            available |= mt.getValue();
        }
        return available;
    }

    @Override
    public void applyMiscTweak(MiscTweak tweak) {
        // TODO Auto-generated method stub

    }
    
}
