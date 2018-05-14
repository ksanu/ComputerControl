package MyComputerControl;

import MyComputerControl.Security.PasswordHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyGUI{

    public static final String confFilePath = "./conf";
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

        btn.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                actiontarget.setFill(Color.GREEN);
                actiontarget.setText("Logowanie...");
            }
        });
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String currentPw = pwBox.getText();
                String filePath = confFilePath;
                Charset utf8 = StandardCharsets.UTF_8;
                try {
                    //Sprawdzenie, czy hasło się zgadza:
                    List<String> allLines = Files.readAllLines(Paths.get(filePath), utf8);
                    String storedPw = allLines.get(0);
                    if(PasswordHandler.check(currentPw, storedPw))
                    {
                        mainStage();
                    }else{
                        actiontarget.setFill(Color.RED);
                        actiontarget.setText("Nieprawidłowe hasło.");
                    }
                }catch (Exception exc)
                {
                    exc.printStackTrace();
                    actiontarget.setFill(Color.RED);
                    actiontarget.setText(exc.getMessage());
                }

            }
        });

        Scene scene = new Scene(grid, 450, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void changePasswordStage()
    {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Zmień hasło");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        grid.add(scenetitle, 0, 0, 2, 1);

        //Label userName = new Label("User Name:");
        //grid.add(userName, 0, 1);

        //TextField userTextField = new TextField();
        // grid.add(userTextField, 1, 1);

        Label oldPw = new Label("Poprzednie hasło:");
        grid.add(oldPw, 0, 2);

        PasswordField oldPwBox = new PasswordField();
        grid.add(oldPwBox, 1, 2);

        Label newPw = new Label("Nowe hasło:");
        grid.add(newPw, 0, 3);

        PasswordField newPwBox = new PasswordField();
        grid.add(newPwBox, 1, 3);

        Button btn = new Button("Zmień hasło");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 5);

        Button cancelBtn = new Button("Powrót");
        HBox hbCancelBtn = new HBox(10);
        hbCancelBtn.setAlignment(Pos.BOTTOM_LEFT);
        hbCancelBtn.getChildren().add(cancelBtn);
        grid.add(hbCancelBtn, 0, 5);
        cancelBtn.setOnAction(event -> {
            mainStage();
        });
        
        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        btn.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                actiontarget.setFill(Color.GREEN);
                actiontarget.setText("Zmiana hasła...");
            }
        });
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                //zmiana hasła
                String oldPw = oldPwBox.getText();
                String newPw = newPwBox.getText();
                String filePath = confFilePath;
                Charset utf8 = StandardCharsets.UTF_8;
                try {
                    List<String> allLines = Files.readAllLines(Paths.get(filePath), utf8);
                    String storedPw = allLines.get(0);
                    //Sprawdzenie, czy stare hasło się zgadza:
                    if(PasswordHandler.check(oldPw, storedPw)){
                        String hashedNewPassword = PasswordHandler.getSaltedHash(newPw);
                        //usunięcie białych znaków
                        hashedNewPassword = hashedNewPassword.replaceAll("\\s+","");
                        List<String> lines = Arrays.asList(hashedNewPassword);
                        //zapisanie zhaszowanego hasła
                        Files.write(Paths.get(filePath), lines, utf8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                        mainStage();
                    }else {
                        actiontarget.setFill(Color.RED);
                        actiontarget.setText("Poprzednie hasło jest błędne.");
                    }


                } catch (IOException e1) {
                    e1.printStackTrace();
                }catch (Exception ex)
                {
                    ex.printStackTrace();
                    actiontarget.setFill(Color.RED);
                    actiontarget.setText(ex.getMessage());
                }

            }
        });

        Scene scene = new Scene(grid, 450, 275);
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

        final Text infoText = new Text();
        infoText.setWrappingWidth(150);
        grid.add(infoText, 0, 4);

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
        Label cbLabel = new Label("Lista akcji:");
        grid.add(cbLabel, 0, 0);
        final ComboBox actionsList = new ComboBox(getActionsList());
        grid.add(actionsList,1, 0);

        Button runActionBtn = new Button("Uruchom akcję");
        HBox hbRunActionBtn = new HBox(10);
        hbRunActionBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbRunActionBtn.getChildren().add(runActionBtn);
        grid.add(hbRunActionBtn, 2, 0);

        runActionBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                String actionName = (String)actionsList.getValue();
                if (actionName != null)
                {
                    try {
                        ActionExecuter.executeAction(actionName);
                    } catch (AWTException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }else
                {
                    infoText.setText("Nie można uruchomić");
                    infoText.setFill(Color.DARKGREEN);
                }

            }
        });

        Button deleteActionBtn = new Button("Usuń akcję");
        HBox hbdeleteActionBtn = new HBox(10);
        hbdeleteActionBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbdeleteActionBtn.getChildren().add(deleteActionBtn);
        grid.add(hbdeleteActionBtn, 2, 1);

        deleteActionBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                //TODO:run action
                String actionName = (String)actionsList.getValue();
                if (actionName != null)
                {
                    //Usuń plik:
                    try {
                        Files.delete(Paths.get("./Akcje/" + actionName));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    actionsList.getItems().remove(actionName);
                    infoText.setText("Usunięto akcję");
                    infoText.setFill(Color.DARKGREEN);
                }else
                {
                    infoText.setText("Nie można usunąć!");
                    infoText.setFill(Color.DARKRED);
                }

            }
        });

        Button newRunAppBtn = new Button("Dodaj uruchamianie aplikacji");
        HBox hbNewRunAppBtn = new HBox(10);
        hbNewRunAppBtn.setAlignment(Pos.BOTTOM_LEFT);
        hbNewRunAppBtn.getChildren().add(newRunAppBtn);

        grid.add(hbNewRunAppBtn, 0, 2, 1, 1);

        newRunAppBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                newRunAppStage();

            }
        });

        Button remoteBtn = new Button("Włącz tryb zdalny");
        HBox hbRemoteBtn = new HBox(10);
        hbRemoteBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbRemoteBtn.getChildren().add(remoteBtn);

        grid.add(hbRemoteBtn, 2, 2, 1, 1);

        remoteBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                remoteConnectionStage();

            }
        });

        Button changePwBtn = new Button("Zmień hasło");
        HBox hbChangePwBtn = new HBox(10);
        hbChangePwBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbChangePwBtn.getChildren().add(changePwBtn);

        grid.add(hbChangePwBtn, 1, 2, 1, 1);

        changePwBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                changePasswordStage();
            }
        });
        //Label userName = new Label("User Name:");
        //grid.add(userName, 0, 1);

        //TextField userTextField = new TextField();
        //grid.add(userTextField, 1, 1);

        Scene scene = new Scene(grid, 450, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void newRunAppStage() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 25, 10, 25));

        Text scenetitle = new Text("Dodawanie aplikacji");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label appName = new Label("Nazwa aplikacji:");
        grid.add(appName, 0, 1);

        TextField appNameField = new TextField("app1");
        grid.add(appNameField, 1, 1);

        Label appPath = new Label("Ścieżka dostępu:");
        grid.add(appPath, 0, 2);

        TextField appPathField = new TextField();
        grid.add(appPathField, 1, 2);

        final Text infoSaveText = new Text();
        grid.add(infoSaveText, 0, 4);

        Button saveAppBtn = new Button("Zapisz");
        saveAppBtn.setDisable(true);
        HBox hbsaveAppBtn = new HBox(10);
        hbsaveAppBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbsaveAppBtn.getChildren().add(saveAppBtn);
        grid.add(hbsaveAppBtn, 0, 3);

        saveAppBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                String appName = appNameField.getText();
                String appPath = appPathField.getText();
                String filePath = "./Akcje/" + appName;

                ObservableList<String> allActions = getActionsList();
                if(allActions.contains(appName))
                {
                    infoSaveText.setText("Ta nazwa już istnieje!");
                    infoSaveText.setFill(Color.DARKRED);
                }else {
                    Charset utf8 = StandardCharsets.UTF_8;
                    List<String> lines = Arrays.asList("RUNAPP " + appPath);

                    try {
                        Files.write(Paths.get(filePath), lines, utf8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    mainStage();
                }
            }
        });

        appPathField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(appPathField.getText().length() > 0)
                {
                    saveAppBtn.setDisable(false);
                }else
                {
                    saveAppBtn.setDisable(true);
                }
            }
        });

        appNameField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(appNameField.getText().length() > 0)
                {
                    saveAppBtn.setDisable(false);
                }else
                {
                    saveAppBtn.setDisable(true);
                }
            }
        });

        Button cancelBtn = new Button("Anuluj");
        HBox hbCancelBtn = new HBox(10);
        hbCancelBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbCancelBtn.getChildren().add(cancelBtn);
        grid.add(hbCancelBtn, 1, 3);

        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                mainStage();
            }
        });

        Scene scene = new Scene(grid, 450, 275);
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public void newActionCaptureStage()
    {
        final ActionCapturer[] actionCapturer = {null};
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

        Label LabelIgnoreMouseMove = new Label("Ignoruj ruchy myszy:");
        grid.add(LabelIgnoreMouseMove, 0, 4, 1, 1);

        CheckBox cbIgnoreMouseMove = new CheckBox();
        cbIgnoreMouseMove.setSelected(false);
        grid.add(cbIgnoreMouseMove,1, 4, 1, 1);
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
        grid.add(infoSaveText, 0, 5);

        Button returnBtn = new Button("Powrót");
        returnBtn.setDisable(true);
        HBox hbReturnBtn = new HBox(10);
        hbReturnBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbReturnBtn.getChildren().add(returnBtn);
        grid.add(hbReturnBtn, 2, 4);

        returnBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                //unhook:
                if(wasStarted)
                {
                    try {
                        GlobalScreen.unregisterNativeHook();
                        actionCapturer[0].stopListening();
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
                String actionName = actionNameField.getText();
                ObservableList<String> allActions = getActionsList();

                if(allActions.contains(actionName))
                {
                    infoSaveText.setText("Ta nazwa już istnieje!");
                    infoSaveText.setFill(Color.DARKRED);
                }
                else{
                    infoSaveText.setText("Wciśnij klawisz Esc, aby zapisać.\nNastępnie powróć.");
                    infoSaveText.setFill(Color.DARKGREEN);

                    actionNameField.setEditable(false);
                    returnBtn.setDisable(false);
                    wasStarted = true;

                    boolean ignoreMouseMove = cbIgnoreMouseMove.isSelected();
                    actionCapturer[0] = new ActionCapturer(actionName, ignoreMouseMove);
                    new Thread(actionCapturer[0]).start();
                    startBtn.setDisable(true);
                }
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

            }

        });
        */

        Button cancelBtn = new Button("Anuluj");
        HBox hbCancelBtn = new HBox(10);
        hbCancelBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbCancelBtn.getChildren().add(cancelBtn);
        grid.add(hbCancelBtn, 2, 3);

        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                if(wasStarted)
                {
                    try {
                        GlobalScreen.unregisterNativeHook();
                        actionCapturer[0].stopListening();
                    } catch (NativeHookException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        Files.delete(Paths.get("./Akcje/" + actionNameField.getText()));
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






        Scene scene = new Scene(grid, 450, 275);
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    public void remoteConnectionStage() {


        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 25, 10, 25));

        Text scenetitle = new Text("Tryb zdalny");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        grid.add(scenetitle, 0, 0, 2, 1);

        final TextArea infoArea = new TextArea();
        BluetoothServer myServer = new BluetoothServer(infoArea);

        grid.add(infoArea, 0, 3, 5, 4);


        Button runRemoteBtn = new Button("Uruchom tryb zdalny");
        HBox hbRunRemoteBtn = new HBox(10);
        hbRunRemoteBtn.setAlignment(Pos.BOTTOM_LEFT);
        hbRunRemoteBtn.getChildren().add(runRemoteBtn);
        hbRunRemoteBtn.setMinWidth(150);
        grid.add(hbRunRemoteBtn, 2, 1);

        runRemoteBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                //Uruchomienie servera, server będzie komunikował się z pilotem i delegował uruchamianie akcji itd.
                myServer.startServer();
                runRemoteBtn.setDisable(true);
            }
        });

        Button cancelBtn = new Button("Anuluj");
        HBox hbCancelBtn = new HBox(10);
        hbCancelBtn.setAlignment(Pos.BOTTOM_LEFT);
        hbCancelBtn.getChildren().add(cancelBtn);
        hbCancelBtn.setMinWidth(150);
        grid.add(hbCancelBtn, 4, 1);

        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                //Uruchomienie servera, server będzie komunikował się z pilotem i delegował uruchamianie akcji itd.
                try {
                    myServer.stopServer();

                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                mainStage();
            }
        });

        Scene scene = new Scene(grid, 450, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
