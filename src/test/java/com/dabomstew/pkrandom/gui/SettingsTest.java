package com.dabomstew.pkrandom.gui;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.swing.JCheckBox;

import com.dabomstew.pkrandom.Settings;
import com.dabomstew.pkrandom.romhandlers.RomHandler;

import org.junit.Test;

public class SettingsTest {
    @Test
    public void TestGen5Separation() throws IOException {
        // Toggling Gen 6 does not toggle Gen 5
        // Gen 5 available without Gen 6
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
}
