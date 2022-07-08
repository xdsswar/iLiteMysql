package com.inv.xflux.model.in;

import com.inv.xflux.entity.Product;

/**
 * @author XDSSWAR
 */
public interface IProductModel {
    void updateQuantity(Product product, double quantity);
    void updatePrice(Product product, double price);
    double getQuantity(Product product);
}
