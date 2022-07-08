package com.inv.ui.widgets;

import com.inv.R;
import com.inv.xflux.entity.in.IEmployee;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import org.jetbrains.annotations.Nullable;

/**
 * Custom List Cell for Staff Members
 * @param <T> Object
 */
public class StaffListCell<T extends IEmployee>{
    /**
     * Create Custom List Cell for Staff
     * @param menu To add ContextMenu Nullable
     * @param htmlBorderColor to set the border color exp: #ffffff
     * @return List Cell
     */
    public  Callback<ListView<T>, ListCell<T>> build(@Nullable ContextMenu menu,@Nullable String htmlBorderColor){
        return new Callback<>() {
            @Override
            public ListCell<T> call(ListView<T> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            String color = "#6c7293";
                            if (htmlBorderColor != null) {
                                color = htmlBorderColor;
                            }
                            AnchorPane pane = new AnchorPane();
                            pane.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color:" + color);
                            HBox hBox = new HBox();
                            hBox.setAlignment(Pos.CENTER_LEFT);
                            VBox vBox = new VBox();
                            vBox.setAlignment(Pos.CENTER_LEFT);
                            Label name = new Label(item.getName() + " " + item.getLastName());
                            name.setTextAlignment(TextAlignment.LEFT);
                            name.setStyle("-fx-font-size: 13; -fx-font-weight: normal;-fx-font-family: 'Roboto Black'");
                            System.out.println(name.getFont().getName());

                            Label user = new Label(item.getUser());
                            user.setTextAlignment(TextAlignment.LEFT);
                            user.setStyle("-fx-font-size: 20; -fx-font-family: 'Robot Medium'");

                            ImageView view = new ImageView();
                            view.setFitHeight(35);
                            view.setFitWidth(35);
                            Rectangle circle = new Rectangle(view.getFitWidth(), view.getFitHeight());
                            circle.setArcHeight(50);
                            circle.setArcWidth(50);
                            view.setClip(circle);
                            view.setImage(item.getPicture());
                            HBox.setMargin(vBox, new Insets(0, 0, 0, 15));
                            vBox.getChildren().addAll(name, user);
                            hBox.getChildren().addAll(view, vBox);
                            AnchorPane.setBottomAnchor(hBox, 5.0);
                            AnchorPane.setTopAnchor(hBox, 5.0);
                            AnchorPane.setRightAnchor(hBox, 0.0);
                            AnchorPane.setLeftAnchor(hBox, 0.0);
                            pane.getChildren().add(hBox);
                            setGraphic(pane);
                        }
                        setText(null);
                    }
                };
            }
        };
    }
}
