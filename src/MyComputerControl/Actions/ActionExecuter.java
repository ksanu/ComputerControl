package MyComputerControl.Actions;

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

    private static List<String> getActionLines(String filePath) throws IOException {
        Charset utf8 = StandardCharsets.UTF_8;
        List<String> allLines = Files.readAllLines(Paths.get(filePath), utf8);
        return allLines;
    }

    public static void typeText(String text)
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
    public static void executeAction(String actionName) throws AWTException, IOException, InterruptedException {

        List<String> actionLines = getActionLines("./Akcje/" + actionName);

        for(String line : actionLines)
        {
            String[] elems = line.split("\\t");
            runSingleAction(elems);
            TimeUnit.MILLISECONDS.sleep(15);


        }


    }
    public static void mouseUp()
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
    public static void mouseDown()
    {
        try {
            Robot r = new Robot();
            Point point = MouseInfo.getPointerInfo().getLocation();
            r.mouseMove(point.x, point.y+3);
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }

    public static void mouseLeft()
    {
        try {
            Robot r = new Robot();
            Point point = MouseInfo.getPointerInfo().getLocation();
            r.mouseMove(point.x-3, point.y);
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }

    public static void mouseRight()
    {
        try {
            Robot r = new Robot();
            Point point = MouseInfo.getPointerInfo().getLocation();
            r.mouseMove(point.x+3, point.y);
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }

    private static int getKeyCodeFromString(String keyText)
    {
        int myKeyCode = KeyEvent.VK_UNDEFINED;
        if(keyText.length() > 1)
        {
            switch (keyText)
            {
                case "F1":
                    myKeyCode = KeyEvent.VK_F1;
                break;
                case "F2":
                    myKeyCode = KeyEvent.VK_F2;
                break;
                case "F3":
                    myKeyCode = KeyEvent.VK_F3;
                break;
                case "F4":
                    myKeyCode = KeyEvent.VK_F4;
                break;
                case "F5":
                    myKeyCode = KeyEvent.VK_F5;
                break;
                case "F6":
                    myKeyCode = KeyEvent.VK_F6;
                break;
                case "F7":
                    myKeyCode = KeyEvent.VK_F7;
                break;
                case "F8":
                    myKeyCode = KeyEvent.VK_F8;
                break;
                case "F9":
                    myKeyCode = KeyEvent.VK_F9;
                break;
                case "F10":
                    myKeyCode = KeyEvent.VK_F10;
                break;
                case "F11":
                    myKeyCode = KeyEvent.VK_F11;
                break;
                case "F12":
                    myKeyCode = KeyEvent.VK_F12;
                break;
                case "F13":
                    myKeyCode = KeyEvent.VK_F13;
                break;
                case "F14":
                    myKeyCode = KeyEvent.VK_F14;
                break;
                case "F15":
                    myKeyCode = KeyEvent.VK_F15;
                break;
                case "F16":
                    myKeyCode = KeyEvent.VK_F16;
                break;
                case "F17":
                    myKeyCode = KeyEvent.VK_F17;
                break;
                case "F18":
                    myKeyCode = KeyEvent.VK_F18;
                break;
                case "F19":
                    myKeyCode = KeyEvent.VK_F19;
                break;
                case "F20":
                    myKeyCode = KeyEvent.VK_F20;
                break;
                case "F21":
                    myKeyCode = KeyEvent.VK_F21;
                break;
                case "F22":
                    myKeyCode = KeyEvent.VK_F22;
                break;
                case "F23":
                    myKeyCode = KeyEvent.VK_F23;
                break;
                case "F24":
                    myKeyCode = KeyEvent.VK_F24;
                break;
                case "Back Quote":
                    myKeyCode = KeyEvent.VK_BACK_QUOTE;
                    break;
                case "Minus":
                    myKeyCode = KeyEvent.VK_MINUS;
                    break;
                case "Equals":
                    myKeyCode = KeyEvent.VK_EQUALS;
                    break;
                case "Backspace":
                    myKeyCode = KeyEvent.VK_BACK_SPACE;
                    break;
                case "Tab":
                    myKeyCode = KeyEvent.VK_TAB;
                    break;
                case "Caps Lock":
                    myKeyCode = KeyEvent.VK_CAPS_LOCK;
                    break;
                case "Open Bracket":
                    myKeyCode = KeyEvent.VK_OPEN_BRACKET;
                    break;
                case "Close Bracket":
                    myKeyCode = KeyEvent.VK_CLOSE_BRACKET;
                    break;
                case "Back Slash":
                    myKeyCode = KeyEvent.VK_BACK_SLASH;
                    break;
                case "Semicolon":
                    myKeyCode = KeyEvent.VK_SEMICOLON;
                    break;
                case "Quote":
                    myKeyCode = KeyEvent.VK_QUOTE;
                    break;
                case "Enter":
                    myKeyCode = KeyEvent.VK_ENTER;
                    break;
                case "Comma":
                    myKeyCode = KeyEvent.VK_COMMA;
                    break;
                case "Period":
                    myKeyCode = KeyEvent.VK_PERIOD;
                    break;
                case "Slash":
                    myKeyCode = KeyEvent.VK_SLASH;
                    break;
                case "Space":
                    myKeyCode = KeyEvent.VK_SPACE;
                    break;
                case "Print Screen":
                    myKeyCode = KeyEvent.VK_PRINTSCREEN;
                    break;
                case "Scroll Lock":
                    myKeyCode = KeyEvent.VK_SCROLL_LOCK;
                    break;
                case "Pause":
                    myKeyCode = KeyEvent.VK_PAUSE;
                    break;
                case "Insert":
                    myKeyCode = KeyEvent.VK_INSERT;
                    break;
                case "Delete":
                    myKeyCode = KeyEvent.VK_DELETE;
                    break;
                case "Home":
                    myKeyCode = KeyEvent.VK_HOME;
                    break;
                case "End":
                    myKeyCode = KeyEvent.VK_END;
                    break;
                case "Page Up":
                    myKeyCode = KeyEvent.VK_PAGE_UP;
                    break;
                case "Page Down":
                    myKeyCode = KeyEvent.VK_PAGE_DOWN;
                    break;
                case "Up":
                    myKeyCode = KeyEvent.VK_UP;
                    break;
                case "Down":
                    myKeyCode = KeyEvent.VK_DOWN;
                    break;
                case "Left":
                    myKeyCode = KeyEvent.VK_LEFT;
                    break;
                case "Right":
                    myKeyCode = KeyEvent.VK_RIGHT;
                    break;
                case "Clear":
                    myKeyCode = KeyEvent.VK_CLEAR;
                    break;
                case "Num Lock":
                    myKeyCode = KeyEvent.VK_NUM_LOCK;
                    break;
                case "Separator":
                    myKeyCode = KeyEvent.VK_SEPARATOR;
                    break;
                case "Shift":
                    myKeyCode = KeyEvent.VK_SHIFT;
                    break;
                case "Ctrl":
                    myKeyCode = KeyEvent.VK_CONTROL;
                    break;
                case "Alt":
                    myKeyCode = KeyEvent.VK_ALT;
                    break;
                case "Meta":
                    myKeyCode = KeyEvent.VK_META;
                    break;
                case "Context Menu":
                    myKeyCode = KeyEvent.VK_CONTEXT_MENU;
                    break;
            }
        }else{

            myKeyCode = KeyEvent.getExtendedKeyCodeForChar(keyText.charAt(0));
        }
        return myKeyCode;
    }


    public static void runSingleAction(String[] args) throws AWTException {
        Robot r = new Robot();
        int x;
        int y;
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
                x = getKeyCodeFromString(args[1]);
                if(x != KeyEvent.VK_UNDEFINED) r.keyPress(x);
                break;
            case "KEYRELEASED":
                x = getKeyCodeFromString(args[1]);
                if(x != KeyEvent.VK_UNDEFINED) r.keyRelease(x);
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