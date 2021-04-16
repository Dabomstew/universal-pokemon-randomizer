package com.dabomstew.pkrandom.romhandlers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import freemarker.template.Template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.IntStream;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.dabomstew.pkrandom.MiscTweak;
import com.dabomstew.pkrandom.constants.Gen4Constants;
import com.dabomstew.pkrandom.constants.Gen5Constants;
import com.dabomstew.pkrandom.constants.GlobalConstants;
import com.dabomstew.pkrandom.newnds.NARCArchive;
import com.dabomstew.pkrandom.pokemon.Move;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Type;
import com.dabomstew.pkrandom.pokemon.Evolution;
import com.dabomstew.pkrandom.pokemon.EvolutionType;
import com.dabomstew.pkrandom.text.Gen5TextHandler;

public class LogTest {

    private static final int MAX_POKE_COUNT = 650;
    
    ArrayList<Move> moveList;
    ArrayList<Pokemon> pokemonList;
    HashMap<String, Object> templateData = new HashMap();

    @Test
    public void TestGen1MoveModernization() {
        RomHandler romhandler = spy(new Gen1RomHandler(new Random()));
        resetDataModel(romhandler, 250);
        romhandler.initMoveModernization();
        
        //**************************
        // Test legacy selection
        //**************************
        romhandler.updateMovesToGen5();
        romhandler.printMoveModernization();
        for(Move m : getTemplateDataMovesMod(romhandler)) {
            assertEquals(m, moveList.get(m.number));
        }
        // No Gen 2 changes
        assertNotEquals(romhandler.getMoves().get(174).type, Type.GHOST);
        
        // Move Modernization list is separate from moves list
        // Verify by randomizing Tackle's power (and every other power even though we don't check)
        while(romhandler.getMoves().get(33).power == 50) {
            romhandler.randomizeMovePowers();
        }
        // Sanity check that we are testing move Id 33 (index 10 in move modifications), which is Tackle)
        assertEquals(getTemplateDataMovesMod(romhandler).get(10).number, 33);
        assertNotEquals(getTemplateDataMovesMod(romhandler).get(10).power, 
                romhandler.getMoves().get(33).power);
        assertEquals(getTemplateDataMovesMod(romhandler).get(10).power, 50);
        assertEquals(getTemplateDataMovesMod(romhandler).size(), 34);
        
        //*****************************************
        // Test Update Moves selection + legacy
        //*****************************************
        romhandler.updateMovesToGen6();
        romhandler.printMoveModernization();

        for(Move m : getTemplateDataMovesMod(romhandler)) {
            assertEquals(m, moveList.get(m.number));
        }
        // No Gen 2 changes
        assertNotEquals(romhandler.getMoves().get(168).pp, 25);
        
        // Move Modernization list is separate from moves list
        // Verify by randomizing Surf's power (and every other power even though we don't check)
        while(romhandler.getMoves().get(57).power == 90) {
            romhandler.randomizeMovePowers();
        }
        // Sanity check that we are testing move Id 57 (index 19 in move modifications), which is Surf)
        assertEquals(getTemplateDataMovesMod(romhandler).get(19).number, 57);
        assertNotEquals(getTemplateDataMovesMod(romhandler).get(19).power, 
                romhandler.getMoves().get(57).power);
        assertEquals(getTemplateDataMovesMod(romhandler).get(19).power, 90);
        assertEquals(getTemplateDataMovesMod(romhandler).size(), 52);

        
        //************************************
        // Test Update Moves selection only
        //************************************
        resetDataModel(romhandler, 250);
        romhandler.initMoveModernization();
        romhandler.updateMovesToGen6();
        romhandler.printMoveModernization();

        for(Move m : getTemplateDataMovesMod(romhandler)) {
            assertEquals(m, moveList.get(m.number));
        }
        // No Gen 2 changes
        assertNotEquals(romhandler.getMoves().get(168).pp, 25);
        
        // Move Modernization list is separate from moves list
        // Verify by randomizing Surf's power (and every other power even though we don't check)
        while(romhandler.getMoves().get(57).power == 90) {
            romhandler.randomizeMovePowers();
        }
        // Sanity check that we are testing move Id 57 (index 5 in move modifications), which is Surf)
        assertEquals(getTemplateDataMovesMod(romhandler).get(5).number, 57);
        assertNotEquals(getTemplateDataMovesMod(romhandler).get(5).power, 
                romhandler.getMoves().get(57).power);
        assertEquals(getTemplateDataMovesMod(romhandler).get(5).power, 90);
        assertEquals(getTemplateDataMovesMod(romhandler).size(), 23);
    }

    @Test
    public void TestGen2MoveModernization() {
        RomHandler romhandler = spy(new Gen2RomHandler(new Random()));
        resetDataModel(romhandler, 353);
        romhandler.initMoveModernization();
        
        //**************************
        // Test legacy selection
        //**************************
        romhandler.updateMovesToGen5();
        romhandler.printMoveModernization();

        for(Move m : getTemplateDataMovesMod(romhandler)) {
            assertEquals(m, moveList.get(m.number));
        }
        // No Gen 3 changes
        assertNotEquals(romhandler.getMoves().get(253).power, 90);
        
        // Move Modernization list is separate from moves list
        // Verify by randomizing Outrage's power (and every other power even though we don't check)
        while(romhandler.getMoves().get(200).power == 120) {
            romhandler.randomizeMovePowers();
        }
        // Sanity check that we are testing move Id 200 (index 39 in move modifications), which is Outrage)
        assertEquals(getTemplateDataMovesMod(romhandler).get(39).number, 200);
        assertNotEquals(getTemplateDataMovesMod(romhandler).get(39).power, 
                romhandler.getMoves().get(200).power);
        assertEquals(getTemplateDataMovesMod(romhandler).get(39).power, 120);
        assertEquals(getTemplateDataMovesMod(romhandler).size(), 45);
        
        //*****************************************
        // Test Update Moves selection + legacy
        //*****************************************
        romhandler.updateMovesToGen6();
        romhandler.printMoveModernization();

        for(Move m : getTemplateDataMovesMod(romhandler)) {
            assertEquals(m, moveList.get(m.number));
        }
        // No Gen 3 changes
        assertNotEquals(romhandler.getMoves().get(257).power, 95);
        
        // Move Modernization list is separate from moves list
        // Verify by randomizing Thief's power (and every other power even though we don't check)
        while(romhandler.getMoves().get(168).power == 60) {
            romhandler.randomizeMovePowers();
        }
        // Sanity check that we are testing move Id 168 (index 52 in move modifications), which is thief)
        assertEquals(getTemplateDataMovesMod(romhandler).get(52).number, 168);
        assertNotEquals(getTemplateDataMovesMod(romhandler).get(52).power, 
                romhandler.getMoves().get(168).power);
        assertEquals(getTemplateDataMovesMod(romhandler).get(52).power, 60);
        assertEquals(getTemplateDataMovesMod(romhandler).size(), 65);

        
        //************************************
        // Test Update Moves selection only
        //************************************
        resetDataModel(romhandler, 353);
        romhandler.initMoveModernization();
        romhandler.updateMovesToGen6();
        romhandler.printMoveModernization();

        for(Move m : getTemplateDataMovesMod(romhandler)) {
            assertEquals(m, moveList.get(m.number));
        }
        // No Gen 3 changes
        assertNotEquals(romhandler.getMoves().get(257).power, 95);
        
        // Move Modernization list is separate from moves list
        // Verify by randomizing Thief's power (and every other power even though we don't check)
        while(romhandler.getMoves().get(168).power == 60) {
            romhandler.randomizeMovePowers();
        }
        // Sanity check that we are testing move Id 168 (index 23 in move modifications), which is Thief)
        assertEquals(getTemplateDataMovesMod(romhandler).get(23).number, 168);
        assertNotEquals(getTemplateDataMovesMod(romhandler).get(23).power, 
                romhandler.getMoves().get(168).power);
        assertEquals(getTemplateDataMovesMod(romhandler).get(23).power, 60);
        assertEquals(getTemplateDataMovesMod(romhandler).size(), 27);
    }
    
    @Test
    public void TestGen3MoveModernization() {
        RomHandler romhandler = spy(new Gen3RomHandler(new Random()));
        resetDataModel(romhandler, 466);
        romhandler.initMoveModernization();
        
        //**************************
        // Test legacy selection
        //**************************
        romhandler.updateMovesToGen5();
        romhandler.printMoveModernization();

        for(Move m : getTemplateDataMovesMod(romhandler)) {
            assertEquals(m, moveList.get(m.number));
        }
        // No Gen 4 changes
        assertNotEquals(romhandler.getMoves().get(364).power, 30);
        
        // Move Modernization list is separate from moves list
        // Verify by randomizing Uproar's power (and every other power even though we don't check)
        while(romhandler.getMoves().get(253).power == 90) {
            romhandler.randomizeMovePowers();
        }
        // Sanity check that we are testing move Id 253 (index 45 in move modifications), which is Uproar)
        assertEquals(getTemplateDataMovesMod(romhandler).get(45).number, 253);
        assertNotEquals(getTemplateDataMovesMod(romhandler).get(45).power, 
                romhandler.getMoves().get(253).power);
        assertEquals(getTemplateDataMovesMod(romhandler).get(45).power, 90);
        assertEquals(getTemplateDataMovesMod(romhandler).size(), 55);
        
        //*****************************************
        // Test Update Moves selection + legacy
        //*****************************************
        romhandler.updateMovesToGen6();
        romhandler.printMoveModernization();

        for(Move m : getTemplateDataMovesMod(romhandler)) {
            assertEquals(m, moveList.get(m.number));
        }
        // No Gen 4 changes
        assertNotEquals(romhandler.getMoves().get(358).power, 70);
        
        // Move Modernization list is separate from moves list
        // Verify by randomizing Heat Waves's power (and every other power even though we don't check)
        while(romhandler.getMoves().get(257).power == 95) {
            romhandler.randomizeMovePowers();
        }
        // Sanity check that we are testing move Id 257 (index 67 in move modifications), which is Heat Wave)
        assertEquals(getTemplateDataMovesMod(romhandler).get(67).number, 257);
        assertNotEquals(getTemplateDataMovesMod(romhandler).get(67).power, 
                romhandler.getMoves().get(257).power);
        assertEquals(getTemplateDataMovesMod(romhandler).get(67).power, 95);
        assertEquals(getTemplateDataMovesMod(romhandler).size(), 85);

        
        //************************************
        // Test Update Moves selection only
        //************************************
        resetDataModel(romhandler, 466);
        romhandler.initMoveModernization();
        romhandler.updateMovesToGen6();
        romhandler.printMoveModernization();

        for(Move m : getTemplateDataMovesMod(romhandler)) {
            assertEquals(m, moveList.get(m.number));
        }
        // No Gen 4 changes
        assertNotEquals(romhandler.getMoves().get(358).power, 70);
        
        // Move Modernization list is separate from moves list
        // Verify by randomizing Heat Wave's power (and every other power even though we don't check)
        while(romhandler.getMoves().get(257).power == 95) {
            romhandler.randomizeMovePowers();
        }
        // Sanity check that we are testing move Id 257 (index 27 in move modifications), which is Heat Wave)
        assertEquals(getTemplateDataMovesMod(romhandler).get(27).number, 257);
        assertNotEquals(getTemplateDataMovesMod(romhandler).get(27).power, 
                romhandler.getMoves().get(257).power);
        assertEquals(getTemplateDataMovesMod(romhandler).get(27).power, 95);
        assertEquals(getTemplateDataMovesMod(romhandler).size(), 38);
    }
    
    @Test
    public void TestGen4MoveModernization() {
        RomHandler romhandler = spy(new Gen4RomHandler(new Random()));
        resetDataModel(romhandler, 558);
        romhandler.initMoveModernization();
        
        //**************************
        // Test legacy selection
        //**************************
        romhandler.updateMovesToGen5();
        romhandler.printMoveModernization();

        for(Move m : getTemplateDataMovesMod(romhandler)) {
            assertEquals(m, moveList.get(m.number));
        }
        
        // Move Modernization list is separate from moves list
        // Verify by randomizing Feint's power (and every other power even though we don't check)
        while(romhandler.getMoves().get(364).power == 30) {
            romhandler.randomizeMovePowers();
        }
        // Sanity check that we are testing move Id 364 (index 55 in move modifications), which is Feint)
        assertEquals(getTemplateDataMovesMod(romhandler).get(55).number, 364);
        assertNotEquals(getTemplateDataMovesMod(romhandler).get(55).power, 
                romhandler.getMoves().get(364).power);
        assertEquals(getTemplateDataMovesMod(romhandler).get(55).power, 30);
        assertEquals(getTemplateDataMovesMod(romhandler).size(), 59);
        
        //*****************************************
        // Test Update Moves selection + legacy
        //*****************************************
        romhandler.updateMovesToGen6();
        romhandler.printMoveModernization();
        
        for(Move m : getTemplateDataMovesMod(romhandler)) {
            assertEquals(m, moveList.get(m.number));
        }
        // No Gen 5 changes
        assertNotEquals(romhandler.getMoves().get(480).power, 60);
        
        // Move Modernization list is separate from moves list
        // Verify by randomizing Wake-Up Slap's power (and every other power even though we don't check)
        while(romhandler.getMoves().get(358).power == 70) {
            romhandler.randomizeMovePowers();
        }
        // Sanity check that we are testing move Id 358 (index 85 in move modifications), which is Wake-Up Slap)
        assertEquals(getTemplateDataMovesMod(romhandler).get(85).number, 358);
        assertNotEquals(getTemplateDataMovesMod(romhandler).get(85).power, 
                romhandler.getMoves().get(358).power);
        assertEquals(getTemplateDataMovesMod(romhandler).get(85).power, 70);
        assertEquals(getTemplateDataMovesMod(romhandler).size(), 102);

        
        //************************************
        // Test Update Moves selection only
        //************************************
        resetDataModel(romhandler, 558);
        romhandler.initMoveModernization();
        romhandler.updateMovesToGen6();
        romhandler.printMoveModernization();

        for(Move m : getTemplateDataMovesMod(romhandler)) {
            assertEquals(m, moveList.get(m.number));
        }
        // No Gen 5 changes
        assertNotEquals(romhandler.getMoves().get(480).power, 60);
        
        // Move Modernization list is separate from moves list
        // Verify by randomizing Wake-Up Slap's power (and every other power even though we don't check)
        while(romhandler.getMoves().get(358).power == 70) {
            romhandler.randomizeMovePowers();
        }
        // Sanity check that we are testing move Id 358 (index 38 in move modifications), which is Wake-Up Slap)
        assertEquals(getTemplateDataMovesMod(romhandler).get(38).number, 358);
        assertNotEquals(getTemplateDataMovesMod(romhandler).get(38).power, 
                romhandler.getMoves().get(358).power);
        assertEquals(getTemplateDataMovesMod(romhandler).get(38).power, 70);
        assertEquals(getTemplateDataMovesMod(romhandler).size(), 52);
    }
    
    @Test
    public void TestGen5MoveModernization() {
        RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
               
        //************************************
        // Test Update Moves selection only
        //************************************
        resetDataModel(romhandler, 999);
        romhandler.initMoveModernization();
        romhandler.updateMovesToGen6();
        romhandler.printMoveModernization();
        for(Move m : getTemplateDataMovesMod(romhandler)) {
            assertEquals(m, moveList.get(m.number));
        }
        
        // Move Modernization list is separate from moves list
        // Verify by randomizing Storm Throw's power (and every other power even though we don't check)
        while(romhandler.getMoves().get(480).power == 60) {
            romhandler.randomizeMovePowers();
        }
        // Sanity check that we are testing move Id 480 (index 52 in move modifications), which is Storm Throw)
        assertEquals(getTemplateDataMovesMod(romhandler).get(52).number, 480);
        assertNotEquals(getTemplateDataMovesMod(romhandler).get(52).power, 
                romhandler.getMoves().get(480).power);
        assertEquals(getTemplateDataMovesMod(romhandler).get(52).power, 60);
        assertEquals(getTemplateDataMovesMod(romhandler).size(), 65);
    }
    
    @Test
    public void TestGen1MiscTweak() {
        Gen1RomHandler romhandler = spy(new Gen1RomHandler(new Random()));
        resetDataModel(romhandler, 250);
        doReturn(Gen1RomHandler.getRomFromSupportedRom("Red (U)")).when(romhandler).getRomEntry();
        doReturn(new int[] {0}).when(romhandler).getPokeNumToRBYTable();
        doNothing().when(romhandler).writeByte(anyInt(), anyByte());
        int mTweaks = romhandler.miscTweaksAvailable();

        //Sanity check
        assertNotEquals(mTweaks, 0);
        try (MockedStatic<com.dabomstew.pkrandom.FileFunctions> mockFileFunctions = mockStatic(com.dabomstew.pkrandom.FileFunctions.class)) {
            for(MiscTweak mt: MiscTweak.allTweaks) {
                romhandler.applyMiscTweak(mt);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        for(MiscTweak mt: MiscTweak.allTweaks) {
            if ((mTweaks & mt.getValue()) > 0) {
                assertEquals(String.format("Failed on %s", mt.getTweakName()), getTemplateDataTweakMap(romhandler).get(mt.getTweakName()), true);
            } else {
                assertNull(getTemplateDataTweakMap(romhandler).get(mt.getTweakName()));
            }
        }
        
    }
    
    @Test
    public void TestGen2MiscTweak() {
        Gen2RomHandler romhandler = spy(new Gen2RomHandler(new Random()));
        resetDataModel(romhandler, 250);
        doReturn(Gen2RomHandler.getRomFromSupportedRom("Silver (U)")).when(romhandler).getRomEntry();
        doNothing().when(romhandler).writeByte(anyInt(), anyByte());
        int mTweaks = romhandler.miscTweaksAvailable();

        //Sanity check
        assertNotEquals(mTweaks, 0);
        try (MockedStatic<com.dabomstew.pkrandom.FileFunctions> mockFileFunctions = mockStatic(com.dabomstew.pkrandom.FileFunctions.class)) {
            for(MiscTweak mt: MiscTweak.allTweaks) {
                romhandler.applyMiscTweak(mt);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        for(MiscTweak mt: MiscTweak.allTweaks) {
            if ((mTweaks & mt.getValue()) > 0) {
                assertEquals(String.format("Failed on %s", mt.getTweakName()), getTemplateDataTweakMap(romhandler).get(mt.getTweakName()), true);
            } else {
                assertNull(getTemplateDataTweakMap(romhandler).get(mt.getTweakName()));
            }
        }
        
    }
    
    @Test
    public void TestGen3MiscTweak() {
        Gen3RomHandler romhandler = spy(new Gen3RomHandler(new Random()));
        resetDataModel(romhandler, 250);
        doReturn(Gen3RomHandler.getRomFromSupportedRom("Ruby (U)")).when(romhandler).getRomEntry();
        doReturn(IntStream.range(0, 649).toArray()).when(romhandler).getPokedexToInternal();
        doNothing().when(romhandler).writeByte(anyInt(), anyByte());
        doNothing().when(romhandler).writeWord(anyInt(), anyInt());
        int mTweaks = romhandler.miscTweaksAvailable();

        //Sanity check
        assertNotEquals(mTweaks, 0);
        try (MockedStatic<com.dabomstew.pkrandom.FileFunctions> mockFileFunctions = mockStatic(com.dabomstew.pkrandom.FileFunctions.class);
             MockedStatic<com.dabomstew.pkrandom.RomFunctions> mockRomFunctions = mockStatic(com.dabomstew.pkrandom.RomFunctions.class)) {
            for(MiscTweak mt: MiscTweak.allTweaks) {
                romhandler.applyMiscTweak(mt);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        for(MiscTweak mt: MiscTweak.allTweaks) {
            // Make sure this tweak is supported, but ignore Nat Dex
            if ((mTweaks & mt.getValue()) > 0 && mt.getValue() != 128) {
                assertEquals(String.format("Failed on %s", mt.getTweakName()), getTemplateDataTweakMap(romhandler).get(mt.getTweakName()), true);
            } else {
                assertNull(getTemplateDataTweakMap(romhandler).get(mt.getTweakName()));
            }
        }
        
        // Check NatDex tweak separately
        // Success mock is too complicated due to numerous dependencies
        assertEquals(romhandler.getTemplateData().get("natDexPatch"), "noDexOffset");
    }
    
    @Test
    public void TestGen4MiscTweak() {
        Gen4RomHandler romhandler = spy(new Gen4RomHandler(new Random()));
        resetDataModel(romhandler, 250);
        doReturn(Gen4RomHandler.getRomFromSupportedRom("Platinum (U)")).when(romhandler).getRomEntry();
        doNothing().when(romhandler).writeLong(any(), anyInt(), anyInt());
        int mTweaks = romhandler.miscTweaksAvailable();

        //Sanity check
        assertNotEquals(mTweaks, 0);
        try (MockedStatic<com.dabomstew.pkrandom.FileFunctions> mockFileFunctions = mockStatic(com.dabomstew.pkrandom.FileFunctions.class);
             MockedStatic<com.dabomstew.pkrandom.RomFunctions> mockRomFunctions = mockStatic(com.dabomstew.pkrandom.RomFunctions.class)) {
            for(MiscTweak mt: MiscTweak.allTweaks) {
                romhandler.applyMiscTweak(mt);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        for(MiscTweak mt: MiscTweak.allTweaks) {
            if ((mTweaks & mt.getValue()) > 0) {
                assertEquals(String.format("Failed on %s", mt.getTweakName()), getTemplateDataTweakMap(romhandler).get(mt.getTweakName()), true);
            } else {
                assertNull(getTemplateDataTweakMap(romhandler).get(mt.getTweakName()));
            }
        }
    }
    
    @Test
    public void TestGen5MiscTweak() {
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        resetDataModel(romhandler, 250);
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
        doNothing().when(romhandler).writeLong(any(), anyInt(), anyInt());
        int mTweaks = romhandler.miscTweaksAvailable();

        //Sanity check
        assertNotEquals(mTweaks, 0);
        try (MockedStatic<com.dabomstew.pkrandom.FileFunctions> mockFileFunctions = mockStatic(com.dabomstew.pkrandom.FileFunctions.class);
             MockedStatic<com.dabomstew.pkrandom.RomFunctions> mockRomFunctions = mockStatic(com.dabomstew.pkrandom.RomFunctions.class)) {
            for(MiscTweak mt: MiscTweak.allTweaks) {
                romhandler.applyMiscTweak(mt);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        for(MiscTweak mt: MiscTweak.allTweaks) {
            // Make sure this tweak is supported, but ignore No Free Lucky Egg (too complicated to mock)
            if ((mTweaks & mt.getValue()) > 0 && mt.getValue() != 8192) {
                assertEquals(String.format("Failed on %s", mt.getTweakName()), getTemplateDataTweakMap(romhandler).get(mt.getTweakName()), true);
            } else {
                assertNull(getTemplateDataTweakMap(romhandler).get(mt.getTweakName()));
            }
        }
    }

    @Test
    public void TestGen1TradeEvo() {
        RomHandler romhandler = spy(new Gen1RomHandler(new Random()));
        resetDataModel(romhandler, 250);
        romhandler.removeTradeEvolutions(true, false);
        assertEquals(((ArrayList)romhandler.getTemplateData().get("removeTradeEvo")).size(), 1);
    }

    @Test
    public void TestGen2TradeEvo() {
        RomHandler romhandler = spy(new Gen2RomHandler(new Random()));
        resetDataModel(romhandler, 250);
        romhandler.removeTradeEvolutions(true, false);
        assertEquals(((ArrayList)romhandler.getTemplateData().get("removeTradeEvo")).size(), 1);
    }

    @Test
    public void TestGen3TradeEvo() {
        try (MockedStatic<com.dabomstew.pkrandom.RomFunctions> mockRomFunctions = mockStatic(com.dabomstew.pkrandom.RomFunctions.class)) {
            Gen3RomHandler romhandler = spy(new Gen3RomHandler(new Random()));
            doReturn(Gen3RomHandler.getRomFromSupportedRom("Emerald (U)")).when(romhandler).getRomEntry();
            resetDataModel(romhandler, 250);
            romhandler.removeTradeEvolutions(true, false);
            assertEquals(((ArrayList)romhandler.getTemplateData().get("removeTradeEvo")).size(), 1);
        }
    }

    @Test
    public void TestGen4TradeEvo() throws IOException {
        Gen4RomHandler romhandler = spy(new Gen4RomHandler(new Random()));
        NARCArchive mock_arch = mock(NARCArchive.class);
        ArrayList<byte[]> mlList = new ArrayList();
        for(int i = 0; i < Gen4Constants.pokemonCount+1; i++) {
            mlList.add(new byte[]{(byte)0xFF, (byte)0xFF});
        }
        doReturn(Gen4RomHandler.getRomFromSupportedRom("Diamond (U)")).when(romhandler).getRomEntry();
        doReturn(mlList).when(mock_arch).getFiles();
        doReturn(mock_arch).when(romhandler).readNARC(anyString());
        resetDataModel(romhandler, 250);
        romhandler.removeTradeEvolutions(true, false);
        assertEquals(((ArrayList)romhandler.getTemplateData().get("removeTradeEvo")).size(), 1);
    }

    @Test
    public void TestGen5TradeEvo() throws IOException {
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        NARCArchive mock_arch = mock(NARCArchive.class);
        ArrayList<byte[]> mlList = new ArrayList();
        for(int i = 0; i < Gen5Constants.pokemonCount+1; i++) {
            mlList.add(new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF});
        }
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
        doReturn(mlList).when(mock_arch).getFiles();
        doReturn(mock_arch).when(romhandler).readNARC(anyString());
        resetDataModel(romhandler, 250);
        romhandler.removeTradeEvolutions(true, false);
        assertEquals(((ArrayList)romhandler.getTemplateData().get("removeTradeEvo")).size(), 1);
    }

    @Test
    public void TestGen1StaticPokemon() {
        Gen1RomHandler romhandler = spy(new Gen1RomHandler(new Random()));
        resetDataModel(romhandler, 250);
        doReturn(Gen1RomHandler.getRomFromSupportedRom("Red (U)")).when(romhandler).getRomEntry();
        doReturn(new int[] {0}).when(romhandler).getPokeRBYToNumTable();
        doReturn(IntStream.range(0, 700).toArray()).when(romhandler).getPokeNumToRBYTable();
        doReturn(0).when(romhandler).readByte(anyInt());
        doNothing().when(romhandler).writeByte(anyInt(), anyByte());
        romhandler.randomizeStaticPokemon(true);
        assertEquals(((TreeMap)romhandler.getTemplateData().get("staticPokemon")).size(), 29);
        romhandler.randomizeStaticPokemon(false);
        assertEquals(((TreeMap)romhandler.getTemplateData().get("staticPokemon")).size(), 29);
    }

    @Test
    public void TestGen2StaticPokemon() {
        Gen2RomHandler romhandler = spy(new Gen2RomHandler(new Random()));
        resetDataModel(romhandler, 250);
        doReturn(Gen2RomHandler.getRomFromSupportedRom("Silver (U)")).when(romhandler).getRomEntry();
        doReturn(new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}).when(romhandler).translateString(anyString());
        doReturn(0).when(romhandler).readByte(anyInt());
        doNothing().when(romhandler).writeByte(anyInt(), anyByte());
        romhandler.randomizeStaticPokemon(true);
        assertEquals(((TreeMap)romhandler.getTemplateData().get("staticPokemon")).size(), 29);
        romhandler.randomizeStaticPokemon(false);
        assertEquals(((TreeMap)romhandler.getTemplateData().get("staticPokemon")).size(), 29);
    }

    @Test
    public void TestGen3StaticPokemon() {
        try (MockedStatic<com.dabomstew.pkrandom.RomFunctions> mockRomFunctions = mockStatic(com.dabomstew.pkrandom.RomFunctions.class)) {
            Gen3RomHandler romhandler = spy(new Gen3RomHandler(new Random()));
            resetDataModel(romhandler, 250);
            doReturn(Gen3RomHandler.getRomFromSupportedRom("Ruby (U)")).when(romhandler).getRomEntry();
            doReturn(0).when(romhandler).readWord(anyInt());
            doReturn(IntStream.range(0, MAX_POKE_COUNT).toArray()).when(romhandler).getPokedexToInternal();
            doReturn(pokemonList.toArray(new Pokemon[pokemonList.size()])).when(romhandler).getPokesInternal();
            doNothing().when(romhandler).writeWord(anyInt(), anyInt());
            romhandler.randomizeStaticPokemon(true);
            assertEquals(((TreeMap)romhandler.getTemplateData().get("staticPokemon")).size(), 18);
            romhandler.randomizeStaticPokemon(false);
            assertEquals(((TreeMap)romhandler.getTemplateData().get("staticPokemon")).size(), 18);
        }
    }

    @Test
    public void TestGen4StaticPokemon() throws IOException {
        Gen4RomHandler romhandler = spy(new Gen4RomHandler(new Random()));
        NARCArchive mock_arch = mock(NARCArchive.class);
        ArrayList<byte[]> mlList = new ArrayList();
        for(int i = 0; i < Gen5Constants.pokemonCount+1; i++) {
            mlList.add(new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF});
        }
        resetDataModel(romhandler, 250);
        doReturn(Gen4RomHandler.getRomFromSupportedRom("Diamond (U)")).when(romhandler).getRomEntry();
        doReturn(0).when(romhandler).readWord(any(), anyInt());
        doReturn(mlList).when(mock_arch).getFiles();
        doReturn(mock_arch).when(romhandler).readNARC(anyString());
        doReturn(mock_arch).when(romhandler).getScriptNARC();
        doNothing().when(romhandler).writeWord(any(), anyInt(), anyInt());
        romhandler.randomizeStaticPokemon(true);
        assertEquals(((TreeMap)romhandler.getTemplateData().get("staticPokemon")).size(), 23);
        romhandler.randomizeStaticPokemon(false);
        assertEquals(((TreeMap)romhandler.getTemplateData().get("staticPokemon")).size(), 23);
    }
    
    @Test
    public void TestGen5StaticPokemon() throws IOException {
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        NARCArchive mock_arch = mock(NARCArchive.class);
        ArrayList<byte[]> mlList = new ArrayList();
        // 877 is the fossil revival pointer. See Gen5_Offsets
        for(int i = 0; i < 878; i++) {
            mlList.add(new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF});
        }
        resetDataModel(romhandler, 250);
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
        doReturn(0).when(romhandler).readWord(any(), anyInt());
        doReturn(mlList).when(mock_arch).getFiles();
        doReturn(mock_arch).when(romhandler).readNARC(anyString());
        doReturn(mock_arch).when(romhandler).getScriptNARC();
        doReturn(mock(Gen5TextHandler.class)).when(romhandler).getTextHandler();
        doNothing().when(romhandler).writeWord(any(), anyInt(), anyInt());
        romhandler.randomizeStaticPokemon(true);
        assertEquals(((TreeMap)romhandler.getTemplateData().get("staticPokemon")).size(), 31);
        romhandler.randomizeStaticPokemon(false);
        assertEquals(((TreeMap)romhandler.getTemplateData().get("staticPokemon")).size(), 31);
    }

    @Test
    public void TestTypeShuffle() {
        RomHandler romhandler = spy(new Gen1RomHandler(new Random()));
        resetDataModel(romhandler, 250);
        romhandler.shufflePokemonTypes();
        assertEquals(romhandler.getTemplateData().get("shuffledTypes"), Type.getShuffledList());
    }

    @Test
    public void TestRandomizedEvolutions() {
        RomHandler romhandler = spy(new Gen1RomHandler(new Random()));
        resetDataModel(romhandler, 250);
        romhandler.randomizeEvolutions(false, false, false, false, false, false, false);
        assertEquals(romhandler.getTemplateData().get("logEvolutions"), true);

    }

    @Test
    public void TestRandomStarter() {
        TestRomHandler romhandler = spy(new TestRomHandler(new Random()));
        resetDataModel(romhandler, 250);
        romhandler.randomStarterPokemon(false, false, false, 999, 0, false, null);
        assertEquals("random", romhandler.getTemplateData().get("logStarters"));
        romhandler.clearStarterPokes();
        romhandler.randomStarterPokemon(false, false, false, 999, 1, false, null);
        assertEquals("1or2evo", romhandler.getTemplateData().get("logStarters"));
        romhandler.clearStarterPokes();
        romhandler.randomStarterPokemon(false, false, false, 999, 2, false, null);
        assertEquals("2evo", romhandler.getTemplateData().get("logStarters"));
    }
    
    @Test
    public void TestGameBreakingMoves() {
        Gen1RomHandler romhandler = spy(new Gen1RomHandler(new Random()));
        doReturn(Gen1RomHandler.getRomFromSupportedRom("Red (U)")).when(romhandler).getRomEntry();
        doReturn(IntStream.range(0, 250).toArray()).when(romhandler).getPokeRBYToNumTable();
        doReturn(IntStream.range(0, 250).toArray()).when(romhandler).getMoveRomToNumTable();
        doReturn(0).when(romhandler).readByte(anyInt());
        doReturn(0).when(romhandler).readWord(anyInt());
        doReturn(0).when(romhandler).calculateOffset(anyInt(), anyInt());
        doNothing().when(romhandler).writeByte(anyInt(), anyByte());
        resetDataModel(romhandler, 250);
        try (MockedStatic<com.dabomstew.pkrandom.FileFunctions> mockFileFunctions = mockStatic(com.dabomstew.pkrandom.FileFunctions.class)) {
            romhandler.randomizeMovesLearnt(false, false, false, 0, 0);
            assertEquals(romhandler.getTemplateData().get("gameBreakingMoves"), false);
            resetDataModel(romhandler, 250);
            romhandler.randomizeMovesLearnt(false, true, false, 0, 0);
            assertEquals(romhandler.getTemplateData().get("gameBreakingMoves"), true);
            resetDataModel(romhandler, 250);
            romhandler.removeBrokenMoves();
            assertEquals(romhandler.getTemplateData().get("gameBreakingMoves"), true);
        }        
    }

    @Test
    public void TestCondensedLevels() {
        RomHandler romhandler = spy(new Gen1RomHandler(new Random()));
        resetDataModel(romhandler, 250);
        romhandler.condenseLevelEvolutions(GlobalConstants.MAXIMUM_EVO_LEVEL, GlobalConstants.MAXIMUM_INTERMEDIATE_EVO_LEVEL);
        assertEquals(((TreeSet)romhandler.getTemplateData().get("condensedEvos")).size(), 1);
    }

    @Test
    public void TestUpdateEffectiveness() {
        Gen1RomHandler romhandler = spy(new Gen1RomHandler(new Random()));
        resetDataModel(romhandler, 250);
        doReturn(Gen1RomHandler.getRomFromSupportedRom("Red (U)")).when(romhandler).getRomEntry();
        doReturn(new int[] {0}).when(romhandler).getPokeNumToRBYTable();
        doNothing().when(romhandler).writeByte(anyInt(), anyByte());
        romhandler.applyMiscTweak(MiscTweak.UPDATE_TYPE_EFFECTIVENESS);
        assertEquals(romhandler.getTemplateData().get("updateEffectiveness"), true);
    }

    @Test
    public void TestTableOfContents() {
        RomHandler romhandler = spy(new Gen1RomHandler(new Random()));
        resetDataModel(romhandler, 250);
        romhandler.generateTableOfContents();
        assertEquals(0, ((ArrayList)romhandler.getTemplateData().get("toc")).size());
        // Spot Check
        romhandler.condenseLevelEvolutions(GlobalConstants.MAXIMUM_EVO_LEVEL, GlobalConstants.MAXIMUM_INTERMEDIATE_EVO_LEVEL);
        romhandler.generateTableOfContents();
        assertArrayEquals(((ArrayList<String[]>)romhandler.getTemplateData().get("toc")).get(0), 
            new String[]{"cle", "Condensed Evos"});
        romhandler.randomStarterPokemon(false, false, false, 999, 0, false, null);
        romhandler.generateTableOfContents();
        assertArrayEquals(((ArrayList<String[]>)romhandler.getTemplateData().get("toc")).get(1), 
            new String[]{"rs", "Starters"});
    }


    /**
     * Function for granular modification of data model
     */
    private void setUp() {
        templateData.clear();
        moveList = spy(ArrayList.class);
        pokemonList = spy(ArrayList.class);
        for(int i = 0; i < 559; i++) {
            Move mv = new Move();
            mv.number = i;
            moveList.add(mv);
        }
        for(int i = 0; i < MAX_POKE_COUNT; i++) {
            Pokemon pk = new Pokemon();
            pk.number = i;
            pk.name = "";
            pk.primaryType = Type.BUG;
            pokemonList.add(pk);
            for (int j = 0; j < i % 2; j++) {
                Evolution ev = new Evolution(pk, new Pokemon(), false, EvolutionType.LEVEL, 1);
                pk.evolutionsFrom.add(ev);
                if (i % 3 == 0) {
                    Evolution ev2 = new Evolution(ev.to, new Pokemon(), false, EvolutionType.LEVEL, 1);
                    ev.to.evolutionsFrom.add(ev2);
                }
            }
        }
        Pokemon evPk = new Pokemon();
        Evolution ev = new Evolution(pokemonList.get(0), evPk, false, EvolutionType.TRADE, 0);
        Evolution ev2 = new Evolution(pokemonList.get(0), evPk, false, EvolutionType.LEVEL, 50);
        pokemonList.get(0).evolutionsFrom.add(ev);
        pokemonList.get(0).evolutionsFrom.add(ev2);
    }
    
    /**
     * Puts data model back to initial form and assigns mock and spy substitutions
     * @param romhandler The RomHandler under test
     * @param moveSize The number to return when queried for move list size (each
     *      generation has a different size move pool which affects which moves are targeted)
     */
    private void resetDataModel(RomHandler romhandler, int moveSize) {
        setUp();        
        doReturn(moveList).when(romhandler).getMoves();
        doReturn(moveSize).when(moveList).size();
        doReturn(pokemonList).when(romhandler).getPokemon();
        doReturn(pokemonList.get(0)).when(romhandler).randomPokemon();
        templateData.put("tweakMap", new HashMap<String, Boolean>());
        romhandler.setTemplate(mock(Template.class), templateData);
    }
    
    /**
     * Encapsulates required casting to maintain type signatures correctly
     * @param romhandler - The RomHandler under test
     * @return The moveMod value in the template data as an ArrayList<Move> 
     */
    private ArrayList<Move> getTemplateDataMovesMod(RomHandler romhandler) {
        return (ArrayList<Move>)romhandler.getTemplateData().get("movesMod");
    }
    
    /**
     * Encapsulates required casting to maintain type signatures correctly
     * @param romhandler - The RomHandler under test
     * @return The tweakMap value in the template data as an HashMap<String, Boolean>
     */
    private HashMap<String, Boolean> getTemplateDataTweakMap(RomHandler romhandler) {
        return (HashMap<String, Boolean>)romhandler.getTemplateData().get("tweakMap");
    }
}
