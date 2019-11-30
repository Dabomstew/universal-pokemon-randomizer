package com.dabomstew.pkrandom.gui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.SwingUtilities;

import com.dabomstew.pkrandom.SysConstants;
import com.dabomstew.pkrandom.FileFunctions;

/*----------------------------------------------------------------------------*/
/*--  UpdateCheckThread.java - Runs a background task to check for new      --*/
/*--                           updates to this program.                     --*/
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
            byte[] versionCheck = FileFunctions.downloadFile(SysConstants.AUTOUPDATE_URL + "latest.txt");
            int version = Integer.parseInt(new String(versionCheck));
            if (version > SysConstants.UPDATE_VERSION) {
                byte[] output = FileFunctions.downloadFile(SysConstants.AUTOUPDATE_URL + "version.txt");
                Scanner sc = new Scanner(new ByteArrayInputStream(output));
                final int newVersion = Integer.parseInt(sc.nextLine());
                if (newVersion > SysConstants.UPDATE_VERSION) {
                    // Update found, parse changelog
                    StringBuilder changelog = new StringBuilder();
                    int clid = Integer.parseInt(sc.nextLine());
                    String nl = System.getProperty("line.separator");
                    boolean first = true;
                    while (clid > SysConstants.UPDATE_VERSION && sc.hasNext()) {
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
                sc.close();
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
