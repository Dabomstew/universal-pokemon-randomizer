package com.dabomstew.pkrandom.romhandlers;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.dabomstew.pkrandom.MiscTweak;
import com.dabomstew.pkrandom.pokemon.EncounterSet;
import com.dabomstew.pkrandom.pokemon.Evolution;
import com.dabomstew.pkrandom.pokemon.IngameTrade;
import com.dabomstew.pkrandom.pokemon.ItemList;
import com.dabomstew.pkrandom.pokemon.Move;
import com.dabomstew.pkrandom.pokemon.MoveLearnt;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Trainer;

/**
 * Exists to assist with AssertJ Swing GUI Unit Testing NOT FUNCTIONAL FOR
 * RANDOMIZATION PURPOSES
 */
public class TestRomHandler extends AbstractRomHandler {

    public TestRomHandler(Random random) {
        super(random);
        // TODO Auto-generated constructor stub
    }

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
    public List<Pokemon> getPokemon() {
        return Arrays.asList(new Pokemon(), new Pokemon(), new Pokemon());
    }

    @Override
    public void removeEvosForPokemonPool() {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Pokemon> getStarters() {
        return Arrays.asList(new Pokemon(), new Pokemon(), new Pokemon());
    }

    @Override
    public boolean setStarters(List<Pokemon> newStarters) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canChangeStarters() {
        return true;
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
    public List<EncounterSet> getEncounters(boolean useTimeOfDay) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setEncounters(boolean useTimeOfDay, List<EncounterSet> encounters) {
        // TODO Auto-generated method stub

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
    public boolean hasPhysicalSpecialSplit() {
        // TODO Auto-generated method stub
        return false;
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
    public boolean canChangeStaticPokemon() {
        // TODO Auto-generated method stub
        return false;
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
    public Map<Pokemon, boolean[]> getMoveTutorCompatibility() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setMoveTutorCompatibility(Map<Pokemon, boolean[]> compatData) {
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
    public List<Integer> getTCNameLengthsByTrainer() {
        // TODO Auto-generated method stub
        return null;
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
    public List<IngameTrade> getIngameTrades() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setIngameTrades(List<IngameTrade> trades) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean hasDVs() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void removeTradeEvolutions(boolean changeMoveEvos, boolean changeMethodEvos) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateExtraInfo(Evolution ev) {
        // TODO Auto-generated method stub

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
    public String getROMName() {
        return "TEST";
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
    public int generationOfPokemon() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public int miscTweaksAvailable() {
        int available = 0;
        for (MiscTweak mt : MiscTweak.allTweaks) {
            available |= mt.getValue();
        }
        return available;
    }

    /**
     * Clears the list of pokemon eligible to be starter pokemon
     */
    public void clearStarterPokes() {
        starterPokes = null;
    }

    /**
     * Returns the list of pokemon eligible to be starter pokemon
     */
    public List<Pokemon> getStarterPokes() {
        return starterPokes.getPokes();
    }
}
