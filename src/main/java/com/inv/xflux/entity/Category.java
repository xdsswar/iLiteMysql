package com.inv.xflux.entity;

import com.inv.xflux.entity.abs.Listable;
import com.inv.xflux.entity.in.ICategory;

/**
 * @author XDSSWAR
 */
public final class Category extends Listable implements ICategory {
    private long id;
    private String name;
    private String description;

    /**
     * Category Constructor
     * @param id Id
     * @param name Name
     * @param description Description
     */
    public Category(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * Category Constructor
     * @param name Name
     * @param description Description
     */
    public Category(String name, String description) {
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
