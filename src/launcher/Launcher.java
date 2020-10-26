package launcher;

/*----------------------------------------------------------------------------*/
/*--  Launcher.java - utility for launching the randomizer with certain     --*/
/*--                  settings.                                             --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2020.                                 --*/
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

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;

public class Launcher {

    private static JFrame frame;

    public static void main(String[] args) {
        try {
            ProcessBuilder pb = new ProcessBuilder("java", "-Xmx4096M", "-jar", "PokeRandoZX.jar", "please-use-the-launcher");
            pb.redirectErrorStream(true);
            File log = new File("launcher-log.txt");
            pb.redirectOutput(ProcessBuilder.Redirect.to(log));

//            throw new IOException("cool");
            Process p = pb.start();
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                frame = new JFrame("Launcher");
                try {
                    String lafName = javax.swing.UIManager.getSystemLookAndFeelClassName();
                    // Only set Native LaF on windows.
                    if (lafName.equalsIgnoreCase("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")) {
                        javax.swing.UIManager.setLookAndFeel(lafName);
                    }
                } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException ex) {
                    ex.printStackTrace();
                }
                String message = e.getMessage();
                Object[] messages = {message};
                JOptionPane.showMessageDialog(frame, messages);
                System.exit(1);
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
