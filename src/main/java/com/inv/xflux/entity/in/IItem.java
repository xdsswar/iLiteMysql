package com.inv.xflux.entity.in;
import com.inv.xflux.entity.Product;
import javafx.beans.property.DoubleProperty;

/**
 * @author XDSSWAR
 */
public interface IItem {
    long getId();
    long getOrderId();
    Product getProduct();
    DoubleProperty getQuantity();
    DoubleProperty getDiscount();
    DoubleProperty getTotal();
}
