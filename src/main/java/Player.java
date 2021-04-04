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
        if (p == null || p.isEmpty()|| p.startsWith("<"))
            return null;
        if (p.startsWith("Flight ganz gebucht."))
            return  null;
        Player player;
        try {
            //System.out.println(p);
            if (p.contains("bekannte Handicaps: "))
                return null;
            String hcp = p.substring(p.indexOf("(") + 1, p.indexOf(")"));

            String club = parseClub(p);
            player = new Player(p.split(" \\(")[0], club, hcp);
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("'"+ p+ "'");
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

    public void setClub(String club) {
        this.club = club;
    }

    public void setHcp(String hcp) {
        this.hcp = hcp;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + " "  + "(" + hcp + ")";
    }
}
