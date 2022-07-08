package com.inv.xflux.model;

import com.inv.data.access.MysqlConnection;
import com.inv.xflux.entity.Company;
import com.inv.xflux.entity.in.IDefault;
import com.inv.xflux.model.in.IDefaultable;
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
public final class CompanyModel implements IModel<Company> , IDefaultable {
    private  Connection con=null;

    /**
     * Insert new Company in Db
     * @param company Company
     */
    @Override
    public void insert(Company company) {
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
               stm=con.prepareStatement("INSERT INTO company(name, address, phone, fax, email) "+
                       "VALUES (?,?,?,?,?)");
               stm.setString(1,company.getName());
               stm.setString(2,company.getAddress());
               stm.setString(3,company.getPhone());
               stm.setString(4,company.getFax());
               stm.setString(5,company.getEmail());
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
     * Delete Company from Db, This is very Important Method.
     * Before we need to check if the company is default , if default then check
     * how many companies are available , if there is more that one is ok , just
     * delete the selected company and set the first one as default, but you can't
     * delete the company if it is the only one available.If the company is not
     * default , that mean there is more that one company available , so just delete it .
     * @param company Selected Company to delete
     */
    @Override
    public void delete(Company company) {
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("DELETE FROM company WHERE id=?");
                stm.setLong(1, company.getId());
                if (company.isDefault()) {
                    if (getAll().size() > 1) {
                        stm.executeUpdate();
                        setDefault(getAll().get(0));
                    }
                }else{
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
     * Update Company
     * @param oldVal old Company
     * @param newVal new Company
     */
    @Override
    public void update(Company oldVal, Company newVal) {
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm=con.prepareStatement("UPDATE company SET name=?, address=?,phone=?, fax=? ,email=? WHERE id=?");
                stm.setString(1,newVal.getName());
                stm.setString(2,newVal.getAddress());
                stm.setString(3,newVal.getPhone());
                stm.setString(4,newVal.getFax());
                stm.setString(5,newVal.getEmail());
                stm.setLong(6,oldVal.getId());
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
     * Get All Companies
     * @return Company List
     */
    @Override
    public ObservableList<Company> getAll() {
        ObservableList<Company> list= FXCollections.observableArrayList();
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("SELECT * FROM company");
                rs=stm.executeQuery();
                while (rs.next()){
                    list.add(new Company(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("phone"),
                            rs.getString("fax"),
                            rs.getString("email"),
                            rs.getInt("status"))
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
     * Get Company by id
     * @param id id
     * @return Company
     */
    @Override
    public Company get(long id) {
        con= MysqlConnection.getInstance();
        Company company=null;
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("SELECT * FROM company WHERE id=?");
                stm.setLong(1,id);
                rs=stm.executeQuery();
                if (rs.next()){
                    company=new Company(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("phone"),
                            rs.getString("fax"),
                            rs.getString("email"),
                            rs.getInt("status")
                    );
                }
            }catch (SQLException e){

            }finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
        return company;
    }

    /**
     * Set Company to default
     * @param company Company
     * @param status EmployeeStatus true when active
     */
    private void changeStatus(Company company, boolean status){
        int newStatus=0;
        if (status) {
            newStatus = 1;
        }
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm=con.prepareStatement("UPDATE company SET status=? WHERE id=?");
                stm.setInt(1,newStatus);
                stm.setLong(2,company.getId());
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
     * Set Default Company
     * @param company company
     */
    @Override
    public void setDefault(IDefault company){
        for (Company c:getAll()){
            changeStatus(c,false);
        }
        changeStatus((Company) company,true);
    }

  /**
     * Get Default Company
     * @return Company
     */
    @Override
    public Company getDefault(){
        Company company=null;
        con=MysqlConnection.getInstance();
        if (con!=null){
            ResultSet rs=null;
            PreparedStatement stm=null;
            try {
                stm=con.prepareStatement("SELECT * FROM company WHERE status=?");
                stm.setInt(1,1);
                rs=stm.executeQuery();
                if (rs.next()){
                    company=new Company(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("phone"),
                            rs.getString("fax"),
                            rs.getString("email"),
                            rs.getInt("status")
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
        return company;
    }

}
