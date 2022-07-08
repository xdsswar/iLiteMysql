package com.inv.ui.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Collections;

/**
 * @author XDSSWAR
 */
public class Range {
    /**
     * Get min from Integer list
     * @param list list
     * @return min
     */
    public static Double getMin(ObservableList<Double> list){
        if (list == null || list.size() == 0) {
            return Double.MAX_VALUE;
        }
        ObservableList<Double> sortedlist = FXCollections.observableArrayList(list);
        Collections.sort(sortedlist);
        return sortedlist.get(0);
    }

    /**
     * Get Max from Integer List
     * @param list list
     * @return max
     */
    public static Double getMax(ObservableList<Double> list){
        if (list == null || list.size() == 0) {
            return Double.MIN_VALUE;
        }
        ObservableList<Double> sortedlist = FXCollections.observableArrayList(list);
        Collections.sort(sortedlist);
        return sortedlist.get(sortedlist.size() - 1);
    }
}
