package com.inv.ui.util;

import com.inv.xflux.entity.Category;
import com.inv.xflux.entity.abs.Listable;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Util to get Index From list
 * @param <T> Generic
 */
public class IndexFinder<T extends Listable> extends ArrayList<T> {
    /**
     * Get index From List
     * @param valList List T
     * @param item T item
     * @return int index
     */
    public int find(ObservableList<T> valList, T item){
        int index=-1;
        var size=valList.size();
        String[] s=new String[size];
        for(int i=0;i<size;i++){
           s[i]=valList.get(i).getName();
           if (item.getName().equals(s[i])){
               index=i;
               break;
           }
        }
        return index;
    }

}
