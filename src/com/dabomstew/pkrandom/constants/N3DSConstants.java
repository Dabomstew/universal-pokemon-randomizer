package com.dabomstew.pkrandom.constants;

/*----------------------------------------------------------------------------*/
/*--  N3DSConstants.java - constants that are relevant for all of the 3DS   --*/
/*--                       games                                            --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2020.                                 --*/
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

import java.util.HashMap;
import java.util.Map;

public class N3DSConstants {

    public static final int Type_XY = 0, Type_ORAS = 1, Type_SM = 2, Type_USUM = 3;

    private static final Map<Integer,String> textVariableCodesXY = setupTextVariableCodes(Type_XY);
    private static final Map<Integer,String> textVariableCodesORAS = setupTextVariableCodes(Type_ORAS);
    private static final Map<Integer,String> textVariableCodesSM = setupTextVariableCodes(Type_SM);

    public static Map<Integer,String> getTextVariableCodes(int romType) {
        if (romType == Type_XY) {
            return textVariableCodesXY;
        } else if (romType == Type_ORAS) {
            return textVariableCodesORAS;
        } else if (romType == Type_SM || romType == Type_USUM) {
            return textVariableCodesSM;
        }
        return new HashMap<>();
    }

    private static Map<Integer,String> setupTextVariableCodes(int romType) {
        Map<Integer,String> map = new HashMap<>();

        if (romType == Type_XY) {
            map.put(0xFF00, "COLOR");
            map.put(0x0100, "TRNAME");
            map.put(0x0101, "PKNAME");
            map.put(0x0102, "PKNICK");
            map.put(0x0103, "TYPE");
            map.put(0x0105, "LOCATION");
            map.put(0x0106, "ABILITY");
            map.put(0x0107, "MOVE");
            map.put(0x0108, "ITEM1");
            map.put(0x0109, "ITEM2");
            map.put(0x010A, "sTRBAG");
            map.put(0x010B, "BOX");
            map.put(0x010D, "EVSTAT");
            map.put(0x0110, "OPOWER");
            map.put(0x0127, "RIBBON");
            map.put(0x0134, "MIINAME");
            map.put(0x013E, "WEATHER");
            map.put(0x0189, "TRNICK");
            map.put(0x018A, "1stchrTR");
            map.put(0x018B, "SHOUTOUT");
            map.put(0x018E, "BERRY");
            map.put(0x018F, "REMFEEL");
            map.put(0x0190, "REMQUAL");
            map.put(0x0191, "WEBSITE");
            map.put(0x019C, "CHOICECOS");
            map.put(0x01A1, "GSYNCID");
            map.put(0x0192, "PRVIDSAY");
            map.put(0x0193, "BTLTEST");
            map.put(0x0195, "GENLOC");
            map.put(0x0199, "CHOICEFOOD");
            map.put(0x019A, "HOTELITEM");
            map.put(0x019B, "TAXISTOP");
            map.put(0x019F, "MAISTITLE");
            map.put(0x1000, "ITEMPLUR0");
            map.put(0x1001, "ITEMPLUR1");
            map.put(0x1100, "GENDBR");
            map.put(0x1101, "NUMBRNCH");
            map.put(0x1302, "iCOLOR2");
            map.put(0x1303, "iCOLOR3");
            map.put(0x0200, "NUM1");
            map.put(0x0201, "NUM2");
            map.put(0x0202, "NUM3");
            map.put(0x0203, "NUM4");
            map.put(0x0204, "NUM5");
            map.put(0x0205, "NUM6");
            map.put(0x0206, "NUM7");
            map.put(0x0207, "NUM8");
            map.put(0x0208, "NUM9");
        } else if (romType == Type_ORAS) {
            map.put(0xFF00, "COLOR");
            map.put(0x0100, "TRNAME");
            map.put(0x0101, "PKNAME");
            map.put(0x0102, "PKNICK");
            map.put(0x0103, "TYPE");
            map.put(0x0105, "LOCATION");
            map.put(0x0106, "ABILITY");
            map.put(0x0107, "MOVE");
            map.put(0x0108, "ITEM1");
            map.put(0x0109, "ITEM2");
            map.put(0x010A, "sTRBAG");
            map.put(0x010B, "BOX");
            map.put(0x010D, "EVSTAT");
            map.put(0x0110, "OPOWER");
            map.put(0x0127, "RIBBON");
            map.put(0x0134, "MIINAME");
            map.put(0x013E, "WEATHER");
            map.put(0x0189, "TRNICK");
            map.put(0x018A, "1stchrTR");
            map.put(0x018B, "SHOUTOUT");
            map.put(0x018E, "BERRY");
            map.put(0x018F, "REMFEEL");
            map.put(0x0190, "REMQUAL");
            map.put(0x0191, "WEBSITE");
            map.put(0x019C, "CHOICECOS");
            map.put(0x01A1, "GSYNCID");
            map.put(0x0192, "PRVIDSAY");
            map.put(0x0193, "BTLTEST");
            map.put(0x0195, "GENLOC");
            map.put(0x0199, "CHOICEFOOD");
            map.put(0x019A, "HOTELITEM");
            map.put(0x019B, "TAXISTOP");
            map.put(0x019F, "MAISTITLE");
            map.put(0x1000, "ITEMPLUR0");
            map.put(0x1001, "ITEMPLUR1");
            map.put(0x1100, "GENDBR");
            map.put(0x1101, "NUMBRNCH");
            map.put(0x1302, "iCOLOR2");
            map.put(0x1303, "iCOLOR3");
            map.put(0x0200, "NUM1");
            map.put(0x0201, "NUM2");
            map.put(0x0202, "NUM3");
            map.put(0x0203, "NUM4");
            map.put(0x0204, "NUM5");
            map.put(0x0205, "NUM6");
            map.put(0x0206, "NUM7");
            map.put(0x0207, "NUM8");
            map.put(0x0208, "NUM9");
        } else if (romType == Type_SM) {
            map.put(0xFF00, "COLOR");
            map.put(0x0100, "TRNAME");
            map.put(0x0101, "PKNAME");
            map.put(0x0102, "PKNICK");
            map.put(0x0103, "TYPE");
            map.put(0x0105, "LOCATION");
            map.put(0x0106, "ABILITY");
            map.put(0x0107, "MOVE");
            map.put(0x0108, "ITEM1");
            map.put(0x0109, "ITEM2");
            map.put(0x010A, "sTRBAG");
            map.put(0x010B, "BOX");
            map.put(0x010D, "EVSTAT");
            map.put(0x0110, "OPOWER");
            map.put(0x0127, "RIBBON");
            map.put(0x0134, "MIINAME");
            map.put(0x013E, "WEATHER");
            map.put(0x0189, "TRNICK");
            map.put(0x018A, "1stchrTR");
            map.put(0x018B, "SHOUTOUT");
            map.put(0x018E, "BERRY");
            map.put(0x018F, "REMFEEL");
            map.put(0x0190, "REMQUAL");
            map.put(0x0191, "WEBSITE");
            map.put(0x019C, "CHOICECOS");
            map.put(0x01A1, "GSYNCID");
            map.put(0x0192, "PRVIDSAY");
            map.put(0x0193, "BTLTEST");
            map.put(0x0195, "GENLOC");
            map.put(0x0199, "CHOICEFOOD");
            map.put(0x019A, "HOTELITEM");
            map.put(0x019B, "TAXISTOP");
            map.put(0x019F, "MAISTITLE");
            map.put(0x1000, "ITEMPLUR0");
            map.put(0x1001, "ITEMPLUR1");
            map.put(0x1100, "GENDBR");
            map.put(0x1101, "NUMBRNCH");
            map.put(0x1302, "iCOLOR2");
            map.put(0x1303, "iCOLOR3");
            map.put(0x0200, "NUM1");
            map.put(0x0201, "NUM2");
            map.put(0x0202, "NUM3");
            map.put(0x0203, "NUM4");
            map.put(0x0204, "NUM5");
            map.put(0x0205, "NUM6");
            map.put(0x0206, "NUM7");
            map.put(0x0207, "NUM8");
            map.put(0x0208, "NUM9");
        }
        return map;
    }

    public static int getVariableCode(String name, int romType) {
        Map<Integer,String> map = getTextVariableCodes(romType);
        for (int k: map.keySet()) {
            if (map.get(k).equals(name)) {
                return k;
            }
        }
        return 0;
    }
}
