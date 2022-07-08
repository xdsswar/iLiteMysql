package com.inv.ui.util;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.WritableValue;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

/**
 * @author  Xdsswar 12/15/2019
 */
@SuppressWarnings("Duplicates")
public class WidthAnimation  {

    /**
     * Width Animation
     * @param leftPane Node to reduce or increase width
     * @param leftPaneWidth new Node width
     * @param rightPane Node to increase or decrease AnchorPane value
     * @param rightPaneLeftAnchorValue new Node AnchorPane value
     */
    public void widthAnimation(AnchorPane leftPane, double leftPaneWidth,
                                      AnchorPane rightPane, double rightPaneLeftAnchorValue) {
        Runnable task=()->{
            Duration duration = Duration.millis(200);
            WritableValue<Double> customAnchorValue=new WritableValue<Double>() {
                @Override
                public Double getValue() {
                    return AnchorPane.getLeftAnchor(rightPane);
                }

                @Override
                public void setValue(Double value) {
                    AnchorPane.setLeftAnchor(rightPane,value);
                }
            };
            Platform.runLater(()->{
                Timeline timeline = new Timeline(
                        new KeyFrame(duration,new KeyValue(leftPane.prefWidthProperty() ,leftPaneWidth, Interpolator.EASE_BOTH)),
                        new KeyFrame(duration,new KeyValue(customAnchorValue,rightPaneLeftAnchorValue, Interpolator.EASE_BOTH))
                );
                timeline.play();
            });
        };
        Thread thread=new Thread(task);
        thread.start();
    }

    /**
     * This animation Allow to run some code after it finish
     * @param leftPane Node to reduce or increase width
     * @param leftPaneWidth new Node width
     * @param rightPane Node to increase or decrease AnchorPane value
     * @param rightPaneLeftAnchorValue new Node AnchorPane value
     * @return Timeline
     */
    public  Timeline queueAnimation(AnchorPane leftPane, double leftPaneWidth,
                                            AnchorPane rightPane, double rightPaneLeftAnchorValue){
        Timeline timeline=null;
        Duration duration = Duration.millis(200);
        WritableValue<Double> customAnchorValue=new WritableValue<Double>() {
            @Override
            public Double getValue() {
                return AnchorPane.getLeftAnchor(rightPane);
            }

            @Override
            public void setValue(Double value) {
                AnchorPane.setLeftAnchor(rightPane,value);
            }
        };
        timeline = new Timeline(
                new KeyFrame(duration,new KeyValue(leftPane.prefWidthProperty() ,leftPaneWidth, Interpolator.EASE_BOTH)),
                new KeyFrame(duration,new KeyValue(customAnchorValue,rightPaneLeftAnchorValue, Interpolator.EASE_BOTH))
        );
        timeline.play();
        return timeline;
    }

    /**
     * Right to Left With Animation
     * @param pane AnchorPane
     * @param toWidth end Width
     * @param mills mills
     */
    public  void widthAnimation(AnchorPane pane, double toWidth, int mills) {
        Runnable task=()->{
            Duration duration = Duration.millis(mills);
            Platform.runLater(()->{
                Timeline timeline = new Timeline(
                        new KeyFrame(duration,new KeyValue(pane.prefWidthProperty() ,toWidth, Interpolator.EASE_BOTH))
                );
                timeline.play();
            });
        };
        Thread thread=new Thread(task);
        thread.start();
    }

    /**
     * Right to Left With Animation
     * @param pane AnchorPane
     * @param anchorValue end Anchor
     * @param mills mills
     */
    public void anchorAnimation(AnchorPane pane, double anchorValue,double width ,int mills) {
        Runnable task=()->{
            Duration duration = Duration.millis(mills);
            WritableValue<Double> customAnchorValue=new WritableValue<Double>() {
                @Override
                public Double getValue() {
                    return AnchorPane.getLeftAnchor(pane);
                }
                @Override
                public void setValue(Double value) {
                    AnchorPane.setLeftAnchor(pane,value);
                }
            };

            ((AnchorPane)pane.getParent()).widthProperty().addListener((observable, oldValue, newValue) -> {
                if (AnchorPane.getLeftAnchor(pane)>anchorValue) {
                    Runnable runnable=()->{
                        Platform.runLater(()->{
                            pane.setOpacity(0);
                            AnchorPane.setLeftAnchor(pane, newValue.doubleValue());
                        });
                    };
                    runnable.run();
                }
            });

            Platform.runLater(()->{
                pane.setOpacity(1);
                Timeline timeline = new Timeline(
                        new KeyFrame(duration,new KeyValue(pane.prefWidthProperty() ,width, Interpolator.EASE_BOTH)),
                        new KeyFrame(duration,new KeyValue(customAnchorValue,anchorValue, Interpolator.EASE_BOTH))
                );
                timeline.setOnFinished(event -> {
                    if (pane.getWidth()<=1){
                        pane.setOpacity(0);
                    }
                });
                timeline.play();
            });
        };
        Thread thread=new Thread(task);
        thread.start();
    }

    /**
     *Height Animation
     * @param pane the Target Node type AnchorPane
     * @param height the new Height Value
     */
    public  void heightAnimation(AnchorPane pane, double height, int mills){
        Runnable task=()->{
            Duration duration = Duration.millis(mills);
            Platform.runLater(()->{
                Timeline timeline = new Timeline(
                        new KeyFrame(duration,new KeyValue(pane.maxHeightProperty(),height, Interpolator.EASE_BOTH))
                );
                timeline.play();
            });
        };
        Thread thread=new Thread(task);
        thread.start();
    }



}
