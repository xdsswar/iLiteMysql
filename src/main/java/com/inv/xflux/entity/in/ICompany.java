package com.inv.xflux.entity.in;

/**
 * @author XDSSWAR
 */
public interface ICompany extends IDefault {
    long getId();
    String getName();
    String getAddress();
    String getPhone();
    String getFax();
    String getEmail();
    boolean isDefault();
}
