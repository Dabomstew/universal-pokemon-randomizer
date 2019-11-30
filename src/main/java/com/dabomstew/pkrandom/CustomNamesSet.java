package com.dabomstew.pkrandom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/*----------------------------------------------------------------------------*/
/*--  CustomNamesSet.java - represents customized names.                    --*/
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

public class CustomNamesSet {

    private List<String> trainerNames;
    private List<String> trainerClasses;
    private List<String> doublesTrainerNames;
    private List<String> doublesTrainerClasses;
    private List<String> pokemonNicknames;

    private static final int CUSTOM_NAMES_VERSION = 1;

    // Standard constructor: read binary data from an input stream.
    public CustomNamesSet(InputStream data) throws IOException {

        if (data.read() != CUSTOM_NAMES_VERSION) {
            throw new IOException("Invalid custom names file provided.");
        }

        trainerNames = readNamesBlock(data);
        trainerClasses = readNamesBlock(data);
        doublesTrainerNames = readNamesBlock(data);
        doublesTrainerClasses = readNamesBlock(data);
        pokemonNicknames = readNamesBlock(data);
    }

    // Alternate constructor: blank all lists
    // Used for importing old names and on the editor dialog.
    public CustomNamesSet() {
        trainerNames = new ArrayList<String>();
        trainerClasses = new ArrayList<String>();
        doublesTrainerNames = new ArrayList<String>();
        doublesTrainerClasses = new ArrayList<String>();
        pokemonNicknames = new ArrayList<String>();
    }

    private List<String> readNamesBlock(InputStream in) throws IOException {
        // Read the size of the block to come.
        byte[] szData = FileFunctions.readFullyIntoBuffer(in, 4);
        int size = FileFunctions.readFullInt(szData, 0);
        if (in.available() < size) {
            throw new IOException("Invalid size specified.");
        }

        // Read the block and translate it into a list of names.
        byte[] namesData = FileFunctions.readFullyIntoBuffer(in, size);
        List<String> names = new ArrayList<String>();
        Scanner sc = new Scanner(new ByteArrayInputStream(namesData), "UTF-8");
        while (sc.hasNextLine()) {
            String name = sc.nextLine().trim();
            if (!name.isEmpty()) {
                names.add(name);
            }
        }
        sc.close();

        return names;
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        baos.write(CUSTOM_NAMES_VERSION);

        writeNamesBlock(baos, trainerNames);
        writeNamesBlock(baos, trainerClasses);
        writeNamesBlock(baos, doublesTrainerNames);
        writeNamesBlock(baos, doublesTrainerClasses);
        writeNamesBlock(baos, pokemonNicknames);

        return baos.toByteArray();
    }

    private void writeNamesBlock(OutputStream out, List<String> names) throws IOException {
        String newln = SysConstants.LINE_SEP;
        StringBuffer outNames = new StringBuffer();
        boolean first = true;
        for (String name : names) {
            if (!first) {
                outNames.append(newln);
            }
            first = false;
            outNames.append(name);
        }
        byte[] namesData = outNames.toString().getBytes("UTF-8");
        byte[] szData = new byte[4];
        FileFunctions.writeFullInt(szData, 0, namesData.length);
        out.write(szData);
        out.write(namesData);
    }

    public List<String> getTrainerNames() {
        return Collections.unmodifiableList(trainerNames);
    }

    public List<String> getTrainerClasses() {
        return Collections.unmodifiableList(trainerClasses);
    }

    public List<String> getDoublesTrainerNames() {
        return Collections.unmodifiableList(doublesTrainerNames);
    }

    public List<String> getDoublesTrainerClasses() {
        return Collections.unmodifiableList(doublesTrainerClasses);
    }

    public List<String> getPokemonNicknames() {
        return Collections.unmodifiableList(pokemonNicknames);
    }
    
    public void setTrainerNames(List<String> names) {
        trainerNames.clear();
        trainerNames.addAll(names);
    }
    
    public void setTrainerClasses(List<String> names) {
        trainerClasses.clear();
        trainerClasses.addAll(names);
    }
    
    public void setDoublesTrainerNames(List<String> names) {
        doublesTrainerNames.clear();
        doublesTrainerNames.addAll(names);
    }
    
    public void setDoublesTrainerClasses(List<String> names) {
        doublesTrainerClasses.clear();
        doublesTrainerClasses.addAll(names);
    }
    
    public void setPokemonNicknames(List<String> names) {
        pokemonNicknames.clear();
        pokemonNicknames.addAll(names);
    }

    public static CustomNamesSet importOldNames() throws FileNotFoundException {
        CustomNamesSet cns = new CustomNamesSet();

        // Trainer Names
        if (FileFunctions.configExists(SysConstants.tnamesFile)) {
            Scanner sc = new Scanner(FileFunctions.openConfig(SysConstants.tnamesFile), "UTF-8");
            while (sc.hasNextLine()) {
                String trainername = sc.nextLine().trim();
                if (trainername.isEmpty()) {
                    continue;
                }
                if (trainername.startsWith("\uFEFF")) {
                    trainername = trainername.substring(1);
                }
                if (trainername.contains("&")) {
                    cns.doublesTrainerNames.add(trainername);
                } else {
                    cns.trainerNames.add(trainername);
                }
            }
            sc.close();
        }

        // Trainer Classes
        if (FileFunctions.configExists(SysConstants.tclassesFile)) {
            Scanner sc = new Scanner(FileFunctions.openConfig(SysConstants.tclassesFile), "UTF-8");
            while (sc.hasNextLine()) {
                String trainerClassName = sc.nextLine().trim();
                if (trainerClassName.isEmpty()) {
                    continue;
                }
                if (trainerClassName.startsWith("\uFEFF")) {
                    trainerClassName = trainerClassName.substring(1);
                }
                String checkName = trainerClassName.toLowerCase();
                int idx = (checkName.endsWith("couple") || checkName.contains(" and ") || checkName.endsWith("kin")
                        || checkName.endsWith("team") || checkName.contains("&") || (checkName.endsWith("s") && !checkName
                        .endsWith("ss"))) ? 1 : 0;
                if (idx == 1) {
                    cns.doublesTrainerClasses.add(trainerClassName);
                } else {
                    cns.trainerClasses.add(trainerClassName);
                }
            }
            sc.close();
        }

        // Nicknames
        if (FileFunctions.configExists(SysConstants.nnamesFile)) {
            Scanner sc = new Scanner(FileFunctions.openConfig(SysConstants.nnamesFile), "UTF-8");
            while (sc.hasNextLine()) {
                String nickname = sc.nextLine().trim();
                if (nickname.isEmpty()) {
                    continue;
                }
                if (nickname.startsWith("\uFEFF")) {
                    nickname = nickname.substring(1);
                }
                cns.pokemonNicknames.add(nickname);
            }
            sc.close();
        }

        return cns;
    }

}
