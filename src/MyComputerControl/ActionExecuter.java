package MyComputerControl;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class ActionExecuter {


    public void executeAction() throws AWTException, IOException {
       // byte data[] = Files.readAllBytes(Paths.get("file2.bin"));
        Charset utf8 = StandardCharsets.UTF_8;

        List<String> str = Files.readAllLines(Paths.get("file2.bin"), utf8);
        System.out.println(str);

        Robot r = new Robot();
        r.keyPress(65);
        r.keyRelease(65);

    }


    public void test() throws IOException, AWTException {
        Charset utf8 = StandardCharsets.UTF_8;
        List<String> lines = Arrays.asList("1st line");

        Files.write(Paths.get("file2.bin"), lines, utf8,StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        ActionExecuter a = new ActionExecuter();
        a.executeAction();
    }
}