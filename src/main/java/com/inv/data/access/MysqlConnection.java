package com.inv.data.access;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Xdsswar
 */
public class MysqlConnection {
    private static String DB_URL="jdbc:mysql://"+Settings.getSettingsValue("server")+":"+
            Settings.getSettingsValue("port")+"/"+Settings.getSettingsValue("dbname")+
            "?useUnicode=true&useJDBCCompliantTimezoneShift=true" +
            "&useLegacyDatetimeCode=false&characterEncoding=UTF-8&useSSL="+Settings.getSettingsValue("use.ssl")+"&AllowPublicKeyRetrieval=True";//&serverTimezone=UTC

    private static String USER_NAME= (String)Settings.getSettingsValue("dbUser");
    private static String DB_PASS=(String)Settings.getSettingsValue("dbPassword");
    public static Connection instance =null;
    private MysqlConnection(){}

    /**
     * Connection Instance
     * Do not Touch this method , Just keep it like that
     * @return instance
     */
    public synchronized static  Connection getInstance(){
        try {
            instance = DriverManager.getConnection(DB_URL, USER_NAME, DB_PASS);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return instance;
    }

    /**
     * Check Database Connectivity
     * @return true is Ok
     */
    public synchronized static boolean checkConnection(){
        Connection con=getInstance();
        boolean isOk=false;
        if (con!=null){
            isOk=true;
        }
        return isOk;
    }

    /**
     * Reset Connection
     */
    public synchronized static void reset(){
        instance =null;
        DB_URL="jdbc:mysql://"+Settings.getSettingsValue("server")+":"+
                Settings.getSettingsValue("port")+"/"+Settings.getSettingsValue("dbname")+
                "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false" +
                "&characterEncoding=UTF-8&useSSL="+Settings.getSettingsValue("use.ssl")+"&AllowPublicKeyRetrieval=True";//&serverTimezone=UTC
        USER_NAME= (String)Settings.getSettingsValue("dbUser");
        DB_PASS=(String)Settings.getSettingsValue("dbPassword");
    }

}
