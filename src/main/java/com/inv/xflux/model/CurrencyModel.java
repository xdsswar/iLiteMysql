package com.inv.xflux.model;

import com.inv.data.access.MysqlConnection;
import com.inv.xflux.entity.Currency;
import com.inv.xflux.entity.in.IDefault;
import com.inv.xflux.model.in.IDefaultable;
import com.inv.xflux.model.in.IModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.dbutils.DbUtils;
import org.jetbrains.annotations.TestOnly;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author XDSSWAR
 */
@SuppressWarnings("Duplicates")
public final class CurrencyModel implements IModel<Currency> , IDefaultable {
    private  Connection con=null;

    /**
     * Insert new Currencies in Db
     * @param currency Currencies
     */
    @Override
    public void insert(Currency currency) {
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm=con.prepareStatement("INSERT INTO currency(name, symbol) VALUES (?,?)");
                stm.setString(1,currency.getName());
                stm.setString(2,currency.getSymbol());
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
     * Delete Currencies from Db, This is very Important Method.
     * Before we need to check if the currency is default , if default then check
     * how many currencies are available , if there is more that one is ok , just
     * delete the selected currency and set the first one as default, but you can't
     * delete the currency if it is the only one available.If the currency is not
     * default , that mean there is more that one currency available , so just delete it .
     * @param currency selected Currencies to Delete
     */
    @Override
    public void delete(Currency currency)  {
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
               stm=con.prepareStatement("DELETE FROM currency WHERE id=?");
               stm.setLong(1,currency.getId());
               if (currency.isDefault()) {
                   if (getAll().size() > 1) {
                       stm.executeUpdate();
                       setDefault(getAll().get(0));
                   }
               }else {
                   stm.executeUpdate();
               }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
    }

    /**
     * Update Currencies
     * @param oldVal Old Currencies
     * @param newVal New Currencies
     */
    @Override
    public void update(Currency oldVal, Currency newVal) {
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm=con.prepareStatement("UPDATE currency SET name=?, symbol=? WHERE id=?");
                stm.setString(1,newVal.getName());
                stm.setString(2,newVal.getSymbol());
                stm.setLong(3,oldVal.getId());
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
     * Get All Currencies
     * @return Currencies list
     */
    @Override
    public ObservableList<Currency> getAll() {
        con= MysqlConnection.getInstance();
        ObservableList<Currency> list= FXCollections.observableArrayList();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("SELECT * FROM currency");
                rs=stm.executeQuery();
                while (rs.next()){
                    list.add(new Currency(rs.getLong("id"),rs.getString("name"),
                            rs.getString("symbol"),rs.getInt("active")));
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
     * Get Currencies by id
     * @param id Currencies id
     * @return Currencies
     */
    @Override
    public Currency get(long id) {
        con= MysqlConnection.getInstance();
        Currency currency=null;
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("SELECT * FROM currency WHERE id=?");
                stm.setLong(1,id);
                rs=stm.executeQuery();
                if (rs.next()){
                    currency=new Currency(rs.getLong("id"),rs.getString("name"),
                            rs.getString("symbol"),rs.getInt("active"));
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
        return currency;
    }

    /**
     * Get Active Currencies
     * @return Currencies
     */
    @Override
    public Currency getDefault(){
        con= MysqlConnection.getInstance();
        Currency currency=null;
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("SELECT * FROM currency WHERE active=?");
                stm.setInt(1,1);
                rs=stm.executeQuery();
                if (rs.next()){
                    currency=new Currency(rs.getLong("id"),rs.getString("name"),
                            rs.getString("symbol"),rs.getInt("active"));
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
        return currency;
    }

    /**
     * Set Currencies as Default or Activate it
     * @param currency Currencies
     */
    @Override
    public void setDefault(IDefault currency){
        for (Currency c:getAll()){
            changeStatus(c,false);
        }
        changeStatus((Currency) currency,true);
    }

    /**
     * Helper to activate or deactivate Currencies
     * @param currency Currencies
     * @param status boolean is status
     */
    private void changeStatus(Currency currency,boolean status){
        con=MysqlConnection.getInstance();
        int newStatus=0;
        if (status) {
            newStatus = 1;
        }
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm=con.prepareStatement("UPDATE currency SET active=? WHERE id=?");
                stm.setInt(1,newStatus);
                stm.setLong(2,currency.getId());
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
     * Check if Used
     * @param currency Currency
     * @return true when done
     */
    @Deprecated @TestOnly
    public boolean isUsed(Currency currency){
        boolean toReturn=false;
        con=MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("");

                toReturn=true;
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
        return toReturn;
    }

    /**
     * Check if the currency already Exist
     * @param name name
     * @return true when exist
     */
    public boolean alreadyExist(String name){
        boolean toReturn=false;
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("SELECT * FROM currency WHERE name=?");
                stm.setString(1,name);
                rs=stm.executeQuery();
                if (rs.next()){
                    toReturn=true;
                }
            }catch (SQLException e){
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
