package com.inv.data.access;

import com.inv.data.unique.LockedException;
import com.inv.data.unique.Single;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author XDSSWAR
 * THIS CLASS IS VERY IMPORTANT TO MANAGEMENT THE PLATFORM SPECIAL FOLDERS AND THE SETTINGS
 * *******************DO NOT TOUCH OR MODIFY IT********************************************
 */
public final class Settings {
    //Vars
    private static String _OS= System.getProperty("os.name").toLowerCase();
    public static String _PATH= System.getProperty("user.home");
    private static Properties _settings=new Properties();
    private static String _urlToSettings =getSettingsPathUrl();
    private static FileOutputStream out = null;
    private static FileInputStream in=null;

    /**
     * This check if the Application is already running
     * to allow only 1 instance
     * @return true when running
     */
    public static boolean isRunning(){
        String appId = "com.xd.ILite";
        boolean alreadyRunning;
        try {
            Single.acquireLock(appId);
            alreadyRunning = false;
        } catch (LockedException e) {
            alreadyRunning = true;
        }
        return alreadyRunning;
    }

    /**
     *0- Windows
     *1- Mac OS
     *2- Linux
     *3- Platform cannot be determined
     * @return OS
     */
    protected static int getPlatform(){
        if(_OS.contains("win")){
            return 0;//Windows
        }
        if(_OS.contains("mac")){
            return 1;//Mac OS
        }
        if(_OS.contains("nix") || _OS.contains("nux") || _OS.indexOf("aix") > 0 ){
            return 2;//Linux
        }
        return 3;//Platform cannot be determined
    }

    /**
     * Check is Window
     * @return boolean
     */
    public static boolean isWindow(){
        return getPlatform() == 0;
    }

    /**
     * Check is Mac
     * @return boolean
     */
    public static boolean isMac(){
        return getPlatform() == 1;
    }

    /**
     * Check is Linux
     * @return boolean
     */
    public static boolean isLinux(){
        return getPlatform() == 2;
    }

    /**
     * Get the Current Path to Save the Invices
     * @return String Path
     */
    private static String getDocPath(){
        final String folderName="ILite Invoices";
        JFileChooser chooser=new JFileChooser();
        chooser.setVisible(false);
        FileSystemView fw=chooser.getFileSystemView();
        String s=fw.getDefaultDirectory().toString();
        String path=null;
        if (isWindow()){
            path=s+"\\"+folderName;
        }else if (isMac()){
            path=s+"/"+folderName;
        }else {
            path=s+"/"+folderName;
        }
        return path;
    }

    /**
     * Create Documents path if not exist
     */
    private static void initDocPath(){
        if (!Files.exists(Paths.get(getDocPath()))) {
            try {
                Files.createDirectory(Paths.get(getDocPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get Invoices Folder Path
     * @return Path as String
     */
    public static String getInvoicesFolderPath(){
        initDocPath();//Create Invoices  Folder if necessary
        if (isWindow()){
            return getDocPath()+"\\";
        }
        return getDocPath()+"/";
    }

    /**
     * User's Path Url
     * @return Path Url
     */
    public static String getUserPathUrl(){
        if(isWindow()){
            return _PATH+"\\"+ Folder.FOLDER;
        }
        return _PATH+"/"+ Folder.FOLDER;
    }//End Get User Path for OS

    /**
     * Settings Path Url
     * @return path
     */
    public static String getSettingsPathUrl(){
        if(isWindow()){
            return getUserPathUrl()+"\\"+Folder.FILE;
        }
        return getUserPathUrl()+"/"+Folder.FILE;
    }//End Get settings path url

    /**
     * Check if the conf folder exist on the User's special Dir
     * @return true if exist
     */
    public static boolean isUserPathActive(){
        if (Files.exists(Paths.get(getUserPathUrl()))) {
           return true;
        }
        return false;
    }

    /**
     * Check if the conf file exist
     * @return true if exist
     */
    public static boolean isSettingsPathActive(){
        if (Files.exists(Paths.get(_urlToSettings))){
            return true;
        }
        return false;
    }

    /**
     * Activate the App's Folder on the User Dir
     * @return true when done
     */
    public static boolean activateUserPath(){
        try{
            Files.createDirectory(Paths.get(getUserPathUrl()));
            out=new FileOutputStream(_urlToSettings);
            out.close();
            return true;
        }catch (IOException e){
            return false;
        }
    }

    /**
     * Activate the Settings File on the App's Folder
     * @return true when done
     */
    public static boolean activateSettingsPath(){
        try {
            out=new FileOutputStream(_urlToSettings);
            out.close();
            return true;
        } catch (IOException e ) {
            return false;
        }
    }

    /**
     * Get Settings
     * @return settings
     */
    public static Properties getCurrentSettings(){
        return _settings;
    }//End Return Settings

    /**
     * Sett Settings values
     * @param key key
     * @param value value
     */
    public static void setSettings(Object key, Object value){
        if(value==null){
            value="";
        }
        _settings.put(key,value);
        try {
            if(Files.exists(Paths.get(_urlToSettings))) {
                out = new FileOutputStream(_urlToSettings);
                _settings.store(out, null);
                out.close();            }
        }catch (IOException e){}

    }//End Modify Settings

    /**
     * Get Setting val by key
     * @param key key
     * @return value
     */
    public static Object getSettingsValue(Object key){
        try {
            if(Files.exists(Paths.get(_urlToSettings))){
                in=new FileInputStream(_urlToSettings);
                _settings.load(in);
                return _settings.getProperty((String) key);
            }
        } catch (IOException e){
            return e;
        }
        return null;
    }

    /**
     * init config and ensure the all paths are created
     * @return true when done
     */
    public static boolean initSettings(){
        initDocPath();
        if(isUserPathActive()){
            if (isSettingsPathActive()){

            }else if (!isSettingsPathActive()){
                boolean act=activateSettingsPath();
                if (act){
                    init();
                }
            }
        }else if (!isUserPathActive()){
            boolean act=activateUserPath();
            if (act){
                act=activateSettingsPath();
                if (act){
                    init();
                }
            }
        }

        File file=new File(getSettingsPathUrl());
        if (isSettingsPathActive()&& file.length()>0){
            return true;
        }
        return false;
    }

    /**
     * Init default config
     */
    public static void init(){
        setSettings("server","localhost");
        setSettings("port","3306");
        setSettings("dbname","ilite");
        setSettings("dbUser","root");
        setSettings("dbPassword","9210");
        setSettings("max", "1");
        setSettings("expanded","1");
        setSettings("alertSound","1");
        setSettings("use.ssl","false");
    }





}
