package com.dabomstew.pkrandom.newgui;

/*----------------------------------------------------------------------------*/
/*--  NewGenerationLimitDialog.java - a GUI interface to allow users to     --*/
/*--                                  limit which Pokemon appear based on   --*/
/*--                                  their generation of origin.           --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
/*--  Originally part of "Universal Pokemon Randomizer" by Dabomstew        --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2020.                                 --*/
/*--                                                                        --*/
/*--  The custom code written here is licensed under the terms of the GPL:  --*/
/*--                                                                        --*/
/*--  This program is free software: you can redistribute it and/or modify  --*/
/*--  it under the terms of the GNU General Public License as published by  --*/
/*--  the Free Software Foundation, either version 3 of the License, or     --*/
/*--  (at your option) any later version.                                   --*/
/*--                                                                        --*/
/*--  This program is distributed in the hope that it will be useful,       --*/
/*--  but WITHOUT ANY WARRANTY; without even the implied warranty of        --*/
/*--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          --*/
/*--  GNU General Public License for more details.                          --*/
/*--                                                                        --*/
/*--  You should have received a copy of the GNU General Public License     --*/
/*--  along with this program. If not, see <http://www.gnu.org/licenses/>.  --*/
/*----------------------------------------------------------------------------*/

import com.dabomstew.pkrandom.pokemon.GenRestrictions;

import javax.swing.*;

public class NewGenerationLimitDialog extends javax.swing.JDialog {
    private JCheckBox gen1CheckBox;
    private JCheckBox gen2CheckBox;
    private JCheckBox gen3CheckBox;
    private JCheckBox gen4CheckBox;
    private JCheckBox gen5CheckBox;
    private JCheckBox gen6CheckBox;
    private JCheckBox gen7CheckBox;
    private JButton okButton;
    private JButton cancelButton;
    private JCheckBox gen1AssocGen2CheckBox;
    private JCheckBox gen1AssocGen4CheckBox;
    private JCheckBox gen1AssocGen6CheckBox;
    private JCheckBox gen2AssocGen1CheckBox;
    private JCheckBox gen2AssocGen3CheckBox;
    private JCheckBox gen2AssocGen4CheckBox;
    private JCheckBox gen3AssocGen2CheckBox;
    private JCheckBox gen3AssocGen4CheckBox;
    private JCheckBox gen4AssocGen1CheckBox;
    private JCheckBox gen4AssocGen2CheckBox;
    private JCheckBox gen4AssocGen3CheckBox;
    private JCheckBox gen6AssocGen1CheckBox;
    private JPanel mainPanel;
    private JLabel xyWarningLabel;

    private boolean pressedOk;
    private boolean isXY;

    public NewGenerationLimitDialog(JFrame parent, GenRestrictions current, int generation, boolean isXY) {
        super(parent, true);
        add(mainPanel);
        this.isXY = isXY;
        initComponents();
        initialState(generation);
        if (current != null) {
            current.limitToGen(generation);
            restoreFrom(current);
        }
        enableAndDisableBoxes();
        pressedOk = false;
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    public boolean pressedOK() {
        return pressedOk;
    }

    public GenRestrictions getChoice() {
        GenRestrictions gr = new GenRestrictions();
        gr.allow_gen1 = gen1CheckBox.isSelected();
        gr.allow_gen2 = gen2CheckBox.isSelected();
        gr.allow_gen3 = gen3CheckBox.isSelected();
        gr.allow_gen4 = gen4CheckBox.isSelected();
        gr.allow_gen5 = gen5CheckBox.isSelected();
        gr.allow_gen6 = gen6CheckBox.isSelected();
        gr.allow_gen7 = gen7CheckBox.isSelected();

        gr.assoc_g1_g2 = gen1AssocGen2CheckBox.isSelected();
        gr.assoc_g1_g4 = gen1AssocGen4CheckBox.isSelected();
        gr.assoc_g1_g6 = gen1AssocGen6CheckBox.isSelected();

        gr.assoc_g2_g1 = gen2AssocGen1CheckBox.isSelected();
        gr.assoc_g2_g3 = gen2AssocGen3CheckBox.isSelected();
        gr.assoc_g2_g4 = gen2AssocGen4CheckBox.isSelected();

        gr.assoc_g3_g2 = gen3AssocGen2CheckBox.isSelected();
        gr.assoc_g3_g4 = gen3AssocGen4CheckBox.isSelected();

        gr.assoc_g4_g1 = gen4AssocGen1CheckBox.isSelected();
        gr.assoc_g4_g2 = gen4AssocGen2CheckBox.isSelected();
        gr.assoc_g4_g3 = gen4AssocGen3CheckBox.isSelected();

        gr.assoc_g6_g1 = gen6AssocGen1CheckBox.isSelected();

        return gr;
    }

    private void initialState(int generation) {
        if (generation < 2) {
            gen2CheckBox.setVisible(false);
            gen1AssocGen2CheckBox.setVisible(false);
            gen2AssocGen1CheckBox.setVisible(false);
            gen2AssocGen3CheckBox.setVisible(false);
            gen2AssocGen4CheckBox.setVisible(false);
        }
        if (generation < 3) {
            gen3CheckBox.setVisible(false);
            gen2AssocGen3CheckBox.setVisible(false);
            gen3AssocGen2CheckBox.setVisible(false);
            gen3AssocGen4CheckBox.setVisible(false);
        }
        if (generation < 4) {
            gen4CheckBox.setVisible(false);
            gen1AssocGen4CheckBox.setVisible(false);
            gen2AssocGen4CheckBox.setVisible(false);
            gen3AssocGen4CheckBox.setVisible(false);
            gen4AssocGen1CheckBox.setVisible(false);
            gen4AssocGen2CheckBox.setVisible(false);
            gen4AssocGen3CheckBox.setVisible(false);
        }
        if (generation < 5) {
            gen5CheckBox.setVisible(false);
        }
        if (generation < 6) {
            gen6CheckBox.setVisible(false);
            gen1AssocGen6CheckBox.setVisible(false);
            gen6AssocGen1CheckBox.setVisible(false);
        }
        if (generation < 7) {
            gen7CheckBox.setVisible(false);
        }
    }

    private void restoreFrom(GenRestrictions restrict) {
        gen1CheckBox.setSelected(restrict.allow_gen1);
        gen2CheckBox.setSelected(restrict.allow_gen2);
        gen3CheckBox.setSelected(restrict.allow_gen3);
        gen4CheckBox.setSelected(restrict.allow_gen4);
        gen5CheckBox.setSelected(restrict.allow_gen5);
        gen6CheckBox.setSelected(restrict.allow_gen6);
        gen7CheckBox.setSelected(restrict.allow_gen7);

        gen1AssocGen2CheckBox.setSelected(restrict.assoc_g1_g2);
        gen1AssocGen4CheckBox.setSelected(restrict.assoc_g1_g4);
        gen1AssocGen6CheckBox.setSelected(restrict.assoc_g1_g6);

        gen2AssocGen1CheckBox.setSelected(restrict.assoc_g2_g1);
        gen2AssocGen3CheckBox.setSelected(restrict.assoc_g2_g3);
        gen2AssocGen4CheckBox.setSelected(restrict.assoc_g2_g4);

        gen3AssocGen2CheckBox.setSelected(restrict.assoc_g3_g2);
        gen3AssocGen4CheckBox.setSelected(restrict.assoc_g3_g4);

        gen4AssocGen1CheckBox.setSelected(restrict.assoc_g4_g1);
        gen4AssocGen2CheckBox.setSelected(restrict.assoc_g4_g2);
        gen4AssocGen3CheckBox.setSelected(restrict.assoc_g4_g3);

        gen6AssocGen1CheckBox.setSelected(restrict.assoc_g6_g1);
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/dabomstew/pkrandom/newgui/Bundle");
        setTitle(bundle.getString("GenerationLimitDialog.title"));
        gen1CheckBox.addActionListener(ev -> enableAndDisableBoxes());
        gen2CheckBox.addActionListener(ev -> enableAndDisableBoxes());
        gen3CheckBox.addActionListener(ev -> enableAndDisableBoxes());
        gen4CheckBox.addActionListener(ev -> enableAndDisableBoxes());
        gen5CheckBox.addActionListener(ev -> enableAndDisableBoxes());
        gen6CheckBox.addActionListener(ev -> enableAndDisableBoxes());
        gen7CheckBox.addActionListener(ev -> enableAndDisableBoxes());
        okButton.addActionListener(evt -> okButtonActionPerformed());
        cancelButton.addActionListener(evt -> cancelButtonActionPerformed());
        xyWarningLabel.setVisible(isXY);
        if (isXY) {
            okButton.setEnabled(false);
        }
        pack();
    }

    private void enableAndDisableBoxes() {
        // enable sub-boxes of checked main boxes
        gen1AssocGen2CheckBox.setEnabled(gen1CheckBox.isSelected());
        gen1AssocGen4CheckBox.setEnabled(gen1CheckBox.isSelected());
        gen1AssocGen6CheckBox.setEnabled(gen1CheckBox.isSelected());
        gen2AssocGen1CheckBox.setEnabled(gen2CheckBox.isSelected());
        gen2AssocGen3CheckBox.setEnabled(gen2CheckBox.isSelected());
        gen2AssocGen4CheckBox.setEnabled(gen2CheckBox.isSelected());
        gen3AssocGen2CheckBox.setEnabled(gen3CheckBox.isSelected());
        gen3AssocGen4CheckBox.setEnabled(gen3CheckBox.isSelected());
        gen4AssocGen1CheckBox.setEnabled(gen4CheckBox.isSelected());
        gen4AssocGen2CheckBox.setEnabled(gen4CheckBox.isSelected());
        gen4AssocGen3CheckBox.setEnabled(gen4CheckBox.isSelected());
        gen6AssocGen1CheckBox.setEnabled(gen6CheckBox.isSelected());

        // uncheck disabled sub-boxes
        if (!gen1CheckBox.isSelected()) {
            gen1AssocGen2CheckBox.setSelected(false);
            gen1AssocGen4CheckBox.setSelected(false);
            gen1AssocGen6CheckBox.setSelected(false);
        }
        if (!gen2CheckBox.isSelected()) {
            gen2AssocGen1CheckBox.setSelected(false);
            gen2AssocGen3CheckBox.setSelected(false);
            gen2AssocGen4CheckBox.setSelected(false);
        }
        if (!gen3CheckBox.isSelected()) {
            gen3AssocGen2CheckBox.setSelected(false);
            gen3AssocGen4CheckBox.setSelected(false);
        }
        if (!gen4CheckBox.isSelected()) {
            gen4AssocGen1CheckBox.setSelected(false);
            gen4AssocGen2CheckBox.setSelected(false);
            gen4AssocGen3CheckBox.setSelected(false);
        }
        if (!gen6CheckBox.isSelected()) {
            gen6AssocGen1CheckBox.setSelected(false);
        }

        // check and disable implied boxes
        if (gen1CheckBox.isSelected()) {
            gen2AssocGen1CheckBox.setEnabled(false);
            gen2AssocGen1CheckBox.setSelected(true);
            gen4AssocGen1CheckBox.setEnabled(false);
            gen4AssocGen1CheckBox.setSelected(true);
            gen6AssocGen1CheckBox.setEnabled(false);
            gen6AssocGen1CheckBox.setSelected(true);
        }

        if (gen2CheckBox.isSelected()) {
            gen1AssocGen2CheckBox.setEnabled(false);
            gen1AssocGen2CheckBox.setSelected(true);
            gen3AssocGen2CheckBox.setEnabled(false);
            gen3AssocGen2CheckBox.setSelected(true);
            gen4AssocGen2CheckBox.setEnabled(false);
            gen4AssocGen2CheckBox.setSelected(true);
        }

        if (gen3CheckBox.isSelected()) {
            gen2AssocGen3CheckBox.setEnabled(false);
            gen2AssocGen3CheckBox.setSelected(true);
            gen4AssocGen3CheckBox.setEnabled(false);
            gen4AssocGen3CheckBox.setSelected(true);
        }

        if (gen4CheckBox.isSelected()) {
            gen1AssocGen4CheckBox.setEnabled(false);
            gen1AssocGen4CheckBox.setSelected(true);
            gen2AssocGen4CheckBox.setEnabled(false);
            gen2AssocGen4CheckBox.setSelected(true);
            gen3AssocGen4CheckBox.setEnabled(false);
            gen3AssocGen4CheckBox.setSelected(true);
        }

        if (gen6CheckBox.isSelected()) {
            gen1AssocGen6CheckBox.setEnabled(false);
            gen1AssocGen6CheckBox.setSelected(true);
        }

        // To prevent softlocks on the Successor Korrina fight, only turn
        // on the OK button for XY if at least one of Gens 1-4 is selected.
        if (isXY) {
            if (gen1CheckBox.isSelected() || gen2CheckBox.isSelected() || gen3CheckBox.isSelected() || gen4CheckBox.isSelected()) {
                okButton.setEnabled(true);
            } else {
                okButton.setEnabled(false);
            }
        }
    }

    private void okButtonActionPerformed() {
        pressedOk = true;
        setVisible(false);
    }

    private void cancelButtonActionPerformed() {
        pressedOk = false;
        setVisible(false);
    }
}
