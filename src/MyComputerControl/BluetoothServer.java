package MyComputerControl;

import javafx.scene.control.TextArea;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.*;

public class BluetoothServer {

    TextArea infoText;
    Thread myServerThread = null;
    BTServerThread myServerInstance = null;

    public BluetoothServer(TextArea textArea)
    {
        this.infoText = textArea;
    }

    public void sendInfo(String info)
    {
        String oldtext = infoText.getText();
        infoText.setText(oldtext + "\n" + info);
    }

    public void stopServer() throws IOException {
        if(myServerThread.isAlive() && !myServerInstance.equals(null) ) {
            myServerInstance.stopBTServer();
            myServerThread = null;
            myServerInstance = null;
        }
    }

    public void startServer()
    {
        //stworzenie instancji klasy serwera i uruchomienie w nowym wątku
        myServerInstance = new BTServerThread(this.infoText);
        myServerThread = new Thread(myServerInstance);
        myServerThread.start();

    }

}
