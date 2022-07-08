package com.inv.xflux.entity;

import com.inv.xflux.entity.abs.Listable;
import com.inv.xflux.entity.in.IBrand;

/**
 * @author XDSSWAR
 */
public final class Brand extends Listable implements IBrand {
    private long id;
    private String name;
    private String description;

    /**
     * Brand Creator
     * @param id Id
     * @param name Name
     * @param description Description
     */
    public Brand(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    /**
     * Brand Creator
     * @param name Name
     * @param description Description
     */
    public Brand(String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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
}
