package com.inv.ui.ctrl.views.help;

import com.inv.R;
import com.inv.ui.in.IWidget;
import com.inv.ui.util.Anima;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author XDSSWAR
 */
public class Help implements Initializable, IWidget {

    @FXML
    private WebView webView;

    private WebEngine engine;
    private Anima anima;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webView.setOpacity(0);
        anima=new Anima();
        engine=webView.getEngine();
        engine.setJavaScriptEnabled(true);
        webView.setContextMenuEnabled(false);
        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(Worker.State.SUCCEEDED)){
                anima.fadeInTransition(webView);
            }
        });
        init();
    }

    /**
     * Init engine
     */
    private void init(){
        Runnable task=()->{
            Platform.runLater(()->{
                engine.load(R.class.getResource("/html/index.html").toExternalForm());
            });
        };
        Thread thread=new Thread(task);
        thread.start();
    }
}
