import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class Scraper {
    public static void main(String[] args) throws IOException {
        new Scraper().run();
    }

    private void run() throws IOException {
        String tmpUsername = "";
        String pwd = "";
        String club = "";
        try (InputStream input = Scraper.class.getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            tmpUsername = prop.getProperty("username");
            pwd = prop.getProperty("password");
            club = prop.getProperty("club");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (pwd.isEmpty() || tmpUsername.isEmpty()) {
            throw new IllegalStateException("Username or password are empty!");
            //exit(1);
        }

        // POST login data
        // get login form
        Connection.Response loginForm = Jsoup.connect("https://www.golf.at/mygolf/login/")
                .method(Connection.Method.GET)
                .execute();

//        list(loginForm.cookies());
        Map<String, String> koks = new HashMap<>(loginForm.cookies());
//        System.out.println(loginForm.parse().text());
        Jsoup.connect("https://www.golf.at/mygolf/login/")
                .data("loginusername", tmpUsername)
                .data("loginpassword", pwd)
                .data("a", "dologin")
                .data("wantedurl", "")
                .cookies(koks)
                .method(Connection.Method.POST)
                .timeout(100000)
                .execute();

        String url = "https://www.golf.at/mobile/startzeiten.asp?uri=/mygolf/tee-online/verfuegbare-startzeiten/" + club;
        Document clubsResponse = Jsoup.connect(url)

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
        String time;
        String who;
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
                    if (who.contains("bekannte Spieler:")) {
                        who = who.split("bekannte Spieler:")[1];
                    } else {
                        who = who.split("<br><br>")[0];
                    }
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

}
