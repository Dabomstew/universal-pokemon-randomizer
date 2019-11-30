package com.dabomstew.pkrandom.gui;

/*----------------------------------------------------------------------------*/
/*--  CustomNamesEditorDialog.java - a GUI interface to allow users to edit --*/
/*--                                 their custom names for trainers etc.   --*/
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.dabomstew.pkrandom.CustomNamesSet;
import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.SysConstants;

public class CustomNamesEditorDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = -1421503126547242929L;
    private boolean pendingChanges;

    /**
     * Creates new form CustomNamesEditorDialog
     */
    public CustomNamesEditorDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        setLocationRelativeTo(parent);

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                setVisible(true);
            }
        });

        // load trainer names etc
        try {
            CustomNamesSet cns = FileFunctions.getCustomNames();
            populateNames(trainerNamesText, cns.getTrainerNames());
            populateNames(trainerClassesText, cns.getTrainerClasses());
            populateNames(doublesTrainerNamesText, cns.getDoublesTrainerNames());
            populateNames(doublesTrainerClassesText, cns.getDoublesTrainerClasses());
            populateNames(nicknamesText, cns.getPokemonNicknames());
        } catch (IOException ex) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(CustomNamesEditorDialog.this,
                            "Your custom names file is for a different randomizer version or otherwise corrupt.");
                }
            });
        }

        // dialog if there's no custom names file yet
        if (!new File(SysConstants.ROOT_PATH + SysConstants.customNamesFile).exists()) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(
                            CustomNamesEditorDialog.this,
                            String.format(
                                    "Welcome to the custom names editor!\nThis is where you can edit the names used for options like \"Randomize Trainer Names\".\nThe names are initially populated with a few default names included with the randomizer.\nYou can share your customized name sets with others, too!\nJust send them the %s file created in the randomizer directory.",
                                    SysConstants.customNamesFile));
                }
            });
        }

        pendingChanges = false;

        addDocListener(trainerNamesText);
        addDocListener(trainerClassesText);
        addDocListener(doublesTrainerNamesText);
        addDocListener(doublesTrainerClassesText);
        addDocListener(nicknamesText);
    }

    private void addDocListener(JTextArea textArea) {
        textArea.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                pendingChanges = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                pendingChanges = true;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                pendingChanges = true;
            }
        });

    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowClosing
        attemptClose();
    }// GEN-LAST:event_formWindowClosing

    private void saveBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveBtnActionPerformed
        save();
    }// GEN-LAST:event_saveBtnActionPerformed

    private void closeBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_closeBtnActionPerformed
        attemptClose();
    }// GEN-LAST:event_closeBtnActionPerformed

    private boolean save() {
        CustomNamesSet cns = new CustomNamesSet();
        cns.setTrainerNames(getNameList(trainerNamesText));
        cns.setTrainerClasses(getNameList(trainerClassesText));
        cns.setDoublesTrainerNames(getNameList(doublesTrainerNamesText));
        cns.setDoublesTrainerClasses(getNameList(doublesTrainerClassesText));
        cns.setPokemonNicknames(getNameList(nicknamesText));
        try {
            byte[] data = cns.getBytes();
            FileFunctions.writeBytesToFile(SysConstants.customNamesFile, data);
            pendingChanges = false;
            JOptionPane.showMessageDialog(this, "Custom names saved.");
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save changes.");
            return false;
        }
    }

    private void attemptClose() {
        if (pendingChanges) {
            int result = JOptionPane
                    .showConfirmDialog(this,
                            "You've made some unsaved changes to your custom names.\nDo you want to save them before closing the editor?");
            if (result == JOptionPane.YES_OPTION) {
                if (save()) {
                    dispose();
                }
            } else if (result == JOptionPane.NO_OPTION) {
                dispose();
            }
        } else {
            dispose();
        }
    }

    private List<String> getNameList(JTextArea textArea) {
        String contents = textArea.getText();
        // standardize newlines
        contents = contents.replace("\r\n", "\n");
        contents = contents.replace("\r", "\n");
        // split by them
        String[] names = contents.split("\n");
        List<String> results = new ArrayList<String>();
        for (String name : names) {
            String ln = name.trim();
            if (!ln.isEmpty()) {
                results.add(ln);
            }
        }
        return results;
    }

    private void populateNames(JTextArea textArea, List<String> names) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String name : names) {
            if (!first) {
                sb.append(SysConstants.LINE_SEP);
            }
            first = false;
            sb.append(name);
        }
        textArea.setText(sb.toString());
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

        editorTabsPane = new javax.swing.JTabbedPane();
        trainerNamesSP = new javax.swing.JScrollPane();
        trainerNamesText = new javax.swing.JTextArea();
        trainerClassesSP = new javax.swing.JScrollPane();
        trainerClassesText = new javax.swing.JTextArea();
        doublesTrainerNamesSP = new javax.swing.JScrollPane();
        doublesTrainerNamesText = new javax.swing.JTextArea();
        doublesTrainerClassesSP = new javax.swing.JScrollPane();
        doublesTrainerClassesText = new javax.swing.JTextArea();
        nicknamesSP = new javax.swing.JScrollPane();
        nicknamesText = new javax.swing.JTextArea();
        saveBtn = new javax.swing.JButton();
        closeBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/dabomstew/pkrandom/gui/Bundle"); // NOI18N
        setTitle(bundle.getString("CustomNamesEditorDialog.title")); // NOI18N
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        trainerNamesSP.setHorizontalScrollBar(null);

        trainerNamesText.setColumns(20);
        trainerNamesText.setRows(5);
        trainerNamesSP.setViewportView(trainerNamesText);

        editorTabsPane.addTab(bundle.getString("CustomNamesEditorDialog.trainerNamesSP.TabConstraints.tabTitle"), trainerNamesSP); // NOI18N

        trainerClassesSP.setHorizontalScrollBar(null);

        trainerClassesText.setColumns(20);
        trainerClassesText.setRows(5);
        trainerClassesSP.setViewportView(trainerClassesText);

        editorTabsPane.addTab(bundle.getString("CustomNamesEditorDialog.trainerClassesSP.TabConstraints.tabTitle"), trainerClassesSP); // NOI18N

        doublesTrainerNamesSP.setHorizontalScrollBar(null);

        doublesTrainerNamesText.setColumns(20);
        doublesTrainerNamesText.setRows(5);
        doublesTrainerNamesSP.setViewportView(doublesTrainerNamesText);

        editorTabsPane.addTab(bundle.getString("CustomNamesEditorDialog.doublesTrainerNamesSP.TabConstraints.tabTitle"), doublesTrainerNamesSP); // NOI18N

        doublesTrainerClassesSP.setHorizontalScrollBar(null);

        doublesTrainerClassesText.setColumns(20);
        doublesTrainerClassesText.setRows(5);
        doublesTrainerClassesSP.setViewportView(doublesTrainerClassesText);

        editorTabsPane.addTab(bundle.getString("CustomNamesEditorDialog.doublesTrainerClassesSP.TabConstraints.tabTitle"), doublesTrainerClassesSP); // NOI18N

        nicknamesSP.setHorizontalScrollBar(null);

        nicknamesText.setColumns(20);
        nicknamesText.setRows(5);
        nicknamesSP.setViewportView(nicknamesText);

        editorTabsPane.addTab(bundle.getString("CustomNamesEditorDialog.nicknamesSP.TabConstraints.tabTitle"), nicknamesSP); // NOI18N

        saveBtn.setText(bundle.getString("CustomNamesEditorDialog.saveBtn.text")); // NOI18N
        saveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBtnActionPerformed(evt);
            }
        });

        closeBtn.setText(bundle.getString("CustomNamesEditorDialog.closeBtn.text")); // NOI18N
        closeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(editorTabsPane, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(saveBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(closeBtn)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(editorTabsPane, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveBtn)
                    .addComponent(closeBtn))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeBtn;
    private javax.swing.JScrollPane doublesTrainerClassesSP;
    private javax.swing.JTextArea doublesTrainerClassesText;
    private javax.swing.JScrollPane doublesTrainerNamesSP;
    private javax.swing.JTextArea doublesTrainerNamesText;
    private javax.swing.JTabbedPane editorTabsPane;
    private javax.swing.JScrollPane nicknamesSP;
    private javax.swing.JTextArea nicknamesText;
    private javax.swing.JButton saveBtn;
    private javax.swing.JScrollPane trainerClassesSP;
    private javax.swing.JTextArea trainerClassesText;
    private javax.swing.JScrollPane trainerNamesSP;
    private javax.swing.JTextArea trainerNamesText;
    // End of variables declaration//GEN-END:variables
    /* @formatter:on */
}
