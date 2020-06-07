import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;

public class Teetime {


    public boolean isBooked() {
        return state.equals(State.BOOKED) || state.equals(State.ALL_BOOKED);
    }

    public void parseTime(String time) {
        String[] split = time.split("Startzeit: ");
        setTime(split[1]);
    }

    enum State {
        ALL_AVAILABLE, NOT_AVAILABLE,
        ALL_BOOKED, BOOKED;
    }
    String day;
    String time;
    State state;
    Set<Player> players = new HashSet<>();

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    public void setState(State state) {
        this.state = state;
    }



    @Override
    public String toString() {
        return
                time  +
                " " + Joiner.on(", ").join( players)
                ;
    }
}
