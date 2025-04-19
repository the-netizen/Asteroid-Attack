package Application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;

public class GameOver_Controller {

    @FXML
    Pane gameOver_pane;
    @FXML
    Button menu_button, yes_button, no_button;

    Stage stage;
    Scene gameOverScene;
    Parent root;


    public void switchToWelcome(ActionEvent event) throws Exception{ //go to Welcome screen when menu_button is pressed
        FXMLLoader loader = new FXMLLoader(new File("C:/Users/pc/IdeaProjects/AsteroidAttack/src/main/resources/Application/welcome.fxml").toURI().toURL());
        root = loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();  //get current window
        gameOverScene = new Scene(root);
        stage.setScene(gameOverScene);
        stage.show();
    }

    public void switchToGame(ActionEvent event) throws  Exception{ //go back to Game screen when 'yes' is pressed
        FXMLLoader loader = new FXMLLoader(new File("C:/Users/pc/IdeaProjects/AsteroidAttack/src/main/resources/Application/game.fxml").toURI().toURL());
        root = loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow(); //get current window
        gameOverScene = new Scene(root);
        stage.setScene(gameOverScene);
        stage.show();
    }

    public void exit(ActionEvent event){ //exit game when 'no' is pressed
        System.exit(0);
    }
} //class ends
