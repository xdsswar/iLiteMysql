package com.inv.ui.ctrl.views.categories;

import com.inv.R;
import com.inv.ui.ctrl.dialogs.Message;
import com.inv.ui.in.IWidget;
import com.inv.ui.widgets.ListableXListFactory;
import com.inv.xflux.entity.Category;
import com.inv.xflux.model.CategoryModel;
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
public final class Categories implements Initializable , IWidget {

    @FXML
    private ListView<Category> listView;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField nameText;

    @FXML
    private TextArea descriptionText;

    @FXML
    private XButton applyButton;

    @FXML
    private XButton newBtn;

    @FXML
    private XButton refreshBtn;

    private final TextField search;
    private ObservableList<Category> catList= FXCollections.observableArrayList();
    private CategoryModel categoryModel;
    /*
    This boolean is very important , Dont play with it
 */
    private static boolean editing=false;

    public Categories( final TextField search){
        this.search=search;
        categoryModel=new CategoryModel();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPane.setFitToWidth(true);
        initList();
        loadData();

        //Evade Editing another row while the form still filled
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
         * Enable Form to create new Category
         */
        newBtn.setOnAction(event -> {
            if (R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin()) {
                nameText.clear();
                descriptionText.clear();
                disableForm(false);
            }else {
                Message.showMessage(scrollPane.getScene().getWindow(),"Security Alert!","Only Admins and Managers can create new Categories!",
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
         *Save Category or edit Category
         */
        applyButton.setOnAction(event -> {
            if (R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin()) {
                if (nameText.getText().isEmpty()) {
                    nameText.getStyleClass().add("unit-input-error");
                }
                if (editing) {
                    editing = !editCategory(listView.getSelectionModel().getSelectedItem(),
                            nameText.getText(), descriptionText.getText());

                } else {
                    saveCategory(nameText.getText(), descriptionText.getText());
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

        final Runnable delete=()->{
            editing=false;
            if (listView.getSelectionModel().getSelectedItem().getId() != 1) {
                if (!categoryModel.isUsed(listView.getSelectionModel().getSelectedItem())){
                    Platform.runLater(() -> {
                        boolean del = Message.showMessage(scrollPane.getScene().getWindow(), "Warning!",
                                "Are you sure you want tp proceed?. This action can't be undone.",
                                Message.MESSAGE_TYPE_WARNING);
                        if (del) {
                            Runnable r = () -> {
                                categoryModel.delete(listView.getSelectionModel().getSelectedItem());
                                loadData();
                                Platform.runLater(() -> {
                                    nameText.clear();
                                    descriptionText.clear();
                                    disableForm(true);
                                });
                                showAlert("Success!","Successfully Deleted!",Message.MESSAGE_TYPE_SUCCESS);
                            };
                            r.run();
                        }
                    });
                }else {
                    showAlert("Error!","You can edit, but you cannot delete a used Category!",
                            Message.MESSAGE_TYPE_ERROR);
                }
            } else {
                showAlert("Error!","You can not delete a default Category!",Message.MESSAGE_TYPE_ERROR);
            }
        };
        listView.setCellFactory(new ListableXListFactory<Category>().build("#007ee5","CERTIFICATE",
                R.ONLINE_EMPLOYEE.isManager() || R.ONLINE_EMPLOYEE.isAdmin() ,add,edit,delete));
    }

    /**
     * Load all Categories
     */
    private void loadData(){
        search.clear();
        Runnable task=()->{
            catList=categoryModel.getAll();
            Duration duration = Duration.millis(500);
            Timeline timeline = new Timeline(
                    new KeyFrame(duration,new KeyValue(listView.itemsProperty(),catList, Interpolator.EASE_BOTH))
            );
            timeline.play();
            //Add items to the Table
            Platform.runLater(()->{
                //Hack Around for java 11
                listView.setItems(catList);
                search.clear();
                initSearchFilter(catList,search);
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
     * @param catVal List with all the values
     * @param textField TextField instance from the MainCeTrl
     */
    private void initSearchFilter(ObservableList<Category> catVal, TextField textField){
        FilteredList<Category> filteredList=new FilteredList<>(catVal, e->true);
        textField.setOnKeyReleased(event -> {
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate((Predicate<? super Category>) cat->{
                    if (newValue.isEmpty()){
                        return true;
                    }
                    String lcF=newValue.toLowerCase();
                    if (cat.getName().toLowerCase().contains(lcF)){
                        return true;
                    } else if (cat.getDescription().toLowerCase().contains(lcF)) {
                        return true;
                    }
                    return false;
                });
            });
            SortedList<Category> sortedList=new SortedList<>(filteredList);
            listView.setItems(sortedList);
        });
    }

    /**
     * Fill The form
     * @param value Category
     */
    private void fillForm(Category value, boolean edit){
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
     * Util to show Alerts on Category saving
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
     * Util to Save Category
     * @param name Category Name
     * @param description Category Description
     */
    private void saveCategory(String name, String description){
        Runnable task=()->{
            if (!name.isEmpty()) {
                if (!categoryModel.alreadyExist(name)) {
                    Category category = new Category(name, description);
                    categoryModel.insert(category);
                    Platform.runLater(() -> {
                        disableForm(true);
                        nameText.clear();
                        descriptionText.clear();
                    });
                    showAlert("Success!", "Category Successfully Created!", Message.MESSAGE_TYPE_SUCCESS);
                    loadData();
                }else {
                    showAlert("Error!", "A Category with that name already exist!", Message.MESSAGE_TYPE_ERROR);
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
     * Save Edited Category
     * @param oldCat Category to be edited
     * @param name name
     * @param description description
     * @return boolean
     */
    private boolean editCategory(Category oldCat, String name, String description){
        boolean toReturn=false;
        if (!name.isEmpty()) {
            if (!categoryModel.alreadyExist(name) || oldCat.getName().equalsIgnoreCase(name)) {
                Category category = new Category(name, description);
                categoryModel.update(oldCat, category);
                Platform.runLater(() -> {
                    disableForm(true);
                    nameText.clear();
                    descriptionText.clear();
                });
                showAlert("Success!", "Category Successfully Updated!", Message.MESSAGE_TYPE_SUCCESS);
                loadData();
                toReturn = true;
            }else {
                showAlert("Error!", "A Category with that name already exist!", Message.MESSAGE_TYPE_ERROR);
            }
        } else {
            showAlert("Alert!", "Fill all required fields!", Message.MESSAGE_TYPE_ERROR);
        }
        return toReturn;
    }
}
