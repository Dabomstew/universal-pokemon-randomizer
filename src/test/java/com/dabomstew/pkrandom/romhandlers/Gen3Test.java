package com.dabomstew.pkrandom.romhandlers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
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
            romhandler.removeTradeEvolutions(true);
            // Stream the "evolutionsFrom" list to see if any evolutions are now HAPPINESS
            assertTrue(pokemonList.get(0).evolutionsFrom.stream().anyMatch(item -> EvolutionType.HAPPINESS.equals(item.type)));
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
