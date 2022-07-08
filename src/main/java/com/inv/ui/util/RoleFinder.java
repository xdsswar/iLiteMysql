package com.inv.ui.util;

import com.inv.data.access.emums.Role;
import javafx.collections.ObservableList;

/**
 * @author XDSSWAR
 * @param <T> Generic
 */
public class RoleFinder<T extends Role> {

    /**
     * Find the Role index on the list
     * @param valList Role list
     * @param item Role
     * @return index
     */
    public int find(ObservableList<T> valList, T item){
        int index=-1;
        var size=valList.size();
        String[] s=new String[size];
        for(int i=0;i<size;i++){
            s[i]=valList.get(i).name();
            if (item.name().equalsIgnoreCase(s[i])){
                index=i;
                break;
            }
        }
        return index;
    }
}
