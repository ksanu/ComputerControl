package MyComputerControl;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;


public class GlobalKeyListener implements NativeKeyListener{
    public String filePath;
    GlobalKeyListener(String filePath) throws IOException {
        this.filePath = filePath;

    }
    public void nativeKeyPressed(NativeKeyEvent e)   {
        System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));


        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e1) {
                e1.printStackTrace();
            }
        }else{
            Charset utf8 = StandardCharsets.UTF_8;
            List<String> lines = Arrays.asList("KEYPRESSED\t" + e.getRawCode());

            try {
                Files.write(Paths.get(filePath), lines, utf8,StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
        System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
        //System.out.println("Id: " + e.getID() + "\tModifier: " + e.getModifiers()+"raw: " + e.getRawCode());
        Charset utf8 = StandardCharsets.UTF_8;
        List<String> lines = Arrays.asList("KEYRELEASED\t" + e.getRawCode());
        try {
            Files.write(Paths.get(filePath), lines, utf8,StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    public void nativeKeyTyped(NativeKeyEvent e) {
        //System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));

    }



}