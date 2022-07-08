package com.inv.ui.ctrl.views.settings;

import com.inv.ui.ctrl.views.settings.company.CompanyView;
import com.inv.ui.ctrl.views.settings.maintenance.Maintenance;
import com.inv.ui.ctrl.views.settings.settingsView.SettingsView;
import com.inv.ui.in.IWidget;
import com.inv.ui.util.WidgetUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author XDSSWAR
 */
public class Settings implements Initializable, IWidget {
    @FXML
    private TabPane tabPane;

    @FXML
    private Tab settingsTab;

    @FXML
    private AnchorPane settingsContainer;

    @FXML
    private Tab companyTab;

    @FXML
    private AnchorPane companyContainer;

    @FXML
    private Tab maintenanceTab;

    @FXML
    private AnchorPane maintenanceContainer;

    private final TextField search;

    private SettingsView settingsViewController=null;
    private CompanyView companyViewController=null;
    private Maintenance maintenanceController=null;


    /**
     * Constructor
     * @param search TextField from MainCtrl
     */
    public Settings(final TextField search){
        this.search=search;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue==settingsTab){;
                loadSettingsView();
            }
            if (newValue==companyTab){
                loadCompanyView();
            }
            if (newValue==maintenanceTab){
                loadMaintenanceView();
            }
        });

        tabPane.getSelectionModel().select(companyTab);
    }

    /**
     * Clear all Content from the Tabs
     */
    public void clearContainers(){
        settingsContainer.getChildren().clear();
        companyContainer.getChildren().clear();
        maintenanceContainer.getChildren().clear();
    }

    /**
     * Load Settings View
     */
    private void loadSettingsView(){
        WidgetUtil widgetUtil=new WidgetUtil();
        Runnable task=()->{
            settingsViewController=new SettingsView();
            Platform.runLater(()->{
                clearContainers();
                widgetUtil.addViewWithController(settingsContainer,"settings/settings/settingsView",settingsViewController);
            });
        };
        Thread thead = new Thread(task);
        thead.start();
    }

    /**
     * Load Company View
     */
    private void loadCompanyView(){
        WidgetUtil widgetUtil=new WidgetUtil();
        Runnable task=()->{
            companyViewController=new CompanyView(search);
            Platform.runLater(()->{
                clearContainers();
                widgetUtil.addViewWithController(companyContainer,"settings/company/company",companyViewController);
            });
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    /**
     * Load Maintenance View
     */
    private void loadMaintenanceView(){
        WidgetUtil widgetUtil=new WidgetUtil();
        Runnable task=()->{
            maintenanceController=new Maintenance();
            Platform.runLater(()->{
                clearContainers();
                widgetUtil.addViewWithController(maintenanceContainer,
                        "settings/maintenance/maintenance",maintenanceController);
            });
        };
        Thread thread=new Thread(task);
        thread.start();
    }
}
