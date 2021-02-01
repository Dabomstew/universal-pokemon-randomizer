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

    public void bw1CasteliaCityPraiseTextModifications(Map<String, Type> taggedGroupTypes) {
        List<String> gymPraiseSpeech = getStrings(getRomEntry().getInt("CasteliaPraiseTextOffset"));
        
        // If we have types available for the groups, use them
        if (taggedGroupTypes != null) {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymPraiseSpeech.set(Gen5Constants.bw1CasteliaCityPraiseTextOffset,
                    "The Striaton City Gym Leaders are a\\xFFFEgreat triplet "
                    + "combo!\\xF000\\xBE01\\x0000\\xFFFEThe "
                    + taggedGroupTypes.get("CILAN").camelCase()
                    + "-type Pok\\x00E9mon user, Cilan,\\xFFFEchooses great tea "
                    + "leaves.\\xF000\\xBE01\\x0000\\xFFFEThe "
                    + taggedGroupTypes.get("CRESS").camelCase()
                    + "-type Pok\\x00E9mon user, Cress,\\xFFFEprepares the best "
                    + "water.\\xF000\\xBE01\\x0000\\xFFFEAnd the "
                    + taggedGroupTypes.get("CHILI").camelCase()
                    + "-type Pok\\x00E9mon user, Chili,\\xFFFEpours hot water at "
                    + "the right heat.\\xF000\\xBE01\\x0000\\xFFFENo "
                    + "wonder their tea is the best!");
            }
        } 
        // Types are not available - Make any type references generic
        else {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymPraiseSpeech.set(Gen5Constants.bw1CasteliaCityPraiseTextOffset,
                    "The Striaton City Gym Leaders are a\\xFFFEgreat triplet "
                    + "combo!\\xF000\\xBE01\\x0000\\xFFFEThe herbalist, "
                    + "Cilan,\\xFFFEchooses great tea leaves."
                    + "\\xF000\\xBE01\\x0000\\xFFFEThe general specialist, "
                    + "Cress,\\xFFFEprepares the best water."
                    + "\\xF000\\xBE01\\x0000\\xFFFEAnd the jalape\\x00F1o "
                    + "scorcher, Chili,\\xFFFEpours hot water at "
                    + "the right heat.\\xF000\\xBE01\\x0000\\xFFFENo "
                    + "wonder their tea is the best!");
            }
        }
        setStrings(getRomEntry().getInt("CasteliaPraiseTextOffset"), gymPraiseSpeech);        
    }

    public void bw1CasteliaCityBurghTextModifications(Map<String, Type> taggedGroupTypes) {
        List<String> gymLeaderSpeech = getStrings(getRomEntry().getInt("CasteliaBurghTextOffset"));
        
        // If we have types available for the groups, use them
        if (taggedGroupTypes != null) {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymLeaderSpeech.set(Gen5Constants.bw1CasteliaCityBurghTextOffset,
                    "Thanks again for your help.\\xF000\\xBE01\\x0000\\xFFFEMy"
                    + taggedGroupTypes.get("GYM3").camelCase() + " Pok\\x00E9mon "
                    + "are scurrying with\\xFFFEexcitement about getting to "
                    + "battle you.\\xF000\\xBE01\\x0000\\xFFFELet's get "
                    + "straight to it!\\xF000\\xBE01\\x0000");
                gymLeaderSpeech.set(Gen5Constants.bw1CasteliaCityBurghText2Offset,
                    "How many discoveries have you made\\xFFFEsince you "
                    + "started your adventure?\\xF000\\xBE01\\x0000\\xFFFE"
                    + "When I was a kid, my innocent heart was\\xFFFEcaptured "
                    + "by the beauty of " + taggedGroupTypes.get("GYM3").camelCase() 
                    + " Pok\\x00E9mon.\\xF000\\xBE01\\x0000\\xFFFEI drew with "
                    + "them and battled with them,\\xFFFEand after all this time, "
                    + "I continue\\xF000\\xBE01\\x0000\\xFFFEto discover new "
                    + "things.\\xF000\\xBE01\\x0000\\xFFFEA world shared with "
                    + "Pok\\x00E9mon is a world\\xFFFEswarming with mysteries.");
                gymLeaderSpeech.set(Gen5Constants.bw1CasteliaCityBurghText3Offset,
                    "If you hadn't stepped on that switch,\\xFFFEI'd still be "
                    + "stuck hidden away.\\xF000\\xBE01\\x0000\\xFFFEThat's "
                    + "right! " + taggedGroupTypes.get("GYM3").camelCase() 
                    + " Gym Trainers like to\\xFFFEbe bugged!");
            }
        }
        // Types are not available - Make any type references generic
        else {
             // Update the text for the English games
             if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymLeaderSpeech.set(Gen5Constants.bw1CasteliaCityBurghTextOffset,
                    "Thanks again for your help.\\xF000\\xBE01\\x0000\\xFFFEMy "
                    + "Pok\\x00E9mon are scurrying with\\xFFFEexcitement about "
                    + "getting to battle you.\\xF000\\xBE01\\x0000\\xFFFELet's "
                    + "get straight to it!\\xF000\\xBE01\\x0000");
                gymLeaderSpeech.set(Gen5Constants.bw1CasteliaCityBurghText2Offset,
                    "How many discoveries have you made\\xFFFEsince you "
                    + "started your adventure?\\xF000\\xBE01\\x0000\\xFFFE"
                    + "When I was a kid, my innocent heart was\\xFFFEcaptured "
                    + "by the beauty of Pok\\x00E9mon.\\xF000\\xBE01\\x0000\\xFFFE"
                    + "I drew with them and battled with them,\\xFFFEand after "
                    + "all this time, I continue\\xF000\\xBE01\\x0000\\xFFFEto "
                    + "discover new things.\\xF000\\xBE01\\x0000\\xFFFEA "
                    + "world shared with Pok\\x00E9mon is a world\\xFFFEswarming "
                    + "with mysteries.");
                gymLeaderSpeech.set(Gen5Constants.bw1CasteliaCityBurghText3Offset,
                    "If you hadn't stepped on that switch,\\xFFFEI'd still be "
                    + "stuck hidden away.\\xF000\\xBE01\\x0000\\xFFFEThat's "
                    + "right! Gym Trainers like to\\xFFFEbe bugged!");
            }
        }
        setStrings(getRomEntry().getInt("CasteliaBurghTextOffset"), gymLeaderSpeech);        
    }

    public void bw1CherenBurghTextModifications(Map<String, Type> taggedGroupTypes) {
        List<String> gymLeaderSpeech = getStrings(getRomEntry().getInt("CherenBurghTextOffset"));
        
        // If we have types available for the groups, use them
        if (taggedGroupTypes != null) {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymLeaderSpeech.set(Gen5Constants.bw1CherenBurghTextOffset,
                    "He's a seasoned Gym Leader.\\xFFFEHe made me work for "
                    + "that Gym Badge!\\xF000\\xBE01\\x0000\\xFFFEBut for me, "
                    + taggedGroupTypes.get("GYM3").camelCase()  
                    + "-type Pok\\x00E9mon\\xFFFEaren't much of a challenge."
                    + "\\xF000\\xBE01\\x0000");
            }
        }
        else {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymLeaderSpeech.set(Gen5Constants.bw1CherenBurghTextOffset,
                    "He's a seasoned Gym Leader.\\xFFFEHe made me work for "
                    + "that Gym Badge!\\xF000\\xBE01\\x0000\\xFFFEBut for me, "
                    + "his Pok\\x00E9mon\\xFFFEaren't much of a challenge."
                    + "\\xF000\\xBE01\\x0000");
            }
        }
        setStrings(getRomEntry().getInt("CherenBurghTextOffset"), gymLeaderSpeech);  
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
    
    public void bw1CasteliaCityItemTextModifications(List<Pokemon> newStarters) {
        List<String> itemGuyStrings = getStrings(getRomEntry().getInt("CasteliaItemTextOffset"));
        
        // Update the text for the English games
        if (getRomEntry().getRomCode().charAt(3) == 'O') {
            itemGuyStrings.set(Gen5Constants.bw1CasteliaCityItemTextOffset, 
                "You have " + newStarters.get(0).getName() + "! "
                + "Then I will give you "
                + "this!\\xF000\\xBE01\\x0000\\xFFFEWhen you let your "
                + "Pok\\x00E9mon hold it, it can\\xFFFEraise the power of " 
                + "Grass-type moves!\\xF000\\xBE01\\x0000");
            itemGuyStrings.set(Gen5Constants.bw1CasteliaCityItemTextOffset + 1, 
                "You have " + newStarters.get(1).getName() + "! Then I "
                + "will give\\xFFFEyou "
                + "this!\\xF000\\xBE01\\x0000\\xFFFEWhen you let your "
                + "Pok\\x00E9mon hold it, it can\\xFFFEraise the power of "
                + "Water-type moves!\\xF000\\xBE01\\x0000");
            itemGuyStrings.set(Gen5Constants.bw1CasteliaCityItemTextOffset + 2, 
                "You have " + newStarters.get(2).getName() + "! "
                + "Then I will give you "
                + "this!\\xF000\\xBE01\\x0000\\xFFFEWhen you let your "
                + "Pok\\x00E9mon hold it, it can\\xFFFEraise the power of "
                + "Fire-type moves!\\xF000\\xBE01\\x0000");
            itemGuyStrings.set(Gen5Constants.bw1CasteliaCityItemText2Offset, 
                newStarters.get(0).getName());
            itemGuyStrings.set(Gen5Constants.bw1CasteliaCityItemText2Offset + 1, 
                newStarters.get(1).getName());
            itemGuyStrings.set(Gen5Constants.bw1CasteliaCityItemText2Offset + 2, 
                newStarters.get(2).getName());
            itemGuyStrings.set(Gen5Constants.bw1CasteliaCityItemText3Offset, 
                "Do you have " + newStarters.get(0).getName() + "?");
            itemGuyStrings.set(Gen5Constants.bw1CasteliaCityItemText3Offset + 1, 
                "Do you have " + newStarters.get(1).getName() + "?");
            itemGuyStrings.set(Gen5Constants.bw1CasteliaCityItemText3Offset + 2, 
                "Do you have " + newStarters.get(2).getName() + "?");
        }

        setStrings(getRomEntry().getInt("CasteliaItemTextOffset"), itemGuyStrings); 
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
