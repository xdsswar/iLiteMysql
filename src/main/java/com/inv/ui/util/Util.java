package com.inv.ui.util;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author XDSSWAR
 */
public class Util {
    /**
     * Gets the top level Stage associated with this node.
     * @param node the node
     * @return the top level Stage
     */
    public static Stage getWindow(final Node node) {
        final Scene scene = node == null ? null : node.getScene();
        return scene == null ? null : (Stage) scene.getWindow();
    }
}
