package com.dabomstew.pkrandom.romhandlers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import com.dabomstew.pkrandom.constants.Gen2Constants;
import com.dabomstew.pkrandom.pokemon.Evolution;
import com.dabomstew.pkrandom.pokemon.EvolutionType;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Type;

import org.junit.Test;

public class Gen2Test {
    ArrayList<Pokemon> pokemonList;

    /**
     * Test Gen2 change methods only includes methods available in Gen2
     * Also verify no duplicate methods used, and no invalid evolutions
     */
    @Test
    public void TestGen2ChangeMethods() {
        Gen2RomHandler romhandler = spy(new Gen2RomHandler(new Random()));
        resetDataModel(romhandler);
        doReturn(mock(Map.class)).when(romhandler).getTemplateData();
        romhandler.randomizeEvolutions(false, false, true, true, false, false, false);
        romhandler.getPokemon().forEach(pk -> {
            ArrayList<EvolutionType> usedMethods = new ArrayList<EvolutionType>();
            ArrayList<Integer> usedStones = new ArrayList<Integer>();
            ArrayList<Integer> usedItems = new ArrayList<Integer>();
            pk.evolutionsFrom.forEach(evo -> {
                assertTrue("Evolution is invalid - " + evo, evo.type != null && evo.type != EvolutionType.NONE);
                assertTrue(evo.type + " was not available in Gen 2", EvolutionType.isInGeneration(2, evo.type));
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
     * Test Gen2 change methods is correctly affected by remove impossible evos
     * Also verify no duplicate methods used, and no invalid evolutions
     */
    @Test
    public void TestGen2RemoveEvosChangeMethods() {
        Gen2RomHandler romhandler = spy(new Gen2RomHandler(new Random()));
        resetDataModel(romhandler);
        doReturn(mock(Map.class)).when(romhandler).getTemplateData();
        romhandler.randomizeEvolutions(false, false, true, true, false, false, false);
        romhandler.removeTradeEvolutions(false, true);
        romhandler.getPokemon().forEach(pk -> {
            ArrayList<EvolutionType> usedMethods = new ArrayList<EvolutionType>();
            ArrayList<Integer> usedStones = new ArrayList<Integer>();
            ArrayList<Integer> usedItems = new ArrayList<Integer>();
            pk.evolutionsFrom.forEach(evo -> {
                assertTrue("Evolution is invalid - " + evo, evo.type != null && evo.type != EvolutionType.NONE);
                assertTrue(evo.type + " was not available in Gen 2", EvolutionType.isInGeneration(2, evo.type));
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

    /**
     * Function for granular modification of data model
     */
    private void setUp() {
        pokemonList = spy(ArrayList.class);
        for(int i = 0; i < Gen2Constants.pokemonCount + 1; i++) {
            Pokemon pk = new Pokemon();
            pk.number = i;
            pk.name = "";
            pk.primaryType = Type.values()[i%17];
            pokemonList.add(pk);
            for (int j = 0; j < i % 3; j++) {
                Evolution ev = new Evolution(pk, new Pokemon(), false, EvolutionType.LEVEL, 1);
                pk.evolutionsFrom.add(ev);
            }
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
    }   
}
