package com.inv.xflux.entity;

import com.inv.xflux.entity.abs.Listable;
import com.inv.xflux.entity.in.IClient;

/**
 * @author XDSSWAR
 */
public final class Client extends Listable implements IClient {
    private long id;
    private String name;
    private String email;
    private String phone;

    /**
     * Client Constructor
     * @param id id
     * @param name Full Name
     * @param email Email
     * @param phone Phone
     */
    public Client(long id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
    /**
     * Client Constructor
     * @param name Full Name
     * @param email Email
     * @param phone Phone
     */
    public Client(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
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
}
