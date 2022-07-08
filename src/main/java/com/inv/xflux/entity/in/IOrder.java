package com.inv.xflux.entity.in;

import com.inv.data.access.emums.OrderStatus;
import com.inv.xflux.entity.Client;
import com.inv.xflux.entity.Employee;
import com.inv.xflux.entity.Item;
import com.inv.xflux.entity.Tax;
import javafx.collections.ObservableList;

import java.sql.Date;

/**
 * @author XDSSWAR
 */
public interface IOrder {
    long getId();
    Client getClient();
    OrderStatus getStatus();
    ObservableList<Item> getItems();
    double getTotal();
    Tax getTax();
    double getTotalWithTax();
    double getPaidMoney();
    Employee getCreator();
    Date getDate();
    Date getDueDate();
}
