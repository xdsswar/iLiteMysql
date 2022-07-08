package com.inv.ui.widgets;

import com.inv.R;
import com.inv.ui.in.IWidget;
import com.inv.ui.util.DoubleFormat;
import com.inv.ui.util.StageHelper;
import com.inv.ui.util.WidgetUtil;
import com.inv.xflux.entity.Currency;
import com.inv.xflux.entity.Item;
import com.inv.xflux.model.CurrencyModel;
import com.xd.controls.XButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author XDSSWAR
 * @param <T> Generic
 */
@SuppressWarnings("all")
public class XItemsListCell<T extends Item>{

    private final Currency currency;
    private final WidgetUtil widgetUtil;
    private final StageHelper stageHelper;

    public XItemsListCell(){
        CurrencyModel currencyModel = new CurrencyModel();
        currency= currencyModel.getDefault();
        widgetUtil=new WidgetUtil();
        stageHelper=new StageHelper();
    }

    public Callback<ListView<T>,ListCell<T>> build(){
        return new Callback<>() {
            @Override
            public ListCell<T> call(ListView<T> param) {
                ListCell<T> cell;
                cell = new ListCell<>() {
                    @Override
                    protected void updateItem(final T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                            getStyleClass().remove("unit-list-cell");
                            getStyleClass().add("unit-list-cell-empty");
                        } else {
                            XItemsListCell<T>.Controller controller = new XItemsListCell<T>.Controller();
                            AnchorPane pane = widgetUtil.addFactoryView("orders/actions/listCell", controller);

                            controller.nameLabel.setText(item.getProduct().getName());
                            controller.qtyField.setText(DoubleFormat.sFormat(item.getQuantity().get()));
                            controller.unitLabel.setText(item.getProduct().getUnit().getSymbol());
                            controller.discField.setText(DoubleFormat.sFormat(item.getDiscount().get()));
                            controller.dcLabel.setText(currency.getSymbol());
                            controller.priceField.setText( DoubleFormat.sFormat(item.getProduct().getSellPrice()));
                            controller.pcLabel.setText(currency.getSymbol());
                            controller.totalField.setText(DoubleFormat.sFormat(item.getTotal().get()));
                            controller.tcLabel.setText(currency.getSymbol());
                            //Button to remove Item
                            controller.removeItemBtn.setOnAction(event -> {
                                getListView().getItems().remove(item);
                            });
                            //Listen to the Quantity Change
                            item.getQuantity().addListener((observable, oldValue, newValue) -> {
                                recalculateFields(item, controller);
                            });

                            //Listen to the Discount Change
                            item.getDiscount().addListener((observable, oldValue, newValue) -> {
                                recalculateFields(item, controller);
                            });

                            item.getTotal().addListener((observable, oldValue, newValue) -> {
                                Platform.runLater(()->{
                                    getListView().refresh();
                                });

                            });

                            //Edit Item Action
                            setOnMouseClicked(event -> {
                                if (event.getClickCount() == 2) {
                                    //Show Editing Form
                                    XItemsListCell<T>.EditController editController=new EditController(item);
                                    showEditDialog(((Stage)getListView().getScene().getWindow()),editController);

                                }
                            });

                            setGraphic(pane);
                        }

                    }
                };

                return cell;
            }
        };
    }

    /**
     * This method will recalculate and set the field values
     * for the ListFactory Item
     * @param item T extends Item
     * @param controller XItemsListCell<T>.Controller type
     */
    private  void recalculateFields(T item, XItemsListCell<T>.Controller controller){
        Runnable task=()->{
            item.setTotal(item.getQuantity().get()*item.getProduct().getSellPrice()-item.getDiscount().get());
            Platform.runLater(()->{
                controller.qtyField.setText(DoubleFormat.sFormat(item.getQuantity().get()));
                controller.discField.setText(DoubleFormat.sFormat(item.getDiscount().get()));
                controller.totalField.setText(DoubleFormat.sFormat(item.getTotal().get()));
            });
        };
        Thread thread=new Thread(task);
        thread.start();
    }

    /**
     * Show Dialog to edit Quantity and Discount
     * @param owner Stage Owner
     * @param controller Controller
     */
    private void showEditDialog(Stage owner,XItemsListCell<T>.EditController controller){
        stageHelper.showAndWaitDiaalog(owner,"orders/actions/itemEdition", controller);
    }
    /**
     * @author XDSSWAR
     * This class is the Factory Controller
     */
    private  class Controller implements IWidget{

        @FXML
        private Label nameLabel;

        @FXML
        private Label qtyField;

        @FXML
        private Label unitLabel;

        @FXML
        private Label discField;

        @FXML
        private Label dcLabel;

        @FXML
        private Label priceField;

        @FXML
        private Label pcLabel;

        @FXML
        private Label totalField;

        @FXML
        private Label tcLabel;

        @FXML
        private XButton removeItemBtn;

        @FXML
        private VBox icVbox;
    }

    /**
     * This Class is the Controller for the Window to edit the Quantity and Discount
     */
    private class EditController implements Initializable, IWidget {
        /**
         * Edit Dialog Controls
         */
        @FXML
        private AnchorPane base;
        @FXML
        private XButton applyDialogBtn;

        @FXML
        private TextField qtyField;

        @FXML
        private TextField discField;

        private final T item;
        private final String title;

        /**
         * Constructor
         * @param item Item
         */
        public EditController(T item){
            this.item=item;
            this.title=item.getProduct().getName();
        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            qtyField.setText(DoubleFormat.sFormat(item.getQuantity().get()));
            discField.setText(DoubleFormat.sFormat(item.getDiscount().get()));
            R.MODAL_STAGE.setTitle(title);

            //Apply Values
            applyDialogBtn.setOnAction(event -> {
                item.getQuantity().set(DoubleFormat.dFormat(Double.parseDouble(qtyField.getText())));
                item.getDiscount().set(DoubleFormat.dFormat(Double.parseDouble(discField.getText())));
                R.MODAL_STAGE.close();
            });

        }
    }



}
