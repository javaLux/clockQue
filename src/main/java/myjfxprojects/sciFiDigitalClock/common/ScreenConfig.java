package myjfxprojects.sciFiDigitalClock.common;

import javafx.stage.Screen;

/**
 * Class to get currently screen configurations
 */
public class ScreenConfig {

    private ScreenConfig() {}

    /**
     * Method returns the number of currently available screens
     * @return ->   int
     */
    public static int getAvailableScreens() {

        return Screen.getScreens().size();
    }

    public static double getHeightOfPrimaryScreen() {
        return Screen.getPrimary().getBounds().getHeight();
    }

    public static double getWidthOfPrimaryScreen() {
        return Screen.getPrimary().getBounds().getWidth();
    }
}
