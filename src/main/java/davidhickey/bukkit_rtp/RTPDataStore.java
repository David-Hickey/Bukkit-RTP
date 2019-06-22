package davidhickey.bukkit_rtp;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.List;

public class RTPDataStore {

    private final Map<UUID, RTPWorldInfo> worldInfo;
    private final Map<UUID, Long> lastTeleported;
    private final int cooldown;
    private final int maxChecks;

    public RTPDataStore(FileConfiguration conf, Logger logger, List<World> worlds) {
        this.worldInfo = new HashMap<>();
        this.lastTeleported = new HashMap<>();

        if (conf.isInt("cooldown-seconds")) {
            this.cooldown = conf.getInt("cooldown-seconds");
        } else {
            logger.warning("Configuration key 'cooldown-seconds' is missing or invalid, defaulting to 60...");
            this.cooldown = 60;
        }

        if (conf.isInt("maximum-checks")) {
            this.maxChecks = conf.getInt("maximum-checks");
        } else {
            logger.warning("Configuration key 'maximum-checks' is missing or invalid, defaulting to 100...");
            this.maxChecks = 100;
        }

        ConfigurationSection enabledWorldsSection = conf.getConfigurationSection("enabled-worlds");

        for (String key : enabledWorldsSection.getKeys(false)) {
            World world = worldByName(key, worlds);

            if (world == null) {
                logger.warning("World '" + key + "' does not exist, ignoring configuration entry...'");
                continue;
            }

            if (!enabledWorldsSection.isInt("radius")) {
                logger.warning("Radius for world '" + key + "' missing or invalid, ignoring configuration entry...'");
                continue;
            }

            if (!enabledWorldsSection.isConfigurationSection(("centre"))) {
                logger.warning("Centre for world '" + key + "' missing or invalid, ignoring configuration entry...'");
                continue;
            }

            int radius = enabledWorldsSection.getInt("radius");
            Location centre = parseConfigurationSectionAsLocation(world, enabledWorldsSection.getConfigurationSection("centre"));

            if (centre == null) {
                logger.warning("Centre for world '" + key + "' is malformed, ignoring configuration entry...'");
                continue;
            }

            RTPWorldInfo info = new RTPWorldInfo(radius, centre);
            worldInfo.put(world.getUID(), info);
        }
    }

    private World worldByName(String name, List<World> worlds) {
        for (World w : worlds) {
            if (w.getName().equalsIgnoreCase(name)) {
                return w;
            }
        }

        return null;
    }

    private Location parseConfigurationSectionAsLocation(World w, ConfigurationSection s) {
        int y = s.isInt("y") ? s.getInt("y") : 0;

        int x, z;
        if (s.isInt("x")) {
            x = s.getInt("x");
        } else {
            return null;
        }

        if (s.isInt("z")) {
            z = s.getInt("z");
        } else {
            return null;
        }

        return new Location(w, x, y, z);
    }

    public boolean isEnabledForWorld(World w) {
        return worldInfo.containsKey(w.getUID());
    }

    public boolean canPlayerTeleportNow(Player p, long timeNow) {
        if (lastTeleported.containsKey(p.getUniqueId())) {
            return timeNow - lastTeleported.get(p.getUniqueId()) > 20 * this.cooldown;
        } else {
            return true;
        }
    }

    public void playerTeleported(Player p, long timeNow) {
        lastTeleported.put(p.getUniqueId(), timeNow);
    }

    public RTPWorldInfo getWorldInfo(World w) {
        return this.worldInfo.get(w.getUID());
    }

    public int getMaxChecks() {
        return this.maxChecks;
    }

    public int getCooldown() {
        return this.cooldown;
    }
}
