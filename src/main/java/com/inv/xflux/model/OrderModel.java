package com.inv.xflux.model;

import com.inv.R;
import com.inv.data.access.MysqlConnection;
import com.inv.data.access.emums.OrderStatus;
import com.inv.ui.util.DateConverter;
import com.inv.xflux.entity.Employee;
import com.inv.xflux.entity.Item;
import com.inv.xflux.entity.Order;
import com.inv.xflux.model.in.IModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author XDSSWARR
 */
@SuppressWarnings("all")
public final class OrderModel implements IModel<Order> {
    private Connection con;
    private Map<String,String> MONTHS;

    public OrderModel(){
        MONTHS = new HashMap<>();
        MONTHS.put("January", "01");
        MONTHS.put("February", "02");
        MONTHS.put("March", "03");
        MONTHS.put("April", "04");
        MONTHS.put("May", "05");
        MONTHS.put("June", "06");
        MONTHS.put("July", "07");
        MONTHS.put("August", "08");
        MONTHS.put("September", "09");
        MONTHS.put("October", "10");
        MONTHS.put("November", "11");
        MONTHS.put("December", "12");
    }
    /**
     * Insert Order
     * @param order Order
     */
    @Override
    public void insert(Order order){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                if (order.getStatus()!=OrderStatus.PAID) {
                    stm = con.prepareStatement("INSERT INTO `order`(clientId, status, total, taxId,totalTax, paidMoney, empId) " +
                            "VALUES (?,?,?,?,?,?,?)");
                    stm.setLong(1, order.getClient().getId());
                    stm.setString(2, OrderStatus.getStatusToInsert(order.getStatus()));
                    stm.setDouble(3, order.getTotal());
                    stm.setDouble(4, order.getTax().getId());
                    stm.setDouble(5, order.getTotalWithTax());
                    stm.setDouble(6, order.getPaidMoney());
                    stm.setLong(7, order.getCreator().getId());
                }else {
                    stm = con.prepareStatement("INSERT INTO `order`(clientId, status, total, taxId, totalTax,paidMoney, empId,orderDueDate) " +
                            "VALUES (?,?,?,?,?,?,?,CURRENT_TIMESTAMP())");
                    stm.setLong(1, order.getClient().getId());
                    stm.setString(2, OrderStatus.getStatusToInsert(order.getStatus()));
                    stm.setDouble(3, order.getTotal());
                    stm.setDouble(4, order.getTax().getId());
                    stm.setDouble(5, order.getTotalWithTax());
                    stm.setDouble(6, order.getPaidMoney());
                    stm.setLong(7, order.getCreator().getId());
                }
                stm.executeUpdate();
                long orderId=getLastId();
                ItemModel model=new ItemModel();
                for (Item i:order.getItems()) {
                    i.setItemOrderId(orderId);
                    model.insert(i);
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
     * Delete Order
     * @param order Order
     */
    @Override
    public void delete(Order order){
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            try {
                stm = con.prepareStatement("DELETE FROM `order` WHERE id=?");
                stm.setLong(1, order.getId());
                stm.executeUpdate();
                new ItemModel().delete(order.getId());
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);  
            }
        }
    }

    /**
     * Update Order
     * @param oldOrder Old order
     * @param newOrder new Order
     */
    @Override
    public void update(Order oldOrder, Order newOrder) {
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ItemModel itemModel=new ItemModel();
            try {
                //Dlete Old Items in the Order
                for(Item i:oldOrder.getItems()){
                    itemModel.delete(i);
                }
                if (newOrder.getStatus()==OrderStatus.PAID){
                    stm = con.prepareStatement("UPDATE `order` SET clientId=?,status=?,total=?," +
                            " taxId=?,totalTax=? , paidMoney=? , orderDueDate=CURRENT_TIMESTAMP() WHERE id=?");
                    stm.setLong(1, newOrder.getClient().getId());
                    stm.setString(2, OrderStatus.getStatusToInsert(newOrder.getStatus()));
                    stm.setDouble(3, newOrder.getTotal());
                    stm.setDouble(4, newOrder.getTax().getId());
                    stm.setDouble(5, newOrder.getTotalWithTax());
                    stm.setDouble(6, newOrder.getPaidMoney());
                    stm.setLong(7, oldOrder.getId());
                }else {
                    stm = con.prepareStatement("UPDATE `order` SET clientId=?,status=?,total=?," +
                            " taxId=?,totalTax=? , paidMoney=? WHERE id=?");
                    stm.setLong(1, newOrder.getClient().getId());
                    stm.setString(2, OrderStatus.getStatusToInsert(newOrder.getStatus()));
                    stm.setDouble(3, newOrder.getTotal());
                    stm.setDouble(4, newOrder.getTax().getId());
                    stm.setDouble(5, newOrder.getTotalWithTax());
                    stm.setDouble(6, newOrder.getPaidMoney());
                    stm.setLong(7, oldOrder.getId());
                }

                stm.executeUpdate();

                //Insert The Items for the Update
                for (Item i:newOrder.getItems()) {
                    i.setItemOrderId(oldOrder.getId());
                    itemModel.insert(i);
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
     * Get All Orders
     * @return Order List
     */
    @Override
    public ObservableList<Order> getAll(){
        ObservableList<Order>list= FXCollections.observableArrayList();
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM `order` ORDER BY id DESC ");
                rs = stm.executeQuery();
                while (rs.next()) {
                    list.add(new Order(
                            rs.getLong("id"),
                            new ClientModel().get(rs.getLong("clientId")),
                            OrderStatus.get(rs.getString("status")),
                            new ItemModel().getAllByOrderId(rs.getLong("id")),
                            rs.getDouble("total"),
                            new TaxModel().get(rs.getLong("taxId")),
                            rs.getDouble("totalTax"),
                            rs.getDouble("paidMoney"),
                            new EmployeeModel().get(rs.getLong("empId")),
                            rs.getDate("orderDate"),
                            rs.getDate("orderDueDate")
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
     * Get Last 10 Orders
     * @return list
     */
    public ObservableList<Order> getLast10(int limit){
        ObservableList<Order>list= FXCollections.observableArrayList();
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM `order` ORDER BY id DESC LIMIT ?");
                stm.setInt(1,limit);
                rs = stm.executeQuery();
                while (rs.next()) {
                    list.add(new Order(
                            rs.getLong("id"),
                            new ClientModel().get(rs.getLong("clientId")),
                            OrderStatus.get(rs.getString("status")),
                            new ItemModel().getAllByOrderId(rs.getLong("id")),
                            rs.getDouble("total"),
                            new TaxModel().get(rs.getLong("taxId")),
                            rs.getDouble("totalTax"),
                            rs.getDouble("paidMoney"),
                            new EmployeeModel().get(rs.getLong("empId")),
                            rs.getDate("orderDate"),
                            rs.getDate("orderDueDate")
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
     * Get all Orders by EmployeeStatus
     * @param status OrderStatus
     * @return list
     */
    public ObservableList<Order> getAllByStatus(OrderStatus status){
        ObservableList<Order>list= FXCollections.observableArrayList();
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM `order` WHERE status=? ORDER BY id DESC ");
                stm.setString(1,OrderStatus.getStatusToInsert(status));
                rs = stm.executeQuery();
                while (rs.next()) {
                    list.add(new Order(
                            rs.getLong("id"),
                            new ClientModel().get(rs.getLong("clientId")),
                            OrderStatus.get(rs.getString("status")),
                            new ItemModel().getAllByOrderId(rs.getLong("id")),
                            rs.getDouble("total"),
                            new TaxModel().get(rs.getLong("taxId")),
                            rs.getDouble("totalTax"),
                            rs.getDouble("paidMoney"),
                            new EmployeeModel().get(rs.getLong("empId")),
                            rs.getDate("orderDate"),
                            rs.getDate("orderDueDate")
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
     * Get All orders by Employee
     * @param employee Employee
     * @return list
     */
    public ObservableList<Order> getAllByEmployee(Employee employee){
        ObservableList<Order>list= FXCollections.observableArrayList();
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM `order` WHERE empId=?");
                stm.setLong(1,employee.getId());
                rs = stm.executeQuery();
                while (rs.next()) {
                    list.add(new Order(
                            rs.getLong("id"),
                            new ClientModel().get(rs.getLong("clientId")),
                            OrderStatus.get(rs.getString("status")),
                            new ItemModel().getAllByOrderId(rs.getLong("id")),
                            rs.getDouble("total"),
                            new TaxModel().get(rs.getLong("taxId")),
                            rs.getDouble("totalTax"),
                            rs.getDouble("paidMoney"),
                            new EmployeeModel().get(rs.getLong("empId")),
                            rs.getDate("orderDate"),
                            rs.getDate("orderDueDate")
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
     * Get Order lis by Date
     * @param year str year
     * @return list
     */
    public ObservableList<Order> getAllByYear(String year){
        ObservableList<Order> list=FXCollections.observableArrayList();
        con=MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("SELECT * FROM `order` WHERE orderDate LIKE ? ORDER BY orderDate ASC ");
                stm.setString(1,"%"+year+"%");
                rs=stm.executeQuery();
                while (rs.next()){
                    list.add(new Order(
                            rs.getLong("id"),
                            new ClientModel().get(rs.getLong("clientId")),
                            OrderStatus.get(rs.getString("status")),
                            new ItemModel().getAllByOrderId(rs.getLong("id")),
                            rs.getDouble("total"),
                            new TaxModel().get(rs.getLong("taxId")),
                            rs.getDouble("totalTax"),
                            rs.getDouble("paidMoney"),
                            new EmployeeModel().get(rs.getLong("empId")),
                            rs.getDate("orderDate"),
                            rs.getDate("orderDueDate")
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
     * Get Order Quantity by year
     * @param year year
     * @return long
     */
    public long getOrderQuantityByYear(String year){
        con=MysqlConnection.getInstance();
        long toReturn=0;
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("SELECT * FROM `order` WHERE orderDate LIKE ? ORDER BY orderDate ASC ");
                stm.setString(1,"%"+year+"%");
                rs=stm.executeQuery();
                while (rs.next()){
                  toReturn++;
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
     * Get Orders Quantity by Month
     * @param month Month , example: January
     * @return
     */
    public long getOrderQuantityByMonth(String month){
        con=MysqlConnection.getInstance();
        long toReturn=0;
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("SELECT * FROM `order` WHERE orderDate LIKE ? ORDER BY orderDate ASC ");
                stm.setString(1,"%"+MONTHS.get(month)+"%");
                rs=stm.executeQuery();
                while (rs.next()){
                    toReturn++;
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

    /***
     * Get orders Quantity by month
     * @param monthName month
     * @return long
     */
    public long getOrderQuantityByMonth(String monthName,String year){
        String month= MONTHS.get(monthName);
        con=MysqlConnection.getInstance();
        long toReturn=0;
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("SELECT * FROM `order` WHERE orderDate LIKE ? ORDER BY orderDate ASC ");
                stm.setString(1,"%"+year+"-"+month+"-%");
                rs=stm.executeQuery();
                while (rs.next()){
                    toReturn++;
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
     * Get All Revenue by year
     * @param year year
     * @return double
     */
    public double getOrderAmountByYear(String year){
        con=MysqlConnection.getInstance();
        double toReturn=0;
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("SELECT total FROM `order` WHERE orderDate LIKE ? ORDER BY orderDate ASC ");
                stm.setString(1,"%"+year+"%");
                rs=stm.executeQuery();
                while (rs.next()){
                    toReturn=toReturn+rs.getDouble("total");
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
     * Get total revenue by month with tax
     * @param monthName
     * @return double amount
     */
    public double getOrderAmountWithTaxByMonth(String monthName,String year){
        String month= MONTHS.get(monthName);
        con=MysqlConnection.getInstance();
        double toReturn=0;
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("SELECT totalTax FROM `order` WHERE orderDate LIKE ? ORDER BY orderDate ASC ");
                stm.setString(1,"%"+year+"-"+month+"-%");
                rs=stm.executeQuery();
                while (rs.next()){
                   toReturn=toReturn+rs.getDouble("totalTax");
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
     * Get Revenue by month without tax
     * @param monthName month
     * @return revenue
     */
    public double getOrderAmountByMonth(String monthName,String year, OrderStatus status){
        String month= MONTHS.get(monthName);
        con=MysqlConnection.getInstance();
        double toReturn=0;
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("SELECT total FROM `order` WHERE orderDate LIKE ? AND status=? ORDER BY id DESC ");
                stm.setString(1,"%"+year+"-"+month+"-%");
                stm.setString(2,OrderStatus.getStatusToInsert(status));
                rs=stm.executeQuery();
                while (rs.next()){
                    toReturn=toReturn+rs.getDouble("total");
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
     * Get Order Amount by year and Order Status
     * @param year year
     * @param status Status
     * @return double Amount
     */
    public double getOrderAmountByYearAndStatus(String year, OrderStatus status){
        con=MysqlConnection.getInstance();
        double toReturn=0;
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=con.prepareStatement("SELECT total FROM `order` WHERE orderDate LIKE ? AND status=? ORDER BY id DESC ");
                stm.setString(1,"%"+year+"%");
                stm.setString(2,OrderStatus.getStatusToInsert(status));
                rs=stm.executeQuery();
                while (rs.next()){
                    toReturn=toReturn+rs.getDouble("total");
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
     * Get Order by  Id
     * @param id Id
     * @return Order
     */

    public Order get(long id){
        Order order=null;
        con= MysqlConnection.getInstance();
        if (con!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm = con.prepareStatement("SELECT * FROM `order` WHERE id=?");
                stm.setLong(1, id);
                rs = stm.executeQuery();
                if (rs.next()) {
                    order = new Order(
                            rs.getLong("id"),
                            new ClientModel().get(rs.getLong("clientId")),
                            OrderStatus.get(rs.getString("status")),
                            new ItemModel().getAllByOrderId(id),
                            rs.getDouble("total"),
                            new TaxModel().get(rs.getLong("taxId")),
                            rs.getDouble("totalTax"),
                            rs.getDouble("paidMoney"),
                            new EmployeeModel().get(rs.getLong("empId")),
                            rs.getDate("orderDate"),
                            rs.getDate("orderDueDate")
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
        return order;
    }


    /**
     * Get All years from orders
     * @return
     */
    public static List<String> getOrderYears(){
        List<String> years=FXCollections.observableArrayList();
        Connection connection=MysqlConnection.getInstance();
        if (connection!=null){
            PreparedStatement stm=null;
            ResultSet rs=null;
            try {
                stm=connection.prepareStatement("SELECT  orderDate FROM  `order`");
                rs=stm.executeQuery();
                while (rs.next()){
                    years.add(DateConverter.getYear(rs.getString("orderDate")));
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(connection);
            }
        }
        years=years.stream().distinct().collect(Collectors.toList());
        return years;
    }

    /**
     * Change Order Status and fix the paydMoney
     * @param order order
     * @param status String OrderStatus '0','1','2','3'
     */
    public boolean setStatus(Order order, OrderStatus status){
        boolean toRet=false;
        con =MysqlConnection.getInstance();
        if (con!=null) {
            PreparedStatement stm = null;
            if (order.getStatus()!=status) {
                try {
                    if (status==OrderStatus.PAID){
                        stm = con.prepareStatement("UPDATE `order` SET status=? ,paidMoney=? , orderDueDate=CURRENT_TIMESTAMP() WHERE id=?");
                        stm.setString(1, OrderStatus.getStatusToInsert(status));
                        stm.setDouble(2,order.getTotalWithTax());
                        stm.setLong(3, order.getId());
                    }else {
                        stm = con.prepareStatement("UPDATE `order` SET status=? , orderDueDate=NULL WHERE id=?");
                        stm.setString(1, OrderStatus.getStatusToInsert(status));
                        stm.setLong(2, order.getId());
                    }
                    stm.executeUpdate();
                    toRet=true;
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    DbUtils.closeQuietly(stm);
                    DbUtils.closeQuietly(con);  
                }
            }
        }
        return toRet;
    }

    /**
     * Helper to get las inserted id
     * @return long last id
     */
    public long getLastId(){
        long id=0;
        con=MysqlConnection.getInstance();
        if (con!=null){
            ResultSet rs=null;
            PreparedStatement stm=null;
            try {
                stm=con.prepareStatement("SELECT * FROM `order` ORDER BY id DESC LIMIT 1");
                rs=stm.executeQuery();
                if (rs.next()){
                    id=rs.getLong("id");
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(stm);
                DbUtils.closeQuietly(con);  
            }
        }

        return id;
    }

}
