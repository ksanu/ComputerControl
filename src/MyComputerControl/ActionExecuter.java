package MyComputerControl;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ActionExecuter {

    static List<String> getActionLines(String filePath) throws IOException {
        Charset utf8 = StandardCharsets.UTF_8;
        List<String> allLines = Files.readAllLines(Paths.get(filePath), utf8);
        return allLines;
    }

    static void typeText(String text)
    {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);

        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }
    static void executeAction(String actionName) throws AWTException, IOException, InterruptedException {

        List<String> actionLines = getActionLines("./Akcje/" + actionName);

        for(String line : actionLines)
        {
            String[] elems = line.split("\\t");
            runSingleAction(elems);
            TimeUnit.MILLISECONDS.sleep(5);


        }


    }
    static void mouseUp()
    {
        try {
            Robot r = new Robot();
            Point point = MouseInfo.getPointerInfo().getLocation();
            if(point.y>0)
                r.mouseMove(point.x, point.y-3);
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }
    static void mouseDown()
    {
        try {
            Robot r = new Robot();
            Point point = MouseInfo.getPointerInfo().getLocation();
            r.mouseMove(point.x, point.y+3);
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }

    static void mouseLeft()
    {
        try {
            Robot r = new Robot();
            Point point = MouseInfo.getPointerInfo().getLocation();
            r.mouseMove(point.x-3, point.y);
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }

    static void mouseRight()
    {
        try {
            Robot r = new Robot();
            Point point = MouseInfo.getPointerInfo().getLocation();
            r.mouseMove(point.x+3, point.y);
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }
    static void runSingleAction(String[] args) throws AWTException {
        Robot r = new Robot();
        int x = 0;
        int y = 0;
        switch(args[0])
        {
            case "MOUSEMOVED":
                x = Integer.parseInt(args[1]);
                y = Integer.parseInt(args[2]);
                r.mouseMove(x, y);
                break;
            case "MOUSERELEASED":
                x = Integer.parseInt(args[1]);
                r.mouseRelease(InputEvent.getMaskForButton(x));
                break;
            case "MOUSEPRESSED":
                x = Integer.parseInt(args[1]);
                r.mousePress(InputEvent.getMaskForButton(x));
                break;
            case "KEYPRESSED":
                x = Integer.parseInt(args[1]);
                //x = java.awt.event.KeyEvent.getExtendedKeyCodeForChar(Integer.parseInt(args[1]));
                r.keyPress(x);
                break;
            case "KEYRELEASED":
                x = Integer.parseInt(args[1]);
                r.keyRelease(x);
                break;
            case "RUNAPP":

                try {
                    String filePath = "\"" + args[1] + "\"";
                    Runtime.getRuntime().exec(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;

        }
    }

}