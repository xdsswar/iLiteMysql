package com.inv.ui.util;

import java.text.DateFormatSymbols;

/**
 * @author XDSSWAR
 * Created on 2/12/2020
 */
public class DateConverter {
    /**
     * Convert Date Needed format 2020-04-04 14:33:44
     * @param date Date
     * @return Date converted
     */
    public static String getMonth(String date){
        int d = Integer.parseInt(date.substring(5, 7));
        return new DateFormatSymbols().getMonths()[d - 1];
    }

    /**
     * Get Year from String Date. Needed format 2020-04-04 14:33:44
     * @param date date
     * @return year
     */
    public static String getYear(String date){
        return date.substring(0, 4);
    }



}
