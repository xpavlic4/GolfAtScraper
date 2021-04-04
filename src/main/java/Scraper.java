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

import static java.lang.System.exit;

public class Scraper {
    public static void main(String[] args) throws IOException {
        new Scraper().run();
    }

    private void run() throws IOException {
        String tmpUsername = "";
        String pwd = "";
        try (InputStream input = Scraper.class.getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            tmpUsername = prop.getProperty("username");
            pwd = prop.getProperty("password");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (pwd.isEmpty() || tmpUsername.isEmpty()) {
            System.err.println("Username or password are empty!");
            exit(1);
        }

        // POST login data
        // get login form
        Connection.Response loginForm = Jsoup.connect("https://www.golf.at/mygolf/login/")
                .method(Connection.Method.GET)
                .execute();

//        list(loginForm.cookies());
        Map<String, String> koks = new HashMap<>(loginForm.cookies());
//        System.out.println(loginForm.parse().text());
        Connection.Response loginResponse = Jsoup.connect("https://www.golf.at/mygolf/login/")
                .data("loginusername", tmpUsername)
                .data("loginpassword", pwd)
                .data("a", "dologin")
                .data("wantedurl", "")
                .cookies(koks)
                .method(Connection.Method.POST)
                .timeout(100000)
                .execute();
//        System.out.println(loginResponse.parse().text());
//        list(koks);


//        String tmpClub = "golfclub-schoenfeld-neun-/338/";
               String tmpClub =  "golfclub-schoenfeld/315/";
        String url = "https://www.golf.at/mobile/startzeiten.asp?uri=/mygolf/tee-online/verfuegbare-startzeiten/" + tmpClub;
//        String url = "https://www.golf.at/mobile/startzeiten.asp?uri=/mygolf/tee-online/verfuegbare-startzeiten/golfclub-schoenfeld/315/";
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
                } else if (who.contains("bekannte Spieler:<br><br><br>")) {
                    // do nothing

                    t.setState(Teetime.State.BOOKED);

                } else if (who.contains("bekannte Spieler:")) {
                    /**
                     * bekannte Handicaps: 11,4 25,9 0,0 0,0 <br><br><br><u>bekannte Spieler:</u><br>Jung Harald (11,4) GOLFCLUB SCHÖNFELD<br>Becher Christian (25,9) GOLFCLUB SCHÖNFELD<br><br><br><br>Flight ganz gebucht.
                     */
                    String[] whos = who.split("bekannte Spieler:");
                    String s = whos[1]
;
                        String[] plauers = s.split("<br>");
                        for (String p :
                                plauers) {
                            Player player = Player.parse(p);
                            if (player != null)
                                t.addPlayer(player);

                        }


                    t.setState(Teetime.State.BOOKED);
                } else if (who.contains("br><br>")) {
                    String[] whos = who.split("<br><br>");

                    for (String s : whos) {
                        String[] plauers = s.split("<br>");
                        for (String p :
                                plauers) {
                            Player player = Player.parse(p);
                            if (player != null)
                                t.addPlayer(player);

                        }
                        break;
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
