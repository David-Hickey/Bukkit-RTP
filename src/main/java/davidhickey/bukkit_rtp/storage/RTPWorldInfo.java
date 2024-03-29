package davidhickey.bukkit_rtp.storage;

import org.bukkit.Location;

public class RTPWorldInfo {

    private final int radius;
    private final Location centre;

    public RTPWorldInfo(int radius, Location centre) {
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
