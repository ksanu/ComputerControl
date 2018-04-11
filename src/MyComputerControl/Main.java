package MyComputerControl;

import javafx.application.Application;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Computer control");

        MyGUI myGUI = new MyGUI(primaryStage);
        myGUI.loginStage();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
