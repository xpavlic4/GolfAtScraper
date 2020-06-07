import com.google.common.io.Resources;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.lang.System.exit;

public class Scraper {
    public static void main(String[] args) throws IOException {
        new Scraper().run();
    }

    private void run() throws IOException {
        Map<String, String> koks = new HashMap<>();
        // get login form
        Connection.Response loginForm = Jsoup.connect("https://www.golf.at/mygolf/login/")
                .method(Connection.Method.GET)
                .execute();

        list(loginForm.cookies());
        koks.putAll(loginForm.cookies());
//        System.out.println(loginForm.parse().text());
        String pwd = Resources.toString(Resources.getResource("password"), Charset.defaultCharset());

        if (pwd.isBlank()) {
            exit(1);
        }
        // POST login data
        Connection.Response loginResponse = Jsoup.connect("https://www.golf.at/mygolf/login/")
                .data("loginusername", "M436244")
                .data("loginpassword", pwd)
                .data("a", "dologin")
                .data("wantedurl", "")
                .cookies(koks)
                .method(Connection.Method.POST)
                .timeout(100000)
                .execute();
//        System.out.println(loginResponse.parse().text());
//        list(koks);


        Document  clubsResponse = Jsoup.connect("https://www.golf.at/mobile/startzeiten.asp?uri=/mygolf/tee-online/verfuegbare-startzeiten/golfclub-schoenfeld/315/")
                .method(Connection.Method.GET)
                .cookies(koks)
                .timeout(100000)
                .get();

        Elements select = clubsResponse.select(".teetime");
        select.forEach(Scraper::process);
//        System.out.println(clubsResponse.text());

    }

    private static void process(Element a) {
        Attributes attrs = a.attributes();
        Iterator<Attribute> iterator = attrs.iterator();
        String time = null;
        String who = null;
        Teetime t = new Teetime();
        while (iterator.hasNext()) {
            Attribute next = iterator.next();
            String key = next.getKey();

            if (key.equals("data-content")) {
                who = next.getValue();
                if (who.startsWith("Zu dieser Zeit ist keine Reservierung")) {
                    t.setState(Teetime.State.ALL_AVAILABLE);
                } else if(who.contains("Gesperrt.")) {
                    t.setState(Teetime.State.NOT_AVAILABLE);
                } else if(who.contains("Flight komplett ver")) {
                    t.setState(Teetime.State.ALL_BOOKED);
                } else if (who.contains("br><br>")) {
                    who = who.split("<br><br>")[0];
                    String[] plauers = who.split("<br>");
                    for (String p :
                            plauers) {
                        Player player = Player.parse(p);
                        if (player != null)
                            t.addPlayer(player);
                    }
                    t.setState(Teetime.State.BOOKED);
                }

            }
            if (key.equals("data-original-title")) {
                time = next.getValue();
                t.parseTime(time);
            }
        }
        if (t.isBooked())
            System.out.println(t.toString());

    }

    static void list(Map<String, String> a) {
        a.forEach((key, b) -> System.out.println(key + " -> " + b));
        System.out.println();
    }
}
