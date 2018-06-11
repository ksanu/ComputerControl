package MyComputerControl.BTServer;

import MyComputerControl.Actions.ActionExecuter;
import MyComputerControl.MyGUI;
import MyComputerControl.Security.AESEncryptor;
import MyComputerControl.Security.PasswordHandler;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class BTServerThread implements Runnable {
    TextArea infoText;
    StreamConnection myBlueroothConnection = null;
    BufferedReader myBlueroothConnectionReader = null;
    PrintWriter myBluetoorhConnectionWriter = null;
    StreamConnectionNotifier myStreamConnecrionNotifier = null;
    Integer btState;
    boolean btLoopRunning = true;
    String encodedPwHash = null;

    public BTServerThread(TextArea textArea)
    {
        this.infoText = textArea;
        this.btState = BTStates.btSetupForWaiting;
    }

    public void sendInfo(String info)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String oldtext = infoText.getText();
                String[] allLines = oldtext.split("\\n");
                if(allLines.length + 1 > 25) {
                    String newText = "";
                    for(int index = allLines.length - 25; index < allLines.length; index++)
                    {
                        newText += allLines[index] + "\n";
                    }
                    infoText.setText(newText);
                }else
                infoText.setText(oldtext + "\n" + info + "\n");
                infoText.positionCaret(infoText.getText().length());
                infoText.selectPositionCaret(infoText.getText().length());
            }
        });
    }
    @Override
    public void run() {
        runServerLoop();
    }

    public void runServerLoop()
    {
        String messageLine=null;
        RemoteDevice device = null;
        while(btLoopRunning && !this.btState.equals(BTStates.btEND)) {
            try {
                //Uruchamianie servera bt
                if(btState.equals(BTStates.btSetupForWaiting) && myStreamConnecrionNotifier == null) {
                    sendInfo("Uruchamianie serwera Bluetooth...");
                    myStreamConnecrionNotifier = setupServerForWaiting();
                    //Jeżeli server został uruchomiony- zmieniamy stan na oczekiwanie na połączenie
                    if(myStreamConnecrionNotifier != null) btState = BTStates.btWaitingForConnection;
                }
                if(btState.equals(BTStates.btWaitingForConnection)) {
                    sendInfo("Oczekiwanie na połączenie...");
                    device = waitForConnection(myStreamConnecrionNotifier);
                    if (device != null) {
                        btState = BTStates.btConnectedWithDevice;
                        sendInfo("Połączono ze zdalnym urządzeniem:\n" + "Adres urządzenia: " + device.getBluetoothAddress() +
                                "\nNazwa urządzenia: " + device.getFriendlyName(true));
                        //mamy połączenie, można nawiązać komunikację
                        openIOStreams();
                    }else {
                    sendInfo("Błąd, nie można połączyć ze zdalnym urządzeniem");
                    }
                }
                if(btState.equals(BTStates.btConnectedWithDevice)) {
                    //mamy połączenie ze zdalnym urządzeniem
                    try {
                        //mając IO streams możemy odbierać i wysyłać komunikaty:
                        messageLine = readLineFromRemote();
                        //System.out.println(messageLine);
                         if(messageLine!=null) {
                             handleMessage(messageLine);
                         }else{
                             //BufferedReader.readline() == null przy zamknięciu socketa
                            sendInfo("...przerwano połączenie...");
                             btState = BTStates.btWaitingForConnection;
                        }

                    } catch (IOException e) {
                        sendInfo("...przerwano połączenie...");
                        btState = BTStates.btWaitingForConnection;
                    }


                }


            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

        }
        //kończymy działanie serwera;
        try {
            if (myBlueroothConnection != null) myBlueroothConnection.close();
            if (myBluetoorhConnectionWriter != null) myBluetoorhConnectionWriter.close();
            if (myBlueroothConnectionReader != null) myBlueroothConnectionReader.close();
            if (myStreamConnecrionNotifier != null) myStreamConnecrionNotifier.close();
            myStreamConnecrionNotifier = null;
            myBlueroothConnection = null;
            myBlueroothConnectionReader = null;
            myBluetoorhConnectionWriter = null;
        }catch (IOException e)
        {
            e.printStackTrace();
            sendInfo("Błąd przy zamykaniu serwera");
        }
    }

    private void sendPasswordSalt()
    {
        try {
            List<String> allLines = Files.readAllLines(Paths.get(MyGUI.confFilePath), StandardCharsets.UTF_8);
            String storedPw = allLines.get(0);
            String salt = PasswordHandler.getStoredSalt(storedPw);
            sendMessage(MessageTypes.Server.PASSWORD_SALT, salt);

        }catch (IOException e)
        {
            sendInfo("Nie można odczytać pliku conf.");
        }
    }

    private void sendAuthorizationResult(String pwHash)
    {
        try {
            List<String> allLines = Files.readAllLines(Paths.get(MyGUI.confFilePath), StandardCharsets.UTF_8);
            String stored = allLines.get(0);
            String[] saltAndPass = stored.split("\\$");

            String result = MessageContent.AUTHORIZATION_RESULT.FAILURE;
            if(pwHash.equals(saltAndPass[1])) result = MessageContent.AUTHORIZATION_RESULT.SUCCESS;
            sendMessage(MessageTypes.Server.AUTHORIZATION_RESULT, result);
        }catch (IOException e)
        {
            sendInfo("Nie można odczytać pliku conf.");
        }catch (Exception e)
        {
            sendInfo(e.getMessage());
        }
    }

    private String decryptMessageLine(String messageLine)
    {
        if(encodedPwHash==null)
        {
            try {
                List<String> allLines = Files.readAllLines(Paths.get(MyGUI.confFilePath), StandardCharsets.UTF_8);
                String stored = allLines.get(0);
                String[] saltAndPass = stored.split("\\$");
                encodedPwHash = saltAndPass[1];

            }catch (IOException e)
            {
                sendInfo("Nie można odczytać pliku conf.");
            }catch (Exception e)
            {
                sendInfo(e.getMessage());
            }

        }
        return AESEncryptor.decrypt(encodedPwHash, messageLine);
    }

    private void handleMessage(String messageLine){
        String[] msgTypeAndContent = messageLine.split("\\t");
        if(msgTypeAndContent[0].equals(MessageTypes.Client.GET_PASSWORD_SALT))
        {
            sendPasswordSalt();
            return;
        }
        msgTypeAndContent = decryptMessageLine(messageLine).split("\\t");
        switch(msgTypeAndContent[0])
        {
            case MessageTypes.Client.GET_PASSWORD_SALT:
                sendPasswordSalt();
                break;
            case MessageTypes.Client.AUTHORIZE_PASSWORD_HASH:
                sendAuthorizationResult(msgTypeAndContent[1]);
                break;
            case MessageTypes.Client.CLIENT_STATE:
                if(msgTypeAndContent[1].equals(MessageContent.STATE.READY_FOR_REMOTE_CONTROL)){
                    sendMessage(MessageTypes.Server.SERVER_STATE, MessageContent.STATE.READY_FOR_REMOTE_CONTROL);
                }
                if(msgTypeAndContent[1].equals(MessageContent.STATE.CLOSING)){
                    sendInfo("Zdalny pilot kończy połączenie.");
                }
                break;
            //RemoteControl
            case MessageTypes.Client.GET_AVAILABLE_ACTIONS:
                File[] files = new File("./Akcje").listFiles();
                if( files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            sendMessage(MessageTypes.Server.AVAILABLE_ACTION, file.getName());
                        }
                    }
                }
                break;
            case MessageTypes.Client.EXECUTE_ACTION:

                String actionName = msgTypeAndContent[1];
                try {
                    ActionExecuter.executeAction(actionName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MessageTypes.Client.TEXT_TO_TYPE:

                String text = msgTypeAndContent[1];
                try {
                    ActionExecuter.typeText(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MessageTypes.Client.EXECUTE_SINGLE_ACTION:
                String[] content = new String[msgTypeAndContent.length-1];
                for(int i=1; i < msgTypeAndContent.length; i++)
                {
                    content[i-1] = msgTypeAndContent[i];
                }
                try {
                    ActionExecuter.runSingleAction(content);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
                break;
            case MessageTypes.Client.EXECUTE_MOUSE_ACTION:
                String mouseAction = msgTypeAndContent[1];
                switch (mouseAction)
                {
                    case MessageContent.SINGLEACTION.MOUSEUP:
                       ActionExecuter.mouseUp();
                       break;
                    case MessageContent.SINGLEACTION.MOUSEDOWN:
                        ActionExecuter.mouseDown();
                        break;
                    case MessageContent.SINGLEACTION.MOUSELEFT:
                        ActionExecuter.mouseLeft();
                        break;
                    case MessageContent.SINGLEACTION.MOUSERIGHT:
                        ActionExecuter.mouseRight();
                        break;
                    default:
                        break;
                }

                break;

            default:
                sendMessage(MessageTypes.Server.AUTHORIZATION_RESULT, MessageContent.AUTHORIZATION_RESULT.FAILURE);
                break;
        }

    }
    public synchronized void stopBTServer()
    {
        btState = BTStates.btEND;
        btLoopRunning = false;
    }

    private void openIOStreams() throws IOException {
        InputStream inStream = myBlueroothConnection.openInputStream();
        this.myBlueroothConnectionReader = new BufferedReader(new InputStreamReader(inStream));

        OutputStream outStream = myBlueroothConnection.openOutputStream();
        myBluetoorhConnectionWriter = new PrintWriter(new OutputStreamWriter(outStream));
    }
    private String readLineFromRemote() throws IOException {
        String receivedLine = myBlueroothConnectionReader.readLine();
        return receivedLine;
    }

    private String encryptMessageLine(String messageLine)
    {
        if(encodedPwHash==null)
        {
            try {
                List<String> allLines = Files.readAllLines(Paths.get(MyGUI.confFilePath), StandardCharsets.UTF_8);
                String stored = allLines.get(0);
                String[] saltAndPass = stored.split("\\$");
                encodedPwHash = saltAndPass[1];

            }catch (IOException e)
            {
                sendInfo("Nie można odczytać pliku conf.");
            }catch (Exception e)
            {
                sendInfo(e.getMessage());
            }

        }
        return AESEncryptor.encrypt(encodedPwHash, messageLine);
    }

    private void sendMessage(String messageType, String messageContent)
    {
        if(messageType.equals(MessageTypes.Server.PASSWORD_SALT)) {
            String msgLine = messageType + "\t" + messageContent;
            myBluetoorhConnectionWriter.write(msgLine + "\n");
            myBluetoorhConnectionWriter.flush();
        }else{
            String msgLine = messageType + "\t" + messageContent;
            String encryptedMsgLine = encryptMessageLine(msgLine);
            myBluetoorhConnectionWriter.write(encryptedMsgLine + "\n");
            myBluetoorhConnectionWriter.flush();
        }

    }





    /**
     * Ustawia server tak, aby mógł oczekiwać na połączenie, a następnie zaakceptować je
     *
     * @return StreamConnectionNotifier używany do akceptacji i otwarcia połączenia. Zwraca null jeżeli uruchomienie nie powiodło się.
     */
    private StreamConnectionNotifier setupServerForWaiting()
    {
        LocalDevice local = null;

        StreamConnectionNotifier notifier = null;

        try {
            // retrieve the local Bluetooth device object
            local = LocalDevice.getLocalDevice();
            int currentDiscoverableMode = local.getDiscoverable();
            //ustawienie trybu widoczności urządzenia. GIAC - General/Unlimited Inquiry Access Code
            if(currentDiscoverableMode != DiscoveryAgent.GIAC) {
                local.setDiscoverable(DiscoveryAgent.GIAC);
            }

            UUID uuid = new UUID(145554050); //unikalny identyfikator mojej usługi bluetooth
                                                    // "08acfa82-0000-1000-8000-00805f9b34fb"

            String url = "btspp://localhost:" + uuid.toString() + ";name=RemoteBluetooth";
            notifier = (StreamConnectionNotifier) Connector.open(url);

        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
        return notifier;
    }

    /**
     * Czekanie na połączenie z urządzeniami
     **/
    private RemoteDevice waitForConnection(StreamConnectionNotifier notifier)
    {
        // waiting for connection
        RemoteDevice device = null;

        try {
            System.out.println("waiting for connection...");
            myBlueroothConnection = notifier.acceptAndOpen();
            device = RemoteDevice.getRemoteDevice(myBlueroothConnection);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return device;
    }
}
