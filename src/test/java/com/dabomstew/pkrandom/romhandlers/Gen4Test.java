package com.dabomstew.pkrandom.romhandlers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.dabomstew.pkrandom.constants.Gen4Constants;
import com.dabomstew.pkrandom.newnds.NARCArchive;
import com.dabomstew.pkrandom.pokemon.Evolution;
import com.dabomstew.pkrandom.pokemon.EvolutionType;
import com.dabomstew.pkrandom.pokemon.MoveLearnt;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.TrainerPokemon;
import com.dabomstew.pkrandom.pokemon.Type;

import org.junit.Test;

public class Gen4Test {
    private final int GET_FILE_LENGTH = 100;

    ArrayList<Pokemon> pokemonList;
    ArrayList<Trainer> trainerList;
    Map<Pokemon, List<MoveLearnt>> movesList;
    NARCArchive mockArch;
    ArrayList<byte[]> mlList;

    /**
     * When removing trades, verify that LEVEL_HIGH_BEAUTY adds a new HAPPINESS
     * evolution
     * 
     * @throws IOException
     */
    @Test
    public void TestHighBeautyTradeEvoRemoval() throws IOException {
        Gen4RomHandler romhandler = spy(new Gen4RomHandler(new Random()));
        NARCArchive mock_arch = mock(NARCArchive.class);
        ArrayList<byte[]> mlList = new ArrayList();
        for(int i = 0; i < Gen4Constants.pokemonCount+1; i++) {
            mlList.add(new byte[]{(byte)0xFF, (byte)0xFF});
        }
        doReturn(Gen4RomHandler.getRomFromSupportedRom("Diamond (U)")).when(romhandler).getRomEntry();
        doReturn(mlList).when(mock_arch).getFiles();
        doReturn(mock_arch).when(romhandler).readNARC(anyString());
        doReturn(mock(Map.class)).when(romhandler).getTemplateData();
        resetDataModel(romhandler);
        romhandler.removeTradeEvolutions(true, false);
        // Stream the "evolutionsFrom" list to see if any evolutions are now HAPPINESS
        assertTrue(pokemonList.get(0).evolutionsFrom.stream().anyMatch(ev -> EvolutionType.HAPPINESS.equals(ev.type)));
    }

        /**
         * Test Gen4 change methods only includes methods available in Gen4 Also verify
         * no duplicate methods used, and no invalid evolutions
         * 
         * @throws IOException
         */
        @Test
        public void TestGen4ChangeMethods() throws IOException {
        Gen4RomHandler romhandler = spy(new Gen4RomHandler(new Random()));
        doReturn(Gen4RomHandler.getRomFromSupportedRom("Diamond (U)")).when(romhandler).getRomEntry();
        doReturn(mock(Map.class)).when(romhandler).getTemplateData();
        resetDataModel(romhandler);
        romhandler.randomizeEvolutions(false, false, true, true, false, false, false);
        romhandler.getPokemon().forEach(pk -> {
            ArrayList<EvolutionType> usedMethods = new ArrayList<EvolutionType>();
            ArrayList<Integer> usedStones = new ArrayList<Integer>();
            ArrayList<Integer> usedItems = new ArrayList<Integer>();
            ArrayList<Integer> usedPokemon = new ArrayList<Integer>();
            ArrayList<Integer> usedMoves = new ArrayList<Integer>();
            pk.evolutionsFrom.forEach(evo -> {
                assertTrue("Evolution is invalid - " + evo, evo.type != null && evo.type != EvolutionType.NONE);
                assertTrue(evo.type + " was not available in Gen 4", EvolutionType.isInGeneration(4, evo.type));
                assertFalse(evo.type + " should be removed", EvolutionType.isOfType("Banned", evo.type));

                // Collect the method
                if (EvolutionType.isOfType("Stone", evo.type)) {
                    usedStones.add(evo.extraInfo);
                } else if (EvolutionType.isOfType("Item", evo.type)) {
                    usedItems.add(evo.extraInfo);
                } else if (EvolutionType.isOfType("Party", evo.type)) {
                    usedPokemon.add(evo.extraInfo);
                } else if (evo.type == EvolutionType.LEVEL_WITH_MOVE) {
                    usedMoves.add(evo.extraInfo);
                } else {
                    usedMethods.add(evo.type);
                }
            });

            // Verify no duplicates
            HashSet<EvolutionType> uniqueMethods = new HashSet<EvolutionType>(usedMethods);
            assertTrue("Duplicate method detected - " + Arrays.toString(usedMethods.toArray()), 
                uniqueMethods.size() == usedMethods.size());
            HashSet<Integer> uniqueStones = new HashSet<Integer>(usedStones);
            assertTrue("Duplicate stone detected - " + Arrays.toString(usedStones.toArray()), 
                    uniqueStones.size() == usedStones.size());
            HashSet<Integer> uniqueItems = new HashSet<Integer>(usedItems);
            assertTrue("Duplicate item detected - " + Arrays.toString(usedItems.toArray()), 
                    uniqueItems.size() == usedItems.size());
            HashSet<Integer> uniquePokemon = new HashSet<Integer>(usedPokemon);
            assertTrue("Duplicate pokemon detected - " + Arrays.toString(usedPokemon.toArray()), 
                    uniquePokemon.size() == usedPokemon.size());
            HashSet<Integer> uniqueMoves = new HashSet<Integer>(usedMoves);
            assertTrue("Duplicate move detected - " + Arrays.toString(usedMoves.toArray()), 
                    uniqueMoves.size() == usedMoves.size());
        });
    }

        /**
         * Test Gen4 change methods is correctly affected by remove impossible evos
         * no duplicate methods used, and no invalid evolutions
         * 
         * @throws IOException
         */
        @Test
        public void TestGen4RemoveEvosChangeMethods() throws IOException {
        Gen4RomHandler romhandler = spy(new Gen4RomHandler(new Random()));
        doReturn(Gen4RomHandler.getRomFromSupportedRom("Diamond (U)")).when(romhandler).getRomEntry();
        doReturn(mock(Map.class)).when(romhandler).getTemplateData();
        resetDataModel(romhandler);
        romhandler.randomizeEvolutions(false, false, true, true, false, false, false);
        romhandler.removeTradeEvolutions(false, true);
        romhandler.getPokemon().forEach(pk -> {
            ArrayList<EvolutionType> usedMethods = new ArrayList<EvolutionType>();
            ArrayList<Integer> usedStones = new ArrayList<Integer>();
            ArrayList<Integer> usedItems = new ArrayList<Integer>();
            ArrayList<Integer> usedPokemon = new ArrayList<Integer>();
            ArrayList<Integer> usedMoves = new ArrayList<Integer>();
            HashMap<Integer, ArrayList<EvolutionType>> itemEvos = new HashMap<Integer, ArrayList<EvolutionType>>();
            pk.evolutionsFrom.forEach(evo -> {
                assertTrue("Evolution is invalid - " + evo, evo.type != null && evo.type != EvolutionType.NONE);
                assertTrue(evo.type + " was not available in Gen 4", EvolutionType.isInGeneration(4, evo.type));
                assertFalse(evo.type + " should be removed", EvolutionType.isOfType("Trade", evo.type) 
                    || EvolutionType.isOfType("Banned", evo.type));

                // Collect the method
                if (EvolutionType.isOfType("Stone", evo.type)) {
                    usedStones.add(evo.extraInfo);
                } else if (EvolutionType.isOfType("Item", evo.type)) {
                    usedItems.add(evo.extraInfo);
                    ArrayList<EvolutionType> etList = itemEvos.getOrDefault(evo.extraInfo, 
                        new ArrayList<EvolutionType>());
                    etList.add(evo.type);
                    itemEvos.put(evo.extraInfo, etList);
                } else if (EvolutionType.isOfType("Party", evo.type)) {
                    usedPokemon.add(evo.extraInfo);
                } else if (evo.type == EvolutionType.LEVEL_WITH_MOVE) {
                    usedMoves.add(evo.extraInfo);
                } else {
                    usedMethods.add(evo.type);
                }
            });

            // Verify no duplicates
            HashSet<EvolutionType> uniqueMethods = new HashSet<EvolutionType>(usedMethods);
            assertTrue("Duplicate method detected - " + Arrays.toString(usedMethods.toArray()), 
                uniqueMethods.size() == usedMethods.size());
            HashSet<Integer> uniqueStones = new HashSet<Integer>(usedStones);
            assertTrue("Duplicate stone detected - " + Arrays.toString(usedStones.toArray()), 
                    uniqueStones.size() == usedStones.size());
            HashSet<Integer> uniquePokemon = new HashSet<Integer>(usedPokemon);
            assertTrue("Duplicate pokemon detected - " + Arrays.toString(usedPokemon.toArray()), 
                    uniquePokemon.size() == usedPokemon.size());
            HashSet<Integer> uniqueMoves = new HashSet<Integer>(usedMoves);
            assertTrue("Duplicate move detected - " + Arrays.toString(usedMoves.toArray()), 
                    uniqueMoves.size() == usedMoves.size());

            // Check if any item duplicates correspond to Night/Day Item
            // This is what TRADE_ITEM turns into after removing trade evos
            // === ASSUMPTIONS ===
            // - More than 1 item may be duplicated
            // - An item can have more than 2 copies
            // - The only acceptable occurrence is if there is exactly 1 LEVEL_ITEM_NIGHT and
            //   exactly 1 LEVEL_ITEM_DAY. 
            HashSet<Integer> uniqueItems = new HashSet<Integer>(usedItems);
            if(uniqueItems.size() != usedItems.size()) {
                // Remove uniques, leaving only duplicates
                for(Integer i : uniqueItems) {
                    usedItems.remove(usedItems.indexOf(i));
                }

                // Make it a set to limit checks
                HashSet<Integer> dupsToCheck = new HashSet<Integer>(usedItems);

                // Check each duplicate item if it conforms to acceptable criteria
                for(Integer j : dupsToCheck) {
                    // Get the list of evolution types that use this item
                    List<EvolutionType> typeWithItem = itemEvos.get(j);

                    // Cannot be more than length 2 and must contain LEVEL_ITEM_DAY and LEVEL_ITEM_NIGHT
                    assertTrue("Duplicate items detected - Item " + j + " is used by " 
                        + Arrays.toString(typeWithItem.toArray()), 
                        typeWithItem.size() == 2 && typeWithItem.contains(EvolutionType.LEVEL_ITEM_DAY)
                        && typeWithItem.contains(EvolutionType.LEVEL_ITEM_NIGHT)); 
                }
            }
        });
    }

    /**
     * Test trainer random held item gives pokemon valid items
     * @throws IOException
     */
    @Test
    public void TestGen4TrainerRandomHeldItem() throws IOException {
        Gen4RomHandler romhandler = spy(new Gen4RomHandler(new Random()));
        doReturn(Gen4RomHandler.getRomFromSupportedRom("Diamond (U)")).when(romhandler).getRomEntry();
        doReturn(mock(Map.class)).when(romhandler).getTemplateData();
        resetDataModel(romhandler);
        romhandler.randomizeTrainerPokes(false, false, false, false, false, false, false, false, true, 0);
        for (Trainer t : romhandler.getTrainers()) {
            for (TrainerPokemon tp : t.getPokemon()) {
                assertTrue(tp.heldItem + " was not in Gen 4 allowed items.", 
                    Gen4Constants.trainerItemList.isAllowed(tp.heldItem));
            }
        }
    }

    /**
     * Function for granular modification of data model
     */
    private void setUp() {
        pokemonList = spy(ArrayList.class);
        trainerList = spy(ArrayList.class);
        movesList = new HashMap<Pokemon, List<MoveLearnt>>();
        mockArch = mock(NARCArchive.class);
        mlList = new ArrayList();
        for(int i = 0; i < Gen4Constants.pokemonCount + 1; i++) {
            Pokemon pk = new Pokemon();
            pk.number = i;
            pk.name = "";
            pk.primaryType = Type.BUG;
            pokemonList.add(pk);
            for (int j = 0; j < i % 3; j++) {
                Evolution ev = new Evolution(pk, new Pokemon(), false, EvolutionType.LEVEL, 1);
                pk.evolutionsFrom.add(ev);
            }
        }
        Evolution ev = new Evolution(pokemonList.get(0), pokemonList.get(1), false, EvolutionType.LEVEL_HIGH_BEAUTY, 0);
        pokemonList.get(0).evolutionsFrom.add(ev);

        while (trainerList.size() < GET_FILE_LENGTH) {
            Trainer t = new Trainer();
            TrainerPokemon tp = mock(TrainerPokemon.class);
            doReturn(pokemonList.get(0)).when(tp).getPokemon();
            t.getPokemon().add(tp);
            trainerList.add(t);
        }
        for(int i = 0; i < pokemonList.size(); i++) {
            ArrayList<MoveLearnt> pMoveList = new ArrayList<MoveLearnt>();
            MoveLearnt ml = new MoveLearnt();
            ml.move = 1;
            ml.level = 1;
            pMoveList.add(ml);
            MoveLearnt ml2 = new MoveLearnt();
            ml.move = 2;
            ml.level = 2;
            pMoveList.add(ml2);
            MoveLearnt ml3 = new MoveLearnt();
            ml.move = 3;
            ml.level = 3;
            pMoveList.add(ml3);
            MoveLearnt ml4 = new MoveLearnt();
            ml.move = 4;
            ml.level = 4;
            pMoveList.add(ml4);
            movesList.put(pokemonList.get(i), pMoveList);   
        }
        for (int i = 0; i < GET_FILE_LENGTH; i++) {
            mlList.add(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF });
        }
    }

    /**
     * Puts data model back to initial form and assigns mock and spy substitutions
     * @param romhandler The RomHandler under test
     * @throws IOException
     */
    private void resetDataModel(Gen4RomHandler romhandler) throws IOException {
        setUp();
        doReturn(pokemonList).when(romhandler).getPokemon();
        doReturn(pokemonList.get(0)).when(romhandler).randomPokemon();
        doReturn(trainerList).when(romhandler).getTrainers();
        doReturn(movesList).when(romhandler).getMovesLearnt();
        doReturn(mlList).when(mockArch).getFiles();
        doReturn(mockArch).when(romhandler).readNARC(anyString());
        doReturn(mockArch).when(romhandler).getScriptNARC();
        doNothing().when(romhandler).writeNARC(anyString(), any());
    }
}
