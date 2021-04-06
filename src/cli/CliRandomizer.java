package cli;

import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.RandomSource;
import com.dabomstew.pkrandom.Settings;
import com.dabomstew.pkrandom.Randomizer;
import com.dabomstew.pkrandom.romhandlers.*;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class CliRandomizer {

    private final static ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/dabomstew/pkrandom/newgui/Bundle");

    private static boolean performDirectRandomization(
            String settingsFilePath,
            String sourceRomFilePath,
            String destinationRomFilePath
    ) {
        return CliRandomizer.performDirectRandomization(settingsFilePath, sourceRomFilePath, destinationRomFilePath, false);
    }

    private static boolean performDirectRandomization(
            String settingsFilePath,
            String sourceRomFilePath,
            String destinationRomFilePath,
            boolean saveAsDirectory
    ) {
        // borrowed directly from NewRandomizerGUI()
        RomHandler.Factory[] checkHandlers = new RomHandler.Factory[] {
                new Gen1RomHandler.Factory(),
                new Gen2RomHandler.Factory(),
                new Gen3RomHandler.Factory(),
                new Gen4RomHandler.Factory(),
                new Gen5RomHandler.Factory(),
                new Gen6RomHandler.Factory(),
                new Gen7RomHandler.Factory()
        };

        Settings settings;
        try {
            File fh = new File(settingsFilePath);
            FileInputStream fis = new FileInputStream(fh);
            settings = Settings.read(fis);
            // taken from com.dabomstew.pkrandom.newgui.NewRandomizerGUI.saveROM, set distinctly from all other settings
            settings.setCustomNames(FileFunctions.getCustomNames());
            fis.close();
        } catch (UnsupportedOperationException ex) {
            ex.printStackTrace();
            System.err.println(ex.getMessage());
            return false;
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            System.err.println(bundle.getString("GUI.invalidSettingsFile") + "%n");
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println(bundle.getString("GUI.settingsLoadFailed") + "%n");
            return false;
        }

        try {
            File romFileHandler = new File(sourceRomFilePath);
            RomHandler romHandler;

            for (RomHandler.Factory rhf : checkHandlers) {
                if (rhf.isLoadable(romFileHandler.getAbsolutePath())) {
                    romHandler = rhf.create(RandomSource.instance());
                    romHandler.loadRom(romFileHandler.getAbsolutePath());
                    if (saveAsDirectory && romHandler.generationOfPokemon() != 6 && romHandler.generationOfPokemon() != 7) {
                        saveAsDirectory = false;
                        System.out.println("WARNING: Saving as directory does not make sense for generations other than 6 and 7, ignoring...");
                    }

                    CliRandomizer.displaySettingsWarnings(settings, romHandler);

                    List<String> extensions = new ArrayList<>(Arrays.asList("sgb", "gbc", "gba", "nds", "cxi"));
                    extensions.remove(romHandler.getDefaultExtension());
                    File fh = FileFunctions.fixFilename(romFileHandler, romHandler.getDefaultExtension(), extensions);
                    if (romHandler instanceof AbstractDSRomHandler || romHandler instanceof Abstract3DSRomHandler) {
                        String currentFN = romHandler.loadedFilename();
                        if (currentFN.equals(fh.getAbsolutePath())) {
                            System.err.println(bundle.getString("GUI.cantOverwriteDS") + "%n");
                            return false;
                        }
                    }

                    Randomizer randomizer = new Randomizer(settings, romHandler, saveAsDirectory);
                    randomizer.randomize(destinationRomFilePath);
                    System.out.println("Randomized succesfully!");
                    // this is the only successful exit, everything else will return false at the end of the function
                    return true;
                }
            }
            // if we get here it means no rom handlers matched the ROM file
            System.err.printf(bundle.getString("GUI.unsupportedRom") + "%n", romFileHandler.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void displaySettingsWarnings(Settings settings, RomHandler romHandler) {
        Settings.TweakForROMFeedback feedback = settings.tweakForRom(romHandler);
        if (feedback.isChangedStarter() && settings.getStartersMod() == Settings.StartersMod.CUSTOM) {
            System.out.println(bundle.getString("GUI.starterUnavailable") + "%n");
        }
        if (settings.isUpdatedFromOldVersion()) {
            System.out.println(bundle.getString("GUI.settingsFileOlder") + "%n");
        }
    }

    public static int invoke(String[] args) {
        String settingsFilePath = null;
        String sourceRomFilePath = null;
        String outputRomFilePath = null;
        boolean saveAsDirectory = false;

        List<String> allowedFlags = Arrays.asList("-i", "-o", "-s", "-d");
        for (int i = 0; i < args.length; i++) {
            if (allowedFlags.contains(args[i])) {
                int flagValueIndex = i + 1;

                switch(args[i]) {
                    case "-i":
                        sourceRomFilePath = args[i + 1];
                        break;
                    case "-o":
                        outputRomFilePath = args[i + 1];
                        break;
                    case "-s":
                        settingsFilePath = args[i + 1];
                        break;
                    case "-d":
                        saveAsDirectory = true;
                        break;
                    default:
                        break;
                }
            }
        }

        if (settingsFilePath == null || sourceRomFilePath == null || outputRomFilePath == null) {
            System.err.println("Missing required argument");
            return 1;

        }

        // now we know we have the right number of args...
        if (!new File(settingsFilePath).exists()) {
            System.err.println("Error: could not read settings file");
            System.out.println(CliRandomizer.usage());
            return 1;
        }

        // check that everything is readable/writable as appropriate
        if (!new File(sourceRomFilePath).exists()) {
            System.err.println("Error: could not read source ROM file");
            System.out.println(CliRandomizer.usage());
            return 1;
        }

        // java will return false for a non-existent file, have to check the parent directory
        if (!new File(outputRomFilePath).getAbsoluteFile().getParentFile().canWrite()) {
            System.err.println("Error: destination ROM path not writable");
            System.out.println(CliRandomizer.usage());
            return 1;
        }

        boolean processResult = CliRandomizer.performDirectRandomization(
                settingsFilePath,
                sourceRomFilePath,
                outputRomFilePath,
                saveAsDirectory
        );
        if (!processResult) {
            System.err.println("Error: randomization failed");
            System.out.println(CliRandomizer.usage());
            return 1;
        }
        return 0;
    }

    private static String usage() {
        return "Usage: java -jar PokeRandoZX.jar cli -s <path to settings file> -i <path to source ROM> -o <path for new ROM> [-d save as directory]";
    }
}
