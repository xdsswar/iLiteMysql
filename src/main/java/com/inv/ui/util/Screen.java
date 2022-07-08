package com.inv.ui.util;

import java.awt.*;

/**
 * @author XDSSWAR
 */
public class Screen {

    public static Double getAllWith(){
        double max=0;
        GraphicsEnvironment ge      = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[]    gs      = ge.getScreenDevices();
        for (GraphicsDevice g : gs) {
            DisplayMode dm = g.getDisplayMode();
            max=max+dm.getWidth();
        }
        return max;
    }
}
