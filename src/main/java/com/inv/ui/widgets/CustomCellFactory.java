package com.inv.ui.widgets;

import com.inv.data.access.emums.OrderStatus;
import com.inv.data.access.orderutil.OrderUtil;
import com.inv.xflux.entity.Order;
import com.xd.ss.war.awesome.FontAwesomeIconView;
import com.xd.ss.war.materialdesignfont.MaterialDesignIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

/**
 * @author XDSSWAR
 */
@SuppressWarnings("all")
public class CustomCellFactory{
    /**
     * Custom ListCell For Recent Orders
     * @return ListCell
     */
    public  Callback<ListView<Order>, ListCell<Order>> buildListCell(){
        return new Callback<ListView<Order>, ListCell<Order>>() {
            @Override
            public ListCell<Order> call(ListView<Order> param) {
                return new ListCell<Order>() {
                    @Override
                    protected void updateItem(Order item, boolean empty) {
                        super.updateItem(item, empty);
                        AnchorPane pane;
                        HBox base;
                        VBox iconContainer;
                        VBox dataContainer;
                        VBox lastContainer;
                        Label title;
                        Label email;
                        Label date;
                        FontAwesomeIconView fIc;
                        MaterialDesignIconView mIc;

                        if (item==null || empty) {
                            setText(null);
                            setStyle(null);
                        }else {
                            String paid="cell-icon-container-paid";
                            String unpaid="cell-icon-container-unpaid";
                            String voidS="cell-icon-container-void";
                            String pending="cell-icon-container-pending";
                            ObservableList<String> styles= FXCollections.observableArrayList(
                                    paid,
                                    unpaid,
                                    voidS,
                                    pending
                            );
                            pane=new AnchorPane();
                            base=new HBox();
                            base.setAlignment(Pos.CENTER_LEFT);
                            iconContainer=new VBox();
                            iconContainer.setPrefHeight(45);
                            iconContainer.setPrefWidth(45);
                            iconContainer.setAlignment(Pos.CENTER);
                            iconContainer.setStyle("-fx-border-radius: 50;-fx-background-radius: 50");

                            dataContainer=new VBox();
                            dataContainer.setPrefHeight(45);
                            dataContainer.setMinWidth(20);
                            HBox.setMargin(dataContainer,new Insets(0,30,0,15));
                            dataContainer.setAlignment(Pos.CENTER_LEFT);
                            lastContainer=new VBox();
                            lastContainer.setAlignment(Pos.CENTER);

                            title=new Label(item.getClient().getName());
                            title.setTextAlignment(TextAlignment.LEFT);
                            title.getStyleClass().add("cell-title-label");

                            email=new Label(item.getClient().getEmail());
                            email.setTextAlignment(TextAlignment.LEFT);
                            email.getStyleClass().add("cell-desc-label");

                            date=new Label(OrderUtil.getOrderDate(item.getDate()));
                            date.setTextAlignment(TextAlignment.RIGHT);
                            date.getStyleClass().add("cell-date-label");

                            fIc=new FontAwesomeIconView();
                            fIc.setGlyphSize(16);
                            fIc.getStyleClass().add("label-ic-status");
                            mIc=new MaterialDesignIconView();
                            mIc.setGlyphSize(16);
                            mIc.getStyleClass().add("label-ic-status");

                            if (item.getStatus()== OrderStatus.PAID){
                                fIc.setGlyphName("CHECK");
                                iconContainer.getChildren().clear();
                                iconContainer.getStyleClass().removeAll(styles);
                                iconContainer.getStyleClass().add(paid);
                                iconContainer.getChildren().add(fIc);
                            }else if (item.getStatus()== OrderStatus.UNPAID){
                                mIc.setGlyphName("FLASK_EMPTY");
                                iconContainer.getChildren().clear();
                                iconContainer.getStyleClass().removeAll(styles);
                                iconContainer.getStyleClass().add(unpaid);
                                iconContainer.getChildren().add(mIc);
                            }else if (item.getStatus()== OrderStatus.VOID){
                                fIc.setGlyphName("BAN");
                                iconContainer.getChildren().clear();
                                iconContainer.getStyleClass().removeAll(styles);
                                iconContainer.getStyleClass().add(voidS);
                                iconContainer.getChildren().add(fIc);
                            }else if (item.getStatus()== OrderStatus.PENDING){
                                fIc.setGlyphName("CLOCK_ALT");
                                iconContainer.getChildren().clear();
                                iconContainer.getStyleClass().removeAll(styles);
                                iconContainer.getStyleClass().add(pending);
                                iconContainer.getChildren().add(fIc);
                            }
                            dataContainer.getChildren().addAll(title,email);
                            lastContainer.getChildren().addAll(date);
                            base.getChildren().addAll(iconContainer,dataContainer);
                            AnchorPane.setLeftAnchor(base,0.0);
                            AnchorPane.setTopAnchor(base,0.0);
                            AnchorPane.setBottomAnchor(base,0.0);

                            AnchorPane.setRightAnchor(lastContainer,0.0);
                            AnchorPane.setTopAnchor(lastContainer,0.0);
                            AnchorPane.setBottomAnchor(lastContainer,0.0);
                            pane.getChildren().addAll(base,lastContainer);
                            setGraphic(pane);
                        }

                    }
                };
            }
        };
    }

}
