package com.inv.ui.ctrl.about;

import com.inv.R;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author XDSSWAR
 */
public class About implements Initializable {
    @FXML
    private Label titleLabel, copyrightLabel,javaVersion;

    @FXML
    private Label softNameLabel;

    @FXML
    private Label softNameDescriptionLabel;

    @FXML
    private Label licenceLabel;

    @FXML
    private Label licenseDescriptionLabel;

    @FXML
    private Label evantoUserLabel;

    @FXML
    private Label evantoUserLabelDescription;

    @FXML
    private Label purchaseCodeLabel;

    private String user,code;
    private boolean activated;

    @FXML
    private Label purchaseCodeLabelDescription;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initKeys();
        titleLabel.setText(R.SOFTWARE_NAME+" 2020");
        softNameLabel.setText(R.SOFTWARE_NAME+" "+R.VERSION);
        softNameDescriptionLabel.setText("Build #"+R.SOFTWARE_BUILD_NUMBER+", built on "+R.SOFTWARE_BUILD_DATE);
    }

    /**
     * set Key Registry needed info
     */
    private void initKeys(){
        licenceLabel.setText("Activation status");
        if (activated) {
            licenseDescriptionLabel.setText("License Status : Activated");
        }else {
            licenseDescriptionLabel.setText("License Status : Unactivated");
        }
        evantoUserLabel.setText("Licence");
        evantoUserLabelDescription.setText("Licensed to "+user);

        purchaseCodeLabel.setText("Product key");
        purchaseCodeLabelDescription.setText(code);

        javaVersion.setText("Java Version: "+System.getProperty("java.version"));
        copyrightLabel.setText(R.SOFTWARE_COPYRIGHT);
    }
}
