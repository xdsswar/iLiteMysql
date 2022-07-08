package com.inv.ui.ctrl.views.tax;

import com.inv.R;
import com.inv.ui.ctrl.dialogs.Message;
import com.inv.ui.in.IWidget;
import com.inv.ui.widgets.ActivableListFactory;
import com.inv.xflux.entity.Tax;
import com.inv.xflux.model.TaxModel;
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
@SuppressWarnings("Duplicates")
public final class Taxes implements Initializable, IWidget {
    @FXML
    private ListView<Tax> listView;

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

    private final TextField search;
    private ObservableList<Tax> taxList= FXCollections.observableArrayList();
    private TaxModel taxModel;
    private static boolean editing=false;

    /**
     * Constructor
     * @param search TexField
     */
    public Taxes(final TextField search){
        this.search=search;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPane.setFitToWidth(true);
        taxModel=new TaxModel();
        initList();
        loadData();

        /*
           Evade editing another Row if the selection change while the form still filled
         */
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            disableForm(true);
            nameText.clear();
            descriptionText.clear();
        });
        /*
         *Listener to remove the Error StyleClass
         */
        nameText.textProperty().addListener((observable, oldValue, newValue) -> {
            nameText.getStyleClass().remove("unit-input-error");
        });

        /*
         * Refresh List
         */
        refreshBtn.setOnAction(event -> {
            loadData();
        });

        /*
         * Enable Form to create new Tax
         */
        newBtn.setOnAction(event -> {
            if (R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin()) {
                nameText.clear();
                descriptionText.clear();
                disableForm(false);
            }else {
                Message.showMessage(scrollPane.getScene().getWindow(),"Security Alert!","Only Admins and Managers can create new Tax Rates!",
                        Message.MESSAGE_TYPE_ERROR);
            }
            editing = false;
        });

        applyButton.setOnAction(event -> {
            if (R.ONLINE_EMPLOYEE.isAdmin()|| R.ONLINE_EMPLOYEE.isManager()) {
                if (nameText.getText().isEmpty()) {
                    nameText.getStyleClass().add("unit-input-error");
                }
                if (editing) {
                    editing = !editTax(listView.getSelectionModel().getSelectedItem(),
                            nameText.getText(), descriptionText.getText());
                } else {
                    saveTax(nameText.getText(), descriptionText.getText());
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
        //Add new Tax
        final Runnable add=()->{
            editing=false;
            fillForm(listView.getSelectionModel().getSelectedItem(),false);
        };

        //Set Default
        Runnable def=()->{
            taxModel.setDefault(listView.getSelectionModel().getSelectedItem());
            loadData();
        };
        //Edit the Tax
        final Runnable edit=()->{
            editing=true;
            fillForm(listView.getSelectionModel().getSelectedItem(),true);
        };

        //Delete the Tax if not used
        final Runnable delete=()-> {
            editing = false;
            if (listView.getSelectionModel().getSelectedItem().getId()!=1) {
                if (!taxModel.isUsed(listView.getSelectionModel().getSelectedItem())) {
                    Platform.runLater(() -> {
                        boolean del = Message.showMessage(scrollPane.getScene().getWindow(), "Warning!",
                                "Are you sure you want tp proceed?. This action can't be undone.",
                                Message.MESSAGE_TYPE_WARNING);
                        if (del) {
                            Runnable r = () -> {
                                taxModel.delete(listView.getSelectionModel().getSelectedItem());
                                loadData();
                                Platform.runLater(() -> {
                                    nameText.clear();
                                    descriptionText.clear();
                                    disableForm(true);
                                    showAlert("Success!", "Successfully Deleted!",
                                            Message.MESSAGE_TYPE_SUCCESS);
                                });
                            };
                            r.run();
                        }
                    });
                } else {
                    showAlert("Alert!", "You can edit, but you cannot delete a used Tax Rate!",
                            Message.MESSAGE_TYPE_ERROR);
                }
            }else {
                showAlert("Error!", "You can not delete a default Tax Rate!",
                        Message.MESSAGE_TYPE_ERROR);
            }

        };
        listView.setCellFactory(new ActivableListFactory<Tax>().build("#69797E","PERCENT",
                R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin() , add,def,edit,delete));
    }

    /**
     * Load all Tax
     */
    private void loadData(){
        search.clear();
        Runnable task=()->{
            taxList=taxModel.getAll();
            Duration duration = Duration.millis(500);
            Timeline timeline = new Timeline(
                    new KeyFrame(duration,new KeyValue(listView.itemsProperty(),taxList, Interpolator.EASE_BOTH))
            );
            timeline.play();
            //Add items to the Table
            Platform.runLater(()->{
                //Hack Around for java 11
                listView.setItems(taxList);
                initSearchFilter(taxList,search);
            });
        };
        Thread thread=new Thread(task);
        thread.start();
    }

    /**
     * Enable or disable the Form
     * @param disable boolean
     */
    private void disableForm(boolean disable){
        nameText.getStyleClass().remove("unit-input-error");
        nameText.setDisable(disable);
        descriptionText.setDisable(disable);
        applyButton.setDisable(disable);
    }

    /**
     * Fill The form
     * @param value Tax
     */
    private void fillForm(Tax value, boolean edit){
        if (value!=null){
            if (edit){
                nameText.setText(value.getTaxVal()+"");
                descriptionText.setText(value.getDescription());
            }else {
                nameText.clear();
                descriptionText.clear();
            }
            disableForm(false);
        }
    }

    /**
     * WARNING , this method can freeze the UI if used in the wrong place
     * Init Search field
     * @param taxVal List with all the values
     * @param textField TextField instance from the MainCeTrl
     */
    private void initSearchFilter(ObservableList<Tax> taxVal, TextField textField){
        FilteredList<Tax> filteredList=new FilteredList<>(taxVal, e->true);
        textField.setOnKeyReleased(event -> {
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate((Predicate<? super Tax>) tax->{
                    if (newValue.isEmpty()){
                        return true;
                    }
                    String lcF=newValue.toLowerCase();
                    if (Double.toString(tax.getTaxVal()).toLowerCase().contains(lcF)){
                        return true;
                    }else if (tax.getDescription().toLowerCase().contains(lcF)){
                        return true;
                    }
                    return false;
                });
            });
            SortedList<Tax> sortedList=new SortedList<>(filteredList);
            listView.setItems(sortedList);
        });
    }


    /**
     * Util to show Alerts on Tax saving
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
     * Save Tax Rate
     * @param taxVal taxVal
     * @param description description address
     */
    private void saveTax(String taxVal, String description){
        Runnable task=()->{
            if (!taxVal.isEmpty()) {
                Tax tax = new Tax(Double.parseDouble(taxVal), description);
                taxModel.insert(tax);
                Platform.runLater(() -> {
                    disableForm(true);
                    nameText.clear();
                    descriptionText.clear();
                });
                showAlert("Success!", "Tax Rate Successfully Created!", Message.MESSAGE_TYPE_SUCCESS);
                loadData();
            } else {
                editing=false;
                showAlert("Information!", "Fill all required fields!", Message.MESSAGE_TYPE_ERROR);
            }
        };
        Thread thread=new Thread(task);
        thread.start();
    }

    /**
     * Update Tax
     * This method should give you true when editing remains on
     * @param oldTax Tax to be updated
     * @param taxVal new taxVal
     * @param description description
     * @return true when updated
     */
    private boolean editTax(Tax oldTax, String taxVal ,String description){
        boolean toReturn=false;
        if (!taxVal.isEmpty()) {
            try {
                Tax tax = new Tax(Double.parseDouble(taxVal), description);
                taxModel.update(oldTax, tax);
                Platform.runLater(() -> {
                    disableForm(true);
                    nameText.clear();
                    descriptionText.clear();
                });
                showAlert("Success!", "Tax Rate Successfully Updated!", Message.MESSAGE_TYPE_SUCCESS);
                loadData();
                toReturn = true;
            }catch (Exception e){
                nameText.getStyleClass().add("unit-input-error");
                showAlert("Error!",
                        "The Tax Value can't be empty and should be in this format ##.##",
                        Message.MESSAGE_TYPE_ERROR);
            }
        } else {
            showAlert("Information!", "Fill all required fields!", Message.MESSAGE_TYPE_ERROR);
        }
        return toReturn;
    }




}
