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
}