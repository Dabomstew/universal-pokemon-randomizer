/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dabomstew.pkrandom.gui;

/*----------------------------------------------------------------------------*/
/*--  RandomizerGUI.java - the main GUI for the randomizer, containing the	--*/
/*--					   various options available and such.				--*/
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.zip.CRC32;

import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.bind.DatatypeConverter;

import com.dabomstew.pkrandom.CodeTweaks;
import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.RandomSource;
import com.dabomstew.pkrandom.pokemon.Encounter;
import com.dabomstew.pkrandom.pokemon.EncounterSet;
import com.dabomstew.pkrandom.pokemon.GenRestrictions;
import com.dabomstew.pkrandom.pokemon.IngameTrade;
import com.dabomstew.pkrandom.pokemon.Move;
import com.dabomstew.pkrandom.pokemon.MoveLearnt;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.TrainerPokemon;
import com.dabomstew.pkrandom.romhandlers.AbstractDSRomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen1RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen2RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen3RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen4RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen5RomHandler;
import com.dabomstew.pkrandom.romhandlers.RomHandler;

/**
 * 
 * @author Stewart
 */
public class RandomizerGUI extends javax.swing.JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 637989089525556154L;
	private RomHandler romHandler;
	protected RomHandler[] checkHandlers;
	public static final int PRESET_FILE_VERSION = 161;
	public static final int UPDATE_VERSION = 1610;

	public static PrintStream verboseLog = System.out;

	private OperationDialog opDialog;
	private boolean presetMode;
	private GenRestrictions currentRestrictions;
	private int currentCodeTweaks;

	private static String rootPath = "./";

	// Settings
	private boolean autoUpdateEnabled;
	private boolean haveCheckedCustomNames;

	java.util.ResourceBundle bundle;

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		boolean autoupdate = true;
		for (String arg : args) {
			if (arg.equalsIgnoreCase("--noupdate")) {
				autoupdate = false;
				break;
			}
		}
		final boolean au = autoupdate;
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager
					.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(RandomizerGUI.class.getName())
					.log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(RandomizerGUI.class.getName())
					.log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(RandomizerGUI.class.getName())
					.log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(RandomizerGUI.class.getName())
					.log(java.util.logging.Level.SEVERE, null, ex);
		}

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new RandomizerGUI(au);
			}
		});
	}

	// constructor
	/**
	 * Creates new form RandomizerGUI
	 * 
	 * @param autoupdate
	 */
	public RandomizerGUI(boolean autoupdate) {

		try {
			URL location = RandomizerGUI.class.getProtectionDomain()
					.getCodeSource().getLocation();
			File fh = new File(java.net.URLDecoder.decode(location.getFile(),
					"UTF-8")).getParentFile();
			rootPath = fh.getAbsolutePath() + File.separator;
		} catch (Exception e) {
			rootPath = "./";
		}

		bundle = java.util.ResourceBundle
				.getBundle("com/dabomstew/pkrandom/gui/Bundle"); // NOI18N
		testForRequiredConfigs();
		checkHandlers = new RomHandler[] { new Gen1RomHandler(),
				new Gen2RomHandler(), new Gen3RomHandler(),
				new Gen4RomHandler(), new Gen5RomHandler() };
		initComponents();
		initialiseState();
		autoUpdateEnabled = true;
		haveCheckedCustomNames = false;
		attemptReadConfig();
		if (!autoupdate) {
			// override autoupdate
			autoUpdateEnabled = false;
		}
		boolean canWrite = attemptWriteConfig();
		if (!canWrite) {
			JOptionPane.showMessageDialog(null,
					bundle.getString("RandomizerGUI.cantWriteConfigFile"));
			autoUpdateEnabled = false;
		}
		setLocationRelativeTo(null);
		setVisible(true);
		checkCustomNames();
		if (autoUpdateEnabled) {
			new UpdateCheckThread(this, false).start();
		}
	}

	// config-related stuff

	private void checkCustomNames() {
		String[] cnamefiles = new String[] { "trainerclasses.txt",
				"trainernames.txt", "nicknames.txt" };
		int[] defaultcsums = new int[] { -1442281799, -1499590809, 1641673648 };

		boolean foundCustom = false;
		for (int file = 0; file < 3; file++) {
			File oldFile = new File(rootPath + "/config/" + cnamefiles[file]);
			File currentFile = new File(rootPath + cnamefiles[file]);
			if (oldFile.exists() && oldFile.canRead() && !currentFile.exists()) {
				try {
					int crc = getFileChecksum(new FileInputStream(oldFile));
					if (crc != defaultcsums[file]) {
						foundCustom = true;
						break;
					}
				} catch (FileNotFoundException e) {
				}
			}
		}

		if (foundCustom) {
			int response = JOptionPane
					.showConfirmDialog(
							RandomizerGUI.this,
							bundle.getString("RandomizerGUI.copyNameFilesDialog.text"),
							bundle.getString("RandomizerGUI.copyNameFilesDialog.title"),
							JOptionPane.YES_NO_OPTION);
			boolean onefailed = false;
			if (response == JOptionPane.YES_OPTION) {
				for (String filename : cnamefiles) {
					if (new File(rootPath + "/config/" + filename).canRead()) {
						try {
							FileInputStream fis = new FileInputStream(new File(
									rootPath + "config/" + filename));
							FileOutputStream fos = new FileOutputStream(
									new File(rootPath + filename));
							byte[] buf = new byte[1024];
							int len;
							while ((len = fis.read(buf)) > 0) {
								fos.write(buf, 0, len);
							}
							fos.close();
							fis.close();
						} catch (IOException ex) {
							onefailed = true;
						}
					}
				}
				if (onefailed) {
					JOptionPane.showMessageDialog(this, bundle
							.getString("RandomizerGUI.copyNameFilesFailed"));
				}
			}
		}

		haveCheckedCustomNames = true;
		attemptWriteConfig();
	}

	private void attemptReadConfig() {
		File fh = new File(rootPath + "config.ini");
		if (!fh.exists() || !fh.canRead()) {
			return;
		}

		try {
			Scanner sc = new Scanner(fh, "UTF-8");
			while (sc.hasNextLine()) {
				String q = sc.nextLine().trim();
				if (q.contains("//")) {
					q = q.substring(0, q.indexOf("//")).trim();
				}
				if (!q.isEmpty()) {
					String[] tokens = q.split("=", 2);
					if (tokens.length == 2) {
						String key = tokens[0].trim();
						if (key.equalsIgnoreCase("autoupdate")) {
							autoUpdateEnabled = Boolean.parseBoolean(tokens[1]
									.trim());
						} else if (key.equalsIgnoreCase("checkedcustomnames")) {
							haveCheckedCustomNames = Boolean
									.parseBoolean(tokens[1].trim());
						}
					}
				}
			}
			sc.close();
		} catch (IOException ex) {

		}
	}

	private boolean attemptWriteConfig() {
		File fh = new File(rootPath + "config.ini");
		if (fh.exists() && !fh.canWrite()) {
			return false;
		}

		try {
			PrintStream ps = new PrintStream(new FileOutputStream(fh), true,
					"UTF-8");
			ps.println("autoupdate=" + autoUpdateEnabled);
			ps.println("checkedcustomnames=" + haveCheckedCustomNames);
			ps.close();
			return true;
		} catch (IOException e) {
			return false;
		}

	}

	private void testForRequiredConfigs() {
		String[] required = new String[] { "gameboy_jap.tbl",
				"rby_english.tbl", "rby_freger.tbl", "rby_espita.tbl",
				"green_translation.tbl", "gsc_english.tbl", "gsc_freger.tbl",
				"gsc_espita.tbl", "gba_english.tbl", "gba_jap.tbl",
				"Generation4.tbl", "Generation5.tbl", "gen1_offsets.ini",
				"gen2_offsets.ini", "gen3_offsets.ini", "gen4_offsets.ini",
				"gen5_offsets.ini", "trainerclasses.txt", "trainernames.txt",
				"nicknames.txt" };
		for (String filename : required) {
			if (!FileFunctions.configExists(filename)) {
				JOptionPane.showMessageDialog(null, String.format(
						bundle.getString("RandomizerGUI.configFileMissing"),
						filename));
				System.exit(1);
				return;
			}
		}
	}

	// form initial state

	private void initialiseState() {
		this.romHandler = null;
		this.currentRestrictions = null;
		this.currentCodeTweaks = 0;
		updateCodeTweaksButtonText();
		initialFormState();
		this.romOpenChooser.setCurrentDirectory(new File(rootPath));
		this.romSaveChooser.setCurrentDirectory(new File(rootPath));
		if (new File(rootPath + "settings/").exists()) {
			this.qsOpenChooser.setCurrentDirectory(new File(rootPath
					+ "settings/"));
			this.qsSaveChooser.setCurrentDirectory(new File(rootPath
					+ "settings/"));
		} else {
			this.qsOpenChooser.setCurrentDirectory(new File(rootPath));
			this.qsSaveChooser.setCurrentDirectory(new File(rootPath));
		}
	}

	private void initialFormState() {
		// Disable all rom components
		this.goRemoveTradeEvosCheckBox.setEnabled(false);
		this.goUpdateMovesCheckBox.setEnabled(false);
		this.goUpdateMovesLegacyCheckBox.setEnabled(false);
		this.goUpdateTypesCheckBox.setEnabled(false);
		this.goLowerCaseNamesCheckBox.setEnabled(false);
		this.goNationalDexCheckBox.setEnabled(false);

		this.goRemoveTradeEvosCheckBox.setSelected(false);
		this.goUpdateMovesCheckBox.setSelected(false);
		this.goUpdateMovesLegacyCheckBox.setSelected(false);
		this.goUpdateTypesCheckBox.setSelected(false);
		this.goLowerCaseNamesCheckBox.setSelected(false);
		this.goNationalDexCheckBox.setSelected(false);

		this.goUpdateMovesLegacyCheckBox.setVisible(true);

		this.codeTweaksCB.setEnabled(false);
		this.codeTweaksCB.setSelected(false);
		this.codeTweaksBtn.setEnabled(false);
		this.codeTweaksBtn.setVisible(true);
		this.codeTweaksCB.setVisible(true);
		this.pokeLimitCB.setEnabled(false);
		this.pokeLimitCB.setSelected(false);
		this.pokeLimitBtn.setEnabled(false);
		this.pokeLimitBtn.setVisible(true);
		this.pokeLimitCB.setVisible(true);
		this.raceModeCB.setEnabled(false);
		this.raceModeCB.setSelected(false);
		this.randomizeHollowsCB.setEnabled(false);
		this.randomizeHollowsCB.setSelected(false);
		this.brokenMovesCB.setEnabled(false);
		this.brokenMovesCB.setSelected(false);

		this.riRomNameLabel.setText(bundle
				.getString("RandomizerGUI.noRomLoaded"));
		this.riRomCodeLabel.setText("");
		this.riRomSupportLabel.setText("");

		this.loadQSButton.setEnabled(false);
		this.saveQSButton.setEnabled(false);

		this.pbsChangesUnchangedRB.setEnabled(false);
		this.pbsChangesRandomEvosRB.setEnabled(false);
		this.pbsChangesRandomTotalRB.setEnabled(false);
		this.pbsChangesShuffleRB.setEnabled(false);
		this.pbsChangesUnchangedRB.setSelected(true);
		this.pbsStandardEXPCurvesCB.setEnabled(false);
		this.pbsStandardEXPCurvesCB.setSelected(false);

		this.abilitiesPanel.setVisible(true);
		this.paUnchangedRB.setEnabled(false);
		this.paRandomizeRB.setEnabled(false);
		this.paWonderGuardCB.setEnabled(false);
		this.paUnchangedRB.setSelected(true);
		this.paWonderGuardCB.setSelected(false);

		this.spCustomPoke1Chooser.setEnabled(false);
		this.spCustomPoke2Chooser.setEnabled(false);
		this.spCustomPoke3Chooser.setEnabled(false);
		this.spCustomPoke1Chooser.setSelectedIndex(0);
		this.spCustomPoke1Chooser.setModel(new DefaultComboBoxModel(
				new String[] { "--" }));
		this.spCustomPoke2Chooser.setSelectedIndex(0);
		this.spCustomPoke2Chooser.setModel(new DefaultComboBoxModel(
				new String[] { "--" }));
		this.spCustomPoke3Chooser.setSelectedIndex(0);
		this.spCustomPoke3Chooser.setModel(new DefaultComboBoxModel(
				new String[] { "--" }));
		this.spCustomRB.setEnabled(false);
		this.spRandomRB.setEnabled(false);
		this.spRandom2EvosRB.setEnabled(false);
		this.spUnchangedRB.setEnabled(false);
		this.spUnchangedRB.setSelected(true);
		this.spHeldItemsCB.setEnabled(false);
		this.spHeldItemsCB.setSelected(false);
		this.spHeldItemsCB.setVisible(true);

		this.pmsRandomTotalRB.setEnabled(false);
		this.pmsRandomTypeRB.setEnabled(false);
		this.pmsUnchangedRB.setEnabled(false);
		this.pmsUnchangedRB.setSelected(true);
		this.pmsMetronomeOnlyRB.setEnabled(false);
		this.pms4MovesCB.setEnabled(false);
		this.pms4MovesCB.setSelected(false);
		this.pms4MovesCB.setVisible(true);

		this.ptRandomFollowEvosRB.setEnabled(false);
		this.ptRandomTotalRB.setEnabled(false);
		this.ptUnchangedRB.setEnabled(false);
		this.ptUnchangedRB.setSelected(true);

		this.tpPowerLevelsCB.setEnabled(false);
		this.tpRandomRB.setEnabled(false);
		this.tpRivalCarriesStarterCB.setEnabled(false);
		this.tpTypeThemedRB.setEnabled(false);
		this.tpTypeWeightingCB.setEnabled(false);
		this.tpNoLegendariesCB.setEnabled(false);
		this.tpNoEarlyShedinjaCB.setEnabled(false);
		this.tpNoEarlyShedinjaCB.setVisible(true);
		this.tpUnchangedRB.setEnabled(false);
		this.tpUnchangedRB.setSelected(true);
		this.tpPowerLevelsCB.setSelected(false);
		this.tpRivalCarriesStarterCB.setSelected(false);
		this.tpTypeWeightingCB.setSelected(false);
		this.tpNoLegendariesCB.setSelected(false);
		this.tpNoEarlyShedinjaCB.setSelected(false);

		this.tnRandomizeCB.setEnabled(false);
		this.tcnRandomizeCB.setEnabled(false);

		this.tnRandomizeCB.setSelected(false);
		this.tcnRandomizeCB.setSelected(false);

		this.wpUnchangedRB.setEnabled(false);
		this.wpRandomRB.setEnabled(false);
		this.wpArea11RB.setEnabled(false);
		this.wpGlobalRB.setEnabled(false);
		this.wpUnchangedRB.setSelected(true);

		this.wpARNoneRB.setEnabled(false);
		this.wpARCatchEmAllRB.setEnabled(false);
		this.wpARTypeThemedRB.setEnabled(false);
		this.wpARSimilarStrengthRB.setEnabled(false);
		this.wpARNoneRB.setSelected(true);

		this.wpUseTimeCB.setEnabled(false);
		this.wpUseTimeCB.setVisible(true);
		this.wpUseTimeCB.setSelected(false);

		this.wpNoLegendariesCB.setEnabled(false);
		this.wpNoLegendariesCB.setSelected(false);

		this.wpCatchRateCB.setEnabled(false);
		this.wpCatchRateCB.setSelected(false);

		this.wpHeldItemsCB.setEnabled(false);
		this.wpHeldItemsCB.setSelected(false);
		this.wpHeldItemsCB.setVisible(true);

		this.stpRandomL4LRB.setEnabled(false);
		this.stpRandomTotalRB.setEnabled(false);
		this.stpUnchangedRB.setEnabled(false);
		this.stpUnchangedRB.setSelected(true);

		this.tmmRandomRB.setEnabled(false);
		this.tmmUnchangedRB.setEnabled(false);
		this.tmmUnchangedRB.setSelected(true);

		this.thcRandomTotalRB.setEnabled(false);
		this.thcRandomTypeRB.setEnabled(false);
		this.thcUnchangedRB.setEnabled(false);
		this.thcUnchangedRB.setSelected(true);

		this.mtmRandomRB.setEnabled(false);
		this.mtmUnchangedRB.setEnabled(false);
		this.mtmUnchangedRB.setSelected(true);

		this.mtcRandomTotalRB.setEnabled(false);
		this.mtcRandomTypeRB.setEnabled(false);
		this.mtcUnchangedRB.setEnabled(false);
		this.mtcUnchangedRB.setSelected(true);

		this.mtMovesPanel.setVisible(true);
		this.mtCompatPanel.setVisible(true);
		this.mtNoExistLabel.setVisible(false);

		this.igtUnchangedRB.setEnabled(false);
		this.igtGivenOnlyRB.setEnabled(false);
		this.igtBothRB.setEnabled(false);
		this.igtUnchangedRB.setSelected(true);

		this.igtRandomItemCB.setEnabled(false);
		this.igtRandomItemCB.setSelected(false);
		this.igtRandomItemCB.setVisible(true);

		this.igtRandomIVsCB.setEnabled(false);
		this.igtRandomIVsCB.setSelected(false);
		this.igtRandomIVsCB.setVisible(true);

		this.igtRandomOTCB.setEnabled(false);
		this.igtRandomOTCB.setSelected(false);
		this.igtRandomOTCB.setVisible(true);

		this.igtRandomNicknameCB.setEnabled(false);
		this.igtRandomNicknameCB.setSelected(false);

		this.fiUnchangedRB.setEnabled(false);
		this.fiShuffleRB.setEnabled(false);
		this.fiRandomRB.setEnabled(false);
		this.fiUnchangedRB.setSelected(true);

	}

	// rom loading

	private void loadROM() {
		romOpenChooser.setSelectedFile(null);
		int returnVal = romOpenChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			final File fh = romOpenChooser.getSelectedFile();
			// first, check for common filetypes that aren't ROMs
			// read first 10 bytes of the file to do this
			try {
				FileInputStream fis = new FileInputStream(fh);
				byte[] sig = new byte[10];
				int siglen = fis.read(sig);
				fis.close();
				if (siglen < 10) {
					JOptionPane.showMessageDialog(this, String.format(
							bundle.getString("RandomizerGUI.tooShortToBeARom"),
							fh.getName()));
					return;
				}
				if (sig[0] == 0x50 && sig[1] == 0x4b && sig[2] == 0x03
						&& sig[3] == 0x04) {
					JOptionPane.showMessageDialog(this, String.format(
							bundle.getString("RandomizerGUI.openedZIPfile"),
							fh.getName()));
					return;
				}
				if (sig[0] == 0x52 && sig[1] == 0x61 && sig[2] == 0x72
						&& sig[3] == 0x21 && sig[4] == 0x1A && sig[5] == 0x07) {
					JOptionPane.showMessageDialog(this, String.format(
							bundle.getString("RandomizerGUI.openedRARfile"),
							fh.getName()));
					return;
				}
				if (sig[0] == 'P' && sig[1] == 'A' && sig[2] == 'T'
						&& sig[3] == 'C' && sig[4] == 'H') {
					JOptionPane.showMessageDialog(this, String.format(
							bundle.getString("RandomizerGUI.openedIPSfile"),
							fh.getName()));
					return;
				}
				// none of these? let's see if it's a valid ROM, then
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, String.format(
						bundle.getString("RandomizerGUI.unreadableRom"),
						fh.getName()));
				return;
			}
			for (RomHandler rh : checkHandlers) {
				if (rh.detectRom(fh.getAbsolutePath())) {
					this.romHandler = rh;
					opDialog = new OperationDialog(
							bundle.getString("RandomizerGUI.loadingText"),
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
								RandomizerGUI.this.romHandler.loadRom(fh
										.getAbsolutePath());
							} catch (Exception ex) {
								long time = System.currentTimeMillis();
								try {
									String errlog = "error_" + time + ".txt";
									PrintStream ps = new PrintStream(
											new FileOutputStream(errlog));
									PrintStream e1 = System.err;
									System.setErr(ps);
									ex.printStackTrace();
									verboseLog.close();
									System.setErr(e1);
									ps.close();
									JOptionPane
											.showMessageDialog(
													RandomizerGUI.this,
													String.format(
															bundle.getString("RandomizerGUI.loadFailed"),
															errlog));
								} catch (Exception logex) {
									JOptionPane
											.showMessageDialog(
													RandomizerGUI.this,
													bundle.getString("RandomizerGUI.loadFailedNoLog"));
									verboseLog.close();
								}
							}
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									RandomizerGUI.this.opDialog
											.setVisible(false);
									RandomizerGUI.this.initialFormState();
									RandomizerGUI.this.romLoaded();
								}
							});
						}
					};
					t.start();

					return;
				}
			}
			JOptionPane.showMessageDialog(this, String.format(
					bundle.getString("RandomizerGUI.unsupportedRom"),
					fh.getName()));
		}

	}

	private void romLoaded() {
		try {
			this.currentRestrictions = null;
			this.currentCodeTweaks = 0;
			updateCodeTweaksButtonText();
			this.riRomNameLabel.setText(this.romHandler.getROMName());
			this.riRomCodeLabel.setText(this.romHandler.getROMCode());
			this.riRomSupportLabel.setText(bundle
					.getString("RandomizerGUI.romSupportPrefix")
					+ " "
					+ this.romHandler.getSupportLevel());
			this.goUpdateMovesCheckBox.setSelected(false);
			if (romHandler instanceof Gen1RomHandler) {
				this.goUpdateTypesCheckBox.setEnabled(true);
			}
			this.goUpdateMovesCheckBox.setSelected(false);
			this.goUpdateMovesCheckBox.setEnabled(true);
			this.goUpdateMovesLegacyCheckBox.setSelected(false);
			this.goUpdateMovesLegacyCheckBox.setEnabled(false);
			this.goUpdateMovesLegacyCheckBox
					.setVisible(!(romHandler instanceof Gen5RomHandler));
			this.goRemoveTradeEvosCheckBox.setSelected(false);
			this.goRemoveTradeEvosCheckBox.setEnabled(true);
			if (!(romHandler instanceof Gen5RomHandler)
					&& !(romHandler instanceof Gen4RomHandler)) {
				this.goLowerCaseNamesCheckBox.setSelected(false);
				this.goLowerCaseNamesCheckBox.setEnabled(true);
			}
			if (romHandler instanceof Gen3RomHandler) {
				this.goNationalDexCheckBox.setSelected(false);
				this.goNationalDexCheckBox.setEnabled(true);
			}
			this.raceModeCB.setSelected(false);
			this.raceModeCB.setEnabled(true);

			this.codeTweaksCB.setSelected(false);
			this.codeTweaksCB.setEnabled(romHandler.codeTweaksAvailable() != 0);
			this.codeTweaksBtn.setEnabled(false);
			this.codeTweaksBtn
					.setVisible(romHandler.codeTweaksAvailable() != 0);
			this.codeTweaksCB.setVisible(romHandler.codeTweaksAvailable() != 0);

			this.pokeLimitCB.setSelected(false);
			this.pokeLimitBtn.setEnabled(false);
			this.pokeLimitCB
					.setEnabled(!(romHandler instanceof Gen1RomHandler));
			this.pokeLimitCB
					.setVisible(!(romHandler instanceof Gen1RomHandler));
			this.pokeLimitBtn
					.setVisible(!(romHandler instanceof Gen1RomHandler));

			this.randomizeHollowsCB.setSelected(false);
			this.randomizeHollowsCB.setEnabled(romHandler
					.hasHiddenHollowPokemon());

			this.brokenMovesCB.setSelected(false);
			this.brokenMovesCB.setEnabled(true);

			this.loadQSButton.setEnabled(true);
			this.saveQSButton.setEnabled(true);

			this.pbsChangesUnchangedRB.setEnabled(true);
			this.pbsChangesUnchangedRB.setSelected(true);
			this.pbsChangesRandomEvosRB.setEnabled(true);
			this.pbsChangesRandomTotalRB.setEnabled(true);
			this.pbsChangesShuffleRB.setEnabled(true);

			this.pbsStandardEXPCurvesCB.setEnabled(true);
			this.pbsStandardEXPCurvesCB.setSelected(false);

			if (romHandler.abilitiesPerPokemon() > 0) {
				this.paUnchangedRB.setEnabled(true);
				this.paUnchangedRB.setSelected(true);
				this.paRandomizeRB.setEnabled(true);
				this.paWonderGuardCB.setEnabled(false);
			} else {
				this.abilitiesPanel.setVisible(false);
			}

			this.spUnchangedRB.setEnabled(true);
			this.spUnchangedRB.setSelected(true);

			this.spCustomPoke3Chooser.setVisible(true);
			if (romHandler.canChangeStarters()) {
				this.spCustomRB.setEnabled(true);
				this.spRandomRB.setEnabled(true);
				this.spRandom2EvosRB.setEnabled(true);
				if (romHandler.isYellow()) {
					this.spCustomPoke3Chooser.setVisible(false);
				}
				populateDropdowns();
			}

			this.spHeldItemsCB.setSelected(false);
			boolean hasStarterHeldItems = (romHandler instanceof Gen2RomHandler || romHandler instanceof Gen3RomHandler);
			this.spHeldItemsCB.setEnabled(hasStarterHeldItems);
			this.spHeldItemsCB.setVisible(hasStarterHeldItems);

			this.pmsRandomTotalRB.setEnabled(true);
			this.pmsRandomTypeRB.setEnabled(true);
			this.pmsUnchangedRB.setEnabled(true);
			this.pmsUnchangedRB.setSelected(true);
			this.pmsMetronomeOnlyRB.setEnabled(true);

			this.pms4MovesCB.setVisible(romHandler.supportsFourStartingMoves());

			this.ptRandomFollowEvosRB.setEnabled(true);
			this.ptRandomTotalRB.setEnabled(true);
			this.ptUnchangedRB.setEnabled(true);
			this.ptUnchangedRB.setSelected(true);

			this.tpRandomRB.setEnabled(true);
			this.tpTypeThemedRB.setEnabled(true);
			this.tpUnchangedRB.setEnabled(true);
			this.tpUnchangedRB.setSelected(true);
			this.tnRandomizeCB.setEnabled(true);
			this.tcnRandomizeCB.setEnabled(true);

			if (romHandler instanceof Gen1RomHandler
					|| romHandler instanceof Gen2RomHandler) {
				this.tpNoEarlyShedinjaCB.setVisible(false);
			} else {
				this.tpNoEarlyShedinjaCB.setVisible(true);
			}
			this.tpNoEarlyShedinjaCB.setSelected(false);

			this.wpArea11RB.setEnabled(true);
			this.wpGlobalRB.setEnabled(true);
			this.wpRandomRB.setEnabled(true);
			this.wpUnchangedRB.setEnabled(true);
			this.wpUnchangedRB.setSelected(true);
			this.wpUseTimeCB.setEnabled(false);
			this.wpNoLegendariesCB.setEnabled(false);
			if (!romHandler.hasTimeBasedEncounters()) {
				this.wpUseTimeCB.setVisible(false);
			}
			this.wpCatchRateCB.setEnabled(true);

			this.wpHeldItemsCB.setSelected(false);
			this.wpHeldItemsCB.setEnabled(true);
			if (romHandler instanceof Gen1RomHandler) {
				this.wpHeldItemsCB.setVisible(false);
			}

			this.stpUnchangedRB.setEnabled(true);
			if (this.romHandler.canChangeStaticPokemon()) {
				this.stpRandomL4LRB.setEnabled(true);
				this.stpRandomTotalRB.setEnabled(true);

			}

			this.tmmRandomRB.setEnabled(true);
			this.tmmUnchangedRB.setEnabled(true);

			this.thcRandomTotalRB.setEnabled(true);
			this.thcRandomTypeRB.setEnabled(true);
			this.thcUnchangedRB.setEnabled(true);

			if (this.romHandler.hasMoveTutors()) {
				this.mtmRandomRB.setEnabled(true);
				this.mtmUnchangedRB.setEnabled(true);

				this.mtcRandomTotalRB.setEnabled(true);
				this.mtcRandomTypeRB.setEnabled(true);
				this.mtcUnchangedRB.setEnabled(true);
			} else {
				this.mtCompatPanel.setVisible(false);
				this.mtMovesPanel.setVisible(false);
				this.mtNoExistLabel.setVisible(true);
			}

			this.igtUnchangedRB.setEnabled(true);
			this.igtBothRB.setEnabled(true);
			this.igtGivenOnlyRB.setEnabled(true);

			if (this.romHandler instanceof Gen1RomHandler) {
				this.igtRandomItemCB.setVisible(false);
				this.igtRandomIVsCB.setVisible(false);
				this.igtRandomOTCB.setVisible(false);
			}

			this.fiUnchangedRB.setEnabled(true);
			this.fiRandomRB.setEnabled(true);
			this.fiShuffleRB.setEnabled(true);

			if (this.romHandler instanceof AbstractDSRomHandler) {
				((AbstractDSRomHandler) this.romHandler).closeInnerRom();
			}
		} catch (Exception ex) {
			long time = System.currentTimeMillis();
			try {
				String errlog = "error_" + time + ".txt";
				PrintStream ps = new PrintStream(new FileOutputStream(errlog));
				PrintStream e1 = System.err;
				System.setErr(ps);
				ex.printStackTrace();
				System.setErr(e1);
				ps.close();
				JOptionPane
						.showMessageDialog(
								RandomizerGUI.this,
								String.format(
										bundle.getString("RandomizerGUI.processFailed"),
										errlog));
			} catch (Exception logex) {
				JOptionPane.showMessageDialog(RandomizerGUI.this,
						bundle.getString("RandomizerGUI.processFailedNoLog"));
			}
		}
	}

	private void populateDropdowns() {
		List<Pokemon> currentStarters = romHandler.getStarters();
		List<Pokemon> allPokes = romHandler.getPokemon();
		String[] pokeNames = new String[allPokes.size() - 1];
		for (int i = 1; i < allPokes.size(); i++) {
			pokeNames[i - 1] = allPokes.get(i).name;
		}
		this.spCustomPoke1Chooser.setModel(new DefaultComboBoxModel(pokeNames));
		this.spCustomPoke1Chooser
				.setSelectedIndex(currentStarters.get(0).number - 1);
		this.spCustomPoke2Chooser.setModel(new DefaultComboBoxModel(pokeNames));
		this.spCustomPoke2Chooser
				.setSelectedIndex(currentStarters.get(1).number - 1);
		if (!romHandler.isYellow()) {
			this.spCustomPoke3Chooser.setModel(new DefaultComboBoxModel(
					pokeNames));
			this.spCustomPoke3Chooser
					.setSelectedIndex(currentStarters.get(2).number - 1);
		}
	}

	private void enableOrDisableSubControls() {
		// This isn't for a new ROM being loaded (that's romLoaded)
		// This is just for when a radio button gets selected or state is loaded
		// and we need to enable/disable secondary controls
		// e.g. wild pokemon / trainer pokemon "modifier"
		// and the 3 starter pokemon dropdowns

		if (this.goUpdateMovesCheckBox.isSelected()
				&& !(romHandler instanceof Gen5RomHandler)) {
			this.goUpdateMovesLegacyCheckBox.setEnabled(true);
		} else {
			this.goUpdateMovesLegacyCheckBox.setEnabled(false);
			this.goUpdateMovesLegacyCheckBox.setSelected(false);
		}

		this.codeTweaksBtn.setEnabled(this.codeTweaksCB.isSelected());
		updateCodeTweaksButtonText();
		this.pokeLimitBtn.setEnabled(this.pokeLimitCB.isSelected());

		if (this.spCustomRB.isSelected()) {
			this.spCustomPoke1Chooser.setEnabled(true);
			this.spCustomPoke2Chooser.setEnabled(true);
			this.spCustomPoke3Chooser.setEnabled(true);
		} else {
			this.spCustomPoke1Chooser.setEnabled(false);
			this.spCustomPoke2Chooser.setEnabled(false);
			this.spCustomPoke3Chooser.setEnabled(false);
		}

		if (this.paRandomizeRB.isSelected()) {
			this.paWonderGuardCB.setEnabled(true);
		} else {
			this.paWonderGuardCB.setEnabled(false);
			this.paWonderGuardCB.setSelected(false);
		}

		if (this.tpUnchangedRB.isSelected()) {
			this.tpPowerLevelsCB.setEnabled(false);
			this.tpRivalCarriesStarterCB.setEnabled(false);
			this.tpNoLegendariesCB.setEnabled(false);
			this.tpNoEarlyShedinjaCB.setEnabled(false);
			this.tpNoEarlyShedinjaCB.setSelected(false);
		} else {
			this.tpPowerLevelsCB.setEnabled(true);
			this.tpRivalCarriesStarterCB.setEnabled(true);
			this.tpNoLegendariesCB.setEnabled(true);
			this.tpNoEarlyShedinjaCB.setEnabled(true);
		}

		if (this.tpTypeThemedRB.isSelected()) {
			this.tpTypeWeightingCB.setEnabled(true);
		} else {
			this.tpTypeWeightingCB.setEnabled(false);
		}

		if (this.wpArea11RB.isSelected() || this.wpRandomRB.isSelected()) {
			this.wpARNoneRB.setEnabled(true);
			this.wpARSimilarStrengthRB.setEnabled(true);
			this.wpARCatchEmAllRB.setEnabled(true);
			this.wpARTypeThemedRB.setEnabled(true);
		} else if (this.wpGlobalRB.isSelected()) {
			if (this.wpARCatchEmAllRB.isSelected()
					|| this.wpARTypeThemedRB.isSelected()) {
				this.wpARNoneRB.setSelected(true);
			}
			this.wpARNoneRB.setEnabled(true);
			this.wpARSimilarStrengthRB.setEnabled(true);
			this.wpARCatchEmAllRB.setEnabled(false);
			this.wpARTypeThemedRB.setEnabled(false);
		} else {
			this.wpARNoneRB.setEnabled(false);
			this.wpARSimilarStrengthRB.setEnabled(false);
			this.wpARCatchEmAllRB.setEnabled(false);
			this.wpARTypeThemedRB.setEnabled(false);
			this.wpARNoneRB.setSelected(true);
		}

		if (this.wpUnchangedRB.isSelected()) {
			this.wpUseTimeCB.setEnabled(false);
			this.wpNoLegendariesCB.setEnabled(false);
		} else {
			this.wpUseTimeCB.setEnabled(true);
			this.wpNoLegendariesCB.setEnabled(true);
		}

		if (this.igtUnchangedRB.isSelected()) {
			this.igtRandomItemCB.setEnabled(false);
			this.igtRandomIVsCB.setEnabled(false);
			this.igtRandomNicknameCB.setEnabled(false);
			this.igtRandomOTCB.setEnabled(false);
		} else {
			this.igtRandomItemCB.setEnabled(true);
			this.igtRandomIVsCB.setEnabled(true);
			this.igtRandomNicknameCB.setEnabled(true);
			this.igtRandomOTCB.setEnabled(true);
		}

		if (this.pmsMetronomeOnlyRB.isSelected()) {
			this.tmmUnchangedRB.setEnabled(false);
			this.tmmRandomRB.setEnabled(false);
			this.tmmUnchangedRB.setSelected(true);

			this.mtmUnchangedRB.setEnabled(false);
			this.mtmRandomRB.setEnabled(false);
			this.mtmUnchangedRB.setSelected(true);
		} else {
			this.tmmUnchangedRB.setEnabled(true);
			this.tmmRandomRB.setEnabled(true);

			this.mtmUnchangedRB.setEnabled(true);
			this.mtmRandomRB.setEnabled(true);
		}

		if (this.pmsMetronomeOnlyRB.isSelected()
				|| this.pmsUnchangedRB.isSelected()) {
			this.pms4MovesCB.setEnabled(false);
			this.pms4MovesCB.setSelected(false);
		} else {
			this.pms4MovesCB.setEnabled(true);
		}
	}

	private void saveROM() {
		if (romHandler == null) {
			return; // none loaded
		}
		if (raceModeCB.isSelected() && tpUnchangedRB.isSelected()
				&& wpUnchangedRB.isSelected()) {
			JOptionPane.showMessageDialog(this,
					bundle.getString("RandomizerGUI.raceModeRequirements"));
			return;
		}
		if (pokeLimitCB.isSelected()
				&& (this.currentRestrictions == null || this.currentRestrictions
						.nothingSelected())) {
			JOptionPane.showMessageDialog(this,
					bundle.getString("RandomizerGUI.pokeLimitNotChosen"));
			return;
		}
		romSaveChooser.setSelectedFile(null);
		int returnVal = romSaveChooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File fh = romSaveChooser.getSelectedFile();
			// Fix or add extension
			List<String> extensions = new ArrayList<String>(Arrays.asList(
					"sgb", "gbc", "gba", "nds"));
			extensions.remove(this.romHandler.getDefaultExtension());
			fh = FileFunctions.fixFilename(fh,
					this.romHandler.getDefaultExtension(), extensions);
			boolean allowed = true;
			if (this.romHandler instanceof AbstractDSRomHandler) {
				String currentFN = this.romHandler.loadedFilename();
				if (currentFN.equals(fh.getAbsolutePath())) {
					JOptionPane.showMessageDialog(this,
							bundle.getString("RandomizerGUI.cantOverwriteDS"));
					allowed = false;
				}
			}
			if (allowed) {
				// Get a seed
				long seed = RandomSource.pickSeed();
				// Apply it
				RandomSource.seed(seed);
				presetMode = false;
				performRandomization(fh.getAbsolutePath(), seed, null, null,
						null);
			}
		}
	}

	private String getConfigString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		baos.write(makeByteSelected(this.goLowerCaseNamesCheckBox,
				this.goNationalDexCheckBox, this.goRemoveTradeEvosCheckBox,
				this.goUpdateMovesCheckBox, this.goUpdateMovesLegacyCheckBox,
				this.goUpdateTypesCheckBox, this.tnRandomizeCB,
				this.tcnRandomizeCB));

		baos.write(makeByteSelected(this.pbsChangesRandomEvosRB,
				this.pbsChangesRandomTotalRB, this.pbsChangesShuffleRB,
				this.pbsChangesUnchangedRB, this.paUnchangedRB,
				this.paRandomizeRB, this.paWonderGuardCB,
				this.pbsStandardEXPCurvesCB));

		baos.write(makeByteSelected(this.ptRandomFollowEvosRB,
				this.ptRandomTotalRB, this.ptUnchangedRB, this.codeTweaksCB,
				this.raceModeCB, this.randomizeHollowsCB, this.brokenMovesCB,
				this.pokeLimitCB));

		baos.write(makeByteSelected(this.spCustomRB, this.spRandomRB,
				this.spUnchangedRB, this.spRandom2EvosRB, this.spHeldItemsCB));

		// @4
		writePokemonIndex(baos, this.spCustomPoke1Chooser);
		writePokemonIndex(baos, this.spCustomPoke2Chooser);
		writePokemonIndex(baos, this.spCustomPoke3Chooser);

		// 10
		baos.write(makeByteSelected(this.pmsRandomTotalRB,
				this.pmsRandomTypeRB, this.pmsUnchangedRB,
				this.pmsMetronomeOnlyRB, this.pms4MovesCB));

		// changed 160
		baos.write(makeByteSelected(this.tpPowerLevelsCB, this.tpRandomRB,
				this.tpRivalCarriesStarterCB, this.tpTypeThemedRB,
				this.tpTypeWeightingCB, this.tpUnchangedRB,
				this.tpNoLegendariesCB, this.tpNoEarlyShedinjaCB));

		baos.write(makeByteSelected(this.wpARCatchEmAllRB, this.wpArea11RB,
				this.wpARNoneRB, this.wpARTypeThemedRB, this.wpGlobalRB,
				this.wpRandomRB, this.wpUnchangedRB, this.wpUseTimeCB));

		// bugfix 161
		baos.write(makeByteSelected(this.wpCatchRateCB, this.wpNoLegendariesCB,
				this.wpARSimilarStrengthRB, this.wpHeldItemsCB));

		baos.write(makeByteSelected(this.stpUnchangedRB, this.stpRandomL4LRB,
				this.stpRandomTotalRB));

		baos.write(makeByteSelected(this.thcRandomTotalRB,
				this.thcRandomTypeRB, this.thcUnchangedRB, this.tmmRandomRB,
				this.tmmUnchangedRB));

		baos.write(makeByteSelected(this.mtcRandomTotalRB,
				this.mtcRandomTypeRB, this.mtcUnchangedRB, this.mtmRandomRB,
				this.mtmUnchangedRB));

		// new 150
		baos.write(makeByteSelected(this.igtBothRB, this.igtGivenOnlyRB,
				this.igtRandomItemCB, this.igtRandomIVsCB,
				this.igtRandomNicknameCB, this.igtRandomOTCB,
				this.igtUnchangedRB));

		baos.write(makeByteSelected(this.fiRandomRB, this.fiShuffleRB,
				this.fiUnchangedRB));

		// @ 19
		try {
			if (currentRestrictions != null) {
				writeFullInt(baos, currentRestrictions.toInt());
			} else {
				writeFullInt(baos, 0);
			}
		} catch (IOException e) {
		}

		// @ 23
		try {
			writeFullInt(baos, currentCodeTweaks);
		} catch (IOException e) {

		}

		try {
			byte[] romName = romHandler.getROMName().getBytes("US-ASCII");
			baos.write(romName.length);
			baos.write(romName);
		} catch (UnsupportedEncodingException e) {
			baos.write(0);
		} catch (IOException e) {
			baos.write(0);
		}

		byte[] current = baos.toByteArray();
		CRC32 checksum = new CRC32();
		checksum.update(current);

		try {
			writeFullInt(baos, (int) checksum.getValue());
			writeFullInt(baos, getFileChecksum("trainerclasses.txt"));
			writeFullInt(baos, getFileChecksum("trainernames.txt"));
			writeFullInt(baos, getFileChecksum("nicknames.txt"));
		} catch (IOException e) {
		}

		return DatatypeConverter.printBase64Binary(baos.toByteArray());
	}

	private static int getFileChecksum(String filename) {
		try {
			return getFileChecksum(FileFunctions.openConfig(filename));
		} catch (IOException e) {
			return 0;
		}
	}

	private static int getFileChecksum(InputStream stream) {
		try {
			Scanner sc = new Scanner(stream, "UTF-8");
			CRC32 checksum = new CRC32();
			while (sc.hasNextLine()) {
				String line = sc.nextLine().trim();
				if (!line.isEmpty()) {
					checksum.update(line.getBytes("UTF-8"));
				}
			}
			sc.close();
			return (int) checksum.getValue();
		} catch (IOException e) {
			return 0;
		}

	}

	public String getValidRequiredROMName(String config, byte[] trainerClasses,
			byte[] trainerNames, byte[] nicknames)
			throws UnsupportedEncodingException,
			InvalidSupplementFilesException {
		byte[] data = DatatypeConverter.parseBase64Binary(config);

		if (data.length < 44) {
			return null; // too short
		}

		// Check the checksum
		ByteBuffer buf = ByteBuffer.allocate(4).put(data, data.length - 16, 4);
		buf.rewind();
		int crc = buf.getInt();

		CRC32 checksum = new CRC32();
		checksum.update(data, 0, data.length - 16);
		if ((int) checksum.getValue() != crc) {
			return null; // checksum failure
		}

		// Check the trainerclass & trainernames crc
		if (trainerClasses == null
				&& !checkOtherCRC(data, 0, 6, "trainerclasses.txt",
						data.length - 12)) {
			JOptionPane.showMessageDialog(null,
					bundle.getString("RandomizerGUI.presetFailTrainerClasses"));
			throw new InvalidSupplementFilesException();
		}
		if (trainerNames == null
				&& (!checkOtherCRC(data, 0, 5, "trainernames.txt",
						data.length - 8) || !checkOtherCRC(data, 16, 5,
						"trainernames.txt", data.length - 8))) {
			JOptionPane.showMessageDialog(null,
					bundle.getString("RandomizerGUI.presetFailTrainerNames"));
			throw new InvalidSupplementFilesException();
		}
		if (nicknames == null
				&& !checkOtherCRC(data, 16, 4, "nicknames.txt", data.length - 4)) {
			JOptionPane.showMessageDialog(null,
					bundle.getString("RandomizerGUI.presetFailNicknames"));
			throw new InvalidSupplementFilesException();
		}

		int nameLength = data[27] & 0xFF;
		if (data.length != 44 + nameLength) {
			return null; // not valid length
		}
		String name = new String(data, 28, nameLength, "US-ASCII");
		return name;
	}

	private boolean restoreFrom(String config) {
		// Need to add enables
		byte[] data = DatatypeConverter.parseBase64Binary(config);

		// Check the checksum
		ByteBuffer buf = ByteBuffer.allocate(4).put(data, data.length - 16, 4);
		buf.rewind();
		int crc = buf.getInt();

		CRC32 checksum = new CRC32();
		checksum.update(data, 0, data.length - 16);

		if ((int) checksum.getValue() != crc) {
			return false; // checksum failure
		}

		// Restore the actual controls
		restoreStates(data[0], this.goLowerCaseNamesCheckBox,
				this.goNationalDexCheckBox, this.goRemoveTradeEvosCheckBox,
				this.goUpdateMovesCheckBox, this.goUpdateMovesLegacyCheckBox,
				this.goUpdateTypesCheckBox, this.tnRandomizeCB,
				this.tcnRandomizeCB);

		// sanity override
		if (this.goUpdateMovesLegacyCheckBox.isSelected()
				&& (romHandler instanceof Gen5RomHandler)) {
			// they probably don't want moves updated actually
			this.goUpdateMovesLegacyCheckBox.setSelected(false);
			this.goUpdateMovesCheckBox.setSelected(false);
		}

		restoreStates(data[1], this.pbsChangesRandomEvosRB,
				this.pbsChangesRandomTotalRB, this.pbsChangesShuffleRB,
				this.pbsChangesUnchangedRB, this.paUnchangedRB,
				this.paRandomizeRB, this.paWonderGuardCB,
				this.pbsStandardEXPCurvesCB);

		restoreStates(data[2], this.ptRandomFollowEvosRB, this.ptRandomTotalRB,
				this.ptUnchangedRB, this.codeTweaksCB, this.raceModeCB,
				this.randomizeHollowsCB, this.brokenMovesCB, this.pokeLimitCB);

		restoreStates(data[3], this.spCustomRB, this.spRandomRB,
				this.spUnchangedRB, this.spRandom2EvosRB, this.spHeldItemsCB);

		restoreSelectedIndex(data, 4, this.spCustomPoke1Chooser);
		restoreSelectedIndex(data, 6, this.spCustomPoke2Chooser);
		restoreSelectedIndex(data, 8, this.spCustomPoke3Chooser);

		restoreStates(data[10], this.pmsRandomTotalRB, this.pmsRandomTypeRB,
				this.pmsUnchangedRB, this.pmsMetronomeOnlyRB, this.pms4MovesCB);

		// changed 160
		restoreStates(data[11], this.tpPowerLevelsCB, this.tpRandomRB,
				this.tpRivalCarriesStarterCB, this.tpTypeThemedRB,
				this.tpTypeWeightingCB, this.tpUnchangedRB,
				this.tpNoLegendariesCB, this.tpNoEarlyShedinjaCB);

		restoreStates(data[12], this.wpARCatchEmAllRB, this.wpArea11RB,
				this.wpARNoneRB, this.wpARTypeThemedRB, this.wpGlobalRB,
				this.wpRandomRB, this.wpUnchangedRB, this.wpUseTimeCB);

		restoreStates(data[13], this.wpCatchRateCB, this.wpNoLegendariesCB,
				this.wpARSimilarStrengthRB, this.wpHeldItemsCB);

		restoreStates(data[14], this.stpUnchangedRB, this.stpRandomL4LRB,
				this.stpRandomTotalRB);

		restoreStates(data[15], this.thcRandomTotalRB, this.thcRandomTypeRB,
				this.thcUnchangedRB, this.tmmRandomRB, this.tmmUnchangedRB);
		restoreStates(data[16], this.mtcRandomTotalRB, this.mtcRandomTypeRB,
				this.mtcUnchangedRB, this.mtmRandomRB, this.mtmUnchangedRB);

		// new 150
		restoreStates(data[17], this.igtBothRB, this.igtGivenOnlyRB,
				this.igtRandomItemCB, this.igtRandomIVsCB,
				this.igtRandomNicknameCB, this.igtRandomOTCB,
				this.igtUnchangedRB);
		restoreStates(data[18], this.fiRandomRB, this.fiShuffleRB,
				this.fiUnchangedRB);

		// gen restrictions
		int genlim = readFullInt(data, 19);
		if (genlim == 0) {
			this.currentRestrictions = null;
		} else {
			this.currentRestrictions = new GenRestrictions(genlim);
			this.currentRestrictions.limitToGen(this.romHandler
					.generationOfPokemon());
		}

		int codetweaks = readFullInt(data, 23);
		this.currentCodeTweaks = codetweaks
				& this.romHandler.codeTweaksAvailable();
		updateCodeTweaksButtonText();
		// Sanity override
		if (this.currentCodeTweaks == 0) {
			this.codeTweaksCB.setSelected(false);
		}

		this.enableOrDisableSubControls();

		// Name data is ignored here - we should've used it earlier.
		return true;
	}

	private void performRandomization(final String filename, final long seed,
			byte[] trainerClasses, byte[] trainerNames, byte[] nicknames) {

		final boolean raceMode = raceModeCB.isSelected();
		int checkValue = 0;
		final long startTime = System.currentTimeMillis();
		// Setup verbose log
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String nl = System.getProperty("line.separator");
		try {
			verboseLog = new PrintStream(baos, false, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			verboseLog = new PrintStream(baos);
		}
		try {
			// limit pokemon?
			if (this.pokeLimitCB.isSelected()) {
				romHandler.setPokemonPool(currentRestrictions);
				romHandler.removeEvosForPokemonPool();
			}

			// Update type effectiveness in RBY?
			if (romHandler instanceof Gen1RomHandler
					&& this.goUpdateTypesCheckBox.isSelected()) {
				romHandler.fixTypeEffectiveness();
			}

			// Move updates
			if (this.goUpdateMovesCheckBox.isSelected()) {
				romHandler.initMoveUpdates();
				if (!(romHandler instanceof Gen5RomHandler)) {
					romHandler.updateMovesToGen5();
				}
				if (!this.goUpdateMovesLegacyCheckBox.isSelected()) {
					romHandler.updateMovesToGen6();
				}
				romHandler.printMoveUpdates();
			}

			List<Move> moves = romHandler.getMoves();

			// Trade evolutions removal
			if (this.goRemoveTradeEvosCheckBox.isSelected()) {
				romHandler.removeTradeEvolutions(!this.pmsUnchangedRB
						.isSelected());
			}

			// Camel case?
			if (!(romHandler instanceof Gen5RomHandler)
					&& !(romHandler instanceof Gen4RomHandler)
					&& this.goLowerCaseNamesCheckBox.isSelected()) {
				romHandler.applyCamelCaseNames();
			}

			// National dex gen3?
			if (romHandler instanceof Gen3RomHandler
					&& this.goNationalDexCheckBox.isSelected()) {
				romHandler.patchForNationalDex();
			}

			// Code Tweaks?
			if (romHandler.codeTweaksAvailable() != 0) {
				int ctavailable = romHandler.codeTweaksAvailable();
				if ((ctavailable & CodeTweaks.BW_EXP_PATCH) > 0
						&& (currentCodeTweaks & CodeTweaks.BW_EXP_PATCH) > 0) {
					romHandler.applyBWEXPPatch();
				}

				if ((ctavailable & CodeTweaks.FIX_CRIT_RATE) > 0
						&& (currentCodeTweaks & CodeTweaks.FIX_CRIT_RATE) > 0) {
					romHandler.applyCritRatePatch();
				}

				if ((ctavailable & CodeTweaks.NERF_X_ACCURACY) > 0
						&& (currentCodeTweaks & CodeTweaks.NERF_X_ACCURACY) > 0) {
					romHandler.applyXAccNerfPatch();
				}
			}

			// Hollows?
			if (romHandler.hasHiddenHollowPokemon()
					&& this.randomizeHollowsCB.isSelected()) {
				romHandler.randomizeHiddenHollowPokemon();
			}

			List<Pokemon> allPokes = romHandler.getPokemon();

			// Base stats changing
			if (this.pbsChangesShuffleRB.isSelected()) {
				romHandler.shufflePokemonStats();
			} else if (this.pbsChangesRandomEvosRB.isSelected()) {
				romHandler.randomizePokemonStats(true);
			} else if (this.pbsChangesRandomTotalRB.isSelected()) {
				romHandler.randomizePokemonStats(false);
			}

			if (this.pbsStandardEXPCurvesCB.isSelected()) {
				romHandler.standardizeEXPCurves();
			}

			// Abilities? (new 1.0.2)
			if (this.romHandler.abilitiesPerPokemon() > 0
					&& this.paRandomizeRB.isSelected()) {
				romHandler
						.randomizeAbilities(this.paWonderGuardCB.isSelected());
			}

			// Pokemon Types
			if (this.ptRandomFollowEvosRB.isSelected()) {
				romHandler.randomizePokemonTypes(true);
			} else if (this.ptRandomTotalRB.isSelected()) {
				romHandler.randomizePokemonTypes(false);
			}

			// Wild Held Items?
			String[] itemNames = romHandler.getItemNames();
			if (this.wpHeldItemsCB.isSelected()) {
				romHandler.randomizeWildHeldItems();
			}

			// Log base stats & types if changed at all
			if (this.pbsChangesUnchangedRB.isSelected()
					&& this.ptUnchangedRB.isSelected()
					&& this.paUnchangedRB.isSelected()
					&& this.wpHeldItemsCB.isSelected() == false) {
				verboseLog.println("Pokemon base stats & type: unchanged" + nl);
			} else {
				verboseLog.println("--Pokemon Base Stats & Types--");
				if (romHandler instanceof Gen1RomHandler) {
					verboseLog
							.println("NUM|NAME      |TYPE             |  HP| ATK| DEF| SPE|SPEC");
					for (Pokemon pkmn : allPokes) {
						if (pkmn != null) {
							String typeString = pkmn.primaryType == null ? "NULL"
									: pkmn.primaryType.toString();
							if (pkmn.secondaryType != null) {
								typeString += "/"
										+ (pkmn.secondaryType == null ? "NULL"
												: pkmn.secondaryType.toString());
							}
							verboseLog.printf(
									"%3d|%-10s|%-17s|%4d|%4d|%4d|%4d|%4d" + nl,
									pkmn.number, pkmn.name, typeString,
									pkmn.hp, pkmn.attack, pkmn.defense,
									pkmn.speed, pkmn.special);
						}

					}
				} else {
					verboseLog
							.print("NUM|NAME      |TYPE             |  HP| ATK| DEF| SPE|SATK|SDEF");
					int abils = romHandler.abilitiesPerPokemon();
					for (int i = 0; i < abils; i++) {
						verboseLog.print("|ABILITY" + (i + 1) + "    ");
					}
					verboseLog.print("|ITEM");
					verboseLog.println();
					for (Pokemon pkmn : allPokes) {
						if (pkmn != null) {
							String typeString = pkmn.primaryType.toString();
							if (pkmn.secondaryType != null) {
								typeString += "/"
										+ pkmn.secondaryType.toString();
							}
							verboseLog.printf(
									"%3d|%-10s|%-17s|%4d|%4d|%4d|%4d|%4d|%4d",
									pkmn.number, pkmn.name, typeString,
									pkmn.hp, pkmn.attack, pkmn.defense,
									pkmn.speed, pkmn.spatk, pkmn.spdef);
							if (abils > 0) {
								verboseLog.printf("|%-12s|%-12s",
										romHandler.abilityName(pkmn.ability1),
										romHandler.abilityName(pkmn.ability2));
								if (abils > 2) {
									verboseLog.printf("|%-12s", romHandler
											.abilityName(pkmn.ability3));
								}
							}
							verboseLog.print("|");
							if (pkmn.guaranteedHeldItem > 0) {
								verboseLog
										.print(itemNames[pkmn.guaranteedHeldItem]
												+ " (100%)");
							} else {
								int itemCount = 0;
								if (pkmn.commonHeldItem > 0) {
									itemCount++;
									verboseLog
											.print(itemNames[pkmn.commonHeldItem]
													+ " (common)");
								}
								if (pkmn.rareHeldItem > 0) {
									if (itemCount > 0) {
										verboseLog.print(", ");
									}
									itemCount++;
									verboseLog
											.print(itemNames[pkmn.rareHeldItem]
													+ " (rare)");
								}
								if (pkmn.darkGrassHeldItem > 0) {
									if (itemCount > 0) {
										verboseLog.print(", ");
									}
									itemCount++;
									verboseLog
											.print(itemNames[pkmn.darkGrassHeldItem]
													+ " (dark grass only)");
								}
							}
							verboseLog.println();
						}

					}
				}
				if (raceMode) {
					for (Pokemon pkmn : allPokes) {
						if (pkmn != null) {
							checkValue = addToCV(checkValue, pkmn.hp,
									pkmn.attack, pkmn.defense, pkmn.speed,
									pkmn.spatk, pkmn.spdef, pkmn.ability1,
									pkmn.ability2, pkmn.ability3);
						}
					}
				}
				verboseLog.println();
			}

			// Starter Pokemon
			// Applied after type to update the strings correctly based on new
			// types
			if (romHandler.canChangeStarters()) {
				if (this.spCustomRB.isSelected()) {
					verboseLog.println("--Custom Starters--");
					Pokemon pkmn1 = allPokes.get(this.spCustomPoke1Chooser
							.getSelectedIndex() + 1);
					verboseLog.println("Set starter 1 to " + pkmn1.name);
					Pokemon pkmn2 = allPokes.get(this.spCustomPoke2Chooser
							.getSelectedIndex() + 1);
					verboseLog.println("Set starter 2 to " + pkmn2.name);
					if (romHandler.isYellow()) {
						romHandler.setStarters(Arrays.asList(pkmn1, pkmn2));
					} else {
						Pokemon pkmn3 = allPokes.get(this.spCustomPoke3Chooser
								.getSelectedIndex() + 1);
						verboseLog.println("Set starter 3 to " + pkmn3.name);
						romHandler.setStarters(Arrays.asList(pkmn1, pkmn2,
								pkmn3));
					}
					verboseLog.println();

				} else if (this.spRandomRB.isSelected()) {
					// Randomise
					verboseLog.println("--Random Starters--");
					int starterCount = 3;
					if (romHandler.isYellow()) {
						starterCount = 2;
					}
					List<Pokemon> starters = new ArrayList<Pokemon>();
					for (int i = 0; i < starterCount; i++) {
						Pokemon pkmn = romHandler.randomPokemon();
						while (starters.contains(pkmn)) {
							pkmn = romHandler.randomPokemon();
						}
						verboseLog.println("Set starter " + (i + 1) + " to "
								+ pkmn.name);
						starters.add(pkmn);
					}
					romHandler.setStarters(starters);
					verboseLog.println();
				} else if (this.spRandom2EvosRB.isSelected()) {
					// Randomise
					verboseLog.println("--Random 2-Evolution Starters--");
					int starterCount = 3;
					if (romHandler.isYellow()) {
						starterCount = 2;
					}
					List<Pokemon> starters = new ArrayList<Pokemon>();
					for (int i = 0; i < starterCount; i++) {
						Pokemon pkmn = romHandler.random2EvosPokemon();
						while (starters.contains(pkmn)) {
							pkmn = romHandler.random2EvosPokemon();
						}
						verboseLog.println("Set starter " + (i + 1) + " to "
								+ pkmn.name);
						starters.add(pkmn);
					}
					romHandler.setStarters(starters);
					verboseLog.println();
				}
				if (this.spHeldItemsCB.isSelected()
						&& (romHandler instanceof Gen1RomHandler) == false) {
					romHandler.randomizeStarterHeldItems();
				}
			}

			// Movesets
			boolean noBrokenMoves = this.brokenMovesCB.isSelected();
			boolean forceFourLv1s = romHandler.supportsFourStartingMoves()
					&& this.pms4MovesCB.isSelected();
			if (this.pmsRandomTypeRB.isSelected()) {
				romHandler.randomizeMovesLearnt(true, noBrokenMoves,
						forceFourLv1s);
			} else if (this.pmsRandomTotalRB.isSelected()) {
				romHandler.randomizeMovesLearnt(false, noBrokenMoves,
						forceFourLv1s);
			}

			// Show the new movesets if applicable
			if (this.pmsUnchangedRB.isSelected()) {
				verboseLog.println("Pokemon Movesets: Unchanged." + nl);
			} else if (this.pmsMetronomeOnlyRB.isSelected()) {
				verboseLog.println("Pokemon Movesets: Metronome Only." + nl);
			} else {
				verboseLog.println("--Pokemon Movesets--");
				List<String> movesets = new ArrayList<String>();
				Map<Pokemon, List<MoveLearnt>> moveData = romHandler
						.getMovesLearnt();
				for (Pokemon pkmn : moveData.keySet()) {
					StringBuilder sb = new StringBuilder();
					sb.append(String.format("%03d %-10s : ", pkmn.number,
							pkmn.name));
					List<MoveLearnt> data = moveData.get(pkmn);
					boolean first = true;
					for (MoveLearnt ml : data) {
						if (!first) {
							sb.append(", ");
						}

						sb.append(moves.get(ml.move).name + " at level "
								+ ml.level);
						first = false;
					}
					movesets.add(sb.toString());
				}
				Collections.sort(movesets);
				for (String moveset : movesets) {
					verboseLog.println(moveset);
				}
				verboseLog.println();
			}

			// Trainer Pokemon
			if (this.tpRandomRB.isSelected()) {
				romHandler.randomizeTrainerPokes(
						this.tpRivalCarriesStarterCB.isSelected(),
						this.tpPowerLevelsCB.isSelected(),
						this.tpNoLegendariesCB.isSelected(),
						this.tpNoEarlyShedinjaCB.isSelected());
			} else if (this.tpTypeThemedRB.isSelected()) {
				romHandler.typeThemeTrainerPokes(
						this.tpRivalCarriesStarterCB.isSelected(),
						this.tpPowerLevelsCB.isSelected(),
						this.tpTypeWeightingCB.isSelected(),
						this.tpNoLegendariesCB.isSelected(),
						this.tpNoEarlyShedinjaCB.isSelected());
			}

			// Trainer names & class names randomization
			// done before trainer log to add proper names

			if (this.tcnRandomizeCB.isSelected()) {
				romHandler.randomizeTrainerClassNames(trainerClasses);
			}

			if (this.tnRandomizeCB.isSelected()) {
				romHandler.randomizeTrainerNames(trainerNames);
			}

			if (this.tpUnchangedRB.isSelected()) {
				verboseLog.println("Trainers: Unchanged." + nl);
			} else {
				verboseLog.println("--Trainers Pokemon--");
				List<Trainer> trainers = romHandler.getTrainers();
				int idx = 0;
				for (Trainer t : trainers) {
					idx++;
					verboseLog.print("#" + idx + " ");
					if (t.fullDisplayName != null) {
						verboseLog.print("(" + t.fullDisplayName + ")");
					} else if (t.name != null) {
						verboseLog.print("(" + t.name + ")");
					}
					if (t.offset != idx && t.offset != 0) {
						verboseLog.printf("@%X", t.offset);
					}
					verboseLog.print(" - ");
					boolean first = true;
					for (TrainerPokemon tpk : t.pokemon) {
						if (!first) {
							verboseLog.print(", ");
						}
						verboseLog.print(tpk.pokemon.name + " Lv" + tpk.level);
						first = false;
					}
					verboseLog.println();
				}
				verboseLog.println();
			}

			// Apply metronome only mode now that trainers have been dealt with
			if (pmsMetronomeOnlyRB.isSelected()) {
				romHandler.metronomeOnlyMode();
			}

			if (raceMode) {
				List<Trainer> trainers = romHandler.getTrainers();
				for (Trainer t : trainers) {
					for (TrainerPokemon tpk : t.pokemon) {
						checkValue = addToCV(checkValue, tpk.level,
								tpk.pokemon.number);
					}
				}
			}

			// Wild Pokemon
			// actually call this code (Kappa)
			if (this.wpCatchRateCB.isSelected()) {
				if (romHandler instanceof Gen5RomHandler) {
					romHandler.minimumCatchRate(50, 25);
				} else {
					romHandler.minimumCatchRate(75, 37);
				}
			}
			if (this.wpRandomRB.isSelected()) {
				romHandler.randomEncounters(this.wpUseTimeCB.isSelected(),
						this.wpARCatchEmAllRB.isSelected(),
						this.wpARTypeThemedRB.isSelected(),
						this.wpARSimilarStrengthRB.isSelected(),
						this.wpNoLegendariesCB.isSelected());
			} else if (this.wpArea11RB.isSelected()) {
				romHandler.area1to1Encounters(this.wpUseTimeCB.isSelected(),
						this.wpARCatchEmAllRB.isSelected(),
						this.wpARTypeThemedRB.isSelected(),
						this.wpARSimilarStrengthRB.isSelected(),
						this.wpNoLegendariesCB.isSelected());
			} else if (this.wpGlobalRB.isSelected()) {
				romHandler.game1to1Encounters(this.wpUseTimeCB.isSelected(),
						this.wpARSimilarStrengthRB.isSelected(),
						this.wpNoLegendariesCB.isSelected());
			}

			if (this.wpUnchangedRB.isSelected()) {
				verboseLog.println("Wild Pokemon: Unchanged." + nl);
			} else {
				verboseLog.println("--Wild Pokemon--");
				List<EncounterSet> encounters = romHandler
						.getEncounters(this.wpUseTimeCB.isSelected());
				int idx = 0;
				for (EncounterSet es : encounters) {
					idx++;
					verboseLog.print("Set #" + idx + " ");
					if (es.displayName != null) {
						verboseLog.print("- " + es.displayName + " ");
					}
					verboseLog.print("(rate=" + es.rate + ")");
					verboseLog.print(" - ");
					boolean first = true;
					for (Encounter e : es.encounters) {
						if (!first) {
							verboseLog.print(", ");
						}
						verboseLog.print(e.pokemon.name + " Lv");
						if (e.maxLevel > 0 && e.maxLevel != e.level) {
							verboseLog.print("s " + e.level + "-" + e.maxLevel);
						} else {
							verboseLog.print(e.level);
						}
						first = false;
					}
					verboseLog.println();
				}
				verboseLog.println();
			}

			if (raceMode) {
				List<EncounterSet> encounters = romHandler
						.getEncounters(this.wpUseTimeCB.isSelected());
				for (EncounterSet es : encounters) {
					for (Encounter e : es.encounters) {
						checkValue = addToCV(checkValue, e.level,
								e.pokemon.number);
					}
				}
			}

			// Static Pokemon

			if (romHandler.canChangeStaticPokemon()) {
				List<Pokemon> oldStatics = romHandler.getStaticPokemon();
				if (this.stpRandomL4LRB.isSelected()) {
					romHandler.randomizeStaticPokemon(true);
				} else if (this.stpRandomTotalRB.isSelected()) {
					romHandler.randomizeStaticPokemon(false);
				}
				List<Pokemon> newStatics = romHandler.getStaticPokemon();
				if (this.stpUnchangedRB.isSelected()) {
					verboseLog.println("Static Pokemon: Unchanged." + nl);
				} else {
					verboseLog.println("--Static Pokemon--");
					Map<Pokemon, Integer> seenPokemon = new TreeMap<Pokemon, Integer>();
					for (int i = 0; i < oldStatics.size(); i++) {
						Pokemon oldP = oldStatics.get(i);
						Pokemon newP = newStatics.get(i);
						if (raceMode) {
							checkValue = addToCV(checkValue, newP.number);
						}
						verboseLog.print(oldP.name);
						if (seenPokemon.containsKey(oldP)) {
							int amount = seenPokemon.get(oldP);
							verboseLog.print("(" + (++amount) + ")");
							seenPokemon.put(oldP, amount);
						} else {
							seenPokemon.put(oldP, 1);
						}
						verboseLog.println(" => " + newP.name);
					}
					verboseLog.println();
				}
			}

			// TMs
			if (!pmsMetronomeOnlyRB.isSelected()
					&& this.tmmRandomRB.isSelected()) {
				romHandler.randomizeTMMoves(noBrokenMoves);
				verboseLog.println("--TM Moves--");
				List<Integer> tmMoves = romHandler.getTMMoves();
				for (int i = 0; i < tmMoves.size(); i++) {
					verboseLog.printf("TM%02d %s" + nl, i + 1,
							moves.get(tmMoves.get(i)).name);
					if (raceMode) {
						checkValue = addToCV(checkValue, tmMoves.get(i));
					}
				}
				verboseLog.println();
			} else if (pmsMetronomeOnlyRB.isSelected()) {
				verboseLog.println("TM Moves: Metronome Only." + nl);
			} else {
				verboseLog.println("TM Moves: Unchanged." + nl);
			}

			// TM/HM compatibility
			if (this.thcRandomTypeRB.isSelected()) {
				romHandler.randomizeTMHMCompatibility(true);
			} else if (this.thcRandomTotalRB.isSelected()) {
				romHandler.randomizeTMHMCompatibility(false);
			}

			// Move Tutors (new 1.0.3)
			if (this.romHandler.hasMoveTutors()) {
				if (!pmsMetronomeOnlyRB.isSelected()
						&& this.mtmRandomRB.isSelected()) {
					List<Integer> oldMtMoves = romHandler.getMoveTutorMoves();
					romHandler.randomizeMoveTutorMoves(noBrokenMoves);
					verboseLog.println("--Move Tutor Moves--");
					List<Integer> newMtMoves = romHandler.getMoveTutorMoves();
					for (int i = 0; i < newMtMoves.size(); i++) {
						verboseLog.printf("%s => %s" + nl,
								moves.get(oldMtMoves.get(i)).name,
								moves.get(newMtMoves.get(i)).name);
						if (raceMode) {
							checkValue = addToCV(checkValue, newMtMoves.get(i));
						}
					}
					verboseLog.println();
				} else if (pmsMetronomeOnlyRB.isSelected()) {
					verboseLog
							.println("Move Tutor Moves: Metronome Only." + nl);
				} else {
					verboseLog.println("Move Tutor Moves: Unchanged." + nl);
				}

				// Compatibility
				if (this.mtcRandomTypeRB.isSelected()) {
					romHandler.randomizeMoveTutorCompatibility(true);
				} else if (this.mtcRandomTotalRB.isSelected()) {
					romHandler.randomizeMoveTutorCompatibility(false);
				}
			}

			// In-game trades
			List<IngameTrade> oldTrades = romHandler.getIngameTrades();
			if (this.igtGivenOnlyRB.isSelected()) {
				romHandler.randomizeIngameTrades(false, nicknames,
						this.igtRandomNicknameCB.isSelected(), trainerNames,
						this.igtRandomOTCB.isSelected(),
						this.igtRandomIVsCB.isSelected(),
						this.igtRandomItemCB.isSelected());
			} else if (this.igtBothRB.isSelected()) {
				romHandler.randomizeIngameTrades(true, nicknames,
						this.igtRandomNicknameCB.isSelected(), trainerNames,
						this.igtRandomOTCB.isSelected(),
						this.igtRandomIVsCB.isSelected(),
						this.igtRandomItemCB.isSelected());
			}

			if (!this.igtUnchangedRB.isSelected()) {
				verboseLog.println("--In-Game Trades--");
				List<IngameTrade> newTrades = romHandler.getIngameTrades();
				int size = oldTrades.size();
				for (int i = 0; i < size; i++) {
					IngameTrade oldT = oldTrades.get(i);
					IngameTrade newT = newTrades.get(i);
					verboseLog.printf(
							"Trading %s for %s the %s has become trading %s for %s the %s"
									+ nl, oldT.requestedPokemon.name,
							oldT.nickname, oldT.givenPokemon.name,
							newT.requestedPokemon.name, newT.nickname,
							newT.givenPokemon.name);
				}
				verboseLog.println();
			}

			// Field Items
			if (this.fiShuffleRB.isSelected()) {
				romHandler.shuffleFieldItems();
			} else if (this.fiRandomRB.isSelected()) {
				romHandler.randomizeFieldItems();
			}

			// Signature...
			romHandler.applySignature();

			// Save
			final int finishedCV = checkValue;
			opDialog = new OperationDialog(
					bundle.getString("RandomizerGUI.savingText"), this, true);
			Thread t = new Thread() {
				@Override
				public void run() {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							opDialog.setVisible(true);
						}
					});
					boolean succeededSave = false;
					try {
						RandomizerGUI.this.romHandler.saveRom(filename);
						succeededSave = true;
					} catch (Exception ex) {
						long time = System.currentTimeMillis();
						try {
							String errlog = "error_" + time + ".txt";
							PrintStream ps = new PrintStream(
									new FileOutputStream(errlog));
							PrintStream e1 = System.err;
							System.setErr(ps);
							ex.printStackTrace();
							verboseLog.close();
							System.setErr(e1);
							ps.close();
							JOptionPane
									.showMessageDialog(
											RandomizerGUI.this,
											String.format(
													bundle.getString("RandomizerGUI.saveFailedIO"),
													errlog));
						} catch (Exception logex) {
							JOptionPane
									.showMessageDialog(
											RandomizerGUI.this,
											bundle.getString("RandomizerGUI.saveFailedIONoLog"));
							verboseLog.close();
						}
					}
					if (succeededSave) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								RandomizerGUI.this.opDialog.setVisible(false);
								// Log tail
								verboseLog
										.println("------------------------------------------------------------------");
								verboseLog.println("Randomization of "
										+ romHandler.getROMName()
										+ " completed.");
								verboseLog.println("Time elapsed: "
										+ (System.currentTimeMillis() - startTime)
										+ "ms");
								verboseLog.println("RNG Calls: "
										+ RandomSource.callsSinceSeed());
								verboseLog
										.println("------------------------------------------------------------------");

								// Log?
								verboseLog.close();
								byte[] out = baos.toByteArray();
								verboseLog = System.out;

								if (raceMode) {
									JOptionPane.showMessageDialog(
											RandomizerGUI.this,
											String.format(
													bundle.getString("RandomizerGUI.raceModeCheckValuePopup"),
													finishedCV));
								} else {
									int response = JOptionPane.showConfirmDialog(
											RandomizerGUI.this,
											bundle.getString("RandomizerGUI.saveLogDialog.text"),
											bundle.getString("RandomizerGUI.saveLogDialog.title"),
											JOptionPane.YES_NO_OPTION);
									if (response == JOptionPane.YES_OPTION) {
										try {
											FileOutputStream fos = new FileOutputStream(
													filename + ".log");
											fos.write(0xEF);
											fos.write(0xBB);
											fos.write(0xBF);
											fos.write(out);
											fos.close();
										} catch (IOException e) {
											JOptionPane.showMessageDialog(
													RandomizerGUI.this,
													bundle.getString("RandomizerGUI.logSaveFailed"));
											return;
										}
										JOptionPane.showMessageDialog(
												RandomizerGUI.this,
												String.format(
														bundle.getString("RandomizerGUI.logSaved"),
														filename));
									}
								}
								if (presetMode) {
									JOptionPane.showMessageDialog(
											RandomizerGUI.this,
											bundle.getString("RandomizerGUI.randomizationDone"));
									// Done
									RandomizerGUI.this.romHandler = null;
									initialFormState();
								} else {
									// Compile a config string
									String configString = getConfigString();
									// Show the preset maker
									new PresetMakeDialog(RandomizerGUI.this,
											seed, configString);

									// Done
									RandomizerGUI.this.romHandler = null;
									initialFormState();
								}
							}
						});
					} else {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								RandomizerGUI.this.opDialog.setVisible(false);
								verboseLog = System.out;
								RandomizerGUI.this.romHandler = null;
								initialFormState();
							}
						});
					}
				}
			};
			t.start();
		} catch (Exception ex) {
			long time = System.currentTimeMillis();
			try {
				String errlog = "error_" + time + ".txt";
				PrintStream ps = new PrintStream(new FileOutputStream(errlog));
				PrintStream e1 = System.err;
				System.setErr(ps);
				ex.printStackTrace();
				verboseLog.close();
				byte[] out = baos.toByteArray();
				System.err.print(new String(out, "UTF-8"));
				System.setErr(e1);
				ps.close();
				JOptionPane.showMessageDialog(this, String.format(
						bundle.getString("RandomizerGUI.saveFailed"), errlog));
			} catch (Exception logex) {
				JOptionPane.showMessageDialog(this,
						bundle.getString("RandomizerGUI.saveFailedNoLog"));
				verboseLog.close();
			}
		}

	}

	private void presetLoader() {
		PresetLoadDialog pld = new PresetLoadDialog(this);
		if (pld.isCompleted()) {
			// Apply it
			long seed = pld.getSeed();
			String config = pld.getConfigString();
			this.romHandler = pld.getROM();
			this.romLoaded();
			this.restoreFrom(config);
			romSaveChooser.setSelectedFile(null);
			int returnVal = romSaveChooser.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File fh = romSaveChooser.getSelectedFile();
				// Fix or add extension
				List<String> extensions = new ArrayList<String>(Arrays.asList(
						"sgb", "gbc", "gba", "nds"));
				extensions.remove(this.romHandler.getDefaultExtension());
				fh = FileFunctions.fixFilename(fh,
						this.romHandler.getDefaultExtension(), extensions);
				boolean allowed = true;
				if (this.romHandler instanceof AbstractDSRomHandler) {
					String currentFN = this.romHandler.loadedFilename();
					if (currentFN.equals(fh.getAbsolutePath())) {
						JOptionPane.showMessageDialog(this, bundle
								.getString("RandomizerGUI.cantOverwriteDS"));
						allowed = false;
					}
				}
				if (allowed) {
					// Apply the seed we were given
					RandomSource.seed(seed);
					presetMode = true;
					performRandomization(fh.getAbsolutePath(), seed,
							pld.getTrainerClasses(), pld.getTrainerNames(),
							pld.getNicknames());
				} else {
					this.romHandler = null;
					initialFormState();
				}

			} else {
				this.romHandler = null;
				initialFormState();
			}
		}

	}

	// helper methods

	private boolean checkOtherCRC(byte[] data, int byteIndex, int switchIndex,
			String filename, int offsetInData) {
		// If the switch at data[byteIndex].switchIndex is on,
		// then check that the CRC at
		// data[offsetInData] ... data[offsetInData+3]
		// matches the CRC of filename.
		// If not, return false.
		// If any other case, return true.
		int switches = data[byteIndex] & 0xFF;
		if (((switches >> switchIndex) & 0x01) == 0x01) {
			// have to check the CRC
			int crc = readFullInt(data, offsetInData);

			if (getFileChecksum(filename) != crc) {
				return false;
			}
		}
		return true;
	}

	private void restoreSelectedIndex(byte[] data, int offset,
			JComboBox comboBox) {
		int selIndex = (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8);
		if (comboBox.getModel().getSize() > selIndex) {
			comboBox.setSelectedIndex(selIndex);
		} else if (this.spCustomRB.isSelected()) {
			JOptionPane.showMessageDialog(this,
					bundle.getString("RandomizerGUI.starterUnavailable"));
		}
	}

	private void restoreStates(byte b, AbstractButton... switches) {
		int value = b & 0xFF;
		for (int i = 0; i < switches.length; i++) {
			int realValue = (value >> i) & 0x01;
			switches[i].setSelected(realValue == 0x01);
		}
	}

	private int readFullInt(byte[] data, int offset) {
		ByteBuffer buf = ByteBuffer.allocate(4).put(data, offset, 4);
		buf.rewind();
		return buf.getInt();
	}

	private void writeFullInt(ByteArrayOutputStream baos, int checksum)
			throws IOException {
		byte[] crc = ByteBuffer.allocate(4).putInt(checksum).array();
		baos.write(crc);

	}

	private void writePokemonIndex(ByteArrayOutputStream baos,
			JComboBox comboBox) {
		baos.write(comboBox.getSelectedIndex() & 0xFF);
		baos.write((comboBox.getSelectedIndex() >> 8) & 0xFF);

	}

	private int makeByteSelected(AbstractButton... switches) {
		if (switches.length > 8) {
			// No can do
			return 0;
		}
		int initial = 0;
		int state = 1;
		for (AbstractButton b : switches) {
			initial |= b.isSelected() ? state : 0;
			state *= 2;
		}
		return initial;
	}

	private int addToCV(int checkValue, int... values) {
		for (int value : values) {
			checkValue = Integer.rotateLeft(checkValue, 3);
			checkValue ^= value;
		}
		return checkValue;
	}

	private void updateCodeTweaksButtonText() {
		if (currentCodeTweaks == 0 || !codeTweaksCB.isSelected()) {
			codeTweaksBtn.setText(bundle
					.getString("RandomizerGUI.codeTweaksBtn.text"));
		} else {
			int ctCount = 0;
			for (int i = 0; i < 32; i++) {
				if ((currentCodeTweaks & (1 << i)) > 0) {
					ctCount++;
				}
			}
			codeTweaksBtn.setText(String.format(bundle
					.getString("RandomizerGUI.codeTweaksBtn.textWithActive"),
					ctCount));
		}
	}

	// public response methods

	public void updateFound(int newVersion, String changelog) {
		new UpdateFoundDialog(this, newVersion, changelog);
	}

	public void noUpdateFound() {
		JOptionPane.showMessageDialog(this,
				bundle.getString("RandomizerGUI.noUpdates"));
	}

	public static String getRootPath() {
		return rootPath;
	}

	// actions

	private void updateSettingsButtonActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_updateSettingsButtonActionPerformed
		if (autoUpdateEnabled) {
			toggleAutoUpdatesMenuItem.setText(bundle
					.getString("RandomizerGUI.disableAutoUpdate"));
		} else {
			toggleAutoUpdatesMenuItem.setText(bundle
					.getString("RandomizerGUI.enableAutoUpdate"));
		}
		updateSettingsMenu.show(updateSettingsButton, 0,
				updateSettingsButton.getHeight());
	}// GEN-LAST:event_updateSettingsButtonActionPerformed

	private void toggleAutoUpdatesMenuItemActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_toggleAutoUpdatesMenuItemActionPerformed
		autoUpdateEnabled = !autoUpdateEnabled;
		if (autoUpdateEnabled) {
			JOptionPane.showMessageDialog(this,
					bundle.getString("RandomizerGUI.autoUpdateEnabled"));
		} else {
			JOptionPane.showMessageDialog(this,
					bundle.getString("RandomizerGUI.autoUpdateDisabled"));
		}
		attemptWriteConfig();
	}// GEN-LAST:event_toggleAutoUpdatesMenuItemActionPerformed

	private void manualUpdateMenuItemActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_manualUpdateMenuItemActionPerformed
		new UpdateCheckThread(this, true).start();
	}// GEN-LAST:event_manualUpdateMenuItemActionPerformed

	private void loadQSButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_loadQSButtonActionPerformed
		if (this.romHandler == null) {
			return;
		}
		qsOpenChooser.setSelectedFile(null);
		int returnVal = qsOpenChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File fh = qsOpenChooser.getSelectedFile();
			try {
				FileInputStream fis = new FileInputStream(fh);
				int version = fis.read();
				if (version > PRESET_FILE_VERSION) {
					JOptionPane
							.showMessageDialog(
									this,
									bundle.getString("RandomizerGUI.settingsFileNewer"));
					fis.close();
					return;
				}
				int cslength = fis.read();
				byte[] csBuf = new byte[cslength];
				fis.read(csBuf);
				fis.close();
				String configString = new String(csBuf, "UTF-8");
				if (version < PRESET_FILE_VERSION) {
					// show a warning dialog, but load it
					JOptionPane
							.showMessageDialog(
									this,
									bundle.getString("RandomizerGUI.settingsFileOlder"));
					configString = new QuickSettingsUpdater().update(version,
							configString);
				}
				String romName = getValidRequiredROMName(configString,
						new byte[] {}, new byte[] {}, new byte[] {});
				if (romName == null) {
					JOptionPane.showMessageDialog(this, bundle
							.getString("RandomizerGUI.invalidSettingsFile"));
				}
				// now we just load it
				initialFormState();
				romLoaded();
				restoreFrom(configString);
				JCheckBox[] checkboxes = new JCheckBox[] { this.brokenMovesCB,
						this.codeTweaksCB, this.goLowerCaseNamesCheckBox,
						this.goNationalDexCheckBox,
						this.goRemoveTradeEvosCheckBox,
						this.goUpdateMovesCheckBox, this.goUpdateTypesCheckBox,
						this.spHeldItemsCB, this.paWonderGuardCB,
						this.raceModeCB, this.randomizeHollowsCB,
						this.tcnRandomizeCB, this.tnRandomizeCB,
						this.tpNoEarlyShedinjaCB, this.tpNoLegendariesCB,
						this.tpPowerLevelsCB, this.tpRivalCarriesStarterCB,
						this.tpTypeWeightingCB, this.wpCatchRateCB,
						this.wpNoLegendariesCB, this.wpUseTimeCB,
						this.igtRandomItemCB, this.igtRandomIVsCB,
						this.igtRandomNicknameCB, this.igtRandomOTCB };
				for (JCheckBox cb : checkboxes) {
					if (!cb.isEnabled() || !cb.isVisible()) {
						cb.setSelected(false);
					}
				}

				if (!this.romHandler.canChangeStaticPokemon()) {
					this.stpUnchangedRB.setSelected(true);
				}

				JOptionPane.showMessageDialog(this, String.format(
						bundle.getString("RandomizerGUI.settingsLoaded"),
						fh.getName()));
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this,
						bundle.getString("RandomizerGUI.settingsLoadFailed"));
			} catch (InvalidSupplementFilesException e) {
				// not possible
			}
		}
	}// GEN-LAST:event_loadQSButtonActionPerformed

	private void saveQSButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveQSButtonActionPerformed
		if (this.romHandler == null) {
			return;
		}
		qsSaveChooser.setSelectedFile(null);
		int returnVal = qsSaveChooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File fh = qsSaveChooser.getSelectedFile();
			// Fix or add extension
			fh = FileFunctions.fixFilename(fh, "rnqs");
			// Save now?
			try {
				FileOutputStream fos = new FileOutputStream(fh);
				fos.write(PRESET_FILE_VERSION);
				byte[] configString = getConfigString().getBytes("UTF-8");
				fos.write(configString.length);
				fos.write(configString);
				fos.close();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this,
						bundle.getString("RandomizerGUI.settingsSaveFailed"));
			}
		}
	}// GEN-LAST:event_saveQSButtonActionPerformed

	private void codeTweaksBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_codeTweaksBtnActionPerformed
		CodeTweaksDialog ctd = new CodeTweaksDialog(this,
				this.currentCodeTweaks, this.romHandler.codeTweaksAvailable());
		if (ctd.pressedOK()) {
			this.currentCodeTweaks = ctd.getChoice();
			updateCodeTweaksButtonText();
		}
	}// GEN-LAST:event_codeTweaksBtnActionPerformed

	private void pokeLimitBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pokeLimitBtnActionPerformed
		GenerationLimitDialog gld = new GenerationLimitDialog(this,
				this.currentRestrictions, this.romHandler.generationOfPokemon());
		if (gld.pressedOK()) {
			this.currentRestrictions = gld.getChoice();
		}
	}// GEN-LAST:event_pokeLimitBtnActionPerformed

	private void goUpdateMovesCheckBoxActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_goUpdateMovesCheckBoxActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_goUpdateMovesCheckBoxActionPerformed

	private void codeTweaksCBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_codeTweaksCBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_codeTweaksCBActionPerformed

	private void pokeLimitCBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pokeLimitCBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_pokeLimitCBActionPerformed

	private void pmsMetronomeOnlyRBActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pmsMetronomeOnlyRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_pmsMetronomeOnlyRBActionPerformed

	private void igtUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_igtUnchangedRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_igtUnchangedRBActionPerformed

	private void igtGivenOnlyRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_igtGivenOnlyRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_igtGivenOnlyRBActionPerformed

	private void igtBothRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_igtBothRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_igtBothRBActionPerformed

	private void wpARNoneRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_wpARNoneRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_wpARNoneRBActionPerformed

	private void wpARSimilarStrengthRBActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_wpARSimilarStrengthRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_wpARSimilarStrengthRBActionPerformed

	private void wpARCatchEmAllRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_wpARCatchEmAllRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_wpARCatchEmAllRBActionPerformed

	private void wpARTypeThemedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_wpARTypeThemedRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_wpARTypeThemedRBActionPerformed

	private void pmsUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pmsUnchangedRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_pmsUnchangedRBActionPerformed

	private void pmsRandomTypeRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pmsRandomTypeRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_pmsRandomTypeRBActionPerformed

	private void pmsRandomTotalRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pmsRandomTotalRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_pmsRandomTotalRBActionPerformed

	private void mtmUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mtmUnchangedRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_mtmUnchangedRBActionPerformed

	private void paUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_paUnchangedRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_paUnchangedRBActionPerformed

	private void paRandomizeRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_paRandomizeRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_paRandomizeRBActionPerformed

	private void aboutButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_aboutButtonActionPerformed
		new AboutDialog(this, true).setVisible(true);
	}// GEN-LAST:event_aboutButtonActionPerformed

	private void openROMButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_openROMButtonActionPerformed
		loadROM();
	}// GEN-LAST:event_openROMButtonActionPerformed

	private void saveROMButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveROMButtonActionPerformed
		saveROM();
	}// GEN-LAST:event_saveROMButtonActionPerformed

	private void usePresetsButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_usePresetsButtonActionPerformed
		presetLoader();
	}// GEN-LAST:event_usePresetsButtonActionPerformed

	private void wpUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_wpUnchangedRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_wpUnchangedRBActionPerformed

	private void tpUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tpUnchangedRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_tpUnchangedRBActionPerformed

	private void tpRandomRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tpRandomRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_tpRandomRBActionPerformed

	private void tpTypeThemedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tpTypeThemedRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_tpTypeThemedRBActionPerformed

	private void spUnchangedRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_spUnchangedRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_spUnchangedRBActionPerformed

	private void spCustomRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_spCustomRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_spCustomRBActionPerformed

	private void spRandomRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_spRandomRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_spRandomRBActionPerformed

	private void wpRandomRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_wpRandomRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_wpRandomRBActionPerformed

	private void wpArea11RBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_wpArea11RBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_wpArea11RBActionPerformed

	private void wpGlobalRBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_wpGlobalRBActionPerformed
		this.enableOrDisableSubControls();
	}// GEN-LAST:event_wpGlobalRBActionPerformed

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// <editor-fold defaultstate="collapsed"
	// <editor-fold defaultstate="collapsed"
	// <editor-fold defaultstate="collapsed"
	// <editor-fold defaultstate="collapsed"
	// <editor-fold defaultstate="collapsed"
	// <editor-fold defaultstate="collapsed"
	// <editor-fold defaultstate="collapsed"
	// <editor-fold defaultstate="collapsed"
	// <editor-fold defaultstate="collapsed"
	// <editor-fold defaultstate="collapsed"
	// <editor-fold defaultstate="collapsed"
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		pokeStatChangesButtonGroup = new javax.swing.ButtonGroup();
		pokeTypesButtonGroup = new javax.swing.ButtonGroup();
		pokeMovesetsButtonGroup = new javax.swing.ButtonGroup();
		trainerPokesButtonGroup = new javax.swing.ButtonGroup();
		wildPokesButtonGroup = new javax.swing.ButtonGroup();
		wildPokesARuleButtonGroup = new javax.swing.ButtonGroup();
		starterPokemonButtonGroup = new javax.swing.ButtonGroup();
		romOpenChooser = new javax.swing.JFileChooser();
		romSaveChooser = new JFileChooser() {

			private static final long serialVersionUID = 3244234325234511L;

			public void approveSelection() {
				File fh = getSelectedFile();
				// Fix or add extension
				List<String> extensions = new ArrayList<String>(Arrays.asList(
						"sgb", "gbc", "gba", "nds"));
				extensions.remove(RandomizerGUI.this.romHandler
						.getDefaultExtension());
				fh = FileFunctions.fixFilename(fh,
						RandomizerGUI.this.romHandler.getDefaultExtension(),
						extensions);
				if (fh.exists() && getDialogType() == SAVE_DIALOG) {
					int result = JOptionPane.showConfirmDialog(this,
							"The file exists, overwrite?", "Existing file",
							JOptionPane.YES_NO_CANCEL_OPTION);
					switch (result) {
					case JOptionPane.YES_OPTION:
						super.approveSelection();
						return;
					case JOptionPane.CANCEL_OPTION:
						cancelSelection();
						return;
					default:
						return;
					}
				}
				super.approveSelection();
			}
		};
		qsOpenChooser = new javax.swing.JFileChooser();
		qsSaveChooser = new javax.swing.JFileChooser();
		staticPokemonButtonGroup = new javax.swing.ButtonGroup();
		tmMovesButtonGroup = new javax.swing.ButtonGroup();
		tmHmCompatibilityButtonGroup = new javax.swing.ButtonGroup();
		pokeAbilitiesButtonGroup = new javax.swing.ButtonGroup();
		mtMovesButtonGroup = new javax.swing.ButtonGroup();
		mtCompatibilityButtonGroup = new javax.swing.ButtonGroup();
		ingameTradesButtonGroup = new javax.swing.ButtonGroup();
		fieldItemsButtonGroup = new javax.swing.ButtonGroup();
		updateSettingsMenu = new javax.swing.JPopupMenu();
		toggleAutoUpdatesMenuItem = new javax.swing.JMenuItem();
		manualUpdateMenuItem = new javax.swing.JMenuItem();
		generalOptionsPanel = new javax.swing.JPanel();
		goUpdateTypesCheckBox = new javax.swing.JCheckBox();
		goUpdateMovesCheckBox = new javax.swing.JCheckBox();
		goRemoveTradeEvosCheckBox = new javax.swing.JCheckBox();
		goLowerCaseNamesCheckBox = new javax.swing.JCheckBox();
		goNationalDexCheckBox = new javax.swing.JCheckBox();
		goUpdateMovesLegacyCheckBox = new javax.swing.JCheckBox();
		romInfoPanel = new javax.swing.JPanel();
		riRomNameLabel = new javax.swing.JLabel();
		riRomCodeLabel = new javax.swing.JLabel();
		riRomSupportLabel = new javax.swing.JLabel();
		optionsScrollPane = new javax.swing.JScrollPane();
		optionsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		optionsContainerPanel = new javax.swing.JPanel();
		baseStatsPanel = new javax.swing.JPanel();
		pbsChangesUnchangedRB = new javax.swing.JRadioButton();
		pbsChangesShuffleRB = new javax.swing.JRadioButton();
		pbsChangesRandomEvosRB = new javax.swing.JRadioButton();
		pbsChangesRandomTotalRB = new javax.swing.JRadioButton();
		pbsStandardEXPCurvesCB = new javax.swing.JCheckBox();
		pokemonTypesPanel = new javax.swing.JPanel();
		ptUnchangedRB = new javax.swing.JRadioButton();
		ptRandomFollowEvosRB = new javax.swing.JRadioButton();
		ptRandomTotalRB = new javax.swing.JRadioButton();
		pokemonMovesetsPanel = new javax.swing.JPanel();
		pmsUnchangedRB = new javax.swing.JRadioButton();
		pmsRandomTypeRB = new javax.swing.JRadioButton();
		pmsRandomTotalRB = new javax.swing.JRadioButton();
		pmsMetronomeOnlyRB = new javax.swing.JRadioButton();
		pms4MovesCB = new javax.swing.JCheckBox();
		trainersPokemonPanel = new javax.swing.JPanel();
		tpUnchangedRB = new javax.swing.JRadioButton();
		tpRandomRB = new javax.swing.JRadioButton();
		tpTypeThemedRB = new javax.swing.JRadioButton();
		tpPowerLevelsCB = new javax.swing.JCheckBox();
		tpTypeWeightingCB = new javax.swing.JCheckBox();
		tpRivalCarriesStarterCB = new javax.swing.JCheckBox();
		tpNoLegendariesCB = new javax.swing.JCheckBox();
		tnRandomizeCB = new javax.swing.JCheckBox();
		tcnRandomizeCB = new javax.swing.JCheckBox();
		tpNoEarlyShedinjaCB = new javax.swing.JCheckBox();
		wildPokemonPanel = new javax.swing.JPanel();
		wpUnchangedRB = new javax.swing.JRadioButton();
		wpRandomRB = new javax.swing.JRadioButton();
		wpArea11RB = new javax.swing.JRadioButton();
		wpGlobalRB = new javax.swing.JRadioButton();
		wildPokemonARulePanel = new javax.swing.JPanel();
		wpARNoneRB = new javax.swing.JRadioButton();
		wpARCatchEmAllRB = new javax.swing.JRadioButton();
		wpARTypeThemedRB = new javax.swing.JRadioButton();
		wpARSimilarStrengthRB = new javax.swing.JRadioButton();
		wpUseTimeCB = new javax.swing.JCheckBox();
		wpNoLegendariesCB = new javax.swing.JCheckBox();
		wpCatchRateCB = new javax.swing.JCheckBox();
		wpHeldItemsCB = new javax.swing.JCheckBox();
		starterPokemonPanel = new javax.swing.JPanel();
		spUnchangedRB = new javax.swing.JRadioButton();
		spCustomRB = new javax.swing.JRadioButton();
		spCustomPoke1Chooser = new javax.swing.JComboBox();
		spCustomPoke2Chooser = new javax.swing.JComboBox();
		spCustomPoke3Chooser = new javax.swing.JComboBox();
		spRandomRB = new javax.swing.JRadioButton();
		spRandom2EvosRB = new javax.swing.JRadioButton();
		spHeldItemsCB = new javax.swing.JCheckBox();
		staticPokemonPanel = new javax.swing.JPanel();
		stpUnchangedRB = new javax.swing.JRadioButton();
		stpRandomL4LRB = new javax.swing.JRadioButton();
		stpRandomTotalRB = new javax.swing.JRadioButton();
		tmhmsPanel = new javax.swing.JPanel();
		tmMovesPanel = new javax.swing.JPanel();
		tmmUnchangedRB = new javax.swing.JRadioButton();
		tmmRandomRB = new javax.swing.JRadioButton();
		tmHmCompatPanel = new javax.swing.JPanel();
		thcUnchangedRB = new javax.swing.JRadioButton();
		thcRandomTypeRB = new javax.swing.JRadioButton();
		thcRandomTotalRB = new javax.swing.JRadioButton();
		abilitiesPanel = new javax.swing.JPanel();
		paUnchangedRB = new javax.swing.JRadioButton();
		paRandomizeRB = new javax.swing.JRadioButton();
		paWonderGuardCB = new javax.swing.JCheckBox();
		moveTutorsPanel = new javax.swing.JPanel();
		mtMovesPanel = new javax.swing.JPanel();
		mtmUnchangedRB = new javax.swing.JRadioButton();
		mtmRandomRB = new javax.swing.JRadioButton();
		mtCompatPanel = new javax.swing.JPanel();
		mtcUnchangedRB = new javax.swing.JRadioButton();
		mtcRandomTypeRB = new javax.swing.JRadioButton();
		mtcRandomTotalRB = new javax.swing.JRadioButton();
		mtNoExistLabel = new javax.swing.JLabel();
		inGameTradesPanel = new javax.swing.JPanel();
		igtUnchangedRB = new javax.swing.JRadioButton();
		igtGivenOnlyRB = new javax.swing.JRadioButton();
		igtBothRB = new javax.swing.JRadioButton();
		igtRandomNicknameCB = new javax.swing.JCheckBox();
		igtRandomOTCB = new javax.swing.JCheckBox();
		igtRandomIVsCB = new javax.swing.JCheckBox();
		igtRandomItemCB = new javax.swing.JCheckBox();
		fieldItemsPanel = new javax.swing.JPanel();
		fiUnchangedRB = new javax.swing.JRadioButton();
		fiShuffleRB = new javax.swing.JRadioButton();
		fiRandomRB = new javax.swing.JRadioButton();
		openROMButton = new javax.swing.JButton();
		saveROMButton = new javax.swing.JButton();
		usePresetsButton = new javax.swing.JButton();
		aboutButton = new javax.swing.JButton();
		otherOptionsPanel = new javax.swing.JPanel();
		codeTweaksCB = new javax.swing.JCheckBox();
		raceModeCB = new javax.swing.JCheckBox();
		randomizeHollowsCB = new javax.swing.JCheckBox();
		brokenMovesCB = new javax.swing.JCheckBox();
		codeTweaksBtn = new javax.swing.JButton();
		pokeLimitCB = new javax.swing.JCheckBox();
		pokeLimitBtn = new javax.swing.JButton();
		loadQSButton = new javax.swing.JButton();
		saveQSButton = new javax.swing.JButton();
		updateSettingsButton = new javax.swing.JButton();

		romOpenChooser.setFileFilter(new ROMFilter());

		romSaveChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
		romSaveChooser.setFileFilter(new ROMFilter());

		qsOpenChooser.setFileFilter(new QSFileFilter());

		qsSaveChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
		qsSaveChooser.setFileFilter(new QSFileFilter());

		java.util.ResourceBundle bundle = java.util.ResourceBundle
				.getBundle("com/dabomstew/pkrandom/gui/Bundle"); // NOI18N
		toggleAutoUpdatesMenuItem.setText(bundle
				.getString("RandomizerGUI.toggleAutoUpdatesMenuItem.text")); // NOI18N
		toggleAutoUpdatesMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						toggleAutoUpdatesMenuItemActionPerformed(evt);
					}
				});
		updateSettingsMenu.add(toggleAutoUpdatesMenuItem);

		manualUpdateMenuItem.setText(bundle
				.getString("RandomizerGUI.manualUpdateMenuItem.text")); // NOI18N
		manualUpdateMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						manualUpdateMenuItemActionPerformed(evt);
					}
				});
		updateSettingsMenu.add(manualUpdateMenuItem);

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle(bundle.getString("RandomizerGUI.title")); // NOI18N

		generalOptionsPanel
				.setBorder(javax.swing.BorderFactory.createTitledBorder(
						null,
						bundle.getString("RandomizerGUI.generalOptionsPanel.border.title"),
						javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
						javax.swing.border.TitledBorder.DEFAULT_POSITION,
						new java.awt.Font("Tahoma", 1, 11))); // NOI18N

		goUpdateTypesCheckBox.setText(bundle
				.getString("RandomizerGUI.goUpdateTypesCheckBox.text")); // NOI18N
		goUpdateTypesCheckBox.setToolTipText(bundle
				.getString("RandomizerGUI.goUpdateTypesCheckBox.toolTipText")); // NOI18N

		goUpdateMovesCheckBox.setText(bundle
				.getString("RandomizerGUI.goUpdateMovesCheckBox.text")); // NOI18N
		goUpdateMovesCheckBox.setToolTipText(bundle
				.getString("RandomizerGUI.goUpdateMovesCheckBox.toolTipText")); // NOI18N
		goUpdateMovesCheckBox
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						goUpdateMovesCheckBoxActionPerformed(evt);
					}
				});

		goRemoveTradeEvosCheckBox.setText(bundle
				.getString("RandomizerGUI.goRemoveTradeEvosCheckBox.text")); // NOI18N
		goRemoveTradeEvosCheckBox
				.setToolTipText(bundle
						.getString("RandomizerGUI.goRemoveTradeEvosCheckBox.toolTipText")); // NOI18N

		goLowerCaseNamesCheckBox.setText(bundle
				.getString("RandomizerGUI.goLowerCaseNamesCheckBox.text")); // NOI18N
		goLowerCaseNamesCheckBox
				.setToolTipText(bundle
						.getString("RandomizerGUI.goLowerCaseNamesCheckBox.toolTipText")); // NOI18N

		goNationalDexCheckBox.setText(bundle
				.getString("RandomizerGUI.goNationalDexCheckBox.text")); // NOI18N
		goNationalDexCheckBox.setToolTipText(bundle
				.getString("RandomizerGUI.goNationalDexCheckBox.toolTipText")); // NOI18N

		goUpdateMovesLegacyCheckBox.setText(bundle
				.getString("RandomizerGUI.goUpdateMovesLegacyCheckBox.text")); // NOI18N
		goUpdateMovesLegacyCheckBox
				.setToolTipText(bundle
						.getString("RandomizerGUI.goUpdateMovesLegacyCheckBox.toolTipText")); // NOI18N

		javax.swing.GroupLayout generalOptionsPanelLayout = new javax.swing.GroupLayout(
				generalOptionsPanel);
		generalOptionsPanel.setLayout(generalOptionsPanelLayout);
		generalOptionsPanelLayout
				.setHorizontalGroup(generalOptionsPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								generalOptionsPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												generalOptionsPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																generalOptionsPanelLayout
																		.createSequentialGroup()
																		.addGroup(
																				generalOptionsPanelLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								goUpdateTypesCheckBox)
																						.addComponent(
																								goRemoveTradeEvosCheckBox)
																						.addComponent(
																								goNationalDexCheckBox))
																		.addContainerGap(
																				14,
																				Short.MAX_VALUE))
														.addGroup(
																generalOptionsPanelLayout
																		.createSequentialGroup()
																		.addGroup(
																				generalOptionsPanelLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addGroup(
																								generalOptionsPanelLayout
																										.createSequentialGroup()
																										.addComponent(
																												goUpdateMovesCheckBox)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																										.addComponent(
																												goUpdateMovesLegacyCheckBox))
																						.addComponent(
																								goLowerCaseNamesCheckBox))
																		.addGap(0,
																				0,
																				Short.MAX_VALUE)))));
		generalOptionsPanelLayout
				.setVerticalGroup(generalOptionsPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								generalOptionsPanelLayout
										.createSequentialGroup()
										.addComponent(goUpdateTypesCheckBox)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												generalOptionsPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																goUpdateMovesCheckBox)
														.addComponent(
																goUpdateMovesLegacyCheckBox))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(goRemoveTradeEvosCheckBox)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(goLowerCaseNamesCheckBox)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(goNationalDexCheckBox)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		romInfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null,
				bundle.getString("RandomizerGUI.romInfoPanel.border.title"),
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Tahoma", 1, 11))); // NOI18N

		riRomNameLabel.setText(bundle
				.getString("RandomizerGUI.riRomNameLabel.text")); // NOI18N

		riRomCodeLabel.setText(bundle
				.getString("RandomizerGUI.riRomCodeLabel.text")); // NOI18N

		riRomSupportLabel.setText(bundle
				.getString("RandomizerGUI.riRomSupportLabel.text")); // NOI18N

		javax.swing.GroupLayout romInfoPanelLayout = new javax.swing.GroupLayout(
				romInfoPanel);
		romInfoPanel.setLayout(romInfoPanelLayout);
		romInfoPanelLayout
				.setHorizontalGroup(romInfoPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								romInfoPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												romInfoPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																riRomNameLabel)
														.addComponent(
																riRomCodeLabel)
														.addComponent(
																riRomSupportLabel))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
		romInfoPanelLayout
				.setVerticalGroup(romInfoPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								romInfoPanelLayout
										.createSequentialGroup()
										.addGap(5, 5, 5)
										.addComponent(riRomNameLabel)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(riRomCodeLabel)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(riRomSupportLabel)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		baseStatsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null,
				bundle.getString("RandomizerGUI.baseStatsPanel.border.title"),
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Tahoma", 1, 11))); // NOI18N

		pokeStatChangesButtonGroup.add(pbsChangesUnchangedRB);
		pbsChangesUnchangedRB.setSelected(true);
		pbsChangesUnchangedRB.setText(bundle
				.getString("RandomizerGUI.pbsChangesUnchangedRB.text")); // NOI18N
		pbsChangesUnchangedRB.setToolTipText(bundle
				.getString("RandomizerGUI.pbsChangesUnchangedRB.toolTipText")); // NOI18N

		pokeStatChangesButtonGroup.add(pbsChangesShuffleRB);
		pbsChangesShuffleRB.setText(bundle
				.getString("RandomizerGUI.pbsChangesShuffleRB.text")); // NOI18N
		pbsChangesShuffleRB.setToolTipText(bundle
				.getString("RandomizerGUI.pbsChangesShuffleRB.toolTipText")); // NOI18N

		pokeStatChangesButtonGroup.add(pbsChangesRandomEvosRB);
		pbsChangesRandomEvosRB.setText(bundle
				.getString("RandomizerGUI.pbsChangesRandomEvosRB.text")); // NOI18N
		pbsChangesRandomEvosRB.setToolTipText(bundle
				.getString("RandomizerGUI.pbsChangesRandomEvosRB.toolTipText")); // NOI18N

		pokeStatChangesButtonGroup.add(pbsChangesRandomTotalRB);
		pbsChangesRandomTotalRB.setText(bundle
				.getString("RandomizerGUI.pbsChangesRandomTotalRB.text")); // NOI18N
		pbsChangesRandomTotalRB
				.setToolTipText(bundle
						.getString("RandomizerGUI.pbsChangesRandomTotalRB.toolTipText")); // NOI18N

		pbsStandardEXPCurvesCB.setText(bundle
				.getString("RandomizerGUI.pbsStandardEXPCurvesCB.text")); // NOI18N
		pbsStandardEXPCurvesCB.setToolTipText(bundle
				.getString("RandomizerGUI.pbsStandardEXPCurvesCB.toolTipText")); // NOI18N

		javax.swing.GroupLayout baseStatsPanelLayout = new javax.swing.GroupLayout(
				baseStatsPanel);
		baseStatsPanel.setLayout(baseStatsPanelLayout);
		baseStatsPanelLayout
				.setHorizontalGroup(baseStatsPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								baseStatsPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												baseStatsPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																baseStatsPanelLayout
																		.createSequentialGroup()
																		.addGroup(
																				baseStatsPanelLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								pbsChangesUnchangedRB)
																						.addComponent(
																								pbsChangesRandomEvosRB)
																						.addComponent(
																								pbsChangesRandomTotalRB))
																		.addContainerGap(
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE))
														.addGroup(
																baseStatsPanelLayout
																		.createSequentialGroup()
																		.addComponent(
																				pbsChangesShuffleRB)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				125,
																				Short.MAX_VALUE)
																		.addComponent(
																				pbsStandardEXPCurvesCB)
																		.addGap(38,
																				38,
																				38)))));
		baseStatsPanelLayout
				.setVerticalGroup(baseStatsPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								baseStatsPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(pbsChangesUnchangedRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												baseStatsPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																pbsChangesShuffleRB)
														.addComponent(
																pbsStandardEXPCurvesCB))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(pbsChangesRandomEvosRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(pbsChangesRandomTotalRB)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		pokemonTypesPanel
				.setBorder(javax.swing.BorderFactory.createTitledBorder(
						null,
						bundle.getString("RandomizerGUI.pokemonTypesPanel.border.title"),
						javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
						javax.swing.border.TitledBorder.DEFAULT_POSITION,
						new java.awt.Font("Tahoma", 1, 11))); // NOI18N

		pokeTypesButtonGroup.add(ptUnchangedRB);
		ptUnchangedRB.setSelected(true);
		ptUnchangedRB.setText(bundle
				.getString("RandomizerGUI.ptUnchangedRB.text")); // NOI18N
		ptUnchangedRB.setToolTipText(bundle
				.getString("RandomizerGUI.ptUnchangedRB.toolTipText")); // NOI18N

		pokeTypesButtonGroup.add(ptRandomFollowEvosRB);
		ptRandomFollowEvosRB.setText(bundle
				.getString("RandomizerGUI.ptRandomFollowEvosRB.text")); // NOI18N
		ptRandomFollowEvosRB.setToolTipText(bundle
				.getString("RandomizerGUI.ptRandomFollowEvosRB.toolTipText")); // NOI18N

		pokeTypesButtonGroup.add(ptRandomTotalRB);
		ptRandomTotalRB.setText(bundle
				.getString("RandomizerGUI.ptRandomTotalRB.text")); // NOI18N
		ptRandomTotalRB.setToolTipText(bundle
				.getString("RandomizerGUI.ptRandomTotalRB.toolTipText")); // NOI18N

		javax.swing.GroupLayout pokemonTypesPanelLayout = new javax.swing.GroupLayout(
				pokemonTypesPanel);
		pokemonTypesPanel.setLayout(pokemonTypesPanelLayout);
		pokemonTypesPanelLayout
				.setHorizontalGroup(pokemonTypesPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								pokemonTypesPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												pokemonTypesPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																ptUnchangedRB)
														.addComponent(
																ptRandomFollowEvosRB)
														.addComponent(
																ptRandomTotalRB))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
		pokemonTypesPanelLayout
				.setVerticalGroup(pokemonTypesPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								pokemonTypesPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(ptUnchangedRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(ptRandomFollowEvosRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(ptRandomTotalRB)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		pokemonMovesetsPanel
				.setBorder(javax.swing.BorderFactory.createTitledBorder(
						null,
						bundle.getString("RandomizerGUI.pokemonMovesetsPanel.border.title"),
						javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
						javax.swing.border.TitledBorder.DEFAULT_POSITION,
						new java.awt.Font("Tahoma", 1, 11))); // NOI18N

		pokeMovesetsButtonGroup.add(pmsUnchangedRB);
		pmsUnchangedRB.setSelected(true);
		pmsUnchangedRB.setText(bundle
				.getString("RandomizerGUI.pmsUnchangedRB.text")); // NOI18N
		pmsUnchangedRB.setToolTipText(bundle
				.getString("RandomizerGUI.pmsUnchangedRB.toolTipText")); // NOI18N
		pmsUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				pmsUnchangedRBActionPerformed(evt);
			}
		});

		pokeMovesetsButtonGroup.add(pmsRandomTypeRB);
		pmsRandomTypeRB.setText(bundle
				.getString("RandomizerGUI.pmsRandomTypeRB.text")); // NOI18N
		pmsRandomTypeRB.setToolTipText(bundle
				.getString("RandomizerGUI.pmsRandomTypeRB.toolTipText")); // NOI18N
		pmsRandomTypeRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				pmsRandomTypeRBActionPerformed(evt);
			}
		});

		pokeMovesetsButtonGroup.add(pmsRandomTotalRB);
		pmsRandomTotalRB.setText(bundle
				.getString("RandomizerGUI.pmsRandomTotalRB.text")); // NOI18N
		pmsRandomTotalRB.setToolTipText(bundle
				.getString("RandomizerGUI.pmsRandomTotalRB.toolTipText")); // NOI18N
		pmsRandomTotalRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				pmsRandomTotalRBActionPerformed(evt);
			}
		});

		pokeMovesetsButtonGroup.add(pmsMetronomeOnlyRB);
		pmsMetronomeOnlyRB.setText(bundle
				.getString("RandomizerGUI.pmsMetronomeOnlyRB.text")); // NOI18N
		pmsMetronomeOnlyRB.setToolTipText(bundle
				.getString("RandomizerGUI.pmsMetronomeOnlyRB.toolTipText")); // NOI18N
		pmsMetronomeOnlyRB
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						pmsMetronomeOnlyRBActionPerformed(evt);
					}
				});

		pms4MovesCB.setText(bundle.getString("RandomizerGUI.pms4MovesCB.text")); // NOI18N
		pms4MovesCB.setToolTipText(bundle
				.getString("RandomizerGUI.pms4MovesCB.toolTipText")); // NOI18N

		javax.swing.GroupLayout pokemonMovesetsPanelLayout = new javax.swing.GroupLayout(
				pokemonMovesetsPanel);
		pokemonMovesetsPanel.setLayout(pokemonMovesetsPanelLayout);
		pokemonMovesetsPanelLayout
				.setHorizontalGroup(pokemonMovesetsPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								pokemonMovesetsPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												pokemonMovesetsPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																pmsUnchangedRB)
														.addGroup(
																pokemonMovesetsPanelLayout
																		.createSequentialGroup()
																		.addComponent(
																				pmsRandomTypeRB)
																		.addGap(198,
																				198,
																				198)
																		.addComponent(
																				pms4MovesCB))
														.addComponent(
																pmsRandomTotalRB)
														.addComponent(
																pmsMetronomeOnlyRB))
										.addContainerGap(134, Short.MAX_VALUE)));
		pokemonMovesetsPanelLayout
				.setVerticalGroup(pokemonMovesetsPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								pokemonMovesetsPanelLayout
										.createSequentialGroup()
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addComponent(pmsUnchangedRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												pokemonMovesetsPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																pmsRandomTypeRB)
														.addComponent(
																pms4MovesCB))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(pmsRandomTotalRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(pmsMetronomeOnlyRB)));

		trainersPokemonPanel
				.setBorder(javax.swing.BorderFactory.createTitledBorder(
						null,
						bundle.getString("RandomizerGUI.trainersPokemonPanel.border.title"),
						javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
						javax.swing.border.TitledBorder.DEFAULT_POSITION,
						new java.awt.Font("Tahoma", 1, 11))); // NOI18N

		trainerPokesButtonGroup.add(tpUnchangedRB);
		tpUnchangedRB.setSelected(true);
		tpUnchangedRB.setText(bundle
				.getString("RandomizerGUI.tpUnchangedRB.text")); // NOI18N
		tpUnchangedRB.setToolTipText(bundle
				.getString("RandomizerGUI.tpUnchangedRB.toolTipText")); // NOI18N
		tpUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				tpUnchangedRBActionPerformed(evt);
			}
		});

		trainerPokesButtonGroup.add(tpRandomRB);
		tpRandomRB.setText(bundle.getString("RandomizerGUI.tpRandomRB.text")); // NOI18N
		tpRandomRB.setToolTipText(bundle
				.getString("RandomizerGUI.tpRandomRB.toolTipText")); // NOI18N
		tpRandomRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				tpRandomRBActionPerformed(evt);
			}
		});

		trainerPokesButtonGroup.add(tpTypeThemedRB);
		tpTypeThemedRB.setText(bundle
				.getString("RandomizerGUI.tpTypeThemedRB.text")); // NOI18N
		tpTypeThemedRB.setToolTipText(bundle
				.getString("RandomizerGUI.tpTypeThemedRB.toolTipText")); // NOI18N
		tpTypeThemedRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				tpTypeThemedRBActionPerformed(evt);
			}
		});

		tpPowerLevelsCB.setText(bundle
				.getString("RandomizerGUI.tpPowerLevelsCB.text")); // NOI18N
		tpPowerLevelsCB.setToolTipText(bundle
				.getString("RandomizerGUI.tpPowerLevelsCB.toolTipText")); // NOI18N
		tpPowerLevelsCB.setEnabled(false);

		tpTypeWeightingCB.setText(bundle
				.getString("RandomizerGUI.tpTypeWeightingCB.text")); // NOI18N
		tpTypeWeightingCB.setToolTipText(bundle
				.getString("RandomizerGUI.tpTypeWeightingCB.toolTipText")); // NOI18N
		tpTypeWeightingCB.setEnabled(false);

		tpRivalCarriesStarterCB.setText(bundle
				.getString("RandomizerGUI.tpRivalCarriesStarterCB.text")); // NOI18N
		tpRivalCarriesStarterCB
				.setToolTipText(bundle
						.getString("RandomizerGUI.tpRivalCarriesStarterCB.toolTipText")); // NOI18N
		tpRivalCarriesStarterCB.setEnabled(false);

		tpNoLegendariesCB.setText(bundle
				.getString("RandomizerGUI.tpNoLegendariesCB.text")); // NOI18N
		tpNoLegendariesCB.setEnabled(false);

		tnRandomizeCB.setText(bundle
				.getString("RandomizerGUI.tnRandomizeCB.text")); // NOI18N
		tnRandomizeCB.setToolTipText(bundle
				.getString("RandomizerGUI.tnRandomizeCB.toolTipText")); // NOI18N

		tcnRandomizeCB.setText(bundle
				.getString("RandomizerGUI.tcnRandomizeCB.text")); // NOI18N
		tcnRandomizeCB.setToolTipText(bundle
				.getString("RandomizerGUI.tcnRandomizeCB.toolTipText")); // NOI18N

		tpNoEarlyShedinjaCB.setText(bundle
				.getString("RandomizerGUI.tpNoEarlyShedinjaCB.text")); // NOI18N
		tpNoEarlyShedinjaCB.setToolTipText(bundle
				.getString("RandomizerGUI.tpNoEarlyShedinjaCB.toolTipText")); // NOI18N

		javax.swing.GroupLayout trainersPokemonPanelLayout = new javax.swing.GroupLayout(
				trainersPokemonPanel);
		trainersPokemonPanel.setLayout(trainersPokemonPanelLayout);
		trainersPokemonPanelLayout
				.setHorizontalGroup(trainersPokemonPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								trainersPokemonPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												trainersPokemonPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																tpTypeThemedRB)
														.addGroup(
																trainersPokemonPanelLayout
																		.createSequentialGroup()
																		.addGroup(
																				trainersPokemonPanelLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								tpUnchangedRB)
																						.addComponent(
																								tpRandomRB))
																		.addGap(47,
																				47,
																				47)
																		.addGroup(
																				trainersPokemonPanelLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								tpNoEarlyShedinjaCB)
																						.addGroup(
																								trainersPokemonPanelLayout
																										.createSequentialGroup()
																										.addGroup(
																												trainersPokemonPanelLayout
																														.createParallelGroup(
																																javax.swing.GroupLayout.Alignment.LEADING,
																																false)
																														.addComponent(
																																tpTypeWeightingCB,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																Short.MAX_VALUE)
																														.addComponent(
																																tpRivalCarriesStarterCB,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																Short.MAX_VALUE)
																														.addComponent(
																																tpPowerLevelsCB,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																Short.MAX_VALUE)
																														.addComponent(
																																tpNoLegendariesCB,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																javax.swing.GroupLayout.DEFAULT_SIZE,
																																Short.MAX_VALUE))
																										.addGap(18,
																												18,
																												18)
																										.addGroup(
																												trainersPokemonPanelLayout
																														.createParallelGroup(
																																javax.swing.GroupLayout.Alignment.LEADING)
																														.addComponent(
																																tnRandomizeCB)
																														.addComponent(
																																tcnRandomizeCB))))))
										.addContainerGap(160, Short.MAX_VALUE)));
		trainersPokemonPanelLayout
				.setVerticalGroup(trainersPokemonPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								trainersPokemonPanelLayout
										.createSequentialGroup()
										.addGroup(
												trainersPokemonPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																tpUnchangedRB)
														.addComponent(
																tpRivalCarriesStarterCB)
														.addComponent(
																tnRandomizeCB))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												trainersPokemonPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																tpRandomRB)
														.addComponent(
																tpPowerLevelsCB)
														.addComponent(
																tcnRandomizeCB))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												trainersPokemonPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																tpTypeThemedRB)
														.addComponent(
																tpTypeWeightingCB))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(tpNoLegendariesCB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(tpNoEarlyShedinjaCB)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		wildPokemonPanel
				.setBorder(javax.swing.BorderFactory.createTitledBorder(
						null,
						bundle.getString("RandomizerGUI.wildPokemonPanel.border.title"),
						javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
						javax.swing.border.TitledBorder.DEFAULT_POSITION,
						new java.awt.Font("Tahoma", 1, 11))); // NOI18N

		wildPokesButtonGroup.add(wpUnchangedRB);
		wpUnchangedRB.setSelected(true);
		wpUnchangedRB.setText(bundle
				.getString("RandomizerGUI.wpUnchangedRB.text")); // NOI18N
		wpUnchangedRB.setToolTipText(bundle
				.getString("RandomizerGUI.wpUnchangedRB.toolTipText")); // NOI18N
		wpUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				wpUnchangedRBActionPerformed(evt);
			}
		});

		wildPokesButtonGroup.add(wpRandomRB);
		wpRandomRB.setText(bundle.getString("RandomizerGUI.wpRandomRB.text")); // NOI18N
		wpRandomRB.setToolTipText(bundle
				.getString("RandomizerGUI.wpRandomRB.toolTipText")); // NOI18N
		wpRandomRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				wpRandomRBActionPerformed(evt);
			}
		});

		wildPokesButtonGroup.add(wpArea11RB);
		wpArea11RB.setText(bundle.getString("RandomizerGUI.wpArea11RB.text")); // NOI18N
		wpArea11RB.setToolTipText(bundle
				.getString("RandomizerGUI.wpArea11RB.toolTipText")); // NOI18N
		wpArea11RB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				wpArea11RBActionPerformed(evt);
			}
		});

		wildPokesButtonGroup.add(wpGlobalRB);
		wpGlobalRB.setText(bundle.getString("RandomizerGUI.wpGlobalRB.text")); // NOI18N
		wpGlobalRB.setToolTipText(bundle
				.getString("RandomizerGUI.wpGlobalRB.toolTipText")); // NOI18N
		wpGlobalRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				wpGlobalRBActionPerformed(evt);
			}
		});

		wildPokemonARulePanel
				.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle
						.getString("RandomizerGUI.wildPokemonARulePanel.border.title"))); // NOI18N

		wildPokesARuleButtonGroup.add(wpARNoneRB);
		wpARNoneRB.setSelected(true);
		wpARNoneRB.setText(bundle.getString("RandomizerGUI.wpARNoneRB.text")); // NOI18N
		wpARNoneRB.setToolTipText(bundle
				.getString("RandomizerGUI.wpARNoneRB.toolTipText")); // NOI18N
		wpARNoneRB.setEnabled(false);
		wpARNoneRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				wpARNoneRBActionPerformed(evt);
			}
		});

		wildPokesARuleButtonGroup.add(wpARCatchEmAllRB);
		wpARCatchEmAllRB.setText(bundle
				.getString("RandomizerGUI.wpARCatchEmAllRB.text")); // NOI18N
		wpARCatchEmAllRB.setToolTipText(bundle
				.getString("RandomizerGUI.wpARCatchEmAllRB.toolTipText")); // NOI18N
		wpARCatchEmAllRB.setEnabled(false);
		wpARCatchEmAllRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				wpARCatchEmAllRBActionPerformed(evt);
			}
		});

		wildPokesARuleButtonGroup.add(wpARTypeThemedRB);
		wpARTypeThemedRB.setText(bundle
				.getString("RandomizerGUI.wpARTypeThemedRB.text")); // NOI18N
		wpARTypeThemedRB.setToolTipText(bundle
				.getString("RandomizerGUI.wpARTypeThemedRB.toolTipText")); // NOI18N
		wpARTypeThemedRB.setEnabled(false);
		wpARTypeThemedRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				wpARTypeThemedRBActionPerformed(evt);
			}
		});

		wildPokesARuleButtonGroup.add(wpARSimilarStrengthRB);
		wpARSimilarStrengthRB.setText(bundle
				.getString("RandomizerGUI.wpARSimilarStrengthRB.text")); // NOI18N
		wpARSimilarStrengthRB.setToolTipText(bundle
				.getString("RandomizerGUI.wpARSimilarStrengthRB.toolTipText")); // NOI18N
		wpARSimilarStrengthRB.setEnabled(false);
		wpARSimilarStrengthRB
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						wpARSimilarStrengthRBActionPerformed(evt);
					}
				});

		javax.swing.GroupLayout wildPokemonARulePanelLayout = new javax.swing.GroupLayout(
				wildPokemonARulePanel);
		wildPokemonARulePanel.setLayout(wildPokemonARulePanelLayout);
		wildPokemonARulePanelLayout
				.setHorizontalGroup(wildPokemonARulePanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								wildPokemonARulePanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												wildPokemonARulePanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																wildPokemonARulePanelLayout
																		.createSequentialGroup()
																		.addComponent(
																				wpARTypeThemedRB)
																		.addGap(0,
																				0,
																				Short.MAX_VALUE))
														.addGroup(
																wildPokemonARulePanelLayout
																		.createSequentialGroup()
																		.addGroup(
																				wildPokemonARulePanelLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								wpARSimilarStrengthRB)
																						.addComponent(
																								wpARNoneRB)
																						.addComponent(
																								wpARCatchEmAllRB))
																		.addContainerGap(
																				58,
																				Short.MAX_VALUE)))));
		wildPokemonARulePanelLayout
				.setVerticalGroup(wildPokemonARulePanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								wildPokemonARulePanelLayout
										.createSequentialGroup()
										.addComponent(wpARNoneRB)
										.addGap(3, 3, 3)
										.addComponent(wpARSimilarStrengthRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(wpARCatchEmAllRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												3, Short.MAX_VALUE)
										.addComponent(
												wpARTypeThemedRB,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												30,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap()));

		wpUseTimeCB.setText(bundle.getString("RandomizerGUI.wpUseTimeCB.text")); // NOI18N
		wpUseTimeCB.setToolTipText(bundle
				.getString("RandomizerGUI.wpUseTimeCB.toolTipText")); // NOI18N

		wpNoLegendariesCB.setText(bundle
				.getString("RandomizerGUI.wpNoLegendariesCB.text")); // NOI18N

		wpCatchRateCB.setText(bundle
				.getString("RandomizerGUI.wpCatchRateCB.text")); // NOI18N
		wpCatchRateCB.setToolTipText(bundle
				.getString("RandomizerGUI.wpCatchRateCB.toolTipText")); // NOI18N

		wpHeldItemsCB.setText(bundle
				.getString("RandomizerGUI.wpHeldItemsCB.text")); // NOI18N
		wpHeldItemsCB.setToolTipText(bundle
				.getString("RandomizerGUI.wpHeldItemsCB.toolTipText")); // NOI18N

		javax.swing.GroupLayout wildPokemonPanelLayout = new javax.swing.GroupLayout(
				wildPokemonPanel);
		wildPokemonPanel.setLayout(wildPokemonPanelLayout);
		wildPokemonPanelLayout
				.setHorizontalGroup(wildPokemonPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								wildPokemonPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												wildPokemonPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																wpUnchangedRB)
														.addComponent(
																wpRandomRB)
														.addComponent(
																wpArea11RB)
														.addComponent(
																wpGlobalRB))
										.addGap(18, 18, 18)
										.addComponent(
												wildPokemonARulePanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(18, 18, 18)
										.addGroup(
												wildPokemonPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																wpUseTimeCB)
														.addComponent(
																wpNoLegendariesCB)
														.addComponent(
																wpCatchRateCB)
														.addComponent(
																wpHeldItemsCB))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
		wildPokemonPanelLayout
				.setVerticalGroup(wildPokemonPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.TRAILING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.LEADING,
								wildPokemonPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(wpUnchangedRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(wpRandomRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(wpArea11RB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(wpGlobalRB))
						.addGroup(
								javax.swing.GroupLayout.Alignment.LEADING,
								wildPokemonPanelLayout
										.createSequentialGroup()
										.addGap(28, 28, 28)
										.addComponent(wpUseTimeCB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(wpNoLegendariesCB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(wpCatchRateCB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(wpHeldItemsCB))
						.addGroup(
								javax.swing.GroupLayout.Alignment.LEADING,
								wildPokemonPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												wildPokemonARulePanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)));

		starterPokemonPanel
				.setBorder(javax.swing.BorderFactory.createTitledBorder(
						null,
						bundle.getString("RandomizerGUI.starterPokemonPanel.border.title"),
						javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
						javax.swing.border.TitledBorder.DEFAULT_POSITION,
						new java.awt.Font("Tahoma", 1, 11))); // NOI18N

		starterPokemonButtonGroup.add(spUnchangedRB);
		spUnchangedRB.setSelected(true);
		spUnchangedRB.setText(bundle
				.getString("RandomizerGUI.spUnchangedRB.text")); // NOI18N
		spUnchangedRB.setToolTipText(bundle
				.getString("RandomizerGUI.spUnchangedRB.toolTipText")); // NOI18N
		spUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				spUnchangedRBActionPerformed(evt);
			}
		});

		starterPokemonButtonGroup.add(spCustomRB);
		spCustomRB.setText(bundle.getString("RandomizerGUI.spCustomRB.text")); // NOI18N
		spCustomRB.setToolTipText(bundle
				.getString("RandomizerGUI.spCustomRB.toolTipText")); // NOI18N
		spCustomRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				spCustomRBActionPerformed(evt);
			}
		});

		spCustomPoke1Chooser.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
		spCustomPoke1Chooser.setEnabled(false);

		spCustomPoke2Chooser.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
		spCustomPoke2Chooser.setEnabled(false);

		spCustomPoke3Chooser.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
		spCustomPoke3Chooser.setEnabled(false);

		starterPokemonButtonGroup.add(spRandomRB);
		spRandomRB.setText(bundle.getString("RandomizerGUI.spRandomRB.text")); // NOI18N
		spRandomRB.setToolTipText(bundle
				.getString("RandomizerGUI.spRandomRB.toolTipText")); // NOI18N
		spRandomRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				spRandomRBActionPerformed(evt);
			}
		});

		starterPokemonButtonGroup.add(spRandom2EvosRB);
		spRandom2EvosRB.setText(bundle
				.getString("RandomizerGUI.spRandom2EvosRB.text")); // NOI18N
		spRandom2EvosRB.setToolTipText(bundle
				.getString("RandomizerGUI.spRandom2EvosRB.toolTipText")); // NOI18N

		spHeldItemsCB.setText(bundle
				.getString("RandomizerGUI.spHeldItemsCB.text")); // NOI18N
		spHeldItemsCB.setToolTipText(bundle
				.getString("RandomizerGUI.spHeldItemsCB.toolTipText")); // NOI18N

		javax.swing.GroupLayout starterPokemonPanelLayout = new javax.swing.GroupLayout(
				starterPokemonPanel);
		starterPokemonPanel.setLayout(starterPokemonPanelLayout);
		starterPokemonPanelLayout
				.setHorizontalGroup(starterPokemonPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								starterPokemonPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												starterPokemonPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																spUnchangedRB)
														.addGroup(
																starterPokemonPanelLayout
																		.createSequentialGroup()
																		.addComponent(
																				spCustomRB)
																		.addGap(18,
																				18,
																				18)
																		.addComponent(
																				spCustomPoke1Chooser,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				90,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				spCustomPoke2Chooser,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				90,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				spCustomPoke3Chooser,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				90,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addGap(18,
																				18,
																				18)
																		.addComponent(
																				spHeldItemsCB))
														.addComponent(
																spRandomRB)
														.addComponent(
																spRandom2EvosRB))
										.addContainerGap(162, Short.MAX_VALUE)));
		starterPokemonPanelLayout
				.setVerticalGroup(starterPokemonPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								starterPokemonPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(spUnchangedRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												starterPokemonPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																spCustomRB)
														.addComponent(
																spCustomPoke1Chooser,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																spCustomPoke2Chooser,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																spCustomPoke3Chooser,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																spHeldItemsCB))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(spRandomRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(spRandom2EvosRB)
										.addContainerGap(11, Short.MAX_VALUE)));

		staticPokemonPanel
				.setBorder(javax.swing.BorderFactory.createTitledBorder(
						null,
						bundle.getString("RandomizerGUI.staticPokemonPanel.border.title"),
						javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
						javax.swing.border.TitledBorder.DEFAULT_POSITION,
						new java.awt.Font("Tahoma", 1, 11))); // NOI18N

		staticPokemonButtonGroup.add(stpUnchangedRB);
		stpUnchangedRB.setSelected(true);
		stpUnchangedRB.setText(bundle
				.getString("RandomizerGUI.stpUnchangedRB.text")); // NOI18N
		stpUnchangedRB.setToolTipText(bundle
				.getString("RandomizerGUI.stpUnchangedRB.toolTipText")); // NOI18N

		staticPokemonButtonGroup.add(stpRandomL4LRB);
		stpRandomL4LRB.setText(bundle
				.getString("RandomizerGUI.stpRandomL4LRB.text")); // NOI18N
		stpRandomL4LRB.setToolTipText(bundle
				.getString("RandomizerGUI.stpRandomL4LRB.toolTipText")); // NOI18N

		staticPokemonButtonGroup.add(stpRandomTotalRB);
		stpRandomTotalRB.setText(bundle
				.getString("RandomizerGUI.stpRandomTotalRB.text")); // NOI18N
		stpRandomTotalRB.setToolTipText(bundle
				.getString("RandomizerGUI.stpRandomTotalRB.toolTipText")); // NOI18N

		javax.swing.GroupLayout staticPokemonPanelLayout = new javax.swing.GroupLayout(
				staticPokemonPanel);
		staticPokemonPanel.setLayout(staticPokemonPanelLayout);
		staticPokemonPanelLayout
				.setHorizontalGroup(staticPokemonPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								staticPokemonPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												staticPokemonPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																stpUnchangedRB)
														.addComponent(
																stpRandomL4LRB)
														.addComponent(
																stpRandomTotalRB))
										.addContainerGap(401, Short.MAX_VALUE)));
		staticPokemonPanelLayout
				.setVerticalGroup(staticPokemonPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								staticPokemonPanelLayout
										.createSequentialGroup()
										.addComponent(stpUnchangedRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(stpRandomL4LRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(stpRandomTotalRB)));

		tmhmsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
				bundle.getString("RandomizerGUI.tmhmsPanel.border.title"),
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Tahoma", 1, 11))); // NOI18N

		tmMovesPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(bundle
						.getString("RandomizerGUI.tmMovesPanel.border.title"))); // NOI18N

		tmMovesButtonGroup.add(tmmUnchangedRB);
		tmmUnchangedRB.setSelected(true);
		tmmUnchangedRB.setText(bundle
				.getString("RandomizerGUI.tmmUnchangedRB.text")); // NOI18N
		tmmUnchangedRB.setToolTipText(bundle
				.getString("RandomizerGUI.tmmUnchangedRB.toolTipText")); // NOI18N

		tmMovesButtonGroup.add(tmmRandomRB);
		tmmRandomRB.setText(bundle.getString("RandomizerGUI.tmmRandomRB.text")); // NOI18N
		tmmRandomRB.setToolTipText(bundle
				.getString("RandomizerGUI.tmmRandomRB.toolTipText")); // NOI18N

		javax.swing.GroupLayout tmMovesPanelLayout = new javax.swing.GroupLayout(
				tmMovesPanel);
		tmMovesPanel.setLayout(tmMovesPanelLayout);
		tmMovesPanelLayout
				.setHorizontalGroup(tmMovesPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								tmMovesPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												tmMovesPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																tmmUnchangedRB)
														.addComponent(
																tmmRandomRB))
										.addContainerGap(118, Short.MAX_VALUE)));
		tmMovesPanelLayout
				.setVerticalGroup(tmMovesPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								tmMovesPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(tmmUnchangedRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(tmmRandomRB)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		tmHmCompatPanel
				.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle
						.getString("RandomizerGUI.tmHmCompatPanel.border.title"))); // NOI18N

		tmHmCompatibilityButtonGroup.add(thcUnchangedRB);
		thcUnchangedRB.setSelected(true);
		thcUnchangedRB.setText(bundle
				.getString("RandomizerGUI.thcUnchangedRB.text")); // NOI18N
		thcUnchangedRB.setToolTipText(bundle
				.getString("RandomizerGUI.thcUnchangedRB.toolTipText")); // NOI18N

		tmHmCompatibilityButtonGroup.add(thcRandomTypeRB);
		thcRandomTypeRB.setText(bundle
				.getString("RandomizerGUI.thcRandomTypeRB.text")); // NOI18N
		thcRandomTypeRB.setToolTipText(bundle
				.getString("RandomizerGUI.thcRandomTypeRB.toolTipText")); // NOI18N

		tmHmCompatibilityButtonGroup.add(thcRandomTotalRB);
		thcRandomTotalRB.setText(bundle
				.getString("RandomizerGUI.thcRandomTotalRB.text")); // NOI18N
		thcRandomTotalRB.setToolTipText(bundle
				.getString("RandomizerGUI.thcRandomTotalRB.toolTipText")); // NOI18N

		javax.swing.GroupLayout tmHmCompatPanelLayout = new javax.swing.GroupLayout(
				tmHmCompatPanel);
		tmHmCompatPanel.setLayout(tmHmCompatPanelLayout);
		tmHmCompatPanelLayout
				.setHorizontalGroup(tmHmCompatPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								tmHmCompatPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												tmHmCompatPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																thcUnchangedRB)
														.addComponent(
																thcRandomTypeRB)
														.addComponent(
																thcRandomTotalRB))
										.addContainerGap(79, Short.MAX_VALUE)));
		tmHmCompatPanelLayout
				.setVerticalGroup(tmHmCompatPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								tmHmCompatPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(thcUnchangedRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(thcRandomTypeRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(thcRandomTotalRB)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		javax.swing.GroupLayout tmhmsPanelLayout = new javax.swing.GroupLayout(
				tmhmsPanel);
		tmhmsPanel.setLayout(tmhmsPanelLayout);
		tmhmsPanelLayout
				.setHorizontalGroup(tmhmsPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								tmhmsPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												tmMovesPanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addComponent(
												tmHmCompatPanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap()));
		tmhmsPanelLayout
				.setVerticalGroup(tmhmsPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								tmhmsPanelLayout
										.createSequentialGroup()
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addGroup(
												tmhmsPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addComponent(
																tmHmCompatPanel,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																tmMovesPanel,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))));

		abilitiesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null,
				bundle.getString("RandomizerGUI.abilitiesPanel.border.title"),
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Tahoma", 1, 11))); // NOI18N

		pokeAbilitiesButtonGroup.add(paUnchangedRB);
		paUnchangedRB.setSelected(true);
		paUnchangedRB.setText(bundle
				.getString("RandomizerGUI.paUnchangedRB.text")); // NOI18N
		paUnchangedRB.setToolTipText(bundle
				.getString("RandomizerGUI.paUnchangedRB.toolTipText")); // NOI18N
		paUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				paUnchangedRBActionPerformed(evt);
			}
		});

		pokeAbilitiesButtonGroup.add(paRandomizeRB);
		paRandomizeRB.setText(bundle
				.getString("RandomizerGUI.paRandomizeRB.text")); // NOI18N
		paRandomizeRB.setToolTipText(bundle
				.getString("RandomizerGUI.paRandomizeRB.toolTipText")); // NOI18N
		paRandomizeRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				paRandomizeRBActionPerformed(evt);
			}
		});

		paWonderGuardCB.setText(bundle
				.getString("RandomizerGUI.paWonderGuardCB.text")); // NOI18N
		paWonderGuardCB.setToolTipText(bundle
				.getString("RandomizerGUI.paWonderGuardCB.toolTipText")); // NOI18N

		javax.swing.GroupLayout abilitiesPanelLayout = new javax.swing.GroupLayout(
				abilitiesPanel);
		abilitiesPanel.setLayout(abilitiesPanelLayout);
		abilitiesPanelLayout
				.setHorizontalGroup(abilitiesPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								abilitiesPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												abilitiesPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																paUnchangedRB)
														.addComponent(
																paRandomizeRB)
														.addComponent(
																paWonderGuardCB))
										.addContainerGap(190, Short.MAX_VALUE)));
		abilitiesPanelLayout
				.setVerticalGroup(abilitiesPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								abilitiesPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(paUnchangedRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(paRandomizeRB)
										.addGap(18, 18, 18)
										.addComponent(paWonderGuardCB)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		moveTutorsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null,
				bundle.getString("RandomizerGUI.moveTutorsPanel.border.title"),
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Tahoma", 1, 11))); // NOI18N

		mtMovesPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(bundle
						.getString("RandomizerGUI.mtMovesPanel.border.title"))); // NOI18N

		mtMovesButtonGroup.add(mtmUnchangedRB);
		mtmUnchangedRB.setSelected(true);
		mtmUnchangedRB.setText(bundle
				.getString("RandomizerGUI.mtmUnchangedRB.text")); // NOI18N
		mtmUnchangedRB.setToolTipText(bundle
				.getString("RandomizerGUI.mtmUnchangedRB.toolTipText")); // NOI18N
		mtmUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				mtmUnchangedRBActionPerformed(evt);
			}
		});

		mtMovesButtonGroup.add(mtmRandomRB);
		mtmRandomRB.setText(bundle.getString("RandomizerGUI.mtmRandomRB.text")); // NOI18N
		mtmRandomRB.setToolTipText(bundle
				.getString("RandomizerGUI.mtmRandomRB.toolTipText")); // NOI18N

		javax.swing.GroupLayout mtMovesPanelLayout = new javax.swing.GroupLayout(
				mtMovesPanel);
		mtMovesPanel.setLayout(mtMovesPanelLayout);
		mtMovesPanelLayout
				.setHorizontalGroup(mtMovesPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								mtMovesPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												mtMovesPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																mtmUnchangedRB)
														.addComponent(
																mtmRandomRB))
										.addContainerGap(118, Short.MAX_VALUE)));
		mtMovesPanelLayout
				.setVerticalGroup(mtMovesPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								mtMovesPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(mtmUnchangedRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(mtmRandomRB)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		mtCompatPanel
				.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle
						.getString("RandomizerGUI.mtCompatPanel.border.title"))); // NOI18N

		mtCompatibilityButtonGroup.add(mtcUnchangedRB);
		mtcUnchangedRB.setSelected(true);
		mtcUnchangedRB.setText(bundle
				.getString("RandomizerGUI.mtcUnchangedRB.text")); // NOI18N
		mtcUnchangedRB.setToolTipText(bundle
				.getString("RandomizerGUI.mtcUnchangedRB.toolTipText")); // NOI18N

		mtCompatibilityButtonGroup.add(mtcRandomTypeRB);
		mtcRandomTypeRB.setText(bundle
				.getString("RandomizerGUI.mtcRandomTypeRB.text")); // NOI18N
		mtcRandomTypeRB.setToolTipText(bundle
				.getString("RandomizerGUI.mtcRandomTypeRB.toolTipText")); // NOI18N

		mtCompatibilityButtonGroup.add(mtcRandomTotalRB);
		mtcRandomTotalRB.setText(bundle
				.getString("RandomizerGUI.mtcRandomTotalRB.text")); // NOI18N
		mtcRandomTotalRB.setToolTipText(bundle
				.getString("RandomizerGUI.mtcRandomTotalRB.toolTipText")); // NOI18N

		javax.swing.GroupLayout mtCompatPanelLayout = new javax.swing.GroupLayout(
				mtCompatPanel);
		mtCompatPanel.setLayout(mtCompatPanelLayout);
		mtCompatPanelLayout
				.setHorizontalGroup(mtCompatPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								mtCompatPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												mtCompatPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																mtcUnchangedRB)
														.addComponent(
																mtcRandomTypeRB)
														.addComponent(
																mtcRandomTotalRB))
										.addContainerGap(79, Short.MAX_VALUE)));
		mtCompatPanelLayout
				.setVerticalGroup(mtCompatPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								mtCompatPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(mtcUnchangedRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(mtcRandomTypeRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(mtcRandomTotalRB)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		mtNoExistLabel.setText(bundle
				.getString("RandomizerGUI.mtNoExistLabel.text")); // NOI18N

		javax.swing.GroupLayout moveTutorsPanelLayout = new javax.swing.GroupLayout(
				moveTutorsPanel);
		moveTutorsPanel.setLayout(moveTutorsPanelLayout);
		moveTutorsPanelLayout
				.setHorizontalGroup(moveTutorsPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								moveTutorsPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												moveTutorsPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																moveTutorsPanelLayout
																		.createSequentialGroup()
																		.addComponent(
																				mtMovesPanel,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addComponent(
																				mtCompatPanel,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(
																moveTutorsPanelLayout
																		.createSequentialGroup()
																		.addComponent(
																				mtNoExistLabel)
																		.addGap(0,
																				0,
																				Short.MAX_VALUE)))
										.addContainerGap()));
		moveTutorsPanelLayout
				.setVerticalGroup(moveTutorsPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								moveTutorsPanelLayout
										.createSequentialGroup()
										.addComponent(mtNoExistLabel)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addGroup(
												moveTutorsPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addComponent(
																mtCompatPanel,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																mtMovesPanel,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))));

		inGameTradesPanel
				.setBorder(javax.swing.BorderFactory.createTitledBorder(
						null,
						bundle.getString("RandomizerGUI.inGameTradesPanel.border.title"),
						javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
						javax.swing.border.TitledBorder.DEFAULT_POSITION,
						new java.awt.Font("Tahoma", 1, 11))); // NOI18N

		ingameTradesButtonGroup.add(igtUnchangedRB);
		igtUnchangedRB.setSelected(true);
		igtUnchangedRB.setText(bundle
				.getString("RandomizerGUI.igtUnchangedRB.text")); // NOI18N
		igtUnchangedRB.setToolTipText(bundle
				.getString("RandomizerGUI.igtUnchangedRB.toolTipText")); // NOI18N
		igtUnchangedRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				igtUnchangedRBActionPerformed(evt);
			}
		});

		ingameTradesButtonGroup.add(igtGivenOnlyRB);
		igtGivenOnlyRB.setText(bundle
				.getString("RandomizerGUI.igtGivenOnlyRB.text")); // NOI18N
		igtGivenOnlyRB.setToolTipText(bundle
				.getString("RandomizerGUI.igtGivenOnlyRB.toolTipText")); // NOI18N
		igtGivenOnlyRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				igtGivenOnlyRBActionPerformed(evt);
			}
		});

		ingameTradesButtonGroup.add(igtBothRB);
		igtBothRB.setText(bundle.getString("RandomizerGUI.igtBothRB.text")); // NOI18N
		igtBothRB.setToolTipText(bundle
				.getString("RandomizerGUI.igtBothRB.toolTipText")); // NOI18N
		igtBothRB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				igtBothRBActionPerformed(evt);
			}
		});

		igtRandomNicknameCB.setText(bundle
				.getString("RandomizerGUI.igtRandomNicknameCB.text")); // NOI18N
		igtRandomNicknameCB.setToolTipText(bundle
				.getString("RandomizerGUI.igtRandomNicknameCB.toolTipText")); // NOI18N

		igtRandomOTCB.setText(bundle
				.getString("RandomizerGUI.igtRandomOTCB.text")); // NOI18N
		igtRandomOTCB.setToolTipText(bundle
				.getString("RandomizerGUI.igtRandomOTCB.toolTipText")); // NOI18N

		igtRandomIVsCB.setText(bundle
				.getString("RandomizerGUI.igtRandomIVsCB.text")); // NOI18N
		igtRandomIVsCB.setToolTipText(bundle
				.getString("RandomizerGUI.igtRandomIVsCB.toolTipText")); // NOI18N

		igtRandomItemCB.setText(bundle
				.getString("RandomizerGUI.igtRandomItemCB.text")); // NOI18N
		igtRandomItemCB.setToolTipText(bundle
				.getString("RandomizerGUI.igtRandomItemCB.toolTipText")); // NOI18N

		javax.swing.GroupLayout inGameTradesPanelLayout = new javax.swing.GroupLayout(
				inGameTradesPanel);
		inGameTradesPanel.setLayout(inGameTradesPanelLayout);
		inGameTradesPanelLayout
				.setHorizontalGroup(inGameTradesPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								inGameTradesPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												inGameTradesPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																igtUnchangedRB)
														.addComponent(
																igtGivenOnlyRB)
														.addComponent(igtBothRB))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addGroup(
												inGameTradesPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																igtRandomItemCB)
														.addComponent(
																igtRandomNicknameCB)
														.addComponent(
																igtRandomOTCB)
														.addComponent(
																igtRandomIVsCB))
										.addGap(113, 113, 113)));
		inGameTradesPanelLayout
				.setVerticalGroup(inGameTradesPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								inGameTradesPanelLayout
										.createSequentialGroup()
										.addGroup(
												inGameTradesPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																igtUnchangedRB)
														.addComponent(
																igtRandomNicknameCB))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												inGameTradesPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																igtGivenOnlyRB)
														.addComponent(
																igtRandomOTCB))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												inGameTradesPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(igtBothRB)
														.addComponent(
																igtRandomIVsCB))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(igtRandomItemCB)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		fieldItemsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null,
				bundle.getString("RandomizerGUI.fieldItemsPanel.border.title"),
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Tahoma", 1, 11))); // NOI18N

		fieldItemsButtonGroup.add(fiUnchangedRB);
		fiUnchangedRB.setSelected(true);
		fiUnchangedRB.setText(bundle
				.getString("RandomizerGUI.fiUnchangedRB.text")); // NOI18N
		fiUnchangedRB.setToolTipText(bundle
				.getString("RandomizerGUI.fiUnchangedRB.toolTipText")); // NOI18N

		fieldItemsButtonGroup.add(fiShuffleRB);
		fiShuffleRB.setText(bundle.getString("RandomizerGUI.fiShuffleRB.text")); // NOI18N
		fiShuffleRB.setToolTipText(bundle
				.getString("RandomizerGUI.fiShuffleRB.toolTipText")); // NOI18N

		fieldItemsButtonGroup.add(fiRandomRB);
		fiRandomRB.setText(bundle.getString("RandomizerGUI.fiRandomRB.text")); // NOI18N
		fiRandomRB.setToolTipText(bundle
				.getString("RandomizerGUI.fiRandomRB.toolTipText")); // NOI18N

		javax.swing.GroupLayout fieldItemsPanelLayout = new javax.swing.GroupLayout(
				fieldItemsPanel);
		fieldItemsPanel.setLayout(fieldItemsPanelLayout);
		fieldItemsPanelLayout
				.setHorizontalGroup(fieldItemsPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								fieldItemsPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												fieldItemsPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																fiUnchangedRB)
														.addComponent(
																fiShuffleRB)
														.addComponent(
																fiRandomRB))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
		fieldItemsPanelLayout
				.setVerticalGroup(fieldItemsPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								fieldItemsPanelLayout
										.createSequentialGroup()
										.addComponent(fiUnchangedRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(fiShuffleRB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(fiRandomRB)));

		javax.swing.GroupLayout optionsContainerPanelLayout = new javax.swing.GroupLayout(
				optionsContainerPanel);
		optionsContainerPanel.setLayout(optionsContainerPanelLayout);
		optionsContainerPanelLayout
				.setHorizontalGroup(optionsContainerPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(pokemonTypesPanel,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addComponent(pokemonMovesetsPanel,
								javax.swing.GroupLayout.Alignment.TRAILING,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addComponent(trainersPokemonPanel,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addComponent(wildPokemonPanel,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addComponent(starterPokemonPanel,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addComponent(staticPokemonPanel,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addComponent(tmhmsPanel,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addGroup(
								optionsContainerPanelLayout
										.createSequentialGroup()
										.addComponent(
												baseStatsPanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												abilitiesPanel,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE))
						.addComponent(moveTutorsPanel,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addComponent(inGameTradesPanel,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addComponent(fieldItemsPanel,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE));
		optionsContainerPanelLayout
				.setVerticalGroup(optionsContainerPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								optionsContainerPanelLayout
										.createSequentialGroup()
										.addGroup(
												optionsContainerPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addComponent(
																baseStatsPanel,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																abilitiesPanel,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												starterPokemonPanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												pokemonTypesPanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												pokemonMovesetsPanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												trainersPokemonPanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												wildPokemonPanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												staticPokemonPanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												tmhmsPanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												moveTutorsPanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												inGameTradesPanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												fieldItemsPanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		optionsScrollPane.setViewportView(optionsContainerPanel);

		openROMButton.setText(bundle
				.getString("RandomizerGUI.openROMButton.text")); // NOI18N
		openROMButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				openROMButtonActionPerformed(evt);
			}
		});

		saveROMButton.setText(bundle
				.getString("RandomizerGUI.saveROMButton.text")); // NOI18N
		saveROMButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveROMButtonActionPerformed(evt);
			}
		});

		usePresetsButton.setText(bundle
				.getString("RandomizerGUI.usePresetsButton.text")); // NOI18N
		usePresetsButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				usePresetsButtonActionPerformed(evt);
			}
		});

		aboutButton.setText(bundle.getString("RandomizerGUI.aboutButton.text")); // NOI18N
		aboutButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				aboutButtonActionPerformed(evt);
			}
		});

		otherOptionsPanel
				.setBorder(javax.swing.BorderFactory.createTitledBorder(
						null,
						bundle.getString("RandomizerGUI.otherOptionsPanel.border.title"),
						javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
						javax.swing.border.TitledBorder.DEFAULT_POSITION,
						new java.awt.Font("Tahoma", 1, 11))); // NOI18N

		codeTweaksCB.setText(bundle
				.getString("RandomizerGUI.codeTweaksCB.text")); // NOI18N
		codeTweaksCB.setToolTipText(bundle
				.getString("RandomizerGUI.codeTweaksCB.toolTipText")); // NOI18N
		codeTweaksCB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				codeTweaksCBActionPerformed(evt);
			}
		});

		raceModeCB.setText(bundle.getString("RandomizerGUI.raceModeCB.text")); // NOI18N
		raceModeCB.setToolTipText(bundle
				.getString("RandomizerGUI.raceModeCB.toolTipText")); // NOI18N

		randomizeHollowsCB.setText(bundle
				.getString("RandomizerGUI.randomizeHollowsCB.text")); // NOI18N
		randomizeHollowsCB.setToolTipText(bundle
				.getString("RandomizerGUI.randomizeHollowsCB.toolTipText")); // NOI18N

		brokenMovesCB.setText(bundle
				.getString("RandomizerGUI.brokenMovesCB.text")); // NOI18N
		brokenMovesCB.setToolTipText(bundle
				.getString("RandomizerGUI.brokenMovesCB.toolTipText")); // NOI18N

		codeTweaksBtn.setText(bundle
				.getString("RandomizerGUI.codeTweaksBtn.text")); // NOI18N
		codeTweaksBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				codeTweaksBtnActionPerformed(evt);
			}
		});

		pokeLimitCB.setText(bundle.getString("RandomizerGUI.pokeLimitCB.text")); // NOI18N
		pokeLimitCB.setToolTipText(bundle
				.getString("RandomizerGUI.pokeLimitCB.toolTipText")); // NOI18N
		pokeLimitCB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				pokeLimitCBActionPerformed(evt);
			}
		});

		pokeLimitBtn.setText(bundle
				.getString("RandomizerGUI.pokeLimitBtn.text")); // NOI18N
		pokeLimitBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				pokeLimitBtnActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout otherOptionsPanelLayout = new javax.swing.GroupLayout(
				otherOptionsPanel);
		otherOptionsPanel.setLayout(otherOptionsPanelLayout);
		otherOptionsPanelLayout
				.setHorizontalGroup(otherOptionsPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								otherOptionsPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												otherOptionsPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																raceModeCB)
														.addComponent(
																brokenMovesCB)
														.addGroup(
																otherOptionsPanelLayout
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.TRAILING,
																				false)
																		.addGroup(
																				javax.swing.GroupLayout.Alignment.LEADING,
																				otherOptionsPanelLayout
																						.createSequentialGroup()
																						.addComponent(
																								codeTweaksCB)
																						.addPreferredGap(
																								javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																						.addComponent(
																								codeTweaksBtn,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE))
																		.addGroup(
																				javax.swing.GroupLayout.Alignment.LEADING,
																				otherOptionsPanelLayout
																						.createSequentialGroup()
																						.addComponent(
																								pokeLimitCB)
																						.addPreferredGap(
																								javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																						.addComponent(
																								pokeLimitBtn))))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE))
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								otherOptionsPanelLayout
										.createSequentialGroup()
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addComponent(randomizeHollowsCB)
										.addContainerGap()));
		otherOptionsPanelLayout
				.setVerticalGroup(otherOptionsPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								otherOptionsPanelLayout
										.createSequentialGroup()
										.addGroup(
												otherOptionsPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																codeTweaksCB)
														.addComponent(
																codeTweaksBtn))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												otherOptionsPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																pokeLimitBtn)
														.addComponent(
																pokeLimitCB))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(raceModeCB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(randomizeHollowsCB)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(brokenMovesCB)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		loadQSButton.setText(bundle
				.getString("RandomizerGUI.loadQSButton.text")); // NOI18N
		loadQSButton.setToolTipText(bundle
				.getString("RandomizerGUI.loadQSButton.toolTipText")); // NOI18N
		loadQSButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loadQSButtonActionPerformed(evt);
			}
		});

		saveQSButton.setText(bundle
				.getString("RandomizerGUI.saveQSButton.text")); // NOI18N
		saveQSButton.setToolTipText(bundle
				.getString("RandomizerGUI.saveQSButton.toolTipText")); // NOI18N
		saveQSButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveQSButtonActionPerformed(evt);
			}
		});

		updateSettingsButton.setText(bundle
				.getString("RandomizerGUI.updateSettingsButton.text")); // NOI18N
		updateSettingsButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						updateSettingsButtonActionPerformed(evt);
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
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(
														optionsScrollPane,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														747, Short.MAX_VALUE)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		generalOptionsPanel,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(
																		otherOptionsPanel,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING,
																				false)
																				.addGroup(
																						layout.createSequentialGroup()
																								.addComponent(
																										loadQSButton)
																								.addPreferredGap(
																										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																								.addComponent(
																										saveQSButton))
																				.addComponent(
																						romInfoPanel,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						Short.MAX_VALUE))
																.addGap(18, 18,
																		18)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(
																						saveROMButton,
																						javax.swing.GroupLayout.Alignment.TRAILING,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						147,
																						Short.MAX_VALUE)
																				.addComponent(
																						usePresetsButton,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						Short.MAX_VALUE)
																				.addComponent(
																						openROMButton,
																						javax.swing.GroupLayout.Alignment.TRAILING,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						Short.MAX_VALUE)
																				.addComponent(
																						updateSettingsButton,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						Short.MAX_VALUE)
																				.addComponent(
																						aboutButton,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						Short.MAX_VALUE))))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																false)
																.addComponent(
																		generalOptionsPanel,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)
																.addComponent(
																		otherOptionsPanel,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		0,
																		Short.MAX_VALUE))
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		romInfoPanel,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(
																						loadQSButton)
																				.addComponent(
																						saveQSButton)))
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		openROMButton)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		saveROMButton)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		usePresetsButton)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		updateSettingsButton)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		aboutButton)))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(optionsScrollPane,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										380, Short.MAX_VALUE).addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel abilitiesPanel;
	private javax.swing.JButton aboutButton;
	private javax.swing.JPanel baseStatsPanel;
	private javax.swing.JCheckBox brokenMovesCB;
	private javax.swing.JButton codeTweaksBtn;
	private javax.swing.JCheckBox codeTweaksCB;
	private javax.swing.JRadioButton fiRandomRB;
	private javax.swing.JRadioButton fiShuffleRB;
	private javax.swing.JRadioButton fiUnchangedRB;
	private javax.swing.ButtonGroup fieldItemsButtonGroup;
	private javax.swing.JPanel fieldItemsPanel;
	private javax.swing.JPanel generalOptionsPanel;
	private javax.swing.JCheckBox goLowerCaseNamesCheckBox;
	private javax.swing.JCheckBox goNationalDexCheckBox;
	private javax.swing.JCheckBox goRemoveTradeEvosCheckBox;
	private javax.swing.JCheckBox goUpdateMovesCheckBox;
	private javax.swing.JCheckBox goUpdateMovesLegacyCheckBox;
	private javax.swing.JCheckBox goUpdateTypesCheckBox;
	private javax.swing.JRadioButton igtBothRB;
	private javax.swing.JRadioButton igtGivenOnlyRB;
	private javax.swing.JCheckBox igtRandomIVsCB;
	private javax.swing.JCheckBox igtRandomItemCB;
	private javax.swing.JCheckBox igtRandomNicknameCB;
	private javax.swing.JCheckBox igtRandomOTCB;
	private javax.swing.JRadioButton igtUnchangedRB;
	private javax.swing.JPanel inGameTradesPanel;
	private javax.swing.ButtonGroup ingameTradesButtonGroup;
	private javax.swing.JButton loadQSButton;
	private javax.swing.JMenuItem manualUpdateMenuItem;
	private javax.swing.JPanel moveTutorsPanel;
	private javax.swing.JPanel mtCompatPanel;
	private javax.swing.ButtonGroup mtCompatibilityButtonGroup;
	private javax.swing.ButtonGroup mtMovesButtonGroup;
	private javax.swing.JPanel mtMovesPanel;
	private javax.swing.JLabel mtNoExistLabel;
	private javax.swing.JRadioButton mtcRandomTotalRB;
	private javax.swing.JRadioButton mtcRandomTypeRB;
	private javax.swing.JRadioButton mtcUnchangedRB;
	private javax.swing.JRadioButton mtmRandomRB;
	private javax.swing.JRadioButton mtmUnchangedRB;
	private javax.swing.JButton openROMButton;
	private javax.swing.JPanel optionsContainerPanel;
	private javax.swing.JScrollPane optionsScrollPane;
	private javax.swing.JPanel otherOptionsPanel;
	private javax.swing.JRadioButton paRandomizeRB;
	private javax.swing.JRadioButton paUnchangedRB;
	private javax.swing.JCheckBox paWonderGuardCB;
	private javax.swing.JRadioButton pbsChangesRandomEvosRB;
	private javax.swing.JRadioButton pbsChangesRandomTotalRB;
	private javax.swing.JRadioButton pbsChangesShuffleRB;
	private javax.swing.JRadioButton pbsChangesUnchangedRB;
	private javax.swing.JCheckBox pbsStandardEXPCurvesCB;
	private javax.swing.JCheckBox pms4MovesCB;
	private javax.swing.JRadioButton pmsMetronomeOnlyRB;
	private javax.swing.JRadioButton pmsRandomTotalRB;
	private javax.swing.JRadioButton pmsRandomTypeRB;
	private javax.swing.JRadioButton pmsUnchangedRB;
	private javax.swing.ButtonGroup pokeAbilitiesButtonGroup;
	private javax.swing.JButton pokeLimitBtn;
	private javax.swing.JCheckBox pokeLimitCB;
	private javax.swing.ButtonGroup pokeMovesetsButtonGroup;
	private javax.swing.ButtonGroup pokeStatChangesButtonGroup;
	private javax.swing.ButtonGroup pokeTypesButtonGroup;
	private javax.swing.JPanel pokemonMovesetsPanel;
	private javax.swing.JPanel pokemonTypesPanel;
	private javax.swing.JRadioButton ptRandomFollowEvosRB;
	private javax.swing.JRadioButton ptRandomTotalRB;
	private javax.swing.JRadioButton ptUnchangedRB;
	private javax.swing.JFileChooser qsOpenChooser;
	private javax.swing.JFileChooser qsSaveChooser;
	private javax.swing.JCheckBox raceModeCB;
	private javax.swing.JCheckBox randomizeHollowsCB;
	private javax.swing.JLabel riRomCodeLabel;
	private javax.swing.JLabel riRomNameLabel;
	private javax.swing.JLabel riRomSupportLabel;
	private javax.swing.JPanel romInfoPanel;
	private javax.swing.JFileChooser romOpenChooser;
	private javax.swing.JFileChooser romSaveChooser;
	private javax.swing.JButton saveQSButton;
	private javax.swing.JButton saveROMButton;
	private javax.swing.JComboBox spCustomPoke1Chooser;
	private javax.swing.JComboBox spCustomPoke2Chooser;
	private javax.swing.JComboBox spCustomPoke3Chooser;
	private javax.swing.JRadioButton spCustomRB;
	private javax.swing.JCheckBox spHeldItemsCB;
	private javax.swing.JRadioButton spRandom2EvosRB;
	private javax.swing.JRadioButton spRandomRB;
	private javax.swing.JRadioButton spUnchangedRB;
	private javax.swing.ButtonGroup starterPokemonButtonGroup;
	private javax.swing.JPanel starterPokemonPanel;
	private javax.swing.ButtonGroup staticPokemonButtonGroup;
	private javax.swing.JPanel staticPokemonPanel;
	private javax.swing.JRadioButton stpRandomL4LRB;
	private javax.swing.JRadioButton stpRandomTotalRB;
	private javax.swing.JRadioButton stpUnchangedRB;
	private javax.swing.JCheckBox tcnRandomizeCB;
	private javax.swing.JRadioButton thcRandomTotalRB;
	private javax.swing.JRadioButton thcRandomTypeRB;
	private javax.swing.JRadioButton thcUnchangedRB;
	private javax.swing.JPanel tmHmCompatPanel;
	private javax.swing.ButtonGroup tmHmCompatibilityButtonGroup;
	private javax.swing.ButtonGroup tmMovesButtonGroup;
	private javax.swing.JPanel tmMovesPanel;
	private javax.swing.JPanel tmhmsPanel;
	private javax.swing.JRadioButton tmmRandomRB;
	private javax.swing.JRadioButton tmmUnchangedRB;
	private javax.swing.JCheckBox tnRandomizeCB;
	private javax.swing.JMenuItem toggleAutoUpdatesMenuItem;
	private javax.swing.JCheckBox tpNoEarlyShedinjaCB;
	private javax.swing.JCheckBox tpNoLegendariesCB;
	private javax.swing.JCheckBox tpPowerLevelsCB;
	private javax.swing.JRadioButton tpRandomRB;
	private javax.swing.JCheckBox tpRivalCarriesStarterCB;
	private javax.swing.JRadioButton tpTypeThemedRB;
	private javax.swing.JCheckBox tpTypeWeightingCB;
	private javax.swing.JRadioButton tpUnchangedRB;
	private javax.swing.ButtonGroup trainerPokesButtonGroup;
	private javax.swing.JPanel trainersPokemonPanel;
	private javax.swing.JButton updateSettingsButton;
	private javax.swing.JPopupMenu updateSettingsMenu;
	private javax.swing.JButton usePresetsButton;
	private javax.swing.JPanel wildPokemonARulePanel;
	private javax.swing.JPanel wildPokemonPanel;
	private javax.swing.ButtonGroup wildPokesARuleButtonGroup;
	private javax.swing.ButtonGroup wildPokesButtonGroup;
	private javax.swing.JRadioButton wpARCatchEmAllRB;
	private javax.swing.JRadioButton wpARNoneRB;
	private javax.swing.JRadioButton wpARSimilarStrengthRB;
	private javax.swing.JRadioButton wpARTypeThemedRB;
	private javax.swing.JRadioButton wpArea11RB;
	private javax.swing.JCheckBox wpCatchRateCB;
	private javax.swing.JRadioButton wpGlobalRB;
	private javax.swing.JCheckBox wpHeldItemsCB;
	private javax.swing.JCheckBox wpNoLegendariesCB;
	private javax.swing.JRadioButton wpRandomRB;
	private javax.swing.JRadioButton wpUnchangedRB;
	private javax.swing.JCheckBox wpUseTimeCB;
	// End of variables declaration//GEN-END:variables
}
