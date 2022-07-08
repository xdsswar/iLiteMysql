package com.inv.ui.widgets;

import com.inv.data.access.emums.OrderStatus;
import com.inv.xflux.entity.Order;
import com.xd.controls.XButton;
import com.xd.ss.war.awesome.FontAwesomeIconView;
import com.xd.ss.war.materialdesignfont.MaterialDesignIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

/**
 * @author XDSSWAR
 * Do not Modify any libe below , it's is going to impact on the Table behaivior.
 * This class is intended to be used with all Tables , just you need to create custon threas
 * to do the stuff you want , then pass they as parameters when you call the function getActionFactory(params ...)
 * @param <S> Item Type
 * @param <T> Item Type
 */
@SuppressWarnings("all")
public  class CustomActionCellFactory<S extends Order,T>{

    /**
     * This will Return a Custom Template of a CellFactory  with
     * 3 buttons to view , edit and delete objects from the TableView
     * @param viewOrder Runnable to view Order
     * @param editOrder Runnable to edit Order
     * @param deleteOrder Runnable to delete Order
     * @return Callback<TableColumn<S, T>, TableCell<S, T>>
     */
    public Callback<TableColumn<S, T>, TableCell<S, T>> buildActionCell(Runnable viewOrder, Runnable editOrder, Runnable deleteOrder){
        Callback<TableColumn<S, T>, TableCell<S, T>> cellFactory;
        cellFactory = new Callback<TableColumn<S, T>, TableCell<S, T>>() {
            @Override
            public TableCell<S, T> call(final TableColumn<S, T> param) {
                return new TableCell<S, T>() {
                    private final XButton vOrder=new XButton();
                    private final XButton dOrder=new XButton();
                    private final XButton eOrder=new XButton();

                    private final Tooltip invoiceTooltip =new Tooltip("Invoice");
                    private final Tooltip viewOrderTooltip =new Tooltip("View Order");
                    private final Tooltip dOrderTooltip=new Tooltip("Delete");

                    private final FontAwesomeIconView view=new FontAwesomeIconView();
                    {
                        view.setGlyphSize(16);
                        view.setStyleClass("orders-action-icon");
                        view.setGlyphName("EDIT");
                        vOrder.getStyleClass().add("orders-action-btn");
                        invoiceTooltip.getStyleClass().add("home-tooltip");
                        vOrder.setTooltip(invoiceTooltip);
                        vOrder.setGraphic(view);
                    }
                    private final FontAwesomeIconView edit =new FontAwesomeIconView();
                    {
                        edit.setGlyphSize(16);
                        edit.setStyleClass("orders-action-icon");
                        edit.setGlyphName("EYE");
                        eOrder.getStyleClass().add("orders-action-btn");
                        viewOrderTooltip.getStyleClass().add("home-tooltip");
                        eOrder.setTooltip(viewOrderTooltip);
                        eOrder.setGraphic(edit);
                    }
                    private final FontAwesomeIconView delete =new FontAwesomeIconView();
                    {
                        delete.setGlyphSize(16);
                        delete.setStyleClass("orders-action-delete-icon");
                        delete.setGlyphName("CLOSE");
                        dOrder.getStyleClass().add("orders-action-btn");
                        dOrderTooltip.getStyleClass().add("home-tooltip");
                        dOrder.setTooltip(dOrderTooltip);
                        dOrder.setGraphic(delete);
                    }
                    final HBox hBox=new HBox();
                    {
                        hBox.setAlignment(Pos.CENTER);
                        hBox.getChildren().addAll(vOrder,eOrder,dOrder);
                    }
                    @Override
                    public void updateItem(T item, boolean empty) {
                        super.updateItem(item , empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            vOrder.setOnAction(event -> {
                                getTableView().getSelectionModel().select(getIndex());
                                viewOrder.run();
                            });
                            eOrder.setOnAction(event -> {
                                getTableView().getSelectionModel().select(getIndex());
                                editOrder.run();
                            });
                            dOrder.setOnAction(event -> {
                                getTableView().getSelectionModel().select(getIndex());
                                deleteOrder.run();
                            });
                            setGraphic(hBox);
                        }
                        setText(null);
                    }
                };
            }
        };
        return cellFactory;
    }

    /**
     * This wll return a custom Column factory wish change the properties
     * depending on it's  values
     * @return Callback<TableColumn<Order,String>, TableCell<Order,String>>
     */
    public Callback<TableColumn<S,String>, TableCell<S,String>> buildTableCellWithBg() {
        ObservableList<String> styleList= FXCollections.observableArrayList();
        styleList.addAll("paid-row","unpaid-row","void-row","pending-row");
        return new Callback<TableColumn<S,String>, TableCell<S,String>>() {
            public TableCell<S,String> call(TableColumn param) {
                return new TableCell<S,String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {

                        super.updateItem(item, empty);
                        if (item==null || empty) {
                            setText(null);
                            setStyle(null);
                            getStyleClass().removeAll(styleList);
                        }else {
                            if (item.contains("Paid")){
                                getStyleClass().removeAll(styleList);
                                getStyleClass().add("paid-row");
                            }else  if (item.contains("Unpaid")){
                                getStyleClass().removeAll(styleList);
                                getStyleClass().add("unpaid-row");
                            }else if (item.contains("Void")){
                                getStyleClass().removeAll(styleList);
                                getStyleClass().add("void-row");
                            }else if (item.contains("Pending")){
                                getStyleClass().removeAll(styleList);
                                getStyleClass().add("pending-row");
                            }
                            setText(item);
                        }
                    }
                };
            }
        };

    }

    /**
     * Create Custom Table Column
     * @return Column
     */
    public Callback<TableColumn<S,String>, TableCell<S,String>> buildTableCell() {
        ObservableList<String> styleList= FXCollections.observableArrayList();
        String paid="paid-cell";
        String unpaid="unpaid-cell";
        String voiD="void-cell";
        String pending="pending-cell";
        styleList.addAll(paid,unpaid,voiD,pending);
        return new Callback<TableColumn<S,String>, TableCell<S,String>>() {
            public TableCell<S,String> call(TableColumn param) {
                return new TableCell<S,String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        FontAwesomeIconView pay=new FontAwesomeIconView();
                        MaterialDesignIconView unpaidIcon=new MaterialDesignIconView();
                        VBox container;
                        Label label;

                        if (item==null || empty) {
                            setText(null);
                            setStyle(null);
                            getChildren().clear();
                        }else {
                            container=new VBox();
                            label=new Label();
                            container.setAlignment(Pos.CENTER);
                            label.setAlignment(Pos.CENTER);
                            label.setTextAlignment(TextAlignment.CENTER);
                            pay.setGlyphSize(14);
                            unpaidIcon.setGlyphSize(14);
                            pay.getStyleClass().add("label-ic-status");
                            unpaidIcon.getStyleClass().add("label-ic-status");
                            container.getChildren().add(label);

                            if (item.contains("Paid")){
                                pay.setGlyphName("CHECK");
                                label.setGraphic(pay);
                                label.getStyleClass().removeAll(styleList);
                                label.getStyleClass().add(paid);
                            }else  if (item.contains("Unpaid")){
                                unpaidIcon.setGlyphName("FLASK_EMPTY");
                                label.setGraphic(unpaidIcon);
                                label.getStyleClass().removeAll(styleList);
                                label.getStyleClass().add(unpaid);
                            }else if (item.contains("Void")){
                                pay.setGlyphName("BAN");
                                label.setGraphic(pay);
                                label.getStyleClass().removeAll(styleList);
                                label.getStyleClass().add(voiD);
                            }else if (item.contains("Pending")){
                                pay.setGlyphName("CLOCK_ALT");
                                label.setGraphic(pay);
                                label.getStyleClass().removeAll(styleList);
                                label.getStyleClass().add(pending);
                            }
                            setText(null);
                            setGraphic(container);
                            label.setText(item);
                        }
                    }
                };
            }
        };

    }

    /**
     * Custom CallBack Table Cell with colored Border
     * @return  CallBack
     */
    public Callback<TableColumn<S,String>, TableCell<S,String>> buildTableCellWithBorder() {
        ObservableList<String> styleList= FXCollections.observableArrayList();
        ObservableList<String> icsStyleList= FXCollections.observableArrayList();
        String paid="paid-cell-border";
        String unpaid="unpaid-cell-border";
        String voiD="void-cell-border";
        String pending="pending-cell-border";

        String green="green-ic";
        String red="red-ic";
        String orange="orange-ic";
        String violet="violet-ic";
        styleList.addAll(paid,unpaid,voiD,pending);
        icsStyleList.addAll(green,red,orange,violet);

        return new Callback<>() {
            public TableCell<S, String> call(TableColumn param) {
                return new TableCell<>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        FontAwesomeIconView pay = new FontAwesomeIconView();
                        MaterialDesignIconView unpaidIcon = new MaterialDesignIconView();
                        VBox container;
                        Label label;

                        if (item == null || empty) {
                            setText(null);
                            setStyle(null);
                            getChildren().clear();
                        } else {
                            container = new VBox();
                            label = new Label();
                            container.setAlignment(Pos.CENTER);
                            label.setAlignment(Pos.CENTER);
                            label.setTextAlignment(TextAlignment.CENTER);
                            label.setPrefHeight(35);
                            label.setPrefWidth(160);
                            pay.setGlyphSize(15);
                            unpaidIcon.setGlyphSize(15);
                            container.getChildren().add(label);

                            if (item.contains("Paid")) {
                                pay.setGlyphName("CHECK");
                                label.setGraphic(pay);
                                label.getStyleClass().removeAll(styleList);
                                label.getStyleClass().add(paid);
                                pay.getStyleClass().removeAll(icsStyleList);
                                pay.getStyleClass().add(green);
                            } else if (item.contains("Unpaid")) {
                                unpaidIcon.setGlyphName("FLASK_EMPTY");
                                label.setGraphic(unpaidIcon);
                                label.getStyleClass().removeAll(styleList);
                                label.getStyleClass().add(unpaid);
                                unpaidIcon.getStyleClass().removeAll(icsStyleList);
                                unpaidIcon.getStyleClass().add(red);
                            } else if (item.contains("Void")) {
                                pay.setGlyphName("BAN");
                                label.setGraphic(pay);
                                label.getStyleClass().removeAll(styleList);
                                label.getStyleClass().add(voiD);
                                pay.getStyleClass().removeAll(icsStyleList);
                                pay.getStyleClass().add(violet);
                            } else if (item.contains("Pending")) {
                                pay.setGlyphName("CLOCK_ALT");
                                label.setGraphic(pay);
                                label.getStyleClass().removeAll(styleList);
                                label.getStyleClass().add(pending);
                                pay.getStyleClass().removeAll(icsStyleList);
                                pay.getStyleClass().add(orange);
                            }
                            setText(null);
                            setGraphic(container);
                            label.setText(item);
                        }
                    }
                };
            }
        };

    }


    /**
     * Custom cell for the balance
     * @return TableCell
     */
    public Callback<TableColumn<S,String>, TableCell<S,String>> buildTableCellForBalance() {
        ObservableList<String> styleList= FXCollections.observableArrayList();
        String paid="paid-cell-balance";
        String unpaid="unpaid-cell-balance";
        String voiD="void-cell-balance";
        String pending="pending-cell-balance";

        styleList.addAll(paid,unpaid,voiD,pending);

        return new Callback<>() {
            public TableCell<S, String> call(TableColumn param) {
                return new TableCell<>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        VBox container;
                        Label label;

                        if (item == null || empty) {
                            setText(null);
                            setStyle(null);
                            getChildren().clear();
                        } else {
                            container = new VBox();
                            label = new Label();
                            container.setAlignment(Pos.CENTER);
                            label.setAlignment(Pos.CENTER);
                            label.setTextAlignment(TextAlignment.CENTER);
                            label.setPrefHeight(35);
                            label.setPrefWidth(160);
                            container.getChildren().add(label);
                            try {
                               final S sParam = getTableRow().getItem();
                                if (sParam.getPaidMoney() == sParam.getTotalWithTax()) {
                                    label.getStyleClass().removeAll(styleList);
                                    label.getStyleClass().add(paid);
                                }
                                else if (sParam.getPaidMoney() == 0) {
                                    label.getStyleClass().removeAll(styleList);
                                    label.getStyleClass().add(unpaid);
                                }
                                else if (sParam.getStatus() == OrderStatus.VOID) {
                                    label.getStyleClass().removeAll(styleList);
                                    label.getStyleClass().add(voiD);
                                }
                                else if (sParam.getPaidMoney() > 0 && sParam.getPaidMoney() < sParam.getTotalWithTax()) {
                                    label.getStyleClass().removeAll(styleList);
                                    label.getStyleClass().add(pending);
                                }
                            }catch (Exception ignored){
                            }

                            setText(null);
                            label.setText(item);
                            setGraphic(container);

                        }
                    }
                };
            }
        };

    }


}
