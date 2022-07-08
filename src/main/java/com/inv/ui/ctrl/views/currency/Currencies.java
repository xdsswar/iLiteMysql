package com.inv.ui.ctrl.views.currency;

import com.inv.R;
import com.inv.ui.ctrl.dialogs.Message;
import com.inv.ui.in.IWidget;
import com.inv.ui.widgets.ActivableListFactory;
import com.inv.xflux.entity.Currency;
import com.inv.xflux.model.CurrencyModel;
import com.xd.controls.XButton;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * @author XDSSWAR
 */
@SuppressWarnings("Duplicates")
public final class Currencies implements Initializable, IWidget {

    @FXML
    private ListView<Currency> listView;

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
    private TextField symbolText;

    private final TextField search;
    private ObservableList<Currency> currencyList= FXCollections.observableArrayList();
    private CurrencyModel currencyModel;
    private static boolean editing=false;

    /**
     * Constructor
     * @param search TextField from MainCtrl
     */
    public Currencies( final TextField search){
        this.search=search;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPane.setFitToWidth(true);
        currencyModel=new CurrencyModel();
        initList();
        loadData();

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            disableForm(true);
            nameText.clear();
            symbolText.clear();
        });
        /*
         *Listener to remove the Error StyleClass
         */
        nameText.textProperty().addListener((observable, oldValue, newValue) -> {
            nameText.getStyleClass().remove("unit-input-error");
        });
        symbolText.textProperty().addListener((observable, oldValue, newValue) -> {
            symbolText.getStyleClass().remove("unit-input-error");
        });

        /*
         * Enable Form to create
         */
        newBtn.setOnAction(event -> {
            if (R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin()) {
                nameText.clear();
                symbolText.clear();
                disableForm(false);
            }else {
                Message.showMessage(scrollPane.getScene().getWindow(),"Security Alert!","Only Admins and Managers can create new Currencies!",
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
         *Save or edit
         */
        applyButton.setOnAction(event -> {
            if (R.ONLINE_EMPLOYEE.isAdmin() || R.ONLINE_EMPLOYEE.isManager()) {
                if (nameText.getText().isEmpty()) {
                    nameText.getStyleClass().add("unit-input-error");
                }
                if (symbolText.getText().isEmpty()) {
                    symbolText.getStyleClass().add("unit-input-error");
                }
                if (editing) {
                    editing = !editCurrency(listView.getSelectionModel().getSelectedItem(),
                            nameText.getText(), symbolText.getText());
                } else {
                    saveCurrency(nameText.getText(), symbolText.getText());
                }
            }else {
                 showAlert("Access Denied!","You don't have enough rights to perform this action!",Message.MESSAGE_TYPE_ERROR);
            }

        });


    }

    /**
     * Init the ListView Properties and all the Threads associated
     */
    private void  initList(){
        final Runnable add=()->{
            editing=false;
            fillForm(listView.getSelectionModel().getSelectedItem(),false);
        };
        final Runnable edit=()->{
            editing=true;
            fillForm(listView.getSelectionModel().getSelectedItem(),true);
        };
        final Runnable def=()->{
            currencyModel.setDefault(listView.getSelectionModel().getSelectedItem());
            loadData();
        };
        final Runnable delete=()->{
            editing=false;
            Platform.runLater(()->{
                if (listView.getSelectionModel().getSelectedItem().getId()!=1) {
                    boolean del = Message.showMessage(scrollPane.getScene().getWindow(), "Warning!",
                            "Are you sure you want tp proceed?. This action can't be undone.",
                            Message.MESSAGE_TYPE_WARNING);
                    if (del) {
                        Runnable r = () -> {
                            currencyModel.delete(listView.getSelectionModel().getSelectedItem());
                            loadData();
                            Platform.runLater(() -> {
                                nameText.clear();
                                symbolText.clear();
                                disableForm(true);
                                showAlert("Success!","Successfully Deleted!",Message.MESSAGE_TYPE_SUCCESS);
                            });
                        };
                        r.run();
                    }
                }else {
                    showAlert("Error!","You can not delete a default Currency!",Message.MESSAGE_TYPE_ERROR);
                }
            });
        };
        listView.setCellFactory(new ActivableListFactory<Currency>().build("#2ecc71","MONEY",
                R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin() , add,def,edit,delete));
    }

    /**
     * Load all Currencies
     */
    public void loadData(){
        search.clear();
        Runnable task=()->{
            currencyList=currencyModel.getAll();
            Duration duration = Duration.millis(500);
            Timeline timeline = new Timeline(
                    new KeyFrame(duration,new KeyValue(listView.itemsProperty(),currencyList, Interpolator.EASE_BOTH))
            );
            timeline.play();
            //Add items to the Table
            Platform.runLater(()->{
                //Hack Around for java 11
                listView.setItems(currencyList);
                search.clear();
                initSearchFilter(currencyList,search);
            });
        };
        Thread loadDataThread=new Thread(task);
        loadDataThread.start();
        disableForm(true);
        nameText.clear();
        symbolText.clear();
    }

    /**
     * Enable or disable the Form
     * @param disable boolean
     */
    private void disableForm(boolean disable){
        nameText.setDisable(disable);
        symbolText.setDisable(disable);
        applyButton.setDisable(disable);
        nameText.getStyleClass().remove("unit-input-error");
        symbolText.getStyleClass().remove("unit-input-error");
    }

    /**
     * Fill The form
     * @param value Currency
     */
    private void fillForm(Currency value, boolean edit){
        if (value!=null){
            if (edit){
                nameText.setText(value.getName());
                symbolText.setText(value.getSymbol());
            }else {
                nameText.clear();
                symbolText.clear();
            }
            disableForm(false);
        }
    }

    /**
     * WARNING , this method can freeze the UI if used in the wrong place
     * Init Search field
     * @param curVal List with all the values
     * @param textField TextField instance from the MainCeTrl
     */
    private void initSearchFilter(ObservableList<Currency> curVal, TextField textField){
        FilteredList<Currency> filteredList=new FilteredList<>(curVal, e->true);
        textField.setOnKeyReleased(event -> {
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate((Predicate<? super Currency>) cur->{
                    if (newValue.isEmpty()){
                        return true;
                    }
                    String lcF=newValue.toLowerCase();
                    if (cur.getName().toLowerCase().contains(lcF)){
                        return true;
                    } 
                    return false;
                });
            });
            SortedList<Currency> sortedList=new SortedList<>(filteredList);
            listView.setItems(sortedList);
        });
    }

    /**
     * Util to Save
     * @param name  Name
     * @param symbol  Symbol
     */
    private void saveCurrency(String name, String symbol){
        Runnable task=()->{
            if (!name.isEmpty() && !symbol.isEmpty()) {
                if (!currencyModel.alreadyExist(name)) {
                    Currency currency = new Currency(name, symbol, 0);
                    currencyModel.insert(currency);
                    Platform.runLater(() -> {
                        disableForm(true);
                        nameText.clear();
                        symbolText.clear();
                    });
                    showAlert("Success!", "Currency Successfully Created!", Message.MESSAGE_TYPE_SUCCESS);
                    loadData();
                }else {
                    showAlert("Error!", "A Currency with that name already exist!", Message.MESSAGE_TYPE_ERROR);
                }
            } else {
                editing=false;
                showAlert("Information!", "Fill all required fields!", Message.MESSAGE_TYPE_ERROR);
            }
        };
        Thread thread=new Thread(task);
        thread.start();
    }

    /**
     * Save Edited
     * @param oldCur  to be edited
     * @param name name
     * @param symbol symbol
     * @return boolean
     */
    private boolean editCurrency(Currency oldCur, String name , String symbol){
        boolean toReturn=false;
        if (!name.isEmpty() && !symbol.isEmpty()) {
            if (!currencyModel.alreadyExist(name) || oldCur.getName().equalsIgnoreCase(name)) {
                Currency currency = new Currency(name, symbol, 0);
                currencyModel.update(oldCur, currency);
                Platform.runLater(() -> {
                    disableForm(true);
                    nameText.clear();
                    symbolText.clear();
                });
                showAlert("Success!", "Currency Successfully Updated!", Message.MESSAGE_TYPE_SUCCESS);
                loadData();
                toReturn = true;
            }else {
                showAlert("Error!", "A Currency with that name already exist!", Message.MESSAGE_TYPE_ERROR);
            }
        } else {
            showAlert("Alert!", "Fill all required fields!", Message.MESSAGE_TYPE_ERROR);
        }
        return toReturn;
    }

    /**
     * Util to show Alerts on saving
     * @param title Title
     * @param message Message
     * @param messageType Type
     */
    private void showAlert(String title, String message, final short messageType){
        Platform.runLater(()->{
            Message.showMessage(scrollPane.getScene().getWindow(),title,message,messageType);
        });
    }
}
