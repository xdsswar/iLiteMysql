package com.inv.ui.widgets;

import com.xd.ss.war.GlyphIcon;
import javafx.scene.control.MenuItem;

/**
 * @author XDSSWAR
 */
public class ContextUtil {

    /**
     * MenuItems generator
     * @param text Label
     * @param icon Icon
     * @param toDo Runnable task
     * @return MenuItem
     */
    public  MenuItem buildMenuItem(String text, GlyphIcon icon, Runnable toDo){
        MenuItem menuItem=new MenuItem("    "+text);
        if (icon!=null){
            icon.setStyleClass("custom-menuitem-icon");
            icon.setGlyphSize(14);
            menuItem.setGraphic(icon);
        }
        menuItem.setOnAction(event -> {
            Thread t=new Thread(toDo);
            t.start();
        });
        return menuItem;
    }
}
