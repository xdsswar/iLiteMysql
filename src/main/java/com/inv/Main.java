package com.inv;

import com.inv.data.access.Settings;
import com.inv.ui.util.BgWorker;
import com.inv.ui.util.StageHelper;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author XDSSWAR
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        R.APP=this;
        R.STAGE=stage;
        Settings.initSettings();
        StageHelper helper=new StageHelper();
        helper.showStage(R.STAGE,"splash.fxml");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        BgWorker.getInstance().shutdownNow();
    }

}
