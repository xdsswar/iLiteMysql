package com.inv.ui.ctrl.activation;

import com.inv.ui.util.Internet;
import com.xd.controls.XButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author XDSSWAR
 */
public class Activation implements Initializable {

    @FXML
    private TextField userTxt;

    @FXML
    private TextField codeTxt;

    @FXML
    private XButton activateTxt;

    @FXML
    private Label errorLabel;

    @FXML
    private ProgressBar progressBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();
    }

    /**
     * Init events and actions
     */
    private void init(){
        if (!Internet.check()){
            errorLabel.setText("No Internet Connection Available!");
            errorLabel.setVisible(true);
            activateTxt.setDisable(true);
        }

        userTxt.textProperty().addListener(e->{
            errorLabel.setVisible(false);
        });
        codeTxt.textProperty().addListener(e->{
            errorLabel.setVisible(false);
        });
    }
}
