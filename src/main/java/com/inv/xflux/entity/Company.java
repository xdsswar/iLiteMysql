package com.inv.xflux.entity;

import com.inv.xflux.entity.abs.Activable;
import com.inv.xflux.entity.in.ICompany;
import com.inv.xflux.entity.in.IDefault;

/**
 * @author XDSSWAR
 */
public final class Company extends Activable implements ICompany, IDefault {
    private long id;
    private String name;
    private String address;
    private String phone;
    private String fax;
    private String email;
    private int status;//0->inactive, 1->active

    /**
     * Constructor to get a Company From Database
     * @param id id
     * @param name name
     * @param address address
     * @param phone phone
     * @param fax fax
     * @param email email
     * @param status status
     */
    public Company(long id, String name, String address, String phone, String fax, String email,int status) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.fax = fax;
        this.email = email;
        this.status=status;
    }

    /**
     * Constructor to create new Company
     * @param name name
     * @param address address
     * @param phone phone
     * @param fax fax
     * @param email email
     */
    public Company( String name, String address, String phone, String fax, String email) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.fax = fax;
        this.email = email;
    }

    /**
     * Get Id
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * Get Name
     * @return name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get Address
     * @return address
     */
    @Override
    public String getAddress() {
        return address;
    }

    /**
     * Get Phone
     * @return phone
     */
    @Override
    public String getPhone() {
        return phone;
    }

    /**
     * Get Fax
     * @return fax
     */
    @Override
    public String getFax() {
        return fax;
    }

    /**
     * Get Email
     * @return email
     */
    @Override
    public String getEmail() {
        return email;
    }

    /**
     * Get EmployeeStatus
     * @return status
     */
    @Override
    public boolean isDefault() {
        if (status>0){
            return true;
        }
        return false;
    }
}
