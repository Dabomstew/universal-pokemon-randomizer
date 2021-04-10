package com.dabomstew.pkrandom.settings;

/*----------------------------------------------------------------------------*/
/*--  SettingsMap.java -   wraps a Map object with utility functions to     --*/
/*--                       interact with either the SettingsOption object   --*/
/*--                       in the map or the methods of that SettingsOption --*/ 
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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class SettingsMap {
    Map<String, SettingsOption> map;

    public SettingsMap() {
        map = new HashMap<String, SettingsOption>(256, 0.75f);
    }

    public SettingsOption getOption(String option) {
        return map.get(option);
    }

    public <T> T getValue(String option) {
        return (T) map.get(option).getItem();
    }

    public SettingsOption putOption(String name, SettingsOption option) {
        return map.put(name, option);
    }

    public <T> T putValue(String name, T value) {
        T prev = getValue(name);
        map.get(name).setItem(value);
        return prev;
    }

    public void forEachParent(Consumer<Entry<String, SettingsOption>> method) {
        map.entrySet().stream().filter((entry) -> !entry.getValue().isChild()).forEach(method);
    }
}
