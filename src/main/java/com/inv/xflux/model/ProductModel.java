package com.inv.xflux.model;

import com.inv.xflux.entity.*;
import com.inv.xflux.model.in.IModel;
import com.inv.xflux.model.in.IProductModel;
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
public final class ProductModel implements IModel<Product>, IProductModel {
    private Connection con;

    /**
     * Insert New Product
     * @param product Product
     */
    @Override
    public void insert(Product product){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM product WHERE name=?");
                stm.setString(1, product.getName());
                rs = stm.executeQuery();
                if (!rs.next()) {
                    PreparedStatement statement = con.prepareStatement("INSERT INTO " +
                            "product(name, description, catId, provId, brandId, unitId, quantity," +
                            " buyCost, sellPrice) " +
                            "VALUES (?,?,?,?,?,?,?,?,?)");
                    statement.setString(1, product.getName());
                    statement.setString(2, product.getDescription());
                    statement.setLong(3, product.getCategory().getId());
                    statement.setLong(4, product.getProvider().getId());
                    statement.setLong(5, product.getBrand().getId());
                    statement.setLong(6, product.getUnit().getId());
                    statement.setDouble(7, product.getQuantity());
                    statement.setDouble(8, product.getBuyCost());
                    statement.setDouble(9, product.getSellPrice());
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
     * Delete Product
     * @param product Product
     */
    @Override
    public void delete(Product product){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("DELETE FROM product WHERE id=?");
                stm.setLong(1, product.getId());
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
     * Update Product
     * @param oldProduct Old Product
     * @param newProduct new Product
     */
    @Override
    public void update(Product oldProduct, Product newProduct) {
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("UPDATE product SET " +
                        "name=?,description=?,catId=?,provId=?,brandId=?,unitId=?,quantity=?," +
                        "buyCost=?,sellPrice=? WHERE id=?");
                stm.setString(1, newProduct.getName());
                stm.setString(2, newProduct.getDescription());
                stm.setLong(3, newProduct.getCategory().getId());
                stm.setLong(4, newProduct.getProvider().getId());
                stm.setLong(5, newProduct.getBrand().getId());
                stm.setLong(6, newProduct.getUnit().getId());
                stm.setDouble(7, newProduct.getQuantity());
                stm.setDouble(8, newProduct.getBuyCost());
                stm.setDouble(9, newProduct.getSellPrice());
                stm.setLong(10, oldProduct.getId());
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
     * Get All Products
     * @return Products List
     */
    @Override
    public ObservableList<Product> getAll(){
        ObservableList<Product> list= FXCollections.observableArrayList();
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM product");
                rs = stm.executeQuery();
                while (rs.next()) {
                    list.add(
                            new Product(
                                    rs.getLong("id"),
                                    rs.getString("name"),
                                    rs.getString("description"),
                                    new CategoryModel().get(rs.getLong("catId")),
                                    new ProviderModel().get(rs.getLong("provId")),
                                    new BrandModel().get(rs.getLong("brandId")),
                                    new UnitModel().get(rs.getLong("unitId")),
                                    rs.getDouble("quantity"),
                                    rs.getDouble("buyCost"),
                                    rs.getDouble("sellPrice")
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
     * Get Product by Id
     * @param id Id
     * @return Product
     */
    @Override
    public Product get(long id){
        Product product=null;
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM product WHERE id=?");
                stm.setLong(1, id);
                rs = stm.executeQuery();
                if (rs.next()) {
                    product = new Product(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            new CategoryModel().get(rs.getLong("catId")),
                            new ProviderModel().get(rs.getLong("provId")),
                            new BrandModel().get(rs.getLong("brandId")),
                            new UnitModel().get(rs.getLong("unitId")),
                            rs.getDouble("quantity"),
                            rs.getDouble("buyCost"),
                            rs.getDouble("sellPrice")
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
        return product;
    }

    /**
     * Update Product Quantity
     * @param product Product
     * @param quantity Quantity
     */
    @Override
    public void updateQuantity(Product product, double quantity){
        con=MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("UPDATE product SET quantity=? WHERE id=?");
                stm.setDouble(1, quantity);
                stm.setLong(2, product.getId());
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
     * Update Product Price
     * @param product Product
     * @param price Price
     */
    @Override
    public void updatePrice(Product product, double price){
        con=MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("UPDATE product SET sellPrice=? WHERE id=?");
                stm.setDouble(1, price);
                stm.setLong(2, product.getId());
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
     * Get Product Quantity
     * @param product Product
     * @return quantity
     */
    @Override
    public double getQuantity(Product product){
        double quantity=0;
        con=MysqlConnection.getInstance();
        if (con!=null) {
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("SELECT quantity FROM product WHERE id=?");
                stm.setLong(1, product.getId());
                ResultSet rs = stm.executeQuery();
                if (rs.next()) {
                    quantity = rs.getDouble("quantity");
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
        return quantity;
    }

    /**
     * Check if the product is linked to an order
     * @param product Product
     * @return true when linked
     */
    public boolean isUsed(Product product){
        boolean toReturn=false;
        con=MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("SELECT * FROM item WHERE prodId=?");
                stm.setLong(1,product.getId());
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
     *Check if the Product Already Exist
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
                stm = con.prepareStatement("SELECT * FROM product WHERE name=?");
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
