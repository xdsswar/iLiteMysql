package com.inv.ui.ctrl.dialogs;

import com.inv.R;
import com.inv.data.access.Settings;
import com.inv.ui.util.Loader;
import com.xd.controls.XButton;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.media.AudioClip;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author XDSSWAR
 * Handle Dialogs
 * alertSound for Settings
 */
public class Message {
    public static final short MESSAGE_TYPE_SUCCESS=1;
    public static final short MESSAGE_TYPE_ERROR=0;
    public static final short MESSAGE_TYPE_WARNING=2;
    public static final short MESSAGE_TYPE_INFORMATION=3;
    private static final Image INFO_ICON=new Image(R.class.getResource("/assets/dialogs/info.png").toExternalForm());
    private static final Image ERROR_ICON=new Image(R.class.getResource("/assets/dialogs/error.png").toExternalForm());
    private static final Image SUCCESS_ICON=new Image(R.class.getResource("/assets/dialogs/success.png").toExternalForm());
    private static final Image WARNING_ICON=new Image(R.class.getResource("/assets/dialogs/warn.png").toExternalForm());
    //private static final AudioClip AUDIO_ERROR = new AudioClip(R.class.getResource("/assets/sounds/error_warn.wav").toString());
    //private static final AudioClip AUDIO_NOTIFY = new AudioClip(R.class.getResource("/assets/sounds/notify.wav").toString());


    /**
     * Show Message Dialog
     * @param title Message Title
     * @param message Message
     * @param messageType Message Type
     */
    public static boolean showMessage(Window owner, String title, String message, short messageType){
        AtomicBoolean toReturn= new AtomicBoolean(false);
        Parent parent=new Loader().load("dialogs/dialog.fxml");
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setResizable(false);
        stage.setTitle(title);
        Scene scene=new Scene(parent);

        //Here is where the magic start
        HBox hbox=(HBox)scene.lookup("#hbox");
        ImageView imageView=(ImageView) scene.lookup("#imageView");
        XButton okBtn=(XButton) scene.lookup("#okButton");
        XButton cancelBtn=(XButton) scene.lookup("#cancelButton");
        Label messageTxt=(Label) scene.lookup("#message");
        boolean playSound= Objects.equals(Settings.getSettingsValue("alertSound"), "1");

        switch (messageType){
            case MESSAGE_TYPE_SUCCESS:
                imageView.setImage(SUCCESS_ICON);
                stage.getIcons().add(SUCCESS_ICON);
                cancelBtn.setPrefWidth(0);
                hbox.getChildren().remove(cancelBtn);
                hbox.setAlignment(Pos.CENTER);
                if (playSound) {
                   // AUDIO_NOTIFY.play();
                }
                break;
            case MESSAGE_TYPE_ERROR:
                imageView.setImage(ERROR_ICON);
                stage.getIcons().add(ERROR_ICON);
                cancelBtn.setPrefWidth(0);
                hbox.getChildren().remove(cancelBtn);
                hbox.setAlignment(Pos.CENTER);
                if (playSound) {
                   // AUDIO_ERROR.play();
                }
                break;
            case MESSAGE_TYPE_WARNING:
                imageView.setImage(WARNING_ICON);
                stage.getIcons().add(WARNING_ICON);
                if (playSound) {
                    //AUDIO_ERROR.play();
                }
                break;
            case MESSAGE_TYPE_INFORMATION:
                imageView.setImage(INFO_ICON);
                stage.getIcons().add(INFO_ICON);
                if (playSound) {
                   // AUDIO_NOTIFY.play();
                }
                break;
        }
        okBtn.setOnAction(event ->{
            toReturn.set(true);
            stage.close();
        });
        cancelBtn.setOnAction(event -> {
            toReturn.set(false);
            stage.close();
        });
        messageTxt.setText(message);
        stage.setScene(scene);
        stage.showAndWait();
        return toReturn.get();
    }
}
