package com.inv.xflux.model;

import com.inv.xflux.entity.Provider;
import com.inv.data.access.MysqlConnection;
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
public final class ProviderModel implements IModel<Provider> {
    private Connection con;

    /**
     * Inset Provider Into DB
     * @param provider Provider
     */
    @Override
    public void insert(Provider provider){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM provider WHERE name=?");
                stm.setString(1, provider.getName());
                rs = stm.executeQuery();
                if (!rs.next()) {
                    PreparedStatement statement = con.prepareStatement("INSERT INTO provider(name, email, phone, address) " +
                            "VALUES (?,?,?,?)");
                    statement.setString(1, provider.getName());
                    statement.setString(2, provider.getEmail());
                    statement.setString(3, provider.getPhone());
                    statement.setString(4, provider.getAddress());
                    statement.executeUpdate();
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
    }

    /**
     * Delete Provider
     * @param provider Provider
     */
    @Override
    public void delete(Provider provider){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("DELETE FROM provider WHERE id=?");
                stm.setLong(1, provider.getId());
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
     * Update Provider
     * @param oldProvider Old Provider
     * @param newProvider New Provider
     */
    @Override
    public void update(Provider oldProvider, Provider newProvider){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm= con.prepareStatement("UPDATE provider SET name=?,email=?,phone=?," +
                        "address=? WHERE id=?");
                stm.setString(1, newProvider.getName());
                stm.setString(2, newProvider.getEmail());
                stm.setString(3, newProvider.getPhone());
                stm.setString(4, newProvider.getAddress());
                stm.setLong(5, oldProvider.getId());
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
     * Get all Providers
     * @return Provider List
     */
    @Override
    public ObservableList<Provider> getAll(){
        ObservableList<Provider> list= FXCollections.observableArrayList();
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM  provider");
                rs = stm.executeQuery();
                while (rs.next()) {
                    list.add(
                            new Provider(
                                    rs.getLong("id"),
                                    rs.getString("name"),
                                    rs.getString("email"),
                                    rs.getString("phone"),
                                    rs.getString("address")
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
     * Get Provider by Id
     * @param id Id
     * @return Provider
     */
    @Override
    public Provider get(long id){
        Provider provider=null;
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM provider WHERE id=?");
                stm.setLong(1, id);
                rs = stm.executeQuery();
                if (rs.next()) {
                    provider = new Provider(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("address")
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
        return provider;
    }

    /**
     * Check if Provider is in use
     * @param provider Provider
     * @return true when used
     */
    public boolean isUsed(Provider provider){
        boolean toReturn=false;
        con=MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("SELECT * FROM product WHERE provId=?");
                stm.setLong(1,provider.getId());
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

    /**
     * Check if the Provider Already Exist
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
                stm = con.prepareStatement("SELECT * FROM provider WHERE name=?");
                stm.setString(1, name);
                rs = stm.executeQuery();
                if (rs.next()) {
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
