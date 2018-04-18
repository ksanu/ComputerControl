package MyComputerControl;

import javafx.scene.control.TextArea;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.*;

public class BTServerThread implements Runnable {
    TextArea infoText;
    StreamConnection myBlueroothConnection = null;
    BufferedReader myBlueroothConnectionReader = null;
    PrintWriter myBluetoorhConnectionWriter = null;
    StreamConnectionNotifier myStreamConnecrionNotifier = null;
    Integer btState;
    boolean btLoopRunning = true;
    public BTServerThread(TextArea textArea)
    {
        this.infoText = textArea;
        this.btState = BTStates.btSetupForWaiting;
    }

    public void sendInfo(String info)
    {
        String oldtext = infoText.getText();
        infoText.setText(oldtext + "\n" + info);
    }
    @Override
    public void run() {
        runServerLoop();
    }

    public void runServerLoop()
    {
        while(btLoopRunning && this.btState != BTStates.btEND) {
            try {
                //Uruchamianie servera bt
                if(btState.equals(BTStates.btSetupForWaiting) && myStreamConnecrionNotifier == null) {
                    sendInfo("Uruchamianie serwera Bluetooth...");
                    myStreamConnecrionNotifier = setupServerForWaiting();
                    //Jeżeli server został uruchomiony- zmieniamy stan na oczekiwanie na połączenie
                    if(myStreamConnecrionNotifier != null) btState = BTStates.btWaitingForConnection;
                }
                RemoteDevice device = null;
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
                        //test:
                        sendLineToRemote("hello");
                        sendInfo(readLineFromRemote());

                    } catch (IOException e) {
                        e.printStackTrace();
                        btState = BTStates.btWaitingForConnection;
                    }


                }


            } catch (Exception e) {
                e.printStackTrace();
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
    public synchronized void stopBTServer()
    {
        btState = BTStates.btEND;
        btLoopRunning = false;
    }

    public void openIOStreams() throws IOException {
        InputStream inStream = myBlueroothConnection.openInputStream();
        this.myBlueroothConnectionReader = new BufferedReader(new InputStreamReader(inStream));

        OutputStream outStream = myBlueroothConnection.openOutputStream();
        myBluetoorhConnectionWriter = new PrintWriter(new OutputStreamWriter(outStream));
    }
    public String readLineFromRemote() throws IOException {
        String receivedLine = myBlueroothConnectionReader.readLine();
        return receivedLine;
    }
    public void sendLineToRemote(String line)
    {
        myBluetoorhConnectionWriter.write(line + "\n");
        myBluetoorhConnectionWriter.flush();
    }





    /**
     * Ustawia server tak, aby mógł oczekiwać na połączenie, a następnie zaakceptować je
     *
     * @return StreamConnectionNotifier używany do akceptacji i otwarcia połączenia. Zwraca null jeżeli uruchomienie nie powiodło się.
     */
    private StreamConnectionNotifier setupServerForWaiting()
    {
        // retrieve the local Bluetooth device object
        LocalDevice local = null;

        StreamConnectionNotifier notifier = null;

        try {
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
            System.out.println(uuid.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
        return notifier;
    }

    /**
     * Czekanie na połączenie z urządzeniami
     **/
    public RemoteDevice waitForConnection(StreamConnectionNotifier notifier)
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
