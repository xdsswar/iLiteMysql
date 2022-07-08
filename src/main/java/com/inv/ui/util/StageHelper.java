package com.inv.ui.util;

import com.inv.R;
import com.inv.data.access.Settings;
import com.inv.ui.ctrl.dialogs.Message;
import com.inv.ui.in.IWidget;
import com.inv.xflux.entity.Employee;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * @author XDSSWAR
 * DO NOT MODIFY THIS CLASS
 */
@SuppressWarnings("all")
public class StageHelper {
    /**
     * Do not use it outside Main class
     * Mainly used to show the SplashScreen
     * @param primaryStage primaryStage from Application
     * @param fxml file name
     */
    public  void showStage(Stage stage, String fxml) {
        if (Settings.isRunning()) {
            Message.showMessage(null,"Error!",
                    "Operation not allowed!\nThe current application is already running.",
                    Message.MESSAGE_TYPE_ERROR);
            Platform.exit();
        } else{
            stage.initStyle(StageStyle.TRANSPARENT);
            Parent parent = new Loader().load(fxml);
            Scene scene = new Scene(parent);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.getIcons().add(R.APP_ICON);
            stage.show();
        }
    }

    /**
     * Use this only for login stages or borderless Stages
     * @param fxml location
     */
    public void showStage(String fxml, String title){
        if (Settings.isMac()){
            R.STAGE.setMaximized(false);
        }
        R.STAGE.close();
        Runtime.getRuntime().gc();
        R.STAGE =new Stage();
        R.STAGE.setResizable(false);
        R.STAGE.initStyle(StageStyle.TRANSPARENT);
        Parent parent=new Loader().load(fxml);
        Scene scene=new Scene(parent);
        scene.setFill(Color.TRANSPARENT);
        R.STAGE.setScene(scene);
        R.STAGE.getIcons().add(R.APP_ICON);
        R.STAGE.setOpacity(0);
        R.STAGE.show();
        R.STAGE.setTitle(title);
        Anima anima=new Anima();
        anima.fadeInStage(R.STAGE,500);
    }

    /**
     * Show Dashboard Stage , ONLY FORR DASHBOARD
     * @param employee Online Employee
     */
    public void showDashboardStage(Node node,Employee employee){
        R.ONLINE_EMPLOYEE=employee;
        R.STAGE.close();
        R.STAGE=new Stage();
        Parent parent=new Loader().load("main.fxml");
        Scene scene=new Scene(parent);
        R.STAGE.setTitle(R.SOFTWARE_NAME+" "+ R.VERSION);
        R.STAGE.getIcons().add(R.APP_ICON);
        R.STAGE.setOpacity(0);
        R.STAGE.setScene(scene);
        R.STAGE.setMinWidth(960);
        R.STAGE.setMinHeight(600);
        if (R.IS_DASHBOARD_MAXIMIZED){
            R.STAGE.setMaximized(true);
        }
        R.STAGE.setHeight(700);
        R.STAGE.setWidth(1200);
        R.STAGE.show();
        Anima anima=new Anima();
        anima.fadeInStage(R.STAGE,300);
        //Handel the Window State
        R.STAGE.setOnCloseRequest(event -> {
            setDashboardWindowState(R.STAGE);
        });
    }

    /**
     * Save last Window state and Menu State
     * ONLY FOR THE DASHBOARD STAGE
     * When you logOut the window state will be the last one
     * saved when the app was closed last time,
     * but the menu State will be loaded every time you logOut
     */
    public  void setDashboardWindowState(Stage stage){
        if (stage.isMaximized()){
            Runnable task=()->{
                Settings.setSettings("max","1");
            };
            Thread thread=new Thread(task);
            thread.start();
        }else {
            Runnable task=()->{
                Settings.setSettings("max","0");
            };
            Thread thread=new Thread(task);
            thread.start();
        }

        //Save last Menu State
        if (R.IS_DASHBOARD_MENU_EXPANDED){
            Runnable task=()->{
                Settings.setSettings("expanded","1");
            };
            Thread thread=new Thread(task);
            thread.start();

        }else {
            Runnable task=()->{
                Settings.setSettings("expanded","0");
            };
            Thread thread=new Thread(task);
            thread.start();
        }
    }

    /**
     * Show About Stage
     * @param owner Stage Owner
     */
    public  void showInternWindow(Stage owner, String fxml, String title, Image icon){
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.initOwner(owner);
        stage.getIcons().addAll(icon);
        stage.setTitle(title);
        Parent parent=new Loader().load(fxml);
        Scene scene=new Scene(parent);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.setOpacity(0);
        stage.show();
        Anima anima=new Anima();
        anima.fadeInStage(stage,500);
    }


    /**
     * Show and Wait a dialog
     * @param owner Stage owner
     * @param fileName fxml file name
     * @param controller Controller
     */
    public void showAndWaitDiaalog(Stage owner,String fileName, IWidget controller){
        R.MODAL_STAGE=new Stage();
        R.MODAL_STAGE.initModality(Modality.APPLICATION_MODAL);
        R.MODAL_STAGE.setResizable(false);
        R.MODAL_STAGE.initOwner(owner);
        AnchorPane pane=null;
        try {
            FXMLLoader loader=new FXMLLoader(R.class.getResource("/fxml/views/" +fileName+".fxml"));
            loader.setController(controller);
            pane= loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene=new Scene(pane);
        R.MODAL_STAGE.setScene(scene);
        R.MODAL_STAGE.showAndWait();

    }

}
