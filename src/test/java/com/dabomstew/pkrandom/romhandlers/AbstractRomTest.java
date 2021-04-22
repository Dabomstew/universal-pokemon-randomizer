package com.dabomstew.pkrandom.romhandlers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.dabomstew.pkrandom.constants.Gen5Constants;
import com.dabomstew.pkrandom.constants.GlobalConstants;
import com.dabomstew.pkrandom.exceptions.RandomizationException;
import com.dabomstew.pkrandom.pokemon.Encounter;
import com.dabomstew.pkrandom.pokemon.EncounterSet;
import com.dabomstew.pkrandom.pokemon.Evolution;
import com.dabomstew.pkrandom.pokemon.EvolutionType;
import com.dabomstew.pkrandom.pokemon.GenRestrictions;
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
    ArrayList<EncounterSet> encountersList;

    @Captor
    ArgumentCaptor<ArrayList<Trainer>> trainerCap;

    @Captor
    ArgumentCaptor<ArrayList<Pokemon>> pokemonCap;

    @Captor
    ArgumentCaptor<ArrayList<EncounterSet>> encounterCap;

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
        romhandler.randomizeTrainerPokes(false, false, false, false, false, false, false, true, false, 0);
        ArrayList<Trainer> newTrainers = trainerCap.getValue();
        // Get gym1Type
        for (Trainer t : newTrainers.stream().filter(t -> t.getTag() != null)
            .filter(t -> t.getTag().equals("GYM1")).collect(Collectors.toList())) {
            for (TrainerPokemon tp : t.getPokemon()) {
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
            if (t.getTag() == null) {
                continue;
            }
            // CHILI fights the first starter (index 0)
            if (t.getTag().equals("CHILI")) {
                // Electric-Steel like Magnemite should fight Ground
                assertTrue("No GROUND type found for CHILI", t.getPokemon().stream().anyMatch(
                        tp -> tp.pokemon.primaryType == Type.GROUND || tp.pokemon.secondaryType == Type.GROUND));
                // Find out how many pokemon share a type with GYM1, subtract it from the total
                // number of pokemon on the team, and ensure either 0 or 1 pokemon don't share
                // the type. 0 is possible due to the shared type and trump type
                // being the same or a dual-typed pokemon sharing one from each.
                assertTrue("More than 1 CHILI pokemon did not match the GYM1 type",
                        t.getPokemon().size() - t.getPokemon().stream().filter(tp -> gym1Type.contains(tp.pokemon.primaryType)
                                || gym1Type.contains(tp.pokemon.secondaryType)).count() < 2);
            }
            // CRESS fights the second starter (index 1)
            else if (t.getTag().equals("CRESS")) {
                // Pure Fire type like Ponyta should fight random weakness
                assertTrue("No type found that is in STRONG_AGAINST_FIRE for CRESS",
                        t.getPokemon().stream().anyMatch(tp -> Type.STRONG_AGAINST_FIRE.contains(tp.pokemon.primaryType)
                                || Type.STRONG_AGAINST_FIRE.contains(tp.pokemon.secondaryType)));
                // Find out how many pokemon share a type with GYM1, subtract it from the total
                // number of pokemon on the team, and ensure either 0 or 1 pokemon don't share
                // the type. 0 is possible due to the shared type and trump type
                // being the same or a dual-typed pokemon sharing one from each.
                assertTrue("More than 1 CRESS pokemon did not match the GYM1 type",
                        t.getPokemon().size() - t.getPokemon().stream().filter(tp -> gym1Type.contains(tp.pokemon.primaryType)
                                || gym1Type.contains(tp.pokemon.secondaryType)).count() < 2);
            }
            // CILAN fights the last starter (index 2)
            else if (t.getTag().equals("CILAN")) {
                // Pure Normal type like Rattata should fight Fighting
                assertTrue("No FIGHTING type found for CILAN", t.getPokemon().stream().anyMatch(
                        tp -> tp.pokemon.primaryType == Type.FIGHTING || tp.pokemon.secondaryType == Type.FIGHTING));
                // Find out how many pokemon share a type with GYM1, subtract it from the total
                // number of pokemon on the team, and ensure either 0 or 1 pokemon don't share
                // the type. 0 is possible due to the shared type and trump type
                // being the same or a dual-typed pokemon sharing one from each.
                assertTrue("More than 1 CILAN pokemon did not match the GYM1 type",
                        t.getPokemon().size() - t.getPokemon().stream().filter(tp -> gym1Type.contains(tp.pokemon.primaryType)
                                || gym1Type.contains(tp.pokemon.secondaryType)).count() < 2);
            }
        }
        // **************************
        // Test defensive selection
        // **************************
        romhandler.randomizeTrainerPokes(false, false, false, false, true, false, false, true, false, 0);
        newTrainers = trainerCap.getValue();
        // Get gym1Type
        for (Trainer t : newTrainers.stream().filter(t -> t.getTag() != null)
            .filter(t -> t.getTag().equals("GYM1")).collect(Collectors.toList())) {
            for (TrainerPokemon tp : t.getPokemon()) {
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
            if (t.getTag() == null) {
                continue;
            }
            // CHILI fights the first starter (index 0)
            if (t.getTag().equals("CHILI")) {
                // Electric-Steel like Magnemite should fight Electric
                assertTrue("No ELECTRIC type found for CHILI", t.getPokemon().stream().anyMatch(
                        tp -> tp.pokemon.primaryType == Type.ELECTRIC || tp.pokemon.secondaryType == Type.ELECTRIC));
                // Find out how many pokemon share a type with GYM1, subtract it from the total
                // number of pokemon on the team, and ensure either 0 or 1 pokemon don't share
                // the type. 0 is possible due to the shared type and trump type
                // being the same or a dual-typed pokemon sharing one from each.
                assertTrue("More than 1 CHILI pokemon did not match the GYM1 type",
                        t.getPokemon().size() - t.getPokemon().stream().filter(tp -> gym1Type.contains(tp.pokemon.primaryType)
                                || gym1Type.contains(tp.pokemon.secondaryType)).count() < 2);
            }
            // CRESS fights the second starter (index 1)
            else if (t.getTag().equals("CRESS")) {
                // Pure Fire type like Ponyta should fight random weakness
                assertTrue("No type found that is in RESISTANT_TO_FIRE for CRESS",
                        t.getPokemon().stream().anyMatch(tp -> Type.RESISTANT_TO_FIRE.contains(tp.pokemon.primaryType)
                                || Type.RESISTANT_TO_FIRE.contains(tp.pokemon.secondaryType)));
                // Find out how many pokemon share a type with GYM1, subtract it from the total
                // number of pokemon on the team, and ensure either 0 or 1 pokemon don't share
                // the type. 0 is possible due to the shared type and trump type
                // being the same or a dual-typed pokemon sharing one from each.
                assertTrue("More than 1 CRESS pokemon did not match the GYM1 type",
                        t.getPokemon().size() - t.getPokemon().stream().filter(tp -> gym1Type.contains(tp.pokemon.primaryType)
                                || gym1Type.contains(tp.pokemon.secondaryType)).count() < 2);
            }
            // CILAN fights the last starter (index 2)
            else if (t.getTag().equals("CILAN")) {
                // Pure Normal type like Rattata should fight random weakness
                assertTrue("No type found that is in RESISTANT_TO_NORMAL for CILAN",
                        t.getPokemon().stream().anyMatch(tp -> Type.RESISTANT_TO_NORMAL.contains(tp.pokemon.primaryType)
                                || Type.RESISTANT_TO_NORMAL.contains(tp.pokemon.secondaryType)));
                // Find out how many pokemon share a type with GYM1, subtract it from the total
                // number of pokemon on the team, and ensure either 0 or 1 pokemon don't share
                // the type. 0 is possible due to the shared type and trump type
                // being the same or a dual-typed pokemon sharing one from each.
                assertTrue("More than 1 CILAN pokemon did not match the GYM1 type",
                        t.getPokemon().size() - t.getPokemon().stream().filter(tp -> gym1Type.contains(tp.pokemon.primaryType)
                                || gym1Type.contains(tp.pokemon.secondaryType)).count() < 2);
            }
        }
    }

    /**
     * Black/White Cilan/Chili/Cress use a type that is either offensively superior
     * or defensively resistant to the starter of the player
     * This should also adhere to global swap rules
     */
    @Test
    public void TestGlobalSwapCilanChiliCressTrumpStarter() {
        // Magnemite should fight ground offense or electric defense
        // Ponyta should return a random element from the Fire listing
        // All 3 have 1 pokemon with a shared type
        HashMap<Pokemon, Pokemon> pokemonSwap = new HashMap<Pokemon, Pokemon>(Gen5Constants.pokemonCount + 1);
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
        ArrayList<Trainer> originalTrainer;

        // **************************
        // Test offensive selection
        // **************************
        originalTrainer = new ArrayList();
        for(Trainer t : romhandler.getTrainers()) {
            originalTrainer.add(new Trainer(t));
        }
        romhandler.randomizeTrainerPokes(false, false, false, false, false, false, true, true, false, 0);
        ArrayList<Trainer> newTrainers = trainerCap.getValue();
        // Get gym1Type
        for (Trainer t : newTrainers.stream().filter(t -> t.getTag() != null)
            .filter(t -> t.getTag().equals("GYM1")).collect(Collectors.toList())) {
            for (TrainerPokemon tp : t.getPokemon()) {
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
        // Reverse order so tagged trainers go last
        for (int i = newTrainers.size() - 1; i >= 0 ; i--) {
            Trainer newT = newTrainers.get(i);
            Trainer oldT = originalTrainer.get(i);
            assertTrue("Trainer did not match name. Make sure the trainer list is ordered the same",
                newT.getName().equals(oldT.getName()));
            if (newT.getTag() != null) {
                // CHILI fights the first starter (index 0)
                if (newT.getTag().equals("CHILI")) {
                    // Electric-Steel like Magnemite should fight Ground
                    assertTrue("No GROUND type found for CHILI", newT.getPokemon().stream().anyMatch(
                            tp -> tp.pokemon.primaryType == Type.GROUND || tp.pokemon.secondaryType == Type.GROUND));
                    // Find out how many pokemon share a type with GYM1, subtract it from the total
                    // number of pokemon on the team, and ensure either 0 or 1 pokemon don't share
                    // the type. 0 is possible due to the shared type and trump type
                    // being the same or a dual-typed pokemon sharing one from each.
                    assertTrue("More than 1 CHILI pokemon did not match the GYM1 type",
                            newT.getPokemon().size() - newT.getPokemon().stream().filter(tp -> gym1Type.contains(tp.pokemon.primaryType)
                                    || gym1Type.contains(tp.pokemon.secondaryType)).count() < 2);
                }
                // CRESS fights the second starter (index 1)
                else if (newT.getTag().equals("CRESS")) {
                    // Pure Fire type like Ponyta should fight random weakness
                    assertTrue("No type found that is in STRONG_AGAINST_FIRE for CRESS",
                            newT.getPokemon().stream().anyMatch(tp -> Type.STRONG_AGAINST_FIRE.contains(tp.pokemon.primaryType)
                                    || Type.STRONG_AGAINST_FIRE.contains(tp.pokemon.secondaryType)));
                    // Find out how many pokemon share a type with GYM1, subtract it from the total
                    // number of pokemon on the team, and ensure either 0 or 1 pokemon don't share
                    // the type. 0 is possible due to the shared type and trump type
                    // being the same or a dual-typed pokemon sharing one from each.
                    assertTrue("More than 1 CRESS pokemon did not match the GYM1 type",
                            newT.getPokemon().size() - newT.getPokemon().stream().filter(tp -> gym1Type.contains(tp.pokemon.primaryType)
                                    || gym1Type.contains(tp.pokemon.secondaryType)).count() < 2);
                }
                // CILAN fights the last starter (index 2)
                else if (newT.getTag().equals("CILAN")) {
                    // Pure Normal type like Rattata should fight Fighting
                    assertTrue("No FIGHTING type found for CILAN", newT.getPokemon().stream().anyMatch(
                            tp -> tp.pokemon.primaryType == Type.FIGHTING || tp.pokemon.secondaryType == Type.FIGHTING));
                    // Find out how many pokemon share a type with GYM1, subtract it from the total
                    // number of pokemon on the team, and ensure either 0 or 1 pokemon don't share
                    // the type. 0 is possible due to the shared type and trump type
                    // being the same or a dual-typed pokemon sharing one from each.
                    assertTrue("More than 1 CILAN pokemon did not match the GYM1 type",
                            newT.getPokemon().size() - newT.getPokemon().stream().filter(tp -> gym1Type.contains(tp.pokemon.primaryType)
                                    || gym1Type.contains(tp.pokemon.secondaryType)).count() < 2);
                }
            }
            Collections.sort(newT.getPokemon(), (o1, o2) -> o1.getNickname().compareTo(o2.getNickname()));
            Collections.sort(oldT.getPokemon(), (o1, o2) -> o1.getNickname().compareTo(o2.getNickname()));
            for (int j = 0; j < newT.getPokemon().size(); j++) {
                TrainerPokemon newTp = newT.getPokemon().get(j);
                TrainerPokemon oldTp = oldT.getPokemon().get(j);
                // Initialize the set or check the value
                if (pokemonSwap.containsKey(oldTp.pokemon)) {
                    Pokemon cached = pokemonSwap.get(oldTp.pokemon);
                    if (newT.getTag() == null) {
                        assertTrue("Pokemon did not match the replacement - " +
                            oldTp.pokemon.number + " gave " + cached.number + " but newTp was " +
                            newTp.pokemon.number,
                            cached.equals(newTp.pokemon));
                    } else {
                        // Only tagged teams can ignore global swap
                        switch (newT.getTag()) {
                            case "GYM1":
                                assertTrue("Pokemon did not match the replacement - " +
                                oldTp.pokemon.number + " gave " + cached.number + " but newTp was " +
                                newTp.pokemon.number + " and type did not match GYM1",
                                cached.equals(newTp.pokemon) || gym1Type.contains(newTp.pokemon.primaryType)
                                || gym1Type.contains(newTp.pokemon.secondaryType));
                                break;
                            case "CHILI":
                                assertTrue("Pokemon did not match the replacement - " +
                                oldTp.pokemon.number + " gave " + cached.number + " but newTp was " +
                                newTp.pokemon.number + " and type did not match GYM1 or GROUND",
                                cached.equals(newTp.pokemon) || gym1Type.contains(newTp.pokemon.primaryType)
                                || gym1Type.contains(newTp.pokemon.secondaryType) 
                                || newTp.pokemon.primaryType == Type.GROUND || newTp.pokemon.secondaryType == Type.GROUND);
                                break;
                            case "CRESS":
                                assertTrue("Pokemon did not match the replacement - " +
                                oldTp.pokemon.number + " gave " + cached.number + " but newTp was " +
                                newTp.pokemon.number + " and type did not match GYM1 or STRONG_AGAINST_FIRE",
                                cached.equals(newTp.pokemon) || gym1Type.contains(newTp.pokemon.primaryType)
                                || gym1Type.contains(newTp.pokemon.secondaryType) 
                                || Type.STRONG_AGAINST_FIRE.contains(newTp.pokemon.primaryType)
                                || Type.STRONG_AGAINST_FIRE.contains(newTp.pokemon.secondaryType));
                                break;
                            case "CILAN":
                                assertTrue("Pokemon did not match the replacement - " +
                                oldTp.pokemon.number + " gave " + cached.number + " but newTp was " +
                                newTp.pokemon.number + " and type did not match GYM1 or FIGHTING",
                                cached.equals(newTp.pokemon) || gym1Type.contains(newTp.pokemon.primaryType)
                                || gym1Type.contains(newTp.pokemon.secondaryType) 
                                || newTp.pokemon.primaryType == Type.FIGHTING 
                                || newTp.pokemon.secondaryType == Type.FIGHTING);
                                break;
                        }
                    }
                } else {
                    pokemonSwap.put(oldTp.pokemon, newTp.pokemon);
                }
            } 
        }
        // **************************
        // Test defensive selection
        // **************************
        pokemonSwap = new HashMap<Pokemon, Pokemon>(Gen5Constants.pokemonCount + 1);
        originalTrainer = new ArrayList();
        for(Trainer t : romhandler.getTrainers()) {
            originalTrainer.add(new Trainer(t));
        }
        romhandler.randomizeTrainerPokes(false, false, false, false, true, false, true, true, false, 0);
        newTrainers = trainerCap.getValue();
        // Get gym1Type
        for (Trainer t : newTrainers.stream().filter(t -> t.getTag() != null)
            .filter(t -> t.getTag().equals("GYM1")).collect(Collectors.toList())) {
            for (TrainerPokemon tp : t.getPokemon()) {
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
        // Reverse order so tagged trainers go last
        for (int i = newTrainers.size() - 1; i >= 0; i--) {
            Trainer newT = newTrainers.get(i);
            Trainer oldT = originalTrainer.get(i);
            assertTrue("Trainer did not match name. Make sure the trainer list is ordered the same",
                newT.getName().equals(oldT.getName()));
            if (newT.getTag() != null) {
                // CHILI fights the first starter (index 0)
                if (newT.getTag().equals("CHILI")) {
                    // Electric-Steel like Magnemite should fight Electric
                    assertTrue("No ELECTRIC type found for CHILI", newT.getPokemon().stream().anyMatch(
                            tp -> tp.pokemon.primaryType == Type.ELECTRIC || tp.pokemon.secondaryType == Type.ELECTRIC));
                    // Find out how many pokemon share a type with GYM1, subtract it from the total
                    // number of pokemon on the team, and ensure either 0 or 1 pokemon don't share
                    // the type. 0 is possible due to the shared type and trump type
                    // being the same or a dual-typed pokemon sharing one from each.
                    assertTrue("More than 1 CHILI pokemon did not match the GYM1 type",
                            newT.getPokemon().size() - newT.getPokemon().stream().filter(tp -> gym1Type.contains(tp.pokemon.primaryType)
                                    || gym1Type.contains(tp.pokemon.secondaryType)).count() < 2);
                }
                // CRESS fights the second starter (index 1)
                else if (newT.getTag().equals("CRESS")) {
                    // Pure Fire type like Ponyta should fight random weakness
                    assertTrue("No type found that is in RESISTANT_TO_FIRE for CRESS",
                            newT.getPokemon().stream().anyMatch(tp -> Type.RESISTANT_TO_FIRE.contains(tp.pokemon.primaryType)
                                    || Type.RESISTANT_TO_FIRE.contains(tp.pokemon.secondaryType)));
                    // Find out how many pokemon share a type with GYM1, subtract it from the total
                    // number of pokemon on the team, and ensure either 0 or 1 pokemon don't share
                    // the type. 0 is possible due to the shared type and trump type
                    // being the same or a dual-typed pokemon sharing one from each.
                    assertTrue("More than 1 CRESS pokemon did not match the GYM1 type",
                            newT.getPokemon().size() - newT.getPokemon().stream().filter(tp -> gym1Type.contains(tp.pokemon.primaryType)
                                    || gym1Type.contains(tp.pokemon.secondaryType)).count() < 2);
                }
                // CILAN fights the last starter (index 2)
                else if (newT.getTag().equals("CILAN")) {
                    // Pure Normal type like Rattata should fight random weakness
                    assertTrue("No type found that is in RESISTANT_TO_NORMAL for CILAN",
                            newT.getPokemon().stream().anyMatch(tp -> Type.RESISTANT_TO_NORMAL.contains(tp.pokemon.primaryType)
                                    || Type.RESISTANT_TO_NORMAL.contains(tp.pokemon.secondaryType)));
                    // Find out how many pokemon share a type with GYM1, subtract it from the total
                    // number of pokemon on the team, and ensure either 0 or 1 pokemon don't share
                    // the type. 0 is possible due to the shared type and trump type
                    // being the same or a dual-typed pokemon sharing one from each.
                    assertTrue("More than 1 CILAN pokemon did not match the GYM1 type",
                            newT.getPokemon().size() - newT.getPokemon().stream().filter(tp -> gym1Type.contains(tp.pokemon.primaryType)
                                    || gym1Type.contains(tp.pokemon.secondaryType)).count() < 2);
                }
            }
            Collections.sort(newT.getPokemon(), (o1, o2) -> o1.getNickname().compareTo(o2.getNickname()));
            Collections.sort(oldT.getPokemon(), (o1, o2) -> o1.getNickname().compareTo(o2.getNickname()));
            for (int j = 0; j < newT.getPokemon().size(); j++) {
                TrainerPokemon newTp = newT.getPokemon().get(j);
                TrainerPokemon oldTp = oldT.getPokemon().get(j);
                // Initialize the set or check the value
                if (pokemonSwap.containsKey(oldTp.pokemon)) {
                    Pokemon cached = pokemonSwap.get(oldTp.pokemon);
                    if (newT.getTag() == null) {
                        assertTrue("Pokemon did not match the replacement - " +
                            oldTp.pokemon.number + " gave " + cached.number + " but newTp was " +
                            newTp.pokemon.number,
                            cached.equals(newTp.pokemon));
                    } else {
                        // Only tagged teams can ignore global swap
                        switch (newT.getTag()) {
                            case "GYM1":
                                assertTrue("Pokemon did not match the replacement - " +
                                oldTp.pokemon.number + " gave " + cached.number + " but newTp was " +
                                newTp.pokemon.number + " and type did not match GYM1",
                                cached.equals(newTp.pokemon) || gym1Type.contains(newTp.pokemon.primaryType)
                                || gym1Type.contains(newTp.pokemon.secondaryType));
                                break;
                            case "CHILI":
                                assertTrue("Pokemon did not match the replacement - " +
                                oldTp.pokemon.number + " gave " + cached.number + " but newTp was " +
                                newTp.pokemon.number + " and type did not match GYM1 or ELECTRIC",
                                cached.equals(newTp.pokemon) || gym1Type.contains(newTp.pokemon.primaryType)
                                || gym1Type.contains(newTp.pokemon.secondaryType) 
                                || newTp.pokemon.primaryType == Type.ELECTRIC || newTp.pokemon.secondaryType == Type.ELECTRIC);
                                break;
                            case "CRESS":
                                assertTrue("Pokemon did not match the replacement - " +
                                oldTp.pokemon.number + " gave " + cached.number + " but newTp was " +
                                newTp.pokemon.number + " and type did not match GYM1 or RESISTANT_TO_FIRE",
                                cached.equals(newTp.pokemon) || gym1Type.contains(newTp.pokemon.primaryType)
                                || gym1Type.contains(newTp.pokemon.secondaryType) 
                                || Type.RESISTANT_TO_FIRE.contains(newTp.pokemon.primaryType)
                                || Type.RESISTANT_TO_FIRE.contains(newTp.pokemon.secondaryType));
                                break;
                            case "CILAN":
                                assertTrue("Pokemon did not match the replacement - " +
                                oldTp.pokemon.number + " gave " + cached.number + " but newTp was " +
                                newTp.pokemon.number + " and type did not match GYM1 or RESISTANT_TO_NORMAL",
                                cached.equals(newTp.pokemon) || gym1Type.contains(newTp.pokemon.primaryType)
                                || gym1Type.contains(newTp.pokemon.secondaryType) 
                                || Type.RESISTANT_TO_NORMAL.contains(newTp.pokemon.primaryType)
                                || Type.RESISTANT_TO_NORMAL.contains(newTp.pokemon.secondaryType));
                                break;
                        }
                    }
                } else {
                    pokemonSwap.put(oldTp.pokemon, newTp.pokemon);
                }
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
        ArrayList<Type> weaknessTypes = new ArrayList<Type>();
        Type.STRONG_AGAINST.get(startersList.get(1).primaryType.ordinal()).forEach(t -> {
            // Get the list of weaknesses for that type and add them to acceptable types
            weaknessTypes.addAll(Type.STRONG_AGAINST.get(t.ordinal()));
        });

        weaknessTypes.addAll(Type.STRONG_AGAINST_ROCK);
        assertTrue("Grass monkey replacement type was not found in weakness list for starter 1",
            !Collections.disjoint(weaknessTypes, types));
        
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
        romhandler.randomizeTrainerPokes(false, false, false, false, false, false, false, true, false, 0);
        romhandler.randomizeStaticPokemon(false);
        Map<String, Type> taggedTypes = romhandler.getTaggedGroupTypes();

        // Grass monkey (first element in static list)
        // Thus the first replacement in the capture
        pkmn = pokemonCap.getValue().get(0);

        // Gather both types of the replacement into a list
        types = new ArrayList<Type>();
        types.add(pkmn.primaryType);
        if (pkmn.secondaryType != null) {
            types.add(pkmn.secondaryType);
        }

        // Add in all weaknesses of CRESS
        Type cressType = taggedTypes.get("CRESS");
        ArrayList<Type> acceptableTypes = new ArrayList<Type>();
        acceptableTypes.addAll(Type.STRONG_AGAINST.get(cressType.ordinal()));
        assertTrue("Grass monkey replacement type " + pkmn.primaryType + "/" 
        + pkmn.secondaryType  + " was not found in weakness list for CRESS " + cressType.camelCase(),
            acceptableTypes.contains(pkmn.primaryType) || acceptableTypes.contains(pkmn.secondaryType));

        // Fire monkey (third element in the static list)
        // Thus the third replacement in the capture
        pkmn = pokemonCap.getValue().get(2);

        // Gather both types of the replacement into a list
        types = new ArrayList<Type>();
        types.add(pkmn.primaryType);
        if (pkmn.secondaryType != null) {
            types.add(pkmn.secondaryType);
        }

        // Add in all weakneses of CILAN
        Type cilanType = taggedTypes.get("CILAN");
        acceptableTypes = new ArrayList<Type>();
        acceptableTypes.addAll(Type.STRONG_AGAINST.get(cilanType.ordinal()));
        assertTrue("Fire monkey replacement type " + pkmn.primaryType + "/" 
        + pkmn.secondaryType  + " was not found in weakness list for CILAN " + cilanType.camelCase(),
            acceptableTypes.contains(pkmn.primaryType) || acceptableTypes.contains(pkmn.secondaryType));

        // Water monkey (fifth element in the static list)
        // Thus the fifth replacement in the capture
        pkmn = pokemonCap.getValue().get(4);

        // Gather both types of the replacement into a list
        types = new ArrayList<Type>();
        types.add(pkmn.primaryType);
        if (pkmn.secondaryType != null) {
            types.add(pkmn.secondaryType);
        }

        // Add in all weakneses of CHILI
        Type chiliType = taggedTypes.get("CHILI");
        acceptableTypes = new ArrayList<Type>();
        acceptableTypes.addAll(Type.STRONG_AGAINST.get(chiliType.ordinal()));
        assertTrue("Water monkey replacement type " + pkmn.primaryType + "/" 
        + pkmn.secondaryType  + " was not found in weakness list for CHILI " + chiliType.camelCase(),
            acceptableTypes.contains(pkmn.primaryType) || acceptableTypes.contains(pkmn.secondaryType));

        //**************************
        // Test Striaton defense selection
        //**************************
        romhandler.randomizeTrainerPokes(false, false, false, false, true, false, false, true, false, 0);
        romhandler.randomizeStaticPokemon(false);
        taggedTypes = romhandler.getTaggedGroupTypes();


        // Grass monkey (first element in static list)
        // Thus the first replacement in the capture
        pkmn = pokemonCap.getValue().get(0);

        // Gather both types of the replacement into a list
        types = new ArrayList<Type>();
        types.add(pkmn.primaryType);
        if (pkmn.secondaryType != null) {
            types.add(pkmn.secondaryType);
        }

        // Add in all weaknesses of CRESS
        cressType = taggedTypes.get("CRESS");
        acceptableTypes = new ArrayList<Type>();
        acceptableTypes.addAll(Type.STRONG_AGAINST.get(cressType.ordinal()));
        assertTrue("Grass monkey replacement type " + pkmn.primaryType + "/" 
        + pkmn.secondaryType  + " was not found in weakness list for CRESS " + cressType.camelCase(),
            acceptableTypes.contains(pkmn.primaryType) || acceptableTypes.contains(pkmn.secondaryType));

        // Fire monkey (third element in the static list)
        // Thus the third replacement in the capture
        pkmn = pokemonCap.getValue().get(2);

        // Gather both types of the replacement into a list
        types = new ArrayList<Type>();
        types.add(pkmn.primaryType);
        if (pkmn.secondaryType != null) {
            types.add(pkmn.secondaryType);
        }

        // Add in all weakneses of CILAN
        cilanType = taggedTypes.get("CILAN");
        acceptableTypes = new ArrayList<Type>();
        acceptableTypes.addAll(Type.STRONG_AGAINST.get(cilanType.ordinal()));
        assertTrue("Fire monkey replacement type " + pkmn.primaryType + "/" 
        + pkmn.secondaryType  + " was not found in weakness list for CILAN " + cilanType.camelCase(),
            acceptableTypes.contains(pkmn.primaryType) || acceptableTypes.contains(pkmn.secondaryType));

        // Water monkey (fifth element in the static list)
        // Thus the fifth replacement in the capture
        pkmn = pokemonCap.getValue().get(4);

        // Gather both types of the replacement into a list
        types = new ArrayList<Type>();
        types.add(pkmn.primaryType);
        if (pkmn.secondaryType != null) {
            types.add(pkmn.secondaryType);
        }

        // Add in all weakneses of CHILI
        chiliType = taggedTypes.get("CHILI");
        acceptableTypes = new ArrayList<Type>();
        acceptableTypes.addAll(Type.STRONG_AGAINST.get(chiliType.ordinal()));
        assertTrue("Water monkey replacement type " + pkmn.primaryType + "/" 
        + pkmn.secondaryType  + " was not found in weakness list for CHILI " + chiliType.camelCase(),
            acceptableTypes.contains(pkmn.primaryType) || acceptableTypes.contains(pkmn.secondaryType));
    }

    @Test
    public void TestMinimumEvos() {
        TestRomHandler romhandler = spy(new TestRomHandler(new Random()));
        resetDataModel(romhandler);
        romhandler.randomStarterPokemon(false, false, false, 999, 0, false, null);
        boolean pokemonWithZeroEvo = false, pokemonWithOneEvo = false, pokemonWithTwoEvo = false;
        for(Pokemon pk : romhandler.getStarterPokes()) {
            int evoLength = romhandler.evolutionChainSize(pk);
            if (evoLength == 1) {
                pokemonWithZeroEvo = true;
            } else if (evoLength == 2) {
                pokemonWithOneEvo = true;
            } else if (evoLength > 2) {
                pokemonWithTwoEvo = true;
            }

            // End loop if we have all the conditions met
            if (pokemonWithOneEvo && pokemonWithTwoEvo && pokemonWithZeroEvo) {
                break;
            }
        }
        assertTrue("Matches should be true: zeroEvo - " + pokemonWithZeroEvo + ", oneEvo - " + pokemonWithOneEvo
            + ", twoEvo - " + pokemonWithTwoEvo, pokemonWithZeroEvo && pokemonWithOneEvo && pokemonWithTwoEvo);
        pokemonWithZeroEvo = false;
        pokemonWithOneEvo = false;
        pokemonWithTwoEvo = false;
        romhandler.clearStarterPokes();
        romhandler.randomStarterPokemon(false, false, false, 999, 1, false, null);
        for(Pokemon pk : romhandler.getStarterPokes()) {
            int evoLength = romhandler.evolutionChainSize(pk);
            if (evoLength == 1) {
                pokemonWithZeroEvo = true;
            } else if (evoLength == 2) {
                pokemonWithOneEvo = true;
            } else if (evoLength > 2) {
                pokemonWithTwoEvo = true;
            }
        }
        assertTrue("Matches should be false: zeroEvo - " + pokemonWithZeroEvo 
            + "\nMatches should be true: oneEvo - " + pokemonWithOneEvo
            + ", twoEvo - " + pokemonWithTwoEvo, !pokemonWithZeroEvo && pokemonWithOneEvo && pokemonWithTwoEvo);
        pokemonWithZeroEvo = false;
        pokemonWithOneEvo = false;
        pokemonWithTwoEvo = false;
        romhandler.clearStarterPokes();
        romhandler.randomStarterPokemon(false, false, false, 999, 2, false, null);
        for(Pokemon pk : romhandler.getStarterPokes()) {
            int evoLength = romhandler.evolutionChainSize(pk);
            if (evoLength == 1) {
                pokemonWithZeroEvo = true;
            } else if (evoLength == 2) {
                pokemonWithOneEvo = true;
            } else if (evoLength > 2) {
                pokemonWithTwoEvo = true;
            }
        }
        assertTrue("Matches should be false: zeroEvo - " + pokemonWithZeroEvo + ", oneEvo - " + pokemonWithOneEvo
            + "\nMatches should be true: twoEvo - " + pokemonWithTwoEvo, 
            !pokemonWithZeroEvo && !pokemonWithOneEvo && pokemonWithTwoEvo);
    }

    @Test
    public void TestExactEvos() {
        TestRomHandler romhandler = spy(new TestRomHandler(new Random()));
        resetDataModel(romhandler);
        romhandler.randomStarterPokemon(false, false, false, 999, 0, true, null);
        boolean pokemonWithZeroEvo = false, pokemonWithOneEvo = false, pokemonWithTwoEvo = false;
        for(Pokemon pk : romhandler.getStarterPokes()) {
            int evoLength = romhandler.evolutionChainSize(pk);
            if (evoLength == 1) {
                pokemonWithZeroEvo = true;
            } else if (evoLength == 2) {
                pokemonWithOneEvo = true;
            } else if (evoLength > 2) {
                pokemonWithTwoEvo = true;
            }
        }
        assertTrue("Matches should be true: zeroEvo - " + pokemonWithZeroEvo + 
            "\nMatches should be false: oneEvo - " + pokemonWithOneEvo
            + ", twoEvo - " + pokemonWithTwoEvo, pokemonWithZeroEvo && !pokemonWithOneEvo && !pokemonWithTwoEvo);
        pokemonWithZeroEvo = false;
        pokemonWithOneEvo = false;
        pokemonWithTwoEvo = false;
        romhandler.clearStarterPokes();
        romhandler.randomStarterPokemon(false, false, false, 999, 1, true, null);
        for(Pokemon pk : romhandler.getStarterPokes()) {
            int evoLength = romhandler.evolutionChainSize(pk);
            if (evoLength == 1) {
                pokemonWithZeroEvo = true;
            } else if (evoLength == 2) {
                pokemonWithOneEvo = true;
            } else if (evoLength > 2) {
                pokemonWithTwoEvo = true;
            }
        }
        assertTrue("Matches should be false: zeroEvo - " + pokemonWithZeroEvo + ", twoEvo - " + pokemonWithTwoEvo
        + "\nMatches should be true: oneEvo - " + pokemonWithOneEvo, !pokemonWithZeroEvo && pokemonWithOneEvo && !pokemonWithTwoEvo);
        pokemonWithZeroEvo = false;
        pokemonWithOneEvo = false;
        pokemonWithTwoEvo = false;
        boolean pokemonWithMoreThanTwoEvo = false;
        romhandler.clearStarterPokes();
        romhandler.randomStarterPokemon(false, false, false, 999, 2, true, null);
        for(Pokemon pk : romhandler.getStarterPokes()) {
            int evoLength = romhandler.evolutionChainSize(pk);
            if (evoLength == 1) {
                pokemonWithZeroEvo = true;
            } else if (evoLength == 2) {
                pokemonWithOneEvo = true;
            } else if (evoLength == 3) {
                pokemonWithTwoEvo = true;
            } else if (evoLength > 3) {
                pokemonWithMoreThanTwoEvo = true;
            }
        }
        assertTrue("Matches should be false: zeroEvo - " + pokemonWithZeroEvo + ", oneEvo - " + pokemonWithOneEvo
            + ", moreThanTwo - " + pokemonWithMoreThanTwoEvo
            + "\nMatches should be true: twoEvo - " + pokemonWithTwoEvo, 
            !pokemonWithZeroEvo && !pokemonWithOneEvo && pokemonWithTwoEvo && !pokemonWithMoreThanTwoEvo);
    }

    /**
     * All trainers are type themed
     */
    @Test
    public void TestTypeTheme() {
        HashSet<Type> trainerType = new HashSet<Type>();
        TestRomHandler romhandler = spy(new TestRomHandler(new Random()));
        resetDataModel(romhandler);
        romhandler.randomizeTrainerPokes(false, false, false, false, false, true, false, false, false, 0);
        for (Trainer t : romhandler.getTrainers()) {
            for (TrainerPokemon tp : t.getPokemon()) {
                // Initialize the set
                if (trainerType.size() == 0) {
                    trainerType.add(tp.pokemon.primaryType);
                    if (tp.pokemon.secondaryType != null) {
                        trainerType.add(tp.pokemon.secondaryType);
                    }
                }
                // Only keep the shared type
                else {
                    HashSet<Type> intersect = new HashSet<Type>();
                    intersect.add(tp.pokemon.primaryType);
                    if (tp.pokemon.secondaryType != null) {
                        intersect.add(tp.pokemon.secondaryType);
                    }
                    trainerType.retainAll(intersect);
                }
            }
            assertTrue("More than 2 types found - " + Arrays.toString(trainerType.toArray()),
                trainerType.size() < 3);
        }
    }

    /**
     * Only tagged trainers (like GYM, UBER) are type themed
     */
    @Test
    public void TestGymTypeTheme() {
        TestRomHandler romhandler = spy(new TestRomHandler(new Random()));
        resetDataModel(romhandler);
        HashSet<Type> trainerType;
        romhandler.randomizeTrainerPokes(false, false, false, false, false, false, false, true, false, 0);
        for (Trainer t : romhandler.getTrainers()) {
            // Skip anyone that is not tagged
            // Skip CHILI, CRESS, CILAN due to unique requirements to have different types
            if (t.getTag() != null && !Arrays.asList("CHILI", "CRESS", "CILAN").contains(t.getTag())) {
                trainerType = new HashSet<Type>();
                for (TrainerPokemon tp : t.getPokemon()) {
                    // Initialize the set
                    if (trainerType.size() == 0) {
                        trainerType.add(tp.pokemon.primaryType);
                        if (tp.pokemon.secondaryType != null) {
                            trainerType.add(tp.pokemon.secondaryType);
                        }
                    }
                    // Only keep the shared type
                    else {
                        HashSet<Type> intersect = new HashSet<Type>();
                        intersect.add(tp.pokemon.primaryType);
                        if (tp.pokemon.secondaryType != null) {
                            intersect.add(tp.pokemon.secondaryType);
                        }
                        trainerType.retainAll(intersect);
                    }
                }
                // Test for 2 since there could be only 1 pokemon with 2 types, or all pokemon
                // share the same 2 types even though the gym only requires 1 of those types
                assertTrue("More than 2 types found - " + Arrays.toString(trainerType.toArray()),
                    trainerType.size() < 3);

                // Test to make sure at least 1 type was found
                assertTrue("Less than 1 type found - " + Arrays.toString(trainerType.toArray()),
                trainerType.size() > 0);
            }
        }
    }

    /**
     * Test that starters are filtered by type when a type is provided, or ignores type filtering
     * when the types argument is null
     */
    @Test
    public void TestStartersTypeRestriction() {
        TestRomHandler romhandler = spy(new TestRomHandler(new Random()));
        resetDataModel(romhandler);
        // Test null first
        romhandler.randomStarterPokemon(false, false, false, 999, 0, false, null);
        assertTrue("Starters list did not contain all available pokemon", 
            // Subtract due to randomStarterPokemon doing a pop operation and reducing by 1
            romhandler.getStarterPokes().size() == pokemonList.size()-1);
        
        // Test with 1 type
        romhandler.clearStarterPokes();
        romhandler.randomStarterPokemon(false, false, false, 999, 0, false, Arrays.asList(Type.FIRE));
        assertTrue("Starters list did not contain only FIRE types", 
            romhandler.getStarterPokes().stream().allMatch(pk -> pk.primaryType == Type.FIRE || pk.secondaryType == Type.FIRE));

        // Test with 3 types
        romhandler.clearStarterPokes();
        ArrayList<Type> typesArr = new ArrayList<Type>(Arrays.asList(Type.FIRE, Type.BUG, Type.DARK));
        romhandler.randomStarterPokemon(false, false, false, 999, 0, false, typesArr);
        assertTrue("Starters list did not contain only FIRE, BUG, and DARK types",
            romhandler.getStarterPokes().stream().allMatch(pk -> typesArr.contains(pk.primaryType) || typesArr.contains(pk.secondaryType)));
    }

    /**
     * Pokemon are always replaced with the same thing
     */
    @Test
    public void TestGlobalSwap() {
        HashMap<Pokemon, Pokemon> pokemonSwap = new HashMap<Pokemon, Pokemon>(Gen5Constants.pokemonCount + 1);
        TestRomHandler romhandler = spy(new TestRomHandler(new Random()));
        resetDataModel(romhandler);
        doNothing().when(romhandler).setTrainers(trainerCap.capture());
        ArrayList<Trainer> originalTrainer = new ArrayList();
        for(Trainer t : romhandler.getTrainers()) {
            originalTrainer.add(new Trainer(t));
        }
        romhandler.randomizeTrainerPokes(false, false, false, false, false, false, true, false, false, 0);
        for (int i = 0; i < trainerCap.getValue().size(); i++) {
            Trainer newT = trainerCap.getValue().get(i);
            Trainer oldT = originalTrainer.get(i);
            assertTrue("Trainer did not match name. Make sure the trainer list is ordered the same",
                newT.getName().equals(oldT.getName()));
            Collections.sort(newT.getPokemon(), (o1, o2) -> o1.getNickname().compareTo(o2.getNickname()));
            Collections.sort(oldT.getPokemon(), (o1, o2) -> o1.getNickname().compareTo(o2.getNickname()));
            for (int j = 0; j < newT.getPokemon().size(); j++) {
                TrainerPokemon newTp = newT.getPokemon().get(j);
                TrainerPokemon oldTp = oldT.getPokemon().get(j);
                // Initialize the set
                if (pokemonSwap.containsKey(oldTp.pokemon)) {
                    Pokemon cached = pokemonSwap.get(oldTp.pokemon);
                    assertTrue("Pokemon did not match the replacement - " +
                        oldTp.pokemon.number + " gave " + cached.number + " but newTp was " +
                        newTp.pokemon.number,
                        cached.equals(newTp.pokemon));
                } else {
                    pokemonSwap.put(oldTp.pokemon, newTp.pokemon);
                }
            }
        }
    }

    /**
     * Pokemon are always replaced with the same thing
     * Gyms are stil appropriately type themed
     */
    @Test
    public void TestGlobalSwapGymTypeTheme() {
        HashMap<Pokemon, Pokemon> pokemonSwap = new HashMap<Pokemon, Pokemon>(Gen5Constants.pokemonCount + 1);
        TestRomHandler romhandler = spy(new TestRomHandler(new Random()));
        resetDataModel(romhandler);
        doNothing().when(romhandler).setTrainers(trainerCap.capture());
        ArrayList<Trainer> originalTrainer = new ArrayList();
        HashSet<Type> trainerType = null;
        for(Trainer t : romhandler.getTrainers()) {
            originalTrainer.add(new Trainer(t));
        }
        romhandler.randomizeTrainerPokes(false, false, false, false, false, false, true, true, false, 0);
        // Reverse order so tagged trainers are last, preventing them from caching the wrong pokemon
        for (int i = trainerCap.getValue().size()-1; i >= 0 ; i--) {
            Trainer newT = trainerCap.getValue().get(i);
            Trainer oldT = originalTrainer.get(i);
            assertTrue("Trainer did not match name. Make sure the trainer list is ordered the same",
            newT.getName().equals(oldT.getName()));
            // Skip CHILI, CRESS, CILAN due to unique requirements to have different types
            if (Arrays.asList("CHILI", "CRESS", "CILAN").contains(newT.getTag())) {
                continue;
            }
            // Only tagged trainers are required to have a single type
            // Everyone else is just required to obey the global swap
            if (newT.getTag() != null) {
                trainerType = new HashSet<Type>();
                for (TrainerPokemon tp : newT.getPokemon()) {
                    // Initialize the set
                    if (trainerType.size() == 0) {
                        trainerType.add(tp.pokemon.primaryType);
                        if (tp.pokemon.secondaryType != null) {
                            trainerType.add(tp.pokemon.secondaryType);
                        }
                    }
                    // Only keep the shared type
                    else {
                        HashSet<Type> intersect = new HashSet<Type>();
                        intersect.add(tp.pokemon.primaryType);
                        if (tp.pokemon.secondaryType != null) {
                            intersect.add(tp.pokemon.secondaryType);
                        }
                        trainerType.retainAll(intersect);
                    }
                }
                // Test for 2 since there could be only 1 pokemon with 2 types, or all pokemon
                // share the same 2 types even though the gym only requires 1 of those types
                assertTrue("More than 2 types found - " + Arrays.toString(trainerType.toArray()),
                    trainerType.size() < 3);
                
                // Test to make sure at least 1 type was found
                assertTrue("Less than 1 type found - " + Arrays.toString(trainerType.toArray()),
                trainerType.size() > 0);
            }
            Collections.sort(newT.getPokemon(), (o1, o2) -> o1.getNickname().compareTo(o2.getNickname()));
            Collections.sort(oldT.getPokemon(), (o1, o2) -> o1.getNickname().compareTo(o2.getNickname()));
            for (int j = 0; j < newT.getPokemon().size(); j++) {
                TrainerPokemon newTp = newT.getPokemon().get(j);
                TrainerPokemon oldTp = oldT.getPokemon().get(j);
                // Initialize the set or check the value
                if (pokemonSwap.containsKey(oldTp.pokemon)) {
                    Pokemon cached = pokemonSwap.get(oldTp.pokemon);
                    if (newT.getTag() == null) {
                        assertTrue("Pokemon did not match the replacement - " +
                            oldTp.pokemon.number + " gave " + cached.number + " but newTp was " +
                            newTp.pokemon.number,
                            cached.equals(newTp.pokemon));
                    } else {
                        // Verify trainerType has been initialized
                        assertFalse("Tagged trainer " + newT.getTag() + " did not have types initialized",
                            trainerType == null);
                        // Only tagged teams get permission to override global swap
                        assertTrue("Pokemon did not match the replacement - " +
                        oldTp.pokemon.number + " gave " + cached.number + " but newTp was " +
                        newTp.pokemon.number + " and type was not found",
                        cached.equals(newTp.pokemon) || trainerType.contains(newTp.pokemon.primaryType)
                        || trainerType.contains(newTp.pokemon.secondaryType));
                    }
                } else {
                    pokemonSwap.put(oldTp.pokemon, newTp.pokemon);
                }
            }            
        }
    }

    /**
     * If a GenRestrictions is given such that no pokemon exist in the main list
     * at the end, it should throw an exception and stop execution
     */
    @Test(expected=RandomizationException.class)
    public void TestGenRestrictionsThrowsException() {
        TestRomHandler romhandler = spy(new TestRomHandler(new Random()));
        resetDataModel(romhandler);
        GenRestrictions restrictions = new GenRestrictions();
        romhandler.setPokemonPool(restrictions);
    }

    /**
     * For valid permutations of GenRestrictions, Game1To1Encounters
     * should not set any encounter to null and encounters must be
     * appropriate for the generations allowed
     */
    @Test
    public void TestGenRestrictionGame1To1Encounters() {
        TestRomHandler romhandler = spy(new TestRomHandler(new Random()));
        resetDataModel(romhandler);
        doNothing().when(romhandler).setEncounters(anyBoolean(), encounterCap.capture());
        GenRestrictions restrictions = new GenRestrictions();
        ArrayList<Integer> allowedNumbers = new ArrayList();
        ArrayList<Integer> gen1Numbers = new ArrayList<Integer>(
            IntStream.range(1, 152).boxed().collect(Collectors.toList()));
        ArrayList<Integer> gen2Numbers = new ArrayList<Integer>(
            IntStream.range(152, 252).boxed().collect(Collectors.toList()));
        ArrayList<Integer> gen3Numbers = new ArrayList<Integer>(
            IntStream.range(252, 387).boxed().collect(Collectors.toList()));
        ArrayList<Integer> gen4Numbers = new ArrayList<Integer>(
            IntStream.range(387, 494).boxed().collect(Collectors.toList()));
        ArrayList<Integer> gen5Numbers = new ArrayList<Integer>(
            IntStream.range(494, 650).boxed().collect(Collectors.toList()));
        for(int a = 0; a < 2; a++) {
            restrictions.allow_gen1 = 1 - a == 0 ? true : false;
            for(int b = 0; b < 2; b++) {
                restrictions.allow_gen2 = 1 - b == 0 ? true : false;
                for(int c = 0; c < 2; c++) {
                    restrictions.allow_gen3 = 1 - c == 0 ? true : false;
                    for(int d = 0; d < 2; d++) {
                        restrictions.allow_gen4 = 1 - d == 0 ? true : false;
                        for(int e = 0; e < 2; e++) {
                            restrictions.allow_gen5 = 1 - e == 0 ? true : false;
                            // If everything is 0, then we have all false
                            // This throws an exception, which is not desirable
                            // So we skip this condition
                            if (a+b+c+d+e == 0) {
                                continue;
                            }
                            if (restrictions.allow_gen1) {
                                allowedNumbers.addAll(gen1Numbers);
                            } else {
                                allowedNumbers.removeAll(gen1Numbers);
                            }
                            if (restrictions.allow_gen2) {
                                allowedNumbers.addAll(gen2Numbers);
                            } else {
                                allowedNumbers.removeAll(gen2Numbers);
                            }
                            if (restrictions.allow_gen3) {
                                allowedNumbers.addAll(gen3Numbers);
                            } else {
                                allowedNumbers.removeAll(gen3Numbers);
                            }
                            if (restrictions.allow_gen4) {
                                allowedNumbers.addAll(gen4Numbers);
                            } else {
                                allowedNumbers.removeAll(gen4Numbers);
                            }
                            if (restrictions.allow_gen5) {
                                allowedNumbers.addAll(gen5Numbers);
                            } else {
                                allowedNumbers.removeAll(gen5Numbers);
                            }
                            romhandler.setPokemonPool(restrictions);
                            romhandler.game1to1Encounters(false, false, true);
                            for (EncounterSet es : encounterCap.getValue()) {
                                for (Encounter enc : es.getEncounters()) {
                                    assertFalse("A null encounter was found", enc.getPokemon() == null);
                                    assertTrue(enc.getPokemon().getNumber() + " was found with these restrictions " +
                                        restrictions,
                                        allowedNumbers.contains(enc.getPokemon().getNumber()));
                                }
                            }
                            romhandler.game1to1Encounters(false, false, false);
                            for (EncounterSet es : encounterCap.getValue()) {
                                for (Encounter enc : es.getEncounters()) {
                                    assertFalse("A null encounter was found", enc.getPokemon() == null);
                                    assertTrue(enc.getPokemon().getNumber() + " was found with these restrictions " +
                                        restrictions,
                                        allowedNumbers.contains(enc.getPokemon().getNumber()));
                                }
                            }
                        }
                    }            
                }            
            }            
        }
    }

    /**
     * Function for granular modification of data model
     */
    private void setUp() {
        pokemonList = new ArrayList();
        trainerList = new ArrayList();
        startersList = new ArrayList();
        encountersList = new ArrayList();

        for(int i = 0; i < Gen5Constants.pokemonCount + 1; i++) {
            Pokemon pk = new Pokemon();
            pk.number = i;
            pk.name = "";
            pk.primaryType = Type.values()[i%17];
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
        for(String tag: new String[]{"GYM1", "CILAN", "CHILI", "CRESS"}) {
            Trainer t = new Trainer();
            t.setTag(tag);
            t.setName(tag);
            t.setOffset(trainerList.size());
            while (t.getPokemon().size() < 2) {
                TrainerPokemon tp = new TrainerPokemon();
                tp.pokemon = pokemonList.get(t.getPokemon().size());
                tp.setNickname("number"+t.getPokemon().size());
                t.getPokemon().add(tp);
            }
            trainerList.add(t);
        }
        while (trainerList.size() < 100) {
            Trainer t = new Trainer();
            t.setName("generic"+trainerList.size());
            t.setOffset(trainerList.size());
            while (t.getPokemon().size() < 2) {
                TrainerPokemon tp = new TrainerPokemon();
                tp.pokemon = pokemonList.get(new Random().nextInt(pokemonList.size()));
                tp.setNickname("number"+t.getPokemon().size());
                t.getPokemon().add(tp);
            }
            trainerList.add(t);
        }
        for(int i = 0; i < 3; i++) {
            startersList.add(new Pokemon());
        }
        
        for (int i = 0; i < 5; i++) {
            EncounterSet es = new EncounterSet();
            Encounter e1 = new Encounter();
            e1.setPokemon(pokemonList.get(new Random().nextInt(pokemonList.size())));
            es.setEncounters(Arrays.asList(e1));
            encountersList.add(es);
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
        doReturn(encountersList).when(romhandler).getEncounters(anyBoolean());
        doReturn(mock(Map.class)).when(romhandler).getTemplateData();
    }
}
