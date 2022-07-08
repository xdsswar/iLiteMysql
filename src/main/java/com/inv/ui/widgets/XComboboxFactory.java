package com.inv.ui.widgets;

import com.inv.xflux.entity.Unit;
import com.inv.xflux.entity.abs.Listable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Custom Combobox Factory
 * @param <T> Generic
 */
public class XComboboxFactory<T extends Listable> {

    /**
     * Custom Factory for Combobox
     * @return Custom Factory
     */
    public Callback<ListView<T>, ListCell<T>> build(){
       return new Callback<>() {
           @Override
           public ListCell<T> call(ListView<T> l) {
               return new ListCell<>() {
                   @Override
                   protected void updateItem(T item, boolean empty) {
                       super.updateItem(item, empty);
                       if (item == null || empty) {
                           setGraphic(null);
                           setText(null);
                       } else {
                           if (item instanceof Unit) {
                               setText(item.getName() + " - " + item.getSymbol());
                           } else {
                               setText(item.getName());
                           }

                       }
                   }
               };
           }
       };
    }
}
