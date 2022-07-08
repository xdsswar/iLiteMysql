package com.inv.data.access.orderutil;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author Xdsswar
 */
public class OrderUtil {

    /**
     * Get Order date Without the time part
     * @param date date
     * @return date
     */
    public static String getOrderDate(Date date){
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        if (date==null){
            return "Null";
        }
        //return df.format(date).substring(0,10);
        return df.format(date);
    }
}
