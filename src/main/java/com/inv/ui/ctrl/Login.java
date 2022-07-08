package com.inv.ui.ctrl;

import com.inv.R;
import com.inv.data.access.MysqlConnection;
import com.inv.data.access.Settings;
import com.inv.data.access.maintenance.Check;
import com.inv.ui.util.Anima;
import com.inv.ui.util.DragUtil;
import com.inv.ui.util.StageHelper;
import com.inv.xflux.entity.Employee;
import com.inv.xflux.model.EmployeeModel;
import com.xd.controls.XButton;
import com.xd.ss.war.materialicon.MaterialIconView;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author XDSSWAR
 */
@SuppressWarnings("all")
public final class Login extends DragUtil implements Initializable {


    @FXML
    private AnchorPane base;

    @FXML
    private HBox loginDialog;

    @FXML
    private TextField userText;

    @FXML
    private PasswordField passText;

    @FXML
    private XButton loginBtn;

    @FXML
    private Label errorLabel;

    @FXML
    private Label settingsBtn;

    @FXML
    private MaterialIconView exitBtn;

    @FXML
    private AnchorPane settingsDialog;

    @FXML
    private TextField serverTxt;

    @FXML
    private TextField portTxt;

    @FXML
    private TextField dbNameTxt;

    @FXML
    private TextField dbUserTxt;

    @FXML
    private PasswordField dbPassTxt;

    @FXML
    private XButton applySettingsBtn;

    @FXML
    private MaterialIconView settingsClose;

    @FXML
    private Label signInBtn,settingsErrorLabel;

    @FXML
    private Hyperlink inteliJLink;

    @FXML
    private CheckBox useSslCheckBox;

    @FXML
    private ImageView headerIcon, bottomIcon, settingsImageView,loginImageView;


    private BooleanProperty isSettingsActive=new SimpleBooleanProperty(false);
    public static final String ERROR_LOGIN_CLASS="login-error-label";
    public static final String DONE_LOGIN_CLASS="login-done-label";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initImageClips();
        if (!R.IS_DB_OK){
            showSettings();
        }else {
            signInBtn.requestFocus();
        }
        //Set class for error inputs
        errorLabel.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                if (userText.getText().isEmpty()) {
                    userText.getStyleClass().add("login-input-error");
                }
                if (passText.getText().isEmpty()){
                    passText.getStyleClass().add("login-input-error");
                }
                if (!passText.getText().isEmpty() && !userText.getText().isEmpty()){
                    userText.getStyleClass().add("login-input-error");
                    passText.getStyleClass().add("login-input-error");
                }
            }else {
                userText.getStyleClass().remove("login-input-error");
                passText.getStyleClass().remove("login-input-error");
            }
        });
        //Make Stage Draggable
        setDraggable(base,false);
        //Exit Application
        exitBtn.setOnMouseClicked(event -> Platform.exit());
        settingsClose.setOnMouseClicked(event->Platform.exit());
        //Show Settings Dialog
        settingsBtn.setOnMouseClicked(event->showSettings());
        //Show Sign in dialog
        signInBtn.setOnMouseClicked(event->showSettings());
        //Login
        loginBtn.setOnAction(event -> login());
        //Hide Error label when user types something
        userText.textProperty().addListener(e->setErrorLabel(errorLabel,"",false));
        passText.textProperty().addListener(e->setErrorLabel(errorLabel,"",false));
        serverTxt.textProperty().addListener(e->setErrorLabel(settingsErrorLabel,"",false));
        portTxt.textProperty().addListener(e->setErrorLabel(settingsErrorLabel,"",false));
        dbNameTxt.textProperty().addListener(e->setErrorLabel(settingsErrorLabel,"",false));
        dbUserTxt.textProperty().addListener(e->setErrorLabel(settingsErrorLabel,"",false));
        dbPassTxt.textProperty().addListener(e->setErrorLabel(settingsErrorLabel,"",false));
        //Apply Database Settings
        applySettingsBtn.setOnAction(event->{
            saveSettings(
                    serverTxt.getText(),
                    portTxt.getText(),
                    dbNameTxt.getText(),
                    dbUserTxt.getText(),
                    dbPassTxt.getText()
            );
        });
        //Sign In Password Enter key Event
        passText.setOnKeyPressed(event -> {
            if (event.getCode()== KeyCode.ENTER){
                login();
            }
        });
        //DB Settings Password Enter key Event
        dbPassTxt.setOnKeyPressed(event -> {
            if (event.getCode()==KeyCode.ENTER){
                saveSettings(
                        serverTxt.getText(),
                        portTxt.getText(),
                        dbNameTxt.getText(),
                        dbUserTxt.getText(),
                        dbPassTxt.getText()
                );
            }
        });

        //HiperLink Event
        inteliJLink.setOnAction(event -> {
            R.APP.getHostServices().showDocument("https://www.jetbrains.com/idea/");
        });
        //Init login Slider
        Anima.stopSlideShow();
        initSettingsSlideShow(loginImageView,3);

        isSettingsActive.addListener((observable, oldValue, newValue) -> {
            if (newValue){
                Anima.stopSlideShow();
                initSettingsSlideShow(settingsImageView,3);
            }else {
                Anima.stopSlideShow();
                initSettingsSlideShow(loginImageView,3);
            }
        });

    }

    /**
     * Show Settings Dialog
     */
    private void showSettings(){
        Anima anima=new Anima();
        if (isSettingsActive.get()){
            isSettingsActive.set(false);
            settingsDialog.toBack();
            loginDialog.setOpacity(0);
            loginDialog.setVisible(true);
            loginDialog.toFront();
            anima.fadeInTransition(loginDialog);
            settingsDialog.setVisible(false);
            R.STAGE.setTitle("Sign In");
            signInBtn.requestFocus();
        }else {
            isSettingsActive.set(true);
            loginDialog.toBack();
            settingsDialog.setOpacity(0);
            settingsDialog.setVisible(true);
            settingsDialog.toFront();
            anima.fadeInTransition(settingsDialog);
            loginDialog.setVisible(false);
            R.STAGE.setTitle("Database Settings");
            applySettingsBtn.requestFocus();
            loadSettings();
        }
        settingsErrorLabel.getStyleClass().add(ERROR_LOGIN_CLASS);
        setErrorLabel(settingsErrorLabel,"",false);
        setErrorLabel(errorLabel,"",false);
        errorLabel.getStyleClass().add(ERROR_LOGIN_CLASS);
    }

    /**
     * Login
     * Do not modify anything here if you dont have idea
     */
    private void login(){
        Runnable task=()->{
            if (MysqlConnection.checkConnection()) {
                R.IS_DB_OK=true;
                if (!userText.getText().isEmpty() && !passText.getText().isEmpty()) {
                    if (Check.isEmployee(userText.getText(), passText.getText())) {
                        Platform.runLater(()-> {
                            errorLabel.getStyleClass().add(DONE_LOGIN_CLASS);
                            setErrorLabel(errorLabel,"Signing In...", true);
                            //Show Dashboard here
                            showDashboard(new EmployeeModel().getByUser(userText.getText()));

                        });
                    } else {
                        Platform.runLater(()->{
                            errorLabel.getStyleClass().add(ERROR_LOGIN_CLASS);
                            setErrorLabel(errorLabel,"Wrong Username or Password!", true);
                        });
                    }
                }else {
                    Platform.runLater(()->{
                        errorLabel.getStyleClass().add(ERROR_LOGIN_CLASS);
                        setErrorLabel(errorLabel,"Please enter a valid Username & Password!", true);
                    });
                }
            }else {
                Platform.runLater(()->{
                    errorLabel.getStyleClass().add(ERROR_LOGIN_CLASS);
                    setErrorLabel(errorLabel,"Database Connection Error!",true);
                    R.IS_DB_OK=false;
                });
            }
        };
        Thread thread=new Thread(task);
        thread.start();
    }

    /**
     * Set error label Message
     * @param msg Message
     */
    private void setErrorLabel(Label label,String msg,boolean visible){
        label.setText(msg);
        label.setVisible(visible);
    }

    /**
     * Save database Settings
     * @param server Server
     * @param port Port
     * @param dbName Db name
     * @param dbUser Db user
     * @param dbPass Db Pass
     */
    private void saveSettings(String server,String port,String dbName,String dbUser,String dbPass){
        Runnable task=()->{
            if (server.isEmpty() || port.isEmpty() || dbName.isEmpty() || dbUser.isEmpty()){
                Platform.runLater(()->{
                    settingsErrorLabel.getStyleClass().remove(DONE_LOGIN_CLASS);
                    settingsErrorLabel.getStyleClass().add(ERROR_LOGIN_CLASS);
                    setErrorLabel(settingsErrorLabel,"All fields are required!",true);
                });
            }else {
                //Thread to Save Settings and Reset MySql Connection
                Runnable r1=()->{
                    Platform.runLater(()->{
                        settingsErrorLabel.getStyleClass().remove(ERROR_LOGIN_CLASS);
                        settingsErrorLabel.getStyleClass().add(DONE_LOGIN_CLASS);
                        setErrorLabel(settingsErrorLabel,"Success...",true);
                    });
                    Settings.setSettings("server", server);
                    Settings.setSettings("port",port);
                    Settings.setSettings("dbname",dbName);
                    Settings.setSettings("dbUser",dbUser);
                    if (dbPass.isEmpty()) {
                        Settings.setSettings("dbPassword","");
                    }
                    Settings.setSettings("dbPassword",dbPass);
                    if (useSslCheckBox.isSelected()){
                        Settings.setSettings("use.ssl","true");
                    }else {
                        Settings.setSettings("use.ssl","false");
                    }
                    MysqlConnection.reset();
                };
                Thread t1 = new Thread(r1);
                t1.start();

                //Thread to send back to loagin
                Runnable r2=()->{
                    Platform.runLater(()->{
                        settingsErrorLabel.getStyleClass().remove(DONE_LOGIN_CLASS);
                        settingsErrorLabel.getStyleClass().add(ERROR_LOGIN_CLASS);
                        setErrorLabel(settingsErrorLabel,"Success...",false);
                        showSettings();
                    });
                };
                Thread t2=new Thread(r2);
                t2.start();
            }
        };
        Thread saveSettings=new Thread(task);
        saveSettings.start();
    }

    /**
     *Load Database Settings Once its Displayed
     */
    private void loadSettings(){
        Runnable task=()->{
            String server=Objects.requireNonNull(Settings.getSettingsValue("server")).toString();
            String port=Objects.requireNonNull(Settings.getSettingsValue("port")).toString();
            String dbName=Objects.requireNonNull(Settings.getSettingsValue("dbname")).toString();
            String dbUser=Objects.requireNonNull(Settings.getSettingsValue("dbUser")).toString();
            String password=Objects.requireNonNull(Settings.getSettingsValue("dbPassword")).toString();
            String useSslVal=Objects.requireNonNull(Settings.getSettingsValue("use.ssl")).toString();
            Platform.runLater(()->{
                serverTxt.setText(server);
                portTxt.setText(port);
                dbNameTxt.setText(dbName);
                dbUserTxt.setText(dbUser);
                dbPassTxt.setText(password);
                if (useSslVal.equalsIgnoreCase("true")){
                    useSslCheckBox.setSelected(true);
                }else {
                    useSslCheckBox.setSelected(false);
                }
            });
        };
        Thread thread=new Thread(task);
        thread.start();

    }

    /**
     * Show Dashboard
     * @param employee Online Employee
     */
    private void showDashboard(Employee employee){
        StageHelper helper=new StageHelper();
        helper.showDashboardStage(base,employee);
    }

    /**
     * Set and Init image Clips
     */
    private void initImageClips(){
        Rectangle circleHeader=new Rectangle(headerIcon.getFitWidth(),headerIcon.getFitHeight());
        circleHeader.setArcHeight(50);
        circleHeader.setArcWidth(50);
        Rectangle circleBottom=new Rectangle(bottomIcon.getFitWidth(),bottomIcon.getFitHeight());
        circleBottom.setArcHeight(50);
        circleBottom.setArcWidth(50);
        headerIcon.setClip(circleHeader);
        bottomIcon.setClip(circleBottom);
    }

    /**
     * Init Settings SlideShow
     */
    private void initSettingsSlideShow(ImageView imageView,int seconds){
        Runnable task=()->{
            ObservableList<Image> list=FXCollections.observableArrayList(
                    new Image(R.class.getResource("/assets/slide/1.png").toExternalForm()),
                    new Image(R.class.getResource("/assets/slide/2.png").toExternalForm()),
                    new Image(R.class.getResource("/assets/slide/3.png").toExternalForm()),
                    new Image(R.class.getResource("/assets/slide/4.png").toExternalForm()),
                    new Image(R.class.getResource("/assets/slide/5.png").toExternalForm()),
                    new Image(R.class.getResource("/assets/slide/6.png").toExternalForm()),
                    new Image(R.class.getResource("/assets/slide/7.png").toExternalForm())
            );
            Platform.runLater(()->Anima.slideShow(imageView,list,seconds));
        };
        Thread slideTread = new Thread(task);
        slideTread.start();
    }
    /**
     * Init Settings SlideShow
     */
    private void initLoginSlideShow(ImageView imageView,int seconds){
        Runnable task=()->{
            ObservableList<Image> list=FXCollections.observableArrayList(
                    new Image(R.class.getResource("/assets/slide/1.png").toExternalForm()),
                    new Image(R.class.getResource("/assets/slide/2.png").toExternalForm()),
                    new Image(R.class.getResource("/assets/slide/3.png").toExternalForm()),
                    new Image(R.class.getResource("/assets/slide/4.png").toExternalForm()),
                    new Image(R.class.getResource("/assets/slide/5.png").toExternalForm()),
                    new Image(R.class.getResource("/assets/slide/6.png").toExternalForm()),
                    new Image(R.class.getResource("/assets/slide/7.png").toExternalForm())
            );
            Platform.runLater(()->Anima.slideShow(imageView,list,seconds));
        };
        Thread slideTread = new Thread(task);
        slideTread.start();
    }
}
