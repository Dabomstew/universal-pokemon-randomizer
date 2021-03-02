package com.dabomstew.pkrandom.gui;

import static org.junit.Assert.*;

import java.awt.Component;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JRadioButton;

import com.dabomstew.pkrandom.MiscTweak;
import com.dabomstew.pkrandom.Settings;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.fixture.JCheckBoxFixture;
import org.assertj.swing.fixture.JRadioButtonFixture;
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
        JCheckBoxFixture updateMovesCBFixture = this.frame.checkBox(new GenericTypeMatcher(JCheckBox.class, true) {
            @Override
            protected boolean isMatching(Component component) {
                if (component.getName() != null && component.getName().equals("goUpdateMovesCheckBox")) {
                    return true;
                }
                return false;
            }
        });
        JCheckBoxFixture updateMovesLegacyCBFixture = this.frame.checkBox(new GenericTypeMatcher(JCheckBox.class, true) {
            @Override
            protected boolean isMatching(Component component) {
                if (component.getName() != null && component.getName().equals("goUpdateMovesLegacyCheckBox")) {
                    return true;
                }
                return false;
            }
        });
        Settings settings = this.mainWindow.getCurrentSettings();
        // Sanity check - Should initialize to False
        assertFalse("Update Moves started as selected", settings.isUpdateMoves());
        assertFalse("Update Moves Legacy started as selected", settings.isUpdateMovesLegacy());

        // Toggle Gen 5
        updateMovesLegacyCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertFalse("Update Moves was selected even though it was not clicked", settings.isUpdateMoves());
        assertTrue("Update Moves Legacy was not selected even though it was clicked", settings.isUpdateMovesLegacy());
        assertTrue("Update Moves is disabled but was expected to be enabled", updateMovesCBFixture.requireVisible().isEnabled());
        assertTrue("Update Moves Legacy is disabled but was expected to be enabled", updateMovesLegacyCBFixture.requireVisible().isEnabled());

        // Toggle Gen 5 + Gen 6
        updateMovesCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Update Moves was not selected even though it was clicked", settings.isUpdateMoves());
        assertTrue("Update Moves Legacy was not selected even though state was not changed", settings.isUpdateMovesLegacy());
        assertTrue("Update Moves is disabled but was expected to be enabled", updateMovesCBFixture.requireVisible().isEnabled());
        assertTrue("Update Moves Legacy is disabled but was expected to be enabled", updateMovesLegacyCBFixture.requireVisible().isEnabled());

        // Toggle Gen 5 off leaving Gen 6
        updateMovesLegacyCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("Update Moves was not selected even though state was not changed", settings.isUpdateMoves());
        assertFalse("Update Moves Legacy was selected even though it was toggled off", settings.isUpdateMovesLegacy());
        assertTrue("Update Moves is disabled but was expected to be enabled", updateMovesCBFixture.requireVisible().isEnabled());
        assertTrue("Update Moves Legacy is disabled but was expected to be enabled", updateMovesLegacyCBFixture.requireVisible().isEnabled());

        //Toggle Gen 6 off leaving nothing
        updateMovesCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertFalse("Update Moves was selected even though it was toggled off", settings.isUpdateMoves());
        assertFalse("Update Moves Legacy was selected even though state was not changed", settings.isUpdateMovesLegacy());
        assertTrue("Update Moves is disabled but was expected to be enabled", updateMovesCBFixture.requireVisible().isEnabled());
        assertTrue("Update Moves Legacy is disabled but was expected to be enabled", updateMovesLegacyCBFixture.requireVisible().isEnabled());
    }

    /**
     * Selecting USE_RESISTANT_TYPE evaluates to true Not selecting evaluates to
     * false
     * 
     * @throws IOException
     */
    @Test(timeout = 4000)
    public void TestUseResistantType() throws IOException {
        JCheckBoxFixture resistantTypeCBFixture = this.frame.checkBox(new GenericTypeMatcher(JCheckBox.class, true) {
            @Override
            protected boolean isMatching(Component component) {
                if (component.getName() != null && component.getName().equals("Use Resistant Type")) {
                    return true;
                }
                return false;
            }
        });
        Settings settings = this.mainWindow.getCurrentSettings();
        // Sanity check - should evaluate to false
        assertTrue("Misc Tweaks should not be set yet", settings.getCurrentMiscTweaks() == 0);

        // Turn USE_RESISTANT_TYPE to true
        resistantTypeCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("USE_RESISTANT_TYPE should evaluate to true", 
            (settings.getCurrentMiscTweaks() & MiscTweak.USE_RESISTANT_TYPE.getValue()) > 0);

        // Turn USE_RESISTANT_TYPE to false
        resistantTypeCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertFalse("USE_RESISTANT_TYPE should evaluate to false", 
            (settings.getCurrentMiscTweaks() & MiscTweak.USE_RESISTANT_TYPE.getValue()) > 0);
    }

    /**
     * Selecting "RANDOM" opens up the "Change Methods" option
     * Change Methods correctly updates settings
     * Toggling the "RANDOM" evolution mod radio button disables "Change Methods"
     * 
     * @throws IOException
     */
    @Test
    public void TestChangeMethods() throws IOException {
        JRadioButtonFixture unchangedEvoRBFixture = this.frame.radioButton(new GenericTypeMatcher(JRadioButton.class, true) {
            @Override
            protected boolean isMatching(Component component) {
                if (component.getName() != null && component.getName().equals("peUnchangedRB")) {
                    return true;
                }
                return false;
            }
        });
        JRadioButtonFixture randomEvoRBFixture = this.frame.radioButton(new GenericTypeMatcher(JRadioButton.class, true) {
            @Override
            protected boolean isMatching(Component component) {
                if (component.getName() != null && component.getName().equals("peRandomRB")) {
                    return true;
                }
                return false;
            }
        });
        JCheckBoxFixture changeMethodsCBFixture = this.frame.checkBox(new GenericTypeMatcher(JCheckBox.class, true) {
            @Override
            protected boolean isMatching(Component component) {
                if (component.getName() != null && component.getName().equals("peChangeMethodsCB")) {
                    return true;
                }
                return false;
            }
        });
        Settings settings = this.mainWindow.getCurrentSettings();
        // Sanity check - should evaluate to false
        assertFalse("Change Methods should not be set yet", settings.isEvosChangeMethod());
        assertFalse("Change Methods should not be enabled yet", changeMethodsCBFixture.isEnabled());
        assertTrue("Evolutions should be set to UNCHANGED but was not", settings.getEvolutionsMod() == Settings.EvolutionsMod.UNCHANGED);

        // Turn randomEvos on
        randomEvoRBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertFalse("Change Methods should not be set yet", settings.isEvosChangeMethod());
        assertTrue("Change Methods should be enabled now", changeMethodsCBFixture.isEnabled());
        assertTrue("Evolutions were not set to RANDOM", settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM);

        // Turn evosChangeMethod to true
        changeMethodsCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("evosChangeMethod should evaluate to true", settings.isEvosChangeMethod());

        // Turn evosChangeMethod to false
        changeMethodsCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertFalse("evosChangeMethod should evaluate to false", settings.isEvosChangeMethod());

        // Turn randomEvos off while evosChangeMethod is true should turn evosChangeMethod to false
        changeMethodsCBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertTrue("evosChangeMethod should evaluate to true", settings.isEvosChangeMethod());
        assertTrue("Evolutions should be set to RANDOM as state did not change", settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM);
        unchangedEvoRBFixture.requireVisible().requireEnabled().click();
        settings = this.mainWindow.getCurrentSettings();
        assertFalse("evosChangeMethod should evaluate to false", settings.isEvosChangeMethod());
        assertFalse("Change Methods should be disabled now", changeMethodsCBFixture.isEnabled());
        assertTrue("Evolutions should be set to UNCHANGED but was not", settings.getEvolutionsMod() == Settings.EvolutionsMod.UNCHANGED);
    } 
}
