package com.inv.ui.widgets;

import com.inv.data.access.emums.EmployeeStatus;
import com.inv.data.access.emums.Role;
import com.inv.xflux.entity.*;
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
 * @param <T> Generic extend from Employee
 */
@SuppressWarnings("Duplicates")
public class EmpListFactory<T extends Employee> {
    private final ContextMenu contextMenu, addContext;
    private final ContextUtil contextUtil;

    public EmpListFactory(){
        this.contextMenu=new ContextMenu();
        this.contextMenu.getStyleClass().add("custom-context");
        this.addContext=new ContextMenu();
        this.addContext.getStyleClass().add("custom-context");
        contextUtil=new ContextUtil();
    }

    /**
     * Add contextMenu to the Employee List
     * @param htmlColor Color for the Circle
     * @param iconName Icon in the Circle
     * @param addContextMenu add Context bool
     * @param rAddNew Thread to add new Employee
     * @param rSetActive Thread to set Active
     * @param rSetInactive Thread to set Inactive
     * @param rEdit Thread to edit Employee
     * @param rDelete Thread to Delete
     * @return Callback
     */
    public Callback<ListView<T>, ListCell<T>> build(String htmlColor, String iconName,
              boolean addContextMenu, Runnable rAddNew , Runnable rSetActive,
                                                    Runnable rSetInactive, Runnable rEdit, Runnable rDelete){
        FontAwesomeIconView add=new FontAwesomeIconView();
        add.setGlyphName("PLUS");

        FontAwesomeIconView add2=new FontAwesomeIconView();
        add2.setGlyphName("PLUS");

        FontAwesomeIconView activeIcon=new FontAwesomeIconView();
        activeIcon.setGlyphName("CHECK");

        FontAwesomeIconView edit=new FontAwesomeIconView();
        edit.setGlyphName("EDIT");

        FontAwesomeIconView delete=new FontAwesomeIconView();
        delete.setGlyphName("TRASH");

        MenuItem addItem=contextUtil.buildMenuItem("New",add,rAddNew);
        MenuItem addItem2=contextUtil.buildMenuItem("New",add2,rAddNew);

        MenuItem editItem=contextUtil.buildMenuItem("Edit",edit,rEdit);
        MenuItem deleteItem=contextUtil.buildMenuItem("Delete",delete,rDelete);

        Menu status=new Menu("    Status     ");
        status.getStyleClass().add("custom-menu-in-context");
        FontAwesomeIconView statusIc=new FontAwesomeIconView();
        statusIc.setGlyphName("RANDOM");
        statusIc.setGlyphSize(14);
        statusIc.setStyleClass("custom-menuitem-icon");
        status.setGraphic(statusIc);

        FontAwesomeIconView inactiveIcon=new FontAwesomeIconView();
        inactiveIcon.setGlyphName("BAN");

        MenuItem setActiveItem=contextUtil.buildMenuItem("Active",activeIcon,rSetActive);
        MenuItem setInactiveItem=contextUtil.buildMenuItem("Inactive",inactiveIcon,rSetInactive);
        status.getItems().addAll(setActiveItem,setInactiveItem);

        addContext.getItems().add(addItem2);
        contextMenu.getItems().addAll(addItem, editItem, status, deleteItem);

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
                            title = new Label(item.getName()+" "+item.getLastName());
                            desc = new Label(item.getPhone());
                            title.setTextAlignment(TextAlignment.LEFT);
                            title.getStyleClass().add("cell-n-label");
                            XTooltip tooltip=new XTooltip("Role", Role.getRoleAsString(item.getRole()));

                            desc.setTextAlignment(TextAlignment.LEFT);
                            desc.getStyleClass().add("cell-d-label");

                            fIc = new FontAwesomeIconView();
                            fIc.setGlyphSize(22);
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

                            if (item.isAdmin()){
                                fIc.setGlyphName("SHIELD");
                            }
                            else if (item.isManager()){
                                fIc.setGlyphName("TASKS");
                                iconContainer.setStyle("-fx-background-color: #a0522d;");
                            }else {
                                iconContainer.setStyle("-fx-background-color: #e3c800;");
                            }

                            if (item.getStatus()== EmployeeStatus.INACTIVE){
                                fIc.setGlyphName("BAN");
                                fIc.getStyleClass().add("inactive-e");
                            }


                            pane.getChildren().addAll(base, lastContainer);
                            Tooltip.install(pane,tooltip);
                            setOnMouseClicked(event -> {
                                if (event.getClickCount() == 2) {
                                    rEdit.run();
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
