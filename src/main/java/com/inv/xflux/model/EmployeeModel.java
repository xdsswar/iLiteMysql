package com.inv.xflux.model;

import com.inv.data.access.MysqlConnection;
import com.inv.data.access.emums.EmployeeStatus;
import com.inv.data.access.emums.Role;
import com.inv.data.access.orderutil.Image64;
import com.inv.data.access.sec.Security;
import com.inv.xflux.entity.Employee;
import com.inv.xflux.model.in.IModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author XDSSWAR
 */
@SuppressWarnings("Duplicates")
public final class EmployeeModel implements IModel<Employee> {
    private Connection con;

    /**
     * Insert Employee into Database
     * @param employee New Employee
     */
    @Override
    public void insert(Employee employee){
        this.con= MysqlConnection.getInstance();
        if (this.con!=null) {
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("INSERT INTO employee(name,lastName,user,phone,email,address," +
                        "date, picture)  VALUES (?,?,?,?,?,?,?,?)");
                stm.setString(1, employee.getName());
                stm.setString(2, employee.getLastName());
                stm.setString(3, employee.getUser());
                stm.setString(4, employee.getPhone());
                stm.setString(5, employee.getEmail());
                stm.setString(6, employee.getAddress());
                stm.setString(7, employee.getBirthDate());
                stm.setString(8, Image64.encodeImage(employee.getPicture()));
                stm.executeUpdate();
            }catch (SQLException e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
    }

    /**
     * Delete Employee from Db
     * @param employee Employee
     */
    @Override
    public void delete(Employee employee) {
        this.con= MysqlConnection.getInstance();
        if (this.con!=null) {
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("DELETE  FROM employee WHERE id=?");
                stm.setLong(1, employee.getId());
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
     * Update Employee
     * @param oldEmp Employee to be Updated
     * @param newEmployee Employee Containing new Information
     */
    @Override
    public void update(Employee oldEmp, Employee newEmployee) {
        this.con=MysqlConnection.getInstance();
        if (this.con!=null) {
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("UPDATE employee SET name=?, lastName=?," +
                        "user=?, phone=?, email=?, address=?, date=? WHERE id=?");
                stm.setString(1, newEmployee.getName());
                stm.setString(2, newEmployee.getLastName());
                stm.setString(3, newEmployee.getUser());
                stm.setString(4, newEmployee.getPhone());
                stm.setString(5, newEmployee.getEmail());
                stm.setString(6, newEmployee.getAddress());
                stm.setString(7,newEmployee.getBirthDate());
                stm.setLong(8, oldEmp.getId());
                stm.executeUpdate();
            }catch (SQLException e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
    }

    /**
     * Check is the username is already taked
     * @param userName userName
     * @return true when is taked
     */
    public boolean isUserTaked(String userName){
        boolean toReturn=false;
        this.con=MysqlConnection.getInstance();
        if (this.con!=null) {
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM employee WHERE user=?");
                stm.setString(1, userName);
                rs = stm.executeQuery();
                if (rs.next()){
                    toReturn=true;
                }
            }catch (SQLException e){
                System.out.println(e.getMessage());
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
     * Update Picture
     * @param emp Employee
     * @param image new Image
     * @return true when done
     */
    public boolean updatePicture(Employee emp, Image  image){
        boolean toReturn=false;
        this.con=MysqlConnection.getInstance();
        if (this.con!=null) {
            PreparedStatement stm = null;
            try {
                stm=con.prepareStatement("UPDATE employee SET picture=? WHERE id=?");
                stm.setString(1,Image64.encodeImage(image));
                stm.setLong(2,emp.getId());
                stm.executeUpdate();
                toReturn=true;
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
        return toReturn;
    }

    /**
     * Get All Employees
     * @return List
     */
    @Override
    public ObservableList<Employee> getAll(){
        ObservableList<Employee> list= FXCollections.observableArrayList();
        this.con=MysqlConnection.getInstance();
        if (this.con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM employee");
                rs = stm.executeQuery();
                while (rs.next()) {
                    list.add(
                            new Employee(
                                    rs.getLong("id"),
                                    rs.getString("name"),
                                    rs.getString("lastName"),
                                    rs.getString("user"),
                                    rs.getString("phone"),
                                    rs.getString("email"),
                                    rs.getString("address"),
                                    rs.getString("date"),
                                    EmployeeStatus.get(rs.getString("status")),
                                    Role.get(rs.getString("role")),
                                    Image64.decodeImage(rs.getString("picture"))
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
     * Get Employee by Id
     * @param id Id
     * @return Employee
     */
    @Override
    public Employee get(long id){
        this.con=MysqlConnection.getInstance();
        Employee employee=null;
        if (this.con!=null) {
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM employee WHERE id=?");
                stm.setLong(1, id);
                rs = stm.executeQuery();
                if (rs.next()) {
                    employee = new Employee(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("lastName"),
                            rs.getString("user"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getString("address"),
                            rs.getString("date"),
                            EmployeeStatus.get(rs.getString("status")),
                            Role.get(rs.getString("role")),
                            Image64.decodeImage(rs.getString("picture"))
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
        return employee;
    }

    /**
     * Set Employee Role
     * @param employee Employee
     * @param role Role
     */
    public void setRole(Employee employee, Role role){
        this.con= MysqlConnection.getInstance();
        if (this.con!=null) {
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("UPDATE employee SET role=? WHERE id=?");
                stm.setString(1, Role.getRoleAsString(role));
                stm.setLong(2, employee.getId());
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
     * Set Employee EmployeeStatus
     * @param employee Employee
     * @param status EmployeeStatus
     * @return true when Done
     */
    public boolean setStatus(Employee employee, EmployeeStatus status){
        boolean toReturn=false;
        this.con= MysqlConnection.getInstance();
        if (this.con!=null) {
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("UPDATE employee SET status=? WHERE id=?");
                stm.setString(1, EmployeeStatus.getStatusAsString(status));
                stm.setLong(2, employee.getId());
                stm.executeUpdate();
                toReturn=true;
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
        return toReturn;
    }

    /**
     * Set Employee Password
     * @param employee Employee
     * @param password Password
     */
    public void setPassword(Employee employee, String password){
        this.con= MysqlConnection.getInstance();
        if (this.con!=null) {
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("UPDATE employee SET password=? WHERE id=?");
                stm.setString(1, Security.encryptPassword(password));
                stm.setLong(2, employee.getId());
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
     * Check User For Login
     * @param user Employee User
     * @param password Password
     * @return true if Exits
     */
    public boolean checkEmployee(String user, String password){
        this.con= MysqlConnection.getInstance();
        boolean toReturn=false;
        if (this.con!=null) {
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM employee WHERE user=? AND password=?");
                stm.setString(1, user);
                stm.setString(2, Security.encryptPassword(password));
                rs = stm.executeQuery();
                if (rs.next()) {
                    toReturn = true;
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
     * Get Employee Role by User
     * @param user User
     * @return Role
     */
    public Role getRoleByUser(String user){
        this.con=MysqlConnection.getInstance();
        Role roleToReturn=null;
        if (this.con!=null) {
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM employee WHERE user=?");
                stm.setString(1, user);
                rs = stm.executeQuery();
                if (rs.next()) {
                    roleToReturn=Role.get(rs.getString("role"));
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);
            }
        }
        return roleToReturn;
    }

    /**
     * Get Employee by Id
     * @param user username
     * @return Employee
     */
    public Employee getByUser(String user){
        this.con=MysqlConnection.getInstance();
        Employee employee=null;
        if (this.con!=null) {
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM employee WHERE user=?");
                stm.setString(1, user);
                rs = stm.executeQuery();
                if (rs.next()) {
                    employee = new Employee(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("lastName"),
                            rs.getString("user"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getString("address"),
                            rs.getString("date"),
                            EmployeeStatus.get(rs.getString("status")),
                            Role.get(rs.getString("role")),
                            Image64.decodeImage(rs.getString("picture"))
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
        return employee;
    }

    /**
     * Check if the Employee has Data  associated
     * @param employee Employee
     * @return true when has data
     */
    public boolean hasOrders(Employee employee){
        boolean toReturn=false;
        con=MysqlConnection.getInstance();
        if (this.con!=null) {
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM `order` WHERE empId=?");
                stm.setLong(1,employee.getId());
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
