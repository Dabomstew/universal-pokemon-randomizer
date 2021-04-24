package com.dabomstew.pkrandom.gui;

import static org.junit.Assert.*;
import static org.awaitility.Awaitility.await;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import com.dabomstew.pkrandom.MiscTweak;
import com.dabomstew.pkrandom.pokemon.Type;
import com.dabomstew.pkrandom.settings.Settings;

import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JCheckBoxFixture;
import org.assertj.swing.fixture.JListFixture;
import org.assertj.swing.fixture.JRadioButtonFixture;
import org.assertj.swing.fixture.JSliderFixture;
import org.junit.Test;

public class SettingsTest extends AbstractUIBase {

    /**
     * Toggling Gen 6 does not toggle Gen 5
     * Gen 5 available without Gen 6
     * 
     * @throws IOException
     */
    @Test(timeout = 4000)
    public void TestGen5Separation() throws IOException {
        JCheckBoxFixture updateMovesCBFixture = getCheckBoxByName("goUpdateMovesCheckBox");
        JCheckBoxFixture updateMovesLegacyCBFixture = getCheckBoxByName("goUpdateMovesLegacyCheckBox");
        Settings settings = this.mainWindow.getCurrentSettings();
        // Sanity check - Should initialize to False
        assertFalse("Update Moves started as selected", settings.isUpdateMoves());
        assertFalse("Update Moves Legacy started as selected", settings.isUpdateMovesLegacy());
        // Sanity check - Should not fail with 0 options
        String setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertFalse("Update Moves was selected after reloading settings 0", settings.isUpdateMoves());
        assertFalse("Update Moves Legacy was selected after reloading settings 0", settings.isUpdateMovesLegacy());

        // Toggle Gen 5
        updateMovesLegacyCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertFalse("Update Moves was selected even though it was not clicked", settings.isUpdateMoves());
        assertTrue("Update Moves Legacy was not selected even though it was clicked", settings.isUpdateMovesLegacy());
        assertTrue("Update Moves is disabled but was expected to be enabled", updateMovesCBFixture.requireVisible().isEnabled());
        assertTrue("Update Moves Legacy is disabled but was expected to be enabled", updateMovesLegacyCBFixture.requireVisible().isEnabled());
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertFalse("Update Moves was selected after reloading settings 1", settings.isUpdateMoves());
        assertTrue("Update Moves Legacy was not selected after reloading settings 1", settings.isUpdateMovesLegacy());

        // Toggle Gen 5 + Gen 6
        updateMovesCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Update Moves was not selected even though it was clicked", settings.isUpdateMoves());
        assertTrue("Update Moves Legacy was not selected even though state was not changed", settings.isUpdateMovesLegacy());
        assertTrue("Update Moves is disabled but was expected to be enabled", updateMovesCBFixture.requireVisible().isEnabled());
        assertTrue("Update Moves Legacy is disabled but was expected to be enabled", updateMovesLegacyCBFixture.requireVisible().isEnabled());
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertTrue("Update Moves was not selected after reloading settings 2", settings.isUpdateMoves());
        assertTrue("Update Moves Legacy was not selected after reloading settings 2", settings.isUpdateMovesLegacy());

        // Toggle Gen 5 off leaving Gen 6
        updateMovesLegacyCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Update Moves was not selected even though state was not changed", settings.isUpdateMoves());
        assertFalse("Update Moves Legacy was selected even though it was toggled off", settings.isUpdateMovesLegacy());
        assertTrue("Update Moves is disabled but was expected to be enabled", updateMovesCBFixture.requireVisible().isEnabled());
        assertTrue("Update Moves Legacy is disabled but was expected to be enabled", updateMovesLegacyCBFixture.requireVisible().isEnabled());
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertTrue("Update Moves was not selected after reloading settings 3", settings.isUpdateMoves());
        assertFalse("Update Moves Legacy was selected after reloading settings 3", settings.isUpdateMovesLegacy());

        //Toggle Gen 6 off leaving nothing
        updateMovesCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertFalse("Update Moves was selected even though it was toggled off", settings.isUpdateMoves());
        assertFalse("Update Moves Legacy was selected even though state was not changed", settings.isUpdateMovesLegacy());
        assertTrue("Update Moves is disabled but was expected to be enabled", updateMovesCBFixture.requireVisible().isEnabled());
        assertTrue("Update Moves Legacy is disabled but was expected to be enabled", updateMovesLegacyCBFixture.requireVisible().isEnabled());
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertFalse("Update Moves was selected after reloading settings 4", settings.isUpdateMoves());
        assertFalse("Update Moves Legacy was selected after reloading settings 4", settings.isUpdateMovesLegacy());
    }

    /**
     * Selecting USE_RESISTANT_TYPE evaluates to true Not selecting evaluates to
     * false
     * 
     * @throws IOException
     */
    @Test(timeout = 4000)
    public void TestUseResistantType() throws IOException {
        JCheckBoxFixture resistantTypeCBFixture = getCheckBoxByName("Use Resistant Type");
        Settings settings = this.mainWindow.getCurrentSettings();
        // Sanity check - should evaluate to false
        assertTrue("Misc Tweaks should not be set yet", settings.getCurrentMiscTweaks() == 0);
        // Sanity check - Should not fail with 0 options
        String setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertTrue("Misc Tweaks were selected after reloading settings 0", settings.getCurrentMiscTweaks() == 0);

        // Turn USE_RESISTANT_TYPE to true
        resistantTypeCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("USE_RESISTANT_TYPE should evaluate to true", 
            (settings.getCurrentMiscTweaks() & MiscTweak.USE_RESISTANT_TYPE.getValue()) > 0);
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertTrue("USE_RESISTANT_TYPE should be selected after reloading settings 1", 
            (settings.getCurrentMiscTweaks() & MiscTweak.USE_RESISTANT_TYPE.getValue()) > 0);

        // Turn USE_RESISTANT_TYPE to false
        resistantTypeCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertFalse("USE_RESISTANT_TYPE should evaluate to false", 
            (settings.getCurrentMiscTweaks() & MiscTweak.USE_RESISTANT_TYPE.getValue()) > 0);
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertFalse("USE_RESISTANT_TYPE should be selected after reloading settings 2", 
            (settings.getCurrentMiscTweaks() & MiscTweak.USE_RESISTANT_TYPE.getValue()) > 0);
    }

    /**
     * Selecting "RANDOM" enables the change methods checkbox
     * Toggles the change methods checkbox
     * Selecting "UNCHANGED" disables the change methods checkbox and resets state to false
     * Verifies settings can be stored and loaded with no error and preserve state
     * @throws IOException
     */
    @Test(timeout = 4000)
    public void TestChangeMethods() throws IOException {
        JRadioButtonFixture unchangedEvoRBFixture = getRadioButtonByName("peUnchangedRB");
        JRadioButtonFixture randomEvoRBFixture = getRadioButtonByName("peRandomRB");
        JCheckBoxFixture changeMethodsCBFixture = getCheckBoxByName("peChangeMethodsCB");
        TestCheckboxBasedOnRadioButton(unchangedEvoRBFixture, randomEvoRBFixture, changeMethodsCBFixture, (settings) -> settings.isEvosChangeMethod(),
        (evolutionMod) -> evolutionMod == Settings.EvolutionsMod.UNCHANGED, (evolutionMod) -> evolutionMod == Settings.EvolutionsMod.RANDOM,
        (settings) -> settings.getEvolutionsMod(), "Evolutions");
    }
    

    /**
     * Selecting "RANDOM" enables the minimum evos slider
     * Selecting 0, 1, or 2 is reflected in the settings
     * Selecting "UNCHANGED" disables the minimum evos slider and resets the value to 0
     * Saving settings to string and restoring produces no errors and retains state
     * @throws IOException
     */
    @Test(timeout = 4000)
    public void TestStartersMinimumEvos() throws IOException {
        JRadioButtonFixture unchangedStarterRBFixture = getRadioButtonByName("spUnchangedRB");
        JRadioButtonFixture randomStarterRBFixture = getRadioButtonByName("spRandomRB");
        JSliderFixture startersMinimumEvosFixture = getSliderByName("spRandomSlider");
        Settings settings = this.mainWindow.getCurrentSettings();
        // Sanity check - should evaluate to defaults
        assertTrue("Starters minimum evos should not be set yet", settings.getStartersMinimumEvos() == 0);
        assertFalse("Starters minimum evos should not be enabled yet", startersMinimumEvosFixture.isEnabled());
        assertTrue("Starters should be set to UNCHANGED but was not", settings.getStartersMod() == Settings.StartersMod.UNCHANGED);
        // Sanity check - Should not fail with 0 options
        String setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertTrue("Starters minimum evos was not zero after reloading settings 0", settings.getStartersMinimumEvos() == 0);
        assertTrue("Starters was not UNCHANGED after reloading settings 0", settings.getStartersMod() == Settings.StartersMod.UNCHANGED);

        // Turn random starters on
        clickRBAndWait(randomStarterRBFixture);
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Starters minimum evos should not be set yet", settings.getStartersMinimumEvos() == 0);
        assertTrue("Starters minimum evos should be enabled", startersMinimumEvosFixture.isEnabled());
        assertTrue("Starters should be set to RANDOM but was not", settings.getStartersMod() == Settings.StartersMod.RANDOM);
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertTrue("Starters minimum evos was not zero after reloading settings 1", settings.getStartersMinimumEvos() == 0);
        assertTrue("Starters was not RANDOM after reloading settings 1", settings.getStartersMod() == Settings.StartersMod.RANDOM);
        
        // Set Slider to 1
        startersMinimumEvosFixture.requireVisible().requireEnabled().slideTo(1);
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Starters minimum evos should be set to 1", settings.getStartersMinimumEvos() == 1);
        assertTrue("Starters minimum evos should be enabled", startersMinimumEvosFixture.isEnabled());
        assertTrue("Starters should be set to RANDOM as state did not change", settings.getStartersMod() == Settings.StartersMod.RANDOM);
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertTrue("Starters minimum evos was not one after reloading settings 2", settings.getStartersMinimumEvos() == 1);
        assertTrue("Starters was not RANDOM after reloading settings 2", settings.getStartersMod() == Settings.StartersMod.RANDOM);

        // Set Slider to 2
        startersMinimumEvosFixture.requireVisible().requireEnabled().slideTo(2);
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Starters minimum evos should be set to 2", settings.getStartersMinimumEvos() == 2);
        assertTrue("Starters minimum evos should be enabled", startersMinimumEvosFixture.isEnabled());
        assertTrue("Starters should be set to RANDOM as state did not change", settings.getStartersMod() == Settings.StartersMod.RANDOM);
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertTrue("Starters minimum evos was not two after reloading settings 3", settings.getStartersMinimumEvos() == 2);
        assertTrue("Starters was not RANDOM after reloading settings 3", settings.getStartersMod() == Settings.StartersMod.RANDOM);

        // Set Slider to 0
        startersMinimumEvosFixture.requireVisible().requireEnabled().slideTo(0);
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Starters minimum evos should be set to 0", settings.getStartersMinimumEvos() == 0);
        assertTrue("Starters minimum evos should be enabled", startersMinimumEvosFixture.isEnabled());
        assertTrue("Starters should be set to RANDOM as state did not change", settings.getStartersMod() == Settings.StartersMod.RANDOM);
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertTrue("Starters minimum evos was not zero after reloading settings 4", settings.getStartersMinimumEvos() == 0);
        assertTrue("Starters was not RANDOM after reloading settings 4", settings.getStartersMod() == Settings.StartersMod.RANDOM);

        // Turn random starters off while slider is set to a non-zero number should result in value being 0
        startersMinimumEvosFixture.requireVisible().requireEnabled().slideTo(1);
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Starters minimum evos should be set to 1", settings.getStartersMinimumEvos() == 1);
        assertTrue("Starters minimum evos should be enabled", startersMinimumEvosFixture.isEnabled());
        assertTrue("Starters should be set to RANDOM as state did not change", settings.getStartersMod() == Settings.StartersMod.RANDOM);
        clickRBAndWait(unchangedStarterRBFixture);
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Starters minimum evos should be set to 0", settings.getStartersMinimumEvos() == 0);
        assertFalse("Starters minimum evos should not be enabled", startersMinimumEvosFixture.isEnabled());
        assertTrue("Starters should be set to UNCHANGED but was not", settings.getStartersMod() == Settings.StartersMod.UNCHANGED);
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertTrue("Starters minimum evos was not zero after reloading settings 5", settings.getStartersMinimumEvos() == 0);
        assertTrue("Starters was not UNCHANGED after reloading settings 5", settings.getStartersMod() == Settings.StartersMod.UNCHANGED);
    }


    /**
     * Selecting "RANDOM" enables the exact evos checkbox
     * Toggles the exact evo checkbox
     * Selecting "UNCHANGED" disables the exact evos checkbox and resets state to false
     * Verifies settings can be stored and loaded with no error and preserve state
     * @throws IOException
     */
    @Test(timeout = 4000)
    public void TestExactEvo() throws IOException {
        JRadioButtonFixture unchangedStarterRBFixture = getRadioButtonByName("spUnchangedRB");
        JRadioButtonFixture randomStarterRBFixture = getRadioButtonByName("spRandomRB");
        JCheckBoxFixture exactEvoCBFixture = getCheckBoxByName("spExactEvoCB");
        TestCheckboxBasedOnRadioButton(unchangedStarterRBFixture, randomStarterRBFixture, exactEvoCBFixture, (settings) -> settings.isStartersExactEvo(),
            (startersMod) -> startersMod == Settings.StartersMod.UNCHANGED, (startersMod) -> startersMod == Settings.StartersMod.RANDOM,
            (settings) -> settings.getStartersMod(), "Starters");
    }

    /**
     * Selecting "RANDOM" enables the No Split Evos checkbox
     * Toggles the No Split Evo checkbox
     * Selecting "UNCHANGED" disables the No Split Evocheckbox and resets state to false
     * Verifies settings can be stored and loaded with no error and preserve state
     * @throws IOException
     */
    @Test(timeout = 4000)
    public void TestNoSplitEvos() throws IOException {
        JRadioButtonFixture unchangedStarterRBFixture = getRadioButtonByName("spUnchangedRB");
        JRadioButtonFixture randomStarterRBFixture = getRadioButtonByName("spRandomRB");
        JCheckBoxFixture noSplitCBFixture = getCheckBoxByName("spNoSplitCB");
        TestCheckboxBasedOnRadioButton(unchangedStarterRBFixture, randomStarterRBFixture, noSplitCBFixture, (settings) -> settings.isStartersNoSplit(), 
            (starterMod) -> starterMod == Settings.StartersMod.UNCHANGED, (starterMod) -> starterMod == Settings.StartersMod.RANDOM, 
            (settings) -> settings.getStartersMod(), "Starters");
    }

    /**
     * Toggle RandomizeHeldItems available on any trainers mod setting
     * 
     * @throws IOException
     */
    @Test(timeout = 4000)
    public void TestTrainerRandomHeldItem() throws IOException {
        JCheckBoxFixture randomHeldItemCBFixture = getCheckBoxByName("tpRandomHeldItemCB");
        Settings settings = this.mainWindow.getCurrentSettings();
        // Sanity check - Should initialize to False
        assertFalse("Trainer Random Held Item should not be set yet", settings.isTrainersRandomHeldItem());
        // Sanity check - Should not fail with 0 options
        String setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertFalse("Trainer Random Held Item was selected after reloading settings 0", settings.isTrainersRandomHeldItem());

        // Toggle on
        randomHeldItemCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Trainer Random Held Item was not selected even though it was clicked", settings.isTrainersRandomHeldItem());
        assertTrue("Trainer Random Held Item is disabled but was expected to be enabled", randomHeldItemCBFixture.requireVisible().isEnabled());
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertTrue("Trainer Random Held Item was not selected after reloading settings 1", settings.isTrainersRandomHeldItem());

        //Toggle off
        randomHeldItemCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertFalse("Trainer Random Held Item was selected even though it was toggled off", settings.isTrainersRandomHeldItem());
        assertTrue("Trainer Random Held Item is disabled but was expected to be enabled", randomHeldItemCBFixture.requireVisible().isEnabled());
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertFalse("Trainer Random Held Item was selected after reloading settings 2", settings.isTrainersRandomHeldItem());
    }

     /**
     * Selecting "RANDOM" or "GLOBAL SWAP" enables the Gym Type Theme checkbox
     * Toggles the Gym Type Theme checkbox
     * Selecting "UNCHANGED" or "TYPE THEME" disables the Gym Type Theme checkbox and resets state to false
     * Verifies settings can be stored and loaded with no error and preserve state
     * @throws IOException
     */
    @Test(timeout = 8000)
    public void TestGymTypeTheme() throws IOException {
        JRadioButtonFixture unchangedTrainerRBFixture = getRadioButtonByName("tpUnchangedRB");
        JRadioButtonFixture randomTrainerRBFixture = getRadioButtonByName("tpRandomRB");
        JRadioButtonFixture typeThemeTrainerRBFixture = getRadioButtonByName("tpTypeThemeRB");
        JRadioButtonFixture globalSwapRBFixture = getRadioButtonByName("tpGlobalSwapRB");
        JCheckBoxFixture gymTypeThemeCBFixture = getCheckBoxByName("tpGymTypeThemeCB");

        // Check basic case of UNCHANGED and RANDOM
        TestCheckboxBasedOnRadioButton(unchangedTrainerRBFixture, randomTrainerRBFixture, gymTypeThemeCBFixture, (settings) -> settings.isGymTypeTheme(),
            (trainersMod) -> trainersMod == Settings.TrainersMod.UNCHANGED, (trainersMod) -> trainersMod == Settings.TrainersMod.RANDOM,
            (settings) -> settings.getTrainersMod(), "Trainers");

        // Check UNCHANGED and GLOBAL_MAPPING
        TestCheckboxBasedOnRadioButton(unchangedTrainerRBFixture, globalSwapRBFixture, gymTypeThemeCBFixture, (settings) -> settings.isGymTypeTheme(),
        (trainersMod) -> trainersMod == Settings.TrainersMod.UNCHANGED, (trainersMod) -> trainersMod == Settings.TrainersMod.GLOBAL_MAPPING,
        (settings) -> settings.getTrainersMod(), "Trainers");

        // Selecting Type Theme trainer should not enable Gym Type Theme
        clickRBAndWait(typeThemeTrainerRBFixture);
        Settings settings = this.mainWindow.getCurrentSettings();
        assertFalse("Gym Type Theme should be unset still", settings.isGymTypeTheme());
        assertFalse("Gym Type Theme should be disabled still", gymTypeThemeCBFixture.isEnabled());
        assertTrue("Trainers should be set to TYPE THEMED but was not", settings.getTrainersMod() == Settings.TrainersMod.TYPE_THEMED);
        String setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertFalse("Gym Type Theme was not false after reloading settings TYPE THEME", settings.isGymTypeTheme());
        assertTrue("Trainers was not TYPE THEME after reloading settings TYPE THEME", settings.getTrainersMod() == Settings.TrainersMod.TYPE_THEMED);
    }

    /**
     * Clicking the random settings button results in a successful operation.
     * Verifies settings can be stored and loaded with no error and preserve state
     * @throws IOException
     */
    @Test(timeout = 4000)
    public void TestRandomSettingsButton() throws IOException {
        JButtonFixture randomQSButtonFixture = getButtonByName("randomQSButton");
        randomQSButtonFixture.requireVisible().requireEnabled().click();
        Settings settings = this.mainWindow.getCurrentSettings();
        String settingsString = settings.toString();
        settings = Settings.fromString(settingsString);
        assertTrue("Settings do not match after reload - \nBefore: " + settingsString + "\nAfter: " + settings.toString(),
            settingsString.equals(settings.toString()));
    }

    /**
     * Minimum Catch Rate Checkbox is enabled regardless of radio button selection
     * Verifies settings can be stored and loaded with no error and preserve state
     * @throws IOException
     */
    @Test(timeout = 4000)
    public void TestMinimumCatchRateCB() throws IOException {
        JRadioButtonFixture unchangedWildPokemonRBFixture = getRadioButtonByName("wpUnchangedRB");
        JRadioButtonFixture randomWildPokemonRBFixture = getRadioButtonByName("wpRandomRB");
        JCheckBoxFixture minimumCatchRateCBFixture = getCheckBoxByName("wpCatchRateCB");
        int settingsReloadCount = 0;
        // Sanity check - should evaluate to false
        Settings settings = this.mainWindow.getCurrentSettings();
        assertFalse(minimumCatchRateCBFixture.text() + " should not be set yet", settings.isUseMinimumCatchRate());
        assertTrue(minimumCatchRateCBFixture.text() + " should be enabled", minimumCatchRateCBFixture.isEnabled());
        assertTrue("Wild Pokemon should be set to " + unchangedWildPokemonRBFixture.text() + " but was not", settings.getWildPokemonMod() == Settings.WildPokemonMod.UNCHANGED);
        // Sanity check - Should not fail with 0 options
        String setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertFalse(minimumCatchRateCBFixture.text() + " was not false after reloading settings " + settingsReloadCount, settings.isUseMinimumCatchRate());
        assertTrue("Wild Pokemon was not " + unchangedWildPokemonRBFixture.text() + " after reloading settings " + settingsReloadCount, settings.getWildPokemonMod() == Settings.WildPokemonMod.UNCHANGED);
        settingsReloadCount++;

        // Turn Minimum Catch Rate on
        minimumCatchRateCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue(minimumCatchRateCBFixture.text() + " should be set now", settings.isUseMinimumCatchRate());
        assertTrue(minimumCatchRateCBFixture.text() + " should be enabled", minimumCatchRateCBFixture.isEnabled());
        assertTrue("Wild Pokemon should be set to " + unchangedWildPokemonRBFixture.text() + " but was not", settings.getWildPokemonMod() == Settings.WildPokemonMod.UNCHANGED);
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertTrue(minimumCatchRateCBFixture.text() + " was not true after reloading settings " + settingsReloadCount, settings.isUseMinimumCatchRate());
        assertTrue("Wild Pokemon was not " + unchangedWildPokemonRBFixture.text() + " after reloading settings " + settingsReloadCount, settings.getWildPokemonMod() == Settings.WildPokemonMod.UNCHANGED);
        settingsReloadCount++;

        // Turn Random RB on
        randomWildPokemonRBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue(minimumCatchRateCBFixture.text() + " should still be set", settings.isUseMinimumCatchRate());
        assertTrue(minimumCatchRateCBFixture.text() + " should be enabled", minimumCatchRateCBFixture.isEnabled());
        assertTrue("Wild Pokemon should be set to " + randomWildPokemonRBFixture.text() + " but was not", settings.getWildPokemonMod() == Settings.WildPokemonMod.RANDOM);
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertTrue(minimumCatchRateCBFixture.text() + " was not true after reloading settings " + settingsReloadCount, settings.isUseMinimumCatchRate());
        assertTrue("Wild Pokemon was not " + randomWildPokemonRBFixture.text() + " after reloading settings " + settingsReloadCount, settings.getWildPokemonMod() == Settings.WildPokemonMod.RANDOM);
        settingsReloadCount++;

        // Turn Unchanged RB on
        unchangedWildPokemonRBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue(minimumCatchRateCBFixture.text() + " should still be set", settings.isUseMinimumCatchRate());
        assertTrue(minimumCatchRateCBFixture.text() + " should be enabled", minimumCatchRateCBFixture.isEnabled());
        assertTrue("Wild Pokemon should be set to " + unchangedWildPokemonRBFixture.text() + " but was not", settings.getWildPokemonMod() == Settings.WildPokemonMod.UNCHANGED);
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertTrue(minimumCatchRateCBFixture.text() + " was not true after reloading settings " + settingsReloadCount, settings.isUseMinimumCatchRate());
        assertTrue("Wild Pokemon was not " + unchangedWildPokemonRBFixture.text() + " after reloading settings " + settingsReloadCount, settings.getWildPokemonMod() == Settings.WildPokemonMod.UNCHANGED);
        settingsReloadCount++;

        // Turn Minimum Catch Rate off
        minimumCatchRateCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertFalse(minimumCatchRateCBFixture.text() + " should not be set", settings.isUseMinimumCatchRate());
        assertTrue(minimumCatchRateCBFixture.text() + " should be enabled", minimumCatchRateCBFixture.isEnabled());
        assertTrue("Wild Pokemon should be set to " + unchangedWildPokemonRBFixture.text() + " but was not", settings.getWildPokemonMod() == Settings.WildPokemonMod.UNCHANGED);
        // Sanity check - Should not fail with 0 options
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertFalse(minimumCatchRateCBFixture.text() + " was not false after reloading settings " + settingsReloadCount, settings.isUseMinimumCatchRate());
        assertTrue("Wild Pokemon was not " + unchangedWildPokemonRBFixture.text() + " after reloading settings " + settingsReloadCount, settings.getWildPokemonMod() == Settings.WildPokemonMod.UNCHANGED);
        settingsReloadCount++;
    }

    /**
     * Selecting "RANDOM" enables the SE Triangle checkbox
     * Toggles the SE Triangle checkbox
     * Selecting "UNCHANGED" disables the SE Triangle checkbox and resets state to false
     * Verifies settings can be stored and loaded with no error and preserve state
     * @throws IOException
     */
    @Test(timeout = 4000)
    public void TestStartersSETriangle() throws IOException {
        JRadioButtonFixture unchangedStarterRBFixture = getRadioButtonByName("spUnchangedRB");
        JRadioButtonFixture randomStarterRBFixture = getRadioButtonByName("spRandomRB");
        JCheckBoxFixture seTriangleCBFixture = getCheckBoxByName("spSETriangleCB");
        TestCheckboxBasedOnRadioButton(unchangedStarterRBFixture, randomStarterRBFixture, seTriangleCBFixture, (settings) -> settings.isStartersSETriangle(), 
            (starterMod) -> starterMod == Settings.StartersMod.UNCHANGED, (starterMod) -> starterMod == Settings.StartersMod.RANDOM, 
            (settings) -> settings.getStartersMod(), "Starters");
    }

    /**
     * Selecting "GLOBAL SWAP" toggles the radio button group
     * Selecting "UNCHANGED" toggles the radio button group away from GLOBAL_MAPPING
     * Verifies settings can be stored and loaded with no error and preserve state
     * @throws IOException
     */
    @Test(timeout = 4000)
    public void TestGlobalSwap() throws IOException {
        int settingsReloadCount = 0;
        JRadioButtonFixture unchangedTrainerRBFixture = getRadioButtonByName("tpUnchangedRB");
        JRadioButtonFixture globalSwapRBFixture = getRadioButtonByName("tpGlobalSwapRB");
        // Sanity check - should evaluate to false
        Settings settings = this.mainWindow.getCurrentSettings();
        assertTrue("Global Swap RB should be false", globalSwapRBFixture.requireNotSelected() == globalSwapRBFixture);
        assertTrue("Trainers should be set to UNCHANGED but was not", settings.getTrainersMod() == Settings.TrainersMod.UNCHANGED);
        // Sanity check - Should not fail with 0 options
        String setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertTrue("Global Swap RB was not false after reloading settings " + settingsReloadCount, globalSwapRBFixture.requireNotSelected() == globalSwapRBFixture);
        assertTrue("Trainers was not UNCHANGED after reloading settings " + settingsReloadCount, settings.getTrainersMod() == Settings.TrainersMod.UNCHANGED);
        settingsReloadCount++;

        // Turn Global Swap on
        clickRBAndWait(globalSwapRBFixture);
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Global Swap RB should be true", globalSwapRBFixture.requireSelected() == globalSwapRBFixture);
        assertTrue("Trainers should be set to GLOBAL_MAPPING but was not", settings.getTrainersMod() == Settings.TrainersMod.GLOBAL_MAPPING);
        // Reloading settings
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertTrue("Global Swap RB was not true after reloading settings " + settingsReloadCount, globalSwapRBFixture.requireSelected() == globalSwapRBFixture);
        assertTrue("Trainers was not GLOBAL_MAPPING after reloading settings " + settingsReloadCount, settings.getTrainersMod() == Settings.TrainersMod.GLOBAL_MAPPING);
        settingsReloadCount++;

        // Turn Unchanged on
        clickRBAndWait(unchangedTrainerRBFixture);
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Global Swap RB should be false", globalSwapRBFixture.requireNotSelected() == globalSwapRBFixture);
        assertTrue("Trainers should be set to UNCHANGED but was not", settings.getTrainersMod() == Settings.TrainersMod.UNCHANGED);
        // Reloading settings
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertTrue("Global Swap RB was not false after reloading settings " + settingsReloadCount, globalSwapRBFixture.requireNotSelected() == globalSwapRBFixture);
        assertTrue("Trainers was not UNCHANGED after reloading settings " + settingsReloadCount, settings.getTrainersMod() == Settings.TrainersMod.UNCHANGED);
        settingsReloadCount++;
    }

    @Test(timeout = 10000)
    public void TestStarterTypeFilter() throws IOException {
        int settingsReloadCount = 0;
        JRadioButtonFixture unchangedStarterRBFixture = getRadioButtonByName("spUnchangedRB");
        JRadioButtonFixture randomStarterRBFixture = getRadioButtonByName("spRandomRB");
        JCheckBoxFixture seTriangleCBFixture = getCheckBoxByName("spSETriangleCB");
        JButtonFixture filterTypesButtonFixture = getButtonByName("spTypeFilterButton");

        // Sanity check - Should not be enabled
        Settings settings = this.mainWindow.getCurrentSettings();
        assertTrue("Starters should be set to UNCHANGED but was not", settings.getStartersMod() == Settings.StartersMod.UNCHANGED);
        assertFalse("Starter Type Filter should not be enabled yet", filterTypesButtonFixture.isEnabled());
        assertTrue("Starter Types should initialze as null", settings.getStarterTypes() == null);
        // Sanity check - Should not fail with 0 options
        String settingsString = settings.toString();
        settings = Settings.fromString(settingsString);
        assertTrue("Starter Types was not null after reloading settings " + settingsReloadCount, settings.getStarterTypes() == null);
        assertTrue("Starters was not UNCHANGED after reloading settings " + settingsReloadCount,
            settings.getStartersMod() == Settings.StartersMod.UNCHANGED);
        settingsReloadCount++;

        //Turn randomStarterRB on
        clickRBAndWait(randomStarterRBFixture);
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Starters should be set to RANDOM but was not", settings.getStartersMod() == Settings.StartersMod.RANDOM);
        assertTrue("Starter Type Filter should be enabled now", filterTypesButtonFixture.isEnabled());
        assertTrue("Starter Types should still be null", settings.getStarterTypes() == null);
        settingsString = settings.toString();
        settings = Settings.fromString(settingsString);
        assertTrue("Starter Types was not null after reloading settings " + settingsReloadCount, settings.getStarterTypes() == null);
        assertTrue("Starters was not RANDOM after reloading settings " + settingsReloadCount,
            settings.getStartersMod() == Settings.StartersMod.RANDOM);
        settingsReloadCount++;

        // Select the first and last item from Type Filter
        filterTypesButtonFixture.requireVisible().requireEnabled().click();
        DialogFixture typeFilterDialog = getDialogByClass(TypeFilterDialog.class);
        JListFixture typeCheckboxList = getListByName("typeCheckboxList");
        JButtonFixture filterOkButton = getButtonByName("okButton");
        typeCheckboxList.selectItem(0);
        typeCheckboxList.selectItem(typeCheckboxList.contents().length-1);
        filterOkButton.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Type filter dialog was not closed", typeFilterDialog.requireNotVisible() != null);
        assertTrue("Starter types did not include NORMAL and DARK",
            settings.getStarterTypes().containsAll(Arrays.asList(Type.NORMAL, Type.DARK)));
        settingsString = settings.toString();
        settings = Settings.fromString(settingsString);
        assertTrue("Starter Types did not include NORMAL and DARK after reloading settings " + settingsReloadCount,
            settings.getStarterTypes().containsAll(Arrays.asList(Type.NORMAL, Type.DARK)));
        assertTrue("Starters was not RANDOM after reloading settings " + settingsReloadCount,
            settings.getStartersMod() == Settings.StartersMod.RANDOM);
        settingsReloadCount++;
        
        // Reopen and assert choices do not change when clicking cancel
        filterTypesButtonFixture.requireVisible().requireEnabled().click();
        typeFilterDialog = getDialogByClass(TypeFilterDialog.class);
        typeCheckboxList = getListByName("typeCheckboxList");
        JButtonFixture filterCancelButton = getButtonByName("cancelButton");
        // Only select the first item in the list to make the state different than prior
        typeCheckboxList.selectItem(0);
        filterCancelButton.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Type filter dialog was not closed", typeFilterDialog.requireNotVisible() != null);
        // Cancel should restore to prior state and not new state
        assertTrue("Starter types did not include NORMAL and DARK",
        settings.getStarterTypes().containsAll(Arrays.asList(Type.NORMAL, Type.DARK)));
        settingsString = settings.toString();
        settings = Settings.fromString(settingsString);
        assertTrue("Starter Types did not include NORMAL and DARK after reloading settings " + settingsReloadCount,
            settings.getStarterTypes().containsAll(Arrays.asList(Type.NORMAL, Type.DARK)));
        assertTrue("Starters was not RANDOM after reloading settings " + settingsReloadCount,
            settings.getStartersMod() == Settings.StartersMod.RANDOM);
        settingsReloadCount++;

        // Turn SETriangle on - should diable Type Filter and null starterTypes
        clickCBAndWait(seTriangleCBFixture);
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Starters should be set to RANDOM but was not", settings.getStartersMod() == Settings.StartersMod.RANDOM);
        assertFalse("Starter Type Filter should be disabled now", filterTypesButtonFixture.isEnabled());
        assertTrue("Starter Types should be null again", settings.getStarterTypes() == null);
        settingsString = settings.toString();
        settings = Settings.fromString(settingsString);
        assertTrue("Starter Types was not null after reloading settings " + settingsReloadCount, settings.getStarterTypes() == null);
        assertTrue("Starters was not RANDOM after reloading settings " + settingsReloadCount,
            settings.getStartersMod() == Settings.StartersMod.RANDOM);
        settingsReloadCount++;
        // Deselect SETriangle
        clickCBAndWait(seTriangleCBFixture);

        // Assert select all chooses everything
        filterTypesButtonFixture.requireVisible().requireEnabled().click();
        typeFilterDialog = getDialogByClass(TypeFilterDialog.class);
        typeCheckboxList = getListByName("typeCheckboxList");
        filterOkButton = getButtonByName("okButton");
        JButtonFixture filterSelectAllButton = getButtonByName("selectAllButton");
        int typeSize = typeCheckboxList.contents().length;
        filterSelectAllButton.requireVisible().requireEnabled().click();
        filterOkButton.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Type filter dialog was not closed", typeFilterDialog.requireNotVisible() != null);
        List<Type> starterTypes1 = settings.getStarterTypes();
        Type.getTypes(typeSize).forEach(type -> assertTrue("Missing " + type + " from starter types",
            starterTypes1.contains(type)));
        settingsString = settings.toString();
        settings = Settings.fromString(settingsString);
        List<Type> starterTypes2 = settings.getStarterTypes();
        Type.getTypes(typeSize).forEach(type -> assertTrue("Missing " + type + " from starter types after reload",
            starterTypes2.contains(type)));
        assertTrue("Starters was not RANDOM after reloading settings " + settingsReloadCount,
            settings.getStartersMod() == Settings.StartersMod.RANDOM);
        settingsReloadCount++;

        // Assert deselect all sets starterTypes to null
        filterTypesButtonFixture.requireVisible().requireEnabled().click();
        typeFilterDialog = getDialogByClass(TypeFilterDialog.class);
        typeCheckboxList = getListByName("typeCheckboxList");
        filterOkButton = getButtonByName("okButton");
        JButtonFixture filterDeselectAllButton = getButtonByName("deselectAllButton");
        typeSize = typeCheckboxList.contents().length;
        filterDeselectAllButton.requireVisible().requireEnabled().click();
        filterOkButton.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Type filter dialog was not closed", typeFilterDialog.requireNotVisible() != null);
        assertTrue("Starter types shoudl be null again", settings.getStarterTypes() == null);
        settingsString = settings.toString();
        settings = Settings.fromString(settingsString);
        assertTrue("Starter Types was not null after reloading settings " + settingsReloadCount, settings.getStarterTypes() == null);
        assertTrue("Starters was not RANDOM after reloading settings " + settingsReloadCount,
            settings.getStartersMod() == Settings.StartersMod.RANDOM);
        settingsReloadCount++;

        // Turn unchangedStarterRB on
        // Populate starter types first
        filterTypesButtonFixture.requireVisible().requireEnabled().click();
        typeFilterDialog = getDialogByClass(TypeFilterDialog.class);
        typeCheckboxList = getListByName("typeCheckboxList");
        filterOkButton = getButtonByName("okButton");
        typeCheckboxList.selectItem(0);
        typeCheckboxList.selectItem(typeCheckboxList.contents().length-1);
        filterOkButton.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Type filter dialog was not closed", typeFilterDialog.requireNotVisible() != null);
        assertTrue("Starter types did not include NORMAL and DARK",
            settings.getStarterTypes().containsAll(Arrays.asList(Type.NORMAL, Type.DARK)));
        // Now turn unchangedStarterRB on
        clickRBAndWait(unchangedStarterRBFixture);
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Starters should be set to UNCHANGED but was not",
            settings.getStartersMod() == Settings.StartersMod.UNCHANGED);
        assertFalse("Starter Type Filter should be disabled now", filterTypesButtonFixture.isEnabled());
        assertTrue("Starter Types should be null again", settings.getStarterTypes() == null);
        settingsString = settings.toString();
        settings = Settings.fromString(settingsString);
        assertTrue("Starter Types was not null after reloading settings " + settingsReloadCount, settings.getStarterTypes() == null);
        assertTrue("Starters was not UNCHANGED after reloading settings " + settingsReloadCount,
            settings.getStartersMod() == Settings.StartersMod.UNCHANGED);
        settingsReloadCount++;
    }

    /**
     * Captures a common sequence of a checkbox being enabled or disabled based on radio button selection
     * 
     * @param defaultRB - The radio button fixture that disables the checkbox (must be the one that's on by default)
     * @param triggerRB - The radio button fixture that enables the checkbox
     * @param checkboxToTest - The checkbox fixture that is being tested
     * @param settingsCheckboxFunction - The method in Settings.java that refers to the state of the checkbox
     * @param defaultRBCondition - The enum value that represents the defaultRb in radio button group
     * @param triggerRBCondition - The enum value that represents the triggerRB in the radio button group
     * @param settingsRBFunction - The method in Settings.java that refers to the state of the enum
     * @param buttonGroup - The name of the radio button group. Used for descriptive error messages.
     * @throws IOException
     */
    private void TestCheckboxBasedOnRadioButton(JRadioButtonFixture defaultRB, JRadioButtonFixture triggerRB, JCheckBoxFixture checkboxToTest, Predicate<Settings> settingsCheckboxFunction,
        Predicate<Enum> defaultRBCondition, Predicate<Enum> triggerRBCondition, Function<Settings, Enum> settingsRBFunction, String buttonGroup) throws IOException {
        int settingsReloadCount = 0;
        // Sanity check - should evaluate to false
        Settings settings = this.mainWindow.getCurrentSettings();
        assertFalse(checkboxToTest.text() + " should not be set yet", settingsCheckboxFunction.test(settings));
        assertFalse(checkboxToTest.text() + " should not be enabled yet", checkboxToTest.isEnabled());
        assertTrue(buttonGroup + " should be set to " + defaultRB.text() + " but was not", defaultRBCondition.test(settingsRBFunction.apply(settings)));
        // Sanity check - Should not fail with 0 options
        String setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertFalse(checkboxToTest.text() + " was not false after reloading settings " + settingsReloadCount, settingsCheckboxFunction.test(settings));
        assertTrue(buttonGroup + " was not " + defaultRB.text() + " after reloading settings " + settingsReloadCount, defaultRBCondition.test(settingsRBFunction.apply(settings)));
        settingsReloadCount++;

        // Turn triggerRB on
        clickRBAndWait(triggerRB);
        settings = this.mainWindow.getCurrentSettings();
        assertFalse(checkboxToTest.text() + " should not be set yet", settingsCheckboxFunction.test(settings));
        assertTrue(checkboxToTest.text() + " should be enabled now", checkboxToTest.isEnabled());
        assertTrue(buttonGroup + " should be set to " + triggerRB.text() + " but was not", triggerRBCondition.test(settingsRBFunction.apply(settings)));
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertFalse(checkboxToTest.text() + " was not false after reloading settings " + settingsReloadCount, settingsCheckboxFunction.test(settings));
        assertTrue(buttonGroup + " was not " + triggerRB.text() + " after reloading settings " + settingsReloadCount, triggerRBCondition.test(settingsRBFunction.apply(settings)));
        settingsReloadCount++;

        // Toggle checkboxToTest on
        checkboxToTest.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue(checkboxToTest.text() + " should be set now", settingsCheckboxFunction.test(settings));
        assertTrue(checkboxToTest.text() + " should be enabled as state did not change", checkboxToTest.isEnabled());
        assertTrue(buttonGroup + " should be set to " + triggerRB.text() + " as state did not change", triggerRBCondition.test(settingsRBFunction.apply(settings)));
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertTrue(checkboxToTest.text() + " was not true after reloading settings " + settingsReloadCount, settingsCheckboxFunction.test(settings));
        assertTrue(buttonGroup + " was not " + triggerRB.text() + " after reloading settings " + settingsReloadCount, triggerRBCondition.test(settingsRBFunction.apply(settings)));
        settingsReloadCount++;

        // Toggle checkboxToTest off
        checkboxToTest.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertFalse(checkboxToTest.text() + " should be unset now", settingsCheckboxFunction.test(settings));
        assertTrue(checkboxToTest.text() + " should be enabled as state did not change", checkboxToTest.isEnabled());
        assertTrue(buttonGroup + " should be set to " + triggerRB.text() + " as state did not change", triggerRBCondition.test(settingsRBFunction.apply(settings)));
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertFalse(checkboxToTest.text() + " was not false after reloading settings " + settingsReloadCount, settingsCheckboxFunction.test(settings));
        assertTrue(buttonGroup + " was not " + triggerRB.text() + " after reloading settings " + settingsReloadCount, triggerRBCondition.test(settingsRBFunction.apply(settings)));
        settingsReloadCount++;

        // Turn defaultRB on while checkboxToTest is true should set checkboxToTest to false
        checkboxToTest.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue(checkboxToTest.text() + " should be set now", settingsCheckboxFunction.test(settings));
        assertTrue(checkboxToTest.text() + " should be enabled as state did not change", checkboxToTest.isEnabled());
        assertTrue(buttonGroup + " should be set to " + triggerRB.text() + " as state did not change", triggerRBCondition.test(settingsRBFunction.apply(settings)));
        clickRBAndWait(defaultRB);
        settings = this.mainWindow.getCurrentSettings();
        assertFalse(checkboxToTest.text() + " should be unset now", settingsCheckboxFunction.test(settings));
        assertFalse(checkboxToTest.text() + " should be disabled now", checkboxToTest.isEnabled());
        assertTrue(buttonGroup + " should be set to " + defaultRB.text()+ " but was not", defaultRBCondition.test(settingsRBFunction.apply(settings)));
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertFalse(checkboxToTest.text() + " was not false after reloading settings " + settingsReloadCount, settingsCheckboxFunction.test(settings));
        assertTrue(buttonGroup + " was not " + defaultRB.text() + " after reloading settings " + settingsReloadCount, defaultRBCondition.test(settingsRBFunction.apply(settings)));
        settingsReloadCount++;
    }

    /**
     * Clicks a JRadioButton and waits for the UI to update before completing
     * @param rbFixture - The fixture representing the radio button to click
     */
    private void clickRBAndWait(JRadioButtonFixture rbFixture) {
        rbFixture.requireVisible().requireEnabled().click();
        await().until(() -> this.mainWindow.isUIUpdated());
    }

    /**
     * Clicks a JCheckBox and waits for the UI to update before completing
     * @param cbFixture - The fixture representing the radio button to click
     */
    private void clickCBAndWait(JCheckBoxFixture cbFixture) {
        cbFixture.requireVisible().requireEnabled().click();
        await().until(() -> this.mainWindow.isUIUpdated());
    }
}
