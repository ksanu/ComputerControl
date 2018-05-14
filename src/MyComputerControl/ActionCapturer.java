package MyComputerControl;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.io.IOException;

public class ActionCapturer implements Runnable {
    String filePath;
    boolean ignoreMouseMove;
    GlobalMouseListener myMouseListener = null;
    GlobalKeyListener myKeyListener = null;
    @Override
    public void run() {

        try {
            startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Thread " +  this.toString() + " exiting.");
    }
    public ActionCapturer(String filePath, boolean ignoreMouseMove)
    {
        this.filePath = "./Akcje/" + filePath;
        this.ignoreMouseMove = ignoreMouseMove;
    }
    public void startListening() throws IOException {
        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        myMouseListener = new GlobalMouseListener(this.filePath);

        // Add the appropriate listeners.
        GlobalScreen.addNativeMouseListener(myMouseListener);
        if(ignoreMouseMove == false) GlobalScreen.addNativeMouseMotionListener(myMouseListener);


        myKeyListener = new GlobalKeyListener(this.filePath);
        GlobalScreen.addNativeKeyListener(myKeyListener);
    }
    public void stopListening()
    {
        GlobalScreen.removeNativeKeyListener(myKeyListener);
        GlobalScreen.removeNativeMouseListener(myMouseListener);
        GlobalScreen.removeNativeMouseMotionListener(myMouseListener);
    }

}
