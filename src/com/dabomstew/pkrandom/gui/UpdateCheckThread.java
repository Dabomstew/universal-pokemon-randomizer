package com.dabomstew.pkrandom.gui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.SwingUtilities;

import com.dabomstew.pkrandom.FileFunctions;

public class UpdateCheckThread extends Thread {
	private RandomizerGUI mainWindow;
	private boolean doNotifyNone;

	public UpdateCheckThread(RandomizerGUI mainWindow, boolean doNotifyNone) {
		this.mainWindow = mainWindow;
		this.doNotifyNone = doNotifyNone;
	}

	@Override
	public void run() {
		boolean found = false;
		try {
			byte[] versionCheck = FileFunctions
					.downloadFile("http://pokehacks.dabomstew.com/randomizer/autoupdate/latest.txt");
			int version = Integer.parseInt(new String(versionCheck));
			if (version > RandomizerGUI.UPDATE_VERSION) {
				byte[] output = FileFunctions
						.downloadFile("http://pokehacks.dabomstew.com/randomizer/autoupdate/version.txt");
				Scanner sc = new Scanner(new ByteArrayInputStream(output));
				final int newVersion = Integer.parseInt(sc.nextLine());
				if (newVersion > RandomizerGUI.UPDATE_VERSION) {
					// Update found, parse changelog
					StringBuilder changelog = new StringBuilder();
					int clid = Integer.parseInt(sc.nextLine());
					String nl = System.getProperty("line.separator");
					boolean first = true;
					while (clid > RandomizerGUI.UPDATE_VERSION && sc.hasNext()) {
						if (!first) {
							changelog.append(nl);
						}
						first = false;
						String line = sc.nextLine();
						while (line.equals("EOV") == false && sc.hasNext()) {
							changelog.append(line + nl);
							line = sc.nextLine();
						}
						clid = Integer.parseInt(sc.nextLine());
					}
					final String doneCL = changelog.toString();
					found = true;
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							mainWindow.updateFound(newVersion, doneCL);
						}
					});
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}
		if (!found && this.doNotifyNone) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					mainWindow.noUpdateFound();
				}
			});
		}
	}

}
