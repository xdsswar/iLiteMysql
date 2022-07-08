package com.inv.xflux.model;

import com.inv.data.access.MysqlConnection;
import com.inv.xflux.entity.Tax;
import com.inv.xflux.model.in.IModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author XDSSWAR
 */
@SuppressWarnings("Duplicates")
public final class TaxModel implements IModel<Tax> {
    private Connection con;

    /**
     * Insert new Tax on Db
     * @param tax Tax
     */
    @Override
    public void insert(Tax tax){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("INSERT INTO tax(taxVal,description) " +
                        "VALUE(?,?)");
                stm.setDouble(1, tax.getTaxVal());
                stm.setString(2, tax.getDescription());
                stm.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
    }

    /**
     * Delete Tax from Db
     * @param tax Tax
     */
    @Override
    public void delete(Tax tax){
        con= MysqlConnection.getInstance();
        if (con!=null) {
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("DELETE  FROM tax WHERE id=?");
                stm.setLong(1, tax.getId());
                stm.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
    }

    /**
     * Update Tax
     * @param tax Old Tax
     * @param newTax new Tax
     */
    @Override
    public void update(Tax tax, Tax newTax) {
        con=MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("UPDATE tax SET taxVal=?, description=? WHERE id=?");
                stm.setDouble(1, newTax.getTaxVal());
                stm.setString(2, newTax.getDescription());
                stm.setLong(3, tax.getId());
                stm.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
    }

    /**
     * Get All Taxes
     * @return List
     */
    @Override
    public ObservableList<Tax> getAll(){
        ObservableList<Tax> list= FXCollections.observableArrayList();
        con=MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM tax");
                rs = stm.executeQuery();
                while (rs.next()) {
                    list.add(
                            new Tax(
                                    rs.getLong("id"),
                                    rs.getDouble("taxVal"),
                                    rs.getString("description"),
                                    rs.getInt("active")
                            )
                    );
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
        return list;
    }

    /**
     * Get Tax by Id
     * @param id Id
     * @return Tax
     */
    @Override
    public Tax get(long id){
        con=MysqlConnection.getInstance();
        Tax tax=null;
        if (con!=null) {
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM tax WHERE id=?");
                stm.setLong(1, id);
                rs = stm.executeQuery();
                if (rs.next()) {
                    tax = new Tax(
                            rs.getLong("id"),
                            rs.getDouble("taxVal"),
                            rs.getString("description"),
                            rs.getInt("active")
                    );
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
        return tax;
    }

    /**
     * Get Tax by Id
     * @return Tax
     */
    public Tax getDefault(){
        con=MysqlConnection.getInstance();
        Tax tax=null;
        if (con!=null) {
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM tax WHERE active=?");
                stm.setLong(1, 1);
                rs = stm.executeQuery();
                if (rs.next()) {
                    tax = new Tax(
                            rs.getLong("id"),
                            rs.getDouble("taxVal"),
                            rs.getString("description"),
                            rs.getInt("active")
                    );
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
        return tax;
    }

    /**
     * Change Tax Status
     * @param tax Tax
     */
    public void setDefault(Tax tax){
        for (Tax t:getAll()){
            changeStatus(t,false);
        }
        changeStatus(tax,true);
    }

    /**
     * Change the Status on Tax
     * @param tax Tax
     * @param status 1 on true 0 on false
     */
    private void changeStatus(Tax tax , boolean status){
        con=MysqlConnection.getInstance();
        int newStatus=0;
        if (status) {
            newStatus = 1;
        }
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm=con.prepareStatement("UPDATE tax SET active=? WHERE id=?");
                stm.setInt(1, newStatus);
                stm.setLong(2,tax.getId());
                stm.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
    }

    /**
     * Check if tax is used
     * @param tax Tax
     * @return true when used
     */
    public boolean isUsed(Tax tax){
        boolean toReturn=false;
        con=MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("SELECT * FROM `order` WHERE taxId=?");
                stm.setLong(1,tax.getId());
                rs=stm.executeQuery();
                if (rs.next()){
                    toReturn=true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
        return toReturn;
    }


}
