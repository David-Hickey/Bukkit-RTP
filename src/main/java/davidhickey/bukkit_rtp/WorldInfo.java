package davidhickey.bukkit_rtp;

import org.bukkit.Location;

public class WorldInfo {

    private final int radius;
    private final Location centre;

    public WorldInfo(int radius, Location centre) {
        this.radius = radius;
        this.centre = centre;
    }

    public int getRadius() {
        return radius;
    }

    public Location getCentre() {
        return centre;
    }
}
