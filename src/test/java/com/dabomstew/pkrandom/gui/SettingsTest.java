package com.dabomstew.pkrandom.gui;

import static org.junit.Assert.*;
import static org.awaitility.Awaitility.await;

import java.io.IOException;

import com.dabomstew.pkrandom.MiscTweak;
import com.dabomstew.pkrandom.Settings;

import org.assertj.swing.fixture.JCheckBoxFixture;
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
     * Selecting "RANDOM" opens up the "Change Methods" option
     * Change Methods correctly updates settings
     * Toggling the "RANDOM" evolution mod radio button disables "Change Methods"
     * 
     * @throws IOException
     */
    @Test(timeout = 4000)
    public void TestChangeMethods() throws IOException {
        JRadioButtonFixture unchangedEvoRBFixture = getRadoiButtonByName("peUnchangedRB");
        JRadioButtonFixture randomEvoRBFixture = getRadoiButtonByName("peRandomRB");
        JCheckBoxFixture changeMethodsCBFixture = getCheckBoxByName("peChangeMethodsCB");
        Settings settings = this.mainWindow.getCurrentSettings();
        // Sanity check - should evaluate to false
        assertFalse("Change Methods should not be set yet", settings.isEvosChangeMethod());
        assertFalse("Change Methods should not be enabled yet", changeMethodsCBFixture.isEnabled());
        assertTrue("Evolutions should be set to UNCHANGED but was not", settings.getEvolutionsMod() == Settings.EvolutionsMod.UNCHANGED);
        // Sanity check - Should not fail with 0 options
        String setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertFalse("Change Methods was selected after reloading settings 0", settings.isEvosChangeMethod());
        assertTrue("Evolutions was not UNCHANGED after reloading settings 0", settings.getEvolutionsMod() == Settings.EvolutionsMod.UNCHANGED);


        // Turn randomEvos on
        clickRBAndWait(randomEvoRBFixture);
        settings = this.mainWindow.getCurrentSettings();
        assertFalse("Change Methods should not be set yet", settings.isEvosChangeMethod());
        assertTrue("Change Methods should be enabled now", changeMethodsCBFixture.isEnabled());
        assertTrue("Evolutions were not set to RANDOM", settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM);
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertFalse("Change Methods was selected after reloading settings 1", settings.isEvosChangeMethod());
        assertTrue("Evolutions was not RANDOM after reloading settings 1", settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM);

        // Turn evosChangeMethod to true
        changeMethodsCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("evosChangeMethod should evaluate to true", settings.isEvosChangeMethod());
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertTrue("Change Methods was not selected after reloading settings 2", settings.isEvosChangeMethod());
        assertTrue("Evolutions was not RANDOM after reloading settings 2", settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM);

        // Turn evosChangeMethod to false
        changeMethodsCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertFalse("evosChangeMethod should evaluate to false", settings.isEvosChangeMethod());
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertFalse("Change Methods was selected after reloading settings 3", settings.isEvosChangeMethod());
        assertTrue("Evolutions was not RANDOM after reloading settings 3", settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM);

        // Turn randomEvos off while evosChangeMethod is true should turn evosChangeMethod to false
        changeMethodsCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("evosChangeMethod should evaluate to true", settings.isEvosChangeMethod());
        assertTrue("Evolutions should be set to RANDOM as state did not change", settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM);
        clickRBAndWait(unchangedEvoRBFixture);
        settings = this.mainWindow.getCurrentSettings();
        assertFalse("evosChangeMethod should evaluate to false", settings.isEvosChangeMethod());
        assertFalse("Change Methods should be disabled now", changeMethodsCBFixture.isEnabled());
        assertTrue("Evolutions should be set to UNCHANGED but was not", settings.getEvolutionsMod() == Settings.EvolutionsMod.UNCHANGED);
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);
        assertFalse("Change Methods was selected after reloading settings 4", settings.isEvosChangeMethod());
        assertTrue("Evolutions was not UNCHANGED after reloading settings 4", settings.getEvolutionsMod() == Settings.EvolutionsMod.UNCHANGED);
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
        JRadioButtonFixture unchangedStarterRBFixture = getRadoiButtonByName("spUnchangedRB");
        JRadioButtonFixture randomStarterRBFixture = getRadoiButtonByName("spRandomRB");
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
        JRadioButtonFixture unchangedStarterRBFixture = getRadoiButtonByName("spUnchangedRB");
        JRadioButtonFixture randomStarterRBFixture = getRadoiButtonByName("spRandomRB");
        JCheckBoxFixture exactEvoCBFixture = getCheckBoxByName("spExactEvoCB");
        // Sanity check - should evaluate to false
        Settings settings = this.mainWindow.getCurrentSettings();
        assertFalse("Exact Evo should not be set yet", settings.isStartersExactEvo());
        assertFalse("Exact Evo should not be enabled yet", exactEvoCBFixture.isEnabled());
        assertTrue("Starters should be set to UNCHANGED but was not", settings.getStartersMod() == Settings.StartersMod.UNCHANGED);
        // Sanity check - Should not fail with 0 options
        String setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertFalse("Exact Evo was not false after reloading settings 0", settings.isStartersExactEvo());
        assertTrue("Starters was not UNCHANGED after reloading settings 0", settings.getStartersMod() == Settings.StartersMod.UNCHANGED);

        // Turn random starters on
        clickRBAndWait(randomStarterRBFixture);
        settings = this.mainWindow.getCurrentSettings();
        assertFalse("Exact Evo should not be set yet", settings.isStartersExactEvo());
        assertTrue("Exact Evo should be enabled now", exactEvoCBFixture.isEnabled());
        assertTrue("Starters should be set to RANDOM but was not", settings.getStartersMod() == Settings.StartersMod.RANDOM);
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertFalse("Exact Evo was not false after reloading settings 1", settings.isStartersExactEvo());
        assertTrue("Starters was not RANDOM after reloading settings 1", settings.getStartersMod() == Settings.StartersMod.RANDOM);

        // Toggle exact evo on
        exactEvoCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Exact Evo should be set now", settings.isStartersExactEvo());
        assertTrue("Exact Evo should be enabled as state did not change", exactEvoCBFixture.isEnabled());
        assertTrue("Starters should be set to RANDOM as state did not change", settings.getStartersMod() == Settings.StartersMod.RANDOM);
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertTrue("Exact Evo was not true after reloading settings 2", settings.isStartersExactEvo());
        assertTrue("Starters was not RANDOM after reloading settings 2", settings.getStartersMod() == Settings.StartersMod.RANDOM);

        // Toggle exact evo off
        exactEvoCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertFalse("Exact Evo should be unset now", settings.isStartersExactEvo());
        assertTrue("Exact Evo should be enabled as state did not change", exactEvoCBFixture.isEnabled());
        assertTrue("Starters should be set to RANDOM as state did not change", settings.getStartersMod() == Settings.StartersMod.RANDOM);
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertFalse("Exact Evo was not false after reloading settings 3", settings.isStartersExactEvo());
        assertTrue("Starters was not RANDOM after reloading settings 3", settings.getStartersMod() == Settings.StartersMod.RANDOM);

        // Turn random starter off while exact evo is true should set exact evo to false
        exactEvoCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Exact Evo should be set now", settings.isStartersExactEvo());
        assertTrue("Exact Evo should be enabled as state did not change", exactEvoCBFixture.isEnabled());
        assertTrue("Starters should be set to RANDOM as state did not change", settings.getStartersMod() == Settings.StartersMod.RANDOM);
        clickRBAndWait(unchangedStarterRBFixture);
        settings = this.mainWindow.getCurrentSettings();
        assertFalse("Exact Evo should be unset now", settings.isStartersExactEvo());
        assertFalse("Exact Evo should be disabled now", exactEvoCBFixture.isEnabled());
        assertTrue("Starters should be set to UNCHANGED but was not", settings.getStartersMod() == Settings.StartersMod.UNCHANGED);
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertFalse("Exact Evo was not false after reloading settings 4", settings.isStartersExactEvo());
        assertTrue("Starters was not UNCHANGED after reloading settings 4", settings.getStartersMod() == Settings.StartersMod.UNCHANGED);
    }

    /**
     * Selecting "RANDOM" enables the No Split Evos checkbox
     * Toggles the No Split Evo checkbox
     * Selecting "UNCHANGED" disables the No Split Evocheckbox and resets state to false
     * Verifies settings can be stored and loaded with no error and preserve state
     * @throws IOException
     */
    @Test
    public void TestNoSplitEvos() throws IOException {
        JRadioButtonFixture unchangedStarterRBFixture = getRadoiButtonByName("spUnchangedRB");
        JRadioButtonFixture randomStarterRBFixture = getRadoiButtonByName("spRandomRB");
        JCheckBoxFixture noSplitCBFixture = getCheckBoxByName("spNoSplitCB");
        // Sanity check - should evaluate to false
        Settings settings = this.mainWindow.getCurrentSettings();
        assertFalse("No Split Evo should not be set yet", settings.isStartersNoSplit());
        assertFalse("No Split Evo should not be enabled yet", noSplitCBFixture.isEnabled());
        assertTrue("Starters should be set to UNCHANGED but was not", settings.getStartersMod() == Settings.StartersMod.UNCHANGED);
        // Sanity check - Should not fail with 0 options
        String setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertFalse("No Split Evo was not false after reloading settings 0", settings.isStartersNoSplit());
        assertTrue("Starters was not UNCHANGED after reloading settings 0", settings.getStartersMod() == Settings.StartersMod.UNCHANGED);

        // Turn random starters on
        clickRBAndWait(randomStarterRBFixture);
        settings = this.mainWindow.getCurrentSettings();
        assertFalse("No Split Evo should not be set yet", settings.isStartersNoSplit());
        assertTrue("No Split Evo should be enabled now", noSplitCBFixture.isEnabled());
        assertTrue("Starters should be set to RANDOM but was not", settings.getStartersMod() == Settings.StartersMod.RANDOM);
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertFalse("No Split Evo was not false after reloading settings 1", settings.isStartersNoSplit());
        assertTrue("Starters was not RANDOM after reloading settings 1", settings.getStartersMod() == Settings.StartersMod.RANDOM);

        // Toggle No Split Evo on
        noSplitCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("No Split Evo should be set now", settings.isStartersNoSplit());
        assertTrue("No Split Evo should be enabled as state did not change", noSplitCBFixture.isEnabled());
        assertTrue("Starters should be set to RANDOM as state did not change", settings.getStartersMod() == Settings.StartersMod.RANDOM);
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertTrue("No Split Evo was not true after reloading settings 2", settings.isStartersNoSplit());
        assertTrue("Starters was not RANDOM after reloading settings 2", settings.getStartersMod() == Settings.StartersMod.RANDOM);

        // Toggle No Split Evo off
        noSplitCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertFalse("No Split Evo should be unset now", settings.isStartersNoSplit());
        assertTrue("No Split Evo should be enabled as state did not change", noSplitCBFixture.isEnabled());
        assertTrue("Starters should be set to RANDOM as state did not change", settings.getStartersMod() == Settings.StartersMod.RANDOM);
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertFalse("No Split Evo was not false after reloading settings 3", settings.isStartersNoSplit());
        assertTrue("Starters was not RANDOM after reloading settings 3", settings.getStartersMod() == Settings.StartersMod.RANDOM);

        // Turn random starter off while No Split Evo is true should set No Split Evo to false
        noSplitCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("No Split Evo should be set now", settings.isStartersNoSplit());
        assertTrue("No Split Evo should be enabled as state did not change", noSplitCBFixture.isEnabled());
        assertTrue("Starters should be set to RANDOM as state did not change", settings.getStartersMod() == Settings.StartersMod.RANDOM);
        clickRBAndWait(unchangedStarterRBFixture);
        settings = this.mainWindow.getCurrentSettings();
        assertFalse("No Split Evo should be unset now", settings.isStartersNoSplit());
        assertFalse("No Split Evo should be disabled now", noSplitCBFixture.isEnabled());
        assertTrue("Starters should be set to UNCHANGED but was not", settings.getStartersMod() == Settings.StartersMod.UNCHANGED);
        setttingsString = settings.toString();
        settings = Settings.fromString(setttingsString);        
        assertFalse("No Split Evo was not false after reloading settings 4", settings.isStartersNoSplit());
        assertTrue("Starters was not UNCHANGED after reloading settings 4", settings.getStartersMod() == Settings.StartersMod.UNCHANGED);
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
     * Clicks a JRadioButton and waits for the UI to update before completing
     * @param rbFixture - The fixture representing the radio button to click
     */
    private void clickRBAndWait(JRadioButtonFixture rbFixture) {
        rbFixture.requireVisible().requireEnabled().click();
        await().until(() -> this.mainWindow.isUIUpdated());
    }
}
