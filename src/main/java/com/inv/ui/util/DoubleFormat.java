package com.inv.ui.util;

import java.text.DecimalFormat;

/**
 * @author XDSSWAR
 * This class will help you to get Formmatted Double values
 */
public class DoubleFormat {

     /**
     * Get Formatted double with 2 decimal places
     * @param value double value exp:15.45345
     * @return formatted double exp: 15.45
     */
    public static Double dFormat(double value){
        final DecimalFormat FORMAT_2_DECIMAL = new DecimalFormat("#.00");
        return  Double.parseDouble(FORMAT_2_DECIMAL.format(value));
    }

    /**
     * Get Formatted double as String
     * @param value double value exp:15.45345
     * @return formatted value as String
     */
    public static String sFormat(double value){
        final DecimalFormat FORMAT_2_DECIMAL = new DecimalFormat("##########0.00");
        return FORMAT_2_DECIMAL.format(value);
    }
}
