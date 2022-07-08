package com.inv.ui.widgets;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author XDSSWAR
 * Custom BaseToolTip for Charts
 */
public class XTooltip extends Tooltip {
    //Vars
    private static final int X_OFFSET = 10;
    private static final int Y_OFFSET = 7;

    /**
     * XToolTip Constructor
     * @param header Header Text
     * @param l1 First label text NotNull
     * @param l2 Second First label text NotNull
     */
    public XTooltip(String header, @NotNull String l1, @NotNull String l2){
        super();
        this.getStyleClass().add("xtooltip-base");
        VBox box=new VBox();
        box.getStyleClass().add("xtooltip-vbox");
        box.setAlignment(Pos.CENTER);
        Label h=new Label(header);
        h.getStyleClass().add("xtooltip-header");
        if (!h.getText().isEmpty() || h.getText()!=null) {
            box.getChildren().add(h);
        }
        if (!l1.isEmpty()){
            Label ll1=new Label(l1);
            ll1.getStyleClass().add("xtooltip-l1");
            box.getChildren().add(ll1);
        }
        if (!l2.isEmpty()){
            Label ll2=new Label(l2);
            ll2.getStyleClass().add("xtooltip-l2");
            box.getChildren().add(ll2);
        }
        this.setText(null);
        this.setGraphic(box);
    }

    /**
     * XTooltip Constructor
     * @param header Header Text
     * @param content Content Text
     */
    public XTooltip(@Nullable String header,@NotNull String content){
        super();
        this.setWrapText(true);
        this.getStyleClass().add("xtooltip-base");
        VBox box=new VBox();
        box.getStyleClass().add("xtooltip-vbox");
        box.setAlignment(Pos.TOP_CENTER);
        Label h=new Label(header);
        h.getStyleClass().add("xtooltip-header");
        if (!h.getText().isEmpty() || h.getText()!=null) {
            box.getChildren().add(h);
        }
        Label ll1=new Label(content);
        ll1.wrapTextProperty().bind(this.wrapTextProperty());
        ll1.getStyleClass().add("xtooltip-l-alter");
        box.getChildren().add(ll1);
        this.setText(null);
        this.setGraphic(box);
    }


    /**
     * Install Tooltip to de node  we Want
     * @param node Node
     * @param xTooltip XTooltip
     */
    public static void install(@NotNull final Node node, @NotNull final XTooltip xTooltip){
        node.setOnMouseMoved(event -> {
            xTooltip.show(node, event.getScreenX()+X_OFFSET, event.getScreenY() + Y_OFFSET);
        });
        node.setOnMouseExited(event -> {
            xTooltip.hide();
        });
    }



}
