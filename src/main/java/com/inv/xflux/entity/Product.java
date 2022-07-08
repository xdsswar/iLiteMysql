package com.inv.xflux.entity;

import com.inv.xflux.entity.abs.Listable;
import com.inv.xflux.entity.in.IProduct;

/**
 * @author XDSSWAR
 */
public final class Product extends Listable implements IProduct {
    private long id;
    private String name;
    private String description;
    private Category category;
    private Provider provider;
    private Brand brand;
    private Unit unit;
    private double quantity;
    private double buyCost;
    private double sellPrice;

    /**
     * Product Constructor
     * @param id Id
     * @param name Name
     * @param description Description
     * @param category Category
     * @param provider Provider
     * @param brand Brand
     * @param unit Unit
     * @param quantity Quantity
     * @param buyCost Buy Cost
     * @param sellPrice Sell Price
     */
    public Product(long id, String name, String description, Category category,
                   Provider provider, Brand brand, Unit unit, double quantity,
                   double buyCost, double sellPrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.provider = provider;
        this.brand = brand;
        this.unit=unit;
        this.quantity = quantity;
        this.buyCost = buyCost;
        this.sellPrice = sellPrice;
    }

    /**
     * Product Constructor
     * @param name Name
     * @param description Description
     * @param category Category
     * @param provider Provider
     * @param brand Brand
     * @param unit Unit
     * @param quantity Quantity
     * @param buyCost Buy Cost
     * @param sellPrice Sell Price
     */
    public Product(String name, String description, Category category,
                   Provider provider, Brand brand,Unit unit, double quantity,
                   double buyCost, double sellPrice) {

        this.name = name;
        this.description = description;
        this.category = category;
        this.provider = provider;
        this.brand = brand;
        this.unit=unit;
        this.quantity = quantity;
        this.buyCost = buyCost;
        this.sellPrice = sellPrice;
    }

    @Override
    public long getId() {
        return id;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getDescription() {
        return description;
    }
    @Override
    public Category getCategory() {
        return category;
    }
    @Override
    public Provider getProvider() {
        return provider;
    }
    @Override
    public Brand getBrand() {
        return brand;
    }
    @Override
    public Unit getUnit(){return this.unit;}
    @Override
    public double getQuantity() {
        return quantity;
    }
    @Override
    public double getBuyCost() {
        return buyCost;
    }
    @Override
    public double getSellPrice() {
        return sellPrice;
    }
}
