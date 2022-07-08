package com.inv.ui.ctrl;

import com.inv.R;
import com.inv.ui.ctrl.dialogs.Message;
import com.inv.ui.ctrl.views.brands.Brands;
import com.inv.ui.ctrl.views.categories.Categories;
import com.inv.ui.ctrl.views.clients.Clients;
import com.inv.ui.ctrl.views.currency.Currencies;
import com.inv.ui.ctrl.views.help.Help;
import com.inv.ui.ctrl.views.home.Home;
import com.inv.ui.ctrl.views.orders.Orders;
import com.inv.ui.ctrl.views.products.Products;
import com.inv.ui.ctrl.views.providers.Providers;
import com.inv.ui.ctrl.views.settings.Settings;
import com.inv.ui.ctrl.views.staff.Staff;
import com.inv.ui.ctrl.views.tax.Taxes;
import com.inv.ui.ctrl.views.units.Units;
import com.inv.ui.util.Anima;
import com.inv.ui.util.StageHelper;
import com.inv.ui.util.WidgetUtil;
import com.inv.ui.util.WidthAnimation;
import com.xd.controls.XButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author XDSSWAR
 */
@SuppressWarnings("all")
public final   class MainCtrl implements Initializable {


    @FXML
    private AnchorPane baseAnchor;

    @FXML
    private AnchorPane leftPane;

    @FXML
    private XButton menuBtn;

    @FXML
    private XButton dashBtn;

    @FXML
    private XButton ordersBtn;

    @FXML
    private XButton clientsBtn;

    @FXML
    private XButton categoriesBtn;

    @FXML
    private XButton brandsBtn;

    @FXML
    private XButton providerBtn;

    @FXML
    private XButton productsBtn;

    @FXML
    private XButton currencyBtn;

    @FXML
    private XButton taxBtn;

    @FXML
    private XButton unitBtn;

    @FXML
    private XButton staffBtn;

    @FXML
    private XButton reportsBtn;

    @FXML
    private XButton settingsBtn;

    @FXML
    private XButton helpBtn;

    @FXML
    private AnchorPane rightPane;

    @FXML
    private AnchorPane dashHeader;

    @FXML
    private TextField searchTxt;

    @FXML
    private XButton searchBtn;

    @FXML
    private Button notifyButton;

    @FXML
    private Button userBtn;

    @FXML
    private ImageView userImg;

    @FXML
    private ContextMenu userContextMenu;

    @FXML
    private MenuItem profileMenuItem;

    @FXML
    private MenuItem settingsMenuItem;

    @FXML
    private MenuItem activityMenuItem;

    @FXML
    private MenuItem logOutMenuItem;

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private AnchorPane widgetContainer;

    @FXML
    private ScrollPane scrollPane;

    private final ObservableList<Button> buttons= FXCollections.observableArrayList();
    private static WidgetUtil widgetUtil=null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        scrollPane.setFitToWidth(true);
        menuBtn.setDisableVisualFocus(true);
        Rectangle circle = new Rectangle(userImg.getFitWidth(), userImg.getFitHeight());
        circle.setArcWidth(50);
        circle.setArcHeight(50);
        userImg.setClip(circle);
        userImg.setImage(R.ONLINE_EMPLOYEE.getPicture());
        //Open menu in the saved state
        loadMenuState();
        //Menu Animation on click
        menuBtn.setOnAction(event -> menuAnimation());
        //User Menu
        initUserContext();
        //Menu Buttons Events
        setMenuEvents();
        //Context Menu Events
        setContextMenuEvents();
        buttons.clear();
        buttons.addAll(dashBtn,ordersBtn,clientsBtn,categoriesBtn,brandsBtn ,taxBtn,
                providerBtn,productsBtn,currencyBtn,unitBtn,staffBtn, reportsBtn,
                settingsBtn,helpBtn);
        //Load Home Widget
        addButtonSelectedEffect(dashBtn,buttons);
        searchTxt.setDisable(true);
        Home homeController=new Home(searchTxt);
        widgetUtil=new WidgetUtil();
        widgetUtil.addScrollPane(widgetContainer,"home/home",homeController);
    }

    /**
     * This will contain all Menu Buttons Events
     */
    private void setMenuEvents(){

        dashBtn.setOnAction(event -> {
            searchTxt.setDisable(true);
            addButtonSelectedEffect(dashBtn,buttons);
            Home homeController=new Home(searchTxt);
            widgetUtil.addScrollPane(widgetContainer, "home/home",homeController);
        });

        ordersBtn.setOnAction(event -> {
            searchTxt.setDisable(false);
            addButtonSelectedEffect(ordersBtn,buttons);
            Orders ordersController = new Orders(searchTxt,false);
            widgetUtil.addViewWithController(widgetContainer, "orders/orders",ordersController);
        });

        clientsBtn.setOnAction(event -> {
            searchTxt.setDisable(false);
            addButtonSelectedEffect(clientsBtn,buttons);
            Clients clientsController = new Clients(searchTxt);
            widgetUtil.addViewWithController(widgetContainer, "clients/clients",clientsController);
        });

        categoriesBtn.setOnAction(event -> {
            searchTxt.setDisable(false);
            addButtonSelectedEffect(categoriesBtn,buttons);
            Categories categoriesController = new Categories(searchTxt);
            widgetUtil.addViewWithController(widgetContainer, "categories/categories",categoriesController);
        });

        brandsBtn.setOnAction(event -> {
            searchTxt.setDisable(false);
            Brands brandsController = new Brands(searchTxt);
            addButtonSelectedEffect(brandsBtn,buttons);
            widgetUtil.addViewWithController(widgetContainer,"brands/brands",brandsController);
        });

        providerBtn.setOnAction(event -> {
            searchTxt.setDisable(false);
            Providers providersController = new Providers(searchTxt);
            addButtonSelectedEffect(providerBtn,buttons);
            widgetUtil.addViewWithController(widgetContainer, "providers/providers",providersController);
        });

        productsBtn.setOnAction(event -> {
            searchTxt.setDisable(false);
            Products productsController = new Products(searchTxt);
            addButtonSelectedEffect(productsBtn,buttons);
            widgetUtil.addViewWithController(widgetContainer, "products/products",productsController);
        });

        currencyBtn.setOnAction(event -> {
            searchTxt.setDisable(false);
            Currencies currenciesController = new Currencies(searchTxt);
            addButtonSelectedEffect(currencyBtn,buttons);
            widgetUtil.addViewWithController(widgetContainer, "currency/currency", currenciesController);
        });

        taxBtn.setOnAction(event -> {
            searchTxt.setDisable(false);
            Taxes taxesController = new Taxes(searchTxt);
            addButtonSelectedEffect(taxBtn,buttons);
            widgetUtil.addViewWithController(widgetContainer,"tax/tax",taxesController);
        });

        unitBtn.setOnAction(event -> {
            searchTxt.setDisable(false);
            Units unitsController = new Units(searchTxt);
            addButtonSelectedEffect(unitBtn,buttons);
            widgetUtil.addViewWithController(widgetContainer, "units/units",unitsController);
        });

        //Staff View
        staffBtn.setOnAction(event -> {
            //Enforce Roles for Protection
            if (R.ONLINE_EMPLOYEE.isAdmin()){
                searchTxt.setDisable(false);
                Staff staffController=new Staff(searchTxt,null);
                addButtonSelectedEffect(staffBtn,buttons);
                widgetUtil.addViewWithController(widgetContainer, "staff/staff",staffController);
            }else {
                showDeniedNotice();
            }
        });

        reportsBtn.setOnAction(event -> {
            searchTxt.setDisable(true);
            addButtonSelectedEffect(reportsBtn,buttons);
            widgetUtil.addScrollPane(widgetContainer, "reports/reports");
        });

        //Settings
        settingsBtn.setOnAction(event -> {
            //Enforce Roles for Protection
            if (R.ONLINE_EMPLOYEE.isAdmin()) {
                searchTxt.setDisable(false);
                Settings settingsController = new Settings(searchTxt);
                addButtonSelectedEffect(settingsBtn, buttons);
                widgetUtil.addViewWithController(widgetContainer, "settings/settings", settingsController);
            }else {
                showDeniedNotice();
            }
        });

        helpBtn.setOnAction(event -> {
            searchTxt.setDisable(true);
            addButtonSelectedEffect(helpBtn,buttons);
            Help helpController=new Help();
            widgetUtil.addViewWithController(widgetContainer,"help/help",helpController);
        });
    }

    /**
     * Add Selected Effect to the Selected Button
     * @param target Button Target
     * @param list Button list
     */
    private void addButtonSelectedEffect(Button target, ObservableList<Button> list){
        final String effect="dash-left-pane-buttons-effect";
        for (Button b:list){
            b.getStyleClass().removeAll(effect);
        }
        target.getStyleClass().add(effect);
        Runtime.getRuntime().gc();
    }

    /**
     * Set all Context MenuItems Events
     */
    private  void setContextMenuEvents(){
        StageHelper helper=new StageHelper();
        //Go to Profile of current Online Employee
        profileMenuItem.setOnAction(event -> {
            //Enforce Roles to Protect Information
            if (R.ONLINE_EMPLOYEE.isAdmin()){
                searchTxt.setDisable(false);
                Staff staffController=new Staff(searchTxt,R.ONLINE_EMPLOYEE);
                addButtonSelectedEffect(staffBtn,buttons);
                widgetUtil.addViewWithController(widgetContainer, "staff/staff",staffController);
            }else {
                showDeniedNotice();
            }
        });

        //Go to  Settings
        settingsMenuItem.setOnAction(event -> {
            //Enforce Roles for Protection
            if (R.ONLINE_EMPLOYEE.isAdmin()) {
                Settings settingsController = new Settings(searchTxt);
                addButtonSelectedEffect(settingsBtn, buttons);
                widgetUtil.addViewWithController(widgetContainer, "settings/settings", settingsController);
            }else {
                showDeniedNotice();
            }
        });
        activityMenuItem.setOnAction(event -> {

        });
        //Log Out
        logOutMenuItem.setOnAction(event ->{
            helper.showStage("login.fxml","Sign In");
        });
        //About
        aboutMenuItem.setOnAction(event -> {
            helper.showInternWindow(((Stage)baseAnchor.getScene().getWindow()),"about/about.fxml","About "+R.SOFTWARE_NAME,R.ABOUT_ICON);
        });
    }


    /**
     * This method will run the menu animation and save the menu state
     */
    private void menuAnimation(){
        WidthAnimation widthAnimation=new WidthAnimation();
        if (leftPane.getWidth()<52){
            widthAnimation.widthAnimation(leftPane,200,rightPane,200);
            Runnable task=()->{
                R.IS_DASHBOARD_MENU_EXPANDED=true;
            };
            Thread thread = new Thread(task);
            thread.start();
        }else if (leftPane.getWidth()>199){
            widthAnimation.widthAnimation(leftPane,50,rightPane,50);
            Runnable task=()->{
                R.IS_DASHBOARD_MENU_EXPANDED=false;
            };
            Thread thread = new Thread(task);
            thread.start();
        }
    }

    /**
     * This will load the last menu State
     */
    private void loadMenuState(){
        WidthAnimation widthAnimation=new WidthAnimation();
        if (R.IS_DASHBOARD_MENU_EXPANDED){
            widthAnimation.widthAnimation(leftPane,200,rightPane,200);
        }else {
            widthAnimation.widthAnimation(leftPane,50,rightPane,50);
        }
    }

    /**
     * Init User MEnu Context
     */
    private void initUserContext(){
        userBtn.setText(R.ONLINE_EMPLOYEE.getName()+" "+R.ONLINE_EMPLOYEE.getLastName());
        userBtn.setOnMouseClicked(event -> {
            Anima anima=new Anima();
            userContextMenu.setOpacity(0);
            userContextMenu.show(rightPane, event.getScreenX()-event.getX(),
                    event.getScreenY()+(userBtn.getHeight()-event.getY())+7);
            anima.fadeInContextMenu(userContextMenu,400);
        });
    }

    /**
     * Show Denied Dialog
     */
    private void showDeniedNotice(){
        Message.showMessage(baseAnchor.getScene().getWindow(),"Access Denied!",
                "You don't have enough rights to view this information!",Message.MESSAGE_TYPE_ERROR);
    }

}
