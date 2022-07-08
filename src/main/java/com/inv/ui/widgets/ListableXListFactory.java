package com.inv.ui.widgets;

import com.inv.xflux.entity.*;
import com.inv.xflux.entity.abs.Listable;
import com.xd.ss.war.awesome.FontAwesomeIconView;
import com.xd.ss.war.materialicon.MaterialIconView;
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
public class ListableXListFactory<T extends Listable> {

    private final ContextMenu contextMenu;
    private final ContextMenu addContext;
    private final ContextUtil contextUtil;

    /**
     * Constructor
     */
    public ListableXListFactory(){
        this.contextMenu=new ContextMenu();
        this.contextMenu.getStyleClass().add("custom-context");
        this.addContext=new ContextMenu();
        this.addContext.getStyleClass().add("custom-context");
        contextUtil=new ContextUtil();
    }

    /**
     * This method is to create a custom ListCell with some features needed
     * Dont play with it
     * @param htmlColor color as html format exp:#ffffff
     * @param iconName icon name ex:ANCHOR
     * @param addContextMenu boolean to show context menu
     * @param r1 Runnable to add new T
     * @param r2 Runnable to edit T
     * @param r3 Runnable to delete T
     * @return Custom ListCell<T>
     */
    public Callback<ListView<T>,ListCell<T>> build(String htmlColor, String iconName,
               boolean addContextMenu,Runnable r1 ,Runnable r2, Runnable r3){
        FontAwesomeIconView add=new FontAwesomeIconView();
        add.setGlyphName("PLUS");
        FontAwesomeIconView add2=new FontAwesomeIconView();
        add2.setGlyphName("PLUS");

        FontAwesomeIconView edit=new FontAwesomeIconView();
        edit.setGlyphName("EDIT");
        FontAwesomeIconView delete=new FontAwesomeIconView();
        delete.setGlyphName("TRASH");

        MenuItem addItem=contextUtil.buildMenuItem("New",add,r1);
        MenuItem addItem2=contextUtil.buildMenuItem("New",add2,r1);

        MenuItem editItem=contextUtil.buildMenuItem("Edit",edit,r2);
        MenuItem deleteItem=contextUtil.buildMenuItem("Delete",delete,r3);

        addContext.getItems().add(addItem2);
        contextMenu.getItems().addAll(addItem, editItem, deleteItem);

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
                           MaterialIconView mIc;

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

                           title = new Label(item.getName());
                           title.setTextAlignment(TextAlignment.LEFT);
                           title.getStyleClass().add("cell-n-label");

                           //Add Tooltip
                           String description="";
                           XTooltip tooltip;
                           if (item instanceof Unit) {
                               var unitDesc=item.getDescription();
                               description=unitDesc.length()<20 ? unitDesc : unitDesc.substring(0,20);
                               tooltip = new XTooltip(item.getName() + "-" + item.getSymbol(),description +" ...");
                           }
                           else if (item instanceof Category || item instanceof Brand){
                               var catDesc=item.getDescription();
                               description=catDesc.length()<20 ? catDesc : catDesc.substring(0,20);
                               tooltip = new XTooltip(item.getName(),description+" ...");
                           }
                           else if (item instanceof Provider){
                               description=item.getPhone();
                               tooltip = new XTooltip(item.getName(),item.getPhone()+
                                       "\n"+item.getEmail());
                           }
                           else if (item instanceof Product){
                               description=item.getDescription().length()<20 ? item.getDescription() : item.getDescription().substring(0,20);
                               tooltip = new XTooltip(item.getName(),"In Stock : " + item.getQuantity());
                           }
                           else if (item instanceof Client){
                               description=item.getPhone();
                               tooltip = new XTooltip(item.getName(),item.getPhone());
                           }
                           else {
                               tooltip = new XTooltip(item.getName(),"");
                           }

                           desc = new Label(description);

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
                           pane.getChildren().addAll(base, lastContainer);


                           //Handle Tooltip Stuff
                           Tooltip.install(pane, tooltip);
                           setOnMouseClicked(event -> {
                               if (event.getClickCount() == 2) {
                                   r2.run();
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
