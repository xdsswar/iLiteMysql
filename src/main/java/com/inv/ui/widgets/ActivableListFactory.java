package com.inv.ui.widgets;

import com.inv.xflux.entity.Company;
import com.inv.xflux.entity.Tax;
import com.inv.xflux.entity.abs.Activable;
import com.xd.ss.war.awesome.FontAwesomeIconView;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

/**
 * @author XDSSWAR
 * @param <T> Generic extend Activable
 * Custom ListView Factory for T list
 * This class is very important , so dont touch anything if you dont have idea of  how it works
 */
@SuppressWarnings("Duplicates")
public class ActivableListFactory<T extends Activable> {

    private final ContextMenu contextMenu;
    private final ContextMenu addContext;
    private final ContextUtil contextUtil;

    /**
     * Constructor
     */
    public ActivableListFactory(){
        this.contextMenu=new ContextMenu();
        this.contextMenu.getStyleClass().add("custom-context");
        this.addContext=new ContextMenu();
        this.addContext.getStyleClass().add("custom-context");
        contextUtil=new ContextUtil();
    }

    /**
     * Custom Cell Factory
     * @param htmlColor Color in html format exp:#ffffff
     * @param iconName Icon Name
     * @param addContextMenu Add context menu if true
     * @param r1 Runnable to add new T
     * @param r2 Runnable to edit T
     * @param r3 Runnable to set Default T
     * @param r4 Runnable to Delete T
     * @return Custom ListCell<T>
     */
    public Callback<ListView<T>, ListCell<T>> build(String htmlColor, String iconName,
                                                    boolean addContextMenu, Runnable r1 ,Runnable r2, Runnable r3, Runnable r4){
        FontAwesomeIconView add=new FontAwesomeIconView();
        add.setGlyphName("PLUS");

        FontAwesomeIconView add2=new FontAwesomeIconView();
        add2.setGlyphName("PLUS");

        FontAwesomeIconView defIcon=new FontAwesomeIconView();
        defIcon.setGlyphName("CHECK");

        FontAwesomeIconView edit=new FontAwesomeIconView();
        edit.setGlyphName("EDIT");

        FontAwesomeIconView delete=new FontAwesomeIconView();
        delete.setGlyphName("TRASH");

        MenuItem addItem=contextUtil.buildMenuItem("New",add,r1);
        MenuItem addItem2=contextUtil.buildMenuItem("New",add2,r1);

        MenuItem defItem=contextUtil.buildMenuItem("Set as Default",defIcon,r2);
        MenuItem editItem=contextUtil.buildMenuItem("Edit",edit,r3);
        MenuItem deleteItem=contextUtil.buildMenuItem("Delete",delete,r4);

        addContext.getItems().add(addItem2);
        contextMenu.getItems().addAll(addItem, defItem, editItem, deleteItem);

        return new Callback<>() {
            @Override
            public ListCell<T> call(ListView<T> param) {
                ListCell<T> cell;
                cell = new ListCell<>() {
                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                            getStyleClass().remove("unit-list-cell");
                            getStyleClass().add("unit-list-cell-empty");
                        } else {
                            getStyleClass().remove("unit-list-cell-empty");
                            getStyleClass().add("unit-list-cell");
                            setText(null);
                            AnchorPane pane;
                            HBox base;
                            VBox iconContainer;
                            VBox dataContainer;
                            VBox lastContainer;
                            Label title;
                            Label desc;
                            FontAwesomeIconView fIc;

                            pane = new AnchorPane();
                            base = new HBox();
                            base.setAlignment(Pos.CENTER_LEFT);
                            iconContainer = new VBox();
                            iconContainer.setPrefHeight(45);
                            iconContainer.setPrefWidth(45);
                            iconContainer.setAlignment(Pos.CENTER);
                            iconContainer.setStyle("-fx-border-radius: 50;" +
                                    "-fx-background-radius: 50;" +
                                    " -fx-background-color:" + htmlColor + ";");


                            dataContainer = new VBox();
                            dataContainer.setPrefHeight(45);
                            dataContainer.setMinWidth(20);
                            HBox.setMargin(dataContainer, new Insets(0, 30, 0, 15));
                            dataContainer.setAlignment(Pos.CENTER_LEFT);
                            lastContainer = new VBox();
                            lastContainer.setAlignment(Pos.CENTER);
                            title = new Label();
                            desc = new Label();
                            title.setTextAlignment(TextAlignment.LEFT);
                            title.getStyleClass().add("cell-n-label");

                            if (item instanceof Company) {
                                desc.setText(item.getPhone());
                            } else {
                                desc.setText(item.getSymbol());
                            }

                            if (item instanceof Tax) {
                                title.setText(item.getTaxVal() + "%");
                                if (item.getDescription().length() < 37) {
                                    desc.setText(item.getDescription());
                                } else {
                                    desc.setText(item.getDescription().substring(0, 34) + "...");
                                }
                            } else {
                                title.setText(item.getName());
                            }


                            desc.setTextAlignment(TextAlignment.LEFT);
                            desc.getStyleClass().add("cell-d-label");

                            fIc = new FontAwesomeIconView();
                            fIc.setGlyphSize(18);
                            fIc.getStyleClass().add("label-ic-status");


                            fIc.setGlyphName(iconName);
                            iconContainer.getChildren().clear();
                            iconContainer.getStyleClass().removeAll("unit-list-cell-icon");
                            iconContainer.getStyleClass().add("unit-list-cell-icon");
                            iconContainer.getChildren().add(fIc);

                            dataContainer.getChildren().addAll(title, desc);
                            base.getChildren().addAll(iconContainer, dataContainer);
                            AnchorPane.setLeftAnchor(base, 0.0);
                            AnchorPane.setTopAnchor(base, 0.0);
                            AnchorPane.setBottomAnchor(base, 0.0);

                            AnchorPane.setRightAnchor(lastContainer, 0.0);
                            AnchorPane.setTopAnchor(lastContainer, 0.0);
                            AnchorPane.setBottomAnchor(lastContainer, 0.0);

                            if (item.isDefault()) {
                                FontAwesomeIconView active = new FontAwesomeIconView();
                                active.setGlyphName("CHECK");
                                active.setGlyphSize(22);
                                active.getStyleClass().add("is-active");
                                lastContainer.getChildren().add(active);
                            }
                            pane.getChildren().addAll(base, lastContainer);
                            setOnMouseClicked(event -> {
                                if (event.getClickCount() == 2) {
                                    r3.run();
                                }
                            });
                            setGraphic(pane);
                        }
                        //Add ContextMenu
                    }
                };
                if (addContextMenu) {
                    cell.contextMenuProperty().bind(Bindings.when(cell.emptyProperty())
                            .then(addContext).otherwise(contextMenu));
                }

                return cell;
            }
        };
    }

}
