package MyComputerControl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MyGUI{

    public Stage primaryStage;
    public boolean wasStarted;
    public MyGUI(Stage primaryStage)
    {
       this.primaryStage = primaryStage;
       this.wasStarted = false;
    }

    public void loginStage()
    {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Witaj");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        //Label userName = new Label("User Name:");
        //grid.add(userName, 0, 1);

        //TextField userTextField = new TextField();
        // grid.add(userTextField, 1, 1);

        Label pw = new Label("Hasło:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btn = new Button("Zaloguj");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("Logowanie...");
                //TODO: logowanie - Backend
                mainStage();

            }
        });

        Scene scene = new Scene(grid, 400, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ObservableList<String> getActionsList()
    {
        List<String> results = new ArrayList<String>();

        File[] files = new File("./Akcje").listFiles();

        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
            }
        }
        ObservableList<String> options = FXCollections.observableArrayList(results);
        return options;
    }

    public void mainStage()
    {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));


        //TODO: przycisk - dodanie nowej akcji

        Button newActionCaptureBtn = new Button("Dodaj automatyczną akcję");
        HBox hbNewActionCaptureBtn = new HBox(10);
        hbNewActionCaptureBtn.setAlignment(Pos.BOTTOM_LEFT);
        hbNewActionCaptureBtn.getChildren().add(newActionCaptureBtn);

        grid.add(hbNewActionCaptureBtn, 0, 1, 2, 1);

        newActionCaptureBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                newActionCaptureStage();

            }
        });
        //TODO:lista akcji, rozwijana, do wybrania
        Label cbLabel = new Label("Lista akcji:");
        grid.add(cbLabel, 0, 0);
        final ComboBox actionsList = new ComboBox(getActionsList());
        grid.add(actionsList,1, 0);

        //TODO: Przycisk - uruchomienia akcji
        Button runActionBtn = new Button("Uruchom akcję");
        HBox hbRunActionBtn = new HBox(10);
        hbRunActionBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbRunActionBtn.getChildren().add(runActionBtn);
        grid.add(hbRunActionBtn, 2, 0);

        runActionBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                    //TODO:run action
            }
        });
        //TODO: Stan połączenia z pilotem

        //Label userName = new Label("User Name:");
        //grid.add(userName, 0, 1);

        //TextField userTextField = new TextField();
        //grid.add(userTextField, 1, 1);

        Scene scene = new Scene(grid, 400, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    public void newActionCaptureStage()
    {
        final Thread[] captureThread = {null};
        wasStarted = false;
        //JIntellitype.getInstance().registerHotKey(1, 0 ,122);//123 - F11
        //JIntellitype.getInstance().registerHotKey(2, 0, 27);//27 - Esc
       // JIntellitype.getInstance().addHotKeyListener((HotkeyListener) this);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 25, 10, 25));

        Text scenetitle = new Text("Tworzenie automatycznej akcji");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        grid.add(scenetitle, 0, 0, 2, 1);

        /*
        // Register an event handler for a single node and a specific event type
        grid.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ESCAPE)
                {
                    System.out.println("esc hotkey pressed");

                }
                System.out.println("hotkey pressed: " + event.getCode());

            }
        });
*/


        Label actionName = new Label("Nazwa automatycznej akcji:");
        grid.add(actionName, 0, 1);

        TextField actionNameField = new TextField("nazwa akcji");
        grid.add(actionNameField, 1, 1);

        final Text infoSaveText = new Text();
        grid.add(infoSaveText, 0, 4);

        Button returnBtn = new Button("Powrót");
        returnBtn.setDisable(true);
        HBox hbReturnBtn = new HBox(10);
        hbReturnBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbReturnBtn.getChildren().add(returnBtn);
        grid.add(hbReturnBtn, 1, 4);

        returnBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                //unhook:
                if(wasStarted)
                {
                    try {
                        GlobalScreen.unregisterNativeHook();
                    } catch (NativeHookException e1) {
                        e1.printStackTrace();
                    }
                }

                mainStage();
            }
        });

        Button startBtn = new Button("Start");
        HBox hbStartBtn = new HBox(10);
        hbStartBtn.setAlignment(Pos.BOTTOM_LEFT);
        hbStartBtn.getChildren().add(startBtn);
        grid.add(hbStartBtn, 0, 3);

        startBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                //todo: rozpocznij łapanie ruchu myszy
                infoSaveText.setText("Wciśnij klawisz Esc, aby zapisać.\nNastępnie powróć.");
                infoSaveText.setFill(Color.DARKGREEN);

                actionNameField.setEditable(false);
                returnBtn.setDisable(false);
                wasStarted = true;

                String path = actionNameField.getText();
                captureThread[0] = new Thread(new ActionCapturer(path));
                captureThread[0].start();
                startBtn.setDisable(true);
                
                //primaryStage.setIconified(true);

            }
        });


        /*
        Button saveBtn = new Button("Zapisz");
        HBox hbSaveBtn = new HBox(10);
        hbSaveBtn.setAlignment(Pos.BOTTOM_LEFT);
        hbSaveBtn.getChildren().add(saveBtn);
        grid.add(hbSaveBtn, 0, 3);

        saveBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                //todo: zapisz złapany ruch myszy i przejdź do sceny głównej

            }

        });
        */

        Button cancelBtn = new Button("Anuluj");
        HBox hbCancelBtn = new HBox(10);
        hbCancelBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbCancelBtn.getChildren().add(cancelBtn);
        grid.add(hbCancelBtn, 1, 3);

        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                //todo: cancel thread and delete file, unhook listener
                if(wasStarted)
                {
                    try {
                        GlobalScreen.unregisterNativeHook();
                    } catch (NativeHookException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        Files.delete(Paths.get("./Akcje/" + actionNameField.getText()));
                        captureThread[0].stop();
                        captureThread[0] = null;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    };
                }



                mainStage();

            }
        });

        //JIntellitype.getInstance().unregisterHotKey(1);
        //JIntellitype.getInstance().unregisterHotKey(2);
       // JIntellitype.getInstance().cleanUp();






        Scene scene = new Scene(grid, 400, 275);
        primaryStage.setScene(scene);
        primaryStage.show();


    }
    /*
    @Override
    public void onHotKey(int aIdentifier)
    {
        if (aIdentifier == 1) {
            System.out.println("f11 hotkey pressed");
            this.keyPressed = 122;

        }
        if (aIdentifier == 2){
            System.out.println("esc hotkey pressed");
            this.keyPressed = 27;

        }
    }
    */
}
