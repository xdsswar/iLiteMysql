package com.inv.data.access.maintenance;

import com.inv.data.access.MysqlConnection;
import com.inv.data.access.sec.Security;
import org.apache.commons.dbutils.DbUtils;

import java.sql.*;

/**
 * @author XDSSWAR
 */
@SuppressWarnings("Duplicates")
public class Check {
    private static  Connection con=null;
    /**
     * Check if User and Password are ok
     * @param user User
     * @param password Password
     * @return true when Ok
     */
    public static boolean isEmployee(String user,String password){
        boolean isEmployee=false;
        con = MysqlConnection.getInstance();
        if (con !=null) {
            PreparedStatement stm = null;
            ResultSet rs=null;
            try {
                stm= con.prepareStatement("SELECT * FROM employee WHERE user=? AND password=?");
                stm.setString(1,user);
                stm.setString(2, Security.encryptPassword(password));
                rs=stm.executeQuery();
                if (rs.next()){
                    isEmployee=true;
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }

        return isEmployee;
    }


    /**
     * Check if table exist in database
     * @param tableName table name
     * @return true when exist
     */
    private static boolean tableExist(String tableName){
        boolean toReturn=false;
        con = MysqlConnection.getInstance();
        if (con!=null){
            DatabaseMetaData metaData=null;
            ResultSet tables=null;
            try {
                 metaData=con.getMetaData();
                 tables=metaData.getTables(null,null,tableName,null);
                 toReturn=tables.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(tables);
                DbUtils.closeQuietly(con);
            }
        }
        return toReturn;
    }



}
