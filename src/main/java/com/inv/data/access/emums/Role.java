package com.inv.data.access.emums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @author XDSSWAR
 */
public enum  Role {
    ADMIN("Admin"),
    MANAGER("Manager"),
    EMPLOYEE("Employee");

    Role(String manager) {
    }

    /**
     * Get Role from RoleName
     * @param roleName roleName
     * @return Role
     */
    public static Role get(String roleName){
        switch (roleName){
            case "Admin":
                return ADMIN;
            case "Manager":
                return MANAGER;
            case "Employee":
                return EMPLOYEE;
        }
        return EMPLOYEE;
    }

    /**
     * Get Role as String
     * @param role Role
     * @return Role as String
     */
    public  static String getRoleAsString(Role role){
        switch (role){
            case ADMIN:
                return "Admin";
            case MANAGER:
                return "Manager";
            case EMPLOYEE:
                return "Employee";
        }
        return "Employee";
    }

    /**
     * Get All roles
     * @return Roles list
     */
    public static ObservableList<Role> getAll(){
        List<Role> roles= new ArrayList<>(EnumSet.allOf(Role.class));
        return FXCollections.observableList(roles);
    }

}
