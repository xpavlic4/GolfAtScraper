import com.google.common.base.Objects;

public class Player {


    private static Player Unknown = new Player("Unknown", "", "");

    public Player(String name, String club, String hcp) {
        this.hcp = hcp;
        this.name = name;
        this.club = club;
    }

    String hcp;
    String name;
    String club;

    public static Player parse(String p) {
        Player player;
        try {
            //System.out.println(p);
            if (p.contains("Flight ganz gebucht."))
                return null;
            if (p.startsWith("<") || p.isEmpty())
                return null;
            if (p.contains("bekannte Handicaps: "))
                return null;
            if (!p.contains("(")) {
                player = new Player(p, "","" );
            } else {
                String hcp = p.substring(p.indexOf("(") + 1, p.indexOf(")"));

                String club = parseClub(p);
                player = new Player(p.split(" \\(")[0], club, hcp);
            }
        } catch (Exception e) {
            System.out.println(p);
            throw e;
        }
        return player;
    }

    private static String parseClub(String p) {

        String[] split = p.split("\\) ");
        return split.length == 2 ? split[1] : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equal(hcp, player.hcp) &&
                Objects.equal(name, player.name) &&
                Objects.equal(club, player.club);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(hcp, name, club);
    }

    public String getHcp() {
        return hcp;
    }

    public String getClub() {
        return club;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name);
        if (hcp != null && !hcp.isEmpty())
            sb.append("(" + hcp + ")");
        return sb.toString();

    }
}
