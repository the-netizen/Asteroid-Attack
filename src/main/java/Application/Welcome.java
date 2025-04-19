package Application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Welcome extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(new File("C:/Users/pc/IdeaProjects/AsteroidAttack/src/main/resources/Application/welcome.fxml").toURI().toURL());
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Asteroid Attack");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}