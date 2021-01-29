package com.dabomstew.pkrandom.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.dabomstew.pkrandom.constants.Gen5Constants;
import com.dabomstew.pkrandom.newnds.NARCArchive;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Type;
import com.dabomstew.pkrandom.romhandlers.IRomEntry;

import pptxt.PPTxtHandler;

/*----------------------------------------------------------------------------*/
/*--  Gen5TextHandler.java - rom text handler for B/W/B2/W2.                --*/
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

/**
 * Please note that certain characters need to be redefined with their Unicode
 * hexidecimal number (such as é is x00E9). If you encounter issues with
 * characters not being convertable into numbers, please use this site
 * to look up the conversion code https://unicodemap.org/
 * 
 * Successful translation requires maintaining the proper spacing characters
 * (such as \\xF000\\xBE01\\x0000\\xFFFE). It is easy to start with the
 * original text by exporting a/0/0/3 from the ROM (using a tool like
 * NitroExplorer2) and opening it up in PPTxt, then copying that into here.
 */

public class Gen5TextHandler {
    IRomEntry romEntry;
    NARCArchive storyNARC;

    public void bw1StriatonCityTextModifications(Map<String, Type> taggedGroupTypes) {
        List<String> gymLeaderSpeech = getStrings(getRomEntry().getInt("StriatonLeaderTextOffset"));
        
        // If we have types available for the groups, use them
        if (taggedGroupTypes != null) {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymTextOffset,
                    "I'm Chili! I light things up with\\xFFFE" 
                    + taggedGroupTypes.get("CHILI").camelCase()
                    + "-type Pok\\x00E9mon!\\xF000\\xBE01\\x0000");
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymTextOffset+1,
                    "I'm a " + taggedGroupTypes.get("CRESS").camelCase()
                    + "-type specialist,\\xFFFEand my name is Cress."
                    + "\\xF000\\xBE01\\x0000\\xFFFEPleased "
                    + "to make your acquaintance.\\xF000\\xBE01\\x0000");
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymTextOffset+2,
                    "And my name is Cilan.\\xFFFEI like "
                    + taggedGroupTypes.get("CILAN").camelCase()
                    +"-type Pok\\x00E9mon.\\xF000\\xBE01\\x0000");
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymText2Offset,
                    "Ta-da! The " + taggedGroupTypes.get("CHILI").camelCase() 
                    + "-type scorcher Chili--\\xFFFEthat's me--will be your "
                    + "opponent!\\xF000\\xBE01\\x0000");
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymText2Offset+1,                   
                    "That is correct!\\xF000\\xBE01\\x0000\\xFFFEIt shall "
                    + "be I and my esteemed " + taggedGroupTypes.get("CRESS").camelCase() 
                    + "\\xFFFEtypes that you must face in battle!\\xF000\\xBE01\\x0000");
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymText2Offset+2,
                    "Nothing personal... No hard feelings...\\xFFFEMe and my "
                    + taggedGroupTypes.get("CILAN").camelCase()
                    + "-type Pok\\x00E9mon will...um..."
                    + "\\xF000\\xBE01\\x0000\\xFFFEWe're gonna battle, "
                    + "come what may.\\xF000\\xBE01\\x0000");
            }
        } 
        // Types are not available - Make any type references generic
        else {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymTextOffset,
                    "I'm Chili! I light things up with\\xFFFE" 
                    + "hot Pok\\x00E9mon!\\xF000\\xBE01\\x0000");
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymTextOffset+1,
                    "I'm a general specialist,\\xFFFEand my name is Cress."
                    + "\\xF000\\xBE01\\x0000\\xFFFEPleased "
                    + "to make your acquaintance.\\xF000\\xBE01\\x0000");
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymTextOffset+2,
                    "And my name is Cilan.\\xFFFEI like "
                    +"any Pok\\x00E9mon.\\xF000\\xBE01\\x0000");
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymText2Offset,
                    "Ta-da! The jalape\\x00F1o scorcher "
                    + "Chili--\\xFFFEthat's me--will be your "
                    + "opponent!\\xF000\\xBE01\\x0000");
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymText2Offset+1,                   
                    "That is correct!\\xF000\\xBE01\\x0000\\xFFFEIt shall "
                    + "be I and my esteemed team\\xFFFEof Pok\\x00E9mon "
                    + "that you must face in battle!\\xF000\\xBE01\\x0000");
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymText2Offset+2,
                    "Nothing personal... No hard feelings...\\xFFFEMe and my "
                    + "favorite Pok\\x00E9mon will...um..."
                    + "\\xF000\\xBE01\\x0000\\xFFFEWe're gonna battle, "
                    + "come what may.\\xF000\\xBE01\\x0000");
            }
        }
        setStrings(getRomEntry().getInt("StriatonLeaderTextOffset"), gymLeaderSpeech);        
    }

    public void bw1NacreneCityTextModifications(Map<String, Type> taggedGroupTypes, Random random) {
        List<String> gymLeaderSpeech = getStrings(getRomEntry().getInt("NacreneLeaderTextOffset"));
        
        // If we have types available for the groups, use them
        if (taggedGroupTypes != null) {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymLeaderSpeech.set(Gen5Constants.bw1NacreneCityGymTextOffset,
                    "All Trainers in this Gym use\\xFFFE"
                    + taggedGroupTypes.get("GYM2").camelCase() 
                    + "-type Pok\\x00E9mon.\\xF000\\xBE01\\x0000\\xFFFEThis "
                    + "is just between you and me...\\xF000\\xBE01\\x0000\\xFFFE"
                    + taggedGroupTypes.get("GYM2").camelCase() 
                    + "-type Pok\\x00E9mon are weak against\\xFFFE"
                    + Type.randomWeakness(random, false, taggedGroupTypes.get("GYM2")).camelCase()
                    + "-type Pok\\x00E9mon.\\xF000\\xBE01\\x0000\\xFFFEUse "
                    + "your Pok\\x00E9dex to find where that type is."
                    + "\\xF000\\xBE01\\x0000\\xFFFENow, I will explain "
                    + "about the Gym itself!\\xF000\\xBE01\\x0000\\xFFFE"
                    + "In this Pok\\x00E9mon Gym, if you answer\\xFFFEquestions "
                    + "hidden in books,\\xF000\\xBE01\\x0000\\xFFFEyou "
                    + "can move forward.\\xF000\\xBE01\\x0000\\xFFFE"
                    + "For your information, the first book is\\xFFFE"
                    + "\\x0022Nice to Meet You, Pok\\x00E9mon.\\x0022"
                    + "\\xF000\\xBE01\\x0000\\xFFFE"
                    + "If you don't know where the book is,\\xFFFEplease "
                    + "ask anyone!\\xF000\\xBE01\\x0000");
                gymLeaderSpeech.set(Gen5Constants.bw1NacreneCityGymTextOffset + 1,
                    "All Trainers in this Gym use\\xFFFE"
                    + taggedGroupTypes.get("GYM2").camelCase() + "-type Pok\\x00E9mon."
                    + "\\xF000\\xBE01\\x0000\\xFFFEThis is just between you "
                    + "and me...\\xF000\\xBE01\\x0000\\xFFFE"
                    + taggedGroupTypes.get("GYM2").camelCase() + "-type Pok\\x00E9mon "
                    + "are weak against\\xFFFE"
                    + Type.randomWeakness(random, false, taggedGroupTypes.get("GYM2")).camelCase()
                    + "-type Pok\\x00E9mon.\\xF000\\xBE01\\x0000\\xFFFEUse "
                    + "your Pok\\x00E9dex to find where that type is."
                    + "\\xF000\\xBE01\\x0000\\xFFFENow, I will explain about the "
                    + "Gym itself!\\xF000\\xBE01\\x0000\\xFFFEIn this Pok\\x00E9mon Gym, "
                    + "if you answer\\xFFFEquestions hidden in books,"
                    + "\\xF000\\xBE01\\x0000\\xFFFEyou can move forward."
                    + "\\xF000\\xBE01\\x0000\\xFFFEFor your information, the first "
                    + "book is\\xFFFE\\x0022Nice to Meet You, Pok\\x00E9mon.\\x0022"
                    + "\\xF000\\xBE01\\x0000\\xFFFEIf you don't know where the book is,"
                    + "\\xFFFEplease ask anyone!");
            }
        }
        // Types are not available - Make any type references generic
        else {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymLeaderSpeech.set(Gen5Constants.bw1NacreneCityGymTextOffset,
                    "All Trainers in this Gym use\\xFFFE"
                    + "awesome Pok\\x00E9mon.\\xF000\\xBE01\\x0000\\xFFFEThis "
                    + "is just between you and me...\\xF000\\xBE01\\x0000\\xFFFE"
                    + "you have awesome Pok\\x00E9mmon too!"
                    + "\\xF000\\xBE01\\x0000\\xFFFENow, I will explain "
                    + "about the Gym itself!\\xF000\\xBE01\\x0000\\xFFFE"
                    + "In this Pok\\x00E9mon Gym, if you answer\\xFFFEquestions "
                    + "hidden in books,\\xF000\\xBE01\\x0000\\xFFFEyou "
                    + "can move forward.\\xF000\\xBE01\\x0000\\xFFFE"
                    + "For your information, the first book is\\xFFFE"
                    + "\\x0022Nice to Meet You, Pok\\x00E9mon.\\x0022"
                    + "\\xF000\\xBE01\\x0000\\xFFFE"
                    + "If you don't know where the book is,\\xFFFEplease "
                    + "ask anyone!\\xF000\\xBE01\\x0000");
                gymLeaderSpeech.set(Gen5Constants.bw1NacreneCityGymTextOffset + 1,
                    "All Trainers in this Gym use\\xFFFE"
                    + "awesome Pok\\x00E9mon."
                    + "\\xF000\\xBE01\\x0000\\xFFFEThis is just between you "
                    + "and me...\\xF000\\xBE01\\x0000\\xFFFE"
                    + "you have awesome Pok\\x00E9mmon too!"
                    + "\\xF000\\xBE01\\x0000\\xFFFENow, I will explain about the "
                    + "Gym itself!\\xF000\\xBE01\\x0000\\xFFFEIn this Pok\\x00E9mon Gym, "
                    + "if you answer\\xFFFEquestions hidden in books,"
                    + "\\xF000\\xBE01\\x0000\\xFFFEyou can move forward."
                    + "\\xF000\\xBE01\\x0000\\xFFFEFor your information, the first "
                    + "book is\\xFFFE\\x0022Nice to Meet You, Pok\\x00E9mon.\\x0022"
                    + "\\xF000\\xBE01\\x0000\\xFFFEIf you don't know where the book is,"
                    + "\\xFFFEplease ask anyone!");
            }
        }

        setStrings(getRomEntry().getInt("NacreneLeaderTextOffset"), gymLeaderSpeech);    
    }

    public void bw1StarterTextModifications(List<Pokemon> newStarters) {
        List<String> yourHouseStrings = getStrings(getRomEntry().getInt("StarterLocationTextOffset"));
        // Update the text for the English games
        if (getRomEntry().getRomCode().charAt(3) == 'O') {
            for (int i = 0; i < 3; i++) {
                yourHouseStrings.set(Gen5Constants.bw1StarterTextOffset - i,
                        "\\xF000\\xBD02\\x0000The " + newStarters.get(i).primaryType.camelCase()
                                + "-type Pok\\x00E9mon\\xFFFE\\xF000\\xBD02\\x0000" + newStarters.get(i).name);
            }
            // Update what the friends say
            yourHouseStrings
                    .set(Gen5Constants.bw1CherenText1Offset,
                            "Cheren: Hey, how come you get to pick\\xFFFEout my Pok\\x00E9mon?"
                                    + "\\xF000\\xBE01\\x0000\\xFFFEOh, never mind. I wanted this one"
                                    + "\\xFFFEfrom the start, anyway."
                                    + "\\xF000\\xBE01\\x0000");
            yourHouseStrings.set(Gen5Constants.bw1CherenText2Offset,
                    "It's decided. You'll be my opponent...\\xFFFEin our first Pok\\x00E9mon battle!"
                            + "\\xF000\\xBE01\\x0000\\xFFFELet's see what you can do, \\xFFFEmy Pok\\x00E9mon!"
                            + "\\xF000\\xBE01\\x0000");
        }

        // rewrite
        setStrings(getRomEntry().getInt("StarterLocationTextOffset"), yourHouseStrings);

    } 

    public void bw2StarterTextModifications(List<Pokemon> newStarters) {
        List<String> starterTownStrings = getStrings(getRomEntry().getInt("StarterLocationTextOffset"));
        // Update the text for the English games
        if (getRomEntry().getRomCode().charAt(3) == 'O') {
            for (int i = 0; i < 3; i++) {
                starterTownStrings.set(Gen5Constants.bw2StarterTextOffset - i, "\\xF000\\xBD02\\x0000The "
                        + newStarters.get(i).primaryType.camelCase()
                        + "-type Pok\\x00E9mon\\xFFFE\\xF000\\xBD02\\x0000" + newStarters.get(i).name);
            }
            // Update what the rival says
            starterTownStrings.set(Gen5Constants.bw2RivalTextOffset,
                    "\\xF000\\x0100\\x0001\\x0001: Let's see how good\\xFFFEa Trainer you are!"
                            + "\\xF000\\xBE01\\x0000\\xFFFEI'll use my Pok\\x00E9mon"
                            + "\\xFFFEthat I raised from an Egg!\\xF000\\xBE01\\x0000");
        }
        
        // rewrite
        setStrings(getRomEntry().getInt("StarterLocationTextOffset"), starterTownStrings);
    }

    private List<String> getStrings(int index) {
        byte[] rawFile = getStoryNARC().files.get(index);
        return new ArrayList<String>(PPTxtHandler.readTexts(rawFile));
    }

    private void setStrings(int index, List<String> strings) {
        byte[] oldRawFile = getStoryNARC().files.get(index);
        byte[] newRawFile = PPTxtHandler.saveEntry(oldRawFile, strings);
        getStoryNARC().files.set(index, newRawFile);
    }

    public IRomEntry getRomEntry() {
    	return this.romEntry;
    }

    public void setRomEntry(IRomEntry romEntry) {
    	this.romEntry = romEntry;
    }

    public NARCArchive getStoryNARC() {
    	return this.storyNARC;
    }

    public void setStoryNARC(NARCArchive storyNARC) {
    	this.storyNARC = storyNARC;
    }
}
