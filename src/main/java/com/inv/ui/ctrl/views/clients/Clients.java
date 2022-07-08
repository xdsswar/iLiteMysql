package com.inv.ui.ctrl.views.clients;

import com.inv.R;
import com.inv.ui.ctrl.dialogs.Message;
import com.inv.ui.in.IWidget;
import com.inv.ui.util.Validator;
import com.inv.ui.widgets.ListableXListFactory;
import com.inv.xflux.entity.Client;
import com.inv.xflux.model.ClientModel;
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
import com.xd.text.*;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * @author XDSSWAR
 */
@SuppressWarnings("Duplicates")
public final class Clients implements Initializable, IWidget {
    @FXML
    private ListView<Client> listView;

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
    private MaskedTextField phoneText;

    private final TextField search;
    private ObservableList<Client> clientsList= FXCollections.observableArrayList();
    private ClientModel clientModel;
    private final String ERROR_STYLE_CLASS="unit-input-error";
    /*
    This boolean is very important , Dont play with it
 */
    private static boolean editing=false;

    /**
     * Constructor
     * @param search TextField
     */
    public Clients( final TextField search){
        this.search=search;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPane.setFitToWidth(true);
        clientModel=new ClientModel();
        initList();
        loadData();

        //Evade Editing another row while the form still filled
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            disableForm(true);
            nameText.clear();
            emailText.clear();
            phoneText.clear();
        });
        /*
         *Listener to remove the Error StyleClass
         */
        nameText.textProperty().addListener((observable, oldValue, newValue) -> {
            nameText.getStyleClass().remove(ERROR_STYLE_CLASS);
        });
        emailText.textProperty().addListener((observable, oldValue, newValue) -> {
            emailText.getStyleClass().remove(ERROR_STYLE_CLASS);
        });
        phoneText.textProperty().addListener((observable, oldValue, newValue) -> {
            phoneText.getStyleClass().remove(ERROR_STYLE_CLASS);

        });

        /*
         * Enable Form to create new Client
         */
        newBtn.setOnAction(event -> {
            nameText.clear();
            emailText.clear();
            phoneText.clear();
            disableForm(false);
            editing = false;
        });

        /*
         * Refresh List
         */
        refreshBtn.setOnAction(event -> {
            loadData();
        });

        /*
         *Save Client or edit Client
         */
        applyButton.setOnAction(event -> {
            if (nameText.getText().isEmpty()){
                nameText.getStyleClass().add(ERROR_STYLE_CLASS);
            }
            if (emailText.getText().isEmpty() && phoneText.getText().isEmpty()){
                emailText.getStyleClass().add(ERROR_STYLE_CLASS);
            }
            if (phoneText.getText().isEmpty() && emailText.getText().isEmpty()){
                phoneText.getStyleClass().add(ERROR_STYLE_CLASS);
            }
            if (editing){
                editing= !editClient(listView.getSelectionModel().getSelectedItem(),
                        nameText.getText(), emailText.getText(), phoneText.getText());
            }else {
                saveClient(nameText.getText(),emailText.getText(),phoneText.getText());
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

        final Runnable delete=()->{
            editing=false;
            if (!clientModel.hasOrders(listView.getSelectionModel().getSelectedItem())) {
                Platform.runLater(() -> {
                    boolean del = Message.showMessage(scrollPane.getScene().getWindow(), "Warning!",
                            "Are you sure you want tp proceed?. This action can't be undone.",
                            Message.MESSAGE_TYPE_WARNING);
                    if (del) {
                        Runnable r = () -> {
                            clientModel.delete(listView.getSelectionModel().getSelectedItem());
                            loadData();
                            Platform.runLater(() -> {
                                nameText.clear();
                                emailText.clear();
                                phoneText.clear();
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
                showAlert("Error!","You can edit, but you cannot delete a used Client!",
                        Message.MESSAGE_TYPE_ERROR);
            }
        };
        listView.setCellFactory(new ListableXListFactory<Client>().build("#303960","USER",
                true , add,edit,delete));
    }

    /**
     * Load all Clients
     */
    private void loadData(){
        search.clear();
        Runnable task=()->{
            clientsList=clientModel.getAll();
            Duration duration = Duration.millis(500);
            Timeline timeline = new Timeline(
                    new KeyFrame(duration,new KeyValue(listView.itemsProperty(),clientsList, Interpolator.EASE_BOTH))
            );
            timeline.play();
            //Add items to the Table
            Platform.runLater(()->{
                //Hack Around for java 11
                listView.setItems(clientsList);
                search.clear();
                initSearchFilter(clientsList,search);
            });
        };
        Thread loadDataThread=new Thread(task);
        loadDataThread.start();
        disableForm(true);
        nameText.clear();
        emailText.clear();
        phoneText.clear();
    }


    /**
     * Fill The form
     * @param value Client
     */
    private void fillForm(Client value, boolean edit){
        if (value!=null){
            if (edit){
                nameText.setText(value.getName());
                emailText.setText(value.getEmail());
                phoneText.setText(value.getPhone());
            }else {
                nameText.clear();
                emailText.clear();
                phoneText.clear();
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
        emailText.setDisable(disable);
        phoneText.setDisable(disable);
        applyButton.setDisable(disable);
        nameText.getStyleClass().remove(ERROR_STYLE_CLASS);
        phoneText.getStyleClass().remove(ERROR_STYLE_CLASS);
        emailText.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * WARNING , this method can freeze the UI if used in the wrong place
     * Init Search field
     * @param clientVal List with all the values
     * @param textField TextField instance from the MainCeTrl
     */
    private void initSearchFilter(ObservableList<Client> clientVal, TextField textField){
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
                    } else if (client.getPhone().contains(lcF)) {
                        return true;
                    }
                    return false;
                });
            });
            SortedList<Client> sortedList=new SortedList<>(filteredList);
            listView.setItems(sortedList);
        });
    }

    /**
     * Util to Save Client
     * @param name Client Name
     * @param email Client Email
     * @param phone Client Phone
     */
    private void saveClient(String name, String email, String phone){
        Runnable task=()->{
            if (!name.isEmpty() && (!email.isEmpty() || !phone.isEmpty())) {
                if (Validator.validateEmail(email)) {
                    Client client = new Client(name, email, phone);
                    clientModel.insert(client);
                    Platform.runLater(() -> {
                        disableForm(true);
                        nameText.clear();
                        emailText.clear();
                        phoneText.clear();
                        nameText.getStyleClass().remove(ERROR_STYLE_CLASS);
                        emailText.getStyleClass().remove(ERROR_STYLE_CLASS);
                        phoneText.getStyleClass().remove(ERROR_STYLE_CLASS);
                    });
                    showAlert("Success!", "Client Successfully Created!", Message.MESSAGE_TYPE_SUCCESS);
                    loadData();
                }else {
                    Platform.runLater(()->emailText.getStyleClass().add(ERROR_STYLE_CLASS));
                    showAlert("Attention!", "Please enter a valid Email Address!", Message.MESSAGE_TYPE_ERROR);
                }

            } else {
                editing = false;
                showAlert("Information!", "Fill all required fields!", Message.MESSAGE_TYPE_ERROR);
            }

        };
        Thread thread=new Thread(task);
        thread.start();
    }


    /**
     * Save Edited Client
     * @param oldClient Client to be edited
     * @param name name
     * @param email email
     * @param phone phone
     */
    private boolean editClient(Client oldClient, String name , String email, String phone){
        boolean toReturn=false;
        if (Validator.validateEmail(email)){
            if (!name.isEmpty() && (!email.isEmpty()|| !phone.isEmpty())) {
                Client client = new Client(name, email, phone);
                clientModel.update(oldClient,client);
                Platform.runLater(() -> {
                    disableForm(true);
                    nameText.clear();
                    emailText.clear();
                    phoneText.clear();
                    nameText.getStyleClass().remove(ERROR_STYLE_CLASS);
                    emailText.getStyleClass().remove(ERROR_STYLE_CLASS);
                    phoneText.getStyleClass().remove(ERROR_STYLE_CLASS);
                });
                showAlert("Success!", "Client Successfully Updated!", Message.MESSAGE_TYPE_SUCCESS);
                loadData();
                toReturn=true;
            } else {
                showAlert("Attention!", "Fill all required fields!", Message.MESSAGE_TYPE_ERROR);
            }
        }else {
            Platform.runLater(()->emailText.getStyleClass().add(ERROR_STYLE_CLASS));
            showAlert("Attention!", "Please enter a valid Email Address!", Message.MESSAGE_TYPE_ERROR);
        }
        return toReturn;
    }

    /**
     * Util to show Alerts on Client saving
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
