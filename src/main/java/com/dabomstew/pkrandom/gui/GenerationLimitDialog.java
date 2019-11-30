package com.dabomstew.pkrandom.gui;

import com.dabomstew.pkrandom.pokemon.GenRestrictions;

/*----------------------------------------------------------------------------*/
/*--  GenerationLimitDialog.java - Interface for disabling pokemon from     --*/
/*--                               being used in the results.               --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer" by Dabomstew                   --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2012.                                 --*/
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

public class GenerationLimitDialog extends javax.swing.JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 106783506965080925L;
    private boolean pressedOk;

    /**
     * Creates new form GenerationLimitDialog
     */
    public GenerationLimitDialog(RandomizerGUI parent, GenRestrictions current, int generation) {
        super(parent, true);
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
        gr.allow_gen1 = this.gen1CB.isSelected();
        gr.allow_gen2 = this.gen2CB.isSelected();
        gr.allow_gen3 = this.gen3CB.isSelected();
        gr.allow_gen4 = this.gen4CB.isSelected();
        gr.allow_gen5 = this.gen5CB.isSelected();

        gr.assoc_g1_g2 = this.g1Rg2CB.isSelected();
        gr.assoc_g1_g4 = this.g1Rg4CB.isSelected();

        gr.assoc_g2_g1 = this.g2Rg1CB.isSelected();
        gr.assoc_g2_g3 = this.g2Rg3CB.isSelected();
        gr.assoc_g2_g4 = this.g2Rg4CB.isSelected();

        gr.assoc_g3_g2 = this.g3Rg2CB.isSelected();
        gr.assoc_g3_g4 = this.g3Rg4CB.isSelected();

        gr.assoc_g4_g1 = this.g4Rg1CB.isSelected();
        gr.assoc_g4_g2 = this.g4Rg2CB.isSelected();
        gr.assoc_g4_g3 = this.g4Rg3CB.isSelected();

        return gr;
    }

    private void initialState(int generation) {
        if (generation < 2) {
            gen2CB.setVisible(false);
            g1Rg2CB.setVisible(false);
            g2Rg1CB.setVisible(false);
            g2Rg3CB.setVisible(false);
            g2Rg4CB.setVisible(false);
        }
        if (generation < 3) {
            gen3CB.setVisible(false);
            g2Rg3CB.setVisible(false);
            g3Rg2CB.setVisible(false);
            g3Rg4CB.setVisible(false);
        }
        if (generation < 4) {
            gen4CB.setVisible(false);
            g1Rg4CB.setVisible(false);
            g2Rg4CB.setVisible(false);
            g3Rg4CB.setVisible(false);
            g4Rg1CB.setVisible(false);
            g4Rg2CB.setVisible(false);
            g4Rg3CB.setVisible(false);
        }
        if (generation < 5) {
            gen5CB.setVisible(false);
        }
    }

    private void restoreFrom(GenRestrictions restrict) {
        gen1CB.setSelected(restrict.allow_gen1);
        gen2CB.setSelected(restrict.allow_gen2);
        gen3CB.setSelected(restrict.allow_gen3);
        gen4CB.setSelected(restrict.allow_gen4);
        gen5CB.setSelected(restrict.allow_gen5);

        g1Rg2CB.setSelected(restrict.assoc_g1_g2);
        g1Rg4CB.setSelected(restrict.assoc_g1_g4);

        g2Rg1CB.setSelected(restrict.assoc_g2_g1);
        g2Rg3CB.setSelected(restrict.assoc_g2_g3);
        g2Rg4CB.setSelected(restrict.assoc_g2_g4);

        g3Rg2CB.setSelected(restrict.assoc_g3_g2);
        g3Rg4CB.setSelected(restrict.assoc_g3_g4);

        g4Rg1CB.setSelected(restrict.assoc_g4_g1);
        g4Rg2CB.setSelected(restrict.assoc_g4_g2);
        g4Rg3CB.setSelected(restrict.assoc_g4_g3);
    }

    /* @formatter:off */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        includePokemonHeader = new javax.swing.JLabel();
        gen1CB = new javax.swing.JCheckBox();
        gen2CB = new javax.swing.JCheckBox();
        gen3CB = new javax.swing.JCheckBox();
        gen4CB = new javax.swing.JCheckBox();
        gen5CB = new javax.swing.JCheckBox();
        relatedPokemonHeader = new javax.swing.JLabel();
        g1Rg2CB = new javax.swing.JCheckBox();
        g1Rg4CB = new javax.swing.JCheckBox();
        g2Rg1CB = new javax.swing.JCheckBox();
        g2Rg3CB = new javax.swing.JCheckBox();
        g2Rg4CB = new javax.swing.JCheckBox();
        g3Rg2CB = new javax.swing.JCheckBox();
        g3Rg4CB = new javax.swing.JCheckBox();
        g4Rg1CB = new javax.swing.JCheckBox();
        g4Rg2CB = new javax.swing.JCheckBox();
        g4Rg3CB = new javax.swing.JCheckBox();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        warningRomHackLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/dabomstew/pkrandom/gui/Bundle"); // NOI18N
        setTitle(bundle.getString("GenerationLimitDialog.title")); // NOI18N

        includePokemonHeader.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        includePokemonHeader.setText(bundle.getString("GenerationLimitDialog.includePokemonHeader.text")); // NOI18N

        gen1CB.setText(bundle.getString("GenerationLimitDialog.gen1CB.text")); // NOI18N
        gen1CB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gen1CBActionPerformed(evt);
            }
        });

        gen2CB.setText(bundle.getString("GenerationLimitDialog.gen2CB.text")); // NOI18N
        gen2CB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gen2CBActionPerformed(evt);
            }
        });

        gen3CB.setText(bundle.getString("GenerationLimitDialog.gen3CB.text")); // NOI18N
        gen3CB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gen3CBActionPerformed(evt);
            }
        });

        gen4CB.setText(bundle.getString("GenerationLimitDialog.gen4CB.text")); // NOI18N
        gen4CB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gen4CBActionPerformed(evt);
            }
        });

        gen5CB.setText(bundle.getString("GenerationLimitDialog.gen5CB.text")); // NOI18N
        gen5CB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gen5CBActionPerformed(evt);
            }
        });

        relatedPokemonHeader.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        relatedPokemonHeader.setText(bundle.getString("GenerationLimitDialog.relatedPokemonHeader.text")); // NOI18N

        g1Rg2CB.setText(bundle.getString("GenerationLimitDialog.gen2Short")); // NOI18N

        g1Rg4CB.setText(bundle.getString("GenerationLimitDialog.gen4Short")); // NOI18N

        g2Rg1CB.setText(bundle.getString("GenerationLimitDialog.gen1Short")); // NOI18N

        g2Rg3CB.setText(bundle.getString("GenerationLimitDialog.gen3Short")); // NOI18N

        g2Rg4CB.setText(bundle.getString("GenerationLimitDialog.gen4Short")); // NOI18N

        g3Rg2CB.setText(bundle.getString("GenerationLimitDialog.gen2Short")); // NOI18N

        g3Rg4CB.setText(bundle.getString("GenerationLimitDialog.gen4Short")); // NOI18N

        g4Rg1CB.setText(bundle.getString("GenerationLimitDialog.gen1Short")); // NOI18N

        g4Rg2CB.setText(bundle.getString("GenerationLimitDialog.gen2Short")); // NOI18N

        g4Rg3CB.setText(bundle.getString("GenerationLimitDialog.gen3Short")); // NOI18N

        okButton.setText(bundle.getString("GenerationLimitDialog.okButton.text")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(bundle.getString("GenerationLimitDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        warningRomHackLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        warningRomHackLabel.setForeground(new java.awt.Color(255, 0, 0));
        warningRomHackLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        warningRomHackLabel.setText(bundle.getString("GenerationLimitDialog.warningRomHackLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(gen5CB)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(includePokemonHeader)
                                    .addComponent(gen1CB)
                                    .addComponent(gen2CB)
                                    .addComponent(gen3CB)
                                    .addComponent(gen4CB))
                                .addGap(106, 106, 106)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(g4Rg1CB)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(g4Rg2CB)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(g4Rg3CB))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(g3Rg2CB)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(g3Rg4CB))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(g2Rg1CB)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(g2Rg3CB)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(g2Rg4CB))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(g1Rg2CB)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(g1Rg4CB))
                                    .addComponent(relatedPokemonHeader))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton)
                        .addGap(32, 32, 32)
                        .addComponent(cancelButton)
                        .addGap(134, 134, 134))))
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(warningRomHackLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(includePokemonHeader)
                    .addComponent(relatedPokemonHeader))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(gen1CB)
                    .addComponent(g1Rg2CB)
                    .addComponent(g1Rg4CB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(gen2CB)
                    .addComponent(g2Rg1CB)
                    .addComponent(g2Rg3CB)
                    .addComponent(g2Rg4CB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(gen3CB)
                    .addComponent(g3Rg2CB)
                    .addComponent(g3Rg4CB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(gen4CB)
                    .addComponent(g4Rg1CB)
                    .addComponent(g4Rg2CB)
                    .addComponent(g4Rg3CB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(gen5CB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(warningRomHackLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void enableAndDisableBoxes() {

        // enable sub-boxes of checked main boxes
        g1Rg2CB.setEnabled(gen1CB.isSelected());
        g1Rg4CB.setEnabled(gen1CB.isSelected());
        g2Rg1CB.setEnabled(gen2CB.isSelected());
        g2Rg3CB.setEnabled(gen2CB.isSelected());
        g2Rg4CB.setEnabled(gen2CB.isSelected());
        g3Rg2CB.setEnabled(gen3CB.isSelected());
        g3Rg4CB.setEnabled(gen3CB.isSelected());
        g4Rg1CB.setEnabled(gen4CB.isSelected());
        g4Rg2CB.setEnabled(gen4CB.isSelected());
        g4Rg3CB.setEnabled(gen4CB.isSelected());

        // uncheck disabled subboxes
        if (!gen1CB.isSelected()) {
            g1Rg2CB.setSelected(false);
            g1Rg4CB.setSelected(false);
        }
        if (!gen2CB.isSelected()) {
            g2Rg1CB.setSelected(false);
            g2Rg3CB.setSelected(false);
            g2Rg4CB.setSelected(false);
        }
        if (!gen3CB.isSelected()) {
            g3Rg2CB.setSelected(false);
            g3Rg4CB.setSelected(false);
        }
        if (!gen4CB.isSelected()) {
            g4Rg1CB.setSelected(false);
            g4Rg2CB.setSelected(false);
            g4Rg3CB.setSelected(false);
        }

        // check and disable implied boxes
        if (gen1CB.isSelected()) {
            g2Rg1CB.setEnabled(false);
            g2Rg1CB.setSelected(true);
            g4Rg1CB.setEnabled(false);
            g4Rg1CB.setSelected(true);
        }

        if (gen2CB.isSelected()) {
            g1Rg2CB.setEnabled(false);
            g1Rg2CB.setSelected(true);
            g3Rg2CB.setEnabled(false);
            g3Rg2CB.setSelected(true);
            g4Rg2CB.setEnabled(false);
            g4Rg2CB.setSelected(true);
        }

        if (gen3CB.isSelected()) {
            g2Rg3CB.setEnabled(false);
            g2Rg3CB.setSelected(true);
            g4Rg3CB.setEnabled(false);
            g4Rg3CB.setSelected(true);
        }

        if (gen4CB.isSelected()) {
            g1Rg4CB.setEnabled(false);
            g1Rg4CB.setSelected(true);
            g2Rg4CB.setEnabled(false);
            g2Rg4CB.setSelected(true);
            g3Rg4CB.setEnabled(false);
            g3Rg4CB.setSelected(true);
        }
    }

    private void gen1CBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_gen1CBActionPerformed
        enableAndDisableBoxes();
    }// GEN-LAST:event_gen1CBActionPerformed

    private void gen2CBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_gen2CBActionPerformed
        enableAndDisableBoxes();
    }// GEN-LAST:event_gen2CBActionPerformed

    private void gen3CBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_gen3CBActionPerformed
        enableAndDisableBoxes();
    }// GEN-LAST:event_gen3CBActionPerformed

    private void gen4CBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_gen4CBActionPerformed
        enableAndDisableBoxes();
    }// GEN-LAST:event_gen4CBActionPerformed

    private void gen5CBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_gen5CBActionPerformed
        enableAndDisableBoxes();
    }// GEN-LAST:event_gen5CBActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_okButtonActionPerformed
        pressedOk=true;
        setVisible(false);
    }// GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelButtonActionPerformed
        pressedOk=false;
        setVisible(false);
    }// GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox g1Rg2CB;
    private javax.swing.JCheckBox g1Rg4CB;
    private javax.swing.JCheckBox g2Rg1CB;
    private javax.swing.JCheckBox g2Rg3CB;
    private javax.swing.JCheckBox g2Rg4CB;
    private javax.swing.JCheckBox g3Rg2CB;
    private javax.swing.JCheckBox g3Rg4CB;
    private javax.swing.JCheckBox g4Rg1CB;
    private javax.swing.JCheckBox g4Rg2CB;
    private javax.swing.JCheckBox g4Rg3CB;
    private javax.swing.JCheckBox gen1CB;
    private javax.swing.JCheckBox gen2CB;
    private javax.swing.JCheckBox gen3CB;
    private javax.swing.JCheckBox gen4CB;
    private javax.swing.JCheckBox gen5CB;
    private javax.swing.JLabel includePokemonHeader;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel relatedPokemonHeader;
    private javax.swing.JLabel warningRomHackLabel;
    // End of variables declaration//GEN-END:variables
    /* @formatter:on */
}
