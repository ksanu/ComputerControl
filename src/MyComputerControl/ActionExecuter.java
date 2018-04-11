package MyComputerControl;

import java.awt.*;
import java.awt.event.InputEvent;
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

    public List<String> getActionLines(String filePath) throws IOException {
        Charset utf8 = StandardCharsets.UTF_8;
        List<String> allLines = Files.readAllLines(Paths.get(filePath), utf8);
        return allLines;
    }

    public void executeAction(String actionName) throws AWTException, IOException, InterruptedException {

        List<String> actionLines = getActionLines("./Akcje/" + actionName);

        for(String line : actionLines)
        {
            String[] elems = line.split("\\t");
            runSingleAction(elems);
            TimeUnit.MILLISECONDS.sleep(5);


        }


    }

    private void runSingleAction(String[] args) throws AWTException {
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