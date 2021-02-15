package com.dabomstew.pkrandom.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

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
 * (such as \xf000븀\x0000\xfffe). It is easy to start with the
 * original text by exporting a/0/0/3 from the ROM (using a tool like
 * NitroExplorer2) and opening it up in PPTxt, then copying that into here.
 */

public class Gen5TextHandler {

    // Fast replacement for \xf000븁\x0000\xfffe
    private static final String MAJOR_LINE_BREAK = "\\xF000\\xBE01\\x0000\\xFFFE";
    private static final String MINOR_LINE_BREAK = "\\xF000\\xBE01\\x0000";

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
                    + "-type Pok\\x00E9mon!" + MINOR_LINE_BREAK);
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymTextOffset+1,
                    "I'm a " + taggedGroupTypes.get("CRESS").camelCase()
                    + "-type specialist,\\xFFFEand my name is Cress."
                    + MAJOR_LINE_BREAK + "Pleased "
                    + "to make your acquaintance." + MINOR_LINE_BREAK);
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymTextOffset+2,
                    "And my name is Cilan.\\xFFFEI like "
                    + taggedGroupTypes.get("CILAN").camelCase()
                    +"-type Pok\\x00E9mon." + MINOR_LINE_BREAK);
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymText2Offset,
                    "Ta-da! The " + taggedGroupTypes.get("CHILI").camelCase() 
                    + "-type scorcher Chili--\\xFFFEthat's me--will be your "
                    + "opponent!" + MINOR_LINE_BREAK);
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymText2Offset+1,                   
                    "That is correct!" + MAJOR_LINE_BREAK + "It shall "
                    + "be I and my esteemed " + taggedGroupTypes.get("CRESS").camelCase() 
                    + "\\xFFFEtypes that you must face in battle!" + MINOR_LINE_BREAK);
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymText2Offset+2,
                    "Nothing personal... No hard feelings...\\xFFFEMe and my "
                    + taggedGroupTypes.get("CILAN").camelCase()
                    + "-type Pok\\x00E9mon will...um..."
                    + MAJOR_LINE_BREAK + "We're gonna battle, "
                    + "come what may." + MINOR_LINE_BREAK);
            }
        } 
        // Types are not available - Make any type references generic
        else {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymTextOffset,
                    "I'm Chili! I light things up with\\xFFFE" 
                    + "hot Pok\\x00E9mon!" + MINOR_LINE_BREAK);
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymTextOffset+1,
                    "I'm a general specialist,\\xFFFEand my name is Cress."
                    + MAJOR_LINE_BREAK + "Pleased "
                    + "to make your acquaintance." + MINOR_LINE_BREAK);
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymTextOffset+2,
                    "And my name is Cilan.\\xFFFEI like "
                    +"any Pok\\x00E9mon." + MINOR_LINE_BREAK);
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymText2Offset,
                    "Ta-da! The jalape\\x00F1o scorcher "
                    + "Chili--\\xFFFEthat's me--will be your "
                    + "opponent!" + MINOR_LINE_BREAK);
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymText2Offset+1,                   
                    "That is correct!" + MAJOR_LINE_BREAK + "It shall "
                    + "be I and my esteemed team\\xFFFEof Pok\\x00E9mon "
                    + "that you must face in battle!" + MINOR_LINE_BREAK);
                gymLeaderSpeech.set(Gen5Constants.bw1StriatonCityGymText2Offset+2,
                    "Nothing personal... No hard feelings...\\xFFFEMe and my "
                    + "favorite Pok\\x00E9mon will...um..."
                    + MAJOR_LINE_BREAK + "We're gonna battle, "
                    + "come what may." + MINOR_LINE_BREAK);
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
                    + "-type Pok\\x00E9mon." + MAJOR_LINE_BREAK + "This "
                    + "is just between you and me..." + MAJOR_LINE_BREAK
                    + taggedGroupTypes.get("GYM2").camelCase() 
                    + "-type Pok\\x00E9mon are weak against\\xFFFE"
                    + Type.randomWeakness(random, false, taggedGroupTypes.get("GYM2")).camelCase()
                    + "-type Pok\\x00E9mon." + MAJOR_LINE_BREAK + "Use "
                    + "your Pok\\x00E9dex to find where that type is."
                    + MAJOR_LINE_BREAK + "Now, I will explain "
                    + "about the Gym itself!" + MAJOR_LINE_BREAK
                    + "In this Pok\\x00E9mon Gym, if you answer\\xFFFEquestions "
                    + "hidden in books," + MAJOR_LINE_BREAK
                    + "you can move forward." + MAJOR_LINE_BREAK
                    + "For your information, the first book is\\xFFFE"
                    + "\\x0022Nice to Meet You, Pok\\x00E9mon.\\x0022"
                    + MAJOR_LINE_BREAK
                    + "If you don't know where the book is,\\xFFFEplease "
                    + "ask anyone!" + MINOR_LINE_BREAK);
                gymLeaderSpeech.set(Gen5Constants.bw1NacreneCityGymTextOffset + 1,
                    "All Trainers in this Gym use\\xFFFE"
                    + taggedGroupTypes.get("GYM2").camelCase() + "-type Pok\\x00E9mon."
                    + MAJOR_LINE_BREAK + "This is just between you "
                    + "and me..." + MAJOR_LINE_BREAK
                    + taggedGroupTypes.get("GYM2").camelCase() + "-type Pok\\x00E9mon "
                    + "are weak against\\xFFFE"
                    + Type.randomWeakness(random, false, taggedGroupTypes.get("GYM2")).camelCase()
                    + "-type Pok\\x00E9mon." + MAJOR_LINE_BREAK
                    + "Use your Pok\\x00E9dex to find where that type is."
                    + MAJOR_LINE_BREAK + "Now, I will explain about the "
                    + "Gym itself!" + MAJOR_LINE_BREAK + "In this Pok\\x00E9mon Gym, "
                    + "if you answer\\xFFFEquestions hidden in books,"
                    + MAJOR_LINE_BREAK + "you can move forward."
                    + MAJOR_LINE_BREAK + "For your information, the first "
                    + "book is\\xFFFE\\x0022Nice to Meet You, Pok\\x00E9mon.\\x0022"
                    + MAJOR_LINE_BREAK + "If you don't know where the book is,"
                    + "\\xFFFEplease ask anyone!");
            }
        }
        // Types are not available - Make any type references generic
        else {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymLeaderSpeech.set(Gen5Constants.bw1NacreneCityGymTextOffset,
                    "All Trainers in this Gym use\\xFFFE"
                    + "awesome Pok\\x00E9mon." + MAJOR_LINE_BREAK
                    + "This is just between you and me..." + MAJOR_LINE_BREAK
                    + "you have awesome Pok\\x00E9mmon too!"
                    + MAJOR_LINE_BREAK + "Now, I will explain "
                    + "about the Gym itself!" + MAJOR_LINE_BREAK
                    + "In this Pok\\x00E9mon Gym, if you answer\\xFFFEquestions "
                    + "hidden in books," + MAJOR_LINE_BREAK
                    + "you can move forward." + MAJOR_LINE_BREAK
                    + "For your information, the first book is\\xFFFE"
                    + "\\x0022Nice to Meet You, Pok\\x00E9mon.\\x0022"
                    + MAJOR_LINE_BREAK
                    + "If you don't know where the book is,\\xFFFEplease "
                    + "ask anyone!" + MINOR_LINE_BREAK);
                gymLeaderSpeech.set(Gen5Constants.bw1NacreneCityGymTextOffset + 1,
                    "All Trainers in this Gym use\\xFFFE"
                    + "awesome Pok\\x00E9mon."
                    + MAJOR_LINE_BREAK + "This is just between you "
                    + "and me..." + MAJOR_LINE_BREAK
                    + "you have awesome Pok\\x00E9mmon too!"
                    + MAJOR_LINE_BREAK + "Now, I will explain about the "
                    + "Gym itself!" + MAJOR_LINE_BREAK + "In this Pok\\x00E9mon Gym, "
                    + "if you answer\\xFFFEquestions hidden in books,"
                    + MAJOR_LINE_BREAK + "you can move forward."
                    + MAJOR_LINE_BREAK + "For your information, the first "
                    + "book is\\xFFFE\\x0022Nice to Meet You, Pok\\x00E9mon.\\x0022"
                    + MAJOR_LINE_BREAK + "If you don't know where the book is,"
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
                    + "combo!" + MAJOR_LINE_BREAK + "The "
                    + taggedGroupTypes.get("CILAN").camelCase()
                    + "-type Pok\\x00E9mon user, Cilan,\\xFFFEchooses great tea "
                    + "leaves." + MAJOR_LINE_BREAK + "The "
                    + taggedGroupTypes.get("CRESS").camelCase()
                    + "-type Pok\\x00E9mon user, Cress,\\xFFFEprepares the best "
                    + "water." + MAJOR_LINE_BREAK + "And the "
                    + taggedGroupTypes.get("CHILI").camelCase()
                    + "-type Pok\\x00E9mon user, Chili,\\xFFFEpours hot water at "
                    + "the right heat." + MAJOR_LINE_BREAK + "No "
                    + "wonder their tea is the best!");
            }
        } 
        // Types are not available - Make any type references generic
        else {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymPraiseSpeech.set(Gen5Constants.bw1CasteliaCityPraiseTextOffset,
                    "The Striaton City Gym Leaders are a\\xFFFEgreat triplet "
                    + "combo!" + MAJOR_LINE_BREAK + "The herbalist, "
                    + "Cilan,\\xFFFEchooses great tea leaves."
                    + MAJOR_LINE_BREAK + "The general specialist, "
                    + "Cress,\\xFFFEprepares the best water."
                    + MAJOR_LINE_BREAK + "And the jalape\\x00F1o "
                    + "scorcher, Chili,\\xFFFEpours hot water at "
                    + "the right heat." + MAJOR_LINE_BREAK + "No "
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
                    "Thanks again for your help." + MAJOR_LINE_BREAK + "My "
                    + taggedGroupTypes.get("GYM3").camelCase() + " Pok\\x00E9mon "
                    + "are scurrying with\\xFFFEexcitement about getting to "
                    + "battle you." + MAJOR_LINE_BREAK + "Let's get "
                    + "straight to it!" + MINOR_LINE_BREAK);
                gymLeaderSpeech.set(Gen5Constants.bw1CasteliaCityBurghText2Offset,
                    "How many discoveries have you made\\xFFFEsince you "
                    + "started your adventure?" + MAJOR_LINE_BREAK
                    + "When I was a kid, my innocent heart was\\xFFFEcaptured "
                    + "by the beauty of " + taggedGroupTypes.get("GYM3").camelCase() 
                    + " Pok\\x00E9mon." + MAJOR_LINE_BREAK + "I drew with "
                    + "them and battled with them,\\xFFFEand after all this time, "
                    + "I continue" + MAJOR_LINE_BREAK + "to discover new "
                    + "things." + MAJOR_LINE_BREAK + "A world shared with "
                    + "Pok\\x00E9mon is a world\\xFFFEswarming with mysteries.");
                gymLeaderSpeech.set(Gen5Constants.bw1CasteliaCityBurghText3Offset,
                    "If you hadn't stepped on that switch,\\xFFFEI'd still be "
                    + "stuck hidden away." + MAJOR_LINE_BREAK + "That's "
                    + "right! " + taggedGroupTypes.get("GYM3").camelCase() 
                    + " Gym Trainers like to\\xFFFEbe bugged!");
            }
        }
        // Types are not available - Make any type references generic
        else {
             // Update the text for the English games
             if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymLeaderSpeech.set(Gen5Constants.bw1CasteliaCityBurghTextOffset,
                    "Thanks again for your help." + MAJOR_LINE_BREAK + "My "
                    + "Pok\\x00E9mon are scurrying with\\xFFFEexcitement about "
                    + "getting to battle you." + MAJOR_LINE_BREAK + "Let's "
                    + "get straight to it!" + MINOR_LINE_BREAK);
                gymLeaderSpeech.set(Gen5Constants.bw1CasteliaCityBurghText2Offset,
                    "How many discoveries have you made\\xFFFEsince you "
                    + "started your adventure?" + MAJOR_LINE_BREAK
                    + "When I was a kid, my innocent heart was\\xFFFEcaptured "
                    + "by the beauty of Pok\\x00E9mon." + MAJOR_LINE_BREAK
                    + "I drew with them and battled with them,\\xFFFEand after "
                    + "all this time, I continue" + MAJOR_LINE_BREAK
                    + "to discover new things." + MAJOR_LINE_BREAK
                    + "A world shared with Pok\\x00E9mon is a world\\xFFFE"
                    + "swarming with mysteries.");
                gymLeaderSpeech.set(Gen5Constants.bw1CasteliaCityBurghText3Offset,
                    "If you hadn't stepped on that switch,\\xFFFEI'd still be "
                    + "stuck hidden away." + MAJOR_LINE_BREAK + "That's "
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
                    + "that Gym Badge!" + MAJOR_LINE_BREAK + "But for me, "
                    + taggedGroupTypes.get("GYM3").camelCase()  
                    + "-type Pok\\x00E9mon\\xFFFEaren't much of a challenge."
                    + MINOR_LINE_BREAK);
            }
        }
        // Types are not available - Make any type references generic
        else {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymLeaderSpeech.set(Gen5Constants.bw1CherenBurghTextOffset,
                    "He's a seasoned Gym Leader.\\xFFFEHe made me work for "
                    + "that Gym Badge!" + MAJOR_LINE_BREAK + "But for me, "
                    + "his Pok\\x00E9mon\\xFFFEaren't much of a challenge."
                    + MINOR_LINE_BREAK);
            }
        }
        setStrings(getRomEntry().getInt("CherenBurghTextOffset"), gymLeaderSpeech);  
    }

    public void bw1NimbasaCityTextModifications(Map<String, Type> taggedGroupTypes, Random random) {
        List<String> gymLeaderSpeech = getStrings(getRomEntry().getInt("NimbasaLeaderTextOffset"));
        
        // If we have types available for the groups, use them
        if (taggedGroupTypes != null) {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymLeaderSpeech.set(Gen5Constants.bw1NimbasaCityTextOffset, 
                    "In this roller coaster Gym, the first\\xFFFEstep "
                    + "is to get in the car." + MAJOR_LINE_BREAK
                    + "Next comes the platform!\\xFFFEThere, you can "
                    + "change" + MAJOR_LINE_BREAK + "where the "
                    + "coaster is going!" + MAJOR_LINE_BREAK
                    + "Sometimes you continue by riding\\xFFFEthe cars "
                    + "of opponents you defeat." + MAJOR_LINE_BREAK
                    + "That's how you aim for the Gym Leader!"
                    + MAJOR_LINE_BREAK + "By the way, "
                    + taggedGroupTypes.get("GYM4").camelCase() 
                    + "-type Pok\\x00E9mon don't\\xFFFEdo well against "
                    + Type.randomWeakness(random, false, taggedGroupTypes.get("GYM4")).camelCase()
                    + "-type moves...");
                gymLeaderSpeech.set(Gen5Constants.bw1NimbasaCityTextOffset + 1,
                    "Elesa uses dazzling\\xFFFE"
                    + taggedGroupTypes.get("GYM4").camelCase() 
                    + "-type attacks!" + MAJOR_LINE_BREAK + "But "
                    + "the combination of you and your\\xFFFEPok\\x00E9mon "
                    + "was even more impressive!" + MAJOR_LINE_BREAK
                    + "It was... It was...\\xFFFEan emotional roller coaster!");
            }
        }
        // Types are not available - Make any type references generic
        else {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymLeaderSpeech.set(Gen5Constants.bw1NimbasaCityTextOffset, 
                    "In this roller coaster Gym, the first\\xFFFEstep "
                    + "is to get in the car." + MAJOR_LINE_BREAK
                    + "Next comes the platform!\\xFFFEThere, you can "
                    + "change" + MAJOR_LINE_BREAK + "where the "
                    + "coaster is going!" + MAJOR_LINE_BREAK
                    + "Sometimes you continue by riding\\xFFFEthe cars "
                    + "of opponents you defeat." + MAJOR_LINE_BREAK
                    + "That's how you aim for the Gym Leader!"
                    + MAJOR_LINE_BREAK + "By the way, "
                    + "she got moves...");
                gymLeaderSpeech.set(Gen5Constants.bw1NimbasaCityTextOffset + 1,
                    "Elesa uses dazzling\\xFFFE"
                    + "attacks!" + MAJOR_LINE_BREAK + "But "
                    + "the combination of you and your\\xFFFEPok\\x00E9mon "
                    + "was even more impressive!" + MAJOR_LINE_BREAK
                    + "It was... It was...\\xFFFEan emotional roller coaster!");
            }
        }
        setStrings(getRomEntry().getInt("NimbasaLeaderTextOffset"), gymLeaderSpeech);  
    }

    public void bw1JuniperTextModifications(Map<String, Type> taggedGroupTypes) {
        List<String> professorSpeech = getStrings(getRomEntry().getInt("JuniperElesaTextOffset"));
        
        // If we have types available for the groups, use them
        if (taggedGroupTypes != null) {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                professorSpeech.set(Gen5Constants.bw1JuniperElesaTextOffset, 
                    "Professor Juniper: Elesa called and was"
                    + "\\xFFFEasking me all about "
                    + taggedGroupTypes.get("GYM4").camelCase() 
                    + "-type" + MAJOR_LINE_BREAK +"Pok\\x00E9mon, and I "
                    + "thought about you guys." + MAJOR_LINE_BREAK 
                    + "That's why I wanted to see you.\\xFFFETa-da! Freebies for you!"
                    + MINOR_LINE_BREAK);
            }
        }
        // Types are not available - Make any type references generic
        else {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                professorSpeech.set(Gen5Constants.bw1JuniperElesaTextOffset, 
                    "Professor Juniper: Elesa called and was"
                    + "\\xFFFEasking me all about her favorite "
                    + "type" + MAJOR_LINE_BREAK +"of Pok\\x00E9mon, and I "
                    + "thought about you guys." + MAJOR_LINE_BREAK 
                    + "That's why I wanted to see you.\\xFFFETa-da! Freebies for you!"
                    + MINOR_LINE_BREAK);
            }
        }
        setStrings(getRomEntry().getInt("JuniperElesaTextOffset"), professorSpeech);
    } 


    public void bw1DriftveilCityTextModifications(Map<String, Type> taggedGroupTypes, Random random) {
        List<String> gymLeaderSpeech = getStrings(getRomEntry().getInt("DriftveilLeaderTextOffset"));
        
        // If we have types available for the groups, use them
        if (taggedGroupTypes != null) {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymLeaderSpeech.set(Gen5Constants.bw1DriftveilCityTextOffset, 
                    "The Gym Leader Clay uses\\xFFFE" 
                    + taggedGroupTypes.get("GYM5").camelCase()
                    + "-type Pok\\x00E9mon!" + MAJOR_LINE_BREAK
                    + "Well, just between you and me,\\xFFFE"
                    + taggedGroupTypes.get("GYM5").camelCase()
                    + "-type Pok\\x00E9mon aren't good against" + MAJOR_LINE_BREAK
                    + Type.randomWeakness(random, false, taggedGroupTypes.get("GYM5")).camelCase()
                    + "-type attacks." + MAJOR_LINE_BREAK
                    + "Kinda makes me wonder why Clay is living\\xFFFEexposed to them.");
            }
        }
        // Types are not available - Make any type references generic
        else {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymLeaderSpeech.set(Gen5Constants.bw1DriftveilCityTextOffset, 
                "The Gym Leader Clay uses\\xFFFE" 
                + "very strong Pok\\x00E9mon!" + MAJOR_LINE_BREAK
                + "Well, just between you and me,\\xFFFE"
                + "those kind of Pok\\x00E9mon aren't good against" + MAJOR_LINE_BREAK
                + "strategic attacks." + MAJOR_LINE_BREAK
                + "Kinda makes me wonder why Clay is living\\xFFFEexposed to them.");
            }
        }
        setStrings(getRomEntry().getInt("DriftveilLeaderTextOffset"), gymLeaderSpeech);
    }

    public void bw1MistraltonCityTextModifications(Map<String, Type> taggedGroupTypes) {
        List<String> gymLeaderSpeech = getStrings(getRomEntry().getInt("MistraltonLeaderTextOffset"));
        
        // If we have types available for the groups, use them
        if (taggedGroupTypes != null) {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymLeaderSpeech.set(Gen5Constants.bw1MistraltonCityTextOffset,
                    "Just between you and me..." + MAJOR_LINE_BREAK
                    + Type.getWeaknesses(taggedGroupTypes.get("GYM6"), 3).stream()
                      .map(type -> type.camelCase()).collect(Collectors.joining(", "))
                    + "... " + taggedGroupTypes.get("GYM6").camelCase() 
                    + "-types have\\xFFFEmore weaknesses than people know about."
                    + MAJOR_LINE_BREAK + "If you use Pok\\x00E9mon and moves of those\\xFFFEtypes, "
                    + "victory is practically yours!" + MAJOR_LINE_BREAK + "By the way, "
                    + "to proceed in this Gym,\\xFFFEyou climb in the cannons to move forward."
                    + MAJOR_LINE_BREAK + "The cannons go up, down, left, and right.\\xFFFE"
                    + "You can get in them from anywhere!");
            }
        }
        // Types are not available - Make any type references generic
        else {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymLeaderSpeech.set(Gen5Constants.bw1MistraltonCityTextOffset,
                    "Just between you and me..." + MAJOR_LINE_BREAK
                    + "these types have\\xFFFEmore weaknesses than people know about."
                    + MAJOR_LINE_BREAK + "If you use Pok\\x00E9mon and moves of those\\xFFFEtypes, "
                    + "victory is practically yours!" + MAJOR_LINE_BREAK + "By the way, "
                    + "to proceed in this Gym,\\xFFFEyou climb in the cannons to move forward."
                    + MAJOR_LINE_BREAK + "The cannons go up, down, left, and right.\\xFFFE"
                    + "You can get in them from anywhere!");
            }
        }
        setStrings(getRomEntry().getInt("MistraltonLeaderTextOffset"), gymLeaderSpeech);  
    }

    public void bw1PinwheelForestTextModifications(Map<String, Type> taggedGroupTypes) {
        List<String> plasmaGormSpeech = getStrings(getRomEntry().getInt("PinwheelGormTextOffset"));
        
        // If we have types available for the groups, use them
        if (taggedGroupTypes != null) {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                plasmaGormSpeech.set(Gen5Constants.bw1PinwheelForestTextOffset,
                    "Gorm: I am one of the Seven Sages\\xFFFEof Team Plasma."
                    + MAJOR_LINE_BREAK + "Ghetsis, another of the Seven Sages,"
                    + "\\xFFFEwill liberate Pok\\x00E9mon with words alone!"
                    + MAJOR_LINE_BREAK + "The remainder of the Seven Sages have"
                    + "\\xFFFEordered their compatriots to take" + MAJOR_LINE_BREAK
                    + "Pok\\x00E9mon with full force!" + MAJOR_LINE_BREAK + "But the "
                    + "odds are a little against us now." + MAJOR_LINE_BREAK 
                    + "To you, the " + taggedGroupTypes.get("GYM3").camelCase()
                    + " Pok\\x00E9mon user Burgh and\\xFFFEthe "
                    + taggedGroupTypes.get("GYM2").camelCase() + " Pok\\x00E9mon user "
                    + "Lenora, I say..." + MAJOR_LINE_BREAK + "Know your enemies, "
                    + "know yourself,\\xFFFEand you need not fear the result"
                    + MAJOR_LINE_BREAK + "of a hundred battles..." + MAJOR_LINE_BREAK 
                    + "This time, we shall retreat quietly." + MINOR_LINE_BREAK);
            }
        }
        // Types are not available - Make any type references generic
        else {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                plasmaGormSpeech.set(Gen5Constants.bw1PinwheelForestTextOffset,
                    "Gorm: I am one of the Seven Sages\\xFFFEof Team Plasma."
                    + MAJOR_LINE_BREAK + "Ghetsis, another of the Seven Sages,"
                    + "\\xFFFEwill liberate Pok\\x00E9mon with words alone!"
                    + MAJOR_LINE_BREAK + "The remainder of the Seven Sages have"
                    + "\\xFFFEordered their compatriots to take" + MAJOR_LINE_BREAK
                    + "Pok\\x00E9mon with full force!" + MAJOR_LINE_BREAK + "But the "
                    + "odds are a little against us now." + MAJOR_LINE_BREAK 
                    + "To you, the average Pok\\x00E9mon user Burgh and\\xFFFEthe "
                    + "simple Pok\\x00E9mon user "
                    + "Lenora, I say..." + MAJOR_LINE_BREAK + "Know your enemies, "
                    + "know yourself,\\xFFFEand you need not fear the result"
                    + MAJOR_LINE_BREAK + "of a hundred battles..." + MAJOR_LINE_BREAK 
                    + "This time, we shall retreat quietly." + MINOR_LINE_BREAK);
            }
        }
        setStrings(getRomEntry().getInt("PinwheelGormTextOffset"), plasmaGormSpeech);  
    }

    public void bw1CelestialTowerTextModifications(Map<String, Type> taggedGroupTypes) {
        List<String> gymLeaderSpeech = getStrings(getRomEntry().getInt("CelestialSkylaTextOffset"));
        
        // If we have types available for the groups, use them
        if (taggedGroupTypes != null) {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymLeaderSpeech.set(Gen5Constants.bw1CelestialTowerTextOffset, 
                    "I'd like to introduce myself again!" + MAJOR_LINE_BREAK 
                    + "I'm Skyla, the Leader of\\xFFFEMistralton's "
                    + "Pok\\x00E9mon Gym." + MAJOR_LINE_BREAK + "I use "
                    + taggedGroupTypes.get("GYM6").camelCase()
                    + "-type Pok\\x00E9mon." + MAJOR_LINE_BREAK
                    + "When you are ready, please\\xFFFEcome to the Gym."
                    + MAJOR_LINE_BREAK + "I'll give you a big welcome!"
                    + MINOR_LINE_BREAK);
            }
        }
        // Types are not available - Make any type references generic
        else {
            // Update the text for the English games
            if (getRomEntry().getRomCode().charAt(3) == 'O') {
                gymLeaderSpeech.set(Gen5Constants.bw1CelestialTowerTextOffset, 
                    "I'd like to introduce myself again!" + MAJOR_LINE_BREAK 
                    + "I'm Skyla, the Leader of\\xFFFEMistralton's "
                    + "Pok\\x00E9mon Gym." + MAJOR_LINE_BREAK + "I use "
                    + "efficient Pok\\x00E9mon." + MAJOR_LINE_BREAK
                    + "When you are ready, please\\xFFFEcome to the Gym."
                    + MAJOR_LINE_BREAK + "I'll give you a big welcome!"
                    + MINOR_LINE_BREAK);
            }
        }
        setStrings(getRomEntry().getInt("CelestialSkylaTextOffset"), gymLeaderSpeech);  
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
                                    + MAJOR_LINE_BREAK + "Oh, never mind. I wanted this one"
                                    + "\\xFFFEfrom the start, anyway."
                                    + MINOR_LINE_BREAK);
            yourHouseStrings.set(Gen5Constants.bw1CherenText2Offset,
                    "It's decided. You'll be my opponent...\\xFFFEin our first Pok\\x00E9mon battle!"
                            + MAJOR_LINE_BREAK + "Let's see what you can do, \\xFFFEmy Pok\\x00E9mon!"
                            + MINOR_LINE_BREAK);
        }

        // rewrite
        setStrings(getRomEntry().getInt("StarterLocationTextOffset"), yourHouseStrings);

    }
    
    public void bw1MonkeyTextModifications(List<Pokemon> newStatics, Map<String, Type> taggedGroupTypes, Random random) {
        List<String> monkeyGuyStrings = getStrings(getRomEntry().getInt("MonkeyGiverTextOffset"));
        // Superior order is CILAN CHILI CRESS
        // Revised order is CRESS CILAN CHILI to match static order
        List<String> striatonStrings = Arrays.asList("CRESS", "CILAN", "CHILI");
        // Update the text for the English games
        if (getRomEntry().getRomCode().charAt(3) == 'O') {
            for (int i = 0; i < 3; i++) {
                // Original order is FIRE WATER GRASS
                // Static Pokemon order is GRASS FIRE WATER
                int staticPointer = (i + 1) % 3;
                Type superiorType = null, staticType = newStatics.get(staticPointer).primaryType;
                if (taggedGroupTypes != null) {
                    superiorType = taggedGroupTypes.get(striatonStrings.get(staticPointer));
                    // Check if the primary type is strong against the superior type
                    // If not, use the secondary type
                    if (newStatics.get(staticPointer).secondaryType != null &&
                        !Type.STRONG_AGAINST.get(superiorType.ordinal()).contains(staticType)) {
                        staticType = newStatics.get(staticPointer).secondaryType;
                    }
                } else {
                    superiorType = Type.randomStrength(random, false, newStatics.get(staticPointer).primaryType);
                }

                if (superiorType != null) {
                    monkeyGuyStrings.set(Gen5Constants.bw1MonkeyGiverTextOffset + i,
                        "OK. Here you go!" + MAJOR_LINE_BREAK + "It can use "
                        + staticType.camelCase() + "-type moves, "
                        + "so that\\xFFFEmakes it great against "
                        + superiorType.camelCase() + " types!" + MINOR_LINE_BREAK);
                } 
                // The monkey replacement is probably Normal type
                else {
                    monkeyGuyStrings.set(Gen5Constants.bw1MonkeyGiverTextOffset + i,
                        "OK. Here you go!" + MAJOR_LINE_BREAK + "It can use "
                        + staticType.camelCase() + "-type moves, "
                        + "so that\\xFFFEmakes it unremarkable against "
                        + "most types!" + MINOR_LINE_BREAK);
                }
            }
        }

        // rewrite
        setStrings(getRomEntry().getInt("MonkeyGiverTextOffset"), monkeyGuyStrings);
    }

    public void bw1CasteliaCityItemTextModifications(List<Pokemon> newStarters) {
        List<String> itemGuyStrings = getStrings(getRomEntry().getInt("CasteliaItemTextOffset"));
        
        // Update the text for the English games
        if (getRomEntry().getRomCode().charAt(3) == 'O') {
            itemGuyStrings.set(Gen5Constants.bw1CasteliaCityItemTextOffset, 
                "You have " + newStarters.get(0).getName() + "! "
                + "Then I will give you "
                + "this!" + MAJOR_LINE_BREAK + "When you let your "
                + "Pok\\x00E9mon hold it, it can\\xFFFEraise the power of " 
                + "Grass-type moves!" + MINOR_LINE_BREAK);
            itemGuyStrings.set(Gen5Constants.bw1CasteliaCityItemTextOffset + 1, 
                "You have " + newStarters.get(1).getName() + "! Then I "
                + "will give\\xFFFEyou "
                + "this!" + MAJOR_LINE_BREAK + "When you let your "
                + "Pok\\x00E9mon hold it, it can\\xFFFEraise the power of "
                + "Water-type moves!" + MINOR_LINE_BREAK);
            itemGuyStrings.set(Gen5Constants.bw1CasteliaCityItemTextOffset + 2, 
                "You have " + newStarters.get(2).getName() + "! "
                + "Then I will give you "
                + "this!" + MAJOR_LINE_BREAK + "When you let your "
                + "Pok\\x00E9mon hold it, it can\\xFFFEraise the power of "
                + "Fire-type moves!" + MINOR_LINE_BREAK);
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
                            + MAJOR_LINE_BREAK + "I'll use my Pok\\x00E9mon"
                            + "\\xFFFEthat I raised from an Egg!" + MINOR_LINE_BREAK);
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
