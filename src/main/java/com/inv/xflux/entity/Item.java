package com.inv.xflux.entity;

import com.inv.xflux.entity.in.IItem;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.Objects;
import java.util.Random;

/**
 * @author XDSSWAR
 */
public final class Item implements IItem {
    private long id;
    private long orderId;
    private Product product;
    private DoubleProperty quantity=new SimpleDoubleProperty();
    private DoubleProperty discount=new SimpleDoubleProperty();
    private DoubleProperty total=new SimpleDoubleProperty();
    private final int value;

    /**
     * Items Constructor to get Items from Db
     * @param id id
     * @param orderId Order Id
     * @param product Product
     * @param quantity Quantity
     * @param discount Discount
     * @param total Total
     */
    public Item(long id, long orderId, Product product, double quantity, double discount, double total) {
        this.id = id;
        this.orderId = orderId;
        this.product = product;
        this.quantity.set(quantity);
        this.discount.set(discount);
        this.total.set(total);
        this.value=gen();
    }
    /**
     * Items Constructor used to insert into db
     * NOTE: Before inserting we need to call setItemOrderId(long orderId) to set
     * the orderId
     * @param product Product
     * @param quantity Quantity
     * @param discount Discount
     */
    public Item(Product product, double quantity, double discount) {
        this.product = product;
        this.quantity.set(quantity);
        this.discount.set(discount);
        this.total.set(quantity*product.getSellPrice()-discount);
        this.value=gen();
    }

    /**
     * Set Item's orderId.
     * This method will be used only to set the items order id
     * after the current item's order is inserted and before
     * inserting the items
     * @param orderId order id
     */
    public void setItemOrderId(long orderId){
        this.orderId=orderId;
    }
    @Override
    public long getId() {
        return id;
    }
    @Override
    public long getOrderId() {
        return orderId;
    }
    @Override
    public Product getProduct() {
        return product;
    }
    @Override
    public DoubleProperty getQuantity() {
        return quantity;
    }
    @Override
    public DoubleProperty getDiscount() {
        return discount;
    }
    @Override
    public DoubleProperty getTotal() {
        return total;
    }

    public void setQuantity(double quantity) {
        this.quantity.set(quantity);
    }

    public void setTotal(double total) {
        this.total.set(total);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id && orderId == item.orderId && value == item.value && Objects.equals(product, item.product) && Objects.equals(quantity, item.quantity) && Objects.equals(discount, item.discount) && Objects.equals(total, item.total);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderId, product, quantity, discount, total, value);
    }

    /**
     * Generate Random numbers
     * @return random values
     */
    private int gen(){
        return new Random().nextInt(1000000);
    }

}
