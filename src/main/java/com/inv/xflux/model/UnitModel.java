package com.inv.xflux.model;

import com.inv.xflux.entity.Unit;
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
public final class UnitModel implements IModel<Unit> {
    private Connection con;
    /**
     * Inset new Unit
     * @param unit Unit
     */
    @Override
    public void insert(Unit unit){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                    PreparedStatement statement = con.prepareStatement("INSERT INTO unit(name, symbol, description) " +
                            "VALUES (?,?,?)");
                    statement.setString(1, unit.getName());
                    statement.setString(2, unit.getSymbol());
                    statement.setString(3, unit.getDescription());
                    statement.executeUpdate();

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
     * Delete Unit
     * @param unit Unit
     */
    @Override
    public void delete(Unit unit){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("DELETE  FROM unit WHERE id=?");
                stm.setLong(1, unit.getId());
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
     *  Update Unit
     * @param oldUnit old Unit
     * @param newUnit new Unit
     */
    @Override
    public void update(Unit oldUnit, Unit newUnit){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("UPDATE unit SET name=?," +
                        "symbol=?, description=? WHERE id=?");
                stm.setString(1, newUnit.getName());
                stm.setString(2, newUnit.getSymbol());
                stm.setString(3, newUnit.getDescription());
                stm.setLong(4, oldUnit.getId());
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
     * Get All Units
     * @return Unit List
     */
    @Override
    public ObservableList<Unit> getAll(){
        ObservableList<Unit> list= FXCollections.observableArrayList();
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM unit");
                rs = stm.executeQuery();
                while (rs.next()) {
                    list.add(
                            new Unit(
                                    rs.getLong("id"),
                                    rs.getString("name"),
                                    rs.getString("symbol"),
                                    rs.getString("description")
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
     * Get Unit by Id
     * @param id Id
     * @return Unit
     */
    @Override
    public Unit get(long id){
        Unit unit=null;
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm= con.prepareStatement("SELECT * FROM unit WHERE id=?");
                stm.setLong(1, id);
                rs = stm.executeQuery();
                if (rs.next()) {
                    unit = new Unit(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("symbol"),
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
        return unit;
    }

    /**
     * Check if the Unit its Used
     * @param unit Unit
     * @return boolean true if used
     */
    public boolean isUsed(Unit unit){
        boolean toReturn=false;
        con=MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("SELECT * FROM product WHERE unitId=?");
                stm.setLong(1,unit.getId());
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

    /**
     * Check is the Unit already Exist
     * @param name name
     * @return true when already exist
     */
    public boolean alreadyExist(String name){
        boolean toReturn=false;
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm= con.prepareStatement("SELECT * FROM unit WHERE name=?");
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
