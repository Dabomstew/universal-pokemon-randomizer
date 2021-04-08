package com.dabomstew.pkrandom.settings;

import java.util.Arrays;

/*----------------------------------------------------------------------------*/
/*--  SettingsOptionFactory.java - encapsulates the instantiation of        --*/
/*--                               SettingsOption objects to reduce         --*/
/*--                               creation parameters into easier modules  --*/
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

public class SettingsOptionFactory {
    private static SettingsOptionFactory instance;
    private SettingsMap settingsMap;

    private SettingsOptionFactory() {}
    public static SettingsOptionFactory getInstance() {
        if (instance == null) {
            instance = new SettingsOptionFactory();
        }
        return instance;
    }

    public static void setSettingsMap(SettingsMap refMap) {
        getInstance().settingsMap = refMap;
    }

    public static <T> SettingsOptionComposite<T> createSettingsOption(SettingsOption.Builder builder) {
        SettingsOptionComposite<T> soc = builder.build();
        if (soc.getMatches().size() > 0) {
            soc.setIsChild(true);
            soc.getMatches().forEach((match) -> match.getParent().add(soc));
        } else {
            soc.setIsChild(false);
        }
        getInstance().settingsMap.putOption(soc.getName(), soc); 
        return soc;
    }
}
