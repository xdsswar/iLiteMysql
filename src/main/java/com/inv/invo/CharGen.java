package com.inv.invo;

/**
 * @author XDSSWAR
 */
public class CharGen {
    private static final String ALPHA_NUMERIC_STRING = "0123456789";

    /**
     * Generate random char str
     * @return string
     */
    public static String genRandom() {
        StringBuilder builder = new StringBuilder();
        int count=7;
        while (count--!= 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return "INV"+builder.toString();
    }

}
