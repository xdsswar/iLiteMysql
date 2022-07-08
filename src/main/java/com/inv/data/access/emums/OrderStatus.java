package com.inv.data.access.emums;

/**
 * @author XDSSWAR
 */
public enum OrderStatus {
    UNPAID("0"),
    PAID("1"),
    PENDING("2"),
    VOID("3");

    OrderStatus(String status) {
    }

    /**
     * Get OrderStatus based on the value str
     * @param status string
     * @return OrderStatus
     */
    public static OrderStatus get(String status){
        switch (status){
            case "0":
                return UNPAID;
            case "1":
                return PAID;
            case "2":
                return PENDING;
            case "3":
                return VOID;
        }
        return VOID;
    }

    /**
     * Get the Order EmployeeStatus based on a number string
     * @param status status (1,2,3,0)
     * @return EmployeeStatus
     */
    public static String getStatusAsString(OrderStatus status){
        String st="Unpaid";
        if (status==OrderStatus.PAID){
            st="Paid";
        }else if (status==OrderStatus.PENDING){
            st="Pending";
        }else if (status==OrderStatus.VOID){
            st="Void";
        }
        return st;
    }

    /**
     * Get the value to insert on db base on OrderStatus
     * @param status status
     * @return sting
     */
    public static String getStatusToInsert(OrderStatus status){
        switch (status){
            case UNPAID:
                return "0";
            case PAID:
                return "1";
            case PENDING:
                return "2";
        }
        return "3";//VOID
    }
}
