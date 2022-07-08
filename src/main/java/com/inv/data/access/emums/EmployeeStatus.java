package com.inv.data.access.emums;

/**
 * @author XDSSWAR
 */
public enum EmployeeStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    SUSPENDED("Suspended");

    EmployeeStatus(String suspended) {
    }

    /**
     * Get EmployeeStatus by String name
     * @param status EmployeeStatus
     * @return EmployeeStatus
     */
    public static EmployeeStatus get(String status){
        switch (status){
            case "Active":
                return ACTIVE;
            case "Inactive":
                return INACTIVE;
            case "Suspended":
                return SUSPENDED;
        }
        return ACTIVE;
    }

    /**
     * Get EmployeeStatus as String
     * @param status status
     * @return EmployeeStatus as String
     */
    public static String getStatusAsString(EmployeeStatus status){
        switch (status){
            case ACTIVE:
                return "Active";
            case INACTIVE:
                return "Inactive";
            case SUSPENDED:
                return "Suspended";
        }
        return "Active";
    }
}
