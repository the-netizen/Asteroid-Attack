package Application;

import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.io.File;
import java.net.URL;
import java.util.*;

public class Game_Controller implements Initializable {
//    Asteroids asteroids;
    Welcome_Controller controller = new Welcome_Controller();
    @FXML
    Pane gamePane;
    @FXML
    ImageView spaceship, heart1, heart2, heart3;
    @FXML
    Label scoresLabel;
    @FXML
    MenuBar menuBar;


    AnimationTimer spaceshipAnimation; //spaceship animation
    Timeline addAsteroidTimeline;
//    double sPOSX, sPOSY, bPOSX, bPOSY, aPOSX, aPOSY; //spaceship / bullet / asteroid XY positions
    double WIDTH; //pane width
    double HEIGHT; //pane height
    double x, y, w, h; //for draw method()
    int collisions, numMissed; // number of collisions / number of times asteroid missed
    int bsize = 6; // initial bullet size
    int asteroidNumbers; //number of asteroids per time
    int scores = 0; //scores
    double asteroidSpeed =5; //speed of Asteroids falling
    double spaceshipSpeed =500; //max spaceship speed
    double spaceshipVelocity; //dynamic spaceship speed
    double bulletVelocity; //dynamic bullet speed
    boolean running; //is game running
    boolean collide, miss; //is spaceship colliding with asteroid / did spaceship miss the asteroid?
    Random RAND = new Random();
    ArrayList<ImageView> asteroidsCreated; //contains all asteroids spawned
    ArrayList<Circle> bulletList; //contains all bullets shot
    long lastUpdateTime; //last time when Animations handle() was called
    double elapsedSeconds, deltaX, oldX, newX, deltaY, oldY, newY; //for spaceship /bullet animation
    Circle bullet; //bullet shape
    ImageView asteroidsHolder;
    Bounds bulletBounds;
    Bounds asteroidBounds;
    Canvas canvas;
    GraphicsContext gc;
    BooleanProperty rightPressed = new SimpleBooleanProperty(false); //initially RIGHT key is not pressed
    BooleanProperty leftPressed = new SimpleBooleanProperty(false); //initially LEFT key is not pressed
    BooleanProperty spacePressed = new SimpleBooleanProperty(false); //initially SPACE is not pressed
    double refresh = 2; //refresh speed for timeLine
    double addAsteroidDuration = 5; //time to add the next asteroid
    HashMap<Circle, TranslateTransition> bulletTransition; //map of bullet transitions
    HashMap<ImageView, TranslateTransition> asteroidTransitions; //map of asteroid transitions


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("java version: "+System.getProperty("java.version"));
        System.out.println("javafx.version: " + System.getProperty("javafx.version"));

        running = true;
        scoresLabel.setText("SCORE : " + scores); //initial scores = 0
        WIDTH = gamePane.getPrefWidth(); //screen width
        HEIGHT = gamePane.getPrefHeight(); //screen height
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        gamePane.getChildren().add(gc.getCanvas());
        asteroidsCreated = new ArrayList<>(); //stores all asteroids created
        bulletTransition = new HashMap<>();
        asteroidTransitions = new HashMap<>();
        spaceship.setImage(new Image("C:/Users/pc/IdeaProjects/AsteroidAttack/src/main/java/Images/spaceship.png"));
        heart1.setImage(new Image("C:/Users/pc/IdeaProjects/AsteroidAttack/src/main/java/Images/heart.png"));
        heart2.setImage(new Image("C:/Users/pc/IdeaProjects/AsteroidAttack/src/main/java/Images/heart.png"));
        heart3.setImage(new Image("C:/Users/pc/IdeaProjects/AsteroidAttack/src/main/java/Images/heart.png"));
        spaceship.setFocusTraversable(true); //get focus on spaceship

        spaceshipAnimation = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    int minX = -495;
                    int maxX = 495;
                    if(lastUpdateTime > 0) {
                        elapsedSeconds = (now - lastUpdateTime)/ 1_000_000_000.0; //1 second = 1_000_000_000.0 nanoseconds
                        deltaX = elapsedSeconds * spaceshipVelocity; //spaceship movement overtime
                        oldX = spaceship.getTranslateX(); //old spaceship position
                        newX =  Math.max(minX, Math.min(maxX, oldX + deltaX));
                        spaceship.setTranslateX(newX); //new spaceship position
//                        sPOSX = spaceship.getBoundsInParent().getCenterX();
                    }
                    lastUpdateTime = now;
                } //handle ends
        };
        spaceshipAnimation.start(); //start animation Timer

        //add EventHandler
        spaceship.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case RIGHT:
                        rightPressed.set(true);
                        if(rightPressed.get()) {
                            spaceshipVelocity += 20; //increase velocity overtime
                            spaceship.setTranslateX(newX);
                            spaceshipVelocity = spaceshipSpeed; //velocity will remain constant now
                        }
                        else{
                        spaceship.setTranslateX(oldX); //set current position
                        }
                        break;
                case LEFT:
                        leftPressed.set(true);
                        if(leftPressed.get()){
                                spaceshipVelocity -= 20; //-ve velocity increases overtime
                                spaceship.setTranslateX(newX); //translate spaceship to new position
                                spaceshipVelocity = -spaceshipSpeed; //velocity becomes constant
                            }
                        else{
                                spaceship.setTranslateX(oldX); //set current position
                        }
                        break;
                case SPACE:
                        spacePressed.set(true);
                        shoot();
                        break;
                default: break;
            }//switch ends
        });//setOnKeyPressed ends


        spaceship.setOnKeyReleased(event -> {
            if(event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT) {
                spaceshipVelocity = 0;   //reset spaceship velocity when key is released.
            }
            if(event.getCode() == KeyCode.RIGHT){ //when RIGHT key releases, set rightPressed to false
                rightPressed.set(false);
            }
            else if (event.getCode() == KeyCode.LEFT) { //when LEFT key releases, set leftPressed to false
                leftPressed.set(false);
            }
            else if(event.getCode() == KeyCode.SPACE){//when SPACE key releases, set spacePressed to false
                spacePressed.set(false);
            }
        });


        addAsteroidTimeline = new Timeline(new KeyFrame(Duration.seconds(addAsteroidDuration), e -> {
            spawnAsteroids();
        }));
        addAsteroidTimeline.setCycleCount(Animation.INDEFINITE); //loop
        addAsteroidTimeline.play(); //start timeLine
        addAsteroidTimeline.setRate(addAsteroidDuration);

    }// initialize ends


    public void spawnAsteroids(){
        Image[] array = {
                new Image("C:/Users/pc/IdeaProjects/AsteroidAttack/src/main/java/Images/a1.png"),
                new Image("C:/Users/pc/IdeaProjects/AsteroidAttack/src/main/java/Images/a2.png"),
                new Image("C:/Users/pc/IdeaProjects/AsteroidAttack/src/main/java/Images/a3.png"),
                new Image("C:/Users/pc/IdeaProjects/AsteroidAttack/src/main/java/Images/a4.png"),
                new Image("C:/Users/pc/IdeaProjects/AsteroidAttack/src/main/java/Images/a5.png"),
                new Image("C:/Users/pc/IdeaProjects/AsteroidAttack/src/main/java/Images/a6.png"),
                new Image("C:/Users/pc/IdeaProjects/AsteroidAttack/src/main/java/Images/a7.png"),
                new Image("C:/Users/pc/IdeaProjects/AsteroidAttack/src/main/java/Images/a8.png"),
                new Image("C:/Users/pc/IdeaProjects/AsteroidAttack/src/main/java/Images/a9.png"),
                new Image("C:/Users/pc/IdeaProjects/AsteroidAttack/src/main/java/Images/a10.png")
        };
        ArrayList<Image> asteroid_list = new ArrayList<>(Arrays.asList(array));
        ImageView holder = new ImageView();

        int minX =0; //left most position
        int maxX =935; //right most position
        int randomXPosition = RAND.nextInt((maxX - (minX)) + 1) + (minX); //random X position for asteroid within bounds
//        aPOSX = randomXPosition; //asteroid position X

        holder.setImage(asteroid_list.get(RAND.nextInt(asteroid_list.size()))); //spawn random asteroids
        holder.setLayoutX(randomXPosition);// spawn asteroids at random X position
        holder.setLayoutY(-70); // asteroids at top of gamePane
        asteroidsCreated.add(holder); //add every imageView into arrayList
        gamePane.getChildren().add(holder); //add ImageView to gamePane
        asteroidNumbers++;
        moveAsteroid();
//        checkCollision(spaceship);
        detectCollision(spaceship);
    }//spawnAsteroids ends


    public void moveAsteroid(){
//        iterate through all asteroid ImageViews created and move them individually
        for(ImageView x : asteroidsCreated) {
            TranslateTransition translate = new TranslateTransition(); //asteroid movement
            translate.setNode(x);
            translate.setDuration(Duration.seconds(addAsteroidDuration));
            translate.setToY(1000); //image will come downwards
            translate.setInterpolator(Interpolator.LINEAR);
            translate.play();
            asteroidTransitions.put(x, translate); //put the moving asteroid into the Hashmap
//            aPOSY = translate.getToY(); //asteroid position Y
        }
    } //moveAsteroid ends



    public void checkCollision(ImageView spaceship){
        collide = false;
        for(ImageView x : asteroidsCreated) {
            if(spaceship.getBoundsInParent().intersects(x.getBoundsInParent())) { //if there is a collision :
                collide = true;
                collisions++;
                if (collisions == 1) {
                    heart3.setImage(null); //remove 3rd heart
                } else if (collisions == 2) {
                    heart2.setImage(null); //remove 2nd heart
                } else if (collisions == 3) {
                    heart1.setImage(null); //remove 1st heart
                    spaceshipAnimation.stop(); //stop moving spaceship
                    addAsteroidTimeline.stop(); //stop adding more asteroids
                    running = false; //stop running program
                    try {
                        switchToGameOver(); //switch to gameOver screen
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            else if (!(spaceship.getBoundsInParent().intersects(x.getBoundsInParent()))) { //if NO collision :
                collide = false;
            }
        } //for loop ends
    } //checkCollision ends


    public void detectCollision(ImageView spaceship){
        PixelReader spaceshipPIXELS = spaceship.getImage().getPixelReader(); //get pixels for spaceship
        int sw = (int) spaceship.getImage().getWidth(); //spaceship width
        int sh = (int) spaceship.getImage().getHeight(); //spaceship height
        double sPOSX = spaceship.getBoundsInParent().getMinX(); //spaceship x coordinates
        double sPOSY = spaceship.getBoundsInParent().getMinY(); //spaceship y coordinates

        for(ImageView asteroid : asteroidsCreated) {
            PixelReader asteroidPIXELS = asteroid.getImage().getPixelReader(); //get pixels for all asteroids
            int aw = (int) asteroid.getImage().getWidth(); //asteroid width
            int ah = (int) asteroid.getImage().getHeight(); //asteroid height
            double aPOSX = asteroid.getBoundsInParent().getMinX(); //asteroid x coordinates
            double aPOSY = asteroid.getBoundsInParent().getMinY(); //asteroid y coordinates

            // Loop through spaceship image
            for (int i = 0; i < sw; i++) {
                for (int j = 0; j < sh; j++) {
                    //color in first image
                    Color c1 = spaceshipPIXELS.getColor(i, j);
                    int argb1 = spaceshipPIXELS.getArgb(i, j);

                    //pixel coordinates in second image
                    int i2 = (int) (i + sPOSX - aPOSX);
                    int j2 = (int) (j + sPOSY - aPOSY);


                    //if within bounds of second image
                    if (i2 >= 0 && i2 < aw && j2 >= 0 && j2 < ah) { //get color of 2nd img
                        Color c2 = asteroidPIXELS.getColor(i2, j2);
                         int argb2 = asteroidPIXELS.getArgb(i2, j2);

                        //compare colors of both pixels
                        if (c1.equals(c2)) {
//                        if(argb1 == argb2){
                            // collision
                            collide = true;
                            collisions++;
                            System.out.println("COLLIISION");
                        }
                    }
                }
            }

            if (collisions == 1) {
                heart3.setImage(null); //remove 3rd heart
            } else if (collisions == 2) {
                heart2.setImage(null); //remove 2nd heart
            } else if (collisions == 3) {
                heart1.setImage(null); //remove 1st heart
                spaceshipAnimation.stop(); //stop moving spaceship
                addAsteroidTimeline.stop(); //stop adding more asteroids
                running = false; //stop running program
                try {
                    switchToGameOver(); //switch to gameOver screen
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else{
                collide = false;
            }

        } //for asteroid ends
    } //detect collision ends



    public void shoot(){ //create bullets
        bullet = new Circle(); //make circle for bullets
        bullet.setLayoutX(spaceship.getBoundsInParent().getCenterX()); //bullets come from center of moving spaceship
        bullet.setLayoutY(spaceship.getBoundsInParent().getCenterY());
        bullet.setRadius(bsize); //size of bullet
        bullet.setStroke(Color.WHITE); //white border
        bullet.setFill(Color.RED); //red color
        bullet.setVisible(true);
        gamePane.getChildren().add(bullet); //add bullet to pane
        bulletList = new ArrayList<>();
        bulletList.add(bullet); //add bullet to a bulletList
        bulletMovement();
    } //shoot ends



    public void bulletMovement(){
        for(Circle b : bulletList) {
            TranslateTransition translate = new TranslateTransition(); // bullet movement
            translate.setNode(b);
            translate.setDuration(Duration.seconds(2));
            translate.setToY(-1000); //bullets move upwards
            translate.setInterpolator(Interpolator.LINEAR); //smooth
            translate.play(); //play translation
            bulletTransition.put(b, translate); //add moving bullets to HashMap
            checkScores(b); //scores
        }
    } //bullet_Movement ends



    public void checkScores(Circle bullet){
        //Create a binding that observes the bounds of the bullet and the asteroids
        ObservableBooleanValue colliding = Bindings.createBooleanBinding(() -> {
            for (ImageView asteroid : asteroidsCreated) {
                if (bullet.getBoundsInParent().intersects(asteroid.getBoundsInParent())) {
                    return true; //Collision detected
                }
            }
            return false; //No collision detected
        }, bullet.boundsInParentProperty());

        //Add a listener to the binding that triggers an action when the collision occurs
        colliding.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    //Collision detected
                    for (ImageView asteroid : asteroidsCreated) {
                        if (bullet.getBoundsInParent().intersects(asteroid.getBoundsInParent())) {
                            //Remove the bullet and asteroid from the scene
//                            gamePane.getChildren().removeAll(bullet, asteroid);
                            gamePane.getChildren().remove(asteroid);
//                            bullet.setVisible(false);

                            //Stop the transitions of the bullet and asteroid
//                            bulletTransition.get(bullet).stop();
                            asteroidTransitions.get(asteroid).stop();

                            //Increase the score
                            scores++;
                            scoresLabel.setText("SCORES : " + scores);
                            //Break out of the loop
                            break;
                        }
                    }
                }
            }
        });

    } //check_Scores ends



    public void switchToGameOver() throws Exception{
        FXMLLoader load = new FXMLLoader(new File("C:/Users/pc/IdeaProjects/AsteroidAttack/src/main/resources/Application/gameOver.fxml").toURI().toURL());
        Parent root = load.load();
        gamePane.getChildren().setAll(root);
    } //switch_to_game_Over ends



} //class end
