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

import com.dabomstew.pkrandom.constants.Gen5Constants;
import com.dabomstew.pkrandom.newnds.NARCArchive;
import com.dabomstew.pkrandom.pokemon.Evolution;
import com.dabomstew.pkrandom.pokemon.EvolutionType;
import com.dabomstew.pkrandom.pokemon.MoveLearnt;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.TrainerPokemon;
import com.dabomstew.pkrandom.pokemon.Type;
import com.dabomstew.pkrandom.text.Gen5TextHandler;

import org.junit.Test;

public class Gen5Test {

    private final int GET_FILE_LENGTH = 900;

    ArrayList<Pokemon> pokemonList;
    ArrayList<Trainer> trainerList;
    Gen5TextHandler mockTextHandler;
    NARCArchive mockArch;
    ArrayList<byte[]> mlList;
    Map<Pokemon, List<MoveLearnt>> movesList;

    /**
     * When altering starters, make sure the ROM's text is updated when it is a
     * Black/White ROM
     * 
     * @throws IOException
     */
    @Test
    public void TestBW1StarterText() throws IOException {
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        resetDataModel(romhandler);
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
        romhandler.setStarters(pokemonList.subList(0, 3));
        verify(mockTextHandler, times(1)).bw1StarterTextModifications(any());
        verify(mockTextHandler, times(0)).bw2StarterTextModifications(any());
    }

    /**
     * When altering starters, make sure the ROM's text is updated when it is a
     * Black 2/White 2 ROM
     * 
     * @throws IOException
     */
    @Test
    public void TestBW2StarterText() throws IOException {
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        resetDataModel(romhandler);
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black 2 (U)")).when(romhandler).getRomEntry();
        romhandler.setStarters(pokemonList.subList(0, 3));
        verify(mockTextHandler, times(0)).bw1StarterTextModifications(any());
        verify(mockTextHandler, times(1)).bw2StarterTextModifications(any());
    }

    /**
     * When altering statics, make sure the ROM's text is updated when it is a
     * Black/White ROM
     * 
     * @throws IOException
     */
    @Test
    public void TestBW1MonkeyText() throws IOException {
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        resetDataModel(romhandler);
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
        // The length of this sublist must match the length of static pokemon in the rom
        // Run a print statement in the setStaticPokemon function to get it
        romhandler.setStaticPokemon(pokemonList.subList(0, 31));
        verify(mockTextHandler, times(1)).bw1MonkeyTextModifications(any(), any(), any());
    }

    /**
     * When altering trainers, make sure the ROM's text is updated when it is a
     * Black/White ROM
     * 
     * @throws IOException
     */
    @Test
    public void TestBW1StriatonCityText() throws IOException {
        // Test with and without null
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        resetDataModel(romhandler);
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
        doReturn(0xFFFF).when(romhandler).readWord(any(), anyInt());
        doReturn(pokemonList.subList(0, 3)).when(romhandler).getStarters();
        
        // Verify with random team
        romhandler.randomizeTrainerPokes(false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1StriatonCityTextModifications(isNull());

        // Verify with type theming
        romhandler.typeThemeTrainerPokes(false, false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1StriatonCityTextModifications(isNotNull());
    }

    /**
     * When altering trainers, make sure the ROM's text is altered when it is a
     * Black/White ROM
     * @throws IOException
     */
    @Test
    public void TestBW1NacreneCityText() throws IOException {
        // Test with and without null
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        resetDataModel(romhandler);
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
        doReturn(0xFFFF).when(romhandler).readWord(any(), anyInt());
        doReturn(pokemonList.subList(0, 3)).when(romhandler).getStarters();
        
        // Verify with random team
        romhandler.randomizeTrainerPokes(false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1NacreneCityTextModifications(isNull(), any());

        // Verify with type theming
        romhandler.typeThemeTrainerPokes(false, false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1NacreneCityTextModifications(isNotNull(), any());
    }

    /**
     * When altering starters, make sure the ROM's text is updated when it is a
     * Black/White ROM
     * 
     * @throws IOException
     */
    @Test
    public void TestBW1CasteliaCityText() throws IOException {
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        resetDataModel(romhandler);
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
        romhandler.setStarters(pokemonList.subList(0, 3));
        verify(mockTextHandler, times(1)).bw1CasteliaCityItemTextModifications(any());
    }

    /**
     * When altering trainers, make sure the ROM's text is updated when it is a
     * Black/White ROM
     * 
     * @throws IOException
     */
    @Test
    public void TestBW1CasteliaCityPraiseText() throws IOException {
        // Test with and without null
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        resetDataModel(romhandler);
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
        doReturn(0xFFFF).when(romhandler).readWord(any(), anyInt());
        doReturn(pokemonList.subList(0, 3)).when(romhandler).getStarters();
        
        // Verify with random team
        romhandler.randomizeTrainerPokes(false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1CasteliaCityPraiseTextModifications(isNull());

        // Verify with type theming
        romhandler.typeThemeTrainerPokes(false, false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1CasteliaCityPraiseTextModifications(isNotNull());
    }

    /**
     * When altering trainers, make sure the ROM's text is updated when it is a
     * Black/White ROM
     * 
     * @throws IOException
     */
    @Test
    public void TestBW1CasteliaCityBurghText() throws IOException {
        // Test with and without null
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        resetDataModel(romhandler);
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
        doReturn(0xFFFF).when(romhandler).readWord(any(), anyInt());
        doReturn(pokemonList.subList(0, 3)).when(romhandler).getStarters();
        
        // Verify with random team
        romhandler.randomizeTrainerPokes(false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1CasteliaCityBurghTextModifications(isNull());

        // Verify with type theming
        romhandler.typeThemeTrainerPokes(false, false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1CasteliaCityBurghTextModifications(isNotNull());
    }

    /**
     * When altering trainers, make sure the ROM's text is updated when it is a
     * Black/White ROM
     * 
     * @throws IOException
     */
    @Test
    public void TestBW1CherenBurghText() throws IOException {
        // Test with and without null
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        resetDataModel(romhandler);
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
        doReturn(0xFFFF).when(romhandler).readWord(any(), anyInt());
        doReturn(pokemonList.subList(0, 3)).when(romhandler).getStarters();
        
        // Verify with random team
        romhandler.randomizeTrainerPokes(false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1CherenBurghTextModifications(isNull());

        // Verify with type theming
        romhandler.typeThemeTrainerPokes(false, false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1CherenBurghTextModifications(isNotNull());
    }

    /**
     * When altering trainers, make sure the ROM's text is altered when it is a
     * Black/White ROM
     * @throws IOException
     */
    @Test
    public void TestBW1NimbasaCityText() throws IOException {
        // Test with and without null
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        resetDataModel(romhandler);
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
        doReturn(0xFFFF).when(romhandler).readWord(any(), anyInt());
        doReturn(pokemonList.subList(0, 3)).when(romhandler).getStarters();
        
        // Verify with random team
        romhandler.randomizeTrainerPokes(false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1NimbasaCityTextModifications(isNull(), any());

        // Verify with type theming
        romhandler.typeThemeTrainerPokes(false, false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1NimbasaCityTextModifications(isNotNull(), any());
    }  
    
    /**
     * When altering trainers, make sure the ROM's text is updated when it is a
     * Black/White ROM
     * 
     * @throws IOException
     */
    @Test
    public void TestBW1JuniperElesaText() throws IOException {
        // Test with and without null
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        resetDataModel(romhandler);
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
        doReturn(0xFFFF).when(romhandler).readWord(any(), anyInt());
        doReturn(pokemonList.subList(0, 3)).when(romhandler).getStarters();
        
        // Verify with random team
        romhandler.randomizeTrainerPokes(false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1JuniperTextModifications(isNull());

        // Verify with type theming
        romhandler.typeThemeTrainerPokes(false, false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1JuniperTextModifications(isNotNull());
    }

    /**
     * When altering trainers, make sure the ROM's text is altered when it is a
     * Black/White ROM
     * @throws IOException
     */
    @Test
    public void TestBW1DriftveilCityText() throws IOException {
        // Test with and without null
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        resetDataModel(romhandler);
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
        doReturn(0xFFFF).when(romhandler).readWord(any(), anyInt());
        doReturn(pokemonList.subList(0, 3)).when(romhandler).getStarters();
        
        // Verify with random team
        romhandler.randomizeTrainerPokes(false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1DriftveilCityTextModifications(isNull(), any());

        // Verify with type theming
        romhandler.typeThemeTrainerPokes(false, false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1DriftveilCityTextModifications(isNotNull(), any());
    }
    
    /**
     * When altering trainers, make sure the ROM's text is updated when it is a
     * Black/White ROM
     * 
     * @throws IOException
     */
    @Test
    public void TestBW1MistraltonCityText() throws IOException {
        // Test with and without null
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        resetDataModel(romhandler);
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
        doReturn(0xFFFF).when(romhandler).readWord(any(), anyInt());
        doReturn(pokemonList.subList(0, 3)).when(romhandler).getStarters();
        
        // Verify with random team
        romhandler.randomizeTrainerPokes(false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1MistraltonCityTextModifications(isNull());

        // Verify with type theming
        romhandler.typeThemeTrainerPokes(false, false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1MistraltonCityTextModifications(isNotNull());
    }    

    /**
     * When altering trainers, make sure the ROM's text is updated when it is a
     * Black/White ROM
     * 
     * @throws IOException
     */
    @Test
    public void TestBW1PinwheelForestText() throws IOException {
        // Test with and without null
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        resetDataModel(romhandler);
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
        doReturn(0xFFFF).when(romhandler).readWord(any(), anyInt());
        doReturn(pokemonList.subList(0, 3)).when(romhandler).getStarters();
        
        // Verify with random team
        romhandler.randomizeTrainerPokes(false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1PinwheelForestTextModifications(isNull());

        // Verify with type theming
        romhandler.typeThemeTrainerPokes(false, false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1PinwheelForestTextModifications(isNotNull());
    }  

    /**
     * When altering trainers, make sure the ROM's text is updated when it is a
     * Black/White ROM
     * 
     * @throws IOException
     */
    @Test
    public void TestBW1CelestialTowerText() throws IOException {
        // Test with and without null
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        resetDataModel(romhandler);
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
        doReturn(0xFFFF).when(romhandler).readWord(any(), anyInt());
        doReturn(pokemonList.subList(0, 3)).when(romhandler).getStarters();
        
        // Verify with random team
        romhandler.randomizeTrainerPokes(false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1CelestialTowerTextModifications(isNull());

        // Verify with type theming
        romhandler.typeThemeTrainerPokes(false, false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1CelestialTowerTextModifications(isNotNull());
    }  
            
    /**
     * Test Gen5 change methods only includes methods available in Gen5
     * Also verify no duplicate methods used, and no invalid evolutions
     * 
     * @throws IOException
     */
    @Test
    public void TestGen5ChangeMethods() throws IOException {
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
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
                assertTrue(evo.type + " was not available in Gen 5", EvolutionType.isInGeneration(5, evo.type));
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
     * Test Gen5 change methods is correctly affected by remvoe impossible evos
     * Also verify no duplicate methods used, and no invalid evolutions
     * 
     * @throws IOException
     */
    @Test
    public void TestGen5RemoveEvosChangeMethods() throws IOException {
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
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
                assertTrue(evo.type + " was not available in Gen 5", EvolutionType.isInGeneration(5, evo.type));
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
     * Function for granular modification of data model
     */
    private void setUp() {
        pokemonList = new ArrayList<Pokemon>();
        trainerList = new ArrayList<Trainer>();
        movesList = new HashMap<Pokemon, List<MoveLearnt>>();
        mockTextHandler = mock(Gen5TextHandler.class);
        mockArch = mock(NARCArchive.class);
        mlList = new ArrayList();
        for (int i = 0; i < Gen5Constants.pokemonCount + 1; i++) {
            Pokemon pk = new Pokemon();
            pk.number = i;
            pk.name = "";
            pk.primaryType = Type.values()[i%Type.values().length];
            pokemonList.add(pk);
            for (int j = 0; j < i % 3; j++) {
                Evolution ev = new Evolution(pk, new Pokemon(), false, EvolutionType.LEVEL, 1);
                pk.evolutionsFrom.add(ev);
            }
        }
        for(String tag: new String[]{"GYM1", "CILAN", "CHILI", "CRESS"}) {
            Trainer t = new Trainer();
            t.tag = tag;
            TrainerPokemon tp1 = mock(TrainerPokemon.class);
            TrainerPokemon tp2 = mock(TrainerPokemon.class);
            doReturn(pokemonList.get(0)).when(tp1).getPokemon();
            doReturn(pokemonList.get(0)).when(tp2).getPokemon();
            t.pokemon.add(tp1);
            t.pokemon.add(tp2);
            trainerList.add(t);
        }
        // Fill in the rest to match length of getFile()
        while (trainerList.size() < GET_FILE_LENGTH) {
            Trainer t = new Trainer();
            TrainerPokemon tp = mock(TrainerPokemon.class);
            doReturn(pokemonList.get(0)).when(tp).getPokemon();
            t.pokemon.add(tp);
            trainerList.add(t);
        }
        for (int i = 0; i < GET_FILE_LENGTH; i++) {
            mlList.add(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF });
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
    }

    /**
     * Puts data model back to initial form and assigns mock and spy substitutions
     * 
     * @param romhandler
     *                       The RomHandler under test
     * @throws IOException
     */
    private void resetDataModel(Gen5RomHandler romhandler) throws IOException {
        setUp();
        doReturn(pokemonList).when(romhandler).getPokemon();
        doReturn(pokemonList).when(romhandler).getCachedAllList();
        doReturn(pokemonList.get(0)).when(romhandler).randomPokemon();
        doReturn(trainerList).when(romhandler).getTrainers();
        doReturn(mockTextHandler).when(romhandler).getTextHandler();
        doReturn(0).when(romhandler).readWord(any(), anyInt());
        doReturn(mlList).when(mockArch).getFiles();
        doReturn(mockArch).when(romhandler).readNARC(anyString());
        doReturn(mockArch).when(romhandler).getScriptNARC();
        doNothing().when(romhandler).writeWord(any(), anyInt(), anyInt());
        doNothing().when(romhandler).writeNARC(anyString(), any());
        doNothing().when(romhandler).writeLong(any(), anyInt(), anyInt());
        doReturn(movesList).when(romhandler).getMovesLearnt();
    }
    
}
