package com.inv.ui.ctrl.views.orders;

import com.inv.R;
import com.inv.data.access.emums.OrderStatus;
import com.inv.data.access.orderutil.OrderUtil;
import com.inv.pdf.PdfExport;
import com.inv.ui.ctrl.dialogs.Message;
import com.inv.ui.in.IWidget;
import com.inv.ui.util.Anima;
import com.inv.ui.util.DoubleFormat;
import com.inv.ui.util.WidgetUtil;
import com.inv.ui.util.WidthAnimation;
import com.inv.ui.widgets.ContextUtil;
import com.inv.ui.widgets.CustomActionCellFactory;
import com.inv.ui.widgets.ListableXListFactory;
import com.inv.ui.widgets.XItemsListCell;
import com.inv.xflux.entity.*;
import com.inv.xflux.model.*;
import com.xd.controls.XButton;
import com.xd.ss.war.awesome.FontAwesomeIconView;
import com.xd.ss.war.materialdesignfont.MaterialDesignIconView;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * @author XDSSWAR
 * This Class manages the order edition/creation . In future versions
 * more features like edit produc's quantity and discount ina existing order, etc
 */
@SuppressWarnings("all")
public final  class Orders implements Initializable, IWidget {
    /**
     * Controls from Order
     */
    @FXML
    private AnchorPane baseOrderContainer, orderTableContainer;

    @FXML
    private AnchorPane orderActionBase;

    @FXML
    private AnchorPane orderActionContainer;

    @FXML
    private TableView<Order> ordersTable;

    @FXML
    private TableColumn<Order, Long> orderIdCol;

    @FXML
    private TableColumn<Order, String> orderClientCol;

    @FXML
    private TableColumn<Order, String> totalCol;

    @FXML
    private TableColumn<Order, String> balanceCol;

    @FXML
    private TableColumn<Order, String> amountCol;

    @FXML
    private TableColumn<Order, String> taxCol;

    @FXML
    private TableColumn<Order, String> dateCol;

    @FXML
    private TableColumn<Order, String> dueDateCol;

    @FXML
    private TableColumn<Order, String> statusCol;

    @FXML
    private TableColumn<Order, Void> actionCol;

    @FXML
    private XButton newOrderBtn,goBackBtn;

    /**
     * Controls from OrderAction
     */

    @FXML
    private AnchorPane innerOrderLeftPane, innerOrderRightPane;

    @FXML
    private ListView<Client> clientsListView;

    @FXML
    private XButton newClientBtn;

    @FXML
    private XButton refreshClientsBtn;

    @FXML
    private TextField clientSearch;

    @FXML
    private ListView<Product> productsListView;

    @FXML
    private XButton newProductBtn;

    @FXML
    private XButton refreshProductBtn;

    @FXML
    private XButton closeNewOrder;

    @FXML
    private TextField productSearch;

    @FXML
    private TextField clientNameText;

    @FXML
    private XButton selectClientBtn;

    @FXML
    private TextField clientEmailText;

    @FXML
    private TextField clientPhoneText;

    @FXML
    private TextField dateTextField;

    @FXML
    private TextField dueDateTextField;

    @FXML
    private Label orderIdLabel;

    @FXML
    private ListView<Item> itemsListView;

    @FXML
    private TextField subtotalField;

    @FXML
    private TextField discountField;

    @FXML
    private TextField taxField;

    @FXML
    private TextField totalField;

    @FXML
    private XButton saveOrderBtn;

    private ClientModel clientModel=null;
    private ObservableList<Client> clientList=FXCollections.observableArrayList();
    private ProductModel productModel=null;
    private ObservableList<Product> productList=FXCollections.observableArrayList();
    private ChangeListener<Number> changeListener;
    private Client orderClient=null;
    private TaxModel taxModel=null;
    private Tax orderTax=null;
    private boolean newOrder;
    private boolean updateOrder;
    private Order editingOrder;
    private int itemsInitInEditingOrder;
    private Item[] editingOrderItems;

    /**
     * Private Controls
     */
    private OrderModel orderModel=null;
    private Currency currency=null;
    private ContextMenu contextMenu;
    private ObservableList<Order> ordersList= FXCollections.observableArrayList();
    private final TextField search;
    private final ContextUtil contextUtil;

    /**
     * Constructor
     * @param search TextSearch
     */
    public Orders( final TextField search, final boolean newOrder){
        this.search=search;
        contextUtil=new ContextUtil();
        this.newOrder=newOrder;
        updateOrder=false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        taxModel=new TaxModel();
        orderTax=taxModel.getDefault();
        orderModel=new OrderModel();
        currency=new CurrencyModel().getDefault();
        contextMenu=new ContextMenu();
        newOrderBtn.setOnAction(event ->{
            newOrder=true;
            showOrderWizard(null);
            newOrder=false;
        });
        goBackBtn.setOnAction(event -> {
            newOrder=false;
            showOrderWizard(null);
            newOrder=true;
        });
        initTableView();
        initContext();
        if (newOrder){
            showOrderWizard(null);
            newOrder=false;
        }else {
            loadData();
        }
    }


    /**
     * Init TableView columns
     * Do not play with this method
     */
    private void initTableView(){
        orderIdCol.setCellValueFactory((TableColumn.CellDataFeatures<Order, Long> order)
                -> new SimpleObjectProperty<>(order.getValue().getId()));

        orderClientCol.setCellValueFactory((TableColumn.CellDataFeatures<Order,String> order)
                ->new SimpleStringProperty(order.getValue().getClient().getName()));

        amountCol.setCellValueFactory((TableColumn.CellDataFeatures<Order, String> order)
                -> new SimpleObjectProperty<>(currency.getSymbol()+DoubleFormat.sFormat(order.getValue().getTotal())));

        taxCol.setCellValueFactory((TableColumn.CellDataFeatures<Order, String> order)
                        -> new SimpleObjectProperty<>(
                        currency.getSymbol()+DoubleFormat.sFormat((order.getValue().getTotalWithTax()-order.getValue().getTotal()))
                )
        );
        totalCol.setCellValueFactory((TableColumn.CellDataFeatures<Order, String> order)
                -> new SimpleObjectProperty<>(currency.getSymbol()+DoubleFormat.sFormat(order.getValue().getTotalWithTax())));

        balanceCol.setCellValueFactory((TableColumn.CellDataFeatures<Order, String> order)
                        -> new SimpleObjectProperty<>(
                        currency.getSymbol()+DoubleFormat.sFormat(
                                order.getValue().getTotalWithTax()-order.getValue().getPaidMoney())
                )
        );
        balanceCol.setCellFactory(new CustomActionCellFactory<Order,String>().buildTableCellForBalance());

        dateCol.setCellValueFactory((TableColumn.CellDataFeatures<Order, String> order)
                -> new SimpleStringProperty(OrderUtil.getOrderDate(order.getValue().getDate())));

        dueDateCol.setCellValueFactory((TableColumn.CellDataFeatures<Order, String> order)
                -> new SimpleStringProperty(OrderUtil.getOrderDate(order.getValue().getDueDate())));

        statusCol.setCellValueFactory((TableColumn.CellDataFeatures<Order, String> order)
                -> new SimpleStringProperty(OrderStatus.getStatusAsString(order.getValue().getStatus())));

        statusCol.setCellFactory(new CustomActionCellFactory<Order,String>().buildTableCellWithBorder());

        actionCol.setCellFactory(
                new CustomActionCellFactory<Order,Void>().buildActionCell(()-> {
                    genPdfAndView(ordersTable.getSelectionModel().getSelectedItem());
                }, ()-> {
                    editOrder(ordersTable.getSelectionModel().getSelectedItem());
                },()-> {
                    if (R.ONLINE_EMPLOYEE.isAdmin() || R.ONLINE_EMPLOYEE.isManager()) {
                        deleteOrder(ordersTable.getSelectionModel().getSelectedItem());
                    } else {
                        showAlert("Access Denied!","You don't have enough rights to perform this action!",Message.MESSAGE_TYPE_ERROR);
                    }
                })
        );

    }

    /**
     * Get all Orders from db
     * This is an important method . it is called at at the first
     * time you load the orders widget and every time you update the table
     * Do not play with it
     */
    private void loadData(){
        Runnable task=()->{
            ordersList.clear();
            ordersList=orderModel.getAll();
            //Add items to the Table
            Platform.runLater(()->{
                ordersTable.setItems(ordersList);
                search.clear();
                initSearchFilter(ordersList,search);
            });
        };
        Thread loadDataThread=new Thread(task);
        loadDataThread.start();
    }

    /**
     * Set and Init the Context Menu in the TableView
     * This method will add a context in every non empty row
     *
     */
    private void initContext(){
        Runnable task=()-> {
            contextMenu.getStyleClass().add("custom-context");
            //Add new
            FontAwesomeIconView newIcon=new FontAwesomeIconView();
            newIcon.setGlyphName("PLUS");
            contextMenu.getItems().add(contextUtil.buildMenuItem("New Order",newIcon,()->{
                newOrder=true;
                showOrderWizard(null);
                newOrder=false;
                ;            }));
            //Refresh Table
            FontAwesomeIconView refresh=new FontAwesomeIconView();
            refresh.setGlyphName("REFRESH");
            contextMenu.getItems().add(contextUtil.buildMenuItem("Refresh", refresh, this::loadData));
            //View Order
            FontAwesomeIconView view=new FontAwesomeIconView();
            view.setGlyphName("EDIT");
            contextMenu.getItems().add(contextUtil.buildMenuItem("Invoice", view, ()-> genPdfAndView(
                    ordersTable.getSelectionModel().getSelectedItem()
            )));
            //Edit
            FontAwesomeIconView edit=new FontAwesomeIconView();
            edit.setGlyphName("EYE");
            contextMenu.getItems().add(contextUtil.buildMenuItem("View Order", edit, ()-> editOrder(
                    ordersTable.getSelectionModel().getSelectedItem()
            )));
            /**
             * Status Menu
             */
            Menu status=new Menu("    Status");
            status.getStyleClass().add("custom-menu-in-context");
            FontAwesomeIconView st=new FontAwesomeIconView();
            st.setGlyphName("RANDOM");
            st.setStyleClass("custom-menuitem-icon");
            st.setGlyphSize(14);
            status.setGraphic(st);
            //Set as Paid
            FontAwesomeIconView pay=new FontAwesomeIconView();
            pay.setGlyphName("CHECK");
            status.getItems().add(contextUtil.buildMenuItem("Paid",pay,()->{
                boolean load=orderModel.setStatus(ordersTable.getSelectionModel().getSelectedItem(), OrderStatus.PAID);
                if (load) {
                    loadData();
                }else {
                    showOrderStatusAlert("Paid");
                }
            }));
            //Set as Unpaid
            MaterialDesignIconView unpaid=new MaterialDesignIconView();
            unpaid.setGlyphName("FLASK_EMPTY");
            status.getItems().add(contextUtil.buildMenuItem("Unpaid",unpaid,()->{
                boolean load= orderModel.setStatus(ordersTable.getSelectionModel().getSelectedItem(),OrderStatus.UNPAID);
                if (load) {
                    loadData();
                }else {
                    showOrderStatusAlert("Unpaid");
                }
            }));
            //Set as Void
            FontAwesomeIconView voids=new FontAwesomeIconView();
            voids.setGlyphName("BAN");
            status.getItems().add(contextUtil.buildMenuItem("Void",voids,()->{
                if (R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin()) {
                    boolean load = orderModel.setStatus(ordersTable.getSelectionModel().getSelectedItem(), OrderStatus.VOID);
                    if (load) {
                        loadData();
                    } else {
                        showOrderStatusAlert("Void");
                    }
                }else {
                    showAlert("Access Denied!","You don't have enough rights to perform this action!",Message.MESSAGE_TYPE_ERROR);
                }
            }));
            //Processing
            FontAwesomeIconView process=new FontAwesomeIconView();
            process.setGlyphName("CLOCK_ALT");
            status.getItems().add(contextUtil.buildMenuItem("Pending",process,()->{
                boolean load= orderModel.setStatus(ordersTable.getSelectionModel().getSelectedItem(),OrderStatus.PENDING);
                if (load) {
                    loadData();
                }else {
                    showOrderStatusAlert("Pending");
                }
            }));
            contextMenu.getItems().addAll(status);

            //Delete
            FontAwesomeIconView delete=new FontAwesomeIconView();
            delete.setGlyphName("CLOSE");
            contextMenu.getItems().add(contextUtil.buildMenuItem("Delete", delete, ()->{
                if (R.ONLINE_EMPLOYEE.isAdmin() || R.ONLINE_EMPLOYEE.isManager()) {
                    deleteOrder(ordersTable.getSelectionModel().getSelectedItem());
                } else {
                    showAlert("Access Denied!","You don't have enough rights to perform this action!",Message.MESSAGE_TYPE_ERROR);
                }
            }));

            //Ad context Menu to each row
            ordersTable.setRowFactory(rf -> {
                TableRow<Order> row = new TableRow<Order>(){
                    @Override
                    protected void updateItem(Order order, boolean empty){
                        super.updateItem(order,empty);
                        if (empty || order==null){
                            getStyleClass().add("empty-row");
                        }else {
                            getStyleClass().remove("empty-row");
                        }
                    }
                };
                row.contextMenuProperty().bind(Bindings.when(row.emptyProperty())
                        .then((ContextMenu) null)
                        .otherwise(contextMenu));

                return row;
            });
        };

        Thread contextThread=new Thread(task);
        contextThread.start();
    }

    /**
     * Generate pdf View Order method
     * @param order Order
     */
    private void genPdfAndView(Order order){
        PdfExport.makePDF(order,baseOrderContainer);
    }

    /**
     * Edit Order
     * @param order order
     */
    private void editOrder(Order order){
        newOrder=true;
        showOrderWizard(order);
        newOrder=false;
    }

    /**
     * Delete Order from Table and database
     * @param order Order to be deleted
     */
    private void deleteOrder(Order order){
        Platform.runLater(()->{
            boolean del=Message.showMessage(baseOrderContainer.getScene().getWindow(),
                    "Warning",
                    "Are you sure you want to permanently remove the Order with id: " + order.getId() + " ?\nThis Action can't be undone.",
                    Message.MESSAGE_TYPE_WARNING);
            if (del) {
                orderModel.delete(order);
                loadData();
                Message.showMessage(baseOrderContainer.getScene().getWindow(),"Success","Order Successfully Removed!",Message.MESSAGE_TYPE_SUCCESS);
            }
        });

    }

    /**
     * WARNING , this method can freeze the UI if used in the wrong place
     * Init Search field
     * @param orderVal List with all the values
     * @param textField TextField instance from the MainCeTrl
     */
    private void initSearchFilter(ObservableList<Order> orderVal,TextField textField){
        FilteredList<Order> filteredList=new FilteredList<>(orderVal, e->true);
        textField.setOnKeyReleased(event -> {
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate((Predicate<? super Order>) ord->{
                    if (newValue.isEmpty()){
                        return true;
                    }
                    String lcF=newValue.toLowerCase();
                    if (Long.toString(ord.getId()).contains(lcF)){
                        return true;
                    }else if (ord.getClient().getName().toLowerCase().contains(lcF)){
                        return true;
                    } else if (ord.getClient().getPhone().toLowerCase().contains(lcF)) {
                        return true;
                    }else if (ord.getClient().getEmail().toLowerCase().contains(lcF)) {
                        return true;
                    }else if (ord.getCreator().getUser().toLowerCase().contains(lcF)) {
                        return true;
                    }else if (ord.getCreator().getName().toLowerCase().contains(lcF)) {
                        return true;
                    }
                    return false;
                });
            });
            SortedList<Order> sortedList=new SortedList<>(filteredList);
            ordersTable.setItems(sortedList);
        });
    }

    /**
     * Show Alert when Order EmployeeStatus is already set
     * @param st String status name
     */
    private void showOrderStatusAlert(String st){
        Runnable task=()->{
            Platform.runLater(()->
                    Message.showMessage(baseOrderContainer.getScene().getWindow(),"Order EmployeeStatus Alert",
                            "The Selected Order is already set as "+st+"!",Message.MESSAGE_TYPE_SUCCESS));
        };
        Thread thread=new Thread(task);
        thread.start();
    }


    /*
     * ==============FROM HERE EVERYTHING IS TO CONTROL THE CREATION AND EDITION OF ORDERS==============================
     */

    /**
     * This variables are very important at the time of Edition or creation
     * of orders , they are globally declared but only used when the Edit/Create orders
     * view is visible or loaded into the Scene , before and after that this vars are unused.
     * Dont play with this variables if you dont really know about Java or Flow Control.
     */
    private double subTotal= 0.00;
    private double discount=0.00;
    private double tax=0.00;
    private double total=0.00;
    private double paidMoney=0.00;

    /**
     * Show or hide the  order Wizard for Editing or Creating.
     * This Method is very important , dont mess with it
     * First the app need to know if we are going to create a new Order
     * or edit a existing one. Depending on that the app will show the
     * required view and set all the required parameters
     */
    private void showOrderWizard(Order order){
        /**
         * Show a filled form for updating a existing Order
         */
        WidgetUtil widgetUtil=new WidgetUtil();
        Anima anima=new Anima();
        if (order!=null && newOrder){
            //Make ready all the required parameters
            editingOrder = order;
            orderClient = order.getClient();
            itemsInitInEditingOrder=order.getItems().size();
            subTotal = order.getTotal();
            for (Item item : order.getItems()) {
                discount = discount + item.getDiscount().get();
            }
            tax = order.getTotalWithTax() - order.getTotal();
            total = order.getTotalWithTax();
            paidMoney = order.getPaidMoney();

            Platform.runLater(() -> {
                orderTableContainer.toBack();
                orderTableContainer.setOpacity(0);
                orderActionBase.setOpacity(0);
                anima.fadeOutTransition(newOrderBtn);
                newOrderBtn.setOpacity(0);
                newOrderBtn.toBack();
                anima.fadeInTransition(goBackBtn);
                widgetUtil.addViewWithController(orderActionBase, "orders/orderAction", this);
                anima.fadeInTransitionFast(orderActionBase);
                clientNameText.setText(order.getClient().getName());
                clientEmailText.setText(order.getClient().getEmail());
                clientPhoneText.setText(order.getClient().getPhone());
                dateTextField.setText(OrderUtil.getOrderDate(order.getDate()));
                if (order.getDueDate() != null) {
                    dueDateTextField.setText(OrderUtil.getOrderDate(order.getDueDate()));
                }

                itemsListView.setCellFactory(new XItemsListCell<>().build());
                itemsListView.setItems(order.getItems());
                editingOrderItems=order.getItems().toArray(Item[]::new);

                orderIdLabel.setText("Order #" + order.getId());
                subtotalField.setText(currency.getSymbol() + DoubleFormat.sFormat(subTotal));
                discountField.setText(currency.getSymbol() + DoubleFormat.sFormat(discount));
                taxField.setText(currency.getSymbol() + DoubleFormat.sFormat(tax));
                totalField.setText(currency.getSymbol() + DoubleFormat.sFormat(total - paidMoney));
                loadProductsView();
                itemsListView.setDisable(true);
                productsListView.setDisable(true);
                selectClientBtn.setDisable(true);
                updateOrder = true;
                if (total-paidMoney==0){
                    saveOrderBtn.setDisable(true);
                }

                /**
                 * Attach the events in order to listen to the total changes for any item.
                 *
                 */
                initOrderActionEvents();
                order.getItems().forEach(item -> {
                    item.getTotal().addListener((observable, oldValue, newValue) -> {
                        reCalculateForEditingOrder();
                    });
                });
            });
        }
        else if (newOrder) {
            /**
             * Show A clean Form to create new orders
             */
            subTotal = 0.00;
            discount = 0.00;
            tax = 0.00;
            total = 0.00;
            paidMoney = 0.00;
            final long orderId = orderModel.getLastId() + 1;
            Platform.runLater(() -> {
                orderTableContainer.toBack();
                orderTableContainer.setOpacity(0);
                orderActionBase.setOpacity(0);
                anima.fadeOutTransition(newOrderBtn);
                newOrderBtn.setOpacity(0);
                newOrderBtn.toBack();
                anima.fadeInTransition(goBackBtn);
                widgetUtil.addViewWithController(orderActionBase, "orders/orderAction", this);
                anima.fadeInTransitionFast(orderActionBase);
                itemsListView.setCellFactory(new XItemsListCell<>().build());
                orderIdLabel.setText("Order #" + orderId);
                subtotalField.setText(currency.getSymbol() + "0.00");
                discountField.setText(currency.getSymbol() + "0.00");
                taxField.setText(currency.getSymbol() + "0.00");
                totalField.setText(currency.getSymbol() + "0.00");
                updateOrder=false;
                saveOrderBtn.setDisable(false);
                initOrderActionEvents();

            });
        }
        else {
            /**
             * Show normal Orders View
             */
            itemsInitInEditingOrder=0;
            loadData();
            Platform.runLater(() -> {
                orderActionBase.toBack();
                orderActionBase.setOpacity(0);
                orderTableContainer.setOpacity(0);
                newOrderBtn.setOpacity(0);
                anima.fadeOutTransition(goBackBtn);
                goBackBtn.toBack();
                goBackBtn.setOpacity(0);
                anima.fadeInTransition(orderTableContainer);
                anima.fadeInTransition(newOrderBtn);
                updateOrder=false;
            });
        }
    }


    /**
     * Clear inner left Pane and make it ready for Loading new Content
     */
    protected void clearInnerLeftPane(){
        innerOrderLeftPane.getChildren().clear();
    }

    /**
     * Load Clients List to select client for the order
     */
    protected void loadClientsView(){
        WidgetUtil widgetUtil=new WidgetUtil();
        WidthAnimation widthAnimation=new WidthAnimation();
        if (innerOrderLeftPane.getWidth()>299){
            widthAnimation.queueAnimation(innerOrderLeftPane,
                    0,innerOrderRightPane,
                    0).setOnFinished(event -> {
                clearInnerLeftPane();
                widgetUtil.addViewWithController(innerOrderLeftPane,"orders/actions/clients",this);
                initClients();
                widthAnimation.widthAnimation(innerOrderLeftPane,300,innerOrderRightPane,300);
                initClientsEvents();
            });
        }else {
            clearInnerLeftPane();
            widgetUtil.addViewWithController(innerOrderLeftPane,"orders/actions/clients",this);
            initClients();
            widthAnimation.widthAnimation(innerOrderLeftPane, 300, innerOrderRightPane, 300);
            initClientsEvents();
        }

    }

    /**
     * Load Products list to select to the order
     */
    protected void loadProductsView(){
        WidthAnimation widthAnimation=new WidthAnimation();
        WidgetUtil widgetUtil=new WidgetUtil();
        if (innerOrderLeftPane.getWidth()>299){
            widthAnimation.queueAnimation(innerOrderLeftPane,
                    0,innerOrderRightPane,
                    0).setOnFinished(event -> {
                clearInnerLeftPane();
                widgetUtil.addViewWithController(innerOrderLeftPane,"orders/actions/products",this);
                initProducts();
                widthAnimation.widthAnimation(innerOrderLeftPane,300,innerOrderRightPane,300);
                initProductsEvents();
            });
        }else {
            clearInnerLeftPane();
            widgetUtil.addViewWithController(innerOrderLeftPane,"orders/actions/products",this);
            initProducts();
            widthAnimation.widthAnimation(innerOrderLeftPane, 300, innerOrderRightPane, 300);
            initProductsEvents();
        }

    }

    /**
     * Init Clients View
     */
    protected void initClients(){
        clientsListView.setCellFactory(new ListableXListFactory<Client>().build("#303960","USER",
                false, null,()->{
                    //Add here code to set order client on double click
                    orderClient=clientsListView.getSelectionModel().getSelectedItem();
                    Platform.runLater(()->{
                        clientNameText.setText(orderClient.getName());
                        clientEmailText.setText(orderClient.getEmail());
                        clientPhoneText.setText(orderClient.getPhone());
                    });
                    //Load Products after selecting the Order Client
                    loadProductsView();
                },null));
        loadClientData();
    }

    /**
     * Init Products View
     */
    protected void initProducts(){
        productsListView.setCellFactory(new ListableXListFactory<Product>().build("#8e44ad","CUBES",
                false , null,()->{
                    //Add Product to the Items List
                    Product prod=productsListView.getSelectionModel().getSelectedItem();
                    if (prod.getQuantity()>0) {
                        Item addItem=new Item(prod, 1, 0);
                        Platform.runLater(()->itemsListView.getItems().add(addItem));

                    }else {
                        Platform.runLater(()->{
                            Message.showMessage(baseOrderContainer.getScene().getWindow(),
                                    "Out of stock!","This Product is out of stock!",
                                    Message.MESSAGE_TYPE_ERROR);
                        });
                    }
                },null));
        loadProductData();
    }

    /**
     * Load all Products
     */
    private void loadProductData(){
        productSearch.clear();
        Runnable task=()->{
            productModel=new ProductModel();
            productList=productModel.getAll();
            Duration duration = Duration.millis(0);
            Timeline timeline = new Timeline(
                    new KeyFrame(duration,new KeyValue(productsListView.itemsProperty(),productList, Interpolator.EASE_BOTH))
            );
            timeline.play();
            //Add items
            Platform.runLater(()->{
                //Hack Around for java 11
                productsListView.setItems(productList);
                initSearchProductFilter(productList,productSearch);
            });
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    /**
     * WARNING , this method can freeze the UI if used in the wrong place
     * Init Search field
     * @param prodVal List with all the values
     * @param textField TextField instance from the MainCeTrl
     */
    private void initSearchProductFilter(ObservableList<Product> prodVal, TextField textField) {
        FilteredList<Product> filteredList = new FilteredList<>(prodVal, e -> true);
        textField.setOnKeyReleased(event -> {
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate((Predicate<? super Product>) product -> {
                    if (newValue.isEmpty()) {
                        return true;
                    }
                    String lcF = newValue.toLowerCase();
                    if (product.getName().toLowerCase().contains(lcF)) {
                        return true;
                    } else if (product.getCategory().getName().toLowerCase().contains(lcF)) {
                        return true;
                    } else if (product.getBrand().getName().toLowerCase().contains(lcF)) {
                        return true;
                    } else if (product.getProvider().getName().toLowerCase().contains(lcF)) {
                        return true;
                    } else if (product.getProvider().getEmail().toLowerCase().contains(lcF)) {
                        return true;
                    } else if (product.getProvider().getPhone().toLowerCase().contains(lcF)) {
                        return true;
                    }
                    return false;
                });
            });
            SortedList<Product> sortedList = new SortedList<>(filteredList);
            productsListView.setItems(sortedList);
        });
    }

    /**
     * Load all Clients
     */
    private void loadClientData(){
        clientSearch.clear();
        Runnable task=()->{
            clientModel=new ClientModel();
            clientList=clientModel.getAll();
            Duration duration = Duration.millis(0);
            Timeline timeline = new Timeline(
                    new KeyFrame(duration,new KeyValue(clientsListView.itemsProperty(),clientList, Interpolator.EASE_BOTH))
            );
            timeline.play();
            //Add items to the Table
            Platform.runLater(()->{
                //Hack Around for java 11
                clientsListView.setItems(clientList);
                initSearchClientFilter(clientList,clientSearch);
            });
        };
        Thread loadDataThread=new Thread(task);
        loadDataThread.start();
    }

    /**
     * Search Filter for Clients
     * @param clientVal Clients list
     * @param textField search field
     */
    private void initSearchClientFilter(ObservableList<Client> clientVal, TextField textField){
        FilteredList<Client> filteredList=new FilteredList<>(clientVal, e->true);
        textField.setOnKeyReleased(event -> {
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate((Predicate<? super Client>) client->{
                    if (newValue.isEmpty()){
                        return true;
                    }
                    String lcF=newValue.toLowerCase();
                    if (client.getName().toLowerCase().contains(lcF)){
                        return true;
                    } else if (client.getEmail().toLowerCase().contains(lcF)) {
                        return true;
                    } else if (client.getPhone().toLowerCase().contains(lcF)) {
                        return true;
                    }
                    return false;
                });
            });
            SortedList<Client> sortedList=new SortedList<>(filteredList);
            clientsListView.setItems(sortedList);
        });
    }

    /**
     * Add de Listener to the Item listview
     * This will listen for any changes made to the items on the list
     * removed or added
     */
    private void addItemListChangeListener(){
        itemsListView.getItems().addListener((ListChangeListener<Item>) c -> {
            if (itemsListView.getItems().size()>0) {
                for (Item item : itemsListView.getItems()) {
                    item.getTotal().addListener(changeListener);
                }
                if (updateOrder && itemsListView.getItems().size()>editingOrderItems.length){
                    reCalculateForEditingOrder();
                }
                else if (updateOrder && itemsListView.getItems().size()==editingOrderItems.length){
                    subTotal = editingOrder.getTotal();
                    for (Item item : editingOrderItems) {
                        discount = discount + item.getDiscount().get();
                    }
                    tax = editingOrder.getTotalWithTax() - editingOrder.getTotal();
                    total = editingOrder.getTotalWithTax();
                    paidMoney =editingOrder.getPaidMoney();

                    Platform.runLater(()->{
                        subtotalField.setText(currency.getSymbol() + DoubleFormat.sFormat(subTotal));
                        discountField.setText(currency.getSymbol() + DoubleFormat.sFormat(discount));
                        taxField.setText(currency.getSymbol() + DoubleFormat.sFormat(tax));
                        totalField.setText(currency.getSymbol() + DoubleFormat.sFormat(total - paidMoney));
                    });

                }
                else {
                    calculate();
                }
            }else {
                subTotal=0.00;
                discount=0.00;
                tax=0.00;
                total=0.00;
                Platform.runLater(()->{
                    subtotalField.setText(currency.getSymbol()+"0.00");
                    discountField.setText(currency.getSymbol()+"0.00");
                    taxField.setText(currency.getSymbol()+"0.00");
                    totalField.setText(currency.getSymbol()+"0.00");
                });
            }

        });
    }
    /**
     * This Method contains all the methods for creating and editing orders
     * This method need to be called after the orderAction.fxml is loaded
     */
    private void initOrderActionEvents(){
        //Init the ChangeListener for the Items Quantity
        changeListener= (observable, oldValue, newValue) -> {
            calculate();
        };
        //Close the new Order Wizard
        closeNewOrder.setOnAction(event -> {
            showOrderWizard(null);
        });

        //Select Client
        selectClientBtn.setOnAction(event -> {
            loadClientsView();
        });

        //Detect changes on Items List
        addItemListChangeListener();

        /*
         * This event is very important
         * Before saving an Order we need to check certain
         * parameters in order to insert it to the MySql database
         */
        saveOrderBtn.setOnAction(event -> {
            Runnable task=()-> {
                if (updateOrder) {
                    /**
                     * For now only we can update the pay amount.
                     * In future versions editing the items and adding will be supported
                     */
                    if (itemsListView.getItems().size()>0){
                        paidMoney = Double.parseDouble(totalField.getText().replaceAll("[^0-9.]", ""));
                        if (paidMoney!=0){
                            //Set Order Status
                            OrderStatus status = OrderStatus.VOID;
                            if (paidMoney + editingOrder.getPaidMoney() == 0) {
                                status = OrderStatus.UNPAID;
                            } else if (paidMoney + editingOrder.getPaidMoney() > 0 && paidMoney + editingOrder.getPaidMoney() < total) {
                                status = OrderStatus.PENDING;
                            } else if (paidMoney + editingOrder.getPaidMoney() == editingOrder.getTotalWithTax()) {
                                status = OrderStatus.PAID;
                            }


                            Order order = new Order(orderClient, status, itemsListView.getItems(),
                                    subTotal, orderTax, total,
                                    paidMoney + editingOrder.getPaidMoney(),
                                    R.ONLINE_EMPLOYEE, null);

                            orderModel.update(editingOrder, order);
                            showOrderWizard(null);

                        }else {
                            showOrderWizard(null);
                        }
                    }

                }else {
                    //Check if Item List is Empty
                    //Save new Order
                    if (itemsListView.getItems().size() > 0) {
                        OrderStatus status = OrderStatus.VOID;
                        paidMoney = Double.parseDouble(totalField.getText().replaceAll("[^0-9.]", ""));
                        if (paidMoney == 0) {
                            status = OrderStatus.UNPAID;
                        } else if (paidMoney > 0 && paidMoney < total) {
                            status = OrderStatus.PENDING;
                        } else if (paidMoney == total) {
                            status = OrderStatus.PAID;
                        }
                        ObservableList<Item> items=itemsListView.getItems();
                        //Update the Product Quantity
                        items.forEach(item -> {
                            productModel.updateQuantity(
                                    item.getProduct(),
                                    item.getProduct().getQuantity()-item.getQuantity().get()
                            );
                        });
                        Order order = new Order(orderClient, status, items,
                                subTotal, orderTax, total, paidMoney, R.ONLINE_EMPLOYEE, null);
                        orderModel.insert(order);
                        showOrderWizard(null);
                    } else {
                        showAlert("Error!", "You can not save an empty Order!\n" +
                                "Please add some Products to the Order.", Message.MESSAGE_TYPE_ERROR);
                    }
                }
                orderClient=null;
                editingOrder=null;

            };
            Thread thread =new Thread(task);
            thread.start();
        });

    }

    /**
     * All Events related to Clients View
     */
    private void initClientsEvents(){
        //Refresh Clients
        refreshClientsBtn.setOnAction(event -> {
            loadClientData();
        });
    }

    /**
     * All Events related to Products View
     */
    private void initProductsEvents(){
        //Refresh Products
        refreshProductBtn.setOnAction(event -> {
            loadProductData();
        });
    }

    /**
     * This Method will calculate all the money and set the values to
     * the respective Fields. Do not play with it. Dont use it when
     * Editing Orders
     */
    private void calculate(){
        Runnable task=()->{
            subTotal=0.00;
            discount=0.00;
            tax=0.00;
            total=0.00;
            for (Item item:itemsListView.getItems()) {
                subTotal = DoubleFormat.dFormat(subTotal + item.getTotal().get());
                discount = DoubleFormat.dFormat(discount + item.getDiscount().get());
                tax = DoubleFormat.dFormat(tax + (item.getTotal().get() * (orderTax.getTaxVal() / 100)));
            }
            total=DoubleFormat.dFormat(subTotal+tax);
            Platform.runLater(()->{
                subtotalField.setText(currency.getSymbol()+DoubleFormat.sFormat(subTotal));
                discountField.setText(currency.getSymbol()+DoubleFormat.sFormat(discount));
                taxField.setText(currency.getSymbol()+DoubleFormat.sFormat(tax));
                totalField.setText(currency.getSymbol()+DoubleFormat.sFormat(total));
            });
        };
        Thread thread = new Thread(task);
        thread.start();

    }

    /**
     * This method is used to recalculate the money in case you add or remove
     * Items for an Existent Order. Do not use this for new Orders
     */
    private void reCalculateForEditingOrder(){
        Runnable task=()->{
            subTotal=0.00;
            discount=0.00;
            tax=0.00;
            total=0.00;

            double tempSubTotal=0;
            double tempDiscount=0;
            double tempTax=0;
            double tempTotal=0;

            for (Item item:editingOrderItems){
                tempSubTotal=tempSubTotal+item.getTotal().get();
                tempDiscount=tempDiscount+item.getDiscount().get();
                tempTax=tax+(item.getTotal().get()*(orderTax.getTaxVal()/100));
            }
            tempTotal=tempSubTotal+tempTax;

            for (Item item:itemsListView.getItems()) {
                subTotal = DoubleFormat.dFormat(subTotal + item.getTotal().get());
                discount = DoubleFormat.dFormat(discount + item.getDiscount().get());
                tax = DoubleFormat.dFormat(tax + (item.getTotal().get() * (orderTax.getTaxVal() / 100)));
            }
            subTotal=subTotal-tempSubTotal;
            discount=discount-tempDiscount;
            tax=tax-tempTax;

            total=DoubleFormat.dFormat(subTotal+tax);
            Platform.runLater(()->{
                subtotalField.setText(currency.getSymbol()+DoubleFormat.sFormat(subTotal));
                discountField.setText(currency.getSymbol()+DoubleFormat.sFormat(discount));
                taxField.setText(currency.getSymbol()+DoubleFormat.sFormat(tax));
                totalField.setText(currency.getSymbol()+DoubleFormat.sFormat(total));
            });
        };
        Thread thread = new Thread(task);
        thread.start();
    }


    /**
     * Show Alert
     * @param title Title
     * @param message Message
     * @param messageType Type
     */
    private void showAlert(String title, String message, final short messageType){
        Platform.runLater(()->{
            Message.showMessage(baseOrderContainer.getScene().getWindow(),title,message,messageType);
        });
    }

}


