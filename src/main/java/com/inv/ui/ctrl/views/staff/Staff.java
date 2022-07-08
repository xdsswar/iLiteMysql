package com.inv.ui.ctrl.views.staff;

import com.inv.R;
import com.inv.data.access.emums.EmployeeStatus;
import com.inv.data.access.emums.Role;
import com.inv.ui.ctrl.dialogs.Message;
import com.inv.ui.in.IWidget;
import com.inv.ui.util.RoleFinder;
import com.inv.ui.util.Validator;
import com.inv.ui.widgets.EmpListFactory;
import com.inv.ui.widgets.StaffComboboxFactory;
import com.inv.xflux.entity.Employee;
import com.inv.xflux.model.EmployeeModel;
import com.xd.controls.XButton;
import com.xd.text.MaskedTextField;
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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * @author XDSSWAR
 */
@SuppressWarnings("Duplicates")
public class Staff implements Initializable, IWidget {

    @FXML
    private ListView<Employee> listView;

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
    private TextField lastNameTextField;

    @FXML
    private TextField userTextField;

    @FXML
    private MaskedTextField phoneTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField addressTextField;

    @FXML
    private MaskedTextField dateOfBirthTextField;

    @FXML
    private ComboBox<Role> roleCombobox;

    @FXML
    private ImageView profileImage;

    @FXML
    private XButton selectImageBtn;

    @FXML
    private PasswordField passwordField;

    private static final String ERROR_CLASS="unit-input-error";
    private final TextField search;
    private EmployeeModel employeeModel;
    private Employee employee;
    private ObservableList<Employee> list= FXCollections.observableArrayList();
    private static boolean editing=false;

    /**
     * Constructor
     * @param search SearchText
     * @param employee Employee to be edited in case we click profile
     */
    public Staff(final TextField search,Employee employee){
        this.search=search;
        this.employee=employee;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPane.setFitToWidth(true);
        Rectangle circle=new Rectangle(profileImage.getFitWidth(),profileImage.getFitHeight());
        circle.setArcWidth(150);
        circle.setArcHeight(150);
        profileImage.setClip(circle);
        profileImage.setCache(false);
        initCombobox();
        initList();
        loadData();
        initEvents();
        //Remove Error Class When Input Change
        initInputs();
        if (this.employee!=null){
            editing=true;
            fillForm(R.ONLINE_EMPLOYEE,true);
        }
    }

    /**
     * Init all Events
     */
    private void initEvents(){
        //Add ListView listener to clean fields
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            disableForm(true);
            editing=false;
            employee=null;
        });

        //Refresh
        refreshBtn.setOnAction(event -> {
            loadData();
        });

        //Add new
        newBtn.setOnAction(event -> {
            if (R.ONLINE_EMPLOYEE.isAdmin()){
               disableForm(false);
            }else {
                showAlert("Access Denied!","Only Administrators can create new Employees!",Message.MESSAGE_TYPE_ERROR);
            }
        });

        //Select Profile Image
        selectImageBtn.setOnAction(event -> {
            FileChooser fileChooser=new FileChooser();
            fileChooser.setTitle("Open Image");
            FileChooser.ExtensionFilter filter= new FileChooser.ExtensionFilter("Image Files", "*.png");
            fileChooser.getExtensionFilters().add(filter);
            File file=fileChooser.showOpenDialog(scrollPane.getScene().getWindow());
            if (file!=null){
                profileImage.setImage(new Image(file.toURI().toString()));
            }
        });

        //Save Client or Changes
        applyButton.setOnAction(event -> {
            if (R.ONLINE_EMPLOYEE.isAdmin()) {
                if (nameText.getText().isEmpty()) {
                    nameText.getStyleClass().add(ERROR_CLASS);
                }
                if (lastNameTextField.getText().isEmpty()) {
                    lastNameTextField.getStyleClass().add(ERROR_CLASS);
                }
                if (userTextField.getText().isEmpty()) {
                    userTextField.getStyleClass().add(ERROR_CLASS);
                }
                if (phoneTextField.getText().isEmpty()) {
                    phoneTextField.getStyleClass().add(ERROR_CLASS);
                }
                if (emailTextField.getText().isEmpty()) {
                    emailTextField.getStyleClass().add(ERROR_CLASS);
                }
                if (addressTextField.getText().isEmpty()) {
                    addressTextField.getStyleClass().add(ERROR_CLASS);
                }
                if (dateOfBirthTextField.getText().isEmpty()) {
                    dateOfBirthTextField.getStyleClass().add(ERROR_CLASS);
                }
                if (roleCombobox.getSelectionModel().isEmpty()) {
                    roleCombobox.getStyleClass().add("products-error-combobox");
                }

                //Save the Employee Here
                if (editing) {
                    Employee toEdit = listView.getSelectionModel().getSelectedItem();
                    if (employee != null) {
                        toEdit = employee;
                    }
                    editing = !editEmployee(toEdit,
                            nameText.getText(), lastNameTextField.getText(), userTextField.getText(),
                            phoneTextField.getText(), emailTextField.getText(), addressTextField.getText(),
                            dateOfBirthTextField.getText(), profileImage.getImage(), passwordField.getText());
                } else {
                    saveEmployee(nameText.getText(), lastNameTextField.getText(), userTextField.getText(),
                            phoneTextField.getText(), emailTextField.getText(), addressTextField.getText(),
                            dateOfBirthTextField.getText(), profileImage.getImage(), passwordField.getText());
                }

                employee = null;
            }else {
                showAlert("Access Denied!","You don't have enough rights to perform this action!",Message.MESSAGE_TYPE_ERROR);
            }
        });
    }

    /**
     * Init all Inputs
     */
    private void initInputs(){
        nameText.textProperty().addListener((observable, oldValue, newValue) -> {
            nameText.getStyleClass().remove(ERROR_CLASS);
        });
        lastNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            lastNameTextField.getStyleClass().remove(ERROR_CLASS);
        });
        userTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            userTextField.getStyleClass().remove(ERROR_CLASS);
        });
        phoneTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            phoneTextField.getStyleClass().remove(ERROR_CLASS);
        });
        emailTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            emailTextField.getStyleClass().remove(ERROR_CLASS);
        });
        addressTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            addressTextField.getStyleClass().remove(ERROR_CLASS);
        });
        dateOfBirthTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            dateOfBirthTextField.getStyleClass().remove(ERROR_CLASS);
        });
        roleCombobox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            roleCombobox.getStyleClass().remove("products-error-combobox");
        });


    }

    private void initList(){
        final Runnable add=()->{
            editing=false;
            fillForm(listView.getSelectionModel().getSelectedItem(),false);
        };

        final Runnable setActive=()->{
            if (employeeModel.setStatus(listView.getSelectionModel().getSelectedItem(), EmployeeStatus.ACTIVE)){
                loadData();
                showAlert("Success!"," Status successfully Changed!",Message.MESSAGE_TYPE_SUCCESS);
            }else {
                showAlert("Error!"," An Error has occurred while changing the Status!",Message.MESSAGE_TYPE_ERROR);
            }

        };

        final Runnable setInactive=()->{
            if (employeeModel.setStatus(listView.getSelectionModel().getSelectedItem(), EmployeeStatus.INACTIVE)){
                loadData();
                showAlert("Success!"," Status successfully Changed!",Message.MESSAGE_TYPE_SUCCESS);
            }else {
                showAlert("Error!"," An Error has occurred while changing the Status!",Message.MESSAGE_TYPE_ERROR);
            }
        };

        final Runnable edit=()->{
            editing=true;
            fillForm(listView.getSelectionModel().getSelectedItem(),true);
        };

        //Delete Employee
        final Runnable delete=()->{
            editing=false;
            if (listView.getSelectionModel().getSelectedItem().getId()!=R.ONLINE_EMPLOYEE.getId()) {
                if (!employeeModel.hasOrders(listView.getSelectionModel().getSelectedItem())) {
                    Platform.runLater(() -> {
                        boolean del = Message.showMessage(scrollPane.getScene().getWindow(), "Warning!",
                                "Are you sure you want tp proceed?. This action can't be undone.",
                                Message.MESSAGE_TYPE_WARNING);
                        if (del) {
                            Runnable task = () -> {
                                employeeModel.delete(listView.getSelectionModel().getSelectedItem());
                                loadData();
                                disableForm(true);
                                showAlert("Success!", "Successfully Deleted!", Message.MESSAGE_TYPE_SUCCESS);
                            };
                            Thread thread = new Thread(task);
                            thread.start();
                        }
                    });
                } else {
                    showAlert("Alert!", "You can edit, but you cannot delete an Employee who has Orders!",
                            Message.MESSAGE_TYPE_ERROR);
                }
            }else {
                showAlert("Error!", "You can edit, but you cannot delete your own account!",
                        Message.MESSAGE_TYPE_ERROR);
            }
        };


        listView.setCellFactory(new EmpListFactory<>().build("#3aaf85","USER",
                R.ONLINE_EMPLOYEE.isAdmin(),add,setActive,setInactive,edit,delete));
    }
    private void  loadData(){
        search.clear();
        Runnable task=()->{
            employeeModel=new EmployeeModel();
            list=employeeModel.getAll();
            Duration duration = Duration.millis(500);
            Timeline timeline=new Timeline(
                    new KeyFrame(duration,new KeyValue(listView.itemsProperty(),list, Interpolator.EASE_BOTH))
            );
            timeline.play();
            //Add Items
            Platform.runLater(()->{
                listView.setItems(list);
                initSearchFilter(list,search);
            });

        };
        Thread thread=new Thread(task);
        thread.start();
        disableForm(true);
    }

    /**
     * Init the Filter to search Items , this is very important method.
     * Dont place it in the wrong place , it can freeze the UI
     * @param employees List will all Employees
     * @param textField Search Text
     */
    private void initSearchFilter(ObservableList<Employee> employees, TextField textField){
        FilteredList<Employee> filteredList=new FilteredList<>(employees,e->true);
        textField.setOnKeyReleased(event -> {
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate((Predicate<? super Employee>) emp->{
                    if (newValue.isEmpty()){
                        return true;
                    }
                    String  lcf=newValue.toLowerCase();
                    if (emp.getName().toLowerCase().contains(lcf)){
                        return true;
                    }
                    else if(emp.getLastName().toLowerCase().contains(lcf)){
                        return true;
                    }
                    else if ((emp.getName()+" "+emp.getLastName()).toLowerCase().contains(lcf)){
                        return true;
                    }
                    else if (emp.getUser().toLowerCase().contains(lcf)){
                        return true;
                    }else if (emp.getPhone().contains(lcf)){
                        return true;
                    }else if (emp.getEmail().toLowerCase().contains(lcf)){
                        return true;
                    }

                    return false;
                });
            });

            SortedList<Employee> sortedList=new SortedList<>(filteredList);
            listView.setItems(sortedList);
        });
    }

    /**
     * Disable The Form
     * @param disable bool
     */
    private void disableForm(boolean disable){
        Platform.runLater(()->{
            nameText.setDisable(disable);
            lastNameTextField.setDisable(disable);
            userTextField.setDisable(disable);
            phoneTextField.setDisable(disable);
            emailTextField.setDisable(disable);
            addressTextField.setDisable(disable);
            dateOfBirthTextField.setDisable(disable);
            roleCombobox.setDisable(disable);
            passwordField.setDisable(disable);
            selectImageBtn.setDisable(disable);
            applyButton.setDisable(disable);
            profileImage.setVisible(!disable);
        });
        removeErrorClass();
        clear();
    }

    /**
     * Remove the style class Error
     */
    private void removeErrorClass(){
        Platform.runLater(()->{
            nameText.getStyleClass().remove(ERROR_CLASS);
            lastNameTextField.getStyleClass().remove(ERROR_CLASS);
            userTextField.getStyleClass().remove(ERROR_CLASS);
            phoneTextField.getStyleClass().remove(ERROR_CLASS);
            emailTextField.getStyleClass().remove(ERROR_CLASS);
            addressTextField.getStyleClass().remove(ERROR_CLASS);
            dateOfBirthTextField.getStyleClass().remove(ERROR_CLASS);
            roleCombobox.getStyleClass().remove("products-error-combobox");
        });
    }

    /**
     * Clear all Fields
     */
    private void clear(){
        Platform.runLater(()->{
            nameText.clear();
            lastNameTextField.clear();
            userTextField.clear();
            phoneTextField.clear();
            emailTextField.clear();
            addressTextField.clear();
            dateOfBirthTextField.clear();
            dateOfBirthTextField.setPromptText("MM-DD-YYYY");
            roleCombobox.getSelectionModel().clearSelection();
            roleCombobox.setPromptText("Select Role");
            profileImage.setImage(null);
            passwordField.clear();
        });
    }

    /**
     * Set up the Combobox
     */
    private void initCombobox(){
        roleCombobox.setButtonCell(new StaffComboboxFactory<>().build().call(null));
        roleCombobox.setCellFactory(new StaffComboboxFactory<>().build());
        roleCombobox.setItems(Role.getAll());
    }

    /**
     * Fill The form With Employee Data
     * @param editEmp Employee
     * @param edit bool edit true in case we want to edit an Employee
     */
    private void fillForm(Employee editEmp, boolean edit){
        if (editEmp!=null){
            if (edit){
                disableForm(false);
                Runnable task=()->{
                    RoleFinder<Role> finder=new RoleFinder<>();
                    Image image= editEmp.getPicture();
                    int index=finder.find(Role.getAll(),editEmp.getRole());
                    Platform.runLater(()->{
                        profileImage.setImage(image);
                        nameText.setText(editEmp.getName());
                        lastNameTextField.setText(editEmp.getLastName());
                        userTextField.setText(editEmp.getUser());
                        phoneTextField.setText(editEmp.getPhone());
                        emailTextField.setText(editEmp.getEmail());
                        addressTextField.setText(editEmp.getAddress());
                        dateOfBirthTextField.setText(editEmp.getBirthDate());
                        roleCombobox.getSelectionModel().select(index);
                    });
                };
                Thread thread=new Thread(task);
                thread.start();
            }else {
                disableForm(false);
            }
        }
    }


    /**
     * Save Employee
     * @param name Name
     * @param lastName Last Name
     * @param user User Name
     * @param phone Phone
     * @param email Email
     * @param address Address
     * @param birthDate Date of Birth
     * @param picture Profile Image
     * @param password Password
     */
    private void saveEmployee(String name,String lastName,String user,String phone,
                              String email,String address,String birthDate, Image picture, String password){
        Runnable task=()->{
            if (!name.isEmpty()&&!lastName.isEmpty()&&!user.isEmpty()&&!phone.isEmpty()&&!email.isEmpty()
                    &&!address.isEmpty()&&!birthDate.isEmpty()){
                //Check if the username is already taked
                if (!employeeModel.isUserTaked(user)){
                    if (Validator.validateEmail(email)) {
                        if (picture != null) {
                            Employee emp = new Employee(name, lastName, user, phone, email, address, birthDate, picture);
                            employeeModel.insert(emp);
                            emp = employeeModel.getByUser(user);
                            //Set the Role
                            if (!roleCombobox.getSelectionModel().isEmpty()) {
                                employeeModel.setRole(emp, roleCombobox.getSelectionModel().getSelectedItem());
                            }
                            //Update Password if not Empty
                            if (!password.isEmpty()) {
                                employeeModel.setPassword(emp, password);
                            }

                            disableForm(true);
                            showAlert("Success!", "Employee Successfully Created!", Message.MESSAGE_TYPE_SUCCESS);
                            loadData();
                        } else {
                            showAlert("Alert!", "Please select a profile image!", Message.MESSAGE_TYPE_ERROR);
                        }
                    }else {
                        Platform.runLater(()->{
                            emailTextField.getStyleClass().add(ERROR_CLASS);
                        });
                        showAlert("Attention!", "Please enter a valid Email Address!", Message.MESSAGE_TYPE_ERROR);
                    }
                }else {
                    Platform.runLater(()->{
                        userTextField.getStyleClass().add(ERROR_CLASS);
                    });
                    showAlert("Warning!", "The username you entered is already in use!", Message.MESSAGE_TYPE_ERROR);
                }
            }else {
                editing=false;
                showAlert("Information!", "Fill all required fields!", Message.MESSAGE_TYPE_ERROR);
            }
        };

        Thread thread=new Thread(task);
        thread.start();

    }

    /**
     * Edit Selected Employee , only the information you edit will be saved
     * @param oldEmp Employee to be edited
     * @param name new Name
     * @param lastName new las name
     * @param user new User
     * @param phone new Phone
     * @param email new Email
     * @param address  new Address
     * @param birthDate new date
     * @param picture new Image
     * @param password new Password
     * @return true when done
     */
    private boolean editEmployee(Employee oldEmp,String name,String lastName,String user,String phone,
                                 String email,String address,String birthDate, Image picture, String password){
        boolean toReturn=false;
        if (!name.isEmpty()&&!lastName.isEmpty()&&!user.isEmpty()&&!phone.isEmpty()&&!email.isEmpty()
                &&!address.isEmpty()&&!birthDate.isEmpty()){
            //Check if Username is already in use but skipping the actual username
            //in case we kee that username
            if (!employeeModel.isUserTaked(user) || oldEmp.getUser().equalsIgnoreCase(user)) {
                if (Validator.validateEmail(email)) {
                    Employee newEmployee = new Employee(name, lastName, user, phone, email, address, birthDate, picture);
                    employeeModel.update(oldEmp, newEmployee);
                    //Set the Role
                    if (!roleCombobox.getSelectionModel().isEmpty()) {
                        employeeModel.setRole(oldEmp, roleCombobox.getSelectionModel().getSelectedItem());
                    }
                    //Update Picture
                    if (picture.getUrl() != null) {
                        employeeModel.updatePicture(oldEmp, picture);
                    }
                    //Update Password if not Empty
                    if (!password.isEmpty()) {
                        employeeModel.setPassword(oldEmp, password);
                    }

                    disableForm(true);
                    showAlert("Success!", "Employee Successfully Updated!", Message.MESSAGE_TYPE_SUCCESS);
                    loadData();
                    toReturn = true;
                }else {
                    Platform.runLater(()->{
                        emailTextField.getStyleClass().add(ERROR_CLASS);
                    });
                    showAlert("Attention!", "Please enter a valid Email Address!", Message.MESSAGE_TYPE_ERROR);

                }
            }else {
                Platform.runLater(()->{
                    userTextField.getStyleClass().add(ERROR_CLASS);
                });
                showAlert("Warning!", "The username you entered is already in use!", Message.MESSAGE_TYPE_ERROR);
            }

        }else {
            showAlert("Information!", "Fill all required fields!", Message.MESSAGE_TYPE_ERROR);
        }

        return toReturn;
    }


    /**
     * Util to show Alerts on  saving
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
