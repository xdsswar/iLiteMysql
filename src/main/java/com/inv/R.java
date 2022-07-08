package com.inv;

import com.inv.data.access.Settings;
import com.inv.xflux.entity.Employee;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * @author XDSSWAR
 * This Class keeps all the global variables
 * Do not modify anything here if you don't know , this can affect the entire application.
 */
public  final   class R {
    public static Stage STAGE;
    public static Application APP;
    public static final String CP="\u00a9";
    public static final String VERSION="1.0";
    public static final String SOFTWARE_NAME="ILite";
    public static final String SOFTWARE_DESCRIPTION="A new way to do things better!";
    public static final String SOFTWARE_BUILD_NUMBER="IL-0001";
    public static final String SOFTWARE_BUILD_DATE="August 2020";
    public static final String SOFTWARE_COPYRIGHT=CP+" Copyright AlterSoft. All rights reserved.";
    public static final Image  APP_ICON=new Image(R.class.getResource("/assets/icon.png").toExternalForm());
    public static final Image  ABOUT_ICON=new Image(R.class.getResource("/assets/about.png").toExternalForm());
    public static final Image  ACTIVATE_ICON=new Image(R.class.getResource("/assets/activate.png").toExternalForm());
    public static boolean IS_DB_OK=true;
    public static Employee ONLINE_EMPLOYEE=null;
    public static boolean IS_DASHBOARD_MAXIMIZED=Objects.equals(Settings.getSettingsValue("max"), "1");
    public static boolean IS_DASHBOARD_MENU_EXPANDED=Objects.equals(Settings.getSettingsValue("expanded"), "1");
    public static Stage MODAL_STAGE;


    static {
        try {
            /*
             * font-family: 'Heebo Regular';
             */
            Font.loadFont(R.class.getResource("/fonts/Heebo/Heebo-Regular.ttf").openStream(),10.0D);
            /*
             * font-family: 'Pacifico Regular';
             */
            Font.loadFont(R.class.getResource("/fonts/Pacifico/Pacifico-Regular.ttf").openStream(), 10.0D);
            /*
             * font-family: 'Rubik Regular';
             */
            Font.loadFont(R.class.getResource("/fonts/Rubik/Rubik-Regular.ttf").openStream(),10.0D);
            /*
             * font-family: 'Aclonica Regular';
             */
            Font.loadFont(R.class.getResource("/fonts/Aclonica/Aclonica-Regular.ttf").openStream(),10.0D);
            /*
             * font-family: 'Lovelo Line Light';
             */
            Font.loadFont(R.class.getResource("/fonts/Lovelo/Lovelo-LineLight.ttf").openStream(),10.0D);
            /*
             * font-family: 'Lovelo Line Bold';
             */
            Font.loadFont(R.class.getResource("/fonts/Lovelo/Lovelo-LineBold.ttf").openStream(),10.0D);
            /*
             * font-family: 'Lovelo Black';
             */
            Font.loadFont(R.class.getResource("/fonts/Lovelo/Lovelo-Black.ttf").openStream(),10.0D);

            /*
             * font-family: 'Lato Black';
             */
            Font.loadFont(R.class.getResource("/fonts/Lato/Lato-Black.ttf").openStream(),10.0D);
            /*
             * font-family: 'Lato Bold';
             */
            Font.loadFont(R.class.getResource("/fonts/Lato/Lato-Bold.ttf").openStream(),10.0D);
            /*
             * font-family: 'Lato Regular';
             */
            Font.loadFont(R.class.getResource("/fonts/Lato/Lato-Regular.ttf").openStream(),10.0D);

            /*
             * font-family: 'Roboto Black';
             */
            Font.loadFont(R.class.getResource("/fonts/Roboto/Roboto-Black.ttf").openStream(),10.0D);
            /*
             * font-family: 'Roboto Bold';
             */
            Font.loadFont(R.class.getResource("/fonts/Roboto/Roboto-Bold.ttf").openStream(),10.0D);
            /*
             * font-family: 'Roboto Light';
             */
            Font.loadFont(R.class.getResource("/fonts/Roboto/Roboto-Light.ttf").openStream(),10.0D);
            /*
             * font-family: 'Roboto Medium';
             */
            Font.loadFont(R.class.getResource("/fonts/Roboto/Roboto-Medium.ttf").openStream(),10.0D);
            /*
             * font-family: 'Roboto Regular';
             */
            Font.loadFont(R.class.getResource("/fonts/Roboto/Roboto-Regular.ttf").openStream(),10.0D);
            /*
             * font-family: 'Roboto Thin';
             */
            Font.loadFont(R.class.getResource("/fonts/Roboto/Roboto-Thin.ttf").openStream(),10.0D);

            //Open Sans
            /*
             * font-family: 'Open Sans SemiBold';
             */
            Font.loadFont(R.class.getResource("/fonts/OpenSans/OpenSans-SemiBold.ttf").openStream(),10.0D);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
