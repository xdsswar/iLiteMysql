package com.inv.xflux.model;

import com.inv.data.access.MysqlConnection;
import com.inv.xflux.entity.Brand;
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
public final class BrandModel implements IModel<Brand> {
    private Connection con;

    /**
     * Insert new Brand
     * @param brand Brand
     */
    @Override
    public void insert(Brand brand){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                   stm = con.prepareStatement("INSERT INTO brand(name, description) " +
                            "VALUES (?,?)");
                    stm.setString(1, brand.getName());
                    stm.setString(2, brand.getDescription());
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
     * Delete  Brand
     * @param brand Brand
     */
    @Override
    public void delete(Brand brand){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("DELETE FROM brand WHERE id=?");
                stm.setLong(1, brand.getId());
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
     * Update Brand
     * @param oldBrand Old Brand
     * @param newBrand New Brand
     */
    @Override
    public void update(Brand oldBrand, Brand newBrand){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("UPDATE brand SET name=?,description=? " +
                        "WHERE id=?");
                stm.setString(1, newBrand.getName());
                stm.setString(2, newBrand.getDescription());
                stm.setLong(3, oldBrand.getId());
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
     * Get All Brands
     * @return Brand List
     */
    @Override
    public ObservableList<Brand> getAll(){
        ObservableList<Brand> list= FXCollections.observableArrayList();
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM brand");
                rs = stm.executeQuery();
                while (rs.next()) {
                    list.add(
                            new Brand(
                                    rs.getLong("id"),
                                    rs.getString("name"),
                                    rs.getString("description")
                            )
                    );
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
            finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
        return list;
    }

    /**
     * Check is the brand is already created
     * @param name name
     * @return true when the brand already exist
     */
    public boolean alreadyExist(String name){
        boolean toReturn=false;
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM brand WHERE name=?");
                stm.setString(1,name);
                rs = stm.executeQuery();
                if (rs.next()) {
                    toReturn=true;
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
            finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
        return toReturn;
    }

    /**
     * Get Brand by Id
     * @param id Id
     * @return Brand
     */
    @Override
    public Brand get(long id){
        Brand brand=null;
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM brand WHERE id=?");
                stm.setLong(1, id);
                rs = stm.executeQuery();
                if (rs.next()) {
                    brand = new Brand(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("description")
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
        return brand;
    }

    /**
     * Check if a Brand is used
     * @param brand Brand
     * @return true when use
     */
    public boolean isUsed(Brand brand){
        boolean toReturn=false;
        con=MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("SELECT * FROM product WHERE brandId=?");
                stm.setLong(1,brand.getId());
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
