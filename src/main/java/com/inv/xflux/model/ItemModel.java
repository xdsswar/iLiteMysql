package com.inv.xflux.model;

import com.inv.xflux.entity.Item;
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
@SuppressWarnings("Duplicates")
public final class ItemModel implements IModel<Item> {

    private Connection con;
    /**
     *Inset new Item
     * @param item Item
     */
    @Override
    public void insert(Item item){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("INSERT INTO item(orderId, prodId, quantity, discount, total) " +
                        "VALUES (?,?,?,?,?)");
                stm.setLong(1, item.getOrderId());
                stm.setLong(2, item.getProduct().getId());
                stm.setDouble(3, item.getQuantity().get());
                stm.setDouble(4, item.getDiscount().get());
                stm.setDouble(5, item.getTotal().get());
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
     * Delete Item
     * @param item Item
     */
    @Override
    public void delete(Item item){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("DELETE FROM item WHERE orderId=?");
                stm.setLong(1, item.getOrderId());
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
     * Update Item
     * @param oldItem old Item
     * @param newItem new Item
     */
    @Override
    public void update(Item oldItem, Item newItem){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("UPDATE item SET prodId=?, quantity=?, discount=?, " +
                        "total=? WHERE id=?");
                stm.setLong(1, newItem.getProduct().getId());
                stm.setDouble(2, newItem.getQuantity().get());
                stm.setDouble(3, newItem.getDiscount().get());
                stm.setDouble(4, newItem.getTotal().get());
                stm.setLong(5, oldItem.getId());
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
     * Get all Items
     * @return Items List
     */
    @Override
    public ObservableList<Item> getAll(){
        ObservableList<Item> list= FXCollections.observableArrayList();
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM item");
                rs = stm.executeQuery();
                while (rs.next()) {
                    list.add(
                            new Item(
                                    rs.getLong("id"),
                                    rs.getLong("orderId"),
                                    new ProductModel().get(rs.getLong("prodId")),
                                    rs.getDouble("quantity"),
                                    rs.getDouble("discount"),
                                    rs.getDouble("total")
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
     * Get Item by Id
     * @param id Id
     * @return Item
     */
    @Override
    public Item get(long id) {
        con= MysqlConnection.getInstance();
        Item item=null;
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("SELECT * FROM item WHERE id=?");
                stm.setLong(1,id);
                rs=stm.executeQuery();
                if (rs.next()){
                    item=new Item(
                            rs.getLong("id"),
                            rs.getLong("orderId"),
                            new ProductModel().get(rs.getLong("prodId")),
                            rs.getDouble("quantity"),
                            rs.getDouble("discount"),
                            rs.getDouble("total")
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
        return item;
    }

    /**
     * Get all items in an order
     * @param orderId Order id
     * @return Item list
     */
    public ObservableList<Item> getAllByOrderId(long orderId){
        ObservableList<Item> list=FXCollections.observableArrayList();
        con=MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM item WHERE orderId=?");
                stm.setLong(1, orderId);
                rs = stm.executeQuery();
                while (rs.next()) {
                    list.add(new Item(
                            rs.getLong("id"),
                            rs.getLong("orderId"),
                            new ProductModel().get(rs.getLong("prodId")),
                            rs.getDouble("quantity"),
                            rs.getDouble("discount"),
                            rs.getDouble("total")
                    ));
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
     * Delete Item
     * @param orderId Order Id
     */
    public void delete(long orderId){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("DELETE FROM item WHERE orderId=?");
                stm.setLong(1, orderId);
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
     * Update Items Product quantity
     * @param item Item
     * @param quantity Quantity
     */
    public void updateQuantity(Item item, double quantity){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("UPDATE item SET quantity=? WHERE id=?");
                stm.setDouble(1, quantity);
                stm.setLong(2, item.getId());
                stm.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
    }
}
