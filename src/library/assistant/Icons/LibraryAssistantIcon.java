package library.assistant.Icons;

import javafx.scene.image.Image;
import javafx.stage.Stage;


public class LibraryAssistantIcon {
    private static final String IMAGE_LOC = "/resources/icon.png";
    
    public static void setStageIcon(Stage stage){
        stage.getIcons().add(new Image(IMAGE_LOC));
    }
}
