package com.dabomstew.pkrandom.romhandlers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import com.dabomstew.pkrandom.constants.Gen3Constants;
import com.dabomstew.pkrandom.pokemon.Evolution;
import com.dabomstew.pkrandom.pokemon.EvolutionType;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Type;

import org.junit.Test;
import org.mockito.MockedStatic;

public class Gen3Test {

    ArrayList<Pokemon> pokemonList;
    
    /**
     * When removing trades, verify that LEVEL_HIGH_BEAUTY adds
     * a new HAPPINESS evolution
     */
    @Test
    public void TestHighBeautyTradeEvoRemoval() {
        try (MockedStatic<com.dabomstew.pkrandom.RomFunctions> mockRomFunctions = mockStatic(com.dabomstew.pkrandom.RomFunctions.class)) {
            Gen3RomHandler romhandler = spy(new Gen3RomHandler(new Random()));
            doReturn(Gen3RomHandler.getRomFromSupportedRom("Emerald (U)")).when(romhandler).getRomEntry();
            doReturn(mock(Map.class)).when(romhandler).getTemplateData();
            resetDataModel(romhandler);
            romhandler.removeTradeEvolutions(true, false);
            // Stream the "evolutionsFrom" list to see if any evolutions are now HAPPINESS
            assertTrue(pokemonList.get(0).evolutionsFrom.stream().anyMatch(ev -> EvolutionType.HAPPINESS.equals(ev.type)));
        }
    }

    /**
     * Test Gen3 change methods only includes methods available in Gen3
     * Also verify no duplicate methods used, and no invalid evolutions
     */
    @Test
    public void TestGen3ChangeMethods() {
        Gen3RomHandler romhandler = spy(new Gen3RomHandler(new Random()));
        resetDataModel(romhandler);
        doReturn(mock(Map.class)).when(romhandler).getTemplateData();
        romhandler.randomizeEvolutions(false, false, true, true, false, false, false);
        romhandler.getPokemon().forEach(pk -> {
            ArrayList<EvolutionType> usedMethods = new ArrayList<EvolutionType>();
            ArrayList<Integer> usedStones = new ArrayList<Integer>();
            ArrayList<Integer> usedItems = new ArrayList<Integer>();
            pk.evolutionsFrom.forEach(evo -> {
                assertTrue("Evolution is invalid - " + evo, evo.type != null && evo.type != EvolutionType.NONE);
                assertTrue(evo.type + " was not available in Gen 3", EvolutionType.isInGeneration(3, evo.type));
                assertFalse(evo.type + " should be removed", EvolutionType.isOfType("Banned", evo.type));

                // Collect the method
                if (EvolutionType.isOfType("Stone", evo.type)) {
                    usedStones.add(evo.extraInfo);
                } else if (EvolutionType.isOfType("Item", evo.type)) {
                    usedItems.add(evo.extraInfo);
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
        });
    }

    /**
     * Test Gen3 change methods is correctly affected by remove impossible evos
     * Also verify no duplicate methods used, and no invalid evolutions
     */
    @Test
    public void TestGen3RemoveEvosChangeMethods() {
        try (MockedStatic<com.dabomstew.pkrandom.RomFunctions> mockRomFunctions = mockStatic(com.dabomstew.pkrandom.RomFunctions.class)) {
            Gen3RomHandler romhandler = spy(new Gen3RomHandler(new Random()));
            resetDataModel(romhandler);
            doReturn(mock(Map.class)).when(romhandler).getTemplateData();
            doReturn(Gen3RomHandler.getRomFromSupportedRom("Emerald (U)")).when(romhandler).getRomEntry();
            mockRomFunctions.when(() -> com.dabomstew.pkrandom.RomFunctions.removeUsedStones(any(), any())).thenCallRealMethod();
            romhandler.randomizeEvolutions(false, false, true, true, false, false, false);
            romhandler.removeTradeEvolutions(false, true);
            romhandler.getPokemon().forEach(pk -> {
                ArrayList<EvolutionType> usedMethods = new ArrayList<EvolutionType>();
                ArrayList<Integer> usedStones = new ArrayList<Integer>();
                ArrayList<Integer> usedItems = new ArrayList<Integer>();
                pk.evolutionsFrom.forEach(evo -> {
                    assertTrue("Evolution is invalid - " + evo, evo.type != null && evo.type != EvolutionType.NONE);
                    assertTrue(evo.type + " was not available in Gen 3", EvolutionType.isInGeneration(3, evo.type));
                    assertFalse(evo.type + " should be removed", EvolutionType.isOfType("Trade", evo.type) 
                    || EvolutionType.isOfType("Banned", evo.type));

                    // Collect the method
                    if (EvolutionType.isOfType("Stone", evo.type)) {
                        usedStones.add(evo.extraInfo);
                    } else if (EvolutionType.isOfType("Item", evo.type)) {
                        usedItems.add(evo.extraInfo);
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
            });
        }
    }

    /**
     * Function for granular modification of data model
     */
    private void setUp() {
        pokemonList = spy(ArrayList.class);
        for(int i = 0; i < Gen3Constants.unhackedRealPokedex; i++) {
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
    }

    /**
     * Puts data model back to initial form and assigns mock and spy substitutions
     * @param romhandler The RomHandler under test
     */
    private void resetDataModel(RomHandler romhandler) {
        setUp();
        doReturn(pokemonList).when(romhandler).getPokemon();
        doReturn(pokemonList.get(0)).when(romhandler).randomPokemon();
    }
}
