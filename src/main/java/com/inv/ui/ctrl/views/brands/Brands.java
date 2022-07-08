package com.inv.ui.ctrl.views.brands;

import com.inv.R;
import com.inv.ui.ctrl.dialogs.Message;
import com.inv.ui.in.IWidget;
import com.inv.ui.widgets.ListableXListFactory;
import com.inv.xflux.entity.Brand;
import com.inv.xflux.model.BrandModel;
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
public final class Brands implements Initializable, IWidget {
    @FXML
    private ListView<Brand> listView;

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
    private ObservableList<Brand> brandList= FXCollections.observableArrayList();
    private BrandModel brandModel;
    private static boolean editing=false;

    public Brands(final TextField search){
        this.search=search;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPane.setFitToWidth(true);
        brandModel=new BrandModel();
        initList();
        loadData();

        /*
         *Listener to remove the Error StyleClass
         */
        nameText.textProperty().addListener((observable, oldValue, newValue) -> {
            nameText.getStyleClass().remove("unit-input-error");
        });

        /*
         * Enable Form to create new Brand
         */
        newBtn.setOnAction(event -> {
            if (R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin()) {
                nameText.clear();;
                descriptionText.clear();
                disableForm(false);
            }else {
                Message.showMessage(scrollPane.getScene().getWindow(),"Security Alert!","Only Admins and Managers can create new Brands!",
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
         *Save or edit Brand
         */
        applyButton.setOnAction(event -> {
            if (R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin()) {
                if (nameText.getText().isEmpty()) {
                    nameText.getStyleClass().add("unit-input-error");
                }
                if (editing) {
                    editing = !editBrand(listView.getSelectionModel().getSelectedItem(),
                            nameText.getText(), descriptionText.getText());
                } else {
                    saveBrand(nameText.getText(), descriptionText.getText());
                }
            }else {
                 showAlert("Access Denied!","You don't have enough rights to perform this action!",Message.MESSAGE_TYPE_ERROR);
            }

        });

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            disableForm(true);
            nameText.clear();
            descriptionText.clear();
        });

    }

    /**
     * Init the ListView Properties and all the Threads associated
     */
    private void  initList(){
        //Add new Brand
        final Runnable add=()->{
            editing=false;
            fillForm(listView.getSelectionModel().getSelectedItem(),false);
        };
        //Edit the Brand
        final Runnable edit=()->{
            editing=true;
            fillForm(listView.getSelectionModel().getSelectedItem(),true);
        };

        //Delete the Brand if not used
        final Runnable delete=()->{
            editing=false;
            if (listView.getSelectionModel().getSelectedItem().getId()!=1) {
                if (!brandModel.isUsed(listView.getSelectionModel().getSelectedItem())){
                    Platform.runLater(()->{

                        boolean del = Message.showMessage(scrollPane.getScene().getWindow(), "Warning!",
                                "Are you sure you want tp proceed?. This action can't be undone.",
                                Message.MESSAGE_TYPE_WARNING);
                        if (del) {
                            Runnable r = () -> {
                                brandModel.delete(listView.getSelectionModel().getSelectedItem());
                                loadData();
                                Platform.runLater(() -> {
                                    nameText.clear();
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
                }else {
                    showAlert("Alert!","You can edit, but you cannot delete a used Brand!",
                            Message.MESSAGE_TYPE_ERROR);
                }
            }else {
                showAlert("Error!","You can not delete a default Brand!",
                        Message.MESSAGE_TYPE_ERROR);
            }



        };
        listView.setCellFactory(new ListableXListFactory<Brand>().build("#d80073","BULLHORN",
                R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin() , add,edit,delete));
    }

    /**
     * Load all Brands
     */
    private void loadData(){
        search.clear();
        Runnable task=()->{
            brandList=brandModel.getAll();
            Duration duration = Duration.millis(500);
            Timeline timeline = new Timeline(
                    new KeyFrame(duration,new KeyValue(listView.itemsProperty(),brandList, Interpolator.EASE_BOTH))
            );
            timeline.play();
            //Add items to the Table
            Platform.runLater(()->{
                //Hack Around for java 11
                listView.setItems(brandList);
                search.clear();
                initSearchFilter(brandList,search);
            });
        };
        Thread loadDataThread=new Thread(task);
        loadDataThread.start();
        disableForm(true);
        nameText.clear();
        descriptionText.clear();
    }
    /**
     * WARNING , this method can freeze the UI if used in the wrong place
     * Init Search field
     * @param brandVal List with all the values
     * @param textField TextField instance from the MainCTrl
     */
    private void initSearchFilter(ObservableList<Brand> brandVal, TextField textField){
        FilteredList<Brand> filteredList=new FilteredList<>(brandVal, e->true);
        textField.setOnKeyReleased(event -> {
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate((Predicate<? super Brand>) brand->{
                    if (newValue.isEmpty()){
                        return true;
                    }
                    String lcF=newValue.toLowerCase();
                    if (brand.getName().toLowerCase().contains(lcF)){
                        return true;
                    } else if (brand.getDescription().toLowerCase().contains(lcF)) {
                        return true;
                    }
                    return false;
                });
            });
            SortedList<Brand> sortedList=new SortedList<>(filteredList);
            listView.setItems(sortedList);
        });
    }

    /**
     * Enable or disable the Form
     * @param disable boolean
     */
    private void disableForm(boolean disable){
        nameText.setDisable(disable);
        descriptionText.setDisable(disable);
        applyButton.setDisable(disable);
        nameText.getStyleClass().remove("unit-input-error");
    }

    /**
     * Util to show Alerts on Brand saving
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
     * Save Edited Brand
     * @param oldBrand Brand to be edited
     * @param name name
     * @param description description
     * @return boolean
     */
    private boolean editBrand(Brand oldBrand, String name ,String description){
        boolean toReturn=false;
        if (!name.isEmpty()) {
            if (!brandModel.alreadyExist(name) || oldBrand.getName().equalsIgnoreCase(name)) {
                Brand brand = new Brand(name, description);
                brandModel.update(oldBrand, brand);
                Platform.runLater(() -> {
                    disableForm(true);
                    nameText.clear();
                    descriptionText.clear();
                });
                showAlert("Success!", "Brand Successfully Updated!", Message.MESSAGE_TYPE_SUCCESS);
                loadData();
                toReturn = true;
            }else {
                showAlert("Error!", "A Brand with  that name already exist!", Message.MESSAGE_TYPE_ERROR);
            }
        } else {
            showAlert("Alert!", "Fill all required fields!", Message.MESSAGE_TYPE_ERROR);
        }
        return toReturn;
    }

    /**
     * Util to Save Brand
     * @param name  Name
     * @param description  Description
     */
    private void saveBrand(String name,String description){
        Runnable task=()->{
            if (!name.isEmpty()) {
                if (!brandModel.alreadyExist(name)) {
                    Brand brand = new Brand(name, description);
                    brandModel.insert(brand);
                    Platform.runLater(() -> {
                        disableForm(true);
                        nameText.clear();
                        descriptionText.clear();
                    });
                    showAlert("Success!", "Brand Successfully Created!", Message.MESSAGE_TYPE_SUCCESS);
                    loadData();
                }else {
                    showAlert("Error!", "A Brand with  that name already exist!", Message.MESSAGE_TYPE_ERROR);
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
     * Fill The form
     * @param value Brand
     */
    private void fillForm(Brand value, boolean edit){
        if (value!=null){
            if (edit){
                nameText.setText(value.getName());
                descriptionText.setText(value.getDescription());
            }else {
                nameText.clear();
                descriptionText.clear();
            }
            disableForm(false);
        }
    }
}
