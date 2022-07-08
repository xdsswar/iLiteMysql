package com.inv.xflux.model;

import com.inv.data.access.MysqlConnection;
import com.inv.xflux.entity.Category;
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
public final class CategoryModel implements IModel<Category> {
    private Connection con;
    private ProductModel model;

    /**
     * Insert new Category
     *
     * @param category Category
     */
    @Override
    public void insert(Category category) {
        con = MysqlConnection.getInstance();
        if (con != null) {
            PreparedStatement stm = null;
            try {
                stm = con.prepareStatement("INSERT INTO category(name, description) " +
                        "VALUES (?,?)");
                stm.setString(1, category.getName());
                stm.setString(2, category.getDescription());
                stm.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
    }

    /**
     * Delete Category
     *
     * @param category Category
     */
    @Override
    public void delete(Category category) {
        con = MysqlConnection.getInstance();
        if (con != null) {
            PreparedStatement stm = null;
            try {
                stm = con.prepareStatement("DELETE  FROM category WHERE id=?");
                stm.setLong(1, category.getId());
                stm.executeUpdate();
                model = new ProductModel();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
    }

    /**
     * Update Category
     *
     * @param category    Old Category
     * @param newCategory new Category
     */
    @Override
    public void update(Category category, Category newCategory) {
        con = MysqlConnection.getInstance();
        if (con != null) {
            PreparedStatement stm = null;
            try {
                stm = con.prepareStatement("UPDATE category SET name=?,description=? " +
                        "WHERE id=?");
                stm.setString(1, newCategory.getName());
                stm.setString(2, newCategory.getDescription());
                stm.setLong(3, category.getId());
                stm.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
    }

    /**
     * Get all Categories
     *
     * @return List
     */
    @Override
    public ObservableList<Category> getAll() {
        ObservableList<Category> list = FXCollections.observableArrayList();
        con = MysqlConnection.getInstance();
        if (con != null) {
            PreparedStatement stm = null;
            ResultSet rs = null;
            try {
                stm = con.prepareStatement("SELECT * FROM category");
                rs = stm.executeQuery();
                while (rs.next()) {
                    list.add(
                            new Category(
                                    rs.getLong("id"),
                                    rs.getString("name"),
                                    rs.getString("description")
                            )
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
        return list;
    }

    /**
     * Get Category by Id
     *
     * @param id Id
     * @return Category
     */
    @Override
    public Category get(long id) {
        Category category = null;
        con = MysqlConnection.getInstance();
        if (con != null) {
            PreparedStatement stm = null;
            ResultSet rs = null;
            try {
                stm = con.prepareStatement("SELECT * FROM category WHERE id=?");
                stm.setLong(1, id);
                rs = stm.executeQuery();
                if (rs.next()) {
                    category = new Category(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("description")
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
        return category;
    }

    /**
     * Check if The Category is used
     *
     * @param category Category
     * @return true when used
     */
    public boolean isUsed(Category category) {
        boolean toReturn = false;
        con = MysqlConnection.getInstance();
        if (con != null) {
            PreparedStatement stm = null;
            ResultSet rs = null;
            try {
                stm = con.prepareStatement("SELECT * FROM product WHERE catId=?");
                stm.setLong(1, category.getId());
                rs = stm.executeQuery();
                if (rs.next()) {
                    toReturn = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
        return toReturn;
    }


    /**
     * Check if the Category Already exist
     *
     * @param name name
     * @return true when already exist
     */
    public boolean alreadyExist(String name) {
        boolean toReturn = false;
        con = MysqlConnection.getInstance();
        if (con != null) {
            PreparedStatement stm = null;
            ResultSet rs = null;
            try {
                stm = con.prepareStatement("SELECT * FROM category WHERE name=?");
                stm.setString(1, name);
                rs = stm.executeQuery();
                if (rs.next()) {
                    toReturn = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
        return toReturn;
    }

}
