package MyComputerControl;

import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class GlobalMouseListener implements NativeMouseInputListener {
    public String filePath;


    public GlobalMouseListener(String filePath)
    {
        this.filePath = filePath;
    }


    public void nativeMouseClicked(NativeMouseEvent e) {
        //System.out.println("Mouse Clicked: " + e.getClickCount());

    }

    public void nativeMousePressed(NativeMouseEvent e) {
        System.out.println("Mouse Pressed: " + e.getButton());
        Charset utf8 = StandardCharsets.UTF_8;
        List<String> lines = Arrays.asList("MOUSEPRESSED " + e.getButton());

        try {
            Files.write(Paths.get(filePath), lines, utf8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void nativeMouseReleased(NativeMouseEvent e) {
        System.out.println("Mouse Released: " + e.getButton());
        Charset utf8 = StandardCharsets.UTF_8;
        List<String> lines = Arrays.asList("MOUSERELEASED " + e.getButton());

        try {
            Files.write(Paths.get(filePath), lines, utf8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void nativeMouseMoved(NativeMouseEvent e) {
        System.out.println("Mouse Moved: " + e.getX() + ", " + e.getY());
        Charset utf8 = StandardCharsets.UTF_8;
        List<String> lines = Arrays.asList("MOUSEMOVED " + + e.getX() + " " + e.getY());

        try {
            Files.write(Paths.get(filePath), lines, utf8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void nativeMouseDragged(NativeMouseEvent e) {
        //System.out.println("Mouse Dragged: " + e.getX() + ", " + e.getY());
    }


}