package com.inv.ui.widgets;

import com.inv.data.access.emums.Role;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * @author XDSSWAR
 * @param <T> Generic
 */
public class StaffComboboxFactory<T extends Role> {

    /**
     * Set up the Factory
     * @return Callback
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
                           setText(Role.getRoleAsString(item));
                        }
                    }
                };
            }
        };
    }
}
