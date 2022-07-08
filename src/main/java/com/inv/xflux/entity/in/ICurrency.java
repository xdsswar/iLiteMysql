package com.inv.xflux.entity.in;

/**
 * @author XDSSWAR
 */
public interface ICurrency extends IDefault {
    long getId();
    String getName();
    String getSymbol();
    boolean isDefault();
}
