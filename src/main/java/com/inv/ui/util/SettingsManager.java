package com.inv.ui.util;

import com.inv.data.access.Settings;

/**
 * @author XDSSWAR
 */
public class SettingsManager {
    /**
     * Server setting
     * @param host host
     * @param port port def 3306
     * @param dbName Database Name
     * @param dbUser Database User
     * @param dbPass Database Password
     */
    public static void setServerSettings(String host,String port, String dbName, String dbUser, String dbPass){
        if (!host.isEmpty()) {
            Settings.setSettings("server", host);
        }else {
            Settings.setSettings("server", "localhost");
        }
        if (!port.isEmpty()) {
            Settings.setSettings("port", port);
        }else {
            Settings.setSettings("port", "3306");
        }
        if (!dbName.isEmpty()) {
            Settings.setSettings("dbname", dbName);
        }else {
            Settings.setSettings("dbname", "flux");
        }
        if (!dbUser.isEmpty()) {
            Settings.setSettings("dbUser", dbUser);
        }else {
            Settings.setSettings("dbUser", "root");
        }
        if (!dbPass.isEmpty()) {
            Settings.setSettings("dbPassword", dbPass);
        }else {
            Settings.setSettings("dbPassword", "9210");
        }
    }
}
