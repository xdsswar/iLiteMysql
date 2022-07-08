package com.inv.ui.ctrl.views.products;

import com.inv.R;
import com.inv.ui.ctrl.dialogs.Message;
import com.inv.ui.in.IWidget;
import com.inv.ui.util.DecimalFormatter;
import com.inv.ui.util.IndexFinder;
import com.inv.ui.widgets.ListableXListFactory;
import com.inv.ui.widgets.XComboboxFactory;
import com.inv.xflux.entity.*;
import com.inv.xflux.model.*;
import com.xd.controls.XButton;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * @author XDSSWAR
 */
@SuppressWarnings("Duplicates")
public final class Products implements Initializable, IWidget {
    @FXML
    private ListView<Product> listView;

    @FXML
    private XButton newBtn;

    @FXML
    private XButton refreshBtn;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField nameText;

    @FXML
    private XButton applyButton;

    @FXML
    private TextArea descriptionText;

    @FXML
    private ComboBox<Category> categoryCombobox;

    @FXML
    private ComboBox<Brand> brandCombobox;

    @FXML
    private ComboBox<Provider> providerCombobox;

    @FXML
    private ComboBox<Unit> unitCombobox;

    @FXML
    private TextField stockText;

    @FXML
    private TextField listingText;

    @FXML
    private TextField retailText;

    private final TextField search;
    private ObservableList<Product> productList= FXCollections.observableArrayList();
    private ObservableList<Brand> brandList=FXCollections.observableArrayList();
    private ObservableList<Provider> providerList=FXCollections.observableArrayList();
    private ObservableList<Category> categoryList=FXCollections.observableArrayList();
    private ObservableList<Unit> unitList=FXCollections.observableArrayList();
    private ProductModel productModel;
    private BrandModel brandModel;
    private ProviderModel providerModel;
    private CategoryModel categoryModel;
    private UnitModel unitModel;
    private static boolean editing=false;

    /**
     * Constructor
     * @param search TextField from MainCtrl
     */
    public Products(final TextField search){
        this.search=search;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPane.setFitToWidth(true);
        productModel=new ProductModel();
        brandModel=new BrandModel();
        providerModel=new ProviderModel();
        categoryModel=new CategoryModel();
        unitModel=new UnitModel();
        initAllLists();
        initList();
        loadData();

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            disableForm(true);
            clear();
        });

        /*
         *Listener to remove the Error StyleClass
         */
        initInputs();

        /*
         * Enable Form to create new Products
         */
        newBtn.setOnAction(event -> {
            if (R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin()) {
                clear();
                disableForm(false);
            }else {
                Message.showMessage(scrollPane.getScene().getWindow(),"Security Alert!","Only Admins and Managers can create new Products!",
                        Message.MESSAGE_TYPE_ERROR);
            }
            editing = false;
        });

        /*
         * Refresh List
         */
        refreshBtn.setOnAction(event -> {
            loadData();
        });

        /*
         * Save Product
         */
        applyButton.setOnAction(event -> {
            if (R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin()) {
                if (nameText.getText().isEmpty()) {
                    nameText.getStyleClass().add("unit-input-error");
                }
                if (categoryCombobox.getSelectionModel().isEmpty()) {
                    categoryCombobox.getStyleClass().add("products-error-combobox");
                }
                if (brandCombobox.getSelectionModel().isEmpty()) {
                    brandCombobox.getStyleClass().add("products-error-combobox");
                }
                if (unitCombobox.getSelectionModel().isEmpty()) {
                    unitCombobox.getStyleClass().add("products-error-combobox");
                }
                if (providerCombobox.getSelectionModel().isEmpty()) {
                    providerCombobox.getStyleClass().add("products-error-combobox");
                }

                //Check the Double values
                boolean stockOk = true;
                if (!checkDoubleParsing(stockText)) {
                    stockText.getStyleClass().add("unit-input-error");
                    stockOk = false;
                }
                boolean listingOk = true;
                if (!checkDoubleParsing(listingText)) {
                    listingText.getStyleClass().add("unit-input-error");
                    listingOk = false;
                }
                boolean retailOk = true;
                if (!checkDoubleParsing(retailText)) {
                    retailText.getStyleClass().add("unit-input-error");
                    retailOk = false;
                }

                if (stockOk && listingOk && retailOk) {
                    Runnable task = () -> {
                        if (editing) {
                            editing = !editProduct(listView.getSelectionModel().getSelectedItem(),
                                    nameText.getText(), descriptionText.getText(), categoryCombobox.getSelectionModel().getSelectedItem(),
                                    providerCombobox.getSelectionModel().getSelectedItem(), brandCombobox.getSelectionModel().getSelectedItem(),
                                    unitCombobox.getSelectionModel().getSelectedItem(), stockText, listingText, retailText);
                        } else {
                            saveProduct(nameText.getText(), descriptionText.getText(), categoryCombobox.getSelectionModel().getSelectedItem(),
                                    providerCombobox.getSelectionModel().getSelectedItem(), brandCombobox.getSelectionModel().getSelectedItem(),
                                    unitCombobox.getSelectionModel().getSelectedItem(), stockText, listingText, retailText);
                        }
                    };
                    Thread thread = new Thread(task);
                    thread.start();
                } else {
                    showAlert("Alert!", "Some fields are empty or have a wrong value!", Message.MESSAGE_TYPE_ERROR);
                }

            } else {
                showAlert("Access Denied!","You don't have enough rights to perform this action!",Message.MESSAGE_TYPE_ERROR);
            }

        });

    }

    /**
     * Init all inputs properties
     */
    private void initInputs(){
        nameText.textProperty().addListener((observable, oldValue, newValue) -> {
            nameText.getStyleClass().remove("unit-input-error");
        });
        categoryCombobox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            categoryCombobox.getStyleClass().remove("products-error-combobox");
        });
        brandCombobox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            brandCombobox.getStyleClass().remove("products-error-combobox");
        });
        providerCombobox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            providerCombobox.getStyleClass().remove("products-error-combobox");
        });
        unitCombobox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            unitCombobox.getStyleClass().remove("products-error-combobox");
        });
        stockText.textProperty().addListener((observable, oldValue, newValue) -> {
            stockText.getStyleClass().remove("unit-input-error");
        });
        stockText.setTextFormatter(new DecimalFormatter(0,2));

        listingText.textProperty().addListener((observable, oldValue, newValue) -> {
            listingText.getStyleClass().remove("unit-input-error");
        });
        listingText.setTextFormatter(new DecimalFormatter(0,2));

        retailText.textProperty().addListener((observable, oldValue, newValue) -> {
            retailText.getStyleClass().remove("unit-input-error");
        });
        retailText.setTextFormatter(new DecimalFormatter(0,2));
    }
    /**
     * Check double Conversion and return boolean
     * @param field TextField
     * @return true when double conversion is ok
     */
    private boolean checkDoubleParsing(TextField field){
        boolean toReturn=false;
        if (!field.getText().isEmpty()){
            try {
                Double.parseDouble(field.getText());
                toReturn=true;
            }catch (Exception e) {
                field.getStyleClass().add("unit-input-error");
            }
        }
        return toReturn;
    }


    /**
     * Convert String to Double
     * @param value String
     * @return double value
     */
    private double convertToDouble(TextField value){
        double result=0;
        if (checkDoubleParsing(value)){
            result=Double.parseDouble(value.getText());
        }
        return result;
    }


    /**
     * Init the ListView Properties and all the Threads associated
     */
    private void  initList(){
        //Add new Unit
        final Runnable add=()->{
            editing=false;
            fillForm(listView.getSelectionModel().getSelectedItem(),false);
        };
        //Edit the Unit
        final Runnable edit=()->{
            editing=true;
            fillForm(listView.getSelectionModel().getSelectedItem(),true);
        };

        //Delete the Unit if not used
        final Runnable delete=()->{
            editing=false;
            if (!productModel.isUsed(listView.getSelectionModel().getSelectedItem())){
                Platform.runLater(()->{
                    boolean del=Message.showMessage(scrollPane.getScene().getWindow(), "Warning!",
                            "Are you sure you want tp proceed?. This action can't be undone.",
                            Message.MESSAGE_TYPE_WARNING);
                    if (del){
                        Runnable r=()->{
                            productModel.delete(listView.getSelectionModel().getSelectedItem());
                            loadData();
                            Platform.runLater(()->{
                                clear();
                                disableForm(true);
                                Message.showMessage(scrollPane.getScene().getWindow(), "Success!",
                                        "Successfully Deleted!",
                                        Message.MESSAGE_TYPE_SUCCESS);
                            });
                        };
                        r.run();
                    }
                });
            }else {
                showAlert("Alert!","You can edit, but you cannot delete a used Product!",
                        Message.MESSAGE_TYPE_ERROR);
            }
        };
        listView.setCellFactory(new ListableXListFactory<Product>().build("#8e44ad","CUBES",
                R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin() , add,edit,delete));
    }

    /**
     * Init all Lists data
     */
    private void initAllLists(){
        Runnable task=()->{
            brandList=brandModel.getAll();
            providerList=providerModel.getAll();
            categoryList=categoryModel.getAll();
            unitList=unitModel.getAll();
            Platform.runLater(this::initAllCombobox);
        };
        Thread thread=new Thread(task);
        thread.start();
    }

    /**
     * Load all Products
     */
    private void loadData(){
        search.clear();
        Runnable task=()->{
            productList=productModel.getAll();
            Duration duration = Duration.millis(200);
            Timeline timeline = new Timeline(
                    new KeyFrame(duration,new KeyValue(listView.itemsProperty(),productList, Interpolator.EASE_BOTH))
            );
            timeline.play();
            //Add items
            Platform.runLater(()->{
                //Hack Around for java 11
                listView.setItems(productList);
                search.clear();
                initSearchFilter(productList,search);
            });
        };
        Thread thread = new Thread(task);
        thread.start();
        disableForm(true);
        clear();
    }

    /**
     * Setup all Combobox Stuff
     */
    private void initAllCombobox(){
        categoryCombobox.setButtonCell(new XComboboxFactory<Category>().build().call(null));
        categoryCombobox.setCellFactory(new XComboboxFactory<Category>().build());
        categoryCombobox.setItems(categoryList);
        brandCombobox.setButtonCell(new XComboboxFactory<Brand>().build().call(null));
        brandCombobox.setCellFactory(new XComboboxFactory<Brand>().build());
        brandCombobox.setItems(brandList);
        providerCombobox.setButtonCell(new XComboboxFactory<Provider>().build().call(null));
        providerCombobox.setCellFactory(new XComboboxFactory<Provider>().build());
        providerCombobox.setItems(providerList);
        unitCombobox.setButtonCell(new XComboboxFactory<Unit>().build().call(null));
        unitCombobox.setCellFactory(new XComboboxFactory<Unit>().build());
        unitCombobox.setItems(unitList);
    }

    /**
     * Disable or enable the Form
     * @param disable boolean
     */
    private void disableForm(boolean disable){
        Platform.runLater(()->{
            nameText.setDisable(disable);
            categoryCombobox.setDisable(disable);
            brandCombobox.setDisable(disable);
            providerCombobox.setDisable(disable);
            unitCombobox.setDisable(disable);
            stockText.setDisable(disable);
            listingText.setDisable(disable);
            retailText.setDisable(disable);
            descriptionText.setDisable(disable);
            applyButton.setDisable(disable);
            nameText.getStyleClass().remove("unit-input-error");
            categoryCombobox.getStyleClass().remove("products-error-combobox");
            brandCombobox.getStyleClass().remove("products-error-combobox");
            providerCombobox.getStyleClass().remove("products-error-combobox");
            unitCombobox.getStyleClass().remove("products-error-combobox");
            stockText.getStyleClass().remove("unit-input-error");
            listingText.getStyleClass().remove("unit-input-error");
            retailText.getStyleClass().remove("unit-input-error");
        });

    }

    /**
     * Clear all
     */
    private void clear(){
        Platform.runLater(()->{
            nameText.clear();
            categoryCombobox.getSelectionModel().clearSelection();
            brandCombobox.getSelectionModel().clearSelection();
            providerCombobox.getSelectionModel().clearSelection();
            unitCombobox.getSelectionModel().clearSelection();
            stockText.clear();
            listingText.clear();
            retailText.clear();
            descriptionText.clear();
        });

    }

    /**
     * WARNING , this method can freeze the UI if used in the wrong place
     * Init Search field
     * @param prodVal List with all the values
     * @param textField TextField instance from the MainCeTrl
     */
    private void initSearchFilter(ObservableList<Product> prodVal, TextField textField){
        FilteredList<Product> filteredList=new FilteredList<>(prodVal, e->true);
        textField.setOnKeyReleased(event -> {
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate((Predicate<? super Product>) product->{
                    if (newValue.isEmpty()){
                        return true;
                    }
                    String lcF=newValue.toLowerCase();
                    if (product.getName().toLowerCase().contains(lcF)){
                        return true;
                    } else if (product.getCategory().getName().toLowerCase().contains(lcF)) {
                        return true;
                    }else if (product.getBrand().getName().toLowerCase().contains(lcF)){
                        return true;
                    }else if (product.getProvider().getName().toLowerCase().contains(lcF)){
                        return true;
                    }else if (product.getProvider().getEmail().toLowerCase().contains(lcF)){
                        return true;
                    }else if (product.getProvider().getPhone().toLowerCase().contains(lcF)){
                        return true;
                    }
                    return false;
                });
            });
            SortedList<Product> sortedList=new SortedList<>(filteredList);
            listView.setItems(sortedList);
        });
    }


    /**
     * Util to show Alerts on Product saving
     * @param title Title
     * @param message Message
     * @param messageType Type
     */
    private void showAlert(String title, String message, final short messageType){
        Platform.runLater(()->{
            Message.showMessage(scrollPane.getScene().getWindow(),title,message,messageType);
        });
    }


    /**
     * Fill The form
     * @param value Product
     * @param edit boolean editing
     */
    private void fillForm(Product value, boolean edit){
        if (value!=null){
            if (edit){
                Runnable task=()->{
                    //Get all required indexes fro each Combobox
                    IndexFinder<Category> categoryIndexFinder=new IndexFinder<>();
                    int catIndex= categoryIndexFinder.find(categoryCombobox.getItems(),value.getCategory());
                    IndexFinder<Brand> brandIndexFinder=new IndexFinder<>();
                    int brandIndex=brandIndexFinder.find(brandCombobox.getItems(),value.getBrand());
                    IndexFinder<Provider> providerIndexFinder=new IndexFinder<>();
                    int providerIndex=providerIndexFinder.find(providerCombobox.getItems(),value.getProvider());
                    IndexFinder<Unit> unitIndexFinder=new IndexFinder<>();
                    int unitIndex=unitIndexFinder.find(unitCombobox.getItems(),value.getUnit());

                    Platform.runLater(()-> {
                        categoryCombobox.getSelectionModel().select(catIndex>=0?catIndex:1);
                        brandCombobox.getSelectionModel().select(brandIndex);
                        providerCombobox.getSelectionModel().select(providerIndex);
                        unitCombobox.getSelectionModel().select(unitIndex);
                        nameText.setText(value.getName());
                        stockText.setText(value.getQuantity() + "");
                        listingText.setText(value.getBuyCost() + "");
                        retailText.setText(value.getSellPrice() + "");
                        descriptionText.setText(value.getDescription());
                    });
                };

                Thread thread=new Thread(task);
                thread.start();
            }else {
                clear();
            }
            disableForm(false);
        }
    }


    /***
     * Save new Product
     * @param name Name
     * @param description Description
     * @param category Category
     * @param provider Provider
     * @param brand Brand
     * @param unit Unit
     * @param quantity Quantity or Stock
     * @param buyCost Listing Price
     * @param sellPrice Retail Price
     */
    private void saveProduct(String name, String description, Category category,Provider provider,Brand brand,Unit unit,
                             TextField quantity,TextField buyCost,TextField sellPrice){
        Runnable task=()->{
            if (!name.isEmpty() && category!=null && provider!=null && brand!=null && unit!=null) {
                if (!productModel.alreadyExist(name)) {
                    Product product = new Product(name, description, category,
                            provider, brand, unit, convertToDouble(quantity), convertToDouble(buyCost), convertToDouble(sellPrice));
                    productModel.insert(product);
                    Platform.runLater(() -> {
                        disableForm(true);
                        clear();
                    });
                    showAlert("Success!", "Product Successfully Created!", Message.MESSAGE_TYPE_SUCCESS);
                    loadData();
                }else {
                    showAlert("Error!", "A product with that name already exist!", Message.MESSAGE_TYPE_ERROR);
                }
            } else {
                editing=false;
                showAlert("Information!", "Fill all required fields!", Message.MESSAGE_TYPE_ERROR);
            }
        };
        Thread thread=new Thread(task);
        thread.start();
    }


    /***
     * Edit Product
     * @param oldProduct Product to be edited
     * @param name Name
     * @param description Description
     * @param category Category
     * @param provider Provider
     * @param brand Brand
     * @param unit Unit
     * @param quantity Quantity or Stock
     * @param buyCost Listing Price
     * @param sellPrice Retail Price
     * @return true when edited
     */
    private boolean editProduct(Product oldProduct, String name, String description, Category category,Provider provider,
                                Brand brand,Unit unit,TextField quantity,TextField buyCost,TextField sellPrice){
        boolean toReturn=false;
        if (!name.isEmpty() && category!=null && provider!=null && brand!=null && unit!=null) {
            if (!productModel.alreadyExist(name) || oldProduct.getName().equalsIgnoreCase(name)) {

                Product product = new Product(name, description, category,
                        provider, brand, unit, convertToDouble(quantity), convertToDouble(buyCost), convertToDouble(sellPrice));
                productModel.update(oldProduct, product);
                Platform.runLater(() -> {
                    disableForm(true);
                    clear();
                });
                showAlert("Success!", "Product Successfully Updated!", Message.MESSAGE_TYPE_SUCCESS);
                loadData();
                toReturn = true;
            }else {
                showAlert("Error!", "A product with that name already exist!", Message.MESSAGE_TYPE_ERROR);
            }
        } else {
            showAlert("Alert!", "Fill all required fields!", Message.MESSAGE_TYPE_ERROR);
        }
        return toReturn;
    }


}
