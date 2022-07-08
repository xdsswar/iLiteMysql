package com.inv.xflux.entity;

import com.inv.xflux.entity.abs.Listable;
import com.inv.xflux.entity.in.IProvider;

/**
 * @author XDSSWAR
 */
public final class Provider extends Listable implements IProvider {
    private long id;
    private String name;
    private String email;
    private String phone;
    private String address;

    /**
     * Provider Constructor
     * @param id Id
     * @param name Name
     * @param email Email
     * @param phone Phone
     * @param address Address
     */
    public Provider(long id, String name, String email, String phone, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
    /**
     * Provider Constructor
     * @param name Name
     * @param email Email
     * @param phone Phone
     * @param address Address
     */
    public Provider(String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
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
    public String getEmail() {
        return email;
    }
    @Override
    public String getPhone() {
        return phone;
    }
    @Override
    public String getAddress() {
        return address;
    }
}
