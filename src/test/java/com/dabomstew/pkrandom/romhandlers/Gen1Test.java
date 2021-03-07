package com.dabomstew.pkrandom.romhandlers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import com.dabomstew.pkrandom.pokemon.Evolution;
import com.dabomstew.pkrandom.pokemon.EvolutionType;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Type;

import org.junit.Test;

public class Gen1Test {

    ArrayList<Pokemon> pokemonList;

    /**
     * Test Gen1 change methods only includes methods available in Gen1
     * Also verify no duplicate methods used, and no invalid evolutions
     */
    @Test
    public void TestGen1ChangeMethods() {
        Gen1RomHandler romhandler = spy(new Gen1RomHandler(new Random()));
        resetDataModel(romhandler);
        doReturn(mock(Map.class)).when(romhandler).getTemplateData();
        romhandler.randomizeEvolutions(false, false, true, true, false, false, false);
        romhandler.getPokemon().forEach(pk -> {
            ArrayList<EvolutionType> usedMethods = new ArrayList<EvolutionType>();
            ArrayList<Integer> usedStones = new ArrayList<Integer>();
            pk.evolutionsFrom.forEach(evo -> {
                assertTrue("Evolution is invalid - " + evo, evo.type != null && evo.type != EvolutionType.NONE);
                assertTrue(evo.type + " was not available in Gen 1", EvolutionType.isInGeneration(1, evo.type));
                assertFalse(evo.type + " should be removed", EvolutionType.isOfType("Banned", evo.type));

                // Collect the method
                if (EvolutionType.isOfType("Stone", evo.type)) {
                    usedStones.add(evo.extraInfo);
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
        });
    }

        /**
         * Test Gen1 change methods is correctly affected by remove impossible evos
         * Also verify no duplicate methods used, and no invalid evolutions
         */
        @Test
        public void TestGen1RemoveEvosWithChangeMethods() {
            Gen1RomHandler romhandler = spy(new Gen1RomHandler(new Random()));
            resetDataModel(romhandler);
            doReturn(mock(Map.class)).when(romhandler).getTemplateData();
            romhandler.randomizeEvolutions(false, false, true, true, false, false, false);
            romhandler.removeTradeEvolutions(false, true);
            romhandler.getPokemon().forEach(pk -> {
                ArrayList<EvolutionType> usedMethods = new ArrayList<EvolutionType>();
                ArrayList<Integer> usedStones = new ArrayList<Integer>();
                pk.evolutionsFrom.forEach(evo -> {
                    assertTrue("Evolution is invalid - " + evo, evo.type != null && evo.type != EvolutionType.NONE);
                    assertTrue(evo.type + " was not available in Gen 1", EvolutionType.isInGeneration(1, evo.type));
                    assertFalse(evo.type + " should be removed", EvolutionType.isOfType("Trade", evo.type) 
                        || EvolutionType.isOfType("Banned", evo.type));

                    // Collect the method
                    if (EvolutionType.isOfType("Stone", evo.type)) {
                        usedStones.add(evo.extraInfo);
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
            });
        }

    /**
     * Function for granular modification of data model
     */
    private void setUp() {
        pokemonList = spy(ArrayList.class);
        for(int i = 0; i < 151 + 1; i++) {
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
