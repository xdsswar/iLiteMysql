package com.inv.xflux.entity;

import com.inv.xflux.entity.abs.Listable;
import com.inv.xflux.entity.in.IUnit;

/**
 * @author XDSSWAR
 */
public final class Unit extends Listable implements IUnit {
    private long id;
    private String unitName;
    private String unitSymbol;
    private String unitDescription;

    /**
     * Unit Constructor
     * @param id Id
     * @param unitName Unit Name
     * @param unitSymbol Unit Symbol (Like Lb , Kg, T, etc)
     * @param unitDescription Description
     */
    public Unit(long id, String unitName, String unitSymbol, String unitDescription) {
        this.id = id;
        this.unitName = unitName;
        this.unitSymbol = unitSymbol;
        this.unitDescription = unitDescription;
    }
    /**
     * Unit Constructor
     * @param unitName Unit Name
     * @param unitSymbol Unit Symbol (Like Lb , Kg, T, etc)
     * @param unitDescription Description
     */
    public Unit(String unitName, String unitSymbol, String unitDescription) {
        this.unitName = unitName;
        this.unitSymbol = unitSymbol;
        this.unitDescription = unitDescription;
    }

    @Override
    public long getId() {
        return id;
    }
    @Override
    public String getName() {
        return unitName;
    }
    @Override
    public String getSymbol() {
        return unitSymbol;
    }
    @Override
    public String getDescription() {
        return unitDescription;
    }

}
