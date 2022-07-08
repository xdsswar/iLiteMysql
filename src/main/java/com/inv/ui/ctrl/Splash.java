package com.inv.ui.ctrl;

import com.inv.R;
import com.inv.data.access.MysqlConnection;
import com.inv.data.access.maintenance.Check;
import com.inv.ui.ctrl.dialogs.Message;
import com.inv.ui.util.DragUtil;
import com.inv.ui.util.StageHelper;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author XDSSWAR
 */
public final class Splash extends DragUtil implements Initializable {
    @FXML
    private Label softName;

    @FXML
    private AnchorPane header;

    @FXML
    private Label softDescription;

    @FXML
    private Label copyText, progressLabel;

    @FXML
    private ProgressBar progressBar;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setDraggable(header,false);
        softName.setText(R.SOFTWARE_NAME);
        softDescription.setText(R.SOFTWARE_DESCRIPTION);
        copyText.setText(R.SOFTWARE_COPYRIGHT);
        initSplash();
    }

    /**
     * Init Splash
     */
    private void initSplash(){
        progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
            if((double)newValue==1){
                ((Stage)header.getScene().getWindow()).close();

                //Show Dialog if the Connection is wrong
                if (!R.IS_DB_OK){
                    Message.showMessage(null,
                            "Database Connection Error !",
                            "Error establishing a database connection!\nPlease review your Database Settings" +
                                    " and try again.",
                            Message.MESSAGE_TYPE_ERROR
                    );
                }

                StageHelper helper=new StageHelper();
                helper.showStage("login.fxml","Sign In");
            }
        });
        final Task<Void> worker = createWorker();
        Platform.runLater(()-> {
                progressBar.setProgress(0);
                progressLabel.textProperty().unbind();
                progressBar.progressProperty().unbind();
                progressBar.progressProperty().bind(worker.progressProperty());
                progressLabel.textProperty().bind(worker.messageProperty());
        });
        new Thread(worker).start();
    }

    /**
     * Create task for the Progress
     * @return Task
     */
    private Task<Void> createWorker() {
        return new Task<>() {
            @Override
            public Void call() {

                for (int i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException ignored) {
                    }
                    if (i < 39) {
                        updateMessage(i + "%" + " Loading Settings...");
                    }
                    if (i == 40) {
                        updateMessage(i + "%" + " Checking Database Connection...");
                        R.IS_DB_OK = MysqlConnection.checkConnection();
                    }
                    if (i > 41 && i < 70) {
                        updateMessage(i + "%" + " Loading Modules ...");
                        try {
                            Thread.sleep(70);
                        } catch (InterruptedException ignored) {
                        }
                    }
                    if (i > 70 && i < 85) {
                        updateMessage(i + "%" + " Checking License Status...");
                        try {
                            Thread.sleep(80);
                        } catch (InterruptedException ignored) {
                        }
                    }
                    if (i > 86) {
                        updateMessage(i + "%" + " Starting UI...");
                    }
                    if (i > 97) {
                        try {
                            Thread.sleep(700);
                        } catch (InterruptedException ignored) {
                        }
                        updateMessage(100 + "%" + " Starting UI...");
                    }
                    updateProgress(i + 1, 100);

                }
                return null;
            }
        };
    }

}
