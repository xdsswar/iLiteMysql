package com.inv.ui.util;

import com.inv.Main;
import com.inv.R;
import com.inv.ui.in.IView;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * @author XDSSWAR
 */
public class Loader {

    /**
     * Load Parent
     * @param location location path
     * @return Parent
     */
    public  Parent load(String location){
        Parent parent=null;
        try{
            parent= FXMLLoader.load(R.class.getResource("/fxml/"+location));
        }catch (IOException e){
            e.printStackTrace();
        }
        return parent;
    }

    /**
     * Load FXML Window  and Set Controller
     * @param fxmlLocation location
     * @param controller Controller , it must implement IView
     * @return Parent
     */
    public Parent loadWithController(String fxmlLocation, IView controller){
        Parent parent= null;
        try {
            FXMLLoader loader = new FXMLLoader((getClass().getResource(fxmlLocation)));
            loader.setController(controller);
            parent=loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parent;
    }

    /**
     * Load Fxml
     * @param fxmlLocation Location
     * @return Node
     */
    public Node loadView(String fxmlLocation){
        Node node= null;
        try {
            node = FXMLLoader.load(com.inv.Main.class.getResource("/fxml/"+fxmlLocation));
        } catch (IOException e) {
            e.printStackTrace();
        }
        AnchorPane.setBottomAnchor(node,0.0);
        AnchorPane.setTopAnchor(node,0.0);
        AnchorPane.setRightAnchor(node,0.0);
        AnchorPane.setLeftAnchor(node,0.0);
        return node;
    }
    /**
     * Load Widget
     * @param fxmlLocation Location
     * @return Node
     */
    public Node loadWidget(String fxmlLocation){
        Node node= null;
        try {
            node = FXMLLoader.load(Main.class.getResource("/fxml/"+fxmlLocation));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return node;
    }

    /**
     * Load View Fxml with Controller
     * @param fxmlLocation location
     * @param controller Controller
     * @return Node
     */
    public Node loadViewWithController(String fxmlLocation, IView controller){
        Node node= null;
        try {
            FXMLLoader loader = new FXMLLoader((getClass().getResource(fxmlLocation)));
            loader.setController(controller);
            node = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        AnchorPane.setBottomAnchor(node,0.0);
        AnchorPane.setTopAnchor(node,0.0);
        AnchorPane.setRightAnchor(node,0.0);
        AnchorPane.setLeftAnchor(node,0.0);
        return node;
    }


    /**
     * Add Node to Parent With Animation
     * @param parent Parent
     * @param child Child
     */
    public void addNode(AnchorPane parent, Node child){
        Anima anima=new Anima();
        Platform.runLater(new Thread(){
            public void run(){
                parent.getChildren().clear();
                child.setOpacity(0);
                parent.getChildren().add(child);
                anima.fadeInTransitionFast(child);
            }
        });
    }




}
