package com.inv.data.access.orderutil;

import com.inv.ui.util.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Base64;

/**
 * @author XDSSWAR
 */
@SuppressWarnings({"deprecation","all"})
public class Image64 {

    /**
     * Encode Image to Base64
     * @param owner Main Stage for the FileChoser
     * @return String Image encoded
     */
    public static String encodeImage(Stage owner) {
        String base64Image = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        FileChooser.ExtensionFilter filter=new FileChooser.ExtensionFilter("Image Files","*.jpg", "*.png");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(owner);
        if (file!=null) {
            try {
                FileInputStream imageInFile = new FileInputStream(file);
                byte[] imageData = new byte[(int) file.length()];
                imageInFile.read(imageData);
                base64Image = Base64.getEncoder().encodeToString(imageData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            base64Image=defImageData;
        }
        return base64Image;
    }

    /**
     * Encode Image directly from resources
     * @param image Image
     * @return String
     */
    public static String encodeImage(Image image) {
        String base64Image = null;
        File file = null;
        try {
            if (image.getUrl()==null){
                //In case the image url is null we need to create temp file
                //with the imageview's content , then pass it as parameter
                //to a new file.
                file = File.createTempFile("temp", null);
                System.out.println(file.getPath());
                BufferedImage img= SwingFXUtils.fromFXImage(image,null);
                ImageIO.write(img,"png",file);
                file=new File(file.getPath());
            }else {
                file = new File(new URI(image.getUrl()));
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        if (file!=null) {
            try {
                FileInputStream imageInFile = new FileInputStream(file);
                byte[] imageData = new byte[(int) file.length()];
                imageInFile.read(imageData);
                base64Image = Base64.getEncoder().encodeToString(imageData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            base64Image=defImageData;
        }

        return base64Image;
    }


    /**
     * Decode Base64 Image
     * @param base64Image String Image encoded
     * @return Image
     */
    public static Image decodeImage(String base64Image) {
        byte[] imageByteArray= Base64.getDecoder().decode(base64Image);
        ByteArrayInputStream io = new ByteArrayInputStream(imageByteArray);
        return new Image(io);
    }


    /**
     * Default Image
     */
    private static final String defImageData="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";


}
