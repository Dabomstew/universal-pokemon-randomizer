/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dabomstew.pkrandom.gui;

/*----------------------------------------------------------------------------*/
/*--  PresetLoadDialog.java - a dialog to allow use of preset files or 		--*/
/*--					      random seed/config string pairs to produce	--*/
/*--						  premade ROMs.									--*/
/*--  																		--*/
/*--  Part of "Universal Pokemon Randomizer" by Dabomstew					--*/
/*--  Pokemon and any associated names and the like are						--*/
/*--  trademark and (C) Nintendo 1996-2012.									--*/
/*--  																		--*/
/*--  The custom code written here is licensed under the terms of the GPL:	--*/
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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.dabomstew.pkrandom.romhandlers.RomHandler;

/**
 * 
 * @author Stewart
 */
public class PresetLoadDialog extends javax.swing.JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7898067118947765260L;
	private RandomizerGUI parentGUI;
	private RomHandler currentROM;
	private boolean completed = false;
	private String requiredName = null;
	private volatile boolean changeFieldsWithoutCheck = false;
	private byte[] trainerClasses = null, trainerNames = null, nicknames = null;

	/**
	 * Creates new form PresetLoadDialog
	 */
	public PresetLoadDialog(RandomizerGUI parent) {
		super(parent, true);
		initComponents();
		this.parentGUI = parent;
		this.presetFileChooser.setCurrentDirectory(new File("./"));
		this.romFileChooser.setCurrentDirectory(new File("./"));
		initialState();
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	private void initialState() {
		this.romFileButton.setEnabled(false);
		this.acceptButton.setEnabled(false);
		addChangeListener(this.randomSeedField);
		addChangeListener(this.configStringField);
	}

	private void addChangeListener(JTextField field) {
		field.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (!changeFieldsWithoutCheck)
					PresetLoadDialog.this.checkValues();

			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (!changeFieldsWithoutCheck)
					PresetLoadDialog.this.checkValues();

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (!changeFieldsWithoutCheck)
					PresetLoadDialog.this.checkValues();

			}
		});

	}

	protected boolean checkValues() {
		String name;
		try {
			Long.parseLong(this.randomSeedField.getText());
		} catch (NumberFormatException ex) {
			invalidValues();
			return false;
		}
		try {
			name = this.parentGUI.getValidRequiredROMName(
					this.configStringField.getText(), trainerClasses,
					trainerNames, nicknames);
		} catch (InvalidSupplementFilesException ex) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					changeFieldsWithoutCheck = true;
					configStringField.setText("");
					randomSeedField.setText("");
					changeFieldsWithoutCheck = false;
				}
			});
			invalidValues();
			return false;
		} catch (Exception ex) {
			// other exception
			ex.printStackTrace();
			invalidValues();
			return false;
		}
		if (name == null) {
			invalidValues();
			return false;
		}
		requiredName = name;
		this.romRequiredLabel.setText("ROM Required: " + name);
		this.romFileButton.setEnabled(true);

		if (currentROM != null && currentROM.getROMName().equals(name) == false) {
			this.currentROM = null;
			this.acceptButton.setEnabled(false);
			this.romFileField.setText("");
		}
		return true;
	}

	private void invalidValues() {
		this.currentROM = null;
		this.romFileField.setText("");
		this.romRequiredLabel
				.setText("ROM Required: Enter settings above first.");
		this.romFileButton.setEnabled(false);
		this.acceptButton.setEnabled(false);
		this.requiredName = null;

	}

	public boolean isCompleted() {
		return completed;
	}

	public RomHandler getROM() {
		return currentROM;
	}

	public long getSeed() {
		return Long.parseLong(this.randomSeedField.getText());
	}

	public String getConfigString() {
		return this.configStringField.getText();
	}

	public byte[] getTrainerClasses() {
		return trainerClasses;
	}

	public byte[] getTrainerNames() {
		return trainerNames;
	}
	
	public byte[] getNicknames() {
		return nicknames;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	private void initComponents() {

		presetFileChooser = new javax.swing.JFileChooser();
		romFileChooser = new javax.swing.JFileChooser();
		presetFileLabel = new javax.swing.JLabel();
		presetFileField = new javax.swing.JTextField();
		presetFileButton = new javax.swing.JButton();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		randomSeedField = new javax.swing.JTextField();
		jLabel3 = new javax.swing.JLabel();
		configStringField = new javax.swing.JTextField();
		romRequiredLabel = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		romFileField = new javax.swing.JTextField();
		romFileButton = new javax.swing.JButton();
		acceptButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();

		presetFileChooser.setFileFilter(new PresetFileFilter());

		romFileChooser.setFileFilter(new ROMFilter());

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Use Premade Seed");
		setModal(true);
		setResizable(false);

		presetFileLabel.setText("Seed File:");

		presetFileField.setEditable(false);

		presetFileButton.setText("...");
		presetFileButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				presetFileButtonActionPerformed(evt);
			}
		});

		jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel1.setText("-OR-");

		jLabel2.setText("Random Seed:");

		jLabel3.setText("Config String:");

		romRequiredLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		romRequiredLabel.setText("ROM Required: Enter settings above first.");

		jLabel5.setText("Rom File:");

		romFileField.setEditable(false);

		romFileButton.setText("...");
		romFileButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				romFileButtonActionPerformed(evt);
			}
		});

		acceptButton.setText("Apply Randomization Settings");
		acceptButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				acceptButtonActionPerformed(evt);
			}
		});

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.TRAILING,
												false)
												.addComponent(
														jLabel5,
														javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addComponent(
														presetFileLabel,
														javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addComponent(
														jLabel2,
														javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addComponent(
														jLabel3,
														javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE))
								.addGap(18, 18, 18)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		acceptButton)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																		169,
																		Short.MAX_VALUE)
																.addComponent(
																		cancelButton))
												.addComponent(randomSeedField)
												.addComponent(configStringField)
												.addComponent(presetFileField)
												.addComponent(romFileField))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING,
												false)
												.addComponent(
														presetFileButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														26,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(
														romFileButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														1, Short.MAX_VALUE))
								.addGap(12, 12, 12))
				.addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(romRequiredLabel,
						javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(presetFileLabel)
												.addComponent(
														presetFileField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(presetFileButton))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jLabel1)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jLabel2)
												.addComponent(
														randomSeedField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jLabel3)
												.addComponent(
														configStringField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(romRequiredLabel)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jLabel5)
												.addComponent(
														romFileField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(romFileButton))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED,
										23, Short.MAX_VALUE)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(acceptButton)
												.addComponent(cancelButton))
								.addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void presetFileButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_presetFileButtonActionPerformed
		presetFileChooser.setSelectedFile(null);
		int returnVal = presetFileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File fh = presetFileChooser.getSelectedFile();
			try {
				DataInputStream dis = new DataInputStream(new FileInputStream(
						fh));
				int checkByte = dis.readByte() & 0xFF;
				if (checkByte != RandomizerGUI.PRESET_FILE_VERSION) {
					dis.close();
					JOptionPane
							.showMessageDialog(this,
									"This seed file is not for this version of the randomizer.");
					return;
				}
				long seed = dis.readLong();
				String preset = dis.readUTF();
				int tclen = dis.readInt();
				trainerClasses = new byte[tclen];
				dis.read(trainerClasses);
				int tnlen = dis.readInt();
				trainerNames = new byte[tnlen];
				dis.read(trainerNames);
				int nnlen = dis.readInt();
				nicknames = new byte[nnlen];
				dis.read(nicknames);
				changeFieldsWithoutCheck = true;
				this.randomSeedField.setText(Long.toString(seed));
				this.configStringField.setText(preset);
				changeFieldsWithoutCheck = false;
				if (checkValues()) {
					this.randomSeedField.setEnabled(false);
					this.configStringField.setEnabled(false);
					this.presetFileField.setText(fh.getAbsolutePath());
				} else {
					this.randomSeedField.setText("");
					this.configStringField.setText("");
					this.randomSeedField.setEnabled(true);
					this.configStringField.setEnabled(true);
					this.presetFileField.setText("");
					trainerClasses = null;
					trainerNames = null;
					JOptionPane.showMessageDialog(this,
							"The seed file did not contain valid settings.");
				}
				dis.close();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this,
						"Could not load seed file.");
			}
		}
	}// GEN-LAST:event_presetFileButtonActionPerformed

	private void romFileButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_romFileButtonActionPerformed
		romFileChooser.setSelectedFile(null);
		int returnVal = romFileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			final File fh = romFileChooser.getSelectedFile();
			for (RomHandler rh : parentGUI.checkHandlers) {
				if (rh.detectRom(fh.getAbsolutePath())) {
					final RomHandler checkHandler = rh;
					final JDialog opDialog = new OperationDialog("Loading...",
							this, true);
					Thread t = new Thread() {
						@Override
						public void run() {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									opDialog.setVisible(true);
								}
							});
							try {
								checkHandler.loadRom(fh.getAbsolutePath());
							} catch (Exception ex) {
								JOptionPane.showMessageDialog(
										PresetLoadDialog.this,
										"ROM load failed.");
							}
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									opDialog.setVisible(false);
									if (checkHandler.getROMName().equals(
											requiredName)) {
										// Got it
										romFileField.setText(fh
												.getAbsolutePath());
										currentROM = checkHandler;
										acceptButton.setEnabled(true);
										return;
									} else {
										JOptionPane.showMessageDialog(
												PresetLoadDialog.this,
												"This isn't the required ROM.\nRequired: "
														+ requiredName
														+ "\nThis ROM: "
														+ checkHandler
																.getROMName());
										return;
									}
								}
							});
						}
					};
					t.start();
					return;
				}
			}
			JOptionPane.showMessageDialog(this,
					"The file you specified isn't a valid Pokemon ROM.");
		}
	}// GEN-LAST:event_romFileButtonActionPerformed

	private void acceptButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_acceptButtonActionPerformed
		completed = true;
		this.setVisible(false);
	}// GEN-LAST:event_acceptButtonActionPerformed

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelButtonActionPerformed
		completed = false;
		this.setVisible(false);
	}// GEN-LAST:event_cancelButtonActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton acceptButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JTextField configStringField;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JButton presetFileButton;
	private javax.swing.JFileChooser presetFileChooser;
	private javax.swing.JTextField presetFileField;
	private javax.swing.JLabel presetFileLabel;
	private javax.swing.JTextField randomSeedField;
	private javax.swing.JButton romFileButton;
	private javax.swing.JFileChooser romFileChooser;
	private javax.swing.JTextField romFileField;
	private javax.swing.JLabel romRequiredLabel;
	// End of variables declaration//GEN-END:variables
}
