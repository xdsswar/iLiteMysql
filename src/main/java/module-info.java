module ILite {
    requires javafx.base;
    requires commons.dbutils;
    requires java.sql;
    requires mysql.connector.java;
    requires java.desktop;
    requires javafx.fxml;
    requires javafx.controls;
    requires xctrl;
    requires javafx.media;
    requires iconsfx;
    requires XPieChart;
    requires itextpdf;
    requires annotations;
    requires xtext;
    requires javafx.web;
    exports com.inv to javafx.graphics;
    exports com.inv.ui.ctrl to javafx.fxml;
    opens com.inv.ui.ctrl to javafx.fxml;
    exports com.inv.ui.ctrl.views.home to javafx.fxml;
    exports com.inv.ui.ctrl.views.staff to javafx.fxml;
    opens com.inv.ui.ctrl.views.orders to javafx.fxml;
    opens com.inv.ui.ctrl.views.clients to javafx.fxml;
    opens com.inv.ui.ctrl.views.categories to javafx.fxml;
    opens com.inv.ui.ctrl.views.brands to javafx.fxml;
    opens com.inv.ui.ctrl.views.units to javafx.fxml;
    opens com.inv.ui.ctrl.views.currency to javafx.fxml;
    opens com.inv.ui.ctrl.views.tax to javafx.fxml;
    opens com.inv.ui.ctrl.views.providers to javafx.fxml;
    opens com.inv.ui.ctrl.views.products to javafx.fxml;
    opens com.inv.ui.ctrl.views.home to javafx.fxml;
    exports com.inv.ui.widgets to javafx.fxml;
    opens com.inv.ui.widgets to javafx.fxml;
    opens com.inv.ui.ctrl.views.settings to javafx.fxml;
    opens com.inv.ui.ctrl.views.settings.company to javafx.fxml;
    opens com.inv.ui.ctrl.views.settings.maintenance to javafx.fxml;
    exports com.inv.ui.ctrl.about to javafx.fxml;
    opens com.inv.ui.ctrl.about to javafx.fxml;
    exports com.inv.ui.ctrl.dialogs to javafx.fxml;
    exports com.inv.xflux.entity;
    exports com.inv.xflux.model;
    opens com.inv.ui.util;
    opens com.inv.pdf;
    exports com.inv.data.access.emums;
    opens com.inv.ui.ctrl.views.help to javafx.fxml;
    opens com.inv.ui.ctrl.views.staff to javafx.fxml;




}