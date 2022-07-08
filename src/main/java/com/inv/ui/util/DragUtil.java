package com.inv.ui.util;

import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * @author  Xdsswar 12/15/2019
 */
public abstract class DragUtil {

    /**
     * Set Stage Draggable from a node
     * @param node Node
     * @param opacityEffect Effect if you want to add some transparency while dragging the Window
     */
    public void setDraggable(Node node, boolean opacityEffect){

        node.setOnMousePressed(event -> {
            if (event.getButton()==MouseButton.PRIMARY) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });

        node.setOnMouseDragged(event -> {
            if (event.getButton()==MouseButton.PRIMARY) {
                node.getScene().getWindow().setX(event.getScreenX() - xOffset);
                node.getScene().getWindow().setY(event.getScreenY() - yOffset);
                if (opacityEffect) {
                    node.getScene().getWindow().setOpacity(0.7f);
                }
            }
        });

        if(opacityEffect) {
            node.setOnMouseReleased(event -> {
                node.getScene().getWindow().setOpacity(1.0f);
            });
        }
    }

    //Vars
    private double xOffset=0;
    private double yOffset=0;
    //End Vars
}