package library.assistant.ui.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import library.assistant.database.DatabaseHandler;
import library.assistant.Icons.LibraryAssistantIcon;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/library/assistant/ui/main/main.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Library");
        
        LibraryAssistantIcon.setStageIcon(stage);
        
        new Thread(()-> DatabaseHandler.getInstance()).start();
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}
