package com.inv.xflux.model;

import com.inv.xflux.entity.Client;
import com.inv.xflux.model.in.IModel;
import com.inv.data.access.MysqlConnection;
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
public final class ClientModel implements IModel<Client> {
    private Connection con;

    /**
     * Inset new Client
     * @param client Client
     */
    @Override
    public void insert(Client client){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("INSERT INTO client(name, email, phone) " +
                        "VALUES (?,?,?)");
                stm.setString(1, client.getName());
                stm.setString(2, client.getEmail());
                stm.setString(3, client.getPhone());
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
     * Delete Client
     * @param client Client
     */
    @Override
    public void delete(Client client) {
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("DELETE FROM client WHERE id=?");
                stm.setLong(1, client.getId());
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
     * Update Client
     * @param client  Client
     * @param newClient new Client
     */
    @Override
    public void update(Client client, Client newClient){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("UPDATE client SET name=?, email=?," +
                        "phone=? WHERE id=?");
                stm.setString(1, newClient.getName());
                stm.setString(2, newClient.getEmail());
                stm.setString(3, newClient.getPhone());
                stm.setLong(4, client.getId());
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
     * Get All Clients
     * @return List
     */
    @Override
    public ObservableList<Client> getAll(){
        ObservableList<Client> list= FXCollections.observableArrayList();
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM client");
                rs = stm.executeQuery();
                while (rs.next()) {
                    list.add(
                            new Client(
                                    rs.getLong("id"),
                                    rs.getString("name"),
                                    rs.getString("email"),
                                    rs.getString("phone")
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
     * Get a Client by Id
     * @param id Id
     * @return Client
     */
    @Override
    public Client get(long id){
        Client client=null;
        con= MysqlConnection.getInstance();
        if (con!=null) {
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM client WHERE id=?");
                stm.setLong(1, id);
                rs = stm.executeQuery();
                if (rs.next()) {
                    client = new Client(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone")
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
        return client;
    }

    /**
     * Check if the Client has Orders
     * This is used for example when removing a client , if has orders you cannot remove it
     * @param client Client
     * @return true when has orders
     */
    public boolean hasOrders(Client client){
        boolean toReturn=false;
        con=MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
               stm=con.prepareStatement("SELECT * FROM `order` WHERE clientId=?");
               stm.setLong(1,client.getId());
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
