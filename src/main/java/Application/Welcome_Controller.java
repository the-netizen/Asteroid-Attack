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

public class Welcome_Controller {

    @FXML
    Pane welcomePane;
    @FXML
    Button newGameButton, continueGameButton, leaderBoardButton, gameModeButton, chooseSpaceshipButton, exitButton;
    Stage stage;
    Scene gameScene;
    private Parent root;

    public void switchToGame(ActionEvent event) throws Exception{ // when New game or continue game Button is pressed ->
//        root = FXMLLoader.load(getClass().getResource("game.fxml")); //load game screen
        FXMLLoader loader = new FXMLLoader(new File("C:/Users/pc/IdeaProjects/AsteroidAttack/src/main/resources/Application/game.fxml").toURI().toURL());
        root = loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();  //get current window
        gameScene = new Scene(root);
        stage.setScene(gameScene);
        stage.show();
//        stage.setFullScreen(true); //sets stage to full screen
//        stage.setMaximized(true); //sets stage to full screen
//        controller.createAsteroid(){}
    }
    public void displayLeaderBoard(){}
    public void exit(){
        System.exit(0);
    }
}
