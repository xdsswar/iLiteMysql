package com.inv.ui.util;

import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author  Xdsswar 12/15/2019
 */
public class Anima {

    private static FadeTransition transition=null;
    /**
     * Fade in Transition
     * @param node the Node
     */
    public  void fadeInTransition(Node node){
        node.toFront();
        FadeTransition ft=new FadeTransition(Duration.millis(1000),node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setAutoReverse(false);
        ft.setCycleCount(1);
        ft.play();
    }


    public  void fadeInContextMenu(ContextMenu menu, int millis){
        Timeline timeline = new Timeline();
        KeyFrame key = new KeyFrame(Duration.millis(millis),
                new KeyValue(menu.opacityProperty(), 1.0));
        timeline.getKeyFrames().add(key);
        timeline.play();

    }



    /**
     * Fade in Transition
     * @param node the Node
     */
    public  void fadeInTransitionFast(Node node){
        node.toFront();
        FadeTransition ft=new FadeTransition(Duration.millis(300),node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setAutoReverse(false);
        ft.setCycleCount(1);
        ft.play();
    }

    /**
     * Fade Out Transition
     * @param node the Node
     */
    public  void fadeOutTransition(Node node){
        FadeTransition ft=new FadeTransition(Duration.millis(1000),node);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setAutoReverse(false);
        ft.setCycleCount(1);
        ft.play();
        node.toBack();
    }
//End for nodes

//Fade Out Window

    /**
     * Fade Out Stage
     * @param stage The Stage
     * @param millis Millis
     */
    public void fadeOutStage(Stage stage, int millis){
           timeline = new Timeline();
           KeyFrame key = new KeyFrame(Duration.millis(millis), new KeyValue(stage.opacityProperty(), 0.0));
           timeline.getKeyFrames().add(key);
           timeline.play();

    }//End Fade Out Window


    /**
     * Create SlideShow
     * @param imageView ImageView
     * @param images List of Images for the slider
     * @param seconds transition time
     */
    public static void slideShow(ImageView imageView, ObservableList<Image> images, int seconds){
        int length= images.size();
        AtomicInteger index= new AtomicInteger(0);
        imageView.opacityProperty().addListener((observable, oldValue, newValue) -> {
             if (newValue.doubleValue()==0){
                if (index.get()<length){
                    imageView.setImage(images.get(index.get()));
                    index.getAndIncrement();
                }else {
                    imageView.setImage(images.get(0));
                    index.set(0);
                }
             }
        });

        transition = new FadeTransition(Duration.seconds(seconds), imageView);
        transition.setFromValue(0.0);
        transition.setToValue(1.0);
        transition.setAutoReverse(true);
        transition.setCycleCount(Timeline.INDEFINITE);
        transition.play();
    }

    /**
     * Stop SlideShow
     */
    public static void stopSlideShow(){
        if (transition!=null) {
            transition.stop();
        }
    }


    /**
     * Fade In Stage
     * @param stage Stage
     * @param millis Millis
     */
    public  void fadeInStage(Stage stage, int millis){
        timeline = new Timeline();
        KeyFrame key = new KeyFrame(Duration.millis(millis),
                new KeyValue(stage.opacityProperty(), 1.0));
        timeline.getKeyFrames().add(key);
        timeline.play();

    }//End Fade In Window

    /**
     * Rotate Animation
     * @param node The node
     * @param cycleCount Cycle Count
     * @param point3D Rotate Axis
     * @param duration duration in Mills
     * @param fromAngle From Angle
     * @param toAngle To Angle
     */
    public void rotateTransition(Node node, int cycleCount , Point3D point3D, double duration , int fromAngle, int toAngle) {
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(duration), node);
        rotateTransition.setAxis(point3D);
        rotateTransition.setFromAngle(fromAngle);
        rotateTransition.setToAngle(toAngle);
        rotateTransition.setInterpolator(Interpolator.EASE_BOTH);
        rotateTransition.setCycleCount(cycleCount);
        rotateTransition.play();
    }


    /**
     * ZoomAnimation
     * @param node Node
     * @param zoomFactor Factor
     * @param cycleCount Cycle Count
     * @param mills Time in mills
     */
    public void zoomAnimation(Node node, float zoomFactor, int cycleCount, double mills) {
        timeline=new Timeline();
        ScaleTransition st = new ScaleTransition(Duration.millis(mills), node);
        st.setByX(zoomFactor);
        st.setByY(zoomFactor);
        st.setCycleCount(cycleCount);
        st.setAutoReverse(true);
        st.play();
        timeline.setOnFinished(event -> {

        });
    }

    //Vars
    public Timeline timeline;

}
