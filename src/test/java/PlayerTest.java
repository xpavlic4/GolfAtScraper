import com.google.common.annotations.VisibleForTesting;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class PlayerTest {
    @Test
    public void testMatch() {
        String s = "Radim Pavlicek (5.1) Schoenfeld";
        Player parse = Player.parse(s);
        assertEquals(parse.getName(), "Radim Pavlicek");
        assertEquals(parse.getHcp(), "5.1");
        assertEquals(parse.getClub(), "Schoenfeld");
    }

    @Test
    public void test2() {
        String x= "Hello (5.1) Shoenfeld";
        Matcher m = Pattern.compile("(.*) \\((.*?)\\) (.*)").matcher(x);
        System.out.println(m.groupCount());
        while(m.find()) {
            System.out.println(m.group(1));
            System.out.println(m.group(2));
            System.out.println(m.group(3));
        }


    }
    @Test
    public void test3() {
        String x = "Ju Ha (11,4) GOLFCLUB SCHÖNFELD<br>Be Chr (25,9) GOLFCLUB SCHÖNFELD<br><br><br><br>Flight ganz gebucht.";
        Player parse = Player.parse(x);
        System.out.println(parse);
    }
}