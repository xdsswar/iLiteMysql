package com.inv.ui.util;

import java.net.URL;
import java.net.URLConnection;

/**
 * @author XDSSWAR
 */
public class Internet {
    /**
     * Check Internet Connection
     * @return true when done
     */
    public static boolean check(){
        try {
            URL url = new URL("https://envato.com");
            URLConnection connection = url.openConnection();
            connection.connect();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
