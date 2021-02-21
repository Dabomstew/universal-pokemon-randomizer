package com.dabomstew.pkrandom.romhandlers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.dabomstew.pkrandom.constants.Gen5Constants;
import com.dabomstew.pkrandom.constants.GlobalConstants;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.TrainerPokemon;
import com.dabomstew.pkrandom.pokemon.Type;
import com.dabomstew.pkrandom.text.Gen5TextHandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import freemarker.template.Template;

public class AbstractRomTest {

    ArrayList<Pokemon> pokemonList;
    ArrayList<Trainer> trainerList;
    ArrayList<Pokemon> startersList;

    @Captor
    ArgumentCaptor<ArrayList<Trainer>> trainerCap;

    @Captor
    ArgumentCaptor<ArrayList<Pokemon>> pokemonCap;

    /**
     * Initializes any annotated mockito objects
     */
    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Black/White Cilan/Chili/Cress use a type that is either offensively superior
     * or defensively resistant to the starter of the player
     */
    @Test
    public void TestCilanChiliCressTrumpStarter() {
        // Magnemite should fight ground offense or electric defense
        // Ponyta should return a random element from the Fire listing
        // All 3 have 1 pokemon with a shared type
        HashSet<Type> gym1Type = new HashSet<Type>();
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        resetDataModel(romhandler);
        startersList.get(0).primaryType = Type.ELECTRIC;
        startersList.get(0).secondaryType = Type.STEEL;
        startersList.get(1).primaryType = Type.FIRE;
        startersList.get(2).primaryType = Type.NORMAL;
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
        doReturn(mock(Gen5TextHandler.class)).when(romhandler).getTextHandler();
        doNothing().when(romhandler).setTrainers(trainerCap.capture());

        // **************************
        // Test offensive selection
        // **************************
        romhandler.typeThemeTrainerPokes(false, false, false, false, false, 0);
        ArrayList<Trainer> newTrainers = trainerCap.getValue();
        // Get gym1Type
        for (Trainer t : newTrainers.stream().filter(t -> t.tag.equals("GYM1")).collect(Collectors.toList())) {
            for (TrainerPokemon tp : t.pokemon) {
                // Initialize the set
                if (gym1Type.size() == 0) {
                    gym1Type.add(tp.pokemon.primaryType);
                    if (tp.pokemon.secondaryType != null) {
                        gym1Type.add(tp.pokemon.secondaryType);
                    }
                }
                // Only keep the shared type
                else {
                    HashSet<Type> intersect = new HashSet<Type>();
                    intersect.add(tp.pokemon.primaryType);
                    if (tp.pokemon.secondaryType != null) {
                        intersect.add(tp.pokemon.secondaryType);
                    }
                    gym1Type.retainAll(intersect);
                }
            }
        }

        // Check CHILI, CRESS, CILAN against starters
        // Check CHILI, CRESS, CILAN share 1 type with GYM1
        for (Trainer t : newTrainers) {
            // CHILI fights the first starter (index 0)
            if (t.tag.equals("CHILI")) {
                // Electric-Steel like Magnemite should fight Ground
                assertTrue("No GROUND type found for CHILI", t.pokemon.stream().anyMatch(
                        tp -> tp.pokemon.primaryType == Type.GROUND || tp.pokemon.secondaryType == Type.GROUND));
                // Find out how many pokemon share a type with GYM1, subtract it from the total
                // number of pokemon on the team, and ensure either 0 or 1 pokemon don't share
                // the type. 0 is possible due to the shared type and trump type
                // being the same or a dual-typed pokemon sharing one from each.
                assertTrue("More than 1 CHILI pokemon did not match the GYM1 type",
                        t.pokemon.size() - t.pokemon.stream().filter(tp -> gym1Type.contains(tp.pokemon.primaryType)
                                || gym1Type.contains(tp.pokemon.secondaryType)).count() < 2);
            }
            // CRESS fights the second starter (index 1)
            else if (t.tag.equals("CRESS")) {
                // Pure Fire type like Ponyta should fight random weakness
                assertTrue("No type found that is in STRONG_AGAINST_FIRE for CRESS",
                        t.pokemon.stream().anyMatch(tp -> Type.STRONG_AGAINST_FIRE.contains(tp.pokemon.primaryType)
                                || Type.STRONG_AGAINST_FIRE.contains(tp.pokemon.secondaryType)));
                // Find out how many pokemon share a type with GYM1, subtract it from the total
                // number of pokemon on the team, and ensure either 0 or 1 pokemon don't share
                // the type. 0 is possible due to the shared type and trump type
                // being the same or a dual-typed pokemon sharing one from each.
                assertTrue("More than 1 CRESS pokemon did not match the GYM1 type",
                        t.pokemon.size() - t.pokemon.stream().filter(tp -> gym1Type.contains(tp.pokemon.primaryType)
                                || gym1Type.contains(tp.pokemon.secondaryType)).count() < 2);
            }
            // CILAN fights the last starter (index 2)
            else if (t.tag.equals("CILAN")) {
                // Pure Normal type like Rattata should fight Fighting
                assertTrue("No FIGHTING type found for CILAN", t.pokemon.stream().anyMatch(
                        tp -> tp.pokemon.primaryType == Type.FIGHTING || tp.pokemon.secondaryType == Type.FIGHTING));
                // Find out how many pokemon share a type with GYM1, subtract it from the total
                // number of pokemon on the team, and ensure either 0 or 1 pokemon don't share
                // the type. 0 is possible due to the shared type and trump type
                // being the same or a dual-typed pokemon sharing one from each.
                assertTrue("More than 1 CILAN pokemon did not match the GYM1 type",
                        t.pokemon.size() - t.pokemon.stream().filter(tp -> gym1Type.contains(tp.pokemon.primaryType)
                                || gym1Type.contains(tp.pokemon.secondaryType)).count() < 2);
            }
        }
        // **************************
        // Test defensive selection
        // **************************
        romhandler.typeThemeTrainerPokes(false, false, false, false, true, 0);
        newTrainers = trainerCap.getValue();
        // Get gym1Type
        for (Trainer t : newTrainers.stream().filter(t -> t.tag.equals("GYM1")).collect(Collectors.toList())) {
            for (TrainerPokemon tp : t.pokemon) {
                // Initialize the set
                if (gym1Type.size() == 0) {
                    gym1Type.add(tp.pokemon.primaryType);
                    if (tp.pokemon.secondaryType != null) {
                        gym1Type.add(tp.pokemon.secondaryType);
                    }
                }
                // Only keep the shared type
                else {
                    HashSet<Type> intersect = new HashSet<Type>();
                    intersect.add(tp.pokemon.primaryType);
                    if (tp.pokemon.secondaryType != null) {
                        intersect.add(tp.pokemon.secondaryType);
                    }
                    gym1Type.retainAll(intersect);
                }
            }
        }

        // Check CHILI, CRESS, CILAN against starters
        // Check CHILI, CRESS, CILAN share 1 type with GYM1
        for (Trainer t : newTrainers) {
            // CHILI fights the first starter (index 0)
            if (t.tag.equals("CHILI")) {
                // Electric-Steel like Magnemite should fight Electric
                assertTrue("No ELECTRIC type found for CHILI", t.pokemon.stream().anyMatch(
                        tp -> tp.pokemon.primaryType == Type.ELECTRIC || tp.pokemon.secondaryType == Type.ELECTRIC));
                // Find out how many pokemon share a type with GYM1, subtract it from the total
                // number of pokemon on the team, and ensure either 0 or 1 pokemon don't share
                // the type. 0 is possible due to the shared type and trump type
                // being the same or a dual-typed pokemon sharing one from each.
                assertTrue("More than 1 CHILI pokemon did not match the GYM1 type",
                        t.pokemon.size() - t.pokemon.stream().filter(tp -> gym1Type.contains(tp.pokemon.primaryType)
                                || gym1Type.contains(tp.pokemon.secondaryType)).count() < 2);
            }
            // CRESS fights the second starter (index 1)
            else if (t.tag.equals("CRESS")) {
                // Pure Fire type like Ponyta should fight random weakness
                assertTrue("No type found that is in RESISTANT_TO_FIRE for CRESS",
                        t.pokemon.stream().anyMatch(tp -> Type.RESISTANT_TO_FIRE.contains(tp.pokemon.primaryType)
                                || Type.RESISTANT_TO_FIRE.contains(tp.pokemon.secondaryType)));
                // Find out how many pokemon share a type with GYM1, subtract it from the total
                // number of pokemon on the team, and ensure either 0 or 1 pokemon don't share
                // the type. 0 is possible due to the shared type and trump type
                // being the same or a dual-typed pokemon sharing one from each.
                assertTrue("More than 1 CRESS pokemon did not match the GYM1 type",
                        t.pokemon.size() - t.pokemon.stream().filter(tp -> gym1Type.contains(tp.pokemon.primaryType)
                                || gym1Type.contains(tp.pokemon.secondaryType)).count() < 2);
            }
            // CILAN fights the last starter (index 2)
            else if (t.tag.equals("CILAN")) {
                // Pure Normal type like Rattata should fight random weakness
                assertTrue("No type found that is in RESISTANT_TO_NORMAL for CILAN",
                        t.pokemon.stream().anyMatch(tp -> Type.RESISTANT_TO_NORMAL.contains(tp.pokemon.primaryType)
                                || Type.RESISTANT_TO_NORMAL.contains(tp.pokemon.secondaryType)));
                // Find out how many pokemon share a type with GYM1, subtract it from the total
                // number of pokemon on the team, and ensure either 0 or 1 pokemon don't share
                // the type. 0 is possible due to the shared type and trump type
                // being the same or a dual-typed pokemon sharing one from each.
                assertTrue("More than 1 CILAN pokemon did not match the GYM1 type",
                        t.pokemon.size() - t.pokemon.stream().filter(tp -> gym1Type.contains(tp.pokemon.primaryType)
                                || gym1Type.contains(tp.pokemon.secondaryType)).count() < 2);
            }
        }
    }

    /**
     * Check that negative abilities like Normalize are removed
     */
    @Test
    public void TestNegativeAbilityRemoved() {
        RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        resetDataModel(romhandler);
        romhandler.randomizeAbilities(false, false, false, true);
        for (Pokemon p : pokemonList) {
            assertFalse("" + p.getAbility1() + " was in negativeAbilities, but still found",
                    GlobalConstants.negativeAbilities.contains(p.getAbility1()));
            assertFalse("" + p.getAbility2() + " was in negativeAbilities, but still found",
                    GlobalConstants.negativeAbilities.contains(p.getAbility2()));
        }
    }

    /**
     * When replacing statics, the elemental monkies should either be a type that is
     * superior to the Striaton gym, or covers a weakness of the starter
     * 
     * @throws IOException
     */
    @Test
    public void TestMonkeyStaticReplacementCoversWeakness() throws IOException {
        Gen5RomHandler romhandler = spy(new Gen5RomHandler(new Random()));
        resetDataModel(romhandler);
        startersList.get(0).primaryType = Type.ELECTRIC;
        startersList.get(0).secondaryType = Type.STEEL;
        startersList.get(1).primaryType = Type.FIRE;
        startersList.get(2).primaryType = Type.NORMAL;
        doReturn(Gen5RomHandler.getRomFromSupportedRom("Black (U)")).when(romhandler).getRomEntry();
        doReturn(mock(Gen5TextHandler.class)).when(romhandler).getTextHandler();
        doReturn(pokemonList.subList(511, 516)).when(romhandler).getStaticPokemon();
        doNothing().when(romhandler).setTrainers(trainerCap.capture());
        doReturn(true).when(romhandler).setStaticPokemon(pokemonCap.capture());
        romhandler.setTemplate(mock(Template.class), mock(Map.class));

        //**************************
        // Test general selection
        //**************************
        romhandler.randomizeStaticPokemon(false);
    
        // Grass monkey (first element in static list)
        // Thus the first replacement in the capture
        Pokemon pkmn = pokemonCap.getValue().get(0);

        // Gather both types of the replacement into a list
        ArrayList<Type> types = new ArrayList<Type>();
        types.add(pkmn.primaryType);
        if (pkmn.secondaryType != null) {
            types.add(pkmn.secondaryType);
        }

        // This one needs to cover a random fire type weakness, which gives us a wide selection
        ArrayList<Type> acceptableTypes = new ArrayList<Type>();
        Type.STRONG_AGAINST.get(startersList.get(1).primaryType.ordinal()).forEach(t -> {
            // Get the list of weaknesses for that type and add them to acceptable types
            acceptableTypes.addAll(Type.STRONG_AGAINST.get(t.ordinal()));
        });

        acceptableTypes.addAll(Type.STRONG_AGAINST_ROCK);
        assertTrue("Grass monkey replacement type was not found in weakness list for starter 1",
            !Collections.disjoint(acceptableTypes, types));
        
        // Fire monkey (third element in the static list)
        // Thus the third replacement in the capture
        pkmn = pokemonCap.getValue().get(2);

        // Gather both types of the replacement into a list
        types = new ArrayList<Type>();
        types.add(pkmn.primaryType);
        if (pkmn.secondaryType != null) {
            types.add(pkmn.secondaryType);
        }

        // Check if the weakness list for FIGHTING (which is the only thing Normal type is weak to) includes 
        // at least ine of the replacement types
        assertTrue("Fire monkey replacement type was not found in weakness list for starter 2",
            !Collections.disjoint(Type.STRONG_AGAINST_FIGHTING, types));

        // Water monkey (fifth element in the static list)
        // Thus the fifth replacement in the capture
        pkmn = pokemonCap.getValue().get(4);

        // Gather both types of the replacement into a list
        types = new ArrayList<Type>();
        types.add(pkmn.primaryType);
        if (pkmn.secondaryType != null) {
            types.add(pkmn.secondaryType);
        }

        // Check if the weakness list for GROUND (which is the only shared weakness for Electric and Steel) includes
        // at least one of the replacement's types
        assertTrue("Water monkey replacement type was not found in weakness list for starter 0",
            !Collections.disjoint(Type.STRONG_AGAINST_GROUND, types));


        //**************************
        // Test Striaton offense selection
        //**************************
        romhandler.typeThemeTrainerPokes(false, false, false, false, false, 0);
        romhandler.randomizeStaticPokemon(false);
        Map<String, Type> taggedTypes = romhandler.getTaggedGroupTypes();

        // TODO: Replace for loop with specific pokemonCap entry (same as above)
        // Current implementation is skipped as no replacement has the same number
        for (Pokemon pk : pokemonCap.getValue()) {
            // Grass monkey (starter 1)
            if (pk.number == 511) {
                Type cressType = taggedTypes.get("CRESS");
                assertTrue("Grass monkey replacement type " + pk.primaryType.camelCase() + "/" 
                + pk.secondaryType  + " was not found in weakness list for CRESS " + cressType.camelCase(),
                    Type.STRONG_AGAINST.get(cressType.ordinal()).contains(pk.primaryType) ||
                    Type.STRONG_AGAINST.get(cressType.ordinal()).contains(pk.secondaryType));
            }
            // Fire monkey (starter 2)
            else if (pk.number == 513) {
                Type cilanType = taggedTypes.get("CILAN");
                assertTrue("Fire monkey replacement type " + pk.primaryType.camelCase() + "/" 
                + pk.secondaryType  + " was not found in weakness list for CILAN " + cilanType.camelCase(),
                    Type.STRONG_AGAINST.get(cilanType.ordinal()).contains(pk.primaryType) ||
                    Type.STRONG_AGAINST.get(cilanType.ordinal()).contains(pk.secondaryType));
            }
            // Water monkey (starter 0)
            else if (pk.number == 515) {
                Type chiliType = taggedTypes.get("CHILI");
                assertTrue("Water monkey replacement type " + pk.primaryType.camelCase() + "/" 
                + pk.secondaryType  + " was not found in weakness list for CHILI " + chiliType.camelCase(),
                    Type.STRONG_AGAINST.get(chiliType.ordinal()).contains(pk.primaryType) ||
                    Type.STRONG_AGAINST.get(chiliType.ordinal()).contains(pk.secondaryType));
            }
        }

        //**************************
        // Test Striaton defense selection
        //**************************
        romhandler.typeThemeTrainerPokes(false, false, false, false, true, 0);
        romhandler.randomizeStaticPokemon(false);
        taggedTypes = romhandler.getTaggedGroupTypes();


        for (Pokemon pk : pokemonCap.getValue()) {
            // Grass monkey (starter 1)
            if (pk.number == 511) {
                Type cressType = taggedTypes.get("CRESS");
                assertTrue("Grass monkey replacement type " + pk.primaryType.camelCase() + "/" 
                + pk.secondaryType  + " was not found in weakness list for CRESS " + cressType.camelCase(),
                    Type.STRONG_AGAINST.get(cressType.ordinal()).contains(pk.primaryType) ||
                    Type.STRONG_AGAINST.get(cressType.ordinal()).contains(pk.secondaryType));
            }
            // Fire monkey (starter 2)
            else if (pk.number == 513) {
                Type cilanType = taggedTypes.get("CILAN");
                assertTrue("Fire monkey replacement type " + pk.primaryType.camelCase() + "/" 
                + pk.secondaryType  + " was not found in weakness list for CILAN " + cilanType.camelCase(),
                    Type.STRONG_AGAINST.get(cilanType.ordinal()).contains(pk.primaryType) ||
                    Type.STRONG_AGAINST.get(cilanType.ordinal()).contains(pk.secondaryType));
            }
            // Water monkey (starter 0)
            else if (pk.number == 515) {
                Type chiliType = taggedTypes.get("CHILI");
                assertTrue("Water monkey replacement type " + pk.primaryType.camelCase() + "/" 
                + pk.secondaryType  + " was not found in weakness list for CHILI " + chiliType.camelCase(),
                    Type.STRONG_AGAINST.get(chiliType.ordinal()).contains(pk.primaryType) ||
                    Type.STRONG_AGAINST.get(chiliType.ordinal()).contains(pk.secondaryType));
            }
        }
    }
    

    /**
     * Function for granular modification of data model
     */
    private void setUp() {
        pokemonList = spy(ArrayList.class);
        trainerList = spy(ArrayList.class);
        startersList = spy(ArrayList.class);
        for(int i = 0; i < Gen5Constants.pokemonCount + 1; i++) {
            Pokemon pk = new Pokemon();
            pk.number = i;
            pk.name = "";
            pk.primaryType = Type.values()[i%17];
            pokemonList.add(pk);
        }
        for(String tag: new String[]{"GYM1", "CILAN", "CHILI", "CRESS"}) {
            Trainer t = new Trainer();
            t.tag = tag;
            t.pokemon.add(new TrainerPokemon());
            t.pokemon.add(new TrainerPokemon());
            trainerList.add(t);
        }
        for(int i = 0; i < 3; i++) {
            startersList.add(new Pokemon());
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
        doReturn(trainerList).when(romhandler).getTrainers();
        doReturn(startersList).when(romhandler).getStarters();
    }
}
