package com.inv.xflux.entity;

import com.inv.data.access.emums.OrderStatus;
import com.inv.xflux.entity.in.IOrder;
import javafx.collections.ObservableList;

import java.sql.Date;

/**
 * @author XDSSWAR
 */
public final class Order implements IOrder {
    private  long id;
    private Client client;
    private OrderStatus status;
    private ObservableList<Item> items;
    private double total;
    private Tax tax;
    private double totalWithTax;
    private double paidMoney;
    private Employee orderCreator;
    private Date date;
    private Date dueDate;

    /**
     * Order Constructor to get Orders from Db
     * @param id id
     * @param client Client
     * @param status OrderStatus Enum  Paid , Unpaid , Fully Paid
     * @param items List with all Products
     * @param total Total
     * @param tax Tax %
     * @param totalWithTax Total after Tax
     * @param orderCreator Employee Creator
     * @param date Creation date
     * @param dueDate Date when it was paid
     */
    public Order(long id, Client client, OrderStatus status, ObservableList<Item> items,
                 double total, Tax tax,double totalWithTax, double paidMoney, Employee orderCreator,Date date,Date dueDate) {
        this.id = id;
        this.client = client;
        this.status = status;
        this.items = items;
        this.total = total;
        this.tax = tax;
        this.totalWithTax=totalWithTax;
        this.paidMoney=paidMoney;
        this.orderCreator = orderCreator;
        this.date=date;
        this.dueDate=dueDate;
    }
    /**
     * Order Constructor to create Orders
     * @param client Client
     * @param status EmployeeStatus  Paid , Unpaid , Fully Paid
     * @param items List with all Products
     * @param total Total
     * @param tax Tax %
     * @param totalWithTax Total after Tax
     * @param orderCreator Employee Creator
     * @param date Creation date
     */
    public Order(Client client, OrderStatus status, ObservableList<Item> items,
                 double total, Tax tax,double totalWithTax,double paidMoney, Employee orderCreator,Date date) {
        this.client = client;
        this.status = status;
        this.items = items;
        this.total = total;
        this.tax = tax;
        this.totalWithTax=totalWithTax;
        this.paidMoney=paidMoney;
        this.orderCreator = orderCreator;
        this.date=date;
    }

    @Override
    public long getId() {
        return id;
    }
    @Override
    public Client getClient() {
        return client;
    }
    @Override
    public OrderStatus getStatus() {
        return status;
    }
    @Override
    public ObservableList<Item> getItems() {
        return items;
    }
    @Override
    public double getTotal() {
        return total;
    }
    @Override
    public Tax getTax() {
        return tax;
    }
    @Override
    public double getTotalWithTax() {
        return totalWithTax;
    }
    @Override
    public double getPaidMoney(){
        return this.paidMoney;
    }
    @Override
    public Employee getCreator() {
        return orderCreator;
    }
    @Override
    public Date getDate() {
        return this.date;
    }
    @Override
    public Date getDueDate() {
        return this.dueDate;
    }
}
