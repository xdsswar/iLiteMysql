package com.inv.ui.ctrl.views.settings.company;

import com.inv.R;
import com.inv.ui.ctrl.dialogs.Message;
import com.inv.ui.in.IWidget;
import com.inv.ui.widgets.ActivableListFactory;
import com.inv.xflux.entity.Company;
import com.inv.xflux.model.CompanyModel;
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
public class CompanyView implements Initializable, IWidget {

    @FXML
    private ListView<Company> listView;

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
    private TextField addressText;

    @FXML
    private TextField phoneText;

    @FXML
    private TextField faxText;

    @FXML
    private TextField emailText;

    private final TextField search;
    private CompanyModel companyModel;
    private static boolean editing=false;
    private ObservableList<Company> companyList= FXCollections.observableArrayList();

    /**
     * Constructor
     * @param search TextField Reference from MainCtrl
     */
    public CompanyView(final TextField search){
        this.search=search;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPane.setFitToWidth(true);
        companyModel=new CompanyModel();
        initList();
        loadData();
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            disableForm(true);
            clear();
        });

        /*
         *Listener to remove the Error StyleClass
         */
        nameText.textProperty().addListener((observable, oldValue, newValue) -> {
            nameText.getStyleClass().remove("unit-input-error");
        });
        addressText.textProperty().addListener((observable, oldValue, newValue) -> {
            addressText.getStyleClass().remove("unit-input-error");
        });
        phoneText.textProperty().addListener((observable, oldValue, newValue) -> {
            phoneText.getStyleClass().remove("unit-input-error");
        });
        faxText.textProperty().addListener((observable, oldValue, newValue) -> {
            faxText.getStyleClass().remove("unit-input-error");
        });
        emailText.textProperty().addListener((observable, oldValue, newValue) -> {
            emailText.getStyleClass().remove("unit-input-error");
        });

        /*
         * Enable Form to create
         */
        newBtn.setOnAction(event -> {
            if (R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin()) {
                clear();
                disableForm(false);
            }else {
                Message.showMessage(scrollPane.getScene().getWindow(),"Security Alert!","Only Admins and Managers can create new Company Profiles!",
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
            if (nameText.getText().isEmpty()){
                nameText.getStyleClass().add("unit-input-error");
            }
            if (addressText.getText().isEmpty()){
                addressText.getStyleClass().add("unit-input-error");
            }
            if (phoneText.getText().isEmpty()){
                phoneText.getStyleClass().add("unit-input-error");
            }
            if (faxText.getText().isEmpty()){
                faxText.getStyleClass().add("unit-input-error");
            }
            if (emailText.getText().isEmpty()){
                emailText.getStyleClass().add("unit-input-error");
            }
            if (editing){
                editing=!editCompany(listView.getSelectionModel().getSelectedItem(),
                        nameText.getText(),addressText.getText(),phoneText.getText(),faxText.getText(),emailText.getText());
            }else {
                saveCompany(nameText.getText(),addressText.getText(),phoneText.getText(),faxText.getText(),emailText.getText());
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
            companyModel.setDefault(listView.getSelectionModel().getSelectedItem());
            loadData();
        };
        final Runnable delete=()->{
            editing=false;
            Platform.runLater(()->{
                boolean del= Message.showMessage(scrollPane.getScene().getWindow(), "Warning!",
                        "Are you sure you want tp proceed?. This action can't be undone.",
                        Message.MESSAGE_TYPE_WARNING);
                if (del){
                    Runnable r=()->{
                        companyModel.delete(listView.getSelectionModel().getSelectedItem());
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
        };
        listView.setCellFactory(new ActivableListFactory<Company>().build("#3b5999","ARCHIVE",
                R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin() , add,def,edit,delete));
    }


    /**
     * Fill The form
     * @param value Company
     */
    private void fillForm(Company value, boolean edit){
        if (value!=null){
            if (edit){
                nameText.setText(value.getName());
                addressText.setText(value.getAddress());
                phoneText.setText(value.getPhone());
                faxText.setText(value.getFax());
                emailText.setText(value.getEmail());
            }else {
                clear();
            }
            disableForm(false);
        }
    }

    /**
     * Load all Available Companies
     */
    public void loadData(){
        search.clear();
        Runnable task=()->{
            companyList=companyModel.getAll();
            Duration duration = Duration.millis(200);
            Timeline timeline = new Timeline(
                    new KeyFrame(duration,new KeyValue(listView.itemsProperty(),companyList, Interpolator.EASE_BOTH))
            );
            timeline.play();
            //Add items to the Table
            Platform.runLater(()->{
                //Hack Around for java 11
                listView.setItems(companyList);
                search.clear();
                initSearchFilter(companyList,this.search);
            });
        };
        Thread loadDataThread=new Thread(task);
        loadDataThread.start();
        disableForm(true);
        clear();
    }

    /**
     * WARNING , this method can freeze the UI if used in the wrong place
     * Init Search field
     * @param compVal List with all the values
     * @param textField TextField instance from the MainCeTrl
     */
    private void initSearchFilter(ObservableList<Company> compVal, TextField textField){
        FilteredList<Company> filteredList=new FilteredList<>(compVal, e->true);
        textField.setOnKeyReleased(event -> {
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate((Predicate<? super Company>) comp->{
                    if (newValue.isEmpty()){
                        return true;
                    }
                    String lcF=newValue.toLowerCase();
                    if (comp.getName().toLowerCase().contains(lcF)){
                        return true;
                    }
                    return false;
                });
            });
            SortedList<Company> sortedList=new SortedList<>(filteredList);
            listView.setItems(sortedList);
        });
    }

    /**
     * Util to show Alerts on saving
     * @param title Title
     * @param message Message
     * @param messageType Type
     */
    private void showAlertOnSave(String title, String message, final short messageType){
        Platform.runLater(()->{
            Message.showMessage(scrollPane.getScene().getWindow(),title,message,messageType);
        });
    }

    /**
     * Enable or disable the Form
     * @param disable boolean
     */
    private void disableForm(boolean disable){
        nameText.setDisable(disable);
        addressText.setDisable(disable);
        phoneText.setDisable(disable);
        faxText.setDisable(disable);
        emailText.setDisable(disable);
        applyButton.setDisable(disable);
        nameText.getStyleClass().remove("unit-input-error");
        addressText.getStyleClass().remove("unit-input-error");
        phoneText.getStyleClass().remove("unit-input-error");
        faxText.getStyleClass().remove("unit-input-error");
        emailText.getStyleClass().remove("unit-input-error");
    }

    /**
     * Clear all Fields
     */
    private void clear(){
        nameText.clear();
        addressText.clear();
        phoneText.clear();
        emailText.clear();
        faxText.clear();
    }

    /**
     * Util to Save new Company
     * @param name  Name
     * @param address  Address
     * @param phone phone
     * @param fax fax
     * @param email email
     */
    private void saveCompany(String name, String address,String phone,String fax,String email){
        Runnable task=()->{
            if (!name.isEmpty() && !address.isEmpty() && !phone.isEmpty() && !fax.isEmpty() && !email.isEmpty()) {
                Company company = new Company(name, address,phone,fax,email);
                companyModel.insert(company);
                Platform.runLater(() -> {
                    disableForm(true);
                    clear();
                });
                showAlertOnSave("Success!", "Company Successfully Created!", Message.MESSAGE_TYPE_SUCCESS);
                loadData();
            } else {
                editing=false;
                showAlertOnSave("Information!", "Fill all required fields!", Message.MESSAGE_TYPE_ERROR);
            }
        };
        Thread thread=new Thread(task);
        thread.start();
    }

    /**
     *  Edit Company
     * @param oldComp Company to be edited
     * @param name name
     * @param address address
     * @param phone phone
     * @param fax Fax
     * @param email email
     * @return true when done
     */
    private boolean editCompany(Company oldComp, String name, String address,String phone,String fax,String email){
        boolean toReturn=false;
        if (!name.isEmpty() && !address.isEmpty() && !phone.isEmpty() && !fax.isEmpty() && !email.isEmpty()) {
            Company company = new Company(name, address,phone,fax,email);
            companyModel.update(oldComp,company);
            Platform.runLater(() -> {
                disableForm(true);
                clear();
            });
            showAlertOnSave("Success!", "Company Successfully Updated!", Message.MESSAGE_TYPE_SUCCESS);
            loadData();
            toReturn=true;
        } else {
            showAlertOnSave("Information!", "Fill all required fields!", Message.MESSAGE_TYPE_ERROR);
        }
        return toReturn;
    }
}
