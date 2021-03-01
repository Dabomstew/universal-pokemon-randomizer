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
import com.dabomstew.pkrandom.pokemon.Type;

import org.junit.Test;

public class Gen4Test {

    ArrayList<Pokemon> pokemonList;
    Map<Pokemon, List<MoveLearnt>> movesList;

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
        romhandler.getPokemon().stream().forEach(pk -> {
            ArrayList<EvolutionType> usedMethods = new ArrayList<EvolutionType>();
            ArrayList<Integer> usedStones = new ArrayList<Integer>();
            ArrayList<Integer> usedItems = new ArrayList<Integer>();
            ArrayList<Integer> usedPokemon = new ArrayList<Integer>();
            ArrayList<Integer> usedMoves = new ArrayList<Integer>();
            pk.evolutionsFrom.stream().forEach(evo -> {
                assertTrue("Evolution is invalid - " + evo, evo.type != null && evo.type != EvolutionType.NONE);
                assertTrue(evo.type + " was not available in Gen 4", EvolutionType.isInGeneration(4, evo.type));

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
     * Function for granular modification of data model
     */
    private void setUp() {
        pokemonList = spy(ArrayList.class);
        movesList = new HashMap<Pokemon, List<MoveLearnt>>();
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
    }

    /**
     * Puts data model back to initial form and assigns mock and spy substitutions
     * @param romhandler The RomHandler under test
     */
    private void resetDataModel(RomHandler romhandler) {
        setUp();
        doReturn(pokemonList).when(romhandler).getPokemon();
        doReturn(pokemonList.get(0)).when(romhandler).randomPokemon();
        doReturn(movesList).when(romhandler).getMovesLearnt();
    }
}
