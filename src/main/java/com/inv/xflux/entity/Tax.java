package com.inv.xflux.entity;

import com.inv.xflux.entity.abs.Activable;
import com.inv.xflux.entity.in.ITax;

/**
 * @author XDSSWAR
 */
public final class Tax extends Activable implements ITax {
    private long id;
    private double taxVal;
    private String description;
    private int active;

    /**
     * Constructor
     * @param id id
     * @param taxVal val in %
     * @param description Description
     */
    public Tax(long id, double taxVal, String description, int active) {
        this.id = id;
        this.taxVal = taxVal;
        this.description = description;
        this.active=active;
    }


    /**
     * Constructor
     * @param taxVal val in %
     * @param description Description
     */
    public Tax(double taxVal, String description) {
        this.taxVal = taxVal;
        this.description = description;
    }

    @Override
    public long getId() {
        return id;
    }
    @Override
    public double getTaxVal() {
        return taxVal;
    }
    @Override
    public String getDescription() {
        return description;
    }

    public boolean isDefault(){
        if (active>0){
            return true;
        }
        return false;
    }

}
