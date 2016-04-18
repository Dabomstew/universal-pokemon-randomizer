package com.dabomstew.pkrandom;

import java.io.File;

public class SysConstants {

    public static final String AUTOUPDATE_URL = "http://pokehacks.dabomstew.com/randomizer/autoupdate/";
    public static final String WEBSITE_URL = "http://pokehacks.dabomstew.com/randomizer/";
    public static final int UPDATE_VERSION = 1702;
    public static final String ROOT_PATH = getRootPath();

    private static String getRootPath() {
        try {
            File fh = Utils.getExecutionLocation().getParentFile();
            return fh.getAbsolutePath() + File.separator;
        } catch (Exception e) {
            return "./";
        }
    }

}
