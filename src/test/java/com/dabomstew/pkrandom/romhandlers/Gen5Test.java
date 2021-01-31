package com.dabomstew.pkrandom.romhandlers;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.dabomstew.pkrandom.constants.Gen5Constants;
import com.dabomstew.pkrandom.newnds.NARCArchive;
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
        romhandler.randomizeTrainerPokes(false, false, false, 0);
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
        romhandler.randomizeTrainerPokes(false, false, false, 0);
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
        romhandler.randomizeTrainerPokes(false, false, false, 0);
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
        romhandler.randomizeTrainerPokes(false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1CasteliaCityBurghTextModifications(isNull());

        // Verify with type theming
        romhandler.typeThemeTrainerPokes(false, false, false, false, false, 0);
        verify(mockTextHandler, times(1)).bw1CasteliaCityBurghTextModifications(isNotNull());
    }

    /**
     * Function for granular modification of data model
     */
    private void setUp() {
        pokemonList = new ArrayList<Pokemon>();
        trainerList = new ArrayList<Trainer>();
        mockTextHandler = mock(Gen5TextHandler.class);
        mockArch = mock(NARCArchive.class);
        mlList = new ArrayList();
        for (int i = 0; i < Gen5Constants.pokemonCount + 1; i++) {
            Pokemon pk = new Pokemon();
            pk.number = i;
            pk.name = "";
            pk.primaryType = Type.values()[i%Type.values().length];
            pokemonList.add(pk);
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
    }
    
}
