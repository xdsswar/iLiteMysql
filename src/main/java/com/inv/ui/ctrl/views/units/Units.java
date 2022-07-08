package com.inv.ui.ctrl.views.units;

import com.inv.R;
import com.inv.ui.ctrl.dialogs.Message;
import com.inv.ui.in.IWidget;
import com.inv.ui.widgets.ListableXListFactory;
import com.inv.xflux.entity.Unit;
import com.inv.xflux.model.UnitModel;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * @author XDSSWAR
 */
@SuppressWarnings("all")
public final class Units implements Initializable , IWidget {

    @FXML
    private ListView<Unit> listView;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField nameText;

    @FXML
    private TextField symbolText;

    @FXML
    private TextArea descriptionText;

    @FXML
    private XButton applyButton;

    @FXML
    private XButton newBtn;

    @FXML
    private XButton refreshBtn;

    private final TextField search;
    private ObservableList<Unit> unitList= FXCollections.observableArrayList();
    private UnitModel unitModel;
    /*
        This boolean is very important , Dont play with it
     */
    private static boolean editing=false;

    /**
     * Constructor
     * @param search TextField
     */
    public Units( final TextField search){
        this.search = search;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        unitModel=new UnitModel();
        scrollPane.setFitToWidth(true);
        initList();
        loadData();

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            disableForm(true);
            nameText.clear();
            symbolText.clear();
            descriptionText.clear();
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
         * Enable Form to create new Unit
         */
        newBtn.setOnAction(event -> {
            if (R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin()) {
                nameText.clear();
                symbolText.clear();
                descriptionText.clear();
                disableForm(false);
            }else {
                Message.showMessage(scrollPane.getScene().getWindow(),"Security Alert!","Only Admins and Managers can create new Units!",
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
         *Save Unit or edit unit
         */
        applyButton.setOnAction(event -> {
            if (R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin()) {
                if (nameText.getText().isEmpty()) {
                    nameText.getStyleClass().add("unit-input-error");
                }
                if (symbolText.getText().isEmpty()) {
                    symbolText.getStyleClass().add("unit-input-error");
                }
                if (editing) {
                    editing = !editUnit(listView.getSelectionModel().getSelectedItem(),
                            nameText.getText(), symbolText.getText(), descriptionText.getText());
                } else {
                    saveUnit(nameText.getText(), symbolText.getText(), descriptionText.getText());
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
            if (listView.getSelectionModel().getSelectedItem().getId()!=1) {
                if (!unitModel.isUsed(listView.getSelectionModel().getSelectedItem())) {
                    Platform.runLater(() -> {
                        boolean del = Message.showMessage(scrollPane.getScene().getWindow(), "Warning!",
                                "Are you sure you want tp proceed?. This action can't be undone.",
                                Message.MESSAGE_TYPE_WARNING);
                        if (del) {
                            Runnable r = () -> {
                                unitModel.delete(listView.getSelectionModel().getSelectedItem());
                                loadData();
                                Platform.runLater(() -> {
                                    nameText.clear();
                                    symbolText.clear();
                                    descriptionText.clear();
                                    disableForm(true);
                                    Message.showMessage(scrollPane.getScene().getWindow(), "Success!",
                                            "Successfully Deleted!",
                                            Message.MESSAGE_TYPE_SUCCESS);
                                });
                            };
                            r.run();
                        }
                    });
                } else {
                    showAlert("Alert!", "You can edit, but you cannot delete a used Unit!",
                            Message.MESSAGE_TYPE_ERROR);
                }
            }else {
                showAlert("Error!", "You can not delete a default Unit!",
                        Message.MESSAGE_TYPE_ERROR);
            }
        };
        listView.setCellFactory(new ListableXListFactory<Unit>().build("#018574","BALANCE_SCALE",
                R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin() , add,edit,delete));
    }
    /**
     * Fill The form
     * @param value Unit
     */
    private void fillForm(Unit value, boolean edit){
        if (value!=null){
            if (edit){
                nameText.setText(value.getName());
                symbolText.setText(value.getSymbol());
                descriptionText.setText(value.getDescription());
            }else {
                nameText.clear();
                symbolText.clear();
                descriptionText.clear();
            }
            disableForm(false);
        }
    }

    /**
     * Enable or disable the Form
     * @param disable boolean
     */
    private void disableForm(boolean disable){
        nameText.setDisable(disable);
        symbolText.setDisable(disable);
        descriptionText.setDisable(disable);
        applyButton.setDisable(disable);
        nameText.getStyleClass().remove("unit-input-error");
        symbolText.getStyleClass().remove("unit-input-error");
    }

    /**
     * Load all Units
     */
    private void loadData(){
        search.clear();
        Runnable task=()->{
            unitList=unitModel.getAll();
            Duration duration = Duration.millis(500);
            Timeline timeline = new Timeline(
                    new KeyFrame(duration,new KeyValue(listView.itemsProperty(),unitList, Interpolator.EASE_BOTH))
            );
            timeline.play();
            //Add items
            Platform.runLater(()->{
                //Hack Around for java 11
                listView.setItems(unitList);
                search.clear();
                initSearchFilter(unitList,search);
            });
        };
        Thread loadDataThread=new Thread(task);
        loadDataThread.start();
        disableForm(true);
        nameText.clear();
        symbolText.clear();
        descriptionText.clear();
    }

    /**
     * WARNING , this method can freeze the UI if used in the wrong place
     * Init Search field
     * @param unitVal List with all the values
     * @param textField TextField instance from the MainCeTrl
     */
    private void initSearchFilter(ObservableList<Unit> unitVal, TextField textField){
        FilteredList<Unit> filteredList=new FilteredList<>(unitVal, e->true);
        textField.setOnKeyReleased(event -> {
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate((Predicate<? super Unit>) uni->{
                    if (newValue.isEmpty()){
                        return true;
                    }
                    String lcF=newValue.toLowerCase();
                     if (uni.getName().toLowerCase().contains(lcF)){
                        return true;
                    } else if (uni.getDescription().toLowerCase().contains(lcF)) {
                        return true;
                    }
                    return false;
                });
            });
            SortedList<Unit> sortedList=new SortedList<>(filteredList);
            listView.setItems(sortedList);
        });
    }

    /**
     * Util to Save Unit
     * @param name Unit Name
     * @param symbol Unit Symbol
     * @param description Unit Description
     */
    private void saveUnit(String name, String symbol, String description){
        Runnable task=()->{
            if (!name.isEmpty() && !symbol.isEmpty()) {
                if (!unitModel.alreadyExist(name)) {
                    Unit unit = new Unit(name, symbol, description);
                    unitModel.insert(unit);
                    Platform.runLater(() -> {
                        disableForm(true);
                        nameText.clear();
                        symbolText.clear();
                        descriptionText.clear();
                    });
                    showAlert("Success!", "Unit Successfully Created!", Message.MESSAGE_TYPE_SUCCESS);
                    loadData();
                }else {
                    showAlert("Error!", "A Unit with that name already exist!", Message.MESSAGE_TYPE_ERROR);
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
     * Save Edited Unit
     * @param oldUnit unit to be edited
     * @param name name
     * @param symbol symbol
     * @param description description
     * @return boolean
     */
    private boolean editUnit(Unit oldUnit, String name ,String symbol, String description){
        boolean toReturn=false;
        if (!name.isEmpty() && !symbol.isEmpty()) {
            if (!unitModel.alreadyExist(name) || oldUnit.getName().equalsIgnoreCase(name)) {
                Unit unit = new Unit(name, symbol, description);
                unitModel.update(oldUnit, unit);
                Platform.runLater(() -> {
                    disableForm(true);
                    nameText.clear();
                    symbolText.clear();
                    descriptionText.clear();
                });
                showAlert("Success!", "Unit Successfully Updated!", Message.MESSAGE_TYPE_SUCCESS);
                loadData();
                toReturn = true;
            }else {
                showAlert("Error!", "A Unit with that name already exist!", Message.MESSAGE_TYPE_ERROR);
            }

        } else {
            showAlert("Alert!", "Fill all required fields!", Message.MESSAGE_TYPE_ERROR);
        }
        return toReturn;
    }

    /**
     * Util to show Alerts on Unit saving
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
