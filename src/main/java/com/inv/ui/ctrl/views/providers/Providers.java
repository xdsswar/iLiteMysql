package com.inv.ui.ctrl.views.providers;

import com.inv.R;
import com.inv.ui.ctrl.dialogs.Message;
import com.inv.ui.in.IWidget;
import com.inv.ui.widgets.ListableXListFactory;
import com.inv.xflux.entity.Provider;
import com.inv.xflux.model.ProviderModel;
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
@SuppressWarnings("all")
public final class Providers implements Initializable, IWidget {


    @FXML
    private ListView<Provider> listView;

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
    private TextField emailText;

    @FXML
    private TextField phoneText;

    @FXML
    private TextField addressText;

    private final TextField search;
    private ObservableList<Provider> providersList= FXCollections.observableArrayList();
    private ProviderModel providerModel;
    /*
      This boolean is very important , Dont play with it
   */
    private static boolean editing=false;


    /**
     * Constructor
     * @param search TextField from MainCtrl
     */
    public Providers( final TextField search){
        this.search=search;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPane.setFitToWidth(true);
        providerModel=new ProviderModel();
        initList();
        loadData();
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            disableForm(true);
            nameText.clear();
            emailText.clear();
            phoneText.clear();
            addressText.clear();
        });
        /*
         *Listener to remove the Error StyleClass
         */
        nameText.textProperty().addListener((observable, oldValue, newValue) -> {
            nameText.getStyleClass().remove("unit-input-error");
        });

        /*
         * Enable Form to create new Provider
         */
        newBtn.setOnAction(event -> {
            if (R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin()) {
                nameText.clear();
                emailText.clear();
                phoneText.clear();
                addressText.clear();
                disableForm(false);
            }else {
                Message.showMessage(scrollPane.getScene().getWindow(),"Security Alert!","Only Admins and Managers can create new Providers!",
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

        applyButton.setOnAction(event -> {
            if (R.ONLINE_EMPLOYEE.isAdmin()|| R.ONLINE_EMPLOYEE.isManager()) {
                if (nameText.getText().isEmpty()) {
                    nameText.getStyleClass().add("unit-input-error");
                }
                if (editing) {
                    editing = !editProvider(listView.getSelectionModel().getSelectedItem(),
                            nameText.getText(), emailText.getText(), phoneText.getText(), addressText.getText());
                } else {
                    saveProvider(nameText.getText(), emailText.getText(), phoneText.getText(), addressText.getText());
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
        //Add new Provider
        final Runnable add=()->{
            editing=false;
            fillForm(listView.getSelectionModel().getSelectedItem(),false);
        };
        //Edit the Provider
        final Runnable edit=()->{
            editing=true;
            fillForm(listView.getSelectionModel().getSelectedItem(),true);
        };

        //Delete the Provider if not used
        final Runnable delete=()->{
            editing=false;
            if (listView.getSelectionModel().getSelectedItem().getId()!=1) {
                if (!providerModel.isUsed(listView.getSelectionModel().getSelectedItem())) {
                    Platform.runLater(() -> {
                        boolean del = Message.showMessage(scrollPane.getScene().getWindow(), "Warning!",
                                "Are you sure you want tp proceed?. This action can't be undone.",
                                Message.MESSAGE_TYPE_WARNING);
                        if (del) {
                            Runnable r = () -> {
                                providerModel.delete(listView.getSelectionModel().getSelectedItem());
                                loadData();
                                Platform.runLater(() -> {
                                    nameText.clear();
                                    emailText.clear();
                                    phoneText.clear();
                                    addressText.clear();
                                    disableForm(true);
                                });
                                showAlert("Success!", "Successfully Deleted!",
                                        Message.MESSAGE_TYPE_SUCCESS);
                            };
                            r.run();
                        }
                    });
                } else {
                    showAlert("Alert!", "You can edit, but you cannot delete a used Provider!",
                            Message.MESSAGE_TYPE_ERROR);
                }
            }else {
                showAlert("Error!", "You can not delete a default Provider!",
                        Message.MESSAGE_TYPE_ERROR);
            }
        };
        listView.setCellFactory(new ListableXListFactory<Provider>().build("#e67e22","TRUCK",
                R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin() , add,edit,delete));
    }

    /**
     * Load all Providers
     */
    private void loadData(){
        search.clear();
        Runnable task=()->{
            providersList=providerModel.getAll();
            Duration duration = Duration.millis(500);
            Timeline timeline = new Timeline(
                    new KeyFrame(duration,new KeyValue(listView.itemsProperty(),providersList, Interpolator.EASE_BOTH))
            );
            timeline.play();
            //Add items to the Table
            Platform.runLater(()->{
                //Hack Around for java 11
                listView.setItems(providersList);
                initSearchFilter(providersList,search);
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
        nameText.setDisable(disable);
        emailText.setDisable(disable);
        phoneText.setDisable(disable);
        addressText.setDisable(disable);
        applyButton.setDisable(disable);
        nameText.getStyleClass().remove("unit-input-error");
    }

    /**
     * Fill The form
     * @param value Provider
     */
    private void fillForm(Provider value, boolean edit){
        if (value!=null){
            if (edit){
                nameText.setText(value.getName());
                emailText.setText(value.getEmail());
                phoneText.setText(value.getPhone());
                addressText.setText(value.getAddress());
            }else {
                nameText.clear();
                emailText.clear();
                phoneText.clear();
                addressText.clear();
            }
            disableForm(false);
        }
    }

    /**
     * WARNING , this method can freeze the UI if used in the wrong place
     * Init Search field
     * @param proVal List with all the values
     * @param textField TextField instance from the MainCeTrl
     */
    private void initSearchFilter(ObservableList<Provider> proVal, TextField textField){
        FilteredList<Provider> filteredList=new FilteredList<>(proVal, e->true);
        textField.setOnKeyReleased(event -> {
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate((Predicate<? super Provider>) pro->{
                    if (newValue.isEmpty()){
                        return true;
                    }
                    String lcF=newValue.toLowerCase();
                    if (pro.getName().toLowerCase().contains(lcF)){
                        return true;
                    }else if (pro.getEmail().toLowerCase().contains(lcF)){
                        return true;
                    }else if (pro.getPhone().toLowerCase().contains(lcF)){
                        return true;
                    }else if (pro.getAddress().toLowerCase().contains(lcF)){
                        return true;
                    }
                    return false;
                });
            });
            SortedList<Provider> sortedList=new SortedList<>(filteredList);
            listView.setItems(sortedList);
        });
    }

    /**
     * Util to show Alerts on Provider saving
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
     * Save Provider
     * @param name name
     * @param email email address
     * @param phone phone number
     * @param address Address
     */
    private void saveProvider(String name, String email, String phone, String address){
        Runnable task=()->{
            if (!name.isEmpty()) {
                if (!providerModel.alreadyExist(name)) {
                    Provider provider = new Provider(name, email, phone, address);
                    providerModel.insert(provider);
                    Platform.runLater(() -> {
                        disableForm(true);
                        nameText.clear();
                        emailText.clear();
                        phoneText.clear();
                        addressText.clear();
                    });
                    showAlert("Success!", "Provider Successfully Created!", Message.MESSAGE_TYPE_SUCCESS);
                    loadData();
                }else {
                    showAlert("Error!", "A Provider with that name already exist!", Message.MESSAGE_TYPE_ERROR);
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
     * Update Provider
     * This method should give you true when editing remains on
     * @param oldProvider Provider to be updated
     * @param name new name
     * @param email email
     * @param phone phone
     * @param address address
     * @return true when updated
     */
    private boolean editProvider(Provider oldProvider, String name ,String email, String phone,String address){
        boolean toReturn=false;
        if (!name.isEmpty()) {
            if (!providerModel.alreadyExist(name) || oldProvider.getName().equalsIgnoreCase(name)) {
                Provider provider = new Provider(name, email, phone, address);
                providerModel.update(oldProvider, provider);
                Platform.runLater(() -> {
                    disableForm(true);
                    nameText.clear();
                    emailText.clear();
                    phoneText.clear();
                    addressText.clear();
                });
                showAlert("Success!", "Provider Successfully Updated!", Message.MESSAGE_TYPE_SUCCESS);
                loadData();
                toReturn = true;
            }else {
                showAlert("Error!", "A Provider with that name already exist!", Message.MESSAGE_TYPE_ERROR);
            }
        } else {
            showAlert("Alert!", "Fill all required fields!", Message.MESSAGE_TYPE_ERROR);
        }
        return toReturn;
    }

}
