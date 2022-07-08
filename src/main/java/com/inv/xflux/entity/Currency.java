package com.inv.xflux.entity;

import com.inv.xflux.entity.abs.Activable;
import com.inv.xflux.entity.in.ICurrency;
import com.inv.xflux.entity.in.IDefault;

/**
 * @author XDSSWAR
 */
public final class Currency extends Activable implements ICurrency , IDefault {
    private long id;
    private String name;
    private String symbol;
    private int active; //0=Inactive, otherwise Active

    /**
     * Create new Currencies from Database
     * @param id Id
     * @param name Name
     * @param symbol Symbol
     * @param active Is Currencies Active
     */
    public Currency(long id, String name, String symbol, int active) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.active=active;
    }

    /**
     * Create new Currencies to insert in Database
     * @param name Name
     * @param symbol Symbol
     * @param active Is Currencies Active
     */
    public Currency( String name, String symbol, int active) {
        this.name = name;
        this.symbol = symbol;
        this.active=active;
    }

    /**
     * Get Currencies Id
     * @return id
     */
    @Override
    public long getId() {
        return id;
    }

    /**
     * Get Currencies Name
     * @return name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get Currencies Symbol like $
     * @return symbol
     */
    @Override
    public String getSymbol() {
        return symbol;
    }

    /**
     * Get Currencies EmployeeStatus
     * @return true if active's val>0
     */
    @Override
    public boolean isDefault() {
        if (active>0){
            return true;
        }
        return false;
    }

}
