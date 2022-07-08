package com.inv.ui.util;

import com.inv.R;
import com.inv.ui.in.IWidget;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * @author XDSSWAR
 */
@SuppressWarnings("Duplicates")
public final class WidgetUtil {
    /**
     * Add ScrollPane widget
     * @param owner AnchorPane container
     * @param fileName location after /fxml/views/
     */
    public void addScrollPane(AnchorPane owner, String fileName){
        if (owner.getChildren().size()>0){
            owner.getChildren().clear();
        }
        ScrollPane pane=null;
        try {
             pane= FXMLLoader.load(R.class.getResource("/fxml/views/" +fileName+".fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        AnchorPane.setBottomAnchor(pane,0.0);
        AnchorPane.setLeftAnchor(pane,0.0);
        AnchorPane.setRightAnchor(pane,0.0);
        AnchorPane.setTopAnchor(pane,-1.0);
        if (pane!=null){
            Anima anima=new Anima();
            pane.setFocusTraversable(false);
            pane.setOpacity(0);
            owner.getChildren().clear();
            owner.getChildren().add(pane);
            pane.setFitToWidth(true);
            anima.fadeInTransitionFast(pane);

        }
    }

    public void addScrollPane(AnchorPane owner, String fileName,IWidget controller){
        if (owner.getChildren().size()>0){
            owner.getChildren().clear();
        }
        ScrollPane pane=null;
        try {
            FXMLLoader loader=new FXMLLoader(R.class.getResource("/fxml/views/" +fileName+".fxml"));
            loader.setController(controller);
            pane= loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        AnchorPane.setBottomAnchor(pane,0.0);
        AnchorPane.setLeftAnchor(pane,0.0);
        AnchorPane.setRightAnchor(pane,0.0);
        AnchorPane.setTopAnchor(pane,-1.0);
        if (pane!=null){
            Anima anima=new Anima();
            pane.setFocusTraversable(false);
            pane.setOpacity(0);
            owner.getChildren().clear();
            owner.getChildren().add(pane);
            pane.setFitToWidth(true);
            anima.fadeInTransitionFast(pane);

        }
    }

    /**
     * Add AnchorPane Widget
     * @param owner AnchorPane container
     * @param fileName location after /fxml/views/
     */
    public  void addAnchorPane(AnchorPane owner, String fileName){
        if (owner.getChildren().size()>0){
            owner.getChildren().clear();
        }
        AnchorPane pane=null;
        try {
            pane= FXMLLoader.load(R.class.getResource("/fxml/views/" +fileName+".fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        AnchorPane.setBottomAnchor(pane,0.0);
        AnchorPane.setLeftAnchor(pane,0.0);
        AnchorPane.setRightAnchor(pane,0.0);
        AnchorPane.setTopAnchor(pane,-1.0);
        if (pane!=null){
            Anima anima=new Anima();
            pane.setFocusTraversable(false);
            pane.setOpacity(0);
            owner.getChildren().clear();
            owner.getChildren().add(pane);
            anima.fadeInTransitionFast(pane);
        }
    }


    /**
     * Add AnchorPane Widget
     * @param owner AnchorPane container
     * @param fileName location after /fxml/views/
     * @param controller IWidget
     */
    public void addViewWithController(AnchorPane owner, String fileName, IWidget controller){
        if (owner.getChildren().size()>0){
            owner.getChildren().clear();
        }
        try {
            FXMLLoader loader = new FXMLLoader(R.class.getResource("/fxml/views/" + fileName + ".fxml"));
            loader.setController(controller);
            AnchorPane pane=(AnchorPane)loader.load();
            AnchorPane.setBottomAnchor(pane, 0.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);
            AnchorPane.setTopAnchor(pane, -1.0);
            if (pane != null) {
                Anima anima=new Anima();
                pane.setFocusTraversable(false);
                pane.setOpacity(0);
                owner.getChildren().clear();
                owner.getChildren().add(pane);
                anima.fadeInTransitionFast(pane);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Load custom fxml for create a listView factory
     * @param fileName fxml file name
     * @param controller Controller
     * @return AnchorPane
     */
    public  AnchorPane addFactoryView(String fileName, IWidget controller){
        AnchorPane pane=null;
        try {
            FXMLLoader loader = new FXMLLoader(R.class.getResource("/fxml/views/" + fileName + ".fxml"));
            loader.setController(controller);
             pane=loader.load();
        }catch (IOException e){
            e.printStackTrace();
        }
        return pane;
    }
}

