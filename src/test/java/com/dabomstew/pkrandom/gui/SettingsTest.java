package com.dabomstew.pkrandom.gui;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.swing.JCheckBox;

import com.dabomstew.pkrandom.MiscTweak;
import com.dabomstew.pkrandom.Settings;
import com.dabomstew.pkrandom.romhandlers.RomHandler;

import org.junit.Test;

public class SettingsTest {

    /**
     * Toggling Gen 6 does not toggle Gen 5
     * Gen 5 available without Gen 6
     * 
     * @throws IOException
     */
    @Test
    public void TestGen5Separation() throws IOException {
        RandomizerGUI rg = spy(new RandomizerGUI(false, false, true));
        RomHandler romhandler = mock(RomHandler.class);
        doReturn(romhandler).when(rg).getRomHandler();
        Settings settings = rg.getCurrentSettings();
        // Sanity check - Should initialize to False
        assertFalse(settings.isUpdateMoves());
        assertFalse(settings.isUpdateMovesLegacy());

        // Get the checkboxes and enable them to mimic romLoaded
        JCheckBox gen5CB = (JCheckBox) rg.getField("updateMovesLegacy");
        JCheckBox gen6CB = (JCheckBox) rg.getField("updateMoves");
        gen5CB.setEnabled(true);
        gen6CB.setEnabled(true);

        // Toggle Gen 5
        gen5CB.setSelected(true);
        rg.enableOrDisableSubControls();
        settings = rg.getCurrentSettings();
        assertFalse(settings.isUpdateMoves());
        assertTrue(settings.isUpdateMovesLegacy());
        assertTrue(gen5CB.isEnabled());
        assertTrue(gen6CB.isEnabled());

        // Toggle Gen 5 + Gen 6
        gen6CB.setSelected(true);
        rg.enableOrDisableSubControls();
        settings = rg.getCurrentSettings();
        assertTrue(settings.isUpdateMoves());
        assertTrue(settings.isUpdateMovesLegacy());
        assertTrue(gen5CB.isEnabled());
        assertTrue(gen6CB.isEnabled());

        // Toggle Gen 6
        gen5CB.setSelected(false);
        rg.enableOrDisableSubControls();
        settings = rg.getCurrentSettings();
        assertTrue(settings.isUpdateMoves());
        assertFalse(settings.isUpdateMovesLegacy());
        assertTrue(gen5CB.isEnabled());
        assertTrue(gen6CB.isEnabled());
    }

    /**
     * Selecting USE_RESISTANT_TYPE evaluates to true Not selecting evaluates to
     * false
     * 
     * @throws IOException
     */
    @Test
    public void TestUseResistantType() throws IOException {
        RandomizerGUI rg = spy(new RandomizerGUI(false, false, true));
        RomHandler romhandler = mock(RomHandler.class);
        doReturn(romhandler).when(rg).getRomHandler();
        Settings settings = rg.getCurrentSettings();
        // Sanity check - should evaluate to false
        assertTrue("Misc Tweaks should not be set yet", settings.getCurrentMiscTweaks() == 0);

        // Turn USE_RESISTANT_TYPE to true
        int mTweaks = 0;
        mTweaks |= MiscTweak.USE_RESISTANT_TYPE.getValue();
        settings.setCurrentMiscTweaks(mTweaks);
        assertTrue("USE_RESISTANT_TYPE should evaluate to true", 
            (settings.getCurrentMiscTweaks() & MiscTweak.USE_RESISTANT_TYPE.getValue()) > 0);

        // Turn USE_RESISTANT_TYPE to false
        mTweaks = 0;
        mTweaks |= MiscTweak.ALLOW_PIKACHU_EVOLUTION.getValue();
        settings.setCurrentMiscTweaks(mTweaks);
        assertFalse("USE_RESISTANT_TYPE should evaluate to false", 
        (settings.getCurrentMiscTweaks() & MiscTweak.USE_RESISTANT_TYPE.getValue()) > 0);
    }
}
