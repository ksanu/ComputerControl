package MyComputerControl;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.io.IOException;

public class ActionCapturer implements Runnable {
    String filePath;
    @Override
    public void run() {

        try {
            startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Thread " +  this.toString() + " exiting.");
    }
    public ActionCapturer(String filePath)
    {
        //new File("/Akcje/").mkdirs();

        this.filePath = "./Akcje/" + filePath;
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

        // Construct the example object.
        GlobalMouseListener myMouseListener = new GlobalMouseListener(this.filePath);

        // Add the appropriate listeners.
        GlobalScreen.addNativeMouseListener(myMouseListener);
        GlobalScreen.addNativeMouseMotionListener(myMouseListener);

        GlobalScreen.addNativeKeyListener(new GlobalKeyListener(this.filePath));

    }

}
